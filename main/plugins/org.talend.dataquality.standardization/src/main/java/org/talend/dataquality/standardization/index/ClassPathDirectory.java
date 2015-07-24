package org.talend.dataquality.standardization.index;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * A read-only directory that reads index from a JAR file.
 */
public class ClassPathDirectory extends Directory {

    private static final Map<String, JARDescriptor> openedJars = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(ClassPathDirectory.class);

    private static final String uuid = UUID.randomUUID().toString();

    private final JARDescriptor descriptor;

    private final String directory;

    private LockFactory lockFactory;

    public ClassPathDirectory(JARDescriptor descriptor, String directory) {
        this.descriptor = descriptor;
        this.directory = directory;
    }

    public static Directory open(URI uri) {
        LOGGER.debug("Opening '" + uri + "' ...");
        if ("jar".equals(uri.getScheme())) {
            try {
                synchronized (openedJars) {
                    String jarFile = StringUtils.substringBefore(uri.toString(), "!"); //$NON-NLS-1$
                    JARDescriptor openedJar;
                    openedJar = openedJars.get(jarFile);
                    if (openedJar == null) {
                        openedJar = new JARDescriptor();
                        // Extract all nested JARs
                        StringTokenizer tokenizer = new StringTokenizer(uri.toString(), "!");
                        FileSystem fs = null;
                        while (tokenizer.hasMoreTokens()) {
                            final String current = tokenizer.nextToken();
                            if (!tokenizer.hasMoreTokens()) {
                                break;
                            } else if (fs == null) {
                                fs = FileSystems.newFileSystem(URI.create(current), Collections.<String, String> emptyMap());
                            } else { // fs != null
                                try (FileSystem ignored = fs) {
                                    final Path path = fs.getPath(current);
                                    final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
                                    final String unzipFile = tempDirectory + '/' + uuid + '/' + path.getFileName();
                                    final Path destFile = Paths.get(unzipFile);
                                    final File destinationFile = destFile.toFile();
                                    if (!destinationFile.exists()) {
                                        destinationFile.mkdirs();
                                        Files.copy(path, destFile, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                    fs = FileSystems.newFileSystem(destFile, Thread.currentThread().getContextClassLoader());
                                }
                            }
                        }
                        openedJar.fileSystem = fs;
                        openedJar.jarFileName = jarFile;
                        openedJars.put(jarFile, openedJar);
                    } else {
                        LOGGER.debug("Reusing previously opened '" + jarFile + "'...");
                    }
                    String directory = StringUtils.substringAfterLast(uri.toString(), "!"); //$NON-NLS-1$
                    LOGGER.debug("Opening '" + jarFile + "' at directory '" + directory + "' ...");
                    // Increment open count (in case of multiple JAR open and proper resource management).
                    openedJar.openCount.incrementAndGet();
                    return new ClassPathDirectory(openedJar, directory);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to open JAR '" + uri + "'.", e);
            }
        } else if ("file".equals(uri.getScheme())) {
            try {
                return new SimpleFSDirectory(new File(uri));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open path '" + uri + "'.", e);
            }
        } else if ("bundleresource".equals(uri.getScheme())) { // for OSGI environment
            try {
                final String path = PlatformPathUtil.getFilePathByPlatformURL(uri.toURL());
                return new SimpleFSDirectory(new File(path));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open bundleresource '" + uri + "'.", e);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported scheme '" + uri.getScheme() + "'.");
        }
    }

    @Override
    public String[] listAll() throws IOException {
        Path start = getStartPath();
        final List<String> files = new LinkedList<>();
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                files.add(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });
        return files.toArray(new String[files.size()]);
    }

    private Path getStartPath() {
        return descriptor.fileSystem.getPath(directory);
    }

    @Override
    public boolean fileExists(final String name) throws IOException {
        Path start = getStartPath();
        final AtomicBoolean exist = new AtomicBoolean();
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (name.equals(file.getFileName().toString())) {
                    exist.set(true);
                    return FileVisitResult.TERMINATE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
        return exist.get();
    }

    @Override
    public void deleteFile(String name) throws IOException {
        throw new UnsupportedOperationException("Read only directory, unable to delete file.");
    }

    @Override
    public long fileLength(final String name) throws IOException {
        Path start = getStartPath();
        final AtomicLong length = new AtomicLong();
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (name.equals(file.getFileName().toString())) {
                    length.set(file.toFile().length());
                    return FileVisitResult.TERMINATE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
        return length.get();
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        throw new UnsupportedOperationException("Read only directory, unable to modify.");
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
    }

    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        Path start = getStartPath();
        final IndexInput[] input = new IndexInput[1];
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (name.equals(file.getFileName().toString())) {
                    final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
                    final String unzipFile = tempDirectory + '/' + uuid + '/' + directory + '/' + file.getFileName();
                    final Path destFile = Paths.get(unzipFile);
                    final File destinationFile = destFile.toFile();
                    if (!destinationFile.exists()) {
                        destinationFile.mkdirs();
                        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    RandomAccessFile raf = new RandomAccessFile(destinationFile, "r");
                    input[0] = new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + file.getFileName() + "\")", raf, context);
                    return FileVisitResult.TERMINATE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
        return input[0];
    }

    @Override
    public Lock makeLock(String name) {
        return lockFactory.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
    }

    @Override
    public void close() throws IOException {
        // Release FS resources
        synchronized (openedJars) {
            final int openCount = descriptor.openCount.decrementAndGet();
            if (openCount <= 0) {
                LOGGER.debug("Releasing file system (jar '" + descriptor.jarFileName + "' is no longer used.");
                descriptor.fileSystem.close();
                openedJars.remove(descriptor.jarFileName);
            } else {
                LOGGER.debug("*Not* releasing file system (jar '" + descriptor.jarFileName + "' is still being used.");
            }
        }
        if (openedJars.isEmpty()) {
            // Delete temporary resources
            final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
            final String unzipLocation = tempDirectory + '/' + uuid;
            try {
                Path path = FileSystems.getDefault().getPath(unzipLocation);
                if (!path.toFile().exists()) {
                    return;
                }
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        // Skip NFS file content
                        if (!file.getFileName().toFile().getName().startsWith(".nfs")) { //$NON-NLS-1$
                            Files.delete(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (e == null) {
                            return FileVisitResult.CONTINUE;
                        } else {
                            // directory iteration failed
                            throw e;
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public LockFactory getLockFactory() {
        return lockFactory;
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        this.lockFactory = lockFactory;
    }

    private static class JARDescriptor {

        AtomicInteger openCount = new AtomicInteger(0);

        FileSystem fileSystem;

        String jarFileName;
    }

    /**
     * Reads bytes with {@link RandomAccessFile#seek(long)} followed by {@link RandomAccessFile#read(byte[], int, int)}.
     *
     * <b>Note</b>This is a copy from {@link SimpleFSDirectory} since no one thought it would be nice to expose it.
     */
    static class SimpleFSIndexInput extends BufferedIndexInput {

        /**
         * The maximum chunk size is 8192 bytes, because {@link RandomAccessFile} mallocs a native buffer outside of
         * stack if the read buffer size is larger.
         */
        private static final int CHUNK_SIZE = 8192;

        /** the file channel we will read from */
        protected final RandomAccessFile file;

        /** start offset: non-zero in the slice case */
        protected final long off;

        /** end offset (start+length) */
        protected final long end;

        /** is this instance a clone and hence does not own the file to close it */
        boolean isClone = false;

        public SimpleFSIndexInput(String resourceDesc, RandomAccessFile file, IOContext context) throws IOException {
            super(resourceDesc, context);
            this.file = file;
            this.off = 0L;
            this.end = file.length();
        }

        public SimpleFSIndexInput(String resourceDesc, RandomAccessFile file, long off, long length, int bufferSize) {
            super(resourceDesc, bufferSize);
            this.file = file;
            this.off = off;
            this.end = off + length;
            this.isClone = true;
        }

        @Override
        public void close() throws IOException {
            if (!isClone) {
                file.close();
            }
        }

        @Override
        public SimpleFSIndexInput clone() {
            SimpleFSIndexInput clone = (SimpleFSIndexInput) super.clone();
            clone.isClone = true;
            return clone;
        }

        @Override
        public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
            if (offset < 0 || length < 0 || offset + length > this.length()) {
                throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + this);
            }
            return new SimpleFSIndexInput(sliceDescription, file, off + offset, length, getBufferSize());
        }

        @Override
        public final long length() {
            return end - off;
        }

        /** IndexInput methods */
        @Override
        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            synchronized (file) {
                long position = off + getFilePointer();
                file.seek(position);
                int total = 0;

                if (position + len > end) {
                    throw new EOFException("read past EOF: " + this);
                }

                try {
                    while (total < len) {
                        final int toRead = Math.min(CHUNK_SIZE, len - total);
                        final int i = file.read(b, offset + total, toRead);
                        if (i < 0) { // be defensive here, even though we checked before hand, something could have
                                     // changed
                            throw new EOFException("read past EOF: " + this + " off: " + offset + " len: " + len + " total: "
                                    + total + " chunkLen: " + toRead + " end: " + end);
                        }
                        assert i > 0 : "RandomAccessFile.read with non zero-length toRead must always read at least one byte";
                        total += i;
                    }
                    assert total == len;
                } catch (IOException ioe) {
                    throw new IOException(ioe.getMessage() + ": " + this, ioe);
                }
            }
        }

        @Override
        protected void seekInternal(long position) {
        }

    }
}

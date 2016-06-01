// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.index;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.apache.lucene.store.*;

/**
 * A read-only directory that reads index from a JAR file. It supports several URI scheme:
 * <ul>
 * <li>jar</li>
 * <li>file</li>
 * <li>bundleresource</li>
 * </ul>
 */
public class JARDirectory extends Directory {

    private static final Logger LOGGER = Logger.getLogger(JARDirectory.class);

    private final JARDescriptor descriptor;

    private final Object extractLock = new Object();

    final String uuid;

    final String directory;

    private LockFactory lockFactory;

    /**
     * <p>
     * Creates a new {@link Directory directory} that looks up for Lucene indexes inside a JAR file.
     * </p>
     * <p>
     * <b>Important #1</b>: Intentionally left private. Use {@link ClassPathDirectory#open(URI)} to create an instance of this.
     * </p>
     * <p>
     * <b>Important #2</b>: URI's scheme "jar" stores content in a temporary directory, and is only cleaned upon
     * {@link #close()} call.
     * </p>
     *
     * @param uuid A unique identifier to identify this Lucene directory (used in case of concurrent opens of the same
     * JAR file).
     * @param descriptor A {@link JARDescriptor descriptor} to the JAR file to open.
     * @param directory A path inside the opened JAR file.
     * @see ClassPathDirectory#open(URI)
     */
    public JARDirectory(String uuid, JARDescriptor descriptor, String directory) {
        this.uuid = uuid;
        this.descriptor = descriptor;
        this.directory = directory;
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
    public synchronized IndexInput openInput(final String name, final IOContext context) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        final String unzipFile = tempDirectory + '/' + uuid + '/' + directory + '/' + name;
        final Path destFile = Paths.get(unzipFile);
        final File destinationFile = destFile.toFile();
        if (destinationFile.exists()) {
            // File was already extracted, reuse it
            RandomAccessFile raf = new RandomAccessFile(destinationFile, "r");
            return new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + name + "\")", raf, context);
        } else {
            // File was not previously extracted to temp directory, extract it and returns it.
            // To prevent multiple concurrent extracts, lock on common object
            synchronized (extractLock) {
                Path start = getStartPath();
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        if (name.equals(file.getFileName().toString())) {
                            if (!destinationFile.exists()) {
                                destinationFile.mkdirs();
                                Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                            }
                            return FileVisitResult.TERMINATE;
                        } else {
                            return FileVisitResult.CONTINUE;
                        }
                    }
                });
                RandomAccessFile raf = new RandomAccessFile(destFile.toFile(), "r");
                return new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + name + "\")", raf, context);
            }
        }
    }

    @Override
    public Lock makeLock(String name) {
        return lockFactory.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
        lockFactory.makeLock(name).close();
    }

    @Override
    public void close() throws IOException {
        // Release FS resources
        LOGGER.debug("Releasing JAR file (" + descriptor.jarFileName + ").");
        descriptor.fileSystem.close();
        // Delete temporary resources
        final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        final String unzipLocation = tempDirectory + '/' + uuid;
        try (FileSystem fileSystem = FileSystems.getDefault()) {
            Path path = fileSystem.getPath(unzipLocation);
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
            if (!path.toFile().delete()) {
                LOGGER.warn("Unable to clean directory '" + unzipLocation + "'.");
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

    /**
     * Describes a opened JAR file.
     */
    public static class JARDescriptor {

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

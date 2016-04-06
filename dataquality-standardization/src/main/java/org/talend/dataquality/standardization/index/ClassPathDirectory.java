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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * A read-only directory that reads index from a JAR file. It supports several URI scheme:
 * <ul>
 * <li>jar</li>
 * <li>file</li>
 * <li>bundleresource</li>
 * </ul>
 */
public class ClassPathDirectory {

    private static final Logger LOGGER = Logger.getLogger(ClassPathDirectory.class);

    private static JARDirectoryProvider provider = new BasicProvider();

    /**
     * Allow external code to change behavior about extracted Lucene indexes (always extract a fresh copy or reuse
     * previous extract).
     * 
     * @param provider An implementation of {@link JARDirectoryProvider}. See {@link BasicProvider} or
     * {@link SingletonProvider} for examples.
     * @see BasicProvider
     * @see SingletonProvider
     */
    public synchronized static void setProvider(JARDirectoryProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider can not be null.");
        }
        ClassPathDirectory.provider = provider;
    }

    /**
     * <p>
     * Creates a new {@link Directory directory} that picks up the right implementation depending on URI's scheme.
     * </p>
     * 
     * @param uri A valid URI to a Lucene index
     * @return A {@link Directory} to the Lucene content in <code>uri</code>.
     */
    public synchronized static Directory open(URI uri) {
        LOGGER.debug("Opening '" + uri + "' ...");
        if ("jar".equals(uri.getScheme())) {
            try {
                return provider.get(uri);
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

    public static void destroy() {
        provider.destroy();
    }

    /**
     * An interface to provide Lucene indexes based on provided location (as URI).
     */
    public interface JARDirectoryProvider {

        /**
         * Returns a {@link Directory lucene directory} for provided location (as URI).
         * @param uri An URI to a JAR file.
         * @return A {@link Directory lucene directory} ready to be used in Lucene code.
         * @throws Exception
         */
        Directory get(URI uri) throws Exception;

        /**
         * Destroys all cached resources by this provider.
         */
        void destroy();
    }

    /**
     * An implementation that extract only once content on disk for a given URI.
     */
    public static class SingletonProvider implements JARDirectoryProvider {

        private static final BasicProvider provider = new BasicProvider();

        private static final Map<URI, Directory> instances = new HashMap<>();

        @Override
        public synchronized Directory get(URI uri) throws Exception {
            if (instances.get(uri) == null) {
                instances.put(uri, provider.get(uri));
            }
            return instances.get(uri);
        }

        @Override
        public void destroy() {
            provider.destroy();
        }
    }

    /**
     * An implementation that does not perform any reuse of previously extracted content.
     */
    public static class BasicProvider implements JARDirectoryProvider {

        private static final Map<URI, FileSystem> openedJars = new HashMap<>();

        /**
         * Holds all opened class path directory instances for clean up TODO This is temporary until a more global
         * resource management system is found/proposed
         *
         * @see #destroy()
         */
        private static final Set<JARDirectory> classPathDirectories = new HashSet<>();

        private static FileSystem openOrGet(String uri) throws IOException {
            FileSystem fs;
            final URI jarURI = URI.create(uri);
            synchronized (openedJars) {
                fs = openedJars.get(jarURI);
                if (fs == null) {
                    fs = FileSystems.newFileSystem(jarURI, Collections.<String, String> emptyMap());
                    openedJars.put(jarURI, fs);
                }
            }
            return fs;
        }

        @Override
        public Directory get(URI uri) throws Exception {
            String jarFile = StringUtils.substringBefore(uri.toString(), "!"); //$NON-NLS-1$
            final String uuid = UUID.randomUUID().toString();
            JARDirectory.JARDescriptor openedJar = new JARDirectory.JARDescriptor();
            // Extract all nested JARs
            StringTokenizer tokenizer = new StringTokenizer(uri.toString(), "!");
            FileSystem fs = null;
            while (tokenizer.hasMoreTokens()) {
                final String current = tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    break;
                } else if (fs == null) {
                    fs = openOrGet(current);
                } else { // fs != null
                    final Path path = fs.getPath(current);
                    final String tempDirectory = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
                    final String unzipFile = tempDirectory + '/' + uuid + '/' + path.getFileName();
                    final Path destFile = Paths.get(unzipFile);
                    final File destinationFile = destFile.toFile();
                    if (!destinationFile.exists()) {
                        destinationFile.mkdirs();
                        Files.copy(path, destFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    // UUID ensures the path is unique, no need for openOrGet(...)
                    fs = FileSystems.newFileSystem(destFile, Thread.currentThread().getContextClassLoader());
                }
            }
            openedJar.fileSystem = fs;
            openedJar.jarFileName = jarFile;
            String directory = StringUtils.substringAfterLast(uri.toString(), "!"); //$NON-NLS-1$
            LOGGER.debug("Opening '" + jarFile + "' at directory '" + directory + "' ...");
            final JARDirectory jarDirectory = new JARDirectory(uuid, openedJar, directory);
            classPathDirectories.add(jarDirectory);
            return jarDirectory;
        }

        /**
         * Destroy all resources that instances may have created on disk.
         */
        public void destroy() {
            final Iterator<JARDirectory> iterator = classPathDirectories.iterator();
            while (iterator.hasNext()) {
                final JARDirectory jarDirectory = iterator.next();
                try {
                    jarDirectory.close();
                } catch (Exception e) {
                    LOGGER.error(
                            "Unable to close directory at " + jarDirectory.directory + " (uuid : " + jarDirectory.uuid + ").");
                } finally {
                    iterator.remove();
                }
            }
            for (Map.Entry<URI, FileSystem> entry : openedJars.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close " + entry.getValue() + ".", e);
                }
            }
        }
    }

}

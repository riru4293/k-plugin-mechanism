/*
 * Copyright (c) 2023, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.plugin.impl;

import java.io.IOException;
import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.PluginLoadingException;
import jp.mydns.projectk.plugin.PluginStorage;

/**
 * Implements of the {@code PluginStorage}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class PluginStorageImpl implements PluginStorage {

    private static final Logger LOGGER = System.getLogger(PluginStorageImpl.class.getName());
    private static final String PLUGINFILE_EXTENTION = ".jar";
    private final List<PluginLoadingSource> sources;

    /**
     * Construct from plug-in stored directories. Directory is not recursively search, and ignore invalid jar files as
     * plug-in. If multiple plug-in files with the same name exist in directories, the last one found will be used.
     *
     * @param storages directories where the plug-in jar files are stored. The one specified later has priority.
     * @throws NullPointerException if {@code storages} is {@code null} or it contains {@code null} element.
     * @throws PluginLoadingException if occurs unexpected error
     * @since 1.0.0
     */
    public PluginStorageImpl(Path... storages) {
        this.sources = List.of(storages).stream().flatMap(this::toChildren)
                .filter(p -> p.toString().endsWith(PLUGINFILE_EXTENTION)).flatMap(this::toPluginLoadingSource).toList();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public Stream<PluginLoadingSource> stream() {
        return sources.stream();
    }

    private Stream<Path> toChildren(Path dir) {

        if (Files.isDirectory(dir)) {

            try (Stream<Path> files = Files.list(dir)) {
                return files.toList().stream();
            } catch (IOException ex) {
                LOGGER.log(WARNING, String.format("I/O error occurs when opening the directory. [%s]", dir), ex);
                throw new PluginLoadingException("Occurs an I/O error while searching the plug-in files.");
            }

        } else {

            return Stream.empty();

        }

    }

    private Stream<PluginLoadingSource> toPluginLoadingSource(Path file) {

        try (var j = new JarFile(file.toFile());) {

            Manifest mf = j.getManifest();
            String mainName = Optional.ofNullable(mf).map(Manifest::getMainAttributes)
                    .map(a -> a.getValue(Attributes.Name.MAIN_CLASS)).orElseThrow(
                    () -> new NoSuchElementException("Could not find a valid manifest file as a plug-in within jar file."));

            return Stream.of(new PluginLoadingSourceImpl(mainName, file));

        } catch (IOException | RuntimeException ignore) {

            LOGGER.log(DEBUG, String.format("Occurs an error while analysis the jar file as a plug-in.", file), ignore);
            return Stream.empty();

        }
    }

    private class PluginLoadingSourceImpl implements PluginLoadingSource {

        private final String mainClassName;
        private final URL mainJar;
        private final URL libraryDirectory;

        PluginLoadingSourceImpl(String mainName, Path mainJar) throws MalformedURLException {
            this.mainClassName = mainName;
            this.mainJar = mainJar.toUri().toURL();
            this.libraryDirectory = toLibraryDirectory(mainJar);
        }

        private URL toLibraryDirectory(Path mainJar) throws MalformedURLException {

            Path parent = mainJar.getParent();
            String name = mainJar.getFileName().toString();
            String libDirName = name.substring(0, name.length() - PLUGINFILE_EXTENTION.length());

            return parent.resolve(libDirName).toUri().toURL();

        }

        @Override
        public String getClassName() {
            return mainClassName;
        }

        @Override
        public URL[] getClassPath() {
            return new URL[]{mainJar, libraryDirectory};
        }
    }
}

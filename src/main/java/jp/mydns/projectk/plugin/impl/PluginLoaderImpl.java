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

import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;
import java.lang.ref.Cleaner;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.NoSuchPluginException;
import jp.mydns.projectk.plugin.Plugin;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.plugin.PluginLoadingException;
import jp.mydns.projectk.plugin.PluginStorage;
import jp.mydns.projectk.plugin.PluginStorage.PluginResource;

/**
 * A simple plug-in loading facility.
 *
 * @param <T> Plug-in interface type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class PluginLoaderImpl<T extends Plugin> implements PluginLoader<T> {

    private static final Logger LOGGER = System.getLogger(PluginLoaderImpl.class.getName());

    static {
        // Note: Provisional response to the problem that "commons-vfs" cannot be unloaded.
        URLConnection.setDefaultUseCaches("file", false);
        URLConnection.setDefaultUseCaches("jar", false);
    }

    private static final Cleaner CLEANER = Cleaner.create();

    private final ClassLoader parent = Thread.currentThread().getContextClassLoader();
    private final Map<String, Supplier<T>> suppliers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Class<T> clazz;
    private final Cleaner.Cleanable cleanable;

    /**
     * Construct from the {@code PluginStorage}.
     *
     * @param clazz plug-in type
     * @param storage the {@code PluginStorage}
     * @throws NullPointerException if any argument is {@code null}
     */
    public PluginLoaderImpl(Class<T> clazz, PluginStorage storage) {

        Objects.requireNonNull(clazz);
        Objects.requireNonNull(storage);

        Map<String, URLClassLoader> loaders = storage.stream().collect(
                toMap(this::toPluginName, this::toURLClassLoader, (first, last) -> last, LinkedHashMap::new));

        this.clazz = clazz;

        this.cleanable = toCleanable(loaders.values());

        loaders.entrySet().stream().forEachOrdered(e -> suppliers.put(e.getKey(), new PluginSupplier(e.getValue())));
    }

    private Cleaner.Cleanable toCleanable(Collection<URLClassLoader> loaders) {

        return CLEANER.register(this, new PluginLoaderCloser(loaders));
    }

    private String toPluginName(PluginStorage.PluginResource r) {

        String className = r.getClassName();

        int idx = className.lastIndexOf('.');

        return idx > 0 ? className.substring(idx + 1, className.length()) : className;
    }

    private URLClassLoader toURLClassLoader(PluginResource r) {

        return new URLClassLoader(r.getClassName(), r.getClassPath(), parent);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     * @throws NoSuchPluginException {@inheritDoc}
     * @throws PluginLoadingException {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public T load(String name) {

        Objects.requireNonNull(name);

        return Optional.ofNullable(suppliers.get(name)).orElseThrow(
                () -> new NoSuchPluginException("No such a plug-in [%s]. Availables are %s."
                        .formatted(name, suppliers.keySet()))
        ).get();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public Stream<Map.Entry<String, Supplier<T>>> stream() {

        return suppliers.entrySet().stream();
    }

    class PluginSupplier implements Supplier<T> {

        final String mainClassName;
        final URLClassLoader loader;

        PluginSupplier(URLClassLoader e) {

            this.mainClassName = e.getName();
            this.loader = e;
        }

        @Override
        public T get() {

            try {

                Class<?> cls = loader.loadClass(mainClassName);

                Constructor<? extends T> constructor = cls.asSubclass(clazz).getConstructor();

                return constructor.newInstance();

            } catch (ReflectiveOperationException | RuntimeException ignore) {

                LOGGER.log(WARNING, "Failed load a plug-in. Class name is %s. Class paths are %s."
                        .formatted(mainClassName, Arrays.toString(loader.getURLs())), ignore);

                throw new PluginLoadingException("An invalid plug-in was found.");
            }
        }
    }

    /**
     * Close class loader of all plug-in loaded by this.
     *
     * @since 1.0.0
     */
    @Override
    public void close() {

        cleanable.clean();
    }
}

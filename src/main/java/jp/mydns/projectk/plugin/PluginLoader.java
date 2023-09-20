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
package jp.mydns.projectk.plugin;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Plug-in loader interface. Each plug-in has its own class loader, and the parent class loader is the
 * {@code ContextClassLoader} for the thread in which the plug-in loader is instantiated.
 * <p>
 * The "plug-in" is a single jar file containing the implementation of the {@link Plugin} interface.
 * <p>
 * Implements of plug-in is main class. Identified by a "Main-Class" attribute in the "META-INF/MANIFEST.MF".
 *
 * @param <T> Plug-in interface type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PluginLoader<T extends Plugin> extends AutoCloseable {

    /**
     * Close class loader of all plug-ins loaded by this loader.
     *
     * @since 1.0.0
     */
    @Override
    void close();

    /**
     * Load a specified plug-in.
     *
     * @param name plug-in name. It case insensitive.
     * @return loaded plug-in
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws PluginLoadingException if an exception occurs while plug-in loading
     * @since 1.0.0
     */
    T load(String name);

    /**
     * Plug-in supplier entries. Entry key is plug-in name. Supplied all plug-in.
     *
     * @return suppliers. Used {@link #load(java.lang.String)} when supply.
     * @since 1.0.0
     */
    Stream<Map.Entry<String, Supplier<T>>> stream();
}

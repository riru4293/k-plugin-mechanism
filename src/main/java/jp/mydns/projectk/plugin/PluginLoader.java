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
 * The {@code plug-in} is a single jar file containing a single implementation of the {@link Plugin} interface. The
 * location of a plug-in implementation in a jar file is identified by a {@code Main-Class} attribute in the
 * {@code META-INF/MANIFEST.MF}.
 *
 * @param <T> Plug-in interface type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PluginLoader<T extends Plugin> extends AutoCloseable {

    /**
     * Close all class loaders for plug-in loaded by this loader.
     *
     * @since 1.0.0
     */
    @Override
    void close();

    /**
     * Load one plug-in by specified name.
     * <p>
     * A plug-in can be unloaded when all of the following three conditions are met. Whether it is actually unloaded or
     * not depends on the Java VM implementation.
     * <ol>
     * <li>The plug-in instance disappears from the heap memory.</li>
     * <li>There are no threads running static methods of the plug-in.</li>
     * <li>The instance of the plug-in loader that loaded the plug-in disappears from the heap memory. (Must be closed
     * the plug-in loader)</li>
     * </ol>
     *
     * @param name plug-in name. It case insensitive.
     * @return loaded plug-in
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws NoSuchPluginException if no found a plug-in
     * @throws PluginLoadingException if an error occurs while plug-in loading
     * @since 1.0.0
     */
    T load(String name);

    /**
     * Returns a stream of plug-in suppliers. The stream contains all plug-in known to this plug-in loader instance.
     * Entry key represents a plug-in name, and entry value is plug-in instance supplier.
     *
     * @return plug-in suppliers. Used {@link #load(java.lang.String)} when supply.
     * @since 1.0.0
     */
    Stream<Map.Entry<String, Supplier<T>>> stream();
}

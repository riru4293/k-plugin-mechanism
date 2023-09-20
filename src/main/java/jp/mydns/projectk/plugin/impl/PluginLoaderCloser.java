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
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

/**
 * Closer for the {@code PluginLoader}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class PluginLoaderCloser implements Runnable {

    final List<URLClassLoader> loaders;

    /**
     * Constructor.
     *
     * @param loaders all the {@code URLClassLoader} that loaded the plug-in
     * @throws NullPointerException if {@code loaders} is {@code null}, or it contain any {@code null} elements.
     * @since 1.0.0
     */
    public PluginLoaderCloser(Collection<URLClassLoader> loaders) {

        this.loaders = List.copyOf(loaders);
    }

    /**
     * Close all managed class-loaders.
     * <p>
     * If occurred I/O error when closing the {@code URLClassLoader} then ignore that. Because can not do anything else.
     *
     * @since 1.0.0
     */
    @Override
    public void run() {

        loaders.stream().forEach(this::silentClose);
    }

    private void silentClose(URLClassLoader loader) {

        try {
            loader.close();
        } catch (IOException ignore) {
            // ignore
        }
    }
}

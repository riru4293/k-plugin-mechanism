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

import java.net.URL;
import java.util.stream.Stream;

/**
 * Logical plug-in storage.
 * <p>
 * <i>What is a "plug-in storage"?</i><br>
 * A collection of information required to load a single plug-in.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PluginStorage {

    /**
     * Returns a stream of {@code PluginLoadingSource}.
     *
     * @return plug-in loading source stream
     * @since 1.0.0
     */
    Stream<PluginLoadingSource> stream();

    /**
     * Information required to load a single plug-in.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    interface PluginLoadingSource {

        /**
         * Get full class name of plug-in's class.
         *
         * @return class name
         * @since 1.0.0
         */
        String getClassName();

        /**
         * Get class path of plug-in's class and plug-in's libraries.
         *
         * @return class paths
         * @since 1.0.0
         */
        URL[] getClassPath();
    }
}

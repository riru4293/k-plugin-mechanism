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

import jakarta.json.JsonObject;

/**
 * Plug-in interface.
 * <p>
 * <i>What is a "Plug-in"?</i><br>
 * The actual plug-in is a single jar file. The implementation of this interface must exist as a main class in it. In
 * addition, you need to define the main class in MANIFEST.MF file.
 * <p>
 * <i>Plug-in's name</i><br>
 * The "Plug-in name" is the simple class name of plug-in implementation. Package name is not included in it.
 * <p>
 * <i>Interrupt</i><br>
 * If the current thread is interrupted, you must stop processing quickly and safely and throw a
 * {@code InterruptedException}.
 * <p>
 * <i>Exception</i><br>
 * All of the {@code Exception} other than the {@link PluginRuntimeException} must not be thrown to outside the plug-in.
 * The only exception is when a thread is interrupted, which must throw the {@link InterruptedException}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Plugin {

    /**
     * Get plug-in description. Must not be thrown exception and never return {@code null}.
     *
     * @return plug-in description
     * @since 1.0.0
     */
    String getAbout();

    /**
     * Get plug-in version. Must not be thrown exception and never return {@code null}.
     *
     * @return plug-in version
     * @since 1.0.0
     */
    String getVersion();

    /**
     * Get plug-in properties. Used to convey environment-dependent values ​​of the plugin caller to the plugin. Must
     * not be thrown exception and never return {@code null}.
     *
     * @return plug-in properties. It never {@code null}.
     * @since 1.0.0
     */
    JsonObject getPluginProperties();

    /**
     * Set plug-in properties. This method is used by the plug-in loader.
     *
     * @param props properties
     * @throws NullPointerException if {@code props} is {@code null}
     * @since 1.0.0
     */
    void setPluginProperties(JsonObject props);
}

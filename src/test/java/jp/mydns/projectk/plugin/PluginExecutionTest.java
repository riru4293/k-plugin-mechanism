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

import java.net.URISyntaxException;
import java.nio.file.Path;
import jp.mydns.projectk.plugin.impl.PluginLoaderImpl;
import jp.mydns.projectk.plugin.impl.PluginStorageImpl;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of load and execute a plug-in. Since this project does not have an actual plug-in, a test plug-in was created
 * using Maven before the test, and the loading and execution of the created plug-in was tested.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class PluginExecutionTest {

    @Test
    void test() throws URISyntaxException {

        // The Maven phase "process-test-classes" creates the plug-in for testing.
        // For details, see configuration of the maven-compiler-plugin in "pom.xml".
        Path pluginDir = Path.of(System.getProperties().getProperty("PluginStorage"));
        PluginStorage storage = new PluginStorageImpl(pluginDir);

        // Create a plug-in loader with the plug-in's interface type.
        try (var loader = new PluginLoaderImpl<>(ExecutablePlugin.class, storage)) {

            // Loads a plug-in by plug-in name.
            // The plug-in name is the main class name indicated in the MANIFEST.MF file in the jar file.
            // It does not include the package name.
            ExecutablePlugin plugin = loader.load("ExecutablePlugin$Impl");

            // Run the loaded plug-in and get the results. The specified arguments will be passed to the plug-in.
            String result = plugin.execute("hello");

            assertThat(result).isEqualTo("Argument: hello, About: For testing, Version: 1.0.0");
        }
    }
}

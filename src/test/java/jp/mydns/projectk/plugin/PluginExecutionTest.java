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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.net.URISyntaxException;
import java.nio.file.Path;
import jp.mydns.projectk.plugin.impl.PluginLoaderImpl;
import jp.mydns.projectk.plugin.impl.PluginStorageImpl;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Load and execute a plug-in.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class PluginExecutionTest {

    @Test
    void test() throws URISyntaxException {

        Path pluginDir = Path.of(System.getProperties().getProperty("PluginStorage"));

        PluginStorage storage = new PluginStorageImpl(pluginDir);

        JsonObject props = Json.createObjectBuilder().add("name", "Project-K").build();

        try (var loader = new PluginLoaderImpl<>(ExecutablePlugin.class, storage)) {

            ExecutablePlugin plugin = loader.load("PluginImpl");
            plugin.setPluginProperties(props);

            String result = plugin.execute("hello");

            assertThat(result).isEqualTo("Argument: hello, About: For testing, Version: 1.0.0, Property[name]: Project-K");
        }
    }
}

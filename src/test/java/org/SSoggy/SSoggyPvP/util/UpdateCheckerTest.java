package org.SSoggy.SSoggyPvP.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateCheckerTest {

    private HttpServer httpServer;
    private String serverUrl;

    @BeforeEach
    public void setup() throws IOException {
        Server server = mock(Server.class);
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(server.getScheduler()).thenReturn(scheduler);
        when(server.getLogger()).thenReturn(Logger.getLogger("TestLogger"));

        // run immediately instead of asynchronously
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(1);
            task.run();
            return null;
        }).when(scheduler).runTaskAsynchronously(any(Plugin.class), any(Runnable.class));

        Bukkit.setServer(server);

        // start mock http server
        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        serverUrl = "http://localhost:" + httpServer.getAddress().getPort() + "/latest";
        httpServer.setExecutor(null);
        httpServer.start();
    }

    @AfterEach
    public void teardown() throws Exception {
        if (httpServer != null) {
            httpServer.stop(0);
        }

        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, null);
    }

    @Test
    public void testCheckNewVersionAvailable() throws Exception {
        httpServer.createContext("/latest", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "{\"tag_name\":\"v2.0.0\"}";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        PvPTogglePlugin plugin = mock(PvPTogglePlugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("TestPluginLogger"));
        PluginDescriptionFile desc = mock(PluginDescriptionFile.class);
        when(desc.getVersion()).thenReturn("1.0.0");
        when(plugin.getDescription()).thenReturn(desc);

        UpdateChecker checker = new UpdateChecker(plugin);
        checker.setApiUrl(serverUrl);
        checker.check();

        assertEquals("2.0.0", checker.getLatestVersion());
    }

    @Test
    public void testCheckNoNewVersion() throws Exception {
        httpServer.createContext("/latest", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "{\"tag_name\":\"v1.0.0\"}";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        PvPTogglePlugin plugin = mock(PvPTogglePlugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("TestPluginLogger"));
        PluginDescriptionFile desc = mock(PluginDescriptionFile.class);
        when(desc.getVersion()).thenReturn("1.0.0");
        when(plugin.getDescription()).thenReturn(desc);

        UpdateChecker checker = new UpdateChecker(plugin);
        checker.setApiUrl(serverUrl);
        checker.check();

        assertNull(checker.getLatestVersion());
    }

    @Test
    public void testCheckInvalidJson() throws Exception {
        httpServer.createContext("/latest", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "invalid json";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        PvPTogglePlugin plugin = mock(PvPTogglePlugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("TestPluginLogger"));
        PluginDescriptionFile desc = mock(PluginDescriptionFile.class);
        when(desc.getVersion()).thenReturn("1.0.0");
        when(plugin.getDescription()).thenReturn(desc);

        UpdateChecker checker = new UpdateChecker(plugin);
        checker.setApiUrl(serverUrl);
        checker.check();

        assertNull(checker.getLatestVersion());
    }

    @Test
    public void testExtractTag() throws Exception {
        UpdateChecker checker = new UpdateChecker(mock(PvPTogglePlugin.class));
        Method extractTag = UpdateChecker.class.getDeclaredMethod("extractTag", String.class);
        extractTag.setAccessible(true);

        String json = "{\"url\":\"foo\",\"tag_name\":\"v1.2.3\",\"name\":\"Release\"}";
        String result = (String) extractTag.invoke(checker, json);
        assertEquals("v1.2.3", result);

        json = "{\"url\":\"foo\"}";
        result = (String) extractTag.invoke(checker, json);
        assertNull(result);
    }

    @Test
    public void testIsNewer() throws Exception {
        UpdateChecker checker = new UpdateChecker(mock(PvPTogglePlugin.class));
        Method isNewer = UpdateChecker.class.getDeclaredMethod("isNewer", String.class, String.class);
        isNewer.setAccessible(true);

        assertTrue((Boolean) isNewer.invoke(checker, "1.2.0", "1.1.0"));
        assertTrue((Boolean) isNewer.invoke(checker, "1.2", "1.1.0"));
        assertFalse((Boolean) isNewer.invoke(checker, "1.1.0", "1.2.0"));
        assertFalse((Boolean) isNewer.invoke(checker, "1.1.0", "1.1.0"));
        assertFalse((Boolean) isNewer.invoke(checker, "1.0.0", "1.0.1"));
        assertTrue((Boolean) isNewer.invoke(checker, "1.0.1", "1.0.0"));
    }
}

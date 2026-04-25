package dev.piotr_weychan.szlaban.advisor.adapter;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerPropertiesAdapterTest {
  @TempDir
  File tempDir;

  JavaPlugin plugin;

  @BeforeEach
  void setUp(@TempDir File tempDir) {
    // re-mock java plugin
    plugin = mock(JavaPlugin.class, RETURNS_DEEP_STUBS);
    // allow using the temp directory with server.properties finding
    when(plugin.getServer().getPluginsFolder().getParentFile()).thenReturn(tempDir);

    // save our sample server properties into the temp dir
    try {
      Files.copy(Path.of("src/test/resources/test-server.properties"), tempDir.toPath().resolve("server.properties"));
    } catch (IOException e) {
      // it should not call this, if it does, skip the tests
      Assumptions.abort("Could not copy server.properties to temporary directory: " + e.getMessage());
    }
  }

  @Test
  void canReadAllValues() {
    ConfigAdapter propsAdapter = new ServerPropertiesAdapter(plugin);

    assertEquals("true", propsAdapter.getValue("test-value-1"));
    assertEquals(
        "this is a long string to test if we can see the contents of this config file",
        propsAdapter.getValue("test-value-2")
    );
  }

  @Test
  void setUpdatesValues() {
    ConfigAdapter propsAdapter = new ServerPropertiesAdapter(plugin);
    propsAdapter.setValue("test-value-1", "false");
    propsAdapter.setValue("test-value-2", "the old string is gone");

    assertEquals("false", propsAdapter.getValue("test-value-1"));
    assertEquals("the old string is gone", propsAdapter.getValue("test-value-2"));
  }

  @Test
  void setDoesNotChangeFile() {
    ConfigAdapter propsAdapter = new ServerPropertiesAdapter(plugin);

    propsAdapter.setValue("test-value-1", "false");

    ConfigAdapter propsAdapter2 = new ServerPropertiesAdapter(plugin);

    assertEquals("true", propsAdapter2.getValue("test-value-1"));
  }

  @Test
  void saveChangesFileContents() {
    ConfigAdapter propsAdapter = new ServerPropertiesAdapter(plugin);

    propsAdapter.setValue("test-value-1", "false");
    propsAdapter.save();

    ConfigAdapter propsAdapter2 = new ServerPropertiesAdapter(plugin);

    assertEquals("false", propsAdapter2.getValue("test-value-1"));
  }

}

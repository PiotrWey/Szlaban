package dev.piotr_weychan.szlaban.advisor.advisory;


import dev.piotr_weychan.szlaban.advisor.adapter.ConfigAdapter;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.empty;
import static org.junit.jupiter.api.Assertions.*;

class ConfigAdvisoryTest {

  // config adapter that just wraps a map and tracks save calls
  private static class TestAdapter implements ConfigAdapter {
    private final Map<String, Object> config = new HashMap<>();
    public int saveCount = 0;

    // query internal state
    @Override
    public @Nullable Object getValue(String key) {
      return config.get(key);
    }

    // update internal value
    @Override
    public void setValue(String key, Object value) {
      config.put(key, value);
    }

    // does nothing
    @Override
    public void save() {
      saveCount++;
    }
  }

  // detects if the value is unresolved at the start
  @Test
  void detectsUnresolved() {
    ConfigAdapter testAdapter = new TestAdapter();
    testAdapter.setValue("resolved", false);

    Advisory advisory = new ConfigAdvisory<>(
        testAdapter,
        "resolved", true,
        "", empty(), empty()
    );

    assertFalse(advisory.isResolved());
  }

  // detects if the config was already resolved
  @Test
  void detectsPreResolved() {
    ConfigAdapter testAdapter = new TestAdapter();
    testAdapter.setValue("resolved", true);

    Advisory advisory = new ConfigAdvisory<>(
        testAdapter,
        "resolved", true,
        "", empty(), empty()
    );

    assertTrue(advisory.isResolved());
  }

  @Test
  void isResolvedUsesState() {
    ConfigAdapter testAdapter = new TestAdapter();
    testAdapter.setValue("resolved", false);

    Advisory advisory = new ConfigAdvisory<>(
        testAdapter,
        "resolved", true,
        "", empty(), empty()
    );

    assertFalse(advisory.isResolved());
    advisory.resolve();
    assertTrue(advisory.isResolved());
  }

  @Test
  void resolveMutatesAdapter() {
    ConfigAdapter testAdapter = new TestAdapter();
    testAdapter.setValue("resolved", false);

    Advisory advisory = new ConfigAdvisory<>(
        testAdapter,
        "resolved", true,
        "", empty(), empty()
    );

    assertEquals(false, testAdapter.getValue("resolved"));
    advisory.resolve();
    assertEquals(true, testAdapter.getValue("resolved"));
  }

  @Test
  void resolveSavesAdapter() {
    TestAdapter testAdapter = new TestAdapter();
    testAdapter.setValue("resolved", false);

    Advisory advisory = new ConfigAdvisory<>(
        testAdapter,
        "resolved", true,
        "", empty(), empty()
    );

    assertEquals(0, testAdapter.saveCount);
    advisory.resolve();
    assertEquals(1, testAdapter.saveCount);
  }

}

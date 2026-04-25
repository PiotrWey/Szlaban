package dev.piotr_weychan.szlaban.advisor.advisory;

import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.empty;
import static org.junit.jupiter.api.Assertions.*;

class LambdaAdvisoryTest {
  private static class BooleanWrapper {
    public BooleanWrapper(boolean value) {
      this.value = value;
    }
    private boolean value;
    public boolean get() { return value; }
    public void set(boolean v) { this.value = v; }
  }

  // resolving the (unresolved) advisory should change its state
  @Test
  void resolveResolves() {
    BooleanWrapper bw = new BooleanWrapper(false);

    Advisory advisory = new LambdaAdvisory(
        bw::get,
        () -> bw.set(true),
        "", empty(), empty()
    );

    assertFalse(advisory.isResolved());
    advisory.resolve();
    assertTrue(advisory.isResolved());
  }

  // if the advisory starts as resolved, it should show that it's resolved
  @Test
  void detectsUnresolved() {
    BooleanWrapper bw = new BooleanWrapper(false);

    Advisory advisory = new LambdaAdvisory(
        bw::get,
        () -> bw.set(true),
        "", empty(), empty()
    );

    assertFalse(advisory.isResolved());
  }

  // if the advisory starts as resolved, it should show that it's resolved
  @Test
  void detectsPreResolved() {
    BooleanWrapper bw = new BooleanWrapper(true);

    Advisory advisory = new LambdaAdvisory(
        bw::get,
        () -> bw.set(true),
        "", empty(), empty()
    );

    assertTrue(advisory.isResolved());
  }

}

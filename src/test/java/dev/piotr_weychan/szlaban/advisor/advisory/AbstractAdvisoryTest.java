package dev.piotr_weychan.szlaban.advisor.advisory;

import net.kyori.adventure.text.Component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AbstractAdvisoryTest {

  private static class TestAdvisory extends AbstractAdvisory {
    public int resolutionCount = 0;
    private boolean resolved = false;

    public TestAdvisory(boolean resolved) {
      super("", Component.empty(), Component.empty());
      this.resolved = resolved;
    }

    // not used, implementation detail
    @Override
    public boolean isResolved() {
      return resolved;
    }

    // resolutionCount is only incremented here, nowhere else
    @Override
    protected void onResolve() {
      resolutionCount++;
      resolved = true;
    }
  }

  // test that onResolve is called
  @Test
  void resolveHookIsCalled() {
    TestAdvisory testAdvisory = new TestAdvisory(false);

    testAdvisory.resolve();
    assertEquals(1, testAdvisory.resolutionCount);
  }

  // test that onResolve is only called once if state changes correctly on resolve
  @Test
  void canOnlyResolveOnce() {
    TestAdvisory testAdvisory = new TestAdvisory(false);

    // resolve twice
    testAdvisory.resolve();
    testAdvisory.resolve();

    assertEquals(1, testAdvisory.resolutionCount);
  }

  // test that onResolve is not called if it is already resolved
  @Test
  void resolvedDoesNotResolve() {
    // this one's already resolved
    TestAdvisory testAdvisory = new TestAdvisory(true);

    testAdvisory.resolve();

    assertEquals(0,  testAdvisory.resolutionCount);
  }
}

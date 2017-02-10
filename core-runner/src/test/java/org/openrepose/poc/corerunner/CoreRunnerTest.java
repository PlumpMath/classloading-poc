package org.openrepose.poc.corerunner;

import org.junit.Test;

/**
 * Created by adrian on 2/10/17.
 */
public class CoreRunnerTest {
    @Test
    public void runIt() throws Exception {
        new CoreRunner().initializeContext();
    }
}
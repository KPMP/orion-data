package org.kpmp.externalProcess;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommandResultTest {

    private CommandResult commandResult;

    @Before
    public void setUp() throws Exception {
        commandResult = new CommandResult();
    }

    @After
    public void tearDown() throws Exception {
        commandResult = null;
    }

    @Test
    public void testOutput() throws Exception {
        commandResult.setOutput("This is the output");
        assertEquals("This is the output", commandResult.getOutput());
    }

    @Test
    public void testResult() throws Exception {
        commandResult.setResult(false);
        assertFalse(commandResult.isResult());
    }


}

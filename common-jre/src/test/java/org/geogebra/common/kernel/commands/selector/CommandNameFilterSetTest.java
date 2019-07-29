package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandNameFilterSetTest extends BaseUnitTest {

    private CommandNameFilter factory = null;

    @Before
    public void setupTest() {
        factory = CommandNameFilterFactory.createCasCommandNameFilter();
    }

    @Test
    public void T1() {
        Assert.assertFalse(factory.isCommandAllowed(Commands.Delete));
    }
    @Test
    public void T2() {
        Assert.assertTrue(factory.isCommandAllowed(Commands.First));
    }
    @Test
    public void T3() {
        Assert.assertFalse(factory.isCommandAllowed(Commands.Intersect));
    }
}

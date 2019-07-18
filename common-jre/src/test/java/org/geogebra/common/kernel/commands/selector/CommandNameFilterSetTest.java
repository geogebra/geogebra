package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.Commands;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * kovacs jozsef
 * 523/1 es csoport
 * kjim1739
 */
public class CommandNameFilterSetTest extends BaseUnitTest {

    private CommandNameFilter factory = null;

    @Before
    public void setupTest() {
        factory = CommandNameFilterFactory.createCasCommandNameFilter();
    }


    @Test
    public void testCommandNameFilterTestIsAvailable() {
        Assert.assertTrue(factory.isCommandAllowed(Commands.Delete));
    }
}

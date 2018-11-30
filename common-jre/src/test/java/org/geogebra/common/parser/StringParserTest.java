package org.geogebra.common.parser;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringParserTest extends BaseUnitTest {

    private StringParser stringParser;

    @Before
    public void setupStringParserTest() {
        stringParser = new StringParser(getKernel().getAlgebraProcessor());
    }

    @Test
    public void testExceptionThrowing() {
        try {
            stringParser.convertToDouble("a");
            Assert.fail("This should have thrown an exception");
        } catch (NumberFormatException ignored) {

        }
    }

    @Test
    public void testConversion() {
        Assert.assertEquals(stringParser.convertToDouble("-1"), -1, DELATA);
    }
}

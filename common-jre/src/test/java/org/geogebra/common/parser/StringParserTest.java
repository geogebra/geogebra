package org.geogebra.common.parser;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class StringParserTest extends BaseUnitTest {

    private StringParser stringParser;

    @Before
    public void setupStringParserTest() {
        stringParser = new StringParser(getKernel().getAlgebraProcessor());
    }

	@Test
	public void testExceptionThrowing() {
		shouldFail("x y");
		shouldFail("xy");
		shouldFail("a");
		shouldFail("(1,1)");
		shouldFail("x");
		shouldFail("1+" + Unicode.IMAGINARY);
	}

	private void shouldFail(String string) {
		Throwable err = null;
		try {
			stringParser.convertToDouble(string);
		} catch (Throwable thrown) {
			err = thrown;
		}
		Assert.assertTrue(err instanceof NumberFormatException);
	}

	@Test
	public void testConversion() {
		shouldParseAs("-1", -1);
		shouldParseAs("-1,5", -1.5);
		shouldParseAs("360deg", 2 * Math.PI);
	}

	private void shouldParseAs(String string, double i) {
		Assert.assertEquals(stringParser.convertToDouble(string), i, DELTA);
	}
}

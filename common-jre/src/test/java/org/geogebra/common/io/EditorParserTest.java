package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.util.Unicode;

public class EditorParserTest {

	private static void parsesAs(String input, String serialized) {
		try {
			Parser parser = new Parser(new MetaModel());
			GeoGebraSerializer serializer = new GeoGebraSerializer();
			String result = serializer.serialize(parser.parse(input));
			assertEquals(serialized, result);
		} catch (ParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void trigonometricFunctionPowerTest() {
		String oneOverCosPower = "(1)/(cos^(4)(x))";
		parsesAs("1/cos^(4)(x)", oneOverCosPower);
		parsesAs("1/cos" + Unicode.SUPERSCRIPT_4 + "(x)", oneOverCosPower);

		String cosSquaredTimesFraction = "cos^(2)(x)(sin(x))/(x)";
		parsesAs("cos^(2)(x)sin(x)/x", cosSquaredTimesFraction);
		parsesAs("cos" + Unicode.SUPERSCRIPT_2 + "(x)sin(x)/x", cosSquaredTimesFraction);

		String expArctan = "e^(tan^(-1)(1))";
		parsesAs("e^(tan^(-1)(1))", expArctan);
		parsesAs("e^(tan" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1))", expArctan);

		String xToArcsin = "x^(sin^(-1)(x))";
		parsesAs("x^(sin^(-1)(x))", xToArcsin);
		parsesAs("x^(sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x))", xToArcsin);
	}
}

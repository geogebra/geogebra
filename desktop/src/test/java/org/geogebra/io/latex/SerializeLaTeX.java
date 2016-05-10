package org.geogebra.io.latex;

import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.MetaModelParser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.Resource;


public class SerializeLaTeX {
	static Parser parser;
	private static GeoGebraSerializer serializer;

	@BeforeClass
	public static void prepare() {
		FactoryProvider.INSTANCE = new FactoryProviderDesktop();
		MetaModel m = new MetaModelParser().parse(new Resource().loadResource(
				"/com/himamis/retex/editor/desktop/meta/Octave.xml"));
		parser = new Parser(m);
		serializer = new GeoGebraSerializer();
	}

	@Test
	public void testAtoms() {
		checkCannon("a", "a");
	}

	@Test
	public void testExpr() {
		checkCannon("1 * 2", "1*2");
		checkCannon("(1 * 2)", "(1*2)");
		
	}

	@Test
	public void testPoint() {
		checkCannon("(1, 2)", "(1,2)");
		checkCannon("(1 ; 2)", "(1;2)");

	}

	private void checkCannon(String input, String output) {
		MathFormula mf = null;
		try {
			mf = parser.parse(input);
			System.out.println(mf.getRootComponent());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertEquals(output, serializer.serialize(mf));
		
	}

}

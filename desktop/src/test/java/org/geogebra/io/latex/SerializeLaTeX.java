package org.geogebra.io.latex;

import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.junit.Assert;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.MetaModelParser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.Resource;


public class SerializeLaTeX {

	@Test
	public void test() {
		FactoryProvider.INSTANCE = new FactoryProviderDesktop();
		MetaModel m = new MetaModelParser().parse(new Resource().loadResource(
				"/com/himamis/retex/editor/desktop/meta/Octave.xml"));
		Parser parser = new Parser(m);
		MathFormula mf = null;
		try {
			mf = parser.parse("(1; 2)");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GeoGebraSerializer serializer = new GeoGebraSerializer();
		Assert.assertEquals("(1; 2)", serializer.serialize(mf));
	}

}

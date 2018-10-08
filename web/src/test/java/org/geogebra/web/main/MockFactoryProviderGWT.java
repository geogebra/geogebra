package org.geogebra.web.main;

import org.geogebra.web.xml.ParserD;

import com.himamis.retex.renderer.share.platform.parser.Parser;
import com.himamis.retex.renderer.share.platform.parser.ParserFactory;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class MockFactoryProviderGWT extends FactoryProviderGWT {


	protected ParserFactory createParserFactory() {
		// TODO Auto-generated method stub
		return new ParserFactory() {

			@Override
			public Parser createParser() {
				return new ParserD();
			}

		};
	}


}

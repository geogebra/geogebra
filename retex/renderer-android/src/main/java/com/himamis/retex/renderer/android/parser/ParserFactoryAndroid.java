package com.himamis.retex.renderer.android.parser;

import com.himamis.retex.renderer.share.platform.parser.Parser;
import com.himamis.retex.renderer.share.platform.parser.ParserFactory;

public class ParserFactoryAndroid extends ParserFactory {

	@Override
	public Parser createParser() {
		return new ParserA();
	}

}

package org.geogebra.web.mml;


import org.geogebra.common.io.MathMLParser;

import com.google.gwt.core.client.EntryPoint;



/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MML2LaTeX implements EntryPoint {

	final private static MathMLParser mathmlParserLaTeX = new MathMLParser(
			false);

	private static String convert(String mml) {
		return mathmlParserLaTeX.parse(mml, false, false);
	}

	public void onModuleLoad() {
		exportJS();
	
	}

	private native void exportJS() /*-{
		$wnd.toLaTeX = function(mml) {
			return @org.geogebra.web.mml.MML2LaTeX::convert(Ljava/lang/String;)(mml);
		}
	}-*/;



	
}

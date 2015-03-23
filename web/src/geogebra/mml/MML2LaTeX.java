package geogebra.mml;


import geogebra.common.io.MathMLParser;

import com.google.gwt.core.client.EntryPoint;



/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MML2LaTeX implements EntryPoint {

	private static MathMLParser mathmlParserLaTeX;

	private static String convert(String mml) {
		return mathmlParserLaTeX.parse(mml, false, false);
	}

	public void onModuleLoad() {
		mathmlParserLaTeX = new MathMLParser(false);
		exportJS();
	
	}

	private native void exportJS() /*-{
		$wnd.toLaTeX = function(mml) {
			return @geogebra.mml.MML2LaTeX::convert(Ljava/lang/String;)(mml);
		}
	}-*/;



	
}

package org.geogebra.web.mml;

import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.util.ExternalAccess;

import com.google.gwt.core.client.EntryPoint;

/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MML2LaTeX implements EntryPoint {

	final private static MathMLParser MATHML_PARSER_LATEX = new MathMLParser(
			false);

	@ExternalAccess
	private static String convert(String mml) {
		return MATHML_PARSER_LATEX.parse(mml, false, false);
	}

	@Override
	public void onModuleLoad() {
		exportJS();
	}

	private native void exportJS() /*-{
		$wnd.toLaTeX = function(mml) {
			return @org.geogebra.web.mml.MML2LaTeX::convert(Ljava/lang/String;)(mml);
		}
	}-*/;
}

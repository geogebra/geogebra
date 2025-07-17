package org.geogebra.web.mml;

import org.geogebra.common.io.MathMLParser;

import com.google.gwt.core.client.EntryPoint;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;

/**
 * Entry point for MML to LaTeX converter, exports toLaTeX global function
 */
public class MML2LaTeX implements EntryPoint {

	final private static MathMLParser MATHML_PARSER_LATEX = new MathMLParser(
			false);

	@JsFunction
	public interface Converter {
		/**
		 * @param input MathML string
		 * @return LaTeX string
		 */
		String convert(String input);
	}

	@Override
	public void onModuleLoad() {
		Converter converter = mml -> MATHML_PARSER_LATEX.parse(mml, false, false);
		Js.asPropertyMap(DomGlobal.window).set("toLaTeX", converter);
	}

}

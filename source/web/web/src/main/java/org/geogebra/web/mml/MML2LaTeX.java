/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

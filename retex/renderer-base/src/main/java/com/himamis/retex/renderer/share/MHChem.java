package com.himamis.retex.renderer.share;

public class MHChem {

	/**
	 * @param input
	 *            mhchem syntax
	 * @return input converted to LaTeX syntax
	 */
	public static String parse(String input) {

		// example for testing
		// input CO2 + C -> 2 CO
		// return "{\\mathrm{CO}{\\vphantom{X}}_{\\smash[t]{2}} {}+{}
		// \\mathrm{C} {}\\mathrel{\\longrightarrow}{} 2\\,\\mathrm{CO}}";

		return "\\backslash ce \\text{ not implemented}";
	}

}

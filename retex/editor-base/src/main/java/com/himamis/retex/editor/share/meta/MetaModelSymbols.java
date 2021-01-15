package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaCharacter.OPERATOR;
import static com.himamis.retex.editor.share.meta.MetaCharacter.SYMBOL;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

class MetaModelSymbols {

	private static MetaSymbol createSymbol(String name, String cas,
			String tex, char unicode, int type) {
		return new MetaSymbol(name, cas, tex, unicode, type);
	}

	private static MetaSymbol createOperator(String name, String cas,
			String tex,  char unicode) {
		return createSymbol(name, cas, tex, unicode, OPERATOR);
	}

	private static MetaSymbol createOperator(String name, String cas,
			String tex) {
		char key = name.length() == 1 ? name.charAt(0) : 0;

		return createOperator(name, cas, tex, key);
	}

	private static MetaSymbol createOperator(String name, String tex,
			char unicode) {
		return createOperator(name, name, tex, unicode);
	}

	private static MetaSymbol createOperator(String name) {
		return createOperator(name, name, name);
	}

	private static MetaSymbol createSymbol(String name, String cas,
			String tex, char unicode) {
		return createSymbol(name, cas, tex, unicode, SYMBOL);
	}

	private static MetaSymbol createSymbol(String name, String tex,
			char unicode) {
		return createSymbol(name, name, tex, unicode);
	}

	MapMetaGroup createOperators() {
		MapMetaGroup collection = new MapMetaGroup();

		collection.addComponent(createOperator("-"));
		collection.addComponent(createOperator(Unicode.MINUS + ""));
		collection.addComponent(createOperator("+"));
		collection.addComponent(createOperator("*", "\\cdot{}", '*'));
		collection.addComponent(createOperator("/"));
		collection.addComponent(createOperator("\\"));
		// removed GGB-2539
		// collection.addComponent(createOperator("'"));
		// collection.addComponent(createOperator("!"));
		collection.addComponent(createOperator(":"));
		collection.addComponent(createOperator("="));
		// APPS-557
		collection.addComponent(createOperator(Unicode.COLON_EQUALS + ""));
		collection.addComponent(createOperator("<"));
		collection.addComponent(createOperator(">"));
		collection.addComponent(createOperator(","));
		collection.addComponent(createOperator(";"));

		collection.addComponent(createOperator("percent", "\\%", '%'));

		// GGB-2178 want $ to behave like a letter
		// so don't want this here
		// collection.addComponent(createOperator("dollar", "\\$", '$'));

		// don't want this, see \cdot
		// collection.addComponent(createOperator("times", "\\times", '*'));

		collection.addComponent(createOperator("div", "/", '/'));
		collection.addComponent(
				createOperator("ne", "!=", "\\ne{}", Unicode.NOTEQUAL));
		collection.addComponent(createOperator("equal", "==",
				Unicode.QUESTEQ + "", Unicode.QUESTEQ));
		collection.addComponent(
				createOperator("equiv", "NaN", "\\equiv{}", '\u2261'));

		// don't want this, ! should be factorial
		// also used for !=
		// collection.addComponent(createOperator("neg", "!", "\\neg", '!'));

		collection
				.addComponent(
						createOperator("vee", "||", "\\vee{}", Unicode.OR));
		collection.addComponent(
				createOperator("oplus", "NaN", "\\oplus{}", Unicode.XOR));
		collection.addComponent(
				createOperator("wedge", "&&", "\\wedge{}", Unicode.AND));
		collection
				.addComponent(createOperator("implication", "->", "\\implies{}",
				Unicode.IMPLIES));

		// operator not symbol
		collection.addComponent(
				createOperator("otimes", "\\otimes{}", Unicode.VECTOR_PRODUCT));

		collection.addComponent(createOperator("<"));
		collection.addComponent(createOperator(">"));
		collection.addComponent(
				createOperator("leq", "<=", "\\leq{}", Unicode.LESS_EQUAL));
		collection.addComponent(
				createOperator("geq", ">=", "\\geq{}", Unicode.GREATER_EQUAL));
		collection
				.addComponent(createOperator("ll", "NaN", "\\ll{}", '\u226a'));
		collection
				.addComponent(createOperator("gg", "NaN", "\\gg{}", '\u226b'));

		collection
				.addComponent(
						createOperator("sim", "NaN", "\\sim{}", '\u223c'));
		collection.addComponent(
				createOperator("approx", "NaN", "\\approx{}", '\u2248'));
		collection.addComponent(
				createOperator("simeq", "NaN", "\\simeq{}", '\u2243'));
		collection.addComponent(
				createOperator("propto", "NaN", "\\propto{}", '\u221d'));

		collection.addComponent(
				createOperator("forall", "NaN", "\\forall{}", '\u2200'));
		collection.addComponent(
				createOperator("exists", "NaN", "\\exists{}", '\u2203'));

		collection.addComponent(createOperator("perpendicular", "\\perp{}",
				Unicode.PERPENDICULAR));
		collection.addComponent(createOperator("\u27c2"));
		collection.addComponent(
				createOperator("parallel", "\\parallel{}", Unicode.PARALLEL));

		collection.addComponent(createOperator("subset", "NaN", "\\subset{}",
				Unicode.IS_SUBSET_OF_STRICT));
		collection.addComponent(
				createOperator("supset", "NaN", "\\supset{}", '\u2283'));
		collection
				.addComponent(createOperator("subseteq", "NaN", "\\subseteq{}",
				Unicode.IS_SUBSET_OF));
		collection.addComponent(
				createOperator("supseteq", "NaN", "\\supseteq{}", '\u2287'));
		collection
				.addComponent(
						createOperator("cup", "NaN", "\\cup{}", '\u222a'));
		collection
				.addComponent(
						createOperator("cap", "NaN", "\\cap{}", '\u2229'));
		collection.addComponent(
				createOperator("in", "NaN", "\\in{}", Unicode.IS_ELEMENT_OF));
		collection.addComponent(
				createOperator("empty", "NaN", "\\emptyset{}", '\u2205'));

		collection.addComponent(
				createOperator("pm", "NaN", "\\pm{}", Unicode.PLUSMINUS));
		collection.addComponent(
				createOperator("prime", "NaN", "\\prime{}", '\u2032'));
		collection.addComponent(
				createOperator("circ", "NaN", "\\circ{}", '\u2218'));
		collection.addComponent(
				createOperator("partial", "NaN", "\\partial{}", '\u2202'));

		collection.addComponent(
				createOperator("leftarrow", "NaN", "\\leftarrow{}", '\u2190'));
		collection.addComponent(
				createOperator("rightarrow", "NaN", "\\rightarrow{}",
						'\u2192'));
		collection.addComponent(createOperator("leftrightarrow", "NaN",
				"\\leftrightarrow{}", '\u2194'));
		collection.addComponent(createOperator("notrightarrow", "NaN",
				"\\not\\rightarrow{}", '\u219b'));
		collection.addComponent(createOperator("notleftrightarrow", "NaN",
				"\\not\\leftrightarrow{}", '\u21ae'));
		collection.addComponent(
				createOperator("vectorprod", "\\times{}", '\u2a2f'));

		return collection;
	}

	MapMetaGroup createSymbols() {
		MapMetaGroup collection = new MapMetaGroup();

		collection
				.addComponent(
						createSymbol("inf", "\\infty{}", Unicode.INFINITY));

		// collection.addComponent(createSymbol("vareps", "\\varepsilon{}",
		// '\u03f5'));
		// collection.addComponent(createSymbol("varth", "\\vartheta{}",
		// '\u03b8'));
		// collection.addComponent(createSymbol("varpi", "\\varpi{}",
		// Unicode.varpi));
		// collection.addComponent(createSymbol("varrho", "\\varrho{}",
		// Unicode.varrho));

		for (Greek ch : Greek.values()) {
			collection.addComponent(
					createSymbol(ch.name(), "\\" + ch.getLaTeX() + "{}",
							ch.unicode));
		}

		collection
				.addComponent(
						createSymbol("varsigma", "\\varsigma{}", '\u03c2'));
		collection.addComponent(createSymbol("phi", "\\phi{}", Unicode.phi_symbol));

		collection.addComponent(createSymbol("nabla", "\\nabla{}", '\u2207'));
		collection.addComponent(createSymbol("hbar", "\\hbar{}", '\u0127'));
		collection
				.addComponent(createSymbol("ddagger", "\\ddagger{}", '\u2021'));
		collection.addComponent(
				createSymbol("paragraph", "paragraph", "\\paragraph{}", '\0'));

		collection.addComponent(
				createSymbol("degree", "\\degree{}", Unicode.DEGREE_CHAR));
		collection.addComponent(createSymbol("quotes", "\"", '"'));

		return collection;
	}
}

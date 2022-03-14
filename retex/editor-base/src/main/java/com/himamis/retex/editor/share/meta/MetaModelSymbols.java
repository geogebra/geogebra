package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaCharacter.OPERATOR;
import static com.himamis.retex.editor.share.meta.MetaCharacter.SYMBOL;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

class MetaModelSymbols {

	private static MetaSymbol createSymbol(String cas,
			String tex, char unicode, int type) {
		return new MetaSymbol(cas, tex, unicode, type);
	}

	private static MetaSymbol createOperator(String cas,
			String tex,  char unicode) {
		return createSymbol(cas, tex, unicode, OPERATOR);
	}

	private static MetaSymbol createOperator(String tex, char unicode) {
		return createOperator(String.valueOf(unicode), tex, unicode);
	}

	private static MetaSymbol createOperator(String name) {
		char key = name.length() == 1 ? name.charAt(0) : 0;

		return createOperator(name, name, key);
	}

	private static MetaSymbol createSymbol(String cas,
			String tex, char unicode) {
		return createSymbol(cas, tex, unicode, SYMBOL);
	}

	private static MetaSymbol createSymbol(String tex, char unicode) {
		return createSymbol(String.valueOf(unicode), tex, unicode);
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

		collection.addComponent(createOperator("\\%", '%'));

		// GGB-2178 want $ to behave like a letter
		// so don't want this here
		// collection.addComponent(createOperator("dollar", "\\$", '$'));

		// don't want this, see \cdot
		// collection.addComponent(createOperator("times", "\\times", '*'));

		collection.addComponent(createOperator("/", '/'));
		collection.addComponent(
				createOperator("!=", "\\ne{}", Unicode.NOTEQUAL));
		collection.addComponent(createOperator("==",
				Unicode.QUESTEQ + "", Unicode.QUESTEQ));
		collection.addComponent(
				createOperator("\\equiv{}", '\u2261'));

		// don't want this, ! should be factorial
		// also used for !=
		// collection.addComponent(createOperator("neg", "!", "\\neg", '!'));

		collection
				.addComponent(
						createOperator("||", "\\vee{}", Unicode.OR));
		collection.addComponent(
				createOperator("\\oplus{}", Unicode.XOR));
		collection.addComponent(
				createOperator("&&", "\\wedge{}", Unicode.AND));
		collection
				.addComponent(createOperator("->", "\\implies{}",
				Unicode.IMPLIES));

		// operator not symbol
		collection.addComponent(
				createOperator("\\otimes{}", Unicode.VECTOR_PRODUCT));

		collection.addComponent(createOperator("<"));
		collection.addComponent(createOperator(">"));
		collection.addComponent(
				createOperator("<=", "\\leq{}", Unicode.LESS_EQUAL));
		collection.addComponent(
				createOperator(">=", "\\geq{}", Unicode.GREATER_EQUAL));
		collection
				.addComponent(createOperator("\\ll{}", '\u226a'));
		collection
				.addComponent(createOperator("\\gg{}", '\u226b'));

		collection
				.addComponent(
						createOperator("\\sim{}", '\u223c'));
		collection.addComponent(
				createOperator("\\approx{}", '\u2248'));
		collection.addComponent(
				createOperator("\\simeq{}", '\u2243'));
		collection.addComponent(
				createOperator("\\propto{}", '\u221d'));

		collection.addComponent(
				createOperator("\\forall{}", '\u2200'));
		collection.addComponent(
				createOperator("\\exists{}", '\u2203'));

		collection.addComponent(createOperator("\\perp{}",
				Unicode.PERPENDICULAR));
		collection.addComponent(createOperator("\u27c2"));
		collection.addComponent(
				createOperator("\\parallel{}", Unicode.PARALLEL));

		collection.addComponent(createOperator("\\subset{}",
				Unicode.IS_SUBSET_OF_STRICT));
		collection.addComponent(
				createOperator("\\supset{}", '\u2283'));
		collection
				.addComponent(createOperator("\\subseteq{}",
				Unicode.IS_SUBSET_OF));
		collection.addComponent(
				createOperator("\\supseteq{}", '\u2287'));
		collection
				.addComponent(
						createOperator("\\cup{}", '\u222a'));
		collection
				.addComponent(
						createOperator("\\cap{}", '\u2229'));
		collection.addComponent(
				createOperator("\\in{}", Unicode.IS_ELEMENT_OF));
		collection.addComponent(
				createOperator("\\emptyset{}", '\u2205'));

		collection.addComponent(
				createOperator("\\pm{}", Unicode.PLUSMINUS));
		collection.addComponent(
				createOperator("\\prime{}", '\u2032'));
		collection.addComponent(
				createOperator("\\circ{}", '\u2218'));
		collection.addComponent(
				createOperator("\\partial{}", '\u2202'));

		collection.addComponent(
				createOperator("\\leftarrow{}", '\u2190'));
		collection.addComponent(
				createOperator("\\rightarrow{}",
						'\u2192'));
		collection.addComponent(createOperator(
				"\\leftrightarrow{}", '\u2194'));
		collection.addComponent(createOperator(
				"\\not\\rightarrow{}", '\u219b'));
		collection.addComponent(createOperator(
				"\\not\\leftrightarrow{}", '\u21ae'));
		collection.addComponent(
				createOperator("\\times{}", '\u2a2f'));

		return collection;
	}

	MapMetaGroup createSymbols() {
		MapMetaGroup collection = new MapMetaGroup();

		collection
				.addComponent(
						createSymbol("inf", "\\infty{}", Unicode.INFINITY));

		for (Greek ch : Greek.values()) {
			collection.addComponent(
					createSymbol(ch.name(), "\\" + ch.getLaTeX() + "{}",
							ch.unicode));
		}

		collection
				.addComponent(
						createSymbol("varsigma", "\\varsigma{}", '\u03c2'));
		collection.addComponent(createSymbol("phi", "\\phi{}", Unicode.phi_symbol));

		collection.addComponent(createSymbol("\\nabla{}", '\u2207'));
		collection.addComponent(createSymbol("\\hbar{}", '\u0127'));
		collection
				.addComponent(createSymbol("\\ddagger{}", '\u2021'));
		collection.addComponent(
				createSymbol("\\paragraph{}", '\0'));

		collection.addComponent(
				createSymbol("\\degree{}", Unicode.DEGREE_CHAR));
		collection.addComponent(createSymbol("\"", '"'));

		return collection;
	}
}

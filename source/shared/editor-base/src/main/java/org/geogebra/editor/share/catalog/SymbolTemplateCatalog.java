/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.catalog;

import static org.geogebra.editor.share.catalog.CharacterTemplate.TYPE_OPERATOR;
import static org.geogebra.editor.share.catalog.CharacterTemplate.TYPE_SYMBOL;

import org.geogebra.editor.share.util.Greek;
import org.geogebra.editor.share.util.Unicode;

/**
 * Catalog of mathematical symbols and operators.
 */
class SymbolTemplateCatalog {

	private static SymbolTemplate createSymbol(String cas,
			String tex, char unicode, int type) {
		return new SymbolTemplate(cas, tex, unicode, type);
	}

	private static SymbolTemplate createOperator(String cas,
			String tex, char unicode) {
		return createSymbol(cas, tex, unicode, TYPE_OPERATOR);
	}

	private static SymbolTemplate createOperator(String tex, char unicode) {
		return createOperator(String.valueOf(unicode), tex, unicode);
	}

	private static SymbolTemplate createOperator(String name) {
		char key = name.length() == 1 ? name.charAt(0) : 0;

		return createOperator(name, name, key);
	}

	private static SymbolTemplate createSymbol(String cas,
			String tex, char unicode) {
		return createSymbol(cas, tex, unicode, TYPE_SYMBOL);
	}

	private static SymbolTemplate createSymbol(String tex, char unicode) {
		return createSymbol(String.valueOf(unicode), tex, unicode);
	}

	SymbolTemplateMap createOperators() {
		SymbolTemplateMap collection = new SymbolTemplateMap();

		collection.addSymbol(createOperator("-"));
		collection.addSymbol(createOperator(String.valueOf(Unicode.MINUS)));
		collection.addSymbol(createOperator("+"));
		collection.addSymbol(createOperator("*", "\\cdot{}", '*'));
		collection.addSymbol(createOperator("/"));
		collection.addSymbol(createOperator("\\"));
		// removed GGB-2539
		// collection.addComponent(createOperator("'"));
		// collection.addComponent(createOperator("!"));
		collection.addSymbol(createOperator(":", "\\mathpunct{:}", ':'));
		collection.addSymbol(createOperator("="));
		// APPS-557
		collection.addSymbol(createOperator(String.valueOf(Unicode.COLON_EQUALS)));
		collection.addSymbol(createOperator("<"));
		collection.addSymbol(createOperator(">"));
		collection.addSymbol(createOperator(","));
		collection.addSymbol(createOperator(";"));

		collection.addSymbol(createOperator("\\%", '%'));

		// GGB-2178 want $ to behave like a letter
		// so don't want this here
		// collection.addComponent(createOperator("dollar", "\\$", '$'));

		// don't want this, see \cdot
		// collection.addComponent(createOperator("times", "\\times", '*'));

		collection.addSymbol(createOperator("/", '/'));
		collection.addSymbol(
				createOperator("!=", "\\ne{}", Unicode.NOTEQUAL));
		collection.addSymbol(createOperator("==",
				String.valueOf(Unicode.QUESTEQ), Unicode.QUESTEQ));
		collection.addSymbol(
				createOperator("\\equiv{}", '\u2261'));

		// don't want this, ! should be factorial
		// also used for !=
		// collection.addComponent(createOperator("neg", "!", "\\neg", '!'));

		collection
				.addSymbol(
						createOperator("||", "\\vee{}", Unicode.OR));
		collection.addSymbol(
				createOperator("\\oplus{}", Unicode.XOR));
		collection.addSymbol(
				createOperator("&&", "\\wedge{}", Unicode.AND));
		collection
				.addSymbol(createOperator("->", "\\implies{}",
						Unicode.IMPLIES));

		// operator not symbol
		collection.addSymbol(
				createOperator("\\otimes{}", Unicode.VECTOR_PRODUCT));

		collection.addSymbol(createOperator("<"));
		collection.addSymbol(createOperator(">"));
		collection.addSymbol(
				createOperator("<=", "\\leq{}", Unicode.LESS_EQUAL));
		collection.addSymbol(
				createOperator(">=", "\\geq{}", Unicode.GREATER_EQUAL));
		collection
				.addSymbol(createOperator("\\ll{}", '\u226a'));
		collection
				.addSymbol(createOperator("\\gg{}", '\u226b'));

		collection
				.addSymbol(
						createOperator("\\sim{}", '\u223c'));
		collection.addSymbol(
				createOperator("\\approx{}", '\u2248'));
		collection.addSymbol(
				createOperator("\\simeq{}", '\u2243'));
		collection.addSymbol(
				createOperator("\\propto{}", '\u221d'));

		collection.addSymbol(
				createOperator("\\forall{}", '\u2200'));
		collection.addSymbol(
				createOperator("\\exists{}", '\u2203'));

		collection.addSymbol(createOperator("\\perp{}",
				Unicode.PERPENDICULAR));
		collection.addSymbol(createOperator("\u27c2"));
		collection.addSymbol(
				createOperator("\\parallel{}", Unicode.PARALLEL));

		collection.addSymbol(createOperator("\\subset{}",
				Unicode.IS_SUBSET_OF_STRICT));
		collection.addSymbol(
				createOperator("\\supset{}", '\u2283'));
		collection
				.addSymbol(createOperator("\\subseteq{}",
						Unicode.IS_SUBSET_OF));
		collection.addSymbol(
				createOperator("\\supseteq{}", '\u2287'));
		collection
				.addSymbol(
						createOperator("\\cup{}", '\u222a'));
		collection
				.addSymbol(
						createOperator("\\cap{}", '\u2229'));
		collection.addSymbol(
				createOperator("\\in{}", Unicode.IS_ELEMENT_OF));
		collection.addSymbol(
				createOperator("\\emptyset{}", '\u2205'));

		collection.addSymbol(
				createOperator("\\pm{}", Unicode.PLUSMINUS));
		collection.addSymbol(
				createOperator("\\prime{}", '\u2032'));
		collection.addSymbol(
				createOperator("\\circ{}", '\u2218'));
		collection.addSymbol(
				createOperator("\\partial{}", '\u2202'));

		collection.addSymbol(
				createOperator("\\leftarrow{}", '\u2190'));
		collection.addSymbol(
				createOperator("\\rightarrow{}",
						'\u2192'));
		collection.addSymbol(createOperator(
				"\\leftrightarrow{}", '\u2194'));
		collection.addSymbol(createOperator(
				"\\not\\rightarrow{}", '\u219b'));
		collection.addSymbol(createOperator(
				"\\not\\leftrightarrow{}", '\u21ae'));
		collection.addSymbol(
				createOperator("\\times{}", '\u2a2f'));
		collection.addSymbol(createOperator("*", "\\times{}", Unicode.MULTIPLY));
		collection.addSymbol(createOperator(String.valueOf(Unicode.DIVIDE), "\\div{}",
				Unicode.DIVIDE));

		return collection;
	}

	SymbolTemplateMap createSymbols() {
		SymbolTemplateMap collection = new SymbolTemplateMap();

		collection
				.addSymbol(
						createSymbol("inf", "\\infty{}", Unicode.INFINITY));

		for (Greek ch : Greek.values()) {
			collection.addSymbol(
					createSymbol(ch.name(), "\\" + ch.getLaTeX() + "{}",
							ch.unicode));
		}

		collection
				.addSymbol(
						createSymbol("varsigma", "\\varsigma{}", '\u03c2'));
		collection.addSymbol(createSymbol("phi", "\\phi{}", Unicode.phi_symbol));

		collection.addSymbol(createSymbol("\\nabla{}", '\u2207'));
		collection.addSymbol(createSymbol("\\hbar{}", '\u0127'));
		collection
				.addSymbol(createSymbol("\\ddagger{}", '\u2021'));
		collection.addSymbol(
				createSymbol("\\paragraph{}", '\0'));

		collection.addSymbol(
				createSymbol("\\degree{}", Unicode.DEGREE_CHAR));
		collection.addSymbol(createSymbol("\"", '"'));

		return collection;
	}
}

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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.controller.ExpressionReader;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.serializer.ScreenReaderSerializer;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.Unicode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class EditorScreenReaderTest {

	private static Parser parser;
	private static AppCommon app;

	/**
	 * Initialize app and parser.
	 */
	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		app = AppCommonFactory.create3D();
		TemplateCatalog m = new TemplateCatalog();
		parser = new Parser(m);
	}

	@Test
	void testReaderQuadratic() {
		checkReader("1+x^2", "start of formula 1 plus x squared",
				"after 1 before plus", "after plus before x",
				"after x before superscript", "start of superscript before 2",
				"end of superscript after 2",
				"end of formula 1 plus x squared");
	}

	@Test
	void testReaderPower() {
		checkReader("x^3+x^4+1",
				"start of formula x cubed plus x to the power of 4 end power plus 1",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"after plus before x", "after x before superscript",
				"start of superscript before 4", "end of superscript after 4",
				"after x to the power of 4 end power before plus",
				"after plus before 1",
				"end of formula x cubed plus x to the power of 4 end power plus 1");
	}

	@Test
	void testIncompletePower() {
		checkReader("x^3+", "start of formula x cubed plus",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"end of formula x cubed plus");
	}

	@Test
	void testIncompleteFraction() {
		checkReader("x^3/()",
				"start of formula start of fraction x cubed over end of fraction",
				"start of numerator before x", "after x before superscript",
				"start of superscript before 3", "end of superscript after 3",
				"end of numerator after x cubed", "denominator blank",
				"end of formula start of fraction x cubed over end of fraction");
	}

	@Test
	void testIncompleteSqrt() {
		checkReader("sqrt(x+)",
				"start of formula start square root x plus end root",
				"start of square root before x", "after x before plus",
				"end of square root after plus",
				"end of formula start square root x plus end root");
	}

	@Test
	void testSin() {
		checkReader("sin(x+1)",
				"start of formula sin open parenthesis x plus 1 close parenthesis",
				"before sin", "after s before in", "after si before n",
				"after sin before open parenthesis", "start of parentheses before x",
				"after x before plus", "after plus before 1",
				"end of parentheses after 1",
				"end of formula sin open parenthesis x plus 1 close parenthesis");
	}

	@Test
	void testMinusSin() {
		checkReader("3-sin(x)",
				"start of formula 3 minus sin open parenthesis x close parenthesis",
				"after 3 before minus", "after minus before function",
				"before sin", "after s before in", "after si before n",
				"after sin before open parenthesis", "start of parentheses before x",
				"end of parentheses after x",
				"end of formula 3 minus sin open parenthesis x close parenthesis");
	}

	@Test
	void testPlusSin() {
		checkReader("3+sin(x)",
				"start of formula 3 plus sin open parenthesis x close parenthesis",
				"after 3 before plus", "after plus before function",
				"before sin", "after s before in", "after si before n",
				"after sin before open parenthesis", "start of parentheses before x",
				"end of parentheses after x",
				"end of formula 3 plus sin open parenthesis x close parenthesis");
	}

	@Test
	void testCbrt() {
		checkReader("cbrt(x+1)",
				"start of formula start cube root x plus 1 end root",
				"start of cube root before x", "after x before plus",
				"after plus before 1", "end of cube root after 1",
				"end of formula start cube root x plus 1 end root");
	}

	@Test
	void testNroot() {
		checkReader("nroot(x,4)", "start of formula start 4th root x end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x", "end of radicand after x",
				"end of formula start 4th root x end root");
	}

	@Test
	void testNrootIncomplete() {
		checkReader("nroot(x+,4)",
				"start of formula start 4th root x plus end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x", "after x before plus",
				"end of radicand after plus",
				"end of formula start 4th root x plus end root");
	}

	@Test
	void testQuotes() {
		checkReader("\"a{b}c\"",
				"start of formula \"a open brace b close brace c\"",
				"start of quotes before a", "after a before open brace",
				"after open brace before b", "after b before close brace",
				"after close brace before c",
				"end of quotes after c",
				"end of formula \"a open brace b close brace c\"");
	}

	@Test
	void testAbs() {
		checkReader("abs(x+1)",
				"start of formula start of absolute value x plus 1 end of absolute value",
				"start of absolute value before x", "after x before plus",
				"after plus before 1", "end of absolute value after 1",
				"end of formula start of absolute value x plus 1 end of absolute value");
	}

	@Test
	void testReaderSqrt() {
		checkReader("1+sqrt(x^2+2x+1/x+33)",
				"start of formula 1 plus start square root x squared plus 2x"
						+ " plus start of fraction 1 over x end of fraction plus 33 end root",
				"after 1 before plus", "after plus before square root",
				"start of square root before x( squared)?",
				"after x before superscript", "start of superscript before 2",
				"end of superscript after 2", "after x squared before plus",
				"after plus before 2x", "after 2 before x",
				"after 2x before plus", "after plus before fraction",
				"start of numerator before 1", "end of numerator after 1",
				"start of denominator before x", "end of denominator after x",
				"after fraction before plus", "after plus before 33",
				"after 3 before 3", "end of square root after 33",
				"end of formula 1 plus start square root x squared plus 2"
						+ "x plus start of fraction 1 over x end of fraction plus 33 end root");
	}

	@Test
	void testReaderSqrt2() {
		checkReader("sqrt(x)",
				"start of formula start square root x end root",
				"start of square root before x", "end of square root after x",
				"end of formula start square root x end root");
	}

	@Test
	void testReaderSqrtPi() {
		checkReader("sqrt(" + Unicode.pi + ")",
				"start of formula start square root pi end root",
				"start of square root before pi", "end of square root after pi",
				"end of formula start square root pi end root");
	}

	@Test
	void testBrackets() {
		checkReader("2*(3+4)-2",
				"start of formula 2 times open parenthesis 3 plus 4 close parenthesis minus 2",
				"after 2 before times", "after times before open parenthesis",
				"start of parentheses before 3", "after 3 before plus",
				"after plus before 4", "end of parentheses after 4",
				"after close parenthesis before minus", "after minus before 2",
				"end of formula 2 times open parenthesis 3 plus 4 close parenthesis minus 2");
	}

	@Test
	void testBracketsIncomplete() {
		checkReader("3-()", "start of formula 3 minus empty parentheses",
				"after 3 before minus", "after minus before open parenthesis",
				"parentheses blank",
				"end of formula 3 minus empty parentheses");
	}

	@Test
	void testGreek() {
		checkReader("2*pi*x", "start of formula 2 times pi times x",
				"after 2 before times", "after times before pi",
				"after p before i", "after pi before times",
				"after times before x", "end of formula 2 times pi times x");
	}

	@Test
	void testFunction() {
		checkReader("f(x)=x^2",
				"start of formula f open parenthesis x close parenthesis =x squared",
				"before f", "after f before open parenthesis", "start of parentheses before x",
				"end of parentheses after x");
	}

	@Test
	void testPoint() {
		checkReader("B=$point(1,2)",
				"start of formula B= open parenthesis 1 comma 2 close parenthesis",
				"after B before =", "after = before open parenthesis",
				"start of coordinate before 1", "end of coordinate after 1");
	}

	@Test
	void testComma() {
		checkReader("f(1,2)",
				"start of formula f open parenthesis 1 comma 2 close parenthesis",
				"before f", "after f before open parenthesis", "start of parentheses before 1",
				"after 1 before comma", "after comma before 2");
	}

	@Test
	void testEmptyFunction() {
		checkReader("f()",
				"start of formula f empty parentheses",
				"before f", "after f before open parenthesis", "parentheses blank",
				"end of formula f empty parentheses");
	}

	@Test
	void shouldNotRemoveCommasForPoints() throws ParseException {
		Parser p = new Parser(new TemplateCatalog());
		Formula mf = p.parse("(1,2)");
		SequenceNode argument = ((ArrayNode) mf.getRootNode()
				.getChild(0)).getChild(0);
		StringBuilder desc = new StringBuilder();
		for (Node comp: argument) {
			desc.append(ScreenReaderSerializer.fullDescription(comp, null));
		}
		assertEquals("1,2", desc.toString());
		GeoGebraSerializer gs = new GeoGebraSerializer(null);
		gs.setComma("");
		assertEquals("(1,2)", gs.serialize(mf));
	}

	private static void checkReader(String input, String... output) {
		Formula mf = LaTeXSerializationTest.checkLaTeXRender(parser, input);

		SyntaxAdapterImpl adapter = new SyntaxAdapterImpl(app.getKernel());
		final MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), adapter);
		MathFieldInternal mfi = mathField.getInternal();
		mfi.setFormula(Objects.requireNonNull(mf));
		CursorController.firstField(mfi.getEditorState());
		mfi.update();
		ExpressionReader er = ScreenReader.getExpressionReader(app);
		List<String> readerOutputs = new ArrayList<>();
		boolean fuzzyMatch = true;
		for (String s : output) {
			String readerOutput = mfi.getEditorState().getDescription(er, null)
					.replaceAll(" +", " ");
			if (!readerOutput.matches(s)) {
				fuzzyMatch = false;
			}
			readerOutputs.add(readerOutput);
			CursorController.nextCharacter(mfi.getEditorState());
			mfi.update();
		}
		if (!fuzzyMatch) {
			assertEquals(String.join("\n", output), String.join("\n", readerOutputs));
		}
	}
}

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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.gui.view.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:LineLengthCheck",
		"checkstyle:variableDeclarationUsageDistanceCheck"})
public class AlgebraViewItemTest extends BaseAppTestSetup {

	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	@Test
	public void testPointInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("(1,2)");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"A\\, = \\,\\left(1,\\;2 \\right)",
				item.getInputRow().previewLaTex);
		assertEquals(
				"A=$point(1,2)",
				item.getInputRow().editorLaTeX);

		assertFalse(item.getOutputRow().isVisible);
	}

	@Test
	public void testLineInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("Line((0,0), (1,1))");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"f\\mathpunct{:}\\,Line\\left(\\left(0,\\;0 \\right), \\left(1,\\;1 \\right) \\right)",
				item.getInputRow().previewLaTex);
		assertEquals(
				"f: Line($point(0,0),$point(1,1))",
				item.getInputRow().editorLaTeX);

		assertTrue(item.getOutputRow().isVisible);
		assertEquals(AlgebraOutputOperator.EQUALS, item.getOutputRow().outputFormat);
		assertNull(item.getOutputRow().nextOutputFormat);
		assertEquals(
				"-x + y\\, = \\,0",
				item.getOutputRow().laTeX);
		assertFalse(item.getOutputRow().isMoreButtonVisible);
	}

	@Test
	public void testTangentInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement circle = evaluateGeoElement("c: Circle((0, 0), 5)");
		GeoElement a = evaluateGeoElement("A = (6, 6)");
		// this will create two output rows, tangents f & g
		GeoElementND[] geos = evaluate("Tangent(A, c)");
		AlgebraViewItem f = new AlgebraViewItem((GeoElement) geos[0]);
		AlgebraViewItem g = new AlgebraViewItem((GeoElement) geos[1]);

		// f
		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, f.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, f.getHeader().marbleIcon);

		assertTrue(f.getInputRow().isVisible);
		assertFalse(f.getInputRow().isTextCell);
		assertTrue(f.getInputRow().isMoreButtonVisible);
		assertEquals(
				"Tangent\\left(A, c \\right)",
				f.getInputRow().previewLaTex);
		assertEquals(
				"Tangent(A,c)",
				f.getInputRow().editorLaTeX);

		assertTrue(f.getOutputRow().isVisible);
		assertEquals(AlgebraOutputOperator.EQUALS, f.getOutputRow().outputFormat);
		assertNull(f.getOutputRow().nextOutputFormat);
		assertEquals(
				"f\\mathpunct{:}\\,1.0601439164996x - 6.7731894168338y\\, = \\,-34.2782730020052",
				f.getOutputRow().laTeX);
		assertFalse(f.getOutputRow().isMoreButtonVisible);

		// g
		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, g.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, g.getHeader().marbleIcon);
		assertFalse(g.getInputRow().isVisible);
		assertTrue(g.getOutputRow().isVisible);
		assertNull(g.getOutputRow().outputFormat);
		assertNull(g.getOutputRow().nextOutputFormat);
		assertEquals(
				"g\\mathpunct{:}\\,6.7731894168338x - 1.0601439164996y\\, = \\,34.2782730020052",
				g.getOutputRow().laTeX);
		assertTrue(g.getOutputRow().isMoreButtonVisible);
	}

	@Test
	public void testEquationInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("x^2/sqrt(2)=1");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"eq1\\mathpunct{:}\\,\\frac{x^{2}}{\\sqrt{2}}\\, = \\,1",
				item.getInputRow().previewLaTex);
		assertEquals(
				"eq1: (x²)/(sqrt(2))=1",
				item.getInputRow().editorLaTeX);

		assertFalse(item.getOutputRow().isVisible);
	}

	@Test
	public void testTextInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("\"text\"");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		// note: GeoText is euclidian visible (ACTIVE) on creation, but currently hidden
		// post-creation in client code (see https://git.geogebra.org/ggb/geogebra/-/issues/13)
		assertEquals(AlgebraViewItem.MarbleState.ACTIVE, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.QUOTE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertTrue(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"text1 = “text”",
				item.getInputRow().previewLaTex);
		assertEquals(
				"text",
				item.getInputRow().editorLaTeX);

		assertFalse(item.getOutputRow().isVisible);
	}

	@Test
	public void testMinusOneInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("(-1)(9)");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		assertEquals(AlgebraViewItem.MarbleState.DISABLED, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"a\\, = \\,-1 \\cdot 9",
				item.getInputRow().previewLaTex);
		assertEquals(
				"a=-1*9",
				item.getInputRow().editorLaTeX);

		assertTrue(item.getOutputRow().isVisible);
		assertEquals(AlgebraOutputOperator.EQUALS, item.getOutputRow().outputFormat);
		assertNull(item.getOutputRow().nextOutputFormat);
		assertEquals("-9", item.getOutputRow().laTeX);
		assertFalse(item.getOutputRow().isMoreButtonVisible);
	}

	@Test
	public void testPercentageInGraphing() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geo = evaluateGeoElement("5%*5+5");
		AlgebraViewItem item = new AlgebraViewItem(geo);

		assertEquals(AlgebraViewItem.MarbleState.DISABLED, item.getHeader().marbleState);
		assertEquals(AlgebraViewItem.MarbleIcon.NONE, item.getHeader().marbleIcon);

		assertTrue(item.getInputRow().isVisible);
		assertFalse(item.getInputRow().isTextCell);
		assertTrue(item.getInputRow().isMoreButtonVisible);
		assertEquals(
				"a\\, = \\,5\\% \\cdot 5 + 5",
				item.getInputRow().previewLaTex);
		assertEquals(
				"a=5%*5+5",
				item.getInputRow().editorLaTeX);

		assertTrue(item.getOutputRow().isVisible);
		assertEquals(AlgebraOutputOperator.EQUALS, item.getOutputRow().outputFormat);
		assertEquals(AlgebraOutputFormat.APPROXIMATION, item.getOutputRow().nextOutputFormat);
		assertEquals(
				"\\frac{21}{4}",
				item.getOutputRow().laTeX);
		assertFalse(item.getOutputRow().isMoreButtonVisible);
	}
}

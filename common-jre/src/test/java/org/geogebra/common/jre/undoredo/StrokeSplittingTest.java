package org.geogebra.common.jre.undoredo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.BaseControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.main.undo.AppState;
import org.junit.Before;
import org.junit.Test;

public class StrokeSplittingTest extends BaseControllerTest {

	EuclidianController ec;
	EuclidianStyleBarSelection selection;

	@Before
	public void setupApp() {
		getApp().setConfig(new AppConfigNotes());
		getApp().setUndoActive(true);
	}

	/**
	 * initialize EuclidianStyleBarSelection
	 */
	public void init() {
		ec = getApp().getActiveEuclidianView().getEuclidianController();
		selection = new EuclidianStyleBarSelection(getApp(), ec);
	}

	/**
	 * drawAndSelectStroke
	 */
	public void drawAndSelectStroke() {
		drawStroke();
		selectPartOfStroke();
	}

	/**
	 * drawStroke
	 */
	public void drawStroke() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(400, 100);
	}

	/**
	 * drawMultipleStrokes
	 */
	public void drawMultipleStrokes(int y) {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, y);
		dragEnd(400, y);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
	}

	/**
	 * selectPartOfStroke
	 */
	public void selectPartOfStroke() {
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 150);
		dragEnd(250, 50);
	}

	@Test
	public void splitStrokeByDragging() {
		init();
		drawAndSelectStroke();
		dragStart(250, 100);
		dragEnd(400, 200);
		assertSelected(lookup("stroke2"));
	}

	@Test
	public void splitMultipleStrokesByDragging() {
		init();
		drawMultipleStrokes(100);
		drawMultipleStrokes(100);
		drawMultipleStrokes(100);
		drawMultipleStrokes(100);
		selectPartOfStroke();
		dragStart(250, 100);
		dragEnd(400, 200);

		int undoPoints = getConstruction().getUndoManager().getHistorySize();
		assertEquals(undoPoints, 6);
		String s3XMLOriginal = lookup("stroke3").getXML();
		getConstruction().undo(); //undos dragging
		assertNotEquals(s3XMLOriginal, lookup("stroke3").getXML());
		getConstruction().undo(); //undos split stroke
		assertEquals(getConstruction().getUndoManager().getHistorySize(), 4);
		getConstruction().undo();
		getConstruction().undo();
		getConstruction().undo();
		getConstruction().undo();
		assertEquals(getConstruction().getUndoManager().getHistorySize(), 0);
		getConstruction().redo();
		getConstruction().redo();
		getConstruction().redo();
		getConstruction().redo();
		getConstruction().redo();
		getConstruction().redo();
		assertEquals(getConstruction().getUndoManager().getHistorySize(), 6);
		assertThat(lookup("stroke3"), notNullValue());
	}

	@Test
	public void undoRedoStrokeSplitByDragging() {
		init();
		drawAndSelectStroke();
		dragStart(250, 100);
		dragEnd(400, 200);
		String s2Dragged = lookup("stroke2").getXML();
		getConstruction().undo();
		String s2Original = lookup("stroke2").getXML();
		assertNotEquals(s2Dragged, s2Original);
		getConstruction().undo();
		assertThat(lookup("stroke2"), nullValue());
		getConstruction().undo();
		assertThat(lookup("stroke1"), nullValue());
		getConstruction().redo();
		getConstruction().redo();
		getConstruction().redo();
		assertThat(lookup("stroke2"), notNullValue());
	}

	@Test
	public void linePropertiesShouldSplitStroke() {
		init();
		drawAndSelectStroke();
		ArrayList<GeoElement> geos = selection.getGeos();
		EuclidianStyleBarStatic.applyColor(GColor.GREEN, 1, getApp(), geos);

		assertEquals(GColor.GREEN, lookup("stroke2").getObjectColor());
		assertNotEquals(GColor.GREEN, lookup("stroke3").getObjectColor());
	}

	@Test
	public void undoRedoStrokeStylingWithSplitting() {
		init();
		drawAndSelectStroke();
		ArrayList<GeoElement> geos = selection.getGeos();
		EuclidianStyleBarStatic.applyLineStyle(4, 10, getApp(), geos);

		getConstruction().undo();
		assertNotEquals(30, lookup("stroke2").getLineType());
		getConstruction().undo();
		assertThat(lookup("stroke2"), nullValue());
		getConstruction().undo();
		assertThat(lookup("stroke1"), nullValue());
		getConstruction().redo();
		getConstruction().redo();
		assertNotEquals(30, lookup("stroke2").getLineType());
		getConstruction().redo();
		assertEquals(30, lookup("stroke2").getLineType());
	}

	private void assertSelected(GeoElement... geos) {
		assertArrayEquals(getApp().getSelectionManager().getSelectedGeos().toArray(),
				geos);
	}
}

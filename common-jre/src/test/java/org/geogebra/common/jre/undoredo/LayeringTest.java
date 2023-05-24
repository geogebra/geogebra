package org.geogebra.common.jre.undoredo;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.BaseControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.LayerManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LayeringTest extends BaseControllerTest {

	EuclidianController ec;
	EuclidianStyleBarSelection selection;


	private LayerManager layerManager;
	private GeoElement[] geos;


	@Before
	public void setupApp() {
		getApp().setConfig(new AppConfigNotes());
		getApp().setUndoActive(true);
		layerManager = new LayerManager();
		geos = new GeoElement[10];
		for (int i = 0; i < geos.length; i++) {
			geos[i] = createDummyGeo(getConstruction(), i);
			layerManager.addGeo(geos[i]);
		}
	}

	static GeoElement createDummyGeo(Construction construction, int number) {
		GeoElement geo = new GeoPolygon(construction);
		geo.setLabel("p" + number);
		return geo;
	}

	/**
	 * initialize EuclidianStyleBarSelection
	 */
	public void init() {
		ec = getApp().getActiveEuclidianView().getEuclidianController();
		selection = new EuclidianStyleBarSelection(getApp(), ec);
	}


	@Test
	public void testMoveForwardWithUndo() {
		init();
		layerManager.moveForward(Arrays.asList(geos[3], geos[5], geos[9]));
		assertOrdering(0, 1, 2, 4, 6, 7, 8, 3, 5, 9);
		getConstruction().undo();
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		getConstruction().redo();
		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertOrdering(1, 0, 2, 4, 6, 7, 8, 3, 5, 9);
	}

	private void assertOrdering(int... newOrder) {
		assertOrdering(geos, newOrder);
	}

	static void assertOrdering(GeoElement[] geos, int... newOrder) {
		Assert.assertEquals(geos.length, newOrder.length);
		List<Integer> actual = new ArrayList<>();
		List<Integer> expected = new ArrayList<>();
		for (int i = 0; i < geos.length; i++) {
			actual.add((int)geos[newOrder[i]].getOrdering()); //TODO: hi
			expected.add(i);
		}

		// assert once to have nice output when failing.
		Assert.assertEquals(expected, actual);
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

	private void assertSelected(GeoElement... geos) {
		assertArrayEquals(getApp().getSelectionManager().getSelectedGeos().toArray(),
				geos);
	}
}

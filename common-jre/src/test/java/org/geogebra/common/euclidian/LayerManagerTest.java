package org.geogebra.common.euclidian;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LayerManagerTest extends BaseControllerTest {

	private LayerManager layerManager;
	private GeoElement[] geos;

	Construction construction;

	@Before
	public void setupApp() {
		AppCommon app = AppCommonFactory.create();
		construction = app.getKernel().getConstruction();
		layerManager = new LayerManager();
		geos = new GeoElement[10];
		for (int i = 0; i < geos.length; i++) {
			geos[i] = createDummyGeo(construction, i);
			layerManager.addGeo(geos[i]);
		}

		app.setConfig(new AppConfigNotes());
		app.setUndoActive(true);
	}

	void resetGeos() {
		for (int i = 0; i < geos.length; i++) {
			geos[i] = createDummyGeo(construction, i);
			layerManager.addGeo(geos[i]);
		}
	}

	/**
	 *
	 * @param construction the construction.
	 * @param number the number for the label.
	 * @return the geo labeled as number.
	 */
	static GeoElement createDummyGeo(Construction construction, int number) {
		GeoElement geo = new GeoPolygon(construction);
		geo.setLabel("p" + number);
		return geo;
	}

	@Test
	public void testMoveForward() {
 		layerManager.moveForward(asList(geos[3], geos[5], geos[9]));
		assertSorted(geos, asList(0f, 1f, 2f, 4f, 6f, 7f, 8f, 9f, 10f, 11f));

		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertSorted(geos, asList(1f, 1.5f, 2f, 4f, 6f, 7f, 8f, 9f, 10f, 11f));
	}

	@Test
	public void testMoveForwardWithUndo() {
		List<GeoElement> s = asList(geos);
		layerManager.moveForward(asList(geos[3], geos[5], geos[9]));
		assertOrdering(0, 1, 2, 4, 6, 7, 8, 3, 5, 9);
		getConstruction().undo();
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		getConstruction().redo();
		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertOrdering(1, 0, 2, 4, 6, 7, 8, 3, 5, 9);
	}

	@Test
	public void testMoveForwardWithUndoSingle() {
		layerManager.moveForward(asList(geos[3]));
		assertOrdering(0, 1, 2, 4, 3, 5, 6, 7, 8,9);
		getConstruction().undo();
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		getConstruction().redo();
		layerManager.moveForward(Collections.singletonList(geos[9]));
		assertOrdering(0, 1, 2, 4, 3, 5, 6, 7, 8,9);
	}

	@Test
	public void testMoveBackward() {
		layerManager.moveBackward(asList(geos[0], geos[9]));
		assertOrdering(0, 9, 1, 2, 3, 4, 5, 6, 7, 8);

		layerManager.moveBackward(asList(geos[3], geos[5], geos[6]));
		assertOrdering(0, 9, 1, 3, 5, 6, 2, 4, 7, 8);
	}

	@Test
	public void testMoveToFront() {
		layerManager.moveToFront(Collections.singletonList(geos[7]));
		assertSorted(geos, asList(0f, 1f, 2f, 3f, 4f, 5f, 6f, 8f, 9f, 10f));

		layerManager.moveToFront(asList(geos[3], geos[7]));
		assertSorted(geos, asList(0f, 1f, 2f, 4f, 5f, 6f, 8f, 9f, 10f, 11f));

	}

	@Test
	public void testMoveToBack() {
		layerManager.moveToBack(asList(geos[9], geos[6], geos[4]));
		assertOrdering(4, 6, 9, 0, 1, 2, 3, 5, 7, 8);

		layerManager.moveToBack(asList(geos[6], geos[2]));
		assertOrdering(6, 2, 4, 9, 0, 1, 3, 5, 7, 8);
	}

	private void assertOrdering(int... newOrder) {
		assertOrdering(geos, newOrder);
	}

	static void assertSorted(GeoElement[] geos, List<Float> expected) {

		Assert.assertEquals(geos.length, expected.size());

		List<Float> actual = Arrays.stream(geos).sorted((geo1, geo2)
						-> Float.compare(geo1.getOrdering(), geo2.getOrdering()))
				.map(GeoElement::getOrdering).collect(Collectors.toList());

		Assert.assertEquals(actual, expected);

	}

	static void assertOrdering(GeoElement[] geos, int... newOrder) {
		Assert.assertEquals(geos.length, newOrder.length);
		List<Float> actual = new ArrayList<>();
		List<Float> expected = new ArrayList<>();
		for (int i = 0; i < geos.length; i++) {
			actual.add(geos[newOrder[i]].getOrdering());
			expected.add((float)i);
		}

		// assert once to have nice output when failing.
		Assert.assertEquals(expected, actual);
	}

}

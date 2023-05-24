package org.geogebra.common.euclidian;

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
		Construction construction = app.getKernel().getConstruction();
		layerManager = new LayerManager();
		geos = new GeoElement[10];
		for (int i = 0; i < geos.length; i++) {
			geos[i] = createDummyGeo(construction, i);
			layerManager.addGeo(geos[i]);
		}

		app.setConfig(new AppConfigNotes());
		app.setUndoActive(true);
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
		layerManager.moveForward(Arrays.asList(geos[3], geos[5], geos[9]));
		assertOrdering(0, 1, 2, 4, 6, 7, 8, 3, 5, 9);

		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertOrdering(1, 0, 2, 4, 6, 7, 8, 3, 5, 9);
	}

	@Test
	public void testMoveForwardWithUndo() {
		List<GeoElement> s = Arrays.asList(geos);
		//List<Float> depth1 = Arrays.stream(geos).map(geoElement -> {return geoElement.getDepth();}).collect(Collectors.toList());
		layerManager.moveForward(Arrays.asList(geos[3], geos[5], geos[9]));
		assertOrdering(0, 1, 2, 4, 6, 7, 8, 3, 5, 9);
	//	List<Float> depth = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		getConstruction().undo();
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	//	List<Float> depthUndo = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		getConstruction().redo();
		//List<Float> depthRedo = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		layerManager.moveForward(Collections.singletonList(geos[0]));
	//	List<Float> depthForward = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		assertOrdering(1, 0, 2, 4, 6, 7, 8, 3, 5, 9);
	}

	@Test
	public void testMoveForwardWithUndoSingle() {
		List<GeoElement> s = Arrays.asList(geos);
		//List<Float> depth1 = Arrays.stream(geos).map(geoElement -> {return geoElement.getDepth();}).collect(Collectors.toList());
		layerManager.moveForward(Arrays.asList(geos[3]));
		assertOrdering(0, 1, 2, 4, 3, 5, 6, 7, 8,9);
		//	List<Float> depth = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		getConstruction().undo();
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		//	List<Float> depthUndo = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		getConstruction().redo();
		//List<Float> depthRedo = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		layerManager.moveForward(Collections.singletonList(geos[9]));
		//	List<Float> depthForward = Arrays.stream(geos).map(GeoElement::getDepth).collect(Collectors.toList());
		assertOrdering(0, 1, 2, 4, 3, 5, 6, 7, 8,9);
	}

	@Test
	public void testMoveBackward() {
		layerManager.moveBackward(Arrays.asList(geos[0], geos[9]));
		assertOrdering(0, 9, 1, 2, 3, 4, 5, 6, 7, 8);

		layerManager.moveBackward(Arrays.asList(geos[3], geos[5], geos[6]));
		assertOrdering(0, 9, 1, 3, 5, 6, 2, 4, 7, 8);
	}

	@Test
	public void testMoveToFront() {
		layerManager.moveToFront(Collections.singletonList(geos[7]));
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 8, 9, 7);

		layerManager.moveToFront(Arrays.asList(geos[3], geos[7]));
		assertOrdering(0, 1, 2, 4, 5, 6, 8, 9, 3, 7);
	}

	@Test
	public void testMoveToBack() {
		layerManager.moveToBack(Arrays.asList(geos[9], geos[6], geos[4]));
		assertOrdering(4, 6, 9, 0, 1, 2, 3, 5, 7, 8);

		layerManager.moveToBack(Arrays.asList(geos[6], geos[2]));
		assertOrdering(6, 2, 4, 9, 0, 1, 3, 5, 7, 8);
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

}

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

package org.geogebra.common.euclidian;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Before;
import org.junit.Test;

public class LayerManagerTest extends BaseEuclidianControllerTest {

	private LayerManager layerManager;
	private GeoElement[] geos;

	@Before
	public void setupApp() {
		getApp().setConfig(new AppConfigNotes());
		layerManager = new LayerManager();
		geos = new GeoElement[10];
		for (int i = 0; i < geos.length; i++) {
			geos[i] = createDummyGeo(getConstruction(), i);
			layerManager.addGeo(geos[i]);
		}
		getApp().setUndoActive(true);
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
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertSorted(geos, asList(1d, 1.5d, 2d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
	}

	@Test
	public void testMoveForwardWithUndo() {
		layerManager.moveForward(asList(geos[3], geos[5], geos[9]));
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
		getKernel().undo();
		assertSorted(geos, asList(0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d));
		getKernel().redo();
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertSorted(geos, asList(1d, 1.5d, 2d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertSorted(geos, asList(1d, 2d, 3d, 4d, 6d, 7d, 8d, 9d, 10d, 11d));
	}

	@Test
	public void testMoveForwardWithUndoSingle() {
		layerManager.moveForward(asList(geos[3]));
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 4.5d, 5d, 6d, 7d, 8d, 9d));
		getKernel().undo();
		assertSorted(geos, asList(0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d));
		getKernel().redo();
		layerManager.moveForward(Collections.singletonList(geos[9]));
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 4.5d, 5d, 6d, 7d, 8d, 9d));
	}

	@Test
	public void testMoveBackwardWithUndoRedo() {
		layerManager.moveBackward(asList(geos[0], geos[9]));
		assertSorted(geos, asList(-1.0d, 0.0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d));

		layerManager.moveBackward(asList(geos[3], geos[5], geos[6]));
		assertSorted(geos, asList(-1.0, 0.0, 1d, 1.25, 1.5, 1.75, 2d, 4d, 7d, 8d));
		getKernel().undo();
		assertSorted(geos, asList(-1.0d, 0.0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d));
		getKernel().redo();
		assertSorted(geos, asList(-1.0, 0.0, 1d, 1.25, 1.5, 1.75, 2d, 4d, 7d, 8d));
	}

	@Test
	public void testMoveBackward() {
		layerManager.moveBackward(asList(geos[0], geos[9]));
		assertSorted(geos, asList(-1.0d, 0.0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d));

		layerManager.moveBackward(asList(geos[3], geos[5], geos[6]));
		assertSorted(geos, asList(-1.0, 0.0, 1d, 1.25, 1.5, 1.75, 2d, 4d, 7d, 8d));
	}

	@Test
	public void testMoveToFront() {
		layerManager.moveToFront(Collections.singletonList(geos[7]));
		assertSorted(geos, asList(0d, 1d, 2d, 3d, 4d, 5d, 6d, 8d, 9d, 10d));

		layerManager.moveToFront(asList(geos[3], geos[7]));
		assertSorted(geos, asList(0d, 1d, 2d, 4d, 5d, 6d, 8d, 9d, 10d, 11d));
	}

	@Test
	public void testMoveToBack() {
		layerManager.moveToBack(asList(geos[9], geos[6], geos[4]));
		assertSorted(geos, asList(-3d, -2d, -1d, 0d, 1d, 2d, 3d, 5d, 7d, 8d));

		layerManager.moveToBack(asList(geos[6], geos[2]));
		assertSorted(geos, asList(-5d, -4d, -3d, -1d, 0d, 1d, 3d, 5d, 7d, 8d));

	}

	static void assertSorted(GeoElement[] geos, List<Double> expected) {

		assertEquals(geos.length, expected.size());

		List<Double> actual = Arrays.stream(geos)
				.sorted(Comparator.comparingDouble(GeoElement::getOrdering))
				.map(GeoElement::getOrdering).collect(Collectors.toList());

		assertEquals(actual, expected);

	}

	static void assertOrdering(GeoElement[] geos, int... newOrder) {

		List<GeoElement> sorted = Arrays.stream(geos).sorted(Group.orderComparator)
						.collect(Collectors.toList());
		assertEquals(geos.length, newOrder.length);
		List<Integer> actual = new ArrayList<>();
		List<Integer> expected = new ArrayList<>();

		int [] expectedVals = Arrays.stream(newOrder).toArray();

		for (int i = 0; i < sorted.size(); i++) {
			actual.add(Integer.parseInt(sorted.get(i).getLabelSimple().substring(1)));
			expected.add(expectedVals[i]);
		}

		// assert once to have nice output when failing.
		assertEquals(expected, actual);
	}

}

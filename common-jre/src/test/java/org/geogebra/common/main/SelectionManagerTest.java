package org.geogebra.common.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.junit.Before;
import org.junit.Test;

public class SelectionManagerTest extends BaseUnitTest {

	private SelectionManager selectionManager;

	@Before
	public void setupTest() {
		selectionManager = getApp().getSelectionManager();
	}

	@Test
	public void hasNextShouldSkipInvisibleGeos() {
		createSampleGeos();
		assertTrue(selectionManager.hasNext(lookup("firstVisible")));
		assertFalse(selectionManager.hasNext(lookup("lastVisible")));
	}

	private void createSampleGeos() {
		getApp().getGgbApi().setPerspective("G");
		add("firstVisible:(1,1)");
		add("lastVisible:(2,1)");
		GeoElement hidden = add("hidden:(3,1)");
		hidden.setEuclidianVisible(false);
		GeoElement notSelectable = add("notSelectable:(4,1)");
		notSelectable.setSelectionAllowed(false);
	}

	@Test
	public void selectNextShouldSkipInvisibleGeos() {
		getApp().getGgbApi().setPerspective("G");
		createSampleGeos();

		selectionManager.setSelectedGeos(null);
		GeoElement firstVisible = lookup("firstVisible");
		selectionManager.addSelectedGeo(firstVisible);
		// next jumps to second
		selectionManager.selectNextGeo();
		assertTrue(lookup("lastVisible").isSelected());
		// next does not select anything
		assertFalse(selectionManager.selectNextGeo());
		assertEquals(0, selectionManager.selectedGeosSize());
	}

	@Test
	public void selectAllIfGeoHasGroup() {
		ArrayList<GeoElement> geos = geosForGroup();
		getKernel().getConstruction().createGroup(geos);
		selectionManager.addSelectedGeoWithGroup(geos.get(0));
		assertEquals(geos, selectionManager.getSelectedGeos());
	}

	@Test
	public void toggleAllIfGeoHasGroup() {
		ArrayList<GeoElement> geos = geosForGroup();
		getKernel().getConstruction().createGroup(geos);
		selectionManager.addSelectedGeoWithGroup(geos.get(0));
		assertEquals(geos, selectionManager.getSelectedGeos());
		selectionManager.toggleSelectedGeoWithGroup(geos.get(0));
		assertTrue(selectionManager.getSelectedGeos().isEmpty());
	}

	@Test
	public void selectGeoIfNoGroup() {
		GeoElement geo = new GeoPolygon(getKernel().getConstruction());
		selectionManager.addSelectedGeoWithGroup(geo);
		assertEquals(Collections.singletonList(geo), selectionManager.getSelectedGeos());
	}

	@Test
	public void toggleGeoIfNoGroup() {
		GeoElement geo = new GeoPolygon(getKernel().getConstruction());
		selectionManager.addSelectedGeoWithGroup(geo);
		assertEquals(Collections.singletonList(geo), selectionManager.getSelectedGeos());
		selectionManager.toggleSelectedGeoWithGroup(geo);
		assertTrue(selectionManager.getSelectedGeos().isEmpty());
	}

	private ArrayList<GeoElement> geosForGroup() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			GeoPolygon polygon = new GeoPolygon(getKernel().getConstruction());
			polygon.setLabel("label" + i);
			geos.add(polygon);
		}
		return geos;
	}
}

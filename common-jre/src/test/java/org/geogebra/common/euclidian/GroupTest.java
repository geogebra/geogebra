package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Before;
import org.junit.Test;

public class GroupTest {
	private Construction construction;
	private AppCommon app;

	@Before
	public void setUp() {
		AwtFactoryCommon factoryCommon = new AwtFactoryCommon();
		app = new AppCommon(new LocalizationCommon(2), factoryCommon);
		app.setConfig(new AppConfigNotes());
		construction = app.getKernel().getConstruction();
	}

	@Test
	public void testGeosNotGrupped() {
		assertFalse(Group.isInSameGroup(withGivenNumberOfGeos(2)));
	}

	private ArrayList<GeoElement> withGivenNumberOfGeos(int count) {
		ArrayList<GeoElement> geos = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			GeoPolygon polygon = new GeoPolygon(construction);
			polygon.setLabel("label" + i);
			polygon.setOrdering(i + 1);
			geos.add(polygon);
		}
		return geos;
	}

	@Test
	public void testCreateGroup() {
		ArrayList<GeoElement> geos = withGivenNumberOfGeos(3);
		construction.createGroup(geos);
		assertTrue(Group.isInSameGroup(geos));
	}

	@Test
	public void testCreateTwoDifferentGroups() {
		ArrayList<GeoElement> geos1 = withGivenNumberOfGeos(3);
		ArrayList<GeoElement> geos2 = withGivenNumberOfGeos(5);
		construction.createGroup(geos1);
		construction.createGroup(geos2);
		geos1.addAll(geos2);
		assertFalse(Group.isInSameGroup(geos1));
	}

	@Test
	public void testGrouppedGeos() {
		ArrayList<GeoElement> geos = withGivenNumberOfGeos(5);
		Group group = new Group(geos);
		List<GeoElement> result = group.getGroupedGeos();
		assertEquals(geos, result);
	}

	@Test
	public void testGroupElementsOrdering() {
		ArrayList<GeoElement> geos = withGivenNumberOfGeos(10);
		construction.createGroup(geos);
		Group group = geos.get(0).getParentGroup();
		assertEquals(geos.get(0), group.getMinByOrder());
		assertEquals(geos.get(9), group.getMaxByOrder());
	}

	@Test
	public void testRemoveGeoRemovesGroup() {
		ArrayList<GeoElement> geos1 = withGivenNumberOfGeos(2);
		ArrayList<GeoElement> geos2 = withGivenNumberOfGeos(2);
		construction.createGroup(geos1);
		construction.createGroup(geos2);
		geos1.get(0).remove();
		assertEquals(1, construction.getGroups().size());
	}

	// TODO: add tests back in InternalClipboard refactoring
	/*
	@Test
	public void testCopyPasteGroup() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		GeoElement A = new GeoPoint(construction, "A", 0, 0, 1);
		GeoPoint B = new GeoPoint(construction, "B", 3, 0, 1);
		geos.add(A);
		geos.add(B);
		construction.createGroup(geos);
		app.getSelectionManager().setSelectedGeos(geos);
		InternalClipboard.duplicate(app, app.getSelectionManager().getSelectedGeos());
		assertThat(construction.getGroups().size(), equalTo(2));
		assertThat(construction.getGroups().get(0).getGroupedGeos(),
				equalTo(Arrays.asList(A, B)));
		assertThat(construction.getGroups().get(1).getGroupedGeos(),
				equalTo(Arrays.asList(lookup("A_1"), lookup("B_1"))));
	}

	@Test
	public void copyGroupShouldMaintainLayers() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		GeoElement A = new GeoPoint(construction, "A", 0, 0, 1);
		GeoPoint B = new GeoPoint(construction, "B", 3, 0, 1);
		geos.add(A);
		geos.add(B);
		construction.getLayerManager().moveForward(Collections.singletonList(A));
		construction.createGroup(geos);
		assertEquals(0, lookup("B").getOrdering());
		assertEquals(1, lookup("A").getOrdering());
		InternalClipboard.duplicate(app, geos);
		assertEquals(0, lookup("B").getOrdering());
		assertEquals(1, lookup("A").getOrdering());
		assertEquals(2, lookup("B_1").getOrdering());
		assertEquals(3, lookup("A_1").getOrdering());
	}

	private GeoElement lookup(String label) {
		return app.getKernel().lookupLabel(label);
	}*/
}

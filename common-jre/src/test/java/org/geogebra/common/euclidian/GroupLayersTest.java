package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.groups.Group;
import org.junit.Before;
import org.junit.Test;

public class GroupLayersTest {

	private LayerManager layerManager;
	private GeoElement[] geos;
	private Construction construction;

	@Before
	public void setup() {
		AwtFactoryCommon factoryCommon = new AwtFactoryCommon();
		AppCommon app = new AppCommon(new LocalizationCommon(2), factoryCommon);
		construction = app.getKernel().getConstruction();
		layerManager = new LayerManager();
		withGeos(5);
	}

	@Test
	public void testMoveToFront() {
		withDefaultGroup();
		layerManager.moveToFront(selection(1));
		assertOrdering(0, 2, 3, 1, 4);
	}

	private void withDefaultGroup() {
		withGroup(1, 2, 3);
	}

	private void withGeos(final int count) {
		geos = new GeoElement[count];
		for (int i = 0; i < count; i++) {
			geos[i] = LayerManagerTest.createDummyGeo(construction, i);
			layerManager.addGeo(geos[i]);
		}
	}

	private List<GeoElement> selection(int i) {
		return Collections.singletonList(geos[i]);
	}

	private void withGroup(int... indexes) {
		ArrayList<GeoElement> members = new ArrayList<>();
		for (int idx: indexes) {
			members.add(geos[idx]);
		}

		Group group = new Group(members);
		construction.addGroupToGroupList(group);

	}

	private void assertOrdering(int... newOrder) {
		LayerManagerTest.assertOrdering(geos, newOrder);
	}

	@Test
	public void testMoveFirstToFront() {
		withDefaultGroup();
		layerManager.moveToFront(selection(3));
		assertOrderingUnchanged();
	}

	private void assertOrderingUnchanged() {
		assertOrdering(0, 1, 2, 3, 4);
	}

	@Test
	public void testMoveToBack() {
		withDefaultGroup();
		layerManager.moveToBack(selection(3));
		assertOrdering(0, 3, 1, 2, 4);
	}

	@Test
	public void testMoveLastToBack() {
		withDefaultGroup();
		layerManager.moveToBack(selection(1));
		assertOrderingUnchanged();
	}

	@Test
	public void testMoveForward() {
		withDefaultGroup();
		layerManager.moveForward(selection(1));
		assertOrdering(0, 2, 1, 3, 4);
	}

	@Test
	public void testMoveForwardLastInGroup() {
		withDefaultGroup();
		layerManager.moveForward(selection(3));
		assertOrderingUnchanged();
	}

	@Test
	public void testMoveBackwardInGroup() {
		withDefaultGroup();
		layerManager.moveBackward(selection(3));
		assertOrdering(0, 1, 3, 2, 4);
	}

	@Test
	public void testMoveBackwardLastInGroup() {
		withDefaultGroup();
		layerManager.moveBackward(selection(1));
		assertOrderingUnchanged();
	}

	@Test
	public void testMoveToFrontAndBack() {
		withDefaultGroup();
		layerManager.moveToFront(selection(1));
		layerManager.moveToBack(selection(1));
		assertOrderingUnchanged();
	}

	@Test
	public void testMoveForwardAndBack() {
		withDefaultGroup();
		layerManager.moveForward(selection(1));
		layerManager.moveForward(selection(1));
		layerManager.moveBackward(selection(1));
		assertOrdering(0, 2, 1, 3, 4);
	}

	@Test
	public void testMoveBackwardAndReset() {
		withDefaultGroup();
		layerManager.moveBackward(selection(3));
		layerManager.moveBackward(selection(3));
		layerManager.moveForward(selection(3));
		layerManager.moveForward(selection(3));
		assertOrderingUnchanged();
	}

	@Test
	public void testMoveForwardThroughGroup() {
		withDefaultGroup();
		layerManager.moveForward(selection(0));
		assertOrdering(1, 2, 3, 0, 4);
	}

	@Test
	public void testMoveForwardThroughGroupAtTop() {
		withGroup(2, 3, 4);
		layerManager.moveForward(selection(1));
		assertOrdering(0, 2, 3, 4, 1);
	}

	@Test
	public void testMoveBackwardThroughGroup() {
		withDefaultGroup();
		layerManager.moveBackward(selection(4));
		assertOrdering(0, 4, 1, 2, 3);
	}

	@Test
	public void testMoveBackwardThroughGroupAtBeginning() {
		withGroup(0, 1, 2);
		layerManager.moveBackward(selection(3));
		assertOrdering(3, 0, 1, 2, 4);
	}
}

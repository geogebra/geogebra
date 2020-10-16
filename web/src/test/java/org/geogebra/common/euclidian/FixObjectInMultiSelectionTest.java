package org.geogebra.common.euclidian;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.full.gui.ContextMenuMock;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class FixObjectInMultiSelectionTest {

	private ArrayList<GeoElement> geos;
	private Construction construction;
	private Group group;
	private AppW app;
	private ContextMenuMock contextMenu;
	private SelectionManager selectionManager;

	@Before
	public void setup() {
		app = AppMocker.mockNotes(getClass());
		construction = app.getKernel().getConstruction();
		contextMenu = new ContextMenuMock(app);
		selectionManager = app.getSelectionManager();
		withFixableGeos(5);
	}

	private void withFixableGeos(final int count) {
		geos = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			geos.add(createDummyGeo(construction, i));
		}
	}

	private static GeoElement createDummyGeo(Construction construction, int number) {
		GeoElement geo = new GeoPolygon(construction);
		geo.setLabel(number + "");
		return geo;
	}

	@Test
	public void selectionShouldHaveFixObject() {
		withFixableGeos(4);
		selectionManager.addSelectedGeos(geos, false);
		assertThat(contextMenu.getEntriesFor(geos), hasItem("FixObject"));
	}

	@Test
	public void fixObjectShouldBeCheckedIfAllIsFixed() {
		withFixableGeos(4);
		fixAllGeos();
		selectionManager.addSelectedGeos(geos, false);
		assertTrue(contextMenu.isMenuChecked(geos, "FixObject"));
	}

	private void fixAllGeos() {
		for (GeoElement geo : geos) {
			geo.setFixed(true);
		}
	}

	@Test
	public void fixObjectShouldBeUncheckedIfNotAllIsFixed() {
		withFixableGeos(4);
		fixAllGeos();
		geos.get(2).setFixed(false);
		selectionManager.addSelectedGeos(geos, false);
		assertFalse(contextMenu.isMenuChecked(geos, "FixObject"));
	}

	@Test
	public void selectionShouldNotHaveFixObjectIfAnyNonFixable() {
		withFixableGeos(4);
		withNonFixableGeo();
		selectionManager.addSelectedGeos(geos, false);
		assertThat(contextMenu.getEntriesFor(geos), not(hasItem("FixObject")));
	}

	private void withNonFixableGeo() {
		geos.add(new GeoPolygon(construction) {
			@Override
			public boolean isFixable() {
				return false;
			}
		});
	}

	@Test
	public void contextMenuShouldHaveFixObjectForGroups() {
		withGrouped();
		group = new Group(geos);
		construction.addGroupToGroupList(group);
		group.setFixed(true);
		assertThat(contextMenu.getEntriesFor(geos), hasItem("FixObject"));
	}

	private void withGrouped() {
		group = new Group(geos);
		construction.addGroupToGroupList(group);
	}
}

package org.geogebra.common.main.undo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.UpdateActionStore;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Test;

public class UndoManagerTest extends BaseEuclidianControllerTest {

	@Test
	public void undoDeletionGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().setUndoActive(true);
		GeoPoint pt = add("A=(1,1)");
		getApp().storeUndoInfo();
		getApp().getSelectionManager().addSelectedGeo(pt);
		AppState initial = getCheckpoint();
		getApp().deleteSelectedObjects(false);
		assertThat(getCheckpoint(), not(initial));
		assertThat(getConstruction().isEmpty(), is(true));
		getKernel().undo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	@Test
	public void undoDeletionNotes() {
		getApp().setConfig(new AppConfigNotes());
		getApp().setUndoActive(true);
		GeoPoint pt = add("A=(1,1)");
		getApp().getSelectionManager().addSelectedGeo(pt);
		AppState initial = getCheckpoint();
		getApp().deleteSelectedObjects(false);
		assertThat(getCheckpoint(), is(initial));
		assertThat(getConstruction().isEmpty(), is(true));
		getKernel().undo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	@Test
	public void testRemoveActions() {
		getApp().setUndoActive(true);
		GeoElement pt = add("A=(1,1)");
		getUndoManager().storeAddGeo(pt);
		GeoElement pt2 = add("B=(1,2)");
		getUndoManager().storeAddGeo(pt2);
		pt2.remove();
		getUndoManager().removeActionsWithLabel("B");
		assertThat(getUndoManager().undoPossible(), is(true));
		getKernel().undo();
		assertThat(getUndoManager().undoPossible(), is(false));
		assertThat(getUndoManager().redoPossible(), is(true));
		assertThat(lookup("A"), nullValue());
		getKernel().redo();
		assertThat(lookup("A"), hasValue("(1, 1)"));
	}

	@Test
	public void testUndoChangesPolygon() {
		getApp().setUndoActive(true);
		add("A = (1, -1)");
		add("B = (3, -3)");
		add("C = (3, -1)");
		add("t1 = Polygon(A, B, C)");

		dragStart(125, 75);
		dragEnd(225, 75);

		assertThat(lookup("A"), hasValue("(3, -1)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(1, -1)"));
		assertThat(lookup("t1"), hasValue("2"));
	}

	@Test
	public void testUndoChangesMultiplePolygons() {
		getApp().setUndoActive(true);
		add("A = (1, -1)");
		add("B = (3, -3)");
		add("C = (3, -1)");
		GeoElement poly1 = add("t1 = Polygon(A, B, C)");

		add("D = (5, -1)");
		add("E = (5, -3)");
		add("F = (7, -1)");
		GeoElement poly2 = add("t2 = Polygon(D, E, F)");

		getApp().getSelectionManager().setSelectedGeos(List.of(poly1, poly2));
		dragStart(125, 75);
		dragEnd(225, 125);

		assertThat(lookup("A"), hasValue("(3, -2)"));
		assertThat(lookup("D"), hasValue("(7, -2)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(1, -1)"));
		assertThat(lookup("D"), hasValue("(5, -1)"));
		assertThat(lookup("t1"), hasValue("2"));
	}

	@Test
	public void testUndoChangesLine() {
		getApp().setUndoActive(true);
		add("A = (2, -2)");
		add("B = (3, -3)");
		add("f = Line(A, B)");

		dragStart(50, 50);
		dragEnd(150, 100);

		assertThat(lookup("A"), hasValue("(4, -3)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(2, -2)"));
		assertThat(lookup("f"), hasValue("x + y = 0"));
	}

	@Test
	public void testUndoChangesSegment() {
		getApp().setUndoActive(true);
		add("A = (2, -2)");
		add("B = (5, -6)");
		add("f = Segment(A, B)");

		dragStart(150, 166);
		dragEnd(250, 216);

		assertThat(lookup("A"), hasValue("(4, -3)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(2, -2)"));
		assertThat(lookup("f"), hasValue("5"));
	}

	@Test
	public void testUndoChangesSegmentWithOneUnlabeledPoint() {
		getApp().setUndoActive(true);
		add("A = (1, -2)");
		add("f = Segment(A, (3, -4))");

		dragStart(150, 200);
		dragEnd(250, 300);

		assertThat(lookup("A"), hasValue("(3, -4)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(1, -2)"));
		assertThat(lookup("f"), hasValue("2.83"));
	}

	@Test
	public void testUndoChangesPoint() {
		getApp().setUndoActive(true);
		add("A = (3, -2)");
		dragStart(150, 100);
		dragEnd(200, 150);
		assertThat(lookup("A"), hasValue("(4, -3)"));
		getUndoManager().undo();
		assertThat(lookup("A"), hasValue("(3, -2)"));
	}

	@Test
	public void undoDraggingPointOnPath() {
		activateUndo();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		GeoPoint pt = add("A=Point(xAxis)");
		final GeoPoint dependent = add("B=A+(0,1)");
		getApp().getSelectionManager().addSelectedGeo(pt);
		actionStore.storeSelection();
		pt.setCoords(3, 0, 1);
		pt.update();
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(pt, hasValue("(0, 0)"));
		assertThat(dependent, hasValue("(0, 1)"));
		getUndoManager().redo();
		assertThat(pt, hasValue("(3, 0)"));
		assertThat(dependent, hasValue("(3, 1)"));
		assertThat(String.join(",", getApp().getGgbApi().getAllObjectNames()), equalTo("A,B"));
	}

	@Test
	public void undoDraggingPointInRegion() {
		activateUndo();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		add("poly=Polygon((0,0),(5,0),4)");
		GeoPoint pt = add("PointIn(poly)");
		getApp().getSelectionManager().addSelectedGeo(pt);
		actionStore.storeSelection();
		pt.setCoords(3, 0, 1);
		pt.update();
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(pt, hasValue("(0, 0)"));
		getUndoManager().redo();
		assertThat(pt, hasValue("(3, 0)"));
	}

	private UndoManager getUndoManager() {
		return getConstruction().getUndoManager();
	}

	private AppState getCheckpoint() {
		return getConstruction().getUndoManager().getCheckpoint(null).getAppState();
	}
}

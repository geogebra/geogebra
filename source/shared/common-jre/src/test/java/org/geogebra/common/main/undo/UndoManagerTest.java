package org.geogebra.common.main.undo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Objects;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MoveMode;
import org.geogebra.common.euclidian.UpdateActionStore;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.jre.headless.EuclidianController3DNoGui;
import org.geogebra.common.jre.headless.EuclidianView3DNoGui;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.test.annotation.Issue;
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
		actionStore.storeSelection(MoveMode.POINT);
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
	@Issue("APPS-6589")
	public void undoDraggingPointOnPath3D() {
		activateUndo();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		GeoPoint3D pt = add("A=Point(zAxis)");
		final GeoPoint3D dependent = add("B=A+(0,1)");
		getApp().getSelectionManager().addSelectedGeo(pt);
		actionStore.storeSelection(MoveMode.POINT);
		pt.setCoords(0, 0, 3, 1);
		pt.update();
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(pt, hasValue("(0, 0, 0)"));
		assertThat(dependent, hasValue("(0, 1, 0)"));
		getUndoManager().redo();
		assertThat(pt, hasValue("(0, 0, 3)"));
		assertThat(dependent, hasValue("(0, 1, 3)"));
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
		actionStore.storeSelection(MoveMode.POINT);
		pt.setCoords(3, 0, 1);
		pt.update();
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(pt, hasValue("(0, 0)"));
		getUndoManager().redo();
		assertThat(pt, hasValue("(3, 0)"));
	}

	@Test
	@Issue("APPS-5774")
	public void undoDraggingSliderValue() {
		activateUndo();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		GeoNumeric slider = add("a=Slider(1,5,1)");
		getApp().getSelectionManager().addSelectedGeo(slider);
		actionStore.storeSelection(MoveMode.NUMERIC);
		slider.setValue(3);
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(slider, hasValue("1"));
		getUndoManager().redo();
		assertThat(slider, hasValue("3"));
	}

	@Test
	public void undoDraggingSliderPosition() {
		activateUndo();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		GeoNumeric slider = add("a=Slider(1,5,1)");
		getApp().getSelectionManager().addSelectedGeo(slider);
		slider.setSliderLocation(50, 50, true);
		actionStore.storeSelection(MoveMode.SLIDER);
		slider.setSliderLocation(100, 100, true);
		actionStore.storeUndo();
		getUndoManager().undo();
		GeoPointND startPoint = Objects.requireNonNull(slider.getStartPoint());
		assertEquals(50, startPoint.getInhomX(), .1);
		getUndoManager().redo();
		assertEquals(100, startPoint.getInhomX(), .1);
	}

	@Test
	@Issue("APPS-6416")
	public void undoDraggingNet() {
		undoDraggingNet("0");
	}

	@Test
	@Issue("APPS-6589")
	public void undoDraggingNetNamed() {
		add("slider=Slider(0,1,.1)");
		undoDraggingNet("slider");
	}

	private void undoDraggingNet(String paramDefinition) {
		activateUndo();
		add("c=Cube((0,0,0),(0,0,1))");
		add("Net(c," + paramDefinition + ")");
		GeoSegment3D edgeJT = (GeoSegment3D) lookup("edgeJT");
		GeoNumeric param = edgeJT.getChangeableParent3D().getNumber();
		EuclidianView3D view3D = get3Dview();
		UpdateActionStore actionStore = new UpdateActionStore(getApp().getSelectionManager(),
				getUndoManager());
		getApp().getSelectionManager().addSelectedGeo(edgeJT);
		actionStore.storeSelection(MoveMode.DEPENDENT);
		assertThat(edgeJT.getStartPointAsGeoElement(), hasValue("(1, 1, 0)"));
		assertThat(param, hasValue("0"));
		Coords startPoint = new Coords(1, 1, 0);
		edgeJT.getChangeableParent3D().record(view3D, startPoint);
		edgeJT.getChangeableParent3D().move(new Coords(1, 0, 0),
				null, view3D.getViewDirection(),
				null, null,  view3D);
		assertThat(edgeJT.getStartPointAsGeoElement(), hasValue("(2, 0, 0)"));
		assertThat(param, hasValue("1"));
		assertEquals(20, getConstruction().getGeoSetLabelOrder(GeoClass.POINT3D).size());
		actionStore.storeUndo();
		getUndoManager().undo();
		assertThat(edgeJT.getStartPointAsGeoElement(), hasValue("(1, 1, 0)"));
		assertThat(param, hasValue("0"));
		// check that no new points were created
		assertEquals(20, getConstruction().getGeoSetLabelOrder(GeoClass.POINT3D).size());
		getUndoManager().redo();
		assertThat(edgeJT.getStartPointAsGeoElement(), hasValue("(2, 0, 0)"));
		assertThat(param, hasValue("1"));
		assertEquals(20, getConstruction().getGeoSetLabelOrder(GeoClass.POINT3D).size());
	}

	@Test
	@Issue("APPS-7170")
	public void undoUpdateStroke() {
		activateUndo();
		GeoLocusStroke stroke = add("PenStroke(0,0,1,1)");
		AlgoElement parentAlgo = stroke.getParentAlgorithm();
		String oldXML = Objects.requireNonNull(parentAlgo).getXML();
		stroke.appendPointArray(List.of(new MyPoint(2, 1)));
		stroke.update();
		getApp().getUndoManager().buildAction(ActionType.UPDATE, parentAlgo.getXML())
				.withUndo(ActionType.UPDATE, oldXML).withLabels("stroke1").storeAndNotifyUnsaved();
		getUndoManager().undo();
		assertThat(stroke, hasValue("PenStroke[0.0000E0,0.0000E0,1.0000E0,1.0000E0,NaN,NaN]"));
		getUndoManager().redo();
		assertThat(stroke, hasValue("PenStroke[0.0000E0,0.0000E0,1.0000E0,1.0000E0,NaN,NaN,"
				+ "2.0000E0,1.0000E0,2.0000E0,1.0000E0,NaN,NaN]"));
		assertThat(String.join(",", getApp().getGgbApi().getAllObjectNames()), equalTo("stroke1"));
	}

	@Test
	@Issue("APPS-6654")
	public void zoomAfterUndo() {
		getApp().setXML(GeoGebraPreferencesXML.getXML(getApp()), false);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setKeepCenter(true);
		activateUndo();
		view.setCoordSystem(0, 0, 50, 50);
		getUndoManager().storeUndoInfo();
		view.setCoordSystem(100, 200, 50, 50);
		getUndoManager().storeUndoInfo();
		getUndoManager().undo();
		assertEquals(0, view.getXZero(), 0);
		getUndoManager().redo();
		assertEquals(100, view.getXZero(), 0);
	}

	private EuclidianView3D get3Dview() {
		return new EuclidianView3DNoGui(
				new EuclidianController3DNoGui(getApp(), getKernel()),
				this.getSettings().getEuclidian(3));
	}

	@Test
	public void shouldSetUnsaved() {
		getKernel().setUndoActive(true);
		add("x");
		getApp().storeUndoInfo();
		assertFalse(getApp().isSaved());
	}

	private UndoManager getUndoManager() {
		return getConstruction().getUndoManager();
	}

	private AppState getCheckpoint() {
		return getConstruction().getUndoManager().getCheckpoint(null).getAppState();
	}
}

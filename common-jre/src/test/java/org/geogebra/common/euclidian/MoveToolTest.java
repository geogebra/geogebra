package org.geogebra.common.euclidian;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.MockCASGiac;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenPositionModel;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.EventAcumulator;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;
import org.mockito.Mockito;

public class MoveToolTest extends BaseEuclidianControllerTest {

	@Test
	public void moveWithMouseShouldChangeSegment1() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "f = 1.41421");
	}

	@Test
	public void moveWithArrowKeyShouldChangeSegment1() {
		add("A = (0,0)");
		GeoElement segment = add("f = Segment(A, (1,-1))");
		moveObjectWithArrowKey(segment, 1, -2);
		checkContent("A = (1, -2)", "f = 1.41421");
	}

	@Test
	public void moveWithMouseShouldChangeVector1() {
		add("v = Vector((1,-1))");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("v = (2, -3)");
	}

	@Test
	public void moveWithArrowKeyShouldChangeVector1() {
		GeoElement geo = add("v = Vector((1,-1))");
		moveObjectWithArrowKey(geo, 1, -2);
		checkContent("v = (2, -3)");
	}

	@Test
	public void moveWithMouseShouldChangeVector2() {
		add("list = {Vector((1,-1))}");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("list = {(2, -3)}");
	}

	@Test
	public void moveWithArrowKeyShouldChangeVector2() {
		GeoElement list = add("list = {Vector((1,-1))}");
		moveObjectWithArrowKey(list, 1, -2);
		checkContent("list = {(2, -3)}");
	}

	@Test
	public void casListShouldNotBeMoveable() {
		MockCASGiac mockGiac = setupGiac();
		mockGiac.memorize("{(1, -1), (1, 1)}");
		GeoCasCell f = new GeoCasCell(getConstruction());
		getConstruction().addToConstructionList(f, false);
		f.setInput("l5:=Intersect(x^2+y^2=2,(x-2)^2+y^2=2)");
		f.computeOutput();
		GeoList list = (GeoList) f.getTwinGeo();
		list.setLabel("l5");
		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
		moveObjectWithArrowKey(list, 1, -2);
		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
	}

	@Test
	public void casFreeListShouldNotBeMoveable() {
		MockCASGiac mockGiac = setupGiac();
		mockGiac.memorize("{(1, -1), (1, 1)}");
		GeoCasCell f = new GeoCasCell(getConstruction());
		getConstruction().addToConstructionList(f, false);
		f.setInput("l5:={(1, -1), (1, 1)}");
		f.computeOutput();
		GeoList list = (GeoList) f.getTwinGeo();
		list.setLabel("l5");
		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
		moveObjectWithArrowKey(list, 1, -2);
		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
		dragStart(50, 50);
		assertThat(list.isSelected(), equalTo(true));
		dragEnd(100, 50);
		assertThat(list, hasValue("{(1, -1), (1, 1)}"));
	}

	@Test
	public void freeListShouldBeDraggable() {
		GeoList list  = add("{(1, -1), (1, 1)}");
		list.setEuclidianVisible(true);
		list.updateRepaint();
		EventAcumulator acumulator = new EventAcumulator();
		getApp().getEventDispatcher().addEventListener(acumulator);
		dragStart(50, 50);
		dragEnd(100, 50);
		assertThat(list, hasValue("{(2, -1), (2, 1)}"));
		assertTrue("List should have been updated", acumulator.getEvents().contains("UPDATE l1"));
	}

	private MockCASGiac setupGiac() {
		MockCASGiac mockGiac = new MockCASGiac((CASparser) getKernel()
				.getGeoGebraCAS().getCASparser());
		getApp().setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return mockGiac;
			}
		});
		return mockGiac;
	}

	@Test
	public void moveWithMouseShouldChangePolygon1() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		add("SetVisibleInView(B,1,false)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "q = 1", "f = 1", "g = 1", "B = (2, -3)",
				"C = (2, -2)", "h = 1", "i = 1");
	}

	@Test
	public void moveWithArrowKeyShouldChangePolygon1() {
		add("A = (0,0)");
		GeoElement geo = add("q = Polygon(A, (0,-1), 4)");
		moveObjectWithArrowKey(geo, 1, -2);
		checkContent("A = (1, -2)", "q = 1", "f = 1", "g = 1", "B = (2, -3)",
				"C = (2, -2)", "h = 1", "i = 1");
	}

	@Test
	public void moveWithMouseShouldChangePolygon2() {
		GeoElement A = add("A = (0,0)");
		GeoElement q = add("q = Polygon((x(A), y(A)), (2, 0), (2, -2), (0, -2))");
		dragStart(50, 50);
		dragEnd(100, 100);
		assertThat(A, hasValue("(0, 0)"));
		assertThat(q, hasValue("6"));
	}

	@Test
	public void moveWithArrowKeyShouldChangePolygon2() {
		GeoElement A = add("A = (0,0)");
		GeoElement q = add("q = Polygon((x(A), y(A)), (2, 0), (2, -2), (0, -2))");
		moveObjectWithArrowKey(q, 1, -1);
		assertThat(A, hasValue("(0, 0)"));
		assertThat(q, hasValue("6"));
	}

	@Test
	public void moveWithMouseShouldChangePolygon3() {
		GeoElement A = add("A = (0,0)");
		GeoElement q = add("q = Polygon(A, A + (2, 0), A + (2, -2), A + (0, -2))");
		dragStart(50, 50);
		dragEnd(100, 100);
		assertThat(A, hasValue("(1, -1)"));
		assertThat(q, hasValue("4"));
	}

	@Test
	public void moveWithArrowKeyShouldChangePolygon3() {
		GeoElement A = add("A = (0,0)");
		GeoElement q = add("q = Polygon(A, A + (2, 0), A + (2, -2), A + (0, -2))");
		moveObjectWithArrowKey(q, 1, -1);
		assertThat(A, hasValue("(1, -1)"));
		assertThat(q, hasValue("4"));
	}

	@Test
	public void moveWithMouseShouldNotChangeFixedSegment() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		add("SetFixed(f,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "f = 1.41421");
	}

	@Test
	public void moveWithArrowKeyShouldNotChangeFixedSegment() {
		add("A = (0,0)");
		GeoElement geo = add("f = Segment(A, (1,-1))");
		add("SetFixed(f,true)");
		moveObjectWithArrowKey(geo, 1, -2);
		checkContent("A = (0, 0)", "f = 1.41421");
	}

	@Test
	public void moveWithMouseShouldNotChangeFixedPolygon() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		add("SetFixed(q,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "q = 1", "f = 1", "g = 1", "B = (1, -1)",
				"C = (1, 0)", "h = 1", "i = 1");
	}

	@Test
	public void moveWithArrowKeyShouldNotChangeFixedPolygon() {
		add("A = (0,0)");
		GeoElement geo = add("q = Polygon(A, (0,-1), 4)");
		add("SetFixed(q,true)");
		moveObjectWithArrowKey(geo, 1, -2);
		checkContent("A = (0, 0)", "q = 1", "f = 1", "g = 1", "B = (1, -1)",
				"C = (1, 0)", "h = 1", "i = 1");
	}

	@Test
	public void moveWithMouseShouldNotChangeValueOfInfiniteCircle() {
		add("A=(1,-1)");
		add("B=(2,-2)");
		add("C=(3,-3)");
		GeoElement circle = add("Circle(A,B,C)");
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
		dragStart(0, 0);
		dragEnd(50, 50);
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
	}

	@Test
	public void moveWithArrowKeyShouldNotChangeValueOfInfiniteCircle() {
		add("A=(1,-1)");
		add("B=(2,-2)");
		add("C=(3,-3)");
		GeoElement circle = add("Circle(A,B,C)");
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
		moveObjectWithArrowKey(circle, 1, -1);
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
	}

	@Test
	public void moveWithMouseShouldChangeCircle1() {
		add("A=(1, -1)");
		GeoElement circle = add("Circle(A, 2)");
		dragStart(50, 150);
		dragEnd(100, 200);
		assertThat(circle, hasValue("(x - 2)² + (y + 2)² = 4"));
	}

	@Test
	public void moveWithMouseShouldChangeCircle2() {
		add("c = Circle((1, -1), 2)");
		dragStart(50, 150);
		dragEnd(100, 200);
		checkContent("c: (x - 2)² + (y + 2)² = 4");
	}

	@Test
	public void moveWithArrowKeyShouldChangeCircle1() {
		add("A=(1, -1)");
		GeoElement circle = add("Circle(A, 2)");
		moveObjectWithArrowKey(circle, 1, -1);
		assertThat(circle, hasValue("(x - 2)² + (y + 2)² = 4"));
	}

	@Test
	public void moveWithArrowKeyShouldChangeCircle2() {
		GeoElement circle = add("c = Circle((1, -1), 2)");
		moveObjectWithArrowKey(circle, 1, -1);
		checkContent("c: (x - 2)² + (y + 2)² = 4");
	}

	@Test
	public void moveWithMouseShouldChangeEllipse() {
		add("e = Ellipse((1, 1), (2, 2), (3, 3))");
		checkContent("e: 17x² - 2x y + 17y² - 48x - 48y = 0");
		dragStart(0, 0);
		dragEnd(50, 50);
		checkContent("e: 17x² - 2x y + 17y² - 84x - 12y = -36");
	}

	@Test
	public void moveWithArrowKeyShouldChangeEllipse() {
		GeoElement ellipse = add("e = Ellipse((1, 1), (2, 2), (3, 3))");
		checkContent("e: 17x² - 2x y + 17y² - 48x - 48y = 0");
		moveObjectWithArrowKey(ellipse, 1, -1);
		checkContent("e: 17x² - 2x y + 17y² - 84x - 12y = -36");
	}

	@Test
	public void moveWithMouseShouldNotChangeEllipse() {
		add("e = Ellipse((2, 2), (1, 0.6), 2)");
		checkContent("e: 60x² - 11.2x y + 56.16y² - 165.44x - 129.216y = 0.5696");
		dragStart(0, 0);
		dragEnd(50, 50);
		checkContent("e: 60x² - 11.2x y + 56.16y² - 165.44x - 129.216y = 0.5696");
	}

	@Test
	public void moveWithArrowKeyShouldNotChangeEllipse() {
		GeoElement ellipse = add("e = Ellipse((2, 2), (1, 0.6), 2)");
		checkContent("e: 60x² - 11.2x y + 56.16y² - 165.44x - 129.216y = 0.5696");
		moveObjectWithArrowKey(ellipse, 1, -1);
		checkContent("e: 60x² - 11.2x y + 56.16y² - 165.44x - 129.216y = 0.5696");
	}

	@Test
	public void moveWithMouseShouldChangeRay() {
		add("r = Ray((0, 0), (1, -1))");
		dragStart(0, 0);
		dragEnd(100, 50);
		checkContent("r: x + y = 1");
	}

	@Test
	public void moveWithArrowKeyShouldChangeRay() {
		GeoElement ray = add("r = Ray((0, 0), (1, -1))");
		moveObjectWithArrowKey(ray, 2, -1);
		checkContent("r: x + y = 1");
	}

	@Test
	public void moveWithMouseShouldChangeDependentPoint() {
		add("a = 1");
		add("b = -1");
		add("A = (a, b)");
		dragStart(50, 50);
		dragEnd(100, 100);
		checkContent("A = (2, -2)");
	}

	@Test
	public void moveWithArrowKeyShouldChangeDependentPoint() {
		add("a = 1");
		add("b = -1");
		GeoElement point = add("A = (a, b)");
		moveObjectWithArrowKey(point, 1, -1);
		checkContent("A = (2, -2)");
	}

	@Test
	public void moveWithArrowKeyShouldChange3DPolygon() {
		GeoElement pointA = add("A = (1, -1, 0)");
		add("B = (2, -1, 0)");
		add("C = (2, -2, 0)");
		add("D = (1, -2, 0)");
		GeoElement poly = add("Polygon(A, B, C, D)");
		moveObjectWithArrowKey(poly, 1, -1);
		assertThat(pointA, hasValue("(2, -2, 0)"));
	}

	@Test
	public void moveWithMouseShouldChangeOutputOfTranslate1() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement point = add("Translate(A, v)");
		dragStart(50, 50);
		dragEnd(100, 100);
		assertThat(point, hasValue("(2, -2)"));
	}

	@Test
	public void moveWithArrowKeyShouldChangeOutputOfTranslate1() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement point = add("Translate(A, v)");
		moveObjectWithArrowKey(point, 1, -1);
		assertThat(point, hasValue("(2, -2)"));
	}

	@Test
	public void moveWithMouseShouldChangeOutputOfTranslate2() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement list = add("{Translate(A, v)}");
		dragStart(50, 50);
		dragEnd(100, 100);
		assertThat(list, hasValue("{(2, -2)}"));
	}

	@Test
	public void moveWithArrowKeyShouldChangeOutputOfTranslate2() {
		add("A = (2, 2)");
		add("v = Vector((-1, -3))");
		GeoElement list = add("{Translate(A, v)}");
		moveObjectWithArrowKey(list, 1, -1);
		assertThat(list, hasValue("{(2, -2)}"));
	}

	@Test
	public void moveWithArrowsShouldNotChangeAnchoredText() {
		GeoText text = add("t=Text(\"T\",(1, 2))");
		moveObjectWithArrowKey(text, 1, -1);
		assertThat(text.getStartPoint(), hasValue("(1, 2)"));
	}

	@Test
	public void moveWithArrowsShouldChangeFreeText() {
		GeoText text = add("t=Text(\"T\")");
		add("SetCoords(t,3,5)");
		assertThat(text.getStartPoint(), hasValue("(3, 5)"));
		moveObjectWithArrowKey(text, 1, -1);
		assertThat(text.getStartPoint(), hasValue("(4, 4)"));
	}

	@Test
	public void moveWithArrowKeyShouldNotChangeTextWithDependentAbsolutePosition() {
		add("posX = 100");
		add("posY = 100");
		GeoText text = (GeoText) add("Text(\"Try me\")");
		text.setAbsoluteScreenLocActive(true);

		AbsoluteScreenPositionModel modelForX = new AbsoluteScreenPositionModel.ForX(getApp());
		modelForX.setGeos(new GeoElement[]{text});
		modelForX.applyChanges("posX");

		AbsoluteScreenPositionModel modelForY = new AbsoluteScreenPositionModel.ForY(getApp());
		modelForY.setGeos(new GeoElement[]{text});
		modelForY.applyChanges("posY");

		moveObjectWithArrowKey(text, 1, -1);
		assertEquals(100, text.getAbsoluteScreenLocX());
		assertEquals(100, text.getAbsoluteScreenLocY());
	}

	@Test
	public void selectionReadByScreenReaderOnce() {
		ScreenReaderAdapter screenReader = Mockito.spy(ScreenReaderAdapter.class);
		((EuclidianViewNoGui) getApp().getActiveEuclidianView()).setScreenReader(screenReader);
		add("A = (1, -1)");
		dragStart(50, 50);
		dragEnd(50, 50);
		verify(screenReader).readText(anyString());
	}

	@Test
	public void moveChangeableCoords() {
		add("a=1");
		add("b=-1");
		add("A=(a,b)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContentWithVisibility(false, "a = 2", "b = -3");
		checkContent("A = (2, -3)");
	}

	@Test
	public void drag3dPointWithDependencies() {
		add("A=(1,-1,0)");
		add("B=2A");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (2, -3, 0)", "B = (4, -6, 0)");
	}

	@Test
	public void moveButton() {
		GeoElement furniture = add("furniture=Button()");
		assertFurnitureDragBehavior(furniture);
	}

	@Test
	public void moveInputBox() {
		GeoElement furniture = add("furniture=InputBox()");
		assertFurnitureDragBehavior(furniture);
	}

	@Test
	public void moveCheckBox() {
		GeoElement furniture = add("furniture=CheckBox()");
		assertTrue(furniture.isLockedPosition());
		assertCannotDrag(furniture);
		assertCanDrag(furniture, true);
		((GeoBoolean) furniture).setCheckboxFixed(false);
		assertCanDrag(furniture, false);
	}

	@Test
	public void moveImage() {
		GeoImage image = createImage();
		image.setLabel("img");
		((AbsoluteScreenLocateable) image).setAbsoluteScreenLocActive(true);
		add("SetFixed(img,true)");
		assertCannotDrag(image);
		add("SetFixed(img,false)");
		assertCanDrag(image, false);
	}

	@Test
	@Issue("APPS-5592")
	public void moveImageMovesChildren() {
		GeoImage image = createImage();
		image.setLabel("img");
		add("Reflect(img,xAxis)");
		((AbsoluteScreenLocateable) image).setAbsoluteScreenLocActive(true);
		DragResult dr = getDragResult(image, false);
		assertEquals("UPDATE img,UPDATE img',UPDATE_STYLE img", dr.events);
	}

	@Test
	public void moveDropdown() {
		GeoElement furniture = add("furniture={1,2,3}");
		assertArrayEquals(new String[]{"furniture"}, getApp().getGgbApi().getAllObjectNames());
		((GeoList) furniture).setDrawAsComboBox(true);
		furniture.setEuclidianVisible(true);
		furniture.setLabelVisible(true);
		furniture.updateRepaint();
		assertCanDrag(furniture, true); // right-click only; no left-dragging of dropdowns
	}

	@Test
	public void undoMoving() {
		getApp().setUndoActive(true);
		add("A = (1, -1)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (2, -3)");
		getApp().getKernel().undo();
		checkContent("A = (1, -1)");
	}

	@Test
	public void moveTranslateOutput() {
		Stream.of("(0,0)", "(1,0)", "(1,1)", "(0,1)").forEach(this::add);
		add("quad=Polygon(A,B,C,D)");
		add("trV=Translate(quad,(1,-2))");
		add("tr=Translate(quad,(1,-2))");
		GeoElement corner = add("Vertex(tr,1)");
		GeoElement cornerV = add("Vertex(trV,1)");
		// first drag poly translated by point
		assertThat(corner, hasValue("(1, -2)"));
		dragStart(75, 75);
		dragEnd(75, 125);
		assertThat(corner, hasValue("(1, -3)"));
		// now drag poly translated by vector
		assertThat(cornerV, hasValue("(1, -2)"));
		dragStart(75, 75);
		dragEnd(75, 125);
		assertThat(cornerV, hasValue("(1, -3)"));
	}

	@Test
	public void shouldNotMoveDependentTranslateOutput() {
		add("a=-2");
		Stream.of("(0,0)", "(1,0)", "(1,1)", "(0,1)").forEach(this::add);
		add("quad=Polygon(A,B,C,D)");
		add("tr=Translate(quad,Vector((1,a)))");
		GeoElement corner = add("Vertex(tr,1)");
		assertThat(corner, hasValue("(1, -2)"));
		dragStart(75, 75);
		dragEnd(75, 125);
		assertThat(corner, hasValue("(1, -2)"));
	}

	@Test
	public void movePointShouldSnapOnDrag() {
		GeoElement point = add("A = (0, 0)");
		snapToGrid();
		dragStart(0, 0);
		dragEnd(205, 0);
		assertThat(point, hasValue("(4, 0)"));
	}

	private void snapToGrid() {
		getApp().getActiveEuclidianView().setPointCapturing(
				EuclidianStyleConstants.POINT_CAPTURING_ON
		);
	}

	@Test
	public void moveSegmentShouldSnapOnDrag() {
		GeoSegment segment = (GeoSegment) add("Segment((0, 0), (1, 0))");
		snapToGrid();
		dragStart(25, 0);
		dragEnd(230, 0);
		assertThat(segment.startPoint, hasValue("(4, 0)"));
	}

	@Test
	public void moveSegmentShouldRunOnUpdateForEndPoints() {
		add("A = (0,0)");
		GeoElement segment = add("f = Segment(A, (1,-1))");
		moveObjectWithArrowKey(segment, 1, -2);
		checkContent("A = (1, -2)", "f = 1.41421");
	}

	private void assertFurnitureDragBehavior(GeoElement furniture) {
		add("SetFixed(furniture,true)");
		assertCannotDrag(furniture);
		assertCanDrag(furniture, true);
		add("SetFixed(furniture,false)");
		assertCanDrag(furniture, false);
	}

	private void assertCannotDrag(GeoElementND furniture) {
		assertEquals(new DragResult(0, 0, ""),
				getDragResult(furniture, false));
	}

	private void assertCanDrag(GeoElementND furniture, boolean right) {
		assertEquals(new DragResult(100, 50, "UPDATE_STYLE "
						+ furniture.getLabelSimple()), getDragResult(furniture, right));
	}

	private DragResult getDragResult(GeoElementND geo, boolean rightClick) {
		int offX = 10;
		int offY = geo.isGeoImage() ? -10 : 10;
		add("SetCoords(" + geo.getLabelSimple() + ", 100, 100)");
		dragStart(100 + offX, 100 + offY, rightClick);
		EventAcumulator listener = new EventAcumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		dragEnd(200 + offX, 150 + offY, rightClick);
		return new DragResult(((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX() - 100,
				((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocY() - 100,
				listener.getEvents().toArray(new String[0]));
	}

	/**
	 * Moves an object with arrow keys (Translation Vector (x, y, 0))
	 * @param geo GeoElement
	 * @param x x-Axis
	 * @param y y-Axis
	 */
	private void moveObjectWithArrowKey(GeoElement geo, int x, int y) {
		MoveGeos.moveObjects(Collections.singletonList(geo), new Coords(x, y, 0, 0),
				null, null, getApp().getActiveEuclidianView());
	}

	private static class DragResult {
		public final int x;
		public final int y;
		public final String events;

		private DragResult(int x, int y, String... events) {
			this.x = x;
			this.y = y;
			this.events = String.join(",", Arrays.stream(events)
					.filter(s -> s.startsWith("UPDATE")).collect(Collectors.toSet()));
		}

		public String toString() {
			return x + "," + y + ":" + events;
		}

		public boolean equals(Object other) {
			return other instanceof DragResult && toString().equals(other.toString());
		}
	}
}

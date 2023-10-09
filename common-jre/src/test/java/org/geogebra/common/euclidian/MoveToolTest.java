package org.geogebra.common.euclidian;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.EventAcumulator;
import org.junit.Test;
import org.mockito.Mockito;

public class MoveToolTest extends BaseControllerTest {

	@Test
	public void moveShouldChangeSegment() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "f = 1.41421");
	}

	@Test
	public void moveShouldChangeVector() {
		add("v = Vector((1,-1))");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("v = (2, -3)");
	}

	@Test
	public void moveShouldChangePolygon() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (1, -2)", "q = 1", "f = 1", "g = 1", "B = (2, -3)",
				"C = (2, -2)", "h = 1", "i = 1");
	}

	@Test
	public void moveShouldNotChangeFixedSegment() {
		add("A = (0,0)");
		add("f = Segment(A, (1,-1))");
		add("SetFixed(f,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "f = 1.41421");
	}

	@Test
	public void moveShouldNotChangeFixedPolygon() {
		add("A = (0,0)");
		add("q = Polygon(A, (0,-1), 4)");
		add("SetFixed(q,true)");
		dragStart(50, 50);
		dragEnd(100, 150);
		checkContent("A = (0, 0)", "q = 1", "f = 1", "g = 1", "B = (1, -1)",
				"C = (1, 0)", "h = 1", "i = 1");
	}

	@Test
	public void moveShouldNotChangeInfiniteCircle() {
		add("A=(1,-1)");
		add("B=(2,-2)");
		add("C=(3,-3)");
		GeoElement circle = add("Circle(A,B,C)");
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
		dragStart(100, 50);
		dragEnd(100, 150);
		assertThat(circle, hasValue("(-0.71x - 0.71y) (∞) = 0"));
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

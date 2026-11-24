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

package org.geogebra.common.euclidian.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.TestEvent;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;

public class BaseToolTest extends BaseEuclidianControllerTest {

	@BeforeEach
	void setUp() {
		setUpController();
	}

	/**
	 * Click at the given real-world coordinates in the active Euclidian view.
	 *
	 * @param xRW real-world x-coordinate
	 * @param yRW real-world y-coordinate
	 */
	protected void clickRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		int screenX = view.toScreenCoordX(xRW);
		int screenY = view.toScreenCoordY(yRW);
		assertTrue(screenX >= 0 && screenX <= view.getViewWidth(), screenX + " should be on screen");
		assertTrue(screenY >= 0 && screenY <= view.getViewHeight(), screenY + " should be on screen");
		click(screenX, screenY);
	}

	/**
	 * Move the mouse to the given real-world coordinates in the active Euclidian view.
	 *
	 * @param xRW real-world x-coordinate
	 * @param yRW real-world y-coordinate
	 */
	protected void moveMouseRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		ec.wrapMouseMoved(new TestEvent(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW)));
	}

	/**
	 * Drag between the given screen coordinates.
	 *
	 * @param startX screen x-coordinate where the drag starts
	 * @param startY screen y-coordinate where the drag starts
	 * @param endX screen x-coordinate where the drag ends
	 * @param endY screen y-coordinate where the drag ends
	 */
	protected void drag(int startX, int startY, int endX, int endY) {
		dragStart(startX, startY);
		dragEnd(endX, endY);
	}

	/**
	 * Drag between the given real-world coordinates in the active Euclidian view.
	 *
	 * @param startXRW real-world x-coordinate where the drag starts
	 * @param startYRW real-world y-coordinate where the drag starts
	 * @param endXRW real-world x-coordinate where the drag ends
	 * @param endYRW real-world y-coordinate where the drag ends
	 */
	protected void dragRW(double startXRW, double startYRW, double endXRW, double endYRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		drag(
				view.toScreenCoordX(startXRW),
				view.toScreenCoordY(startYRW),
				view.toScreenCoordX(endXRW),
				view.toScreenCoordY(endYRW));
	}

	/**
	 * Drag between the given real-world coordinates in the active Euclidian view,
	 * using  the right mouse button.
	 *
	 * @param startXRW real-world x-coordinate where the drag starts
	 * @param startYRW real-world y-coordinate where the drag starts
	 * @param endXRW real-world x-coordinate where the drag ends
	 * @param endYRW real-world y-coordinate where the drag ends
	 */
	protected void dragRWRight(double startXRW, double startYRW, double endXRW, double endYRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		dragStart(view.toScreenCoordX(startXRW), view.toScreenCoordY(startYRW), true);
		dragEnd(view.toScreenCoordX(endXRW), view.toScreenCoordY(endYRW), true);
	}

	/**
	 * Press the mouse at the given real-world coordinates without releasing it.
	 *
	 * @param xRW real-world x-coordinate
	 * @param yRW real-world y-coordinate
	 */
	protected void pressRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		dragStart(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
	}

	/**
	 * Release the mouse at the given real-world coordinates without sending a drag event.
	 *
	 * @param xRW real-world x-coordinate
	 * @param yRW real-world y-coordinate
	 */
	protected void releaseRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		pointerRelease(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
	}

	/**
	 * Initialize the headless dialog manager with the given inputs.
	 *
	 * @param inputs inputs returned by consecutive dialogs
	 */
	protected void prepareInput(String... inputs) {
		getApp().initDialogManager(false, inputs);
	}

	Set<String> getObjectNames() {
		return Set.copyOf(Arrays.asList(getApp().getGgbApi().getAllObjectNames()));
	}

	<T extends GeoElement> List<T> getNewObjects(Set<String> existingObjects, Class<T> type) {
		return Arrays.stream(getApp().getGgbApi().getAllObjectNames())
				.filter(label -> !existingObjects.contains(label))
				.map(this::lookup)
				.filter(type::isInstance)
				.map(type::cast)
				.toList();
	}

	<T extends GeoElement> T getOnlyNewObject(Set<String> existingObjects, Class<T> type) {
		List<T> newObjects = getNewObjects(existingObjects, type);
		assertEquals(1, newObjects.size());
		return newObjects.get(0);
	}

	Set<GeoElement> getConstructionSnapshot() {
		return new HashSet<>(getApp().getKernel().getConstruction().getGeoSetConstructionOrder());
	}

	List<GeoElement> getNewConstructionElements(
			Set<GeoElement> constructionSnapshot, Predicate<GeoElement> filter) {
		return getApp().getKernel().getConstruction().getGeoSetConstructionOrder().stream()
				.filter(geo -> !constructionSnapshot.contains(geo))
				.filter(filter)
				.toList();
	}

	List<String> definedValueStrings(Collection<? extends GeoElement> elements) {
		return elements.stream()
				.filter(GeoElement::isDefined)
				.map(element -> element.toValueString(StringTemplate.testTemplate))
				.toList();
	}

	/**
	 * Snapshot finite stroke samples and segment types, excluding serialization separators.
	 * @param stroke stroke to inspect
	 * @return geometry suitable for comparison across undo and XML round-trips
	 */
	protected @NonNull List<String> strokeGeometry(@NonNull GeoLocusStroke stroke) {
		return stroke.getPoints().stream()
				.filter(MyPoint::isDefined)
				.map(point -> point.x + ":" + point.y + ":" + point.getSegmentType())
				.toList();
	}

	void assertTextContainsValue(GeoText text, GeoElement value) {
		assertTrue(text.getTextString().contains(value.toValueString(StringTemplate.defaultTemplate)));
	}
	/**
	 * Draw samples with the last position delivered only by the release event.
	 * @param samples screen coordinates, in input order
	 */
	protected void drawSamples(int[] @NonNull [] samples) {
		dragStart(samples[0][0], samples[0][1]);
		for (int i = 1; i < samples.length - 1; i++) {
			drag(samples[i][0], samples[i][1]);
		}
		int[] last = samples[samples.length - 1];
		pointerRelease(last[0], last[1]);
	}

	/**
	 * Draw polygonal edges with input spaced approximately five screen pixels apart.
	 * @param vertices screen coordinates of successive edge endpoints
	 */
	protected void drawEdges(int[] @NonNull [] vertices) {
		dragStart(vertices[0][0], vertices[0][1]);
		for (int i = 1; i < vertices.length; i++) {
			int[] a = vertices[i - 1];
			int[] b = vertices[i];
			int steps = Math.max(1, (int) (Math.hypot(b[0] - a[0], b[1] - a[1]) / 5));
			for (int j = 1; j <= steps; j++) {
				drag(a[0] + (b[0] - a[0]) * j / steps, a[1] + (b[1] - a[1]) * j / steps);
			}
		}
		int[] last = vertices[vertices.length - 1];
		pointerRelease(last[0], last[1]);
	}

	/**
	 * Draw an ellipse in screen coordinates.
	 * @param cx center x-coordinate
	 * @param cy center y-coordinate
	 * @param rx horizontal radius
	 * @param ry vertical radius
	 * @param reverse whether to draw in the opposite direction
	 */
	protected void drawEllipse(int cx, int cy, int rx, int ry, boolean reverse) {
		dragStart(cx + rx, cy);
		for (int i = 1; i <= 80; i++) {
			double angle = (reverse ? -1 : 1) * 2 * Math.PI * i / 80;
			drag(
					cx + (int) Math.round(rx * Math.cos(angle)), cy + (int) Math.round(ry * Math.sin(angle)));
		}
		pointerRelease(cx + rx, cy);
	}
}

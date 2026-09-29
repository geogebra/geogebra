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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;

class BaseToolTest extends BaseEuclidianControllerTest {

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
		click(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
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
	 * Drag between the given real-world coordinates in the active Euclidian view.
	 *
	 * @param startXRW real-world x-coordinate where the drag starts
	 * @param startYRW real-world y-coordinate where the drag starts
	 * @param endXRW real-world x-coordinate where the drag ends
	 * @param endYRW real-world y-coordinate where the drag ends
	 */
	protected void dragRW(double startXRW, double startYRW, double endXRW, double endYRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		dragStart(view.toScreenCoordX(startXRW), view.toScreenCoordY(startYRW));
		dragEnd(view.toScreenCoordX(endXRW), view.toScreenCoordY(endYRW));
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

	void assertTextContainsValue(GeoText text, GeoElement value) {
		assertTrue(text.getTextString().contains(value.toValueString(StringTemplate.defaultTemplate)));
	}
}

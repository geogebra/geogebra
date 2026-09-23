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

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianView;
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
}

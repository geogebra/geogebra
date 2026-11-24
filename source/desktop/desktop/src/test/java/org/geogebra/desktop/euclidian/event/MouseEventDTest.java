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

package org.geogebra.desktop.euclidian.event;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class MouseEventDTest {

	private final JPanel source = new JPanel();

	@Test
	@Issue("APPS-8112")
	void reusedEventGetsNewTime() {
		AbstractEvent press = MouseEventD.wrapEvent(mouseEvent(MouseEvent.MOUSE_PRESSED));
		long pressTime = press.getTime();
		press.release();
		waitForClockToAdvance(pressTime);

		AbstractEvent drag = MouseEventD.wrapEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED));

		assertSame(press, drag);
		assertTrue(drag.getTime() > pressTime);
	}

	private MouseEvent mouseEvent(int id) {
		return new MouseEvent(source, id, System.currentTimeMillis(), 0, 10, 10, 1, false);
	}

	private static void waitForClockToAdvance(long time) {
		while (System.currentTimeMillis() <= time) {
			Thread.onSpinWait();
		}
	}
}

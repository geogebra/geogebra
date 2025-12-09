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

package org.geogebra.web.html5.euclidian.profiler.drawer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.After;
import org.junit.Test;

public class DrawingRecorderTest {

	private DrawingRecorder drawingRecorder = new DrawingRecorder();

	@After
	public void tearDown() {
		drawingRecorder.reset();
	}

	@Test
	public void testToString() {
		assertThat(
				drawingRecorder.toString(),
				equalTo("{\"coords\":[]}"));
	}

	@Test
	public void reset() {
		drawingRecorder.recordCoordinate(0, 1, 2);
		drawingRecorder.recordTouchEnd();
		drawingRecorder.reset();
		assertThat(
				drawingRecorder.toString(),
				equalTo("{\"coords\":[]}"));
	}

	@Test
	public void recordCoordinate() {
		drawingRecorder.recordCoordinate(0, 1, 2);
		assertThat(
				drawingRecorder.toString(),
				equalTo("{\"coords\":[{\"x\":0, \"y\":1, \"time\":2}]}"));
	}

	@Test
	public void recordTouchEnd() {
		drawingRecorder.recordCoordinate(0, 1, 2);
		drawingRecorder.recordTouchEnd();
		assertThat(
				drawingRecorder.toString(),
				equalTo("{\"coords\":[{\"x\":0, \"y\":1, \"time\":2},\n{\"touchEnd\":1}]}"));
	}
}
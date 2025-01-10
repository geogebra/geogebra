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
package org.geogebra.common.gui.popup.autocompletion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.junit.Test;

public class AutocompletionPopupPositionerTest {

	private static final double MARGIN = 8;
	private static final double INPUT_HEIGHT = 56;
	private static final Size SINGLE_LINE_POPUP = new Size(320, 48 + 16);
	private static final Rectangle PHONE_FRAME = new Rectangle(8, 320, 8, 780);
	private static final Rectangle DESKTOP_FRAME = new Rectangle(0, 1000, 0, 1000);

	private final AutocompletionPopupPositioner positioner = new AutocompletionPopupPositioner();

	@Test
	public void testVerticalPositionPopupAboveInputBar() {
		Rectangle inputFrame =
				new Rectangle(0, PHONE_FRAME.getWidth(), PHONE_FRAME.getHeight() / 6 * 4,
						PHONE_FRAME.getHeight() / 6 * 4 + INPUT_HEIGHT);
		Rectangle frame = positioner.calculatePopupFrame(inputFrame,
				SINGLE_LINE_POPUP, PHONE_FRAME);
		assertThat(frame.getMaxY(), greaterThanOrEqualTo(inputFrame.getMinY()));
	}

	@Test
	public void testVerticalPositionPopupBelowInputBar() {
		Rectangle inputFrame = new Rectangle(0, PHONE_FRAME.getWidth(),
				INPUT_HEIGHT / 2, INPUT_HEIGHT / 2 + INPUT_HEIGHT);
		Rectangle frame = positioner.calculatePopupFrame(inputFrame,
				SINGLE_LINE_POPUP, PHONE_FRAME);
		assertThat(frame.getMinY(), greaterThanOrEqualTo(inputFrame.getMaxY()));
	}

	@Test
	public void testHorizontalPositionForPhone() {
		Rectangle inputFrame = new Rectangle(0, PHONE_FRAME.getWidth(), 0, INPUT_HEIGHT);
		Rectangle frame = positioner.calculatePopupFrame(inputFrame,
				SINGLE_LINE_POPUP, PHONE_FRAME);
		assertThat(frame.getMinX(), equalTo(MARGIN));
		assertThat(frame.getMaxX(), equalTo(PHONE_FRAME.getWidth() - MARGIN));
	}

	@Test
	public void testHorizontalPositionForDesktop() {
		Rectangle inputFrame = new Rectangle(48, 256, 0, INPUT_HEIGHT);
		Rectangle frame = positioner.calculatePopupFrame(inputFrame,
				SINGLE_LINE_POPUP, DESKTOP_FRAME);
		assertThat(frame.getMinX(), equalTo(inputFrame.getMinX()));
		assertThat(frame.getWidth(), equalTo(520.0));
	}
}

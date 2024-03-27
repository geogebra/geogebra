package org.geogebra.web.html5.util;

import org.gwtproject.user.client.ui.Widget;

public enum DataTest {
	MARBLE("marble"),
	ALGEBRA_OUTPUT_ROW("algebraOutputRow"),
	ALGEBRA_INPUT("algebraInput"),
	ALGEBRA_ITEM_MORE_BUTTON("algebraItemMore"),
	ALGEBRA_ITEM_PLAY_BUTTON("algebraItemPlayPause"),
	ALGEBRA_ITEM_SYMBOLIC_BUTTON("algebraItemSymbolic"),
	ALGEBRA_ITEM_SLIDER_LABEL_MIN("algebraItemSliderLabelMin"),
	ALGEBRA_ITEM_SLIDER_LABEL_MAX("algebraItemSliderLabelMax"),
	ALGEBRA_ITEM_SLIDER_MIN("algebraItemSliderMin"),
	ALGEBRA_ITEM_SLIDER_MAX("algebraItemSliderMax"),
	ALGEBRA_ITEM_SLIDER_STEP("algebraItemSliderStep");

	private final String name;

	DataTest(String name) {
		this.name = name;
	}

	/**
	 * Sets testing attribute on widget to the enum value.
	 * @param widget to set attribute to.
	 */
	public void apply(Widget widget) {
		if (widget == null) {
			return;
		}

		TestHarness.setAttr(widget, name);
	}

	/**
	 * Sets testing attribute on widget to the enum value.
	 * @param widget to set attribute to.
	 * @param index number to add to the attribute value to
	 * support unique values.
	 */
	public void applyWithIndex(Widget widget, int index) {
		TestHarness.setAttr(widget, name + index);
	}
}

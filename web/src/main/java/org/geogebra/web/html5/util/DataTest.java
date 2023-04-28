package org.geogebra.web.html5.util;

import org.gwtproject.user.client.ui.Widget;

public enum DataTest {
	MARBLE("marble"),
	ALGEBRA_OUTPUT_ROW("algebraOutputRow"),
	ALGEBRA_INPUT("algebraInput") {
		@Override
		public String getId() {
			return name();
		}
	},
	ALGEBRA_ITEM_MORE_BUTTON("algebraItemMore"),
	ALGEBRA_ITEM_PLAY_BUTTON("algebraItemPlay"),
	ALGEBRA_ITEM_SYMBOLIC_BUTTON("algebraItemSymbolic"),
	ALGEBRA_ITEM_SLIDER_MIN("algebraItemSliderMin"),
	ALGEBRA_ITEM_SLIDER_MAX("algebraItemSliderMax"),
	ALGEBRA_ITEM_SLIDER_STEP("algebraItemSliderStep");

	private final String name;
	private int counter;
	DataTest(String name) {
		this.name = name;
		this.counter = 0;
	}


	public String getId() {
		counter++;
		return name + counter;

	}

	public void apply(Widget widget) {
		TestHarness.setAttr(widget, getId());
	}
}

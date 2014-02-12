package geogebra.touch.gui.elements.stylebar;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.util.Slider;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

class ColorBar extends FlowPanel {

	StyleBar styleBar;
	TouchModel touchModel;
	private ColorBarSlider colorBarSlider = new ColorBarSlider();
	private static final int SLIDER_MAX = 10;

	private GColor[] colors = { GColor.BLACK, GeoGebraColorConstants.BROWN,
			GeoGebraColorConstants.ORANGE, GColor.YELLOW, GColor.BLUE,
			GColor.CYAN, GColor.GREEN, GeoGebraColorConstants.DARKGREEN,
			GeoGebraColorConstants.LIGHTBLUE,
			GeoGebraColorConstants.LIGHTVIOLET, GColor.MAGENTA, GColor.RED,
			GColor.DARK_GRAY, GColor.GRAY, GColor.LIGHT_GRAY, GColor.WHITE };

	private FlowPanel colorButtonPanel;

	/**
	 * Initializes the {@link ScrollPanel} and adds the different
	 * {@link geogebra.touch.gui.elements.stylebar.Colors color-choices} to it.
	 */
	ColorBar(StyleBar styleBar, TouchModel touchModel) {

		this.styleBar = styleBar;
		this.touchModel = touchModel;

		this.addStyleName("colorBar");

		this.colorButtonPanel = new FlowPanel();
		this.colorButtonPanel.setStyleName("styleBarButtonPanel");

		for (GColor c : this.colors) {
			this.colorButtonPanel.add(new ColorButton(c));
		}

		this.add(this.colorButtonPanel);

		update();
	}

	private class ColorButton extends FastButton {

		private GColor color;

		ColorButton(GColor c) {

			this.color = c;

			this.setStyleName("button");

			this.getElement().getStyle().setBackgroundImage("initial");

			// windows explorer didn't like .getStyle().setBackgroundColor(...),
			// so
			// I
			// replaced it:
			this.getElement().setAttribute("style",
					"background: " + GColor.getColorString(this.color));

			this.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick() {
					onSingleClick();
				}
			});

		}

		void onSingleClick() {

			ColorBar.this.styleBar.updateColor(GColor
					.getColorString(this.color));
			ColorBar.this.touchModel.getGuiModel().setColor(this.color);

			if (ColorBar.this.touchModel.lastSelected() != null
					&& ColorBar.this.touchModel.isColorChangeAllowed()
					&& StyleBarStatic.applyColor(
							ColorBar.this.touchModel.getSelectedGeos(),
							this.color)) {
				ColorBar.this.touchModel.lastSelected().updateRepaint();
			}

			ColorBar.this.touchModel.setStoreOnClose();
		}

		@Override
		public void onHoldPressDownStyle() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onHoldPressOffStyle() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisablePressStyle() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEnablePressStyle() {
			// TODO Auto-generated method stub

		}
	}

	private class ColorBarSlider extends Slider {
		public ColorBarSlider() {
			this.setMinimum(0);
			this.setMaximum(SLIDER_MAX);

			this.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					// cast to float to prevent int-division (which would always
					// result in 0 or 1)
					ColorBar.this.touchModel.getGuiModel().setAlpha(
							event.getValue().intValue() / (float) SLIDER_MAX);
				}
			});
		}
	}

	void update() {
		remove(this.colorBarSlider);

		// add slider only if there is at least one fillable element
		if (this.touchModel.getLastAlpha() != -1
				|| this.touchModel.getGuiModel().getDefaultType() == ConstructionDefaults.DEFAULT_POLYGON
				|| this.touchModel.getGuiModel().getDefaultType() == ConstructionDefaults.DEFAULT_CONIC
				|| this.touchModel.getGuiModel().getDefaultType() == ConstructionDefaults.DEFAULT_CONIC_SECTOR
				|| this.touchModel.getGuiModel().getDefaultType() == ConstructionDefaults.DEFAULT_ANGLE) {

			int alpha = (int) (this.touchModel.getGuiModel().getDefaultGeo() != null ? this.touchModel
					.getGuiModel().getDefaultGeo().getAlphaValue()
					* SLIDER_MAX
					: ColorBar.this.touchModel.getLastAlpha() * SLIDER_MAX);
			this.colorBarSlider.setValue(Integer.valueOf(alpha));
			this.add(this.colorBarSlider);
		}
	}
}

package geogebra.touch.gui.elements.stylebar;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.html5.gui.util.Slider;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.StyleBarDefaultSettings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ColorBar extends FlowPanel {

	protected StyleBar styleBar;
	protected TouchModel touchModel;
	private ColorBarSlider colorBarSlider = new ColorBarSlider();

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
	public ColorBar(StyleBar styleBar, TouchModel touchModel) {

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

	class ColorButton extends PushButton {

		private GColor color;

		public ColorButton(GColor c) {

			this.color = c;

			this.setStyleName("button");

			this.getElement().getStyle().setBackgroundImage("initial");

			// windows explorer didn't like .getStyle().setBackgroundColor(...),
			// so
			// I
			// replaced it:
			this.getElement().setAttribute("style",
					"background: " + GColor.getColorString(this.color));

		}

		@Override
		public void onClick() {

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

			ColorBar.this.touchModel.storeOnClose();
		}
	}

	class ColorBarSlider extends Slider {
		public ColorBarSlider() {
			this.setMinimum(0);
			this.setMaximum(10);

			this.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					ColorBar.this.touchModel.getGuiModel().setAlpha(
							event.getValue().intValue() / 10f);

					final List<GeoElement> fillable = new ArrayList<GeoElement>();
					for (final GeoElement geo : ColorBar.this.touchModel
							.getSelectedGeos()) {
						if (geo.isFillable()) {
							fillable.add(geo);
						}
					}

					if (fillable.size() > 0
							&& StyleBarStatic.applyAlpha(fillable, event
									.getValue().intValue() / 10f)) {
						fillable.get(0).updateRepaint();
						ColorBar.this.touchModel.storeOnClose();
					}
				}
			});
		}
	}

	public void update() {
		remove(this.colorBarSlider);

		// add slider only if there is at least one fillable element
		if (this.touchModel.getLastAlpha() != -1
				|| this.touchModel.getCommand().getStyleBarEntries() == StyleBarDefaultSettings.Polygon) {
			this.colorBarSlider
					.setValue(Integer.valueOf((int) (ColorBar.this.touchModel
							.getLastAlpha() * 10)));
			this.add(this.colorBarSlider);
		}
	}
}

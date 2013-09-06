package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.util.Slider;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

class LineStyleBar extends FlowPanel {
	private static final int SLIDER_MIN = 1;
	private static final int SLIDER_MAX = 12;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	static FastButton[] lineStyle = {
			new StandardButton(LafIcons.line_solid()),
			new StandardButton(LafIcons.line_dashed_long()),
			new StandardButton(LafIcons.line_dashed_short()),
			new StandardButton(LafIcons.line_dotted()),
			new StandardButton(LafIcons.line_dash_dot()) };

	FastButton activeButton;
	private FlowPanel buttonPanel;
	private Slider slider = new Slider();
	TouchModel touchModel;

	LineStyleBar(final TouchModel model) {
		this.addStyleName("lineStyleBar");

		this.touchModel = model;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < lineStyle.length; i++) {
			final int index = i;

			lineStyle[i].addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick() {
					StyleBarStatic.applyLineStyle(
							LineStyleBar.this.touchModel.getSelectedGeos(),
							index);
					LineStyleBar.this.touchModel.getGuiModel().setLineStyle(
							index);

					if (LineStyleBar.this.activeButton != null
							&& LineStyleBar.this.activeButton != lineStyle[index]) {
						LineStyleBar.this.activeButton
								.removeStyleName("active");
					}
					LineStyleBar.this.activeButton = lineStyle[index];
					LineStyleBar.this.activeButton.addStyleName("active");
					LineStyleBar.this.touchModel.setStoreOnClose();
				}
			});
			this.buttonPanel.add(lineStyle[i]);
		}

		this.add(this.buttonPanel);

		this.slider.setMinimum(SLIDER_MIN);
		this.slider.setMaximum(SLIDER_MAX);

		update();

		if (this.touchModel.lastSelected() != null) {
			this.slider.setValue(Integer.valueOf(this.touchModel.lastSelected()
					.getLineThickness()));
		} else if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.slider.setValue(new Integer(this.touchModel.getKernel()
					.getApplication().getEuclidianView1()
					.getEuclidianController().getPen().getPenSize()));
		}

		this.slider.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				StyleBarStatic.applyLineSize(LineStyleBar.this.touchModel
						.getSelectedGeos(), event.getValue().intValue());
				LineStyleBar.this.touchModel.getGuiModel().setLineSize(
						event.getValue().intValue());
				LineStyleBar.this.touchModel.setStoreOnClose();

			}
		});
		this.add(this.slider);
	}

	void update() {
		if (this.touchModel.getGuiModel().getDefaultGeo() != null) {
			this.slider.setValue(Integer.valueOf(this.touchModel.getGuiModel()
					.getDefaultGeo().getLineThickness()));
		} else if (this.touchModel.lastSelected() != null) {
			this.slider.setValue(Integer.valueOf(this.touchModel.lastSelected()
					.getLineThickness()));
		} else if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.slider.setValue(new Integer(this.touchModel.getKernel()
					.getApplication().getEuclidianView1()
					.getEuclidianController().getPen().getPenSize()));
		}

		// set to -1, in case not GeoElement with linestyle is selected
		int style = this.touchModel.getLineStyle();

		if (style < 0) {
			GeoElement geo = this.touchModel.getGuiModel().getDefaultGeo();
			if (geo != null) {
				// get default style for the actual tool
				style = geo.getLineType();
			}
		}

		if (style >= 0) {
			for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++) {
				if (EuclidianStyleBarStatic.lineStyleArray[i].intValue() == style) {
					if (this.activeButton != null
							&& this.activeButton != lineStyle[i]) {
						this.activeButton.removeStyleName("active");
					}
					this.activeButton = lineStyle[i];
					this.activeButton.addStyleName("active");
				}
			}
		} else if (this.activeButton != null) {
			this.activeButton.removeStyleName("active");
		}
	}
}

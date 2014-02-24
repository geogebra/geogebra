package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.util.Slider;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

class PointStyleBar extends FlowPanel {
	private static final int SLIDER_MIN = 1;
	private static final int SLIDER_MAX = 9;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	static FastButton[] pointStyle = {
			new StandardButton(LafIcons.point_full()),
			new StandardButton(LafIcons.point_cross_diag()),
			new StandardButton(LafIcons.point_empty()),
			new StandardButton(LafIcons.point_cross()),
			new StandardButton(LafIcons.point_diamond()),
			new StandardButton(LafIcons.point_diamond_empty()),
			new StandardButton(LafIcons.point_up()),
			new StandardButton(LafIcons.point_down()),
			new StandardButton(LafIcons.point_right()),
			new StandardButton(LafIcons.point_left()) };

	FastButton activeButton;
	private FlowPanel buttonPanel;
	private Slider slider = new Slider();
	TouchModel touchModel;

	PointStyleBar(final StyleBar styleBar) {
		this.addStyleName("lineStyleBar");

		this.touchModel = styleBar.getTouchModel();

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < pointStyle.length; i++) {
			final int index = i;

			pointStyle[i].addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick() {
					styleBar.setPointStyleImage(pointStyle[index]);
					StyleBarStatic.applyPointStyle(
							PointStyleBar.this.touchModel.getSelectedGeos(),
							index);
					PointStyleBar.this.touchModel.getGuiModel().setPointStyle(
							index);

					if (PointStyleBar.this.activeButton != null
							&& PointStyleBar.this.activeButton != pointStyle[index]) {
						PointStyleBar.this.activeButton
								.removeStyleName("active");
					}
					PointStyleBar.this.activeButton = pointStyle[index];
					PointStyleBar.this.activeButton.addStyleName("active");
					PointStyleBar.this.touchModel.setStoreOnClose();
				}
			});
			this.buttonPanel.add(pointStyle[i]);
		}

		this.add(this.buttonPanel);

		this.slider.setMinimum(SLIDER_MIN);
		this.slider.setMaximum(SLIDER_MAX);

		update();

		if (this.touchModel.lastSelected() instanceof PointProperties) {
			PointProperties pt = (PointProperties) this.touchModel
					.lastSelected();
			this.slider.setValue(Integer.valueOf(pt.getPointSize()));
		}

		this.slider.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				StyleBarStatic.applyPointSize(PointStyleBar.this.touchModel
						.getSelectedGeos(), event.getValue().intValue());
				PointStyleBar.this.touchModel.getGuiModel().setPointSize(
						event.getValue().intValue());
				PointStyleBar.this.touchModel.setStoreOnClose();

			}
		});
		this.add(this.slider);
	}

	void update() {
		if (this.touchModel.getGuiModel().getDefaultGeo() instanceof PointProperties) {
			this.slider.setValue(Integer
					.valueOf(((PointProperties) this.touchModel.getGuiModel()
							.getDefaultGeo()).getPointSize()));
		} else if (this.touchModel.lastSelected() instanceof PointProperties) {
			this.slider.setValue(Integer
					.valueOf(((PointProperties) this.touchModel.lastSelected())
							.getPointSize()));
		}

		// set to -1, in case not GeoElement with pointstyle is selected
		int style = this.touchModel.getPointStyle();

		if (style < 0) {
			GeoElement geo = this.touchModel.getGuiModel().getDefaultGeo();
			if (geo instanceof PointProperties) {
				// get default style for the actual tool
				style = ((PointProperties) geo).getPointStyle();
			}
		}

		if (style >= 0) {
			for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++) {
				if (EuclidianStyleBarStatic.pointStyleArray[i].intValue() == style) {
					if (this.activeButton != null
							&& this.activeButton != pointStyle[i]) {
						this.activeButton.removeStyleName("active");
					}
					this.activeButton = pointStyle[i];
					this.activeButton.addStyleName("active");
				}
			}
		} else if (this.activeButton != null) {
			this.activeButton.removeStyleName("active");
		}
	}
}

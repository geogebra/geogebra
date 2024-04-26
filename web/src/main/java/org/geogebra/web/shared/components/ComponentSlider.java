package org.geogebra.web.shared.components;

import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSlider extends FlowPanel {
	private final AppW appW;

	public ComponentSlider(AppW appW) {
		this.appW = appW;
		buildGui();
	}

	private void buildGui() {
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText("Thickness", "sliderLabel");
		PenPreview preview = new PenPreview(appW, 100, 5);

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		labelPreviewHolder.add(preview);

		add(labelPreviewHolder);
		add(buildSlider());
	}

	private SliderW buildSlider() {
		SliderW slider = new SliderW(0, 20);
		slider.addStyleName("sliderComponent");
		return slider;
	}
}

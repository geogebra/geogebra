package org.geogebra.web.shared.components;

import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSlider extends FlowPanel {
	private final AppW appW;
	private PenPreview preview;

	public ComponentSlider(AppW appW) {
		this.appW = appW;
		addStyleName("sliderComponent");
		buildGui();
	}

	private void buildGui() {
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText("Thickness", "sliderLabel");
		preview = new PenPreview(appW, 100, 5);
		preview.addStyleName("preview");

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		labelPreviewHolder.add(preview);

		add(labelPreviewHolder);
		add(buildSlider());
	}

	private SliderW buildSlider() {
		SliderW slider = new SliderW(0, 20);
		slider.addStyleName("slider");
		return slider;
	}

	public void updatePreview() {
		preview.update();
	}
}

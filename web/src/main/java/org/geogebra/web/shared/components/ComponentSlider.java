package org.geogebra.web.shared.components;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.dom.style.shared.Visibility;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSlider extends FlowPanel {
	private static final int MAX_ERASER_SIZE = 200;
	private static final int MIN_ERASER_SIZE = 10;
	private static final int ERASER_STEP = 10;
	private final AppW appW;
	private PenPreview preview;
	private SliderPanelW sliderPanel;
	private Label sliderLabel;
	private int lastSelectedMode = MODE_PEN;

	/**
	 * constructor
	 * @param appW - application
	 */
	public ComponentSlider(AppW appW) {
		this.appW = appW;
		addStyleName("sliderComponent");
		buildGui();
	}

	private void buildGui() {
		sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu("Thickness"), "sliderLabel");
		preview = new PenPreview(appW.getActiveEuclidianView().getEuclidianController().getPen(),
				30, 30);
		preview.addStyleName("preview");

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		labelPreviewHolder.add(preview);

		add(labelPreviewHolder);
		buildSlider();
		add(sliderPanel);
	}

	private void buildSlider() {
		sliderPanel = new SliderPanelW(0, 20, appW.getKernel(), false);
		sliderPanel.getSlider().addStyleName("slider");
		sliderPanel.getSlider().addInputHandler(() -> sliderValueChanged(sliderPanel.getValue()));
	}

	/**
	 * update preview and slider
	 */
	public void update(int mode) {
		lastSelectedMode = mode;
		preview.getElement().getStyle().setVisibility(mode != MODE_ERASER
				? Visibility.VISIBLE : Visibility.HIDDEN);
		preview.update();
		if (sliderLabel != null) {
			sliderLabel.setText(appW.getLocalization().getMenu(mode == MODE_ERASER
					? "Size" : "Thickness"));
		}
		setSliderRange(mode != MODE_ERASER);
		updateSliderValue(mode);
	}

	private void sliderValueChanged(double value) {
		if (lastSelectedMode == MODE_ERASER) {
			appW.getSettings().getPenTools().setDeleteToolSize((int) value);
		} else {
			appW.getActiveEuclidianView().getEuclidianController()
					.getPen().setPenSize((int) value);
			update(lastSelectedMode);
		}
	}

	private void updateSliderValue(int mode) {
		PenToolsSettings settings = appW.getSettings().getPenTools();
		int sliderValue = 0;
		switch (mode) {
		case MODE_ERASER:
			sliderValue = settings.getDeleteToolSize();
			break;
		case MODE_HIGHLIGHTER:
			sliderValue = settings.getLastHighlighterThickness();
			break;
		case MODE_PEN:
		default:
			sliderValue = settings.getLastPenThickness();
			break;
		}

		sliderPanel.setValue((double) sliderValue);
	}

	private void setSliderRange(boolean isPenOrHighlighter) {
		sliderPanel.setMinimum(isPenOrHighlighter ? EuclidianConstants.MIN_PEN_HIGHLIGHTER_SIZE
				: MIN_ERASER_SIZE, false);
		sliderPanel.setMaximum(isPenOrHighlighter ? EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE
				: MAX_ERASER_SIZE, false);
		sliderPanel.setStep(
				isPenOrHighlighter ? EuclidianConstants.DEFAULT_PEN_STEP : ERASER_STEP);
	}
}

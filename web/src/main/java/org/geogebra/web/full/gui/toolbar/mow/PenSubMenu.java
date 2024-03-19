package org.geogebra.web.full.gui.toolbar.mow;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Pen/Eraser/Color submenu for MOWToolbar.
 */
public class PenSubMenu extends SubMenuPanel {
	private static final int MAX_ERASER_SIZE = 200;
	private static final int MIN_ERASER_SIZE = 10;
	private static final int ERASER_STEP = 10;
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	/** whether colors are enabled */
	boolean colorsEnabled;
	// preset colors black, green, teal, blue, purple, magenta, red, carrot,
	// yellow
	private Map<MOWToolbarColor, Label> colorMap;

	@Override
	public void setAriaHidden(boolean hidden) {
		super.setAriaHidden(hidden);
		if (hidden) {
			setColorsEnabled(false);
			disableSlider(true);
		}
	}

	/**
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app);
		addStyleName("penSubMenu");
		// needed for slider mouse events
		ClickStartHandler.initDefaults(this, false, true);
		app.getSettings().getPenTools().addListener(s -> setMode(app.getMode()));
	}

	/**
	 * Create color buttons for selecting pen color
	 * @param colorData translation key and hex code for the color
	 * @return button
	 */
	private Label createColorButton(MOWToolbarColor colorData) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				colorData.getGColor());
		Label label = new Label();
		AriaHelper.setLabel(label, app.getLocalization().getColor(colorData.getGgbTransKey()));
		label.getElement().setAttribute("role", "button");
		label.getElement().setTabIndex(0);
		color.applyToLabel(label);
		label.addStyleName("mowColorButton");
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!colorsEnabled) {
					return;
				}
				getPenGeo().setPenColor(colorData.getGColor());
				setSelectedColor(colorData.getGColor());
			}
		});
		return label;
	}

	private void fillColorButtonMap() {
		colorMap = Arrays.stream(MOWToolbarColor.values())
				.collect(Collectors.toMap(Function.identity(), this::createColorButton));
		new FocusableWidget(AccessibilityGroup.NOTES_COLOR_PANEL, null,
				colorMap.values().toArray(new Widget[0])).attachTo(app);
	}

	private void createMoreColorButton() {
		btnCustomColor = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(), null, 24);
		AriaHelper.setLabel(btnCustomColor, app.getLocalization().getMenu("ToolbarColor"
				+ ".MoreColors"));
		btnCustomColor.addStyleName("mowColorPlusButton");
		btnCustomColor.addFastClickHandler(source -> openColorDialog());
		new FocusableWidget(
				AccessibilityGroup.NOTES_COLOR_CUSTOM, null, btnCustomColor).attachTo(app);
	}

	private void createColorPanel() {
		FlowPanel colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");

		fillColorButtonMap();
		createMoreColorButton();

		for (Label btn : colorMap.values()) {
			colorPanel.add(btn);
		}
		colorPanel.add(btnCustomColor);
		createSizePanel(colorPanel);
		addToContentPanel(colorPanel);
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel(FlowPanel colorPanel) {
		FlowPanel sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("mowOptionsSlider");
		setSliderRange(true);
		slider.setWidth(150);
		preview = new PenPreview(app, 50, 30);
		preview.addStyleName("preview");
		slider.add(preview);
		sizePanel.add(slider);
		colorPanel.add(sizePanel);
		slider.getSlider().addInputHandler(() -> sliderValueChanged(slider.getValue()));
		new FocusableWidget(AccessibilityGroup.NOTES_PEN_THICKNESS_SLIDER,
				null, slider.getSlider()).attachTo(app);
	}

	private void setSliderRange(boolean isPen) {
		// same min for pen and highlighter
		slider.setMinimum(isPen ? EuclidianConstants.MIN_PEN_HIGHLIGHTER_SIZE
				: MIN_ERASER_SIZE, false);
		slider.setMaximum(isPen ? EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE
				: MAX_ERASER_SIZE, false);
		slider.setStep(
				isPen ? EuclidianConstants.DEFAULT_PEN_STEP : ERASER_STEP);
	}

	/**
	 * Sets the size of pen/eraser from the slider value.
	 * 
	 * @param value
	 *            value of slider
	 */
	void sliderValueChanged(double value) {
		if (colorsEnabled) {
			getPenGeo().setPenSize((int) value);
			updatePreview();
		} else {
			app.getSettings().getPenTools().setDeleteToolSize((int) value);
		}
		closeFloatingMenus();
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		super.createPanelRow(ToolBar.getNotesPenToolBar());
		createColorPanel();
	}

	private void updatePenStyleAndUI(int lastThickness) {
		setColorsEnabled(true);

		setSliderRange(true);
		slider.setValue((double) lastThickness);
		getPenGeo().updateMode();
		disableSlider(false);
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		setColorsEnabled(false);
		setSliderRange(false);
		int delSize = getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		disableSlider(false);
		preview.setVisible(false);
	}

	private void disableSlider(boolean disable) {
		slider.getElement().setAttribute("disabled", String.valueOf(disable));
		AriaHelper.setHidden(slider.getSlider(), disable);
		slider.disableSlider(disable);
	}

	/**
	 * @param selectedColor
	 *            color to select
	 */
	public void setSelectedColor(GColor selectedColor) {
		updateButtons(selectedColor);
		updatePreview();
	}

	private void updateButtons(GColor selectedColor) {
		for (Map.Entry<MOWToolbarColor, Label> colorBtnPair : colorMap.entrySet()) {
			disableButton(colorBtnPair.getValue(), !colorsEnabled);
			colorBtnPair.getValue().setStyleName("mowColorButton-selected",
					colorsEnabled && colorBtnPair.getKey().getGColor().equals(selectedColor));
		}
	}

	private void setColorsEnabled(boolean enable) {
		GColor active = app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER
				? getSettings().getLastSelectedHighlighterColor()
				: getSettings()
				.getLastSelectedPenColor();
		colorsEnabled = enable;
		updateButtons(active);
		disableButton(btnCustomColor, !enable);
	}

	private void disableButton(Widget label, boolean b) {
		Dom.toggleClass(label, "disabled", b);
		AriaHelper.setHidden(label, b);
	}

	private EuclidianPen getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen();
	}

	private PenToolsSettings getSettings() {
		return app.getSettings().getPenTools();
	}

	@Override
	public void setMode(int mode) {
		setColorsEnabled(false);
		super.setMode(mode);
		if (mode == EuclidianConstants.MODE_SELECT
				|| mode == EuclidianConstants.MODE_SELECT_MOW) {
			disableSlider(true);
		} else if (mode == EuclidianConstants.MODE_ERASER) {
			doSelectEraser();
		} else if (mode == EuclidianConstants.MODE_PEN
			|| mode == EuclidianConstants.MODE_HIGHLIGHTER) {
			boolean isPen = mode == EuclidianConstants.MODE_PEN;
			int lastThickness = isPen ? getSettings().getLastPenThickness()
					: getSettings().getLastHighlighterThickness();
			updatePenStyleAndUI(lastThickness);
		}
	}

	/**
	 * @return get preview of pen
	 */
	public PenPreview getPreview() {
		return preview;
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_PEN;
	}

	private void updatePreview() {
		preview.update();
	}

	/**
	 * Opens the custom color dialog
	 */
	public void openColorDialog() {
		if (colorsEnabled) {
			final EuclidianPen penGeo = getPenGeo();
			DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
			GColor originalColor = penGeo.getPenColor();
			dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {
				@Override
				public void onForegroundSelected() {
					// do nothing here
				}

				@Override
				public void onColorChange(GColor color) {
					penGeo.setPenColor(color);
					setSelectedColor(color);
				}

				@Override
				public void onClearBackground() {
					// do nothing
				}

				@Override
				public void onBarSelected() {
					// do nothing
				}

				@Override
				public void onBackgroundSelected() {
					// do nothing
				}

				@Override
				public void onAlphaChange() {
					// do nothing
				}
			});
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		for (Map.Entry<MOWToolbarColor, Label> colorBtnPair : colorMap.entrySet()) {
			AriaHelper.setLabel(colorBtnPair.getValue(),
					app.getLocalization().getColor(colorBtnPair.getKey().getGgbTransKey()));
		}
		AriaHelper.setLabel(btnCustomColor, app.getLocalization().getMenu("ToolbarColor"
				+ ".MoreColors"));
	}
}

package org.geogebra.web.full.gui.toolbar.mow;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.EuclidianSettings;
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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
		slider.addValueChangeHandler(event -> sliderValueChanged(event.getValue()));
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
			getPenGeo().setLineThickness((int) value);
			if (app.getActiveEuclidianView()
					.getMode() == EuclidianConstants.MODE_PEN) {
				getSettings().setLastPenThickness((int) value);
			} else if (app.getActiveEuclidianView()
					.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
				getSettings().setLastHighlighterThinckness((int) value);
			}
			updatePreview();
		} else {
			app.getActiveEuclidianView().getSettings().setDeleteToolSize((int) value);
		}
		closeFloatingMenus();
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		super.createPanelRow(ToolBar.getNotesPenToolBar());
		createColorPanel();
	}

	private void updatePenStyleAndUI(GColor lastColor, int lastThickness,
			int opacity) {
		setColorsEnabled(true);
		selectColor(lastColor);
		setSliderRange(true);
		slider.setValue((double) lastThickness);
		getPenGeo().setLineThickness(lastThickness);
		getPenGeo().setLineOpacity(opacity);
		disableSlider(false);
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		setColorsEnabled(false);
		setSliderRange(false);
		int delSize = app.getActiveEuclidianView().getSettings()
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
		if (selectedColor == null) {
			return;
		}

		for (Map.Entry<MOWToolbarColor, Label> colorBtnPair : colorMap.entrySet()) {
			if (colorBtnPair.getKey().getGColor().equals(selectedColor)) {
				getPenGeo().setObjColor(colorBtnPair.getKey().getGColor());
				if (colorsEnabled) {
					colorBtnPair.getValue().addStyleName("mowColorButton-selected");
					if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
						getPenGeo().setLineOpacity(
								EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
						getSettings().setLastSelectedHighlighterColor(colorBtnPair.getKey()
								.getGColor());
					} else {
						getSettings().setLastSelectedPenColor(colorBtnPair.getKey().getGColor());
					}
				}
			} else {
				colorBtnPair.getValue().removeStyleName("mowColorButton-selected");
			}
		}
		updatePreview();
	}

	// remember and set a color that was picked from color chooser
	private void selectColor(GColor color) {
		getPenGeo().setObjColor(color);
		updatePreview();
	}

	private void setColorsEnabled(boolean enable) {
		for (Map.Entry<MOWToolbarColor, Label> colorBtnPair : colorMap.entrySet()) {
			disableButton(colorBtnPair.getValue(), !enable);
			if (enable) {
				if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
					if (colorBtnPair.getKey().getGColor() == getSettings()
							.getLastSelectedHighlighterColor()) {
						colorBtnPair.getValue().addStyleName("mowColorButton-selected");
					}
				} else if (app.getMode() == EuclidianConstants.MODE_PEN) {
					if (colorBtnPair.getKey().getGColor() == getSettings()
							.getLastSelectedPenColor()) {
						colorBtnPair.getValue().addStyleName("mowColorButton-selected");
					}
				}
			} else {
				colorBtnPair.getValue().removeStyleName("mowColorButton-selected");
			}
		}
		disableButton(btnCustomColor, !enable);
		colorsEnabled = enable;
	}

	private void disableButton(Widget label, boolean b) {
		Dom.toggleClass(label, "disabled", b);
		AriaHelper.setHidden(label, b);
	}

	private GeoElement getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen().defaultPenLine;
	}

	private EuclidianSettings getSettings() {
		return app.getActiveEuclidianView().getSettings();
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
			GColor lastColor = isPen ? getSettings().getLastSelectedPenColor()
					: getSettings().getLastSelectedHighlighterColor();
			int lastThickness = isPen ? getSettings().getLastPenThickness()
					: getSettings().getLastHighlighterThinckness();
			int opacity = isPen ? 255 : EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY;
			updatePenStyleAndUI(lastColor, lastThickness, opacity);
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
			final GeoElement penGeo = getPenGeo();
			DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
			GColor originalColor = penGeo.getObjectColor();
			dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {
				@Override
				public void onForegroundSelected() {
					// do nothing here
				}

				@Override
				public void onColorChange(GColor color) {
					penGeo.setObjColor(color);
					getSettings().setLastSelectedHighlighterColor(color);
					penGeo.setLineOpacity(
							app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER
									? EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY
									: 255);
					setSelectedColor(null);
					getPreview().update();
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

	/**
	 * reset size of pen (for pen and highlighter)
	 */
	public void resetPen() {
		getSettings().setLastPenThickness(EuclidianConstants.DEFAULT_PEN_SIZE);
		getSettings().setLastHighlighterThinckness(EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE);
		if (app.getMode() == EuclidianConstants.MODE_PEN) {
			slider.setValue((double) getSettings().getLastPenThickness());
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

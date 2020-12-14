package org.geogebra.web.full.gui.toolbar.mow;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pen/Eraser/Color submenu for MOWToolbar.
 * 
 * @author Laszlo Gal
 *
 */
public class PenSubMenu extends SubMenuPanel {
	private static final int MAX_ERASER_SIZE = 200;
	private static final int MIN_ERASER_SIZE = 10;
	private static final int ERASER_STEP = 10;
	private ToolButton pen;
	private ToolButton eraser;
	private ToolButton highlighter;
	private ToolButton select;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	/** whether colors are enabled */
	boolean colorsEnabled;
	// preset colors black, green, teal, blue, purple, magenta, red, carrot,
	// yellow
	private HashMap<MOWToolbarColor, Label> colorMap;

	@Override
	public void setAriaHidden(boolean hidden) {
		super.setAriaHidden(hidden);
		if (hidden) {
			setColorsEnabled(false);
			disableSlider(true);
		}
	}

	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app/* , false */);
		addStyleName("penSubMenu");
		// needed for slider mouse events
		ClickStartHandler.initDefaults(this, false, true);
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		penPanel.addStyleName("penPanel");
		pen = new ToolButton(EuclidianConstants.MODE_PEN, app, this);
		eraser = new ToolButton(EuclidianConstants.MODE_ERASER, app, this);
		highlighter = new ToolButton(EuclidianConstants.MODE_HIGHLIGHTER,
				app, this);
		select = new ToolButton(EuclidianConstants.MODE_SELECT_MOW, app,
				this);
		penPanel.add(LayoutUtilW.panelRow(select, pen, eraser, highlighter));
		toolButtons.add(select);
		toolButtons.add(pen);
		toolButtons.add(eraser);
		toolButtons.add(highlighter);
		makeButtonsAccessible(AccessibilityGroup.NOTES_TOOL_SELECT);
	}

	/**
	 * Create color buttons for selecting pen color
	 * 
	 * @param aColor
	 *            color
	 * @param ariaLabelTransKey
	 * 			  ggbtrans key for the aria-label
	 *
	 * @return button
	 */
	private Label createColorButton(final GColor aColor, String ariaLabelTransKey) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				aColor);
		Label label = new Label();
		AriaHelper.setLabel(label, app.getLocalization().getColor(ariaLabelTransKey));
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
				setSelectedColor(aColor);
			}
		});
		return label;
	}

	private void fillColorButtonMap() {
		colorMap = new HashMap<>();
		addToColorMap(MOWToolbarColor.BLACK);
		addToColorMap(MOWToolbarColor.GREEN);
		addToColorMap(MOWToolbarColor.TEAL);
		addToColorMap(MOWToolbarColor.BLUE);
		addToColorMap(MOWToolbarColor.PURPLE);
		addToColorMap(MOWToolbarColor.PINK);
		addToColorMap(MOWToolbarColor.RED);
		addToColorMap(MOWToolbarColor.ORANGE);
		addToColorMap(MOWToolbarColor.YELLOW);
		new FocusableWidget(AccessibilityGroup.NOTES_COLOR_PANEL, null,
				colorMap.values().toArray(new Widget[0])).attachTo(app);
	}

	private void addToColorMap(MOWToolbarColor color) {
		colorMap.put(color, createColorButton(color.getGColor(),
				color.getGgbTransKey()));
	}

	private void createMoreColorButton() {
		btnCustomColor = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(), null, 24);
		AriaHelper.setLabel(btnCustomColor, app.getLocalization().getMenu("ToolbarColor"
				+ ".MoreColors"));
		btnCustomColor.addStyleName("mowColorButton");
		btnCustomColor.addStyleName("mowColorPlusButton");
		btnCustomColor.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				openColorDialog();
			}
		});
		new FocusableWidget(
				AccessibilityGroup.NOTES_COLOR_CUSTOM, null, btnCustomColor).attachTo(app);
	}

	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");

		fillColorButtonMap();
		createMoreColorButton();

		panelRow = new FlowPanel();
		panelRow.setStyleName("panelRow");
		for (Label btn : colorMap.values()) {
			panelRow.add(btn);
		}
		panelRow.add(btnCustomColor);
		colorPanel.add(panelRow);
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("mowOptionsSlider");
		setSliderRange(true);
		slider.setWidth(300);
		preview = new PenPreview(app, 50, 30);
		preview.addStyleName("preview");
		slider.add(preview);
		sizePanel.add(slider);
		slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				sliderValueChanged(event.getValue());
			}
		});
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
		createPenPanel();
		createColorPanel();
		createSizePanel();
		contentPanel.add(
				LayoutUtilW.panelRow(penPanel, colorPanel, sizePanel));
	}

	private void doSelectPen() {
		pen.getElement().setAttribute("selected", "true");
		pen.setSelected(true);
		setColorsEnabled(true);
		selectColor(getSettings().getLastSelectedPenColor());
		setSliderRange(true);
		slider.setValue((double) getSettings().getLastPenThickness());
		getPenGeo().setLineThickness(getSettings().getLastPenThickness());
		getPenGeo().setLineOpacity(255);
		disableSlider(false);
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectHighlighter() {
		highlighter.getElement().setAttribute("selected", "true");
		highlighter.setSelected(true);
		setColorsEnabled(true);
		selectColor(getSettings().getLastSelectedHighlighterColor());
		setSliderRange(true);
		slider.setValue((double) getSettings().getLastHighlighterThinckness());
		getPenGeo().setLineThickness(getSettings().getLastHighlighterThinckness());
		getPenGeo()
				.setLineOpacity(EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
		disableSlider(false);
 		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		reset();
		eraser.getElement().setAttribute("selected", "true");
		eraser.setSelected(true);
		setColorsEnabled(false);
		setSliderRange(false);
		int delSize = app.getActiveEuclidianView().getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		disableSlider(false);
		preview.setVisible(false);
	}

	private void doSelectSelect() {
		reset();
		select.getElement().setAttribute("selected", "true");
		select.setSelected(true);
		disableSlider(true);
	}

	private void disableSlider(boolean disable) {
		slider.getElement().setAttribute("disabled", String.valueOf(disable));
		AriaHelper.setHidden(slider.getSlider(), disable);
		slider.disableSlider(disable);
	}

	/**
	 * Unselect all buttons and disable colors
	 */
	public void reset() {
		for (ToolButton btn : toolButtons) {
			btn.getElement().setAttribute("selected", "false");
			btn.setSelected(false);
		}
		setColorsEnabled(false);
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
		reset();
		if (mode == EuclidianConstants.MODE_SELECT
				|| mode == EuclidianConstants.MODE_SELECT_MOW) {
			doSelectSelect();
		} else if (mode == EuclidianConstants.MODE_ERASER) {
			doSelectEraser();
		} else if (mode == EuclidianConstants.MODE_PEN) {
			doSelectPen();
		} else if (mode == EuclidianConstants.MODE_HIGHLIGHTER) {
			doSelectHighlighter();
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
					// setPenIconColor(color.toString());
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
	public boolean isValidMode(int mode) {
		return mode == EuclidianConstants.MODE_SELECT_MOW
				|| mode == EuclidianConstants.MODE_PEN
				|| mode == EuclidianConstants.MODE_ERASER
				|| mode == EuclidianConstants.MODE_HIGHLIGHTER;
	}

	@Override
	public void setLabels() {
		pen.setLabel();
		select.setLabel();
		eraser.setLabel();
		highlighter.setLabel();
		for (Map.Entry<MOWToolbarColor, Label> colorBtnPair : colorMap.entrySet()) {
			AriaHelper.setLabel(colorBtnPair.getValue(),
					app.getLocalization().getColor(colorBtnPair.getKey().getGgbTransKey()));
		}
		AriaHelper.setLabel(btnCustomColor, app.getLocalization().getMenu("ToolbarColor"
				+ ".MoreColors"));
 	}
}

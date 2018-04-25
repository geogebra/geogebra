package org.geogebra.web.full.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
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
	private static final int MAX_ERASER_SIZE = 100;
	private static final int ERASER_STEP = 20;
	private StandardButton pen;
	private StandardButton eraser;
	private StandardButton highlighter;
	// TODO please remove me if MOW_HIGHLIGHTER_TOOL feature is released!!!
	private StandardButton move;
	private StandardButton select;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label[] btnColor;
	private GColor[] penColor;
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	/** whether colors are enabled */
	boolean colorsEnabled;
	// preset colors black, green, teal,blue, purple,magenta, red, carrot,
	// yellow
	private final static int[] HEX_COLORS = { 0x000000, 0x2E7D32, 0x00A8A8,
			0x1565C0, 0x6557D2, 0xCC0099, 0xD32F2F, 0xDB6114, 0xFFCC00 };
	private GColor lastSelectedPenColor = GColor.BLACK;
	private GColor lastSelectedHighlighterColor = GColor.MOW_GREEN;
	private int lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
	private int lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;

	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app/* , false */);
		addStyleName("penSubMenu");
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		penPanel.addStyleName("penPanel");
		pen = createButton(EuclidianConstants.MODE_PEN);
		// pen gets a separate icon here so it can show the selected color
		NoDragImage im = new NoDragImage(
				ImgResourceHelper.safeURI(
						ToolbarSvgResourcesSync.INSTANCE.mode_pen_black_32()),
				32);
		// im.addStyleName("opacityFixForOldIcons");
		pen.getUpFace().setImage(im);
		pen.addStyleName("plusMarginLeft");
		eraser = createButton(EuclidianConstants.MODE_ERASER);
		highlighter = createButton(EuclidianConstants.MODE_HIGHLIGHTER);
		highlighter.addStyleName("highlighterBtn");
		if (app.has(Feature.MOW_HIGHLIGHTER_TOOL)) {
			highlighter.addStyleName("plusMarginLeft");
			eraser.addStyleName("eraserBtn");
		} else {
			eraser.addStyleName("plusMarginLeft");
		}
		move = createButton(EuclidianConstants.MODE_MOVE);
		select = createButton(EuclidianConstants.MODE_SELECT);
		if (app.has(Feature.MOW_HIGHLIGHTER_TOOL)) {
			penPanel.add(
					LayoutUtilW.panelRow(select, pen, eraser, highlighter));
		} else {
			penPanel.add(LayoutUtilW.panelRow(move, pen,
				select, eraser));
		}
	}

	/**
	 * Create color buttons for selecting pen color
	 * 
	 * @param aColor
	 *            color
	 * @return button
	 */
	private Label createColorButton(GColor aColor, final int colorIndex) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				aColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("mowColorButton");
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!colorsEnabled) {
					return;
				}
				selectColor(colorIndex);
			}
		});
		return label;
	}

	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");
		btnColor = new Label[HEX_COLORS.length];
		penColor = new GColor[HEX_COLORS.length];
		for (int i = 0; i < HEX_COLORS.length; i++) {
			penColor[i] = GColor.newColorRGB(HEX_COLORS[i]);
			btnColor[i] = createColorButton(penColor[i], i);
		}
		btnCustomColor = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(), null, 24, app);
		btnCustomColor.addStyleName("mowColorButton");
		btnCustomColor.addStyleName("mowColorPlusButton");
		btnCustomColor.addFastClickHandler(this);
		colorPanel.add(LayoutUtilW.panelRow(btnColor[0], btnColor[1],
				btnColor[2], btnColor[3], btnColor[4], btnColor[5], btnColor[6],
				btnColor[7], btnColor[8], btnCustomColor));
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("mowOptionsSlider");
		slider.setMinimum(EuclidianConstants.MIN_PEN_HIGHLIGHTER_SIZE, false);
		slider.setMaximum(EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE, false);
		slider.setStep(EuclidianConstants.DEFAULT_PEN_STEP);
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
				lastPenThickness = (int) value;
			} else if (app.getActiveEuclidianView()
					.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
				lastHighlighterThinckness = (int) value;
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

	@Override
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		if (app.isModeValid(menu.get(0).intValue())) {
			panel.add(createButton(menu.get(0).intValue()));
		}
	}

	@Override
	public void onClick(Widget source) {
		if (source == pen) {
			app.setMode(EuclidianConstants.MODE_PEN);
		} else if (source == eraser) {
			app.setMode(EuclidianConstants.MODE_ERASER);
		} else if (source == move) {
			app.setMode(EuclidianConstants.MODE_MOVE);
		} else if (source == select) {
			app.setMode(EuclidianConstants.MODE_SELECT);
		} else if (source == btnCustomColor) {
			openColorDialog();
		} else if (source == highlighter) {
			app.setMode(EuclidianConstants.MODE_HIGHLIGHTER);
		}
		closeFloatingMenus();
	}

	private void doSelectPen() {
		pen.getElement().setAttribute("selected", "true");
		setColorsEnabled(true);
		selectColor(lastSelectedPenColor);
		slider.setValue((double) lastPenThickness);
		getPenGeo().setLineThickness(lastPenThickness);
		getPenGeo().setLineOpacity(255);
		slider.getElement().setAttribute("disabled", "false");
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectHighlighter() {
		highlighter.getElement().setAttribute("selected", "true");
		setColorsEnabled(true);
		selectColor(lastSelectedHighlighterColor);
		slider.setValue((double) lastHighlighterThinckness);
		getPenGeo().setLineThickness(lastHighlighterThinckness);
		getPenGeo()
				.setLineOpacity(EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
		slider.getElement().setAttribute("disabled", "false");
 		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		reset();
		eraser.getElement().setAttribute("selected", "true");
		setColorsEnabled(false);
		slider.setMinimum(1, false);
		slider.setMaximum(MAX_ERASER_SIZE, false);
		slider.setStep(ERASER_STEP);
		int delSize = app.getActiveEuclidianView().getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		slider.getElement().setAttribute("disabled", "false");
		preview.setVisible(false);
	}

	private void doSelectMove() {
		reset();
		move.getElement().setAttribute("selected", "true");
		slider.getElement().setAttribute("disabled", "true");
	}

	private void doSelectSelect() {
		reset();
		select.getElement().setAttribute("selected", "true");
		slider.getElement().setAttribute("disabled", "true");
	}

	/**
	 * Unselect all buttons and disable colors
	 */
	public void reset() {
		pen.getElement().setAttribute("selected", "false");
		eraser.getElement().setAttribute("selected", "false");
		move.getElement().setAttribute("selected", "false");
		select.getElement().setAttribute("selected", "false");
		highlighter.getElement().setAttribute("selected", "false");
		setColorsEnabled(false);
	}

	/**
	 * @param colorStr
	 *            color string
	 */
	/*
	 * public void setPenIconColor(String colorStr) { // set background of pen
	 * icon to selected color
	 * pen.getElement().getFirstChildElement().getNextSiblingElement()
	 * .setAttribute("style", "background-color: " + colorStr); }
	 */

	/**
	 * @param idx
	 *            index
	 */
	public void selectColor(int idx) {
		for (int i = 0; i < btnColor.length; i++) {
			if (idx == i) {
				getPenGeo().setObjColor(penColor[i]);
				if (colorsEnabled) {
					btnColor[i].addStyleName("mowColorButton-selected");
					// setPenIconColor(penColor[i].toString());
					if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
						getPenGeo().setLineOpacity(
								EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
						lastSelectedHighlighterColor = penColor[i];
					} else {
						lastSelectedPenColor = penColor[i];
					}
				}
			} else {
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		updatePreview();
	}

	// remember and set a color that was picked from color chooser
	private void selectColor(GColor color) {
		getPenGeo().setObjColor(color);
		/*
		 * if (colorsEnabled) { setPenIconColor(color.toString()); }
		 */
		updatePreview();
	}

	private void setColorsEnabled(boolean enable) {
		for (int i = 0; i < btnColor.length; i++) {
			if (enable) {
				btnColor[i].removeStyleName("disabled");
				if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
					if (penColor[i] == lastSelectedHighlighterColor) {
						btnColor[i].addStyleName("mowColorButton-selected");
					}
				} else if (app.getMode() == EuclidianConstants.MODE_PEN) {
					if (penColor[i] == lastSelectedPenColor) {
						btnColor[i].addStyleName("mowColorButton-selected");
					}
				}
			} else {
				btnColor[i].addStyleName("disabled");
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		if (enable) {
			btnCustomColor.removeStyleName("disabled");
		} else {
			btnCustomColor.addStyleName("disabled");
		}
		colorsEnabled = enable;
	}

	private GeoElement getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen().defaultPenLine;
	}

	@Override
	public void setMode(int mode) {
		reset();
		if (mode == EuclidianConstants.MODE_MOVE) {
			doSelectMove();
		} else if (mode == EuclidianConstants.MODE_SELECT) {
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
	 * @return last selected pen color
	 */
	public GColor getLastSelectedPenColor() {
		return lastSelectedPenColor;
	}

	/**
	 * @param lastSelectedPenColor
	 *            update last selected pen color
	 */
	public void setLastSelectedPenColor(GColor lastSelectedPenColor) {
		this.lastSelectedPenColor = lastSelectedPenColor;
	}

	/**
	 * @return last selected highlighter color
	 */
	public GColor getLastSelectedHighlighterColor() {
		return lastSelectedHighlighterColor;
	}

	/**
	 * @param lastSelectedHighlighterColor
	 *            update last selected highlighter color
	 */
	public void setLastSelectedColor(GColor lastSelectedHighlighterColor) {
		this.lastSelectedHighlighterColor = lastSelectedHighlighterColor;
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

	private void openColorDialog() {
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
					setLastSelectedColor(color);
					penGeo.setLineOpacity(
							app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER
									? EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY
									: 255);
					selectColor(-1);
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
		lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
		lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;
		if (app.getMode() == EuclidianConstants.MODE_PEN) {
			slider.setValue((double) lastPenThickness);
		}
	}
}

package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PenPreview;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
	private static final int MAX_PEN_SIZE = 12;
	private static final int MAX_ERASER_SIZE = 100;
	private static final int PEN_STEP = 1;
	private static final int ERASER_STEP = 20;
	private static final int BLACK = 0;
	private StandardButton pen;
	private StandardButton eraser;
	private StandardButton freehand;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label btnColor[];
	private GColor penColor[];
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	private boolean colorsEnabled;
	// preset colors: black, purple, teal, orange
	private final static String hexColors[] = { "000000", "673AB7", "009688",
			"E67E22" };
	private GColor lastSelectedColor = null;

	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app, false);
		addStyleName("penSubMenu");
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		penPanel.addStyleName("penPanel");
		pen = createButton(EuclidianConstants.MODE_PEN);
		// pen gets a separate icon here so it can show the selected color
		ToolbarResources pr = ((ImageFactory) GWT.create(ImageFactory.class)).getToolbarResources();
		NoDragImage im = new NoDragImage(ImgResourceHelper.safeURI(pr.mode_pen_white_32()), 32);
		im.addStyleName("opacityFixForOldIcons");
		pen.getUpFace().setImage(im);

		eraser = createButton(EuclidianConstants.MODE_ERASER);
		freehand = createButton(EuclidianConstants.MODE_FREEHAND_SHAPE);
		penPanel.add(LayoutUtilW.panelRow(pen, eraser, freehand));
	}

	/**
	 * Create color buttons for selecting pen color
	 * 
	 * @param aColor
	 * @return
	 */
	private Label createColorButton(GColor aColor) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				aColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("MyCanvasButton");
		label.addStyleName("color-button");
		label.addClickHandler(this);
		return label;
	}
	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");
		btnColor = new Label[hexColors.length];
		penColor = new GColor[hexColors.length];
		for (int i = 0; i < hexColors.length; i++) {
			penColor[i] = GColor
					.newColorRGB(Integer.parseInt(hexColors[i], 16));
			btnColor[i] = createColorButton(penColor[i]);
		}

		btnCustomColor = new StandardButton("+");
		btnCustomColor.addStyleName("MyCanvasButton color-button");
		btnCustomColor.addStyleName("plusButton");
		btnCustomColor.addFastClickHandler(this);
		colorPanel.add(LayoutUtilW.panelRow(btnColor[0], btnColor[1],
				btnColor[2], btnColor[3], btnCustomColor));
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("optionsSlider");

		preview = new PenPreview(app, 50, 30);
		preview.addStyleName("preview");
		slider.add(preview);
		sizePanel.add(slider);
		slider.addValueChangeHandler(new ValueChangeHandler<Double>() {

			public void onValueChange(ValueChangeEvent<Double> event) {
				sliderValueChanged(event.getValue());
			}
		});

	}

	/**
	 * Sets the size of pen/eraser from the slider value.
	 */
	void sliderValueChanged(double value) {
		if (colorsEnabled) {
			getPenGeo().setLineThickness((int) value);
			updatePreview();
		} else {
			app.getActiveEuclidianView().getSettings().setDeleteToolSize((int) value);
		}
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
	public void onClick(Widget source) {
		if (source == pen) {
			app.setMode(EuclidianConstants.MODE_PEN);
		} else if (source == eraser) {
			app.setMode(EuclidianConstants.MODE_ERASER);
		} else if (source == freehand) {
			app.setMode(EuclidianConstants.MODE_FREEHAND_SHAPE);
		} else if (source == btnCustomColor) {
			openColorDialog();
		}

	}

	@Override
	public void onClick(ClickEvent event) {
		if (!colorsEnabled) {
			return;
		}

		Object source = event.getSource();
		for (int i = 0; i < btnColor.length; i++) {
			if (source == btnColor[i]) {
				selectColor(i);
			}
		}

	}

	private void doSelectPen() {
		// reset();
		pen.getElement().setAttribute("selected", "true");
		setColorsEnabled(true);
		// colorPanel.setVisible(true);
		if (lastSelectedColor == null) {
			selectColor(BLACK);
		} else {
			selectColor(lastSelectedColor);
		}
		slider.setMinimum(1, false);
		slider.setMaximum(MAX_PEN_SIZE, false);
		slider.setStep(PEN_STEP);
		slider.setValue((double) getPenGeo().getLineThickness());
		slider.setVisible(true);
		slider.addStyleName("optionsSlider-pen");
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		reset();
		eraser.getElement().setAttribute("selected", "true");
		setColorsEnabled(false);
		// colorPanel.setVisible(false);
		slider.setMinimum(1, false);
		slider.setMaximum(MAX_ERASER_SIZE, false);
		slider.setStep(ERASER_STEP);
		int delSize = app.getActiveEuclidianView().getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		slider.setVisible(true);
		slider.removeStyleName("optionsSlider-pen");
		preview.setVisible(false);
	}

	private void doSelectFreehand() {
		reset();
		// colorPanel.setVisible(false);
		freehand.getElement().setAttribute("selected", "true");
		selectColor(BLACK);
		slider.setVisible(false);
	}


	@Override
	public void reset() {
		pen.getElement().setAttribute("selected", "false");
		eraser.getElement().setAttribute("selected", "false");
		freehand.getElement().setAttribute("selected", "false");
		setColorsEnabled(false);

	}

	private void setPenIconColor(String colorStr) {
		// set background of pen icon to selected color
		pen.getElement().getFirstChildElement().getNextSiblingElement()
				.setAttribute("style", "background-color: " + colorStr);

	}
	private void selectColor(int idx) {
		for (int i = 0; i < btnColor.length; i++) {
			if (idx == i) {
				getPenGeo().setObjColor(penColor[i]);

				if (colorsEnabled) {
					lastSelectedColor = penColor[i];
					btnColor[i].addStyleName("penSubMenu-selected");
					setPenIconColor(penColor[i].toString());
				}
			} else {
				btnColor[i].removeStyleName("penSubMenu-selected");
			}
		}
		updatePreview();

	}

	// remember and set a color that was picked from color chooser
	private void selectColor(GColor color) {
		getPenGeo().setObjColor(color);
		if (colorsEnabled) {
			setPenIconColor(color.toString());
		}
		updatePreview();
	}

	private void setColorsEnabled(boolean enable) {
		for (int i = 0; i < btnColor.length; i++) {
			btnColor[i].removeStyleName("penSubMenu-selected");
			if (enable) {
				btnColor[i].removeStyleName("disabled");
			} else {
				btnColor[i].addStyleName("disabled");

			}
		}
		colorsEnabled = enable;

	}
	private GeoElement getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen().DEFAULT_PEN_LINE;
	}

	@Override
	public void setMode(int mode) {
		reset();
		if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
			doSelectFreehand();
		} else if (mode == EuclidianConstants.MODE_ERASER) {
			doSelectEraser();
		} else if (mode == EuclidianConstants.MODE_PEN) {
			doSelectPen();
		}
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_PEN;
	}


	private void updatePreview() {
		preview.update();
	}

	private void openColorDialog() {
		final GeoElement penGeo = getPenGeo();
		DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
		GColor originalColor = penGeo.getObjectColor();
		dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {

			public void onForegroundSelected() {
				// TODO Auto-generated method stub

			}

			public void onColorChange(GColor color) {
				penGeo.setObjColor(color);
				setPenIconColor(color.toString());
				lastSelectedColor = color;
				updatePreview();
			}

			public void onClearBackground() {
				// TODO Auto-generated method stub

			}

			public void onBarSelected() {
				// TODO Auto-generated method stub

			}

			public void onBackgroundSelected() {
				// TODO Auto-generated method stub

			}

			public void onAlphaChange() {
				// TODO Auto-generated method stub

			}
		});
	}

}

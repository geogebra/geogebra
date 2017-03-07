package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PenSubMenu extends SubMenuPanel
		implements ClickHandler, FastClickHandler {
	private StandardButton pen;
	private StandardButton eraser;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label btnColor[];
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private Kernel kernel;
	private final static String hexColors[] = { "000000", "673AB7", "009688",
			"E67E22" };

	public PenSubMenu(AppW app) {
		super(app, false);
		this.kernel = app.getKernel();
		addStyleName("penSubMenu");
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		Log.debug("app is null: " + (app == null));
		pen = MOWToolbar.createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_PEN, app), this);
		eraser = MOWToolbar.createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_ERASER, app),
				this);
		penPanel.add(LayoutUtilW.panelRow(pen, eraser));
	}

	private Label createColorButton(GColor btnColor) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				btnColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("MyCanvasButton");
		label.addClickHandler(this);
		return label;
	}
	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");
		btnColor = new Label[hexColors.length];
		for (int i = 0; i < hexColors.length; i++) {
			btnColor[i] = createColorButton(
					GColor.newColorRGB(Integer.parseInt(hexColors[i], 16)));
		}

		btnCustomColor = new StandardButton("+");
		colorPanel.add(LayoutUtilW.panelRow(btnColor[0], btnColor[1],
				btnColor[2], btnColor[3], btnCustomColor));
	}

	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 12, app.getKernel(), false);
		sizePanel.add(slider);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		createPenPanel();
		createColorPanel();
		createSizePanel();

		contentPanel.add(LayoutUtilW.panelRow(penPanel, colorPanel, sizePanel));
	}

	public void onClick(Widget source) {
		if (source == pen) {
			selectPen();
		} else if (source == eraser) {
			selectEraser();
		}
	}

	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		for (int i = 0; i < btnColor.length; i++) {
			if (source == btnColor[i]) {
				selectColor(i);
			}
		}

	}

	private void selectPen() {
		app.setMode(EuclidianConstants.MODE_PEN);
		pen.addStyleName("penSubMenu-selected");
		eraser.removeStyleName("penSubMenu-selected");

	}

	private void selectEraser() {
		app.setMode(EuclidianConstants.MODE_ERASER);
		pen.removeStyleName("penSubMenu-selected");
		eraser.addStyleName("penSubMenu-selected");

	}

	@Override
	public void onOpen() {
		selectPen();
		selectColor(0);
	}

	private void selectColor(int idx) {
		for (int i = 0; i < btnColor.length; i++) {
			if (idx == i) {
				btnColor[i].addStyleName("penSubMenu-selected");
			} else {
				btnColor[i].removeStyleName("penSubMenu-selected");
			}
		}

	}
}

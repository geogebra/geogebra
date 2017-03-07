package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PenSubMenu extends SubMenuPanel implements FastClickHandler {
	private StandardButton pen;
	private StandardButton eraser;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label btnColor[];
	private AppW app;
	private final static String hexColors[] = { "000000", "673AB7", "009688",
			"E67E22" };

	public PenSubMenu(AppW app) {
		super(app, false);
		this.app = app;
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		pen = MOWToolbar.createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_PEN, app), this);
		eraser = MOWToolbar.createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_ERASER, app),
				this);
		penPanel.add(LayoutUtilW.panelRow(pen, eraser));
	}

	private Label createColorButton(GColor btnColor) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, btnColor,
				btnColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("MyCanvasButton");
		return label;
	}
	private void createColorPanel() {
		colorPanel = new FlowPanel();
		btnColor = new Label[hexColors.length];
		for (int i = 0; i < hexColors.length; i++) {
			btnColor[i] = createColorButton(
					GColor.newColorRGB(Integer.parseInt(hexColors[i], 16)));
		}

		colorPanel.add(
				LayoutUtilW.panelRow(btnColor[0], btnColor[1], btnColor[2],
						btnColor[3]));
	}

	private void createSizePanel() {
		sizePanel = new FlowPanel();
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
		// TODO Auto-generated method stub

	}

}

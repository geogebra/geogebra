package org.geogebra.web.full.gui.color;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BorderColorPopup extends BgColorPopup {
	private LineThicknessCheckMarkItem noBorder;
	private LineThicknessCheckMarkItem thin;
	private LineThicknessCheckMarkItem thick;

	/**
	 * @param app {@link AppW}
	 * @param colorSetType {@code int}
	 * @param hasSlider {@code boolean}
	 * @param selection selected geos
	 */
	public BorderColorPopup(AppW app, int colorSetType,
			boolean hasSlider, EuclidianStyleBarSelection selection) {
		super(app, colorSetType, hasSlider, selection);
		getMyPopup().addStyleName("borderColPopup");
		addClickHandler(noBorder);
		addClickHandler(thin);
		addClickHandler(thick);
	}

	@Override
	public void addFirstPanel(VerticalPanel panel) {
		FlowPanel borderThicknessPanel = new FlowPanel();
		borderThicknessPanel.addStyleName("thicknessPanel");

		noBorder = new LineThicknessCheckMarkItem(app.getLocalization()
				.getMenu("stylebar.NoBorder"), "textItem");
		borderThicknessPanel.add(noBorder);
		noBorder.setSelected(true);

		thin = new LineThicknessCheckMarkItem("thin");
		borderThicknessPanel.add(thin);
		thin.setSelected(false);

		thick = new LineThicknessCheckMarkItem("thick");
		borderThicknessPanel.add(thick);
		thick.setSelected(false);

		panel.add(borderThicknessPanel);
	}

	private void addClickHandler(LineThicknessCheckMarkItem selectedItem) {
		ClickStartHandler.init(selectedItem,
				new ClickStartHandler(true, true) {

					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						deselectAll();
						selectedItem.setSelected(true);
						List<GeoElement> geos = getSelection().getGeos();
						int thickness = getThickness(selectedItem);
						boolean needUndo = applyBorderThickness(geos, thickness);
						if (needUndo) {
							app.storeUndoInfo();
						}
					}
				});
	}

	private int getThickness(LineThicknessCheckMarkItem selected) {
		if (selected == thin) {
			return 1;
		} else if (selected == thick) {
			return 3;
		} else {
			return 0;
		}
	}

	private boolean applyBorderThickness(List<GeoElement> geos, int borderThickness) {
		boolean needUndo = false;

		for (GeoElement geo : geos) {
			if (geo instanceof GeoInlineText) {
				if (((GeoInlineText) geo).getBorderThickness() != borderThickness) {
					((GeoInlineText) geo).setBorderThickness(borderThickness);

					geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
					needUndo = true;
				}
			}
		}
		return needUndo;
	}

	private void deselectAll() {
		noBorder.setSelected(false);
		thin.setSelected(false);
		thick.setSelected(false);
	}

	@Override
	public void setLabels() {
		noBorder.setLabel(app.getLocalization().getMenu("stylebar.NoBorder"));
	}
}

package org.geogebra.web.full.gui.color;

import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
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

	@Override
	public void setLabels() {
		noBorder.setLabel(app.getLocalization().getMenu("stylebar.NoBorder"));
	}
}

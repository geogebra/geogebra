package org.geogebra.web.html5.gui.tooltip;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 *
 */
public class PreviewPointPopup extends GPopupPanel {

	private FlowPanel content;

	/**
	 * @param appW
	 *            application
	 * @param previewPoints
	 *            list of preview points
	 */
	public PreviewPointPopup(AppW appW, ArrayList<GeoElement> previewPoints) {
		super(appW.getPanel(), appW);
		this.app = appW;
		content = new FlowPanel();
		content.addStyleName("previewPointsPopup");
		createContent(previewPoints);
		add(content);
		setAutoHideEnabled(true);
		positionPopup();
	}

	private void positionPopup() {
		StylebarPositioner positioner = new StylebarPositioner(app);
		GPoint pos = positioner.getPositionOnCanvas(20, 100,
				app.getActiveEuclidianView().getViewHeight());
		this.setPopupPosition(pos.getX() + 360, pos.getY());
	}

	private void createContent(ArrayList<GeoElement> previewPoints) {

		for (GeoElement geo : previewPoints) {
			if (geo.getParentAlgorithm() != null) {
				Label lbl = new Label(app.getLocalization().getMenu(
						geo.getParentAlgorithm().getClassName().getCommand()));
				addToContent(lbl);
			}
		}
		GeoElement point = previewPoints.get(0);
		Label coord = new Label(
				point.getAlgebraDescriptionRHS());
		addToContent(coord);
	}

	private void addToContent(Label lbl) {
		if (!"".equals(lbl.getText())) {
			content.add(lbl);
		}
	}
}

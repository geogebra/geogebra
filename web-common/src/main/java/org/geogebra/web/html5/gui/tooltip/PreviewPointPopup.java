package org.geogebra.web.html5.gui.tooltip;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.popup.specialpoint.SpecialPointPopupHelper;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * @author csilla
 *
 */
public class PreviewPointPopup extends GPopupPanel {

	private final FlowPanel content;

	/**
	 * @param appW
	 *            application
	 * @param previewPoints
	 *            list of preview points
	 */
	public PreviewPointPopup(AppW appW, ArrayList<GeoElement> previewPoints) {
		super(appW.getAppletFrame(), appW);
		this.app = appW;
		content = new FlowPanel();
		this.addStyleName("previewPointsPopup");
		createContent(previewPoints);
		add(content);
		setAutoHideEnabled(true);
	}

	/**
	 * position popup
	 * 
	 * @param offsetWidth
	 *            width of popup
	 * @param offsetHeight
	 *            height of popup
	 */
	public void positionPopup(int offsetWidth, int offsetHeight,
			ArrayList<GeoElement> geos) {
		StylebarPositioner positioner = new StylebarPositioner(app);
		positioner.setCenter(true);
		GPoint pos = positioner.getPositionFor(geos, offsetHeight, 33,
				app.getActiveEuclidianView().getViewHeight() - offsetHeight,
				offsetWidth / 2,
				app.getActiveEuclidianView().getViewWidth() - offsetWidth / 2);
		if (pos != null) {
			this.setPopupPosition(
					pos.getX() + app.getActiveEuclidianView().getAbsoluteLeft()
							- (int) ((AppW) app).getAbsLeft()
							- offsetWidth / 2,
					pos.getY());
		} else {
			hide(true);
		}
	}

	private void createContent(ArrayList<GeoElement> previewPoints) {
		List<String> contentRows = SpecialPointPopupHelper.getContentRows(app, previewPoints);
		for (String row : contentRows) {
			Label lbl = new Label(row);
			addToContent(lbl);
		}
	}

	private void addToContent(Label lbl) {
		if (!"".equals(lbl.getText())) {
			content.add(lbl);
		}
	}
}

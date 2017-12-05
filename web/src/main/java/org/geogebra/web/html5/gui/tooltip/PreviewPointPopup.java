package org.geogebra.web.html5.gui.tooltip;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.DockPanelW;

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
		this.addStyleName("previewPointsPopup");
		createContent(previewPoints);
		add(content);
		setAutoHideEnabled(true);
		setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				positionPopup(offsetWidth);
			}
		});
	}

	/**
	 * position popup
	 * 
	 * @param offsetWidth
	 *            width of popup
	 */
	public void positionPopup(int offsetWidth) {
		StylebarPositioner positioner = new StylebarPositioner(app);
		GPoint pos = positioner.getPositionOnCanvas(60, 100,
				app.getActiveEuclidianView().getViewHeight());
		DockPanelW dp = (DockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);
		if (dp != null && pos != null) {
			this.setPopupPosition(
					pos.getX() + dp.getOffsetWidth() - offsetWidth / 2,
					pos.getY() + 10);
		} else {
			hide(true);
		}
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

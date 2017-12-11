package org.geogebra.web.html5.gui.tooltip;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
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
	public void positionPopup(int offsetWidth, int offsetHeight) {
		StylebarPositioner positioner = new StylebarPositioner(app);
		GPoint pos = positioner.getPositionOnCanvas(60, 100,
				app.getActiveEuclidianView().getViewHeight() - offsetHeight,
				offsetWidth / 2, app.getAppCanvasWidth() - offsetWidth / 2);
		if (pos != null) {
			this.setPopupPosition(
					pos.getX() + app.getActiveEuclidianView().getAbsoluteLeft()
							- (int) ((AppW) app).getAbsLeft()
							- offsetWidth / 2,
					pos.getY() + 10);
		} else {
			hide(true);
		}
	}

	private void createContent(ArrayList<GeoElement> previewPoints) {
		for (GeoElement geo : previewPoints) {
			if (geo.getParentAlgorithm() != null) {
				GetCommand cmd = geo.getParentAlgorithm().getClassName();
				String text;
				if (cmd == Commands.Intersect) {
					text = app.getLocalization().getMenu("yIntercept");
				} else if (cmd == Commands.Roots) {
					text = app.getLocalization().getCommand("Root");
				} else {
					text = app.getLocalization().getCommand(cmd.getCommand());
				}
				Label lbl = new Label(text);
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

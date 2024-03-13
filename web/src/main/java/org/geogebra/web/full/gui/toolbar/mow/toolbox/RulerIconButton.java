package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.euclidian.measurement.MeasurementToolId;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerIconButton extends IconButton {
	private final static int TOOLBOX_PADDING = 8;
	private final EuclidianController ec;
	private final MeasurementController mc;
	private RulerPopup rulerPopup;
	private final AppW appW;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - ui test id
	 */
	public RulerIconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest) {
		super(appW.getLocalization(), icon, ariaLabel, dataTitle, dataTest, null);
		this.appW = appW;
		ec = appW.getActiveEuclidianView().getEuclidianController();
		addFastClickHandler((event) -> {
			setActive(!isActive(), appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));
			showRulerTypePopup();
			handleRuler();
		});
		mc = ec.getMeasurementController();
	}

	private void showRulerTypePopup() {
		if (rulerPopup == null) {
			rulerPopup = new RulerPopup(appW, this);
		}

		rulerPopup.showPopup(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
				(int) (getAbsoluteTop() - appW.getAbsTop()));
	}

	/**
	 * set active ruler, or remove it in switch ruler off
	 */
	public void handleRuler() {
		if (isActive()) {
			appW.setMode(rulerPopup.getActiveRulerType());
		} else {
			removeRuler();
			appW.setMode(MODE_MOVE);
		}
	}
	
	private GeoImage getActiveRuler() {
		int rulerType = rulerPopup.getActiveRulerType();
		MeasurementToolId toolId = MeasurementToolId.byOrder(rulerType);
		if (toolId != MeasurementToolId.NONE) {
			return mc.getToolImage(toolId);
		}
		return null;
	}
	
	private void clearRuler() {
		mc.clear();
	}

	/**
	 * remove ruler from construction
	 */
	public void removeRuler() {
		GeoImage ruler = getActiveRuler();
		if (ruler != null) {
			ruler.remove();
		}
		clearRuler();
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (rulerPopup != null) {
			rulerPopup.setLabels();
		}
	}
}

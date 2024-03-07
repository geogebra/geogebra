package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerIconButton extends IconButton {
	private final static int TOOLBOX_PADDING = 8;
	private RulerPopup rulerPopup;
	private AppW appW;

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
		addFastClickHandler((event) -> {
			setActive(!isActive(), appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));
			showRulerTypePopup();
			handleRuler();
		});
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
		GeoImage ruler;
		switch (rulerPopup.getActiveRulerType()) {
		default:
		case MODE_RULER:
			ruler = appW.getKernel().getConstruction().getRuler();
			break;
		case MODE_PROTRACTOR:
			ruler = appW.getKernel().getConstruction().getProtractor();
			break;
		}
		
		return ruler;
	}
	
	private void clearRuler() {
		switch (rulerPopup.getActiveRulerType()) {
		default:
		case MODE_RULER:
			appW.getKernel().getConstruction().setRuler(null);
			break;
		case MODE_PROTRACTOR:
			appW.getKernel().getConstruction().setProtractor(null);
			break;
		}
	}

	/**
	 * remove ruler from construction
	 */
	public void removeRuler() {
		GeoImage ruler = getActiveRuler();
		if (ruler != null) {
			getActiveRuler().remove();
		}
		clearRuler();
	}

	@Override
	public void updateLabelAndDataTitle(AppW appW, String ariaLabel, String dataTitle) {
		super.updateLabelAndDataTitle(appW, ariaLabel, dataTitle);
		if (rulerPopup != null) {
			rulerPopup.updateGui();
		}
	}
}

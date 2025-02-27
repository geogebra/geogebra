package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.html5.main.AppW;

/**
 * Line style popup
 */
public class EuclidianLineStylePopup extends LineStylePopup {
	private final LineStyleModel model;

	/**
	 * @param app
	 *            application
	 */
	public EuclidianLineStylePopup(AppW app) {
		super(app, LineStylePopup.getLineStyleIcons(), -1, 5,
				SelectionTable.MODE_ICON, true, true);
		model = new LineStyleModel(app);
		this.setKeepVisible(false);
	}

	@Override
	public void update(List<GeoElement> geos) {
		if (geos.isEmpty()) {
			this.setVisible(false);
			return;
		}

		model.setGeos(geos.toArray());
		boolean geosOK = model.checkGeos();
		this.setVisible(geosOK);

		if (geosOK) {
			GeoElement geo0 = model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getLineThickness());
				getSlider().setMinimum(model.maxMinimumThickness());
			}
			selectLineType(geo0.getLineType());
		}
	}
}

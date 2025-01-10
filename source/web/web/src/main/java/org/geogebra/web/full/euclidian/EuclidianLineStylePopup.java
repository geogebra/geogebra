package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.html5.main.AppW;

/**
 * Line style popup
 */
public class EuclidianLineStylePopup extends LineStylePopup implements SetLabels {
	private LineStyleModel model;

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
		if (geos.size() == 0 || app.getMode() == EuclidianConstants.MODE_FREEHAND_SHAPE) {
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
			// showTableItem(5, geo0 instanceof GeoFunction);
			selectLineType(geo0.getLineType());
		}
	}

	@Override
	public void setLabels() {
		// Overridden in MOWLineStyleButton
	}

}

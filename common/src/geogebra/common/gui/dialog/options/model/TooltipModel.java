package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

public class TooltipModel extends OptionsModel {
	private IComboListener listener;
	public TooltipModel(IComboListener listener) {
		this.listener = listener;
	}

	@Override
	public void updateProperties() {
		GeoElement geo0 = getGeoAt(0);
		boolean equalLabelMode = true;

		for (int i = 1; i < getGeosLength(); i++) {
			if (geo0.getLabelMode() != getGeoAt(i).getTooltipMode())
				equalLabelMode = false;

		}

		// set label visible checkbox
		if (equalLabelMode) {
			listener.setSelectedIndex(geo0.getTooltipMode());
		}
		else {
			listener.setSelectedIndex(-1);
		}


	}

	public void fillModes(App app) {
		listener.addItem(app.getMenu("Labeling.automatic")); // index 0
		listener.addItem(app.getMenu("on")); // index 1
		listener.addItem(app.getMenu("off")); // index 2
		listener.addItem(app.getPlain("Caption")); // index 3
		listener.addItem(app.getPlain("NextCell")); // index 4 Michael
															// Borcherds
	}
	
	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isDrawable()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public void applyChanges(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			getGeoAt(i).setTooltipMode(value);
		}
	
	}

}

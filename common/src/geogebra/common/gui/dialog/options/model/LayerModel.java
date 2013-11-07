package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.EuclidianStyleConstants;

public class LayerModel extends OptionsModel {
	private IComboListener listener;
	
	public LayerModel(IComboListener listener) {
		this.listener = listener;
	}

	public void addLayers() {
		for (int layer = 0; layer <= EuclidianStyleConstants.MAX_LAYERS; ++layer) {
			listener.addItem(" " + layer);
		}

	}
	@Override
	public void updateProperties() {
		GeoElement geo0 = getGeoAt(0);
		boolean equalLayer = true;

		for (int i = 1; i < getGeosLength(); i++) {
		// same label visible value
			if (geo0.getLayer() != getGeoAt(i).getLayer()) {
				equalLayer = false;
			}
		}

		if (equalLayer) {
			listener.setSelectedIndex(geo0.getLayer());
		}
		else {
			listener.setSelectedIndex(-1);
		}
	}

	public void applyChanges(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i); 
			geo.setLayer(value);
			geo.updateRepaint();
		}
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

}

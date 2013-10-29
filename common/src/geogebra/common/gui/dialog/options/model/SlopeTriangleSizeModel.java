package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;

public class SlopeTriangleSizeModel extends OptionsModel {
	private ISliderListener listener;
	public SlopeTriangleSizeModel(ISliderListener listener) {
		this.listener = listener;
	}

	public void applyChanges(int size) {
		GeoNumeric num;
		for (int i = 0; i < getGeosLength(); i++) {
			num = (GeoNumeric) getGeoAt(i);
			num.setSlopeTriangleSize(size);
			num.updateRepaint();
		}
	};
	
	@Override
	public void updateProperties() {
		GeoNumeric geo0 = (GeoNumeric) getGeoAt(0);
		listener.setValue(geo0.getSlopeTriangleSize());

	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo =  getGeoAt(i);
			if (!(geo instanceof GeoNumeric && geo.getParentAlgorithm() instanceof AlgoSlope)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;

	}
}

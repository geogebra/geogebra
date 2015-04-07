package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class SlopeTriangleSizeModel extends SliderOptionsModel {
	public SlopeTriangleSizeModel(ISliderListener listener) {
		super(listener);
	}

	private GeoNumeric getNumericAt(int index) {
		return (GeoNumeric) getObjectAt(index);
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo =  getGeoAt(index);
		return (geo instanceof GeoNumeric && 
				geo.getParentAlgorithm() instanceof AlgoSlope);
	}

	@Override
	protected void apply(int index, int value) {
		GeoNumeric num = getNumericAt(index);
		num.setSlopeTriangleSize(value);
		num.updateRepaint();		
	}

	@Override
	protected int getValueAt(int index) {
		return getNumericAt(index).getSlopeTriangleSize();
	}
}

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;

public class DecoAngleModel extends NumberOptionsModel {
	public interface IDecoAngleListener extends IComboListener {

		void setArcSizeMinValue();
		
	}
	private IDecoAngleListener listener;
	
	public DecoAngleModel(IDecoAngleListener listener) {
		this.listener = listener;
	}
	
	private AngleProperties getAnglePropertiesAt(int index) {
		return (AngleProperties)getObjectAt(index);
	}

	@Override
	public void updateProperties() {
	
		AngleProperties geo0 = getAnglePropertiesAt(0);
		listener.setSelectedIndex(geo0.getDecorationType());
		
	}

	
	@Override
	public boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof AngleProperties);
	}

	@Override
	protected void apply(int index, int value) {
		AngleProperties geo = getAnglePropertiesAt(index);
		geo.setDecorationType(value);
		// addded by Loic BEGIN
		// check if decoration could be drawn
		if (geo.getArcSize() < 20
				&& (geo.getDecorationType() == GeoElement.DECORATION_ANGLE_THREE_ARCS || geo
						.getDecorationType() == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
			geo.setArcSize(20);
			listener.setArcSizeMinValue();
		}
		// END
		geo.updateRepaint();
		}

	@Override
	protected int getValueAt(int index) {
		return getAnglePropertiesAt(index).getDecorationType();
	}
	
	public static int getDecoTypeLength() {
		return GeoAngle.getDecoTypes().length;
	}

	@Override
	public boolean updateMPanel(Object[] geos2) {
		return listener.updatePanel(geos2) != null;
	}

}

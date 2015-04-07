package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoTextField;
import org.geogebra.common.main.App;

public class TextFieldSizeModel extends OptionsModel {
	private ITextFieldListener listener;
	private Kernel kernel;
	public TextFieldSizeModel(App app, ITextFieldListener listener) {
		this.listener = listener;
		kernel = app.getKernel();
	}

	private GeoTextField getTextFieldAt(int index) {
		return (GeoTextField)getObjectAt(index);
	}
	@Override
	public void updateProperties() {
		GeoTextField temp, geo0 = getTextFieldAt(0);
		boolean equalSize = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getTextFieldAt(i);
			if (geo0.getLength() != temp.getLength())
				equalSize = false;
		}

		if (equalSize) {
			listener.setText(geo0.getLength() + "");
		} else {
			listener.setText("");
		}


	}

	public void applyChanges(final String strValue) {

		applyChanges(kernel.getAlgebraProcessor().evaluateToNumeric(
			strValue, true));
	}
	
	public void applyChanges(NumberValue value) {
		if (value != null && !Double.isNaN(value.getDouble())) {
			for (int i = 0; i < getGeosLength(); i++) {
				GeoTextField geo = getTextFieldAt(i);
				geo.setLength((int) value.getDouble());
				geo.updateRepaint();
			}
		}	
	}
	
	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index) instanceof GeoTextField);
	}

}

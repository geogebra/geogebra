package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.App;

public class TextFieldSizeModel extends OptionsModel {
	private ITextFieldListener listener;
	private Kernel kernel;
	public TextFieldSizeModel(App app, ITextFieldListener listener) {
		this.listener = listener;
		kernel = app.getKernel();
	}

	@Override
	public void updateProperties() {
		Object[] geos = getGeos();
		GeoTextField temp, geo0 = (GeoTextField) geos[0];
		boolean equalSize = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoTextField) geos[i];
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
				GeoTextField geo =
						(GeoTextField) getGeoAt(i);
				geo.setLength((int) value.getDouble());
				geo.updateRepaint();
			}
		}	
	}
	
	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getGeoAt(i) instanceof GeoTextField)) {
				geosOK = false;
				break;
			}
		}

		return geosOK;
	}

}

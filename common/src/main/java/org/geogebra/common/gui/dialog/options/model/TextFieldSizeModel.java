package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;

public class TextFieldSizeModel extends OptionsModel {
	private ITextFieldListener listener;

	public void setListener(ITextFieldListener listener) {
		this.listener = listener;
	}

	public TextFieldSizeModel(App app) {
		super(app);
	}

	private GeoInputBox getTextFieldAt(int index) {
		return (GeoInputBox) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		GeoInputBox temp, geo0 = getTextFieldAt(0);
		boolean equalSize = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getTextFieldAt(i);
			if (geo0.getLength() != temp.getLength()) {
				equalSize = false;
			}
		}

		if (equalSize) {
			listener.setText(geo0.getLength() + "");
		} else {
			listener.setText("");
		}

	}

	public void applyChanges(final String strValue) {

		applyChanges(app.getKernel().getAlgebraProcessor()
				.evaluateToNumeric(strValue, ErrorHelper.silent()));
	}

	public void applyChanges(NumberValue value) {
		if (value != null && !Double.isNaN(value.getDouble())) {
			for (int i = 0; i < getGeosLength(); i++) {
				GeoInputBox geo = getTextFieldAt(i);
				geo.setLength((int) value.getDouble());
				geo.updateRepaint();
			}
		}
		storeUndoInfo();
	}

	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index) instanceof GeoInputBox);
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

}

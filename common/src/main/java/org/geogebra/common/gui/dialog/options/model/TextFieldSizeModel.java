package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;

public class TextFieldSizeModel extends TextPropertyModel {


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

	@Override
	public String getTitle() {
		return "TextfieldLength";
	}

	@Override
	public void applyChanges(GeoNumberValue value) {
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

}

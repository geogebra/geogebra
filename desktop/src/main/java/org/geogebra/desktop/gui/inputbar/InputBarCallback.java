package org.geogebra.desktop.gui.inputbar;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;

public class InputBarCallback extends AsyncOperation<GeoElement[]> {
	private App app;
	private AutoCompleteTextFieldD inputField;
	private String input;

	public InputBarCallback(App app, AutoCompleteTextFieldD inputField,
			String input) {
		this.app = app;
		this.inputField = inputField;
		this.input = input;
	}

	@Override
	public void callback(GeoElement[] geos) {
		// need label if we type just eg
		// lnx
		if (geos != null && geos.length == 1 && !geos[0].labelSet) {
			geos[0].setLabel(geos[0].getDefaultLabel());
		}

		// set first outputs (same geo class) as selected geos (for
		// properties view)
		if (geos != null && geos.length > 0) {
			ArrayList<GeoElement> list = new ArrayList<GeoElement>();
			// add first output
			GeoElement geo = geos[0];
			list.add(geo);
			GeoClass c = geo.getGeoClassType();
			int i = 1;
			// add following outputs until geo class changes
			while (i < geos.length) {
				geo = geos[i];
				if (geo.getGeoClassType() == c) {
					list.add(geo);
					i++;
				} else {
					i = geos.length;
				}
			}
			app.getSelectionManager().setSelectedGeos(list);
		}

		new InputHelper().centerText(geos, app.getActiveEuclidianView());
		if (geos != null) {
			app.setScrollToShow(false);

			inputField.addToHistory(input);
			if (!inputField.getText().equals(input)) {
				inputField.addToHistory(inputField.getText());
			}
			inputField.setText(null);
		}

	}

}

package org.geogebra.desktop.gui.inputbar;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
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

		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
		if (geos != null && geos.length > 0 && geos[0] != null
				&& geos[0].isGeoText()) {
			GeoText text = (GeoText) geos[0];
			if (!text.isTextCommand() && text.getStartPoint() == null) {

				Construction cons = text.getConstruction();
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

				boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint p = new GeoPoint(text.getConstruction(), null,
						(ev.getXmin() + ev.getXmax()) / 2,
						(ev.getYmin() + ev.getYmax()) / 2, 1.0);
				cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

				try {
					text.setStartPoint(p);
					text.update();
				} catch (CircularDefinitionException e1) {
					e1.printStackTrace();
				}
			}
		}
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

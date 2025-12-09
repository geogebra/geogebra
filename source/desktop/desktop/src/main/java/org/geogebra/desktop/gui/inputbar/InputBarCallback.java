/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.inputbar;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;

public class InputBarCallback implements AsyncOperation<GeoElementND[]> {
	private final App app;
	private final AutoCompleteTextFieldD inputField;
	private final String input;
	private final int oldStep;

	/**
	 * @param app app
	 * @param inputField input field
	 * @param input input to be evaluated
	 * @param oldStep construction step
	 */
	public InputBarCallback(App app, AutoCompleteTextFieldD inputField,
			String input, int oldStep) {
		this.app = app;
		this.inputField = inputField;
		this.input = input;
		this.oldStep = oldStep;
	}

	@Override
	public void callback(GeoElementND[] geos) {

		// need label if we type just eg
		// lnx
		if (geos != null && geos.length == 1 && geos[0] != null
				&& !geos[0].isLabelSet()) {
			geos[0].setLabel(geos[0].getDefaultLabel());
		}

		// set first outputs (same geo class) as selected geos (for
		// properties view)
		if (geos != null && geos.length > 0 && geos[0] != null) {
			ArrayList<GeoElement> list = new ArrayList<GeoElement>();
			// add first output
			GeoElementND geo = geos[0];
			list.add(geo.toGeoElement());
			GeoClass c = geo.getGeoClassType();
			int i = 1;
			// add following outputs until geo class changes
			while (i < geos.length) {
				geo = geos[i];
				if (geo.getGeoClassType() == c) {
					list.add(geo.toGeoElement());
					i++;
				} else {
					i = geos.length;
				}
			}
			app.getSelectionManager().setSelectedGeos(list);
		}

		InputHelper.updateProperties(geos, app.getActiveEuclidianView(),
				oldStep);
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

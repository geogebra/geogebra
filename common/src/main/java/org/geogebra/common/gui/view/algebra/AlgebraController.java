/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class AlgebraController {

	protected Kernel kernel;
	protected App app;
	protected SelectionManager selection;
	private AlgebraView view;

	// private GeoVector tempVec;
	// private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
		selection = app.getSelectionManager();
	}

	public void setView(AlgebraView view) {
		this.view = view;
	}

	public App getApplication() {
		return app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public String getDragText() {
		return getDragText(new ArrayList<String>());
	}

	public String getDragText(ArrayList<String> geoLabelList) {

		String latex = null;

		for (GeoElement geo : selection.getSelectedGeos()) {
			geoLabelList.add(geo.getLabel(StringTemplate.defaultTemplate));
		}

		// if we have something ... do the drag!
		if (geoLabelList.size() > 0) {

			boolean showJustFirstGeoInDrag = false;

			if (selection.getSelectedGeos().size() == 1) {
				showJustFirstGeoInDrag = true;
			} else {

				// workaround for
				// http://forge.scilab.org/index.php/p/jlatexmath/issues/749/#preview
				for (GeoElement geo : selection.getSelectedGeos()) {
					if (geo.isGeoCurveCartesian()) {
						showJustFirstGeoInDrag = true;
						break;
					}
				}
			}

			if (showJustFirstGeoInDrag) {
				latex = selection.getSelectedGeos().get(0)
						.getLaTeXAlgebraDescription(true,
								StringTemplate.latexTemplate);
			} else {

				// create drag image
				StringBuilder sb = new StringBuilder();
				sb.append("\\fbox{\\begin{array}{l}");
				for (GeoElement geo : selection.getSelectedGeos()) {
					sb.append(geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplate));
					sb.append("\\\\");
				}
				sb.append("\\end{array}}");
				latex = sb.toString();
			}

		}
		return latex;
	}

	public void checkGeoTexts(GeoElementND[] newGeos) {
		if (newGeos == null) {
			// no GeoElements were created
			return;
		}
		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn,
		// ZoomOut, ...)
		if (newGeos.length > 0 && newGeos[0] != null
				&& newGeos[0].isGeoText()) {
			InputHelper.centerText((GeoText) newGeos[0],
					kernel.getApplication().getActiveEuclidianView());

		}
	}

	/**
	 * Evaluate the text entered in input. Used in Android and iOS.
	 * 
	 * @param input
	 *            input string
	 * @param errorHandler
	 *            interface to handle errors from evaluating the input
	 * @return evaluation was successful
	 */
	public boolean onTextEntered(String input, ErrorHandler errorHandler) {
		GeoElementND[] geos;
		try {

			AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {
				@Override
				public void callback(GeoElementND[] newGeos) {
					checkGeoTexts(newGeos);
				}
			};

			geos = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(input, true,
							errorHandler, true, callback);

			if (geos != null && geos.length == 1 && !geos[0].isLabelSet()) {
				geos[0].setLabel(geos[0].getDefaultLabel());
			}

		} catch (Exception ee) {
			errorHandler
					.showError(app.getLocalization().getError("InvalidInput"));
			return false;
		} catch (Error ee) {
			errorHandler.showError(ee.getLocalizedMessage());
			return false;
		}

		return geos != null;
	}

	protected AlgebraView getView() {
		return view;
	}
}

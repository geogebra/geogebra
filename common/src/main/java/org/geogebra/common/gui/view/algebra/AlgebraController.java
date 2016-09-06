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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.AsyncOperation;

public class AlgebraController {

	protected Kernel kernel;
	protected App app;
	protected SelectionManager selection;
	protected AlgebraView view;

	//private GeoVector tempVec;
	//private boolean kernelChanged;

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

	protected GeoElement lastSelectedGeo = null;
	protected boolean skipSelection;

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
				latex = selection
						.getSelectedGeos()
						.get(0)
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

	/**
	 * Evaluate the text entered in input. Used in Android and iOS.
	 * @param input input string
	 * @return evaluation was successful
     */
	public boolean onTextEntered(String input) {
		GeoElement[] geos;
		try {

			AsyncOperation callback = new AsyncOperation() {

				@Override
				public void callback(Object obj) {

					if (!(obj instanceof GeoElement[])) {
						// no GeoElements were created
						return;
					}
					GeoElement[] geos = (GeoElement[]) obj;

					// need label if we type just eg lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					// create texts in the middle of the visible view
					// we must check that size of geos is not 0 (ZoomIn,
					// ZoomOut, ...)
					if (geos.length > 0 && geos[0] != null
							&& geos[0].isGeoText()) {
						GeoText text = (GeoText) geos[0];
						if (!text.isTextCommand()
								&& text.getStartPoint() == null) {

							Construction cons = text.getConstruction();
							EuclidianViewInterfaceCommon ev = kernel.getApplication()
									.getActiveEuclidianView();

							boolean oldSuppressLabelsStatus = cons
									.isSuppressLabelsActive();
							cons.setSuppressLabelCreation(true);
							GeoPoint p = new GeoPoint(text.getConstruction(),
									null, (ev.getXmin() + ev.getXmax()) / 2,
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
				}

			};

			geos = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					input, true, app.getErrorHandler(), true, callback);

			if (geos != null && geos.length == 1 && !geos[0].labelSet) {
				geos[0].setLabel(geos[0].getDefaultLabel());
			}

		} catch (Exception ee) {
			app.showError(ee, null);
			return false;
		} catch (Error ee) {
			app.showError(ee.getMessage());
			return false;
		}

		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
		if (geos != null && geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
			GeoText text = (GeoText) geos[0];
			if (!text.isTextCommand() && text.getStartPoint() == null) {

				Construction cons = text.getConstruction();
				EuclidianView ev = app.getActiveEuclidianView();

				boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint p = new GeoPoint(text.getConstruction(), null,
						(ev.getXmin() + ev.getXmax()) / 2, (ev.getYmin() + ev.getYmax()) / 2,
						1.0);
				cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

				try {
					text.setStartPoint(p);
					text.update();
				} catch (CircularDefinitionException e1) {
					e1.printStackTrace();
				}
			}
		}
		return true;
	}
}

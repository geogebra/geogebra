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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

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

}

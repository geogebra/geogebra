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
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

import com.google.j2objc.annotations.Weak;

public class AlgebraController {

	@Weak
	protected Kernel kernel;
	@Weak
	protected App app;
	@Weak
	protected SelectionManager selection;
	@Weak
	private AlgebraView view;
	private boolean isAutoCreateSliders = true;
	private boolean isStoringUndo = true;

	// private GeoVector tempVec;
	// private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
		selection = app.getSelectionManager();
		isAutoCreateSliders = app.getConfig().hasAutomaticSliders();
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

	/**
	 * True if the algebra controller automatically creates sliders
	 * for unknown variables.
	 *
	 * @return true if sliders are created
	 */
	public boolean isAutoCreateSliders() {
		return isAutoCreateSliders;
	}

	/**
	 * Set to true if the algebra controller should store undo
	 * when the evaluation succeeds.
	 *
	 * @param isStoringUndo true to save undo
	 */
	public void setStoringUndo(boolean isStoringUndo) {
		this.isStoringUndo = isStoringUndo;
	}

	/**
	 * @param geoLabelList
	 *            list of geos to drag
	 * @return LaTeX for drag preview
	 */
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

	public boolean onTextEntered(String input, ErrorHandler errorHandler) {
		return onTextEntered(input, errorHandler, null);
	}

	/**
	 * Evaluate the text entered in input. Used in Android and iOS.
	 *
	 * @param input
	 *            input string
	 * @param errorHandler
	 *            interface to handle errors from evaluating the input
	 * @param cb
	 * 			  callback
	 * @return evaluation was successful
	 */
	public boolean onTextEntered(
			String input,
			ErrorHandler errorHandler,
			final AsyncOperation<GeoElementND[]> cb) {

		return onTextEntered(input, errorHandler, null, cb);
	}

	/**
	 * Evaluate the text entered in input. Used in Android and iOS.
	 * 
	 * @param input
	 *            input string
	 * @param errorHandler
	 *            interface to handle errors from evaluating the input
	 * @param info
	 * 			  additional information for the evaluation
	 * @param cb
	 * 			  callback
	 * @return evaluation was successful
	 */
	public boolean onTextEntered(
			String input,
			ErrorHandler errorHandler,
			EvalInfo info,
			final AsyncOperation<GeoElementND[]> cb) {
		GeoElementND[] geos;
		try {

			final AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {
				@Override
				public void callback(GeoElementND[] newGeos) {
					if (cb != null) {
						cb.callback(newGeos);
					}
					kernel.checkGeoTexts(newGeos);
				}
			};

			EvalInfo processingInfo = info;
			if (processingInfo == null) {
				processingInfo = kernel.getAlgebraProcessor()
						.getEvalInfo(isAutoCreateSliders, true);
			}
			geos = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(input,
							isStoringUndo, errorHandler,
							processingInfo.withSliders(isAutoCreateSliders),
							callback);

			if (geos != null && geos.length == 1 && !geos[0].isLabelSet()) {
				geos[0].setLabel(geos[0].getDefaultLabel());
			}
		} catch (Exception ee) {
			errorHandler
					.showError(app.getLocalization().getInvalidInputError());
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

	/**
	 * 
	 * @param property
	 *            visual style property
	 * @return true if changes for this property needs update in AV
	 */
	static public boolean needsUpdateVisualstyle(GProperty property) {
		switch (property) {
		case FONT:
			return true;
		case COLOR:
			return true;
		case POSITION:
			return false;
		case CAPTION:
			return false;
		case COMBINED:
			return true;
		case ANGLE_INTERVAL:
			return false;
		case COLOR_BG:
			return true;
		case LINE_STYLE:
			return false;
		case POINT_STYLE:
			return false;
		case VISIBLE:
			return true;
		case LAYER:
			return false;
		case ANGLE_STYLE:
			return false;
		case LABEL_STYLE:
			return true;
		case LENGTH:
			return true;
		case HATCHING:
			return false;
		case HIGHLIGHT:
			return true;
		default:
			return true;
		}
	}

	/**
	 * 
	 * @param app
	 *            app
	 * @param geoElement
	 *            geo
	 * @return true if geo is shown in AV
	 */
	static public boolean show(App app, GeoElement geoElement) {
		return geoElement.isLabelSet() && geoElement.showInAlgebraView()
				&& geoElement.isSetAlgebraVisible()
				&& (app.showAuxiliaryObjects()
						|| !geoElement.isAuxiliaryObject());
	}
}

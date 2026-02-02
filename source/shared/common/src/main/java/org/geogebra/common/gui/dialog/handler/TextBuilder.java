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

package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class TextBuilder {
	private final App app;
	private final GeoPointND startPoint;
	private final boolean rw;
	private boolean isLaTeX;
	private boolean isSerif;
	private int fontStyle = GFont.PLAIN;
	private TextStyle textStyle;

	/**
	 * @param app application
	 * @param startPoint pos
	 * @param rw whether to use rw coordinates
	 */
	public TextBuilder(App app, GeoPointND startPoint, boolean rw, TextStyle textStyle) {
		this.app = app;
		this.startPoint = startPoint;
		this.rw = rw;
		this.textStyle = textStyle;
	}

	/**
	 * @param app application
	 * @param startPoint pos
	 * @param rw whether to use rw coordinates
	 * @param isLaTeX whether text is LaTeX
	 */
	public TextBuilder(App app, GeoPointND startPoint, boolean rw, boolean isLaTeX) {
		this.app = app;
		this.startPoint = startPoint;
		this.rw = rw;
		this.isLaTeX = isLaTeX;
	}

	/**
	 * @param fontStyle se GFont.getStyle()
	 * @param isSerif whether to use serif font
	 */
	public void setStyle(int fontStyle, boolean isSerif) {
		this.fontStyle = fontStyle;
		this.isSerif = isSerif;
	}

	/**
	 * @param inputValue text definition
	 * @param handler error handler
	 * @param callback callback when text created
	 */
	public void createText(String inputValue, ErrorHandler handler,
			AsyncOperation<Boolean> callback) {
		EvalInfo noLabel = new EvalInfo(false).withSliders(true);
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(inputValue,
						false, handler, noLabel, getCallback(callback));
	}

	private void positionText(GeoText t) {
		EuclidianViewInterfaceCommon activeView = app
				.getActiveEuclidianView();

		if (startPoint.isLabelSet()) {
			t.checkVisibleIn3DViewNeeded();
			try {
				t.setStartPoint(startPoint);
			} catch (Exception e) {
				// circular definition
			}
		} else {

			Coords coords = startPoint.getInhomCoordsInD3();
			if (rw) {
				t.setRealWorldLoc(
						activeView.toRealWorldCoordX(
								coords.getX()),
						activeView.toRealWorldCoordY(
								coords.getY()));
				t.setAbsoluteScreenLocActive(false);
			} else {
				t.setAbsoluteScreenLoc((int) coords.getX(),
						(int) coords.getY());
				t.setAbsoluteScreenLocActive(true);

			}

			// when not a point clicked, show text only in
			// active
			// view
			if (activeView.isEuclidianView3D()) {
				// we need to add it to 3D view since by default
				// it may not
				app.addToViews3D(t);
				app.removeFromEuclidianView(t);
				t.setVisibleInViewForPlane(false);
				app.removeFromViewsForPlane(t);
			} else if (activeView.isDefault2D()) {
				removeFromViews3D(t);
				t.setVisibleInViewForPlane(false);
				app.removeFromViewsForPlane(t);
			} else { // view for plane
				app.removeFromEuclidianView(t);
				removeFromViews3D(t);
				t.setVisibleInViewForPlane(true);
				app.addToViewsForPlane(t);
			}
		}
		// make sure (only) the output of the text tool is
		// selected
		activeView.getEuclidianController()
				.memorizeJustCreatedGeos(t.asArray());
		t.setLabel(null);
	}

	private void removeFromViews3D(GeoText t) {
		if (app.isEuclidianView3Dinited()) {
			app.removeFromViews3D(t);
		} else {
			t.removeViews3D();
		}
	}

	private AsyncOperation<GeoElementND[]> getCallback(
			final AsyncOperation<Boolean> callback) {
		return ret -> {
			if (ret != null && ret[0] instanceof GeoText) {
				GeoText t = (GeoText) ret[0];
				if (textStyle != null) {
					t.setLaTeX(textStyle.isLatex(), true);
					t.setFontStyle(TextOptionsModel.getFontStyle(textStyle.isBold(),
							textStyle.isItalic()));
					t.setSerifFont(textStyle.isSerif());
					t.setBackgroundColor(textStyle.getBgColor());
					t.setObjColor(textStyle.getFontColor());
					// make sure for new LaTeX texts we get nice "x"s
					if (textStyle.isLatex()) {
						t.setSerifFont(true);
					}
				} else {
					t.setLaTeX(isLaTeX, true);
					t.setFontStyle(fontStyle);
					t.setSerifFont(isSerif);
					// make sure for new LaTeX texts we get nice "x"s
					if (isLaTeX) {
						t.setSerifFont(true);
					}
				}
				positionText(t);
				app.storeUndoInfo();
				callback.callback(true);
				return;
			}
			callback.callback(false);
		};
	}
}


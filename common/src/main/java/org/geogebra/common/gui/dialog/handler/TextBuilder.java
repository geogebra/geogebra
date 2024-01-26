package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
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
	private final boolean isLaTeX;
	private boolean isSerif;
	private int fontStyle = GFont.PLAIN;

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
	 * @return this
	 */
	public TextBuilder setStyle(int fontStyle, boolean isSerif) {
		this.fontStyle = fontStyle;
		this.isSerif = isSerif;
		return this;
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
				if (app
						.isEuclidianView3Dinited()) {
					app
							.removeFromViews3D(t);
				} else {
					t.removeViews3D();
				}
				t.setVisibleInViewForPlane(false);
				app
						.removeFromViewsForPlane(t);
			} else { // view for plane
				app.removeFromEuclidianView(t);
				if (app
						.isEuclidianView3Dinited()) {
					app
							.removeFromViews3D(t);
				} else {
					t.removeViews3D();
				}
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

	private AsyncOperation<GeoElementND[]> getCallback(
			final AsyncOperation<Boolean> callback) {
		return ret -> {
			if (ret != null && ret[0] instanceof GeoText) {
				GeoText t = (GeoText) ret[0];
				t.setLaTeX(isLaTeX, true);
				t.setFontStyle(fontStyle);
				t.setSerifFont(isSerif);
				// make sure for new LaTeX texts we get nice "x"s
				if (isLaTeX) {
					t.setSerifFont(true);
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


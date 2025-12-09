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

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class ViewLocationModel extends OptionsModel {
	private IGraphicsViewLocationListener listener;

	public interface IGraphicsViewLocationListener extends PropertyListener {
		@MissingDoc
		public void selectView(int index, boolean isSelected);

		@MissingDoc
		public void setCheckBox3DVisible(boolean flag);

		@MissingDoc
		public void setCheckBoxForPlaneVisible(boolean flag);

		// public void setCheckBoxAlgebraVisible(boolean flag);
	}

	public ViewLocationModel(App app, IGraphicsViewLocationListener listener) {
		super(app);
		this.listener = listener;

	}

	@Override
	public void updateProperties() {
		boolean isInEV = false;
		boolean isInEV2 = false;
		boolean isInEV3D = false;
		boolean isInEVForPlane = false;
		boolean isInAV = false;

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN)) {
				isInEV = true;
			}

			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN2)) {
				isInEV2 = true;
			}

			if (geo.isVisibleInView3D()) {
				isInEV3D = true;
			}

			if (app.hasEuclidianViewForPlane()) {
				if (geo.isVisibleInViewForPlane()) {
					isInEVForPlane = true;
				}
			}

			if (geo.isAlgebraVisible()) {
				isInAV = true;
			}

		}

		listener.selectView(0, isInEV);
		listener.selectView(1, isInEV2);
		listener.selectView(2, isInEV3D);
		listener.selectView(3, isInEVForPlane);

		listener.selectView(4, isInAV);

	}

	public void applyToEuclidianView1(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (value) {
				app.addToEuclidianView(geo);
			} else {
				app.removeFromEuclidianView(geo);
			}
		}
	}

	public void applyToEuclidianView2(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			EuclidianView ev2 = app.getEuclidianView2(1);

			if (value) {
				geo.addView(App.VIEW_EUCLIDIAN2);
				ev2.add(geo);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
				ev2.remove(geo);
			}

		}
		storeUndoInfo();
	}

	public void applyToEuclidianView3D(boolean value) {

		if (!app.isEuclidianView3Dinited()) {
			return;
		}

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			EuclidianView3DInterface ev3D = app.getEuclidianView3D();

			if (value) {
				geo.addViews3D();
				ev3D.add(geo);
			} else {
				geo.removeViews3D();
				ev3D.remove(geo);
			}

		}
		storeUndoInfo();
	}

	public void applyToEuclidianViewForPlane(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);

			if (value) {
				geo.setVisibleInViewForPlane(true);
				app.addToViewsForPlane(geo);
			} else {
				geo.setVisibleInViewForPlane(false);
				app.removeFromViewsForPlane(geo);
			}

		}
		storeUndoInfo();
	}

	public void applyToAlgebraView(Boolean value) {

		AlgebraView av = app.getAlgebraView();
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setAlgebraVisible(value);
			if (value) {
				av.add(geo);
			} else {
				av.remove(geo);
			}
			geo.updateRepaint();

		}
		storeUndoInfo();
	}

	@Override
	public boolean checkGeos() {

		if (listener == null) {
			return false;
		}

		listener.setCheckBox3DVisible(true);

		if (app.hasEuclidianViewForPlane()) {
			listener.setCheckBoxForPlaneVisible(true);
		} else {
			listener.setCheckBoxForPlaneVisible(false);
		}

		boolean go = true;
		for (int i = 0; go && i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!geo.hasDrawable3D()) {
				listener.setCheckBox3DVisible(false);
				listener.setCheckBoxForPlaneVisible(false);
				go = false;
			}
		}

		// if (app.has(Feature.AV_EXTENSIONS)) {
		//
		// }
		return true;
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

}

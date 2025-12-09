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

import java.util.ArrayList;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

import com.google.j2objc.annotations.Weak;

@SuppressWarnings("deprecation")
@Deprecated
/*
* This class is not UI independent that's why it can not be used by mobile. Logic needs changes later to
* be commonly usable
* */
public abstract class OptionsModel {
	private Object[] geos; // currently selected geos

	@Weak
	protected final App app;

	public OptionsModel(App app) {
		this.app = app;
	}

	public Object[] getGeos() {
		return geos;
	}

	public void setGeos(Object[] geos) {
		this.geos = geos;
	}

	public Object getObjectAt(int i) {
		return geos[i];
	}

	public GeoElement getGeoAt(int i) {
		return (GeoElement) geos[i];
	}

	public int getGeosLength() {
		return geos.length;
	}

	public boolean hasGeos() {
		return geos != null && geos.length > 0;
	}

	protected abstract boolean isValidAt(int index);

	@MissingDoc
	public abstract void updateProperties();

	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!isValidAt(i)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	/**
	 * Used for displaying angle properties only, if elements of a list are
	 ** angles
	 */
	public static boolean isAngleList(GeoElement geo) {
		if (geo.isGeoList()) {
			GeoClass elemType = ((GeoList) geo).getElementType();
			return elemType == GeoClass.ANGLE || elemType == GeoClass.ANGLE3D;
		}

		return false;
	}

	@MissingDoc
	public abstract PropertyListener getListener();

	public final boolean updateMPanel(Object[] geos2) {
		if (getListener() == null) {
			setGeos(geos2);
			return this.checkGeos();
		}
		return getListener().updatePanel(geos2) != null;
	}

	public void storeUndoInfo() {
		if (app != null) {
			app.getKernel().getConstruction().getUndoManager()
					.storeUndoInfo();
		}
	}

	/**
	 * @return list of geos
	 */
	public ArrayList<GeoElement> getGeosAsList() {
		int size = getGeosLength();
		ArrayList<GeoElement> geoList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			geoList.add(getGeoAt(i));
		}
		return geoList;
	}
}

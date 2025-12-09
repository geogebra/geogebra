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

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.StringUtil;

public class StartPointModel extends MultipleGeosModel {

	private Kernel kernel;

	public StartPointModel(App app) {
		super(app);
		this.kernel = app.getKernel();
	}

	public Locateable getLocateableAt(int index) {
		return (Locateable) getObjectAt(index);
	}

	@Override
	public void updateProperties() {

		// repopulate model with names of points from the geoList's model
		// take all points from construction
		// TreeSet points =
		// kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);

		// check if properties have same values
		Locateable geo0 = getLocateableAt(0);

		boolean equalLocation = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (geo0.getStartPoint() != getLocateableAt(i).getStartPoint()) {
				equalLocation = false;
				break;
			}

		}

		GeoPointND p = geo0.getStartPoint();
		if (equalLocation && p != null) {
			getListener().setSelectedIndex(0);
		} else {
			getListener().setSelectedIndex(-1);
		}

	}

	@Override
	public String getTitle() {
		return "StartingPoint";
	}

	public void applyChanges(final String strLoc, ErrorHandler handler) {
		GeoPointND newLoc = null;
		handler.resetError();
		if (!StringUtil.emptyTrim(strLoc)) {
			newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
					handler, true);
		}
		if (newLoc == null) {
			return;
		}
		for (int i = 0; i < getGeosLength(); i++) {
			Locateable l = getLocateableAt(i);
			try {
				l.setStartPoint(newLoc);
				l.toGeoElement().updateRepaint();
			} catch (CircularDefinitionException e) {
				ErrorHelper.handleException(e, app, handler);
			}
		}
	}

	@Override
	protected void apply(int index, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getValueAt(int index) {
		// not used
		return 0;
	}

	@Override
	protected boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (!(geo instanceof Locateable && !((Locateable) geo).isAlwaysFixed())
				|| geo.isGeoImage()
				|| geo.getParentAlgorithm() instanceof AlgoVector
				|| isAbsoluteLocation(geo)) {
			valid = false;
		}
		return valid;
	}

	private boolean isAbsoluteLocation(GeoElement geo) {
		return geo instanceof AbsoluteScreenLocateable
				&& ((AbsoluteScreenLocateable) geo).isAbsoluteScreenLocActive();
	}
}

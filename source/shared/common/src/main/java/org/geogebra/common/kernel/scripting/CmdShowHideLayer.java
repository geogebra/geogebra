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

package org.geogebra.common.kernel.scripting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * HideLayer
 */
public class CmdShowHideLayer extends CmdScripting {

	private boolean show;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param show
	 *            whether to show or hide
	 */
	public CmdShowHideLayer(Kernel kernel, boolean show) {
		super(kernel);
		this.show = show;
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0] instanceof NumberValue) {
				int layer = (int) arg[0].evaluateDouble();
				if (layer < 0 || layer > EuclidianStyleConstants.MAX_LAYERS) {
					return arg;
				}
				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				ArrayList<GeoElement> set = new ArrayList<>();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(show);
						set.add(geo);
					}
				}
				GeoElement.updateCascade(set, new TreeSet<>(), true);
				kernel.notifyRepaint();
				return arg;
			}
			throw argErr(c, null);

		default:
			throw argNumErr(c);
		}
	}
}

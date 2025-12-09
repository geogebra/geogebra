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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Helper for Sum[list of lists]
 */
public class ListFold implements FoldComputer {

	private MyList sum;
	private GeoList result;

	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return result = new GeoList(cons);
	}

	@Override
	public void add(GeoElement geoElement, Operation op) {
		sum.applyLeft(op, geoElement, StringTemplate.defaultTemplate);

	}

	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		sum = ((GeoList) geoElement).getMyList();

	}

	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement.isGeoList();
	}

	@Override
	public void finish() {
		result.clear();
		AlgebraProcessor ap = result.getKernel().getAlgebraProcessor();
		boolean oldMode = result.getConstruction().isSuppressLabelsActive();
		result.getConstruction().setSuppressLabelCreation(true);
		for (int i = 0; i < sum.size(); i++) {
			try {
				result.add(ap.processValidExpression(
						sum.get(i).wrap())[0]);
			} catch (MyError | Exception e) {
				result.setUndefined();
				Log.debug(e);
			}
		}
		result.getConstruction().setSuppressLabelCreation(oldMode);

	}

}

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

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

public class ListFold implements FoldComputer {

	private MyList sum;
	private GeoList result;

	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return result = new GeoList(cons);
	}

	public void add(GeoElement geoElement, Operation op) {
		sum.applyLeft(op, geoElement, StringTemplate.defaultTemplate);

	}

	public void setFrom(GeoElement geoElement, Kernel kernel) {
		sum = ((GeoList) geoElement).getMyList();

	}

	public boolean check(GeoElement geoElement) {
		return geoElement.isGeoList();
	}

	public void finish() {
		result.clear();
		AlgebraProcessor ap = result.getKernel().getAlgebraProcessor();
		for (int i = 0; i < sum.size(); i++) {
			try {
				result.add(ap.processValidExpression(
						sum.getListElement(i).wrap())[0]);
			} catch (MyError e) {
				result.setUndefined();
				e.printStackTrace();
			} catch (Exception e) {
				result.setUndefined();
				e.printStackTrace();
			}
		}

	}

}

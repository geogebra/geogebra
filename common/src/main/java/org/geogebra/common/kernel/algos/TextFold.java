package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.Operation;

public class TextFold implements FoldComputer {

	private GeoText result;
	private double x, y, z;
	private StringBuilder sb;

	public GeoElement getTemplate(Construction cons, GeoElement listElement) {
		return this.result = new GeoText(cons);
	}

	public void add(GeoElement p, Operation op) {
		if (p.isGeoText()) {
			sb.append(((GeoText) p).getTextString());
		} else {
			result.setUndefined();
			return;
		}

	}

	public void setFrom(GeoElement geoElement, Kernel kernel) {
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}
		add(geoElement, Operation.PLUS);
	}

	public boolean check(GeoElement geoElement) {
		return geoElement.isGeoText();
	}

	public void finish() {
		result.setTextString(sb.toString());

	}

}

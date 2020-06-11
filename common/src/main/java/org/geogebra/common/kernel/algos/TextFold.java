package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Helper for Sum[list of text]
 */
public class TextFold implements FoldComputer {

	private GeoText result;
	private StringBuilder sb;

	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.result = new GeoText(cons);
	}

	@Override
	public void add(GeoElement p, Operation op) {
		if (p.isGeoText() && ((GeoText) p).getTextString() != null) {
			sb.append(((GeoText) p).getTextString());
		} else {
			result.setUndefined();
		}

	}

	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}
		add(geoElement, Operation.PLUS);
	}

	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement.isGeoText();
	}

	@Override
	public void finish() {
		result.setTextString(sb.toString());

	}

}

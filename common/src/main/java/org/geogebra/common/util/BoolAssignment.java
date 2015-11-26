/**
 * 
 */
package org.geogebra.common.util;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author Christoph
 */
public class BoolAssignment extends Assignment {

	private GeoBoolean geoBoolean;
	private String geoBooleanName;

	public BoolAssignment(GeoBoolean geoBoolean, Kernel kernel) {
		super(kernel);
		this.geoBoolean = geoBoolean;
	}

	public BoolAssignment(String name, Kernel kernel) {
		super(kernel);
		geoBooleanName = name;
	}

	@Override
	public Result checkAssignment(Construction construction) {
		if (getGeoBoolean() == null) {
			res = Result.UNKNOWN;
		} else {
			res = getGeoBoolean().getBoolean() ? Result.CORRECT : Result.WRONG;
		}
		return res;
	}

	@Override
	public Result[] possibleResults() {
		Result[] results = { Result.CORRECT, Result.WRONG, Result.UNKNOWN };
		return results;
	}

	@Override
	public String getIconFileName() {
		// TODO: thats a design flaw somehow
		return "BoolAssignment";
	}

	@Override
	public String getDisplayName() {
		return getGeoBoolean() != null ? getGeoBoolean().getNameDescription()
				: "";
	}

	@Override
	public boolean isValid() {
		// TODO Not sure at the moment this is needed here,
		// but if we do lazy removing of BoolAssignments from Exercise this
		// could be useful.
		return kernel.getConstruction()
				.getGeoSetNameDescriptionOrder().contains(getGeoBoolean());
	}

	/**
	 * Test if a GeoBoolean is used by this assignment
	 * 
	 * @param geo
	 *            GeoBoolean to check for
	 * @return true if geo is used by this assignment
	 */
	public boolean usesGeoBoolean(GeoBoolean geo) {
		return getGeoBoolean() != null && getGeoBoolean().equals(geo);
	}

	public GeoBoolean getGeoBoolean() {
		if (geoBoolean == null) {
			GeoElement geoElem = kernel.lookupLabel(geoBooleanName);
			if (geoElem instanceof GeoBoolean) {
				geoBoolean = (GeoBoolean) geoElem;
			}
		}
		return geoBoolean;
	}

	@Override
	public String getAssignmentXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t<assignment booleanName=\"");
		StringUtil.encodeXML(sb, getGeoBoolean().getLabelSimple());
		sb.append("\">\n");

		getAssignmentXML(sb);

		return sb.toString();
	}
}

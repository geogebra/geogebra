/**
 * 
 */
package org.geogebra.common.util;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.main.App;

/**
 * @author Christoph
 */
public class BoolAssignment extends Assignment {

	private GeoBoolean check;

	public BoolAssignment(GeoBoolean check) {
		super();
		this.check = check;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geogebra.common.util.Assignment#checkAssignment(org.geogebra.common
	 * .kernel.Construction)
	 */
	@Override
	public Result checkAssignment(Construction construction) {
		res = check.getBoolean() ? Result.CORRECT : Result.WRONG;
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
	public String getAssignmentXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		return check.getNameDescription();
	}

	@Override
	public boolean isValid(App app) {
		// TODO Not sure at the moment this is needed here,
		// but if we do lazy removing of BoolAssignments from Exercise this
		// could be useful.
		return app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder().contains(check);
	}

	/**
	 * Test if a GeoBoolean is used by this assignment
	 * 
	 * @param geo
	 *            GeoBoolean to check for
	 * @return true if geo is used by this assignment
	 */
	public boolean usesGeoBoolean(GeoBoolean geo) {
		return check.equals(geo);
	}

	public GeoBoolean getGeoBoolean() {
		return check;
	}

}

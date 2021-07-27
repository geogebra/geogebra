package org.geogebra.common.util;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author Christoph
 */
public class BoolAssignment extends Assignment {

	private GeoBoolean geoBoolean;
	private String geoBooleanLabel;
	private String geoBooleanOldLabel;

	/**
	 * Create a BoolAssignment
	 * 
	 * @param geoBoolean
	 *            a GeoBoolean if true should give a correct assignment false a
	 *            WRONG assignment. Meaning can be reversed by setting negative
	 *            fractions for WRONG and vice versa.
	 * @param kernel
	 *            Kernel
	 */
	public BoolAssignment(GeoBoolean geoBoolean, Kernel kernel) {
		this(geoBoolean.getLabelSimple(), kernel);
		this.geoBooleanOldLabel = geoBoolean.getOldLabel();
		this.geoBoolean = geoBoolean;
	}

	/**
	 * Constructor to be used during file loading when the GeoBoolean to check
	 * for is not existing.
	 * 
	 * @param label
	 *            Label of the GeoBoolean to use for checking
	 * @param kernel
	 *            Kernel
	 */
	public BoolAssignment(String label, Kernel kernel) {
		super(kernel);
		geoBooleanLabel = label;
	}

	@Override
	public Result checkAssignment() {
		if (!isValid()) {
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
		return getClass().getSimpleName();
	}

	@Override
	public String getDisplayName() {
		return getGeoBoolean() != null ? getGeoBoolean().getNameDescription()
				: "";
	}

	@Override
	public boolean isValid() {
		return kernel.getConstruction().getGeoSetNameDescriptionOrder()
				.contains(getGeoBoolean());
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

	/**
	 * @return the {@link GeoBoolean} used to check correctness
	 */
	public GeoBoolean getGeoBoolean() {
		if (geoBoolean == null) {
			GeoElement geoElem = kernel.lookupLabel(geoBooleanLabel);
			if (geoElem instanceof GeoBoolean) {
				geoBoolean = (GeoBoolean) geoElem;
			}
		} else {
			update();
		}
		return geoBoolean;
	}

	@Override
	public String getAssignmentXML() {
		if (getGeoBoolean() == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\t<assignment booleanName=\"");
		StringUtil.encodeXML(sb, getGeoBoolean().getLabelSimple());
		sb.append("\">\n");

		getAssignmentXML(sb);

		return sb.toString();
	}

	/**
	 * @return update reference to Boolean geo
	 */
	public boolean update() {
		String initLabel = geoBoolean == null ? geoBooleanLabel
				: geoBoolean.getLabelSimple();
		GeoElement geo = kernel.lookupLabel(initLabel);
		if (geo == null) {
			geo = kernel.lookupLabel(geoBoolean.getOldLabel());
			if (geo == null) {
				geo = kernel.lookupLabel(geoBooleanOldLabel);
			}
		}
		boolean ret = false;
		if (geo instanceof GeoBoolean) {
			geoBoolean = (GeoBoolean) geo;
			if (!geoBooleanLabel.equals(geoBoolean.getLabelSimple())) {
				geoBooleanOldLabel = geoBooleanLabel;
				geoBooleanLabel = geoBoolean.getLabelSimple();
			}
			ret = true;
		} else {
			ret = false;
		}
		return ret;
	}
}

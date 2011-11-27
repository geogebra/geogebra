package geogebra.kernel.geos;

import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.common.kernel.arithmetic.AbstractCommand;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;

/*
 * needed for minimal applets
 */
public interface GeoGebraCASInterface {

	public StringType getCurrentCASstringType();

	public String evaluateRaw(String geoStr) throws Throwable;

	public CASgeneric getCurrentCAS();

	public String evaluateGeoGebraCAS(ValidExpression evalVE);

	public CASparser getCASparser();

	public boolean isStructurallyEqual(ValidExpression inputVE, String newInput);

	public boolean isCommandAvailable(AbstractCommand cmd);

	public String[] getPolynomialCoeffs(String exp, String variable);

	public String evaluateGeoGebraCAS(String exp);

}

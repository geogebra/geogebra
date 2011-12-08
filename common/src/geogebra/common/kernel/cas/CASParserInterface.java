package geogebra.common.kernel.cas;

import geogebra.common.kernel.arithmetic.ValidExpression;

public interface CASParserInterface {

	ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue);

}

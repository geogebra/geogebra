package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for GeoElements that implement NumberValue
 * @author zbynek
 *
 */
public interface GeoNumberValue extends GeoElementND, NumberValue, SpreadsheetTraceable {
	//just tagging interface
}

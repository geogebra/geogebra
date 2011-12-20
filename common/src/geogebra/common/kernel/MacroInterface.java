package geogebra.common.kernel;

import geogebra.common.euclidian.Test;
import geogebra.common.kernel.geos.GeoElement;

public interface MacroInterface {

	Test[] getInputTypes();

	String getCommandName();

}

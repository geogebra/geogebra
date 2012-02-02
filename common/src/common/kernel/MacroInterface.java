package geogebra.common.kernel;

import geogebra.common.kernel.geos.Test;

public interface MacroInterface {

	Test[] getInputTypes();

	String getCommandName();

}

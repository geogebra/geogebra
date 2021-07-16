package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.main.Localization;

public interface HasLaTeX {

	String toLaTeXString(Localization loc);

	String toLaTeXString(Localization loc, boolean detailed);

	HasLaTeX deepCopy();
}

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.editor.share.util.Unicode;

public class AutomaticDegree extends MySpecialDouble {
	public AutomaticDegree(Kernel kernel) {
		super(kernel, Kernel.PI_180, Unicode.DEGREE_STRING);
	}
}

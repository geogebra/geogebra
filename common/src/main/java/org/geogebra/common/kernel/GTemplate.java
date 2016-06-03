package org.geogebra.common.kernel;

import org.geogebra.common.kernel.Matrix.CoordSys;

public class GTemplate {
	private StringTemplate tpl;
	private Kernel kernel;

	public GTemplate(StringTemplate tpl, Kernel kernel) {
		this.tpl = tpl;
		this.kernel = kernel;
	}

	public StringTemplate getTemplate() {
		return tpl;
	}

	public StringBuilder buildImplicitEquation(CoordSys coordSys, String[] var,
			boolean keep, boolean needsZ) {
		return kernel.buildImplicitEquation(coordSys.getEquationVector().get(),
				var, keep, true, needsZ, '=', tpl);
	}
}

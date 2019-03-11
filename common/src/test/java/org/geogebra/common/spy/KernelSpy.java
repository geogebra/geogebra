package org.geogebra.common.spy;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.spy.construction.ConstructionSpy;

class KernelSpy extends Kernel {

	KernelSpy(App app) {
		this.app = app;
		cons = new ConstructionSpy(this);
	}
}

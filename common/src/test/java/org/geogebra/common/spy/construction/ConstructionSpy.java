package org.geogebra.common.spy.construction;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

public class ConstructionSpy extends Construction {

	public ConstructionSpy(Kernel k) {
		super(k);
	}

	@Override
	protected void newConstructionDefaults() {
		consDefaults = new ConstructionDefaultsSpy(this);
	}
}

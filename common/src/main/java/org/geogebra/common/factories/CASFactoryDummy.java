package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

public class CASFactoryDummy extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, Kernel k) {
		return new CASDummy(p);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}

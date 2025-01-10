package org.geogebra.web.html5.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

public class NoCASFactory extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, Kernel k) {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}

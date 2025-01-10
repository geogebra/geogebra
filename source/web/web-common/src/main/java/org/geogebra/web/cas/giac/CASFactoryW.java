package org.geogebra.web.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

/**
 * GWT CAS loader
 */
public class CASFactoryW extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, Kernel kernel) {
		return new CASgiacW(p, kernel);
	}

}

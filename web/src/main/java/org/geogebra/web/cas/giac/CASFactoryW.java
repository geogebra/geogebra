package org.geogebra.web.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

public class CASFactoryW extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, CasParserTools t,
	        Kernel kernel) {
		return new CASgiacW(p, t, kernel);
	}

}

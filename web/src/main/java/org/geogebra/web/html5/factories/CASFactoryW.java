package org.geogebra.web.html5.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.cas.giac.CASgiacW;

public class CASFactoryW extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, CasParserTools t,
	        Kernel kernel) {
		return new CASgiacW(p, t, kernel);
	}

}

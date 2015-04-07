package org.geogebra.desktop.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.desktop.cas.giac.CASgiacD;

public class CASFactoryD extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, CasParserTools t, Kernel k) {
		return new CASgiacD(p, t, k);
	}

}

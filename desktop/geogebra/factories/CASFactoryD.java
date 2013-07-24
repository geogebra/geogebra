package geogebra.factories;

import geogebra.cas.giac.CASgiacD;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.factories.CASFactory;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;

public class CASFactoryD extends CASFactory {

	@Override
	public CASGenericInterface newGiac(CASparser p, CasParserTools t,Kernel k) {
		return new CASgiacD(p, t, k);
	}

}

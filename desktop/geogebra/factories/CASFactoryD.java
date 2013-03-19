package geogebra.factories;

import geogebra.cas.giac.CASgiacD;
import geogebra.cas.mpreduce.CASmpreduceD;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;

public class CASFactoryD extends geogebra.common.factories.CASFactory {

	@Override
	public CASGenericInterface newMPReduce(CASparser p, CasParserTools t,Kernel k) {
		return new CASmpreduceD(p,t);
	}

	@Override
	public CASGenericInterface newGiac(CASparser p, CasParserTools t,Kernel k) {
		return new CASgiacD(p,t);
	}

}

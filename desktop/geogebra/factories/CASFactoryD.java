package geogebra.factories;

import geogebra.cas.mpreduce.CASmpreduceD;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.Kernel;

public class CASFactoryD extends geogebra.common.factories.CASFactory {

	@Override
	public CASmpreduce newMPReduce(CASparser p, CasParserTools t,Kernel k) {
		return new CASmpreduceD(p,t);
	}

}

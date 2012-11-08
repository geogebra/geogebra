package geogebra.web.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.factories.CASFactory;
import geogebra.common.kernel.Kernel;
import geogebra.web.cas.mpreduce.CASmpreduceW;

public class CASFactoryW extends CASFactory {

	@Override
	public CASmpreduce newMPReduce(CASparser p, CasParserTools t,Kernel kernel) {
		return new CASmpreduceW(p,t,kernel);
	}

}

package geogebra.web.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;
import geogebra.common.factories.CASFactory;
import geogebra.common.kernel.Kernel;
import geogebra.web.cas.mpreduce.CASmpreduce;

public class CASFactoryW extends CASFactory {

	@Override
	public AbstractCASmpreduce newMPReduce(CASparser p, CasParserTools t,Kernel kernel) {
		return new CASmpreduce(p,t,kernel);
	}

}

package geogebra.factories;

import geogebra.cas.mpreduce.CASmpreduceD;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

public class CASFactoryD extends geogebra.common.factories.CASFactory {

	@Override
	public CASmpreduce newMPReduce(App app, CASparser p, CasParserTools t,Kernel kernel) {
		return new CASmpreduceD(app,p,t,kernel.getCasVariablePrefix());
	}

}

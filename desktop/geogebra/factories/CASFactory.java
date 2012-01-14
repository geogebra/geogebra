package geogebra.factories;

import geogebra.cas.mpreduce.CASmpreduce;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;

public class CASFactory extends geogebra.common.factories.CASFactory {

	@Override
	public AbstractCASmpreduce newMPReduce(CASparser p, CasParserTools t) {
		return new CASmpreduce(p,t);
	}

}

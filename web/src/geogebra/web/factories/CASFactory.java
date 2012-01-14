package geogebra.web.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;
import geogebra.web.cas.mpreduce.CASmpreduce;

public class CASFactory extends geogebra.common.factories.CASFactory {

	@Override
	public AbstractCASmpreduce newMPReduce(CASparser p, CasParserTools t) {
		return new CASmpreduce(p,t);
	}

}

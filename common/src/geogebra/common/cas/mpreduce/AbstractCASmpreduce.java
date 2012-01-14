package geogebra.common.cas.mpreduce;

import geogebra.common.cas.CASgeneric;
import geogebra.common.cas.CASparser;

public abstract class AbstractCASmpreduce extends CASgeneric{

	public AbstractCASmpreduce(CASparser casParser) {
		super(casParser);
	}

	public abstract String evaluateMPReduce(String exp);

}

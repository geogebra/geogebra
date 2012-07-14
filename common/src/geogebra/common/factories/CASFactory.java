package geogebra.common.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.Kernel;

public abstract class CASFactory {
	public static CASFactory prototype;
	public abstract CASmpreduce newMPReduce(CASparser p,CasParserTools t,Kernel kernel);
}

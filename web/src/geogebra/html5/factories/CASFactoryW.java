package geogebra.html5.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.factories.CASFactory;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;
import geogebra.html5.cas.giac.CASgiacW;

public class CASFactoryW extends CASFactory {

	@Override
	public CASmpreduce newMPReduce(CASparser p, CasParserTools t,Kernel kernel) {
		throw new IllegalArgumentException();
	}

	@Override
    public CASGenericInterface newGiac(CASparser p, CasParserTools t, Kernel kernel) {
		return new CASgiacW(p, t, kernel);
    }

}

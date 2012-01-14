package geogebra.web.cas.mpreduce;

import geogebra.common.cas.CASException;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;
import geogebra.common.kernel.arithmetic.ValidExpression;

public class CASmpreduce extends AbstractCASmpreduce {


	public CASmpreduce(CASparser casParser, CasParserTools t) {
		super(casParser);
	    // TODO Auto-generated constructor stub
    }

	@Override
	public String evaluateMPReduce(String exp) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String evaluateGeoGebraCAS(ValidExpression casInput)
	        throws CASException {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void unbindVariable(String var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		// TODO Auto-generated method stub

	}

}

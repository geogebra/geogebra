package geogebra.common.kernel.cas;

import java.util.ArrayList;

import geogebra.common.kernel.arithmetic.AbstractCommand;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.AbstractApplication.CasType;


public interface GeoGebraCasInterface {

	public StringType getCurrentCASstringType();

	public String evaluateRaw(String geoStr) throws Throwable;

    public CASGenericInterface getCurrentCAS();

	public String evaluateGeoGebraCAS(ValidExpression evalVE);

	public CASParserInterface getCASparser();

	public boolean isStructurallyEqual(ValidExpression inputVE, String newInput);
	
	public void setCurrentCAS(CasType c);		

	public boolean isCommandAvailable(AbstractCommand cmd);

	public String[] getPolynomialCoeffs(String exp, String variable);

	public String evaluateGeoGebraCAS(String exp);

	public void unbindVariable(String addCASVariablePrefix);

	public void setSignificantFiguresForNumeric(int figures);

	public String getCASCommand(String name, ArrayList<?> args,
			boolean symbolic);

	public CasType getCurrentCASType();
}

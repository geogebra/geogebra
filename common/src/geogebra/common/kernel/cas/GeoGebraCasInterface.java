package geogebra.common.kernel.cas;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.AbstractApplication.CasType;

import java.util.ArrayList;


public interface GeoGebraCasInterface {

	public StringType getCurrentCASstringType();

	public String evaluateRaw(String geoStr) throws Throwable;

    public CASGenericInterface getCurrentCAS();

	public String evaluateGeoGebraCAS(ValidExpression evalVE,MyArbitraryConstant tpl);

	public CASParserInterface getCASparser();

	public boolean isStructurallyEqual(ValidExpression inputVE, String newInput);
	
	public void setCurrentCAS(CasType c);		

	public boolean isCommandAvailable(Command cmd);

	public String[] getPolynomialCoeffs(String exp, String variable);

	public String evaluateGeoGebraCAS(String exp,MyArbitraryConstant cons);

	public void unbindVariable(String addCASVariablePrefix);

	public void setSignificantFiguresForNumeric(int figures);

	public String getCASCommand(String name, ArrayList<ExpressionNode> args,
			boolean symbolic,StringTemplate tpl);

	public CasType getCurrentCASType();

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c);
}

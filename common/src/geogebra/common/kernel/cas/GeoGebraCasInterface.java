package geogebra.common.kernel.cas;

import geogebra.common.cas.CASException;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App.CasType;

import java.util.ArrayList;
import java.util.Set;


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

	public String evaluateGeoGebraCAS(String exp,MyArbitraryConstant cons) throws CASException;
	
	public String evaluateGeoGebraCAS(ValidExpression exp,MyArbitraryConstant cons,StringTemplate tpl) throws CASException;

	public void unbindVariable(String addCASVariablePrefix);

	public void setSignificantFiguresForNumeric(int figures);

	public String getCASCommand(String name, ArrayList<ExpressionNode> args,
			boolean symbolic,StringTemplate tpl);

	public CasType getCurrentCASType();

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c);
	
	String toAssignment(GeoElement geoElement,StringTemplate tpl);

	public Set<String> getAvailableCommandNames();
	
	
}

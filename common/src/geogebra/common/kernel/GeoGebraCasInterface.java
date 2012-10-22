package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.CasType;

import java.util.ArrayList;
import java.util.Set;

public interface GeoGebraCasInterface {

	public StringType getCurrentCASstringType();

	public String evaluateRaw(String geoStr) throws Throwable;

	public CASGenericInterface getCurrentCAS();

	public String evaluateGeoGebraCAS(ValidExpression evalVE,
			MyArbitraryConstant tpl);

	public CASParserInterface getCASparser();

	public boolean isStructurallyEqual(ValidExpression inputVE, String newInput);

	public void setCurrentCAS(final CasType c);

	public boolean isCommandAvailable(final Command cmd);

	public String[] getPolynomialCoeffs(final String exp, final String variable);

	public String evaluateGeoGebraCAS(String exp, MyArbitraryConstant cons)
			throws CASException;

	public String evaluateGeoGebraCAS(ValidExpression exp,
			MyArbitraryConstant cons, StringTemplate tpl) throws CASException;

	public void unbindVariable(final String addCASVariablePrefix);

	public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, final boolean symbolic,
			StringTemplate tpl);

	public CasType getCurrentCASType();

	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c);

	String toAssignment(final GeoElement geoElement, final StringTemplate tpl);

	public Set<String> getAvailableCommandNames();

}

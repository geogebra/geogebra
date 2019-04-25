package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Symbolic geo for CAS computations in AV
 * 
 * @author Zbynek
 */
public class GeoSymbolic extends GeoElement implements GeoSymbolicI, VarString {
	private ExpressionValue value;
	private ArrayList<FunctionVariable> fVars = new ArrayList<>();
	private String casOutputString;
	private GeoElement twinGeo;
	private boolean twinUpToDate = false;

	/**
	 * @return output expression
	 */
	public ExpressionValue getValue() {
		return value;
	}

	/**
	 * @param value
	 *            output expression
	 */
	public void setValue(ExpressionValue value) {
		this.value = value;
	}

	/**
	 * @param c
	 *            construction
	 */
	public GeoSymbolic(Construction c) {
		super(c);
		this.setEuclidianVisible(false);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.UNKNOWN;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SYMBOLIC;
	}

	@Override
	public GeoElement copy() {
		GeoSymbolic copy = new GeoSymbolic(cons);
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		reuseDefinition(geo);
		fVars.clear();
		if (geo instanceof GeoSymbolic) {
			fVars.addAll(((GeoSymbolic) geo).fVars);
			value = ((GeoSymbolic) geo).getValue();
		}
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (value != null) {
			return value.toValueString(tpl);
		}
		return getDefinition().toValueString(tpl);
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		return geo == this;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.NONE;
	}

	@Override
	public void setError(String key) {
		// TODO deal with errors on parsing
	}

	@Override
	public void setAssignmentType(AssignmentType assignmentType) {
		// compatibility with CAS view, do nothing
	}

	@Override
	public void computeOutput() {
		ExpressionValue casInputArg = getDefinition().deepCopy(kernel)
				.traverse(FunctionExpander.getCollector());
		Command casInput;
		if (casInputArg.unwrap() instanceof Command) {
			// don't wrap commands in additional Evaluate
			casInput = (Command) casInputArg.unwrap();
		} else {
			casInput = new Command(kernel, "Evaluate", false);
			casInput.addArgument(casInputArg.wrap());
		}

		String s = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(casInput.wrap(),
				new MyArbitraryConstant(this), StringTemplate.prefixedDefault,
				null, kernel);
		this.casOutputString = s;
		ExpressionValue casOutput = kernel.getGeoGebraCAS().parseOutput(s, this,
				kernel);
		setValue(casOutput);
		twinUpToDate = false;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (value == null) {
			return "?";
		}
		StringBuilder sb = new StringBuilder();
		appendAssignmentLHS(sb, tpl);
		sb.append(" = ");
		sb.append(value.toString(tpl));
		return sb.toString();
	}

	@Override
	public String getAssignmentLHS(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		appendAssignmentLHS(sb, tpl);
		return sb.toString();
	}

	private void appendAssignmentLHS(StringBuilder sb, StringTemplate tpl) {
		sb.append(getLabelSimple());
		if (!fVars.isEmpty()) {
			sb.append(tpl.leftBracket());
			appendVarString(sb, tpl);
			sb.append(tpl.rightBracket());
		}
	}

	private StringBuilder appendVarString(StringBuilder sb,
			final StringTemplate tpl) {
		for (int i = 0; i < fVars.size() - 1; i++) {
			sb.append(fVars.get(i).toString(tpl));
			sb.append(", ");
		}
		sb.append(fVars.get(fVars.size() - 1).toString(tpl));
		return sb;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	/**
	 * @param functionVariables
	 *            function variables
	 */
	public void setVariables(FunctionVariable[] functionVariables) {
		fVars.clear();
		for (FunctionVariable fv : functionVariables) {
			fVars.add(fv.deepCopy(kernel));
		}
	}

	/**
	 * @return function variables
	 */
	@Override
	public FunctionVariable[] getFunctionVariables() {
		return fVars.toArray(new FunctionVariable[0]);
	}

	/**
	 * @return geo for drawing
	 */
	public GeoElementND getTwinGeo() {
		if (twinUpToDate) {
			return twinGeo;
		}
		GeoElementND newTwin = casOutputString == null ? null
				: kernel.getAlgebraProcessor()
						.evaluateToGeoElement(this.casOutputString, false);
		if (twinGeo != null && newTwin != null) {
			twinGeo.set(newTwin);
			newTwin.setVisualStyle(twinGeo);
		} else {
			twinGeo = newTwin == null ? null : newTwin.toGeoElement();
		}
		twinUpToDate = true;
		return newTwin;
	}

	@Override
	public char getLabelDelimiter() {
		return getDefinition().unwrap() instanceof Equation ? ':' : '=';
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fVars.size() - 1; i++) {
			sb.append(fVars.get(i).toString(tpl));
			sb.append(", ");
		}
		sb.append(fVars.get(fVars.size() - 1).toString(tpl));
		return sb.toString();
	}

	@Override
	protected String getDefaultLabel(char[] chars2) {
		GeoElement twinGeo = (GeoElement) getTwinGeo();
		if (twinGeo == null) {
			return super.getDefaultLabel(chars2);
		}
		return twinGeo.getDefaultLabel(chars2);
	}
}

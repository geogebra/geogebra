package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Symbolic geo for CAS computations in AV
 * 
 * @author Zbynek
 */
public class GeoSymbolic extends GeoElement implements GeoSymbolicI {
	private ExpressionValue value;
	private ArrayList<FunctionVariable> fVars = new ArrayList<>();

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
		return false;
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
				.traverse(new Traversing() {
					@Override
					public ExpressionValue process(ExpressionValue ev) {
						if (ev instanceof GeoSymbolic) {
							return ((GeoSymbolic) ev).getDefinition();
						}
						return ev;
					}
				});
		Command casInput = new Command(kernel, "Evaluate", false);
		casInput.addArgument(casInputArg.wrap());
		String s = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(casInput.wrap(),
				new MyArbitraryConstant(this), StringTemplate.prefixedDefault,
				null, kernel);
		ExpressionValue casOutput = kernel.getGeoGebraCAS().parseOutput(s, this,
				kernel);
		setValue(casOutput);
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

}

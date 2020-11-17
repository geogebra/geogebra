package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Operation;

/**
 * Fake expression value for storing the result type in expression node
 *
 */
public class Resolution implements ExpressionValue {

	private ValueType type;
	private int listDepth = 0;

	/**
	 * @param listDepth
	 *            depth of nested lists
	 */
	public void setListDepth(int listDepth) {
		this.listDepth = listDepth;
	}

	/**
	 * @param type
	 *            result type
	 */
	public void setType(ValueType type) {
		this.type = type;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNumberValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return type == ValueType.NONCOMPLEX2D;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return type == ValueType.VECTOR3D;
	}

	@Override
	public boolean evaluatesToList() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getListDepth() {
		return listDepth;
	}

	@Override
	public boolean evaluatesToText() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExpressionNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGeoElement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInTree() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInTree(boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(ExpressionValue ev) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExpressionValue deepCopy(Kernel kernel) {
		return this;
	}

	@Override
	public double evaluateDouble() {
		return 0;
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return "";
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// has no variables
	}

	@Override
	public String toString(StringTemplate tpl) {
		return "";
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return false;
	}

	@Override
	public ExpressionValue unwrap() {
		return null;
	}

	@Override
	public ExpressionNode wrap() {
		return null;
	}

	@Override
	public boolean hasCoords() {
		return false;
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel) {
		return null;
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel) {
		return null;
	}

	@Override
	public boolean evaluatesToNumber(boolean def) {
		return false;
	}

	@Override
	public String getAssignmentOperator() {
		return ":";
	}

	@Override
	public ValueType getValueType() {
		return type;
	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel) {
		Resolution res = new Resolution();
		res.listDepth = this.listDepth;
		res.type = this.type;
		return res;
	}

	@Override
	public ExpressionValue toValidExpression() {
		return this;
	}

	@Override
	public boolean evaluatesToNDVector() {
		// TODO Auto-generated method stub
		return evaluatesTo3DVector() || evaluatesToNonComplex2DVector();
	}

	@Override
	public boolean isOperation(Operation derivative) {
		return false;
	}
}

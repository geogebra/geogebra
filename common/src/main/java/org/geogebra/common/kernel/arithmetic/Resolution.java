package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Fake expression value for storing the result type in expression node
 *
 */
public class Resolution implements ExpressionValue {

	private ValueType type;
	private int listDepth = 0;

	/**
	 * New resolution
	 */
	public Resolution() {

	}

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

	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNumberValue() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean evaluatesToNonComplex2DVector() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean evaluatesToVectorNotPoint() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean evaluatesTo3DVector() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean evaluatesToList() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getListDepth() {
		return listDepth;
	}

	public boolean evaluatesToText() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isExpressionNode() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGeoElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInTree() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setInTree(boolean flag) {
		// TODO Auto-generated method stub

	}

	public boolean contains(ExpressionValue ev) {
		// TODO Auto-generated method stub
		return false;
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		return this;
	}

	public double evaluateDouble() {
		return 0;
	}

	public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	public HashSet<GeoElement> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return "";
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return "";
	}

	public void resolveVariables() {
		// has no variables
	}

	public String toString(StringTemplate tpl) {
		return "";
	}

	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return "";
	}

	public ExpressionValue traverse(Traversing t) {
		return this;
	}

	public boolean inspect(Inspecting t) {
		return false;
	}

	public ExpressionValue unwrap() {
		return null;
	}

	public ExpressionNode wrap() {
		return null;
	}

	public boolean hasCoords() {
		return false;
	}

	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel) {
		return null;
	}

	public ExpressionValue integral(FunctionVariable fv, Kernel kernel) {
		return null;
	}

	public boolean evaluatesToNumber(boolean def) {
		return false;
	}

	public String getAssignmentOperator() {
		return ":";
	}

	public ValueType getValueType() {
		return type;
	}

}

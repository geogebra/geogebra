/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.MaxSizeHashMap;
import geogebra.util.MyMath;

import java.util.HashSet;
import java.util.Set;

/**
 * Function of N variables that returns either a number or a boolean. This
 * depends on the expression this function is based on.
 * 
 * @author Markus Hohenwarter + mathieu
 */
public class FunctionNVar extends ValidExpression implements ReplaceableValue,
		FunctionalNVar {

	/** function expression */
	protected ExpressionNode expression;
	/** function variables */
	protected FunctionVariable[] fVars;
	private IneqTree ineqs;

	/** standard case: number function, see initFunction() */
	protected boolean isBooleanFunction = false;

	/** if the function is of type f(x) = c */
	protected boolean isConstantFunction = false;

	/** kernel */
	protected Kernel kernel;

	private StringBuilder sb = new StringBuilder(80);

	/**
	 * Tree containing inequalities (possibly with NOT) in leaves and AND or OR
	 * operations in other nodes
	 * 
	 * @author Zbynek Konecny
	 * 
	 */
	public class IneqTree {
		private IneqTree left, right;
		private Inequality ineq;
		private int operation = ExpressionNode.NO_OPERATION;

		/**
		 * @param right
		 *            the right to set
		 */
		public void setRight(IneqTree right) {
			this.right = right;
		}

		/**
		 * @return the right
		 */
		public IneqTree getRight() {
			return right;
		}

		/**
		 * @param left
		 *            the left to set
		 */
		public void setLeft(IneqTree left) {
			this.left = left;
		}

		/**
		 * @return the left
		 */
		public IneqTree getLeft() {
			return left;
		}

		/**
		 * @param operation
		 *            the operation to set
		 */
		public void setOperation(int operation) {
			this.operation = operation;
		}

		/**
		 * @return the operation
		 */
		public int getOperation() {
			return operation;
		}

		/**
		 * @param ineq
		 *            the ineq to set
		 */
		public void setIneq(Inequality ineq) {
			this.ineq = ineq;
		}

		/**
		 * @return the ineq
		 */
		public Inequality getIneq() {
			return ineq;
		}

		public boolean updateCoef() {
			if (ineq != null) {
				ineq.updateCoef();
				//Application.debug(ineq.getType());
				return ineq.getType() != Inequality.INEQUALITY_INVALID;
			}
			if (left == null && right == null)
				return false;
			boolean b = true;
			if (left != null)
				b &= left.updateCoef();
			if (right != null)
				b &= right.updateCoef();
			//Application.debug("tree" + b);
			return b;
		}

		private int size;

		public int getSize() {
			return size;
		}

		public Inequality get(int i) {
			if (ineq != null)
				return ineq;
			if (i < left.getSize())
				return left.get(i);
			return right.get(i - left.getSize());
		}

		public void recomputeSize() {
			if (ineq != null) {
				size = 1;
			} else
				size = 0;
			if (left != null) {
				left.recomputeSize();
				size += left.size;
			}
			if (right != null) {
				right.recomputeSize();
				size += right.size;
			}

		}

		public double[] getZeros(Set<Double> zeros) {
			if (ineq != null) {
				GeoPoint[] zeroPoints = ineq.getZeros();
				for (int i = 0; i < zeroPoints.length; i++) {
					zeros.add(zeroPoints[i].getX());
				}
			}
			if (left != null) {
				left.getZeros(zeros);
			}
			if (right != null) {
				right.getZeros(zeros);
			}
			return null;
		}
	}

	/**
	 * Creates new Function from expression. Note: call initFunction() after
	 * this constructor.
	 * 
	 * @param expression
	 */
	public FunctionNVar(ExpressionNode expression) {
		kernel = expression.getKernel();

		this.expression = expression;
	}

	/**
	 * Creates new Function from expression where the function variables in
	 * expression is already known.
	 * 
	 * @param exp
	 * @param fVars
	 */
	public FunctionNVar(ExpressionNode exp, FunctionVariable[] fVars) {
		kernel = exp.getKernel();

		expression = exp;
		this.fVars = fVars;
	}

	/**
	 * Creates a Function that has no expression yet. Use setExpression() to do
	 * this later.
	 * 
	 * @param kernel
	 *            kernel
	 */
	public FunctionNVar(Kernel kernel) {
		this.kernel = kernel;

	}

	/**
	 * copy constructor
	 * 
	 * @param f
	 *            source function
	 * @param kernel
	 */
	public FunctionNVar(FunctionNVar f, Kernel kernel) {
		expression = f.expression.getCopy(kernel);
		fVars = f.fVars; // no deep copy of function variable
		isBooleanFunction = f.isBooleanFunction;
		isConstantFunction = f.isConstantFunction;

		this.kernel = kernel;
	}

	/**
	 * Determine whether var is function variable of this function
	 * 
	 * @param var
	 * @return true if var is function variable of this function
	 */
	public boolean isFunctionVariable(String var) {
		if (fVars == null)
			return false;
		else {
			for (int i = 0; i < fVars.length; i++)
				if (fVars[i].toString().equals(var))
					return true;
			return false; // if none of function vars equals var
		}
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		return new FunctionNVar(this, kernel);
	}

	/**
	 * @return function expression
	 */
	final public ExpressionNode getExpression() {
		return expression;
	}

	public void resolveVariables() {
		expression.resolveVariables();
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 */
	public void replaceChildrenByValues(GeoElement geo) {
		if (expression != null) {
			expression.replaceChildrenByValues(geo);
		}
	}

	/**
	 * Use this method only if you really know what you are doing.
	 * 
	 * @param exp
	 */
	public void setExpression(ExpressionNode exp) {
		expression = exp;
	}

	/**
	 * Use this method only if you really know what you are doing.
	 * 
	 * @param exp
	 * @param vars
	 */
	public void setExpression(ExpressionNode exp, FunctionVariable[] vars) {
		expression = exp;
		fVars = vars;
	}

	public FunctionNVar getFunction() {
		return this;
	}

	/**
	 * Returns array of all variables
	 * 
	 * @return array of variables
	 */
	public FunctionVariable[] getFunctionVariables() {
		return fVars;
	}

	/**
	 * Returns name of i-th variable
	 * 
	 * @param i
	 * @return name of i-th variable
	 */
	final public String getVarString(int i) {
		return fVars[i].toString();
	}

	/**
	 * Number of arguments of this function, e.g. 2 for f(x,y)
	 * 
	 * @return number of variables
	 */
	final public int getVarNumber() {
		return fVars.length;
	}

	/**
	 * Returns variable names separated by ", "
	 * 
	 * @return variable names separated by ", "
	 */
	public String getVarString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fVars.length - 1; i++) {
			sb.append(fVars[i].toString());
			sb.append(", ");
		}
		sb.append(fVars[fVars.length - 1].toString());
		return sb.toString();
	}

	/**
	 * Call this function to resolve variables and init the function. May throw
	 * MyError (InvalidFunction).
	 */
	public void initFunction() {
		// replace function variables in tree
		for (int i = 0; i < fVars.length; i++) {
			FunctionVariable fVar = fVars[i];

			// look for Variable objects with name of function variable and
			// replace them
			int replacements = expression.replaceVariables(fVar.getSetVarString(), fVar);
			isConstantFunction = isConstantFunction && replacements == 0;

			if (replacements == 0) {
				// x, y got polynomials while parsing
				replacements = expression.replacePolynomials(fVar);
				isConstantFunction = isConstantFunction && replacements == 0;
			}
		}

		// replace variable names by objects
		expression.resolveVariables();

		// the idea here was to allow something like: Derivative[f] + 3x
		// but wrapping the GeoFunction objects as ExpressionNodes of type
		// FUNCTION
		// leads to Derivative[f](x) + 3x
		// expression.wrapGeoFunctionsAsExpressionNode();

		// replace all polynomials in expression (they are all equal to "1x" if
		// we got this far)
		// by an instance of MyDouble

		// simplify constant parts in expression
		expression.simplifyConstantIntegers();
		
		// evaluate expression to find out about the type of function
		ExpressionValue ev;
		try {
			ev = expression.evaluate();
		} 
		catch (MyError err) {
			// Evaluation failed: DESPERATE MODE
			try {
				// try to fix structure of expression and then try evaluation again
				fixStructure();
				ev = expression.evaluate();
			} catch (Throwable th) {
				// throw original error when desperate mode failed
				throw err;
			}
		}

		// initialize type as boolean or numeric function
		initType(ev);
	}
	
	/**
	 * Tries to fix a structural problem leading to an evaluation error, e.g.
	 * x(x+1) is interpreted as xcoord(x+1). This can be fixed by changing the structure
	 * to x*(x+1) for example.
	 */
	private void fixStructure() {
		// get function variables for x, y, z
		FunctionVariable xVar = null, yVar = null, zVar = null;
		for (FunctionVariable fVar : fVars) {
			if ("x".equals(fVar.toString())) xVar = fVar;
			else if ("y".equals(fVar.toString())) yVar = fVar;
			else if ("z".equals(fVar.toString())) zVar = fVar;
		}
		
		// try to replace x(x+1) by x*(x+1)
		expression.replaceXYZnodes(xVar, yVar, zVar);		
	}

	private void initType(ExpressionValue ev) {
		if (ev.isBooleanValue()) {
			isBooleanFunction = true;
		} else if (ev.isNumberValue()) {
			isBooleanFunction = false;		
		} else if (ev instanceof FunctionNVar){
				expression = ((FunctionNVar)ev).getExpression();
				fVars = ((FunctionNVar)ev).getFunctionVariables();	
		}else{
			Application.debug("InvalidFunction:" + expression.toString() + " "
					+ ev.toString() + ev.getClass().getName());
			throw new MyError(kernel.getApplication(), "InvalidFunction");
		}
	}

	/**
	 * Returns whether this function always evaluates to BooleanValue.
	 */
	final public boolean isBooleanFunction() {
		return isBooleanFunction;
	}

	/**
	 * Returns whether this function always evaluates to the same numerical
	 * value, i.e. it is of the form f(x1,...,xn) = c.
	 * 
	 * @return true iff constant
	 */
	final public boolean isConstantFunction() {
		if (isConstantFunction)
			return true;
		for (int i = 0; i < fVars.length; i++)
			if (expression.contains(fVars[i]))
				return false;
		return true; // none of the vars appears in the expression
	}

	public boolean isConstant() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public ExpressionValue evaluate() {
		return this;
	}

	/**
	 * Returns this function's value at position.
	 * 
	 * @param vals
	 * @return f(vals)
	 */
	final public double evaluate(double[] vals) {
		if (isBooleanFunction) {
			// BooleanValue
			return evaluateBoolean(vals) ? 1 : 0;
		} else {
			// NumberValue
			for (int i = 0; i < fVars.length; i++) {
				// Application.debug(fVars[i].toString()+" <= "+vals[i]);
				fVars[i].set(vals[i]);
			}
			return ((NumberValue) expression.evaluate()).getDouble();
		}
	}

	/**
	 * Returns this function's value at position vals. (Note: use this method if
	 * isBooleanFunction() returns true.
	 * 
	 * @param vals
	 * @return f(vals)
	 */
	final public boolean evaluateBoolean(double[] vals) {
		for (int i = 0; i < fVars.length; i++)
			fVars[i].set(vals[i]);
		return ((BooleanValue) expression.evaluate()).getBoolean();
	}

	public HashSet<GeoElement> getVariables() {
		return expression.getVariables();
	}

	public GeoElement[] getGeoElementVariables() {
		return expression.getGeoElementVariables();
	}

	public String toString() {
		return expression.toString();
	}

	final public String toValueString() {
		return expression.toValueString();
	}

	final public String toOutputValueString() {
		return expression.toOutputValueString();
	}

	final public String toLaTeXString(boolean symbolic) {
		return expression.toLaTeXString(symbolic);
	}
	
	

	/* ***************
	 * CAS Stuff **************
	 */

	/**
	 * Evaluates this function using the given CAS command. Caching is used for
	 * symbolic CAS evaluations.
	 * 
	 * @param ggbCasCmd
	 *            the GeoGebraCAS command needs to include % in all places where
	 *            the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param symbolic
	 *            true for symbolic evaluation, false to use values of
	 *            GeoElement variables
	 * @return resulting function
	 */
	final public FunctionNVar evalCasCommand(String ggbCasCmd, boolean symbolic) {
		// remember expression and its CAS string
		boolean useCaching = true;

		// make sure to use temporary variable names 
		// e.g. a in Derivative[a*x^2,x] needs to be renamed temporarily when a exists in GeoGebra
		// see http://www.geogebra.org/trac/ticket/929
		boolean oldTempVariableValue = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		
		// did expression change since last time?
		if (casEvalExpression != expression) {
			casEvalExpression = expression;
			if (symbolic) {
				casEvalStringSymbolic = expression.getCASstring(ExpressionNode.STRING_TYPE_GEOGEBRA, true);
			}

			// caching should only be done if the expression doesn't contain
			// other functions
			// e.g. this is important for f(x) = x^2, g(x,y) = f(x) + y,
			// Derivative(g(x,y), x)
			// where we cannot cache the derivative of g because g may have
			// changed
			useCaching = symbolic
					&& !expression
							.containsObjectType(CasEvaluableFunction.class);
		}

		// build command string for CAS
		String expString = symbolic ? 
				casEvalStringSymbolic : 
				expression.getCASstring(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
		
		// set back kernel
		kernel.setUseTempVariablePrefix(oldTempVariableValue);

		// substitute % by expString in ggbCasCmd
		String casString = ggbCasCmd.replaceAll("%", expString);
		FunctionNVar resultFun = null;		

		// eval with CAS
		try {
			if (useCaching) {
				// check if result is in cache
				resultFun = lookupCasEvalMap(casString);
				if (resultFun != null) {
					//System.out.println("caching worked: " + casString + " -> " + resultFun);
					return resultFun;
				}					
			}
			
			// evaluate expression by CAS
			String result = symbolic ?
					kernel.evaluateGeoGebraCAS(casString) :  // symbolic
					kernel.evaluateCachedGeoGebraCAS(casString); // value string
			//System.out.println("evaluateGeoGebraCAS: " + casString + " -> " + result);

			// parse CAS result back into GeoGebra
			sb.setLength(0);
			sb.append("f("); // this name is never used, just needed for parsing
			sb.append(getVarString());
			sb.append(") = ");
			sb.append(result);

			// parse result
			if (getVarNumber() == 1) {
				resultFun = kernel.getParser().parseFunction(sb.toString());
			} else {
				resultFun = kernel.getParser().parseFunctionNVar(sb.toString());
			}

			resultFun.initFunction();			
		} catch (Error err) {
			err.printStackTrace();
			resultFun = null;
		} catch (Exception e) {
			e.printStackTrace();
			resultFun = null;
		} catch (Throwable e) {
			resultFun = null;
		}

		// cache result
		if (useCaching && resultFun != null) {
			getCasEvalMap().put(casString, resultFun);
		}
			

		// System.out.println("NO caching: " + casString + " -> " + resultFun);

		return resultFun;
	}

	private ExpressionNode casEvalExpression;
	private String casEvalStringSymbolic;

	private MaxSizeHashMap<String, FunctionNVar> getCasEvalMap() {
		if (casEvalMap == null) {
			casEvalMap = new MaxSizeHashMap<String, FunctionNVar>(MAX_CAS_EVAL_MAP_SIZE);
		}
		return casEvalMap;
	}
	
	private FunctionNVar lookupCasEvalMap(String casString) {
		if (casEvalMap == null) 
			return null;
		else
			return casEvalMap.get(casString);
	}

//	/**
//	 * Clears those entries in the function cache which contain this label
//	 * or clear everything if the label is null (called by clearConstruction)
//	 * @param label
//	 */
//	public void clearCasEvalMap(String label) {
//		if (casEvalMap == null) return;
//		
//		if (label == null) {
//			casEvalMap.clear();
//		} else {
//			Set<String> keyset = getCasEvalMap().keySet();
//			Iterator<String> it = keyset.iterator();
//			String actual = null;
//			while (it.hasNext()) {
//				actual = it.next();
//				if (actual.indexOf(label) != -1)
//					casEvalMap.remove(actual);
//			}
//		}
//	}

	private final static int MAX_CAS_EVAL_MAP_SIZE = 100;
	private MaxSizeHashMap<String, FunctionNVar> casEvalMap;

	/**
	 * MathPiper may return something like Deriv(x) f(x). This method converts
	 * such expressions into Derivative[f(x), x]
	 * 
	 * @param order
	 * @return
	 */
	private String handleDeriv(String casResult) {
		if (casResult.indexOf("Deriv[") < 0)
			return casResult;

		StringBuilder sb = new StringBuilder();
		try {
			// look for "Deriv[x] f(x,y)" strings and
			// replace them with "Derivative[f(x,y), x]"
			String[] derivs = casResult.split("Deriv\\[");
			sb.append(derivs[0]); // part before first "Deriv(x)"
			for (int i = 1; i < derivs.length; i++) {
				// we now have something like "x) f(x, y) ..."
				// get variable part "x" before first closing )
				int pos1 = derivs[i].indexOf(']');
				String var = derivs[i].substring(0, pos1);

				// get function part "f(x,y)" before second closing )
				pos1 += 1;
				int pos2 = derivs[i].indexOf(')', pos1) + 1;
				String funPart = derivs[i].substring(pos1, pos2);

				sb.append("Derivative[");
				sb.append(funPart);
				sb.append(",");
				sb.append(var);
				sb.append("] ");
				sb.append(derivs[i].substring(pos2));
			}
		} catch (Exception e) {
			// TODO : remove
			e.printStackTrace();
			return casResult;
		}
		return sb.toString();
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isBooleanValue() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		return false;
	}

	public String getLabelForAssignment() {
		StringBuilder sb = new StringBuilder();
		// function, e.g. f(x) := 2*x
		sb.append(getLabel());
		sb.append("(");
		sb.append(getVarString());
		sb.append(")");
		return sb.toString();
	}

	public IneqTree getIneqs() {
		return ineqs;
	}

	/**
	 * initializes inequalities
	 * 
	 * @param fe expression node
	 * @param functional
	 *            function to which ineqs are associated
	 * @return true if the functions consists of inequalities
	 */
	public boolean initIneqs(ExpressionNode fe, FunctionalNVar functional) {
		if (ineqs == null || fe == getExpression())
			ineqs = new IneqTree();
		boolean b = initIneqs(fe, functional, ineqs,false);
		ineqs.recomputeSize();
		return b;
	}

	private boolean initIneqs(ExpressionNode fe, FunctionalNVar functional,
			IneqTree tree, boolean negate) {
		int op = fe.getOperation();
		ExpressionNode leftTree = fe.getLeftTree();
		ExpressionNode rightTree = fe.getRightTree();
		if (op == ExpressionNode.GREATER || op == ExpressionNode.GREATER_EQUAL
				|| op == ExpressionNode.LESS || op == ExpressionNode.LESS_EQUAL) {
			Inequality newIneq = new Inequality(kernel, leftTree, rightTree,
					adjustOp(op,negate), getFunction().getFunctionVariables(), functional);
			if (newIneq.getType() != Inequality.INEQUALITY_INVALID) {
				if (newIneq.getType() != Inequality.INEQUALITY_1VAR_X
						&& newIneq.getType() != Inequality.INEQUALITY_1VAR_Y)
					newIneq.getBorder().setInverseFill(newIneq.isAboveBorder());
				tree.setIneq(newIneq);
			}
			return newIneq.getType() != Inequality.INEQUALITY_INVALID;
		} else if (op == ExpressionNode.AND || op == ExpressionNode.OR
				|| op == ExpressionNode.EQUAL_BOOLEAN
				|| op == ExpressionNode.NOT_EQUAL) {
			tree.operation = adjustOp(op,negate);
			tree.left = new IneqTree();
			tree.right = new IneqTree();
			return initIneqs(leftTree, functional, tree.left,negate)
					&& initIneqs(rightTree, functional, tree.right,negate);
		} else if (op == ExpressionNode.NOT) {
			return initIneqs(leftTree, functional, tree,!negate);
		}else if (op == ExpressionNode.FUNCTION_NVAR) {
			FunctionalNVar nv = (FunctionalNVar)leftTree.getLeft();
			IneqTree otherTree = nv.getIneqs();
			if(otherTree == null || otherTree.getSize()==0){
				return false;
			}
			tree.left = otherTree.left;
			tree.right = otherTree.right;
			tree.operation =otherTree.operation;
			tree.ineq =otherTree.ineq;
			return true;
		} 
		else
			return false;

	}

	private int adjustOp(int op, boolean negate) {
		if(negate==false)
			return op;
		switch(op){
			case ExpressionNode.AND: return ExpressionNode.OR;
			case ExpressionNode.OR: return ExpressionNode.AND;
			case ExpressionNode.GREATER_EQUAL: return ExpressionNode.LESS;
			case ExpressionNode.GREATER: return ExpressionNode.LESS_EQUAL;
			case ExpressionNode.LESS_EQUAL: return ExpressionNode.GREATER;
			case ExpressionNode.LESS: return ExpressionNode.GREATER_EQUAL;
			case ExpressionNode.EQUAL_BOOLEAN: return ExpressionNode.NOT_EQUAL;
			case ExpressionNode.NOT_EQUAL: return ExpressionNode.EQUAL_BOOLEAN;
		}
		return -1;
	}

	/**
	 * updates list of inequalities
	 * @return true iff all inequalities are drawable
	 */
	public boolean updateIneqs() {
		if (ineqs == null)
			return false;
		return ineqs.updateCoef();
	}

	/**
	 * Evaluates function at given point
	 * 
	 * @param pt
	 * @return function value
	 */
	public double evaluate(GeoPoint pt) {
		if (fVars.length == 1 && "y".equals(fVars[0].toString()))
			return evaluate(new double[] { pt.y / pt.z });
		return evaluate(new double[] { pt.x / pt.z, pt.y / pt.z });
	}

	/**
	 * Evaluates function at given point as boolean
	 * 
	 * @param pt
	 * @return function value
	 */
	public boolean evaluateBoolean(GeoPoint pt) {
		if (fVars.length == 1 && "y".equals(fVars[0].toString()))
			return evaluateBoolean(new double[] { pt.y / pt.z });
		return evaluateBoolean(new double[] { pt.x / pt.z, pt.y / pt.z });
	}

	public void translate(double vx, double vy) {

		// translate x
		if (!Kernel.isZero(vx)) {
			translateX(expression, vx, 0);

		}
		if (!Kernel.isZero(vy)) {
			translateX(expression, vy, 1);

		}

		// make sure that expression object is changed!
		// this is needed to know that the expression has changed
		if (expression.isLeaf() && expression.getLeft().isExpressionNode()) {
			expression = new ExpressionNode((ExpressionNode) expression
					.getLeft());
		} else {
			expression = new ExpressionNode(expression);
		}
	}

	// replace every x in tree by (x - vx)
	// i.e. replace fVar with (fvar - vx)
	private void translateX(ExpressionNode en, double vx, int varNo) {
		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();

		// left tree
		if (left == fVars[varNo]) {
			try { // is there a constant number to the right?
				MyDouble num = (MyDouble) right;
				double temp;
				switch (en.getOperation()) {
				case ExpressionNode.PLUS:
					temp = num.getDouble() - vx;
					if (Kernel.isZero(temp)) {
						expression = expression.replaceAndWrap(en, fVars[varNo]);
					} else if (temp < 0) {
						en.setOperation(ExpressionNode.MINUS);
						num.set(-temp);
					} else {
						num.set(temp);
					}
					return;

				case ExpressionNode.MINUS:
					temp = num.getDouble() + vx;
					if (Kernel.isZero(temp)) {
						expression = expression.replaceAndWrap(en, fVars[varNo]);
					} else if (temp < 0) {
						en.setOperation(ExpressionNode.PLUS);
						num.set(-temp);
					} else {
						num.set(temp);
					}
					return;

				default:
					en.setLeft(shiftXnode(vx, varNo));
				}
			} catch (Exception e) {
				en.setLeft(shiftXnode(vx, varNo));
			}
		} else if (left instanceof ExpressionNode) {
			translateX((ExpressionNode) left, vx, varNo);
		}

		// right tree
		if (right == fVars[varNo]) {
			en.setRight(shiftXnode(vx, varNo));
		} else if (right instanceof ExpressionNode) {
			translateX((ExpressionNode) right, vx, varNo);
		}
	}

	// node for (x - vx)
	private ExpressionNode shiftXnode(double vx, int varNo) {
		ExpressionNode node;
		if (vx > 0) {
			node = new ExpressionNode(kernel, fVars[varNo],
					ExpressionNode.MINUS, new MyDouble(kernel, vx));
		} else {
			node = new ExpressionNode(kernel, fVars[varNo],
					ExpressionNode.PLUS, new MyDouble(kernel, -vx));
		}
		return node;
	}

	public void matrixTransform(double a00, double a01, double a10, double a11) {
		ExpressionNode dummy = new ExpressionNode();
		expression.replaceAndWrap(fVars[0], dummy);
		MyDouble ma00 = new MyDouble(kernel, a00);
		MyDouble ma01 = new MyDouble(kernel, a01);
		MyDouble ma10 = new MyDouble(kernel, a10);
		MyDouble ma11 = new MyDouble(kernel, a11);

		ExpressionNode newX = new ExpressionNode(kernel, ma00,
				ExpressionNode.MULTIPLY, fVars[0]).plus(new ExpressionNode(
				kernel, ma01, ExpressionNode.MULTIPLY, fVars[1]));
		ExpressionNode newY = new ExpressionNode(kernel, ma10,
				ExpressionNode.MULTIPLY, fVars[0]).plus(new ExpressionNode(
				kernel, ma11, ExpressionNode.MULTIPLY, fVars[1]));
		expression = expression.replaceAndWrap(fVars[1], newY);
		expression = expression.replaceAndWrap(dummy, newX);
		this.initIneqs(expression, this);
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		ExpressionNode dummy = new ExpressionNode();
		expression = expression.replaceAndWrap(fVars[0], dummy);
		double[][] b = MyMath.adjoint(a00, a01, a02, a10, a11, a12, a20, a21,
				a22);
		MyDouble[][] mbTrans = new MyDouble[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mbTrans[i][j] = new MyDouble(kernel, b[j][i]);
		ExpressionNode newZ = new ExpressionNode(kernel, mbTrans[2][0],
				ExpressionNode.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[2][1], ExpressionNode.MULTIPLY,
						fVars[1])).plus(mbTrans[2][2]);
		ExpressionNode newX = new ExpressionNode(kernel, mbTrans[0][0],
				ExpressionNode.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[0][1], ExpressionNode.MULTIPLY,
						fVars[1])).plus(mbTrans[0][2]);
		ExpressionNode newY = new ExpressionNode(kernel, mbTrans[1][0],
				ExpressionNode.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[1][1], ExpressionNode.MULTIPLY,
						fVars[1])).plus(mbTrans[1][2]);				
		expression = expression.replaceAndWrap(fVars[1], newY.divide(newZ));
		expression = expression.replaceAndWrap(dummy, newX.divide(newZ));
		this.initIneqs(expression, this);
	}
	
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
		expression = expression.replaceAndWrap(oldOb, newOb);
        return this;
    }

}

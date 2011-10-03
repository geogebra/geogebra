/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * ExpressionNode.java
 *
 * binary tree node for ExpressionValues (NumberValues, VectorValues)
 *
 * Created on 03. Oktober 2001, 09:37
 */

package geogebra.kernel.arithmetic;

import geogebra.gui.DynamicTextInputPane;
import geogebra.gui.TextInputDialog;
import geogebra.kernel.GeoDummyVariable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Unicode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.Document;

/**
 * Tree node for expressions like "3*a - b/5"
 * 
 * @author Markus
 * @version
 */
public class ExpressionNode extends ValidExpression implements ReplaceableValue,
		ExpressionNodeConstants {

	public Application app;
	public Kernel kernel;
	public ExpressionValue left, right;
	public int operation = NO_OPERATION;
	public boolean forceVector = false, forcePoint = false,
			forceFunction = false;

	public boolean holdsLaTeXtext = false;

	// for leaf mode
	public boolean leaf = false;

	public ExpressionNode() {
	};

	/** Creates new ExpressionNode */
	public ExpressionNode(Kernel kernel, ExpressionValue left, int operation,
			ExpressionValue right) {
		this.kernel = kernel;
		app = kernel.getApplication();

		this.operation = operation;
		setLeft(left);
		if (right != null) {
			setRight(right);
		} else { // set dummy value
			setRight(new MyDouble(kernel, Double.NaN));
		}		
	}

	/** for only one leaf */
	// for wrapping ExpressionValues as ValidExpression
	public ExpressionNode(Kernel kernel, ExpressionValue leaf) {
		this.kernel = kernel;
		app = kernel.getApplication();

		setLeft(leaf);
		this.leaf = true;		
	}

	// copy constructor: NO deep copy of subtrees is done here!
	// this is needed for translation of functions
	public ExpressionNode(ExpressionNode node) {
		kernel = node.kernel;
		app = node.app;

		leaf = node.leaf;
		operation = node.operation;
		setLeft(node.left);
		setRight(node.right);		
	}

	public Kernel getKernel() {
		return kernel;
	}

	final public int getOperation() {
		return operation;
	}

	public void setOperation(int op) {
		operation = op;
	}

	public void setHoldsLaTeXtext(boolean flag) {
		holdsLaTeXtext = flag;
	}

	final public ExpressionValue getLeft() {
		return left;
	}

	final public void setLeft(ExpressionValue l) {
		left = l;
		left.setInTree(true); // needed fot list operations eg k=2 then k {1,2}
	}

	public ExpressionNode getLeftTree() {
		if (left.isExpressionNode())
			return (ExpressionNode) left;
		else
			return new ExpressionNode(kernel, left);
	}

	final public ExpressionValue getRight() {
		return right;
	}

	final public void setRight(ExpressionValue r) {
		right = r;
		if (right != null)
			right.setInTree(true); // needed for list operations eg k=2 then k {1,2}
		leaf = operation == NO_OPERATION; // right is a dummy MyDouble by
											// default
	}

	public ExpressionNode getRightTree() {
		if (right == null)
			return null;

		if (right.isExpressionNode())
			return (ExpressionNode) right;
		else
			return new ExpressionNode(kernel, right);
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		return getCopy(kernel);
	}

	/** copy the whole tree structure except leafs */
	public ExpressionNode getCopy(Kernel kernel) {
		// Application.debug("getCopy() input: " + this);
		ExpressionNode newNode = null;
		ExpressionValue lev = null, rev = null;

		if (left != null)
			lev = copy(left, kernel);
		if (right != null)
			rev = copy(right, kernel);

		if (lev != null) {
			newNode = new ExpressionNode(kernel, lev, operation, rev);
			newNode.leaf = leaf;
		} else
			// something went wrong
			return null;

		// set member vars that are not set by constructors
		newNode.forceVector = forceVector;
		newNode.forcePoint = forcePoint;
		newNode.forceFunction = forceFunction;
		// Application.debug("getCopy() output: " + newNode);
		return newNode;
	}

	/** deep copy except for GeoElements */
	public static ExpressionValue copy(ExpressionValue ev, Kernel kernel) {
		if (ev == null)
			return null;

		ExpressionValue ret = null;
		// Application.debug("copy ExpressionValue input: " + ev);
		if (ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;
			ret = en.getCopy(kernel);
		}
		// deep copy
		else if (ev.isPolynomialInstance() || ev.isConstant()
				|| ev instanceof Command) {
			ret = ev.deepCopy(kernel);
		} else {
			ret = ev;
		}
		// Application.debug("copy ExpressionValue output: " + ev);
		return ret;
	}

	/**
	 * Replaces all ExpressionNodes in tree that are leafs (=wrappers) by their
	 * leaf objects (of type ExpressionValue).
	 */
	final public void simplifyLeafs() {
		if (left.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) left;
			if (node.leaf) {
				left = node.left;
				simplifyLeafs();
			}
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				ExpressionNode node = (ExpressionNode) right;
				if (node.leaf) {
					right = node.left;
					simplifyLeafs();
				}
			}
		}
	}

	/**
	 * Replaces all Command objects in tree by their evaluated GeoElement
	 * objects.
	 */
	final private void simplifyAndEvalCommands() {
		// don't evaluate any commands for the CAS here
		if (kernel.isResolveUnkownVarsAsDummyGeos())
			return;

		if (left.isExpressionNode()) {
			((ExpressionNode) left).simplifyAndEvalCommands();
		} else if (left instanceof Command) {
			left = ((Command) left).evaluate();
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).simplifyAndEvalCommands();
			} else if (right instanceof Command) {
				right = ((Command) right).evaluate();
			}
		}
	}

	/**
	 * Replaces all constant parts in tree by their values
	 */
	final public void simplifyConstantIntegers() {
		if (left.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) left;
			if (left.isConstant()) {
				ExpressionValue eval = node.evaluate();
				if (eval.isNumberValue()) {
					// we only simplify numbers that have integer values
					if (kernel.isInteger(((NumberValue) eval).getDouble()))
						left = eval;
				} else {
					left = eval;
				}
			} else
				node.simplifyConstantIntegers();
		}

		if (right != null && right.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) right;
			if (right.isConstant()) {
				ExpressionValue eval = node.evaluate();
				if (eval.isNumberValue()) {
					// we only simplify numbers that have integer values
					if (kernel.isInteger(((NumberValue) eval).getDouble()))
						right = eval;
				} else {
					right = eval;
				}
			} else
				node.simplifyConstantIntegers();
		}
	}

	/*
	 * Markus: no longer needed as we have added rules in MathPiper to support
	 * this notation directly Expands equation expressions like (3*x + 2 = 5) /
	 * 2 to (3*x + 2)/2 = 5/2.
	 */
	// final public ExpressionValue expandEquationExpressions() {
	// if (leaf) return this;
	//
	// if (left.isExpressionNode()) {
	// left = ((ExpressionNode) left).expandEquationExpressions();
	// }
	// if (right.isExpressionNode()) {
	// right = ((ExpressionNode) right).expandEquationExpressions();
	// }
	//
	// switch (operation) {
	// case PLUS:
	// case MINUS:
	// case MULTIPLY:
	// case DIVIDE:
	// // equ <operation> val
	// if (left instanceof Equation) {
	// ((Equation) left).applyOperation(operation, right, false);
	// leaf = true;
	// right = null;
	// }
	// // val <operation> equ
	// else if (right instanceof Equation) {
	// ((Equation) right).applyOperation(operation, left, true);
	// left = right;
	// right = null;
	// leaf = true;
	// }
	// break;
	// }
	//
	// return this;
	// }

	// used for 3D
	/*
	 * protected ExpressionValue evaluate(ExpressionValue v){ return
	 * v.evaluate(); }
	 */

	/**
	 * interface ExpressionValue implementation
	 */

	public ExpressionValue evaluate() {
		return kernel.getExpressionNodeEvaluator().evaluate(this);
	}

	public ExpressionValue evaluate(boolean cache) {
		return kernel.getExpressionNodeEvaluator().evaluate(this);
	}

	/**
	 * 
	 * @param lt
	 * @param rt
	 * @return false if not defined
	 */
	private MyBoolean evalEquals(ExpressionValue lt, ExpressionValue rt) {
		// booleans
		if (lt.isBooleanValue() && rt.isBooleanValue())
			return new MyBoolean(kernel, 
					((BooleanValue) lt).getBoolean() == ((BooleanValue) rt)
							.getBoolean());

		// nummber == number
		else if (lt.isNumberValue() && rt.isNumberValue())
			return new MyBoolean(kernel, kernel.isEqual(((NumberValue) lt).getDouble(),
					((NumberValue) rt).getDouble()));

		// needed for eg If[""=="a",0,1]
		// when lt and rt are MyStringBuffers
		else if (lt.isTextValue() && rt.isTextValue()) {

			String strL = ((TextValue) lt).toValueString();
			String strR = ((TextValue) rt).toValueString();

			// needed for eg Sequence[If[Element[list1,i]=="b",0,1],i,i,i]
			if (strL == null || strR == null)
				return new MyBoolean(kernel, false);

			return new MyBoolean(kernel, strL.equals(strR));
		} else if (lt.isGeoElement() && rt.isGeoElement()) {
			GeoElement geo1 = (GeoElement) lt;
			GeoElement geo2 = (GeoElement) rt;

			return new MyBoolean(kernel, geo1.isEqual(geo2));
		} else if (lt.isVectorValue() && rt.isVectorValue()) {
			VectorValue vec1 = (VectorValue) lt;
			VectorValue vec2 = (VectorValue) rt;
			return new MyBoolean(kernel, vec1.getVector().equals(vec2.getVector()));
		}

		/*
		 * // Michael Borcherds 2008-05-01 // replaced following code with one
		 * line:
		 * 
		 * if (geo1.isGeoPoint() && geo2.isGeoPoint()) { return new
		 * MyBoolean(((GeoPoint)geo1).equals((GeoPoint) geo2)); } else if
		 * (geo1.isGeoLine() && geo2.isGeoLine()) { return new
		 * MyBoolean(((GeoLine)geo1).equals((GeoLine) geo2)); } else if
		 * (geo1.isGeoConic() && geo2.isGeoConic()) { return new
		 * MyBoolean(((GeoConic)geo1).equals((GeoConic) geo2)); } else if
		 * (geo1.isGeoVector() && geo2.isGeoVector()) { return new
		 * MyBoolean(((GeoVector)geo1).equals((GeoVector) geo2)); } else if
		 * (geo1.isGeoList() && geo2.isGeoList()) { // Michael Borcherds
		 * 2008-04-12 return new MyBoolean(kernel, ((GeoList)geo1).equals((GeoList)
		 * geo2)); }
		 */

		return new MyBoolean(kernel, false);
	}

	/**
	 * look for Variable objects in the tree and replace them by their resolved
	 * GeoElement
	 */
	public void resolveVariables() {
		doResolveVariables();
		simplifyAndEvalCommands();
		simplifyLeafs();

		//left instanceof NumberValue needed rather than left.isNumberValue() as left can be an
		//ExpressionNode, eg Normal[0,1,x]
		switch (operation) {
		case POWER: // eg e^x
			if (left instanceof NumberValue && ((NumberValue)left).getDouble() == Math.E) {
				GeoElement geo = kernel.lookupLabel("e");
				if (geo != null && geo.needsReplacingInExpressionNode()) {

					// replace e^x with exp(x)
					// if e was autocreated
					operation = EXP;
					left = right;
					kernel.getConstruction().removeLabel(geo);
				}
			}
			break;
		case MULTIPLY: // eg 1 * e or e * 1
		case DIVIDE: // eg 1 / e or e / 1
		case PLUS: // eg 1 + e or e + 1
		case MINUS: // eg 1 - e or e - 1
			if (left instanceof NumberValue && ((NumberValue)left).getDouble() == Math.E) {
				GeoElement geo = kernel.lookupLabel("e");
				if (geo != null && geo.needsReplacingInExpressionNode()) {

					// replace 'e' with exp(1) 
					// if e was autocreated
					left = new ExpressionNode(kernel, new MyDouble(kernel, 1.0), EXP, null);
					kernel.getConstruction().removeLabel(geo);
				}
			} else if (right instanceof NumberValue && ((NumberValue)right).getDouble() == Math.E) {
				GeoElement geo = kernel.lookupLabel("e");
				if (geo != null && geo.needsReplacingInExpressionNode()) {

					// replace 'e' with exp(1) 
					// if e was autocreated
					right = new ExpressionNode(kernel, new MyDouble(kernel, 1.0), EXP, null);
					kernel.getConstruction().removeLabel(geo);
				}
			}
			break;
		}
	}

	private void doResolveVariables() {
		// resolve left wing
		if (left.isVariable()) {
			left = ((Variable) left).resolveAsExpressionValue();
		} else
			left.resolveVariables();

		// resolve right wing
		if (right != null) {
			if (right.isVariable()) {
				right = ((Variable) right).resolveAsExpressionValue();
			} else
				right.resolveVariables();
		}
	}

	/**
	 * Looks for GeoDummyVariable objects that hold String var in the tree and replaces
	 * them by their newOb.
	 * @return whether replacement was done
	 */
	public boolean replaceGeoDummyVariables(String var, ExpressionValue newOb) {
		boolean didReplacement = false;
		
		// left wing
		if (left instanceof GeoDummyVariable) {
			if (var.equals(((GeoDummyVariable) left).toString())) {
				left = newOb;
				didReplacement = true;
			}
		}
		else if (left instanceof Command) {
			didReplacement = ((Command) left).replaceGeoDummyVariables(var, newOb);
		}
		else if (left instanceof Equation) {
			didReplacement = ((Equation) left).replaceGeoDummyVariables(var, newOb);
		}
		else if (left.isExpressionNode()) {
			didReplacement = ((ExpressionNode) left).replaceGeoDummyVariables(var, newOb);
		} 
		
		// right wing
		if (right != null) {
			if (right instanceof GeoDummyVariable) {
				if (var.equals(((GeoDummyVariable) right).toString())) {
					right = newOb;
					didReplacement = true;
				}
			} 
			else if (right instanceof Command) {
				didReplacement = ((Command) right).replaceGeoDummyVariables(var, newOb);
			}
			else if (right instanceof Equation) {
				didReplacement = ((Equation) right).replaceGeoDummyVariables(var, newOb);
			}
			else if (right.isExpressionNode()) {
				didReplacement = ((ExpressionNode) right).replaceGeoDummyVariables(var, newOb) || didReplacement;
			}
		}
		
		return didReplacement;
	}


	/**
	 * look for GeoFunction objects in the tree and replace them by FUNCTION
	 * ExpressionNodes. This makes operations like f + g possible by changing
	 * this to f(x) + g(x)
	 * 
	 * public void wrapGeoFunctionsAsExpressionNode() { Polynomial polyX = new
	 * Polynomial(kernel, "x");
	 * 
	 * // left wing if (left.isExpressionNode()) {
	 * ((ExpressionNode)left).wrapGeoFunctionsAsExpressionNode(); } else if
	 * (left instanceof GeoFunction) { left = new ExpressionNode(kernel, left,
	 * ExpressionNode.FUNCTION, polyX); }
	 * 
	 * // resolve right wing if (right != null) { if (right.isExpressionNode())
	 * { ((ExpressionNode)right).wrapGeoFunctionsAsExpressionNode(); } else if
	 * (right instanceof GeoFunction) { right = new ExpressionNode(kernel,
	 * right, ExpressionNode.FUNCTION, polyX); } } }
	 */

	/**
	 * returns true if there is at least one Polynomial in the tree
	 */
	public boolean includesPolynomial() {
		return getPolynomialVars().size() > 0;
	}

	/**
	 * Returns all polynomial variables (x, y, and/or z) in this tree as a list.
	 * 
	 * @return list with all variables as Strings
	 */
	public TreeSet<String> getPolynomialVars() {
		TreeSet<String> vars = new TreeSet<String>();
		getPolynomialVars(vars);
		return vars;
	}

	/**
	 * Adds all polynomial variables (x, y, and/or z) in this tree to vars.
	 * 
	 * @param vars
	 *            the set to add all variables as Strings
	 */
	private void getPolynomialVars(TreeSet<String> vars) {
		if (left.isExpressionNode()) {
			((ExpressionNode) left).getPolynomialVars(vars);
		} else if (left.isPolynomialInstance()) {
			vars.add(left.toString());
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).getPolynomialVars(vars);
			} else if (right.isPolynomialInstance()) {
				vars.add(right.toString());
			// changed to avoid deepcopy in MyList.getMyList() eg Sequence[f(Element[GP,k]),k,1,NP]
			//} else if (right.isListValue()){ //to get polynomial vars in GeoFunctionNVar
			//MyList list=((ListValue)right).getMyList();
			} else if (right instanceof MyList){ //to get polynomial vars in GeoFunctionNVar
				MyList list = (MyList)right;
				for (int i=0;i<list.size();i++){
					ExpressionValue elem=list.getListElement(i);
					if (elem.isExpressionNode()) {
						((ExpressionNode) elem).getPolynomialVars(vars);
					} else if (elem.isPolynomialInstance()) {
						vars.add(elem.toString());
					}
				}
			}
		}
	}

	/**
	 * Returns whether this ExpressionNode should evaluate to a GeoVector. This
	 * method returns true when all GeoElements in this tree are GeoVectors and
	 * there are no other constanct VectorValues (i.e. constant points)
	 */
	public boolean shouldEvaluateToGeoVector() {
		boolean evalToVector = false;

		if (left.isExpressionNode()) {
			evalToVector = (((ExpressionNode) left).shouldEvaluateToGeoVector());
		} else if (left.isGeoElement()) {
			GeoElement geo = (GeoElement) left;
			evalToVector = geo.isGeoVector() || geo.isNumberValue();
		} else if (left.isNumberValue()) {
			evalToVector = true;
		}

		if (right != null && evalToVector) {
			if (right.isExpressionNode()) {
				evalToVector = ((ExpressionNode) right)
						.shouldEvaluateToGeoVector();
			} else if (right.isGeoElement()) {
				GeoElement geo = (GeoElement) right;
				evalToVector = geo.isGeoVector() || geo.isNumberValue();
			} else if (right.isNumberValue()) {
				evalToVector = true;
			}
		}

		return evalToVector;
	}

	/**
	 * Returns true if this tree includes a division by val
	 */
	final public boolean includesDivisionBy(ExpressionValue val) {
		if (operation == DIVIDE) {
			if (right.contains(val))
				return true;

			if (left.isExpressionNode()
					&& ((ExpressionNode) left).includesDivisionBy(val))
				return true;
		} else {
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).includesDivisionBy(val))
				return true;

			if (right != null && right.isExpressionNode()
					&& ((ExpressionNode) right).includesDivisionBy(val))
				return true;
		}

		return false;
	}

	/**
	 * Replaces all Variable objects with the given varName in tree by the given
	 * FunctionVariable object.
	 * 
	 * @return number of replacements done
	 */
	public final int replaceVariables(String varName, FunctionVariable fVar) {
		int replacements = 0;

		// left tree
		if (left.isExpressionNode()) {
			replacements += ((ExpressionNode) left).replaceVariables(varName,
					fVar);
		} else if (left instanceof MyList) {
			replacements += ((MyList) left).replaceVariables(varName, fVar);
		} else if (left instanceof Variable) {
			if (varName.equals(((Variable) left).getName())) {
				left = fVar;
				replacements++;
			}
		}
		if (left instanceof GeoDummyVariable) {
			if (varName.equals(((GeoDummyVariable) left).toString())) {
				left = fVar;
				replacements++;
			}
		}  
		else if (left instanceof FunctionVariable) {
			if (varName.equals(((FunctionVariable) left).toString())) {
				left = fVar;
				replacements++;
			}	
		}

		// right tree
		if (right != null) {
			if (right.isExpressionNode()) {
				replacements += ((ExpressionNode) right).replaceVariables(
						varName, fVar);
			} else if (right instanceof MyList) {
				replacements += ((MyList) right)
						.replaceVariables(varName, fVar);
			} else if (right instanceof Variable) {
				if (varName.equals(((Variable) right).getName())) {
					right = fVar;
					replacements++;
				}
			}  
			else if (right instanceof GeoDummyVariable) {
				if (varName.equals(((GeoDummyVariable) right).toString())) {
					right = fVar;
					replacements++;
				}
			}  
			else if (right instanceof FunctionVariable) {
				if (varName.equals(((FunctionVariable) right).toString())) {
					right = fVar;
					replacements++;
				}
			}
		}

		return replacements;
	}

	/**
	 * Replaces all Polynomials in tree by function variable
	 * 
	 * @return number of replacements done
	 */
	int replacePolynomials(FunctionVariable x) {
		int replacements = 0;

		// left tree
		if (left.isExpressionNode()) {
			replacements += ((ExpressionNode) left).replacePolynomials(x);
		} else if (left instanceof MyList) {
			replacements += ((MyList) left).replacePolynomials(x);
		} else if (left.isPolynomialInstance()
				&& x.toString().equals(left.toString())) {
			left = x;
			replacements++;
		}

		// right tree
		if (right != null) {
			if (right.isExpressionNode()) {
				replacements += ((ExpressionNode) right).replacePolynomials(x);
			} else if (right instanceof MyList) {
				replacements += ((MyList) right).replacePolynomials(x);
			} else if (right.isPolynomialInstance()
					&& x.toString().equals(right.toString())) {
				right = x;
				replacements++;
			}
		}

		return replacements;
	}
	
	/**
	 * Replaces all XCOORD, YCOORD, ZCOORD nodes by mutliplication nodes,
	 * e.g. x(x+1) becomes x*(x+1). The given function variables for "x", "y", "z" are
	 * used in this process.
	 * 
	 * @return number of replacements done
	 */
	int replaceXYZnodes(FunctionVariable xVar, FunctionVariable yVar, FunctionVariable zVar) {				
		if (xVar == null && yVar == null & zVar == null) return 0;
		
		// left tree
		int replacements = 0;
		if (left.isExpressionNode()) {
			replacements += ((ExpressionNode) left).replaceXYZnodes(xVar, yVar, zVar);
		} 
		// right tree
		if (right != null && right.isExpressionNode()) {
			replacements += ((ExpressionNode) right).replaceXYZnodes(xVar, yVar, zVar);
		}
		
		switch (operation) {
			case XCOORD:
				if (xVar != null) {
					replacements++;
					operation = MULTIPLY;
					right = left;
					left = xVar;
				}
				break;
				
			case YCOORD:
				if (yVar != null) {
					replacements++;
					operation = MULTIPLY;
					right = left;
					left = yVar;
				}
				break;
				
			case ZCOORD:
				if (zVar != null) {
					replacements++;
					operation = MULTIPLY;
					right = left;
					left = zVar;
				}
				break;
		}

		return replacements;
	}

	/**
	 * Replaces every oldOb by newOb in this ExpressionNode tree and
	 * makes sure that the result is again an ExpressionNode object.
	 * 
	 * @return resulting ExpressionNode
	 */
	public ExpressionNode replaceAndWrap(ExpressionValue oldOb, ExpressionValue newOb) {
		ExpressionValue ev = replace(oldOb, newOb);
		
		// replace root by new object
		if (ev.isExpressionNode()){
			return (ExpressionNode) ev;
		}
		else
			return new ExpressionNode(kernel, ev);	
	}
	
	/**
	 * Replaces every oldOb by newOb in expression.
	 * @return resulting expression
	 */
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
		if (this == oldOb) {
			return newOb;
		}
		
		// left tree
		if (left == oldOb) {
			left = newOb;
		} 			
		else if (left instanceof ReplaceableValue) {
			left = ((ReplaceableValue) left).replace(oldOb, newOb);			
		}

		// right tree
		if (right == oldOb) {
			right = newOb;
		} 			
		else if (right instanceof ReplaceableValue) {
			right = ((ReplaceableValue) right).replace(oldOb, newOb);			
		}		
		return this;
	}

	/**
	 * Replaces geo and all its dependent geos in this tree by copies of their
	 * values.
	 */
	public void replaceChildrenByValues(GeoElement geo) {
		// left tree
		if (left.isGeoElement()) {
			GeoElement treeGeo = (GeoElement) left;
			if (left == geo || treeGeo.isChildOf(geo)) {
				left = treeGeo.copyInternal(treeGeo.getConstruction());
			}
		} else if (left.isExpressionNode()) {
			((ExpressionNode) left).replaceChildrenByValues(geo);
		}
		// handle command arguments
		else if (left instanceof Command) {
			((Command) left).replaceChildrenByValues(geo);
		}

		// right tree
		if (right != null) {
			if (right.isGeoElement()) {
				GeoElement treeGeo = (GeoElement) right;
				if (right == geo || treeGeo.isChildOf(geo)) {
					right = treeGeo.copyInternal(treeGeo.getConstruction());
				}
			} else if (right.isExpressionNode()) {
				((ExpressionNode) right).replaceChildrenByValues(geo);
			}
			// handle command arguments
			else if (right instanceof Command) {
				((Command) right).replaceChildrenByValues(geo);
			}
		}
	}

	/**
	 * Returns true when the given object is found in this expression tree.
	 */
	final public boolean contains(ExpressionValue ev) {
		if (leaf)
			return left.contains(ev);
		else
			return left.contains(ev) || right.contains(ev);
	}

	/**
	 * Returns true when the given object is found in this expression tree.
	 */
	final public boolean containsObjectType(Class type) {
		if (type.isInstance(left) || type.isInstance(right))
			return true;

		if (left instanceof ExpressionNode
				&& ((ExpressionNode) left).containsObjectType(type)) {
			return true;
		}
		if (right instanceof ExpressionNode
				&& ((ExpressionNode) right).containsObjectType(type)) {
			return true;
		}

		return false;
	}

	/**
	 * transfers every non-polynomial in this tree to a polynomial. This is
	 * needed to enable polynomial simplification by evaluate()
	 */
	final void makePolynomialTree(Equation equ) {
		
		if (operation==ExpressionNodeConstants.FUNCTION_NVAR){
			if (left instanceof FunctionalNVar && right instanceof MyList){
				MyList list=((MyList)right); 
				FunctionNVar func=((FunctionalNVar)left).getFunction();
				ExpressionNode expr=func.getExpression().getCopy(kernel);
				if (func.getFunctionVariables().length==list.size()){
					for (int i=0;i<list.size();i++){
						ExpressionValue ev=list.getListElement(i);
						if (ev instanceof ExpressionNode){
							ExpressionNode en=((ExpressionNode)ev).getCopy(kernel);
							if (!equ.isFunctionDependent()){
								equ.setFunctionDependent(en.includesPolynomial());
							}
							en.makePolynomialTree(equ);
							ev=en;
						}else if (list.getListElement(i).isPolynomialInstance()){
							equ.setFunctionDependent(true);
						}
						expr=expr.replaceAndWrap(func.getFunctionVariables()[i], ev);
					}
				}else{
					throw new MyError(app,new String[]{"IllegalArgumentNumber"});
				}
				expr.makePolynomialTree(equ);
				left=expr.left;
				right=expr.right;
				operation=expr.getOperation();
			}
		}else if (operation==ExpressionNodeConstants.FUNCTION){
			if (left instanceof GeoFunction){
				Function func=((Functional)left).getFunction();
				ExpressionNode expr=func.getExpression().getCopy(kernel);
				if (right instanceof ExpressionNode){
					if (!equ.isFunctionDependent()){
						equ.setFunctionDependent(((ExpressionNode)right).includesPolynomial());
					}
					((ExpressionNode)right).makePolynomialTree(equ);
				}else if (right.isPolynomialInstance()){
					equ.setFunctionDependent(true);
				}
				expr=expr.replaceAndWrap(func.getFunctionVariable(),right);
				expr.makePolynomialTree(equ);
				left=expr.left;
				right=expr.right;
				operation=expr.getOperation();
			}
		}
		// transfer left subtree
		if (left.isExpressionNode()) {
			((ExpressionNode) left).makePolynomialTree(equ);
		} else if (!(left.isPolynomialInstance())) {
			left = new Polynomial(kernel, new Term(kernel, left, ""));
		}

		// transfer right subtree
		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).makePolynomialTree(equ);
			} else if (!(right.isPolynomialInstance())) {
				right = new Polynomial(kernel, new Term(kernel, right, ""));
			}
		}
	}

	/**
	 * returns true, if there are no variable objects in the subtree
	 */
	final public boolean isConstant() {
		if (isLeaf())
			return left.isConstant();
		else
			return left.isConstant() && right.isConstant();
	}

	
	/**
	 * returns true, if no variable is a point (GeoPoint)
	 */
	final public boolean isVectorValue() {
		if (forcePoint)
			return false;
		if (forceVector)
			return true;

		return shouldEvaluateToGeoVector();
	}

	public void setForceVector() {
		// this expression should be considered as a vector, not a point
		forceVector = true;
	}

	final public boolean isForcedVector() {
		return forceVector;
	}

	public void setForcePoint() {
		// this expression should be considered as a point, not a vector
		forcePoint = true;
	}

	final public boolean isForcedPoint() {
		return forcePoint;
	}

	public void setForceFunction() {
		// this expression should be considered as a point, not a vector
		forceFunction = true;
	}

	final public boolean isForcedFunction() {
		return forceFunction;
	}

	/**
	 * Returns whether this tree has any operations
	 */
	final public boolean hasOperations() {
		if (leaf) {
			if (left.isExpressionNode()) {
				((ExpressionNode) left).hasOperations();
			} else
				return false;
		}

		return (right != null);
	}

	/**
	 * Returns all GeoElement objects in the subtree
	 */
	final public HashSet<GeoElement> getVariables() {
		if (leaf)
			return left.getVariables();

		HashSet<GeoElement> leftVars = left.getVariables();
		HashSet<GeoElement> rightVars = right.getVariables();
		if (leftVars == null) {
			return rightVars;
		} else if (rightVars == null) {
			return leftVars;
		} else {
			leftVars.addAll(rightVars);
			return leftVars;
		}
	}

	public void addCommands(Set commands) {
		if (left instanceof Command) {
			((Command) left).addCommands(commands);
		} else if (left instanceof ExpressionNode) {
			((ExpressionNode) left).addCommands(commands);
		}

		if (right instanceof Command) {
			((Command) right).addCommands(commands);
		} else if (right instanceof ExpressionNode) {
			((ExpressionNode) right).addCommands(commands);
		}
	}

	final public GeoElement[] getGeoElementVariables() {
		HashSet varset = getVariables();
		if (varset == null)
			return null;
		Iterator i = varset.iterator();
		GeoElement[] ret = new GeoElement[varset.size()];
		int j = 0;
		while (i.hasNext()) {
			ret[j++] = (GeoElement) i.next();
		}
		return ret;
	}
	
	


	final public boolean isLeaf() {
		return leaf; // || operation == NO_OPERATION;
	}

	final public boolean isSingleGeoElement() {
		return leaf && left.isGeoElement();
	}

	final public GeoElement getSingleGeoElement() {
		return (GeoElement) left;
	}

	public boolean isSingleVariable() {
		return (isLeaf() && (left instanceof Variable));
	}
	
	public boolean isImaginaryUnit() {
		return (isLeaf() 
				&& (left instanceof GeoVec2D) 
				&& ((GeoVec2D) left).isImaginaryUnit());
	}

	/**
	 * Returns a string representation of this node that can be used with the
	 * given CAS, e.g. "*" and "^" are always printed.
	 * 
	 * @param symbolic
	 *            true for variable names, false for values of variables
	 * @param STRING_TYPE
	 *            e.g. ExpressionNode.STRING_TYPE_JASYMCA
	 * @return string representation of this node that can be used with given
	 *         CAS
	 */
	final public String getCASstring(int STRING_TYPE, boolean symbolic) {
		int oldPrintForm = kernel.getCASPrintForm();
		kernel.setCASPrintForm(STRING_TYPE);

		String ret = printCASstring(symbolic);

		kernel.setCASPrintForm(oldPrintForm);
		return ret;
	}

	/**
	 * Returns a string representation of this node that can be used with the
	 * GeoGebraCAS.
	 * 
	 * @return GeoGebra CAS string representation of this node
	 */
	final public String getCASstring(boolean symbolic) {
		return getCASstring(kernel.getCASPrintForm(), symbolic);
	}
	
	private boolean containsMyStringBuffer() {
				
		if (left instanceof MyStringBuffer || right instanceof MyStringBuffer) return true;
		
		boolean ret = false;
		
		if (left.isExpressionNode()) ret = ret || ((ExpressionNode)left).containsMyStringBuffer();
		if (right != null && right.isExpressionNode()) ret = ret || ((ExpressionNode)right).containsMyStringBuffer();

		return ret;
	
	}
	
	/*
	 * splits a string up for editing with the Text Tool
	 * adapted from printCASstring()
	 */
	public void splitString(DynamicTextInputPane dt,TextInputDialog id) {
		
		if (leaf) { 

			if (left.isGeoElement()) {
				Document d = dt.insertDynamicText(((GeoElement) left).getLabel(), -1, id);
				d.addDocumentListener(id);
			}
			else if (left.isExpressionNode())
				((ExpressionNode) left).splitString(dt, id);
			else if (left instanceof MyStringBuffer) {
				dt.insertString(-1, left.toString().replaceAll("\"", ""), null);				
			} else {
				dt.insertDynamicText(left.toString(), -1, id);
			}

		}

		// STANDARD case: no leaf
		else {
			
			if (right != null && !containsMyStringBuffer()) {
				// neither left nor right are free texts, eg a+3 in (a+3)+"hello"
				// so no splitting needed
				dt.insertDynamicText(toString(), -1, id);
				return;
			}
			
			
			// expression node
			if (left.isGeoElement()) {
				Document d = dt.insertDynamicText(((GeoElement) left).getLabel(), -1, id);
				d.addDocumentListener(id);
			}
			else if (left.isExpressionNode())
				((ExpressionNode) left).splitString(dt, id);
			else if (left instanceof MyStringBuffer) {
				dt.insertString(-1, left.toString().replaceAll("\"", ""), null);				
			} else {
				dt.insertDynamicText(left.toString(), -1, id);
			}

			if (right != null) {
				if (right.isGeoElement()) {
					Document d = dt.insertDynamicText(((GeoElement) right).getLabel(), -1, id);
					d.addDocumentListener(id);
				}
				else if (right.isExpressionNode())
					((ExpressionNode) right).splitString(dt, id);
				else if (right instanceof MyStringBuffer) {
					dt.insertString(-1, right.toString().replaceAll("\"", ""), null);				
				} else {
					dt.insertDynamicText(right.toString(), -1, id);
				}
			}
		}
		
	}

	private String printCASstring(boolean symbolic) {
		String ret = null;
		
		kernel.setTemporaryPrintFigures(15);
		try {

		if (leaf) { // leaf is GeoElement or not
			/*
			 * if (symbolic) { if (left.isGeoElement()) ret = ((GeoElement)
			 * left).getLabel(); else if (left.isExpressionNode()) ret =
			 * ((ExpressionNode)left).printJSCLString(symbolic); else ret =
			 * left.toString(); } else { ret = left.toValueString(); }
			 */

			if (symbolic && left.isGeoElement())
				ret = ((GeoElement) left).getLabel();
			else if (left.isExpressionNode())
				ret = ((ExpressionNode) left).printCASstring(symbolic);
			else
				ret = symbolic ? left.toString() : left.toValueString();
		}

		// STANDARD case: no leaf
		else {
			// expression node
			String leftStr = null, rightStr = null;
			if (symbolic && left.isGeoElement()) {
				leftStr = ((GeoElement) left).getLabel();
			} else if (left.isExpressionNode()) {
				leftStr = ((ExpressionNode) left).printCASstring(symbolic);
			} else {
				leftStr = symbolic ? left.toString() : left.toValueString();
			}

			if (right != null) {
				if (symbolic && right.isGeoElement()) {
					rightStr = ((GeoElement) right).getLabel();
				} else if (right.isExpressionNode()) {
					rightStr = ((ExpressionNode) right)
							.printCASstring(symbolic);
				} else {
					rightStr = symbolic ? right.toString() : right
							.toValueString();
				}
			}
			ret = operationToString(leftStr, rightStr, !symbolic);
		}
		}
		finally {
			kernel.restorePrintAccuracy();
		}
		
		return ret;
	}

	/**
	 * @return a representation of all classes present in the tree
	 */
	final public String getTreeClass() {
		return getTreeClass("");
	}

	/**
	 * @param prefix
	 * @return a representation of all classes present in the tree
	 */
	final private String getTreeClass(String prefix) {

		String ret = "";

		ret += "-\n";

		if (left != null) {
			ret += prefix + "  \\l:";
			if (left instanceof ExpressionNode)
				ret += ((ExpressionNode) left).getTreeClass(prefix + "   ");
			else
				ret += left.getClass();
			ret += "\n";
		}

		if (right != null) {
			ret += prefix + "  \\r:";
			if (right instanceof ExpressionNode)
				ret += ((ExpressionNode) right).getTreeClass(prefix + "   ");
			else
				ret += right.getClass();
			ret += "\n";
		}

		return ret;

	}

	/**
	 * Returns a string representation of this node.
	 */
	final public String toString() {
		if (leaf) { // leaf is GeoElement or not
			if (left.isGeoElement())
				return ((GeoElement) left).getLabel();
			else
				return left.toString();
		}

		// expression node
		String leftStr = null, rightStr = null;
		if (left.isGeoElement()) {
			leftStr = ((GeoElement) left).getLabel();
		} else {
			leftStr = left.toString();
		}

		if (right != null) {
			if (right.isGeoElement()) {
				rightStr = ((GeoElement) right).getLabel();
			} else {
				rightStr = right.toString();
			}
		}
		return operationToString(leftStr, rightStr, false);
	}

	/** like toString() but with current values of variables */
	final public String toValueString() {
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null)
				return left.toValueString();
		}

		// expression node
		String leftStr = left.toValueString();

		String rightStr = null;
		if (right != null) {
			rightStr = right.toValueString();
		}

		return operationToString(leftStr, rightStr, true);
	}

	final public String toOutputValueString() {
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null)
				return left.toOutputValueString();
		}

		// expression node
		String leftStr = left.toOutputValueString();

		String rightStr = null;
		if (right != null) {
			rightStr = right.toOutputValueString();
		}

		return operationToString(leftStr, rightStr, true);
	}

	/**
	 * Returns a string representation of this node in LaTeX syntax. Note: the
	 * resulting string may contain special unicode characters like greek
	 * characters or special signs for integer exponents. These sould be handled
	 * afterwards!
	 * 
	 * @param symbolic
	 *            true for variable names, false for values of variables
	 */
	final public String toLaTeXString(boolean symbolic) {
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null)
				return left.toLaTeXString(symbolic);
		}

		// expression node
		String leftStr = left.toLaTeXString(symbolic);
		String rightStr = null;
		if (right != null) {
			rightStr = right.toLaTeXString(symbolic);
			if ((operation == FUNCTION_NVAR || operation == ELEMENT_OF) &&
				(right instanceof MyList))
			{
				// 1 character will be taken from the left and right
				// of rightStr in operationToString, but more
				// is necessary in case of LaTeX, we do that here
				// " \\{ " is put by MyList 5 - 1(escape) -1(operationToString)
				rightStr = rightStr.substring(3, rightStr.length() - 3);
			}
		}

		// build latex string
		int oldPrintForm = kernel.getCASPrintForm();
		kernel.setCASPrintForm(STRING_TYPE_LATEX);
		String ret = operationToString(leftStr, rightStr, !symbolic);
		kernel.setCASPrintForm(oldPrintForm);

		return ret;
	}

	/**
	 * Returns a string representation of this node. Note: STRING_TYPE is used
	 * for LaTeX, MathPiper, Jasymca conform output, valueForm is used by
	 * toValueString(), forLaTeX is used for LaTeX output
	 * 
	 */
	final private String operationToString(String leftStr, String rightStr,
			boolean valueForm) {		
		
		ExpressionValue leftEval;
		StringBuilder sb = new StringBuilder();

		int STRING_TYPE = kernel.getCASPrintForm();

		switch (operation) {
		case NOT:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\neg ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Not ");
				break;
				
			default:
				sb.append(strNOT);
			}
			if (left.isLeaf()) {
				sb.append(leftStr);
			} else {
				sb.append(leftBracket(STRING_TYPE));
				sb.append(leftStr);
				sb.append(rightBracket(STRING_TYPE));
			}
			break;

		case OR:
			append(sb, leftStr, left, operation, STRING_TYPE);
			sb.append(' ');

			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\vee");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Or");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("or ");
				break;

			default:
				sb.append(strOR);
			}

			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case AND:
			append(sb, leftStr, left, operation, STRING_TYPE);

			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\wedge");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("And");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("and ");
				break;

			default:
				sb.append(strAND);
			}
			sb.append(' ');

			append(sb, rightStr, right, operation, STRING_TYPE);
			break;

		case EQUAL_BOOLEAN:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MPREDUCE:
				sb.append("=");
				break;

			default:
				sb.append(strEQUAL_BOOLEAN);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case NOT_EQUAL:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\neq");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("!=");
				break;
			
			case STRING_TYPE_MPREDUCE:
				sb.append("neq");
				break;

			default:
				sb.append(strNOT_EQUAL);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case IS_ELEMENT_OF:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\in");
				break;

			default:
				sb.append(strIS_ELEMENT_OF);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case IS_SUBSET_OF:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\subseteq");
				break;

			default:
				sb.append(strIS_SUBSET_OF);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case IS_SUBSET_OF_STRICT:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\subset");
				break;

			default:
				sb.append(strIS_SUBSET_OF_STRICT);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case SET_DIFFERENCE:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\setminus");
				break;

			default:
				sb.append(strSET_DIFFERENCE);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case LESS:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-< ");
			else sb.append(" < ");
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case GREATER:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\->");
			else sb.append(" > ");
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case LESS_EQUAL:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\leq");
				break;

			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MPREDUCE:
				sb.append("<=");
				break;

			default:
				sb.append(strLESS_EQUAL);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case GREATER_EQUAL:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\geq");
				break;

			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MPREDUCE:
				sb.append(">=");
				break;

			default:
				sb.append(strGREATER_EQUAL);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case PARALLEL:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\parallel");
				break;

			default:
				sb.append(strPARALLEL);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case PERPENDICULAR:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\perp");
				break;

			default:
				sb.append(strPERPENDICULAR);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case VECTORPRODUCT:
			append(sb, leftStr, left, operation, STRING_TYPE);
			// sb.append(leftStr);
			sb.append(' ');
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				if (kernel.isInsertLineBreaks()) sb.append("\\-");
				sb.append("\\times");
				break;

			default:
				sb.append(strVECTORPRODUCT);
			}
			sb.append(' ');
			append(sb, rightStr, right, operation, STRING_TYPE);
			// sb.append(rightStr);
			break;

		case PLUS:
			switch (STRING_TYPE) {
			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
				sb.append('(');
				sb.append(leftStr);
				sb.append(") + (");
				sb.append(rightStr);
				sb.append(')');
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("addition(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
				
			default:
				// check for 0
				if (valueForm) {
					if (isEqualString(left, 0, !valueForm)) {
						append(sb, rightStr, right, operation, STRING_TYPE);
						break;
					} else if (isEqualString(right, 0, !valueForm)) {
						append(sb, leftStr, left, operation, STRING_TYPE);
						break;
					}
				}

				if (left instanceof Equation) {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				} else {
					sb.append(leftStr);
				}

				// we need parantheses around right text
				// if right is not a leaf expression or
				// it is a leaf GeoElement without a label (i.e. it is
				// calculated somehow)
				if (left.isTextValue()
						&& (!right.isLeaf() || (right.isGeoElement() && !((GeoElement) right)
								.isLabelSet()))) {
					if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-+ ");
					else sb.append(" + ");
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
				} else {
					if (rightStr.charAt(0) == '-') { // convert + - to -
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-- ");
						else sb.append(" - ");
						sb.append(rightStr.substring(1));
					} else if (rightStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic convert + - to -
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-- ");
						else sb.append(" - ");
						sb.append(rightStr.substring(3));
					} else {
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-+ ");
						else sb.append(" + ");
						sb.append(rightStr);
					}
				}
				break;
			}
			break;

		case MINUS:
			switch (STRING_TYPE) {
			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
				sb.append('(');
				sb.append(leftStr);
				sb.append(") - (");
				sb.append(rightStr);
				sb.append(')');
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("subtraction(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
				
			default:
				if (left instanceof Equation) {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				} else {
					sb.append(leftStr);
				}

				// check for 0 at right
				if (valueForm && rightStr.equals(Application.unicodeZero+"")) {
					break;
				}

				if (right.isLeaf() || opID(right) >= MULTIPLY) { // not +, -
					if (rightStr.charAt(0) == '-') { // convert - - to +
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-+ ");
						else sb.append(" + ");
						sb.append(rightStr.substring(1));
					} else if (rightStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic convert - - to +
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-+ ");
						else sb.append(" + ");
						sb.append(rightStr.substring(3));
					} else {
						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-- ");
						// fix for changing height in Algebra View plus / minus
						else sb.append(" - ");
						sb.append(rightStr);
					}
				} else {
					// fix for changing height in Algebra View plus / minus
					if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append(" \\-- ");
					else sb.append(" - ");
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
				}
				break;
			}
			break;

		case MULTIPLY:
			switch (STRING_TYPE) {

			default:
				// check for 1 at left
				if (isEqualString(left, 1, !valueForm)) {
					append(sb, rightStr, right, operation, STRING_TYPE);
					break;
				}
				// check for 1 at right
				else if (isEqualString(right, 1, !valueForm)) {
					append(sb, leftStr, left, operation, STRING_TYPE);
					break;
				}

				// removed 0 handling due to problems with functions,
				// e.g 0 * x + 1 becomes 0 + 1 and no longer is a function
				// // check for 0 at left
				// else if (valueForm && isEqualString(left, 0, !valueForm)) {
				// sb.append("0");
				// break;
				// }
				// // check for 0 at right
				// else if (valueForm && isEqualString(right, 0, !valueForm)) {
				// sb.append("0");
				// break;
				// }

				// check for degree sign or 1degree or degree1 (eg for Arabic)
				else if ((rightStr.length() == 2 && ((rightStr.charAt(0) == Unicode.degreeChar && rightStr.charAt(1) == (Application.unicodeZero +1)) || (rightStr.charAt(0) == Unicode.degreeChar && rightStr.charAt(1) == (Application.unicodeZero +1))))
						|| rightStr.equals(Unicode.degree)) {
					
					boolean rtl = app.isRightToLeftDigits();
					
					if (rtl) {
						sb.append(Unicode.degree);
					}
					
					if (!left.isLeaf())
						sb.append('('); // needed for eg (a+b)\u00b0
					sb.append(leftStr);
					if (!left.isLeaf())
						sb.append(')'); // needed for eg (a+b)\u00b0
					
					if (!rtl) sb.append(Unicode.degree);
					
					break;
				}

			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_LATEX:

				boolean nounary = true;

				// left wing
				if (left.isLeaf() || opID(left) >= MULTIPLY) { // not +, -
					if (isEqualString(left, -1, !valueForm)) { // unary minus
						nounary = false;
						sb.append('-');
					} else {
						if (leftStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) {
							// brackets needed for eg Arabic digits
							sb.append(Unicode.RightToLeftMark);
							sb.append(leftBracket(STRING_TYPE));
							sb.append(leftStr);
							sb.append(rightBracket(STRING_TYPE));							
							sb.append(Unicode.RightToLeftMark);
						} else {
							sb.append(leftStr);
						}
					}
				} else {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				}

				// right wing
				int opIDright = opID(right);
				if (right.isLeaf() || opIDright >= MULTIPLY) { // not +, -
					boolean showMultiplicationSign = false;
					boolean multiplicationSpaceNeeded = true;
					if (nounary) {
						switch (STRING_TYPE) {						
						case STRING_TYPE_PGF:
						case STRING_TYPE_PSTRICKS:
						case STRING_TYPE_GEOGEBRA_XML:
						case STRING_TYPE_JASYMCA:
						case STRING_TYPE_MATH_PIPER:
						case STRING_TYPE_MAXIMA:
							showMultiplicationSign = true;
							break;
							
						case STRING_TYPE_LATEX:
							// check if we need a multiplication sign, see #414
							// digit-digit, e.g. 3 * 5
							// digit-fraction, e.g. 3 * \frac{5}{2}
							char lastLeft = leftStr.charAt(leftStr.length() - 1);
							char firstRight = rightStr.charAt(0);
							showMultiplicationSign = 
								// left is digit or ends with }, e.g. exponent, fraction
								( 	Character.isDigit(lastLeft) || lastLeft == '}' )
										&&  
								// right is digit or fraction
								(	Character.isDigit(firstRight) || rightStr.startsWith("\\frac"));
							break;

						default: // GeoGebra syntax
							char firstLeft = leftStr.charAt(0);
							lastLeft = leftStr.charAt(leftStr.length() - 1);
							firstRight = rightStr.charAt(0);
							// check if we need a multiplication sign, see #414
							// digit-digit, e.g. 3 * 5
							showMultiplicationSign = Character.isDigit(lastLeft) &&  Character.isDigit(firstRight);
							
							// check if we need a multiplication space:
							multiplicationSpaceNeeded = showMultiplicationSign;
							if (!multiplicationSpaceNeeded) {
								// check if we need a multiplication space:
								// it's needed except for number * character, e.g. 23x
								// need to check start and end for eg A1 * A2
								boolean leftIsNumber = left.isLeaf() && 
									Character.isDigit(firstLeft) && Character.isDigit(lastLeft);
								
								// check if we need a multiplication space:
								// all cases except number * character, e.g. 3x
								multiplicationSpaceNeeded = showMultiplicationSign ||
									!(leftIsNumber && !Character.isDigit(firstRight));
							}
						}

						if (STRING_TYPE == STRING_TYPE_LATEX && kernel.isInsertLineBreaks()) sb.append("\\-");
						
						if (showMultiplicationSign) {
							sb.append(multiplicationSign(STRING_TYPE));
						} 
						else if (multiplicationSpaceNeeded)
						{
							// space instead of multiplication sign
							sb.append(multiplicationSpace(STRING_TYPE));
						}
					}

					boolean rtlMinus;
					// show parentheses around these cases
					if (((rtlMinus = rightStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) || rightStr.charAt(0) == '-') // 2 (-5) or -(-5)
							|| !nounary
								&& !right.isLeaf()
								&& opIDright <= DIVIDE // -(x * a) or -(x / a)
							|| showMultiplicationSign && STRING_TYPE == STRING_TYPE_GEOGEBRA) // 3 (5)
					{
						if (rtlMinus) sb.append(Unicode.RightToLeftMark);
						sb.append(leftBracket(STRING_TYPE));
						sb.append(rightStr);
						sb.append(rightBracket(STRING_TYPE));
						if (rtlMinus) sb.append(Unicode.RightToLeftMark);
					} else {
						// -1.0 * 5 becomes "-5"
						sb.append(rightStr);
					}
				} 
				else { // right is + or - tree
					if (nounary) {
						switch (STRING_TYPE) {
						case STRING_TYPE_PGF:
						case STRING_TYPE_PSTRICKS:
						case STRING_TYPE_GEOGEBRA_XML:
						case STRING_TYPE_JASYMCA:
						case STRING_TYPE_MATH_PIPER:
						case STRING_TYPE_MAXIMA:
							sb.append(multiplicationSign(STRING_TYPE));
							break;

						default:
							// space instead of multiplication sign
							sb.append(multiplicationSpace(STRING_TYPE));
						}
					}
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
				}
				break;
			
			case STRING_TYPE_MPREDUCE:
				
				if (isEqualString(left, -1, !valueForm)) {
					sb.append("-(");
					sb.append(rightStr);
					sb.append(')');
				} else {
					sb.append("multiplication(");
					sb.append(leftStr);
					sb.append(",");
					sb.append(rightStr);
					sb.append(")");
					break;
				}
			}		
			break;

		case DIVIDE:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\frac{");
				sb.append(leftStr);
				sb.append("}{");
				sb.append(rightStr);
				sb.append("}");
				break;

			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
				sb.append('(');
				sb.append(leftStr);
				sb.append(")/(");
				sb.append(rightStr);
				sb.append(')');
				break;

			default:
				// check for 1 in denominator
				if (isEqualString(right, 1, !valueForm)) {
					sb.append(leftStr);
					break;
				}

				// left wing
				// put parantheses around +, -, *
				append(sb, leftStr, left, DIVIDE, STRING_TYPE); 
				sb.append(" / ");

				// right wing
				append(sb, rightStr, right, POWER, STRING_TYPE); // not +, -, *,
																	// /
			}
			break;

		case POWER:
			/*
			 * support for sin^2(x) for display, too slow and hacky if
			 * (STRING_TYPE == STRING_TYPE_GEOGEBRA &&
			 * leftStr.startsWith("sin(")) { //&& rightStr.equals("2")) { int
			 * index; try { index = Integer.parseInt(rightStr); } catch
			 * (NumberFormatException nfe) { index = Integer.MAX_VALUE; }
			 * 
			 * if (index > 0 && index != Integer.MAX_VALUE) { sb.append("sin");
			 * sb.append(Unicode.numberToIndex(index));
			 * sb.append(leftStr.substring(3)); // everying except the "sin"
			 * break; }
			 * 
			 * }//
			 */

			// support for sin^2(x) for LaTeX, eg FormulaText[]
			if (STRING_TYPE == STRING_TYPE_LATEX
					&& (leftStr.startsWith("\\sin ") || leftStr
							.startsWith("\\cos "))
					|| leftStr.startsWith("\\tan ")
					|| leftStr.startsWith("\\csc ")
					|| leftStr.startsWith("\\sec ")
					|| leftStr.startsWith("\\cot ")
					|| leftStr.startsWith("\\sinh ")
					|| leftStr.startsWith("\\cosh ")
					|| leftStr.startsWith("\\tanh ")
					|| leftStr.startsWith("\\coth ")
					|| leftStr.startsWith("\\sech ")
					|| leftStr.startsWith("\\csch ")) {
				// && rightStr.equals("2")) {
				int index;
				try {
					index = Integer.parseInt(rightStr);
				} catch (NumberFormatException nfe) {
					index = Integer.MAX_VALUE;
				}

				if (index > 0 && index != Integer.MAX_VALUE) {
					int spaceIndex = leftStr.indexOf(' ');
					sb.append(leftStr.substring(0, spaceIndex));
					sb.append(" ^{");
					sb.append(rightStr);
					sb.append("}");
					sb.append(leftStr.substring(spaceIndex + 1)); // everything
																	// except
																	// the
																	// "\\sin "
					break;
				}

			}// */

			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:

				// checks if the basis is leaf and if so
				// omits the brackets
				if (left.isLeaf() && leftStr.charAt(0) != '-') {
					sb.append(leftStr);
					break;
				}
				// else fall through
			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
				sb.append(leftBracket(STRING_TYPE));
				sb.append(leftStr);
				sb.append(rightBracket(STRING_TYPE));
				break;

			default:

				/*
				 * removed Michael Borcherds 2009-02-08 doesn't work eg m=1 g(x)
				 * = (x - 1)^m (x - 3)
				 * 
				 * 
				 * // check for 1 in exponent if (isEqualString(right, 1,
				 * !valueForm)) { sb.append(leftStr); break; } //
				 */

				// left wing
				if (leftStr.charAt(0) != '-' && // no unary
						(left.isLeaf() || opID(left) > POWER
								&& opID(left) != EXP)) { // not +, -, *, /, ^,
															// e^x
					sb.append(leftStr);
				} else {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				}
				break;
			}

			// right wing
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append('^');
				

				// add brackets for eg a^b^c -> a^(b^c)
				boolean addParentheses = (right.isExpressionNode() && ((ExpressionNode)right).getOperation() == POWER);
				
				sb.append('{');
				if (addParentheses) sb.append(leftBracket(STRING_TYPE));
				sb.append(rightStr);
				if (addParentheses) sb.append(rightBracket(STRING_TYPE));
				sb.append('}');
				break;

			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_GEOGEBRA_XML:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
				sb.append('^');
				sb.append('(');
				sb.append(rightStr);
				sb.append(')');
				break;

			default:
				if (right.isLeaf() || opID(right) > POWER && opID(right) != EXP) { // not
																					// +,
																					// -,
																					// *,
																					// /,
																					// ^,
																					// e^x
				// Michael Borcherds 2008-05-14
				// display powers over 9 as unicode superscript
					try {
						int i = Integer.parseInt(rightStr);
						String index = "";
						if (i < 0) {
							sb.append('\u207B'); // superscript minus sign
							i = -i;
						}

						if (i == 0)
							sb.append('\u2070'); // zero
						else
							while (i > 0) {
								switch (i % 10) {
								case 0:
									index = "\u2070" + index;
									break;
								case 1:
									index = "\u00b9" + index;
									break;
								case 2:
									index = "\u00b2" + index;
									break;
								case 3:
									index = "\u00b3" + index;
									break;
								case 4:
									index = "\u2074" + index;
									break;
								case 5:
									index = "\u2075" + index;
									break;
								case 6:
									index = "\u2076" + index;
									break;
								case 7:
									index = "\u2077" + index;
									break;
								case 8:
									index = "\u2078" + index;
									break;
								case 9:
									index = "\u2079" + index;
									break;

								}
								i = i / 10;
							}

						sb.append(index);
					} catch (Exception e) {
						sb.append('^');
						sb.append(rightStr);
					}

					/*
					 * 
					 * if (rightStr.length() == 1) { switch (rightStr.charAt(0))
					 * {
					 * 
					 * case '0': sb.append('\u2070'); break; case '1':
					 * sb.append('\u00b9'); break; case '2':
					 * sb.append('\u00b2'); break; case '3':
					 * sb.append('\u00b3'); break; case '4':
					 * sb.append('\u2074'); break; case '5':
					 * sb.append('\u2075'); break; case '6':
					 * sb.append('\u2076'); break; case '7':
					 * sb.append('\u2077'); break; case '8':
					 * sb.append('\u2078'); break; case '9':
					 * sb.append('\u2079'); break; default: sb.append('^');
					 * sb.append(rightStr); } } else { sb.append('^');
					 * sb.append(rightStr); }
					 */
				} else {
					sb.append('^');
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
				}
			}
			break;

		case FACTORIAL:
			switch (STRING_TYPE) {
			case STRING_TYPE_MPREDUCE:
				sb.append("factorial(");
				sb.append(leftStr);
				sb.append(")");				
				break;
				
			default:
				if (leftStr.charAt(0) != '-' && // no unary
						left.isLeaf() || opID(left) > POWER) { // not +, -, *, /, ^
					sb.append(leftStr);
				} else {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				}
				sb.append('!');
				break;
			}
			break;

		case COS:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\cos \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Cos(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("COS(");
				break;

			default:
				sb.append("cos(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case SIN:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sin \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sin(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("SIN(");
				break;

			default:
				sb.append("sin(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));

			break;

		case TAN:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\tan \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Tan(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("TAN(");
				break;

			default:
				sb.append("tan(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case CSC:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\csc \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Csc(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("CSC(");
				break;

			default:
				sb.append("csc(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case SEC:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sec \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sec(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("SEC(");
				break;

			default:
				sb.append("sec(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case COT:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\cot \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Cot(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("COT(");
				break;

			default:
				sb.append("cot(");
			}
			if (STRING_TYPE == STRING_TYPE_PGF)
				sb.append("(" + leftStr + ") 180/pi");
			else
				sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case CSCH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\csch \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Csch(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("CSCH(");
				break;

			default:
				sb.append("csch(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case SECH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sech \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sech(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("SECH(");
				break;

			default:
				sb.append("sech(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case COTH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\coth \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Coth(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("COTH(");
				break;

			default:
				sb.append("coth(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ARCCOS:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("acos \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcCos(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ACOS(");
				break;

			default:
				sb.append("acos(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ARCSIN:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("asin \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcSin(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ASIN(");
				break;

			default:
				sb.append("asin(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ARCTAN:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("atan \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcTan(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ATAN(");
				break;

			default:
				sb.append("atan(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ARCTAN2:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("atan2 \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcTan2(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ATAN2(");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("myatan2(");
				break;

			default:
				sb.append("atan2(");
			}
			sb.append(leftStr);
			sb.append(',');
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case COSH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\cosh \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Cosh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("COSH(");
				break;

			default:
				sb.append("cosh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case SINH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sinh \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sinh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("SINH(");
				break;

			default:
				sb.append("sinh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case TANH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\tanh \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Tanh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("TANH(");
				break;

			default:
				sb.append("tanh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ACOSH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\operatorname{acosh} \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcCosh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ACOSH(");
				break;

			default:
				sb.append("acosh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ASINH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\operatorname{asinh} \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcSinh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ASINH(");
				break;

			default:
				sb.append("asinh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case ATANH:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\operatorname{atanh} \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("ArcTanh(");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("ATANH(");
				break;

			default:
				sb.append("atanh(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case EXP:
			// Application.debug("EXP");
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				
				// add brackets for eg e^b^c -> e^(b^c)
				boolean addParentheses = (left.isExpressionNode() && ((ExpressionNode)left).getOperation() == POWER);
				
				sb.append("e^{");
				if (addParentheses) sb.append(leftBracket(STRING_TYPE));
				sb.append(leftStr);
				if (addParentheses) sb.append(rightBracket(STRING_TYPE));
				sb.append('}');
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Exp(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_GEOGEBRA_XML:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
				sb.append("exp(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("EXP(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append(Unicode.EULER_STRING);
				if (left.isLeaf()) {
					sb.append("^");
					sb.append(leftStr);
				} else {
					sb.append("^(");
					sb.append(leftStr);
					sb.append(')');
				}
				break;
			}
			break;

		case LOG:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\ln \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Ln(");
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_GEOGEBRA_XML:
			case STRING_TYPE_MPREDUCE:
				sb.append("log(");
				break;

			case STRING_TYPE_PSTRICKS:
			case STRING_TYPE_PGF:
			default:
				sb.append("ln(");
				break;
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;
			
			
		case LOGB:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\log_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(leftBracket(STRING_TYPE));
				sb.append(rightStr);
				sb.append(rightBracket(STRING_TYPE));
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MATH_PIPER:
				// user defined function
				sb.append("logB(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("logb(");
				sb.append(rightStr);
				sb.append(',');
				sb.append(leftStr);
				sb.append(')');
				break;

			case STRING_TYPE_PSTRICKS:
			case STRING_TYPE_PGF:
				// ln(x)/ln(b)
				sb.append("ln(");
				sb.append(rightStr);
				sb.append(")/ln(");
				sb.append(leftStr);
				sb.append(')');
				break;
				
			default:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append(')');
				break;
				
			}
			break;

		case ERF:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\erf");
				sb.append(leftBracket(STRING_TYPE));
				sb.append(leftStr);
				sb.append(rightBracket(STRING_TYPE));
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MPREDUCE:
				sb.append("erf(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("erf(");
				sb.append(leftStr);
				sb.append(')');
				break;
				
			}
			break;

		case LOG10:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\log_{10} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;

			case STRING_TYPE_PSTRICKS:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MPREDUCE:
			case STRING_TYPE_PGF:
				sb.append("log10("); // user-defined function in Maxima
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("lg(");
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;

		case LOG2:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\log_{2} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MATH_PIPER:
				sb.append("log2("); // user-defined function in Maxima
				sb.append(leftStr);
				sb.append(')');
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("logb(");
				sb.append(leftStr);
				sb.append(",2)");
				break;

			default:
				sb.append("ld(");
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;

		case SQRT:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sqrt{");
				sb.append(leftStr);
				sb.append('}');
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sqrt(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("sqrt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CBRT:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\sqrt[3]{");
				sb.append(leftStr);
				sb.append('}');
				break;

			case STRING_TYPE_MATH_PIPER:
			case STRING_TYPE_MPREDUCE:
				sb.append("(");
				sb.append(leftStr);
				sb.append(")^(1/3)");
				break;

			default:
				sb.append("cbrt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ABS:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\left|");
				sb.append(leftStr);
				sb.append("\\right|");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Abs(");
				sb.append(leftStr);
				sb.append(')');
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("myabs(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("abs(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case SGN:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\mathrm{sgn}(");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Sign(");
				break;

			case STRING_TYPE_JASYMCA:
			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
				sb.append("sign(");
				break;

			default:
				sb.append("sgn(");
			}
			sb.append(leftStr);
			sb.append(')');
			break;

		/*
		 * FIXME: Complex numbers here are sometimes (a,b) and sometimes (a+bi),
		 * Maxima needs a+b*%i, Mathpiper a+b*I or complex(a,b).
		 */
		case CONJUGATE:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\overline{");
				sb.append(leftStr);
				sb.append("}");
				break;
			case STRING_TYPE_MATH_PIPER:
				sb.append("Conjugate(");
				sb.append(leftStr);
				sb.append(")");
				break;
			default:
				sb.append("conjugate(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ARG:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\arg \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case STRING_TYPE_MATH_PIPER:
				sb.append("Arg(");
				sb.append(leftStr);
				sb.append(")");
				break;
			case STRING_TYPE_MAXIMA:
				sb.append("carg(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case STRING_TYPE_MPREDUCE:
				sb.append("myarg(");
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				sb.append("arg(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case FLOOR:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\left\\lfloor ");
				sb.append(leftStr);
				sb.append("\\right\\rfloor ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Floor(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("floor(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CEIL:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\left\\lceil ");
				sb.append(leftStr);
				sb.append("\\right\\rceil ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Ceil(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case STRING_TYPE_MAXIMA:
			case STRING_TYPE_MPREDUCE:
			case STRING_TYPE_PSTRICKS:
				sb.append("ceiling(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("ceil(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ROUND:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\mathrm{round} \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Round(");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("myround(");
				break;
				
			default:
				sb.append("round(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case GAMMA:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\Gamma \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Gamma(");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case GAMMA_INCOMPLETE:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\gamma \\left( ");
				break;

			case STRING_TYPE_MAXIMA:
				sb.append("gamma_incomplete(");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("gamma2(");

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case GAMMA_INCOMPLETE_REGULARIZED:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("P \\left( ");
				break;

			case STRING_TYPE_MAXIMA:
				sb.append("gamma_incomplete_regularized(");
				break;

			default:
				sb.append("gammaRegularized(");
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case BETA:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\Beta \\left( ");
				break;

			case STRING_TYPE_MATH_PIPER:
				sb.append("Beta(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case BETA_INCOMPLETE:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("\\Beta \\left( ");
				break;

			case STRING_TYPE_MAXIMA:
				sb.append("beta_incomplete(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case BETA_INCOMPLETE_REGULARIZED:
			switch (STRING_TYPE) {
			case STRING_TYPE_LATEX:
				sb.append("I \\left( ");
				break;

			case STRING_TYPE_MAXIMA:
				sb.append("beta_incomplete_regularized(");
				break;
				
			case STRING_TYPE_MPREDUCE:
				sb.append("beta3(");
				break;

			default:
				sb.append("betaRegularized(");
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case RANDOM:
			if (valueForm)
				sb.append(leftStr);
			else
				switch (STRING_TYPE) {
				case STRING_TYPE_MPREDUCE:
					sb.append("myrandom()");
					break;
				default:
					sb.append("random()");
				}
			break;

		case XCOORD:
			if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getX()));
			} else if (valueForm
					&& (leftEval = left.evaluate()).isVector3DValue()) {
				sb.append(kernel.format(((Vector3DValue) leftEval)
						.getPointAsDouble()[0]));
			} else if (valueForm
					&& (leftEval = left.evaluate()) instanceof GeoLine) {
				sb.append(kernel.format(((GeoLine) leftEval).x));
			} else {
				switch (STRING_TYPE) {
				case STRING_TYPE_LATEX:
					sb.append("\\mathrm{x} \\left( ");
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
					break;
					
        		case STRING_TYPE_MATH_PIPER:
        		case STRING_TYPE_MAXIMA:
        		case STRING_TYPE_MPREDUCE:
        			// we need to protect x(A) as a constant in the CAS
        			// see http://www.geogebra.org/trac/ticket/662
        			// see http://www.geogebra.org/trac/ticket/922
        			sb.append("xcoord(");
					sb.append(leftStr);
					sb.append(')');
        			break;
        			
				default:
					sb.append("x(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case YCOORD:
			if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getY()));
			} else if (valueForm
					&& (leftEval = left.evaluate()).isVector3DValue()) {
				sb.append(kernel.format(((Vector3DValue) leftEval)
						.getPointAsDouble()[1]));
			} else if (valueForm
					&& (leftEval = left.evaluate()) instanceof GeoLine) {
				sb.append(kernel.format(((GeoLine) leftEval).y));
			} else {
				switch (STRING_TYPE) {
				case STRING_TYPE_LATEX:
					sb.append("\\mathrm{y} \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;
					
				case STRING_TYPE_MATH_PIPER:
        		case STRING_TYPE_MAXIMA:
        		case STRING_TYPE_MPREDUCE:
        			// we need to protect x(A) as a constant in the CAS
        			// see http://www.geogebra.org/trac/ticket/662
        			// see http://www.geogebra.org/trac/ticket/922
        			sb.append("ycoord(");
					sb.append(leftStr);
					sb.append(')');
        			break;

				default:
					sb.append("y(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case ZCOORD:
			if (valueForm && (leftEval = left.evaluate()).isVector3DValue()) {
				sb.append(kernel.format(((Vector3DValue) leftEval)
						.getPointAsDouble()[2]));
			} else if (valueForm
					&& (leftEval = left.evaluate()) instanceof GeoLine) {
				sb.append(kernel.format(((GeoLine) leftEval).z));
			} else {
				switch (STRING_TYPE) {
				case STRING_TYPE_LATEX:
					sb.append("\\mathrm{z} \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;

				case STRING_TYPE_MATH_PIPER:
        		case STRING_TYPE_MAXIMA:
        		case STRING_TYPE_MPREDUCE:
        			// we need to protect x(A) as a constant in the CAS
        			// see http://www.geogebra.org/trac/ticket/662
        			// see http://www.geogebra.org/trac/ticket/922
        			sb.append("zcoord(");
					sb.append(leftStr);
					sb.append(')');
        			break;

				default:
					sb.append("z(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case FUNCTION:			
			// GeoFunction and GeoFunctionConditional should not be expanded
			if (left instanceof GeoFunction) {
				GeoFunction geo = (GeoFunction) left;
				if (geo.isLabelSet()) {
					sb.append(geo.getLabel());
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
				} else {					
					// inline function: replace function var by right side
					FunctionVariable var = geo.getFunction()
							.getFunctionVariable();
					String oldVarStr = var.toString();
					var.setVarString(rightStr);
					sb.append(geo.getLabel());
					var.setVarString(oldVarStr);
				}
			} else if (valueForm && left.isExpressionNode()) {				
				ExpressionNode en = (ExpressionNode) left;
				// left could contain $ nodes to wrap a GeoElement
				// e.g. A1(x) = x^2 and B1(x) = $A$1(x)
				// value form of B1 is x^2 and NOT x^2(x)
				switch (en.operation) {
				case $VAR_ROW:
				case $VAR_COL:
				case $VAR_ROW_COL:
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
					break;

				default:
					sb.append(leftStr);
					sb.append(leftBracket(STRING_TYPE));
					sb.append(rightStr);
					sb.append(rightBracket(STRING_TYPE));
					break;
				}
			} else {
				// standard case if we get here
				sb.append(leftStr);
				sb.append(leftBracket(STRING_TYPE));
				sb.append(rightStr);
				sb.append(rightBracket(STRING_TYPE));
			}
			break;

			// TODO: put back into case FUNCTION_NVAR:, see #1115
		case ELEMENT_OF:
			sb.append(app.getCommand("Element"));
			sb.append('[');
			if (left.isGeoElement()) {
				sb.append(((GeoElement) left).getLabel());
			} else
				sb.append(leftStr);
			sb.append(", ");
			sb.append(((MyList) right).getListElement(0).toString());
			sb.append(']');
			break;
			
		case FUNCTION_NVAR:
			if (valueForm) {
				// TODO: avoid replacing of expressions in operationToString
				if (left instanceof FunctionalNVar && right instanceof MyList) {
					FunctionNVar func = ((FunctionalNVar) left).getFunction();
					ExpressionNode en = func.expression.getCopy(kernel);
					for (int i = 0; i < func.getVarNumber()
							&& i < ((MyList) right).size(); i++) {
						en.replace(func.getFunctionVariables()[i],
								((MyList) right).getListElement(i));
					}
					// add brackets, see http://www.geogebra.org/trac/ticket/1446
					if (STRING_TYPE != STRING_TYPE_LATEX) 
						sb.append(leftBracket(STRING_TYPE));
					sb.append(en.toValueString());
					if (STRING_TYPE != STRING_TYPE_LATEX) 
						sb.append(rightBracket(STRING_TYPE));
				} 
				else {
					sb.append(leftBracket(STRING_TYPE));
					sb.append(leftStr);
					sb.append(rightBracket(STRING_TYPE));
				}
			} else {
				// multivariate functions
				if (left.isGeoElement()) {
					sb.append(((GeoElement) left).getLabel());
				} else
					sb.append(leftStr);
				sb.append(leftBracket(STRING_TYPE));
				// rightStr is a list of arguments, e.g. {2, 3}
				// drop the curly braces { and }
				// or list( and ) in case of mpreduce
				if (STRING_TYPE == STRING_TYPE_MPREDUCE)
					sb.append(rightStr.substring(22, rightStr.length() - 2));
				else
					sb.append(rightStr.substring(1, rightStr.length() - 1));
				sb.append(rightBracket(STRING_TYPE));
			}
			break;

		case VEC_FUNCTION:
			// GeoCurveables should not be expanded
			if (left.isGeoElement() && ((GeoElement) left).isGeoCurveable()) {
				sb.append(((GeoElement) left).getLabel());
			} else
				sb.append(leftStr);
			sb.append(leftBracket(STRING_TYPE));
			sb.append(rightStr);
			sb.append(rightBracket(STRING_TYPE));
			break;

		case DERIVATIVE: // e.g. f''
			// labeled GeoElements should not be expanded
			if (left.isGeoElement() && ((GeoElement) left).isLabelSet()) {
				sb.append(((GeoElement) left).getLabel());
			} else
				sb.append(leftStr);

			if (right.isNumberValue()) {
				int order = (int) Math.round(((MyDouble) right).getDouble());
				for (; order > 0; order--)
					sb.append('\'');
			} else
				sb.append(right);
			break;

		case $VAR_ROW: // e.g. A$1
			if (valueForm) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(false, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case $VAR_COL: // e.g. $A1
			if (valueForm) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, false));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case $VAR_ROW_COL: // e.g. $A$1
			if (valueForm) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		default:
			sb.append("unhandled operation " + operation);
		}
		return sb.toString();
	}

	/**
	 * return operation number
	 * 
	 * @param ev
	 * @return operation number
	 */
	static public int opID(ExpressionValue ev) {
		if (ev.isExpressionNode()){
			int op = ((ExpressionNode) ev).operation;
			//input (x>y)==(x+y>3) must be kept
			if(op == GREATER || op == LESS || op == LESS_EQUAL || op == GREATER_EQUAL)
				return NOT_EQUAL-1;
			return op;
		}
		else
			return -1;
	}

	public boolean isNumberValue() {
		return evaluate().isNumberValue();
	}

	public boolean isBooleanValue() {
		return evaluate().isBooleanValue();
	}

	public boolean isListValue() {
		return evaluate().isListValue();
	}

	public boolean isPolynomialInstance() {
		// return evaluate().isPolynomial();
		return false;
	}

	public boolean isTextValue() {
		// should be efficient as it is used in operationToString()
		if (leaf)
			return left.isTextValue();
		else
			return (operation == PLUS && (left.isTextValue() || right
					.isTextValue()));
	}

	final public boolean isExpressionNode() {
		return true;
	}

	public boolean isVector3DValue() {
		return false;
	}

	/**
	 * Returns true iff ev1 and ev2 are equal
	 * 
	 * @param ev1
	 * @param ev2
	 * @return true iff ev1 and ev2 are equal
	 */
	public static boolean isEqual(ExpressionValue ev1, ExpressionValue ev2) {
		if (ev1.isNumberValue() && ev2.isNumberValue()) {
			return Kernel.isEqual(((NumberValue) ev1).getDouble(),
					((NumberValue) ev2).getDouble(), Kernel.EPSILON);
		} else if (ev1.isTextValue() && ev2.isTextValue()) {
			return ((TextValue) ev1).toString().equals(
					((TextValue) ev2).toString());
		} else if (ev1.isGeoElement() && ev2.isGeoElement()) {
			return ((GeoElement) ev1).isEqual(((GeoElement) ev2));
		} else
			return false;
	}

	/**
	 * Returns whether the given expression will give the same String output as
	 * val.
	 * 
	 * @param symbolic
	 *            whether we should use the value (true) or the label (false) of
	 *            ev when it is a GeoElement
	 * @param val
	 * @param ev
	 * @return true iff output of ev and val are the same
	 */
	final public static boolean isEqualString(ExpressionValue ev, double val,
			boolean symbolic) {
		if (ev.isLeaf() && ev instanceof NumberValue) {
			// function variables need to be kept
			if (ev instanceof FunctionVariable) {
				return false;
			} else if (ev instanceof MySpecialDouble) {
				// special doubles like pi, degree, rad need to be kept
				return false;
			}

			// check if ev is a labeled GeoElement
			if (symbolic) {
				if (ev.isGeoElement()) {
					// labeled GeoElement
					GeoElement geo = (GeoElement) ev;
					if (geo.isLabelSet() || geo.isLocalVariable()
							|| !geo.isIndependent())
						return false;
				}
			}

			NumberValue nv = (NumberValue) ev;
			return nv.getDouble() == val;
		}
		return false;
	}

	public boolean isTopLevelCommand() {
		return isLeaf() && left instanceof Command;
	}

	public Command getTopLevelCommand() {
		if (isTopLevelCommand())
			return (Command) left;
		else
			return null;
	}

	private String leftBracket(int type) {
		return (type == STRING_TYPE_LATEX) ? " \\left( " : "(";
	}

	private String rightBracket(int type) {
		return (type == STRING_TYPE_LATEX) ? " \\right) " : ")";
	}

	private String multiplicationSign(int type) {
		switch (type) {
			case STRING_TYPE_LATEX:
				return " \\cdot ";
			
			case STRING_TYPE_GEOGEBRA:
				return " "; // space for multiplication
				
			default: 
				return " * ";
		}
	}

	private String multiplicationSpace(int type) {
		// wide space for multiplicatoin space in LaTeX
		return (type == STRING_TYPE_LATEX) ? " \\; " : " ";
	}

	/**
	 * If the expression is linear in fv, returns the corresponding coefficient.
	 * Otherwise returns null.
	 * 
	 * @param fv
	 * @return coefficient or null
	 */
	public Double getCoefficient(FunctionVariable fv) {
		if (this.isLeaf()) {
			if (this.equals(fv) || left.equals(fv)) {
				return 1.0;
			}

			return 0.0;

		}

		Double lc = getLeftTree() == null ? null : getLeftTree()
				.getCoefficient(fv);
		Double rc = getRightTree() == null ? null : getRightTree()
				.getCoefficient(fv);
		if (lc == null || rc == null)
			return null;
		if (this.operation == PLUS && lc != null && rc != null) {
			return lc + rc;
		} else if (this.operation == MINUS) {
			return lc - rc;
		} else if (this.operation == MULTIPLY
				&& !getRightTree().containsObjectType(fv.getClass())) {
			return lc * ((NumberValue) getRightTree().evaluate()).getDouble();
		} else if (this.operation == MULTIPLY
				&& !getLeftTree().containsObjectType(fv.getClass())) {
			return rc * ((NumberValue) getLeftTree().evaluate()).getDouble();
		} else if (this.operation == DIVIDE
				&& !getRightTree().containsObjectType(fv.getClass())) {
			return lc / ((NumberValue) getRightTree().evaluate()).getDouble();
		} else if ((left.contains(fv) || right.contains(fv)))
			return null;
		return 0.0;

	}

	/*
	 * appends a string to sb, brackets are put around it if the order of operation
	 * dictates
	 */
	private void append(StringBuilder sb, String str, ExpressionValue ev,
			int op, int STRING_TYPE) {
		if (ev.isLeaf() || opID(ev) >= op) {
			sb.append(str);
		} else {
			sb.append(leftBracket(STRING_TYPE));
			sb.append(str);
			sb.append(rightBracket(STRING_TYPE));
		}

	}

	public String toRealString() {
		if (leaf) { // leaf is GeoElement or not
			if (left.isGeoElement())
				return ((GeoElement) left).getRealLabel();
			else
				return left.toRealString();
		}

		// expression node
		String leftStr = null, rightStr = null;
		if (left.isGeoElement()) {
			leftStr = ((GeoElement) left).getRealLabel();
		} else {
			leftStr = left.toRealString();
		}

		if (right != null) {
			if (right.isGeoElement()) {
				rightStr = ((GeoElement) right).getRealLabel();
			} else {
				rightStr = right.toRealString();
			}
		}
		return operationToString(leftStr, rightStr, false);
	}
	
	public ExpressionNode plus(ExpressionValue v2){
		return new ExpressionNode(kernel,this,ExpressionNode.PLUS,v2);
	}
	public ExpressionNode multiply(ExpressionValue v2){
		return new ExpressionNode(kernel,v2,ExpressionNode.MULTIPLY,this);
	}
	public ExpressionNode power(ExpressionValue v2){
		return new ExpressionNode(kernel,this,ExpressionNode.POWER,v2);
	}
	public ExpressionNode divide(ExpressionValue v2){
		return new ExpressionNode(kernel,this,ExpressionNode.DIVIDE,v2);
	}
	public ExpressionNode and(ExpressionValue v2){
		return new ExpressionNode(kernel,this,ExpressionNode.AND,v2);
	}
	public ExpressionNode negation(){
		if(operation==GREATER)
			return new ExpressionNode(kernel,left,LESS_EQUAL,right);
		if(operation==GREATER_EQUAL)
			return new ExpressionNode(kernel,left,LESS,right);
		if(operation==LESS)
			return new ExpressionNode(kernel,left,GREATER_EQUAL,right);
		if(operation==LESS_EQUAL)
			return new ExpressionNode(kernel,left,GREATER,right);
		if(operation==EQUAL_BOOLEAN)
			return new ExpressionNode(kernel,left,NOT_EQUAL,right);
		if(operation==NOT_EQUAL)
			return new ExpressionNode(kernel,left,EQUAL_BOOLEAN,right);		
		return new ExpressionNode(kernel,this,ExpressionNode.NOT,null);
	}

	public double getDegree(FunctionVariable fv) {
		if (this.isLeaf()) {
			if (this.equals(fv) || left.equals(fv)) {
				return 1.0;
			}

			return 0.0;

		}
		if(operation == POWER && left == fv){
			ExpressionValue rt = right.evaluate();
			if(rt.isNumberValue())
				return ((NumberValue)rt).getDouble();
		}
		if(operation == MULTIPLY)
			return getLeftTree().getDegree(fv)+getRightTree().getDegree(fv);
		if(operation == PLUS || operation == MINUS)
			return Math.max(getLeftTree().getDegree(fv),getRightTree().getDegree(fv));
		return 0;
		
	}
	

}

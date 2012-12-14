package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.Geo3DVec;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

/**
 * @author ggb3D
 * 
 *         Evaluator for ExpressionNode (used in Operation.evaluate())
 */
public class ExpressionNodeEvaluator implements ExpressionNodeConstants {
	private static final StringTemplate errorTemplate = StringTemplate.defaultTemplate;
	private Localization l10n;
	
	/**
	 * Creates new expression node evaluator
	 * @param l10n localization for errors
	 */
	public ExpressionNodeEvaluator(Localization l10n) {
		this.l10n = l10n;
	}

	/**
	 * Evaluates the ExpressionNode described by the parameters
	 * 
	 * @param expressionNode
	 *            ExpressionNode to evaluate
	 * @param tpl
	 *            template needed for nodes containing string concatenation
	 * @return corresponding ExpressionValue
	 */
	public ExpressionValue evaluate(ExpressionNode expressionNode,
			StringTemplate tpl) {
		boolean leaf = expressionNode.leaf;
		ExpressionValue left = expressionNode.getLeft();

		if (leaf) {
			return left.evaluate(tpl); // for wrapping ExpressionValues as
			// ValidExpression
		}

		ExpressionValue right = expressionNode.getRight();
		Operation operation = expressionNode.getOperation();

		boolean holdsLaTeXtext = expressionNode.holdsLaTeXtext;

		// Application.debug(operation+"");

		ExpressionValue lt, rt;

		lt = left.evaluate(tpl); // left tree
		if (operation.equals(Operation.NO_OPERATION)) {
			return lt;
		}
		rt = right.evaluate(tpl); // right tree

		// handle list operations first

		ExpressionValue special = handleSpecial(lt, rt, left, right, operation,
				tpl);
		if (special != null)
			return special;
		// NON-List operations (apart from EQUAL_BOOLEAN and list + text)
		return operation.handle(this, lt, rt, left, right, tpl, holdsLaTeXtext);

	}

	private static ExpressionValue handleSpecial(ExpressionValue lt,
			ExpressionValue rt, ExpressionValue left, ExpressionValue right,
			Operation operation, StringTemplate tpl) {
		if (lt.isListValue()) {
			if ((operation == Operation.MULTIPLY)) {

				if (rt.isVectorValue()) {
					MyList myList = ((ListValue) lt).getMyList();
					boolean isMatrix = myList.isMatrix();
					int rows = myList.getMatrixRows();
					int cols = myList.getMatrixCols();
					if (isMatrix && (rows == 2) && (cols == 2)) {
						GeoVec2D myVec = ((VectorValue) rt).getVector();
						// 2x2 matrix
						myVec.multiplyMatrix(myList);

						return myVec;
					} else if (isMatrix && (rows == 3) && (cols == 3)) {
						GeoVec2D myVec = ((VectorValue) rt).getVector();
						// 3x3 matrix, assume it's affine
						myVec.multiplyMatrixAffine(myList, rt);
						return myVec;
					}

				} else if (rt.isVector3DValue()) {
					MyList myList = ((ListValue) lt).getMyList();
					boolean isMatrix = myList.isMatrix();
					int rows = myList.getMatrixRows();
					int cols = myList.getMatrixCols();
					if (isMatrix && (rows == 3) && (cols == 3)) {
						Geo3DVec myVec = ((Vector3DValue) rt).get3DVec();
						// 3x3 matrix * 3D vector / point
						myVec.multiplyMatrix(myList, rt);
						return myVec;
					}

				}

			} else if ((operation == Operation.VECTORPRODUCT)
					&& rt.isListValue()) {

				MyList listL = ((ListValue) lt.evaluate(tpl)).getMyList();
				MyList listR = ((ListValue) rt.evaluate(tpl)).getMyList();
				if (((listL.size() == 3) && (listR.size() == 3))
						|| ((listL.size() == 2) && (listR.size() == 2))) {
					listL.vectorProduct(listR);
					return listL;
				}

			}
			// we cannot use elseif here as we might need multiplication
			if ((operation != Operation.EQUAL_BOOLEAN // added
														// EQUAL_BOOLEAN
														// Michael
					)
					// Borcherds 2008-04-12
					&& (operation != Operation.NOT_EQUAL // ditto
					) && (operation != Operation.IS_SUBSET_OF // ditto
					) && (operation != Operation.IS_SUBSET_OF_STRICT // ditto
					) && (operation != Operation.SET_DIFFERENCE // ditto
					) && (operation != Operation.ELEMENT_OF // list1(1) to get
															// first element
					) && (operation != Operation.IS_ELEMENT_OF // list1(1) to
																// get
					// first element
					) && !rt.isVectorValue() // eg {1,2} + (1,2)
					&& !rt.isTextValue()) // bugfix "" + {1,2} Michael Borcherds
											// 2008-06-05
			{
				MyList myList = ((ListValue) lt).getMyList();
				// list lt operation rt
				myList.applyRight(operation, rt, tpl);
				return myList;
			}
		} else if (rt.isListValue()
				&& !operation.equals(Operation.EQUAL_BOOLEAN) // added
				// EQUAL_BOOLEAN
				// Michael
				// Borcherds
				// 2008-04-12
				&& !operation.equals(Operation.NOT_EQUAL) // ditto
				&& !operation.equals(Operation.FUNCTION_NVAR) // ditto
				&& !operation.equals(Operation.FREEHAND) // ditto
				&& !lt.isVectorValue() // eg {1,2} + (1,2)
				&& !lt.isTextValue() // bugfix "" + {1,2} Michael Borcherds
				// 2008-06-05
				&& !operation.equals(Operation.IS_ELEMENT_OF)) {
			MyList myList = ((ListValue) rt).getMyList();
			// lt operation list rt
			myList.applyLeft(operation, lt, tpl);
			return myList;
		}

		else if ((lt instanceof FunctionalNVar)
				&& (rt instanceof FunctionalNVar)
				&& !operation.equals(Operation.EQUAL_BOOLEAN)
				&& !operation.equals(Operation.NOT_EQUAL)) {
			return GeoFunction.operationSymb(operation, (FunctionalNVar) lt,
					(FunctionalNVar) rt);
		}
		// we want to use function arithmetic in cases like f*2 or f+x^2, but
		// not for f(2), f'(2) etc.
		else if ((lt instanceof FunctionalNVar) && rt.isNumberValue()
				&& (operation.ordinal() < Operation.FUNCTION.ordinal())) {
			return GeoFunction.applyNumberSymb(operation, (FunctionalNVar) lt,
					right, true);
		} else if ((rt instanceof FunctionalNVar) && lt.isNumberValue()) {
			return GeoFunction.applyNumberSymb(operation, (FunctionalNVar) rt,
					left, false);
		}
		return null;
	}

	/**
	 * Checks whether first object equals second
	 * 
	 * @param kernel
	 *            kernel
	 * @param lt
	 *            first object
	 * @param rt
	 *            second object
	 * @return false if not defined
	 */
	public static MyBoolean evalEquals(Kernel kernel, ExpressionValue lt,
			ExpressionValue rt) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		// booleans
		if (lt.isBooleanValue() && rt.isBooleanValue()) {
			return new MyBoolean(kernel,
					((BooleanValue) lt).getBoolean() == ((BooleanValue) rt)
							.getBoolean());
		} else if (lt.isNumberValue() && rt.isNumberValue()) {
			return new MyBoolean(kernel, Kernel.isEqual(
					((NumberValue) lt).getDouble(),
					((NumberValue) rt).getDouble()));
		} else if (lt.isTextValue() && rt.isTextValue()) {

			String strL = ((TextValue) lt).toValueString(tpl);
			String strR = ((TextValue) rt).toValueString(tpl);

			// needed for eg Sequence[If[Element[list1,i]=="b",0,1],i,i,i]
			if ((strL == null) || (strR == null)) {
				return new MyBoolean(kernel, false);
			}

			return new MyBoolean(kernel, strL.equals(strR));
		} else if (lt.isListValue() && rt.isListValue()) {

			MyList list1 = ((ListValue) lt).getMyList();
			MyList list2 = ((ListValue) rt).getMyList();

			int size = list1.size();

			if (size != list2.size()) {
				return new MyBoolean(kernel, false);
			}

			for (int i = 0; i < size; i++) {
				if (!evalEquals(kernel, list1.getListElement(i).evaluate(tpl),
						list2.getListElement(i).evaluate(tpl)).getBoolean()) {
					return new MyBoolean(kernel, false);
				}
			}

			return new MyBoolean(kernel, true);

		} else if (lt.isGeoElement() && rt.isGeoElement()) {
			GeoElement geo1 = (GeoElement) lt;
			GeoElement geo2 = (GeoElement) rt;

			return new MyBoolean(kernel, geo1.isEqual(geo2));
		} else if (lt.isVectorValue() && rt.isVectorValue()) {
			VectorValue vec1 = (VectorValue) lt;
			VectorValue vec2 = (VectorValue) rt;
			return new MyBoolean(kernel, vec1.getVector().isEqual(
					vec2.getVector()));
		} else if (lt.isVector3DValue() && rt.isVector3DValue()) {
			Vector3DValue vec1 = (Vector3DValue) lt;
			Vector3DValue vec2 = (Vector3DValue) rt;
			return new MyBoolean(kernel, vec1.get3DVec().isEqual(
					vec2.get3DVec()));
		}

		return new MyBoolean(kernel, false);
	}

	/**
	 * @param arg vector or line
	 * @param op XCOORD or REAL
	 * @return x coordinate
	 */
	public ExpressionValue handleXcoord(ExpressionValue arg,Operation op) {
		Kernel kernel = arg.getKernel();
		if (arg.isVectorValue()) {
			return new MyDouble(kernel, ((VectorValue) arg).getVector().getX());
		} else if (arg.isVector3DValue()) {
			return new MyDouble(kernel,
					((Vector3DValue) arg).getPointAsDouble()[0]);
		} else if (arg instanceof GeoLine) {
			return new MyDouble(kernel, ((GeoLine) arg).x);
		} else
			return polynomialOrDie(arg, Operation.XCOORD, op==Operation.YCOORD?  "y(":"imaginary(");

	}
	/**
	 * @param arg vector or line
	 * @param op YCOORD or IMAGINARY
	 * @return y coordinate
	 */
	public ExpressionValue handleYcoord(ExpressionValue arg,Operation op) {

		Kernel kernel = arg.getKernel();

		// y(vector)
		if (arg.isVectorValue()) {
			return new MyDouble(kernel, ((VectorValue) arg).getVector().getY());
		} else if (arg.isVector3DValue()) {
			return new MyDouble(kernel,
					((Vector3DValue) arg).getPointAsDouble()[1]);
		} else if (arg instanceof GeoLine) {
			return new MyDouble(kernel, ((GeoLine) arg).y);
		} else
			return polynomialOrDie(arg, op, op==Operation.YCOORD?  "y(":"imaginary(");
	}

	/**
	 * Performs multiplication
	 * @param lt left argument
	 * @param rt right argument
	 * @param tpl string template (may be  string concatenation)
	 * @param holdsLaTeXtext whether parent node holds LaTeX
	 * @return result
	 */
	public ExpressionValue handleMult(ExpressionValue lt, ExpressionValue rt,
			StringTemplate tpl, boolean holdsLaTeXtext) {
		Kernel kernel = lt.getKernel();
		MyDouble num;
		GeoVec2D vec;
		MyStringBuffer msb;
		Polynomial poly;

		if (lt.isNumberValue()) {
			// number * number
			if (rt.isNumberValue()) {
				num = ((NumberValue) lt).getNumber();
				MyDouble.mult(num, (NumberValue) rt, num);
				return num;
			}
			// number * vector
			else if (rt.isVectorValue()) {
				vec = ((VectorValue) rt).getVector();
				GeoVec2D.mult(vec, ((NumberValue) lt).getDouble(), vec);
				return vec;
			}
			// number * boolean -- already in number * number

		}
		// text concatenation (left)
		if (lt.isTextValue()) {
			msb = ((TextValue) lt).getText();
			if (holdsLaTeXtext) {
				msb.append(rt.toLaTeXString(false, tpl));
			} else {
				if (rt.isGeoElement()) {
					GeoElement geo = (GeoElement) rt;
					msb.append(geo.toDefinedValueString(tpl));
				} else {
					msb.append(rt.toValueString(tpl));
				}
			}
			return msb;
		} // text concatenation (right)
		else if (rt.isTextValue()) {
			msb = ((TextValue) rt).getText();
			if (holdsLaTeXtext) {
				msb.insert(0, lt.toLaTeXString(false, tpl));
			} else {
				if (lt.isGeoElement()) {
					GeoElement geo = (GeoElement) lt;
					msb.insert(0, geo.toDefinedValueString(tpl));
				} else {
					msb.insert(0, lt.toValueString(tpl));
				}
			}
			return msb;
		} else
		// number * ...

		// boolean * number
		if (lt.isBooleanValue() && rt.isNumberValue()) {
			num = ((NumberValue) rt).getNumber();
			MyDouble.mult(num, ((BooleanValue) lt).getDouble(), num);
			return num;
		}
		// vector * ...
		else if (lt.isVectorValue()) {
			// vector * number
			if (rt.isNumberValue()) {
				vec = ((VectorValue) lt).getVector();
				GeoVec2D.mult(vec, ((NumberValue) rt).getDouble(), vec);
				return vec;
			}
			// vector * vector (inner/dot product)
			else if (rt.isVectorValue()) {
				vec = ((VectorValue) lt).getVector();
				if (vec.getMode() == Kernel.COORD_COMPLEX) {

					// complex multiply

					GeoVec2D.complexMultiply(vec,
							((VectorValue) rt).getVector(), vec);
					return vec;
				}
				num = new MyDouble(kernel);
				GeoVec2D.inner(vec, ((VectorValue) rt).getVector(), num);
				return num;
			}
			return illegalBinary(lt,rt, "IllegalMultiplication","*");
			
		}
		// polynomial * polynomial
		else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {
			poly = new Polynomial(kernel, (Polynomial) lt);
			poly.multiply((Polynomial) rt);
			return poly;
		} else if (lt.isTextValue()) {
			msb = ((TextValue) lt).getText();
			if (holdsLaTeXtext) {
				msb.append(rt.toLaTeXString(false, tpl));
			} else {
				if (rt.isGeoElement()) {
					GeoElement geo = (GeoElement) rt;
					msb.append(geo.toDefinedValueString(tpl));
				} else {
					msb.append(rt.toValueString(tpl));
				}
			}
			return msb;
		} // text concatenation (right)
		else if (rt.isTextValue()) {
			msb = ((TextValue) rt).getText();
			if (holdsLaTeXtext) {
				msb.insert(0, lt.toLaTeXString(false, tpl));
			} else {
				if (lt.isGeoElement()) {
					GeoElement geo = (GeoElement) lt;
					msb.insert(0, geo.toDefinedValueString(tpl));
				} else {
					msb.insert(0, lt.toValueString(tpl));
				}
			}
			return msb;
		} 
		return illegalBinary(lt,rt, "IllegalMultiplication","*");
	}
	/**
	 * Performs addition
	 * @param lt left argument
	 * @param rt right argument
	 * @param tpl string template (may be  string concatenation)
	 * @param holdsLaTeXtext whether parent node holds LaTeX
	 * @return result
	 */
	public ExpressionValue handlePlus(ExpressionValue lt, ExpressionValue rt,
			StringTemplate tpl, boolean holdsLaTeXtext) {
		String[] str;
		Kernel kernel = lt.getKernel();
		MyDouble num;
		GeoVec2D vec;
		MyStringBuffer msb;
		Polynomial poly;
		if (lt.isNumberValue() && rt.isNumberValue()) {
			num = ((NumberValue) lt).getNumber();
			MyDouble.add(num, ((NumberValue) rt).getNumber(), num);
			return num;
		}
		// vector + vector
		else if (lt.isVectorValue() && rt.isVectorValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.add(vec, ((VectorValue) rt).getVector(), vec);
			return vec;
		}
		// vector + number (for complex addition)
		else if (lt.isVectorValue() && rt.isNumberValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.add(vec, ((NumberValue) rt), vec);
			return vec;
		}
		// number + vector (for complex addition)
		else if (lt.isNumberValue() && rt.isVectorValue()) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.add(vec, ((NumberValue) lt), vec);
			return vec;
		}
		// list + vector
		else if (lt.isListValue() && rt.isVectorValue()) {
			MyList list = ((ListValue) lt).getMyList();
			if (list.size() > 0) {
				ExpressionValue ev = list.getListElement(0);
				if (ev.isNumberValue()) { // eg {1,2} + (1,2) treat as point
					// + point
					vec = ((VectorValue) rt).getVector();
					GeoVec2D.add(vec, ((ListValue) lt), vec);
					return vec;
				}
			}
			// not a list with numbers, do list operation
			MyList myList = ((ListValue) lt).getMyList();
			// list lt operation rt
			myList.applyRight(Operation.PLUS, rt, tpl);
			return myList;

		}
		// vector + list
		else if (rt.isListValue() && lt.isVectorValue()) {
			MyList list = ((ListValue) rt).getMyList();
			if (list.size() > 0) {
				ExpressionValue ev = list.getListElement(0);
				if (ev.isNumberValue()) { // eg {1,2} + (1,2) treat as point
					// + point
					vec = ((VectorValue) lt).getVector();
					GeoVec2D.add(vec, ((ListValue) rt), vec);
					return vec;
				}
			}
			// not a list with numbers, do list operation
			MyList myList = ((ListValue) rt).getMyList();
			// lt operation list rt
			myList.applyLeft(Operation.PLUS, lt, tpl);
			return myList;
		}
		// text concatenation (left)
		else if (lt.isTextValue()) {
			msb = ((TextValue) lt).getText();
			if (holdsLaTeXtext) {
				msb.append(rt.toLaTeXString(false, tpl));
			} else {
				if (rt.isGeoElement()) {
					GeoElement geo = (GeoElement) rt;
					msb.append(geo.toDefinedValueString(tpl));
				} else {
					msb.append(rt.toValueString(tpl));
				}
			}
			return msb;
		} // text concatenation (right)
		else if (rt.isTextValue()) {
			msb = ((TextValue) rt).getText();
			if (holdsLaTeXtext) {
				msb.insert(0, lt.toLaTeXString(false, tpl));
			} else {
				if (lt.isGeoElement()) {
					GeoElement geo = (GeoElement) lt;
					msb.insert(0, geo.toDefinedValueString(tpl));
				} else {
					msb.insert(0, lt.toValueString(tpl));
				}
			}
			return msb;
		}
		// polynomial + polynomial
		else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {
			poly = new Polynomial(kernel, (Polynomial) lt);
			poly.add((Polynomial) rt);
			return poly;
		} else {
			str = new String[] { "IllegalAddition", lt.toString(errorTemplate),
					"+", rt.toString(errorTemplate) };
			throw new MyError(l10n, str);
		}

	}
	/**
	 * Performs division
	 * @param lt left argument (evaluated)
	 * @param rt right argument (evaluated)
	 * @param left left argument before evaluation
	 * @param right right argument before evaluation
	 * 
	 * @return result
	 */
	public ExpressionValue handleDivide(ExpressionValue lt, ExpressionValue rt,
			ExpressionValue left, ExpressionValue right) {
		// sin(number)
		String[] str;
		Kernel kernel = lt.getKernel();
		MyDouble num;
		GeoVec2D vec;
		Polynomial poly;
		if (rt.isNumberValue()) {
			// number / number
			if (lt.isNumberValue()) {
				num = ((NumberValue) lt).getNumber();
				MyDouble.div(num, ((NumberValue) rt).getNumber(), num);
				return num;
			}
			// vector / number
			else if (lt.isVectorValue()) {
				vec = ((VectorValue) lt).getVector();
				GeoVec2D.div(vec, ((NumberValue) rt).getDouble(), vec);
				return vec;
			} else if (lt instanceof GeoFunction) {
				return GeoFunction.applyNumberSymb(Operation.DIVIDE,
						(GeoFunction) lt, right, true);
			}
			/*
			 * // number * 3D vector else if (lt.isVector3DValue()) { Geo3DVec
			 * vec3D = ((Vector3DValue)lt).get3DVec(); Geo3DVec.div(vec3D,
			 * ((NumberValue)rt).getDouble(), vec3D); return vec3D; }
			 */
			else {
				str = new String[] { "IllegalDivision",
						lt.toString(errorTemplate), "/",
						rt.toString(errorTemplate) };
				throw new MyError(l10n, str);
			}
		}
		// polynomial / polynomial
		else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {
			// the divisor must be a polynom of degree 0
			if (((Polynomial) rt).degree() != 0) {
				str = new String[] { "DivisorMustBeConstant",
						lt.toString(errorTemplate), "/",
						rt.toString(errorTemplate) };
				throw new MyError(l10n, str);
			}

			poly = new Polynomial(kernel, (Polynomial) lt);
			poly.divide((Polynomial) rt);
			return poly;
		}
		// vector / vector (complex division Michael Borcherds 2007-12-09)
		else if (lt.isVectorValue() && rt.isVectorValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.complexDivide(vec, ((VectorValue) rt).getVector(), vec);
			return vec;

		}
		// number / vector (complex division Michael Borcherds 2007-12-09)
		else if (lt.isNumberValue() && rt.isVectorValue()) {
			vec = ((VectorValue) rt).getVector(); // just to
													// initialise
													// vec
			GeoVec2D.complexDivide((NumberValue) lt,
					((VectorValue) rt).getVector(), vec);
			return vec;

		}

		else if ((rt instanceof GeoFunction) && lt.isNumberValue()) {
			return GeoFunction.applyNumberSymb(Operation.DIVIDE,
					(GeoFunction) rt, left, false);
		} else {
			str = new String[] { "IllegalDivision", lt.toString(errorTemplate),
					"/", rt.toString(errorTemplate) };
			throw new MyError(l10n, str);
		}
	}
	/**
	 * Performs subtraction
	 * @param lt left argument (evaluated)
	 * @param rt right argument (evaluated)
	 * @return result
	 */
	public ExpressionValue handleMinus(ExpressionValue lt, ExpressionValue rt) {
		String[] str;
		Kernel kernel = lt.getKernel();
		MyDouble num;
		GeoVec2D vec;
		Polynomial poly;
		// number - number
		if (lt.isNumberValue() && rt.isNumberValue()) {
			num = ((NumberValue) lt).getNumber();
			MyDouble.sub(num, (NumberValue) rt, num);
			return num;
		}
		// vector - vector
		else if (lt.isVectorValue() && rt.isVectorValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.sub(vec, ((VectorValue) rt).getVector(), vec);
			return vec;
		}
		// 3D vector - 3D vector
		/*
		 * else if (lt.isVector3DValue() && rt.isVector3DValue()) { Geo3DVec
		 * vec3D = ((Vector3DValue)lt).get3DVec(); Geo3DVec.sub(vec3D,
		 * ((Vector3DValue)rt).get3DVec(), vec3D); return vec3D; }
		 */
		// vector - number (for complex subtraction)
		else if (lt.isVectorValue() && rt.isNumberValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.sub(vec, ((NumberValue) rt), vec);
			return vec;
		}
		// number - vector (for complex subtraction)
		else if (lt.isNumberValue() && rt.isVectorValue()) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.sub(((NumberValue) lt), vec, vec);
			return vec;
		}
		// list - vector
		else if (lt.isListValue() && rt.isVectorValue()) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.sub(vec, ((ListValue) lt), vec, false);
			return vec;
		}
		// vector - list
		else if (rt.isListValue() && lt.isVectorValue()) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.sub(vec, ((ListValue) rt), vec, true);
			return vec;
		}
		// polynomial - polynomial
		else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {
			poly = new Polynomial(kernel, (Polynomial) lt);
			poly.sub((Polynomial) rt);
			return poly;
		} else {
			str = new String[] { "IllegalSubtraction",
					lt.toString(errorTemplate), "-", rt.toString(errorTemplate) };
			App.debug(lt.getClass() + "," + rt.getClass());
			throw new MyError(l10n, str);
		}
	}
	/**
	 * Performs power
	 * @param lt left argument (evaluated)
	 * @param rt right argument (evaluated)
	 * @param right right argument before evaluation
	 * 
	 * @return result
	 */
	public ExpressionValue handlePower(ExpressionValue lt, ExpressionValue rt,
			ExpressionValue right) {
		String[] str;
		Kernel kernel = lt.getKernel();
		Polynomial poly;
		MyDouble num;
		GeoVec2D vec, vec2;
		// number ^ number
		if (lt.isNumberValue() && rt.isNumberValue()) {
			num = ((NumberValue) lt).getNumber();
			double base = num.getDouble();
			MyDouble exponent = ((NumberValue) rt).getNumber();

			// special case: e^exponent (Euler number)
			if (base == Math.E) {
				return exponent.exp();
			}

			// special case: left side is negative and
			// right side is a fraction a/b with a and b integers
			// x^(a/b) := (x^a)^(1/b)
			if ((base < 0) && right.isExpressionNode()) {
				ExpressionNode node = (ExpressionNode) right;
				if (node.getOperation().equals(Operation.DIVIDE)) {
					// check if we have a/b with a and b integers
					double a = node.getLeft().evaluateNum().getDouble();
					long al = Math.round(a);
					if (Kernel.isEqual(a, al)) { // a is integer
						double b = node.getRight().evaluateNum().getDouble();
						long bl = Math.round(b);
						if (b == 0) {
							// (x^a)^(1/0)
							num.set(Double.NaN);
						} else if (Kernel.isEqual(b, bl)) { // b is
															// integer
							// divide through greatest common divisor of a
							// and b
							long gcd = Kernel.gcd(al, bl);
							al = al / gcd;
							bl = bl / gcd;

							// we will now evaluate (x^a)^(1/b) instead of
							// x^(a/b)
							// set base = x^a
							if (al != 1) {
								base = Math.pow(base, al);
							}
							if (base > 0) {
								// base > 0 => base^(1/b) is no problem
								num.set(Math.pow(base, 1d / bl));
							} else { // base < 0
								boolean oddB = (Math.abs(bl) % 2) == 1;
								if (oddB) {
									// base < 0 and b odd: (base)^(1/b) =
									// -(-base^(1/b))
									num.set(-Math.pow(-base, 1d / bl));
								} else {
									// base < 0 and a & b even: (base)^(1/b)
									// = undefined
									num.set(Double.NaN);
								}
							}
							return num;
						}
					}
				}
			}

			// standard case
			MyDouble.pow(num, exponent, num);
			return num;
		}
		/*
		 * // vector ^ 2 (inner product) (3D) else if (lt.isVector3DValue() &&
		 * rt.isNumberValue()) { num = ((NumberValue)rt).getNumber(); Geo3DVec
		 * vec3D = ((Vector3DValue)lt).get3DVec(); if (num.getDouble() == 2.0) {
		 * Geo3DVec.inner(vec3D, vec3D, num); } else { num.set(Double.NaN); }
		 * return num; }
		 */
		// vector ^ 2 (inner product)
		else if (lt.isVectorValue() && rt.isNumberValue()) {
			// if (!rt.isConstant()) {
			// String [] str = new String[]{ "ExponentMustBeConstant",
			// lt.toString(),
			// "^", rt.toString() };
			// throw new MyError(l10n, str);
			// }
			vec = ((VectorValue) lt).getVector();

			if (vec.getMode() == Kernel.COORD_COMPLEX) {

				// complex power
				GeoVec2D.complexPower(vec, ((NumberValue) rt), vec);
				return vec;

			}
			num = ((NumberValue) rt).getNumber();
			// inner/scalar/dot product
			if (num.getDouble() == 2.0) {
				GeoVec2D.inner(vec, vec, num);
				return num;
			}
			num.set(Double.NaN);
			return num;
			// String [] str = new String[]{ "IllegalExponent",
			// lt.toString(),
			// "^", rt.toString() };
			// throw new MyError(l10n, str);
		} else if (lt.isVectorValue() && rt.isVectorValue()) {
			// if (!rt.isConstant()) {
			// String [] str = new String[]{ "ExponentMustBeConstant",
			// lt.toString(),
			// "^", rt.toString() };
			// throw new MyError(l10n, str);
			// }
			vec = ((VectorValue) lt).getVector();
			vec2 = ((VectorValue) rt).getVector();

			// complex power

			GeoVec2D.complexPower(vec, vec2, vec);
			return vec;

		} else if (lt.isNumberValue() && rt.isVectorValue()) {
			// if (!rt.isConstant()) {
			// String [] str = new String[]{ "ExponentMustBeConstant",
			// lt.toString(),
			// "^", rt.toString() };
			// throw new MyError(l10n, str);
			// }
			num = ((NumberValue) lt).getNumber();
			vec = ((VectorValue) rt).getVector();

			// real ^ complex

			GeoVec2D.complexPower(num, vec, vec);
			return vec;

		}
		// polynomial ^ number
		else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {
			// the exponent must be a number
			if (((Polynomial) rt).degree() != 0) {
				str = new String[] { "ExponentMustBeInteger",
						lt.toString(errorTemplate), "^",
						rt.toString(errorTemplate) };
				throw new MyError(l10n, str);
			}

			// is the base also a number? In this case pull base^exponent
			// together into lt polynomial
			boolean baseIsNumber = ((Polynomial) lt).degree() == 0;
			if (baseIsNumber) {
				Term base = ((Polynomial) lt).getTerm(0);
				Term exponent = ((Polynomial) rt).getTerm(0);
				Term newBase = new Term(new ExpressionNode(kernel,
						base.getCoefficient(), Operation.POWER,
						exponent.getCoefficient()), "");

				return new Polynomial(kernel, newBase);
			}

			// number is not a base
			if (!rt.isConstant()) {
				str = new String[] { "ExponentMustBeConstant",
						lt.toString(errorTemplate), "^",
						rt.toString(errorTemplate) };
				throw new MyError(l10n, str);
			}

			// get constant coefficent of given polynomial
			double exponent = ((Polynomial) rt).getConstantCoeffValue();
			if ((Kernel.isInteger(exponent) && ((int) exponent >= 0))) {
				poly = new Polynomial(kernel, (Polynomial) lt);
				poly.power((int) exponent);
				return poly;
			}
			str = new String[] { "ExponentMustBeInteger",
					lt.toString(errorTemplate), "^", rt.toString(errorTemplate) };
			throw new MyError(l10n, str);
		} else {
			// AbstractApplication.debug("power: lt :" + lt.getClass()
			// + ", rt: " + rt.getClass());
			str = new String[] { "IllegalExponent", lt.toString(errorTemplate),
					"^", rt.toString(errorTemplate) };
			throw new MyError(l10n, str);
		}
	}

	/**
	 * Computes value of function in given point (or throws error)
	 * @param lt function
	 * @param rt value of variable
	 * @return value of function at given point
	 */
	public ExpressionValue handleFunction(ExpressionValue lt, ExpressionValue rt) {
		String[] str;
		Kernel kernel = lt.getKernel();
		// function(number)
		if (rt.isNumberValue()) {
			if (lt instanceof Evaluatable) {
				NumberValue arg = (NumberValue) rt;
				if ((lt instanceof GeoFunction)
						&& ((GeoFunction) lt).isBooleanFunction()) {
					return new MyBoolean(kernel,
							((GeoFunction) lt).evaluateBoolean(arg.getDouble()));
				}
				return arg.getNumber().apply((Evaluatable) lt);
			}
		} else if (rt instanceof GeoPoint) {
			if (lt instanceof Evaluatable) {
				GeoPoint pt = (GeoPoint) rt;
				if (lt instanceof GeoFunction) {
					FunctionNVar fun = ((GeoFunction) lt).getFunction();
					if (fun.isBooleanFunction()) {
						return new MyBoolean(kernel, fun.evaluateBoolean(pt));
					}
					return new MyDouble(kernel, fun.evaluate(pt));
				} else if (lt instanceof GeoFunctionable) {
					// eg GeoLine
					return new MyDouble(kernel, ((GeoFunctionable) lt)
							.getGeoFunction().getFunction().evaluate(pt));
				} else {
					App.error("missing case in ExpressionNodeEvaluator");
				}
			}
		} else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()
				&& (((Polynomial) rt).degree() == 0)) {
			ExpressionValue c1 = ((Polynomial) lt).getConstantCoefficient();
			ExpressionValue c2 = ((Polynomial) rt).getConstantCoefficient();
			return new Polynomial(kernel, new Term(new ExpressionNode(kernel,
					c1, Operation.FUNCTION, c2), ""));
		}
		// Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass()
		// + " rt: " + rt + ", " + rt.getClass());
		str = new String[] { "IllegalArgument", rt.toString(errorTemplate) };
		throw new MyError(l10n, str);

	}
	
	
	/**
	 * Evaluate function in multiple variables
	 * @param lt left argument (function)
	 * @param rt right argument (MyList of variable values)
	 * @return result (number)
	 */
	public ExpressionValue handleFunctionNVar(ExpressionValue lt,
			ExpressionValue rt) {
		if (rt.isListValue() && (lt instanceof FunctionalNVar)) {
			Kernel kernel = lt.getKernel();
			FunctionNVar funN = ((FunctionalNVar) lt).getFunction();
			ListValue list = (ListValue) rt;
			if (funN.getVarNumber() == list.size()) {
				double[] args = list.toDouble();
				if (args != null) {
					if (funN.isBooleanFunction()) {
						return new MyBoolean(kernel, funN.evaluateBoolean(args));
					}
					return new MyDouble(kernel, funN.evaluate(args));
				}
				// let's assume that we called this as f(x,y) and we
				// actually want the function
				return lt;
			} else if (list.size() == 1) {
				ExpressionValue ev = list.getMyList().getListElement(0)
						.evaluate(StringTemplate.defaultTemplate);
				if ((funN.getVarNumber() == 2) && (ev instanceof GeoPoint)) {
					GeoPoint pt = (GeoPoint) ev;
					if (funN.isBooleanFunction()) {
						return new MyBoolean(kernel, funN.evaluateBoolean(pt));
					}
					return new MyDouble(kernel, funN.evaluate(pt));
				} else if ((funN.getVarNumber() == 2)
						&& (ev instanceof MyVecNode)) {
					MyVecNode pt = (MyVecNode) ev;
					double[] vals = new double[] {
							pt.getX().evaluateNum().getDouble(),
							pt.getY().evaluateNum().getDouble() };
					if (funN.isBooleanFunction()) {
						return new MyBoolean(kernel, funN.evaluateBoolean(vals));
					}
					return new MyDouble(kernel, funN.evaluate(vals));
				} else if ((ev instanceof ListValue)
						&& ((ListValue) ev).getMyList().getListElement(0)
								.evaluate(StringTemplate.defaultTemplate)
								.isNumberValue()) {
					double[] vals = ((ListValue) ev).toDouble();
					if (vals != null) {
						if (funN.isBooleanFunction()) {
							return new MyBoolean(kernel,
									funN.evaluateBoolean(vals));
						}
						return new MyDouble(kernel, funN.evaluate(vals));
					}
				} else if (ev instanceof ListValue) { // f(x,y) called with
					// list of points
					MyList l = ((ListValue) ev).getMyList();
					MyList ret = new MyList(kernel);
					for (int i = 0; i < l.size(); i++) {
						MyList lArg = new MyList(kernel); // need to wrap
						// arguments to
						// f(x,y) in
						// MyList
						lArg.addListElement(l.getListElement(i));
						ret.addListElement(new ExpressionNode(kernel, funN,
								Operation.FUNCTION_NVAR, lArg));
					}
					return ret;
				}

				// let's assume that we called this as f(x,y) and we
				// actually want the function
				return lt;
			}
		}
		// Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass() +
		// " rt: " + rt + ", " + rt.getClass());
		String[] str3 = { "IllegalArgument", rt.toString(errorTemplate) };
		throw new MyError(l10n, str3);
	}
	
	/**
	 * Throw error for unary boolean operation
	 * @param arg operation argument 
	 * @param opname operation string
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalBoolean(ExpressionValue arg, String opname) {
		String[] str = new String[] { "IllegalBoolean", opname,
				arg.toString(errorTemplate) };
		throw new MyError(l10n, str);
	}
	/**
	 * Throw illegal argument exception for multivariable  builtin function
	 * @param lt left argument
	 * @param rt right argument
	 * @param opname operation name
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalArgument(ExpressionValue lt,
			ExpressionValue rt, String opname) {
		String[] str = new String[] { "IllegalArgument", opname,
				lt.toString(errorTemplate), ",", rt.toString(errorTemplate),
				")" };
		throw new MyError(l10n, str);
	}

	/**
	 * Throw simple illegal argument exception
	 * @param arg argument
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalArgument(ExpressionValue arg) {
		String[] str = new String[] { "IllegalArgument",
				arg.toString(errorTemplate) };
		throw new MyError(l10n, str);
	}

	/**
	 * Throw error for infix binary operation
	 * @param lt left argument
	 * @param rt right argument
	 * @param type type (InvalidMultiplication, InvalidAddition, ...)
	 * @param opname operator string
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalBinary(ExpressionValue lt,
			ExpressionValue rt, String type, String opname) {
		String[] str = new String[] { type, lt.toString(errorTemplate), opname,
				rt.toString(errorTemplate) };
		throw new MyError(l10n, str);

	}

	/**
	 * Throw illegal comparison error
	 * @param lt left argument
	 * @param rt rigt argument
	 * @param opname comparison operator
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalComparison(ExpressionValue lt,
			ExpressionValue rt, String opname) {
		String[] str = new String[] { "IllegalComparison",
				lt.toString(errorTemplate), opname, rt.toString(errorTemplate) };
		throw new MyError(l10n, str);

	}

	/**
	 * Throw illegal list operation error
	 * @param lt left argument
	 * @param rt rigt argument
	 * @param opname list operator
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public ExpressionValue illegalListOp(ExpressionValue lt,
			ExpressionValue rt, String opname) {
		String[] str = new String[] { "IllegalListOperation",
				lt.toString(errorTemplate), opname, rt.toString(errorTemplate) };
		throw new MyError(l10n, str);

	}
	
	/**
	 * Check whether lt is constant polynomial and compute op(lt) if it is; if not
	 * throw illegal argument "opname lt)"
	 * @param lt argument
	 * @param op operation
	 * @param opname operation name (including "(")
	 * @return op(lt) or error
	 * @throws MyError if not polynomial or not constant
	 */
	public ExpressionValue polynomialOrDie(ExpressionValue lt, Operation op,
			String opname) {
		return polynomialOrDie(lt, op, opname, ")");
	}
	/**
	 * Check whether lt is constant polynomial and compute op(lt) if it is; if not
	 * throw illegal argument "prefix lt suffix"
	 * @param lt argument
	 * @param op operation
	 * @param prefix prefix of error message
	 * @param suffix of error message
	 * @return op(lt) if lt is constant poly
	 * @throws MyError if not polynomial or not constant
	 */
	public ExpressionValue polynomialOrDie(ExpressionValue lt, Operation op,
			String prefix, String suffix) {
		Kernel kernel = lt.getKernel();
		if (lt.isPolynomialInstance() && (((Polynomial) lt).degree() == 0)) {
			ExpressionValue coeff = ((Polynomial) lt).getConstantCoefficient();
			return new Polynomial(kernel, new Term(new ExpressionNode(kernel,
					coeff, op, null), ""));
		}
		String[] strings = new String[] { "IllegalArgument", prefix,
				lt.toString(errorTemplate), suffix };
		throw new MyError(l10n, strings);

	}
}

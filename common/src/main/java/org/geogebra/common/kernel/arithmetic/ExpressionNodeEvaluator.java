package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * @author ggb3D
 * 
 *         Evaluator for ExpressionNode (used in Operation.evaluate())
 */
public class ExpressionNodeEvaluator implements ExpressionNodeConstants {

	private Localization loc;
	private OperationArgumentFilter filter;

	/**
	 * Kernel used to create the results
	 */
	@Weak
	protected Kernel kernel;

	/**
	 * Creates a new expression node evaluator
	 * @param loc localization
	 * @param kernel kernel
	 */
	public ExpressionNodeEvaluator(Localization loc, Kernel kernel) {
		this(loc, kernel, null);
	}

	/**
	 * Creates new expression node evaluator
	 * 
	 * @param loc
	 *            localization for errors
	 * @param kernel
	 *            kernel
	 */
	public ExpressionNodeEvaluator(Localization loc, Kernel kernel,
								   OperationArgumentFilter filter) {
		this.loc = loc;
		this.kernel = kernel;
		this.filter = filter;
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

		ExpressionValue lt, rt;

		lt = left.evaluate(tpl); // left tree
		// TODO Evaluation of equations is expensive, but better soln needed
		// #4816
		if (left instanceof Equation) {
			expressionNode.setLeft(lt);
		}
		if (operation.equals(Operation.NO_OPERATION)) {
			return lt;
		}
		rt = right.evaluate(tpl); // right tree

		// handle list operations first
		if (filter != null && !filter.isAllowed(operation, lt, rt)) {
			throw illegalBinary(lt, rt, Errors.IllegalArgument, operation.name());
		}
		ExpressionValue special = handleSpecial(lt, rt, left, right, operation,
				tpl);
		if (special != null) {
			return special;
		}
		// NON-List operations (apart from EQUAL_BOOLEAN and list + text)
		return handleOp(operation, lt, rt, left, right, tpl, holdsLaTeXtext);
	}

	/**
	 * @param op
	 *            operation
	 * @param lt
	 *            left evaluated
	 * @param rt
	 *            right evaluated
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param tpl
	 *            template for string ops
	 * @param holdsLaTeX
	 *            whether result should be latex
	 * @return operation result
	 */
	protected ExpressionValue handleOp(Operation op, ExpressionValue lt,
			ExpressionValue rt, ExpressionValue left, ExpressionValue right,
			StringTemplate tpl, boolean holdsLaTeX) {
		return op.handle(this, lt, rt, left, right, tpl, holdsLaTeX);
	}

	/**
	 * 
	 * @param myList
	 *            list (matrix)
	 * @param rt
	 *            vector
	 * @return list (matrix) * vector/point
	 */
	protected ExpressionValue multiply(MyList myList, VectorNDValue rt) {
		if (rt instanceof VectorValue) {
			return multiply2D(myList, myList.getMatrixRows(),
					myList.getMatrixCols(), (VectorValue) rt);
		}

		return null;
	}

	/**
	 * 
	 * @param myList
	 *            list (matrix)
	 * @param rows
	 *            matrix rows length
	 * @param cols
	 *            matrix cols length
	 * @param rt
	 *            vector
	 * @return list (matrix) * 2D vector / point
	 */
	static protected ExpressionValue multiply2D(MyList myList, int rows,
			int cols, VectorValue rt) {

		return multiply2D(myList, rows, cols, rt, rt.getVector());
	}

	/**
	 * @param myList
	 *            list (matrix)
	 * @param rows
	 *            matrix rows length
	 * @param cols
	 *            matrix cols length
	 * @param rt
	 *            vector
	 * @param myVec
	 *            vector set to result
	 * @return list (matrix) * 2D vector / point
	 */
	protected static ExpressionValue multiply2D(MyList myList, int rows,
			int cols, VectorNDValue rt, GeoVec2D myVec) {

		if ((rows == 2) && (cols == 2)) {
			// 2x2 matrix
			GeoVec2D.multiplyMatrix(myList, rt.getVector(), myVec);

			return myVec;
		} else if ((rows == 3) && (cols == 3)) {
			// 3x3 matrix, assume it's affine
			myVec.multiplyMatrixAffine(myList, rt);
			return myVec;
		}

		return null;
	}

	private ExpressionValue handleSpecial(ExpressionValue lt,
			ExpressionValue rt, ExpressionValue left, ExpressionValue right,
			Operation operation, StringTemplate tpl) {
		if (lt instanceof ListValue) {
			if ((operation == Operation.MULTIPLY)
					&& rt instanceof VectorNDValue) {
				MyList myList = ((ListValue) lt).getMyList();
				if (myList.isMatrix()) {
					ExpressionValue ret = multiply(myList, (VectorNDValue) rt);
					if (ret != null) {
						return ret;
					}
				}

			} else if ((operation == Operation.VECTORPRODUCT)
					&& rt instanceof ListValue) {

				MyList listL = ((ListValue) lt.evaluate(tpl)).getMyList();
				MyList listR = ((ListValue) rt.evaluate(tpl)).getMyList();
				if (((listL.size() == 3) && (listR.size() == 3))
						|| ((listL.size() == 2) && (listR.size() == 2))) {
					listL.vectorProduct(listR);
					return listL;
				}

			}
			// we cannot use elseif here as we might need multiplication
			if ((operation != Operation.IF_LIST)
					&& (operation != Operation.PLUSMINUS)
					&& (operation != Operation.MATRIXTOVECTOR)
					&& (operation != Operation.EQUAL_BOOLEAN)
					&& (operation != Operation.NOT_EQUAL // ditto
					) && (operation != Operation.IS_SUBSET_OF // ditto
					) && (operation != Operation.IS_SUBSET_OF_STRICT // ditto
					) && (operation != Operation.SET_DIFFERENCE // ditto
					) && (operation != Operation.ELEMENT_OF // list1(1) to get
															// first element
					) && (operation != Operation.IS_ELEMENT_OF // list1(1) to
																// get
					// first element
					) && !(rt instanceof VectorValue) // eg {1,2} + (1,2)
					&& !(rt instanceof TextValue)) { // bugfix "" + {1,2}
				// list lt operation rt
				return listOperation((ListValue) lt, operation, rt, right,
						true, tpl);
			}
		} else if (rt instanceof ListValue
				// skip operations acting on lists as a whole
				&& !operation.equals(Operation.EQUAL_BOOLEAN)
				&& !operation.equals(Operation.NOT_EQUAL)
				&& !operation.equals(Operation.FUNCTION_NVAR)
				&& !(operation.equals(Operation.VEC_FUNCTION)
						&& lt.isGeoElement()
						&& ((GeoElement) lt).isGeoSurfaceCartesian())
				&& !operation.equals(Operation.FREEHAND)
				&& !operation.equals(Operation.DATA)
				&& (operation != Operation.PLUSMINUS)
				&& !(lt instanceof VectorValue && operation.isPlusorMinus()) // eg
																				// {1,2}
																				// +
																				// (1,2)
				&& !(lt instanceof TextValue) // e.g. "" + {1,2}
				&& !operation.equals(Operation.IS_ELEMENT_OF)) {

			if (operation == Operation.MULTIPLY && lt instanceof VectorValue) {
				MyList myList = ((ListValue) rt).getMyList();
				boolean isMatrix = myList.isMatrix();
				int rows = myList.getMatrixRows();
				int cols = myList.getMatrixCols();
				if (isMatrix && (rows == 2) && (cols == 2)) {
					GeoVec2D myVec = ((VectorValue) lt).getVector();
					// 2x2 matrix
					myVec.multiplyMatrixLeft(myList);

					return myVec;
				}
			}

			// lt operation list rt

			return listOperation((ListValue) rt, operation, lt, left, false,
					tpl);
		}

		else if ((lt instanceof FunctionalNVar)
				&& (rt instanceof FunctionalNVar)
				&& !operation.equals(Operation.EQUAL_BOOLEAN)
				&& !operation.equals(Operation.NOT_EQUAL)) {
			return GeoFunction.operationSymb(operation, (FunctionalNVar) lt,
					(FunctionalNVar) rt);
		} else if ((lt instanceof GeoCurveCartesianND)
				&& (operation == Operation.XCOORD
						|| operation == Operation.YCOORD
						|| operation == Operation.ZCOORD)) {
			return GeoFunction.operationSymb(operation,
					(GeoCurveCartesianND) lt);
		}
		// we want to use function arithmetic in cases like f*2 or f+x^2, but
		// not for f(2), f'(2) etc.
		else if ((lt instanceof FunctionalNVar) && rt instanceof NumberValue
				&& (operation.ordinal() < Operation.FUNCTION.ordinal())) {
			return GeoFunction.applyNumberSymb(operation, (FunctionalNVar) lt,
					right, true);
		} else if ((rt instanceof FunctionalNVar)
				&& lt instanceof NumberValue) {
			return GeoFunction.applyNumberSymb(operation, (FunctionalNVar) rt,
					left, false);
		}
		return null;
	}

	private ExpressionValue listOperation(ListValue lt, Operation operation,
			ExpressionValue rt, ExpressionValue right, boolean b,
			StringTemplate tpl) {
		boolean symbolic = right.wrap().containsFreeFunctionVariable(null);
		ExpressionValue myRt = symbolic && !(rt instanceof ListValue) ? right
				: rt;
		MyList myList = symbolic ? lt.getMyList().deepCopy(kernel) : lt
				.getMyList();
		// list lt operation rt
		myList.apply(operation, myRt, b, tpl);
		return myList;
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
		if (lt instanceof BooleanValue && rt instanceof BooleanValue) {
			return new MyBoolean(kernel, ((BooleanValue) lt)
					.getBoolean() == ((BooleanValue) rt).getBoolean());
		} else if (lt instanceof NumberValue && rt instanceof NumberValue) {
			return new MyBoolean(kernel,
					DoubleUtil.isEqual(lt.evaluateDouble(), rt.evaluateDouble()));
		} else if (lt instanceof TextValue && rt instanceof TextValue) {

			String strL = lt.toValueString(tpl);
			String strR = rt.toValueString(tpl);

			// needed for eg Sequence[If[Element[list1,i]=="b",0,1],i,i,i]
			if ((strL == null) || (strR == null)) {
				return new MyBoolean(kernel, false);
			}

			return new MyBoolean(kernel, strL.equals(strR));
		} else if (lt instanceof ListValue && rt instanceof ListValue) {

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
		} else if (lt instanceof VectorValue && rt instanceof VectorValue) {
			VectorValue vec1 = (VectorValue) lt;
			VectorValue vec2 = (VectorValue) rt;
			return new MyBoolean(kernel,
					vec1.getVector().isEqual(vec2.getVector()));
		} else if (lt instanceof Vector3DValue && rt instanceof Vector3DValue) {
			Vector3DValue vec1 = (Vector3DValue) lt;
			Vector3DValue vec2 = (Vector3DValue) rt;
			return new MyBoolean(kernel,
					vec1.getVector().isEqual(vec2.getVector()));
		}

		return new MyBoolean(kernel, false);
	}

	/**
	 * @param arg
	 *            vector or line
	 * @param op
	 *            XCOORD or REAL
	 * @return x coordinate
	 */
	public double handleXcoord(ExpressionValue arg, Operation op) {
		if (arg instanceof VectorValue) {
			return ((VectorValue) arg).getVector().getX();
		} else if (arg instanceof Vector3DValue) {
			return ((Vector3DValue) arg).getPointAsDouble()[0];
		} else if (arg instanceof GeoLine) {
			return ((GeoLine) arg).x;
		} else if (op == Operation.REAL && arg instanceof NumberValue) {
			// real(3) should return 3
			return arg.evaluateDouble();
		} else {
			throw polynomialOrDie(arg,
					op == Operation.XCOORD ? "x(" : "real(");
		}

	}

	/**
	 * @param arg
	 *            vector or line
	 * @param op
	 *            YCOORD or IMAGINARY
	 * @return y coordinate
	 */
	public double handleYcoord(ExpressionValue arg, Operation op) {

		// y(vector)
		if (arg instanceof VectorValue) {
			return ((VectorValue) arg).getVector().getY();
		} else if (arg instanceof Vector3DValue) {
			return ((Vector3DValue) arg).getPointAsDouble()[1];
		} else if (arg instanceof GeoLine) {
			return ((GeoLine) arg).y;
		} else if (op == Operation.IMAGINARY && arg instanceof NumberValue) {
			// imaginary(3) should return 0
			return 0;
		} else {
			throw polynomialOrDie(arg,
					op == Operation.YCOORD ? "y(" : "imaginary(");
		}
	}

	/**
	 * @param lt
	 *            vector or line
	 * @return z coordinate
	 */
	public double handleZcoord(ExpressionValue lt) {
		if (lt instanceof VectorValue) {
			return 0;
		} else if (lt instanceof Vector3DValue) {
			return ((Vector3DValue) lt).getPointAsDouble()[2];
		} else if (lt instanceof GeoLine) {
			return ((GeoLine) lt).z;
		}
		throw polynomialOrDie(lt, "z(");
	}

	/**
	 * Performs multiplication
	 * 
	 * @param lt
	 *            left argument
	 * @param rt
	 *            right argument
	 * @param tpl
	 *            string template (may be string concatenation)
	 * @param holdsLaTeXtext
	 *            whether parent node holds LaTeX
	 * @return result
	 */
	public ExpressionValue handleMult(ExpressionValue lt, ExpressionValue rt,
			StringTemplate tpl, boolean holdsLaTeXtext) {
		MyDouble num;
		MyStringBuffer msb;

		// Log.debug(lt.getClass()+" "+lt.toString());
		// Log.debug(rt.getClass()+" "+rt.toString());

		if (lt instanceof NumberValue) {
			// number * number
			if (rt instanceof NumberValue) {
				num = ((NumberValue) lt).getNumber();
				MyDouble.mult(num, (NumberValue) rt, num);
				return num;
			}
			// number * vector
			else if (rt instanceof VectorNDValue) {
				return multiply((NumberValue) lt, (VectorNDValue) rt);
			}
			// number * boolean -- already in number * number

		}
		// text concatenation (left)
		if (lt instanceof TextValue) {
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
		else if (rt instanceof TextValue) {
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
		if (lt instanceof BooleanValue && rt instanceof NumberValue) {
			num = ((NumberValue) rt).getNumber();
			MyDouble.mult(num, ((BooleanValue) lt).getDouble(), num);
			return num;
		}
		// vector * ...
		else if (lt instanceof VectorNDValue) {
			// vector * number
			if (rt instanceof NumberValue) {
				return multiply((NumberValue) rt, (VectorNDValue) lt);
			}
			// vector * vector (inner/dot product)
			else if (rt instanceof VectorNDValue) {
				if (((VectorNDValue) lt)
						.getToStringMode() == Kernel.COORD_COMPLEX
						|| ((VectorNDValue) rt)
								.getToStringMode() == Kernel.COORD_COMPLEX) {
					// complex multiply
					return complexMult((VectorNDValue) lt, (VectorNDValue) rt,
							kernel);
				}
				return innerProduct((VectorNDValue) lt, (VectorNDValue) rt,
						kernel);
			}
			throw illegalBinary(lt, rt, Errors.IllegalMultiplication, "*");
		}
		throw illegalBinary(lt, rt, Errors.IllegalMultiplication, "*");
	}

	/**
	 * 
	 * @param en
	 *            number
	 * @param ev
	 *            vector
	 * @return en*ev
	 */
	protected ExpressionValue multiply(NumberValue en, VectorNDValue ev) {

		if (ev instanceof VectorValue) {

			GeoVec2D vec = ((VectorValue) ev).getVector();
			GeoVec2D.mult(vec, en.getDouble(), vec);
			return vec;
		}

		Geo3DVecInterface vec = ((Vector3DValue) ev).getVector();
		vec.mult(en.getDouble());
		return vec;
	}

	/**
	 * 
	 * @param ev1
	 *            first vector
	 * @param ev2
	 *            second vector
	 * @param kernel0
	 *            kernel
	 * @return ev1*ev2 complex product
	 */
	protected ExpressionValue complexMult(VectorNDValue ev1, VectorNDValue ev2,
			Kernel kernel0) {
		GeoVec2D vec = ((VectorValue) ev1).getVector();
		GeoVec2D.complexMultiply(vec, ((VectorValue) ev2).getVector(), vec);
		return vec;
	}

	/**
	 * 
	 * @param ev1
	 *            first vector
	 * @param ev2
	 *            second vector
	 * @param kernel0
	 *            kernel
	 * @return ev1*ev2 inner product
	 */
	protected ExpressionValue innerProduct(VectorNDValue ev1, VectorNDValue ev2,
			Kernel kernel0) {
		MyDouble num = new MyDouble(kernel0);
		GeoVec2D.inner(((VectorValue) ev1).getVector(),
				((VectorValue) ev2).getVector(), num);
		return num;
	}

	/**
	 * Performs addition
	 * 
	 * @param lt
	 *            left argument
	 * @param rt
	 *            right argument
	 * @param tpl
	 *            string template (may be string concatenation)
	 * @param holdsLaTeXtext
	 *            whether parent node holds LaTeX
	 * @return result
	 */
	public ExpressionValue handlePlus(ExpressionValue lt, ExpressionValue rt,
			StringTemplate tpl, boolean holdsLaTeXtext) {
		MyDouble num;
		GeoVec2D vec;
		MyStringBuffer msb;
		if (lt instanceof NumberValue && rt instanceof NumberValue) {
			num = ((NumberValue) lt).getNumber();
			MyDouble.add(num, ((NumberValue) rt).getNumber(), num);
			return num;
		}
		// vector + vector
		else if (lt instanceof VectorValue && rt instanceof VectorValue) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.add(vec, ((VectorValue) rt).getVector(), vec);
			return vec;
		}
		// vector + number (for complex addition)
		else if (lt instanceof VectorValue && rt instanceof NumberValue) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.add(vec, ((NumberValue) rt), vec);
			return vec;
		}
		// number + vector (for complex addition)
		else if (lt instanceof NumberValue && rt instanceof VectorValue) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.add(vec, ((NumberValue) lt), vec);
			return vec;
		}
		// list + vector
		else if (lt instanceof ListValue && rt instanceof VectorValue) {
			MyList list = ((ListValue) lt).getMyList();
			if (list.size() > 0) {
				ExpressionValue ev = list.getListElement(0);
				if (ev instanceof NumberValue) { // eg {1,2} + (1,2) treat as
													// point, ev is evaluated
													// before
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
		else if (rt instanceof ListValue && lt instanceof VectorValue) {
			MyList list = ((ListValue) rt).getMyList();
			if (list.size() > 0) {
				ExpressionValue ev = list.getListElement(0);
				if (ev instanceof NumberValue) { // eg {1,2} + (1,2) treat as
													// point, ev is evaluated
													// before
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
		else if (lt instanceof TextValue) {
			msb = ((TextValue) lt).getText();
			if (holdsLaTeXtext) {
				msb.append(rt.toLaTeXString(false, tpl));
			} else {
				if (rt.isGeoElement()) {
					GeoElement geo = (GeoElement) rt;
					msb.append(getGeoString(geo, tpl));
				} else {
					msb.append(rt.toValueString(tpl));
				}
			}
			return msb;
		} // text concatenation (right)
		else if (rt instanceof TextValue) {
			msb = ((TextValue) rt).getText();
			if (holdsLaTeXtext) {
				msb.insert(0, lt.toLaTeXString(false, tpl));
			} else {
				if (lt.isGeoElement()) {
					GeoElement geo = (GeoElement) lt;
					msb.insert(0, getGeoString(geo, tpl));
				} else {
					msb.insert(0, lt.toValueString(tpl));
				}
			}
			return msb;
		}
		// polynomial + polynomial
		else {
			throw new MyError(loc, Errors.IllegalAddition, lt, "+", rt);
		}

	}

	/**
	 * @param geoElement The GeoElement that should be transformed into string
	 * @param tpl The StringTemplate based on which the GeoElement will be transformed into string
	 * @return The string form of the GeoElement
	 */
	private static String getGeoString(GeoElement geoElement, StringTemplate tpl) {
		return !geoElement.isAllowedToShowValue()
				? geoElement.getDefinition(tpl)
				: geoElement.toDefinedValueString(tpl);
	}

	/**
	 * Performs division
	 * 
	 * @param lt
	 *            left argument (evaluated)
	 * @param rt
	 *            right argument (evaluated)
	 * @param left
	 *            left argument before evaluation
	 * @param right
	 *            right argument before evaluation
	 * 
	 * @return result
	 */
	public ExpressionValue handleDivide(ExpressionValue lt, ExpressionValue rt,
			ExpressionValue left, ExpressionValue right) {
		// sin(number)
		MyDouble num;
		GeoVec2D vec;
		if (rt instanceof NumberValue) {
			// number / number
			if (lt instanceof NumberValue) {
				num = ((NumberValue) lt).getNumber();
				MyDouble.div(num, ((NumberValue) rt).getNumber(), num);
				return num;
			}
			// vector / number
			else if (lt instanceof VectorValue) {
				vec = ((VectorValue) lt).getVector();
				GeoVec2D.div(vec, rt.evaluateDouble(), vec);
				return vec;
			} else if (lt instanceof GeoFunction) {
				return GeoFunction.applyNumberSymb(Operation.DIVIDE,
						(GeoFunction) lt, right, true);
			}
			else {
				throw new MyError(loc, Errors.IllegalDivision, lt, "/", rt);
			}
		}
		// polynomial / polynomial

		// vector / vector (complex division)
		else if (lt instanceof VectorValue && rt instanceof VectorValue) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.complexDivide(vec, ((VectorValue) rt).getVector(), vec);
			return vec;

		}
		// number / vector (complex division)
		else if (lt instanceof NumberValue && rt instanceof VectorValue) {
			vec = ((VectorValue) rt).getVector(); // just to
													// initialise
													// vec
			GeoVec2D.complexDivide((NumberValue) lt,
					((VectorValue) rt).getVector(), vec);
			return vec;

		}

		else if ((rt instanceof GeoFunction) && lt instanceof NumberValue) {
			return GeoFunction.applyNumberSymb(Operation.DIVIDE,
					(GeoFunction) rt, left, false);
		} else {
			throw new MyError(loc, Errors.IllegalDivision, lt, "/", rt);
		}
	}

	/**
	 * Performs subtraction
	 * 
	 * @param lt
	 *            left argument (evaluated)
	 * @param rt
	 *            right argument (evaluated)
	 * @return result
	 */
	public ExpressionValue handleMinus(ExpressionValue lt, ExpressionValue rt) {
		MyDouble num;
		GeoVec2D vec;
		// number - number
		if (lt instanceof NumberValue && rt instanceof NumberValue) {
			num = ((NumberValue) lt).getNumber();
			MyDouble.sub(num, (NumberValue) rt, num);
			return num;
		}
		// vector - vector
		else if (lt instanceof VectorValue && rt instanceof VectorValue) {
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
		else if (lt instanceof VectorValue && rt instanceof NumberValue) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.sub(vec, ((NumberValue) rt), vec);
			return vec;
		}
		// number - vector (for complex subtraction)
		else if (lt instanceof NumberValue && rt instanceof VectorValue) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.sub(((NumberValue) lt), vec, vec);
			return vec;
		}
		// list - vector
		else if (lt instanceof ListValue && rt instanceof VectorValue) {
			vec = ((VectorValue) rt).getVector();
			GeoVec2D.sub(vec, ((ListValue) lt), vec, false);
			return vec;
		}
		// vector - list
		else if (rt instanceof ListValue && lt instanceof VectorValue) {
			vec = ((VectorValue) lt).getVector();
			GeoVec2D.sub(vec, ((ListValue) rt), vec, true);
			return vec;
		} else if (lt instanceof TextValue) {

			return handlePlus(lt,
					rt.wrap().multiply(-1)
							.evaluate(StringTemplate.defaultTemplate),
					StringTemplate.defaultTemplate, false);
		}
		// polynomial - polynomial
		else {
			throw new MyError(loc, Errors.IllegalSubtraction, lt, "-", rt);
		}
	}

	/**
	 * Performs power
	 * 
	 * @param lt
	 *            left argument (evaluated)
	 * @param rt
	 *            right argument (evaluated)
	 * @param right
	 *            right argument before evaluation
	 * 
	 * @return result
	 */
	public ExpressionValue handlePower(ExpressionValue lt, ExpressionValue rt,
			ExpressionValue right) {
		MyDouble num;
		GeoVec2D vec, vec2;
		// number ^ number
		if (lt instanceof NumberValue && rt instanceof NumberValue) {
			num = ((NumberValue) lt).getNumber();
			double base = num.getDouble();
			MyDouble exponent = ((NumberValue) rt).getNumber();

			// special case: e^exponent (Euler number)
			if (MyDouble.exactEqual(base, Math.E)) {
				return exponent.exp();
			}

			// special case: left side is negative and
			// right side is a fraction a/b with a and b integers
			// where a/b can be positive or negative
			// x^(a/b) := (x^a)^(1/b)

			if (base < 0 && right.isExpressionNode()) {
				Double negPower = right.wrap().calculateNegPower(base);
				if (negPower != null) {
					num.set(negPower);
					return num;
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
		else if (lt instanceof VectorValue && rt instanceof NumberValue) {
			// if (!rt.isConstant()) {
			// String [] str = new String[]{ "ExponentMustBeConstant",
			// lt.toString(),
			// "^", rt.toString() };
			// throw new MyError(l10n, str);
			// }
			vec = ((VectorValue) lt).getVector();

			if (vec.getToStringMode() == Kernel.COORD_COMPLEX) {

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
		} else if (lt instanceof TextValue && rt instanceof NumberValue) {
			String txt = ((TextValue) lt).getTextString();
			return new MyStringBuffer(kernel,
					StringUtil.string(txt, (int) rt.evaluateDouble()));
		} else if (lt instanceof VectorValue && rt instanceof VectorValue) {
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

		} else if (lt instanceof NumberValue && rt instanceof VectorValue) {
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
		else {
			throw new MyError(loc, Errors.IllegalExponent, lt, "^", rt);
		}
	}

	/**
	 * @param base0
	 *            base
	 * @param right
	 *            exponent, must be expression of the form a/b
	 * @return base^exponent
	 */
	static double negPower(double base0, ExpressionValue right) {
		double base = base0;
		ExpressionNode node = (ExpressionNode) right;

		// check if we have a/b with a and b integers
		double a = node.getLeft().evaluateDouble();
		long al = Math.round(a);
		if (DoubleUtil.isEqual(a, al)) { // a is integer
			double b = node.getRight().evaluateDouble();
			long bl = Math.round(b);
			if (b == 0) {
				// (x^a)^(1/0)
				return (Double.NaN);
			} else if (DoubleUtil.isEqual(b, bl)) { // b is
												// integer
				// divide through greatest common divisor of a
				// and b
				long gcd = Kernel.gcd(al, bl);
				// fix for java.lang.ArithmeticException: divide by zero
				// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.ArithmeticException&tf=SourceFile&tc=org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator&tm=negPower&nid&an&c&s=new_status_desc&ed=0
				if (gcd == 0) {
					return Double.NaN;
				}

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
					return Math.pow(base, 1d / bl);
				}
				boolean oddB = (Math.abs(bl) % 2) == 1;
				if (oddB) {
					// base < 0 and b odd: (base)^(1/b) =
					// -(-base^(1/b))
					return (-Math.pow(-base, 1d / bl));
				}
				// base < 0 and a & b even: (base)^(1/b)
				// = undefined
				return (Double.NaN);
			}
		}

		return MyDouble.pow(base, right.evaluateDouble());

	}

	/**
	 * Computes value of function in given point (or throws error)
	 * 
	 * @param lt
	 *            function
	 * @param rt
	 *            value of variable
	 * @param left
	 *            left (function) before evaluation
	 * @return value of function at given point
	 */
	public ExpressionValue handleFunction(ExpressionValue lt,
			ExpressionValue rt, ExpressionValue left) {
		// function(number)
		if (rt instanceof NumberValue) {
			if (lt instanceof Evaluatable) {
				NumberValue arg = (NumberValue) rt;
				if ((lt instanceof GeoFunction)
						&& ((GeoFunction) lt).isGeoFunctionBoolean()) {
					return new MyBoolean(kernel, ((GeoFunction) lt)
							.evaluateBoolean(arg.getDouble()));
				}
				return arg.getNumber().apply((Evaluatable) lt);
			} else if (lt instanceof GeoCasCell && ((GeoCasCell) lt)
					.getValue() instanceof Function) {
				// first we give the expression to the cas
				// and then the result of that to the geogebra
				// so that the cas result will be converted
				ExpressionNode node = new ExpressionNode(kernel, lt,
						Operation.FUNCTION, rt);
				FunctionExpander fex = FunctionExpander.getCollector();
				node = (ExpressionNode) node.wrap().getCopy(kernel)
						.traverse(fex);
				String result = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(
						node, null, StringTemplate.numericNoLocal, null,
						kernel);
				boolean mode = kernel.isSilentMode();
				kernel.setSilentMode(true);
				GeoElementND geo = kernel.getAlgebraProcessor()
						.processAlgebraCommand(result, false)[0];
				kernel.setSilentMode(mode);
				return geo;
			} else if (left instanceof GeoCasCell
					&& ((GeoCasCell) left).getTwinGeo() instanceof GeoLine) {
				return ((NumberValue) rt).getNumber()
						.apply((Evaluatable) ((GeoCasCell) left).getTwinGeo());
			} else {
				Log.debug(lt);
			}
		} else if (rt instanceof VectorNDValue) {
			if (lt instanceof Evaluatable) {
				VectorNDValue pt = (VectorNDValue) rt;
				if (lt instanceof GeoFunction) {
					Function fun = ((GeoFunction) lt).getFunction();
					if (pt.getToStringMode() == Kernel.COORD_COMPLEX
							&& rt instanceof VectorValue) {
						return fun.evalComplex(((VectorValue) rt).getVector());
					}
					return evaluateFunctionNvar(fun, pt, lt);
				} else if (lt instanceof GeoFunctionable) {
					// eg GeoLine
					return evaluateFunctionNvar(((GeoFunctionable) lt)
							.getFunction(), pt, lt);
				} else {
					Log.warn("missing case in ExpressionNodeEvaluator");
				}
			}
		}
		// Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass()
		// + " rt: " + rt + ", " + rt.getClass());
		throw new MyError(loc, Errors.IllegalArgument, MyError.toErrorString(rt));

	}

	/**
	 * Evaluate function in multiple variables
	 * 
	 * @param lt
	 *            left argument (function)
	 * @param rt
	 *            right argument (MyList of variable values)
	 * @return result (number)
	 */
	public ExpressionValue handleFunctionNVar(ExpressionValue lt,
			ExpressionValue rt) {
		if (rt instanceof ListValue && (lt instanceof FunctionalNVar)) {
			FunctionNVar funN = ((FunctionalNVar) lt).getFunction();
			if (funN == null) {
				return new MyDouble(kernel, Double.NaN);
			}
			ListValue list = (ListValue) rt;
			if (funN.getVarNumber() == list.size()
					|| funN.getVarNumber() == 1) {
				double[] args = list.toDouble(0);
				return evaluateFunctionNvar(funN, args, lt);
			} else if (list.size() == 1) {
				ExpressionValue ev = list.getMyList().getListElement(0)
						.evaluate(StringTemplate.defaultTemplate);
				if ((funN.getVarNumber() == 2 || funN.getVarNumber() == 3)
						&& (ev instanceof VectorNDValue)) {
					VectorNDValue pt = (VectorNDValue) ev;
					return evaluateFunctionNvar(funN, pt, lt);
				} else if ((ev instanceof ListValue) && (((ListValue) ev)
						.getMyList().size() > 0) && ((ListValue) ev)
						.getMyList().getListElement(0).evaluate(
								StringTemplate.defaultTemplate) instanceof NumberValue) {
					// TODO can we avoid evaluate here
					double[] vals = ((ListValue) ev).toDouble(0);
					return evaluateFunctionNvar(funN, vals, lt);
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
						ret.addListElement(new ExpressionNode(kernel, lt,
								Operation.FUNCTION_NVAR, lArg));
					}
					return ret;
				}
			}
		}
		// Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass() +
		// " rt: " + rt + ", " + rt.getClass());
		throw new MyError(loc, Errors.IllegalArgument, MyError.toErrorString(rt));
	}

	private ExpressionValue evaluateFunctionNvar(FunctionNVar funN,
												 VectorNDValue pt, ExpressionValue lt) {
		if (funN.isBooleanFunction()) {
			return new MyBoolean(kernel, funN.evaluate(pt) > 0);
		} else if (lt.isGeoElement() && !((GeoElement) lt).isDefined()) {
			return new MyDouble(kernel, Double.NaN);
		}
		return new MyDouble(kernel, funN.evaluate(pt));
	}

	private ExpressionValue evaluateFunctionNvar(FunctionNVar funN,
				double[] vals, ExpressionValue lt) {
		if (vals != null) {
			if (funN.isBooleanFunction()) {
				return new MyBoolean(kernel,
						funN.evaluateBoolean(vals));
			} else if (lt.isGeoElement() && !((GeoElement) lt).isDefined()) {
				return new MyDouble(kernel, Double.NaN);
			}
			return new MyDouble(kernel, funN.evaluate(vals));
		}
		return lt;
	}

	/**
	 * Throw error for unary boolean operation
	 * 
	 * @param arg
	 *            operation argument
	 * @param opname
	 *            operation string
	 * @return nothing (error is thrown)
	 * @throws MyError
	 *             (always)
	 */
	public ExpressionValue illegalBoolean(ExpressionValue arg, String opname) {
		throw new MyError(loc, Errors.IllegalBoolean, opname, MyError.toErrorString(arg));
	}

	/**
	 * Throw illegal argument exception for multivariable builtin function
	 * 
	 * @param lt
	 *            left argument
	 * @param rt
	 *            right argument
	 * @param opname
	 *            operation name
	 * @return nothing (error is thrown)
	 * @throws MyError
	 *             (always)
	 */
	public MyError illegalArgument(ExpressionValue lt,
			ExpressionValue rt, String opname) {
		return new MyError(loc, Errors.IllegalArgument, lt, opname, rt);
	}

	/**
	 * Throw simple illegal argument exception
	 * 
	 * @param arg
	 *            argument
	 * @return nothing (error is thrown)
	 * @throws MyError
	 *             (always)
	 */
	public MyError illegalArgument(ExpressionValue arg) {
		return new MyError(loc, Errors.IllegalArgument, MyError.toErrorString(arg));
	}

	/**
	 * Throw error for infix binary operation
	 * 
	 * @param lt     left argument
	 * @param rt     right argument
	 * @param type   type (InvalidMultiplication, InvalidAddition, ...)
	 * @param opname operator string
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public MyError illegalBinary(ExpressionValue lt, ExpressionValue rt, Errors type,
			String opname) {
		return new MyError(loc, type, lt, opname, rt);

	}

	/**
	 * Throw illegal comparison error
	 * 
	 * @param lt     left argument
	 * @param rt     rigt argument
	 * @param opname comparison operator
	 * @return nothing (error is thrown)
	 * @throws MyError (always)
	 */
	public MyError illegalComparison(ExpressionValue lt,
			ExpressionValue rt, String opname) {
		return new MyError(loc, Errors.IllegalComparison, lt, opname, rt);

	}

	/**
	 * Throw illegal list operation error
	 * 
	 * @param lt
	 *            left argument
	 * @param rt
	 *            rigt argument
	 * @param opname
	 *            list operator
	 * @return nothing (error is thrown)
	 * @throws MyError
	 *             (always)
	 */
	public MyError illegalListOp(ExpressionValue lt, ExpressionValue rt,
			String opname) {
		return new MyError(loc, Errors.IllegalListOperation, lt, opname, rt);

	}

	/**
	 * Check whether lt is constant polynomial and compute op(lt) if it is; if
	 * not throw illegal argument "opname lt)"
	 * 
	 * @param lt
	 *            argument
	 * @param opname
	 *            operation name (including "(")
	 * @return op(lt) or error
	 * @throws MyError
	 *             if not polynomial or not constant
	 */
	public MyError polynomialOrDie(ExpressionValue lt, String opname) {
		return polynomialOrDie(lt, opname, ")");
	}

	/**
	 * Check whether lt is constant polynomial and compute op(lt) if it is; if
	 * not throw illegal argument "prefix lt suffix"
	 * 
	 * @param lt
	 *            argument
	 * @param prefix
	 *            prefix of error message
	 * @param suffix
	 *            of error message
	 * @return op(lt) if lt is constant poly
	 * @throws MyError
	 *             if not polynomial or not constant
	 */
	public MyError polynomialOrDie(ExpressionValue lt, String prefix, String suffix) {
		return new MyError(loc, Errors.IllegalArgument, prefix, MyError.toErrorString(lt), suffix);
	}

	/**
	 * Performs vector product
	 * 
	 * @param lt
	 *            left argument
	 * @param rt
	 *            right argument
	 * @return result
	 */
	public ExpressionValue handleVectorProduct(ExpressionValue lt, ExpressionValue rt) {
		if (lt instanceof VectorNDValue && rt instanceof VectorNDValue) {
			return vectorProduct((VectorNDValue) lt, (VectorNDValue) rt);
		}

		throw illegalBinary(lt, rt, Errors.IllegalMultiplication,
				ExpressionNodeConstants.strVECTORPRODUCT);
	}

	/**
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return v1 * v2 vector product
	 */
	protected ExpressionValue vectorProduct(VectorNDValue v1,
			VectorNDValue v2) {
		GeoVecInterface vec1 = v1.getVector();
		GeoVecInterface vec2 = v2.getVector();
		MyDouble num = new MyDouble(kernel);
		GeoVec2D.vectorProduct(vec1, vec2, num);
		return num;
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @param lt
	 *            list from which element is to be chosen
	 * @param rt
	 *            list of indices
	 * @param skip
	 *            0 to evaluate completely, >0 to skip last skip arguments
	 * @return list element
	 */
	public ExpressionValue handleElementOf(ExpressionValue lt,
			ExpressionValue rt, int skip) {
		// TODO not implemented #1115
		// Application.debug(rt.getClass()+" "+rt.getClass());
		if (lt instanceof GeoList && rt instanceof ListValue) {

			GeoList sublist = ((GeoList) lt);
			ListValue lv = (ListValue) rt;
			int idx = -1;
			// convert list1(1,2) into Element[Element[list1,1],2]
			boolean sublistUndefined = false;
			for (int i = 0; i < lv.size(); i++) {
				ExpressionNode ith = (ExpressionNode) lv.getMyList()
						.getListElement(i);
				idx = (int) Math.round(ith.evaluateDouble()) - 1;
				if (i < lv.size() - 1) {
					GeoElement nextSublist;
					if (idx < 0) {
						idx = sublist.size() + 1 + idx;
					}
					if (idx >= 0 && idx < sublist.size()) {
						nextSublist = sublist.get(idx);
					} else {
						nextSublist = sublist.createTemplateElement();
						sublistUndefined = true;
						nextSublist.setUndefined();
					}
					if (nextSublist instanceof GeoList) {
						sublist = (GeoList) nextSublist;
					} else if (i == lv.size() - 2
							&& nextSublist instanceof GeoFunction) {
						if (skip > 0) {
							return functionOrUndefined(nextSublist);
						}
						return new MyDouble(getKernel(),
								((GeoFunction) nextSublist)
										.value(lv.getListElement(i + 1)
												.evaluateDouble()));
					} else if (nextSublist instanceof GeoFunctionNVar
							&& i == lv.size() - ((GeoFunctionNVar) nextSublist)
									.getVarNumber() - 1) {
						if (skip > 0) {
							return functionNvarOrUndefined(nextSublist);
						}
						return new MyDouble(getKernel(),
								((GeoFunctionNVar) nextSublist)
										.evaluate(lv.toDouble(1)));
					} else {
						Log.debug("Wrong depth for Element: " + nextSublist
								+ " :" + (lv.size() - i - 1));
						return new MyDouble(getKernel(), Double.NaN);
					}

				}

			}
			if (idx < 0) {
				idx = sublist.size() + 1 + idx;
			}
			GeoElement ret;
			if (idx >= 0 && idx < sublist.size() && !sublistUndefined) {
				ret = sublist.get(idx).copyInternal(sublist.getConstruction());
			} else {
				ret = sublist.createTemplateElement();

				ret.setUndefined();
			}
			if (ret instanceof GeoFunction) {

				MyList list = lv.getMyList();
				FunctionVariable fv = new FunctionVariable(kernel);
				list.addListElement(fv);
				return new Function(new ExpressionNode(kernel, lt,
						Operation.ELEMENT_OF, list), fv);
			}
			if (ret instanceof GeoFunctionNVar) {
				MyList list = lv.getMyList();
				FunctionVariable[] vars = ((GeoFunctionNVar) ret)
						.getFunctionVariables();
				for (FunctionVariable var : vars) {
					list.addListElement(var);
				}
				return new FunctionNVar(new ExpressionNode(kernel, lt,
						Operation.ELEMENT_OF, list), vars);
			}
			return ret;
		}
		throw illegalArgument(lt);
	}

	private ExpressionValue functionOrUndefined(GeoElement nextSublist) {
		return nextSublist.isDefined() ? nextSublist
				: new Function(new ExpressionNode(getKernel(), Double.NaN),
						new FunctionVariable(getKernel()));
	}

	private ExpressionValue functionNvarOrUndefined(GeoElement nextSublist) {
		return nextSublist.isDefined() ? nextSublist
				: new FunctionNVar(new ExpressionNode(getKernel(), Double.NaN),
						new FunctionVariable[] {});
	}

	/**
	 * @param lt
	 *            condition (unchecked cast ot BooleanValue)
	 * @param rt
	 *            conditional expr
	 * @return rt or ?
	 */
	public ExpressionValue handleIf(ExpressionValue lt, ExpressionValue rt) {
		if (((BooleanValue) lt).getBoolean()) {
			return rt;
		}
		return rt.getUndefinedCopy(getKernel());
	}

	/**
	 * 
	 * eg f(x)=x^2, x+1 instead of f(x) = x^2, x>1
	 * 
	 * @return error for a,b where b is not a condition
	 */
	public MyError illegalCondition() {
		return new MyError(getKernel().getLocalization(), Errors.InvalidInput);
	}

}

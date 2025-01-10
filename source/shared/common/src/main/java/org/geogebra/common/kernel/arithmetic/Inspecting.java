package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Allows checking whether at least one part of structured expression value has
 * certain property.
 *
 */
public interface Inspecting {
	/**
	 * Do the local check
	 *
	 * @param v
	 *            expression
	 * @return whether this expression itself has given property (not the
	 *         subparts)
	 */
	public boolean check(ExpressionValue v);

	/**
	 * Checks whether the expression contains operations &lt; ,&lt;=, &gt;, &gt;=
	 *
	 */
	public enum IneqFinder implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			return v.isExpressionNode()
					&& ((ExpressionNode) v).getOperation().isInequality();
		}

	}

	/** Checks presence of Commands */
	public enum CommandFinder implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			return v instanceof Command;
		}
	}

	/** Checks presence of complex number */
	public enum ComplexChecker implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			return ExpressionNode.isImaginaryUnit(v);
		}
	}

	/**
	 * Checks for presence of commands and derivatives
	 *
	 */
	public enum ExtendedCommandFinder implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			if (v instanceof Command) {
				return true;
			}
			if (v instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) v;
				if (en.getOperation().equals(Operation.DERIVATIVE)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Checks if a ValidExpression is unplottable
	 *
	 * <em>Warning:</em> it always returns false for MyList, the checking has to
	 * be done manually for each element
	 *
	 * @author bencze
	 */
	public class UnplottableChecker implements Inspecting {

		private boolean isOtherVar;
		private int nrOfPoints;
		private static int type;
		private static int dim;
		private static UnplottableChecker checker = new UnplottableChecker();

		private UnplottableChecker() {
			// singleton constructor
		}

		@Override
		public boolean check(ExpressionValue v) {
			switch (type) {
			case 0: // first define the top type of our expression
				isOtherVar = false;
				nrOfPoints = 0;
				break;
			// Command
			case 1:
				return false;
			// Equation
			case 2:
				if (v instanceof MyVec3DNode) {
					nrOfPoints++;
				} else if (v.isExpressionNode()
						&& ((ExpressionNode) v)
								.getLeft() instanceof GeoDummyVariable
						&& ((ExpressionNode) v)
								.getRight() instanceof MyVec3DNode) {
					String str = ((ExpressionNode) v).getLeft()
							.toString(StringTemplate.defaultTemplate);
					if ((Unicode.lambda + "").equals(str)) {
						nrOfPoints++;
					}
				}
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
					String varString = gdv
							.toString(StringTemplate.defaultTemplate);
					if (!"x".equals(varString) && !"y".equals(varString)
							&& !"X".equals(varString)
							&& (dim < 3 || !"z".equals(varString))) {
						if ((Unicode.lambda + "").equals(varString)
								&& !isOtherVar
								&& nrOfPoints == 2) {
							return false;
						}
						isOtherVar = true;
						GeoElement subst = gdv.getElementWithSameName();
						return subst == null || (subst.getSendValueToCas()
								// skip constants
								// needed for GGB-810
								&& (subst.getLabelSimple() == null || !subst
								.getLabelSimple().startsWith("c_")));
					}
					if ("x".equals(varString) || "z".equals(varString)) {
						isOtherVar = true;
					}

				}
				return false;
			// Function, FunctionNVar
			case 3:
			case 10:
				if (v instanceof GeoDummyVariable) {
					GeoElement subst = ((GeoDummyVariable) v)
							.getElementWithSameName();
					return subst == null || (subst.getSendValueToCas()
							// skip constants
							// needed for GGB-810
							&& (subst.getLabelSimple() == null || !subst
							.getLabelSimple().startsWith("c_")));
				}
				return false;
			// MyBoolean
			case 4:
				// MyDouble
			case 5:
				// MyList
			case 6:
				return false;

			// ExpressionNode
			case 11:
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
					if (!gdv.toString(StringTemplate.defaultTemplate)
							.equals("x")
							&& !gdv.toString(StringTemplate.defaultTemplate)
									.equals("y")) {
						return true;
					}
				} else if (!(v instanceof MyDouble
						|| v instanceof ExpressionNode
						|| v instanceof GeoNumeric || v instanceof MyVecNode
						|| v instanceof GeoVector || v instanceof MyList)) {
					return true;
				}
				return false;
			default:
				return false;
			}
			return setType(v);
		}

		private static boolean setType(ExpressionValue v) {
			if (v instanceof Command) {
				type = 1;
				return false;
			} else if (v instanceof Equation) {
				ExpressionValue ev = ((Equation) v).getRHS().unwrap();
				// TODO needs to have evaluatesTo..., also check for complex
				if (!ev.evaluatesToNumber(true) && !ev.evaluatesTo3DVector()) {
					return true;
				}
				type = 2;
				return false;
			} else if (v instanceof Function) {
				type = 3;
				return false;
			} else if (v instanceof MyBoolean) {
				type = 4;
				return true;
			} else if (v instanceof MyDouble) {
				type = 5;
				return false;
			} else if (v instanceof MyList) {
				type = 6;
				return false;
			} else if (v instanceof Polynomial) {
				type = 8;
				return false;
			} else if (v instanceof Variable || v instanceof GeoDummyVariable) {
				return !v.toString(StringTemplate.defaultTemplate).equals("x");
			} else if (v instanceof ExpressionNode) {
				type = 11;
				return false;
			} else if (v instanceof FunctionNVar) {
				type = 10;
				return ((FunctionNVar) v).getVarNumber() > 2;
			}
			return false;

		}

		/**
		 * @param dimension
		 *            2 or 3: in how many dimensions can we plot a point
		 * @return UnplottableChecker singleton instance
		 */
		public static UnplottableChecker getChecker(int dimension) {
			type = 0;
			dim = dimension;
			return checker;
		}
	}

	/**
	 * Checks if a division of vectors is found or not
	 */
	public static Inspecting vectorDivisionFinder = new Inspecting() {

		@Override
		public boolean check(ExpressionValue v) {
			if (v.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode) v;
				if (en.getOperation() == Operation.DIVIDE) {
					return en.getRightTree().evaluatesToNDVector()
							&& en.getLeftTree().evaluatesToNDVector();
				}
			}
			return false;
		}
	};

	/**
	 * Instead of isConstant we sometimes (always?) want to check only for Geos
	 * that are not labeled, symbolic or dependent ie we don't need to
	 * distinguish between MyDouble(1) and GeoNumeric(1)
	 */
	public static Inspecting dynamicGeosFinder = new Inspecting() {

		@Override
		public boolean check(ExpressionValue v) {
			if (!v.isGeoElement()) {
				return false;
			}
			GeoElement geo = (GeoElement) v;
			return !geo.isIndependent() || geo.isLabelSet()
					|| geo.isLocalVariable() || v instanceof GeoDummyVariable
					|| geo.isGeoCasCell() || geo.isRandomGeo();
		}
	};

	/**
	 * returns true if strings have been found in the expression (MyStringBuffer
	 * || GeoText)
	 */
	public static Inspecting textFinder = new Inspecting() {

		@Override
		public boolean check(ExpressionValue v) {
			return v instanceof GeoText || v instanceof MyStringBuffer;
		}
	};

	/**
	 * returns true if Plane have been found in the expression
	 */
	public static Inspecting planeFinder = new Inspecting() {

		@Override
		public boolean check(ExpressionValue v) {
			if (v instanceof GeoDummyVariable) {
				String name = ((GeoDummyVariable) v)
						.toString(StringTemplate.defaultTemplate);
				GeoElement geo = ((GeoDummyVariable) v).getKernel()
						.lookupLabel(name);
				return (geo instanceof GeoPlaneND)
						|| "z".equals(name);
			}
			return v instanceof GeoPlaneND;
		}
	};

	/**
	 * Finds MySpecialDoubles
	 *
	 */
	public enum SpecialDouble implements Inspecting {
		/** singleton instance*/
		INSTANCE;

		@Override
		public boolean check(ExpressionValue v) {
			return v instanceof MySpecialDouble;
		}

	}

	/**
	 * @author csilla check whether the expression contains only "+" (needed for
	 *         Theorem proving)
	 */
	public enum PlusChecker implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			if (v instanceof GeoDummyVariable) {
				return true;
			}
			if (v instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) v;
				if (en.getOperation().equals(Operation.PLUS)) {
					return check(en.getLeft()) && check(en.getRight());
				}
			}
			return false;
		}

	}

	/**
	 * @author csilla check whether the expression contains only "-" (needed for
	 *         Theorem proving)
	 */
	public enum MinusChecker implements Inspecting {
		/** singleton instance */
		INSTANCE;
		@Override
		public boolean check(ExpressionValue v) {
			if (v instanceof GeoDummyVariable) {
				return true;
			}
			if (v instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) v;
				if (en.getOperation().equals(Operation.MULTIPLY)
						&& en.getLeft() instanceof MyDouble
						&& en.getLeft().evaluateDouble() == -1
						&& en.getRight() instanceof GeoDummyVariable) {
					return true;
				}
				if (en.getOperation().equals(Operation.MINUS)) {
					return check(en.getLeft()) && check(en.getRight());
				}
			}
			return false;
		}

	}

	/**
	 * Returns true if any of the expression leaf is undefined.
	 */
	public static Inspecting isUndefinedInspector = new Inspecting() {
		@Override
		public boolean check(ExpressionValue v) {
			return v instanceof NumberValue && !((NumberValue) v).isDefined();
		}
	};
}

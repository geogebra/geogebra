package org.geogebra.common.cas.view;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.editor.share.util.Unicode;

/**
 * Checks if a ValidExpression is unplottable
 * <em>Warning:</em> it always returns false for MyList, the checking has to
 * be done manually for each element
 * @author bencze
 */
public class UnplottableChecker implements Inspecting {

	private boolean isOtherVar;
	private int nrOfPoints;
	private int type = 0;
	private final int dimension;

	/**
	 * Creates a new instance
	 * @param dimension 2 or 3: in how many dimensions can we plot a point
	 */
	public UnplottableChecker(int dimension) {
		this.dimension = dimension;
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
				if (Unicode.lambda_STRING.equals(str)) {
					nrOfPoints++;
				}
			}
			if (v instanceof GeoDummyVariable) {
				GeoDummyVariable gdv = (GeoDummyVariable) v;
				String varString = gdv
						.toString(StringTemplate.defaultTemplate);
				if (!"x".equals(varString) && !"y".equals(varString)
						&& !"X".equals(varString)
						&& (dimension < 3 || !"z".equals(varString))) {
					if (Unicode.lambda_STRING.equals(varString)
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

	private boolean setType(ExpressionValue v) {
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
}
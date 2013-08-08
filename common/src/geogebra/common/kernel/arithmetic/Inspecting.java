package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoVector;

/**
 * Allows checking whether at least one part of structured expression value has certain property.
 *
 */
public interface Inspecting {
	/**
	 * Do the local check
	 * @param v expression
	 * @return whether this expression itself has given property (not the subparts)
	 */
	public boolean check(ExpressionValue v);
	
	/**
	 * Checks whether the expression contains operations <,<=, >, >=
	 *
	 */
	public enum IneqFinder implements Inspecting{
		/** singleton instance */
		INSTANCE;
		public boolean check(ExpressionValue v) {
			return v.isExpressionNode() && ((ExpressionNode)v).getOperation().isInequality();
		}
		
	}
	/** Checks presence of Commands */
	public enum CommandFinder implements Inspecting{
		/** singleton instance */
		INSTANCE;
		public boolean check(ExpressionValue v) {
			return v instanceof Command;
		}
	}
	
	/**
	 * Checks if a ValidExpression is unplottable
	 * @author bencze
	 * @warning it always returns false for MyList, the checking has to be done manually for each element
	 *
	 */
	public class UnplottableChecker implements Inspecting {
		
		private static int type;
		
		public boolean check(ExpressionValue v) {
			switch (type) {
			case 0: // first define the top type of our expression
				break;
			// Command
			case 1:
				return false;
			// Equation
			case 2:
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
					if (!gdv.toString(StringTemplate.defaultTemplate).equals(
							"x")
							&& !gdv.toString(StringTemplate.defaultTemplate)
									.equals("y")) {
						return true;
					}
				}
				return false;
			// Function
			case 3:
				if (v instanceof GeoDummyVariable) {
					return true;
				}
				return false;
			// MyBoolean
			case 4:
			// MyDouble
			case 5:
			// MyList
			case 6:
				return false;
			// FunctionNVar
			case 10:
				return true;
			// ExpressionNode
			case 11:
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
					if (!gdv.toString(StringTemplate.defaultTemplate).equals(
							"x")) {
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
		
		private static UnplottableChecker checker = new UnplottableChecker();
		
		private static boolean setType(ExpressionValue v) {
			if (v instanceof Command) {
				type = 1;
				return false;
			} else if (v instanceof Equation) {
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
			} else if (v instanceof Parametric) {
				type = 7;
				return false;
			} else if (v instanceof Polynomial){
				type = 8;
				return false;
			} else if (v instanceof Variable || v instanceof GeoDummyVariable) {
				if (v.toString(StringTemplate.defaultTemplate).equals("x")) {
					return false;
				}
				return true;
			} else if (v instanceof ExpressionNode) {
				type = 11;
				return false;
			} else if (v instanceof FunctionNVar) {
				type = 10;
				return true;
			}
			return false;

		}
		
		/**
		 * @return UnplottableChecker singleton instance 
		 */
		public static UnplottableChecker getChecker() {
			type = 0;
			return checker;
		}
	}
	
	public static Inspecting dynamicGeosFinder = new Inspecting(){

		public boolean check(ExpressionValue v) {
			return v instanceof GeoElement && (!((GeoElement)v).isIndependent() 
					|| ((GeoElement)v).isLabelSet()) ;
		}
	};
	
}

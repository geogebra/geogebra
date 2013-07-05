package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoNumeric;

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
	 *
	 */
	public class UnplottableChecker implements Inspecting {
		
		private static int type;
		
		public boolean check(ExpressionValue v) {
			switch (type) {
			case 0: //first define the type
				break;
			case 1:
			case 4:
			case 5:
			case 6:
			case 10:
				return true;
			case 2:
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
						if (!gdv.toString(StringTemplate.defaultTemplate).equals("x")
								&& !gdv.toString(StringTemplate.defaultTemplate).equals("y")) {
							return true;
						}
					}
					return false;
			case 3:
				if (v instanceof GeoDummyVariable) {
					return true;
				}
				return false;
			case 11:
				if (v instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) v;
					if (!gdv.toString(StringTemplate.defaultTemplate).equals("x")) {
						return true;
					}
				} else if (!(v instanceof MyDouble) && !(v instanceof ExpressionNode) && !(v instanceof GeoNumeric)) {
					return true;
				}
				return false;
			default: return false;
		
			}
			if (v instanceof Command) {
				type = 1;
				return true;
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
				return true;
			} else if (v instanceof MyList) {
				type = 6;
				return true;
			} else if (v instanceof Parametric) {
				type = 7;
				return false;
			} else if (v instanceof Polynomial){
				type = 8;
				return false;
			} else if (v instanceof Variable || v instanceof GeoDummyVariable) {
				type = 9;
				return false;
			} else if (v instanceof FunctionNVar) {
				type = 10;
				return true;
			} else if (v instanceof ExpressionNode) {
				type = 11;
				return false;
			}
			return false;
		}
		
		private static UnplottableChecker checker = new UnplottableChecker();
		
		/**
		 * @return UnplottableChecker singleton instance 
		 */
		public static UnplottableChecker getChecker() {
			type = 0;
			return checker;
		}
	}
	
	
}

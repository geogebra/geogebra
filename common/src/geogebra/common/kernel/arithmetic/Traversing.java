package geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.Operation;
/**
 *  Traversing objects are allowed to traverse through Equation,
 *  MyList, ExpressionNode and MyVecNode(3D) structure to perform some action,
 *  e.g. replace one type of objects by another.
 *  @author Zbynek Konecny
 */
public interface Traversing {
	/**
	 * Processes a value locally (no recursion)
	 * @param ev value to process
	 * @return processed value
	 */
	public ExpressionValue process(ExpressionValue ev);
	/**
	 * Replaces one object by another
	 */
	public class Replacer implements Traversing {
		private ExpressionValue oldObj;
		private ExpressionValue newObj;
		public ExpressionValue process(ExpressionValue ev) {
				if(ev == oldObj)
					return newObj;
				return ev;
		}
		private static Replacer replacer = new Replacer();
		
		/**
		 * Creates a replacer
		 * @param original object to be replaced
		 * @param replacement replacement
		 * @return replacer
		 */
		public static Replacer getReplacer(ExpressionValue original,ExpressionValue replacement){
			replacer.oldObj = original;
			replacer.newObj = replacement;
			return replacer;
		}
	}
	/**
	 * Replaces dummy variable with given name
	 *
	 */
	public class GeoDummyReplacer implements Traversing {
		private String var;
		private ExpressionValue newObj;
		private boolean didReplacement;
		public ExpressionValue process(ExpressionValue ev) {
				if(!(ev instanceof GeoDummyVariable) ||
						!var.equals(((GeoDummyVariable) ev).toString(StringTemplate.defaultTemplate)))
					return ev;
				didReplacement = true;
				return newObj;
		}
		private static GeoDummyReplacer replacer = new GeoDummyReplacer();
		/**
		 * @param varStr variable name
		 * @param replacement replacement object
		 * @return replacer
		 */
		public static GeoDummyReplacer getReplacer(String varStr,ExpressionValue replacement){
			replacer.var = varStr;
			replacer.newObj = replacement;
			replacer.didReplacement = false;
			return replacer;
		}
		/**
		 * @return true if a replacement was done since getReplacer() call
		 */
		public boolean didReplacement() {
			return didReplacement;
		}
	}
	
	/**
	 * Replaces Variables with given name by given object
	 * @author zbynek
	 *
	 */
	public class VariableReplacer implements Traversing {
		private String var;
		private ExpressionValue newObj;
		public ExpressionValue process(ExpressionValue ev) {
				if(!(ev instanceof Variable))
					return ev;
				if(!var.equals(ev.toString(StringTemplate.defaultTemplate)))
					return ev;
				return newObj;
		}
		private static VariableReplacer replacer = new VariableReplacer();
		/**
		 * @param varStr variable name
		 * @param replacement replacement object
		 * @return replacer
		 */
		public static VariableReplacer getReplacer(String varStr,ExpressionValue replacement){
			replacer.var = varStr;
			replacer.newObj = replacement;
			return replacer;
		}
	}
	/**
	 * Replaces arbconst(), arbint(), arbcomplex() by auxiliary numerics
	 */
	public class ArbconstReplacer implements Traversing {
		private MyArbitraryConstant arbconst;
		public ExpressionValue process(ExpressionValue ev) {
			if(!ev.isExpressionNode())
				return ev;
			ExpressionNode en = (ExpressionNode)ev;
			if(en.getOperation()==Operation.ARBCONST)
				return arbconst.nextConst((MyDouble)en.getRight());
			if(en.getOperation()==Operation.ARBINT)
				return arbconst.nextInt((MyDouble)en.getRight());
			if(en.getOperation()==Operation.ARBCOMPLEX)
				return arbconst.nextComplex((MyDouble)en.getRight());
			return en;
		}
		private static ArbconstReplacer replacer = new ArbconstReplacer();
		
		/**
		 * @param arbconst arbitrary constant handler
		 * @return replacer
		 */
		public static ArbconstReplacer getReplacer(MyArbitraryConstant arbconst){
			replacer.arbconst = arbconst;
			return replacer;
		}
	}
	/**
	 * Replaces powers by roots or vice versa
	 */
	public class PowerRootReplacer implements Traversing {
		private boolean toRoot;
		public ExpressionValue process(ExpressionValue ev) {
			if(!ev.isExpressionNode())
				return ev;
			((ExpressionNode)ev).replacePowersRoots(toRoot);
			return ev;
		}
		private static PowerRootReplacer replacer = new PowerRootReplacer();
		/**
		 * @param toRoot true to replace exponents by roots
		 * @return replacer
		 */
		public static PowerRootReplacer getReplacer(boolean toRoot){
			replacer.toRoot = toRoot;
			return replacer;
		}
	}
	
	/**
	 * Goes through the ExpressionValue and collects all derivatives
	 * from expression nodes into arrays
	 */
	public class DerivativeCollector implements Traversing {
		private List<GeoElement> derivativeFunctions;
		private List<Integer> derivativeDegrees;
		private Set<String> signatures;
		public ExpressionValue process(ExpressionValue ev) {
			if(!ev.isExpressionNode())
				return ev;
			ExpressionNode en = (ExpressionNode)ev;
			if(en.getOperation() ==Operation.DERIVATIVE){
				int degree;
				if(en.getRight() instanceof NumberValue)
					degree = (int)((NumberValue)en.getRight()).getDouble();
				else
					degree = 1;
				String signature = en.getLeft().toString(StringTemplate.defaultTemplate)+","+degree;
				if(!signatures.contains(signature)){
					derivativeFunctions.add((GeoElement)en.getLeft());
					derivativeDegrees.add(degree);
					signatures.add(signature);
				}
				
			}
			return en;
		}
		private static DerivativeCollector collector = new DerivativeCollector();
		/**
		 * Resets and returns the collector
		 * @return derivative collector
		 */
		public static DerivativeCollector getCollector(){
			collector.derivativeFunctions = new ArrayList<GeoElement>();
			collector.derivativeDegrees = new ArrayList<Integer>();
			collector.signatures = new TreeSet<String>();
			return collector;
		}
		/**
		 * @return collected functions
		 */
		public List<GeoElement> getFunctions(){
			return derivativeFunctions;
		}
		/**
		 * @return collectted degrees
		 */
		public List<Integer> getDegrees(){
			return derivativeDegrees;
		}
	}
	
	/**
	 * Goes through the ExpressionValue and collects all derivatives
	 * from expression nodes into arrays
	 */
	public class CommandCollector implements Traversing {
		private Set<Command> commands;
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof Command)
				commands.add((Command)ev);
			return ev;
		}
		private static CommandCollector collector = new CommandCollector();
		/**
		 * Resets and returns the collector
		 * @param commands set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static CommandCollector getCollector(Set<Command> commands){		
			collector.commands = commands;
			return collector;
		}
	}

}

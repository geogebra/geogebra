package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
	public ExpressionValue process(final ExpressionValue ev);
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
	public class CommandReplacer implements Traversing {
		private App app;
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof Command){
				Command c= (Command)ev;
				String cmdName = app.getReverseCommand(c.getName());
				Throwable t = null;
				try{
					Commands.valueOf(cmdName);
				}catch(Throwable t1){
					t= t1;
				}
				if(t == null)
					return ev;
				MyList argList = new MyList(c.getKernel()); 
				for(int i=0;i<c.getArgumentNumber();i++){
					argList.addListElement(c.getItem(i));
				}
				return new ExpressionNode(c.getKernel(),
						new GeoDummyVariable(c.getKernel().getConstruction(),c.getName()),
						Operation.FUNCTION_NVAR,
						argList);
			}
			return ev;
		}
		private static CommandReplacer replacer = new CommandReplacer();
		/**
		 * @param app application (needed to check which commands are valid)
		 * @return replacer
		 */
		public static CommandReplacer getReplacer(App app){
			replacer.app = app;
			return replacer;
		}
	}
	
	/**
	 * Replaces dummy variable with given name
	 *
	 */
	public class PolyReplacer implements Traversing {
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof Polynomial && ((Polynomial)ev).length()==1){
				int[] exponents = new int[]{0,0,0};
				String xyz = ((Polynomial)ev).getTerm(0).getVars();
				for(int i=0;i<xyz.length();i++){
					exponents[xyz.charAt(i)-'x']++;
				}
				Kernel kernel = ev.getKernel();
				
				return new ExpressionNode(kernel,new FunctionVariable(kernel,"x")).power(new MyDouble(kernel,exponents[0])).
						multiply(new ExpressionNode(kernel,new FunctionVariable(kernel,"y")).power(new MyDouble(kernel,exponents[1]))).
						multiply(new ExpressionNode(kernel,new FunctionVariable(kernel,"z")).power(new MyDouble(kernel,exponents[2]))).multiply(((Polynomial)ev).getTerm(0).getCoefficient());
			
			}
			return ev;
		}
		private static PolyReplacer replacer = new PolyReplacer();
		/**
		 * @return replacer
		 */
		public static PolyReplacer getReplacer(){
			return replacer;
		}
	}
	
	/**
	 * Replaces variables and polynomials
	 *
	 */
	public class VariablePolyReplacer implements Traversing {
		private FunctionVariable fv;
		private int replacements;
		public ExpressionValue process(ExpressionValue ev) {
			if ((ev.isPolynomialInstance() || ev instanceof Variable || ev instanceof FunctionVariable || ev instanceof GeoDummyVariable)
					&& fv.toString(StringTemplate.defaultTemplate).equals(
							ev.toString(StringTemplate.defaultTemplate))) {
				replacements++;
				return fv;
			}
				
			return ev;
		}
		/**
		 * @return number of replacements since getReplacer was called
		 */
		public int getReplacements(){
			return replacements;
		}
		private static VariablePolyReplacer replacer = new VariablePolyReplacer();
		/**
		 * @param fv function variable
		 * @return replacer
		 */
		public static VariablePolyReplacer getReplacer(FunctionVariable fv){
			replacer.fv = fv;
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
		private int replacements;
		public ExpressionValue process(ExpressionValue ev) {
				if(!(ev instanceof Variable || ev instanceof FunctionVariable || ev instanceof GeoDummyVariable))
					return ev;
				if(!var.equals(ev.toString(StringTemplate.defaultTemplate))){					
					return ev;
				}
				replacements++;
				return newObj;
		}
		/**
		 * @return number of replacements since getReplacer was called
		 */
		public int getReplacements(){
			return replacements;
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
			replacer.replacements = 0;
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
			if(en.getOperation()==Operation.ARBCONST){
				return arbconst.nextConst((MyDouble)en.getLeft());
			}
			if(en.getOperation()==Operation.ARBINT){
				return arbconst.nextInt((MyDouble)en.getLeft());
			}
			if(en.getOperation()==Operation.ARBCOMPLEX){
				return arbconst.nextComplex((MyDouble)en.getLeft());
			}
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
		/** functions with 100th root are numerically unstable*/
		private static int MAX_ROOT=99;
		public ExpressionValue process(ExpressionValue ev) {
			if(!ev.isExpressionNode())
				return ev;
			((ExpressionNode)ev).replacePowersRoots(toRoot,MAX_ROOT);
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
	
	/**
	 * Collects all function variables
	 * @author zbynek
	 */
	public class FVarCollector implements Traversing {
		private Set<String> commands;
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof FunctionVariable)
				commands.add(((FunctionVariable)ev).getSetVarString());
			return ev;
		}
		private static FVarCollector collector = new FVarCollector();
		/**
		 * Resets and returns the collector
		 * @param commands set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static FVarCollector getCollector(Set<String> commands){		
			collector.commands = commands;
			return collector;
		}
	}
	
	/**
	 * Collects all function variables
	 * @author zbynek
	 */
	public class NonFunctionCollector implements Traversing {
		private Set<String> commands;
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof ExpressionNode){
				ExpressionNode en = (ExpressionNode) ev;
				if(en.getRight() instanceof GeoDummyVariable){	
					commands.add(((GeoDummyVariable)en.getRight()).toString(StringTemplate.defaultTemplate));
				}
				if(en.getOperation()==Operation.FUNCTION || en.getOperation() ==Operation.FUNCTION_NVAR
						|| en.getOperation()==Operation.DERIVATIVE)
				return en;	
				if(en.getLeft() instanceof GeoDummyVariable){	
					commands.add(((GeoDummyVariable)en.getLeft()).toString(StringTemplate.defaultTemplate));
				}
			}
			return ev;
		}
		private static NonFunctionCollector collector = new NonFunctionCollector();
		/**
		 * Resets and returns the collector
		 * @param commands set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static NonFunctionCollector getCollector(Set<String> commands){		
			collector.commands = commands;
			return collector;
		}
	}
	
	/**
	 * Replaces function calls by multiplications in cases where left argument is clearly not a function
	 * (see NonFunctionCollector)
	 * @author zbynek
	 */
	public class NonFunctionReplacer implements Traversing {
		private Set<String> commands;
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof Command){
				Command c = (Command) ev;
				if(commands.contains(c.getName()) && c.getArgumentNumber()==1)
					return new GeoDummyVariable(c.getKernel().getConstruction(),c.getName()).wrap().multiply(c.getArgument(0));
			}
			return ev;
		}
		private static NonFunctionReplacer collector = new NonFunctionReplacer();
		/**
		 * Resets and returns the collector
		 * @param commands set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static NonFunctionReplacer getCollector(Set<String> commands){		
			collector.commands = commands;
			return collector;
		}
	}
	/**
	 * Expands f as f(x) or f(x,y) in CAS
	 * @author zbynek
	 */
	public class FunctionExpander implements Traversing {
		
		private ExpressionValue expand(GeoElement geo){
			if(geo instanceof FunctionalNVar)
				return ((FunctionalNVar)geo).getFunctionExpression().deepCopy(geo.getKernel()).traverse(this);
			if(geo instanceof GeoCasCell){
				return ((GeoCasCell)geo).getOutputValidExpression();
			}			
			return geo;	
		}
		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof ExpressionNode){ 
				ExpressionNode en = (ExpressionNode) ev;
				if(en.getOperation()==Operation.FUNCTION){
					GeoElement geo = (GeoElement)en.getLeft();
					if(geo instanceof GeoDummyVariable){
						geo = ((GeoDummyVariable)en.getRight()).getElementWithSameName();
					}
					ExpressionNode en2 = null;
					FunctionVariable fv = null;
					if(geo instanceof FunctionalNVar){
						en2 = (ExpressionNode)  ((FunctionalNVar)geo).getFunctionExpression().deepCopy(geo.getKernel()).traverse(this);
						fv = ((FunctionalNVar)geo).getFunction().getFunctionVariables()[0];
					}
					if(geo instanceof GeoCasCell){
						en2 = ((GeoCasCell)geo).getOutputValidExpression().wrap();
						fv = (FunctionVariable) ((GeoCasCell)geo).getFunctionVariableList().getListElement(0).unwrap();
					}
					ExpressionValue argument = en.getRight().deepCopy(en.getKernel()).traverse(this);
					VariableReplacer vr = VariableReplacer.getReplacer(fv.getSetVarString(), argument);
					return en2.deepCopy(en.getKernel()).traverse(vr);
				}
				else if(en.getOperation()!=Operation.FUNCTION_NVAR
					&& en.getOperation()!=Operation.DERIVATIVE){
					GeoElement geo = null;
					if(en.getLeft() instanceof GeoDummyVariable){
						geo = ((GeoDummyVariable)en.getLeft()).getElementWithSameName();
						en.setLeft(expand(geo));
					}
										
				}
				if(en.getRight()!=null){
					GeoElement geo = null;
					if(en.getRight() instanceof GeoDummyVariable){
						geo = ((GeoDummyVariable)en.getRight()).getElementWithSameName();
						en.setRight(expand(geo));
					}
				}
			}

			return ev;
		}
		private static FunctionExpander collector = new FunctionExpander();
		/**
		 * Resets and returns the collector
		 * @return function expander
		 */
		public static FunctionExpander getCollector(){		
			return collector;
		}
	}


}

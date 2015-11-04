package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.Operation;

/**
 * Traversing objects are allowed to traverse through Equation, MyList,
 * ExpressionNode and MyVecNode(3D) structure to perform some action, e.g.
 * replace one type of objects by another.
 * 
 * @author Zbynek Konecny
 */
public interface Traversing {
	/**
	 * Processes a value locally (no recursion)
	 * 
	 * @param ev
	 *            value to process
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
			if (ev == oldObj)
				return newObj;
			return ev;
		}

		private static Replacer replacer = new Replacer();

		/**
		 * Creates a replacer
		 * 
		 * @param original
		 *            object to be replaced
		 * @param replacement
		 *            replacement
		 * @return replacer
		 */
		public static Replacer getReplacer(ExpressionValue original,
				ExpressionValue replacement) {
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
			if (ev instanceof Command) {
				Command c = (Command) ev;
				String cmdName = app.getReverseCommand(c.getName());
				Throwable t = null;
				try {
					Commands.valueOf(cmdName);
				} catch (Throwable t1) {
					t = t1;
				}
				if (t == null)
					return ev;
				MyList argList = new MyList(c.getKernel());
				for (int i = 0; i < c.getArgumentNumber(); i++) {
					argList.addListElement(c.getItem(i).traverse(this));
				}
				return new ExpressionNode(c.getKernel(), new GeoDummyVariable(c
						.getKernel().getConstruction(), c.getName()),
						Operation.FUNCTION_NVAR, argList);
			}
			return ev;
		}

		private static CommandReplacer replacer = new CommandReplacer();

		/**
		 * @param app
		 *            application (needed to check which commands are valid)
		 * @return replacer
		 */
		public static CommandReplacer getReplacer(App app) {
			replacer.app = app;
			return replacer;
		}
	}

	/**
	 * Replaces all commands "ggbvect" by their first argument, sets the CAS
	 * vector flag for future serialization
	 *
	 */
	public class GgbVectRemover implements Traversing {

		private static final GgbVectRemover remover = new GgbVectRemover();

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command) {
				Command command = (Command) ev;
				if (command.getName().equals("ggbvect")) {
					ExpressionNode en = command.getArgument(0);
					ExpressionValue unwrapped = en.unwrap();
					if (unwrapped instanceof MyVecNode) {
						MyVecNode vecNode = (MyVecNode) unwrapped;
						vecNode.setCASVector();
						return vecNode;
					} else if (unwrapped instanceof MyVec3DNode) {
						MyVec3DNode vec3DNode = (MyVec3DNode) unwrapped;
						vec3DNode.setCASVector();
						return vec3DNode;
					}
				}

			}
			return ev;
		}

		/**
		 * @return instance of this traversing
		 */
		public static GgbVectRemover getInstance() {
			return remover;
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
			if ((ev instanceof Variable || ev instanceof FunctionVariable || ev instanceof GeoDummyVariable)
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
		public int getReplacements() {
			return replacements;
		}

		private static VariablePolyReplacer replacer = new VariablePolyReplacer();

		/**
		 * @param fv
		 *            function variable
		 * @return replacer
		 */
		public static VariablePolyReplacer getReplacer(FunctionVariable fv) {
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
		private boolean replaceFVs;

		public ExpressionValue process(ExpressionValue ev) {
			boolean hitClass = ev instanceof GeoDummyVariable
					|| (replaceFVs && ev instanceof FunctionVariable);
			if (!hitClass
					|| !var.equals(ev.toString(StringTemplate.defaultTemplate))) {
				return ev;
			}
			didReplacement = true;
			return newObj;
		}

		private static GeoDummyReplacer replacer = new GeoDummyReplacer();

		/**
		 * @param varStr
		 *            variable name
		 * @param replacement
		 *            replacement object
		 * @param replaceFVs
		 *            whether function variables should be replaced also
		 * @return replacer
		 */
		public static GeoDummyReplacer getReplacer(String varStr,
				ExpressionValue replacement, boolean replaceFVs) {
			replacer.var = varStr;
			replacer.newObj = replacement;
			replacer.didReplacement = false;
			replacer.replaceFVs = replaceFVs;
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
	 * 
	 * @author zbynek
	 *
	 */
	public class VariableReplacer implements Traversing {
		private List<String> vars = new ArrayList<String>();
		private List<ExpressionValue> newObjs = new ArrayList<ExpressionValue>();
		private int replacements;
		private Kernel kernel;

		public ExpressionValue process(ExpressionValue ev) {
			ExpressionValue val;
			if ((val = contains(ev)) != null)
				return new ExpressionNode(kernel, val);
			if (!(ev instanceof Variable || ev instanceof FunctionVariable || ev instanceof GeoDummyVariable))
				return ev;
			if ((val = getVar(ev.toString(StringTemplate.defaultTemplate))) == null) {
				return ev;
			}
			replacements++;
			return val;
		}

		/**
		 * @return number of replacements since getReplacer was called
		 */
		public int getReplacements() {
			return replacements;
		}

		private static ExpressionValue contains(ExpressionValue ev) {
			for (int i = 0; i < replacer.newObjs.size(); i++) {
				if (replacer.newObjs.get(i) == ev) {
					return replacer.newObjs.get(i);
				}
			}
			return null;
		}

		private static ExpressionValue getVar(String var) {
			for (int i = 0; i < replacer.vars.size(); i++) {
				if (var.equals(replacer.vars.get(i))) {
					return replacer.newObjs.get(i);
				}
			}
			return null;
		}

		/**
		 * @param varStr
		 *            variable name
		 * @param replacement
		 *            replacement object
		 */
		public static void addVars(String varStr, ExpressionValue replacement) {
			replacer.vars.add(varStr);
			replacer.newObjs.add(replacement);
		}

		private static VariableReplacer replacer = new VariableReplacer();

		/**
		 * @param varStr
		 *            variable name
		 * @param replacement
		 *            replacement object
		 * @param kernel
		 *            kernel
		 * @return replacer
		 */
		public static VariableReplacer getReplacer(String varStr,
				ExpressionValue replacement, Kernel kernel) {
			replacer.vars.clear();
			replacer.newObjs.clear();

			replacer.vars.add(varStr);
			replacer.newObjs.add(replacement);

			replacer.replacements = 0;
			replacer.kernel = kernel;
			return replacer;
		}

		/**
		 * When calling this method, make sure you initialize the replacer with
		 * the {@link #addVars(String, ExpressionValue)} method
		 * 
		 * @return replacer
		 */
		public static VariableReplacer getReplacer() {
			replacer.vars.clear();
			replacer.newObjs.clear();

			replacer.replacements = 0;
			return replacer;
		}
	}

	/**
	 * Renames Spreadsheet Variables with new name according to offset (dx,dy)
	 * 
	 * @author michael
	 *
	 */
	public class SpreadsheetVariableRenamer implements Traversing {
		private int dx;
		private int dy;
		private ArrayList<Variable> variables = new ArrayList<Variable>();

		/**
		 * Renames Spreadsheet Variables with new name according to offset
		 * (dx,dy)
		 * 
		 * @param dx
		 *            x-offset
		 * @param dy
		 *            y-offset
		 */
		public SpreadsheetVariableRenamer(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
			variables.clear();
		}

		public ExpressionValue process(ExpressionValue ev) {

			// check variables to avoid problem with updating twice
			// eg If[0 < A1 < 5, 0, 100] going to If[0 < A3 < 5, 0, 100]
			if (ev instanceof Variable && !variables.contains(ev)) {
				Variable v = (Variable) ev;

				String name = v.getName(StringTemplate.defaultTemplate);

				// App.debug("found VARIABLE: "+name);
				if (GeoElementSpreadsheet.spreadsheetPattern.test(name)) {

					String newName = RelativeCopy.updateCellNameWithOffset(
							name, dx, dy);

					// App.debug("FOUND SPREADSHEET VARIABLE: "+name + " -> " +
					// newName);

					// make sure new cell is autocreated if it doesn't exist
					// already
					v.getKernel().getConstruction().lookupLabel(newName, true);

					// App.debug("setting new name to: "+newName);

					v.setName(newName);
					variables.add(v);

				}
			} else if (ev instanceof GeoElement) {

				GeoElement geo = (GeoElement) ev;

				String name = geo.getLabelSimple();

				if (GeoElementSpreadsheet.spreadsheetPattern.test(name)) {

					String newName = RelativeCopy.updateCellNameWithOffset(
							name, dx, dy);

					// make sure new cell is autocreated if it doesn't exist
					// already
					// and return it
					return geo.getConstruction().lookupLabel(newName, true);

				}

			}

			return ev;
		}

	}

	/**
	 * Replaces undefined variables by sliders
	 * 
	 * @author michael
	 *
	 */
	public class ReplaceUndefinedVariables implements Traversing {
		private final Kernel kernel;
		private String[] except;
		private TreeSet<GeoNumeric> undefined;

		/**
		 * Replaces undefined variables by sliders
		 * 
		 * @param kernel
		 *            kernel
		 * @param undefined
		 *            list of undefined vars (write only)
		 * @param skip
		 *            list of labels to skip
		 * 
		 */
		public ReplaceUndefinedVariables(Kernel kernel,
				TreeSet<GeoNumeric> undefined, String[] skip) {
			this.kernel = kernel;
			this.undefined = undefined;
			this.except = skip;
		}

		public ExpressionValue process(ExpressionValue ev) {

			if (ev instanceof Variable) {
				Variable v = (Variable) ev;
				String name = v.getName(StringTemplate.defaultTemplate);
				ExpressionValue replace = kernel.lookupLabel(name, true, kernel.isResolveUnkownVarsAsDummyGeos());
				if (replace == null) {
					replace = Variable.replacement(kernel, name);
				}
				if (replace instanceof Variable
						&& !name.equals(kernel.getConstruction()
								.getRegisteredFunctionVariable())
						&& !isException(name)) {
					name = ((Variable) replace)
							.getName(StringTemplate.defaultTemplate);

					GeoNumeric slider = new GeoNumeric(
							kernel.getConstruction(), name, 1);
					undefined.add(slider);
					boolean visible = !kernel.getApplication().has(
							Feature.AV_EXTENSIONS)
							|| !kernel.getApplication().showView(
									App.VIEW_ALGEBRA)
							|| kernel.getApplication().showAutoCreatedSlidersInEV();
					GeoNumeric.setSliderFromDefault(slider, false, visible);
					return ev;
				}
			}

			return ev;
		}

		private boolean isException(String name) {
			if (except == null) {
				return false;
			}
			for (int i = 0; i < except.length; i++) {
				if (except[i].equals(name)) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * Collect undefined variables
	 * 
	 * @author michael
	 *
	 */
	public class CollectUndefinedVariables implements Traversing {

		private TreeSet<String> tree = new TreeSet<String>();
		private TreeSet<String> localTree = new TreeSet<String>();

		/**
		 * 
		 * @return list of undefined variables (repeats removed)
		 */
		public TreeSet<String> getResult() {
			tree.removeAll(localTree);
			return tree;
		}

		/**
		 * Collect undefined variables by sliders
		 * 
		 */
		public CollectUndefinedVariables() {
		}

		public ExpressionValue process(ExpressionValue ev) {

			if (ev instanceof Variable) {
				Variable v = (Variable) ev;
				String name = v.getName(StringTemplate.defaultTemplate);
				if (v.getKernel().getApplication().getParserFunctions()
						.isReserved(name)) {
					return ev;
				}
				ExpressionValue ret;
				ret = v.getKernel().lookupLabel(name);
				if (ret == null) {
					ret = Variable.replacement(v.getKernel(), name);
				}

				if (ret instanceof Variable
						&& !v.getKernel().getConstruction()
								.isRegistredFunctionVariable(name)) {
					// App.debug("found undefined variable: "
					// + ((Variable) ret)
					// .getName(StringTemplate.defaultTemplate));
					tree.add(((Variable) ret)
							.getName(StringTemplate.defaultTemplate));
				}
			} else if (ev instanceof Command) {// Iteration[a+1, a, {1},4]

				Command com = (Command) ev;
				if (("Sequence".equals(com.getName()) && com
						.getArgumentNumber() > 2)
						|| "KeepIf".equals(com.getName())
						|| "CountIf".equals(com.getName())) {
					localTree.add(com.getArgument(1).toString(
							StringTemplate.defaultTemplate));
				} else if ("Surface".equals(com.getName())) {
					int len = com.getArgumentNumber();
					localTree.add(com.getArgument(len - 3).toString(
							StringTemplate.defaultTemplate));
					localTree.add(com.getArgument(len - 6).toString(
							StringTemplate.defaultTemplate));
				} else if (("IterationList".equals(com.getName()) || "Iteration"
						.equals(com.getName())) && com.getArgumentNumber() > 3) {

					for (int i = 1; i < com.getArgumentNumber() - 2; i++) {
						localTree.add(com.getArgument(i).toString(
								StringTemplate.defaultTemplate));
					}
				} else if ("Zip".equals(com.getName())) {
					for (int i = 1; i < com.getArgumentNumber(); i += 2) {
						localTree.add(com.getArgument(i).toString(
								StringTemplate.defaultTemplate));
					}
				} else if ("TriangleCurve".equals(com.getName())) {
					localTree.add("A");
					localTree.add("B");
					localTree.add("C");
				}
			}
			return ev;
		}

	}

	/**
	 * Collect FunctionVariables
	 * 
	 * @author michael
	 *
	 */
	public class CollectFunctionVariables implements Traversing {

		private ArrayList<FunctionVariable> al = new ArrayList<FunctionVariable>();

		/**
		 * 
		 * @return list of undefined variables (repeats removed)
		 */
		public ArrayList<FunctionVariable> getResult() {
			return al;
		}

		/**
		 * Collect undefined variables by sliders
		 * 
		 */
		public CollectFunctionVariables() {
		}

		public ExpressionValue process(ExpressionValue ev) {

			if (ev instanceof FunctionVariable) {
				al.add((FunctionVariable) ev);
			}

			return ev;
		}

	}

	/**
	 * Replaces arbconst(), arbint(), arbcomplex() by auxiliary numerics
	 */
	public class ArbconstReplacer implements Traversing {
		private MyArbitraryConstant arbconst;

		public ExpressionValue process(ExpressionValue ev) {
			if (!ev.isExpressionNode())
				return ev;
			ExpressionNode en = (ExpressionNode) ev;
			if (en.getOperation() == Operation.ARBCONST) {
				return arbconst.nextConst(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBINT) {
				return arbconst.nextInt(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBCOMPLEX) {
				return arbconst.nextComplex(en.getLeft().evaluateDouble());
			}
			return en;
		}

		private static ArbconstReplacer replacer = new ArbconstReplacer();

		/**
		 * @param arbconst
		 *            arbitrary constant handler
		 * @return replacer
		 */
		public static ArbconstReplacer getReplacer(MyArbitraryConstant arbconst) {
			replacer.arbconst = arbconst;
			return replacer;
		}
	}

	/**
	 * Replaces powers by roots or vice versa
	 */
	public class PowerRootReplacer implements Traversing {
		private boolean toRoot;
		/** functions with 100th root are numerically unstable */
		private static int MAX_ROOT = 99;

		public ExpressionValue process(ExpressionValue ev) {
			if (!ev.isExpressionNode())
				return ev;
			((ExpressionNode) ev).replacePowersRoots(toRoot, MAX_ROOT);
			return ev;
		}

		private static PowerRootReplacer replacer = new PowerRootReplacer();

		/**
		 * @param toRoot
		 *            true to replace exponents by roots
		 * @return replacer
		 */
		public static PowerRootReplacer getReplacer(boolean toRoot) {
			replacer.toRoot = toRoot;
			return replacer;
		}
	}

	/**
	 * Replaces diff function comming from GIAC
	 * 
	 * @author Zbynek
	 *
	 */
	public class DiffReplacer implements Traversing {

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (!ev.isExpressionNode()) {
				return ev;
			}
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() != Operation.DIFF) {
				return ev;
			}
			Kernel kernel = en.getKernel();
			ExpressionValue expr = en.getLeft();
			ExpressionValue var = en.getRight();
			ExpressionValue deg;
			if (expr instanceof MyNumberPair) {

				var = ((MyNumberPair) expr).y;
				expr = ((MyNumberPair) expr).x;
				deg = en.getRight();
			} else {
				deg = new MyDouble(kernel, 1);
			}
			String expStr = expr.toString(StringTemplate.defaultTemplate);
			int nameEnd = expStr.indexOf('(');
			if (expStr.indexOf('[') > 0) {
				nameEnd = nameEnd > 0 ? Math.min(nameEnd, expStr.indexOf('['))
						: expStr.indexOf('[');
			}
			String funLabel = nameEnd > 0 ? expStr.substring(0, nameEnd)
					: expStr;

			ExpressionValue diffArg = new MyDouble(kernel, Double.NaN);
			ExpressionValue mult = new MyDouble(kernel, 1);
			if (expr.unwrap() instanceof Command) {
				diffArg = ((Command) expr.unwrap()).getArgument(0);
				if (diffArg.unwrap() instanceof FunctionVariable
						&& diffArg
								.toString(StringTemplate.defaultTemplate)
								.equals(var
										.toString(StringTemplate.defaultTemplate))) {
					// keep mult
				} else if (Kernel.isEqual(deg.evaluateDouble(), 1)) {
					CASGenericInterface cas = kernel.getGeoGebraCAS()
							.getCurrentCAS();
					Command derivCommand = new Command(kernel, "Derivative",
							false);
					derivCommand.addArgument(diffArg.wrap());
					derivCommand.addArgument(var.wrap());
					derivCommand.addArgument(deg.wrap());
					mult = cas.evaluateToExpression(derivCommand, null, kernel);
				} else {
					App.printStacktrace(ValidExpression.debugString(diffArg));
					App.printStacktrace(ValidExpression.debugString(var));
					mult = new MyDouble(kernel, Double.NaN);
				}
			}

			// derivative of f gives f'
			ExpressionNode derivative = new ExpressionNode(kernel,
					new Variable(kernel, funLabel), // function label "f"
					Operation.DERIVATIVE, deg);
			// function of given variable gives f'(t)
			return new ExpressionNode(kernel, derivative, Operation.FUNCTION,
					diffArg).multiplyR(mult); // Variable
		}

		/**
		 * Singleton instance
		 */
		public static final DiffReplacer INSTANCE = new DiffReplacer();
	}

	/**
	 * Goes through the ExpressionValue and collects all derivatives from
	 * expression nodes into arrays
	 */
	public class PrefixRemover implements Traversing {

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Variable) {
				return new Variable(((Variable) ev).getKernel(), ev.toString(
						StringTemplate.defaultTemplate).replace(
						Kernel.TMP_VARIABLE_PREFIX, ""));
			}
			return ev;
		}

		private static PrefixRemover collector = new PrefixRemover();

		/**
		 * Resets and returns the collector
		 * 
		 * @return derivative collector
		 */
		public static PrefixRemover getCollector() {
			return collector;
		}

	}

	/**
	 * Goes through the ExpressionValue and collects all derivatives from
	 * expression nodes into arrays
	 */
	public class CommandCollector implements Traversing {
		private Set<Command> commands;

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command)
				commands.add((Command) ev);
			return ev;
		}

		private static CommandCollector collector = new CommandCollector();

		/**
		 * Resets and returns the collector
		 * 
		 * @param commands
		 *            set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static CommandCollector getCollector(Set<Command> commands) {
			collector.commands = commands;
			return collector;
		}
	}

	/**
	 * Collects all function variables
	 * 
	 * @author zbynek
	 */
	public class FVarCollector implements Traversing {
		private Set<String> commands;

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Equation){
				return ev.wrap();
			}
			if (ev instanceof FunctionVariable)
				commands.add(((FunctionVariable) ev).getSetVarString());
			return ev;
		}

		private static FVarCollector collector = new FVarCollector();

		/**
		 * Resets and returns the collector
		 * 
		 * @param commands
		 *            set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static FVarCollector getCollector(Set<String> commands) {
			collector.commands = commands;
			return collector;
		}
	}

	/**
	 * Collects all function variables
	 * 
	 * @author zbynek
	 */
	public class NonFunctionCollector implements Traversing {
		private Set<String> commands;

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ev;
				if (en.getRight() instanceof GeoDummyVariable) {
					add(((GeoDummyVariable) en.getRight()));
				}
				if (en.getOperation() == Operation.FUNCTION
						|| en.getOperation() == Operation.FUNCTION_NVAR
						|| en.getOperation() == Operation.DERIVATIVE)
					return en;
				if (en.getLeft() instanceof GeoDummyVariable) {
					add(((GeoDummyVariable) en.getLeft()));
				}
			}
			return ev;
		}

		private void add(GeoDummyVariable dummy) {
			String str = dummy.toString(StringTemplate.defaultTemplate);
			if (dummy.getKernel().getApplication().getParserFunctions()
					.isReserved(str))
				return;
			commands.add(str);

		}

		private static NonFunctionCollector collector = new NonFunctionCollector();

		/**
		 * Resets and returns the collector
		 * 
		 * @param commands
		 *            set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static NonFunctionCollector getCollector(Set<String> commands) {
			collector.commands = commands;
			return collector;
		}
	}

	/**
	 * Collects all dummy variables and function fariables except those that are
	 * in the role of a function eg. for f(x) we will collect x, but not f
	 * 
	 * @author bencze
	 */
	public class DummyVariableCollector implements Traversing {
		private Set<String> commands;

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ev;
				if (en.getRight() instanceof GeoDummyVariable
						|| en.getRight() instanceof FunctionVariable) {
					add(en.getRight());
				}
				if (en.getOperation() == Operation.FUNCTION
						|| en.getOperation() == Operation.FUNCTION_NVAR
						|| en.getOperation() == Operation.DERIVATIVE)
					return en;
				if (en.getLeft() instanceof GeoDummyVariable
						|| en.getLeft() instanceof FunctionVariable) {
					add(en.getLeft());
				}
			}
			return ev;
		}

		private void add(ExpressionValue dummy) {
			String str = dummy.toString(StringTemplate.defaultTemplate);
			commands.add(str);

		}

		private static DummyVariableCollector collector = new DummyVariableCollector();

		/**
		 * Resets and returns the collector
		 * 
		 * @param commands
		 *            set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static DummyVariableCollector getCollector(Set<String> commands) {
			collector.commands = commands;
			return collector;
		}
	}

	/**
	 * Replaces function calls by multiplications in cases where left argument
	 * is clearly not a function (see NonFunctionCollector)
	 * 
	 * @author zbynek
	 */
	public class NonFunctionReplacer implements Traversing {
		private Set<String> commands;

		public ExpressionValue process(ExpressionValue ev) {
			if(ev instanceof ExpressionNode){
				ExpressionNode en = (ExpressionNode) ev;
				if(en.getOperation() == Operation.POWER && en.getLeft() instanceof Command){
					Command c = (Command)en.getLeft();
					if(commands.contains(c.getName())){
						return new GeoDummyVariable(
								c.getKernel().getConstruction(), c.getName())
								.wrap().multiply(c.getArgument(0).traverse(this).wrap().power(en.getRight()));
					}
				}
				if(en.getOperation() == Operation.FACTORIAL && en.getLeft() instanceof Command){
					Command c = (Command)en.getLeft();
					if(commands.contains(c.getName())){
						return new GeoDummyVariable(
								c.getKernel().getConstruction(), c.getName())
								.wrap().multiply(c.getArgument(0).traverse(this).wrap().factorial());
					}
				}
				if(en.getOperation() == Operation.SQRT_SHORT && en.getLeft() instanceof Command){
					Command c = (Command)en.getLeft();
					if(commands.contains(c.getName())){
						return new GeoDummyVariable(
								c.getKernel().getConstruction(), c.getName())
								.wrap().sqrt().multiply(c.getArgument(0).traverse(this));
					}
				}
			}
			if (ev instanceof Command) {
				Command c = (Command) ev;
				if (commands.contains(c.getName())
						&& c.getArgumentNumber() == 1)
					return new GeoDummyVariable(
							c.getKernel().getConstruction(), c.getName())
							.wrap().multiply(c.getArgument(0).traverse(this));
			}
			return ev;
		}

		private static NonFunctionReplacer collector = new NonFunctionReplacer();

		/**
		 * Resets and returns the collector
		 * 
		 * @param commands
		 *            set into which we want to collect the commands
		 * @return derivative collector
		 */
		public static NonFunctionReplacer getCollector(Set<String> commands) {
			collector.commands = commands;
			return collector;
		}
	}

	/**
	 * Expands f as f(x) or f(x,y) in CAS
	 * 
	 * @author zbynek
	 */
	public class FunctionExpander implements Traversing {

		private ExpressionValue expand(GeoElement geo) {
			if (geo instanceof FunctionalNVar)
				return ((FunctionalNVar) geo).getFunctionExpression()
						.deepCopy(geo.getKernel()).traverse(this);
			if (geo instanceof GeoCasCell) {
				App.debug(geo + ":"
						+ ((GeoCasCell) geo).getOutputValidExpression());
				return ((GeoCasCell) geo).getOutputValidExpression()
						.deepCopy(geo.getKernel()).traverse(this).unwrap();
			}
			return geo;
		}

		private static boolean contains(GeoDummyVariable gdv) {
			if (variables == null) {
				return false;
			}
			for (FunctionVariable funvar : variables) {
				if (funvar.toString(StringTemplate.defaultTemplate).equals(
						gdv.toString(StringTemplate.defaultTemplate))) {
					return true;
				}
			}
			return false;
		}

		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof ExpressionNode) {
				final ExpressionNode en = (ExpressionNode) ev;
				if (en.getOperation() == Operation.FUNCTION
						|| en.getOperation() == Operation.FUNCTION_NVAR
						|| en.getOperation() == Operation.VEC_FUNCTION) {
					ExpressionValue geo = en.getLeft().unwrap();
					ExpressionValue deriv = null;
					if (geo instanceof ExpressionNode
							&& ((ExpressionNode) geo).getOperation() == Operation.DERIVATIVE) {
						// template not important, right it is a constant
						// MyDouble anyway
						deriv = ((ExpressionNode) geo).getRight().evaluate(
								StringTemplate.defaultTemplate);
						geo = ((ExpressionNode) geo).getLeft().unwrap();
					}
					if (geo instanceof GeoDummyVariable) {
						geo = ((GeoDummyVariable) geo).getElementWithSameName();
					}
					ExpressionNode en2 = null;
					FunctionVariable[] fv = null;
					if (geo instanceof GeoCurveCartesian) {
						ExpressionValue en2x = ((GeoCurveCartesian) geo)
								.getFunX().getFunctionExpression()
								.getCopy(((GeoCurveCartesian) geo).getKernel())
								.traverse(this);
						ExpressionValue en2y = ((GeoCurveCartesian) geo)
								.getFunY().getFunctionExpression()
								.getCopy(((GeoCurveCartesian) geo).getKernel())
								.traverse(this);
						en2 = new MyVecNode(
								((GeoCurveCartesian) geo).getKernel(), en2x,
								en2y).wrap();
						fv = ((GeoCurveCartesian) geo).getFunctionVariables();
					}
					if (geo instanceof FunctionalNVar) {
						en2 = (ExpressionNode) ((FunctionalNVar) geo)
								.getFunctionExpression()
								.getCopy(((FunctionalNVar) geo).getKernel())
								.traverse(this);
						fv = ((FunctionalNVar) geo).getFunction()
								.getFunctionVariables();
					}
					if (geo instanceof GeoCasCell) {
						ValidExpression ve = ((GeoCasCell) geo)
								.getOutputValidExpression();
						// related to #4126 -- maybe not needed though
						if (((GeoCasCell) geo).isKeepInputUsed()) {
							ve = expand((GeoCasCell) geo).wrap();
						}
						en2 = ve.unwrap() instanceof FunctionNVar ? ((FunctionNVar) ve
								.unwrap()).getExpression() : ve.wrap();
						en2 = en2.traverse(this).wrap();
						en2 = en2.getCopy(((GeoCasCell) geo).getKernel());
						fv = ((GeoCasCell) geo).getFunctionVariables();
					}
					if (deriv != null) {
						CASGenericInterface cas = en.getKernel()
								.getGeoGebraCAS().getCurrentCAS();
						Command derivCommand = new Command(en.getKernel(),
								"Derivative", false);
						derivCommand.addArgument(en2);
						derivCommand.addArgument(fv[0].wrap());
						derivCommand.addArgument(deriv.wrap());
						en2 = cas.evaluateToExpression(derivCommand, null,
								en.getKernel()).wrap();

					}
					if (fv != null) {
						ExpressionValue argument = en.getRight().wrap()
								.getCopy(en.getKernel()).traverse(this)
								.unwrap();
						ExpressionValue ithArg = argument;
						VariableReplacer vr = VariableReplacer.getReplacer();

						// variables have to be replaced with one traversing
						// or else replacing f(x,y) with f(y,x)
						// will result in f(x, x)
						for (int i = 0; i < fv.length; i++) {
							if (en.getOperation() == Operation.FUNCTION_NVAR) {
								ithArg = ((MyList) argument).getListElement(i);
							}
							VariableReplacer.addVars(fv[i].getSetVarString(),
									ithArg);
						}
						en2 = en2.traverse(vr).wrap();
						return en2;
					}
				}
 else if (en.getOperation() == Operation.DERIVATIVE) {
					// should not get there

				} else {
					GeoElement geo = null;
					if (en.getLeft() instanceof GeoDummyVariable
							&& !contains((GeoDummyVariable) en.getLeft())) {
						geo = ((GeoDummyVariable) en.getLeft())
								.getElementWithSameName();
						if (geo != null) {
							en.setLeft(expand(geo));
						}
					}

				}
				if (en.getRight() != null) {
					GeoElement geo = null;
					if (en.getRight() instanceof GeoDummyVariable
							&& !contains((GeoDummyVariable) en.getRight())) {
						geo = ((GeoDummyVariable) en.getRight())
								.getElementWithSameName();
						if (geo != null) {
							en.setRight(expand(geo));
						}
					}
				}
			} else if (ev instanceof GeoDummyVariable
					&& !contains((GeoDummyVariable) ev)) {
				GeoElement geo = ((GeoDummyVariable) ev)
						.getElementWithSameName();
				if (geo != null)
					return expand(geo);
			} else if (ev instanceof GeoCasCell) {
				// expanding the cell here is necessary #4126
				if (((GeoCasCell) ev).isKeepInputUsed()) {
					return expand((GeoCasCell) ev);
				}
				return ((GeoCasCell) ev).getOutputValidExpression().wrap()
						.getCopy(((GeoCasCell) ev).getKernel());
			} else if (ev instanceof FunctionNVar) {
				variables = ((FunctionNVar) ev).fVars;
			}
			return ev;
		}

		private static FunctionExpander collector = new FunctionExpander();
		// store function variables if needed
		private static FunctionVariable[] variables = null;

		/**
		 * Resets and returns the collector
		 * 
		 * @return function expander
		 */
		public static FunctionExpander getCollector() {
			variables = null;
			return collector;
		}
	}

	/**
	 * Returns the RHS side of an equation if LHS is y Eg y=x! returns x!
	 * 
	 * @author bencze
	 *
	 */
	public class FunctionCreator implements Traversing {
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Equation) {
				Equation eq = (Equation) ev;
				if (eq.getLHS() != null
						&& eq.getLHS().getLeft() instanceof GeoDummyVariable) {
					GeoDummyVariable gdv = (GeoDummyVariable) eq.getLHS()
							.getLeft();
					if (gdv.toString(StringTemplate.defaultTemplate)
							.equals("y")) {
						return eq.getRHS().unwrap();
					}
				}
			}
			return ev;
		}

		private static FunctionCreator creator = new FunctionCreator();

		/**
		 * @return instance of FunctionCreater
		 */
		public static FunctionCreator getCreator() {
			return creator;
		}
	}

	/**
	 * Removes commands from a given expression and returns the first argument
	 * of the command
	 */
	public class CommandRemover implements Traversing {
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command) {
				Command ec = (Command) ev;
				for (int i = 0; i < commands.length; i++) {
					if (ec.getName().equals(commands[i])) {
						return ec.getArgument(0).unwrap();
					}
				}
			}
			return ev;
		}

		private static CommandRemover remover = new CommandRemover();
		private static String[] commands;

		/**
		 * Get the remover
		 * 
		 * @param commands1
		 *            commands to be removed
		 * @return command remover
		 */
		public static CommandRemover getRemover(String... commands1) {
			commands = commands1;
			return remover;
		}
	}

	/**
	 * @author bencze Replaces some of the unknown commands from CAS to known
	 *         commands/expression node values to GeoGebra
	 */
	public class CASCommandReplacer implements Traversing {
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command) {
				Command ec = (Command) ev;
				if ("x".equals(ec.getName())) {
					return new ExpressionNode(ec.getKernel(),
							ec.getArgument(0), Operation.XCOORD, null);
				} else if ("y".equals(ec.getName())) {
					return new ExpressionNode(ec.getKernel(),
							ec.getArgument(0), Operation.YCOORD, null);
				} else if ("z".equals(ec.getName())) {
					return new ExpressionNode(ec.getKernel(),
							ec.getArgument(0), Operation.ZCOORD, null);
				}

			}
			return ev;
		}

		/**
		 * Replacer object
		 */
		public static CASCommandReplacer replacer = new CASCommandReplacer();

	}
}

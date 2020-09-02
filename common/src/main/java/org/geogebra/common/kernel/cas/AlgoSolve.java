package org.geogebra.common.kernel.cas;

import java.util.Collections;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.DegreeVariableReplacer;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

/**
 * Use Solve cas command from AV
 */
public class AlgoSolve extends AlgoElement implements UsesCAS {

	private GeoList solutions;
	private GeoElement equations;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	private Commands type;
	private GeoElement hint;

	/**
	 * @param c
	 *            construction
	 * @param eq
	 *            equation or list thereof
	 * @param hint
	 *            variables or variable = initial value
	 * @param type
	 *            whether to use Solve / NSolve / NSolutions / Solutions
	 */
	public AlgoSolve(Construction c, GeoElement eq, GeoElement hint,
			Commands type) {
		super(c);
		this.type = type;
		this.equations = eq;
		this.hint = hint;
		this.solutions = new GeoList(cons);
		setInputOutput();
		compute();
		if (type != Commands.PlotSolve) {
			solutions.setEuclidianVisible(false);
		}
	}

	@Override
	protected void setInputOutput() {
		input = hint == null ? equations.asArray()
				: new GeoElement[] { equations, hint };
		setOnlyOutput(solutions);
		setDependencies();

	}

	@Override
	public void compute() {
		boolean symbolic = solutions.size() < 1 || solutions.isSymbolicMode();
		boolean trig = false;
		StringBuilder sb = new StringBuilder(type.getCommand());
		sb.append('[');
		String varString = null;
		if (equations instanceof GeoList) {
			TreeSet<String> vars = new TreeSet<>();
			sb.append("{");
			for (int i = 0; i < ((GeoList) equations).size(); i++) {
				if (i != 0) {
					sb.append(',');
				}
				trig = printCAS(((GeoList) equations).get(i), sb) || trig;
				addVars(((GeoList) equations).get(i), vars);
			}
			sb.append("}");
			varString = "{" + StringUtil.join(",", vars) + "}";
		} else {
			trig = printCAS(equations, sb)	;
		}
		if (hint != null) {
			sb.append(',');
			printHint(sb);
		} else if (varString != null) {
			sb.append(',');
			sb.append(varString);
		}
		sb.append("]");
		try {
			arbconst.startBlocking();
			String solns = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);

			GeoList raw = kernel.getAlgebraProcessor().evaluateToList(solns);
			// if we re-evaluate something with arbconst, it will only have
			// undefined lines
			if (raw == null || !elementsDefined(raw)) {
				solutions.clear();
				solutions.setUndefined();
				return;
			}
			if (equations.isGeoList() && raw.size() > 1
					&& (!raw.get(0).isGeoList())) {
				solutions.clear();
				solutions.add(raw);
			} else {
				solutions.set(raw);
			}
			showUserForm(solutions, trig);
			if (type == Commands.Solutions && symbolic) {
				solutions.setSymbolicMode(true, false);
			}

		} catch (Throwable e) {
			solutions.setUndefined();
			e.printStackTrace();
		}
		if (type != Commands.PlotSolve) {
			solutions.setNotDrawable();
		}
	}

	private static void addVars(GeoElement geo, TreeSet<String> vars) {
		if (geo instanceof EquationValue) {
			Collections.addAll(vars, ((EquationValue) geo).getEquationVariables());
		}
	}

	private boolean elementsDefined(GeoList raw) {
		for (int i = 0; i < raw.size(); i++) {
			if (!raw.get(i).isDefinitionValid()) {
				return false;
			}
			if (raw.get(i).isGeoList()
					&& !elementsDefined((GeoList) raw.get(i))) {
				return false;
			}
		}
		return true;
	}

	private void showUserForm(GeoList solutions2, boolean trig) {
		for (int i = 0; i < solutions2.size(); i++) {

			GeoElement el = solutions2.get(i);
			if (el instanceof GeoLine) {
				((GeoLine) el).setMode(GeoLine.EQUATION_USER);
			}
			if (el instanceof GeoPlaneND) {
				((GeoPlaneND) el).setMode(GeoLine.EQUATION_USER);
			}

			if (el instanceof GeoList) {
				showUserForm((GeoList) el, trig);

			}
			else if (trig) {
				ExpressionValue def = el.getDefinition().unwrap();

				if (def instanceof Equation) {
					ExpressionValue rhs = ((Equation) def).getRHS().unwrap();
					((Equation) def).setRHS(makeAngle(rhs).wrap());
				}
				if (el instanceof GeoNumeric) {
					GeoAngle copy = new GeoAngle(cons);
					copy.set(el);
					solutions2.setListElement(i, copy);
				}
			}
		}
		solutions2.setSymbolicMode(true, false);

	}

	private ExpressionValue makeAngle(ExpressionValue rhs) {
		if (rhs instanceof MyDouble) {
			((MyDouble) rhs).setAngle();
		}
		else if (rhs.isExpressionNode()) {
			return rhs.traverse(new Traversing() {

				@Override
				public ExpressionValue process(ExpressionValue ev) {
					if (ev instanceof ExpressionNode) {
						ExpressionNode en = ev.wrap();
						if (en.getOperation() == Operation.MULTIPLY
								&& MyDouble.exactEqual(Math.PI,
									en.getRight().evaluateDouble())) {
							MyDouble angle = new MyDouble(kernel,
									en.getLeft().evaluateDouble() * Math.PI);
							angle.setAngle();
							return angle;

						}
					}
					return ev;
				}

			});
		}
		return rhs;
	}

	private static boolean printCAS(GeoElement equations2, StringBuilder sb) {

		String definition;
		ExpressionValue definitionObject = null;

		if (equations2.getDefinition() != null) {
			definitionObject = equations2.getDefinition()
					.deepCopy(equations2.getKernel())
					.traverse(FunctionExpander.getCollector());

			definition = definitionObject
					.toValueString(StringTemplate.prefixedDefaultSF);
		} else {
			definition = equations2
					.toValueString(StringTemplate.prefixedDefaultSF);
			try {
				definitionObject = equations2.getKernel().getParser()
						.parseGeoGebraExpression(definition);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append(definition);
		if (equations2.getKernel().degreesMode()) {
			return definitionObject.inspect(DegreeVariableReplacer
					.getReplacer(equations2.getKernel()));
		}
		return false;
	}

	private void printHint(StringBuilder sb) {
		String definition;
		if (hint.getDefinition() != null) {
			definition = hint.getDefinition()
					.toValueString(StringTemplate.prefixedDefaultSF);
		} else {
			definition = hint
					.toValueString(StringTemplate.prefixedDefaultSF);
		}
		sb.append(definition);
	}

	@Override
	public GetCommand getClassName() {
		return type;
	}

	/**
	 * Switch between Solve and NSolve and run the update cascade
	 * 
	 * @return whether this is numeric after the toggle
	 */
	public boolean toggleNumeric() {
		type = opposite(type);
		compute();
		solutions.updateCascade();
		return type == Commands.NSolve || type == Commands.NSolutions;
	}

	private static Commands opposite(Commands type2) {
		switch (type2) {
		case Solutions:
			return Commands.NSolutions;
		case NSolutions:
			return Commands.Solutions;
		case NSolve:
			return Commands.Solve;
		default:
			return Commands.NSolve;
		}
	}
}

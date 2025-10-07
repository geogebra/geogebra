package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Algo for updating GeoSymbolic when dependencies change
 * 
 * @author Zbynek
 *
 */
public class AlgoDependentSymbolic extends AlgoElement implements UsesCAS {

	private GeoSymbolic symbolic;
	private ArrayList<GeoElement> vars;

	/**
	 * @param c
	 *            construction
	 * @param def
	 *            symbolic variable definition
	 * @param vars
	 *            parent variables
	 */
	public AlgoDependentSymbolic(
			Construction c,
			@Nonnull ExpressionNode def,
			ArrayList<GeoElement> vars,
			ArbitraryConstantRegistry constant,
			boolean addToConstructionList) {
		super(c, addToConstructionList);
		this.symbolic = new GeoSymbolic(cons, def);
		symbolic.setArbitraryConstant(constant);
		this.vars = vars;
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = vars.toArray(new GeoElement[1]);
		setOnlyOutput(symbolic);
		setDependencies();
	}

	@Override
	public void compute() {
		symbolic.computeOutput();
		for (ExpressionValue val: symbolic.getDefinition()) {
			if (val instanceof Command
					&& Commands.CellRange.name().equals(((Command) val).getName())) {
				String start = getCellName(((Command) val).getArgument(0));
				String end = getCellName(((Command) val).getArgument(1));
				kernel.getApplication().getSpreadsheetTableModel().getCellRangeManager()
						.getAlgoCellRange(cons, null, start, end).getList()
						.addToUpdateSetOnly(this);
			}
		}
	}

	private String getCellName(ExpressionValue v) {
		ExpressionValue unwarapped = v.unwrap();
		if (unwarapped instanceof GeoDummyVariable) {
			return ((GeoDummyVariable) unwarapped).getVarName();
		}
		return unwarapped instanceof GeoElement ? ((GeoElement) unwarapped).getLabelSimple() : "";
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return symbolic.getDefinition().toString(tpl);
	}

	@Override
	protected String toExpString(StringTemplate tpl) {
		ExpressionNode definition = symbolic.getDefinition();
		String rhs = definition.toString(tpl);
		if (isFunction(definition)) {
			String lhs = symbolic.getLabel(tpl) + "(" + symbolic.getVarString(tpl) + ") = ";
			return lhs + rhs;
		} else if (isEquation(definition) && rhs.contains("=")) {
			return symbolic.getLabel(tpl) + ": " + rhs;
		}
		return rhs;
	}

	private boolean isFunction(ExpressionNode definition) {
		return definition.isLeaf() && (definition.getLeft() instanceof Function);
	}

	private boolean isEquation(ExpressionNode definition) {
		return definition.isLeaf() && (definition.getLeft() instanceof Equation);
	}
}

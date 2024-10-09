package org.geogebra.common.kernel.advanced;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHelper;

public class AlgoParseToNumberOrFunction extends AlgoElement {

	private final GeoText text;
	private final GeoElement result;
	private final Commands cmd;
	private final GeoList vars;
	private final String label;
	private GeoElement[] inputForUpdateSetPropagation;
	private final Set<GeoElement> referencedObjects = new HashSet<>();

	/**
	 * @param cons construction
	 * @param text text to be parsed
	 * @param vars function variables (for multivariable)
	 * @param cmd ParseToNumber or ParseToFunction
	 */
	public AlgoParseToNumberOrFunction(Construction cons, GeoText text,
			GeoList vars, Commands cmd, String label) {
		super(cons);
		this.cmd = cmd;
		this.text = text;
		this.vars = vars;
		this.result = initResult();
		this.label = label;
		setInputOutput();
		compute();
	}

	private GeoElement initResult() {
		if (cmd == Commands.ParseToNumber) {
			return new GeoNumeric(cons);
		}
		return vars == null ? new GeoFunction(cons) : new GeoFunctionNVar(cons);
	}

	@Override
	protected void setInputOutput() {
		input = vars == null ? new GeoElement[] {text} : new GeoElement[] {text, vars};
		setOnlyOutput(result);
		setDependencies();
	}

	@Override
	public void compute() {
		GeoElementND num;
		AlgebraProcessor ap = kernel.getAlgebraProcessor();
		String textToParse = text.getTextStringSafe();
		if (cmd == Commands.ParseToNumber) {
			EvalInfo evalInfo = new EvalInfo(!cons.isSuppressLabelsActive(), true)
					.withAutocreate(false);
			num = ap.evaluateToNumeric(textToParse, ErrorHelper.silent(), evalInfo);
			if (num != null) {
				updateReferences(num.getDefinition());
			}
		} else if (vars == null) {
			num = ap.evaluateToFunction(textToParse, true);
			if (num != null) {
				if (!((GeoFunction) num).validate(false, false)) {
					num.setUndefined();
				}
				updateReferences(((GeoFunction) num).getFunctionExpression());
			}
		} else {
			vars.elements().filter(GeoElement::isGeoText).forEach(fVar ->
					cons.registerFunctionVariable(((GeoText) fVar).getTextString()));
			num = ap.evaluateToFunctionNVar(textToParse,
							true, false);
			if (num != null) {
				updateReferences(((GeoFunctionNVar) num).getFunctionExpression());
			}
		}
		if (num == null || num.toGeoElement().isEmptySpreadsheetCell()) {
			result.setUndefined();
		} else {
			result.set(num);
		}
	}

	private void updateReferences(ExpressionNode definition) {
		referencedObjects.clear();
		if (definition != null) {
			definition.getVariables(referencedObjects, SymbolicMode.NONE);
		}
		if (label != null && referencedObjects.remove(kernel.lookupLabel(label))) {
			result.setUndefined();
		}
		if (!referencedObjects.isEmpty()) {
			inputForUpdateSetPropagation = new GeoElement[referencedObjects.size() + 1];
			inputForUpdateSetPropagation[0] = text;
			int i = 1;
			for (GeoElement geo : referencedObjects) {
				inputForUpdateSetPropagation[i] = geo;
				i++;
				geo.addToUpdateSetOnly(this);
				if (result.hasAlgoUpdateSet()) {
					for (AlgoElement child: result.getAlgoUpdateSet()) {
						geo.addToUpdateSetOnly(child);
					}
				}
			}
		}
	}

	@Override
	public GetCommand getClassName() {
		return cmd;
	}

	@Override
	public GeoElement[] getInputForUpdateSetPropagation() {
		if (referencedObjects.isEmpty()) {
			return input;
		}
		return inputForUpdateSetPropagation;
	}
}

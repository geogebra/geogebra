package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.advanced.AlgoParseToNumberOrFunction;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;
	private final TableValuesView tableValues;
	private final TableValuesModel model;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 * @param tableValues Table Values view
	 */
	public TableValuesInputProcessor(Construction cons, TableValuesView tableValues) {
		this.cons = cons;
		this.tableValues = tableValues;
		model = tableValues.getTableValuesModel();
	}

	@Override
	public void processInput(@Nonnull String input, GeoList list, int rowIndex) {
		GeoElement element = parseInput(input);
		if (model.isEmptyValue(element) && (list == null || rowIndex >= list.size())) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		tableValues.set(element, ensureList(list), rowIndex);
		if (list != null) {
			tableValues.updateValuesNoBatch(list);
		}
	}

	private GeoList ensureList(GeoList list) {
		if (list != null) {
			ensureXColumnLabel(list);
			return list;
		}
		GeoList column = new GeoList(cons);
		column.setAuxiliaryObject(true);
		column.notifyAdd();
		tableValues.doShowColumn(column);
		return column;
	}

	private void ensureXColumnLabel(GeoList list) {
		if (tableValues.getValues() == list && !list.isLabelSet()) {
			model.setupXValues(list);
			list.setLabel(cons.buildIndexedLabel("x", false));
			if (GeoGebraConstants.PROBABILITY_APPCODE
					.equals(cons.getApplication().getConfig().getSubAppCode())) {
				cons.removeFromConstructionList(list);
			}
		}
	}

	private GeoElement parseInput(String input) {
		String trimmedInput = input.trim();
		if (trimmedInput.equals("")) {
			return model.createEmptyValue();
		}
		try {
			double parsedInput = Double.parseDouble(trimmedInput);
			return model.createValue(parsedInput);
		} catch (NumberFormatException e) {
			preloadScripting();
			AlgoParseToNumberOrFunction algoParseToNumberOrFunction =
					new AlgoParseToNumberOrFunction(cons,
							new GeoText(cons, input), null, Commands.ParseToNumber, null);
			GeoElement el = algoParseToNumberOrFunction.getOutput(0);
			if (el.isDefined()) {
				algoParseToNumberOrFunction.remove();
				el.setParentAlgorithm(null);
			}
			return el;
		}
	}

	private void preloadScripting() {
		try {
			cons.getKernel().getAlgebraProcessor().getCommandDispatcher()
					.getScriptingCommandProcessorFactory();
		} catch (CommandNotLoadedError err) {
			// preloading
		}
	}
}

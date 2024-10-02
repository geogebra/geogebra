package org.geogebra.common.gui.view.table;

import java.util.HashSet;
import java.util.List;

import org.geogebra.common.gui.view.table.dialog.RegressionBuilder;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatsBuilder;
import org.geogebra.common.gui.view.table.dimensions.LaTeXTextSizeMeasurer;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * The TableValuesView implementation.
 */
public class TableValuesView implements TableValues, SettingListener {

	private static final int MAX_ROWS = 200;

	@Weak
	private final Kernel kernel;
	@Weak
	private final App app;
	@Weak
	private final TableSettings settings;

	private final SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private final LabelController labelController;
	private final HashSet<GeoElementND> elements;
	private final TableValuesInputProcessor processor;
	private boolean algebraLabelVisibleCheck = true;

	/**
	 * Create a new Table Value View.
	 * @param kernel {@link Kernel}
	 */
	public TableValuesView(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
		Settings set = app.getSettings();
		settings = set.getTable();
		model = new SimpleTableValuesModel(kernel, settings);
		elements = new HashSet<>();
		labelController = new LabelController();
		processor = new TableValuesInputProcessor(kernel.getConstruction(), this);
		createTableDimensions();
		settings.addListener(this);
	}

	private void createTableDimensions() {
		LaTeXTextSizeMeasurer measurer = new LaTeXTextSizeMeasurer(16);
		dimensions = new TableValuesViewDimensions(model, measurer);
		model.registerListener(dimensions);
	}

	@Override
	public void showColumn(GeoEvaluatable evaluatable) {
		doShowColumn(evaluatable);
		storeUndoInfo();
	}

	void doShowColumn(GeoEvaluatable evaluatable) {
		if (elements.contains(evaluatable) && evaluatable.hasTableOfValues()) {
			if (evaluatable.getTableColumn() < 0) {
				evaluatable.setTableColumn(model.getColumnCount());
			}
			ensureHasLabel(evaluatable);
			model.addEvaluatable(evaluatable);
		}
	}

	private void ensureHasLabel(GeoEvaluatable evaluatable) {
		if (!evaluatable.getRawCaption().isEmpty() && evaluatable.isLabelSet()) {
			return;
		}
		if (algebraLabelVisibleCheck) {
			labelController.ensureHasLabel(evaluatable);
		} else {
			labelController.ensureHasLabelNoAlgebra(evaluatable);
		}
	}

	@Override
	public void hideColumn(GeoEvaluatable evaluatable) {
		evaluatable.setTableColumn(-1);
		model.removeEvaluatable(evaluatable, true);
		if (evaluatable.isGeoList()) {
			evaluatable.remove();
		}
		storeUndoInfo();
	}

	@Override
	public int getColumn(GeoEvaluatable evaluatable) {
		return model.getEvaluatableIndex(evaluatable);
	}

	@Override
	public GeoEvaluatable getEvaluatable(int column) {
		return model.getEvaluatable(column);
	}

	@Override
	public void setValues(double valuesMin, double valuesMax, double valuesStep)
			throws InvalidValuesException {
		assertValidValues(valuesMin, valuesMax, valuesStep);
		setSettingsValues(valuesMin, valuesMax, valuesStep);
		storeUndoInfo();
	}

	private void setSettingsValues(double valuesMin, double valuesMax, double valuesStep) {
		model.startBatchUpdate();
		settings.beginBatch();
		settings.setValuesMin(valuesMin);
		settings.setValuesMax(valuesMax);
		settings.setValuesStep(valuesStep);
		updateValuesFromRange();
		settings.endBatch();
		model.endBatchUpdate(true);
	}

	@Override
	public double getValuesMin() {
		return settings.getValuesMin();
	}

	@Override
	public double getValuesMax() {
		return settings.getValuesMax();
	}

	@Override
	public double getValuesStep() {
		return settings.getValuesStep();
	}

	@Override
	public GeoList getValues() {
		return model.getValueList();
	}

	private static void assertValidValues(double min, double max, double step)
			throws InvalidValuesException {
		double points = (max - min) / step;
		if (points > MAX_ROWS || points < 0) {
			throw new InvalidValuesException("TooManyRows");
		}
	}

	private void updateValuesFromRange() {
		double min = getValuesMin();
		double max = getValuesMax();
		double step = getValuesStep();
		boolean emptyModel = min == 0 && max == 0 && step == 0;
		boolean invalidValues = min > max || step <= 0;
		if (emptyModel) {
			clearValuesInternal();
		} else {
			int row = 0;
			double value = min;
			if (invalidValues) {
				setValuesRow(value, row++);
			} else {
				while (value <= max) {
					setValuesRow(value, row++);
					value += step;
				}
				if (value - step < max - Kernel.STANDARD_PRECISION) {
					setValuesRow(max, row++);
				}
			}
			for (int index = getValues().size() - 1; index >= row; index--) {
				clearValuesRow(index);
			}
			getValues().updateRepaint();
		}

	}

	private void clearValuesRow(int row) {
		model.set(model.createEmptyValue(), getValues(), row);
	}

	private void setValuesRow(double value, int row) {
		model.set(model.createValue(value), getValues(), row);
	}

	@Override
	public void set(GeoElement element, GeoList column, int rowIndex) {
		model.set(element, column, rowIndex);
		storeUndoInfo();
	}

	@Override
	public TableValuesModel getTableValuesModel() {
		return model;
	}

	@Override
	public TableValuesDimensions getTableValuesDimensions() {
		return dimensions;
	}

	@Override
	public String getValuesMinStr() {
		return format(getValuesMin());
	}

	@Override
	public String getValuesMaxStr() {
		return format(getValuesMax());
	}

	@Override
	public String getValuesStepStr() {
		return format(getValuesStep());
	}

	private String format(double x) {
		return kernel.format(x, StringTemplate.defaultTemplate);
	}

	@Override
	public void add(GeoElement geo) {
		elements.add(geo);
		// Show element if it's loaded from file
		if (geo instanceof GeoEvaluatable) {
			GeoEvaluatable evaluatable = (GeoEvaluatable) geo;
			if (evaluatable.getTableColumn() >= 0) {
				doShowColumn(evaluatable);
			}
		}
	}

	@Override
	public void remove(GeoElement geo) {
		elements.remove(geo);
		removeFromModel(geo);
	}

	private void removeFromModel(GeoElement geo) {
		if (geo instanceof GeoEvaluatable) {
			GeoEvaluatable evaluatable = (GeoEvaluatable) geo;
			if (model.getEvaluatableIndex(evaluatable) > -1) {
				model.removeEvaluatable(evaluatable, false);
			}
		}
	}

	@Override
	public void rename(GeoElement geo) {
		if (geo instanceof GeoEvaluatable) {
			GeoEvaluatable evaluatable = (GeoEvaluatable) geo;
			if (labelController.hasLabel(geo)) {
				model.updateEvaluatableName(evaluatable);
			} else {
				removeFromModel(geo);
			}
		}
	}

	@Override
	public void update(GeoElement geo) {
		if (geo instanceof GeoEvaluatable) {
			GeoEvaluatable evaluatable = (GeoEvaluatable) geo;
			if (geo.hasTableOfValues() || geo == getValues()) {
				model.updateEvaluatable(evaluatable);
			} else {
				model.removeEvaluatable(evaluatable, false);
			}
		} else if ((geo.isIndependent() || Algos.isUsedFor(Commands.ParseToNumber, geo))
				&& (geo instanceof GeoNumeric || geo instanceof GeoText)) {
			model.maybeUpdateListElement(geo);
		}
	}

	@Override
	public void clearView() {
		model.clearModel();
		settings.setValueList(null);
		setSettingsValues(0, 0, 0);
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// ignore
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// ignore
	}

	@Override
	public void repaintView() {
		// ignore
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void reset() {
		// ignore
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// ignore
	}

	@Override
	public int getViewID() {
		return App.VIEW_TABLE;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void startBatchUpdate() {
		model.startBatchUpdate();
	}

	@Override
	public void endBatchUpdate() {
		model.endBatchUpdate(true);
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// ignore
	}

	@Override
	public boolean isEmpty() {
		return model == null
				|| (model.getColumnCount() == 1 && model.isEvaluatableEmptyList(0));
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		GeoList valueList = ((TableSettings) settings).getValueList();
		model.updateValuesColumn();
		if (valueList == null) {
			updateValuesFromRange();
		} else {
			model.updateEvaluatable(valueList);
		}
	}

	@Override
	public List<StatisticGroup> getStatistics1Var(int column) {
		return new StatsBuilder(model.getEvaluatable(column))
				.getStatistics1Var(model.getHeaderAt(column));
	}

	@Override
	public List<StatisticGroup> getStatistics2Var(int column) {
		return new StatsBuilder(model.getEvaluatable(0),
				model.getEvaluatable(column)).getStatistics2Var(model.getHeaderAt(0),
				model.getHeaderAt(column));
	}

	@Override
	public List<RegressionSpecification> getRegressionSpecifications(int column) {
		GeoList[] cleanLists = new StatsBuilder(getEvaluatable(0),
				getEvaluatable(column)).getCleanLists2Var();
		return RegressionSpecification.getForListSize(cleanLists[0].size());
	}

	@Override
	public List<StatisticGroup> getRegression(int column, RegressionSpecification regression) {
		return new RegressionBuilder(model.getEvaluatable(0), model.getEvaluatable(column))
				.getRegression(regression);
	}

	@Override
	public GeoElement plotRegression(int column, RegressionSpecification regression) {
		GeoEvaluatable xVal = model.getEvaluatable(0);
		GeoEvaluatable yVal = model.getEvaluatable(column);
		MyVecNode points = new MyVecNode(kernel, xVal, yVal);
		Command cmd = regression.buildCommand(kernel, points);
		EvalInfo info = new EvalInfo(true, true)
				.withSymbolicMode(kernel.getSymbolicMode());
		try {
			GeoElement element = kernel.getAlgebraProcessor().processValidExpression(cmd, info)[0];
			app.storeUndoInfo();
			return element;
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	@Override
	public TableValuesProcessor getProcessor() {
		return processor;
	}

	@Override
	public void addAndShow(GeoElement geo) {
		add(geo);
		showColumn((GeoEvaluatable) geo);
	}

	@Override
	public void clearValues() {
		clearValuesInternal();
		storeUndoInfo();
	}

	private void clearValuesInternal() {
		model.startBatchUpdate();
		for (int row = getValues().size() - 1; row >= 0; row--) {
			clearValuesRow(row);
		}
		updateValuesNoBatch(getValues());
		model.endBatchUpdate(false);
	}

	protected void updateValuesNoBatch(GeoList list) {
		if (list.hasAlgoUpdateSet()) {
			kernel.getConstruction().updateAllAlgosInSet(list.getAlgoUpdateSet());
		}
	}

	private void storeUndoInfo() {
		app.storeUndoInfo();
	}

	/**
	 * @return whether all columns other than x are undefined
	 */
	@Override
	public boolean hasNoDefinedFunctions() {
		for (int i = 1; i < model.getColumnCount(); i++) {
			if (model.getEvaluatable(i).isDefined()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Disable checking algebraLabelVisible
	 * for Scientific data table.
	 */
	public void noAlgebraLabelVisibleCheck() {
		this.algebraLabelVisibleCheck = false;
	}

	/**
	 * Prepares for tabular data import.
	 * @param nrRows The number of rows to import.
	 * @param nrColumns The number of columns to import.
	 * @param columnNames The column names from the CSV header row, if present. null otherwise.
	 */
	// Data import
	public void startImport(int nrRows, int nrColumns, String[] columnNames) {
		model.startImport(nrRows, nrColumns, columnNames);
	}

	/**
	 * Collects a row of data during import.
	 * @param values The numeric values for the current row. For any null entries in
	 *               this array, rawValues will have the original string value.
	 * @param rawValues The original strings behind the values.
	 */
	public void importRow(Double[] values, String[] rawValues) {
		model.importRow(values, rawValues);
	}

	/**
	 * Cancels import.
	 */
	public void cancelImport() {
		model.cancelImport();
	}

	/**
	 * Commits the imported data.
	 */
	public void commitImport() {
		elements.clear();
		model.commitImport();
	}
}

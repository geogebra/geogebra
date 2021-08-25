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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * The TableValuesView implementation.
 */
public class TableValuesView implements TableValues, SettingListener {

	private static final double[] DEFAULT_RANGE = new double[]{};
	private static final int MAX_ROWS = 200;

	@Weak
	private Kernel kernel;
	@Weak
	private App app;
	@Weak
	private TableSettings settings;

	private GeoList values;
	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private LabelController labelController;
	private HashSet<GeoElementND> elements;
	private TableValuesInputProcessor processor;

	/**
	 * Create a new Table Value View.
	 * @param kernel {@link Kernel}
	 */
	public TableValuesView(Kernel kernel) {
		this.values = new GeoList(kernel.getConstruction());
		this.model = new SimpleTableValuesModel(kernel, values);
		this.app = kernel.getApplication();
		Settings set = app.getSettings();
		this.settings = set.getTable();
		this.elements = new HashSet<>();
		this.kernel = kernel;
		this.labelController = new LabelController();
		this.processor = new TableValuesInputProcessor(kernel.getConstruction(), this);
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
		app.storeUndoInfo();
	}

	private void doShowColumn(GeoEvaluatable evaluatable) {
		if (elements.contains(evaluatable) && evaluatable.hasTableOfValues()) {
			if (evaluatable.getTableColumn() < 0) {
				evaluatable.setTableColumn(model.getColumnCount());
			}
			ensureHasLabel(evaluatable);
			model.addEvaluatable(evaluatable);
		}
	}

	private void ensureHasLabel(GeoEvaluatable evaluatable) {
		labelController.ensureHasLabel(evaluatable);
	}

	@Override
	public void hideColumn(GeoEvaluatable evaluatable) {
		evaluatable.setTableColumn(-1);
		model.removeEvaluatable(evaluatable);
		app.storeUndoInfo();
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
		// empty view: next undo point will be created when geo is added
		if (!isEmpty()) {
			app.storeUndoInfo();
		}
	}

	private void setSettingsValues(double valuesMin, double valuesMax, double valuesStep) {
		settings.beginBatch();
		settings.setValuesMin(valuesMin);
		settings.setValuesMax(valuesMax);
		settings.setValuesStep(valuesStep);
		settings.endBatch();
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
		return values;
	}

	private static void assertValidValues(double min, double max, double step)
			throws InvalidValuesException {
		double points = (max - min) / step;
		if (points > MAX_ROWS || points < 0) {
			throw new InvalidValuesException("TooManyRows");
		}
	}

	private void updateModelValues() {
		double[] range = createRangeOrDefault();
		model.setValues(range);
	}

	private double[] createRangeOrDefault() {
		try {
			double min = getValuesMin();
			double max = getValuesMax();
			double step = getValuesStep();
			return min == 0 && max == 0 && step == 0
					? DEFAULT_RANGE
					: DoubleUtil.range(min, max, step);
		} catch (OutOfMemoryError error) {
			return DEFAULT_RANGE;
		}
	}

	@Override
	public TableValuesModel getTableValuesModel() {
		return model;
	}

	/**
	 * @param column index of column
	 * @return geo at the given column
	 */
	public GeoElement getGeoAt(int column) {
		return this.model.getEvaluatable(column).toGeoElement();
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
				model.removeEvaluatable(evaluatable);
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
			if (geo.hasTableOfValues() || geo == values) {
				model.updateEvaluatable(evaluatable);
			} else {
				model.removeEvaluatable(evaluatable);
			}
		} else if (geo instanceof GeoNumeric) {
			model.maybeUpdateListElement(geo);
		}
	}

	@Override
	public void clearView() {
		model.clearModel();
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
		model.endBatchUpdate();
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// ignore
	}

	@Override
	public boolean isEmpty() {
		return model == null || model.getColumnCount() == 1;
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		updateModelValues();
	}

	/**
	 * @param column column
	 * @return one variable stats
	 */
	public List<StatisticGroup> getStatistics1Var(int column) {
		return new StatsBuilder(model.getEvaluatable(column))
				.getStatistics1Var(model.getHeaderAt(column));
	}

	/**
	 * @param column column
	 * @return two variable stats for first and given column
	 */
	public List<StatisticGroup> getStatistics2Var(int column) {
		return new StatsBuilder(model.getEvaluatable(0),
				model.getEvaluatable(column)).getStatistics2Var(model.getHeaderAt(0),
				model.getHeaderAt(1));
	}

	/**
	 * @param column column
	 * @param regression regression type
	 * @param degree regression polynomial degree
	 * @return regression parameters for first and given column
	 */
	public List<StatisticGroup> getRegression(int column, Regression regression, int degree) {
		return new RegressionBuilder(model.getEvaluatable(0), model.getEvaluatable(column))
				.getRegression(regression, degree);
	}

	/**
	 * @param regression regression type
	 * @param degree regression polynomial degree
	 * @param column column
	 */
	public void plotRegression(int column, Regression regression, int degree) {
		GeoEvaluatable xVal = model.getEvaluatable(0);
		GeoEvaluatable yVal = model.getEvaluatable(column);
		MyVecNode points = new MyVecNode(kernel, xVal, yVal);
		Command cmd = regression.buildCommand(kernel, degree, points);
		try {
			kernel.getAlgebraProcessor().processValidExpression(cmd);
		} catch (Exception e) {
			Log.error(e);
		}
	}

	@Override
	public TableValuesProcessor getProcessor() {
		return processor;
	}
}

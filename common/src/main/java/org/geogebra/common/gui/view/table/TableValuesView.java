package org.geogebra.common.gui.view.table;

import java.util.HashSet;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.util.DoubleUtil;

/**
 * The TableValuesView implementation.
 */
public class TableValuesView implements TableValues, SettingListener {

	private static final int MAX_ROWS = 200;

	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private HashSet<GeoElement> elements;
	private TableSettings settings;
	private Kernel kernel;

	/**
	 * Create a new Table Value View.
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 */
	public TableValuesView(Kernel kernel) {
		this.model = new SimpleTableValuesModel(kernel);
		Settings set = kernel.getApplication().getSettings();
		this.settings = set.getTable();
		this.elements = new HashSet<>();
		this.kernel = kernel;
		createTableDimensions();
		updateModelValues();
		settings.addListener(this);
	}

	private void createTableDimensions() {
		AwtFactory factory = AwtFactory.getPrototype();
		GFont font = factory.newFont("SansSerif", GFont.PLAIN, 16);
		GBufferedImage bufferedImage = factory.createBufferedImage(2, 2, false);
		GGraphics2D graphics = bufferedImage.createGraphics();
		GFontRenderContext fontRenderContext = graphics.getFontRenderContext();
		dimensions = newTableValuesViewDimensions(fontRenderContext);
		dimensions.setFont(font);
		model.registerListener(dimensions);
	}

	/**
	 * 
	 * @param context
	 *            the font renderer context.
	 * @return a new TableValuesViewDimensions instance.
	 */
	protected TableValuesViewDimensions newTableValuesViewDimensions(GFontRenderContext context) {
		return new TableValuesViewDimensions(model, AwtFactory.getPrototype(), context);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void showColumn(GeoEvaluatable evaluatable) {
		if (elements.contains(evaluatable) && evaluatable.hasTableOfValues()) {
			if (evaluatable.getTableColumn() < 0) {
				evaluatable.setTableColumn(model.getColumnCount());
			}

			model.addEvaluatable(evaluatable);
		}
	}

	@Override
	public void hideColumn(GeoEvaluatable evaluatable) {
		model.removeEvaluatable(evaluatable);
	}

	@Override
	public int getColumn(GeoEvaluatable evaluatable) {
		int index = model.getEvaluatableIndex(evaluatable);
		index += index > -1 ? 1 : 0;
		return index;
	}

	@Override
	public GeoEvaluatable getEvaluatable(int column) {
		return model.getEvaluatable(column);
	}

	@Override
	public void setValues(double valuesMin, double valuesMax, double valuesStep)
			throws InvalidValuesException {
		assertValidValues(valuesMin, valuesMax, valuesStep);
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

	private static void assertValidValues(double min, double max, double step)
			throws InvalidValuesException {
		if (!isFinite(min) && !isFinite(max)) {
			throw new InvalidValuesException("TableValuesMinMaxInvalid",
					"Values min and/or max are invalid");
		}
		if (min > max) {
			throw new InvalidValuesException("TableValuesMinGreater",
					"Values min is greater than values max");
		}
		if (!isFinite(step) || step <= 0) {
			throw new InvalidValuesException("TableValuesStepInvalid", "Values step is invalid");
		}

		if (Math.abs(max - min) / step > MAX_ROWS) {
			throw new InvalidValuesException("TableValuesRowLimit",
					"Maximum number of rows is limited to 200. Please, change range values.");
		}
	}

	private static boolean isFinite(double x) {
		return !Double.isInfinite(x) && !Double.isNaN(x);
	}

	private void updateModelValues() {
		double[] values = calculateValues();
		model.setValues(values);
	}

	private double[] calculateValues() {
		double[] values;
		if (getValuesMin() == getValuesMax()) {
			values = new double[] { getValuesMin() };
		} else {
			double stepsDouble = (getValuesMax() - getValuesMin())
					/ getValuesStep();
			int stepsInt = (int) stepsDouble;
			int steps = DoubleUtil.isInteger(stepsDouble) ? stepsInt : stepsInt + 1;
			values = new double[steps + 1];
			values[steps] = getValuesMax();
			for (int i = 0; i < steps; i++) {
				values[i] = getValuesMin() + i * getValuesStep();
			}
		}
		return values;
	}

	@Override
	public TableValuesModel getTableValuesModel() {
		return model;
	}

	/**
	 * @param column
	 *            index of column
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
				showColumn(evaluatable);
			}
		}
	}

	@Override
	public void remove(GeoElement geo) {
		elements.remove(geo);
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
			model.updateEvaluatableName(evaluatable);
		}
	}

	@Override
	public void update(GeoElement geo) {
		if (geo instanceof GeoEvaluatable) {
			GeoEvaluatable evaluatable = (GeoEvaluatable) geo;
			if (geo.hasTableOfValues()) {
				model.updateEvaluatable(evaluatable);
			} else {
				model.removeEvaluatable(evaluatable);
			}
		}
	}

	@Override
	public void clearView() {
		double[] values = calculateValues();
		model.clearModel(values);
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
		return App.VIEW_TABLE_OF_VALUES;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void startBatchUpdate() {
		// ignore
	}

	@Override
	public void endBatchUpdate() {
		// ignore
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

}

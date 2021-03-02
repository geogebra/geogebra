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
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

/**
 * The TableValuesView implementation.
 */
public class TableValuesView implements TableValues, SettingListener {

	private static final double[] DEFAULT_RANGE = new double[] {-2.0, -1.0, 0.0, 1.0, 2.0};
	private static final int MAX_ROWS = 200;

	@Weak
	private Kernel kernel;
	@Weak
	private App app;
	@Weak
	private TableSettings settings;

	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private LabelController labelController;
	private HashSet<GeoElementND> elements;

	/**
	 * Create a new Table Value View.
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 */
	public TableValuesView(Kernel kernel) {
		this.model = new SimpleTableValuesModel(kernel);
		this.app = kernel.getApplication();
		Settings set = app.getSettings();
		this.settings = set.getTable();
		this.elements = new HashSet<>();
		this.kernel = kernel;
		this.labelController = new LabelController();
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
		dimensions = new TableValuesViewDimensions(model, AwtFactory.getPrototype(),
				fontRenderContext);
		dimensions.setFont(font);
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
		if (model.getColumnCount() == 1) {
			setDefaultValues();
		}
		app.storeUndoInfo();
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
			return DoubleUtil.range(getValuesMin(), getValuesMax(), getValuesStep());
		} catch (OutOfMemoryError error) {
			return DEFAULT_RANGE;
		}
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
			if (geo.hasTableOfValues()) {
				model.updateEvaluatable(evaluatable);
			} else {
				model.removeEvaluatable(evaluatable);
			}
		}
	}

	@Override
	public void clearView() {
		model.clearModel();
		setDefaultValues();
	}

	private void setDefaultValues() {
		setSettingsValues(TableSettings.DEFAULT_MIN, TableSettings.DEFAULT_MAX,
				TableSettings.DEFAULT_STEP);
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

}

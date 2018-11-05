package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;

/**
 * The TableValuesView implementation.
 */
public class TableValuesView implements TableValues {

	private double valuesMin = -2.0;
	private double valuesMax = 2.0;
	private double valuesStep = 1.0;

	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private List<GeoElement> elements;

	/**
	 * Create a new Table Value View.
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 */
	public TableValuesView(Kernel kernel) {
		this.model = new SimpleTableValuesModel(kernel);
		this.elements = new ArrayList<>();
		createTableDimensions();
		updateModelValues();
	}

	private void createTableDimensions() {
		AwtFactory factory = AwtFactory.getPrototype();
		GFont font = factory.newFont("SansSerif", GFont.PLAIN, 16);
		GBufferedImage bufferedImage = factory.createBufferedImage(2, 2, false);
		GGraphics2D graphics = bufferedImage.createGraphics();
		GFontRenderContext fontRenderContext = graphics.getFontRenderContext();
		dimensions = new TableValuesViewDimensions(model, AwtFactory.getPrototype(), fontRenderContext);
		dimensions.setFont(font);
		model.registerListener(dimensions);
	}

	@Override
	public void showColumn(Evaluatable evaluatable) {
		if (elements.contains(evaluatable)) {
			model.addEvaluatable(evaluatable);
		}
	}

	@Override
	public void hideColumn(Evaluatable evaluatable) {
		model.removeEvaluatable(evaluatable);
	}

	@Override
	public int getColumn(Evaluatable evaluatable) {
		int index = model.getEvaluatableIndex(evaluatable);
		index += index > -1 ? 1 : 0;
		return index;
	}

	@Override
	public void setValues(double valuesMin, double valuesMax, double valuesStep)
			throws InvalidValuesException {
		assertValidValues(valuesMin, valuesMax, valuesStep);
		this.valuesMin = valuesMin;
		this.valuesMax = valuesMax;
		this.valuesStep = valuesStep;
		updateModelValues();
	}

	@Override
	public double getValuesMin() {
		return valuesMin;
	}

	@Override
	public double getValuesMax() {
		return valuesMax;
	}

	@Override
	public double getValuesStep() {
		return valuesStep;
	}

	private void assertValidValues(double min, double max, double step)
			throws InvalidValuesException {
		if (!isFinite(min) && !isFinite(max)) {
			throw new InvalidValuesException("Values min and/or max are invalid");
		}
		if (min > max) {
			throw new InvalidValuesException("Values min is greater than values max");
		}
		if (Double.isNaN(step) || Double.isInfinite(step) || step <= 0) {
			throw new InvalidValuesException("Values step is invalid");
		}
	}

	private boolean isFinite(double x) {
		return !Double.isInfinite(x) && !Double.isNaN(x);
	}

	private void updateModelValues() {
		double[] values = calulateValues();
		model.setValues(values);
	}

	private double[] calulateValues() {
		double[] values;
		if (valuesMin == valuesMax) {
			values = new double[] { valuesMin };
		} else {
			double stepsDouble = (valuesMax - valuesMin) / valuesStep;
			int stepsInt = (int) Math.round(stepsDouble);
			int steps = DoubleUtil.isInteger(stepsDouble) ? stepsInt : stepsInt + 1;
			values = new double[steps + 1];
			values[steps] = valuesMax;
			for (int i = 0; i < steps; i++) {
				values[i] = valuesMin + i * valuesStep;
			}
		}
		return values;
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
	public void add(GeoElement geo) {
		elements.add(geo);
	}

	@Override
	public void remove(GeoElement geo) {
		elements.remove(geo);
	}

	@Override
	public void rename(GeoElement geo) {
		if (geo instanceof Evaluatable) {
			Evaluatable evaluatable = (Evaluatable) geo;
			model.updateEvaluatableName(evaluatable);
		}
	}

	@Override
	public void update(GeoElement geo) {
		if (geo instanceof Evaluatable) {
			Evaluatable evaluatable = (Evaluatable) geo;
			model.updateEvaluatable(evaluatable);
		}
	}

	@Override
	public void clearView() {
		double[] values = calulateValues();
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

}

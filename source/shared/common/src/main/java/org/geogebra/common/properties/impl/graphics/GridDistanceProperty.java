package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NumericPropertyWithSuggestions;

import com.himamis.retex.editor.share.util.Unicode;

public class GridDistanceProperty extends NumericPropertyWithSuggestions {
	private final EuclidianView euclidianView;
	private final int axis;

	/**
	 * Constructs a grid distance property.
	 * @param processor algebra processor
	 * @param localization localization for the title
	 * @param euclidianView euclidian view
	 * @param label label of the axis
	 * @param axis the axis for the numbering distance will be set
	 */
	public GridDistanceProperty(AlgebraProcessor processor, Localization localization,
			EuclidianView euclidianView, String label, int axis) {
		super(processor, localization, label);
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	public List<String> getSuggestions() {
		return List.of("1", Unicode.PI_STRING, Unicode.PI_HALF_STRING);
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (!Double.isFinite(value.getDouble())) {
			return;
		}
		double[] ticks = euclidianView.getGridDistances();
		ticks[axis] = value.getDouble();
		euclidianView.setGridDistances(ticks);
		euclidianView.updateBackground();
	}

	@Override
	protected NumberValue getNumberValue() {
		return new MyDouble(euclidianView.getKernel(), euclidianView.getGridDistances()[axis]);
	}
}

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ThicknessPropertyDelegate;

/**
 * Line thickness
 */
public class ThicknessProperty extends AbstractRangeProperty<Integer> {

	private final GeoElementDelegate delegate;

	/**
	 * Constructor
	 * @param localization - localization
	 * @param element - geo
	 * @throws NotApplicablePropertyException - exception
	 */
	public ThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Thickness", null, 9, 1);
		delegate = new ThicknessPropertyDelegate(element);
	}

	/**
	 * Constructor
	 * @param localization - localization
	 * @param max - maximum of range
	 * @param delegate - delegate
	 */
	public ThicknessProperty(Localization localization, int max, GeoElementDelegate delegate) {
		super(localization, "Thickness", null, max, 1);
		this.delegate = delegate;
	}

	@Override
	public Integer getMin() {
		return delegate.getElement().getMinimumLineThickness();
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		setThickness(element, value);
		element.updateRepaint();
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getLineThickness();
	}

	private void setThickness(GeoElement element, int size) {
		if (element instanceof GeoList) {
			GeoList list = (GeoList) element;
			for (int i = 0; i < list.size(); i++) {
				setThickness(list.get(i), size);
			}
		} else if (LineStyleModel.match(element)) {
			element.setLineThickness(size);
		}
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}

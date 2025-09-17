package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LineStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Line style
 */
public class LineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private static final PropertyResource[] icons =
			EuclidianStyleConstants.lineStyleIcons.toArray(new PropertyResource[0]);
	private final AbstractGeoElementDelegate delegate;
	private final boolean hidden;

	/***/
	public LineStyleProperty(Localization localization, GeoElement element, boolean hidden)
			throws NotApplicablePropertyException {
		super(localization, "LineStyle");
		delegate = new LineStylePropertyDelegate(element);
		this.hidden = hidden;
		if (hidden && !element.getKernel().getApplication()
				.isEuclidianView3Dinited()) {
			throw new NotApplicablePropertyException(element);
		}
		setValues(EuclidianStyleConstants.lineStyleList);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (hidden) {
			element.setLineTypeHidden(value);
		} else {
			element.setLineType(value);
		}
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public Integer getValue() {
		return hidden ? delegate.getElement().getLineTypeHidden()
				: delegate.getElement().getLineType();
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}

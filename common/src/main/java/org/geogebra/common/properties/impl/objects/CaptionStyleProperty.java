package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.properties.impl.objects.delegate.CaptionStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Caption style
 */
public class CaptionStyleProperty extends AbstractEnumerableProperty {

	private static final int LABEL_HIDDEN = 0;

	private static final String[] captionStyleNames = {
			"Hidden",
			"Name",
			"NameAndValue",
			"Value",
			"Caption"
	};

	private static final List<Integer> labelModes = Arrays.asList(
			GeoElementND.LABEL_DEFAULT,
			GeoElementND.LABEL_NAME,
			GeoElementND.LABEL_NAME_VALUE,
			GeoElementND.LABEL_VALUE,
			GeoElementND.LABEL_CAPTION);

	private final GeoElementDelegate delegate;

	/***/
	public CaptionStyleProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.Caption");
		delegate = new CaptionStyleDelegate(geoElement);
		setValuesAndLocalize(captionStyleNames);
	}

	@Override
	public int getIndex() {
		GeoElement element = delegate.getElement();
		if (!element.isLabelVisible()) {
			return 0;
		}
		int index = labelModes.indexOf(element.getLabelMode());
		return index >= 0 ? index : 1;
	}

	@Override
	protected void setValueSafe(String value, int index) {
		GeoElement element = delegate.getElement();
		element.setLabelMode(labelModes.get(index));
		element.setLabelVisible(index != LABEL_HIDDEN);
		element.updateRepaint();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.CaptionStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the label of elements to be hidden, display the name,
 * value, caption, both name and value, or both caption and value.
 * Counterpart of {@link CaptionStyleProperty} with changes to meet new settings view requirements
 * without altering the original.
 */
public class LabelProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private static final List<Map.Entry<Integer, String>> labels = List.of(
			entry(GeoElementND.LABEL_HIDDEN, "Hidden"),
			entry(GeoElementND.LABEL_NAME, "Name"),
			entry(GeoElementND.LABEL_NAME_VALUE, "NameAndValue"),
			entry(GeoElementND.LABEL_VALUE, "Value"),
			entry(GeoElementND.LABEL_CAPTION, "Caption"),
			entry(GeoElementND.LABEL_CAPTION_VALUE, "CaptionAndValue")
	);
	private final AbstractGeoElementDelegate delegate;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public LabelProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Label");
		delegate = new CaptionStyleDelegate(geoElement);
		setNamedValues(labels);
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (!element.isLabelVisible()) {
			return GeoElementND.LABEL_HIDDEN;
		}
		int labelMode = element.getLabelMode();
		if (labels.stream().anyMatch(entry -> entry.getKey() == labelMode)) {
			return labelMode;
		} else {
			return GeoElementND.LABEL_NAME;
		}
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (value != GeoElementND.LABEL_HIDDEN) {
			element.setLabelMode(value);
		}
		element.setLabelVisible(value != GeoElementND.LABEL_HIDDEN);
		element.updateRepaint();
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}

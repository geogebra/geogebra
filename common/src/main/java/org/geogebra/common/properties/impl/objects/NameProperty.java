package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.properties.StringProperty;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NamePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Name
 */
public class NameProperty extends AbstractProperty implements StringProperty {

	private final GeoElementDelegate delegate;

	/***/
	public NameProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Name");
		delegate = new NamePropertyDelegate(element);
	}

	@Override
	public String getValue() {
		return delegate.getElement().getLabelSimple();
	}

	@Override
	public void setValue(String value) {
		GeoElement element = delegate.getElement();
		App app = element.getApp();
		try {
			element.rename(value);
			element.setAlgebraLabelVisible(true);
			element.getKernel().notifyUpdate(element);
			element.updateRepaint();
		} catch (MyError e) {
			app.showError(e.getLocalizedMessage());
		}
	}

	@Override
	public boolean isValid(String value) {
		GeoElement element = delegate.getElement();
		return !value.isEmpty() && LabelManager.isValidLabel(value, element.getKernel(), element);
	}
}

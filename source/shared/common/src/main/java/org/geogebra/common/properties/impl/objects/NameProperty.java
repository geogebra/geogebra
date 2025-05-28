package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NamePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Name
 */
public class NameProperty extends AbstractValuedProperty<String> implements StringProperty {

	private final GeoElementDelegate delegate;

	/***/
	public NameProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Name");
		delegate = new NamePropertyDelegate(element);
	}

	@Override
	public String getValue() {
		GeoElement element = delegate.getElement();
		if (!element.isAlgebraLabelVisible()) {
			return "";
		}
		return element.getLabelSimple();
	}

	@Override
	public void doSetValue(String value) {
		GeoElement element = delegate.getElement();
		String oldLabel = element.getLabelSimple();
		if (value.equals(oldLabel)) {
			return;
		}
		String newLabel = element.getFreeLabel(value);
		try {
			element.rename(newLabel);
			element.setAlgebraLabelVisible(true);
			element.getKernel().notifyUpdate(element);
			element.updateRepaint();
		} catch (MyError e) {
			App app = element.getApp();
			app.showError(e.getLocalizedMessage());
		}
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		GeoElement element = delegate.getElement();
		if (value.isEmpty() || !LabelManager.isValidLabel(value, element.getKernel(), element)) {
			return getLocalization().getError("InvalidInput");
		}
		return null;
	}
}

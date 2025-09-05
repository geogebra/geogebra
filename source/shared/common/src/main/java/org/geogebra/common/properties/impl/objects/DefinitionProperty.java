package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class DefinitionProperty extends AbstractValuedProperty<String> implements StringProperty {
	private final GeoElement element;
	private ErrorHandler handler = ErrorHelper.silent();

	/**
	 * @param localization this is used to localize the name
	 * @param element the construction element
	 */
	public DefinitionProperty(Localization localization, GeoElement element) {
		super(localization, "Definition");
		this.element = element;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		RedefineInputHandler redefineInputHandler =
				new RedefineInputHandler(element.getKernel().getApplication(), element,
						element.getRedefineString(false, true));
		redefineInputHandler.processInput(value, handler,
				ok -> {
					if (ok && element != redefineInputHandler.getGeoElement()) {
						element.getApp().getSelectionManager()
								.clearSelectedGeos(false, false);
						element.getApp().getSelectionManager()
								.addSelectedGeo(redefineInputHandler.getGeoElement());
					}
				});
	}

	@Override
	public String getValue() {
		return element.getRedefineString(false, true);
	}

	public void setErrorHandler(ErrorHandler handler) {
		this.handler = handler;
	}
}

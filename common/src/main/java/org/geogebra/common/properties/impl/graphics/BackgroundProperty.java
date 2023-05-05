package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class BackgroundProperty extends AbstractNamedEnumeratedProperty<Renderer.BackgroundStyle> {

	private App app;

	/**
	 * Constructs an BackgroundProperty
	 * @param app app
	 * @param localization the localization used
	 */
	public BackgroundProperty(App app, Localization localization) {
		super(localization, "ar.background");
		this.app = app;
		setValues(Renderer.BackgroundStyle.values());
		setValueNames("Camera", "ar.filter", "ar.opaqueColor");
	}

	@Override
	public Renderer.BackgroundStyle getValue() {
		return app.getEuclidianView3D().getRenderer().getBackgroundStyle();
	}

	@Override
	protected void doSetValue(Renderer.BackgroundStyle value) {
		EuclidianView3D euclidianView3D = (EuclidianView3D) app.getActiveEuclidianView();
		if (euclidianView3D.isXREnabled()) {
			euclidianView3D.getRenderer().setBackgroundStyle(value);
		}
	}
}

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class BackgroundProperty extends AbstractEnumerableProperty {

	private App app;

	private Renderer.BackgroundStyle[] backgroundStyles = new Renderer.BackgroundStyle[] {
			Renderer.BackgroundStyle.None,
			Renderer.BackgroundStyle.Transparent,
			Renderer.BackgroundStyle.Opaque};

	/**
	 * Constructs an AbstractEnumerableProperty
	 *
	 * @param app
	 *            app
	 * @param localization
	 *            the localization used
	 */
	public BackgroundProperty(App app, Localization localization) {
		super(localization, "Background");
		this.app = app;

		setValuesAndLocalize(new String[] { "Camera", "Filter", "Opaque Color" });
	}

	@Override
	protected void setValueSafe(String value, int index) {
		// after settings
		EuclidianView3D euclidianView3D = (EuclidianView3D) app
				.getActiveEuclidianView();
		if (euclidianView3D.isAREnabled()) {
			switch (index) {
			case 0:
				euclidianView3D.getRenderer().setBackgroundStyle(
						Renderer.BackgroundStyle.None);
				break;
			case 1:
				euclidianView3D.getRenderer().setBackgroundStyle(
						Renderer.BackgroundStyle.Transparent);
				break;
			case 2:
				euclidianView3D.getRenderer().setBackgroundStyle(
						Renderer.BackgroundStyle.Opaque);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public int getIndex() {
		Renderer.BackgroundStyle backgroundState = app.getEuclidianView3D().getRenderer()
				.getBackgroundStyle();
		for (int i = 0; i < backgroundStyles.length; i++) {
			if (backgroundState == backgroundStyles[i]) {
				return i;
			}
		}
		return -1;
	}
}

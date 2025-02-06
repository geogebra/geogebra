package org.geogebra.common.keyboard;

import org.geogebra.common.main.App;
import org.geogebra.keyboard.base.impl.TemplateKeyProvider;

public class LocalizedTemplateKeyProvider implements TemplateKeyProvider {

	private final App app;

	public LocalizedTemplateKeyProvider(App app) {
		this.app = app;
	}

	@Override
	public String getPointFunction() {
		int dimension = app.getActiveEuclidianView().getDimension();
		return app.getSettings().getGeneral().getPointEditorTemplate() + ":" + dimension;
	}

	@Override
	public String getVectorFunction() {
		int dimension = app.getActiveEuclidianView().getDimension();
		return "$vector:" + dimension;
	}
}

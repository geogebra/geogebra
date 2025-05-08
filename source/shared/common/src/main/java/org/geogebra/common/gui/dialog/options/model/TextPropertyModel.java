package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.StringUtil;

public abstract class TextPropertyModel extends OptionsModel {

	protected ITextFieldListener listener;

	public TextPropertyModel(App app) {
		super(app);
	}

	public void setListener(ITextFieldListener listener) {
		this.listener = listener;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	public void applyChanges(String text) {
		if (!StringUtil.empty(text)) {
			applyChanges(app.getKernel().getAlgebraProcessor()

					.evaluateToNumeric(text, ErrorHelper.silent()), text);
		} else if(!getText().isEmpty()) {
			applyChanges((GeoNumberValue) null, text);
		}
	}

	@Override
	public void updateProperties() {
		listener.setText(getText());
	}

	protected abstract void applyChanges(GeoNumberValue val, String str);

	protected abstract String getText();

	@MissingDoc
	public abstract String getTitle();
}

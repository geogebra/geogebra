/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
			applyChanges(null, text);
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

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

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
import org.geogebra.common.main.App;

public class ScriptEditorModel extends OptionsModel {

	private ScriptEditPanel listener;

	/**
	 * @param app
	 *            application
	 */
	public ScriptEditorModel(App app) {
		super(app);
	}

	@Override
	protected boolean isValidAt(int index) {
		return true;
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
	}

	/**
	 * @param listener
	 *            listener for applyModifications
	 */
	public void setListener(ScriptEditPanel listener) {
		this.listener = listener;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}

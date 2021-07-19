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

package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
import org.geogebra.common.main.App;

public class ScriptEditorModel extends OptionsModel

{
	public ScriptEditorModel(App app) {
		super(app);
	}

	private ScriptEditPanel listener;
	@Override
	protected boolean isValidAt(int index) {
		return true;
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub

	}

	public void setListener(ScriptEditPanel listener) {
		this.listener = listener;
	}
	@Override
	public PropertyListener getListener() {
		return listener;
	}

	public void applyModifications() {
		if (listener != null) {
			listener.applyModifications();
		}

	}
}

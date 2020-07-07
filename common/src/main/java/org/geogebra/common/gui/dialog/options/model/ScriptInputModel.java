package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.AsyncOperation;

public class ScriptInputModel extends OptionsModel {

	private final Kernel kernel;
	private GeoElement geo;
	private boolean global = false;

	private boolean updateScript;
	private IScriptInputListener listener;
	/**
	 * used for update to avoid several updates
	 */
	private boolean handlingDocumentEventOff = false;

	/**
	 * false on init, become true when an edit occurs
	 */
	private boolean editOccurred = false;

	public interface IScriptInputListener extends PropertyListener {
		void setInput(String text, ScriptType type);
	}

	public ScriptInputModel(App app, IScriptInputListener listener,
			boolean updateScript) {
		super(app);
		this.listener = listener;
		this.updateScript = updateScript;
		this.kernel = app.getKernel();
	}

	public void setGeo(GeoElement geo) {

		handlingDocumentEventOff = true;

		if (global) {
			setGlobal();
			handlingDocumentEventOff = false;
			return;
		}
		this.geo = geo;

		if (geo != null) {
			Script script = geo.getScript(
					updateScript ? EventType.UPDATE : EventType.CLICK);

			if (script == null) {
				listener.setInput("", ScriptType.GGBSCRIPT);
			} else {
				listener.setInput(script.getText(), script.getType());
			}
		}

		handlingDocumentEventOff = false;
	}

	/**
	 * edit global javascript
	 */
	public void setGlobal() {
		boolean currentHandlingDocumentEventOff = handlingDocumentEventOff;
		handlingDocumentEventOff = true;

		geo = null;
		global = true;

		listener.setInput(app.getKernel().getLibraryJavaScript(), ScriptType.JAVASCRIPT);

		handlingDocumentEventOff = currentHandlingDocumentEventOff;
	}

	public void processInput(String inputText, ScriptType scriptType,
			AsyncOperation<Boolean> callback) {
		if (inputText == null) {
			callback.callback(false);
			return;
		}

		if (global) {
			app.getKernel().setLibraryJavaScript(inputText);
			callback.callback(true);
			return;
		}

		if (getGeo() == null) {
			setGeo(GeoButton.getNewButton(kernel.getConstruction()));
		}

		// change existing script
		Script script = app.createScript(scriptType, inputText, true);
		if (updateScript) {
			getGeo().setUpdateScript(script);
			// let's suppose fixing this script removed the reason why
			// scripts were blocked
			app.setBlockUpdateScripts(false);
		} else {
			getGeo().setClickScript(script);
		}
		storeUndoInfo();
		callback.callback(true);
	}

	/**
	 * @return the geo
	 */
	public GeoElement getGeo() {
		return geo;
	}

	public void handleDocumentEvent() {
		if (handlingDocumentEventOff) {
			return;
		}

		setEditOccurred(true);
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkGeos() {
		return getGeosLength() == 1;
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub

	}

	public boolean isEditOccurred() {
		return editOccurred;
	}

	public void setEditOccurred(boolean editOccurred) {
		this.editOccurred = editOccurred;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}

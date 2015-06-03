package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;

public class ScriptInputModel extends OptionsModel {
	public interface IScriptInputListener extends PropertyListener {
		void setInputText(String text);

		String getInputText();

		void setLanguageIndex(int index, String name);

	}
	private GeoElement geo;
	private boolean global = false;
	// private boolean javaScript = false;
	private ScriptType scriptType = ScriptType.GGBSCRIPT;
	private boolean updateScript = false;
	private App app;
	private TextInputHandler inputHandler;
	private IScriptInputListener listener;
	/**
	 * used for update to avoid several updates
	 */
	private boolean handlingDocumentEventOff = false;

	/**
	 * false on init, become true when an edit occurs
	 */
	private boolean editOccurred = false;


	public ScriptInputModel(App app, IScriptInputListener listener, boolean updateScript, boolean forceJavaScript) {
		this.app = app;
		this.listener = listener;
		this.updateScript = updateScript;
		inputHandler = new TextInputHandler();
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
			// Default to an empty Ggb script
			if (script == null) {
				script = app.createScript(ScriptType.GGBSCRIPT, "", false);
			}
			// App.debug(script.getText());
			listener.setInputText(script.getText());
			setScriptType(script.getType());
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

		listener.setInputText(app.getKernel().getLibraryJavaScript());

		handlingDocumentEventOff = currentHandlingDocumentEventOff;
	}

	public boolean processInput(String inputText) {
		return inputHandler.processInput(inputText);
	}



	// private void setJSMode(boolean flag){
	// javaScript = flag;
	// ((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit(flag ?
	// "javascript":"geogebra");
	// }

	public void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
		String scriptStr;
		int index = scriptType.ordinal();
		switch (scriptType) {
		default:
		case GGBSCRIPT:
			scriptStr = "geogebra";
			break;

		case JAVASCRIPT:
			scriptStr = "javascript";
			break;

		}
		listener.setLanguageIndex(index, scriptStr);
	}

	/**
	 * @return the geo
	 */
	public GeoElement getGeo() {
		return geo;
	}

	private class TextInputHandler implements InputHandler {

		private Kernel kernel;

		private TextInputHandler() {
			kernel = app.getKernel();
		}

		public boolean processInput(String inputValue) {
			if (inputValue == null)
				return false;

			if (global) {
				app.getKernel().setLibraryJavaScript(inputValue);
				return true;
			}

			if (getGeo() == null) {
				setGeo(GeoButton.getNewButton(kernel.getConstruction()));

			}

			// change existing script
			Script script = app.createScript(scriptType, inputValue, true);
			if (updateScript) {
				getGeo().setUpdateScript(script);
				// let's suppose fixing this script removed the reason why
				// scripts were blocked
				app.setBlockUpdateScripts(false);
			} else {
				getGeo().setClickScript(script);
			}
			return true;
		}
	}


	public void handleDocumentEvent() {

		if (handlingDocumentEventOff)
			return;

		setEditOccurred(true);

	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkGeos(){
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

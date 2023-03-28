package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;

public class ScriptInputModel extends OptionsModel {
	private boolean global = false;

	private EventType type;
	private String title;
	private IScriptInputListener listener;
	/**
	 * used for update to avoid several updates
	 */
	private boolean handlingDocumentEventOff = false;

	/**
	 * false on init, become true when an edit occurs
	 */
	private boolean editOccurred = false;

	/**
	 * @param app application
	 * @return array of all possible models
	 */
	public static ScriptInputModel[] getModels(App app) {
		return new ScriptInputModel[] {
				new ScriptInputModel(app, EventType.CLICK, "OnClick"),
				new ScriptInputModel(app, EventType.UPDATE, "OnUpdate"),
				new ScriptInputModel(app, EventType.DRAG_END, "OnDragEnd"),
				new ScriptInputModel(app, EventType.EDITOR_KEY_TYPED, "OnChange"),
				new ScriptInputModel(app,  EventType.LOAD_PAGE, "GlobalJavaScript")
		};
	}

	public boolean isForcedJs() {
		return type == EventType.LOAD_PAGE;
	}


	public interface IScriptInputListener extends PropertyListener {
		void setInput(String text, ScriptType type);
	}

	public ScriptInputModel(App app,
			EventType type, String title) {
		super(app);
		this.type = type;
		this.title = title;
	}

	public void setListener(IScriptInputListener listener) {
		this.listener = listener;
	}

	public void updatePanel() {

		handlingDocumentEventOff = true;

		if (global) {
			listener.setInput(app.getKernel().getLibraryJavaScript(), ScriptType.JAVASCRIPT);
			handlingDocumentEventOff = false;
			return;
		}

		if (getGeo() != null) {
			Script script = getGeo().getScript(type);

			if (script == null) {
				listener.setInput("", ScriptType.GGBSCRIPT);
			} else {
				listener.setInput(script.getText(), script.getType());
			}
		}

		handlingDocumentEventOff = false;
	}

	public void processInput(String inputText, ScriptType scriptType) {
		if (inputText == null) {
			return;
		}

		if (global) {
			app.getKernel().setLibraryJavaScript(inputText);
			return;
		}

		// change existing script
		Script script = app.createScript(scriptType, inputText, true);
		getGeo().setScript(script, type);
		if (type != EventType.CLICK) {
			// let's suppose fixing this script removed the reason why
			// scripts were blocked
			app.setBlockUpdateScripts(false);
		}
		storeUndoInfo();
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @return the geo
	 */
	public GeoElement getGeo() {
		return getGeoAt(0);
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
		boolean isSingleGeo = getGeosLength() == 1;
		switch(type){
		case LOAD_PAGE:
			return app.getScriptManager().isJsEnabled();
		case CLICK:
			return isSingleGeo && getGeo().canHaveClickScript();
		case UPDATE:
			return isSingleGeo && getGeo().canHaveUpdateScript();
		case EDITOR_KEY_TYPED:
			return isSingleGeo && getGeo().isGeoInputBox();
		case DRAG_END:
			return isSingleGeo && getGeo().isMoveable();
		default:
			return false;
		}
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

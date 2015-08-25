package org.geogebra.common.main.settings;

import java.util.LinkedList;

/**
 * Abstract base class for all setting objects. Provides functionality for
 * setting listeners and batching.
 * 
 * @author Florian Sonner
 * @see "http://www.geogebra.org/trac/wiki/GuiRefactoring"
 */
public abstract class AbstractSettings {
	/**
	 * Running in batch mode: Only at the end of the batch mode listeners are
	 * notified if settings changed.
	 */
	private int runningBatches;

	/**
	 * Remember is settings changed. Used in batch mode.
	 */
	private boolean settingsChanged;

	/**
	 * List with listeners.
	 */
	private LinkedList<SettingListener> listeners;

	public AbstractSettings() {
		listeners = new LinkedList<SettingListener>();
	}

	public AbstractSettings(LinkedList<SettingListener> listeners) {
		this.listeners = listeners;
		settingChanged();
	}

	public void setListeners(LinkedList<SettingListener> listeners) {
		this.listeners = listeners;
	}

	/**
	 * Notify listeners about changed settings. This method has to be called by
	 * implementors of subclasses if a setting's value has been changed.
	 */
	protected final void settingChanged() {
		// batch mode: just set flag to inform listeners at the end
		if (runningBatches > 0) {
			settingsChanged = true;
		}

		// otherwise: inform listeners immediately
		else {
			for (SettingListener listener : listeners) {
				listener.settingsChanged(this);
			}
		}
	}

	/**
	 * Begin batch mode.
	 */
	public final void beginBatch() {
		runningBatches++;
		// settingsChanged = false;
	}

	/**
	 * End batch mode.
	 */
	public final void endBatch() {
		if (runningBatches <= 0) {
			return;
		}
		// notify listeners
		if (runningBatches == 1) {
			if (settingsChanged) {
				for (SettingListener listener : listeners) {
					listener.settingsChanged(this);
				}
			}
		}
		runningBatches--;
	}

	/**
	 * Add a new setting listener to be informed about setting changes.
	 * 
	 * @param listener
	 */
	public final void addListener(SettingListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a setting listener.
	 * 
	 * @param listener
	 */
	public final void removeListener(SettingListener listener) {
		listeners.remove(listener);
	}

	public LinkedList<SettingListener> getListeners() {
		return listeners;
	}
}

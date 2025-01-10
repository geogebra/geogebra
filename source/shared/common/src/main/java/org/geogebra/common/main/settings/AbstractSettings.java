package org.geogebra.common.main.settings;

import java.util.LinkedList;

/**
 * Abstract base class for all setting objects. Provides functionality for
 * setting listeners and batching.
 * 
 * @author Florian Sonner
 * @see "http://dev.geogebra.org/trac/wiki/GuiRefactoring"
 */
public abstract class AbstractSettings implements Resettable {
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

	/**
	 * New abstract settings.
	 */
	public AbstractSettings() {
		listeners = new LinkedList<>();
	}

	/**
	 * @param listeners
	 *            setting listeners
	 */
	public AbstractSettings(LinkedList<SettingListener> listeners) {
		this.listeners = listeners;
		notifyListeners();
	}

	/**
	 * @param listeners
	 *            setting listeners
	 */
	public void setListeners(LinkedList<SettingListener> listeners) {
		this.listeners = listeners;
	}

	/**
	 * Notify listeners about changed settings. This method has to be called by
	 * implementors of subclasses if a setting's value has been changed.
	 */
	protected void settingChanged() {
		// batch mode: just set flag to inform listeners at the end
		if (runningBatches > 0) {
			settingsChanged = true;
		}

		// otherwise: inform listeners immediately
		else {
			notifyListeners();
		}
	}

	void notifyListeners() {
		LinkedList<SettingListener> clone = new LinkedList<>(listeners);
		for (SettingListener listener : clone) {
			listener.settingsChanged(this);
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
				notifyListeners();
			}
		}
		runningBatches--;
	}

	/**
	 * Add a new setting listener to be informed about setting changes.
	 * 
	 * @param listener
	 *            settings listener
	 */
	public final void addListener(SettingListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a setting listener.
	 * 
	 * @param listener
	 *            settings listener
	 */
	public final void removeListener(SettingListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return all listeners
	 */
	public LinkedList<SettingListener> getListeners() {
		return listeners;
	}

	@Override
	public void resetDefaults() {
		runningBatches = 0;
		settingsChanged = false;
	}
}

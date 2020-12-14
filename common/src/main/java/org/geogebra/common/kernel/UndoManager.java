package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.undoredo.UndoInfoStoredListener;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * Undo manager common to Desktop and Web
 */
public abstract class UndoManager {

	/**
	 * maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	 */
	private static final int MAX_CAPACITY = 100;

	/** application */
	@Weak
	public App app;
	/** construction */
	@Weak
	protected Construction construction;
	/** list of undo steps */
	protected LinkedList<UndoCommand> undoInfoList;
	/** invariant: iterator.previous() is current state */
	private ListIterator<UndoCommand> iterator;
	private boolean storeUndoInfoNeededForProperties = false;
	private List<UndoInfoStoredListener> undoInfoStoredListeners;

	/**
	 * @param cons
	 *            construction
	 */
	public UndoManager(Construction cons) {
		construction = cons;
		app = cons.getApplication();
		undoInfoList = new LinkedList<>();
		iterator = undoInfoList.listIterator();
		undoInfoStoredListeners = new ArrayList<>();
	}

	/**
	 * @param slideID
	 *            slide ID
	 * @return last state of given slide
	 */
	public AppState getCheckpoint(String slideID) {
		AppState state = null;
		int steps = 0;
		while (iterator.hasPrevious()) {
			UndoCommand cmd = iterator.previous();
			steps++;
			if (cmd.getAppState() != null && (cmd.getSlideID() == null
					|| cmd.getSlideID().equals(slideID))) {
				state = cmd.getAppState();
				break;
			}
			if ((cmd.getAction() == EventType.PASTE_SLIDE)
					&& cmd.getArgs().length > 1
					&& cmd.getArgs()[1].equals(slideID)) {
				state = extractStateFromFile(cmd.getArgs()[2]);
				break;
			}
		}
		for (int i = 0; i < steps; i++) {
			iterator.next();
		}
		return state;
	}

	protected AppState extractStateFromFile(String arg) {
		return null;
	}

	/**
	 * @param slideID
	 *            slide ID
	 * @return command that created this slide (DUPLICATE or ADD)
	 */
	public UndoCommand getCreationCommand(String slideID) {
		UndoCommand state = null;
		int steps = 0;
		while (iterator.hasPrevious()) {
			UndoCommand cmd = iterator.previous();
			steps++;
			if ((cmd.getAction() == EventType.ADD_SLIDE
					|| cmd.getAction() == EventType.PASTE_SLIDE)
					&& cmd.getArgs().length > 1
					&& cmd.getArgs()[1].equals(slideID)) {

				state = cmd;
				break;
			}
		}

		if (state != null) {
			state.loadStateAfter(this);
		}

		for (int i = 0; i < steps; i++) {
			iterator.next();
		}

		return state;
	}

	/**
	 * @param action
	 *            action type
	 * @param state
	 *            state to restore if applicable
	 * @param args
	 *            action arguments
	 */
	public void executeAction(EventType action, AppState state,
			String... args) {
		app.executeAction(action, state, args);
	}

	/**
	 * Processes XML
	 * 
	 * @param strXML
	 *            XML string
	 * @param isGGTOrDefaults
	 *            whether to treat the XML as defaults
	 * @throws Exception
	 *             on trouble with parsing or running commands
	 */
	final public synchronized void processXML(String strXML,
			boolean isGGTOrDefaults) throws Exception {
		processXML(strXML, isGGTOrDefaults, null);
	}

	/**
	 * Processes XML
	 * 
	 * @param strXML
	 *            XML string
	 * @param isGGTOrDefaults
	 *            whether to treat the XML as defaults
	 * @param info
	 *            EvalInfo (can be null)
	 * @throws Exception
	 *             on trouble with parsing or running commands
	 */
	final public synchronized void processXML(String strXML,
			boolean isGGTOrDefaults, EvalInfo info) throws Exception {

		boolean randomize = info != null && info.updateRandom();

		construction.setFileLoading(true);
		construction.setCasCellUpdate(true);
		construction.getXMLio().processXMLString(strXML, true, isGGTOrDefaults,
				true, randomize);
		construction.setFileLoading(false);
		construction.setCasCellUpdate(false);
	}

	/**
	 * Loads previous construction state from undo info list.
	 */
	public synchronized void undo() {
		if (undoPossible()) {
			UndoCommand last = iterator.previous();
			last.undo(this, iterator);
			updateUndoActions();
		}
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {
		if (redoPossible()) {
			iterator.next().redo(this);
			updateUndoActions();
		}
	}

	/**
	 * Update undo/redo buttons in GUI
	 */
	protected void updateUndoActions() {
		app.updateActions();
		// debugStates();

		for (UndoInfoStoredListener listener: undoInfoStoredListeners) {
			listener.onUndoInfoStored();
		}
	}

	/**
	 * Get current undo info for later comparisons
	 * 
	 * @return Object (the file of last undo)
	 */
	final public synchronized AppState getCurrentUndoInfo() {
		AppState ret = iterator.previous().getAppState();
		iterator.next();
		return ret;
	}

	/**
	 * Store undo info
	 */
	public void storeUndoInfo() {
		storeUndoInfo(false);
	}

	/**
	 * Reloads construction state at current position of undo list (this is
	 * needed for "cancel" actions).
	 */
	final public synchronized void restoreCurrentUndoInfo() {
		app.getSelectionManager().storeSelectedGeosNames();
		if (iterator != null) {
			loadUndoInfo(iterator.previous().getAppState(), null);
			iterator.next();
			updateUndoActions();
		}
		app.getSelectionManager().recallSelectedGeosNames(app.getKernel());
	}

	/**
	 * Clears undo info list and adds current state to the undo info list.
	 */
	public synchronized void initUndoInfo() {
		storeUndoInfoNeededForProperties = false;
		clearUndoInfo();
		storeUndoInfo();
	}

	/**
	 * Returns whether undo operation is possible or not.
	 * 
	 * @return whether undo operation is possible or not.
	 */
	public boolean undoPossible() {
		if (!app.isUndoActive()) {
			return false;
		}
		return iterator.nextIndex() > 1;
	}

	/**
	 * Returns whether redo operation is possible or not.
	 * 
	 * @return whether redo operation is possible or not.
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive()) {
			return false;
		}
		return iterator.hasNext();
	}

	/**
	 * @param currentUndoXML
	 *            construction XML
	 * @param refresh
	 *            whether to reload afterwards
	 */
	public abstract void storeUndoInfo(StringBuilder currentUndoXML,
			boolean refresh);

	/**
	 * Stores undo info
	 *
	 * @param refresh
	 *            true to restore current
	 */
	final public void storeUndoInfo(final boolean refresh) {
		storeUndoInfo(construction.getCurrentUndoXML(true), refresh);
		storeUndoInfoNeededForProperties = false;
	}

	/**
	 * Loads undo info
	 * 
	 * @param state
	 *            stored state
	 * @param slideID
	 *            slide identifier
	 */
	protected abstract void loadUndoInfo(AppState state, String slideID);

	/**
	 * Clears all undo information
	 */
	public synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
	}

	/**
	 * Removes all stored states newer than current or too old
	 */
	public void pruneStateList() {
		// remove everything after the insert position until end of
		// list
		UndoCommand appState = null;
		while (iterator.hasNext()) {
			appState = iterator.next();
			iterator.remove();
			appState.delete();
		}

		// delete first if too many in list
		if (undoInfoList.size() > MAX_CAPACITY) {
			// use iterator to delete to avoid
			// ConcurrentModificationException
			// go to beginning of list
			while (iterator.hasPrevious()) {
				appState = iterator.previous();
			}

			iterator.remove();
			appState.delete();

			while (iterator.hasNext()) {
				iterator.next();
			}
		}
		EmbedManager manager = app.getEmbedManager();
		if (manager != null) {
			manager.executeAction(EventType.EMBEDDED_PRUNE_STATE_LIST);
		}
		// debugStates();
	}

	/**
	 * This stores the undo command, if the state changed.
	 *
	 * @param command the undo command to store
	 * @return true if the command was stored
	 */
	final protected boolean maybeStoreUndoCommand(UndoCommand command) {
		boolean equalsWithPrevious = false;
		if (iterator.hasPrevious()) {
			UndoCommand currentState = iterator.previous();
			iterator.next();
			equalsWithPrevious = currentState.equalsState(command);
		}
		if (!equalsWithPrevious) {
			iterator.add(command);
		}
		return equalsWithPrevious;
	}

	/**
	 * Notify about properties change
	 */
	public void setPropertiesOccured() {
		if (!storeUndoInfoNeededForProperties) {
			storeUndoInfoNeededForProperties = true;
			if (redoPossible()) {
				pruneStateList();
				updateUndoActions();
			}
		}
	}

	/**
	 * End batch of properties changes; reset properties change flag and store
	 * undo if necessary
	 * 
	 * @param isUndoActive
	 *            whether undo should be actually stored
	 */
	public void storeUndoInfoForProperties(boolean isUndoActive) {
		if (isUndoActive && storeUndoInfoNeededForProperties) {
			storeUndoInfo();
		}
		storeUndoInfoNeededForProperties = false;
	}

	/**
	 * @param action
	 *            action type
	 * @param args
	 *            action arguments
	 */
	public void storeAction(EventType action, String... args) {
		iterator.add(new UndoCommand(action, args));
		this.pruneStateList();
		updateUndoActions();
	}

	/**
	 * @return number of available undo points
	 */
	public int getHistorySize() {
		return this.iterator == null ? -1 : this.iterator.previousIndex();
	}

	/**
	 * @param state
	 *            checkpoint
	 * @return XML of the checkpoint
	 */
	public String getXML(AppState state) {
		return state.getXml();
	}

	/**
	 * Adds a listener which will be notified every time when undo info is stored.
	 * @param listener This will be notified when undo info is stored.
	 */
	public void addUndoInfoStoredListener(UndoInfoStoredListener listener) {
		undoInfoStoredListeners.add(listener);
	}

	/**
	 * @param action
	 *            action to be executed
	 * @param id
	 *            embed ID
	 */
	public void embeddedAction(EventType action, String id) {
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			try {
				int embedId = Integer.parseInt(id);
				embedManager.executeAction(action, embedId);
			} catch (RuntimeException e) {
				Log.warn("No undo possible for embed " + id);
			}
		}
	}
}

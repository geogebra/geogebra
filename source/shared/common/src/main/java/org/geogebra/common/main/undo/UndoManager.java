package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.spreadsheet.core.UndoProvider;

import com.google.j2objc.annotations.Weak;

/**
 * Undo manager common to Desktop and Web
 */
public abstract class UndoManager implements UndoProvider {

	/**
	 * maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	 */
	private static final int MAX_CAPACITY = 100;

	/** application */
	@Weak
	protected App app;
	/** construction */
	@Weak
	protected Construction construction;
	/** list of undo steps */
	protected LinkedList<UndoCommand> undoInfoList;
	/** invariant: iterator.previous() is current state */
	private ListIterator<UndoCommand> iterator;
	private boolean storeUndoInfoNeededForProperties = false;
	private List<UndoInfoStoredListener> undoInfoStoredListeners;
	private ArrayList<UndoPossibleListener> mListener = new ArrayList<>();
	private final List<ActionExecutor> executors = new ArrayList<>();
	private boolean allowCheckpoints = true;

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
		executors.add(new ConstructionActionExecutor(app));
	}

	/**
	 * @param slideID
	 *            slide ID
	 * @return last state of given slide
	 */
	public UndoCommand getCheckpoint(String slideID) {
		UndoCommand state = null;
		ListIterator<UndoCommand> undoIterator = undoInfoList.listIterator(iterator.nextIndex());

		while (undoIterator.hasPrevious()) {
			UndoCommand cmd = undoIterator.previous();
			if (cmd.getAppState() != null && (cmd.getSlideID() == null
					|| cmd.getSlideID().equals(slideID))) {
				state = cmd;
				break;
			}
			if ((cmd.getAction() == ActionType.PASTE_PAGE)
					&& cmd.getArgs().length > 1
					&& cmd.getArgs()[1].equals(slideID)) {
				state = cmd;
				break;
			}
		}
		return state;
	}

	protected AppState extractStateFromFile(String arg) {
		return null;
	}

	/**
	 * @param slideID
	 *            slide ID
	 */
	public void redoCreationCommand(String slideID) {
		UndoCommand state = null;
		int steps = 0;
		while (iterator.hasPrevious()) {
			UndoCommand cmd = iterator.previous();
			steps++;
			if ((cmd.getAction() == ActionType.ADD_PAGE
					|| cmd.getAction() == ActionType.PASTE_PAGE)
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
	}

	/**
	 * @param action
	 *            action type
	 * @param args event arguments
	 */
	public void executeAction(ActionType action, String... args) {
		for (ActionExecutor executor: executors) {
			if (executor.executeAction(action, args)) {
				return;
			}
		}
	}

	/**
	 * Loads previous construction state from undo info list.
	 */
	public synchronized void undo() {
		if (undoPossible()) {
			UndoCommand last = iterator.previous();
			do {
				last.undo(this);
				last = iterator.hasPrevious() ? iterator.previous() : null;
			} while (last != null && last.shouldStitchToNext());
			// by checking for stitched commands we've gone one undo point too far, revert
			if (last != null && iterator.hasNext()) {
				iterator.next();
			}
			updateUndoActions();
		}
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {
		if (redoPossible()) {
			UndoCommand next;
			do {
				next = iterator.next();
				next.redo(this);
			} while (iterator.hasNext() && next.shouldStitchToNext());

			updateUndoActions();
		}
	}

	/**
	 * Update undo/redo buttons in GUI
	 */
	protected void onStoreUndo() {
		updateUndoActions();
		// debugStates();
		notifyStoreUndoListeners();
	}

	protected void updateUndoActions() {
		app.updateActions();
		informListener();
	}

	protected void notifyStoreUndoListeners() {
		// first item in undo history is just "blank", do not notify
		if (undoInfoList.size() > 1) {
			for (UndoInfoStoredListener listener : undoInfoStoredListeners) {
				listener.onUndoInfoStored();
			}
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
		if (!allowCheckpoints && iterator.hasPrevious()) {
			return undoInfoList.get(iterator.previousIndex()).getAction() != null;
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
		if (!allowCheckpoints && iterator.hasNext()) {
			return undoInfoList.get(iterator.nextIndex()).getAction() != null;
		}
		return iterator.hasNext();
	}

	/**
	 * @param currentUndoXML
	 *            construction XML
	 */
	public abstract void storeUndoInfo(StringBuilder currentUndoXML);

	/**
	 * Stores undo info
	 */
	@Override
	final public void storeUndoInfo() {
		storeUndoInfo(construction.getCurrentUndoXML(true));
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

	protected void loadUndoInfo(UndoCommand cmd, @Nullable String slideId,
			@Nonnull UndoCommand until) {
		loadUndoInfo(extractFromCommand(cmd), slideId);
		replayActions(cmd, slideId, until);
	}

	/**
	 * @param cmd undo command
	 * @return app state associated with the command
	 */
	public AppState extractFromCommand(UndoCommand cmd) {
		if (cmd == null) {
			return null;
		} else if (cmd.getAction() == ActionType.PASTE_PAGE) {
			return extractStateFromFile(cmd.getArgs()[2]);
		} else {
			return cmd.getAppState();
		}
	}

	public void replayActions(String slideID, @Nonnull UndoCommand until) {
		replayActions(getCheckpoint(slideID), slideID, until);
	}

	private void replayActions(@Nullable UndoCommand checkpoint, @Nullable String slideID,
			@Nonnull UndoCommand until) {
		boolean checkpointReached = checkpoint == null;

		for (UndoCommand undoCommand : undoInfoList) {
			if (undoCommand == until) {
				return;
			}

			if (checkpointReached && undoCommand.getAction() != null
					&& Objects.equals(slideID, undoCommand.getSlideID())) {
				executeAction(undoCommand.getAction(), undoCommand.getArgs());
			}
			checkpointReached |= checkpoint == undoCommand;
		}
	}

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
			manager.executeAction(ActionType.EMBEDDED_PRUNE_STATE_LIST);
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
	public void setPropertiesOccurred() {
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
	public void storeAction(ActionType action, String[] args, ActionType undoAction,
			String... undoArgs) {
		storeActionWithSlideId(null, action, args, undoAction, undoArgs);
	}

	/**
	 * @param action action type
	 * @param slideID slide ID
	 * @param args action arguments
	 */
	public void storeActionWithSlideId(String slideID, ActionType action,  String[] args,
			ActionType undoAction, String[] undoArgs) {
		storeAndNotify(new UndoCommand(slideID, action, args, undoAction, undoArgs));
	}

	protected void storeAndNotify(UndoCommand command) {
		iterator.add(command);
		this.pruneStateList();
		onStoreUndo();
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
	 * Like {@link #storeAddGeo(List)}, but for single geo
	 * @param arg GeoElement just added
	 */
	public void storeAddGeo(GeoElement arg) {
		storeAddGeo(Collections.singletonList(arg));
	}

	/**
	 * Helper method to store undo info about a just created geos.
	 * Please make sure to call it after the styles of the geo
	 * are correctly initialized.
	 * @param arg GeoElement just added
	 */
	public void storeAddGeo(List<GeoElement> arg) {
		Stream<String> stream = arg.stream().map(this::getXMLOf);
		String[] labels = arg.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		buildAction(ActionType.ADD, stream.toArray(String[]::new))
				.withUndo(ActionType.REMOVE, labels)
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	private String getXMLOf(GeoElement arg) {
		String xml;
		if (arg.getParentAlgorithm() != null) {
			xml = arg.getParentAlgorithm().getXML();
		} else {
			xml = arg.getXML();
		}
		return xml;
	}

	/**
	 * Store action and notify listeners
	 * @param type action type
	 * @param args arguments
	 */
	public void storeUndoableAction(ActionType action, String[] args, ActionType type,
			String... undoArgs) {
		buildAction(action, args).withUndo(type, undoArgs).storeAndNotifyUnsaved();
	}

	public UndoCommandBuilder buildAction(ActionType action, String... args) {
		return new UndoCommandBuilder(this, app.getSlideID(), action, args);
	}

	protected void notifyUnsaved() {
		app.setUnsaved();
		app.getEventDispatcher().dispatchEvent(new Event(EventType.STOREUNDO));
	}

	public void addActionExecutor(ActionExecutor executor) {
		executors.add(executor);
	}

	/**
	 * Runs the callback synchronously if the target slide is already active
	 * (or slides are not supported).
	 * If the app needs to load the slide, the callback will run asynchronously.
	 * @param slideID slide ID
	 * @param callback callback to run when slide is loaded
	 */
	public void runAfterSlideLoaded(String slideID, Runnable callback) {
		callback.run();
	}

	/**
	 * Reset before reloading
	 */
	public void resetBeforeReload() {
		app.getSelectionManager().storeSelectedGeosNames();
		app.getCompanion().storeViewCreators();
		app.getKernel().notifyReset();
		app.getKernel().clearJustCreatedGeosInViews();
		app.getActiveEuclidianView().getEuclidianController().clearSelections();
		VideoManager videoManager = app.getVideoManager();
		if (videoManager != null) {
			videoManager.storeVideos();
		}
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			embedManager.storeEmbeds();
		}
		app.getActiveEuclidianView().resetInlineObjects();
	}

	/**
	 * Restore state after reload
	 */
	public void restoreAfterReload() {
		app.getKernel().notifyReset();
		app.getCompanion().recallViewCreators();
		app.getSelectionManager().recallSelectedGeosNames(app.getKernel());
		app.getActiveEuclidianView().restoreDynamicStylebar();
		app.resetPen();
	}

	/**
	 * Save the whole undo list to a map.
	 * @param undoHistory to save to.
	 */
	public void undoHistoryTo(Map<String, UndoHistory> undoHistory) {
		LinkedList<UndoCommand> undoCommands = new LinkedList<>();
		for (UndoCommand undoCommand: undoInfoList) {
			undoCommands.add(new UndoCommand(undoCommand));
		}
		undoHistory.put(app.getConfig().getSubAppCode(),
				new UndoHistory(undoCommands, iterator.nextIndex()));
		clearUndoInfo();
	}

	/**
	 * Reload undo list from map.
	 * @param undoHistory to reload from.
	 */
	public void undoHistoryFrom(Map<String, UndoHistory> undoHistory) {
		String subAppCode = app.getConfig().getSubAppCode();
		if (!undoHistory.containsKey(subAppCode)) {
			return;
		}

		clearUndoInfo();
		UndoHistory history = undoHistory.get(subAppCode);
		undoInfoList.addAll(history.commands());
		iterator = undoInfoList.listIterator(history.iteratorIndex());
		updateUndoActions();
	}

	/**
	 * @param undoPossibleListener
	 *            undo stack listener
	 */
	public void addUndoListener(UndoPossibleListener undoPossibleListener) {
		mListener.add(undoPossibleListener);
	}

	/**
	 * @param undoPossibleListener
	 *            undo stack listener
	 */
	public void removeUndoListener(UndoPossibleListener undoPossibleListener) {
		mListener.remove(undoPossibleListener);
	}

	/**
	 * inform listener that undo - action happened
	 */
	protected void informListener() {
		for (UndoPossibleListener listener : mListener) {
			listener.undoPossible(undoPossible());
			listener.redoPossible(redoPossible());
		}
	}
	
	public void setAllowCheckpoints(boolean val) {
		this.allowCheckpoints = val;
	}

	/**
	 * Remove all actions specific to an object with given label
	 * @param label object label
	 */
	public void removeActionsWithLabel(String label) {
		int updatedIndex = iterator.previousIndex();
		boolean removed = false;
		for (int i = undoInfoList.size() - 1; i > 0; i--) {
			if (undoInfoList.get(i).hasLabel(label)) {
				if (i <= iterator.previousIndex()) {
					updatedIndex--;
				}
				undoInfoList.remove(i);
				removed = true;
			}
		}
		if (removed) {
			iterator = undoInfoList.listIterator(updatedIndex + 1);
			updateUndoActions();
		}
	}
}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.DefaultListSelectionModel;

import org.geogebra.common.jre.util.StreamUtil;
import org.geogebra.common.kernel.AppState;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.UndoCommand;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.cas.view.CASViewD;
import org.geogebra.desktop.io.MyXMLioD;

/**
 * UndoManager handles undo information for a Construction. It uses an undo info
 * list with construction snapshots in temporary files.
 * 
 * @author Markus Hohenwarter
 */
public class UndoManagerD extends UndoManager {

	private boolean sync;

	/**
	 * Creates a new UndowManager for the given Construction.
	 * 
	 * @param cons
	 *            construction
	 * @param sync
	 *            whether to save undo synchronously
	 */
	public UndoManagerD(Construction cons, boolean sync) {
		super(cons);
		this.sync = sync;
	}

	/**
	 * Adds construction state to undo info list
	 */
	@Override
	public void storeUndoInfoAfterPasteOrAdd() {

		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction
				.getCurrentUndoXML(true);
		// force create event dispatcher before we go to thread
		Thread undoSaverThread = new Thread() {
			@Override
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				app.getCopyPaste().pastePutDownCallback(app);
			}
		};
		execute(undoSaverThread);
	}

	private void execute(Runnable undoSaveAction) {
		if (sync) {
			undoSaveAction.run();
		} else {
			new Thread(undoSaveAction).start();
		}
	}

	/**
	 * Adds construction state to undo info list.
	 */
	@Override
	public void storeUndoInfo(final StringBuilder currentUndoXML,
			final boolean refresh) {

		// force create event dispatcher before we go to thread
		app.getEventDispatcher();

		Thread undoSaverThread = new Thread() {
			@Override
			public void run() {
				doStoreUndoInfo(currentUndoXML);
				if (refresh) {
					restoreCurrentUndoInfo();
				}
			}
		};
		execute(undoSaverThread);
	}

	/**
	 * Adds construction state to undo info list.
	 * 
	 * @param undoXML
	 *            string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {
		// avoid security problems calling from JavaScript ie setUndoPoint()
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				try {
					// perform the security-sensitive operation here
					// save to file
					AppState appStateToAdd = new FileAppState(undoXML);

					// insert undo info
					UndoCommand command = new UndoCommand(appStateToAdd);
					maybeStoreUndoCommand(command);
					pruneStateList();
					app.getEventDispatcher().dispatchEvent(
							new Event(EventType.STOREUNDO));

				} catch (Exception e) {
					Log.debug("storeUndoInfo: " + e.toString());
					e.printStackTrace();
				} catch (java.lang.OutOfMemoryError err) {
					Log.debug("UndoManager.storeUndoInfo: " + err.toString());
					err.printStackTrace();
				}

				return null;
			}
		});

		updateUndoActions();
	}

	/**
	 * restore info at position pos of undo list
	 */
	@Override
	final protected synchronized void loadUndoInfo(final AppState info,
			String slideID) {

		InputStream is = null;

		try {
			// load from file
			File tempFile = ((FileAppState) info).getFile();
			is = new FileInputStream(tempFile);

			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// needed for GGB-517
			// keep information form listSelectionModel
			CASViewD casView = null;
			DefaultListSelectionModel listSelModel = null;
			if (app.getGuiManager() != null && app.getGuiManager().hasCasView()
					&& app.getView(App.VIEW_CAS) instanceof CASViewD) {
				casView = (CASViewD) app.getView(App.VIEW_CAS);
			}
			if (casView != null && casView.getListSelModel() != null && casView
					.getListSelModel() instanceof DefaultListSelectionModel) {
				listSelModel = (DefaultListSelectionModel) casView
						.getListSelModel();
			}

			int anchorIndex = 0;
			int leadIndex = 0;
			int maxIndex = 0;
			int minIndex = 0;
			boolean changed = false;

			if (listSelModel != null) {
				anchorIndex = listSelModel.getAnchorSelectionIndex();
				leadIndex = listSelModel.getLeadSelectionIndex();
				maxIndex = listSelModel.getMaxSelectionIndex();
				minIndex = listSelModel.getMinSelectionIndex();
				changed = true;
			}

			// load undo info
			app.getScriptManager().disableListeners();
			((MyXMLioD) construction.getXMLio()).readZipFromMemory(is);
			if (changed) {
				listSelModel.setAnchorSelectionIndex(anchorIndex);
				listSelModel.setLeadSelectionIndex(leadIndex);
				listSelModel.setSelectionInterval(minIndex, maxIndex);
			}
			app.getScriptManager().enableListeners();


		} catch (Exception e) {
			Log.error("setUndoInfo: " + e.toString());
			e.printStackTrace();
			restoreCurrentUndoInfo();
		} catch (java.lang.OutOfMemoryError err) {
			Log.error("UndoManager.loadUndoInfo: " + err.toString());
		} finally {
			StreamUtil.closeSilent(is);
		}

	}

}

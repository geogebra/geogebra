/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.main.undo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.DefaultListSelectionModel;

import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.AppState;
import org.geogebra.common.main.undo.UndoCommand;
import org.geogebra.common.main.undo.UndoManager;
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

	/**
	 * Creates a new UndowManager for the given Construction.
	 * 
	 * @param cons
	 *            construction
	 */
	public UndoManagerD(Construction cons) {
		super(cons);
	}

	/**
	 * Adds construction state to undo info list.
	 */
	@Override
	public void storeUndoInfo(final StringBuilder currentUndoXML) {

		// force create event dispatcher before we go to thread
		app.getEventDispatcher();

		Runnable storeUndoAction = () -> doStoreUndoInfo(currentUndoXML);
		new Thread(storeUndoAction).start();
	}

	/**
	 * Adds construction state to undo info list.
	 * 
	 * @param undoXML
	 *            string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {
		// avoid security problems calling from JavaScript ie setUndoPoint()
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			try {
				// perform the security-sensitive operation here
				// save to file
				AppState appStateToAdd = new FileAppState(undoXML);

				// insert undo info
				UndoCommand command = new UndoCommand(appStateToAdd);
				maybeStoreUndoCommand(command);
				pruneStateList();
				if (undoInfoList.size() > 1) {
					notifyUnsaved();
				}
			} catch (Exception | OutOfMemoryError e) {
				Log.debug("storeUndoInfo: " + e);
				Log.debug(e);
			}

			return null;
		});

		onStoreUndo();
	}

	/**
	 * restore info at position pos of undo list
	 */
	@Override
	final protected synchronized void loadUndoInfo(final AppState info,
			String slideID) {
		if (!(info instanceof FileAppState)) {
			Log.warn("Invalid undo state");
			restoreCurrentUndoInfo();
			return;
		}
		File tempFile = ((FileAppState) info).getFile();

		try (FileInputStream is = new FileInputStream(tempFile)) {
			// load from file

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
			app.getEventDispatcher().disableListeners();
			((MyXMLioD) construction.getXMLio()).readZipFromMemory(is);
			if (changed) {
				listSelModel.setAnchorSelectionIndex(anchorIndex);
				listSelModel.setLeadSelectionIndex(leadIndex);
				listSelModel.setSelectionInterval(minIndex, maxIndex);
			}
			app.getEventDispatcher().enableListeners();
		} catch (IOException | XMLParseException | RuntimeException e) {
			Log.error("Problem setting undo info");
			Log.debug(e);
			restoreCurrentUndoInfo();
		} catch (java.lang.OutOfMemoryError err) {
			Log.error("UndoManager.loadUndoInfo: " + err);
		}

	}

}

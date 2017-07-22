package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.export.AnimationExportDialogD;
import org.geogebra.desktop.export.WorksheetExportDialog;
import org.geogebra.desktop.export.pstricks.AsymptoteFrame;
import org.geogebra.desktop.export.pstricks.GeoGebraToAsymptoteD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPdfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPgfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
import org.geogebra.desktop.export.pstricks.PdfFrame;
import org.geogebra.desktop.export.pstricks.PgfFrame;
import org.geogebra.desktop.export.pstricks.PstricksFrame;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.util.GuiResourcesD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * The "File" menu.
 */
class FileMenuD extends BaseMenu implements EventRenderable {
	private static final long serialVersionUID = -5154067739481481835L;

	private AbstractAction newWindowAction, deleteAll, saveAction, saveAsAction,
			loadAction, loadURLAction, exportWorksheet, shareAction,
			exportGraphicAction, exportAnimationAction, exportPgfAction,
			exportPSTricksAction, exportAsymptoteAction, exportPDFaction, exportSCADaction;
	/** load from MAT item */
	JMenuItem loadURLMenuItem;
	/** share item */
	AbstractAction exportGeoGebraTubeAction;

	private AbstractAction drawingPadToClipboardAction;

	private AbstractAction printEuclidianViewAction;

	private AbstractAction exitAction;

	private AbstractAction exitAllAction;

	/**
	 * @param app
	 *            application
	 */
	public FileMenuD(AppD app) {
		super(app, "File");

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);

	}

	/**
	 * Initialize all items.
	 */
	@Override
	public void initItems() {
		if (!initialized) {
			return;
		}

		removeAll();

		JMenuItem mi;

		if (!app.isApplet()) {
			// "New" in application: new window
			mi = new JMenuItem(newWindowAction);
			setMenuShortCutAccelerator(mi, 'N');
			add(mi);
		}

		// "New": reset
		add(deleteAll);

		mi = add(loadAction);
		setMenuShortCutAccelerator(mi, 'O'); // open

		LoginOperationD signIn = (LoginOperationD) app.getLoginOperation();

		if (!app.isApplet()
				&& (signIn.isTubeAvailable() || !signIn.isTubeCheckDone())) {
			loadURLMenuItem = add(loadURLAction);

			// If GeoGebraTube is not available we disable the item and
			// listen to the event that tube becomes available
			if (!signIn.isTubeAvailable()) {
				loadURLAction.setEnabled(false);
				signIn.getView().add(this);
			}

		}

		// recent SubMenu
		JMenu submenuRecent = new JMenu(loc.getMenu("Recent"));
		submenuRecent.setIcon(app.getEmptyIcon());
		add(submenuRecent);

		// Recent files list
		int size = AppD.getFileListSize();
		if (size > 0) {
			for (int i = 0; i < AppD.MAX_RECENT_FILES; i++) {
				File file = AppD.getFromFileList(i);
				if (file != null) {
					mi = new JMenuItem(file.getName());
					mi.setIcon(app.getMenuIcon(GuiResourcesD.GEOGEBRA));
					ActionListener al = new LoadFileListener(app, file);
					mi.addActionListener(al);
					submenuRecent.add(mi);
				}
			}
		}

		addSeparator();
		mi = add(saveAction);
		setMenuShortCutAccelerator(mi, 'S');
		mi = add(saveAsAction);
		addSeparator();

		mi = add(shareAction);
		mi.setIcon(app.getMenuIcon(GuiResourcesD.EXPORT_SMALL));

		// export
		JMenu submenu = new JMenu(loc.getMenu("Export"));
		submenu.setIcon(app.getEmptyIcon());
		add(submenu);

		mi = submenu.add(exportWorksheet);
		setMenuShortCutShiftAccelerator(mi, 'W');

		mi = submenu.add(exportGraphicAction);
		setMenuShortCutShiftAccelerator(mi, 'U');

		mi = submenu.add(exportAnimationAction);

		// Graphical clipboard is not working under Mac when Java == 7:
		if (!app.isMacOS() || !app.isJava7()) {
			mi = submenu.add(drawingPadToClipboardAction);
			setMenuShortCutShiftAccelerator(mi, 'C');
		}

		submenu.addSeparator();
		mi = submenu.add(exportPSTricksAction);
		setMenuShortCutShiftAccelerator(mi, 'T');

		mi = submenu.add(exportPgfAction);
		mi = submenu.add(exportAsymptoteAction);
		if (app.has(Feature.EXPORT_ANIMATED_PDF)) {
			mi = submenu.add(exportPDFaction);
		}
		if (app.has(Feature.EXPORT_SCAD)) {
			mi = submenu.add(exportSCADaction);
		}
		addSeparator();

		mi = add(printEuclidianViewAction);
		mi.setText(loc.getMenu("PrintPreview"));
		mi.setIcon(app.getMenuIcon(GuiResourcesD.DOCUMENT_PRINT_PREVIEW));
		setMenuShortCutAccelerator(mi, 'P');

		// End Export SubMenu

		// DONE HERE WHEN APPLET
		if (app.isApplet()) {
			return;
		}

		// close
		addSeparator();
		mi = add(exitAction);
		if (AppD.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4,
					InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}

		// close all
		if (GeoGebraFrame.getInstanceCount() > 1) {
			add(exitAllAction);
		}

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Initialize all actions of this menu.
	 */
	@Override
	protected void initActions() {
		deleteAll = new AbstractAction(loc.getMenu("New"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		};

		newWindowAction = new AbstractAction(loc.getMenu("NewWindow"),
				app.getMenuIcon(GuiResourcesD.DOCUMENT_NEW)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						app.createNewWindow();
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		saveAction = new AbstractAction(loc.getMenu("Save"),
				app.getMenuIcon(GuiResourcesD.DOCUMENT_SAVE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().save();
			}
		};

		saveAsAction = new AbstractAction(
				loc.getMenu("SaveAs") + Unicode.ELLIPSIS,
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().saveAs();
			}
		};

		shareAction = new AbstractAction(
				loc.getMenu("Share") + Unicode.ELLIPSIS,
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				exportGeoGebraTubeAction.actionPerformed(e);
			}
		};

		/*
		 * printProtocolAction = new AbstractAction(
		 * loc.getMenu("ConstructionProtocol") + " ...") { private static final
		 * long serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) { Thread runner = new
		 * Thread() { public void run() { ConstructionProtocol constProtocol =
		 * app.getConstructionProtocol(); if (constProtocol == null) {
		 * constProtocol = new ConstructionProtocol(app); }
		 * constProtocol.initProtocol();
		 * 
		 * try { new PrintPreview(app, constProtocol, PageFormat.PORTRAIT); }
		 * catch (Exception e) { Application.debug("Print preview not available"
		 * ); } } }; runner.start(); } };
		 */

		printEuclidianViewAction = new AbstractAction(
				loc.getMenu("DrawingPad") + " ...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				GeoGebraMenuBar.showPrintPreview(app);
			}
		};

		exitAction = new AbstractAction(loc.getMenu("Close"),
				app.getMenuIcon(GuiResourcesD.EXIT)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.exit();
			}
		};

		exitAllAction = new AbstractAction(loc.getMenu("CloseAll"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.exitAll();
			}
		};

		loadAction = new AbstractAction(loc.getMenu("Load") + " ...",
				app.getMenuIcon(GuiResourcesD.DOCUMENT_OPEN)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().openFile();
			}
		};

		loadURLAction = new AbstractAction(
				loc.getMenu("OpenFromGeoGebraTube") + " ...",
				app.getMenuIcon(GuiResourcesD.DOCUMENT_OPEN)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if javafx is available
				boolean javaFx22Available = false;
				try {
					this.getClass().getClassLoader()
							.loadClass("javafx.embed.swing.JFXPanel");
					javaFx22Available = true;
				} catch (ClassNotFoundException ex) {
					Log.error("JavaFX 2.2 not available");
				}

				// Open the Search dialog only when javafx is available.
				// The User can force opening the old 'Open URL' dialog by
				// pressing shift.
				if (javaFx22Available
						&& ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0)) {

					app.getGuiManager().openFromGGT();
				} else {

					// old File -> Open from Webpage by pressing <Shift>
					app.getGuiManager().openURL();
				}
			}
		};

		drawingPadToClipboardAction = new AbstractAction(
				loc.getMenu("DrawingPadToClipboard"),
				app.getMenuIcon(GuiResourcesD.MENU_EDIT_COPY)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getSelectionManager().clearSelectedGeos(true, false);
				app.updateSelection(false);

				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						// copy drawing pad to the system clipboard
						app.copyGraphicsViewToClipboard();
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		/*
		 * updateAction = new AbstractAction(getMenu("Update"), getEmptyIcon())
		 * { private static final long serialVersionUID = 1L; public void
		 * actionPerformed(ActionEvent e) { Thread runner = new Thread() {
		 * public void run() { updateGeoGebra(); } }; runner.start(); } };
		 */

		exportGraphicAction = new AbstractAction(
				loc.getMenu("DrawingPadAsPicture") + " (" + FileExtensions.PNG
						+ ", " + FileExtensions.EPS + ") ...",
				app.getMenuIcon(GuiResourcesD.IMAGE_X_GENERIC)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						try {

							app.getGuiManager().showGraphicExport();

						} catch (Exception e1) {
							Log.debug(
									"GraphicExportDialog not available for 3D view yet");
							// for 3D View
							app.copyGraphicsViewToClipboard();
						}
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		// export slider as animation
		exportAnimationAction = new AbstractAction(
				loc.getMenu("ExportAnimatedGIF") + " ...") {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new AnimationExportDialogD(app);
				} catch (Exception ex) {
					Log.debug("AnimationExportDialog not available");
				}
			}
		};

		exportPSTricksAction = new AbstractAction(
				loc.getMenu("DrawingPadAsPSTricks") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GeoGebraToPstricksD export = new GeoGebraToPstricksD(app);
					new PstricksFrame(export).setVisible(true);
				} catch (Exception ex) {
					Log.debug("GeoGebraToPstricks not available");
				}
			}
		};
		// Added By Loic Le Coq
		exportPgfAction = new AbstractAction(
				loc.getMenu("DrawingPagAsPGF") + " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					GeoGebraToPgfD export = new GeoGebraToPgfD(app);
					new PgfFrame(export);
				} catch (Exception ex) {
					Log.debug("GeoGebraToPGF not available");
				}
			}
		};

		if (app.has(Feature.EXPORT_ANIMATED_PDF)) {
			// added by Hoszu Henrietta, animated pdf export
			exportPDFaction = new AbstractAction(
					loc.getMenu("Graphics View as Animated PDF") + " ...",
					app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				@Override
				@SuppressWarnings("unused")
				public void actionPerformed(ActionEvent e) {
					try {
						GeoGebraToPdfD export = new GeoGebraToPdfD(app);
						new PdfFrame(export);
					} catch (Exception ex) {
						Log.debug("GeoGebraToPDF not available");
					}
				}
			};
		}

		// Added by Andy Zhu; Asymptote export
		exportAsymptoteAction = new AbstractAction(
				loc.getMenu("GraphicsViewAsAsymptote") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					GeoGebraToAsymptote export = new GeoGebraToAsymptoteD(app);
					new AsymptoteFrame(export);
				} catch (Exception ex) {
					Log.debug("GeoGebraToAsymptote not available");
				}
			}
		};

		// End

		exportWorksheet = new AbstractAction(
				loc.getMenu("DynamicWorksheetAsWebpage") + " ("
						+ FileExtensions.HTML + ") ...",
				app.getMenuIcon(GuiResourcesD.TEXT_HTML)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				Thread runner = new Thread() {
					@Override
					public void run() {

						app.setWaitCursor();
						try {
							app.getSelectionManager().clearSelectedGeos(true,
									false);
							app.updateSelection(false);
							WorksheetExportDialog d = new WorksheetExportDialog(
									app);

							d.setVisible(true);
						} catch (Exception e1) {
							Log.debug("WorksheetExportDialog not available");
							e1.printStackTrace();
						}
						app.setDefaultCursor();
					}
				};
				runner.start();

			}
		};

		exportGeoGebraTubeAction = new AbstractAction(
				loc.getMenu("UploadGeoGebraTube") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				Thread runner = new Thread() {
					@Override
					public void run() {

						app.setWaitCursor();
						try {
							app.getSelectionManager().clearSelectedGeos(true,
									false);
							app.updateSelection(false);

							// callback for 3D
							app.uploadToGeoGebraTubeOnCallback();

						} catch (Exception e1) {
							Log.debug("Uploading failed");
							e1.printStackTrace();
						}
						app.setDefaultCursor();
					}
				};
				runner.start();

			}
		};
		
		
		if (app.has(Feature.EXPORT_SCAD)) {
			exportSCADaction = new AbstractAction(
					loc.getMenu("ExportToOpenSCAD") + " ...",
					app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						app.setFlagForSCADexport();
					} catch (Exception ex) {
						Log.debug("Export to OpenSCAD not available");
					}
				}
			};
		}

	}

	@Override
	public void update() {
		//

	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof TubeAvailabilityCheckEvent) {
			TubeAvailabilityCheckEvent checkEvent = (TubeAvailabilityCheckEvent) event;
			if (!checkEvent.isAvailable()) {
				remove(loadURLMenuItem);
			} else {
				loadURLAction.setEnabled(true);
			}
		}
	}

}

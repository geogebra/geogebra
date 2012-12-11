package geogebra.gui.menubar;

import geogebra.common.kernel.View;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;
import geogebra3D.App3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The "File" menu.
 */
class FileMenu extends BaseMenu {
	private static final long serialVersionUID = -5154067739481481835L;
	
	private AbstractAction
		newWindowAction,
		deleteAll,
		saveAction,
		saveAsAction,
		loadAction,
		loadURLAction,
		exportWorksheet,
		shareAction,
		exportGraphicAction,
		exportAnimationAction,
		exportPgfAction,
		exportPSTricksAction,
		exportAsymptoteAction
	;

	AbstractAction exportGeoGebraTubeAction;

	private AbstractAction drawingPadToClipboardAction;

	private AbstractAction printEuclidianViewAction;

	private AbstractAction exitAction;

	private AbstractAction exitAllAction;

	public FileMenu(AppD app) {
		super(app, app.getMenu("File"));
		
		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
		
	}
	
	/**
	 * Initialize all items.
	 */
	public void initItems()
	{
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

		if (AppD.hasFullPermissions()) {
			mi = add(loadAction);
			setMenuShortCutAccelerator(mi, 'O'); // open
			add(loadURLAction);
		
			// recent SubMenu
			JMenu submenuRecent = new JMenu(app.getMenu("Recent"));
			submenuRecent.setIcon(app.getEmptyIcon());
			add(submenuRecent);
			
			// Recent files list
			int size = AppD.getFileListSize();
			if (size > 0) {
				for (int i = 0; i < AppD.MAX_RECENT_FILES ; i++) {
					File file = AppD.getFromFileList(i);
					if (file != null) {
						mi = new JMenuItem(file.getName());
						mi.setIcon(app.getImageIcon("geogebra.png"));
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
			mi.setIcon(app.getImageIcon("export_small.png"));
			
			// export
			JMenu submenu = new JMenu(app.getMenu("Export"));
			submenu.setIcon(app.getEmptyIcon());
			add(submenu);
			
			mi = submenu.add(exportWorksheet);
			setMenuShortCutShiftAccelerator(mi, 'W');
				
			mi = submenu.add(exportGraphicAction);
			setMenuShortCutShiftAccelerator(mi, 'P');
			
			mi = submenu.add(exportAnimationAction);
	
			mi = submenu.add(drawingPadToClipboardAction);
			setMenuShortCutShiftAccelerator(mi, 'C');
	
			submenu.addSeparator();
			mi = submenu.add(exportPSTricksAction);
			setMenuShortCutShiftAccelerator(mi, 'T');
	
			mi = submenu.add(exportPgfAction);
			mi = submenu.add(exportAsymptoteAction);

			

			addSeparator();
			
			mi = add(printEuclidianViewAction);
			mi.setText(app.getMenu("PrintPreview"));
			mi.setIcon(app.getImageIcon("document-print-preview.png"));
			setMenuShortCutAccelerator(mi, 'P');
		}
		
		// End Export SubMenu

		// DONE HERE WHEN APPLET
		if (app.isApplet())
			return;
		
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
	protected void initActions()
	{
		deleteAll = new AbstractAction(app.getMenu("New"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();		
				app.fileNew();
				app.setDefaultCursor();
			}
		};

		newWindowAction = new AbstractAction(app.getMenu("NewWindow"), app
				.getImageIcon("document-new.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						GeoGebraFrame.createNewWindow(null);
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"), app
				.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				((GuiManagerD)app.getGuiManager()).save();
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...", app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				((GuiManagerD)app.getGuiManager()).saveAs();
			}
		};

		shareAction = new AbstractAction(app.getMenu("Share")+"...", app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				exportGeoGebraTubeAction.actionPerformed(e);
			}
		};

		/*
		 * printProtocolAction = new AbstractAction(
		 * app.getPlain("ConstructionProtocol") + " ...") { private static final
		 * long serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) { Thread runner = new
		 * Thread() { public void run() { ConstructionProtocol constProtocol =
		 * app.getConstructionProtocol(); if (constProtocol == null) {
		 * constProtocol = new ConstructionProtocol(app); }
		 * constProtocol.initProtocol();
		 * 
		 * try { new PrintPreview(app, constProtocol, PageFormat.PORTRAIT); }
		 * catch (Exception e) {
		 * Application.debug("Print preview not available"); } } };
		 * runner.start(); } };
		 */

		printEuclidianViewAction = new AbstractAction(app
				.getPlain("DrawingPad")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraMenuBar.showPrintPreview(app);
			}
		};

		exitAction = new AbstractAction(app.getMenu("Close"), app
				.getImageIcon("exit.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exit();
			}
		};

		exitAllAction = new AbstractAction(app.getMenu("CloseAll"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exitAll();
			}
		};

		loadAction = new AbstractAction(app.getMenu("Load") + " ...", app
				.getImageIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				((GuiManagerD)app.getGuiManager()).openFile();
			}
		};

		loadURLAction = new AbstractAction(app.getMenu("OpenWebpage") + " ...", app
				.getImageIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				((GuiManagerD)app.getGuiManager()).openURL();
			}
		};

		drawingPadToClipboardAction = new AbstractAction(app
				.getMenu("DrawingPadToClipboard"), app
				.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.clearSelectedGeos(true,false);
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

		exportGraphicAction = new AbstractAction(app
				.getPlain("DrawingPadAsPicture")
				+ " ("
				+ AppD.FILE_EXT_PNG
				+ ", "
				+ AppD.FILE_EXT_EPS + ") ...", app
				.getImageIcon("image-x-generic.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					Thread runner = new Thread() {
						@Override
						public void run() {
							app.setWaitCursor();
							try {
								
								((GuiManagerD)app.getGuiManager()).showGraphicExport();

							} catch (Exception e1) {
								App
										.debug("GraphicExportDialog not available");
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
		
		// export slider as animation
		exportAnimationAction = new AbstractAction(app.getPlain("ExportAnimatedGIF")+" ...") {	
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new geogebra.export.AnimationExportDialog(app);
				} catch (Exception ex) {
					App.debug("AnimationExportDialog not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		exportPSTricksAction = new AbstractAction(app
				.getPlain("DrawingPadAsPSTricks")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new geogebra.export.pstricks.GeoGebraToPstricks(app);
				} catch (Exception ex) {
					App.debug("GeoGebraToPstricks not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
		// Added By Loïc Le Coq
		exportPgfAction = new AbstractAction(app.getPlain("DrawingPagAsPGF")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new geogebra.export.pstricks.GeoGebraToPgf(app);
				} catch (Exception ex) {
					App.debug("GeoGebraToPGF not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		// Added by Andy Zhu; Asymptote export
		exportAsymptoteAction = new AbstractAction(app.getPlain("GraphicsViewAsAsymptote")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new geogebra.export.pstricks.GeoGebraToAsymptote(app);
				} catch (Exception ex) {
					App.debug("GeoGebraToAsymptote not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		// End

		exportWorksheet = new AbstractAction(app
				.getPlain("DynamicWorksheetAsWebpage")
				+ " (" + AppD.FILE_EXT_HTML + ") ...", app
				.getImageIcon("text-html.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {

					Thread runner = new Thread() {
						@Override
						public void run() {

							app.setWaitCursor();
							try {
								app.clearSelectedGeos(true,false);
								app.updateSelection(false);
								geogebra.export.WorksheetExportDialog d = new geogebra.export.WorksheetExportDialog(
										app);

								d.setVisible(true);
							} catch (Exception e1) {
								App
										.debug("WorksheetExportDialog not available");
								e1.printStackTrace();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
		
		exportGeoGebraTubeAction = new AbstractAction(
				app.getMenu("UploadGeoGebraTube") + " ...", 
				app.getEmptyIcon()
		) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {

					Thread runner = new Thread() {
						@Override
						public void run() {
							
							app.setWaitCursor();
							try {
								app.clearSelectedGeos(true,false);
								app.updateSelection(false);
								
								// create new exporter
								geogebra.export.GeoGebraTubeExportDesktop exporter
									= new geogebra.export.GeoGebraTubeExportDesktop(app);
								
								exporter.uploadWorksheet(null);
								
							} catch (Exception e1) {
								App.debug("Uploading failed");
								e1.printStackTrace();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
	}

	@Override
	public void update() {
		// 
		
	}


}

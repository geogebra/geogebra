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

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatCollada;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatColladaHTML;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatSTL;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.export.AnimationExportDialogD;
import org.geogebra.desktop.export.WorksheetExportDialog;
import org.geogebra.desktop.export.pstricks.AsymptoteFrame;
import org.geogebra.desktop.export.pstricks.PgfFrame;
import org.geogebra.desktop.export.pstricks.PstricksFrame;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * The "File" menu.
 */
class FileMenuD extends BaseMenu {
	private static final long serialVersionUID = -5154067739481481835L;

	private AbstractAction newWindowAction;
	private AbstractAction deleteAll;
	private AbstractAction saveAction;
	private AbstractAction saveAsAction;
	private AbstractAction loadAction;
	private AbstractAction loadURLAction;
	private AbstractAction exportWorksheet;
	private AbstractAction saveOnlineAction;
	private AbstractAction exportGraphicAction;
	private AbstractAction exportAnimationAction;
	private AbstractAction exportPgfAction;
	private AbstractAction exportPSTricksAction;
	private AbstractAction exportAsymptoteAction;
	private AbstractAction exportSTLaction;
	private AbstractAction exportColladaAction;
	private AbstractAction exportColladaHTMLAction;
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

		// "New" in application: new window
		mi = new JMenuItem(newWindowAction);
		setMenuShortCutAccelerator(mi, 'N');
		add(mi);

		// "New": reset
		add(deleteAll);

		mi = add(loadAction);
		setMenuShortCutAccelerator(mi, 'O'); // open

		loadURLMenuItem = add(loadURLAction);

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
		add(saveAsAction);
		addSeparator();

		mi = add(saveOnlineAction);
		mi.setIcon(app.getMenuIcon(GuiResourcesD.EXPORT_SMALL));

		// export
		JMenu submenu = new JMenu(loc.getMenu("Export"));
		submenu.setIcon(app.getEmptyIcon());
		add(submenu);

		mi = submenu.add(exportWorksheet);
		setMenuShortCutShiftAccelerator(mi, 'W');

		mi = submenu.add(exportGraphicAction);
		setMenuShortCutShiftAccelerator(mi, 'U');

		submenu.add(exportAnimationAction);

		mi = submenu.add(drawingPadToClipboardAction);
		setMenuShortCutShiftAccelerator(mi, 'C');

		submenu.addSeparator();
		mi = submenu.add(exportPSTricksAction);
		setMenuShortCutShiftAccelerator(mi, 'T');

		submenu.add(exportPgfAction);
		submenu.add(exportAsymptoteAction);
		submenu.add(exportSTLaction);

		if (app.is3D()) {
			submenu.add(exportColladaAction);
			submenu.add(exportColladaHTMLAction);
		}
		addSeparator();

		mi = add(printEuclidianViewAction);
		mi.setText(loc.getMenu("PrintPreview"));
		mi.setIcon(app.getMenuIcon(GuiResourcesD.DOCUMENT_PRINT_PREVIEW));
		setMenuShortCutAccelerator(mi, 'P');
		// End Export SubMenu

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
				Thread runner = new Thread(() -> {
					app.setWaitCursor();
					app.createNewWindow();
					app.setDefaultCursor();
				});
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

		saveOnlineAction = new AbstractAction(
				loc.getMenu("SaveOnline"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				exportGeoGebraTubeAction.actionPerformed(e);
			}
		};

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
				loc.getMenu("OpenFromWebpage") + " ...",
				app.getMenuIcon(GuiResourcesD.DOCUMENT_OPEN)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().openURL();
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

				Thread runner = new Thread(() -> {
					app.setWaitCursor();
					// copy drawing pad to the system clipboard
					app.copyGraphicsViewToClipboard();
					app.setDefaultCursor();
				});
				runner.start();
			}
		};

		exportGraphicAction = new AbstractAction(
				loc.getMenu("DrawingPadAsPicture") + " (" + FileExtensions.PNG
						+ ", " + FileExtensions.EPS + ") ...",
				app.getMenuIcon(GuiResourcesD.IMAGE_X_GENERIC)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread(() -> {
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
				});
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
					app.newGeoGebraToPstricks(PstricksFrame::new);
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
					app.newGeoGebraToPgf(PgfFrame::new);
				} catch (Exception ex) {
					Log.debug("GeoGebraToPGF not available");
				}
			}
		};

		// Added by Andy Zhu; Asymptote export
		exportAsymptoteAction = new AbstractAction(
				loc.getMenu("GraphicsViewAsAsymptote") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					app.newGeoGebraToAsymptote(AsymptoteFrame::new);
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

				Thread runner = new Thread(() -> {

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
				});
				runner.start();

			}
		};

		exportGeoGebraTubeAction = new AbstractAction(
				loc.getMenu("UploadGeoGebraTube") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				Thread runner = new Thread(() -> {

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
				});
				runner.start();

			}
		};

		exportSTLaction = new AbstractAction("STL" + Unicode.ELLIPSIS,
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					app.setExport3D(new FormatSTL());
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.debug("Problem exporting to STL");
				}
			}
		};

		if (app.is3D()) {
			exportColladaAction = new AbstractAction("Collada" + Unicode.ELLIPSIS,
					app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						app.setExport3D(new FormatCollada());
					} catch (Exception ex) {
						Log.debug("Export to Collada not available");
					}
				}
			};
		}

		if (app.is3D()) {
			exportColladaHTMLAction = new AbstractAction(
					"Collada (html)" + Unicode.ELLIPSIS, app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						app.setExport3D(new FormatColladaHTML());
					} catch (Exception ex) {
						Log.debug("Export to Collada not available");
					}
				}
			};
		}
	}

	@Override
	public void update() {
		// not needed
	}

}

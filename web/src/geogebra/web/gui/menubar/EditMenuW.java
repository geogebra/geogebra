package geogebra.web.gui.menubar;

import geogebra.common.main.OptionType;
import geogebra.common.main.SelectionManager;
import geogebra.common.util.CopyPaste;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.images.AppResources;

/**
 * The "Edit" menu.
 */
public class EditMenuW extends GMenuBar {

	/**
	 * Application instance
	 */
	final AppW app;
	final SelectionManager selection;
	private boolean valid = true;
	/**
	 * Constructs the "Edit" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public EditMenuW(AppW app) {

		super(true, new MenuResources());
		this.app = app;
		this.selection = app.getSelectionManager();
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	void initActions() {

		String noIcon = AppResources.INSTANCE.empty().getSafeUri().asString();
		/*
		 * layer values: -1 means nothing selected -2 means different layers
		 * selected
		 */
		int layer = selection.getSelectedLayer();
		boolean justCreated = !(app.getActiveEuclidianView()
		        .getEuclidianController().getJustCreatedGeos().isEmpty());
		boolean haveSelection = !selection.getSelectedGeos().isEmpty();

		clearItems();

		if (app.getLAF().undoRedoSupported()) {
			addUndoRedo();
			// separator
			addSeparator();
		}

		// copy menu
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
		        .menu_icon_edit_copy().getSafeUri().asString(),
		        app.getMenu("Copy"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
				if (!selection.getSelectedGeos().isEmpty()) {
					app.setWaitCursor();
					CopyPaste.INSTANCE.copyToXML(app,
					        selection.getSelectedGeos(), false);
					initActions(); // app.updateMenubar(); - it's needn't to
					               // update the all menubar here
					app.setDefaultCursor();
				}
			}
		});

		// paste menu
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
		        .menu_icon_edit_paste().getSafeUri().asString(),
		        app.getMenu("Paste"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
				if (!CopyPaste.INSTANCE.isEmpty()) {
					app.setWaitCursor();
					CopyPaste.INSTANCE.pasteFromXML(app, false);
					app.setDefaultCursor();
				}

			}
		});

		if (!(app.isExam()) && app.getLAF().copyToClipboardSupported()) {
			// copy graphics view menu
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
			        .menu_icon_edit_copy().getSafeUri().asString(),
			        app.getMenu("CopyImage"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        app.copyEVtoClipboard();
				        }
			        });
		}

		addSeparator();

		// object properties menu
		if (!app.isApplet()) {

			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
			        .menu_icon_options().getSafeUri().asString(),
			        !app.getKernel().isEmpty() ? app.getPlain("Properties")
			                : app.getMenu("Options") + " ...", true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        if (!selection.getSelectedGeos().isEmpty()) {
						        app.getDialogManager().showPropertiesDialog(
						                OptionType.OBJECTS, null);
					        }
				        }
			        });

			addSeparator();

		}

		// select all menu
		addItem(MainMenu.getMenuBarHtml(noIcon, app.getMenu("SelectAll"), true),
		        true, new MenuCommand(app) {

			        @Override
			        public void doExecute() {
				        if (!app.getKernel().isEmpty()) {
					        selection.selectAll(-1);
				        }
			        }
		        });

		// select current layer menu
		if (selection.getSelectedLayer() >= 0 && app.getMaxLayerUsed() > 0) {
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectCurrentLayer"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        int layer1 = selection.getSelectedLayer();
					        if (layer1 != -1)
						        selection.selectAll(layer1); // select all
						                                     // objects in layer
				        }
			        });
		}

		if (selection.hasDescendants()) {
			// select descendants menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectDescendants"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        selection.selectAllDescendants();
				        }
			        });
		}

		if (selection.hasPredecessors()) {
			// select ancestors menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAncestors"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        selection.selectAllPredecessors();
				        }
			        });

		}

		if (haveSelection) {
			addSeparator();
			// invert selection menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("InvertSelection"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        selection.invertSelection();
				        }
			        });
		}

		// show/hide objects and show/hide labels menus
		if (layer != -1) {
			addItem(MainMenu.getMenuBarHtml(noIcon, app.getMenu("ShowHide"),
			        true), true, new MenuCommand(app) {

				@Override
				public void doExecute() {
					selection.showHideSelection();
				}
			});

			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("ShowHideLabels"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        selection.showHideSelectionLabels();
				        }
			        });
		}

		// Delete menu
		if (layer != -1 || justCreated) {
			addSeparator();
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
			        .menu_icon_edit_delete().getSafeUri().asString(),
			        app.getPlain("Delete"), true), true, new MenuCommand(app) {

				@Override
				public void doExecute() {
					app.deleteSelectedObjects();
				}
			});
		}

	}

	private void addUndoRedo() {
		// undo menu
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
		        .menu_icon_edit_undo().getSafeUri().asString(),
		        app.getMenu("Undo"), true), true, new MenuCommand(app) {

			@Override
			public void execute() {
				if (app.getKernel().undoPossible()) {
					app.getGuiManager().undo();
				}
			}
		});

		// redo menu
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
		        .menu_icon_edit_redo().getSafeUri().asString(),
		        app.getMenu("Redo"), true), true, new MenuCommand(app) {

			@Override
			public void execute() {
				if (app.getKernel().redoPossible()) {
					app.getGuiManager().redo();
				}
			}
		});
	}

	public void invalidate(){
		if (app.isMenuShowing()) {
			this.valid = true;
			this.initActions();
		} else {
			this.valid = false;
		}
	}
	public void update() {
		if (!valid) {
			valid = true;
			this.initActions();
		}

	}

}

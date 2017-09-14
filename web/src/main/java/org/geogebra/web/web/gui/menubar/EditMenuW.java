package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;

/**
 * The "Edit" menu.
 */
public class EditMenuW extends GMenuBar {

	/**
	 * Application instance
	 */
	final AppW app;
	/**
	 * Reference to selection manager
	 */
	final SelectionManager selection;
	private boolean valid = true;
	private Localization loc;
	/**
	 * Constructs the "Edit" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public EditMenuW(AppW app) {

		super(true, "edit", new MenuResources(), app);
		this.app = app;
		this.loc = app.getLocalization();
		this.selection = app.getSelectionManager();
		if (app.isUnbundled() || app.isWhiteboardActive()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
	}

	/**
	 * initializes the menu
	 */
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

		if (app.isUndoRedoEnabled()) {
			addUndoRedo();
			if (!app.isUnbundled()) {
				// separator
				addSeparator();
			}
		}

		addCopy();

		addPasteItem();

		if (!app.isUnbundled()) {
			addSeparator();
		}

		// object properties menu

		addPropertiesItem();

		if (!app.isUnbundled()) {
			addSeparator();
		}

		addSelectAllItem(noIcon);

		addSelectCurrentLayer(noIcon);

		addDescentdantsItem(noIcon);

		addPredecessorsItem(noIcon);

		if (haveSelection) {
			if (!app.isUnbundled()) {
				addSeparator();
			}
			// invert selection menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
					loc.getMenu("InvertSelection"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        selection.invertSelection();
				        }
			        });
		}

		if (layer != -1) {
			addShowHideItem(noIcon);
			addShowHideLabelsItem(noIcon);
		}

		// Delete menu
		if (layer != -1 || justCreated) {
			if (!app.isUnbundled()) {
				addSeparator();
			}
			addDeleteItem();
		}

	}

	private void addShowHideLabelsItem(String noIcon) {
		addItem(MainMenu.getMenuBarHtml(noIcon, loc.getMenu("ShowHideLabels"),
				true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						selection.showHideSelectionLabels();
					}
				});

	}

	private void addShowHideItem(String noIcon) {
		addItem(MainMenu.getMenuBarHtml(noIcon, loc.getMenu("ShowHide"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						selection.showHideSelection();
					}
				});

	}

	private void addDeleteItem() {
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.delete_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE.menu_icon_edit_delete()
								.getSafeUri().asString(),
				loc.getMenu("Delete"), true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.deleteSelectedObjects(false);
					}
				});

	}

	private void addPasteItem() {
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.paste_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE.menu_icon_edit_paste()
								.getSafeUri().asString(),
				loc.getMenu("Paste"), true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						if (!app.getCopyPaste().isEmpty()) {
							app.setWaitCursor();
							app.getCopyPaste().pasteFromXML(app, false);
							app.setDefaultCursor();
						}

					}
				});

	}

	private void addSelectAllItem(String noIcon) {
		// select all menu
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.select_all_black()
								.getSafeUri().asString()
						: noIcon,
				loc.getMenu("SelectAll"), true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						if (!app.getKernel().isEmpty()) {
							selection.selectAll(-1);
						}
					}
				});

	}

	private void addPredecessorsItem(String noIcon) {
		if (selection.hasPredecessors()) {
			// select ancestors menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
					loc.getMenu("SelectAncestors"), true), true,
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							selection.selectAllPredecessors();
						}
					});

		}

	}

	private void addDescentdantsItem(String noIcon) {
		if (selection.hasDescendants()) {
			// select descendants menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
					loc.getMenu("SelectDescendants"), true), true,
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							selection.selectAllDescendants();
						}
					});
		}

	}

	private void addSelectCurrentLayer(String noIcon) {
		if (selection.getSelectedLayer() >= 0 && app.getMaxLayerUsed() > 0) {
			addItem(MainMenu.getMenuBarHtml(noIcon,
					loc.getMenu("SelectCurrentLayer"), true), true,
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							int layer1 = selection.getSelectedLayer();
							if (layer1 != -1) {
								selection.selectAll(layer1); // select all
																// objects in
																// layer
							}
						}
					});
		}

	}

	private void addPropertiesItem() {
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.gere().getSafeUri()
								.asString()
						: GuiResources.INSTANCE.menu_icon_options().getSafeUri()
								.asString(),
				!app.getKernel().isEmpty() ? loc.getMenu("Properties")
						: app.isUnbundled() ? loc.getMenu("Settings")
								: loc.getMenu("Options") + " ...",
				true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getDialogManager()
								.showPropertiesDialog(OptionType.OBJECTS, null);
					}
				});

	}

	private void addCopy() {
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.copy_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE.menu_icon_edit_copy()
								.getSafeUri().asString(),
				loc.getMenu("Copy"), true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						if (!selection.getSelectedGeos().isEmpty()) {
							app.setWaitCursor();
							app.getCopyPaste().copyToXML(app,
									selection.getSelectedGeos(), false);
							initActions(); // app.updateMenubar(); - it's
											// needn't to
											// update the all menubar here
							app.setDefaultCursor();
						}
					}
				});

	}

	private void addUndoRedo() {


		// undo menu
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.
								undo_black().getSafeUri().asString()
						: GuiResources.INSTANCE
				.menu_icon_edit_undo().getSafeUri().asString(),
				loc.getMenu("Undo"), true), true, new MenuCommand(app) {

					@Override
					public void execute() {
						if (app.getKernel().undoPossible()) {
							app.getGuiManager().undo();
						}
					}
				});

		// redo menu
		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.redo_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE
				.menu_icon_edit_redo().getSafeUri().asString(),
				loc.getMenu("Redo"), true), true, new MenuCommand(app) {

					@Override
					public void execute() {
						if (app.getKernel().redoPossible()) {
							app.getGuiManager().redo();
						}
					}
				});


	}

	/**
	 * Make sure next update() rebuilds the UI
	 */
	public void invalidate(){
		if (app.isMenuShowing()) {
			this.valid = true;
			this.initActions();
		} else {
			this.valid = false;
		}
	}

	/**
	 * Rebuild the UI if invalid
	 */
	public void update() {
		if (!valid) {
			valid = true;
			this.initActions();
		}

	}

}

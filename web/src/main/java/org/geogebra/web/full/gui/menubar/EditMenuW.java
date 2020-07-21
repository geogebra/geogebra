package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.Clipboard;
import org.geogebra.web.resources.SVGResource;

/**
 * The "Edit" menu.
 */
public class EditMenuW extends Submenu {

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
		super("edit", app);
		addExpandableStyleWithColor(false);
		this.loc = app.getLocalization();
		this.selection = getApp().getSelectionManager();
		initActions();
	}

	/**
	 * initializes the menu
	 */
	void initActions() {
		/*
		 * layer values: -1 means nothing selected -2 means different layers
		 * selected
		 */
		int layer = selection.getSelectedLayer();
		boolean justCreated = !(getApp().getActiveEuclidianView()
		        .getEuclidianController().getJustCreatedGeos().isEmpty());
		boolean haveSelection = !selection.getSelectedGeos().isEmpty();
		clearItems();
		if (getApp().isUndoRedoEnabled()) {
			addUndoRedo();
			// separator
			addSeparator();
		}
		if (Clipboard.isCopyImageToClipboardAvailable()) {
			addCopyToClipboard();
		}
		addCopy();
		addPasteItem();
		addSeparator();
		// object properties menu
		addPropertiesItem();
		addSeparator();

		addSelectAllItem();
		if (!getApp().isUnbundledOrWhiteboard()) {
			addSelectCurrentLayer();
			addDescentdantsItem();
			addPredecessorsItem();
			if (haveSelection) {
				addSeparator();
				// invert selection menu
				addItem(MainMenu.getMenuBarHtmlEmptyIcon(
						loc.getMenu("InvertSelection")), true,
						new MenuCommand(getApp()) {

				        @Override
				        public void doExecute() {
					        selection.invertSelection();
				        }
			        });
			}
			if (layer != -1) {
				addShowHideItem();
				addShowHideLabelsItem();
			}
			// Delete menu
			if (layer != -1 || justCreated) {
				addSeparator();
				addDeleteItem();
			}
		}
	}

	private void addShowHideLabelsItem() {
		addItem(MainMenu.getMenuBarHtmlEmptyIcon(loc.getMenu("ShowHideLabels")),
				true,
				new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						selection.showHideSelectionLabels();
					}
				});
	}

	private void addShowHideItem() {
		addItem(MainMenu.getMenuBarHtmlEmptyIcon(loc.getMenu("ShowHide")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						selection.showHideSelection();
					}
				});
	}

	private void addDeleteItem() {
		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.delete_black(),
				loc.getMenu("Delete")), true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						getApp().deleteSelectedObjects(false);
					}
				});
	}

	private void addPasteItem() {
		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.paste_black(),
				loc.getMenu("Paste")), true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						getApp().setWaitCursor();
						getApp().getCopyPaste().pasteFromXML(getApp());
						getApp().setDefaultCursor();
					}
				});
	}

	private void addCopyToClipboard() {
		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.copy_black(),
				loc.getMenu("DrawingPadToClipboard")), true,
				new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						app.copyGraphicsViewToClipboard();
						ToolTipManagerW.sharedInstance().showBottomMessage(
								loc.getMenu("GraphicsViewCopiedToClipboard"),
								false, app);
					}
				});
	}

	private void addSelectAllItem() {
		// select all menu
		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.select_all_black(),
				loc.getMenu("SelectAll")), true,
				new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						if (!getApp().getKernel().isEmpty()) {
							selection.selectAll(-1);
						}
					}
				});
	}

	private void addPredecessorsItem() {
		if (selection.hasPredecessors()) {
			// select ancestors menu
			addItem(MainMenu
					.getMenuBarHtmlEmptyIcon(loc.getMenu("SelectAncestors")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							selection.selectAllPredecessors();
						}
					});
		}
	}

	private void addDescentdantsItem() {
		if (selection.hasDescendants()) {
			// select descendants menu
			addItem(MainMenu.getMenuBarHtmlEmptyIcon(
					loc.getMenu("SelectDescendants")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							selection.selectAllDescendants();
						}
					});
		}
	}

	private void addSelectCurrentLayer() {
		if (selection.getSelectedLayer() >= 0
				&& getApp().getMaxLayerUsed() > 0) {
			addItem(MainMenu.getMenuBarHtmlEmptyIcon(
					loc.getMenu("SelectCurrentLayer")), true,
					new MenuCommand(getApp()) {

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
		addItem(MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.gear(),
				!getApp().getKernel().isEmpty() ? loc.getMenu("Properties")
						: getApp().isUnbundledOrWhiteboard()
								? loc.getMenu("Settings")
										: loc.getMenu("Options") + " ..."),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						getApp().getDialogManager()
								.showPropertiesDialog(OptionType.OBJECTS, null);
					}
				});
	}

	private void addCopy() {
		addItem(MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.copy_black(),
						loc.getMenu("Copy")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						if (!selection.getSelectedGeos().isEmpty()) {
							getApp().setWaitCursor();
							getApp().getCopyPaste().copyToXML(getApp(),
									selection.getSelectedGeos());
							initActions(); // getApp().updateMenubar(); - it's
											// needn't to
											// update the all menubar here
							getApp().setDefaultCursor();
						}
					}
				});
	}

	private void addUndoRedo() {
		// undo menu
		addItem(MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.undo_black(),
						loc.getMenu("Undo")),
				true, new MenuCommand(getApp()) {

					@Override
					public void execute() {
						if (getApp().getKernel().undoPossible()) {
							getApp().getGuiManager().undo();
						}
					}
				});
		// redo menu
		addItem(MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.redo_black(),
						loc.getMenu("Redo")),
				true, new MenuCommand(getApp()) {

					@Override
					public void execute() {
						if (getApp().getKernel().redoPossible()) {
							getApp().getGuiManager().redo();
						}
					}
				});
	}

	/**
	 * Make sure next update() rebuilds the UI
	 */
	public void invalidate() {
		if (getApp().isMenuShowing()) {
			this.valid = true;
			this.initActions();
		} else {
			this.valid = false;
		}
	}

	/**
	 * Rebuild the UI if invalid
	 */
	@Override
	public void update() {
		if (!valid) {
			valid = true;
			this.initActions();
		}
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.edit_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Edit";
	}
}

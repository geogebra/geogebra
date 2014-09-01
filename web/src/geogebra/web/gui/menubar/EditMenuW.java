package geogebra.web.gui.menubar;

import geogebra.common.main.OptionType;
import geogebra.common.main.SelectionManager;
import geogebra.common.util.CopyPaste;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * The "Edit" menu.
 */
public class EditMenuW extends GMenuBar {

	/**
	 * Application instance
	 */
	final AppW app;
	final SelectionManager selection;

	InsertImageMenuW iim;

	/**
	 * Constructs the "Edit" menu
	 * @param app Application instance
	 */
	public EditMenuW(AppW app) {

		super(true, new MenuResources());
		this.app = app;
		this.selection = app.getSelectionManager();
		addStyleName("GeoGebraMenuBar");
		iim = new InsertImageMenuW(app);
		initActions();
	}

	void initActions() {
		
		String noIcon = AppResources.INSTANCE.empty().getSafeUri().asString();
		/* layer values:
		 *  -1 means nothing selected
		 *  -2 means different layers selected
		 */
		int layer = selection.getSelectedLayer();	
		boolean justCreated = !(app.getActiveEuclidianView().getEuclidianController().getJustCreatedGeos().isEmpty());
		boolean haveSelection = !selection.getSelectedGeos().isEmpty();
		
		
		

		clearItems();
		
		if(app.getLAF().undoRedoSupported()){
			addUndoRedo();
			// separator
			addSeparator();
		}

		
			
		// copy menu
		if (!selection.getSelectedGeos().isEmpty())
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_copy().getSafeUri().asString(), app.getMenu("Copy"), true),
			        true, new Command() {
				        public void execute() {
					        app.setWaitCursor();
					        CopyPaste.copyToXML(app, selection.getSelectedGeos(), false);
					        initActions(); //app.updateMenubar(); - it's needn't to update the all menubar here
					        app.setDefaultCursor();
				        }
			        });
		else
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_copy().getSafeUri().asString(), app.getMenu("Copy"), false),
			        true, new Command() {
				        public void execute() {
					        // do nothing
				        }
			        });

		// paste menu
		if (!CopyPaste.isEmpty())
			addItem(MainMenu
			        .getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_paste().getSafeUri().asString(), app.getMenu("Paste"), true),
			        true, new Command() {
				        public void execute() {
					        app.setWaitCursor();
					        CopyPaste.pasteFromXML(app, false);
					        app.setDefaultCursor();
				        }
			        });
		else
			addItem(MainMenu
			        .getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_paste().getSafeUri().asString(), app.getMenu("Paste"), false),
			        true, new Command() {
				        public void execute() {
					        // do nothing
				        }
			        });
		if(app.getLAF().copyToClipboardSupported()){
		// copy graphics view menu
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_copy().getSafeUri().asString(), app.getMenu("CopyImage"), true),
		        true, new Command() {
			        public void execute() {
				        app.copyEVtoClipboard();
			        }
		        });
		}
		// separator
		addSeparator();

	
		addItem(MainMenu.getMenuBarHtml(noIcon, app.getMenu("InsertImageFrom"), true),
		        true, iim);
		
		addSeparator();

		// object properties menu
		if (!app.isApplet()){
			
				addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_options().getSafeUri().asString(), 
						!app.getKernel().isEmpty() ? app.getPlain("Properties") : app.getMenu("Options")
					+ " ...", true),
			        true, new Command() {
				        public void execute() {
				        	app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, null);
				        }
			        });
				
				addSeparator();
			
		}
		
		// select all menu
		if (!app.getKernel().isEmpty())
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAll"), true), true, new Command() {
				public void execute() {
					selection.selectAll(-1);
				}
			});
		else
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAll"), false), true, new Command() {
				public void execute() {
					// do nothing
				}
			});
		
		//select current layer menu
		if(selection.getSelectedLayer() >= 0 && app.getMaxLayerUsed()>0){
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectCurrentLayer"), true), true, new Command() {
				public void execute() {
					int layer1 = selection.getSelectedLayer();
					if (layer1 != -1)
						selection.selectAll(layer1); // select all objects in layer
				}
			});			
		}
		
		if(selection.hasDescendants()){
			//select descendants menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectDescendants"), true), true, new Command() {
				public void execute() {
					selection.selectAllDescendants();
				}
			});
		}
		
		if(selection.hasPredecessors()){
			//select ancestors menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAncestors"), true), true, new Command() {
				public void execute() {
					selection.selectAllPredecessors();
				}
			});
			
		}
			
		if(haveSelection){
			addSeparator();
			//invert selection menu
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("InvertSelection"), true), true, new Command() {
				public void execute() {
					selection.invertSelection();
				}
			});
		}
		
		//show/hide objects and show/hide labels menus
		if (layer != -1){
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("ShowHide"), true), true, new Command() {
				public void execute() {
					selection.showHideSelection();
				}
			});	
			
			addItem(MainMenu.getMenuBarHtml(noIcon,
			        app.getMenu("ShowHideLabels"), true), true, new Command() {
				public void execute() {
					selection.showHideSelectionLabels();
				}
			});
		}
		
		//Delete menu
		if (layer != -1 || justCreated){
			addSeparator();
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_delete().getSafeUri().asString(),
			        app.getMenu("Delete"), true), true, new Command() {
				public void execute() {
					app.deleteSelectedObjects();
				}
			});
		}
		

	}

	private void addUndoRedo() {
		// undo menu
				if (app.getKernel().undoPossible())
					addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_undo().getSafeUri().asString(), app.getMenu("Undo"), true),
					        true, new Command() {
						        public void execute() {
							        app.getGuiManager().undo();
						        }
					        });
				else
					addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_undo().getSafeUri().asString(), app.getMenu("Undo"), false),
					        true, new Command() {
						public void execute() {
							// do nothing
						}
					});
				


				// redo menu
				if (app.getKernel().redoPossible())
					addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_redo().getSafeUri().asString(), app.getMenu("Redo"), true),
					        true, new Command() {
						        public void execute() {
							        app.getGuiManager().redo();
						        }
					        });
				else
					addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_edit_redo().getSafeUri().asString(), app.getMenu("Redo"), false),
					        true, new Command() {
						public void execute() {
							// do nothing
						}
					});
    }
	
}

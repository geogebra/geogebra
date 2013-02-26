package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.main.SelectionManager;
import geogebra.common.util.CopyPaste;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Edit" menu.
 */
public class EditMenuW extends MenuBar {

	/**
	 * Application instance
	 */
	final App app;
	final SelectionManager selection;

	InsertImageMenuW iim;

	/**
	 * Constructs the "Edit" menu
	 * @param app Application instance
	 */
	public EditMenuW(App app) {

		super(true);
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
		
		// undo menu
		if (app.getKernel().undoPossible())
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
			        .edit_undo().getSafeUri().asString(), app.getMenu("Undo")),
			        true, new Command() {
				        public void execute() {
					        app.getGuiManager().undo();
				        }
			        });
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE
			        .edit_undo().getSafeUri().asString(), app.getMenu("Undo")),
			        true, new Command() {
				public void execute() {
					// do nothing
				}
			});


		// redo menu
		if (app.getKernel().redoPossible())
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
			        .edit_redo().getSafeUri().asString(), app.getMenu("Redo")),
			        true, new Command() {
				        public void execute() {
					        app.getGuiManager().redo();
				        }
			        });
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE
			        .edit_redo().getSafeUri().asString(), app.getMenu("Redo")),
			        true, new Command() {
				public void execute() {
					// do nothing
				}
			});

		// separator
		addSeparator();
			
		// copy menu
		if (!selection.getSelectedGeos().isEmpty())
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
			        .edit_copy().getSafeUri().asString(), app.getMenu("Copy")),
			        true, new Command() {
				        public void execute() {
					        app.setWaitCursor();
					        CopyPaste.copyToXML(app, selection.getSelectedGeos());
					        app.updateMenubar();
					        app.setDefaultCursor();
				        }
			        });
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE
			        .edit_copy().getSafeUri().asString(), app.getMenu("Copy")),
			        true, new Command() {
				        public void execute() {
					        // do nothing
				        }
			        });

		// paste menu
		if (!CopyPaste.isEmpty())
			addItem(GeoGebraMenubarW
			        .getMenuBarHtml(AppResources.INSTANCE.edit_paste()
			                .getSafeUri().asString(), app.getMenu("Paste")),
			        true, new Command() {
				        public void execute() {
					        app.setWaitCursor();
					        CopyPaste.pasteFromXML(app);
					        app.setDefaultCursor();
				        }
			        });
		else
			addItem(GeoGebraMenubarW
			        .getMenuBarHtmlGrayout(AppResources.INSTANCE.edit_paste()
			                .getSafeUri().asString(), app.getMenu("Paste")),
			        true, new Command() {
				        public void execute() {
					        // do nothing
				        }
			        });

		// copy graphics view menu
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .edit_copy().getSafeUri().asString(), app.getMenu("CopyImage")),
		        true, new Command() {
			        public void execute() {
				        ((AppW) app).copyEVtoClipboard();
			        }
		        });

		// separator
		addSeparator();

		addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon, app.getMenu("InsertImageFrom")),
		        true, iim);

		addSeparator();

		// select all menu
		if (!app.getKernel().isEmpty())
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAll")), true, new Command() {
				public void execute() {
					selection.selectAll(-1);
				}
			});
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(noIcon,
			        app.getMenu("SelectAll")), true, new Command() {
				public void execute() {
					// do nothing
				}
			});
		
		//select current layer menu
		if(selection.getSelectedLayer() >= 0){
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("SelectCurrentLayer")), true, new Command() {
				public void execute() {
					int layer1 = selection.getSelectedLayer();
					if (layer1 != -1)
						selection.selectAll(layer1); // select all objects in layer
				}
			});			
		}
		
		if(haveSelection){
			//select descendants menu
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("SelectDescendants")), true, new Command() {
				public void execute() {
					selection.selectAllDescendants();
				}
			});
		
			//select ancestors menu
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("SelectAncestors")), true, new Command() {
				public void execute() {
					selection.selectAllPredecessors();
				}
			});
			
			addSeparator();
			
			//invert selection menu
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("InvertSelection")), true, new Command() {
				public void execute() {
					selection.invertSelection();
				}
			});
		}
		
		//show/hide objects and show/hide labels menus
		if (layer != -1){
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("ShowHide")), true, new Command() {
				public void execute() {
					selection.showHideSelection();
				}
			});	
			
			addItem(GeoGebraMenubarW.getMenuBarHtml(noIcon,
			        app.getMenu("ShowHideLabels")), true, new Command() {
				public void execute() {
					selection.showHideSelectionLabels();
				}
			});
		}
		
		//Delete menu
		if (layer != -1 || justCreated){
			addSeparator();
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
			        .edit_copy().getSafeUri().asString(),
			        app.getMenu("Delete")), true, new Command() {
				public void execute() {
					app.deleteSelectedObjects();
				}
			});
		}
		

	}
	
}

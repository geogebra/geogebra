package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Context menu for spreadsheet cells, rows and columns
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuW extends GPopupMenuW {
	private static final long serialVersionUID = -7749575525048631798L;

	/** menu background color */
	final static GColor bgColor = GColor.white;

	/** menu foreground color */
	final static GColor fgColor = GColor.black;

	/** spreadsheet table */
	protected MyTableW table = null;

	/** minimum selected row */
	protected int row1 = -1;

	/** maximum selected row */
	protected int row2 = -1;

	/** minimum selected column */
	protected int column1 = -1;

	/** maximum selected column */
	protected int column2 = -1;

	/** list of selected cell ranges */
	protected ArrayList<CellRange> selectedCellRanges;
	private int selectionType;

	/** application */
	protected AppW app;

	/** menu spreadsheet View */
	protected SpreadsheetViewW view;

	private ArrayList<GeoElement> geos;

	/** spreadsheet cell range processor */
	protected CellRangeProcessor cp;

	// for testing
	private boolean isShiftDown = false;

	/************************************************
	 * Constructor
	 * 
	 * @param table
	 * @param isShiftDown
	 */
	public SpreadsheetContextMenuW(MyTableW table, boolean isShiftDown) {
		super((AppW)table.getApplication());

		this.table = table;
		app = (AppW) table.kernel.getApplication();
		cp = table.getCellRangeProcessor();
		view = (SpreadsheetViewW)table.getView();

		column1 = table.selectedCellRanges.get(0).getMinColumn();
		column2 = table.selectedCellRanges.get(0).getMaxColumn();
		row1 = table.selectedCellRanges.get(0).getMinRow();
		row2 = table.selectedCellRanges.get(0).getMaxRow();
		selectionType = table.getSelectionType();
		selectedCellRanges = table.selectedCellRanges;
		geos = app.getSelectionManager().getSelectedGeos();

		getPopupPanel().addStyleName("geogebraweb-popup-spreadsheet");

		// other way
		// getPopupMenu().getElement().getStyle().setBackgroundColor(bgColor.toString());
		// z-index should be greater than 7 (blue dot) and 6 (selection frame)
		// getPopupPanel().getElement().getStyle().setZIndex(9);

		this.isShiftDown = isShiftDown;

		if (isShiftDown) {
			// InspectorView id = new InspectorView(app);
			// id.setVisible(true);
		}

		initMenu();
	}

	protected void initMenu() {

		MenuItem item = null;//new MenuItem();
		GCheckBoxMenuItem cbItem = null;//new GCheckBoxMenuItem();
		//JMenu subMenu = null;//new JMenu();// Menubar

		setTitle(getTitleString());

		// ===============================================
		// Show Object, Show Label
		// ===============================================

		if (!isEmptySelection()) {
			addSeparator();
			final GeoElement geo = geos.get(0);
			if (geo.isEuclidianShowable()
					&& geo.getShowObjectCondition() == null
					&& (!geo.isGeoBoolean() || geo.isIndependent())) {

				cbItem = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.mode_showhideobject_16().getSafeUri().asString(), app.getPlain("ShowObject")), new Command() {

					public void execute() {
						for (int i = geos.size() - 1; i >= 0; i--) {
							GeoElement geo1 = geos.get(i);
							geo1.setEuclidianVisible(!geo1
									.isSetEuclidianVisible());
							geo1.updateRepaint();

						}
						app.storeUndoInfo();
					}
				});
        		cbItem.setSelected(geo.isSetEuclidianVisible());
        		addItem(cbItem);
			}

			// Show Label

			if (geo.isLabelShowable()) {

        		cbItem = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.mode_showhidelabel_16().getSafeUri().asString(), app.getPlain("ShowLabel")), new Command() {
					
					public void execute() {
						for (int i = geos.size() - 1; i >= 0; i--) {
							GeoElement geo1 = geos.get(i);
							geo1.setLabelVisible(!geo1.isLabelVisible());
							geo1.updateRepaint();
						}
						app.storeUndoInfo();				
					}
				});
        		cbItem.setSelected(geo.isLabelVisible());
        		addItem(cbItem);
			}

			// Trace to spreadsheet

			if (geo.isSpreadsheetTraceable()
					&& selectionType != MyTable.ROW_SELECT) {

				/*
				boolean showRecordToSpreadsheet = true;
				// check if other geos are recordable
				for (int i = 1; i < geos.size() && showRecordToSpreadsheet; i++)
					showRecordToSpreadsheet &= geos.get(i)
							.isSpreadsheetTraceable();

				if (showRecordToSpreadsheet) {

	        		cbItem = new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.spreadsheettrace().getSafeUri().asString(), app.getMenu("RecordToSpreadsheet")), new Command() {

						public void execute() {

							GeoElement geoRecordToSpreadSheet;
							if (geos.size() == 1)
								geoRecordToSpreadSheet = geo;
							else {
								geoRecordToSpreadSheet = app.getKernel().getAlgoDispatcher().List(
										null, geos, false);
								geoRecordToSpreadSheet.setAuxiliaryObject(true);
							}

							((GuiManagerW)app.getGuiManager())
									.getSpreadsheetView()
									.showTraceDialog(geoRecordToSpreadSheet,
											null);
						}
					});
	        		cbItem.setSelected(geo.getSpreadsheetTrace());
	        		addItem(cbItem);
				}
				*/
			}

		}

		// ===============================================
		// Cut-Copy-Paste-Delete
		// ===============================================

		addSeparator();

		item = addAction(new Command() {

			public void execute() {
				table.copyPasteCut.copy(column1, row1, column2, row2, false);
			}
		}, GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.edit_copy().getSafeUri().asString(), app.getMenu("Copy")), app.getMenu("Copy"));
		item.setEnabled(!isEmptySelection());


		item = addAction(new Command() {

			public void execute() {
				boolean succ = table.copyPasteCut.paste(column1, row1, column2,
						row2);
				if (succ)
					app.storeUndoInfo();
				table.getView().rowHeaderRevalidate();
			}
		}, GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.edit_paste().getSafeUri().asString(), app.getMenu("Paste")), app.getMenu("Paste"));


		item = addAction(new Command() {

			public void execute() {
				boolean succ = table.copyPasteCut.cut(column1, row1, column2,
						row2);
				if (succ)
					app.storeUndoInfo();
			}
		}, GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.edit_cut().getSafeUri().asString(), app.getMenu("Cut")), app.getMenu("Cut"));
		item.setEnabled(!isEmptySelection());


		item = addAction(new Command() {

			public void execute() {
				boolean succ = table.copyPasteCut.delete(column1, row1,
						column2, row2);
				if (succ)
					app.storeUndoInfo();
			}
		}, GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.delete_small().getSafeUri().asString(), app.getPlain("Delete")), app.getPlain("Delete"));
		item.setEnabled(!allFixed());

		/* should port this later
		
		addSeparator();

		*/

		// ===============================================
		// Insert (new row or new column)
		// ===============================================
		if (selectionType == MyTable.COLUMN_SELECT
				|| selectionType == MyTable.ROW_SELECT) {

			/* should port submenus later

			subMenu = new JMenu(app.getPlain("Insert") + " ...");
			subMenu.setIcon(app.getEmptyIcon());
			addItem(subMenu);

			if (selectionType == MyTable.COLUMN_SELECT) {
				item = new JMenuItem(app.getMenu("InsertLeft"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertLeft(column1, column2);
					}
				});
				addSubItem(subMenu, item);

				item = new JMenuItem(app.getMenu("InsertRight"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertRight(column1, column2);
					}
				});
				addSubItem(subMenu, item);

			}

			if (selectionType == MyTable.ROW_SELECT) {
				item = new JMenuItem(app.getMenu("InsertAbove"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertAbove(row1, row2);
					}
				});
				addSubItem(subMenu, item);

				item = new JMenuItem(app.getMenu("InsertBelow"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertBelow(row1, row2);
					}
				});
				addSubItem(subMenu, item);
			}

			*/
		}

		// ===============================================
		// Create (Lists, Matrix, etc.)
		// ===============================================

		if (!isEmptySelection()) {

			/* should port submenus later

			subMenu = new JMenu(app.getMenu("Create"));
			subMenu.setIcon(app.getEmptyIcon());
			addItem(subMenu);

			item = new JMenuItem(app.getMenu("List"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cp.createList(selectedCellRanges, true, false);
				}
			});
			addSubItem(subMenu, item);

			item = new JMenuItem(app.getMenu("ListOfPoints"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoElement newGeo = cp.createPointGeoList(
							selectedCellRanges, false, true, true, true, true);
					app.getKernel()
							.getConstruction()
							.addToConstructionList(newGeo.getParentAlgorithm(),
									true);
					newGeo.setLabel(null);
				}
			});
			addSubItem(subMenu, item);
			item.setEnabled((cp.isCreatePointListPossible(selectedCellRanges)));

			item = new JMenuItem(app.getMenu("Matrix"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cp.createMatrix(column1, column2, row1, row2, false);
				}
			});
			addSubItem(subMenu, item);
			item.setEnabled(cp.isCreateMatrixPossible(selectedCellRanges));

			item = new JMenuItem(app.getMenu("Table"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cp.createTableText(column1, column2, row1, row2, false,
							false);
				}
			});
			addSubItem(subMenu, item);
			item.setEnabled(cp.isCreateMatrixPossible(selectedCellRanges));

			item = new JMenuItem(app.getMenu("PolyLine"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoElement newGeo = cp.createPolyLine(selectedCellRanges,
							false, true);
					app.getKernel()
							.getConstruction()
							.addToConstructionList(newGeo.getParentAlgorithm(),
									true);
					newGeo.setLabel(null);
				}
			});
			addSubItem(subMenu, item);
			item.setEnabled((cp.isCreatePointListPossible(selectedCellRanges)));

			item = new JMenuItem(app.getMenu("OperationTable"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cp.createOperationTable(selectedCellRanges.get(0), null);
				}
			});
			addSubItem(subMenu, item);
			item.setEnabled(cp
					.isCreateOperationTablePossible(selectedCellRanges));

			*/
		}

		// ===============================================
		// Import Data
		// ===============================================

		if (isEmptySelection() /*&& AppW.hasFullPermissions()*/) {

			/* should port this later
			
			item = new JMenuItem(app.getMenu("ImportDataFile") + " ...");
			item.setIcon(app.getImageIcon("document-open.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File dataFile = app.getGuiManager().getDataFile();
					if (dataFile != null)
						table.getView().loadSpreadsheetFromURL(dataFile);
				}
			});
			addItem(item);

			*/
		}

		// ===============================================
		// Show Browser / Spreadsheet Options
		// ===============================================

		if (isEmptySelection()) {

			/* should port submenus later


			addSeparator();

			subMenu = new JMenu(app.getPlain("Show"));
			subMenu.setIcon(app.getEmptyIcon());
			// addItem(subMenu);

			cbItem = new JCheckBoxMenuItem(app.getMenu("FileBrowser"));
			// cbItem.setIcon(app.getEmptyIcon());
			cbItem.setSelected(view.getShowBrowserPanel());
			cbItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					view.setShowFileBrowser(!view.getShowBrowserPanel());
				}
			});
			addSubItem(subMenu, cbItem);

			item = new JMenuItem(app.getMenu("SpreadsheetOptions") + "...",
					app.getImageIcon("view-properties16.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
			    	app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
			    	app.getGuiManager().setFocusedPanel(App.VIEW_SPREADSHEET, true);
				}
			});
			addItem(item);

			*/
		}

		// ===============================================
		// Object properties
		// ===============================================

		/* should port this later

		if (app.getSelectionManager().selectedGeosSize() > 0 && app.letShowPropertiesDialog()) {
			addSeparator();

			item = addAction(
					new Command() {

						public void execute() {
							app.getDialogManager().showPropertiesDialog();
						}
					},
					GeoGebraMenubarW.getMenuBarHtml(
							AppResources.INSTANCE.view_properties16().getSafeUri().asString(),
							app.getMenu(app.getPlain("Properties")) + "..."),
					app.getMenu(app.getPlain("Properties")) + "...");
		}

		app.setComponentOrientation(this);

		*/
	}

	private String getTitleString() {

		// title = cell range if empty or multiple cell selection
		String title = GeoElementSpreadsheet.getSpreadsheetCellName(column1,
				row1);
		if (column1 != column2 || row1 != row2) {
			title += ":"
					+ GeoElementSpreadsheet.getSpreadsheetCellName(column2,
							row2);
		}
		// title = geo description if single geo in cell
		else if (geos != null && geos.size() == 1) {
			GeoElement geo0 = geos.get(0);
			title = geo0.getLongDescriptionHTML(false, true);
			if (title.length() > 80)
				title = geo0.getNameDescriptionHTML(false, true);
		}

		return title;
	}

	// setTitle (copied from gui.ContextMenuGeoElement)
	private void setTitle(String str) {

	    MenuItem title = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), str),
	    		true, new Command() {
					
					public void execute() {
						setVisible(false);
					}
				});
	    title.addStyleName("menuTitle");
	    addItem(title);

	    /* instead of this, implementation is copied from geogebra.web.gui.ContextMenuGeoElementW

		JLabel title = new JLabel(str);
		title.setFont(app.getBoldFont());
		title.setBackground(bgColor);
		title.setForeground(fgColor);

		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));
		add(title);

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});

		*/
	}

	protected MenuItem addAction(Command action, String html, String text) {
		MenuItem mi;
	    if (html != null) {
	    	mi = new MenuItem(html, true, action);
	    	mi.addStyleName("mi_with_image"); //TEMP
	    } else {
	    	mi = new MenuItem(text, action);
	    	mi.addStyleName("mi_no_image"); //TEMP
	    }
	    
	    addItem(mi);
	    return mi; //TODO: need we this?
		//return wrappedPopup.add(action, html, text);
    }

	/* should port one of these later

	private void addItem(Component mi) {
		mi.setBackground(bgColor);
		add(mi);
	}

	private static void addSubItem(JMenu menu, Component mi) {
		mi.setBackground(bgColor);
		menu.add(mi);
	}

	*/

	private boolean allFixed() {
		boolean allFixed = true;
		if (geos != null && geos.size() > 0) {
			for (int i = 0; (i < geos.size() && allFixed); i++) {
				GeoElement geo = geos.get(i);
				if (!geo.isFixed())
					allFixed = false;
			}
		}
		return allFixed;
	}

	private boolean isEmptySelection() {
		return (app.getSelectionManager().getSelectedGeos().isEmpty());
	}

}

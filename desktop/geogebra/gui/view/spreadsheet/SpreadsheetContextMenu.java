package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Context menu for spreadsheet cells, rows and columns
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenu extends JPopupMenu {
	private static final long serialVersionUID = -7749575525048631798L;

	/** menu background color */
	final static Color bgColor = Color.white;

	/** menu foreground color */
	final static Color fgColor = Color.black;

	/** spreadsheet table */
	protected MyTableD table = null;

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
	protected AppD app;

	/** menu spreadsheet View */
	protected SpreadsheetView view;

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
	public SpreadsheetContextMenu(MyTableD table, boolean isShiftDown) {

		this.table = table;
		app = (AppD) table.kernel.getApplication();
		cp = table.getCellRangeProcessor();
		view = table.getView();

		column1 = table.selectedCellRanges.get(0).getMinColumn();
		column2 = table.selectedCellRanges.get(0).getMaxColumn();
		row1 = table.selectedCellRanges.get(0).getMinRow();
		row2 = table.selectedCellRanges.get(0).getMaxRow();
		selectionType = table.getSelectionType();
		selectedCellRanges = table.selectedCellRanges;
		geos = app.getSelectedGeos();

		setBackground(bgColor);

		this.isShiftDown = isShiftDown;

		if (isShiftDown) {
			// InspectorView id = new InspectorView(app);
			// id.setVisible(true);
		}

		initMenu();
	}

	protected void initMenu() {

		JMenuItem item = new JMenuItem();
		JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem();
		JMenu subMenu = new JMenu();

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
				cbItem = new JCheckBoxMenuItem(app.getPlain("ShowObject"));
				cbItem.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
				cbItem.setSelected(geo.isSetEuclidianVisible());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = geos.size() - 1; i >= 0; i--) {
							GeoElement geo1 = geos.get(i);
							geo1.setEuclidianVisible(!geo1
									.isSetEuclidianVisible());
							geo1.updateRepaint();

						}
						app.storeUndoInfo();
					}
				});
				addItem(cbItem);
			}

			// Show Label

			if (geo.isLabelShowable()) {
				// show label
				cbItem = new JCheckBoxMenuItem(app.getPlain("ShowLabel"));
				cbItem.setSelected(geo.isLabelVisible());
				cbItem.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = geos.size() - 1; i >= 0; i--) {
							GeoElement geo1 = geos.get(i);
							geo1.setLabelVisible(!geo1.isLabelVisible());
							geo1.updateRepaint();

						}
						app.storeUndoInfo();
					}
				});
				addItem(cbItem);
			}

			// Trace to spreadsheet

			if (geo.isSpreadsheetTraceable()
					&& selectionType != MyTable.ROW_SELECT) {

				boolean showRecordToSpreadsheet = true;
				// check if other geos are recordable
				for (int i = 1; i < geos.size() && showRecordToSpreadsheet; i++)
					showRecordToSpreadsheet &= geos.get(i)
							.isSpreadsheetTraceable();

				if (showRecordToSpreadsheet) {
					cbItem = new JCheckBoxMenuItem(
							app.getMenu("RecordToSpreadsheet"));
					cbItem.setIcon(app.getImageIcon("spreadsheettrace.gif"));
					cbItem.setSelected(geo.getSpreadsheetTrace());

					cbItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							GeoElement geoRecordToSpreadSheet;
							if (geos.size() == 1)
								geoRecordToSpreadSheet = geo;
							else {
								geoRecordToSpreadSheet = app.getKernel().List(
										null, geos, false);
								geoRecordToSpreadSheet.setAuxiliaryObject(true);
							}

							app.getGuiManager()
									.getSpreadsheetView()
									.showTraceDialog(geoRecordToSpreadSheet,
											null);
						}
					});
					addItem(cbItem);
				}
			}

		}

		// ===============================================
		// Cut-Copy-Paste-Delete
		// ===============================================

		addSeparator();
		item = new JMenuItem(app.getMenu("Copy"),
				app.getImageIcon("edit-copy.png"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.copyPasteCut.copy(column1, row1, column2, row2, false);
			}
		});
		addItem(item);
		item.setEnabled(!isEmptySelection());

		item = new JMenuItem(app.getMenu("Paste"),
				app.getImageIcon("edit-paste.png"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean succ = table.copyPasteCut.paste(column1, row1, column2,
						row2);
				if (succ)
					app.storeUndoInfo();
				table.getView().getRowHeader().revalidate();
			}
		});
		addItem(item);

		item = new JMenuItem(app.getMenu("Cut"),
				app.getImageIcon("edit-cut.png"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean succ = table.copyPasteCut.cut(column1, row1, column2,
						row2);
				if (succ)
					app.storeUndoInfo();
			}
		});
		addItem(item);
		item.setEnabled(!isEmptySelection());

		item = new JMenuItem(app.getPlain("Delete"),
				app.getImageIcon("delete_small.gif"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean succ = table.copyPasteCut.delete(column1, row1,
						column2, row2);
				if (succ)
					app.storeUndoInfo();
			}
		});
		addItem(item);
		item.setEnabled(!allFixed());

		addSeparator();

		// ===============================================
		// Insert (new row or new column)
		// ===============================================
		if (selectionType == MyTable.COLUMN_SELECT
				|| selectionType == MyTable.ROW_SELECT) {
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
		}

		// ===============================================
		// Create (Lists, Matrix, etc.)
		// ===============================================

		if (!isEmptySelection()) {

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

		}

		// ===============================================
		// Import Data
		// ===============================================

		if (isEmptySelection() && AppD.hasFullPermissions()) {
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
		}

		// ===============================================
		// Show Browser / Spreadsheet Options
		// ===============================================

		if (isEmptySelection()) {
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
		}

		// ===============================================
		// Object properties
		// ===============================================

		if (app.selectedGeosSize() > 0 && app.letShowPropertiesDialog()) {
			addSeparator();
			item = new JMenuItem(app.getMenu(app.getPlain("Properties"))
					+ "...", app.getImageIcon("view-properties16.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.getDialogManager().showPropertiesDialog();
				}
			});
			addItem(item);
		}

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

	}

	private void addItem(Component mi) {
		mi.setBackground(bgColor);
		add(mi);
	}

	private static void addSubItem(JMenu menu, Component mi) {
		mi.setBackground(bgColor);
		menu.add(mi);
	}

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
		return (app.getSelectedGeos().isEmpty());
	}

}

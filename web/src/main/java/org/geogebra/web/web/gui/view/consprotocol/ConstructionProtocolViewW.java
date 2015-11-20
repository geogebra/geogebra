package org.geogebra.web.web.gui.view.consprotocol;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.javax.swing.GImageIconW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.export.PrintableW;
import org.geogebra.web.web.gui.layout.panels.ConstructionProtocolStyleBarW;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of ConstructionProtocol
 *
 */
public class ConstructionProtocolViewW extends ConstructionProtocolView
		implements SetLabels, SettingListener, PrintableW {

	/** contains a scrollPanel with the {@link #table constructionstep-table} **/
	public FlowPanel cpPanel;
	/** table with constructionsteps **/
	protected CellTable<RowData> table;
	private StyleBarW styleBar;
	/** possible drop index **/
	int minIndex;
	/** possible drop index **/
	int maxIndex;
	/** the dragged row **/
	protected Element draggedRow;
	/** index of dragged row **/
	protected int dragIndex;
	GPopupMenuW popupMenu;
	GCheckBoxMenuItem miShowOnlyBreakpoints = null;


	/**
	 * 
	 * @param app {@link AppW}
	 */
	public ConstructionProtocolViewW(final AppW app) {
		cpPanel = new FlowPanel();
		this.app = app;
		kernel = app.getKernel();
		data = new ConstructionTableDataW(this);

		app.getGuiManager().registerConstructionProtocolView(this);

		table = new CellTable<RowData>();
		table.addStyleName("cpTable");
				
//		first attempt with flextable
//		table = new FlexTable();
//		for (int k = 0; k < data.columns.length; k++) {
//			if ((data.columns[k].getTitle() == "number") ||
//					(data.columns[k].getTitle() == "name") ||
//					(data.columns[k].getTitle() == "definition"))
//				table.setText(0, k, data.columns[k].getTitle());
//		}		
//		
//		

		ScrollPanel scrollPane = new ScrollPanel(table);
		scrollPane.setStyleName("cpScrollPanel");
		cpPanel.add(scrollPane);
		
		addDragDropHandlers();
		if (app.has(Feature.CP_POPUP)) {
			this.addHeaderClickHandler();
		}
		
		ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
		settingsChanged(cps);
		cps.addListener(this);

		initGUI();
	}
	
	/**
	 * adds handlers for dragging rows. Overridden for touch. 
	 */
    protected void addDragDropHandlers() {
       	table.addDomHandler(new DragStartHandler() {
			
			@Override
			public void onDragStart(DragStartEvent event) {
				handleDrag(event.getNativeEvent().getClientY());
			}
		}, DragStartEvent.getType());
		
		table.addDomHandler(new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				if (draggedRow != null) {
					draggedRow.removeClassName("isDragging");
				}
				if (Window.getClientWidth()+event.getNativeEvent().getScreenX() > table.getElement().getAbsoluteRight() ||
						Window.getClientWidth()+event.getNativeEvent().getScreenX() < table.getElement().getAbsoluteLeft()) {
					return;
				}
				handleDrop(event.getNativeEvent().getClientY());
			}
		}, DragEndEvent.getType());
    }

    /**
	 * @param y coordinate of dragStart
	 */
    protected void handleDrag(int y) {
        for (int i = 0; i < table.getRowCount(); i++) {
			if (y > table.getRowElement(i).getAbsoluteTop() && y < table.getRowElement(i).getAbsoluteBottom()) {
				draggedRow = table.getRowElement(i);
				draggedRow.addClassName("isDragging");
				GeoElement geo = data.getRow(i).getGeo();
				dragIndex = geo.getConstructionIndex();
				minIndex = geo.getMinConstructionIndex();
				maxIndex = geo.getMaxConstructionIndex();
				return;
			}
		}
    }
    
    /**
     * @param y coordinate of dropTarget
     */
    protected void handleDrop(int y) {
    	for (int i = 0; i < table.getRowCount(); i++) {
    		if ((y > table.getRowElement(i).getAbsoluteTop() && y < table.getRowElement(i).getAbsoluteBottom()) ||
    				//dragEnd happens below the last row
    				(i == table.getRowCount()-1 && y > table.getRowElement(i).getAbsoluteBottom())) {
    			int dropIndex = data.getConstructionIndex(i);
    			if (dropIndex == dragIndex) {
    				//no changes necessary
    				return;
    			}
    			if (dropIndex < minIndex || dropIndex > maxIndex) {
    				//drop not possible
    				//TODO change cursor style before releasing mouse
					ToolTipManagerW.sharedInstance().showBottomMessage(
							"Drop not possible", true, (AppW) app);
    				return;
    			}
    			boolean kernelChanged = ((ConstructionTableDataW)data).moveInConstructionList(dragIndex, dropIndex);
    			if (kernelChanged) {
    				app.storeUndoInfo();
    			}
       			return;
    		}
    	}
    }

    public void setLabels(){
    	initGUI();
    }
    
	public CellTable<RowData> getTable() {
		return table;
	}

	void initGUI() {
		//remove all columns if there are
		int colCount = table.getColumnCount();
		for(int i=0; i<colCount; i++){
			table.removeColumn(0);
		}
		
		if (!app.has(Feature.CP_POPUP)) {  //old source inserted back here
			for (int i = 0; i < data.getColumnCount(); i++) {
				if (data.columns[i].isVisible()) {
					String title = data.columns[i].getTitle();
					Column<RowData, ?> col = getColumn(title);
					if (col != null) {
						table.addColumn(col, app.getPlain(title));
					}
				}
			}
			tableInit();
			rowCountChanged();
			return;
		}
				

		int lastVisibleColData = data.getColumnCount() - 1;
		
		if (app.has(Feature.CP_NEW_COLUMNS)) {
			while (!data.columns[lastVisibleColData].isVisible()) {
				lastVisibleColData--;
			}
		} else {
			while (!data.columns[lastVisibleColData].isVisible()
					|| "ToolbarIcon".equals(data.columns[lastVisibleColData]
							.getTitle())
					|| "Command".equals(data.columns[lastVisibleColData]
							.getTitle())
					|| "Caption".equals(data.columns[lastVisibleColData]
							.getTitle())
					|| "Breakpoint".equals(data.columns[lastVisibleColData]
							.getTitle())) {
				lastVisibleColData--;
			}
		}


		initPopupMenu();
		
		for (int i = 0; i < data.getColumnCount(); i++) {
			if (data.columns[i].isVisible()) {
				String title = data.columns[i].getTitle();
				Column<RowData, ?> col = getColumn(title);
				if (col != null) {
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					if (i == 0) {
						sb.append(SafeHtmlUtils
								.fromSafeConstant("<div id = \"CP_popupImage\">"));
						sb.append(AbstractImagePrototype.create(
								GuiResources.INSTANCE.menu_dots())
								.getSafeHtml());
						sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));

					} else {
						String headerTitle;
						if ("ToolbarIcon".equals(title)) {
							headerTitle = "Icon";
						} else if ("Breakpoint".equals(title)) {
							headerTitle = "Breakpoint.short";
						} else {
							headerTitle = title;
						}
						sb.append(SafeHtmlUtils.fromSafeConstant("<div>"
								+ app.getPlain(headerTitle) + "</div>"));
					}

					table.addColumn(col, sb.toSafeHtml());
				}
			}
		}
		tableInit();
		rowCountChanged();
	}

	protected void initPopupMenu() {

		popupMenu = new GPopupMenuW((AppW) app);
		ScheduledCommand com;

		if (app.getGuiManager().getLayout().getDockManager()
				.getNumberOfOpenViews() > 1) {
			popupMenu.addItem(app.getMenu("Close"), new ScheduledCommand() {

				public void execute() {
					app.getGuiManager().setShowView(false,
							App.VIEW_CONSTRUCTION_PROTOCOL);
					app.updateMenubar();
					((AppW) app).fireViewsChangedEvent();
				}

			});

			popupMenu.addVerticalSeparator();
		}

		// boolean hasColInPopup = false;
		for (int k = 1; k < data.getColumnCount(); k++) {
			ColumnData colData = data.getColumns()[k];
			// On web there is no all columns yet, so temporary must hide
			// some
			// column on stylebar too
			if (!"No.".equals(colData.getTitle())
					&& (!"ToolbarIcon".equals(colData.getTitle()) || app
							.has(Feature.CP_NEW_COLUMNS))
					&& (!"Command".equals(colData.getTitle()) || app
							.has(Feature.CP_NEW_COLUMNS))
					&& (!"Caption".equals(colData.getTitle()) || app
							.has(Feature.CP_NEW_COLUMNS))
					&& (!"Breakpoint".equals(colData.getTitle()) || app
							.has(Feature.CP_NEW_COLUMNS))) {
				final int j = k;
				com = new ScheduledCommand() {
					public void execute() {
						data.columns[j]
								.setVisible(!data.columns[j].isVisible());
						initGUI();
					}
				};

				GCheckBoxMenuItem columnItem = new GCheckBoxMenuItem(
						data.columns[j].getTranslatedTitle(), com, true);
				columnItem.setSelected(data.columns[j].isVisible());
				popupMenu.addItem(columnItem);
			}
		}

		popupMenu.addVerticalSeparator();

		miShowOnlyBreakpoints = new GCheckBoxMenuItem(
				app
				.getLocalization().getPlain("ShowOnlyBreakpoints"),
				new ScheduledCommand() {

					public void execute() {
						showOnlyBreakpointsAction();
						miShowOnlyBreakpoints.setSelected(app.getKernel()
								.getConstruction().showOnlyBreakpoints());
					}

				}, true);
		miShowOnlyBreakpoints.setSelected(app.getKernel().getConstruction()
				.showOnlyBreakpoints());
		popupMenu.addItem(miShowOnlyBreakpoints);

	}

	private void addHeaderClickHandler() {
		table.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				Element imgElement = DOM.getElementById("CP_popupImage")
						.getElementsByTagName("img").getItem(0);

				if (el.equals(imgElement)) { // three-dot menu
					popupMenu.show(new GPoint(el.getAbsoluteLeft(), el
							.getAbsoluteBottom()));

				}
			}

		}, ClickEvent.getType());
	}

	public Column<RowData, ?> getColumn(String title) {
		Column<RowData, ?> col = null;
		if ("No.".equals(title)) {
			col = getColumnId();
		} else if ("Name".equals(title)) {
			col = getColumnName();
		} else if ("ToolbarIcon".equals(title)) {
			if (app.has(Feature.CP_NEW_COLUMNS)) {
				col = getColumnToolbarIcon();
			}
		} else if ("Definition".equals(title)) {
			col = getColumnDefinition();
		} else if ("Command".equals(title)) {
			if (app.has(Feature.CP_NEW_COLUMNS)) {
				col = getColumnCommand();
			}
		} else if ("Value".equals(title)) {
			col = getColumnValue();
		} else if ("Caption".equals(title)) {
			if (app.has(Feature.CP_NEW_COLUMNS)) {
				col = getColumnCaptionSimple();
			}
		} else { // if ("Breakpoint".equals(title)) {
			if (app.has(Feature.CP_NEW_COLUMNS)) {
				col = getColumnBreakpoint();
			}
		}
		
		return col;

	}

	/*
	 * Add a number column to show the id.
	 */
	private static Column<RowData, Number> getColumnId() {
	    Cell<Number> idCell = new NumberCell();
	    Column<RowData, Number> idColumn = new Column<RowData, Number>(idCell) {
	      @Override
	      public Number getValue(RowData object) {
	        return object.getIndex();
	      }
	    };

		return idColumn;
		
	}


	private static class Base64ImageCell extends AbstractCell<String> {
		public Base64ImageCell() {
			super();
		}

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendHtmlConstant("<img src=\"" + value + "\" />");
			}
		}
	}

	/*
	 * Add a column to show the toolbar icon.
	 */
	private static Column<RowData, String> getColumnToolbarIcon() {
		Column<RowData, String> iconColumn = new Column<RowData, String>(
				new Base64ImageCell()) {

			@Override
			public String getValue(RowData object) {
				if (object.getToolbarIcon() == null) {
					return null;
				}
				return ((GImageIconW) object.getToolbarIcon()).getImpl();
			}
		};
		return iconColumn;
	}

	/*
	 * Add a text column to show the name.
	 */
	private static Column<RowData, SafeHtml> getColumnName() {
		Column<RowData, SafeHtml> nameColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {
			
			@Override
            public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getName());
			}

		};    
		return nameColumn;
	}


	/*
	 * Add a text column to show the definition.
	 */
	private static Column<RowData, SafeHtml> getColumnDefinition() {
		Column<RowData, SafeHtml> defColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {
			
			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getDefinition());
			}

		};
		return defColumn;
	}

	/*
	 * Add a text column to show the value.
	 */
	private static Column<RowData, SafeHtml> getColumnValue() {
		Column<RowData, SafeHtml> valColumn = new Column<RowData, SafeHtml>(
		        new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getAlgebra());
			}

		};
		return valColumn;
	}
	
	/*
	 * Add a text column to show the command.
	 */
	private static Column<RowData, SafeHtml> getColumnCommand() {
		Column<RowData, SafeHtml> commandColumn = new Column<RowData, SafeHtml>(
				new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getCommand());
			}

		};
		return commandColumn;
	}
	
	private Column<RowData, String> getColumnCaptionSimple() {

		Column<RowData, String> col = new Column<RowData, String>(
				new TextInputCell()) {

			@Override
			public String getValue(RowData object) {
				return object.getGeo().getCaptionSimple();
			}

		};

		col.setFieldUpdater(new FieldUpdater<RowData, String>() {

			public void update(int index, RowData object, String value) {
				object.getGeo().setCaption(value);
				data.initView();
				object.getGeo().updateVisualStyleRepaint();
			}

		});

		return col;
		
	}	


	/*
	 * Add a column to show end edit the caption.
	 */
	private Column<RowData, String> getColumnCaption() {

		class MyTextInputCell extends TextInputCell {
			private int focusedRow = -1;

			public int getFocusedRow() {
				return focusedRow;
			}

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				if (this.focusedRow == context.getIndex()) {
					super.render(context, value, sb);
				} else {
					sb.appendHtmlConstant(value);
				}

			}

			@Override
			public void onBrowserEvent(Context context, Element parent,
					String value, NativeEvent event,
					ValueUpdater<String> valueUpdater) {

				App.debug("eventType: " + event.getType());

				InputElement input = getInputElement(parent);
				if (input != null) {
					Element target = event.getEventTarget().cast();
					if (input.isOrHasChild(target)) {
						super.onBrowserEvent(context, parent, value, event,
								valueUpdater);
						return;
					}
				}

				String eventType = event.getType();
				if (BrowserEvents.FOCUS.equals(eventType)) {
					focusedRow = context.getIndex();
					// tableInit();
				} else if (BrowserEvents.BLUR.equals(eventType)) {
					focusedRow = -1;
				}

			}

		}

		final MyTextInputCell myCell = new MyTextInputCell();

		Column<RowData, String> col = new Column<RowData, String>(
myCell) {

			@Override
			public String getValue(RowData object) {
				if (object.getIndex() == myCell.getFocusedRow() + 1) {
					return object.getGeo().getCaptionSimple();
				}
				return object.getCaption();
			}


		};

		col.setFieldUpdater(new FieldUpdater<RowData, String>() {

			public void update(int index, RowData object, String value) {
				object.getGeo().setCaption(value);
				data.initView();
				tableInit();

			}

		});

		return col;
	}


	public class MyEditCell extends EditTextCell {
		private int focusedRow = -1;

		public int getFocusedRow() {
			return focusedRow;
		}

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			if (isEditing(context, null, null)) {
				// Doesn't matter if it super.render(...) gets the right value
				// in parameter "value", because this won't write this value
				// into the textfield, but this will use
				// EditTextCell.viewData.getText().
				// super.render(
				// context,
				// (String) table.getColumn(context.getColumn()).getValue(
				// data.getRow(context.getIndex())), sb);
				super.render(context, value, sb);

			} else {
				sb.appendHtmlConstant(value);
			}
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
			App.debug("event type: " + event.getType());

			String eventType = event.getType();
			if (BrowserEvents.CLICK.equals(eventType)) {
				focusedRow = context.getIndex();
			} else if (BrowserEvents.BLUR.equals(eventType)) {
				focusedRow = -1;

			}

			super.onBrowserEvent(
					context,
					parent,
					(String) table.getColumn(context.getColumn()).getValue(
							data.getRow(context.getIndex())), event,
					valueUpdater);

		}
	}

	private Column<RowData, String> getColumnCaption2() {

		final MyEditCell editCell = new MyEditCell();

		
		Column<RowData, String> col = new Column<RowData, String>(editCell) {

			@Override
			public String getValue(RowData object) {
				// ???
				Context context = new Context(object.getIndex(),
						table.getColumnIndex(this), table.getValueKey(object));
				boolean isEditing = editCell.isEditing(context, null, null);

				// isEditing = (object.getIndex() == editCell.getFocusedRow() +
				// 1);
				// App.debug(object.getIndex() + " == "
				// + (editCell.getFocusedRow() + 1));

				// if (isEditing) {

				if (object.getIndex() == ((MyEditCell) getCell())
						.getFocusedRow() + 1) {
					String caption = object.getGeo().getCaptionSimple();
					if (caption == null) {
						caption = "";
					}
					return caption;
				}
				return object.getCaption();
			}

		};

		col.setFieldUpdater(new FieldUpdater<RowData, String>() {

			public void update(int index, RowData object, String value) {
				object.getGeo().setCaption(value);
				data.initView();
				tableInit();
				object.getGeo().updateVisualStyleRepaint();

			}

		});

		table.setColumnWidth(col, 100, Unit.PX);

		return col;
	}

	/*
	 * Add a text column to show the breakpoints.
	 */
	private Column<RowData, Boolean> getColumnBreakpoint() {
		Column<RowData, Boolean> col = new Column<RowData, Boolean>(
				new CheckboxCell()) {

			@Override
			public Boolean getValue(RowData object) {
				return object.getGeo().isConsProtocolBreakpoint();
			}

		};

		col.setFieldUpdater(new FieldUpdater<RowData, Boolean>() {

			public void update(int index, RowData object, Boolean value) {
				object.getGeo().setConsProtocolBreakpoint(value);
				data.initView();
				tableInit();
			}

		});

		return col;
	}

	public void settingsChanged(AbstractSettings settings) {
		ConstructionProtocolSettings cps = (ConstructionProtocolSettings)settings;

		boolean gcv[] = cps.getColsVisibility();
		if (gcv != null) if (gcv.length > 0)
			setColsVisibility(gcv);

		initGUI();
//		update();
		getData().initView();
//		repaint();	
	}
	
	/**
     * @param colsVisibility intended visibility of columns 
     */
	private void setColsVisibility(boolean[] colsVisibility) {

		int k = Math.min(colsVisibility.length, data.columns.length);

		for (int i = 0; i < k; i++) {
			data.columns[i].setVisible(colsVisibility[i]);
		}

	}

	
	@Override
	public void scrollToConstructionStep() {
		if(kernel.isViewReiniting()){
			return;
		}
		if (table != null) {
			int rowCount = table.getRowCount();
			if (rowCount == 0)
				return;

			int step = kernel.getConstructionStep();
			int row = -1; //it's possible that ConsStep == 0, so we need index -1 to deselect all rows
			for (int i = 0; i < rowCount; i++) {
				if (data.getConstructionIndex(i) <= step)
					row = i;
				else
					break;
			}
			final int row2 = row;
			app.getGuiManager().invokeLater(new Runnable() {

				@Override
				public void run() {
					markRowsActive(row2);

				}
			});
			
			
		}
	}
	
	/**
	 * marks the rows (up to {@code index}) selected
	 * @param index int
	 */
	void markRowsActive(int index) {
		for (int i = 0; i < table.getRowCount(); i++) {
			try {
				if (i <= index) {
					GColorW color = (GColorW) data.getRow(i).getGeo()
							.getAlgebraColor();
					table.getRowElement(i).setAttribute(
							"style", "color:" + GColor.getColorString(color));
				} else {
					table.getRowElement(i).setAttribute(
							"style", "opacity:0.5");
				}
			} catch (IndexOutOfBoundsException e) {
				App.debug("OutOfBounds:" + i + "," + table.getRowCount() + ","
						+ table.getPageStart());
			}
		}
	}
	
	/**
	 * @return widget representing this view
	 */
	public FlowPanel getCpPanel(){
		return cpPanel;
	}

	
	/**
	 * Make all currrent rows draggable
	 */
	void makeTableRowsDragable() {
	    for (int i = 0; i < table.getRowCount(); i++) {
			try {
	    		table.getRowElement(i).setDraggable(Element.DRAGGABLE_TRUE);
			} catch (Exception e) {
				App.debug("Out of bounds");
			}
	    }
    }	
	
	/**
	 * Rebuild construction table
	 */
	public void tableInit(){
//		data.updateAll();
		table.setRowCount(data.getrowList().size());
	    table.setRowData(0, data.getrowList());
	    table.setVisibleRange(0, data.getrowList().size()+1);
		scrollToConstructionStep();

	}

	void rowCountChanged() {
		app.getGuiManager().invokeLater(new Runnable() {
	    	public void run(){
	    		makeTableRowsDragable();
	    	}
	    });
		scrollToConstructionStep();
	}
	/**
	 * Web implementation of ConstructionTableData
	 */
	class ConstructionTableDataW extends ConstructionTableData{

		private boolean rowsChanged;
		private boolean needsUpdate;
		private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;

		/**
		 * @param gui gui element on which we delegate setLabels
		 */
		public ConstructionTableDataW(SetLabels gui){
			super(gui);
//			ctDataImpl = new MyGAbstractTableModel();
		}

		@Override
		public void repaintView() {
			app.ensureTimerRunning();
			if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
				waitForRepaint = TimerSystemW.ALGEBRA_LOOPS;
			}
		}

		/**
		 * If there are some updates since last repaint, update the table
		 */
		public void repaintIfNeeded() {
			if (this.rowsChanged) {
				rowsChanged = false;
				needsUpdate = false;
				if (table != null) {
					table.setRowCount(0);
					tableInit();
					rowCountChanged();
				}
			} else if (needsUpdate) {
				needsUpdate = false;
				tableInit();
			}
		}
		/**
		 * 
		 * @param fromIndex from
		 * @param toIndex to
		 * @return whether something changed
		 */
		public boolean moveInConstructionList(int fromIndex, int toIndex) {
			boolean changed = kernel.moveInConstructionList(fromIndex, toIndex);

			// reorder rows in this view
			ConstructionElement ce = kernel.getConstructionElement(toIndex);
			GeoElement[] geos = ce.getGeoElements();
			for (int i = 0; i < geos.length; ++i) {
				remove(geos[i]);
				add(geos[i]);
			}
			return changed;
		}
		
		@Override
		public void fireTableRowsInserted(int firstRow, int lastRow){
			rowsChanged = true;
			needsUpdate = true;
			repaintView();
		}
		
		@Override
		public void fireTableRowsUpdated(int firstRow, int lastRow) {
			// TODO: maybe it's not necessary to reinit the all table
			needsUpdate = true;
			repaintView();
		}

		@Override
		public void fireTableRowsDeleted(int firstRow, int lastRow){
			rowsChanged = true;
			needsUpdate = true;
			repaintView();
		}

		@Override
        public void updateAll(){
			int size = rowList.size();

			for (int i = 0; i < size; ++i) {
				RowData row = rowList.get(i);
				row.updateAll();
			}
		}

		@Override
		public boolean suggestRepaint() {
			if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
				return false;
			}

			if (waitForRepaint == TimerSystemW.REPAINT_FLAG) {
				if (app.showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
					repaintIfNeeded();
					waitForRepaint = TimerSystemW.SLEEPING_FLAG;
				}
				return true;
			}

			waitForRepaint--;
			return true;

		}
	}

	/**
	 * @return stylebar
	 */
	public StyleBarW getStyleBar() {
	    if(styleBar == null){
			styleBar = new ConstructionProtocolStyleBarW(this, (AppW) this.app);
	    }
	    return styleBar;
    }

	public Widget getPrintable() {
		return new Label("Construction Protocol View");
	}
}

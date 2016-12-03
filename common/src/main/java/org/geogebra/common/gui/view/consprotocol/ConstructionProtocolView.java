package org.geogebra.common.gui.view.consprotocol;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.javax.swing.table.GAbstractTableModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.cas.AlgoDependentCasCell;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.debug.Log;

public class ConstructionProtocolView {
	
	public App app;
	public Kernel kernel;
	public ConstructionTableData data;
	protected boolean isViewAttached;
	public ArrayList<ConstructionProtocolNavigation> navigationBars = new ArrayList<ConstructionProtocolNavigation>();
	
	protected boolean useColors, addIcons;
	
	public class RowData {
		int rowNumber = -1;
		int index; // construction index of line: may be different
					// to geo.getConstructionIndex() as not every
					// geo is shown in the protocol
		GeoElement geo;
		GImageIcon toolbarIcon;
		String name, algebra, description, definition, caption;
		boolean includesIndex;
		boolean consProtocolVisible;
		private boolean wrapHTML;

		public RowData(GeoElement geo, boolean wrapHTML) {
			this.geo = geo;
			this.wrapHTML = wrapHTML;
			updateAll();
		}

		public void updateAlgebraAndName() {
			if (geo instanceof GeoText)
				algebra = "\""
						+ geo.toValueString(StringTemplate.defaultTemplate)
						+ "\"";
			else
				algebra = geo.getAlgebraDescriptionTextOrHTMLDefault(new IndexHTMLBuilder(
						true));
			// name description changes if type changes, e.g. ellipse becomes
			// hyperbola
			name = geo.getNameDescriptionTextOrHTML();
			// name = geo.getNameDescriptionHTML(true, true);
		}

		public void updateCaption() {
			caption = geo.getCaptionDescriptionHTML(wrapHTML,
					StringTemplate.defaultTemplate);
		}
		
		public GeoElement getGeo(){
			return geo;
		}
		
		public int getIndex(){
			return index;
		}
		
		public void setIndex(int i){
			index = i;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getDefinition() {
			return definition;
		}

		public String getAlgebra(){
			return algebra;
		}
		
		public String getCaption(){
			return caption;
		}
		
		public GImageIcon getToolbarIcon(){
			return toolbarIcon;
		}
		
		public boolean getCPVisible() {
			return consProtocolVisible;
		}
		
		public int getRowNumber(){
			return rowNumber;
		}
		
		public void setRowNumber(int num){
			rowNumber = num;
		}
		
		public boolean getIncludesIndex(){
			return includesIndex;
		}
		
		public void updateAll() {

			/*
			 * Only one toolbar icon should be displayed for each step, even if
			 * multiple substeps are present in a step (i.e. more rows). For
			 * that, we calculate the index for the current and the previous row
			 * and check if they are equal.
			 */
			int index;
			int prevIndex;

			index = (rowNumber < 0) ? -1 : /*data.*/getConstructionIndex(rowNumber);
			prevIndex = (rowNumber < 1) ? -1 : /*data.*/
					getConstructionIndex(rowNumber - 1);

			// TODO: This logic could be merged with the HTML export logic.
			int m;
			// Markus' idea to find the correct icon:
			// 1) check if an object has a parent algorithm:
			if (geo.getParentAlgorithm() != null) {
				// 2) if it has a parent algorithm and its modeID returned
				// is > -1, then use this one:
				m = geo.getParentAlgorithm().getRelatedModeID();
			}
			// 3) otherwise use the modeID of the GeoElement itself:
			else
				m = geo.getRelatedModeID();

			if (m != -1 && index != prevIndex) {
				toolbarIcon = getModeIcon(m); //app.wrapGetModeIcon(m);
			} else {
				toolbarIcon = null;
			}

			// name = geo.getNameDescriptionHTML(true, true);
			name = geo.getNameDescriptionTextOrHTML();
			// algebra = geo.getRedefineString(true, true);
			// algebra = geo.toOutputValueString();
			if (geo instanceof GeoText)
				algebra = "\""
						+ geo.toValueString(StringTemplate.defaultTemplate)
						+ "\"";
			else
				algebra = geo.getAlgebraDescriptionTextOrHTMLDefault(new IndexHTMLBuilder(
						true));
			description = geo.getDescriptionHTML(true);
			definition = geo.getDefinitionHTML(true);
			updateCaption();
			consProtocolVisible = geo.isConsProtocolBreakpoint();

			// does this line include an index?
			includesIndex = (name.indexOf("<sub>") >= 0)
					|| (algebra.indexOf("<sub>") >= 0)
					|| (description.indexOf("<sub>") >= 0)
					|| (definition.indexOf("<sub>") >= 0)
					|| (caption.indexOf("<sub>") >= 0);
		}

		protected int getConstructionIndex(int row) {
			return data.getConstructionIndex(row);
		}

		protected GImageIcon getModeIcon(int mode) {
			return app.wrapGetModeIcon(mode);
		}

	}

	public class ColumnData {
		String title;
		boolean isVisible; // column is shown in table
		private int prefWidth, minWidth;
		private int alignment;
		private boolean initShow; // should be shown from the beginning
	
		public ColumnData(String title, int prefWidth, int minWidth,
				int alignment, boolean initShow) {
			this.title = title;
			this.prefWidth = prefWidth;
			this.minWidth = minWidth;
			this.alignment = alignment;
			this.initShow = initShow;
	
			isVisible = initShow;
		}
	
		public String getTitle() {
			return title;
		}
	
		public String getTranslatedTitle() {
			return app.getLocalization().getMenu(title);
		}
	
		public int getPreferredWidth() {
			return prefWidth;
		}
	
		public int getMinWidth() {
			return minWidth;
		}
	
		public int getAlignment() {
			return alignment;
		}
	
		public boolean getInitShow() {
			// algebra column should only be shown at startup if algebraview is
			// shown
			// in app
			if (title.equals("Value")
					&& !(app.getGuiManager()).showView(App.VIEW_ALGEBRA))
				return false;
	
			return initShow;
		}
		
		public void setVisible(boolean isVisible){
			this.isVisible = isVisible;
		}
		
		public boolean isVisible(){
			return isVisible;
		}
		
	}

	public final void updateNavBarsAndRepaint() {
		// update all registered navigation bars
		kernel.notifyRepaint();
		int size = navigationBars.size();
		for (int i = 0; i < size; i++) {
			navigationBars.get(i).update();
		}
	}

	/**
	 * Returns the number of the current construction step shown in the
	 * construction protocol's table.
	 */
	public int getCurrentStepNumber() {
		return data.getCurrentStepNumber();
	}
	
	/**
	 * Returns the number of the last construction step shown in the
	 * construction protocol's table.
	 */
	public int getLastStepNumber() {
		return data.getLastStepNumber();
	}

	public void setConstructionStep(int step) {
		if (isViewAttached)
			kernel.detach(data);
		kernel.setConstructionStep(step);
		if (isViewAttached)
			kernel.attach(data);
		updateNavBarsAndRepaint();
	}

	public void nextStep() {
		if (isViewAttached)
			kernel.detach(data);
		kernel.nextStep();
		if (isViewAttached)
			kernel.attach(data);
		updateNavBarsAndRepaint();
		scrollToConstructionStep();
	}

	public void previousStep() {
		if (isViewAttached)
			kernel.detach(data);
		kernel.previousStep();
		if (isViewAttached)
			kernel.attach(data);
		updateNavBarsAndRepaint();
	}

	public void firstStep() {
		if (isViewAttached)
			kernel.detach(data);
		kernel.firstStep();
		if (isViewAttached)
			kernel.attach(data);
		updateNavBarsAndRepaint();
	}

	public void lastStep() {
		if (isViewAttached)
			kernel.detach(data);
		kernel.lastStep();
		if (isViewAttached)
			kernel.attach(data);
		updateNavBarsAndRepaint();
	}


	public ConstructionProtocolView.ConstructionTableData getData() {
		return data;
	}

	public void registerNavigationBar(ConstructionProtocolNavigation nb) {
		if (!navigationBars.contains(nb)) {
			navigationBars.add(nb);
			data.attachView();
		}
	}

	public void scrollToConstructionStep(){
		//TODO Log.debug("ConstructionProtocolView.scrollToConstructionStep - unimplemented in common");
	}
	
	public class ConstructionTableData implements View, SetLabels{

		protected ConstructionTableData ctData = this;

		public final ColumnData columns[] = {
						new ColumnData("No.", 35, 35, SwingConstants.RIGHT, true),
						new ColumnData("Name", 80, 50, SwingConstants.LEFT, true),
						new ColumnData("ToolbarIcon", 35, 35, SwingConstants.CENTER,
								false),
				new ColumnData("Description", 150,
						50, SwingConstants.LEFT, true),
				new ColumnData("Definition", 150, 50,
						SwingConstants.LEFT, false),
						new ColumnData("Value", 150, 50, SwingConstants.LEFT, true),
						new ColumnData("Caption", 150, 50, SwingConstants.LEFT, true),
						new ColumnData("Breakpoint", 70, 35, SwingConstants.CENTER,
								false)
						 };
		protected ArrayList<RowData> rowList;
		protected HashMap<GeoElement, RowData> geoMap;
		protected int columnsCount = columns.length;
		protected boolean notifyUpdateCalled;
		private SetLabels gui;

		public ConstructionTableData(SetLabels gui) {
//			ctDataImpl = new MyGAbstractTableModel();
			rowList = new ArrayList<RowData>();
			geoMap = new HashMap<GeoElement, RowData>();
			this.gui = gui;
		}
		
		public void setLabels(){
	    	this.gui.setLabels();
	    }
		
		@Override
		public void startBatchUpdate() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endBatchUpdate() {
			// TODO Auto-generated method stub
			
		}
		
		public boolean suggestRepaint(){
			return false;
			// not used for this view
		}
		
		public void notifyClear() {
			// TODO Auto-generated method stub

		}

		public ArrayList<RowData> getrowList(){
			return rowList;
		}
		
		public GAbstractTableModel getImpl(){
			Log.debug("ConstructionTableData.getImpl() must be overriden");
			return null;
		}

		public ColumnData[] getColumns() {
			return columns;
		}

		/**
		 * Returns the number of the last construction step shown in the
		 * construction protocol's table.
		 */
		public int getLastStepNumber() {
			int pos = rowList.size() - 1;
			if (pos >= 0)
				return rowList.get(pos).getIndex();
			else
				return 0;
		}

		/**
		 * Returns the number of the current construction step shown in the
		 * construction protocol's table.
		 */
		public int getCurrentStepNumber() {
			int step = kernel.getConstructionStep();
			
			// search the current construction step in the rowList
			int size = rowList.size();
			for (int i = 0; i < size; i++) {
				RowData rd = rowList.get(i);
				if (rd.getGeo().getConstructionIndex() == kernel
						.getClosestStep(step)) {
					return rd.getIndex();
				}
			}
			return 0;
		}

		public void setConstructionStepForRow(int row) {
			if (row >= 0) {
				setConstructionStep(getConstructionIndex(row));
			} else {
				setConstructionStep(-1);
			}
		}

		public int getConstructionIndex(int row) {
			return rowList.get(row).getGeo().getConstructionIndex();
		}

		public RowData getRow(int row) {
			return rowList.get(row);
		}

		/**
		 * Don't react to changing mode.
		 */
		public void setMode(int mode, ModeSetter m) {
		}

		public int getRowCount() {
			return rowList.size();
		}

		public int getColumnCount() {
			return columnsCount;
		}

		public int getRowIndex(RowData row) {
			return rowList.indexOf(row);
		}

		public int getColumnNumber(ColumnData column) {
			int pos = -1;
			for (int i = 0; i < columns.length; i++) {
				if (columns[i] == column) {
					pos = i;
					break;
				}
			}
			return pos;
		}

		public int getColumnNumberByTitle(String t) {
			if (t == null) {
				return -1;
			}
			String title;
			for (int i = 0; i < columns.length; i++) {
				title = columns[i].getTitle();
				if (t.equals(title)) {
					return i;
				}
			}
			return -1;
		}

		public boolean isCellEditable(int nRow, int nCol) {
			
			if((this.columns[nCol].getTitle()).equals("Caption")){ 
				return true;
			}
			return false;
		}


		/***********************
		 * View Implementation *
		 ***********************/
		public void add(GeoElement geo) {
			if ((!geo.isLabelSet() && !geo.isGeoCasCell())
					|| (kernel.getConstruction().showOnlyBreakpoints() && !geo
							.isConsProtocolBreakpoint()))
				return;
			// if we already have twin geo, ignore CAS cell
			if (geo.isGeoCasCell() && ((GeoCasCell) geo).getTwinGeo() != null
					&& ((GeoCasCell) geo).getTwinGeo().isAlgebraVisible()) {
				return;
			}
			// maybe the CAS cell is already in construction protocol (unclear
			// if it may happen)
			if (geo.getCorrespondingCasCell() != null) {
				remove(geo.getCorrespondingCasCell());
				// remove also twinGeo of geoCasCell
				// needed for GGB-810
				if (geo.getCorrespondingCasCell().getTwinGeo() != null
						&& geo.getCorrespondingCasCell().getTwinGeo()
								.equals(geo)) {
					remove(geo);
				}
			}
			RowData row = geoMap.get(geo); // lookup row for geo
			if (row == null) { // new row
				int index = geo.getConstructionIndex();
				// use index of geo instead of corresponding geoCasCell
				// needed for GGB-810
				if (geo.getParentAlgorithm() != null
						&& geo.getParentAlgorithm() instanceof AlgoDependentCasCell) {
					int geoIndex = geo.getAlgoDepCasCellGeoConstIndex();
					if (index < geoIndex) {
						index = geoIndex;
					}
				}
				int pos = 0; // there may be more rows with same index
				int size = rowList.size();
				while (pos < size
						&& index >= rowList.get(pos).getGeo()
								.getConstructionIndex())
					pos++;
		
				row = new RowData(geo, !app.isHTML5Applet());
				if (pos < size) {
					rowList.add(pos, row);
				} else {
					pos = size;
					rowList.add(row);
				}
		
				// insert new row
				geoMap.put(geo, row); // insert (geo, row) pair in map
				updateRowNumbers(pos);
				updateIndices();
				fireTableRowsInserted(pos, pos);
				updateAll();
				updateNavBarsAndRepaint();
			}
		}

		public void updatePreviewFromInputBar(GeoElement[] geos) {
			// TODO
		}

		public void remove(GeoElement geo) {
			RowData row = geoMap.get(geo);
			// lookup row for GeoElement
			if (row != null) {
				rowList.remove(row); // remove row
				geoMap.remove(geo); // remove (geo, row) pair from map
				updateRowNumbers(row.getRowNumber());
				updateIndices();
				fireTableRowsDeleted(row.getRowNumber(), row.getRowNumber());
				updateAll();
				updateNavBarsAndRepaint();
			}
		}

		public void clearView() {
			rowList.clear();
			geoMap.clear();
			notifyClear();
			updateNavBarsAndRepaint();
		}

		public void repaintView() {
			// overridden in subclasses
		}


		public void rename(GeoElement geo) {
			// renaming may affect multiple rows
			// so let's update whole table
			updateAll();
		}
		
		public final void update(GeoElement geo) {
			RowData row = geoMap.get(geo);
			if (row != null) {
				// remove row if only breakpoints
				// are shown and this is no longer a breakpoint (while loading a
				// construction)
				if (!geo.isConsProtocolBreakpoint()
						&& kernel.getConstruction().showOnlyBreakpoints())
					remove(geo);
				else {
					row.updateAlgebraAndName();
					row.updateCaption();
					fireTableRowsUpdated(row.getRowNumber(),
							row.getRowNumber());
				}
			} else {
				// missing row: should be added if only breakpoints
				// are shown and this became a breakpoint (while loading a
				// construction)
				if (kernel.getConstruction().showOnlyBreakpoints()
						&& geo.isConsProtocolBreakpoint())
					add(geo);
			}
			
		}

		protected void fireTableRowsUpdated(int rowNumber, int rowNumber2) {
			// TODO Auto-generated method stub

		}

		public void updateVisualStyle(GeoElement geo, GProperty prop) {
			// TODO Auto-generated method stub
			
		}

		public void updateAuxiliaryObject(GeoElement geo) {
			// TODO Auto-generated method stub
			
		}

		public void reset() {
			// TODO Auto-generated method stub
			
		}

		public int getViewID() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean hasFocus() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isShowing() {
			// TODO Auto-generated method stub
			return false;
		}

		/* End of View Implementation */

		
		private void updateRowNumbers(int row) {
			if (row < 0)
				return;
			int size = rowList.size();
			for (int i = row; i < size; ++i) {
				//rowList.get(i).rowNumber = i;
				rowList.get(i).setRowNumber(i);
			}
		}

		private void updateIndices() {
			int size = rowList.size();
			if (size == 0)
				return;
		
			int lastIndex = -1;
			int count = 0;
			RowData row;
			for (int i = 0; i < size; ++i) {
				row = rowList.get(i);
				int newIndex = row.getGeo().getConstructionIndex();
				if (lastIndex != newIndex) {
					lastIndex = newIndex;
					count++;
				}
				row.setIndex(count);
			}
		}

		
		protected void fireTableRowsDeleted(int firstRow, int lastRow){
			Log.debug("fireTableRowsDeleted - must be overridden");
		}

		protected void fireTableRowsInserted(int firstRow, int lastRow){
			Log.debug("fireTableRowsInserted - must be overriden");
		}
		
		public final void initView() {
			// init view
			rowList.clear();
			geoMap.clear();
			notifyClear();
			notifyAddAll(kernel.getLastConstructionStep());
		}

		public void updateAll() {
			Log.debug("updateAll - unimplemented");
		}

		public void notifyAddAll(int lastConstructionStep) {
			notifyUpdateCalled = true;
			kernel.notifyAddAll(this,kernel.getLastConstructionStep());
			notifyUpdateCalled = false;
			updateAll();
			
		}

		public void attachView() {
			if (!isViewAttached) {
				kernel.attach(this);
				initView();
				isViewAttached = true;
			}
		
			scrollToConstructionStep();
		}

	}

	public final void getXML(StringBuilder sb) {
		
		// COLUMNS
		sb.append("\t<consProtColumns ");
		for (int i = 0; i < data.columns.length; i++) {
			sb.append(" col");
			sb.append(i);
			sb.append("=\"");
			sb.append(data.columns[i].isVisible());
			sb.append("\"");
		}
		sb.append("/>\n");
	
		// consProtocol
		sb.append("\t<consProtocol ");
		sb.append("useColors=\"");
		sb.append(useColors);
		sb.append("\"");
		sb.append(" addIcons=\"");
		sb.append(addIcons);
		sb.append("\"");
		sb.append(" showOnlyBreakpoints=\"");
		sb.append(kernel.getConstruction().showOnlyBreakpoints());
		sb.append("\"");
		sb.append("/>\n");
	
	}
	


	public void showOnlyBreakpointsAction() {
		app.getKernel()
				.getConstruction()
				.setShowOnlyBreakpoints(
						!app.getKernel().getConstruction()
								.showOnlyBreakpoints());
		getData().initView();
		getData().repaintView();
		if (app.getGuiManager() != null) {
			app.getGuiManager().updateNavBars();
		}
	}
	
}

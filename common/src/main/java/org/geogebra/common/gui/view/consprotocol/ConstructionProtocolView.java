package org.geogebra.common.gui.view.consprotocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.cas.AlgoDependentCasCell;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

@SuppressWarnings("javadoc")
public class ConstructionProtocolView {

	public App app;
	public Kernel kernel;
	public ConstructionTableData data;
	protected boolean isViewAttached;
	public ArrayList<ConstructionProtocolNavigation> navigationBars = new ArrayList<>();

	protected boolean useColors, addIcons;

	protected static String getAlgebra(GeoElement geo) {
		return geo.getAlgebraDescriptionHTMLDefault();
	}

	protected static String getName(GeoElement geo) {
		return geo.getNameDescriptionTextOrHTML();
	}

	protected static String getCaption(GeoElement geo, boolean wrapHTML) {
		return geo.getCaptionDescriptionHTML(wrapHTML,
				StringTemplate.defaultTemplate);
	}

	protected static String getDescription(GeoElement geo) {
		return geo.getDescriptionHTML(false);
	}

	protected static String getDefinition(GeoElement geo) {
		return geo.getDefinitionHTML(false);
	}

	protected static boolean getBreakpoint(GeoElement geo) {
		return geo.isConsProtocolBreakpoint();
	}

	protected static String getModeIcon(GeoElement ge) {
		int m;
		// Markus' idea to find the correct icon:
		// 1) check if an object has a parent algorithm:
		if (ge.getParentAlgorithm() != null) {
			// 2) if it has a parent algorithm and its modeID returned
			// is > -1, then use this one:
			m = ge.getParentAlgorithm().getRelatedModeID();
		} else {
			// 3) otherwise use the modeID of the GeoElement itself:
			m = ge.getRelatedModeID();
		}

		if (m == -1 /* || index == prevIndex */) {
			return "";
		}

		App app = ge.getKernel().getApplication();

		String base64 = app.getModeIconBase64(m);

		// ImageIcon icon = ((AppD) app).getModeIcon(m);
		// Image img1 = icon.getImage();
		//
		// BufferedImage img2 = toBufferedImage(img1);
		// String base64 = StringUtil.pngMarker + GgbAPID.base64encode(img2,
		// 72);

		if (!"".equals(base64)) {

			String altText = "Icon for mode "
					+ EuclidianConstants.getModeText(m);

			return "<img alt='" + altText + "' height='32' width='32' src=\""
					+ base64 + "\">";
		}

		return "";
	}

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
			algebra = ConstructionProtocolView.getAlgebra(geo);
			// name description changes if type changes, e.g. ellipse becomes
			// hyperbola
			name = ConstructionProtocolView.getName(geo);
			// name = geo.getNameDescriptionHTML(true, true);
		}

		public void updateCaption() {
			caption = ConstructionProtocolView.getCaption(geo, wrapHTML);
		}

		public GeoElement getGeo() {
			return geo;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int i) {
			index = i;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getDefinition() {
			return definition;
		}

		public String getAlgebra() {
			return algebra;
		}

		public String getCaption() {
			return caption;
		}

		public GImageIcon getToolbarIcon() {
			return toolbarIcon;
		}

		public boolean getCPVisible() {
			return consProtocolVisible;
		}

		public int getRowNumber() {
			return rowNumber;
		}

		public void setRowNumber(int num) {
			rowNumber = num;
		}

		public boolean getIncludesIndex() {
			return includesIndex;
		}

		public void updateAll() {

			/*
			 * Only one toolbar icon should be displayed for each step, even if
			 * multiple substeps are present in a step (i.e. more rows). For
			 * that, we calculate the index for the current and the previous row
			 * and check if they are equal.
			 */
			int index1;
			int prevIndex;

			index1 = (rowNumber < 0) ? -1
					: /* data. */getConstructionIndex(rowNumber);
			prevIndex = (rowNumber < 1) ? -1 : /* data. */
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
			else {
				m = geo.getRelatedModeID();
			}

			if (m != -1 && index1 != prevIndex) {
				toolbarIcon = getModeIcon(m);
				// app.wrapGetModeIcon(m);
			} else {
				toolbarIcon = null;
			}

			// name = geo.getNameDescriptionHTML(true, true);
			name = ConstructionProtocolView.getName(geo);
			// algebra = geo.getRedefineString(true, true);
			// algebra = geo.toOutputValueString();
			algebra = ConstructionProtocolView.getAlgebra(geo);
			description = ConstructionProtocolView.getDescription(geo);
			definition = ConstructionProtocolView.getDefinition(geo);
			updateCaption();
			consProtocolVisible = ConstructionProtocolView.getBreakpoint(geo);

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
			if ("Value".equals(title)
					&& !(app.getGuiManager()).showView(App.VIEW_ALGEBRA)) {
				return false;
			}

			return initShow;
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}

		public boolean isVisible() {
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
		if (isViewAttached) {
			kernel.detach(data);
		}
		kernel.setConstructionStep(step);
		if (isViewAttached) {
			kernel.attach(data);
		}
		updateNavBarsAndRepaint();
	}

	public void nextStep() {
		if (isViewAttached) {
			kernel.detach(data);
		}
		kernel.nextStep();
		if (isViewAttached) {
			kernel.attach(data);
		}
		updateNavBarsAndRepaint();
		scrollToConstructionStep();
	}

	public void previousStep() {
		if (isViewAttached) {
			kernel.detach(data);
		}
		kernel.previousStep();
		if (isViewAttached) {
			kernel.attach(data);
		}
		updateNavBarsAndRepaint();
	}

	public void firstStep() {
		if (isViewAttached) {
			kernel.detach(data);
		}
		kernel.firstStep();
		if (isViewAttached) {
			kernel.attach(data);
		}
		updateNavBarsAndRepaint();
	}

	public void lastStep() {
		if (isViewAttached) {
			kernel.detach(data);
		}
		kernel.lastStep();
		if (isViewAttached) {
			kernel.attach(data);
		}
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

	public void scrollToConstructionStep() {
		// TODO Log.debug("ConstructionProtocolView.scrollToConstructionStep -
		// unimplemented in common");
	}

	public class ConstructionTableData implements View, SetLabels {

		public final ColumnData columns[] = {
				new ColumnData("No.", 35, 35, SwingConstants.RIGHT, true),
				new ColumnData("Name", 80, 50, SwingConstants.LEFT, true),
				new ColumnData("ToolbarIcon", 35, 35, SwingConstants.CENTER,
						false),
				new ColumnData("Description", 150, 50, SwingConstants.LEFT,
						true),
				new ColumnData("Definition", 150, 50, SwingConstants.LEFT,
						false),
				new ColumnData("Value", 150, 50, SwingConstants.LEFT, true),
				new ColumnData("Caption", 150, 50, SwingConstants.LEFT, true),
				new ColumnData("Breakpoint", 70, 35, SwingConstants.CENTER,
						false) };
		protected ArrayList<RowData> rowList;
		protected HashMap<GeoElement, RowData> geoMap;
		protected int columnsCount = columns.length;
		private boolean notifyUpdateCalled;
		private SetLabels gui;

		public ConstructionTableData(SetLabels gui) {
			// ctDataImpl = new MyGAbstractTableModel();
			rowList = new ArrayList<>();
			geoMap = new HashMap<>();
			this.gui = gui;
		}

		@Override
		public void setLabels() {
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

		@Override
		public boolean suggestRepaint() {
			return false;
			// not used for this view
		}

		public void notifyClear() {
			// TODO Auto-generated method stub

		}

		public ArrayList<RowData> getrowList() {
			return rowList;
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
			if (pos >= 0) {
				return rowList.get(pos).getIndex();
			}
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
		@Override
		public void setMode(int mode, ModeSetter m) {
			// mode does not affect CP
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

		public boolean isCellEditable(int nCol) {

			if ((this.columns[nCol].getTitle()).equals("Caption")) {
				return true;
			}
			return false;
		}

		/***********************
		 * View Implementation *
		 ***********************/
		@Override
		public void add(GeoElement geo) {
			if ((!geo.isLabelSet() && !geo.isGeoCasCell())
					|| (kernel.getConstruction().showOnlyBreakpoints()
							&& !geo.isConsProtocolBreakpoint())) {
				return;
			}
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
				if (geo.getCorrespondingCasCell().getTwinGeo() != null && geo
						.getCorrespondingCasCell().getTwinGeo().equals(geo)) {
					remove(geo);
				}
			}
			RowData row = geoMap.get(geo); // lookup row for geo
			if (row == null) { // new row
				int index = geo.getConstructionIndex();
				// use index of geo instead of corresponding geoCasCell
				// needed for GGB-810
				if (geo.getParentAlgorithm() != null && geo
						.getParentAlgorithm() instanceof AlgoDependentCasCell) {
					int geoIndex = geo.getAlgoDepCasCellGeoConstIndex();
					if (index < geoIndex) {
						index = geoIndex;
					}
				}
				int pos = 0; // there may be more rows with same index
				int size = rowList.size();
				while (pos < size && index >= rowList.get(pos).getGeo()
						.getConstructionIndex()) {
					pos++;
				}

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

		@Override
		public void updatePreviewFromInputBar(GeoElement[] geos) {
			// TODO
		}

		@Override
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

		@Override
		public void clearView() {
			rowList.clear();
			geoMap.clear();
			notifyClear();
			updateNavBarsAndRepaint();
		}

		@Override
		public void repaintView() {
			// overridden in subclasses
		}

		@Override
		public void rename(GeoElement geo) {
			// renaming may affect multiple rows
			// so let's update whole table
			updateAll();
		}

		@Override
		public final void update(GeoElement geo) {
			RowData row = geoMap.get(geo);
			if (row != null) {
				// remove row if only breakpoints
				// are shown and this is no longer a breakpoint (while loading a
				// construction)
				if (!geo.isConsProtocolBreakpoint()
						&& kernel.getConstruction().showOnlyBreakpoints()) {
					remove(geo);
				} else {
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
						&& geo.isConsProtocolBreakpoint()) {
					add(geo);
				}
			}

		}

		protected void fireTableRowsUpdated(int rowNumber, int rowNumber2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateVisualStyle(GeoElement geo, GProperty prop) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateHighlight(GeoElementND geo) {
			// nothing to do here
		}

		@Override
		public void updateAuxiliaryObject(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub

		}

		@Override
		public int getViewID() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasFocus() {
			// TODO Auto-generated method stub
			return false;
		}

		/* End of View Implementation */

		private void updateRowNumbers(int row) {
			if (row < 0) {
				return;
			}
			int size = rowList.size();
			for (int i = row; i < size; ++i) {
				// rowList.get(i).rowNumber = i;
				rowList.get(i).setRowNumber(i);
			}
		}

		private void updateIndices() {
			int size = rowList.size();
			if (size == 0) {
				return;
			}

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

		protected void fireTableRowsDeleted(int firstRow, int lastRow) {
			Log.debug("fireTableRowsDeleted - must be overridden");
		}

		protected void fireTableRowsInserted(int firstRow, int lastRow) {
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
			// platform dependent, TODO move this to view
		}

		public void notifyAddAll(int lastConstructionStep) {
			notifyUpdateCalled = true;
			kernel.notifyAddAll(this, kernel.getLastConstructionStep());
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

		final public void detachView() {
			// only detach view if there are
			// no registered navigation bars
			if (isViewAttached && navigationBars.size() == 0) {
				// clear view
				rowList.clear();
				geoMap.clear();
				kernel.detach(this);
				isViewAttached = false;

				// side effect: go to last construction step
				setConstructionStep(kernel.getLastConstructionStep());
			}
		}

		protected boolean isNotifyUpdateCalled() {
			return notifyUpdateCalled;
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
		app.getKernel().getConstruction().setShowOnlyBreakpoints(
				!app.getKernel().getConstruction().showOnlyBreakpoints());
		getData().initView();
		getData().repaintView();
		if (app.getGuiManager() != null) {
			app.getGuiManager().updateNavBars();
		}
	}

	/**
	 * Returns a html representation of the construction protocol.
	 * 
	 * @param imgBase64
	 *            : image file to be included
	 */
	public static String getHTML(String imgBase64, Localization loc,
			Kernel kernel, ArrayList<Columns> columns, boolean addIcons,
			boolean useColors) {

		StringBuilder sb = new StringBuilder();

		boolean icon_column;

		// Let's be W3C compliant:
		sb.append(
				"<!DOCTYPE html>\n");
		sb.append(
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
		sb.append("<head>\n");
		sb.append("<title>");
		sb.append(StringUtil.toHTMLString(GeoGebraConstants.APPLICATION_NAME));
		sb.append(" - ");
		sb.append(loc.getMenu("ConstructionProtocol"));
		sb.append("</title>\n");

		sb.append(
				"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		sb.append("</head>\n");

		sb.append("<body>\n");

		// header with title
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!"".equals(title)) {
			sb.append("<h1>");
			sb.append(StringUtil.toHTMLString(title));
			sb.append("</h1>\n");
		}

		// header with author and date
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!"".equals(author)) {
			line = author;
		}
		if (!"".equals(date)) {
			if (line == null) {
				line = date;
			} else {
				line = line + " - " + date;
			}
		}
		if (line != null) {
			sb.append("<h3>");
			sb.append(StringUtil.toHTMLString(line));
			sb.append("</h3>\n");
		}

		// include image file
		if (imgBase64 != null) {
			sb.append("<p>\n");
			sb.append("<img height='32' width ='32' src=\"");
			sb.append(StringUtil.pngMarker);
			sb.append(imgBase64);
			sb.append("\" alt=\"");
			sb.append(StringUtil
					.toHTMLString(GeoGebraConstants.APPLICATION_NAME));
			sb.append(' ');
			sb.append(StringUtil.toHTMLString(loc.getMenu("DrawingPad")));
			sb.append("\" border=\"1\">\n");
			sb.append("</p>\n");
		}

		// table
		sb.append("<table border=\"1\">\n");

		// table headers
		sb.append("<tr>\n");

		int nColumns = columns.size();

		for (int nCol = 0; nCol < nColumns; nCol++) {
			// toolbar icon will only be inserted on request

			// icon_column = table.getColumnName(nCol).equals("ToolbarIcon");
			icon_column = "ToolbarIcon".equals(columns.get(nCol));
			if ((icon_column && addIcons) || !icon_column) {
				title = columns.get(nCol).getTranslation(loc);
				sb.append("<th>");
				sb.append(StringUtil.toHTMLString(title));
				sb.append("</th>\n");
			}

		}
		sb.append("</tr>\n");

		TreeSet<GeoElement> geos = cons.getGeoSetConstructionOrder();
		Iterator<GeoElement> it = geos.iterator();

		// table rows
		int endRow = geos.size();
		for (int nRow = 0; nRow < endRow; nRow++) {
			GeoElement geo = it.next();
			sb.append("<tr style='vertical-align:baseline;'>\n");
			for (int nCol = 0; nCol < nColumns; nCol++) {

				Columns column = columns.get(nCol);

				// toolbar icon will only be inserted on request
				// icon_column =
				// table.getColumnName(nCol).equals("ToolbarIcon");
				icon_column = "ToolbarIcon".equals(columns.get(nCol));
				if ((icon_column && addIcons) || !icon_column) {

					String str = "";

					switch (column) {
					default:
						str = "";
						break;

					case NUMBER:
						str = (nRow + 1) + "";
						break;
					case CAPTION:
						str = getCaption(geo, false);
						break;

					case NAME:
						str = getName(geo);
						break;

					case TOOLBARICON:
						str = getModeIcon(geo);
						break;

					case DESCRIPTION:
						str = getDescription(geo);
						break;

					case DEFINITION:
						str = getDefinition(geo);
						break;

					case VALUE:
						str = getAlgebra(geo);
						break;

					case BREAKPOINT:
						str = getBreakpoint(geo) + "";
						break;

					}

					sb.append("<td>");
					if ("".equals(str)) {
						sb.append("&nbsp;"); // space
					} else {

						GColor color = useColors ? geo.getAlgebraColor()
								: GColor.BLACK;

						if (!GColor.BLACK.equals(color)) {
							sb.append("<span style=\"color:#");
							sb.append(StringUtil.toHexString(
									(byte) color.getRed(),
									(byte) color.getGreen(),
									(byte) color.getBlue()));
							sb.append("\">");
							sb.append(str);
							sb.append("</span>");
						} else {
							sb.append(str);
						}
					}
					sb.append("</td>\n");
				}

			}
			sb.append("</tr>\n");
		}

		sb.append("</table>\n");

		// footer
		sb.append(getCreatedWithHTML());

		// append base64 string so that file can be reloaded with File -> Open
		sb.append(
				"\n<!-- Base64 string so that this file can be opened in GeoGebra with File -> Open -->");
		sb.append(
				"\n<applet width='1' height='1' code='' style=\"display:none\">");
		sb.append("\n<param name=\"ggbBase64\" value=\"");
		sb.append(kernel.getApplication().getGgbApi().getBase64());
		sb.append("\">\n</applet>");

		sb.append("\n</body>");
		sb.append("\n</html>");

		return sb.toString();
	}

	/**
	 * Returns text "Created with GeoGebra" and link to application homepage in
	 * html.
	 */
	public static String getCreatedWithHTML() {

		return "Created with " + wrapLink("GeoGebra");
	}

	private static String wrapLink(String string) {
		return "<a href=\"" + GeoGebraConstants.GEOGEBRA_WEBSITE
				+ "\" target=\"_blank\" >" + string + "</a>";
	}

	public enum Columns {

		NUMBER("No."),

		NAME("Name"),

		TOOLBARICON("ToolbarIcon"),

		DESCRIPTION("Description"),

		DEFINITION("Definition"),

		VALUE("Value"),

		CAPTION("Caption"),

		BREAKPOINT("Breakpoint");

		private String translationKey;

		Columns(String key) {
			this.translationKey = key;
		}

		public String getTranslation(Localization loc) {
			return loc.getMenu(translationKey);
		}

		public static Columns lookUp(String str, Localization loc) {
			for (Columns col : Columns.values()) {
				if (loc.getMenu(col.translationKey).equals(str)) {
					return col;
				}
			}
			Log.error("column " + str + " not found");
			return Columns.NAME;

		}

	};

}

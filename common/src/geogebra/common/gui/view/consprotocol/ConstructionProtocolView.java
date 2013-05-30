package geogebra.common.gui.view.consprotocol;

import geogebra.common.javax.swing.GImageIcon;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;

public class ConstructionProtocolView {
	
	protected App app;
	
	protected class RowData {
		int rowNumber = -1;
		int index; // construction index of line: may be different
					// to geo.getConstructionIndex() as not every
					// geo is shown in the protocol
		GeoElement geo;
		GImageIcon toolbarIcon;
		String name, algebra, definition, command, caption;
		boolean includesIndex;
		Boolean consProtocolVisible;

		public RowData(GeoElement geo) {
			this.geo = geo;
			updateAll();
		}

		public void updateAlgebraAndName() {
			if (geo instanceof GeoText)
				algebra = "\""
						+ geo.toValueString(StringTemplate.defaultTemplate)
						+ "\"";
			else
				algebra = geo.getAlgebraDescriptionTextOrHTMLDefault();
			// name description changes if type changes, e.g. ellipse becomes
			// hyperbola
			name = geo.getNameDescriptionTextOrHTML();
			// name = geo.getNameDescriptionHTML(true, true);
		}

		public void updateCaption() {
			caption = geo.getCaptionDescriptionHTML(true,
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
		
		public String getDefinition(){
			return definition;
		}
		
		public String getCommand(){
			return command;
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
		
		public Boolean getCPVisible(){
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
		
		//TODO
		//These functions is used until we have no construction protocol view in web.
		//Its value is only temporarily, see comment of index variable.
		//Desktop doesn't use this, it's overridden there.
		protected int getConstructionIndex(int row){
			return row;	
		}
		protected GImageIcon getModeIcon(int mode){
			return null;
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

			if (m != -1 && index != prevIndex)
				toolbarIcon = getModeIcon(m); //app.wrapGetModeIcon(m);
			else
				toolbarIcon = null;

			// name = geo.getNameDescriptionHTML(true, true);
			name = geo.getNameDescriptionTextOrHTML();
			// algebra = geo.getRedefineString(true, true);
			// algebra = geo.toOutputValueString();
			if (geo instanceof GeoText)
				algebra = "\""
						+ geo.toValueString(StringTemplate.defaultTemplate)
						+ "\"";
			else
				algebra = geo.getAlgebraDescriptionTextOrHTMLDefault();
			definition = geo.getDefinitionDescriptionHTML(true);
			command = geo.getCommandDescriptionHTML(true);
			updateCaption();
			consProtocolVisible = new Boolean(geo.isConsProtocolBreakpoint());

			// does this line include an index?
			includesIndex = (name.indexOf("<sub>") >= 0)
					|| (algebra.indexOf("<sub>") >= 0)
					|| (definition.indexOf("<sub>") >= 0)
					|| (command.indexOf("<sub>") >= 0)
					|| (caption.indexOf("<sub>") >= 0);
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
			return app.getPlain(title);
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

	public void setConstructionStep(int consStep){
		App.debug("common/ConstructionProtocolView.setConstructionStep - implementation needed.");
	}

}

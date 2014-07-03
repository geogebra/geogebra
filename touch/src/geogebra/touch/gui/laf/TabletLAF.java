package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUIT;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.elements.ggt.MaterialListElementT;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.elements.header.WorksheetHeaderPanel;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.Window;

public class TabletLAF extends DefaultLAF {
	
	private TabletHeaderPanel hp;
	private BrowseHeaderPanel bhp;
	
	public TabletLAF(TouchApp app) {
		super(app);
	}
	
	@Override
	public void updateUndoSaveButtons() {
//		if (this.getTabletHeaderPanel() != null) {
//			this.getTabletHeaderPanel().enableDisableButtons();
//		}
	}
	
	@Override
	public void stateChanged(final boolean b) {
//		if (this.getTabletHeaderPanel() != null) {
//			this.getTabletHeaderPanel().enableDisableButtons();
//		}
	}
	
	@Override
	public void setTitle(final String title) {
		this.hp.setTitle(title);
	}
	
	@Override
	public int getCanvasHeight() {
		return Window.getClientHeight() - getToolBarHeight()
				- getHeaderHeight();
	}

	
	public void buildTabletHeader(final TouchModel touchModel) {
		this.hp = new TabletHeaderPanel(this.app, touchModel);
		this.gui.addResizeListener(this.hp);
	}

	public WorksheetHeader buildWorksheetHeader(final WorksheetGUI worksheetGUI) {
		final WorksheetHeaderPanel header = new WorksheetHeaderPanel(this.app,
				(TabletGUI) this.gui);
		worksheetGUI.setHeaderWidget(header);
		return header;
	}

	public BrowseHeaderPanel buildBrowseHeader(final BrowseGUIT browseGUI) {
		this.bhp = new BrowseHeaderPanel(this.app.getLocalization(), browseGUI,
				this.app.getNetworkOperation());
		browseGUI.setHeaderWidget(this.bhp);
		browseGUI.addResizeListener(this.bhp);
		return this.bhp;
	}
	
	@Override
	public int getHeaderHeight() {
//		if (this.hp == null) {
//			// otherwise nullPointerException for win8
//			return 0;
//		}
//		return this.hp.getOffsetHeight();
		return 50;
	}

	public int getBrowseHeaderHeight() {
		return this.bhp.getOffsetHeight();
	}
	
	public int getToolBarHeight() {
		//return this.gui.getToolBar().getOffsetHeight();
		return 71;
	}
	
	public TabletHeaderPanel getTabletHeaderPanel() {
		return this.hp;
	}
	
	@Override 
	public void unselectMaterials() {
		TouchEntryPoint.getBrowseGUI().unselectMaterials();
	}
	
	@Override
	public void rememberSelected(MaterialListElementT mat) {
		TouchEntryPoint.getBrowseGUI().rememberSelected(mat);
	}
}

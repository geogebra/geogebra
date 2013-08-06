package geogebra.touch.gui.laf;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.WorksheetHeader;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class WinLAF extends DefaultLAF {

    public WinLAF(TouchApp app) {
	super(app);
    }

    @Override
    public void buildHeader(TabletGUI gui, TouchModel touchModel) {

    }

    @Override
    public int getAppBarHeight() {
	return 0;
    }

    @Override
    public DefaultResources getIcons() {
	return WinResources.INSTANCE;
    }

    @Override
    public int getPaddingLeftOfDialog() {
	return (Window.getClientWidth() - 740) / 2;
    }

    @Override
    public int getPanelsHeight() {
	return 60;
    }

    @Override
    public TabletHeaderPanel getTabletHeaderPanel() {
	return null;
    }

    @Override
    public boolean isMouseDownIgnored() {
	return false;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public native void stateChanged(boolean saved) /*-{
		if (!$wnd.appbar) {
			return;
		}
		$wnd.appbar.saveChanged(saved);
    }-*/;

    @Override
    public void updateUndoSaveButtons() {
	if (this.getApp() != null) {
	    this.updateUndoSaveButtons(this.getApp().getKernel().undoPossible(), this.getApp().getKernel().redoPossible());
	}
    }

    public native void updateUndoSaveButtons(boolean undo, boolean redo) /*-{
		if (!$wnd.appbar) {
			return;
		}
		$wnd.appbar.updateUndoRedo(undo, redo);
    }-*/;
    
    @Override
	public WorksheetHeader buildWorksheetHeader(WorksheetGUI worksheetGUI,TabletGUI tabletGUI) {
    	final Label consTitle = new Label();
    	worksheetGUI.getContent().add(consTitle);
		WorksheetHeader header = new WorksheetHeader(){

			@Override
			public void setLabels() {
				//no native buttons
				
			}

			@Override
			public void setMaterial(Material m) {
				consTitle.setTitle(m.getTitle());
			}};
		return header;
	}
}

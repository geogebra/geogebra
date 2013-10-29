package geogebra.touch.gui.laf;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.euclidian.MsZoomer;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.euclidian.EuclidianViewT;
import geogebra.touch.model.TouchModel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WinLAF extends DefaultLAF {

	public WinLAF(final TouchApp app) {
		super(app);
	}

	@Override
	public void buildTabletHeader(final TouchModel touchModel) {

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
	public TabletHeaderPanel getTabletHeaderPanel() {
		return null;
	}

	@Override
	public boolean receivesDoubledEvents() {
		return false;
	}

	@Override
	public void setTitle(final String title) {

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
			this.updateUndoSaveButtons(
					this.getApp().getKernel().undoPossible(), this.getApp()
							.getKernel().redoPossible());
		}
	}

	private native void updateUndoSaveButtons(boolean undo, boolean redo) /*-{
		if (!$wnd.appbar) {
			return;
		}
		$wnd.appbar.updateUndoRedo(undo, redo);
	}-*/;

	@Override
	public WorksheetHeader buildWorksheetHeader(final WorksheetGUI worksheetGUI) {
		final Label consTitle = new Label();
		consTitle.getElement().addClassName("worksheetNameWindows");
		worksheetGUI.getContent().add(consTitle);
		final WorksheetHeader header = new WorksheetHeader() {

			@Override
			public void setLabels() {
				// no native buttons

			}

			@Override
			public void setMaterial(final Material m) {
				consTitle.setText(m.getTitle());
			}
		};
		return header;
	}
	private MsZoomer zoomer;
	@Override
	public void attachExternalEvents(final EuclidianViewT view,
			final Element element) {
		this.zoomer = new MsZoomer(view.getEuclidianController());
		MsZoomer.attachTo(element, this.zoomer);
	}

	@Override
	public void resetNativeHandlers(){
		if(this.zoomer != null){
			this.zoomer.reset();
		}
	}
		
	@Override
	public void setPopupCenter(final PopupPanel panel) {
		panel.setPopupPosition((Window.getClientWidth() - panel.getWidget()
				.getOffsetWidth()) / 2, 50);
	}

	@Override
	public void loadRTLStyles() {
		StyleInjector.injectStylesheet(DefaultResources.INSTANCE.rtlStyleWin().getText());
	}
	
	@Override
	public void center(Widget w){
		w.getElement().getStyle().setPaddingLeft(getPaddingLeftOfDialog(), Unit.PX);
		w.getElement().getStyle().setPaddingRight(getPaddingLeftOfDialog(), Unit.PX);
	}
}

package geogebra.touch.gui.laf;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.euclidian.EuclidianViewT;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

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

	@Override
	public void attachExternalEvents(final EuclidianViewT view,
			final Element element) {
		addNativeHandlers(element, new MsZoomer(view.getEuclidianController()));
	}

	@Override
	public native void resetNativeHandlers()/*-{
		$wnd.first = {id:-1};
		$wnd.second = {id:-1};
	}-*/;
	
	private native void addNativeHandlers(Element element, MsZoomer zoomer) /*-{
		$wnd.first = {id:-1};
		$wnd.second = {id:-1};
		$wnd.oldDistance = 0;
		$wnd.distance = function(){
				return Math.sqrt(($wnd.first.x-$wnd.second.x)*($wnd.first.x-$wnd.second.x)+
				($wnd.first.y-$wnd.second.y)*($wnd.first.y-$wnd.second.y));
		}
		
		
		element.addEventListener("MSPointerMove",function(e) {
			if($wnd.first.id >=0 && $wnd.second.id>=0){
				if($wnd.first.id === e.pointerId){    	
					$wnd.second.x = e.x;	
					$wnd.second.y = e.y;
				}else{
					$wnd.first.x = e.x;	
					$wnd.first.y = e.y;
				}
				var newDistance = $wnd.distance();
				var ratio = newDistance / $wnd.oldDistance;
				if(ratio > 1.1 || ratio < 1/1.1){
					zoomer.@geogebra.touch.gui.laf.MsZoomer::zoom(D)(ratio);
					$wnd.oldDistance = newDistance;
				}
			}
		});
		
		element.addEventListener("MSPointerDown",function(e) {
			if($wnd.first.id >= 0){
				$wnd.second.id = e.pointerId;
				$wnd.second.x = e.x;	
				$wnd.second.y = e.y;
				zoomer.@geogebra.touch.gui.laf.MsZoomer::setZoomCenter(DD)(($wnd.first.x+$wnd.second.x)/2.0,
				($wnd.first.y+$wnd.second.y)/2.0);
			}else{
				$wnd.first.id = e.pointerId;
				$wnd.first.x = e.x;	
				$wnd.first.y = e.y;
			}
			if($wnd.first.id >=0 && $wnd.second.id>=0){
				$wnd.oldDistance = $wnd.distance();
			}
	
		});
		
		element.addEventListener("MSPointerUp",function(e) {
			if($wnd.first.id == e.pointerId){
				$wnd.first.id = -1;
				zoomer.@geogebra.touch.gui.laf.MsZoomer::setZoomCenter(DD)(-1,-1);
			}else{
				zoomer.@geogebra.touch.gui.laf.MsZoomer::setZoomCenter(DD)(-1,-1);
				$wnd.second.id = -1;
			}
		});
	}-*/;
	
	@Override
	public void setPopupCenter(final PopupPanel panel) {
		panel.setPopupPosition((Window.getClientWidth() - panel.getWidget()
				.getOffsetWidth()) / 2, 50);
	}
}

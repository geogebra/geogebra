package geogebra.web.gui;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.util.MyToggleButton2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	public interface CustomizeToolbarListener {
		void update(int id);
	}
	
	protected static final int GENERAL = -1;
	private AppW app;
	private FlowPanel buttons;
	private int selectedViewId;
	private CustomizeToolbarListener listener;
	CustomizeToolbarHeaderPanel(AppW app, MyHeaderPanel gui) {
	    super(app.getLocalization(), gui);
	    this.app = app;
	    this.listener = (CustomizeToolbarListener)gui;
	    createPanelsToolbar();
	    setLabels();
    }
	
	@Override
    public void setLabels(){
		setText(loc.getMenu("Toolbar.Customize"));
	}
	
	private void createPanelsToolbar() {
		buttons = new FlowPanel();
		buttons.setStyleName("panelRow");
		
		final MyToggleButton2 btnGeneral = new MyToggleButton2(new Image(AppResources.INSTANCE.view_graphics24()));
		buttons.add(btnGeneral);
		DockPanelW[] panels =  ((GuiManagerW)app.getGuiManager()).getLayout().getDockManager().getPanels();
		for(DockPanelW panel : panels) {
			final int viewId = panel.getViewId();
			if(panel.canCustomizeToolbar()) {
				App.debug("[customize] view id for button is " + viewId);
				ImageResource res = null;
				switch (viewId) {
				case App.VIEW_EUCLIDIAN3D:
					res = AppResources.INSTANCE.view_graphics3D24();
					break;
				case App.VIEW_DATA_ANALYSIS:
					res = AppResources.INSTANCE.view_probability24();
					break;
				default:
					res = panel.getIcon();
				}
				
				final MyToggleButton2 btn = new MyToggleButton2(new Image(res));
				btn.addClickHandler(new ClickHandler() {
					
					public void onClick(ClickEvent event) {
						uncheckAll(btn);
						selectedViewId = viewId;
						listener.update(selectedViewId);
					}
				});
				buttons.add(btn);
			}
		}
		btnGeneral.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				uncheckAll(btnGeneral);
				selectedViewId = GENERAL;
				listener.update(selectedViewId);
			}
		});
		
		selectedViewId = GENERAL;
		
		rightPanel.add(buttons);
		add(rightPanel);

	}

	private void uncheckAll(MyToggleButton2 current) {
		for(int i=0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof MyToggleButton2 && w != current) {
				((MyToggleButton2)w).setValue(false);
			}
		}
	}

	public int getSelectedViewId() {
	    return selectedViewId;
    }

}

package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class FileContainer extends HorizontalPanel implements ResizeListener {

	private FlowPanel fileControlPanel;
	private final VerticalMaterialPanel filePanel;
	private HorizontalPanel filePages;
	private FlowPanel providers;
	private StandardButton locationTube;
	private StandardButton locationDrive;
	/*private StandardButton locationSkyDrive;
	private StandardButton locationLocal;*/

	private BrowseGUI bg;
	private AppWeb app;
	private StandardButton locationSkyDrive;
	
	public class MyButton extends FlowPanel{
		public MyButton(BrowseGUI bg){
			super();
			this.setStyleName("button");
			Image icon = new Image(BrowseResources.INSTANCE.location_local());
			Element span = DOM.createElement("span");
			span.setAttribute("style", "position: absolute; width: 50px; height: 50px; padding: 10px; top: 0px; left: 0px; overflow: hidden;");
			span.setInnerHTML("<img src=\""+icon.getUrl()+"\"/>");
			Element input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute("style", "width: 500px; height: 60px; font-size: 56px;" +
					"opacity: 0; position: absolute; right: 0px; top: 0px; cursor: pointer;");
			span.appendChild(input);
			
			DOM.insertChild(getElement(), span, 0);
			addGgbChangeHandler(input,bg);
		}
		
		public native void addGgbChangeHandler(Element el, BrowseGUI bg) /*-{
		var dialog = this;
//		el.setAttribute("accept", "application/vnd.geogebra.file, application/vnd.geogebra.tool");
		el.onchange = function(event) {
			var files = this.files;
			if (files.length) {
				var fileToHandle = files[0];
				bg.@geogebra.html5.gui.browser.BrowseGUI::openFileAsGgb(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle);
			}

		};
		}-*/;
	}
	public FileContainer(String headingName,
			final VerticalMaterialPanel filePanel, BrowseGUI bg, AppWeb app) {
		
		this.filePanel = filePanel;
		this.app = app;
		this.add(filePanel);
		this.bg = bg;
		
		this.providers = new FlowPanel();
		
		initProviders();
		//providers.add(locationDrive);
		
		
		providers.setStyleName("providers");
		this.add(providers);
		app.getLoginOperation().getView().add(new EventRenderable(){

			@Override
            public void renderEvent(BaseEvent event) {
	            initProviders();
	            
            }});
		onResize();
	}

	/**
	 * Initialize the providers panel, needs to be done after google / MS login
	 */
	void initProviders() {
		providers.clear();
		locationTube = new StandardButton(BrowseResources.INSTANCE.location_tube());
		locationTube.addFastClickHandler(new FastClickHandler(){

			@Override
            public void onClick() {
				FileContainer.this.bg.setProvider(Provider.TUBE);
				FileContainer.this.bg.updateGUI();
            }
			
		});
		providers.add(locationTube);
		
		MyButton locationLocal = new MyButton(bg);//StandardButton(AppResources.INSTANCE.folder());
		providers.add(locationLocal);
		//TODO: Only visible if user is logged in with google Account
		GeoGebraTubeUser user = this.app.getLoginOperation().getModel().getLoggedInUser();
		if(user !=null && user.hasGoogleDrive()){
			locationDrive = new StandardButton(BrowseResources.INSTANCE.location_drive());
			providers.add(locationDrive);
			locationDrive.addFastClickHandler(new FastClickHandler(){

				@Override
                public void onClick() {
					FileContainer.this.bg.setProvider(Provider.GOOGLE);
	                ((AppW) FileContainer.this.app).getGoogleDriveOperation().requestDriveLogin();
	                if(((AppW) FileContainer.this.app).getGoogleDriveOperation().isLoggedIntoGoogle()){
	                	((AppW) FileContainer.this.app).getGoogleDriveOperation().initFileNameItems(FileContainer.this.bg);
	                }
                }});
		}else if (user!= null){
			App.debug(user.getIdentifier());
		}
		//TODO: Only visible if user is logged in with google Account
		if(user !=null && user.hasOneDrive()){
				locationSkyDrive = new StandardButton(BrowseResources.INSTANCE.location_skydrive());
				providers.add(locationSkyDrive);
		}
				
				//Set Tube as the active on
				locationTube.addStyleName("selected");
				
    }

	@Override
	public void onResize() {
		
		int contentHeight = (int) (app.getHeight() - BrowseGUI.HEADING_HEIGHT);
		this.setHeight(contentHeight + "px");
		this.filePanel.setWidth((int) app.getWidth() - 70 + "px");
		this.filePanel.setHeight(contentHeight + "px");
		this.providers.setHeight(contentHeight + "px");
	}
}


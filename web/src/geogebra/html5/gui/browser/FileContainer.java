package geogebra.html5.gui.browser;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;

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
	
	public class MyButton extends FlowPanel{
		public MyButton(BrowseGUI bg){
			super();
			this.setStyleName("button");
			Image icon = new Image(BrowseResources.INSTANCE.location_local());
			Element span = DOM.createElement("span");
			span.setAttribute("style", "position: absolute; width: 50px; height: 50px; padding: 10px; top: 0px; left: 0px;");
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
		
		locationTube = new StandardButton(BrowseResources.INSTANCE.location_tube());
		providers.add(locationTube);
		
		MyButton locationLocal = new MyButton(bg);//StandardButton(AppResources.INSTANCE.folder());
		providers.add(locationLocal);
		
		//TODO: Only visible if user is logged in with google Account
		locationDrive = new StandardButton(BrowseResources.INSTANCE.location_drive());
		//providers.add(locationDrive);
		
		//TODO: Only visible if user is logged in with google Account
		//locationSkyDrive = new StandardButton(BrowseResources.INSTANCE.location_skydrive());
		//providers.add(locationSkyDrive);
		
		//Set Tube as the active on
		locationTube.addStyleName("selected");
		
		providers.setStyleName("providers");
		this.add(providers);
		
		onResize();
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


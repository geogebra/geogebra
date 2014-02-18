package geogebra.html5.gui.browser;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class FileContainer extends HorizontalPanel implements ResizeListener {

	private FlowPanel fileControlPanel;
	private final VerticalMaterialPanel filePanel;
	private HorizontalPanel filePages;
	private FlowPanel providers;
	private StandardButton locationTube;
	private StandardButton locationDrive;
	private StandardButton locationSkyDrive;
	private StandardButton locationLocal;
/*
 <form id="fileform" name="fileform" method='post' enctype='multipart/form-data'>
 	<span style="display:block;float:right;position:relative;">+<input name="file01" onchange="document.getElementById('fileform').submit()" type="file" style="width:20px;height:20px;font-size:118px;opacity:0;position:absolute;left:0px"></span>
 </form>
 */
	
	public class MyButton extends Button{
		public MyButton(BrowseGUI bg){
			super();
			Element span = DOM.createElement("span");
			span.setAttribute("style", "display:block;float:right;position:relative;");
			span.setInnerText("+");
			Element input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute("style", "width:20px;height:20px;font-size:56px;opacity:0;position:absolute;left:0px");
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
			final VerticalMaterialPanel filePanel, BrowseGUI bg) {
		this.filePanel = filePanel;
		this.add(filePanel);
		this.providers = new FlowPanel();
		locationTube = new StandardButton(BrowseResources.INSTANCE.location_tube());
		locationLocal = new StandardButton(BrowseResources.INSTANCE.location_local());
		//MyButton local = new MyButton(bg);//StandardButton(AppResources.INSTANCE.folder());
		//providers.add(local);
		providers.add(locationTube);
		providers.add(locationLocal);
		providers.setStyleName("providers");
		this.add(providers);
		onResize();
	}

	@Override
	public void onResize() {
		int contentHeight = Window.getClientHeight() - BrowseGUI.HEADING_HEIGHT;
		this.setHeight(contentHeight + "px");
		this.filePanel.setWidth(Window.getClientWidth() - 70 + "px");
		this.filePanel.setHeight(Window.getClientHeight() - BrowseGUI.HEADING_HEIGHT
				- 10 + "px");
		this.providers.setHeight(Window.getClientHeight() - BrowseGUI.HEADING_HEIGHT
				- 10 + "px");
	}
}


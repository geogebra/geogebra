package geogebra.touch.gui.elements.ggt;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileContainer extends VerticalPanel{
	private FlowPanel localFileControlPanel;
	
	private HorizontalPanel localFilePages;
	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
	private StandardImageButton prevLocalButton = new StandardImageButton(LafIcons.arrow_go_previous());
	private StandardImageButton nextLocalButton = new StandardImageButton(LafIcons.arrow_go_next());
	public FileContainer(String string, Label headingMyProfile,
			final VerticalMaterialPanel localFilePanel) {
		this.addStyleName(string);
		this.add(headingMyProfile);
		this.add(localFilePanel);
		// Panel for page controls local files
				this.localFileControlPanel = new FlowPanel();
				this.localFileControlPanel.setStyleName("fileControlPanel");
				
				this.prevLocalButton.addStyleName("prevButton");
				this.localFileControlPanel.add(this.prevLocalButton);
				
				this.localFilePages = new HorizontalPanel();
				this.localFilePages.setStyleName("filePageControls");
				//TODO: add number buttons here
				
				this.localFileControlPanel.add(this.localFilePages);
				this.nextLocalButton.addStyleName("nextButton");
				this.localFileControlPanel.add(this.nextLocalButton);
				this.nextLocalButton.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						localFilePanel.nextPage();
						
					}});
				this.prevLocalButton.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						localFilePanel.prevPage();
						
					}});
				this.add(this.localFileControlPanel);
	}

}

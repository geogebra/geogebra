package geogebra.touch.gui.elements;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class AuxiliaryHeaderPanel extends FlowPanel{
	private StandardImageButton backButton;
	private HorizontalPanel panel;
	protected HorizontalPanel rightPanel;
	protected Label headerText;
	
	public AuxiliaryHeaderPanel(String title,Localization loc){
		this.backButton = new StandardImageButton(TouchEntryPoint.getLookAndFeel().getIcons().back());
		this.backButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TouchEntryPoint.goBack();
			}
		}, ClickEvent.getType());
		this.panel = new HorizontalPanel();
		this.rightPanel = new HorizontalPanel();
		this.panel.add(this.backButton);
		this.panel.add(new Label(loc.getMenu("Back")));
		
		this.panel.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.rightPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
		
		this.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		this.add(this.panel);
		this.add(this.rightPanel);
		this.headerText = new Label(title);
		this.add(this.headerText);
	}
	
	public void setText(String title) {
		this.headerText.setText(title);
		
	}
}

package geogebra.touch.gui.elements;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AuxiliaryHeaderPanel extends HorizontalPanel {
	
	private StandardImageButton backButton;
	private HorizontalPanel backPanel;
	protected HorizontalPanel searchPanel;
	protected VerticalPanel rightPanel;
	protected Label headerText;
	
	public AuxiliaryHeaderPanel(String title, Localization loc) {
		
		this.backButton = new StandardImageButton(TouchEntryPoint.getLookAndFeel().getIcons().back());
		this.backButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TouchEntryPoint.goBack();
			}
		}, ClickEvent.getType());
		
		this.backPanel = new HorizontalPanel();
		this.backPanel.setStyleName("headerLeft");
		this.backPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.backPanel.add(this.backButton);
		this.backPanel.add(new Label(loc.getMenu("Back")));
		
		this.rightPanel = new VerticalPanel();
		this.rightPanel.setStyleName("headerRight");
		
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.backPanel);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.headerText = new Label(title);
		this.add(this.headerText);
		
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightPanel);
	}
	
	public void setText(String title) {
		this.headerText.setText(title);
	}
}

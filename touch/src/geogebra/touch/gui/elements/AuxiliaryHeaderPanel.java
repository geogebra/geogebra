package geogebra.touch.gui.elements;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.CommonResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AuxiliaryHeaderPanel extends SimplePanel{
	private StandardImageButton backButton;
	protected HorizontalPanel panel;
	private DecoratorPanel decorator;
	public AuxiliaryHeaderPanel(){
		this.backButton = new StandardImageButton(CommonResources.INSTANCE.back());
		this.backButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TouchEntryPoint.showTabletGUI();
			}
		}, ClickEvent.getType());
		this.panel = new HorizontalPanel();
		this.panel.add(this.backButton);
		this.decorator = new DecoratorPanel();
		this.decorator.setWidget(this.panel);
		this.setWidget(this.decorator);
	}
}

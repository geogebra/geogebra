package geogebra.mobile.gui.elements;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class Picker
{
	PopinDialog popinDialog = new PopinDialog();
	Button labelButton;
	Button ok;
	int edges = 3;

	public Picker()
	{
		this.popinDialog.setHideOnBackgroundClick(true);

		VerticalPanel verticalPanel = new VerticalPanel();

		Button[] b = new Button[3];

		b[0] = new Button("+");
		b[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				Picker.this.edges++;
				Picker.this.labelButton.setText(Picker.this.edges + "");
			}
		}, ClickEvent.getType());

		b[1] = new Button("3");
		this.labelButton = b[1];
		b[1].getElement().getStyle().setBackgroundImage("none");

		b[2] = new Button("-");
		b[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (Picker.this.edges > 3)
				{
					Picker.this.edges--;
					Picker.this.labelButton.setText(Picker.this.edges + "");
				}
			}
		}, ClickEvent.getType());

		for (int i = 0; i < b.length; i++)
		{
			b[i].addStyleDependentName("picker");
			verticalPanel.add(b[i]);
		}

		this.ok = new Button("OK");
		this.ok.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				Picker.this.popinDialog.hide();
			}
		}, ClickEvent.getType());
		verticalPanel.add(this.ok);

		RoundPanel p = new RoundPanel(); 
		p.add(verticalPanel); 		
		
		this.popinDialog.add(p);
		this.popinDialog.center();
	}

	public void addHandler(ClickHandler clickHandler)
	{
		this.ok.addDomHandler(clickHandler, ClickEvent.getType());
	}

	public int getNumber()
	{
		return this.edges;
	}
}

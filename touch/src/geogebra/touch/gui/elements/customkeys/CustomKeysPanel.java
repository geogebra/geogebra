package geogebra.touch.gui.elements.customkeys;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class CustomKeysPanel extends PopupPanel
{
	public enum CustomKey
	{
		plus("+"), minus("-"), times("*"), divide("/"), power("^"), leftpar("("), rightpar(")"), squared("\u00B2"), degree("\u00B0"), pi("\u03C0"), leftbracket(
		    "["), rightbracket("]"), leftbrace("{"), rightbrace("}"), equals("=");

		String s;

		CustomKey(String s)
		{
			this.s = s;
		}

		@Override
		public String toString()
		{
			return this.s;
		}
	}

	private HorizontalPanel buttonContainer = new HorizontalPanel();

	private List<CustomKeyListener> listeners;

	public CustomKeysPanel()
	{
		super(false, false);

		this.listeners = new ArrayList<CustomKeyListener>();

		for (final CustomKey k : CustomKey.values())
		{
			Button b = new Button();
			b.setText(k.toString());

			// TODO Steffi!
			b.setSize("40px", "40px");

			b.addDomHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					fireClickEvent(k);
				}
			}, ClickEvent.getType());

			this.buttonContainer.add(b);
		}

		this.add(this.buttonContainer);
	}

	public void addCustomKeyListener(CustomKeyListener l)
	{
		this.listeners.add(l);
	}

	public void removeCustomKeyListener(CustomKeyListener l)
	{
		this.listeners.remove(l);
	}

	protected void fireClickEvent(CustomKey key)
	{
		for (CustomKeyListener c : this.listeners)
		{
			c.onCustomKeyPressed(key);
		}
	}
}
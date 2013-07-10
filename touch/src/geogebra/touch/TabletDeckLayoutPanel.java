package geogebra.touch;

import java.util.EmptyStackException;
import java.util.Stack;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabletDeckLayoutPanel extends DeckLayoutPanel
{
	private Stack<Widget> history;

	public TabletDeckLayoutPanel()
	{
		this.history = new Stack<Widget>();
	}

	@Override
	public void showWidget(Widget widget)
	{
		super.showWidget(widget);
		this.history.push(widget);
	}
	
	public boolean goBack()
	{
		try
		{
			this.showWidget(this.history.pop());
			return true;
		}
		catch(EmptyStackException e)
		{
			return false;
		}	
	}
}

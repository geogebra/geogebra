package geogebra.touch.gui.elements.ggt;

import java.util.ArrayList;
import java.util.List;

import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class SearchBar extends SimplePanel
{
	public interface SearchListener
	{
		void onSearch(String query);
	}

	private DecoratorPanel decorator;
	private HorizontalPanel panel;

	private TextBox query;
	private StandardImageButton searchButton;
	private List<SearchListener> listeners;

	public SearchBar()
	{
		this.listeners = new ArrayList<SearchListener>();

		this.decorator = new DecoratorPanel();
		this.panel = new HorizontalPanel();

		this.query = new TextBox();
		this.query.addKeyUpHandler(new KeyUpHandler()
		{

			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					SearchBar.this.fireSearchEvent();
				}
			}
		});

		this.searchButton = new StandardImageButton(CommonResources.INSTANCE.search());
		this.searchButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				SearchBar.this.fireSearchEvent();
			}
		}, ClickEvent.getType());

		this.panel.add(this.query);
		this.panel.add(this.searchButton);

		this.decorator.setWidget(this.panel);
		this.setWidget(this.decorator);
	}

	public boolean addSearchListener(SearchListener l)
	{
		return this.listeners.add(l);
	}

	public boolean removeSearchListener(SearchListener l)
	{
		return this.listeners.remove(l);
	}

	void fireSearchEvent()
	{
		for (SearchListener s : this.listeners)
		{
			s.onSearch(this.query.getText());
		}
	}

	public void setWidth(int width)
	{
		this.query.setWidth(width - 100 + "px");
	}

	@Override
	public int getOffsetHeight()
	{
		return this.searchButton.getOffsetHeight();
	}

	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth());
	}
}

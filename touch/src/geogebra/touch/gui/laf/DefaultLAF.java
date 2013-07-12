package geogebra.touch.gui.laf;

import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;

public class DefaultLAF extends AbstractLAF<ClickHandler>
{
	@Override
  public Type<ClickHandler> getStylBarEventType()
  {
	  return ClickEvent.getType();
  }
	
	@Override
  public ClickHandler getStylBarHandlerShowHide(final StylingBar stylingBar, final EuclidianViewPanel euclidianViewPanel)
  {
	  return new ClickHandler()
		{			
			@Override
			public void onClick(final ClickEvent event)
			{
				stylingBar.onTouchStartShowHide(event, euclidianViewPanel);
			}
		};
  }

	@Override
  public EventHandler getStylBarButtonHandler(final StylingBar stylingBar, final StandardImageButton newButton, final String process)
  {
	  return new ClickHandler()
		{			
			@Override
			public void onClick(final ClickEvent event)
			{
				stylingBar.onTouchStartStyleBarButton(event, newButton, process);
			}
		};
  }

	@Override
  public EventHandler getOptionalButtonHandler(final StylingBar stylingBar, final StandardImageButton standardImageButton, final OptionType type)
  {
		return new ClickHandler()
		{			
			@Override
			public void onClick(final ClickEvent event)
			{
				stylingBar.onTouchStartOptionalButton(event, standardImageButton, type);
			}
		};
  }
}

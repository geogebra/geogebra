package geogebra.touch.gui.laf;

import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;

public class AppleLAF extends DefaultLAF
{

	@Override
  public Type getStylBarEventType()
  {
	  return TouchStartEvent.getType();
  }

	@Override
  public EventHandler getStyleBarHandlerShowHide(final StylingBar stylingBar, final EuclidianViewPanel euclidianViewPanel)
  {
	  return new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				stylingBar.onTouchStartShowHide(event, euclidianViewPanel);
			}
		};
  }

	@Override
  public EventHandler getStyleBarButtonHandler(final StylingBar stylingBar, final StandardImageButton newButton, final String process)
  {
	  return new TouchStartHandler()
		{			
			@Override
			public void onTouchStart(final TouchStartEvent event)
			{
				stylingBar.onTouchStartStyleBarButton(event, newButton, process);
			}
		};
  }

	@Override
  public EventHandler getOptionalButtonHandler(final StylingBar stylingBar, final StandardImageButton standardImageButton, final OptionType type)
  {
		return new TouchStartHandler()
		{			
			@Override
			public void onTouchStart(final TouchStartEvent event)
			{
				stylingBar.onTouchStartOptionalButton(event, standardImageButton, type);
			}
		};
  }

}

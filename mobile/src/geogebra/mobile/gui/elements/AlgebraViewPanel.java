package geogebra.mobile.gui.elements;

import com.google.gwt.user.client.ui.HTML;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeMoveEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeMoveHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeStartEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeStartHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEvent.DIRECTION;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

public class AlgebraViewPanel extends LayoutPanel
{
	
	private ScrollPanel scrollPanel;
		
	public AlgebraViewPanel()
	{
		this.addStyleName("algebraview");

		this.scrollPanel = new ScrollPanel(); 
		this.scrollPanel.setSize("100%", "100%"); 
		add(this.scrollPanel); 
		
		TouchDelegate touchDelegate = new TouchDelegate(this);
//		touchDelegate.addSwipeStartHandler(new SwipeStartHandler()
//		{
//			@Override
//			public void onSwipeStart(SwipeStartEvent event)
//			{
//				AlgebraViewPanel.this.clear();
//				AlgebraViewPanel.this.add(new HTML("swipe start detected at: " + event.getTouch().getPageX() + " " + event.getTouch().getPageY()));
//			}
//		});
//
//		touchDelegate.addSwipeMoveHandler(new SwipeMoveHandler()
//		{
//			@Override
//			public void onSwipeMove(SwipeMoveEvent event)
//			{
//				AlgebraViewPanel.this.clear();
//				AlgebraViewPanel.this.add(new HTML("swipe move detected at: " + event.getTouch().getPageX() + " " + event.getTouch().getPageY()));
//			}
//		});

		touchDelegate.addSwipeEndHandler(new SwipeEndHandler()
		{

			@Override
			public void onSwipeEnd(SwipeEndEvent event)
			{
//				AlgebraViewPanel.this.add(new HTML("swipe end detected"));
				if (event.getDirection() == DIRECTION.LEFT_TO_RIGHT)
				{
					AlgebraViewPanel.this.setWidth("15%");
				}
				else if (event.getDirection() == DIRECTION.RIGHT_TO_LEFT)
				{
					AlgebraViewPanel.this.setWidth("5%");
				}

			}
		});
	}
}

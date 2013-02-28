package geogebra.mobile.gui.algebra;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraScrollPanel extends ScrollPanel
{

	Point oldPosition = null; 
	
	public AlgebraScrollPanel()
	{
		init(); 		
	}
	
	public AlgebraScrollPanel(Widget w)
	{
		super(w); 
		init();
	}

	private void init(){
		this.getElement().getStyle().setOverflow(Overflow.AUTO); 
		
		this.addDomHandler(new MouseDownHandler()
		{			
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				AlgebraScrollPanel.this.oldPosition = null; 
			}
		}, MouseDownEvent.getType()); 
		
		this.addDomHandler(new MouseMoveHandler()
		{			
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				
				if(AlgebraScrollPanel.this.oldPosition != null)
				{
					AlgebraScrollPanel.this.setHorizontalScrollPosition(AlgebraScrollPanel.this.getHorizontalScrollPosition() + (int) AlgebraScrollPanel.this.oldPosition.getX() - event.getX()); 
					AlgebraScrollPanel.this.setVerticalScrollPosition(AlgebraScrollPanel.this.getVerticalScrollPosition() + (int) AlgebraScrollPanel.this.oldPosition.getY() - event.getY()); 
					event.preventDefault(); 
				}
				AlgebraScrollPanel.this.oldPosition = new Point(event.getX(), event.getY()); 
			}
		}, MouseMoveEvent.getType()); 
		
		this.addDomHandler(new MouseUpHandler()
		{			
			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				AlgebraScrollPanel.this.oldPosition = null; 
			}
		}, MouseUpEvent.getType()); 
		
		this.addDomHandler(new MouseOverHandler()
		{			
			@Override
			public void onMouseOver(MouseOverEvent event)
			{
				AlgebraScrollPanel.this.oldPosition = null; 
			}
		}, MouseOverEvent.getType()); 
	}

}

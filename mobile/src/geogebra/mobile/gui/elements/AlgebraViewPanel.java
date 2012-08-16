package geogebra.mobile.gui.elements;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.mobile.algebra.AlgebraViewM;
import geogebra.mobile.controller.MobileAlgebraController;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEvent.DIRECTION;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

public class AlgebraViewPanel extends LayoutPanel
{

	protected AlgebraView algebraView;
	
	boolean small = false; 
	
	public AlgebraViewPanel()
	{
		this.addStyleName("algebraview");

		TouchDelegate touchDelegate = new TouchDelegate(this);

		touchDelegate.addTapHandler(new TapHandler(){
			@Override
      public void onTap(TapEvent event)
      {
				if(AlgebraViewPanel.this.small){
					extend(); 
				}
      }			
		});

		touchDelegate.addSwipeEndHandler(new SwipeEndHandler()
		{

			@Override
			public void onSwipeEnd(SwipeEndEvent event)
			{
				if (event.getDirection() == DIRECTION.LEFT_TO_RIGHT)
				{
					extend(); 
				}
				else if (event.getDirection() == DIRECTION.RIGHT_TO_LEFT)
				{
					AlgebraViewPanel.this.setWidth("5%");
					AlgebraViewPanel.this.small = true; 
					AlgebraViewPanel.this.remove((Widget) AlgebraViewPanel.this.algebraView); 
				}
			}
		});
	}

	protected void extend(){
		AlgebraViewPanel.this.setWidth("15%");
		add((Widget) this.algebraView); 
	}
	
	public void initAlgebraView(MobileAlgebraController ac, Kernel kernel)
	{
		this.algebraView = new AlgebraViewM(ac);
		kernel.attach(this.algebraView); 
		add((Widget) this.algebraView); 
	}

}

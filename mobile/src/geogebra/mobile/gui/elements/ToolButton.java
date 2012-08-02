package geogebra.mobile.gui.elements;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

import com.google.gwt.user.client.Window;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;


/**
 * A Button for the ToolBar, allowing an SVG graphic to be set as background <br>
 * css-styling: {@code -webkit-background-size: 100%;} <br>
 * for the correct size of the SVG
 * 
 * @see ButtonBarButtonBase
 * @author Matthias Meisinger
 * 
 */
public class ToolButton extends ButtonBarButtonBase
{

	public ToolButton(final ToolBarCommand cmd)
	{		

		super(null);
		this.addStyleName("toolbutton");
		
		super.getElement().getStyle().setBackgroundImage(cmd.getIconUrlAsString());

		this.addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				Window.alert("Mode: " + cmd.getMode());
			}
		});
		
		this.addTouchHandler(new TouchHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				click();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event) {
			}

			@Override
			public void onTouchEnd(TouchEndEvent event) {
				clickEnd();
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event) {
			}
		});

	}
	private void click() {
		this.addStyleName("button-active");
	}

	private void clickEnd() {
		this.removeStyleName("button-active");
	}

}

package geogebra.mobile.gui.elements;

import geogebra.mobile.utils.ToolBarCommand;

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
	}
}

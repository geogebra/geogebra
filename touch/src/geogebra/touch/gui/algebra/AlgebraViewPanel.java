package geogebra.touch.gui.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends DecoratorPanel
{
	private ScrollPanel scrollPanel;
	private AlgebraViewM algebraView;
	private Button button;

	/**
	 * Initializes the {@link TouchDelegate} and adds a {@link TapHandler} and a
	 * {@link SwipeEndHandler}.
	 * 
	 * Creates a {@link ScrollPanel} and adds the {@link AlgebraViewM algebraView}
	 * to it. Attaches the {@link AlgebraViewM algebraView} to the {@link Kernel
	 * kernel}.
	 * 
	 * @param controller
	 *          MobileAlgebraController
	 * @param kernel
	 *          Kernel
	 */
	public AlgebraViewPanel(TouchController controller, Kernel kernel)
	{
		this.scrollPanel = new ScrollPanel();
		this.algebraView = new AlgebraViewM(controller);
		kernel.attach(this.algebraView);

		this.getElement().getStyle().setBackgroundColor(GColor.WHITE.toString());
		this.scrollPanel.getElement().getStyle().setWidth(Window.getClientWidth() / 4, Unit.PX); 
		this.scrollPanel.getElement().getStyle().setHeight(Window.getClientHeight() * 3 / 4, Unit.PX); 

		this.algebraView.getElement().getStyle().setHeight(Window.getClientHeight() * 3 / 4, Unit.PX); 
		
		this.button = new Button();
		this.button.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				toggle();
			}
		}, ClickEvent.getType());

//		this.add(this.button);

		this.scrollPanel.add(this.algebraView);
		this.add(this.scrollPanel);

//		this.extend();
	}

	/**
	 * Extends the {@link AlgebraViewPanel}.
	 */
	protected void extend()
	{
		SVGResource icon = CommonResources.INSTANCE.algebra_close();
		this.button.getElement().setInnerHTML("<img src=\"" + icon.getSafeUri().asString() + "\" style=\"width: 20px; height: 20px;\">");
		this.scrollPanel.setVisible(true);
	}

	protected void minimize()
	{
		SVGResource icon = CommonResources.INSTANCE.algebra_open();
		this.button.getElement().setInnerHTML("<img src=\"" + icon.getSafeUri().asString() + "\" style=\"width: 20px; height: 20px;\">");
		this.scrollPanel.setVisible(false);
	}

	protected void toggle()
	{
		if (this.scrollPanel.isVisible())
		{
			minimize();
		}
		else
		{
			extend();
		}
	}

	public AlgebraView getAlgebraView()
	{
		return this.algebraView;
	}
}
package geogebra.touch.gui.algebra;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends VerticalPanel
{
	private ScrollPanel scrollPanel;
	private AlgebraViewM algebraView;
	private Button button;

	/**
	 * Initializes the {@link TouchDelegate} and adds a {@link TapHandler} and a
	 * {@link SwipeEndHandler}.
	 */
	public AlgebraViewPanel(TouchController controller, Kernel kernel)
	{
		this.initAlgebraView(controller, kernel);
	}

	/**
	 * Creates a {@link ScrollPanel} and adds the {@link AlgebraViewM algebraView}
	 * to it. Attaches the {@link AlgebraViewM algebraView} to the {@link Kernel
	 * kernel}.
	 * 
	 * @param controller
	 *          MobileAlgebraController
	 * @param kernel
	 *          Kernel
	 */
	private void initAlgebraView(TouchController controller, Kernel kernel)
	{
		this.scrollPanel = new ScrollPanel();
		this.algebraView = new AlgebraViewM(controller);
		kernel.attach(this.algebraView);

		this.button = new Button();
		this.button.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				toggle();
			}
		}, ClickEvent.getType());

		this.add(this.button);

		this.scrollPanel.add(this.algebraView);
		this.add(this.scrollPanel);

		this.extend();
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
}
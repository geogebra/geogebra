package geogebra.touch.gui.algebra;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.touch.controller.TouchController;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends ScrollPanel
{
	private AlgebraViewM algebraView;

	// private Button button;

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
		this.algebraView = new AlgebraViewM(controller);
		kernel.attach(this.algebraView);

		this.setWidget(this.algebraView);
	}

	public AlgebraView getAlgebraView()
	{
		return this.algebraView;
	}
	
	@Override
	public void setVisible(boolean flag){
		super.setVisible(flag);
		this.algebraView.setShowing(flag);
	}
}
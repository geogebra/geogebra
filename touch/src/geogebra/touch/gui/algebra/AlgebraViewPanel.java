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

public class AlgebraViewPanel extends LayoutPanel
{
	private AlgebraViewM algebraView;

	private ScrollPanel scroller;
	
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
		this.scroller = new ScrollPanel();

		this.scroller.setWidget(this.algebraView);
		
		this.setStyleName("algebraView");
		
		this.scroller.setStyleName("algebraView-scroller");
		
		this.add(this.scroller);
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

	public void setLabels() {
		if(this.algebraView != null){
			this.algebraView.setLabels();
		}
	}
}
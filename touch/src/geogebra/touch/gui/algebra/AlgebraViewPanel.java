package geogebra.touch.gui.algebra;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends FlowPanel {
	private final AlgebraViewM algebraView;
	private final AlgebraButton arrow;
	private final FlowPanel stylebar;
	private ScrollPanel content;

	/**
	 * Initializes the {@link TouchDelegate} and adds a {@link TapHandler} and a
	 * {@link SwipeEndHandler}.
	 * 
	 * Creates a {@link ScrollPanel} and adds the {@link AlgebraViewM
	 * algebraView} to it. Attaches the {@link AlgebraViewM algebraView} to the
	 * {@link Kernel kernel}.
	 * 
	 * @param controller
	 *            MobileAlgebraController
	 * @param kernel
	 *            Kernel
	 */
	public AlgebraViewPanel(TouchController controller, TabletGUI gui,
			Kernel kernel) {
		this.algebraView = new AlgebraViewM(controller);
		kernel.attach(this.algebraView);
		this.stylebar = new FlowPanel();
		this.arrow = new AlgebraButton(gui);
		this.stylebar.add(this.arrow);
		this.stylebar.setStyleName("algebraStylebar");
		this.add(this.stylebar);
		this.stylebar.setVisible(false);
		this.setStyleName("algebraViewAndStylebar");

		this.content = new ScrollPanel(this.algebraView);
		this.content.setStyleName("algebraView");

		onResize();
		this.add(this.content);
	}

	public void addInsideArrow() {
		this.stylebar.setVisible(true);

	}

	public AlgebraView getAlgebraView() {
		return this.algebraView;
	}

	public void removeInsideArrow() {
		this.stylebar.setVisible(false);

	}

	public void setLabels() {
		if (this.algebraView != null) {
			this.algebraView.setLabels();
		}
	}

	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		this.algebraView.setShowing(flag);
	}

	public void onResize() {
		// Important: Set ViewPort size in Pixels for the ScrollPanel!
		this.content.setWidth(TabletGUI.computeAlgebraWidth() + "px");

		LookAndFeel laf = TouchEntryPoint.getLookAndFeel();

		this.content
				.setHeight((Window.getClientHeight() - laf.getAppBarHeight()
						- laf.getToolBarHeight() - this.stylebar
							.getOffsetHeight()) + "px");
	}
}
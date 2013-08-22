package geogebra.touch.gui.algebra;

import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Extends from {@link FlowPanel}. Holds the instances of the
 * {@link AlgebraView algebraView} and {@link ScrollPanel scrollPanel}.
 */

public class AlgebraViewPanel extends FlowPanel implements ResizeListener {
	private final AlgebraViewT algebraView;
	private FastButton arrow;
	private final FlowPanel stylebar;
	private final ScrollPanel content;
	final TabletGUI gui;

	/**
	 * Initializes the {@link TouchDelegate} and adds a {@link TapHandler} and a
	 * {@link SwipeEndHandler}.
	 * 
	 * Creates a {@link ScrollPanel} and adds the {@link AlgebraViewT
	 * algebraView} to it. Attaches the {@link AlgebraViewT algebraView} to the
	 * {@link Kernel kernel}.
	 * 
	 * @param controller
	 *            MobileAlgebraController
	 * @param kernel
	 *            Kernel
	 */
	public AlgebraViewPanel(final TouchController controller,
			final Kernel kernel) {
		this.gui = (TabletGUI) ((TouchApp) kernel.getApplication())
				.getTouchGui();
		this.algebraView = new AlgebraViewT(controller);
		kernel.attach(this.algebraView);
		this.stylebar = new FlowPanel();

		this.arrow = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().triangle_left());
		this.arrow.setStyleName("arrowRight");
		this.arrow.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				AlgebraViewPanel.this.gui.toggleAlgebraView();
				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel()
							.enableDisableButtons();
				}
			}
		});

		this.stylebar.add(this.arrow);
		this.stylebar.setStyleName("algebraStylebar");
		this.add(this.stylebar);
		this.stylebar.setVisible(false);
		this.setStyleName("algebraViewAndStylebar");

		this.content = new ScrollPanel(this.algebraView);
		this.content.setStyleName("algebraView");
		this.add(this.content);

		this.gui.addResizeListener(this);
	}

	private void addInsideArrow() {
		this.stylebar.setVisible(true);
	}

	private void removeInsideArrow() {
		this.stylebar.setVisible(false);
	}

	public AlgebraView getAlgebraView() {
		return this.algebraView;
	}

	public void setLabels() {
		if (this.algebraView != null) {
			this.algebraView.setLabels();
		}
	}

	@Override
	public void setVisible(final boolean flag) {
		super.setVisible(flag);
		this.algebraView.setShowing(flag);
	}

	@Override
	public void onResize() {
		if (this.gui.isAlgebraShowing()) {
			this.content.setWidth(TabletGUI.computeAlgebraWidth() + "px");
			this.content
					.setHeight((TouchEntryPoint.getLookAndFeel()
							.getContentWidgetHeight() - this.stylebar
							.getOffsetHeight())
							+ "px");

			if (Window.getClientWidth() - TabletGUI.computeAlgebraWidth() <= 0) {
				this.addInsideArrow();
			} else {
				this.removeInsideArrow();
			}
		}
	}
}
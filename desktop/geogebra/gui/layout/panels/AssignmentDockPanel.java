package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.assignment.AssignmentStyleBarD;
import geogebra.gui.view.assignment.AssignmentViewD;
import geogebra.main.AppD;

import javax.swing.JComponent;

/**
 * @author Christoph Reinisch
 * 
 */
public class AssignmentDockPanel extends DockPanel {

	private static final long serialVersionUID = 1L;

	private AssignmentViewD view;

	public AssignmentDockPanel(AppD app) {
		super(App.VIEW_ASSIGNMENT, // view id
				"Assignment", // view title phrase
				null, // toolbar string
				true, // style bar?
				8, // menu order
				'Q' // ctrl-shift-Q
		);

		this.app = app;
		this.setOpenInFrame(false);
		// this.setShowStyleBar(true);
		// super.setDialog(true);
	}

	protected AssignmentViewD getAssignmentView() {
		return (AssignmentViewD) app.getGuiManager().getAssignmentView();
	}

	protected JComponent loadComponent() {
		view = getAssignmentView();

		// if (isOpenInFrame())
		// view.windowPanel();
		// else
		// view.unwindowPanel();

		return view.getWrappedPanel();
	}

	protected JComponent loadStyleBar() {
		return ((AssignmentStyleBarD) getAssignmentView().getStyleBar())
				.getWrappedPanel();
	}


}
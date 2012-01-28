package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * @author Christoph Reinisch
 *
 */
public class AssignmentDockPanel extends DockPanel {

	public AssignmentDockPanel(Application app) {
		super(
			AbstractApplication.VIEW_ASSIGNMENT, 	// view id
			"Assignment", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			8,						// menu order
			'Q' // ctrl-shift-Q
		);
		
		this.app = app;
		this.setOpenInFrame(false);
	}

	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getAssignmentView();
	}

	protected JComponent loadStyleBar() {
		return app.getGuiManager().getAssignmentView().getStyleBar();
	}


}

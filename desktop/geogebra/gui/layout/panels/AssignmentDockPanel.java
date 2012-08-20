package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import javax.swing.JComponent;

/**
 * @author Christoph Reinisch
 *
 */
public class AssignmentDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	public AssignmentDockPanel(AppD app) {
		super(
			App.VIEW_ASSIGNMENT, 	// view id
			"Assignment", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			8,						// menu order
			'Q' // ctrl-shift-Q
		);
		
		this.app = app;
		this.setOpenInFrame(false);
	}

	@Override
	protected JComponent loadComponent() {
		return app.getGuiManagerD().getAssignmentView();
	}

	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManagerD().getAssignmentView().getStyleBar();
	}


}

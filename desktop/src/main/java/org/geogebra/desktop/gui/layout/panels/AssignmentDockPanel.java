package org.geogebra.desktop.gui.layout.panels;

import javax.swing.JComponent;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.main.AppD;

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
		
		setApp(app);
		this.setOpenInFrame(false);
	}

	@Override
	protected JComponent loadComponent() {
		return ((GuiManagerD)app.getGuiManager()).getAssignmentView();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD)app.getGuiManager()).getAssignmentView().getStyleBar();
	}


}

package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

public class PythonDockPanel extends DockPanel {

	private static final long serialVersionUID = 1L;

	public PythonDockPanel(AppD app) {
		super(
				App.VIEW_PYTHON, 		// view id
				"Python", 						// view title phrase
				null, //getDefaultToolbar(),				// toolbar string
				false,								// style bar? TODO: add style bar
				4, 									// menu order
				'Y'									// menu shortcut
		);
		this.app = app;
	}
	@Override
	protected JComponent loadComponent() {
		// TODO Auto-generated method stub
		return app.getPythonBridge().getComponent();
	}
	
	@Override
	protected JMenuBar loadMenuBar() {
		return app.getPythonBridge().getMenuBar();
	}
}

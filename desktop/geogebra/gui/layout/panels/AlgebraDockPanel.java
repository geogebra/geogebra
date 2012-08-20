package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.algebra.AlgebraViewD;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * Dock panel for the algebra view.
 */
public class AlgebraDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private AppD app;
	
	/**
	 * @param app
	 */
	public AlgebraDockPanel(AppD app) {
		super(
			App.VIEW_ALGEBRA,	// view id 
			"AlgebraWindow", 			// view title phrase
			null,						// toolbar string
			true,						// style bar?
			2, 							// menu order
			'A'							// menu shortcut
		);
		
		this.app = app;
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((AlgebraViewD) app.getGuiManager().getAlgebraView()).getHelperBar();
	}
	
	@Override
	protected JComponent loadComponent() {	
		JScrollPane scrollPane = new JScrollPane((Component) app.getGuiManager().getAlgebraView());
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setBackground(Color.white);
		
		return scrollPane;
	}
	

	@Override
	protected void setActiveToolBar(){
		//use the focused euclidian view for active toolbar
		if(dockManager.getFocusedEuclidianPanel()==null || !dockManager.getFocusedEuclidianPanel().hasToolbar()) {
			((GuiManagerD) app.getGuiManager()).getToolbarPanel().setActiveToolbar(-1);
		} else {
			((GuiManagerD) app.getGuiManager()).getToolbarPanel().setActiveToolbar(dockManager.getFocusedEuclidianPanel().getToolbar());
		}
	}
	
	@Override
	public ImageIcon getIcon() { 
			return app.getImageIcon("view-algebra24.png");
	}
	
}

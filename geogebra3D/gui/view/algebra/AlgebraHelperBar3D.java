package geogebra3D.gui.view.algebra;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import geogebra.gui.view.algebra.AlgebraHelperBar;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.Application;

/**
 * Helper bar for algebra view in 3D
 * @author matthieu
 *
 */
public class AlgebraHelperBar3D extends AlgebraHelperBar {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton treeModeView;

	/**
	 * @param algebraView
	 * @param app
	 */
	public AlgebraHelperBar3D(AlgebraView algebraView, Application app) {
		super(algebraView, app);
	}
	
	protected void addButtons(){
		
		super.addButtons();
		
		//addSeparator();
		
		treeModeView = new JButton(app.getImageIcon("tree-view.png"));
		treeModeView.addActionListener(this);
		add(treeModeView);
		
	}
	
	public void updateLabels() {
		super.updateLabels();
		if(algebraView.getTreeMode() == algebraView.MODE_VIEW) {
			treeModeView.setToolTipText(app.getPlainTooltip("TreeModeDependency"));
		} else {
			treeModeView.setToolTipText(app.getPlainTooltip("TreeModeView"));
		}	
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == treeModeView) {
			algebraView.setTreeMode((algebraView.getTreeMode() != AlgebraView.MODE_VIEW) ? AlgebraView.MODE_VIEW : AlgebraView.MODE_DEPENDENCY);
			treeModeView.setSelected(algebraView.getTreeMode() == AlgebraView.MODE_VIEW);
			toggleTypeTreeMode.setSelected(false);
			updateLabels();
		} else  {
			if(e.getSource() == toggleTypeTreeMode)
				treeModeView.setSelected(false);
			super.actionPerformed(e);
		}
	}

}

package geogebra3D.gui.view.algebra;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.gui.view.algebra.AlgebraHelperBar;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

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
	public AlgebraHelperBar3D(AlgebraView algebraView, AppD app) {
		super(algebraView, app);
	}
	
	@Override
	protected void addButtons(){
		
		super.addButtons();
		
		//addSeparator();
		
		treeModeView = new JButton(app.getImageIcon("tree-view.png"));
		treeModeView.addActionListener(this);
		add(treeModeView);
		
	}
	
	@Override
	public void updateLabels() {
		super.updateLabels();
		if(algebraView.getTreeMode().equals(SortMode.VIEW)) {
			treeModeView.setToolTipText(app.getPlainTooltip("TreeModeDependency"));
		} else {
			treeModeView.setToolTipText(app.getPlainTooltip("TreeModeView"));
		}	
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == treeModeView) {
			algebraView.setTreeMode((!algebraView.getTreeMode().equals(SortMode.VIEW)) ? SortMode.VIEW : SortMode.DEPENDENCY);
			treeModeView.setSelected(algebraView.getTreeMode().equals(SortMode.VIEW));
			toggleTypeTreeMode.setSelected(false);
			updateLabels();
		} else  {
			if(e.getSource() == toggleTypeTreeMode)
				treeModeView.setSelected(false);
			super.actionPerformed(e);
		}
	}

}

package geogebra.gui.layout.panels;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.layout.DockPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Abstract class for all "euclidian" panels. 
 * 
 * @author matthieu
 * @remark {@link #getEuclidianView()} has to be overridden if {@link #getComponent()}
 * 			does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelAbstract extends DockPanel {
	/** */
	private static final long serialVersionUID = 1L;
	
	
	private boolean hasEuclidianFocus;

	/**
	 * default constructor
	 * @param id
	 * @param title
	 * @param toolbar
	 * @param hasStyleBar
	 * @param menuOrder
	 */
	public EuclidianDockPanelAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder, char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder, shortcut);
	}

	public void mousePressed(MouseEvent arg0) {
		super.mousePressed(arg0);
		dockManager.setFocusedPanel(this);
	}
	
	/**
	 * @return The euclidian view associated with this dock panel.
	 * @remark This method has to be overridden if the component of the 
	 * 			dock panel is not the euclidian view itself
	 */
	public EuclidianViewInterface getEuclidianView() {
		return (EuclidianViewInterface)getComponent();
	}
	
	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * @param hasFocus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}
	
	/**
	 * create the focus panel (composed of titleLabel, and, for EuclidianDockPanels, focus icon)
	 * @return the focus panel
	 */
	protected JComponent createFocusPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//add title label
		panel.add(super.createFocusPanel(), BorderLayout.WEST);
		
		return panel;
	}

}

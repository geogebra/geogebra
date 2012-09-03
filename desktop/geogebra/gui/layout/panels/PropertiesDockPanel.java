package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.properties.PropertiesStyleBarD;
import geogebra.gui.view.properties.PropertiesViewD;
import geogebra.main.AppD;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Dock panel for the algebra view.
 */
public class PropertiesDockPanel extends DockPanel implements
		WindowFocusListener {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private PropertiesViewD view;

	JDialog dialog = null;

	/**
	 * @param app
	 */
	public PropertiesDockPanel(AppD app) {
		super(App.VIEW_PROPERTIES, // view id
				"GeoGebraProperties", // view title phrase
				null, // toolbar string
				true, // style bar?
				-1, // menu order
				'E' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		super.setDialog(true);

	}
	

	private void getPropertiesView() {
		view = (PropertiesViewD) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected JComponent loadComponent() {

		getPropertiesView();

		if (isOpenInFrame())
			view.windowPanel();
		else
			view.unwindowPanel();
		return view.getWrappedPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		getPropertiesView();
		return ((PropertiesStyleBarD) view.getStyleBar()).getWrappedPanel();
	}

	@Override
	protected void windowPanel() {
		super.windowPanel();
		getPropertiesView();
		view.windowPanel();
	}

	@Override
	protected void unwindowPanel() {
		super.unwindowPanel();
		getPropertiesView();
		view.unwindowPanel();
	}
	
	@Override
	protected void closePanel(boolean isPermanent) {
		super.closePanel(isPermanent);		
		getPropertiesView();
		view.applyModifications();
	}
	

	@Override
	public ImageIcon getIcon() {
		return app.getImageIcon("view-properties22.png");
	}

	@Override
	public void createFrame() {

		super.createFrame();

		getFrame().addWindowFocusListener(this);

	}

	@Override
	public void updateLabels() {
		super.updateLabels();
		if (view != null) {
			titleLabel
					.setText(view.getTypeString(view.getSelectedOptionType()));
		}
	}

	/**
	 * Update all elements in the title bar.
	 */
	@Override
	public void updateTitleBar() {
		super.updateTitleBar();
		titleLabel.setVisible(true);
	}

	public void windowGainedFocus(WindowEvent arg0) {
		//

	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	/*
	 * Window Listener
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		/*
		 * if (!isModal()) { geoTree.setSelected(null, false);
		 * //selectionChanged(); } repaint();
		 */
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		getPropertiesView();
		view.applyModifications();
		closeDialog();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	public void closeDialog() {
		view.closeDialog();
	}

	@Override
	public void setFocus(boolean hasFocus, boolean updatePropertiesView) {

		// no action on properties view

		setFocus(hasFocus);
	}
	
	/**
	 * update menubar (and dockbar) on visibility changes 
	 */
	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);
		if (isVisible || (view!=null)){
			dockManager.getLayout().getApplication().updateMenubar();
			getPropertiesView();
			view.setSelectedOptionPanelVisible(isVisible);
		}
	}

}

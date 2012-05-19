package geogebra.gui.layout.panels;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.properties.PropertiesView;
import geogebra.main.Application;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Dock panel for the algebra view.
 */
public class PropertiesDockPanel extends DockPanel implements
		WindowFocusListener {
	private static final long serialVersionUID = 1L;
	private Application app;
	private PropertiesView view;

	JDialog dialog = null;

	/**
	 * @param app
	 */
	public PropertiesDockPanel(Application app) {
		super(AbstractApplication.VIEW_PROPERTIES, // view id
				"Properties", // view title phrase
				null, // toolbar string
				false, // style bar?
				7, // menu order
				'E' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		// this.setShowStyleBar(true);
		super.setDialog(true);

	}

	@Override
	protected JComponent loadComponent() {

		view = app.getGuiManager().getPropertiesView();

		if (isOpenInFrame())
			view.windowPanel();
		else
			view.unwindowPanel();
		return view;
	}

	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getPropertiesView().getStyleBar();
	}

	@Override
	protected void windowPanel() {
		super.windowPanel();
		app.getGuiManager().getPropertiesView().windowPanel();
	}

	@Override
	protected void unwindowPanel() {
		super.unwindowPanel();
		app.getGuiManager().getPropertiesView().unwindowPanel();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getImageIcon("view-properties24.png");
	}

	@Override
	public void createFrame() {

		super.createFrame();

		frame.addWindowFocusListener(this);
		frame.addWindowListener(this);

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
	public void windowActivated(WindowEvent e) {
		/*
		 * if (!isModal()) { geoTree.setSelected(null, false);
		 * //selectionChanged(); } repaint();
		 */
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		// cancel();
		closeDialog();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void closeDialog() {
		view.closeDialog();
	}
	
	
}

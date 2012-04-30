package geogebra.gui.layout.panels;

import java.awt.event.WindowEvent;

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
		GeoElementSelectionListener {
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
				true, // style bar?
				7, // menu order
				'E' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		this.setShowStyleBar(true);
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

		// create a dialog
		if (dialog == null) {
			dialog = new JDialog(app.getFrame(), false);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setResizable(true);

			dialog.addWindowListener(this);
		}

		// call super to create a hidden frame with PropertiesView content
		super.createFrame(false);

		// swap the contents from the frame to the dialog
		dialog.setContentPane(frame.getContentPane());
		dialog.setLocation(frame.getLocation());
		dialog.setBounds(frame.getBounds());
		dialog.setTitle(frame.getTitle());
		// dialog.pack();
		dialog.setVisible(true);

	}

	@Override
	public void removeFrame() {
		frame.setContentPane(dialog.getContentPane());
		dialog.setVisible(false);
		super.removeFrame();
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER
				|| app.getCurrentSelectionListener() != this) {
			app.setSelectionListenerMode(this);
			view.selectionChanged();
		}
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		view.geoElementSelected(geo, addToSelection);

	}

}

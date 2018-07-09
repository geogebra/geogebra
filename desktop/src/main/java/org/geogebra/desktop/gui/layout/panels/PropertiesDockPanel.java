package org.geogebra.desktop.gui.layout.panels;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.view.properties.PropertiesStyleBarD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the algebra view.
 */
public class PropertiesDockPanel extends DockPanelD
		implements WindowFocusListener {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private PropertiesViewD view;
	private boolean closed;


	/**
	 * @param app
	 *            application
	 */
	public PropertiesDockPanel(AppD app) {
		super(App.VIEW_PROPERTIES, // view id
				"Preferences", // view title phrase
				null, // toolbar string
				true, // style bar?
				-1, // menu order
				'E' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(false);
		super.setDialog(true);

	}

	private void getPropertiesView() {
		view = (PropertiesViewD) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected JComponent loadComponent() {

		getPropertiesView();

		if (isOpenInFrame()) {
			view.windowPanel();
		} else {
			view.unwindowPanel();
		}
		return view.getWrappedPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		getPropertiesView();
		return ((PropertiesStyleBarD) view.getStyleBar()).getWrappedPanel();
	}

	@Override
	protected void updateTitleBarIfNecessary() {
		updateTitleBar();
	}

	@Override
	public void windowPanel() {
		super.windowPanel();
		getPropertiesView();
		view.windowPanel();
	}

	@Override
	public void unwindowPanel() {
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
		return app.getMenuIcon(GuiResourcesD.VIEW_PROPERTIES_22);
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

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		// only handle windowClosing
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		// only handle windowClosing
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (!closed) {
			closed = true;
			getPropertiesView();
			view.applyModifications();
			closeDialog();
		}
	}

	public void closeDialog() {
		view.closeDialog();
	}

	@Override
	public void setFocus(boolean hasFocus, boolean updatePropertiesView) {
		// no action on properties view
	}

	/**
	 * update menubar (and dockbar) on visibility changes
	 */
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible || (view != null)) {
			dockManager.getLayout().getApplication().updateMenubar();
			getPropertiesView();
			view.setSelectedOptionPanelVisible(isVisible);
		}
		if (isVisible) {
			closed = false;
		}
	}

	@Override
	protected void setFocus(boolean hasFocus) {
		// nothing to do for properties view
	}

}

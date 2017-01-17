package org.geogebra.desktop.gui.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.DockBarInterface;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Toolbar to hold launching buttons for minimized views.
 * 
 * @author G. Sturr
 * 
 */
public class DockBar extends JPanel implements SetLabels, DockBarInterface {

	private static final long serialVersionUID = 1L;

	private AppD app;

	private PerspectivePanel popup;
	private JPanel slimSidebarPanel;
	private JLabel lblIcon;

	private boolean isEastOrientation = true;
	private boolean showButtonBar = false;


	/***************************************************
	 * Constructor
	 * 
	 * @param app
	 */
	public DockBar(AppD app) {

		this.app = app;
		setBorder(BorderFactory.createEmptyBorder());
		initGUI();

	}

	// ==============================
	// GUI
	// ==============================

	private void initGUI() {

		// buildButtonPanel();
		buildSlimSidebarPanel();
		slimSidebarPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0,
				SystemColor.controlShadow));
		// buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1,
		// SystemColor.controlShadow));

		registerListeners();
		setLayout(new BorderLayout());
		updateLayout();
	}

	private void registerListeners() {
		SidebarMouseListener l = new SidebarMouseListener();
		slimSidebarPanel.addMouseListener(l);
	}

	/**
	 * 
	 * @return a new perspective panel
	 */
	PerspectivePanel newPerspectivePanel() {
		return new PerspectivePanel(app, this);
	}

	/**
	 * Creates sidebarButtonPanel, a slim vertical bar that acts a button to
	 * open the perspective popup.
	 * 
	 */
	private JPanel buildSlimSidebarPanel() {

		if (slimSidebarPanel == null) {
			slimSidebarPanel = new JPanel(new BorderLayout(0, 0));

			lblIcon = new JLabel(
					app.getScaledIcon(GuiResourcesD.DOCKBAR_TRIANGLE_LEFT));
			lblIcon.setPreferredSize(new Dimension(10, 0));

			slimSidebarPanel.add(lblIcon, BorderLayout.CENTER);

			slimSidebarPanel.setBackground(null);

		}

		return slimSidebarPanel;
	}

	/**
	 * set sidebar triangle orientation
	 * 
	 * @param popupIsVisible
	 *            right if true, left if false
	 */
	public void setSidebarTriangle(boolean popupIsVisible) {

		if (popupIsVisible ^ !isEastOrientation()) {
			lblIcon.setIcon(
					app.getImageIcon(GuiResourcesD.DOCKBAR_TRIANGLE_RIGHT));
		} else {
			lblIcon.setIcon(
					app.getImageIcon(GuiResourcesD.DOCKBAR_TRIANGLE_LEFT));
		}

		slimSidebarPanel.repaint();

	}

	// ==============================
	// Event Handlers
	// ==============================

	/**
	 * Updates the layout to display either (1) a minimized panel that acts as a
	 * button (slimSidebarPanel) or (2) a panel with buttons to hide/show views
	 * (buttonPanel)
	 */
	protected void updateLayout() {

		removeAll();

		if (!showButtonBar) {
			add(slimSidebarPanel, BorderLayout.CENTER);
		}
		revalidate();
	}

	@Override
	public void showPopup() {

		popup = newPerspectivePanel();
		int horizontal;
		if (isEastOrientation()) {
			horizontal = -popup.getPreferredSize().width;
		} else {
			horizontal = slimSidebarPanel.getPreferredSize().width;
		}

		int y = (slimSidebarPanel.getHeight() - popup.getPreferredSize().height)
				/ 2;

		popup.show(this, horizontal, y);

		if (!popup.isVisible()) {
			// has mouse
			popup.superSetVisible(true);
		}
	}

	@Override
	public void hidePopup() {
		if (popup != null && popup.isVisible()) {
			popup.superSetVisible(false);
		}
	}

	void togglePopup() {
		if (popup == null || !popup.isVisible()) {
			showPopup();
		} else {
			popup.superSetVisible(false);
		}

	}

	@Override
	public void setLabels() {

		// btnProperties.setToolTipText(app.getMenu("Layout"));
		// btnKeyboard.setToolTipText(app.getPlain("Keyboard"));

		// btnPrint.setToolTipText(app.getMenu("Print"));
		// btnFileSave.setToolTipText(app.getMenu("Save"));
		// btnFileOpen.setToolTipText(app.getMenu("Load"));

		// updateViewButtons();

	}

	private boolean sideBarHasMouse = false;

	void setSideBarHasMouse(boolean flag) {
		sideBarHasMouse = flag;
	}

	/**
	 * 
	 * @return true if side bar has mouse
	 */
	public boolean sideBarHasMouse() {
		return sideBarHasMouse;
	}

	// ==============================
	// Listeners
	// ==============================

	/**
	 * Mouse listener to handle mouse events for the sidebar.
	 */
	public class SidebarMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 0) {
				togglePopup();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getSource() == slimSidebarPanel) {
				slimSidebarPanel.setBackground(Color.LIGHT_GRAY);
				setSideBarHasMouse(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() == slimSidebarPanel) {
				slimSidebarPanel.setBackground(null);
				setSideBarHasMouse(false);
			}
		}

	}

	// ==============================
	// Getters/Setters
	// ==============================

	@Override
	public boolean isShowButtonBar() {
		return showButtonBar;
	}

	@Override
	public void setShowButtonBar(boolean showButtonBar) {
		this.showButtonBar = showButtonBar;
		updateLayout();
	}

	@Override
	public boolean isEastOrientation() {
		return isEastOrientation;
	}

	@Override
	public void setEastOrientation(boolean isEastOrientation) {
		this.isEastOrientation = isEastOrientation;
		setSidebarTriangle(popup != null && popup.isVisible());
	}

	/***********************************************
	 * Popup class
	 */
	static class MyPopup extends JPopupMenu {

		private static final long serialVersionUID = 1L;

		public MyPopup() {
			super();
			setOpaque(true);
			setBackground(SystemColor.control);
			setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 8));

		}

		@Override
		public void setVisible(boolean isVisible) {

			super.setVisible(isVisible);
		}

	}

}

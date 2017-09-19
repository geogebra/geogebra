package org.geogebra.desktop.gui.layout.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;

/**
 * Abstract class for all "euclidian" panels.
 * 
 * @author Mathieu
 * Remark: {@link #getEuclidianView()} has to be overridden if
 *         {@link #getComponent()} does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelAbstract extends DockPanelD
		implements GetViewId {
	/** */
	private static final long serialVersionUID = 1L;

	private boolean hasEuclidianFocus;
	private JPanel panel;
	/**
	 * Component of the construction protocol navigation bar, invisible if not
	 * needed.
	 */
	private ConstructionProtocolNavigationD consProtNav;

	/**
	 * default constructor
	 * 
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

	@Override
	public void mousePressed(MouseEvent arg0) {
		super.mousePressed(arg0);
		dockManager.setFocusedPanel(this);
	}

	/**
	 * @return The euclidian view associated with this dock panel.
	 * Remark: This method has to be overridden if the component of the dock
	 *         panel is not the euclidian view itself
	 */
	abstract public EuclidianView getEuclidianView();

	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * 
	 * @param hasFocus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}

	@Override
	protected boolean titleIsBold() {
		return super.titleIsBold() || hasEuclidianFocus;
	}

	/**
	 * create the focus panel (composed of titleLabel, and, for
	 * EuclidianDockPanels, focus icon)
	 * 
	 * @return the focus panel
	 */
	@Override
	protected JComponent createFocusPanel() {
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());

		// add title label
		panel1.add(super.createFocusPanel(),
				app.getLocalization().borderWest());

		return panel1;
	}

	@Override
	public boolean updateResizeWeight() {
		return true;
	}

	@Override
	protected void setStyleBar() {
		super.setStyleBar();
		((EuclidianStyleBar) styleBar).resetFirstPaint();
	}

	@Override
	protected final JComponent loadComponent() {
		if (panel == null) {
			panel = new JPanel(new BorderLayout());

			panel.add(
					((EuclidianViewInterfaceD) getEuclidianView()).getJPanel(),
					BorderLayout.CENTER);

			consProtNav = (ConstructionProtocolNavigationD) app.getGuiManager()
					.getConstructionProtocolNavigation(id);

			ConstructionProtocolSettings cps = app.getSettings()
					.getConstructionProtocol();
			consProtNav.settingsChanged(cps);
			cps.addListener(consProtNav);

			if (app.getShowCPNavNeedsUpdate(id)) {
				app.setShowConstructionProtocolNavigation(
						app.showConsProtNavigation(id), id);
			}
			consProtNav.getImpl().setBorder(BorderFactory.createMatteBorder(1,
					0, 0, 0, Color.lightGray));
			consProtNav.getImpl().setVisible(app.showConsProtNavigation(id));

			panel.add(consProtNav.getImpl(), BorderLayout.SOUTH); // may be
																	// invisible,
																	// but made
																	// visible
																	// later
		}

		return panel;
	}

}

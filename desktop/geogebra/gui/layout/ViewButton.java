package geogebra.gui.layout;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

/**
 * Button to hide/show a view panel. Extends JToggleButton.
 * 
 * @author G. Sturr
 */
public class ViewButton extends DockButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private DockPanel panel;
	private int viewId;

	private JToolTip tip;

	/**
	 * Construct a button to hide/show a view panel.
	 * 
	 * @param app
	 * @param panel
	 */
	public ViewButton(AppD app, DockPanel panel) {
		super(app);
		this.app = app;
		this.panel = panel;
		this.viewId = panel.getViewId();
		
		Icon ic = null;
		if (panel.getIcon() != null) {
			ic = panel.getIcon();
		} else {
			ic = app.getEmptyIcon();
		}
		setIcon(ic);

		setToolTipText(panel.getPlainTitle());

		addActionListener(this);

	}

	/**
	 * Hide/show the view panel
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof ViewButton) {
			if (!panel.isAlone()) {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(viewId), viewId, false);
				app.getGuiManager().updateMenubar();
			}
		}
	}

	public DockPanel getPanel() {
		return panel;
	}

	public int getViewId() {
		return viewId;
	}

}

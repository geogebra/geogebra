package geogebra.gui.layout;

import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToolTip;

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
				app.getGuiManagerD().setShowView(
						!app.getGuiManagerD().showView(viewId), viewId, false);
				app.getGuiManagerD().updateMenubar();
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

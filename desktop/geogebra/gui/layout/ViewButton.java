package geogebra.gui.layout;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;

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
public class ViewButton extends JToggleButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private DockPanel panel;
	private int viewId;

	private JToolTip tip;

	/**
	 * Construct a button to hide/show a view panel.
	 * 
	 * @param app
	 * @param panel
	 */
	public ViewButton(Application app, DockPanel panel) {
		super();
		this.app = app;
		this.panel = panel;
		this.viewId = panel.getViewId();
		this.setOpaque(true);
		Icon ic = null;
		if (panel.getIcon() != null) {
			ic = panel.getIcon();
		} else {
			ic = app.getEmptyIcon();
		}
		setIcon(ic);

		setToolTipText(panel.getPlainTitle());
		setFocusable(false);

		addActionListener(this);
		addMouseListener(new ToolTipMouseAdapter());

		this.setBackground(SystemColor.control);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setFocusPainted(false);

		createToolTip();
	}

	/**
	 * Hide/show the view panel
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof ViewButton) {
			if (!panel.isAlone()) {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(viewId), viewId, false);
			}
		}
	}

	public DockPanel getPanel() {
		return panel;
	}

	public int getViewId() {
		return viewId;
	}

	@Override
	public void setIcon(Icon ic) {
		super.setSelectedIcon(GeoGebraIcon.joinIcons(
				app.getImageIcon("check.png"), (ImageIcon) ic));
		int s = app.getImageIcon("check.png").getIconWidth();
		super.setIcon(GeoGebraIcon.joinIcons(
				GeoGebraIcon.createEmptyIcon(s, s), (ImageIcon) ic));

		Dimension dim = new Dimension(getIcon().getIconWidth() + 6, getIcon()
				.getIconHeight() + 10);
		setPreferredSize(dim);
		setMaximumSize(dim);
		setMinimumSize(dim);

	}

	@Override
	public JToolTip createToolTip() {
		tip = super.createToolTip();
		// add margin
		tip.setBorder(BorderFactory.createCompoundBorder(tip.getBorder(),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		return tip;
	}

	@Override
	public Point getToolTipLocation(MouseEvent event) {
		if (tip == null) {
			createToolTip();
		}

		// position the tip to the left of the button, vertically centered
		Point p = new Point();
		p.y = this.getHeight() / 2 - tip.getPreferredSize().height / 2;
		p.x = -tip.getPreferredSize().width - 2;
		
		return p;
	}

	/**
	 * Listeners that give the tool tip a custom initial delay = 0
	 */
	public class ToolTipMouseAdapter extends MouseAdapter {
		private int defaultInitialDelay;
		private boolean preventToolTipDelay = true;

		@Override
		public void mouseEntered(MouseEvent e) {
			defaultInitialDelay = ToolTipManager.sharedInstance()
					.getInitialDelay();
			if (preventToolTipDelay) {
				ToolTipManager.sharedInstance().setInitialDelay(0);
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			ToolTipManager.sharedInstance()
					.setInitialDelay(defaultInitialDelay);

		}

	}
}

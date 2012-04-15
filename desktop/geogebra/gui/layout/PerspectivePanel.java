package geogebra.gui.layout;

import geogebra.common.io.layout.Perspective;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PerspectivePanel extends JPanel {

	private Application app;
	private Layout layout;
	private DockBar dockBar;
	
	private JPanel btnPanel;
	
	
	private AbstractAction changePerspectiveAction, managePerspectivesAction,
			savePerspectiveAction;

	public PerspectivePanel(Application app, DockBar dockBar) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		this.dockBar = dockBar;
		
		this.setLayout(new BorderLayout());
		initActions();
		initItems();
		
		this.setBorder(BorderFactory.createEmptyBorder(100, 20, 100, 20));
		
	}

	
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems()
	{
		//ArrayList<JButton> btnList = new ArrayList<JButton>();
		
		btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JButton btn = new JButton(changePerspectiveAction);
			btn.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			btn.setIcon(app.getImageIcon("geogebra64.png"));
			btn.setVerticalTextPosition(SwingConstants.BOTTOM);
			btn.setHorizontalTextPosition(SwingConstants.CENTER);
			btn.setActionCommand("d" + i);
			//btn.setMinimumSize(new Dimension(100,100));
			btn.setPreferredSize(new Dimension(120,120));
			btnPanel.add(btn);
		}


		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JButton btn = new JButton(changePerspectiveAction);
				btn.setText(perspectives[i].getId());
				btn.setIcon(app.getEmptyIcon());
				btn.setActionCommand(Integer.toString(i));
				btnPanel.add(btn);
			}
		}
		
		this.add(btnPanel, BorderLayout.CENTER);
		
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		savePerspectiveAction = new AbstractAction(app
				.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		managePerspectivesAction = new AbstractAction(app
				.getMenu("ManagePerspectives"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		changePerspectiveAction = new AbstractAction() {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand()
							.substring(1));
					layout.applyPerspective(geogebra.common.gui.Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}
				
				dockBar.update();
			}
		};
	}

	
	
	
	
	
	
}

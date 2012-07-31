package geogebra.gui.layout;

import geogebra.common.io.layout.Perspective;
import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class PerspectivePanel extends JPopupMenu {

	private AppD app;
	private LayoutD layout;
	
	
	private JPanel btnPanel;
	
	
	private AbstractAction changePerspectiveAction, managePerspectivesAction,
			savePerspectiveAction;

	public PerspectivePanel(AppD app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		
		initActions();
		initItems();
		registerListeners();
	}

	private void registerListeners() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e){
				hidePopup();
			}
		});
	}
	
	
	protected void hidePopup() {
		this.setVisible(false);
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems()
	{
		//ArrayList<JButton> btnList = new ArrayList<JButton>();
		/*
		btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JLabel btnText = new JLabel(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			JButton btn = new JButton(changePerspectiveAction);
			btn.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			btn.setIcon(app.getImageIcon("options-large.png"));
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setVerticalTextPosition(SwingConstants.BOTTOM);
			btn.setHorizontalTextPosition(SwingConstants.CENTER);
			btn.setActionCommand("d" + i);
			//btn.setPreferredSize(new Dimension(32,32));
			JPanel p = new JPanel(new BorderLayout(0,0));
			p.add(btn,BorderLayout.CENTER);
			//p.add(OptionsUtil.flowPanelCenter(0, 0, 0, btnText), BorderLayout.SOUTH);
			btnPanel.add(p);
			btnPanel.add(Box.createVerticalStrut(30));
		}


		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JButton btn = new JButton(changePerspectiveAction);
				btn.setText(perspectives[i].getId());
				btn.setIcon(app.getEmptyIcon());
				btn.setActionCommand(Integer.toString(i));
				//btnPanel.add(btn);
			}
		}
		
		this.add(btnPanel, BorderLayout.CENTER);
		
		*/
		
		add(Box.createVerticalStrut(10));
		JLabel title = new JLabel(app.getMenu("Perspectives"));
		title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		title.setFont(app.getBoldFont());
		add(title);
		add(Box.createVerticalStrut(10));
		
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d" + i);
			tmpItem.setIcon(app.getImageIcon("options-large.png"));
			
			Dimension d = tmpItem.getMaximumSize();
			d.height = tmpItem.getPreferredSize().height;
			tmpItem.setMaximumSize(d);
			add(tmpItem);
		}

		//addSeparator();

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				tmpItem.setIcon(app.getImageIcon("options-large.png"));
				
				Dimension d = tmpItem.getMaximumSize();
				d.height = tmpItem.getPreferredSize().height;
				tmpItem.setMaximumSize(d);
				add(tmpItem);
			}
		}
		
		add(Box.createVerticalGlue());
		
		
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
				
			}
		};
	}

	
	
	
	
	
	
}

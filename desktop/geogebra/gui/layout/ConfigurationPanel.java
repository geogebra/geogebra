package geogebra.gui.layout;

import geogebra.gui.SetLabels;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ConfigurationPanel extends JPanel implements ActionListener,
		SetLabels {

	private Application app;
	private DockBar dockBar;
	
	private JPanel buttonPanel, cardPanel;
	private JRadioButton btnPerspectives, btnOptions;
	private CardLayout cardLayout;
	private JButton btnReturn;
	private JRadioButton btnCustomToolBar;

	public ConfigurationPanel(Application app, DockBar dockBar) {

		this.app = app;
		this.dockBar = dockBar;
		setLayout(new BorderLayout());
		createButtonPanel();
		createCardPanel();
		
		cardLayout.show(cardPanel, "perspectivePanel");
		btnPerspectives.setSelected(true);
		
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(0, 0, 1, 0, SystemColor.controlDkShadow),
				BorderFactory.createEmptyBorder(10, 0, 5, 0)));

		add(buttonPanel, BorderLayout.NORTH);
		add(cardPanel, BorderLayout.CENTER);
		
		setLabels();
		

	}

	private void createButtonPanel() {

		btnPerspectives = new JRadioButton();	
		btnPerspectives.addActionListener(this);

		btnOptions = new JRadioButton();
		btnOptions.addActionListener(this);
		
		btnCustomToolBar = new JRadioButton();
		btnCustomToolBar.addActionListener(this);
		
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(btnPerspectives);
		btnGroup.add(btnOptions);
		btnGroup.add(btnCustomToolBar);
		
		btnReturn = new JButton();
		btnReturn.setIcon(app.getImageIcon("go-previous24.png"));
		btnReturn.addActionListener(this);
		//btnSettings.setFocusPainted(false);
		//btnSettings.setBorderPainted(false);
		//btnSettings.setContentAreaFilled(false);
		
		JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		westPanel.add(btnReturn);
		
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerPanel.add(btnPerspectives);
		centerPanel.add(btnOptions);
		centerPanel.add(btnCustomToolBar);
		
		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(westPanel, BorderLayout.WEST);
		buttonPanel.add(centerPanel, BorderLayout.CENTER);

	}

	private void createCardPanel() {

		cardPanel = new JPanel(new CardLayout());
		cardPanel.add(new PerspectivePanel(app, dockBar), "perspectivePanel");
		cardPanel.add(new OptionsPanel(app), "optionsPanel");
		cardPanel.add(new CustomToolBarPanel(app), "toolbarPanel");
		cardLayout = (CardLayout) cardPanel.getLayout();
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == btnPerspectives) {
			cardLayout.show(cardPanel, "perspectivePanel");
		}

		else if (source == btnOptions) {
			cardLayout.show(cardPanel, "optionsPanel");
		}

		else if (source == btnCustomToolBar) {
			cardLayout.show(cardPanel, "toolbarPanel");
		}
		
		else if (source == btnReturn) {
			app.showMainPanel();
		}

	}

	public void setLabels() {
		btnPerspectives.setText(app.getMenu("Perspectives"));
		btnOptions.setText(app.getMenu("Settings"));
		btnCustomToolBar.setText(app.getMenu("Toolbar.Customize"));
	}

}

package geogebra.gui.dialog.options;

import geogebra.common.io.MyXMLHandler;
import geogebra.common.main.settings.CASSettings;
import geogebra.gui.SetLabels;
import geogebra.main.Application;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
/**
 * Options for the CAS view.
 */
public class OptionsCAS  extends JPanel implements ActionListener, SetLabels {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Application object.
	 */
	private Application app;
	
	private CASSettings casSettings;
	
	/** */
	private JLabel timeoutLabel;
	
	/** */
	private JComboBox cbTimeout;

	/** show rational exponents as roots*/
	private JCheckBox cbShowRoots;        
	/**
	 * Construct CAS option panel.
	 * 
	 * @param app
	 */
	public OptionsCAS(Application app) {
		super(new BorderLayout());
		
		this.app = app;
		casSettings = app.getSettings().getCasSettings();
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of calling setLabels()
	 */
	private void initGUI() {
		JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10,1));

		cbTimeout = new JComboBox(MyXMLHandler.cbTimeoutOptions);
		cbTimeout.addActionListener(this);
		
		timeoutLabel = new JLabel();
		timeoutLabel.setLabelFor(cbTimeout);
		
		cbShowRoots = new JCheckBox();
		cbShowRoots.addActionListener(this);
		cbShowRoots.setSelected(casSettings.getShowExpAsRoots());
		
		timeoutPanel.add(timeoutLabel);
		timeoutPanel.add(cbTimeout);
		panel.add(timeoutPanel);
		panel.add(cbShowRoots);
		
		add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {
		casSettings = app.getSettings().getCasSettings();
		cbTimeout.setSelectedItem(MyXMLHandler.getTimeoutOption(
				casSettings.getTimeoutMilliseconds() / 1000));
		cbShowRoots.setSelected(casSettings.getShowExpAsRoots());
	}

	/**
	 * React to actions.
	 */
	public void actionPerformed(ActionEvent e) {
		// change timeout
		if(e.getSource() == cbTimeout) {
			casSettings.setTimeoutMilliseconds(((Integer)cbTimeout.getSelectedItem()) * 1000);
		}
		/** show rational exponents as roots*/
		if(e.getSource() == cbShowRoots){
			casSettings.setShowExpAsRoots(cbShowRoots.isSelected());
		}
	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		timeoutLabel.setText(app.getPlain("CasTimeout"));
		cbShowRoots.setText(app.getPlain("CASShowRationalExponentsAsRoots")); //TODO: get string from resources
	}

	/**
	 * Apply changes
	 */
	public void apply() {
	}
}

package geogebraVoiceCommand.dialog;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class View extends JFrame {

	private JLabel lbStatus;


	public View() throws HeadlessException {
		setTitle("Подключение к Диалогам");
		setSize(300, 100);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);

		lbStatus = new JLabel("...");
		lbStatus.setBounds(30, 20, 200, 40);
		add(lbStatus);


	}


	public void setLbStatus(String line){
		lbStatus.setText(line);
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand;


import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * @author Darkyver
 */
public class RunnerGGB extends JPanel {
	private GeoGebraPanel ggbPanel;
	private JFrame f;

	public RunnerGGB() {
		ggbPanel = new GeoGebraPanel();

		// hide input bar
		ggbPanel.setShowAlgebraInput(false);
		// use smaller icons in toolbar
		ggbPanel.setMaxIconSize(24);

		// show menu bar and toolbar
		ggbPanel.setShowMenubar(true);
		ggbPanel.setShowToolbar(true);
        ggbPanel.buildGUI();

        // add GeoGebraPanel to your application
        f = new JFrame();
        f.add(ggbPanel);
        f.setSize(800, 600);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void runGGB() {
		f.setVisible(true);
		f.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public void resize(int SIZE) {
		f.setExtendedState(SIZE);
	}

	public void sendCommand(String cmd) {
		if (!cmd.contains("null")) {
			ggbPanel.getGeoGebraAPI().evalCommand(cmd);
		}
	}

	public GeoGebraPanel getPanel() {
		return ggbPanel;
	}


}

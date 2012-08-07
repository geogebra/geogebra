package geogebra.gui.dialog;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.geos.GeoTurtle;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TurtleDriverPanel extends JPanel implements ActionListener,
		SetLabels {

	private static final long serialVersionUID = 1L;
	
	private GeoTurtle t;
	private JButton btnFD, btnBK, btnLT, btnRT, btnShape, btnClear;
	private double distance = 1, angle = 10;

	public TurtleDriverPanel(GeoTurtle turtle) {
		this.t = turtle;
		t.setSpeed(0);
		createGUI();
	}

	private void createGUI() {

		btnShape = new JButton();
		btnShape.addActionListener(this);
		btnClear = new JButton();
		btnClear.addActionListener(this);

		btnFD = new JButton();
		btnFD.addActionListener(this);
		btnBK = new JButton();
		btnBK.addActionListener(this);

		btnRT = new JButton();
		btnRT.addActionListener(this);
		btnLT = new JButton();
		btnLT.addActionListener(this);

		setLabels();

		JPanel motionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		motionPanel.add(btnFD);
		motionPanel.add(btnBK);
		motionPanel.add(btnLT);
		motionPanel.add(btnRT);

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(btnShape);
		statusPanel.add(btnClear);

		Box vBox = Box.createVerticalBox();
		vBox.add(motionPanel);
		vBox.add(statusPanel);
		
		this.setLayout(new BorderLayout());
		this.add(vBox, BorderLayout.CENTER);
	
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnFD) {
			t.forward(distance);
		}
		if (source == btnBK) {
			t.forward(-distance);
		}
		if (source == btnLT) {
			t.turn(angle);
		}
		if (source == btnRT) {
			t.turn(-angle);
		}
		if (source == btnShape) {
			t.setTurtle((t.getTurtle() + 1));
			t.updateRepaint();
		}
		if (source == btnClear) {
			t.clear();
		}

	}

	public void setLabels() {
		btnFD.setText("FD");
		btnBK.setText("BK");
		btnLT.setText("LT");
		btnRT.setText("RT");
		btnShape.setText("Set Shape");
		btnClear.setText("Clear");

	}

}

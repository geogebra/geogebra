package geogebra.gui.dialog;

import geogebra.common.kernel.algos.AlgoDensityPlot;
import geogebra.common.main.App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Giuliano Bellucci 05/04/2013
 * 
 */
public class DensityPlotPanel extends JPanel implements ChangeListener,
		MouseListener {

	private AlgoDensityPlot algo;
	private ImagePanel zeroPanel;

	private JSlider scaleX = new JSlider();
	private JSlider scaleY = new JSlider();

	private JCheckBox linkXY = new JCheckBox();

	private JCheckBox grid;
	private JCheckBox axes;

	private JPanel father;

	private Graphics2D g;

	DensityPlotPanel(JPanel panel, App app) {

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		father = panel;

		grid = new JCheckBox(app.getPlain("ShowGrid"));
		axes = new JCheckBox(app.getPlain("ShowAxes"));

		linkXY.setText("Y<->X");
		linkXY.setSelected(true);

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(new Integer(2), new JLabel("0.2"));
		labels.put(new Integer(10000), new JLabel("100"));
		labels.put(new Integer(5001), new JLabel("50"));
		labels.put(new Integer(2000), new JLabel("20"));

		scaleX.setBorder(new TitledBorder(app.getPlain("XScale")));
		scaleX.setMinimum(2);
		scaleX.setMaximum(10000);
		scaleX.setMajorTickSpacing(4999);
		scaleX.setLabelTable(labels);
		scaleX.setPaintTicks(true);
		scaleX.setPaintLabels(true);

		scaleY.setBorder(new TitledBorder(app.getPlain("YScale")));
		scaleY.setMinimum(2);
		scaleY.setMaximum(10000);
		scaleY.setMajorTickSpacing(4999);
		scaleY.setLabelTable(labels);
		scaleY.setPaintTicks(true);
		scaleY.setPaintLabels(true);

		scaleX.addChangeListener(this);
		scaleY.addChangeListener(this);
		grid.addChangeListener(this);
		axes.addChangeListener(this);

		JPanel sliderPanel = new JPanel(new BorderLayout());
		JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkPanel.add(grid);
		checkPanel.add(axes);
		checkPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.add(checkPanel);

		sliderPanel.add(scaleY, BorderLayout.EAST);
		sliderPanel.add(scaleX, BorderLayout.NORTH);
		JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		linkPanel.add(sliderPanel);
		linkPanel.add(linkXY);
		linkPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.add(linkPanel);
		zeroPanel = new ImagePanel();
		zeroPanel.addMouseListener(this);
		g = zeroPanel.getGraphics();
		JPanel zeroLabelPanel = new JPanel();
		zeroLabelPanel.setLayout(new BorderLayout());
		JLabel label = new JLabel(app.getPlain("OriginOfAxes"));
		zeroLabelPanel.add(label, BorderLayout.NORTH);
		zeroLabelPanel.add(zeroPanel, BorderLayout.CENTER);
		zeroLabelPanel
				.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.add(zeroLabelPanel);

	}

	public void setAlgo(AlgoDensityPlot algo) {
		this.algo = algo;
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == scaleX) {
			if (linkXY.isSelected()) {
				scaleY.removeChangeListener(this);
				scaleY.setValue(scaleX.getValue());
				scaleY.addChangeListener(this);
				algo.setScaleY(scaleX.getValue() / 100f);
			}
			algo.setScaleX(scaleX.getValue() / 100f);
		}
		if (e.getSource() == scaleY) {
			if (linkXY.isSelected()) {
				scaleX.removeChangeListener(this);
				scaleX.setValue(scaleY.getValue());
				scaleX.addChangeListener(this);
				algo.setScaleX(scaleY.getValue() / 100f);
			}
			algo.setScaleY(scaleY.getValue() / 100f);
		}
		if (e.getSource() == grid) {
			algo.setGrid(grid.isSelected());
		}
		if (e.getSource() == axes) {
			algo.setAxes(axes.isSelected());
		}
		algo.compute();
		algo.getResult().updateRepaint();
	}

	public void update() {
		scaleX.removeChangeListener(this);
		scaleY.removeChangeListener(this);
		grid.removeChangeListener(this);
		axes.removeChangeListener(this);
		g.setColor(Color.white);
		g.fillRect(0, 0, 100, 100);
		g.setColor(Color.black);
		g.drawLine((int) (50 + algo.getZeroX() * 100 / algo.getScaleX()), 0,
				(int) (50 + algo.getZeroX() * 100 / algo.getScaleX()), 100);
		g.drawLine(0, (int) (50 - algo.getZeroY() * 100 / algo.getScaleY()),
				100, (int) (50 - algo.getZeroY() * 100 / algo.getScaleY()));
		scaleX.setValue((int) (algo.getScaleX() * 100));
		scaleY.setValue((int) (algo.getScaleY() * 100));
		grid.setSelected(algo.hasGrid());
		axes.setSelected(algo.hasAxes());
		scaleX.addChangeListener(this);
		scaleY.addChangeListener(this);
		grid.addChangeListener(this);
		axes.addChangeListener(this);
		father.updateUI();
	}

	public void mouseClicked(MouseEvent arg0) {
		int xx = arg0.getX();
		int yy = arg0.getY();
		double xC = xx - 50;
		xC *= algo.getScaleX() / 100f;
		double yC = 50 - yy;
		yC *= algo.getScaleY() / 100f;
		algo.setZeroX(xC);
		algo.setZeroY(yC);
		algo.compute();
		algo.getResult().updateRepaint();
		update();
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	class ImagePanel extends JPanel {

		BufferedImage zero = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);

		public ImagePanel() {
			this.setPreferredSize(new Dimension(100, 100));
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(zero, 0, 0, null);
		}

		public Graphics2D getGraphics() {
			return zero.createGraphics();
		}
	}
}

package test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class TestSeveralViews {

	public static void main(String[] args) {
		final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Container c = frame.getContentPane();
		c.setLayout(new FlowLayout());

		View view = new View(1f, 0f, 0f, 1);
		Component canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		view = new View(0f, 1f, 0f, 2);
		canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		view = new View(0f, 0f, 1f, 4);
		canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		frame.setTitle("TestSeveralViews");
		frame.pack();
		frame.setVisible(true);
	}

}

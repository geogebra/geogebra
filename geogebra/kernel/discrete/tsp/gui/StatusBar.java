package geogebra.kernel.discrete.tsp.gui;

import geogebra.kernel.discrete.tsp.model.Node;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

public class StatusBar extends JLabel implements Observer {
	public StatusBar() {
		super(" ");
		this.setText();
	}
	
	private int nodes;
	private double circuitCost;
	@SuppressWarnings("unchecked")
	public void update(Observable o, Object arg) {
		if (arg instanceof Double) {
			this.circuitCost = (int) (((Double) arg) * 100 + 0.5) / 100D;
		} else if (arg instanceof Integer) {
			this.nodes = (Integer) arg;
		} else if (arg instanceof List) {
			List<Node> list = (List<Node>) arg;
			if (list.size() > 0) {
				Node n0 = list.get(list.size() - 1);
				double length = 0;
				for (Node node : list) {
					length += n0.getDistance(node);
					n0 = node;
				}
				this.circuitCost = (int) (length * 100 + 0.5) / 100D;
			}
		}
		this.setText();
	}
	private void setText() {
		this.setText("  頂点数： "+ this.nodes + " / 巡回路： "+ this.circuitCost);
	}
}

package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.model.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * nearest neighborã�«ã‚ˆã‚‹å·¡å›žã‚»ãƒ¼ãƒ«ã‚¹ãƒžãƒ³å•�é¡Œã�®æ§‹ç¯‰æ³•ã�§ã�™ã€‚
 * @author ma38su
 */
public class NearestNeighbor implements TspConstruction {
	public List<Node> method(DemoPanel panel) {
		Set<Node> nodes = new HashSet<Node>(panel.getNodes());
		List<Node> route = new ArrayList<Node>(nodes.size() + 1);
		Iterator<Node> itr = nodes.iterator();
		if (itr.hasNext()) {
			Node start = itr.next();
			Node node = start;
			while (!nodes.isEmpty()) {
				route.add(node);
				if (nodes.remove(node) && nodes.isEmpty()) {
					break;
				}
				panel.set(route);
				Node tmp = null;
				double min = Double.POSITIVE_INFINITY;
				for (Node terminal : nodes) {
					double distance = node.getDistance(terminal);
					if (min > distance) {
						min = distance;
						tmp = terminal;
					}
				}
				assert tmp != null;
				node = tmp;
			}
		}
		return route;
	}

	@Override
	public String toString() {
		return "nearest neighbor";
	}
}

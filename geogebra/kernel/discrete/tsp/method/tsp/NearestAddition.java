package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.model.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * nearest additionã�«ã‚ˆã‚‹å·¡å›žã‚»ãƒ¼ãƒ«ã‚¹ãƒžãƒ³å•�é¡Œã�®æ§‹ç¯‰æ³•ã�§ã�™ã€‚
 * @author ma38su
 */
public class NearestAddition implements TspConstruction {
	public List<Node> method(DemoPanel panel) {
		Set<Node> nodes = new HashSet<Node>(panel.getNodes());
		List<Node> route = new ArrayList<Node>(nodes.size() + 1);
		Iterator<Node> itr = nodes.iterator();
		if (itr.hasNext()) {
			Node node = itr.next();
			int insertion = 0;
			while (!nodes.isEmpty()) {
				route.add(insertion, node);
				if (nodes.remove(node) && nodes.isEmpty()) {
					break;
				}
				panel.set(route);
				Node nearest = null;
				insertion = -1;
				double min = Double.POSITIVE_INFINITY;
				for (int i = 0; i < route.size(); i++) {
					Node s = route.get(i);
					for (Node t : nodes) {
						double distance = s.getDistance(t);
						if (min > distance) {
							min = distance;
							insertion = i;
							nearest = t;
						}
					}
				}
				assert insertion != -1 && nearest != null;
				node = nearest;
			}
		}
		return route;
	}

	@Override
	public String toString() {
		return "nearest addition";
	}
}

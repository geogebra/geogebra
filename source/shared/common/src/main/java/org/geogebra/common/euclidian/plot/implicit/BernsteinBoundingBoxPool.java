package org.geogebra.common.euclidian.plot.implicit;

import java.util.LinkedList;
import java.util.Queue;

class BernsteinBoundingBoxPool {
	Queue<BernsteinBoundingBox> queue = new LinkedList<>();

	BernsteinBoundingBox request(double x1, double y1, double x2, double y2) {
		BernsteinBoundingBox box = queue.poll();
		if (box == null) {
			return new BernsteinBoundingBox(x1, y1, x2, y2);
		}
		box.set(x1, y1, x2, y2);
		return box;
	}

	void release(BernsteinBoundingBox box) {
		queue.offer(box);
	}
}

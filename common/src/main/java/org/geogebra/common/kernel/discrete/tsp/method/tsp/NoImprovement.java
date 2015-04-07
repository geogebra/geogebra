package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;

/**
 * æ”¹å–„æ³•ã‚’é�©ç”¨ã�—ã�ªã�„ã�Ÿã‚�ã�®ãƒ€ãƒŸãƒ¼ã�®ã‚¯ãƒ©ã‚¹
 * @author ma38su
 */
public class NoImprovement implements TspImprovement {
	public boolean method(List<Node> route) {
		return false;
	}
	public boolean method(int[] route, double[][] table) {
		return false;
	}

	@Override
	public String toString() {
		return "é�©ç”¨ã�—ã�ªã�„";
	}
}

package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.model.Node;

import java.util.List;


/**
 * å·¡å›žã‚»ãƒ¼ãƒ«ã‚¹ãƒžãƒ³å•�é¡Œã�®æ”¹å–„æ³•ã�®ã‚¤ãƒ³ã‚¿ãƒ¼ãƒ•ã‚§ãƒ¼ã‚¹ã�§ã�™ã€‚
 * æ”¹å–„æ³•ã�§ã�¯æ—¢å­˜ã�®å·¡å›žè·¯ã�‹ã‚‰ã‚ˆã‚Šå°�ã�•ã�„ã‚³ã‚¹ãƒˆã�®å·¡å›žè·¯ã‚’æ±‚ã‚�ã�¾ã�™ã€‚
 * @author ma38su
 */
public interface TspImprovement {
	public boolean method(List<Node> route);
	public boolean method(int[] route, double[][] table);
}

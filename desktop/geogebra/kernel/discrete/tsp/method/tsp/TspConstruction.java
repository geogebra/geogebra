package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.model.Node;

import java.util.List;


/**
 * å·¡å›žã‚»ãƒ¼ãƒ«ã‚¹ãƒžãƒ³å•�é¡Œã�®æ§‹ç¯‰æ³•ã�®ã‚¤ãƒ³ã‚¿ãƒ¼ãƒ•ã‚§ãƒ¼ã‚¹ã�§ã�™ã€‚
 * æ§‹ç¯‰æ³•ã�§ã�¯ä½•ã‚‚ã�ªã�„ã�¨ã�“ã‚�ã�‹ã‚‰å·¡å›žè·¯ã‚’æ±‚ã‚�ã�¾ã�™ã€‚
 * @author ma38su
 */
public interface TspConstruction {
	public List<Node> method(DemoPanel panel);
}

package org.geogebra.common.euclidian.modes;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.BaseControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.jre.util.ScientificFormat;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class ModeDeleteTest extends BaseControllerTest {

	@Test
	public void eraserTest() throws InterruptedException {
		setMode(EuclidianConstants.MODE_ERASER);
		add("stroke = PolyLine((2,-4),(2,-2),(4,-2),(4,-4), true)");
		click(140, 90);

		assertEquals("Polyline[(2.0000E0,-4.0000E0), (1.8720E0,-3.5232E0), "
						+ "(1.7760E0,-3.0656E0), (1.7440E0,-2.6464E0), (1.8080E0,-2.2848E0),"
						+ " (2.0000E0,-2.0000E0), (2.3360E0,-1.8080E0), (2.6000E0,-1.7365E0),"
						+ " (?,?), (3.0000E0,-1.7000E0), (3.2320E0,-1.7120E0),"
						+ " (3.6640E0,-1.8080E0), (4.0000E0,-2.0000E0), (4.1920E0,-2.2848E0),"
						+ " (4.2560E0,-2.6464E0), (4.2240E0,-3.0656E0), (4.1280E0,-3.5232E0),"
						+ " (4.0000E0,-4.0000E0), (?,?), true]",
				getDefinition());

		add("stroke = PolyLine((2,-4),(2,-2),(4,-2),(4,-4), true)");
		click(100, 100);

		assertEquals("Polyline[(2.0000E0,-4.0000E0), (1.8720E0,-3.5232E0),"
						+ " (1.7760E0,-3.0656E0), (1.7440E0,-2.6464E0), (1.8080E0,-2.2848E0),"
						+ " (1.8456E0,-2.2000E0), (?,?), (2.2000E0,-1.8664E0),"
						+ " (2.3360E0,-1.8080E0), (2.7680E0,-1.7120E0), (3.2320E0,-1.7120E0),"
						+ " (3.6640E0,-1.8080E0), (4.0000E0,-2.0000E0), (4.1920E0,-2.2848E0),"
						+ " (4.2560E0,-2.6464E0), (4.2240E0,-3.0656E0), (4.1280E0,-3.5232E0),"
						+ " (4.0000E0,-4.0000E0), (?,?), true]",
				getDefinition());
	}

	@Test
	public void eraserTestStraight() {
		setMode(EuclidianConstants.MODE_ERASER);
		// delete the middle
		add("stroke = PolyLine((2,-2),(3,-2),(4,-2), true)");
		int x = 145;
		int y = 95;
		dragStart(x, y);
		dragEnd(x + 5, y + 5);
		assertEquals("Polyline[(2.0000E0,-2.0000E0), (2.8000E0,-2.0000E0), (?,?),"
						+ " (3.2000E0,-2.0000E0), (4.0000E0,-2.0000E0), (?,?), true]",
				getDefinition());
		// delete the start
		add("stroke = PolyLine((2,-2),(3,-2),(4,-2), true)");
		x = 95;
		y = 95;
		dragStart(x, y);
		dragEnd(x + 5, y + 5);
		assertEquals("Polyline[(2.2000E0,-2.0000E0), (3.0000E0,-2.0000E0),"
						+ " (4.0000E0,-2.0000E0), (?,?), true]",
				getDefinition());
	}

	private String getDefinition() {
		String nanString = new ScientificFormat(1, 10, false).format(Double.NaN);
		String definition1 = lookup("stroke").getDefinition(StringTemplate.defaultTemplate);
		return definition1.replace(nanString, "?");
	}
}

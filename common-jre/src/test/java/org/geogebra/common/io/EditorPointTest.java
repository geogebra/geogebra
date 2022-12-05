package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorPointTest {
	private static final String point3D = "(1,2,3)";
	private static final String emptyPoint3D = "(,,)";
	private static EditorChecker checker;
	private static AppCommon app = AppCommonFactory.create();

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setUp() {
		checker = new EditorChecker(app);
	}

	@Test
	public void testInitialEmptyPoint() {
		checker.convertFormula(emptyPoint3D)
				.checkPlaceholders(0, 2, 4);
	}

	@Test
	public void testEmptyPointWithCursor() {
		checker.convertFormula(emptyPoint3D)
				.right(2)
				.checkPlaceholders(0, 2, 4);
	}
}

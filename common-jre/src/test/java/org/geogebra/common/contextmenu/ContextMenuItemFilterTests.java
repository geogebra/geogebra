package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.SpecialPoints;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.test.TestErrorHandler;
import org.junit.Test;

public class ContextMenuItemFilterTests {
	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	private final App app = AppCommonFactory.create(new AppConfigGeometry());
	private final AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
	private final GeoElement geoElement = add("x");

	@Test
	public void testInitialUnfilteredContextMenuItems() {
		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);
	}

	@Test
	public void testEmptyFilter() {
		contextMenuFactory.addFilter(item -> true);

		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);
	}

	@Test
	public void testSingleItemFilter() {
		contextMenuFactory.addFilter(item -> !item.equals(CreateTableValues));

		assertEquals(
				List.of(SpecialPoints, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);
	}

	@Test
	public void testMultipleItemFilter() {
		contextMenuFactory.addFilter(item -> !Set.of(Delete, Settings).contains(item));

		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);
	}

	@Test
	public void testMultipleFilter() {
		ContextMenuItemFilter filter1 = item -> !Set.of(Delete, Settings).contains(item);
		ContextMenuItemFilter filter2 = item -> !Set.of(Delete, SpecialPoints).contains(item);

		contextMenuFactory.addFilter(filter1);
		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);

		contextMenuFactory.addFilter(filter2);
		assertEquals(
				List.of(CreateTableValues, DuplicateInput),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);

		contextMenuFactory.removeFilter(filter1);
		assertEquals(
				List.of(CreateTableValues, DuplicateInput, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);

		contextMenuFactory.removeFilter(filter2);
		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, algebraProcessor, GeoGebraConstants.GRAPHING_APPCODE)
		);
	}

	private GeoElement add(String command) {
		GeoElementND[] geoElements = algebraProcessor.processAlgebraCommandNoExceptionHandling(
				command, false, TestErrorHandler.INSTANCE, false, null);
		return (GeoElement) geoElements[0];
	}
}

package org.geogebra.web.full.gui;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.web.full.html5.GMenuBarMock;
import org.geogebra.web.full.html5.MenuFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class})
public class InlineTextItemsTest {

	private ContextMenuGeoElementW contextMenu;
	private Construction construction;
	private AppW app;
	private GPoint2D point;
	private ContextMenuFactory factory;

	@Before
	public void setUp() {
		app = AppMocker.mockNotes(getClass());
		construction = app.getKernel().getConstruction();
		point = new GPoint2D(0, 0);
		enableSettingsItem();
	}

	private void enableSettingsItem() {
		app.setShowMenuBar(true);
		app.setRightClickEnabled(true);
	}

	@Test
	public void testOneInlineText() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline());
		factory = new MenuFactory(app, withDrawInlineTest());
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font", "editLink", "removeLink",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	private DrawInlineText withDrawInlineTest() {
		return new DrawInlineText(app.getActiveEuclidianView(), createTextInline()) {
			@Override
			public String getHyperLinkURL() {
				return "www.foo.bar";
			}
		};
	}

	@Test
	public void testPolygon() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createPolygon());
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste", "SEPARATOR", "General.Order", "SEPARATOR",
				"FixObject", "ShowTrace", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	private GeoElement createPolygon() {
		GeoPolygon poly = new GeoPolygon(construction);
		poly.setLabel("poly1");
		return poly;
	}

	private GeoInlineText createTextInline() {
		GeoInlineText text = new GeoInlineText(construction, point);
		text.setLabel("text1");
		return text;
	}
}

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

	public static final String LINK_URL = "www.foo.bar";
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
	public void testSingleInlineTextContextMenu() {
		factory = new MenuFactory(app, getTextWithLink(null));
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1"));
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font", "Link",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	@Test
	public void testSingleInlineTextWithLinkContextMenu() {
		factory = new MenuFactory(app, getTextWithLink(LINK_URL));
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1"));
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

	@Test
	public void testGrouppedInlineTextContextMenu() {
		factory = new MenuFactory(app, getTextWithLink(LINK_URL));
		ArrayList<GeoElement> geos = new ArrayList<>();
		ArrayList<GeoElement> members = new ArrayList<>();
		members.add(createTextInline("text1"));
		members.add(createTextInline("text2"));
		construction.createGroup(members);
		geos.add(members.get(0));
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	@Test
	public void testGrouppedInlineTextAndPolygonContextMenu() {
		factory = new MenuFactory(app, getTextWithLink(LINK_URL));
		ArrayList<GeoElement> geos = new ArrayList<>();
		ArrayList<GeoElement> members = new ArrayList<>();
		members.add(createTextInline("text1"));
		members.add(createPolygon("poly1"));
		construction.createGroup(members);
		geos.add(members.get(0));
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	private DrawInlineText getTextWithLink(final String link) {
		return new DrawInlineText(app.getActiveEuclidianView(), createTextInline("dummy")) {
			@Override
			public String getHyperLinkURL() {
				return link;
			}
		};
	}

	@Test
	public void testPolygonContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createPolygon("Poly1"));
		factory = new MenuFactory(app, null);
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste", "SEPARATOR", "General.Order", "SEPARATOR",
				"FixObject", "ShowTrace", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	private GeoElement createPolygon(String label) {
		GeoPolygon poly = new GeoPolygon(construction);
		poly.setLabel(label);
		return poly;
	}

	private GeoInlineText createTextInline(String label) {
		GeoInlineText text = new GeoInlineText(construction, point);
		text.setLabel(label);
		return text;
	}
}

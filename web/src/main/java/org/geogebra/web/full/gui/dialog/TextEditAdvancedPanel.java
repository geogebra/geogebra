package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.gui.util.TableSymbolsLaTeX;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.ITextEditPanel;
import org.geogebra.web.html5.gui.inputfield.SymbolTableW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Panel with symbols and GeoElements to be inserted into the GeoText editor
 * 
 * @author G. Sturr
 * 
 */
public class TextEditAdvancedPanel extends TabLayoutPanel implements SetLabels {

	private AppW app;
	/** Test edit panel */
	protected ITextEditPanel editPanel;

	private VerticalPanel geoPanel;
	private VerticalPanel symbolPanel;
	private VerticalPanel latexPanel;
	private TextPreviewPanelW previewer;
	private Localization loc;
	private Label previewLabel;
	private Label latexLabel;

	/**
	 * @param app
	 *            application
	 * @param editPanel
	 *            edit panel
	 */
	public TextEditAdvancedPanel(App app, ITextEditPanel editPanel) {
		super(30, Unit.PX);
		this.app = (AppW) app;
		this.editPanel = editPanel;
		loc = app.getLocalization();

		addStyleName("textEditorAdvancedPanel");
		
		createGeoListBox();
		createSymbolPanel();
		createLatexPanel();

		getPreviewer();
		previewer.onResize();

		Image geoTabImage = new NoDragImage(AppResources.INSTANCE.geogebra()
				.getSafeUri().asString(),
				AppResources.INSTANCE.geogebra().getWidth());

		// create the tabs
		previewLabel = new Label(loc.getMenu("Preview"));
		add(new ScrollPanel(getPreviewer().getPanel()),
				previewLabel);
		add(new ScrollPanel(geoPanel), geoTabImage);
		add(new ScrollPanel(symbolPanel), Unicode.ALPHA_BETA_GAMMA);
		latexLabel = new Label(loc.getMenu("LaTeXFormula"));
		add(new ScrollPanel(latexPanel), latexLabel);

		registerListeners();
		setLabels();
	}

	@Override
	public void insert(final Widget child, Widget tab, int beforeIndex) {
		super.insert(child, tab, beforeIndex);
		tab.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				TextEditAdvancedPanel.this.selectTab(child);
				event.preventDefault();
			}
		}, MouseDownEvent.getType());
	}

	private void registerListeners() {
		// update the geoPanel when selected
		addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 1) {
					updateGeoList();
					// geoPanel.setFocus(true);
				}
			}
		});
	}

	/**
	 * @return preview panel
	 */
	public TextPreviewPanelW getPreviewer() {
		if (previewer == null) {
			previewer = new TextPreviewPanelW(app.getKernel());
			previewer.getPanel().setStyleName("previewPanel");
		}
		return previewer;
	}

	@Override
	public void setLabels() {
		previewLabel.setText(loc.getMenu("Preview"));
		latexLabel.setText(loc.getMenu("LaTeXFormula"));
	}

	// =====================================================
	// GeoElement panel
	// =====================================================

	private void createGeoListBox() {
		geoPanel = new VerticalPanel();
		geoPanel.setWidth("100%");
		geoPanel.setHeight("100%");
		geoPanel.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
	}

	/**
	 * Update list of geos that can be inserted
	 */
	public void updateGeoList() {
		geoPanel.clear();
		Object[] datas = getGeoObjectList(editPanel.getEditGeo());
		String[] geoLabels = (String[]) datas[0];
		GColor[] geoColors = (GColor[]) datas[1];

		final SymbolTableW symTable = newSymbolTable(geoLabels, false, 2,
				new AsyncOperation<String>() {

					@Override
					public void callback(String s) {
						insertGeo(s);

					}
				}, geoColors);
		symTable.getColumnFormatter().setStyleName(0, "geoSelectFirst");
		geoPanel.add(symTable);
	}

	/**
	 * @param label
	 *            geo label
	 */
	protected void insertGeo(String label) {
		editPanel.insertGeoElement(app.getKernel().lookupLabel(label));
	}

	/**
	 * Creates an array of labels and colors of existing geos that can be
	 * inserted into the editor content
	 */
	private Object[] getGeoObjectList(GeoText editGeo) {

		TreeSet<GeoElement> ts = app.getKernel().getConstruction()
		        .getGeoSetLabelOrder();
		ArrayList<String> list = new ArrayList<>();
		ArrayList<GColor> colors = new ArrayList<>();

		// first possibility : create empty box
		list.add(loc.getMenu("EmptyBox"));
		colors.add(null);

		// add all geos
		Iterator<GeoElement> iter = ts.iterator();
		while (iter.hasNext()) {
			GeoElement g = iter.next();
			if (g.isLabelSet() && !g.equals(editGeo)) {
				list.add(g.getLabelSimple());
				colors.add(g.getAlgebraColor());
			}
		}
		String[] geoArray = new String[list.size()];
		geoArray = list.toArray(geoArray);
		GColor[] colorArray = new GColor[colors.size()];
		colorArray = colors.toArray(colorArray);

		Object[] objArray = new Object[2];
		objArray[0] = geoArray;
		objArray[1] = colorArray;
		return objArray;
	}

	// =====================================================
	// Symbol panel
	// =====================================================

	private void createSymbolPanel() {

		int defaultRowSize = 15;

		symbolPanel = new VerticalPanel();
		symbolPanel.setWidth("100%");
		symbolPanel.setHeight("100%");

		String[][] map = TableSymbols.basicSymbolsMap(loc);

		addTable(TableSymbols.basicSymbols(loc, map), false,
		        defaultRowSize, false);
		addTable(TableSymbols.OPERATORS, false, defaultRowSize, true);
		addTable(TableSymbols.greekLettersPlusVariants(), false,
		        defaultRowSize, true);
		addTable(TableSymbols.ANALYSIS, false, defaultRowSize, true);
		addTable(TableSymbols.SETS, false, defaultRowSize, true);
		addTable(TableSymbols.LOGICAL, false, defaultRowSize, true);
		addTable(TableSymbols.SUB_SUPERSCRIPTS, false, defaultRowSize, true);
		addTable(TableSymbols.BASIC_ARROWS, false, defaultRowSize, true);
		addTable(TableSymbols.OTHER_ARROWS, false, defaultRowSize, true);
		addTable(TableSymbols.GEOMETRIC_SHAPES, false, defaultRowSize, true);
		addTable(TableSymbols.GAMES_MUSIC, false, defaultRowSize, true);
		addTable(TableSymbols.CURRENCY, false, defaultRowSize, true);
		addTable(TableSymbols.HAND_POINTERS, false, defaultRowSize, true);

	}

	private void addTable(String[] tableSymbols, final boolean isLatex,
			int rowSize,
	        boolean addSeparator) {

		final SymbolTableW symTable = newSymbolTable(tableSymbols, isLatex,
				rowSize, new AsyncOperation<String>() {

					@Override
					public void callback(String s) {
						editPanel.insertTextString(s, isLatex);

					}
				}, null);

		if (addSeparator) {
			symbolPanel.add(new HTML("<hr>"));
		}
		symbolPanel.add(symTable);
	}

	// =====================================================
	// LaTeX panel
	// =====================================================

	private void createLatexPanel() {

		int defaultRowSize = 15;

		latexPanel = new VerticalPanel();
		latexPanel.addStyleName("latexPanel");
		latexPanel.setWidth("100%");
		latexPanel.setHeight("100%");

		addLaTeXTable(
				TableSymbolsLaTeX.roots_fractions, /* "RootsAndFractions", */
		        defaultRowSize, false);
		addLaTeXTable(TableSymbolsLaTeX.sums, /* "SumsAndIntegrals", */
		        defaultRowSize, true);
		addLaTeXTable(TableSymbolsLaTeX.accents,
				/* "Accents", */ defaultRowSize,
		        true);
		addLaTeXTable(TableSymbolsLaTeX.accentsExtended, /* "AccentsExt", */
		        defaultRowSize, true);
		addLaTeXTable(TableSymbolsLaTeX.brackets,
				/* "Brackets", */ defaultRowSize,
		        true);
		// addLaTeXTable(TableSymbolsLaTeX.matrices, "Matrices", defaultRowSize,
		// true);
		// addLaTeXTable(TableSymbolsLaTeX.mathfrak(), "FrakturLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathcal(), "CalligraphicLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathbb(), "BlackboardLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathscr(), "CursiveLetters",
		// defaultRowSize, true);

	}

	private void addLaTeXTable(String[] tableSymbols, 
	        int rowSize, boolean addSeparator) {

		final SymbolTableW symTable = newSymbolTable(tableSymbols, true,
				rowSize, new AsyncOperation<String>() {

					@Override
					public void callback(String s) {
						editPanel.insertTextString(s, true);
						editPanel.ensureLaTeX();
					}
				}, null);

		if (addSeparator) {
			latexPanel.add(new HTML("<hr>"));
		}

		// latexPanel.add(new Label(header));
		latexPanel.add(symTable);
	}

	// =====================================================
	// Symbol table utilities
	// =====================================================

	private SymbolTableW newSymbolTable(String[] table, boolean isLatexSymbol,
			int rowSize, final AsyncOperation<String> onChange,
			GColor[] colors) {

		final SymbolTableW symTable = new SymbolTableW(table, 
				isLatexSymbol, rowSize, app, colors);

		if (Browser.isIE()) {
		symTable.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				/*
				 * Cell clickCell = ((HTMLTable) event.getSource())
				 * .getCellForEvent(event); if (clickCell == null) { return; }
				 * String text = symTable.getSymbolText(clickCell.getRowIndex(),
				 * clickCell.getCellIndex());
				 */

				Element td = ((SymbolTableW) event.getSource())
						.getEventTargetCell(Event
						.as(event.getNativeEvent()));
				if (td != null) {
					int row = TableRowElement.as(td.getParentElement())
							.getSectionRowIndex();
					int column = TableCellElement.as(td).getCellIndex();
						onChange.callback(symTable.getSymbolText(row, column));
				}
				event.preventDefault();
				event.stopPropagation();
				// editPanel.insertTextString(clickCell.getElement()
				// .getInnerText(), false);
			}
		}, MouseDownEvent.getType());
		} else {
			symTable.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					HTMLTable.Cell clickCell = ((HTMLTable) event
							.getSource()).getCellForEvent(event);
					if (clickCell == null) {
						return;
					}
					String text = symTable.getSymbolText(
							clickCell.getRowIndex(), clickCell.getCellIndex());
					onChange.callback(text);
				}
			});
		}
		return symTable;
	}

}

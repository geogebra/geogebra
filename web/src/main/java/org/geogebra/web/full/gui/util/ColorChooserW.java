package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.CustomColorDialog;
import org.geogebra.web.full.gui.dialog.CustomColorDialog.ICustomColor;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.gui.util.SliderInputHandler;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class ColorChooserW extends FlowPanel implements ICustomColor {

	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 100;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_X = 5;
	public static final GColor NO_TILE_COLOR = GColor.newColor(255, 255, 255);
	public static final GColor NORMAL_TILE_COLOR = GColor.newColorRGB(0);
	public static final GColor EMPTY_TILE_COLOR = GColor.newColor(16, 16, 16);
	public static final GColor SELECTED_TILE_COLOR = GColor.newColor(255, 0, 0);
	public static final String TITLE_FONT = "14pt "
			+ GFontW.GEOGEBRA_FONT_SANSERIF;
	public static final int TITLE_HEIGHT = 20;
	public static final GColor FOCUS_COLOR = GColor.newColor(0, 0, 255);
	public static final double BORDER_WIDTH = 2;
	public static final double PREVIEW_BORDER_WIDTH = 14;
	Canvas canvas;
	JLMContext2d ctx;
	Dimension colorIconSize;
	int padding;
	List<ColorTable> tables;
	private ColorTable leftTable;
	private ColorTable mainTable;
	private RecentTable recentTable;
	private ColorTable otherTable;
	private ColorTable lastSource;
	private GColor selectedColor;
	ColorChangeHandler changeHandler;
	PreviewPanel previewPanel;
	private OpacityPanel opacityPanel;
	private BackgroundColorPanel backgroundColorPanel;
	private Button btnCustomColor;
	App app;
	private CustomColorDialog dialog;
	BarList lbBars;
	private int selectedBar;
	private int chartBars;
	private GColor allBarsColor;

	private class ColorTable {
		private int left;
		private int top;
		private int tableOffsetY;
		private int maxCol;
		private int maxRow;
		private String title;
		private List<GColor> palette;
		private int width;
		private int height;
		private HTMLImageElement checkMark;
		private int checkX;
		private int checkY;
		private boolean checkNeeded;
		private double titleOffsetX;
		private double titleOffsetY;
		private int currentCol;
		private int currentRow;
		private int selectedCol;
		private int selectedRow;
		private int capacity;

		public ColorTable(int x, int y, int col, int row, List<Integer> data) {
			left = x;
			top = y;
			tableOffsetY = 0;
			maxCol = col;
			maxRow = row;
			setCapacity(maxCol * maxRow);
			this.title = "";
			palette = new ArrayList<>();
			currentCol = -1;
			currentRow = -1;
			setSelectedCol(-1);
			setSelectedRow(-1);
			if (data != null) {
				for (Integer code : data) {
					palette.add(GColor.newColorRGB(code));
				}
			}

			setWidth(col * colorIconSize.getWidth() + padding);
			setHeight(row * colorIconSize.getHeight() + padding);

			checkMark = Dom.createImage();
			checkMark.src = AppResources.INSTANCE.color_chooser_check().getSafeUri().asString();

			final int checkSize = 12;
			checkX = (colorIconSize.getWidth() - checkSize) / 2 + padding;
			checkY = (colorIconSize.getHeight() - checkSize) / 2 + padding;
			checkNeeded = false;
		}

		protected void drawTitle() {
			if (title.isEmpty()) {
				return;
			}
			ctx.save();
			ctx.translate(left, top);

			ctx.setTextBaseline("top");
			ctx.clearRect(0, 0, width, TITLE_HEIGHT);
			ctx.setFont(TITLE_FONT);
			ctx.fillText(title, titleOffsetX, titleOffsetY);
			ctx.restore();

			tableOffsetY = TITLE_HEIGHT;
		}

		public void draw() {
			drawTitle();
			ctx.save();
			ctx.scale(1, 1);

			ctx.translate(left, top);
			for (int row = 0; row < maxRow; row++) {
				for (int col = 0; col < maxCol; col++) {
					drawColorTile(col, row);
				}
			}

			// ctx.strokeRect(0, 0, getWidth(), getHeight());
			ctx.restore();
		}

		private void drawColorTile(int col, int row) {
			int h = colorIconSize.getHeight();
			int w = colorIconSize.getWidth();

			int x = (col * w);
			int y = tableOffsetY + (row * h);

			GColor borderColor = NORMAL_TILE_COLOR;
			ctx.setLineWidth(1);

			GColor fillColor = getColorFromPalette(col, row);

			boolean emptyTile = (fillColor == null);
			if (emptyTile) {
				fillColor = NO_TILE_COLOR;
			}

			ctx.setFillStyle(StringUtil.toHtmlColor(fillColor));

			ctx.fillRect(x + padding, y + padding, w - padding, h - padding);

			if (emptyTile) {
				borderColor = EMPTY_TILE_COLOR;
			} else if (col == currentCol && row == currentRow && !emptyTile) {
				ctx.setLineWidth(BORDER_WIDTH);
				borderColor = FOCUS_COLOR;
			} else if (col == getSelectedCol() && row == getSelectedRow()) {
				ctx.setLineWidth(BORDER_WIDTH);
				if (checkNeeded) {
					ctx.drawImage(checkMark, x + checkX, y + checkY);
					borderColor = SELECTED_TILE_COLOR;
				}
			}

			ctx.setStrokeStyle(StringUtil.toHtmlColor(borderColor));
			ctx.strokeRect(x + padding, y + padding, w - padding, h - padding);
		}

		public boolean setFocus(int x, int y) {

			if (x < left || x > (left + width) || y < top + tableOffsetY
					|| y > (top + height + tableOffsetY)) {
				focusLost();
				return false;
			}

			boolean result = false;

			int col = (x - left) / colorIconSize.getWidth();
			int row = (y - top - tableOffsetY) / colorIconSize.getHeight();
			if (isValidCol(col) && isValidRow(row)) {
				currentCol = col;
				currentRow = row;
				draw();
				result = true;
			}
			return result;
		}

		private void focusLost() {
			currentCol = -1;
			currentRow = -1;
			draw();

		}

		public void unselect() {
			setSelectedCol(-1);
			setSelectedRow(-1);
			currentCol = -1;
			currentRow = -1;
		}

		public void select(int col, int row) {
			setSelectedCol(col);
			setSelectedRow(row);
			currentCol = col;
			currentRow = row;

			draw();
		}

		public void selectByColor(GColor color) {
			unselect();
			for (int idx = 0; idx < palette.size(); idx++) {
				if (colorEquals(color, palette.get(idx))) {
					select(idx % maxCol, idx / maxCol);
					break;
				}
			}
		}

		private boolean isValidCol(int col) {
			return (col >= 0 && col < maxCol);
		}

		private boolean isValidRow(int row) {
			return (row >= 0 && row < maxRow);
		}

		protected int getIndex(int col, int row) {
			return row * maxCol + col;
		}

		private GColor getColorFromPalette(int col, int row) {
			int idx = getIndex(col, row);
			return (palette != null && idx < palette.size() ? palette.get(idx)
					: null);
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public GColor getSelectedColor() {
			if (!(isValidCol(currentCol) && isValidRow(currentRow))) {
				setSelectedCol(-1);
				setSelectedRow(-1);
				return null;
			}
			setSelectedCol(currentCol);
			setSelectedRow(currentRow);

			return getColorFromPalette(currentCol, currentRow);

		}

		public void injectColor(GColor color) {
			palette.add(0, color);
			draw();
			if (palette.size() > getCapacity()) {
				palette.remove(getCapacity());
			}

		}

		public void setCheckNeeded(boolean checkNeeded) {
			this.checkNeeded = checkNeeded;
		}

		public void setTitle(String title, int offsetX, int offsetY) {
			this.title = title;
			titleOffsetX = offsetX;
			titleOffsetY = offsetY;
		}

		public int getSelectedCol() {
			return selectedCol;
		}

		public void setSelectedCol(int selectedCol) {
			this.selectedCol = selectedCol;
		}

		public int getSelectedRow() {
			return selectedRow;
		}

		public void setSelectedRow(int selectedRow) {
			this.selectedRow = selectedRow;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}
	}

	private class RecentTable extends ColorTable {
		private List<Entry> entries;

		private class Entry {
			ColorTable table;
			int col;
			int row;

			public Entry(ColorTable table) {
				this.table = table;
				this.col = table.getSelectedCol();
				this.row = table.getSelectedRow();
			}
		}

		public RecentTable(int x, int y, int col, int row) {
			super(x, y, col, row, null);
			entries = new ArrayList<>();
		}

		public void injectFrom(ColorTable source) {
			injectColor(source.getSelectedColor());
			entries.add(0, new Entry(source));
			Log.debug("capacity: " + getCapacity() + " Entries size: "
					+ entries.size());
			if (entries.size() > getCapacity()) {
				entries.remove(getCapacity());
			}
		}

		public void apply() {
			Entry entry = entries
					.get(getIndex(getSelectedCol(), getSelectedRow()));
			entry.table.select(entry.col, entry.row);
		}

	}

	private class PreviewPanel extends FlowPanel {
		private Label titleLabel;
		Canvas previewCanvas;
		private JLMContext2d previewCtx;
		private Label rgb;

		public PreviewPanel() {
			FlowPanel m = new FlowPanel();
			m.setStyleName("colorChooserPreview");
			titleLabel = new Label();
			previewCanvas = Canvas.createIfSupported();
			previewCanvas.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
			previewCanvas.setCoordinateSpaceHeight(PREVIEW_HEIGHT);
			previewCanvas.setCoordinateSpaceWidth(PREVIEW_WIDTH);
			previewCtx = Js.uncheckedCast(previewCanvas.getContext2d());
			rgb = new Label();
			add(titleLabel);
			m.add(previewCanvas);
			m.add(rgb);
			add(m);
		}

		public void update() {

			GColor color = getSelectedColor();
			if (color == null) {
				return;
			}
			rgb.setText(ColorObjectModel.getColorAsString(app, color));
			previewCtx.clearRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

			String htmlColor = StringUtil.toHtmlColor(color);

			previewCtx.setFillStyle(htmlColor);

			previewCtx.globalAlpha = getAlphaValue();
			previewCtx.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

			previewCtx.setStrokeStyle(htmlColor);

			previewCtx.globalAlpha = 1.0;
			previewCtx.setLineWidth(PREVIEW_BORDER_WIDTH);
			previewCtx.strokeRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		}

		public void setLabels(String previewTitle) {
			titleLabel.setText(previewTitle);
		}

	}

	private class OpacityPanel extends FlowPanel implements SliderInputHandler {
		private Label title;
		private Label minLabel;
		private Slider slider;
		private Label maxLabel;

		public OpacityPanel() {
			title = new Label();
			add(title);

			FlowPanel sp = new FlowPanel();
			sp.setStyleName("colorSlider");
			minLabel = new Label("0");
			sp.add(minLabel);

			slider = new Slider(0, 100);
			slider.setTickSpacing(1);

			sp.add(slider);
			maxLabel = new Label("100");
			sp.add(maxLabel);
			add(sp);
			slider.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					onSliderInput();
				}
			});
			Slider.addInputHandler(slider.getElement(), this);
		}

		@Override
		public void onSliderInput() {
			if (changeHandler != null) {
				changeHandler.onAlphaChange();
			}
			previewPanel.update();
		}

		public double getAlphaValue() {
			return isVisible() ? slider.getValue() / 100.0 : 1.0;
		}

		public void setLabels(String opacity) {
			title.setText(opacity);
		}

		public void setAlpaValue(double alpha) {
			slider.setValue((int) (alpha * 100));
		}
	}

	private class BackgroundColorPanel extends FlowPanel {
		RadioButton backgroundButton;
		RadioButton foregroundButton;
		GPushButton btnClearBackground;

		public BackgroundColorPanel() {
			setStyleName("BackgroundColorPanel");
			backgroundButton = new RadioButton("bg");
			foregroundButton = new RadioButton("fg");
			backgroundButton.setName("bgfg");
			foregroundButton.setName("bgfg");

			btnClearBackground = new GPushButton(new NoDragImage(
					MaterialDesignResources.INSTANCE.delete_black(), 24));
			btnClearBackground.setStyleName("ClearBackgroundButton");

			updateBackgroundButtons(false);

			add(foregroundButton);
			add(backgroundButton);
			add(btnClearBackground);
			btnClearBackground.setVisible(false);
			foregroundButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					setBackground(false);
				}
			});

			backgroundButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					setBackground(true);
				}
			});

			btnClearBackground.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					changeHandler.onClearBackground();

				}
			});

		}

		protected void setBackground(boolean background) {
			if (background) {
				changeHandler.onBackgroundSelected();
			} else {
				changeHandler.onForegroundSelected();
			}
			btnClearBackground.setVisible(background);
			updateBackgroundButtons(background);
		}

		private void updateBackgroundButtons(boolean background) {
			foregroundButton.setValue(!background);
			backgroundButton.setValue(background);

		}

		public void setLabels(String bgLabel, String fgLabel) {
			backgroundButton.setText(bgLabel);
			foregroundButton.setText(fgLabel);
		}
	}

	/**
	 * @param app
	 *            application
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param colorIconSize
	 *            swatch size
	 * @param padding
	 *            padding
	 */
	public ColorChooserW(final App app, int width, int height,
			Dimension colorIconSize, int padding) {
		this.app = app;
		lbBars = new BarList(app);
		lbBars.setVisible(false);

		canvas = Canvas.createIfSupported();
		canvas.setSize(width + "px", height + "px");
		canvas.setCoordinateSpaceHeight(height);
		canvas.setCoordinateSpaceWidth(width);
		ctx = JLMContextHelper.as(canvas.getContext2d());

		changeHandler = null;
		lastSource = null;

		this.colorIconSize = colorIconSize;
		this.padding = padding;

		int x = MARGIN_X;
		leftTable = new ColorTable(x, MARGIN_TOP, 2, 8,
				Arrays.asList(0xffffff, 0xff0000, 0xc0c0c0, 0xff7f00, 0xa0a0a0,
						0xbfff00, 0x808080, 0x00ff00, 0x606060, 0x00ffff,
						0x404040, 0x0000ff, 0x202020, 0x7f00ff, 0x000000,
						0xff00ff));

		x += leftTable.getWidth() + 5;

		mainTable = new ColorTable(x, 20, 8, 8, Arrays.asList(0xffc0cb,
				0xff99cc, 0xff6699, 0xff3366, 0xff0033, 0xcc0000, 0x800000,
				0x330000, 0xffefd5, 0xffcc33, 0xff9900, 0xff9933, 0xff6600,
				0xcc6600, 0x996600, 0x333300, 0xffeacd, 0xffff99, 0xffff66,
				0xffd700, 0xffcc66, 0xcc9900, 0x993300, 0x663300, 0xccffcc,
				0xccff66, 0x99ff00, 0x99cc00, 0x66cc00, 0x669900, 0x339900,
				0x006633, 0xd0f0c0, 0x99ff99, 0x66ff00, 0x33ff00, 0x00cc00,
				0x009900, 0x006400, 0x003300, 0xafeeee, 0x99ffff, 0x33ffcc,
				0x0099ff, 0x0099cc, 0x006699, 0x0033cc, 0x003399, 0xbcd4e6,
				0x99ccff, 0x66ccff, 0x6699ff, 0x7d7dff, 0x3333ff, 0x0000cc,
				0x000033, 0xccccff, 0xcc99ff, 0xcc66ff, 0x9966ff, 0x6600cc,
				0x800080, 0x4b0082, 0x330033, 0xe0b0ff, 0xff99ff, 0xff9999,
				0xff33cc, 0xdc143c, 0xcc0066, 0x990033, 0x660099));

		x += mainTable.getWidth() + 5;

		recentTable = new RecentTable(x, 22, 6, 4);
		otherTable = new ColorTable(x, 140, 6, 2, null);

		leftTable.setCheckNeeded(true);
		mainTable.setCheckNeeded(true);
		otherTable.setCheckNeeded(true);

		previewPanel = new PreviewPanel();
		previewPanel.setStyleName("optionsPanel");

		opacityPanel = new OpacityPanel();
		opacityPanel.setStyleName("optionsPanel");

		backgroundColorPanel = new BackgroundColorPanel();
		tables = Arrays.asList(leftTable, mainTable, recentTable, otherTable);

		setLabels();

		btnCustomColor = new Button("+");
		btnCustomColor.setStyleName("CustomColorButton");
		btnCustomColor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showCustomColorDialog();
			}

		});
		SimplePanel sp = new SimplePanel(btnCustomColor);
		sp.addStyleName("CustomColorButtonParent");

		add(canvas);
		add(sp);
		add(previewPanel);
		add(opacityPanel);
		add(backgroundColorPanel);
		add(lbBars);

		canvas.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (ColorTable table : tables) {
					GColor color = table.getSelectedColor();
					if (color != null) {
						colorChanged(table, color);
						break;
					}
				}

			}
		});

		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				int mx = event.getRelativeX(canvas.getElement());
				int my = event.getRelativeY(canvas.getElement());
				for (ColorTable table : tables) {
					table.setFocus(mx, my);
				}
			}
		});

		lbBars.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int idx = lbBars.getSelectedIndex();
				setSelectedBar(idx);

				if (changeHandler != null) {
					changeHandler.onBarSelected();
				}
			}
		});
	}

	protected void colorChanged(ColorTable source, GColor color) {
		selectedColor = color;
		previewPanel.update();

		if (lastSource != null && lastSource != source
				&& lastSource != recentTable) {
			lastSource.unselect();
		}

		lastSource = source;

		if (source != recentTable) {
			recentTable.injectFrom(source);
		} else {
			recentTable.apply();
		}

		if (changeHandler != null) {
			changeHandler.onColorChange(getSelectedColor());
		}

		if (source != null) {
			source.draw();
		}
	}

	/**
	 * @param color1
	 *            first color
	 * @param color2
	 *            second color
	 * @return colors are the same but not null
	 */
	public static boolean colorEquals(GColor color1, GColor color2) {
		return (color1 != null && color2 != null)
				&& (color1.getRed() == color2.getRed()
						&& color1.getGreen() == color2.getGreen()
						&& color1.getBlue() == color2.getBlue());
	}

	private void updateTables() {
		for (ColorTable table : tables) {
			table.draw();
		}
	}

	/**
	 * Update the UI.
	 */
	public void update() {
		updateTables();
		lbBars.update(isBarChart());
		setSelectedBar(lbBars.getSelectedIndex());
		previewPanel.update();
	}

	/**
	 * @param background
	 *            whether tho use this for background color
	 */
	public void setBackground(boolean background) {
		if (this.backgroundColorPanel != null) {
			backgroundColorPanel.setBackground(background);
		}
	}

	@Override
	public GColor getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @param color
	 *            selected color
	 */
	public void setSelectedColor(GColor color) {
		selectedColor = color;
		leftTable.selectByColor(color);
		mainTable.selectByColor(color);
		otherTable.selectByColor(color);
	}

	/**
	 * Update localization
	 */
	public void setLabels() {
		Localization loc = app.getLocalization();
		leftTable.setTitle("", 0, 0);
		recentTable.setTitle(loc.getMenu("RecentColor"), 0, 0);
		otherTable.setTitle(loc.getMenu("Other"), 0, 0);
		previewPanel.setLabels(loc.getMenu("Preview"));
		opacityPanel.setLabels(loc.getMenu("Opacity"));
		setBgFgTitles(loc.getMenu("BackgroundColor"),
				loc.getMenu("ForegroundColor"));
	}

	private void setBgFgTitles(String bg, String fg) {
		backgroundColorPanel.setLabels(bg, fg);
		update();
	}

	public void addChangeHandler(ColorChangeHandler handler) {
		this.changeHandler = handler;
	}

	/**
	 * @return alpha value
	 */
	public double getAlphaValue() {
		return opacityPanel.getAlphaValue();
	}

	/**
	 * @param alpha
	 *            alpha value
	 */
	public void setAlphaValue(double alpha) {
		opacityPanel.setAlpaValue(alpha);
	}

	/**
	 * @param enabled
	 *            whether to enable color panel
	 */
	public void enableColorPanel(boolean enabled) {
		canvas.setVisible(enabled);
		previewPanel.setVisible(enabled);
		btnCustomColor.setVisible(enabled);
	}

	public void enableOpacity(boolean enabled) {
		opacityPanel.setVisible(enabled);
	}

	/**
	 * @param enable
	 *            whether to enable background color panel
	 */
	public void enableBackgroundColorPanel(boolean enable) {
		backgroundColorPanel.setVisible(enable);
		// if (!enable) {
		// backgroundColorPanel.selectForeground();
		// }
	}

	/**
	 * @return whether background checkbox is checked (and visible)
	 */
	public boolean isBackgroundColorSelected() {
		return backgroundColorPanel.isVisible()
				&& backgroundColorPanel.backgroundButton.getValue();
	}

	/**
	 * Show custom color dialog.
	 */
	void showCustomColorDialog() {
		app.setWaitCursor();
		if (dialog == null) {
			dialog = new CustomColorDialog(app, this);
		}
		dialog.show(this.selectedColor);

		app.setDefaultCursor();
	}

	@Override
	public void onCustomColor(GColor color) {
		otherTable.injectColor(color);
		otherTable.select(0, 0);
		colorChanged(otherTable, color);
	}

	/**
	 * adds a clickhandler to the color-preview, to open the
	 * {@link CustomColorDialog}
	 */
	public void setColorPreviewClickable() {
		this.previewPanel.previewCanvas.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showCustomColorDialog();
			}
		});
	}

	/**
	 * @return whether this is for a barchart
	 */
	public boolean isBarChart() {
		return chartBars > 0;
	}

	/**
	 * @param chartBars
	 *            number of bars/slices in a chart
	 */
	public void setChartAlgo(int chartBars, Object[] geos) {
		this.chartBars = chartBars;
		lbBars.updateTranslationKeys(geos);
		lbBars.setBarCount(chartBars);
	}

	public int getBarCount() {
		return lbBars.getBarCount();
	}

	public int getSelectedBar() {
		return selectedBar;
	}

	public void setSelectedBar(int selectedBar) {
		this.selectedBar = selectedBar;
	}

	public GColor getAllBarsColor() {
		return allBarsColor;
	}

	public void setAllBarsColor(GColor allBarsColor) {
		this.allBarsColor = allBarsColor;
	}
}

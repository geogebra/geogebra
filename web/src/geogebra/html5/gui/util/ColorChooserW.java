package geogebra.html5.gui.util;

import geogebra.common.awt.GColor;
import geogebra.common.gui.dialog.options.model.ColorObjectModel;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.images.AppResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

public class ColorChooserW extends FlowPanel {

	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 100;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_X = 5;
	public static final GColor DEFAULT_COLOR = new GColorW(0);
	public static final GColor BOX_COLOR = new GColorW(0);
	public static final GColor SELECTED_BOX_COLOR = new GColorW(255, 0, 0);
	public static final String TITLE_FONT = "14pt geogebra-sans-serif";
	public static final int TITLE_HEIGHT = 20;
	public static final GColor FOCUS_COLOR = new GColorW(0, 0, 255);
	public static final double BORDER_WIDTH = 2;
	public static final double PREVIEW_BORDER_WIDTH = 14;
	private Canvas canvas;
	private Context2d ctx;
	private GDimensionW colorIconSize;
	private int padding;
	private List<ColorTable> tables;
	private ColorTable leftTable;
	private ColorTable mainTable;
	private ColorTable recentTable;
	private ColorTable otherTable;
	private GColor selectedColor;
	ColorChangeHandler changeHandler;
	private PreviewPanel previewPanel;
	private OpacityPanel opacityPanel;
	private BackgroundColorPanel backgroundColorPanel;
	
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
		private ImageElement checkMark;
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

		public ColorTable(int x, int y, int col, int row, List<Integer> data)
		{
			left = x;
			top = y;
			tableOffsetY = 0;
			maxCol = col;
			maxRow = row;
			capacity = maxCol * maxRow;
			this.title = "";
			palette = new ArrayList<GColor>();
			currentCol = -1;
			currentRow = -1;
			selectedCol = -1;
			selectedRow = -1;
			if (data != null) {
				for (Integer code: data) {
					palette.add(new GColorW(code));
				}
			}

			setWidth(col * colorIconSize.getWidth() + padding); 
			setHeight(row * colorIconSize.getHeight() + padding);

			checkMark = ImageElement.as(new Image(AppResources.INSTANCE.color_chooser_check().getSafeUri()).getElement());

			checkX = (colorIconSize.getWidth() - checkMark.getWidth()) / 2 + padding;
			checkY = (colorIconSize.getHeight() - checkMark.getHeight()) / 2  + padding;
			checkNeeded = false;
		}

		protected void drawTitle() {
			if (title.isEmpty()) {
				return;
			}
			ctx.save();
			ctx.translate((double)left, (double)top);

			ctx.setTextBaseline(TextBaseline.TOP);
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

			ctx.translate((double)left, (double)top);
			int x = padding;
			int y = tableOffsetY + padding;
			for (int row = 0; row < maxRow; row++) {
				for (int col = 0; col < maxCol; col++) {
					drawColorTile(col, row);
				}	
			}


			ctx.strokeRect(0, 0, getWidth(), getHeight());
			ctx.restore();

		}

		private void drawColorTile(int col, int row) {
			int h = colorIconSize.getHeight();
			int w = colorIconSize.getWidth(); 

			int x = (col * w);
			int y = tableOffsetY + (row * h);

			GColor borderColor = BOX_COLOR;
			ctx.setLineWidth(1);


			GColor fillColor = getColorFromPalette(col, row);
			ctx.setFillStyle(StringUtil.toHtmlColor(fillColor));

			ctx.fillRect(x + padding, y + padding, w - padding, h - padding);

			if (col == currentCol && row == currentRow) {
				ctx.setLineWidth(BORDER_WIDTH);
				borderColor = FOCUS_COLOR;				
			} else if (col == selectedCol && row == selectedRow) {
				ctx.setLineWidth(BORDER_WIDTH);
				if (checkNeeded) {
					ctx.drawImage(checkMark, x + checkX, y + checkY);
					borderColor = SELECTED_BOX_COLOR;
				}

			}


			ctx.setStrokeStyle(StringUtil.toHtmlColor(borderColor));
        	ctx.strokeRect(x + padding, y + padding, w - padding, h - padding);
			//	ctx.stroke();
		}

		public boolean setFocus(int x, int y) {

			if (x < left || x > (left + width) ||
					y < top + tableOffsetY || y > (top + height + tableOffsetY)	) {
				focusLost();
				return false;
			}

			boolean result = false;

			int col = (x - left) / colorIconSize.getWidth();
			int row = (y - top - tableOffsetY) / colorIconSize.getHeight();
			App.debug("Focus " + col + ", " + row);


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

		private boolean isValidCol(int col) {
			return (col  >= 0 && col < maxCol);
		}

		private boolean isValidRow(int row) {
			return (row  >= 0 && row < maxRow);
		}

		private final GColor getColorFromPalette(int col, int row) {
			int idx = row * maxCol + col;
			return (palette != null && idx < palette.size() ? palette.get(idx) : DEFAULT_COLOR);
		}

		public int getHeight() {
			return tableOffsetY + height;
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
				selectedCol = -1;
				selectedRow = -1;
				return null;
			}
			selectedCol = currentCol;
			selectedRow = currentRow;

			return getColorFromPalette(currentCol, currentRow);

		}

		public void injectColor(GColor color) {
			palette.add(0, color);
			draw();
			if (palette.size() > capacity) {
				palette.remove(capacity);
			}
		}

		public boolean isCheckNeeded() {
			return checkNeeded;
		}

		public void setCheckNeeded(boolean checkNeeded) {
			this.checkNeeded = checkNeeded;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title, int offsetX, int offsetY) {
			this.title = title;
			titleOffsetX = offsetX;
			titleOffsetY = offsetY;
		}

	}

	private class PreviewPanel extends FlowPanel {
		private Label title;
		private Canvas canvas;
		private Context2d ctx;
		private Label rgb;

		public PreviewPanel() {
			title = new Label();
			canvas = Canvas.createIfSupported();
			canvas.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
			canvas.setCoordinateSpaceHeight(PREVIEW_HEIGHT);
			canvas.setCoordinateSpaceWidth(PREVIEW_WIDTH);
			ctx = canvas.getContext2d();
			rgb = new Label();
			add(title);
			add(canvas);
			add(rgb);
		}

		public void update(GColor color) {
			rgb.setText(ColorObjectModel.getColorAsString(color));
			ctx.clearRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
			String htmlColor = StringUtil.toHtmlColor(color);
			ctx.setFillStyle(htmlColor);
			ctx.setGlobalAlpha(getAlphaValue());
			ctx.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

			ctx.setStrokeStyle(htmlColor);
			ctx.setGlobalAlpha(1.0);
			ctx.setLineWidth(PREVIEW_BORDER_WIDTH);
			ctx.strokeRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

		}
		
		public void setLabels(String previewTitle) {
			title.setText(previewTitle);
		}

	}
	private class OpacityPanel extends FlowPanel {
		private Label title;
		private Label minLabel;
		private Slider slider;
		private Label maxLabel;
		
		public OpacityPanel() {
			title = new Label();
			add(title);
			minLabel = new Label("1");
			add(minLabel);
	
			slider = new Slider(1, 100);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			add(slider);
			maxLabel = new Label("100");
			add(maxLabel);
			slider.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					colorChanged();
					previewPanel.update(selectedColor);
                }});
		}

		public float getAlphaValue() {
            return isVisible() ? slider.getValue() / 100.0f : 1.0f;
        }
		
		public void setLabels(String opacity) {
			title.setText(opacity);
		}

		public void setAlpaValue(float alpha) {
	        slider.setValue((int) (alpha * 100));
        }
	}

	private class BackgroundColorPanel extends FlowPanel {
		private RadioButton backgroundButton;
		private RadioButton foregroundButton;
		
		public BackgroundColorPanel() {
			backgroundButton = new RadioButton("bg");
			foregroundButton = new RadioButton("fg");
			backgroundButton.setName("bgfg");
			foregroundButton.setName("bgfg");
			add(backgroundButton);
			add(foregroundButton);
			
		}
		
		public void setLabels(String bgLabel, String fgLabel) {
			backgroundButton.setText(bgLabel);
			foregroundButton.setText(bgLabel);
		}
	}
	
	public ColorChooserW(int width, int height, GDimensionW colorIconSize, int padding) {
		canvas = Canvas.createIfSupported();
		canvas.setSize(width + "px", height + "px");
		canvas.setCoordinateSpaceHeight(height);
		canvas.setCoordinateSpaceWidth(width);
		ctx = canvas.getContext2d();
		
		changeHandler = null;

		this.colorIconSize = colorIconSize;
		this.padding = padding;

		int x = MARGIN_X;
		leftTable = new ColorTable(x, MARGIN_TOP, 2, 8, 
				Arrays.asList(
						0xffffff, 0xff0000,
						0xe0e0e0, 0xff7f00,
						0xc0c0c0, 0xffff00,
						0xa0a0a0, 0xbfff00,
						0x808080, 0x00ff00,
						0x606060, 0x00ffff,
						0x404040, 0x0000ff,
						0x202020, 0x7f00ff,
						0x000000, 0xff00ff
						)
				);

		x += leftTable.getWidth() + 5;

		mainTable = new ColorTable(x, 20, 8, 8, 
				Arrays.asList(
						0xffc0cb, 0xff99cc, 0xff6699, 0xff3366, 0xff0033, 0xcc0000, 0x800000, 0x330000, 
						0xffefd5, 0xffcc33, 0xff9900, 0xff9933, 0xff6600, 0xcc6600, 0x996600, 0x333300, 
						0xffeacd, 0xffff99,  0xffff66, 0xffd700, 0xffcc66, 0xcc9900, 0x993300, 0x663300, 
						0xccffcc, 0xccff66, 0x99ff00, 0x99cc00, 0x66cc00, 0x669900, 0x339900, 0x006633, 
						0xd0f0c0, 0x99ff99, 0x66ff00, 0x33ff00, 0x00cc00, 0x009900, 0x006400, 0x003300, 
						0xafeeee, 0x99ffff, 0x33ffcc, 0x0099ff, 0x0099cc, 0x006699, 0x0033cc, 0x003399, 
						0xbcd4e6, 0x99ccff, 0x66ccff, 0x6699ff, 0x7d7dff, 0x3333ff, 0x0000cc, 0x000033, 
						0xccccff, 0xcc99ff, 0xcc66ff, 0x9966ff, 0x6600cc, 0x800080, 0x4b0082, 0x330033, 
						0xe0b0ff, 0xff99ff, 0xff9999, 0xff33cc, 0xdc143c, 0xcc0066, 0x990033, 0x660099
						)); 

		x += mainTable.getWidth() + 5;

		recentTable = new ColorTable(x, 22, 6, 4, null); 
		otherTable = new ColorTable(x, 140, 6, 2, null); 

		leftTable.setCheckNeeded(true);
		mainTable.setCheckNeeded(true);
		
		
		previewPanel = new PreviewPanel();
		
		opacityPanel = new OpacityPanel();
		backgroundColorPanel = new BackgroundColorPanel();
		tables = Arrays.asList(leftTable, mainTable, recentTable, otherTable);
		setTitles("Recent", "Other", "Preview", "Backround", "Foreground");

		add(canvas);
		add(previewPanel);
		add(opacityPanel);
		add(backgroundColorPanel);
		
		canvas.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				GColor color = null;
 				boolean insertToRecent = false;
				for (ColorTable table: tables) {
					color = table.getSelectedColor();
					if (color != null) {
						insertToRecent = table != recentTable;
						break;
					}
				}		

 				if (color != null) {
					setSelectedColor(color);
					if (insertToRecent == true) {
						recentTable.injectColor(selectedColor);
					}
					
					colorChanged();
				}

			}});

		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			public void onMouseMove(MouseMoveEvent event) {
				int x = event.getRelativeX(canvas.getElement());
				int y = event.getRelativeY(canvas.getElement());
				for (ColorTable table: tables) {
					table.setFocus(x, y);
				}				
			}});

	}

	protected void colorChanged() {
		if (changeHandler != null) {
			changeHandler.onChangeColor(selectedColor);
		}
		previewPanel.update(getSelectedColor());
    }

	public boolean colorEquals(GColor color1, GColor color2) {
		return color1.getRGB() == color2.getRGB();
	}

	public void update() {
		for (ColorTable table: tables) {
			table.draw();
		}
	}


	public GColor getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(GColor color) {
		selectedColor = color;
		update();
		}	


	public void setTitles(String recentTitle, String otherTitle, String previewTitle,
			String bg, String fg) {
		leftTable.setTitle("", 0, 0);
		recentTable.setTitle(recentTitle, 0, 0);
		otherTable.setTitle(otherTitle, 0, 0);
        previewPanel.setTitle(previewTitle);
        backgroundColorPanel.setLabels(bg, fg);
		update();
	}
	
	public void setOpacityTitle(String title) {
		opacityPanel.setLabels(title);
	}
	public void addChangeHandler(ColorChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	public float getAlphaValue() {
		return opacityPanel.getAlphaValue();
	}

	public void setAlphaValue(float alpha) {
	    opacityPanel.setAlpaValue(alpha);
	    
    }

	public void enableOpacity(boolean enabled) {
		opacityPanel.setVisible(enabled);
	}
	
	public void enableBackgroundColorPanel(boolean enable) {
		backgroundColorPanel.setVisible(enable);
	}

	public boolean isBackgroundColorSelected() {
		
	    return backgroundColorPanel.backgroundButton.getValue();
    }
}

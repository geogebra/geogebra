package org.geogebra.web.web.gui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.ColorObjectModel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.web.gui.dialog.CustomColorDialog;
import org.geogebra.web.web.gui.dialog.CustomColorDialog.ICustomColor;
import org.geogebra.web.web.gui.images.AppResources;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;

public class ColorChooserW extends FlowPanel implements ICustomColor {

	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 100;
	private static final int MARGIN_TOP = 20;
	private static final int MARGIN_X = 5;
	public static final GColor NO_TILE_COLOR = new GColorW(255, 255, 255);
	public static final GColor NORMAL_TILE_COLOR = new GColorW(0);
	public static final GColor EMPTY_TILE_COLOR = new GColorW(16, 16, 16);
	public static final GColor SELECTED_TILE_COLOR = new GColorW(255, 0, 0);
	public static final String TITLE_FONT = "14pt geogebra-sans-serif";
	public static final int TITLE_HEIGHT = 20;
	public static final GColor FOCUS_COLOR = new GColorW(0, 0, 255);
	public static final double BORDER_WIDTH = 2;
	public static final double PREVIEW_BORDER_WIDTH = 14;
	Canvas canvas;
	Context2d ctx;
	GDimensionW colorIconSize;
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
	private Button addCustomColor;
	App app;
	private CustomColorDialog dialog;

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
			setCapacity(maxCol * maxRow);
			this.title = "";
			palette = new ArrayList<GColor>();
			currentCol = -1;
			currentRow = -1;
			setSelectedCol(-1);
			setSelectedRow(-1);
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
			ctx.translate(left, top);

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

			ctx.translate(left, top);
			int x = padding;
			int y = tableOffsetY + padding;
			for (int row = 0; row < maxRow; row++) {
				for (int col = 0; col < maxCol; col++) {
					drawColorTile(col, row);
				}	
			}

//			ctx.strokeRect(0, 0, getWidth(), getHeight());
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
			if (emptyTile == true) {
				fillColor = NO_TILE_COLOR;
	      	}
			
			ctx.setFillStyle(StringUtil.toHtmlColor(fillColor));

			ctx.fillRect(x + padding, y + padding, w - padding, h - padding);

			if (emptyTile == true) {
				borderColor = EMPTY_TILE_COLOR;
			} else	if (col == currentCol && row == currentRow && !emptyTile) {
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

			if (x < left || x > (left + width) ||
					y < top + tableOffsetY || y > (top + height + tableOffsetY)	) {
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
			return (col  >= 0 && col < maxCol);
		}

		private boolean isValidRow(int row) {
			return (row  >= 0 && row < maxRow);
		}

		protected int getIndex(int col, int row) { 
			return row * maxCol + col;
		}
		private final GColor getColorFromPalette(int col, int row) {
			int idx = getIndex(col, row);
			return (palette != null && idx < palette.size() ? palette.get(idx) : null);
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
		
		private List<Entry> entries;
		public RecentTable(int x, int y, int col, int row) {
	        super(x, y, col, row, null);
	        entries = new ArrayList<Entry>();
	    	
		}
		
		public void injectFrom(ColorTable source) {
			injectColor(source.getSelectedColor());
			entries.add(0, new Entry(source));
			App.debug("capacity: " + getCapacity() + " Entries size: " + entries.size());
			if (entries.size() > getCapacity()) {
				entries.remove(getCapacity());
			}
		}
		
		public void apply() {
			Entry entry = entries.get(getIndex(getSelectedCol(), getSelectedRow()));
			entry.table.select(entry.col, entry.row);
		}
		
		
	}
	private class PreviewPanel extends FlowPanel {
		private Label titleLabel;
		Canvas canvas;
		private Context2d ctx;
		private Label rgb;

		public PreviewPanel() {
			FlowPanel m = new FlowPanel();
			m.setStyleName("colorChooserPreview");
			titleLabel = new Label();
			canvas = Canvas.createIfSupported();
			canvas.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
			canvas.setCoordinateSpaceHeight(PREVIEW_HEIGHT);
			canvas.setCoordinateSpaceWidth(PREVIEW_WIDTH);
			ctx = canvas.getContext2d();
			rgb = new Label();
			add(titleLabel);
			m.add(canvas);
			m.add(rgb);
			add(m);
		}

		public void update(){
			
			GColor color = getSelectedColor();
			if (color == null) {
				return;
			}
			rgb.setText(ColorObjectModel.getColorAsString(app, color));
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
			titleLabel.setText(previewTitle);
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
			
			FlowPanel sp = new FlowPanel();
			sp.setStyleName("colorSlider");
			minLabel = new Label("0");
			sp.add(minLabel);
			
			slider = new Slider(0, 100);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			sp.add(slider);
			maxLabel = new Label("100");
			sp.add(maxLabel);
			add(sp);
			slider.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					if (changeHandler != null) {
						changeHandler.onAlphaChange();
					}
					previewPanel.update();
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
		RadioButton backgroundButton;
		RadioButton foregroundButton;
		PushButton btnClearBackground;
	
		public BackgroundColorPanel() {
			setStyleName("BackgroundColorPanel");
			backgroundButton = new RadioButton("bg");
			foregroundButton = new RadioButton("fg");
			backgroundButton.setName("bgfg");
			foregroundButton.setName("bgfg");
			btnClearBackground = new PushButton(new Image(AppResources.INSTANCE
					.delete_small()));
			btnClearBackground.setStyleName("ClearBackgroundButton");
			
			selectForeground();
			
			add(foregroundButton);
			add(backgroundButton);
			add(btnClearBackground);
			btnClearBackground.setVisible(false);
			foregroundButton.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					btnClearBackground.setVisible(false);
					changeHandler.onForegroundSelected();
                }});		
			
			backgroundButton.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					btnClearBackground.setVisible(true);
					changeHandler.onBackgroundSelected();
				}});		
			
			
			btnClearBackground.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
	                changeHandler.onClearBackground();
	                
                }});
			
		}
		public void selectForeground() {
			foregroundButton.setValue(true);
			backgroundButton.setValue(false);
		}
		public void setLabels(String bgLabel, String fgLabel) {
			backgroundButton.setText(bgLabel);
			foregroundButton.setText(fgLabel);
		}
	}
	
	public ColorChooserW(App app, int width, int height, GDimensionW colorIconSize, int padding) {
		this.app = app;
		canvas = Canvas.createIfSupported();
		canvas.setSize(width + "px", height + "px");
		canvas.setCoordinateSpaceHeight(height);
		canvas.setCoordinateSpaceWidth(width);
		ctx = canvas.getContext2d();
		
		changeHandler = null;
		lastSource = null;

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

		setPaletteTitles("RecentColor", "Other");
		setPreviewTitle("Preview");
		setBgFgTitles("BackgroundColor", "ForegroundColor");
		setOpacityTitle("Opacity");
		addCustomColor = new Button("+");
		addCustomColor.setStyleName("CustomColorButton");
		addCustomColor.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				showCustomColorDialog();
            }

			});
		SimplePanel sp = new SimplePanel(addCustomColor);
		sp.addStyleName("CustomColorButtonParent");
		add(canvas);
		add(sp);
		add(previewPanel);
		add(opacityPanel);
		add(backgroundColorPanel);
		
		canvas.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				for (ColorTable table: tables) {
					GColor color = table.getSelectedColor();
					if (color != null) {
						colorChanged(table, color);
						break;
					}
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

	protected void colorChanged(ColorTable source, GColor color) {

		selectedColor = color;
		previewPanel.update();
		
		if (lastSource != null && lastSource != source &&
				lastSource != recentTable) {
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

	public boolean colorEquals(GColor color1, GColor color2) {
		
		return  (color1 != null && color2 != null) && 
				(color1.getRed() == color2.getRed() &&
				color1.getGreen() == color2.getGreen() &&
				color1.getBlue() == color2.getBlue());
	}

	public void update() {
		for (ColorTable table: tables) {
			table.draw();
		}
		previewPanel.update();
	}


	public GColor getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(GColor color) {
		selectedColor = color;
		leftTable.selectByColor(color);
		mainTable.selectByColor(color);
		otherTable.selectByColor(color);
		
	}	


	public void setPaletteTitles(String recent, String other) {
		leftTable.setTitle("", 0, 0);
		recentTable.setTitle(recent, 0, 0);
		otherTable.setTitle(other, 0, 0);
	}
	
	public void setPreviewTitle(String title) {
			previewPanel.setLabels(title);
	}
	
	public void setBgFgTitles(String bg, String fg) {
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
//		if (!enable) {
//			backgroundColorPanel.selectForeground();
//		}
	}

	public boolean isBackgroundColorSelected() {
		
	    return backgroundColorPanel.isVisible() &&
	    		backgroundColorPanel.backgroundButton.getValue();
    }
	
	void showCustomColorDialog() {
		app.setWaitCursor();
		if (dialog == null) {
			dialog = new CustomColorDialog(app, this);
		}
		dialog.show(this.selectedColor);

		app.setDefaultCursor();
    }

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
		this.previewPanel.canvas.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showCustomColorDialog();
			}
		});
	}
}

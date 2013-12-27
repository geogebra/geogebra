package geogebra.html5.gui.util;

import geogebra.common.awt.GColor;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class ColorChooserW extends FlowPanel {

	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 100;
	private static final int MARGIN_TOP = 20;
	public static final int BOX_COLOR = 0x0;
	private static final int MARGIN_X = 5;
	private Canvas canvas;
	private Context2d ctx;
	private GDimensionW colorIconSize;
	private int padding;
	private ColorTable leftTable;
	private ColorTable mainTable;
	private ColorTable recentTable;
	private ColorTable otherTable;
	private int selectedColor;
	
	private class ColorTable {
		private int startX;
		private int startY;
		private int maxCol;
		private int maxRow;
		private List<Integer> data;
		private int width;
		private int height;

		public ColorTable(int x, int y, int col, int row, List<Integer> data)
		{
			startX = x;
			startY = y;
			maxCol = col;
			maxRow = row;
			this.data = data;
			setWidth(col * (colorIconSize.getWidth() + padding)); 
			setHeight(row * (colorIconSize.getHeight() + padding));
		}
		
		public void draw() {
			ctx.save();
			ctx.scale(1, 1);
			ctx.setLineWidth(0.5);
			ctx.translate((double)startX, (double)startY);
			int x = padding;
			int y = padding;
			int h = colorIconSize.getHeight();
			int w = colorIconSize.getWidth(); 
			for (int row = 0; row < maxRow; row++) {
				for (int col = 0; col < maxCol; col++) {
					final String color = getDataAt(col, row);
					ctx.setFillStyle(color);
					ctx.fillRect(x + padding , y + padding, w - padding, h - padding);	
					ctx.setFillStyle("#" + StringUtil.toHexString(BOX_COLOR));
					ctx.rect(x + padding , y + padding, w - padding, h - padding);	
					x += w ;
				}	
				x = padding;
				y += h;
			}
		
			ctx.setLineWidth(1);
		//	ctx.rect(0, 0, getWidth(), getHeight());
			ctx.stroke();
			ctx.restore();
			
		}
		
		private final String getDataAt(int col, int row) {
			int idx = row * maxCol + col;
			Integer color = (data != null && idx < data.size() ? data.get(idx) : 0xffffff);
			return "#" + StringUtil.toHexString(color);
		}

		public int getHeight() {
	        return height;
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

		public int getColorAt(int x, int y) {
	        if (x < startX || x > (startX + width) ||
	        		y < startY || y > (startY + height)	) {
	        	return -1;
	        }
	        
	        int col = (x - startX) / (colorIconSize.getWidth() + padding);
	        int row = (y - startY) / (colorIconSize.getHeight() + padding);
	        App.debug("HIT! " + col + ", " + row);
	        return 0xff;//getDataAt(col, row);
 
        }


	}

	public ColorChooserW(int width, int height, GDimensionW colorIconSize, int padding) {
		canvas = Canvas.createIfSupported();
		canvas.setSize(width + "px", height + "px");
		canvas.setCoordinateSpaceHeight(height);
		canvas.setCoordinateSpaceWidth(width);
		ctx = canvas.getContext2d();
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
	
		add(canvas);
		canvas.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				int x = event.getRelativeX(canvas.getElement());
				int y = event.getRelativeY(canvas.getElement());
	            int color = leftTable.getColorAt(x, y);
	            if (color == -1) {
	            	 color = mainTable.getColorAt(x, y);
	  	             	
	            }
	            
	            if (color == -1) {
	            	 color = recentTable.getColorAt(x, y);
	  	             	
	            }
	            
	            if (color == -1) {
	            	 color = otherTable.getColorAt(x, y);
	  	             	
	            }
	           
	            if (color != -1) {
	            	setSelectedColor(color);
	            
	            }

	            
            }});
	}
	
	public void update() {
		leftTable.draw();		
		mainTable.draw();
		recentTable.draw();
		otherTable.draw();
		drawPreview();
		}
	
	private void drawPreview() {
	    if (selectedColor == -1) {
	    	return;
	    }
	    ctx.save();
	    int x = padding;
	    int y = MARGIN_TOP + leftTable.getHeight() + 10;
	    ctx.setFillStyle("#" + StringUtil.toHexString(selectedColor));
	    ctx.fillRect(x, y, PREVIEW_WIDTH, PREVIEW_HEIGHT);
	    ctx.restore();
    }

	public int getSelectedColor() {
        return selectedColor;
    }

	public GColor getSelectedGColor() {
        return new GColorW(selectedColor);
    }

	public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        update();
	}	
}

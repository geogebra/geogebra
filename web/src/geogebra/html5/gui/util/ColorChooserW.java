package geogebra.html5.gui.util;

import geogebra.common.main.App;
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
	public static final String BOX_COLOR = "#000000";
	private static final int MARGIN_X = 5;
	private Canvas canvas;
	private Context2d ctx;
	private GDimensionW colorIconSize;
	private int padding;
	private ColorTable leftTable;
	private ColorTable mainTable;
	private ColorTable recentTable;
	private ColorTable otherTable;
	private String selectedColor;
	
	private class ColorTable {
		private int startX;
		private int startY;
		private int maxCol;
		private int maxRow;
		private List<String> data;
		private int width;
		private int height;

		public ColorTable(int x, int y, int col, int row, List<String> data)
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
					ctx.setFillStyle(BOX_COLOR);
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
			return data != null && idx < data.size() ? data.get(idx) : "#ffffff";
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

		public String getColorAt(int x, int y) {
	        if (x < startX || x > (startX + width) ||
	        		y < startY || y > (startY + height)	) {
	        	return null;
	        }
	        
	        int col = (x - startX) / (colorIconSize.getWidth() + padding);
	        int row = (y - startY) / (colorIconSize.getHeight() + padding);
	        App.debug("HIT! " + col + ", " + row);
	        return getDataAt(col, row);
 
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
						"#ffffff", "#ff0000",
						"#e0e0e0", "#ff7f00",
						"#c0c0c0", "#ffff00",
						"#a0a0a0", "#bfff00",
						"#808080", "#00ff00",
						"#606060", "#00ffff",
						"#404040", "#0000ff",
						"#202020", "#7f00ff",
						"#000000", "#ff00ff"
							)
				);
		
		x += leftTable.getWidth() + 5;
		
		mainTable = new ColorTable(x, 20, 8, 8, 
				Arrays.asList(
						"#ffc0cb", "#ff99cc", "#ff6699", "#ff3366", "#ff0033", "#cc0000", "#800000", "#330000", 
						"#ffefd5", "#ffcc33", "#ff9900", "#ff9933", "#ff6600", "#cc6600", "#996600", "#333300", 
						"#ffeacd", "#ffff99",  "#ffff66", "#ffd700", "#ffcc66", "#cc9900", "#993300", "#663300", 
						"#ccffcc", "#ccff66", "#99ff00", "#99cc00", "#66cc00", "#669900", "#339900", "#006633", 
						"#d0f0c0", "#99ff99", "#66ff00", "#33ff00", "#00cc00", "#009900", "#006400", "#003300", 
						"#afeeee", "#99ffff", "#33ffcc", "#0099ff", "#0099cc", "#006699", "#0033cc", "#003399", 
						"#bcd4e6", "#99ccff", "#66ccff", "#6699ff", "#7d7dff", "#3333ff", "#0000cc", "#000033", 
						"#ccccff", "#cc99ff", "#cc66ff", "#9966ff", "#6600cc", "#800080", "#4b0082", "#330033", 
						"#e0b0ff", "#ff99ff", "#ff9999", "#ff33cc", "#dc143c", "#cc0066", "#990033", "#660099"
						)); 
		
		x += mainTable.getWidth() + 5;
		
		recentTable = new ColorTable(x, 22, 6, 4, null); 
		otherTable = new ColorTable(x, 140, 6, 2, null); 
	
		add(canvas);
		canvas.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				int x = event.getRelativeX(canvas.getElement());
				int y = event.getRelativeY(canvas.getElement());
	            String color = leftTable.getColorAt(x, y);
	            if (color == null) {
	            	 color = mainTable.getColorAt(x, y);
	  	             	
	            }
	            
	            if (color == null) {
	            	 color = recentTable.getColorAt(x, y);
	  	             	
	            }
	            
	            if (color == null) {
	            	 color = otherTable.getColorAt(x, y);
	  	             	
	            }
	           
	            if (color != null) {
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
	    if (selectedColor == null) {
	    	return;
	    }
	    ctx.save();
	    int x = padding;
	    int y = MARGIN_TOP + leftTable.getHeight() + 10;
	    ctx.setFillStyle(selectedColor);
	    ctx.fillRect(x, y, PREVIEW_WIDTH, PREVIEW_HEIGHT);
	    ctx.restore();
    }

	public String getSelectedColor() {
        return selectedColor;
    }

	public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
        update();
	}	
}

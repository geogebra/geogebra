package geogebra.html5.gui.util;

import geogebra.html5.awt.GDimensionW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.FlowPanel;

public class ColorChooserW extends FlowPanel {

	private Canvas canvas;
	private Context2d ctx;
	private GDimensionW colorIconSize;
	private int padding;
	private ColorTable leftTable;
	private ColorTable mainTable;
	private ColorTable recentTable;
	private ColorTable otherTable;
	
	private class ColorTable {
		private int startX;
		private int startY;
		private int maxCol;
		private int maxRow;
		private int[] data;
		private int width;
		private int height;
		
		public ColorTable(int x, int y, int col, int row, int[] data)
		{
			startX = x;
			startY = y;
			maxCol = col;
			maxRow = row;
			this.data = data;
			width = col * (colorIconSize.getWidth() + padding) + padding; 
			height = row * (colorIconSize.getHeight() + padding) + padding;
			add(canvas);
		}
		
		public void draw() {
			ctx.save();
			ctx.scale(1, 1);
			ctx.translate((double)startX, (double)startY);
			int x = padding;
			int y = padding;
			int h = colorIconSize.getHeight();
			int w = colorIconSize.getWidth(); 
			for (int row = 0; row < maxRow; row++) {
				for (int col = 0; col < maxCol; col++) {
					ctx.setFillStyle("#ffff00");
					ctx.fillRect(x + padding , y + padding, w - padding, h - padding);	
					x += w ;
				}	
				x = padding;
				y += h;
			}
			ctx.setLineWidth(1);
			ctx.rect(0, 0, width, height);
			ctx.stroke();
			ctx.restore();
			
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
		
		leftTable = new ColorTable(20, 20, 2, 9, null); 
		mainTable = new ColorTable(90, 20, 9, 9, null); 
		recentTable = new ColorTable(300, 42, 6, 4, null); 
		otherTable = new ColorTable(300, 160, 6, 2, null); 
	}
	
	public void update() {
		leftTable.draw();		
		mainTable.draw();
		recentTable.draw();
		otherTable.draw();
		}
	
	
}

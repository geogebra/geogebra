package geogebra.export;

import geogebra.gui.view.Gridable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

public class ScalingPrintGridable extends PrintGridable {

	private double scale;

	public ScalingPrintGridable(Gridable g) {
		super(g);
		scale = 1;
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		double pWidth=pageFormat.getImageableWidth();
		double pHeight=pageFormat.getImageableHeight();
		
//		double pSum=0;
		int sum=0;
//		int pagesHor=0;
		ArrayList<Integer> boundsHor=new ArrayList<Integer>();
		boundsHor.add(sum);
		for (int i=0;i<colWidths.length;i++){
			if ((sum+colWidths[i]-boundsHor.get(boundsHor.size()-1)>pWidth)&& //the next cell won't fit
					(sum>boundsHor.get(boundsHor.size()-1))){ //the size increased
				boundsHor.add(sum);
			}
			sum+=colWidths[i];
		}
		boundsHor.add(sum);
		
		sum=0;
		ArrayList<Integer> boundsVer=new ArrayList<Integer>();
		boundsVer.add(sum);
		for (int i=0;i<rowHeights.length;i++){
			if ((sum+rowHeights[i]-boundsVer.get(boundsVer.size()-1)>pHeight)&& //the next cell won't fit
					(sum>boundsVer.get(boundsVer.size()-1))){ //the size increased
				boundsVer.add(sum);
			}
			sum+=rowHeights[i];
		}
		boundsVer.add(sum);
		int pagesHor=boundsHor.size()-1;
		int pagesVer=boundsVer.size()-1;
		
		
		if (pageIndex>=pagesHor*pagesVer)
			return Printable.NO_SUCH_PAGE;
		
		int px=pageIndex%pagesHor;
		int py=pageIndex/pagesHor;
		
		Rectangle bounds=new Rectangle(boundsHor.get(px),boundsVer.get(py),
				boundsHor.get(px+1)-boundsHor.get(px),boundsVer.get(py+1)-boundsVer.get(py));
		
		Graphics2D g2d=(Graphics2D)graphics;
		g2d.scale(scale, scale);
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2d.translate(-bounds.x, -bounds.y);
		g2d.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);
		gridable.getApplication().exporting=true;
		Component[][] comp=gridable.getPrintComponents();
		int down=0;
		for (int i=0;i<comp.length;i++){
			int height=0;
			int left=0;
			for (int j=0;j<comp[i].length;j++){
				comp[i][j].print(g2d);
				g2d.translate(comp[i][j].getWidth(), 0);
				left+=comp[i][j].getWidth();
				height=Math.max(height, comp[i][j].getHeight());
			}
			g2d.translate(-left,height);
			down+=height;
		}
		g2d.translate(0,-down);
		g2d.setColor(Color.BLACK);
		g2d.draw(bounds);

		gridable.getApplication().exporting=false;
		return Printable.PAGE_EXISTS;
	}

	void setScale(double scale) {
		this.scale = scale;
	}
}

package geogebra.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.main.GeoGebraColorConstants;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Set;


/**
 * Methods for drawing custom spreadsheet borders
 * 
 * @author G. Sturr
 *
 */
public class SpreadsheetBorders {

	/**
	 * Draws custom borders for all cells recorded in the spreadsheet border
	 * format map.
	 * 
	 * @param g2
	 * @param table
	 */
	public static void drawFormatBorders(Graphics2D g2, MyTableD table){

		g2.setColor(geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.BLACK));
		g2.setStroke(new BasicStroke(1));

		HashMap<GPoint,Object> map = table.getCellFormatHandler().getFormatMap(CellFormat.FORMAT_BORDER);
		Set<GPoint> formatCell = map.keySet();

		int c = 0,r = 0;
		for(GPoint cell:formatCell){

			Byte b = (Byte) table.getCellFormatHandler().getCellFormat(cell, CellFormat.FORMAT_BORDER);
			if(b != null){
				c = cell.x;
				r  = cell.y;
				//System.out.println(cell.toString());
				if(c == -1 || r == -1)
					handleRowOrColumnGridFormat(g2, table, c, r, b);
				else
					drawPartialBorder(g2,table, c,r,c+1,r+1,b);
			}
		}

	}

	/**
	 * Draws row/column grid lines. Byte v determines the line type.
	 * (top/bottom row line or a left/right column line).
	 * 
	 * @param g2
	 * @param table
	 * @param col
	 * @param row
	 * @param v
	 */
	public static void handleRowOrColumnGridFormat(Graphics2D g2, MyTableD table, int col, int row, byte v){

		// row
		if(col == -1){

			// if the format includes right or left border then draw borders for each cell individually
			if(!isZeroBit(v,0) || !isZeroBit(v,2)){
				for(int c = 0; c < table.getColumnCount(); c++){
					drawPartialBorder(g2,table, c,row,c+1,row+1,v);
				}

				// if no row borders are given then this must be an inside border
				// so inside row lines need to be drawn
				if(!isZeroBit(v,1) || !isZeroBit(v,3)){
					// how?
				}

			}

			// otherwise just draw a border line for an entire row
			else{	
				// top bar
				if(!isZeroBit(v,1))
					drawRowBorder(g2, table, row);
				// bottom bar
				if(!isZeroBit(v,3))
					drawRowBorder(g2, table, row+1);
			}
		}


		// column
		if(row == -1){

			// if the format includes row borders then draw each cell individually
			if(!isZeroBit(v,1) || !isZeroBit(v,3)){
				for(int r = 0; r < table.getRowCount(); r++){
					drawPartialBorder(g2,table, col,r,col+1,r+1,v);
				}
			}

			// otherwise just draw a border line for an entire column
			else{	
				// left column
				if(!isZeroBit(v,0))
					drawColumnBorder(g2, table, col);
				// right column
				if(!isZeroBit(v,2))
					drawColumnBorder(g2, table, col+1);
			}
		}
	}


	/**
	 * Draws a partial border around the rectangular region from row1 down to row2
	 * and across from col1 to col2. Byte v determines which sides of the border are drawn.
	 * 
	 * @param g2
	 * @param table
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @param v
	 */
	public static void drawPartialBorder(Graphics2D g2, MyTableD table, int col1, int row1, int col2, int row2, byte v){

		Rectangle rect1 = table.getCellRect(row1, col1, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = table.getCellRect(row2, col2, true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		// Draw bars by bit position
		//
		//    1 
		//  0   2 
		//    3
		// 
		// left bar, 0
		if(!isZeroBit(v,0))
			g2.drawLine(r1, c1, r1, c2);
		// top bar, 1
		if(!isZeroBit(v,1))
			g2.drawLine(r1, c1, r2, c1);
		// right bar, 2
		if(!isZeroBit(v,2))
			g2.drawLine(r2, c1, r2, c2);
		// bottom bar, 3
		if(!isZeroBit(v,3))
			g2.drawLine(r1, c2, r2, c2);

	}


	/**
	 * Draws a grid line beneath the given row
	 * @param g2
	 * @param table
	 * @param row
	 */
	public static void drawRowBorder(Graphics2D g2, MyTableD table, int row){

		Rectangle rect1 = table.getCellRect(row, 0, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = table.getCellRect(row, table.getColumnCount(), true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		g2.drawLine(r1, c1, r2, c2);

	}


	/**
	 * Draws a grid line to the right the give column
	 * @param g2
	 * @param table
	 * @param column
	 */
	public static void drawColumnBorder(Graphics2D g2, MyTableD table, int column){

		Rectangle rect1 = table.getCellRect(0, column, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = table.getCellRect(table.getRowCount(), column, true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		g2.drawLine(r1, c1, r2, c2);

	}


	public static  boolean isZeroBit(int value, int position){
		return (value &= (1 << position)) == 0;
	} 



}

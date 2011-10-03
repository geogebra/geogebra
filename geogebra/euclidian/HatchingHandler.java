package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.discrete.delauney.Pnt;
import geogebra.kernel.discrete.delauney.Triangle;
import geogebra.kernel.discrete.delauney.Triangulation;
import geogebra.main.Application;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Michael Borcherds
 *
 */
public class HatchingHandler {

	private static BufferedImage bufferedImage = null;

	public static void setHatching(Graphics2D g2, BasicStroke objStroke, Color color, Color bgColor, float backgroundTransparency, double dist, double angle ) {

		// round to nearest 5 degrees
		angle = Math.round(angle / 5) * Math.PI/36;

		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI) angle = 0;

		// constrain distance between 5 and 50 pixels
		if (dist < 5) dist = 5;
		else if (dist > 50) dist = 50;

		double x = dist / Math.sin(angle);
		double y = dist / Math.cos(angle);

		int xInt = (int)Math.abs(Math.round((x)));
		int yInt = (int)Math.abs(Math.round((y)));

		if (angle == 0) { // horizontal

			xInt = 20;
			yInt = (int)dist;

		} else if (Kernel.isEqual(Math.PI/2, angle, 10E-8)) { // vertical
			xInt = (int)dist;
			yInt = 20;

		}

		int currentWidth = bufferedImage == null ? 0 : bufferedImage.getWidth();
		int currentHeight = bufferedImage == null ? 0 : bufferedImage.getHeight();

		if ( bufferedImage == null || currentWidth < xInt * 3 || currentHeight < yInt * 3)
			bufferedImage =
				new BufferedImage(Math.max(currentWidth, xInt * 3), Math.max(currentHeight, yInt * 3), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = bufferedImage.createGraphics();

		// enable anti-aliasing
		EuclidianView.setAntialiasing(g2d);

		// enable transparency
		g2d.setComposite(AlphaComposite.Src);

		// paint background transparent
		if (bgColor == null)
			g2d.setColor(new Color(255, 255, 255, (int)(backgroundTransparency * 255f))); 
		else
			g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), (int)(backgroundTransparency * 255f))); 
		g2d.fillRect(0, 0, xInt*3, yInt*3);


		//g2d.setColor(color);
		g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));

		g2d.setStroke(objStroke);
		if (angle == 0) { // horizontal

			g2d.drawLine(0, yInt, xInt * 3, yInt);
			g2d.drawLine(0, yInt * 2, xInt * 3, yInt * 2);

		} else if (Kernel.isEqual(Math.PI/2, angle, 10E-8)) { // vertical 
			g2d.drawLine(xInt, 0, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, xInt * 2, yInt * 3);

		}
		else if (y > 0) {
			g2d.drawLine(xInt * 3, 0, 0, yInt * 3);
			g2d.drawLine(xInt * 3, yInt, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, 0, yInt * 2);
		}
		else 
		{
			g2d.drawLine(0, 0, xInt * 3, yInt * 3);
			g2d.drawLine(0, yInt, xInt * 2, yInt * 3);
			g2d.drawLine(xInt, 0, xInt * 3, yInt * 2);
		}

		// paint with the texturing brush
		Rectangle rect = new Rectangle(0, 0, xInt, yInt);

		// use the middle square of our 3 x 3 grid to fill with
		g2.setPaint(new TexturePaint(bufferedImage.getSubimage(xInt, yInt, xInt, yInt), rect));


	}



	public static void setTexture(Graphics2D g2, GeoElement geo, float alpha) {

		if(geo.getFillImage() == null){
			g2.setPaint(geo.getFillColor()); 
			return;
		}

		BufferedImage image = geo.getFillImage();
		Rectangle2D tr = new Rectangle2D.Double(0, 0, image.getWidth(), image
				.getHeight());

		TexturePaint tp;

		if (alpha < 1.0f) {			
			BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = copy.createGraphics();

			// enable anti-aliasing
			EuclidianView.setAntialiasing(g2d);

			// set total transparency
			g2d.setComposite(AlphaComposite.Src);

			Color bgColor = geo.getBackgroundColor();

			// paint background transparent
			if (bgColor == null)
				g2d.setColor(new Color(0,0,0,0));
			else
				g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0)); 
			g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

			if (alpha > 0.0f) {
				// set partial transparency
				AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);                

				// paint image with specified transparency
				g2d.drawImage(image, 0, 0, null);
			}

			tp = new TexturePaint(copy, tr);
		}
		else {
			tp = new TexturePaint(image, tr);
		}

		//tr = new Rectangle2D.Double(0, 0, 200, 200);
		//tp = new TexturePaint(getSeamlessTexture(4, 50), tr);

		g2.setPaint(tp);       



	}

	private static BufferedImage getSeamlessTexture(int n, int pixelsPerPoint) {
		Triangulation dt;                   // Delaunay triangulation
		Triangle initialTriangle;           // Initial triangle

		int width = n * pixelsPerPoint;
		int height = width;

		int initialSize = width * 2;     // Size of initial triangle

		initialTriangle = new Triangle(
				new Pnt(-initialSize, -initialSize),
				new Pnt( initialSize, -initialSize),
				new Pnt(           0,  initialSize));
		dt = new Triangulation(initialTriangle);

		ArrayList<Pnt> points = new ArrayList<Pnt>();

		// place random points
		for (double i = 0 ; i < n ; i++) {
			for (double j = 0 ; j < n ; j++) {

				double x = i + Math.random()*0.8+0.1;
				double y = j + Math.random()*0.8+0.1;

				Pnt point = new Pnt(x, y);
				delaunayPlace(dt, points, point);

				if (i == 0) {
					delaunayPlace(dt, points, new Pnt(x + n, y));

					if (j == 0) delaunayPlace(dt, points, new Pnt(x + n, y + n));
					else if (j == n - 1) delaunayPlace(dt, points, new Pnt(x + n, y - n));
				} else  if (i == n - 1) {
					delaunayPlace(dt, points, new Pnt(x - n, y));

					if (j == 0) delaunayPlace(dt, points, new Pnt(x - n, y + n));
					else if (j == n - 1) delaunayPlace(dt, points, new Pnt(x - n, y + n));

				}


				if (j == 0) {
					delaunayPlace(dt, points, new Pnt(x, y + n));

				} else if (j == n - 1) {
					delaunayPlace(dt, points, new Pnt(x, y - n));
				}

			}
		}
		System.err.println("\n\n\n");

		BufferedImage image = new BufferedImage(n * pixelsPerPoint, n * pixelsPerPoint, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();


		g2d.setStroke(new MyBasicStroke(0.0f));

		GeneralPath gp = new GeneralPath();
		Area intersection = new Area();

		//Rectangle2D left = new Rectangle(0, 0, 0, height);
		//Rectangle2D right = new Rectangle(width, 0, width, height);
		//Rectangle2D top = new Rectangle(0, height, width, height);
		//Rectangle2D bottom = new Rectangle(0, 0, width, 0);

		Point2D bottomRight = new Point.Float(width - 1,0);
		Point2D bottomLeft = new Point.Float(0,0);
		Point2D topRight = new Point.Float(width - 1, height - 1);
		Point2D topLeft = new Point.Float(0, height -1);

		gp.reset();
		gp.moveTo(0, 0);
		gp.lineTo(0, height);
		gp.lineTo(-1, height);
		gp.lineTo(-1, 0);
		gp.closePath();
		Area left = new Area(gp);

		gp.reset();
		gp.moveTo(width, 0);
		gp.lineTo(width, height);
		gp.lineTo(width + 1, height);
		gp.lineTo(width + 1, 0);
		gp.closePath();
		Area right = new Area(gp);

		gp.reset();
		gp.moveTo(0, 0);
		gp.lineTo(width, 0);
		gp.lineTo(width, -1);
		gp.lineTo(0,-1);
		gp.closePath();
		Area bottom = new Area(gp);

		gp.reset();
		gp.moveTo(0, height);
		gp.lineTo(width, height);
		gp.lineTo(width, height + 1);
		gp.lineTo(0, height + 1);
		gp.closePath();
		Area top = new Area(gp);

		//Area left = new Area(new Line2D.Double(0,0,0,height));
		//gp.reset();
		//Area right = new Area(new Line2D.Double(width,0,width,height));
		//Area top = new Area(new Line2D.Double(0,height,width,height));
		//Area bottom = new Area(new Line2D.Double(0,0,width,0));

		Rectangle2D all = new Rectangle(0, 0, width, height);


		// enable anti-aliasing
		EuclidianView.setAntialiasing(g2d);
		// Keep track of sites done; no drawing for initial triangles sites
		HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
		for (Triangle triangle : dt)
			for (Pnt site: triangle) {
				if (done.contains(site)) continue;
				done.add(site);
				List<Triangle> list = dt.surroundingTriangles(site, triangle);
				//Pnt[] vertices = new Pnt[list.size()];
				int i = 0;
				double sigmaX = 0;
				double sigmaY = 0;
				StringBuilder sb = new StringBuilder();
				sb.append("Polygon[");
				gp.reset();
				//Pnt pointInside = null;
				for (Triangle tri: list) {
					Pnt p = tri.getCircumcenter();
					//if (all.contains(p.coord(0) * pixelsPerPoint, p.coord(1) * pixelsPerPoint)) {
					//	pointInside = p;
					//}
					if (gp.getCurrentPoint() == null) {
						gp.moveTo((float)p.coord(0) * pixelsPerPoint, ((float)p.coord(1) * pixelsPerPoint));
					} else {
						gp.lineTo((float)p.coord(0) * pixelsPerPoint, ((float)p.coord(1) * pixelsPerPoint));
					}
					sb.append('(');
					sb.append(p.coord(0));
					sb.append(',');
					sb.append(p.coord(1));
					sb.append("),");
					//vertices[i++] = tri.getCircumcenter();
				}
				gp.closePath();

				//if (pointInside != null)
				//{ // if null then it's all outside the region -> don't draw

				Color color = null;

				Area areaGP = new Area(gp);



				// left
				intersection.reset();
				intersection.add(areaGP);
				intersection.intersect(left);     	
				if (!intersection.isEmpty()) {
					Rectangle rec = intersection.getBounds();

					//Application.debug("left: testing point at height "+rec.y + rec.height / 2);
					int col = image.getRGB(width - 1, rec.y + rec.height / 2); // test point on right edge
					//Application.debug(col);
					if (col != 0) 
						color = new Color(col);                
				}

				// right
				if (color == null) {
					intersection.reset();
					intersection.add(areaGP);
					intersection.intersect(right);     	
					if (!intersection.isEmpty()) {
						Rectangle rec = intersection.getBounds();

						//Application.debug("right: testing point at height "+rec.y + rec.height / 2);
						int col = image.getRGB(0, rec.y + rec.height / 2); // test point on left edge
						//Application.debug(col);
						if (col != 0) 
							color = new Color(col);                
					}
				}

				// top
				if (color == null) {
					intersection.reset();
					intersection.add(areaGP);
					intersection.intersect(top);     	
					if (!intersection.isEmpty()) {
						Rectangle rec = intersection.getBounds();

						//Application.debug("top: testing point at width "+rec.x + rec.width / 2);
						int col = image.getRGB(rec.x + rec.width / 2, 0); // test point on bottom edge
						//Application.debug(col);
						if (col != 0) 
							color = new Color(col);                
					}
				}

				// bottom
				if (color == null) {
					intersection.reset();
					intersection.add(areaGP);
					intersection.intersect(bottom);     	
					if (!intersection.isEmpty()) {
						Rectangle rec = intersection.getBounds();

						//Application.debug("bottom: testing point at width "+rec.x + rec.width / 2);
						int col = image.getRGB(rec.x + rec.width / 2, height - 1); // test point on top edge
						//Application.debug(col);
						if (col != 0) 
							color = new Color(col);                
					}
				}

				int colCorner = image.getRGB(0,0);
				if (colCorner == 0) colCorner = image.getRGB(0, height - 1);
				if (colCorner == 0) colCorner = image.getRGB(width - 1, height - 1);
				if (colCorner == 0) colCorner = image.getRGB(width - 1, 0);

				// corners
				if (color == null && colCorner != 0) {
					if (gp.contains(topRight) || gp.contains(topLeft) || gp.contains(bottomRight) || gp.contains(bottomLeft)) {
						color = new Color(colCorner);
						Application.debug("Corner!!");
					}
				}

				/*

	                    if (gp.intersects(left)) {
	                    	int col = image.getRGB(width - 1, (int)pointInside.coord(1) * pixelsPerPoint); // test point on right edge
	                    	Application.debug(col);
	                    	if (col != 0) 
	                    		color = new Color(col);                
	                    }
	                    if (color == null && gp.intersects(right)) {
	                    	int col = image.getRGB(0, (int)pointInside.coord(1) * pixelsPerPoint); // test point on left edge
	                    	Application.debug(col);
	                    	if (col != 0) 
	                    		color = new Color(col);                
	                    }
	                    if (color == null && gp.intersects(top)) {
	                    	int col = image.getRGB((int)pointInside.coord(0) * pixelsPerPoint, 0); // test point on bottom edge
	                    	Application.debug(col);
	                    	if (col != 0) 
	                    		color = new Color(col);                
	                    }
	                    if (color == null && gp.intersects(bottom)) {
	                    	int col = image.getRGB((int)pointInside.coord(0) * pixelsPerPoint, height - 1); // test point on top edge
	                    	Application.debug(col);
	                    	if (col != 0) 
	                    		color = new Color(col);                
	                    }*/

				if (color != null)
					g2d.setColor(color);
				else
					g2d.setColor(new Color((float)Math.random(),(float)Math.random(),(float)Math.random(), 1.0f));

				//g2d.setClip((Shape)gp);


				Pnt cent = null;

				// find center
				Iterator<Pnt> it = points.iterator();
				while (it.hasNext()) {
					Pnt ppp = it.next();
					if (gp.contains(new Point2D.Double(ppp.coord(0) * pixelsPerPoint, ppp.coord(1) * pixelsPerPoint)))
						cent = ppp;
				}

				if (cent != null) {
					double radius = 0;
					// find max distance from center
					for (Triangle tri: list) {
						Pnt p = tri.getCircumcenter();
						radius = Math.max(radius, p.distance(cent));
					}

					/*
					Point2D center = new Point2D.Double(cent.coord(0)* pixelsPerPoint, cent.coord(1)* pixelsPerPoint);
					//float radius = 25;
					//Point2D focus = new Point2D.Double(Math.random() * width, Math.random() * height);
					float[] dist = {0.0f, 1.0f};
					Color[] colors = {Color.BLACK, Color.WHITE};
					RadialGradientPaint pp =
						new RadialGradientPaint(center, (float)(radius * pixelsPerPoint * 1.01), 
								dist, colors,
								CycleMethod.NO_CYCLE);

					g2d.setPaint(pp);
*/


/*
					g2d.setClip(gp);
					
					for (int r = (int)(radius* pixelsPerPoint) ; r >= 0 ; r--) {
						g2d.setColor(new Color((int)(r * 255 / radius),(int)(r * 255 / radius),(int)(r * 255 / radius)));
						//g2d.setColor(new Color((float)Math.random(),(float)Math.random(),(float)Math.random(), 1.0f));
						g2d.fillOval((int)(cent.coord(0)* pixelsPerPoint - r/2), (int)(cent.coord(1)* pixelsPerPoint - r/2), r, r);
					}
					*/
					
					g2d.fill(gp);
					
					//g2d.fill(all);
				}
				//}
				sb.setLength(sb.length()-1); // remove ','
				sb.append("]\n"); // remove ','
				g2d.setStroke(EuclidianView.getDefaultStroke());
				//System.err.println(sb.toString());
				//draw(vertices, withFill? getColor(site) : null);
				//if (withSites) draw(site);
			}
		/*
	        for (Simplex<Pnt> triangle: dt)
	            for (Simplex<Pnt> other: dt.neighbors(triangle)) {
	                Pnt p = Pnt.circumcenter(triangle.toArray(new Pnt[0]));
	                Pnt q = Pnt.circumcenter(other.toArray(new Pnt[0]));
	                //draw(p,q);
	                System.err.println("Segment[("+p.coord(0)+","+p.coord(1)+"),("+q.coord(0)+","+q.coord(1)+")]");
	            }*/

		//g2d.setColor(Color.black);
		//g2d.drawLine(0,0,width-1,0);
		//g2d.drawLine(width-1,0, width-1, height-1);
		//g2d.drawLine(width-1, height-1, 0,height-1);
		//g2d.drawLine(0,height-1,0,0);
		return image;

	}



	private static void delaunayPlace(Triangulation dt, ArrayList<Pnt> points,
			Pnt point) {
		dt. delaunayPlace(point);
		points.add(point);
	}

}

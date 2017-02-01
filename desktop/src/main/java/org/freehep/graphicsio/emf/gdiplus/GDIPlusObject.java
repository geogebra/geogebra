// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Properties;

import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The Object metafile record contains information on an object which must be
 * stored for use by a future metafile record. Examples include pens, brushes,
 * fonts, images, StringFormat objects, and so on. Naturally, the data format of
 * each type of object differs, and so the Flags value is used to determine
 * which type of object is being stored.
 * <p>
 * Object metafile records contain a numeric index (0 to 255). The first Object
 * record to appear in the file will have an index of 0, the next record 1, and
 * so on. Applications which process metafiles should store these objects in an
 * index-based collection so that a later record can access them. For example, a
 * future DrawRects record might reference the Pen with index 2, and the
 * application should ensure that that object is still accessible. Since the
 * index is only one byte, only 256 objects can be stored at any time. Thus, a
 * simple stack will not suffice and applications will have to be prepared for
 * new Object records overwriting old ones with the same index.
 * 
 * FIXME: no support for 16 bit
 * 
 * @author Mark Donszelmann
 * @version $Id: GDIPlusObject.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class GDIPlusObject extends EMFPlusTag {

	protected final static int INVALID = 0x000;
	protected final static int BRUSH = 0x100;
	protected final static int PEN = 0x200;
	protected final static int PATH = 0x300;
	protected final static int REGION = 0x400;
	protected final static int IMAGE = 0x500;
	protected final static int FONT = 0x600;
	protected final static int STRING_FORMAT = 0x700;
	protected final static int IMAGE_ATTRIBUTES = 0x800;
	protected final static int CUSTOM_LINE_CAP = 0x900;

	protected final static int BRUSH_TYPE_SOLID_COLOR = 0;
	protected final static int BRUSH_TYPE_HATCH_FILL = 1;
	protected final static int BRUSH_TYPE_TEXTURE_GRADIENT = 2;
	protected final static int BRUSH_TYPE_PATH_GRADIENT = 3;
	protected final static int BRUSH_TYPE_LINEAR_GRADIENT = 4;

	protected final static int WRAP_MODE_TYLE = 0;
	protected final static int WRAP_MODE_TYLE_FLIP_X = 1;
	protected final static int WRAP_MODE_TYLE_FLIP_Y = 2;
	protected final static int WRAP_MODE_TYLE_FLIP_XY = 3;
	protected final static int WRAP_MODE_CLAMP = 4;

	protected final static int IMAGE_TYPE_UNKNOWN = 0;
	protected final static int IMAGE_TYPE_BITMAP = 1;
	protected final static int IMAGE_TYPE_METAFILE = 2;

	protected final static int FILL_MODE_ALTERNATE = 0x0000;
	protected final static int FILL_MODE_WINDING = 0x2000;

	private Paint brush;
	private BasicStroke stroke;
	private PathPoint[] path;
	private int pathFillMode;
	private RenderedImage image;

	public GDIPlusObject() {
		super(8, 1);
	}

	public GDIPlusObject(int index, Paint brush) {
		this();
		this.brush = brush;
		flags = index | BRUSH;
	}

	public GDIPlusObject(int index, Stroke stroke, Paint brush) {
		this();
		if (!(stroke instanceof BasicStroke)) {
			throw new IllegalArgumentException(getClass()
					+ ": can only handle Stroke of class BasicStroke");
		}
		this.stroke = (BasicStroke) stroke;
		this.brush = brush;
		flags = index | PEN;
	}

	public GDIPlusObject(int index, Shape shape, boolean windingFill) {
		this();
		try {
			EMFPlusPathConstructor p = new EMFPlusPathConstructor();
			p.reset();
			p.addPath(shape);
			path = p.getPath();
			flags = index | PATH;
			pathFillMode = windingFill ? FILL_MODE_WINDING
					: FILL_MODE_ALTERNATE;
		} catch (IOException e) {
			// ignored
		}
	}

	public GDIPlusObject(int index, RenderedImage image) {
		this();
		this.image = image;
		flags = index | IMAGE;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		GDIPlusObject tag = new GDIPlusObject();
		tag.flags = flags;
		int type = flags & 0x0000FF00;
		// FIXME some missing
		switch (type) {
		case BRUSH:
			tag.brush = readBrush(emf);
			break;
		case PEN:
			emf.readUINT(); // magic word
			emf.readUINT(); // unknown
			emf.readUINT(); // additional flags, NOTE no join, endcap,
							// miterlimit etc.
			emf.readUINT(); // unknown
			float lineWidth = emf.readFLOAT();
			tag.stroke = new BasicStroke(lineWidth);
			tag.brush = readBrush(emf);
			break;
		case PATH:
			emf.readUINT(); // magic word
			tag.path = new PathPoint[emf.readUINT()];
			int moreFlags = emf.readUINT();
			pathFillMode = moreFlags & 0x2000;
			for (int i = 0; i < tag.path.length; i++) {
				tag.path[i] = new PathPoint();
				tag.path[i].setX(emf.readFLOAT());
				tag.path[i].setY(emf.readFLOAT());
			}
			for (int i = 0; i < tag.path.length; i++) {
				tag.path[i].setType(emf.readUnsignedByte());
			}
			if (tag.path.length % 4 > 0) {
				for (int i = 4 - (tag.path.length % 4); i > 0; i--) {
					emf.readBYTE();
				}
			}
			break;
		case INVALID:
		default:
			System.err.println(
					"GDIObject: Invalid TYPE: " + Integer.toHexString(type));
			break;
		}
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		int type = flags & 0x0000FF00;
		switch (type) {
		case BRUSH:
			writeBrush(emf, brush);
			break;
		case PEN:
			emf.writeUINT(0xDBC01001);
			emf.writeUINT(0x0000); // unknown
			emf.writeUINT(0x0000); // additional flags, NOTE no join, endcap,
									// miterlimit etc.
			emf.writeUINT(0x0000); // unknown
			emf.writeFLOAT(stroke.getLineWidth());
			writeBrush(emf, brush);
			break;
		case PATH:
			emf.writeUINT(0xDBC01001);
			emf.writeUINT(path.length);
			emf.writeUINT(pathFillMode);
			for (int i = 0; i < path.length; i++) {
				emf.writeFLOAT(path[i].getX());
				emf.writeFLOAT(path[i].getY());
			}
			for (int i = 0; i < path.length; i++) {
				emf.writeUnsignedByte(path[i].getType());
			}
			if (path.length % 4 > 0) {
				for (int i = 4 - (path.length % 4); i > 0; i--) {
					emf.writeBYTE(0);
				}
			}
			break;
		case IMAGE:
			writeImage(emf, image);
			break;
		case INVALID:
		default:
			break;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append("\n  ");
		int type = flags & 0x0000FF00;
		switch (type) {
		case BRUSH:
			sb.append("brush: " + brush);
			break;
		case PEN:
			sb.append("stroke: " + stroke);
			sb.append("\n  brush: " + brush);
			break;
		case PATH:
			sb.append("fillMode: " + pathFillMode);
			sb.append("\n  n: " + path.length);
			for (int i = 0; i < path.length; i++) {
				sb.append("\n  0x" + Integer.toHexString(path[i].getType())
						+ " (" + path[i].getX() + ", " + path[i].getY() + ")");
			}
			break;
		default:
			sb.append("UNKNOWN");
			break;
		}
		return sb.toString();
	}

	private static Paint readBrush(EMFInputStream emf) throws IOException {
		emf.readUINT(); // magic word
		int brushType = emf.readUINT();
		switch (brushType) {
		case BRUSH_TYPE_SOLID_COLOR:
			return emf.readCOLOR();
		case BRUSH_TYPE_LINEAR_GRADIENT:
			emf.readUINT(); // special mode ignored
			// FIXME, rest to be done
			return null;
		/*
		 * emf.writeUINT(paint.isCyclic() ? WRAP_MODE_TYLE_FLIP_XY :
		 * WRAP_MODE_CLAMP); // NOTE: check float x1 =
		 * (float)paint.getPoint1().getX(); float y1 =
		 * (float)paint.getPoint1().getY(); float x2 =
		 * (float)paint.getPoint2().getX(); float y2 =
		 * (float)paint.getPoint2().getY(); emf.writeFLOAT(Math.min(x1, x2));
		 * emf.writeFLOAT(Math.min(y1, y2)); emf.writeFLOAT(Math.abs(x1-x2));
		 * emf.writeFLOAT(Math.abs(y1-y2)); emf.writeCOLOR(paint.getColor1());
		 * emf.writeCOLOR(paint.getColor2()); emf.writeCOLOR(paint.getColor1());
		 * emf.writeCOLOR(paint.getColor2()); } else if (brush instanceof
		 * TexturePaint) { // emf.writeUINT(BRUSH_TYPE_TEXTURE_GRADIENT);
		 * //FIXME later when image is done
		 * emf.writeUINT(BRUSH_TYPE_SOLID_COLOR); emf.writeCOLOR(Color.BLACK); }
		 * else { System.err.println("No Brush for paint of class: "
		 * +brush.getClass()+" defaulting to black");
		 * emf.writeUINT(BRUSH_TYPE_SOLID_COLOR); emf.writeCOLOR(Color.BLACK); }
		 */
		}
		return Color.BLACK;
	}

	private static void writeBrush(EMFOutputStream emf, Paint brush)
			throws IOException {
		emf.writeUINT(0xDBC01001);
		if (brush instanceof Color) {
			emf.writeUINT(BRUSH_TYPE_SOLID_COLOR);
			emf.writeCOLOR((Color) brush);
		} else if (brush instanceof GradientPaint) {
			GradientPaint paint = (GradientPaint) brush;
			emf.writeUINT(BRUSH_TYPE_LINEAR_GRADIENT);
			emf.writeUINT(0x02); // write Matrix
			emf.writeUINT(paint.isCyclic() ? WRAP_MODE_TYLE_FLIP_XY
					: WRAP_MODE_TYLE_FLIP_Y); // NOTE: check
			float x1 = (float) paint.getPoint1().getX();
			float y1 = (float) paint.getPoint1().getY();
			float x2 = (float) paint.getPoint2().getX();
			float y2 = (float) paint.getPoint2().getY();
			emf.writeFLOAT(Math.min(x1, x2));
			emf.writeFLOAT(Math.min(y1, y2));
			emf.writeFLOAT(Math.abs(x1 - x2));
			emf.writeFLOAT(Math.abs(y1 - y2));
			emf.writeCOLOR(paint.getColor1());
			emf.writeCOLOR(paint.getColor2());
			emf.writeCOLOR(paint.getColor1());
			emf.writeCOLOR(paint.getColor2());
			float dx = x2 - x1;
			float dy = y2 - y1;
			float scale = (float) paint.getPoint1().distance(paint.getPoint2())
					/ dx;
			System.err.println(paint.getPoint1() + " " + paint.getPoint2());
			System.err.println(x1 + " " + x2 + " " + y1 + " " + y2 + ":" + dx
					+ " " + dy + " " + scale);
			float angle = (float) Math.atan2(dy, dx);
			AffineTransform transform = new AffineTransform(scale, 0, 0, scale,
					dx / 2 + x1, dy / 2 + y1);
			transform = new AffineTransform();
			transform.scale(scale, scale);
			transform.rotate(angle);
			writeTransform(emf, transform);
		} else if (brush instanceof TexturePaint) {
			TexturePaint paint = (TexturePaint) brush;
			emf.writeUINT(BRUSH_TYPE_TEXTURE_GRADIENT);
			emf.writeUINT(0); // no special mode
			emf.writeUINT(WRAP_MODE_TYLE);
			writeImage(emf, paint.getImage());
		} else {
			System.err.println("No Brush for paint of class: "
					+ brush.getClass() + " defaulting to black");
			emf.writeUINT(BRUSH_TYPE_SOLID_COLOR);
			emf.writeCOLOR(Color.BLACK);
		}
	}

	public static void writeTransform(EMFOutputStream emf,
			AffineTransform transform) throws IOException {
		emf.writeFLOAT((float) transform.getScaleX());
		emf.writeFLOAT((float) transform.getShearY());
		emf.writeFLOAT((float) transform.getShearX());
		emf.writeFLOAT((float) transform.getScaleY());
		emf.writeFLOAT((float) transform.getTranslateX());
		emf.writeFLOAT((float) transform.getTranslateY());
	}

	private static void writeImage(EMFOutputStream emf, RenderedImage image)
			throws IOException {
		emf.writeUINT(0xDBC01001);
		emf.writeUINT(IMAGE_TYPE_BITMAP);
		emf.writeUINT(0); // width
		emf.writeUINT(0); // height
		emf.writeUINT(0); // stride
		emf.writeUINT(0); // pixelformat
		emf.writeUINT(0x000001); // 01 00 00 00 for non-native images
		ImageGraphics2D.writeImage(image, "png", new Properties(), emf);
	}
}

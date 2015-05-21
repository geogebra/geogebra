package org.freehep.graphicsio.emf;

/**
 * EMFDisplay.java
 *
 * Created: Mon May 26 09:43:10 2003
 *
 * Copyright:    Copyright (c) 2000, 2001<p>
 * Company:      ATLANTEC Enterprise Solutions GmbH<p>
 *
 * @author Carsten Zerbst carsten.zerbst@atlantec-es.com
 * @version $Id: EMFDisplay.java,v 1.3 2009-08-17 21:44:45 murkle Exp $
 */
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.freehep.graphicsio.emf.gdi.Arc;
import org.freehep.graphicsio.emf.gdi.ArcTo;
import org.freehep.graphicsio.emf.gdi.BeginPath;
import org.freehep.graphicsio.emf.gdi.Chord;
import org.freehep.graphicsio.emf.gdi.CloseFigure;
import org.freehep.graphicsio.emf.gdi.CreatePen;
import org.freehep.graphicsio.emf.gdi.Ellipse;
import org.freehep.graphicsio.emf.gdi.LineTo;
import org.freehep.graphicsio.emf.gdi.LogPen;
import org.freehep.graphicsio.emf.gdi.MoveToEx;
import org.freehep.graphicsio.emf.gdi.Pie;
import org.freehep.util.io.Tag;
import org.geogebra.common.main.App;

/**
 * A simple interpreter displaying an EMF file read in by the EMFInputStream in
 * a JPanel
 */
public class EMFDisplay extends JPanel {

    private static final long serialVersionUID = 1L;

    private EMFInputStream is;

    private Point currentPosition;

//    private Shape currentShape;

    private AffineTransform at;

    public EMFDisplay() {
        super();
        this.setBackground(Color.white);
    }

    public EMFDisplay(EMFInputStream is) {
        super();
        this.is = is;

        // set the size
        try {
            EMFHeader header = is.readHeader();
            this.setSize(header.getBounds().width, header.getBounds().height);
        } catch (IOException ioexp) {
        }

        this.setBackground(Color.white);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (at == null) {
            at = new AffineTransform();
            at.setToIdentity();
        }

        Graphics2D g2 = (Graphics2D) g;
        try {
            // set the size
            EMFHeader header = is.readHeader();

            AffineTransform toCenterAt = new AffineTransform();
            toCenterAt.concatenate(at);

            Rectangle bounds = header.getBounds();
			// System.out.println("bounds " + bounds);
            toCenterAt.translate(50, 50);
            // header.getBounds( ).width / 2, header.getBounds( ).height / 2 );
            g2.transform(at);

            g2.drawLine(-100, 0, 100, 0);
            g2.drawLine(0, -100, 0, 100);
            at = toCenterAt;
            g2.draw(bounds);
			// System.out.println("device " + header.getDevice());

            Tag tag = is.readTag();
            while (tag != null) {
				// System.out.println(tag);
                map(tag, g2);
                tag = is.readTag();
            }
        } catch (IOException ioexp) {
            ioexp.printStackTrace();
        }
    }

    /**
     * the mapping function EMF tags -> java2d methods
     */
    private void map(Tag tag, Graphics2D g2) {
        if (tag instanceof Arc) {
            // The Arc function draws an elliptical arc.
            //
            // BOOL Arc(
            // HDC hdc, // handle to device context
            // int nLeftRect, // x-coord of rectangle's upper-left corner
            // int nTopRect, // y-coord of rectangle's upper-left corner
            // int nRightRect, // x-coord of rectangle's lower-right corner
            // int nBottomRect, // y-coord of rectangle's lower-right corner
            // int nXStartArc, // x-coord of first radial ending point
            // int nYStartArc, // y-coord of first radial ending point
            // int nXEndArc, // x-coord of second radial ending point
            // int nYEndArc // y-coord of second radial ending point
            // );
            // The points (nLeftRect, nTopRect) and (nRightRect, nBottomRect)
            // specify the bounding rectangle.
            // An ellipse formed by the specified bounding rectangle defines the
            // curve of the arc.
            // The arc extends in the current drawing direction from the point
            // where it intersects the
            // radial from the center of the bounding rectangle to the
            // (nXStartArc, nYStartArc) point.
            // The arc ends where it intersects the radial from the center of
            // the bounding rectangle to
            // the (nXEndArc, nYEndArc) point. If the starting point and ending
            // point are the same,
            // a complete ellipse is drawn.
            Arc arc = (Arc) tag;

            // normalize start and end point to a circle
            double nx0 = arc.getStart().x / arc.getBounds().width;

            // double ny0 = arc.getStart().y / arc.getBounds().height;
            double nx1 = arc.getEnd().x / arc.getBounds().width;

            // double ny1 = arc.getEnd().y / arc.getBounds().height;
            // calculate angle of start point
            double alpha0 = Math.acos(nx0);
            double alpha1 = Math.acos(nx1);

            Arc2D arc2d = new Arc2D.Double(arc.getStart().x, arc.getStart().y,
                    arc.getBounds().width, arc.getBounds().height, alpha0,
                    alpha1 - alpha0, Arc2D.OPEN);
//            currentShape = arc2d;
            g2.draw(arc2d);
        } else if (tag instanceof ArcTo) {
            // The ArcTo function draws an elliptical arc.
            //
            // BOOL ArcTo(
            // HDC hdc, // handle to device context
            // int nLeftRect, // x-coord of rectangle's upper-left corner
            // int nTopRect, // y-coord of rectangle's upper-left corner
            // int nRightRect, // x-coord of rectangle's lower-right corner
            // int nBottomRect, // y-coord of rectangle's lower-right corner
            // int nXRadial1, // x-coord of first radial ending point
            // int nYRadial1, // y-coord of first radial ending point
            // int nXRadial2, // x-coord of second radial ending point
            // int nYRadial2 // y-coord of second radial ending point
            // );
            // ArcTo is similar to the Arc function, except that the current
            // position is updated.
            //
            // The points (nLeftRect, nTopRect) and (nRightRect, nBottomRect)
            // specify the bounding rectangle.
            // An ellipse formed by the specified bounding rectangle defines the
            // curve of the arc. The arc extends
            // counterclockwise from the point where it intersects the radial
            // line from the center of the bounding
            // rectangle to the (nXRadial1, nYRadial1) point. The arc ends where
            // it intersects the radial line from
            // the center of the bounding rectangle to the (nXRadial2,
            // nYRadial2) point. If the starting point and
            // ending point are the same, a complete ellipse is drawn.
            //
            // A line is drawn from the current position to the starting point
            // of the arc.
            // If no error occurs, the current position is set to the ending
            // point of the arc.
            //
            // The arc is drawn using the current pen; it is not filled.
            ArcTo arc = (ArcTo) tag;

            // normalize start and end point to a circle
            double nx0 = arc.getStart().x / arc.getBounds().width;

            // double ny0 = arc.getStart().y / arc.getBounds().height;
            double nx1 = arc.getEnd().x / arc.getBounds().width;

            // double ny1 = arc.getEnd().y / arc.getBounds().height;
            // calculate angle of start point
            double alpha0 = Math.acos(nx0);
            double alpha1 = Math.acos(nx1);

            // update currentPosition
            currentPosition = arc.getEnd();

            Arc2D arc2d = new Arc2D.Double(arc.getStart().x, arc.getStart().y,
                    arc.getBounds().width, arc.getBounds().height, alpha0,
                    alpha1 - alpha0, Arc2D.OPEN);
//            currentShape = arc2d;
            g2.draw(arc2d);
        } else if (tag instanceof BeginPath) {
            // The BeginPath function opens a path bracket in the specified
            // device context.
//            currentShape = null;
        } else if (tag instanceof Chord) {
            // The Chord function draws a chord (a region bounded by the
            // intersection of an
            // ellipse and a line segment, called a secant). The chord is
            // outlined by using the
            // current pen and filled by using the current brush.
            Chord arc = (Chord) tag;

            // normalize start and end point to a circle
            double nx0 = arc.getStart().x / arc.getBounds().width;

            // double ny0 = arc.getStart().y / arc.getBounds().height;
            double nx1 = arc.getEnd().x / arc.getBounds().width;

            // double ny1 = arc.getEnd().y / arc.getBounds().height;
            // calculate angle of start point
            double alpha0 = Math.acos(nx0);
            double alpha1 = Math.acos(nx1);

            // update currentPosition
            currentPosition = arc.getEnd();

            Arc2D arc2d = new Arc2D.Double(arc.getStart().x, arc.getStart().y,
                    arc.getBounds().width, arc.getBounds().height, alpha0,
                    alpha1 - alpha0, Arc2D.CHORD);
//            currentShape = arc2d;
            g2.draw(arc2d);
        } else if (tag instanceof CloseFigure) {
            // The CloseFigure function closes an open figure in a path.
        } else if (tag instanceof CreatePen) {
            // CreatePen
            //
            // The CreatePen function creates a logical pen that has the
            // specified style, width, and color.
            // The pen can subsequently be selected into a device context and
            // used to draw lines and curves.
            //
            // HPEN CreatePen(
            // int fnPenStyle, // pen style
            // int nWidth, // pen width
            // COLORREF crColor // pen color
            // );
            CreatePen cpen = (CreatePen) tag;
            LogPen lpen = cpen.getPen();

            float[] dash = null;
            if (lpen.getPenStyle() == EMFConstants.PS_DASH) {
                dash = new float[] { 5, 5 };
            } else if (lpen.getPenStyle() == EMFConstants.PS_DASHDOT) {
                dash = new float[] { 5, 2, 1, 2 };
            } else if (lpen.getPenStyle() == EMFConstants.PS_DASHDOTDOT) {
                dash = new float[] { 5, 2, 1, 2, 1, 2 };
            } else if (lpen.getPenStyle() == EMFConstants.PS_DOT) {
                dash = new float[] { 1, 2 };
            } else if (lpen.getPenStyle() == EMFConstants.PS_SOLID) {
                dash = new float[] { 1 };
            } else {
				App.debug("got unsupported pen style "
                        + lpen.getPenStyle());
            }

            BasicStroke bs = new BasicStroke(lpen.getWidth(),
                    BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1, dash, 0);
            g2.setStroke(bs);
        } else if (tag instanceof Ellipse) {
            // The Ellipse function draws an ellipse. The center of the ellipse
            // is the center of the specified bounding rectangle.
            // The ellipse is outlined by using the current pen and is filled by
            // using the current brush.
            // The current position is neither used nor updated by Ellipse.
            Ellipse el = (Ellipse) tag;

            Ellipse2D el2 = new Ellipse2D.Double(el.getBounds().getX(), el
                    .getBounds().getY(), el.getBounds().getWidth(), el
                    .getBounds().getHeight());
//            currentShape = el2;
            g2.draw(el2);
        } else if (tag instanceof LineTo) {
            // The LineTo function draws a line from the current position up to,
            // but not including, the specified point.
            // The line is drawn by using the current pen and, if the pen is a
            // geometric pen, the current brush.
            LineTo lineTo = (LineTo) tag;
            Line2D l2 = new Line2D.Double(currentPosition, lineTo.getPoint());
            g2.draw(l2);
//            currentShape = l2;
        } else if (tag instanceof MoveToEx) {
            // The MoveToEx function updates the current position to the
            // specified point
            // and optionally returns the previous position.
            MoveToEx mte = (MoveToEx) tag;
            currentPosition = mte.getPoint();
        } else if (tag instanceof Pie) {
            Pie arc = (Pie) tag;

            // normalize start and end point to a circle
            double nx0 = arc.getStart().x / arc.getBounds().width;

            // double ny0 = arc.getStart().y / arc.getBounds().height;
            double nx1 = arc.getEnd().x / arc.getBounds().width;

            // double ny1 = arc.getEnd().y / arc.getBounds().height;
            // calculate angle of start point
            double alpha0 = Math.acos(nx0);
            double alpha1 = Math.acos(nx1);

            Arc2D arc2d = new Arc2D.Double(arc.getStart().x, arc.getStart().y,
                    arc.getBounds().width, arc.getBounds().height, alpha0,
                    alpha1 - alpha0, Arc2D.PIE);
//            currentShape = arc2d;
            g2.draw(arc2d);
        } else {
			App.debug("tag " + tag + " not supported");
        }
    }

    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            EMFInputStream emf = new EMFInputStream(fis);

            JFrame frame = new JFrame("EMF " + args[0]);
            JScrollPane sp = new JScrollPane(new EMFDisplay(emf));
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(sp, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(550, 400);
            frame.setVisible(true);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}

// EMFDisplay

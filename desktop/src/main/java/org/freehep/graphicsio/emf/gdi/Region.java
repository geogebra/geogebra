package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * 
 * @author tonyj
 */
public class Region {
    private Rectangle bounds;

    private Rectangle region;

    public Region(Rectangle bounds, Rectangle region) {
        this.bounds = bounds;
        this.region = region;
    }

    public Region(EMFInputStream emf) throws IOException {
        /* int length = */ emf.readDWORD();
        /* int mode = */ emf.readDWORD();
        /* int nRect = */ emf.readDWORD();
        int size = emf.readDWORD();
        bounds = emf.readRECTL();
        region = emf.readRECTL();
        for (int i = 16; i < size; i += 16)
            emf.readRECTL();
    }

    public void write(EMFOutputStream emf) throws IOException {
        emf.writeDWORD(32);
        emf.writeDWORD(1); // RDH_RECTANGLES
        emf.writeDWORD(1);
        emf.writeDWORD(16);
        emf.writeRECTL(bounds);
        emf.writeRECTL(region);
    }

    public int length() {
        return 48;
    }

    public String toString() {
        return "  Region\n" + "    bounds: " + bounds + "\n" + "    region: "
                + region;
    }
}

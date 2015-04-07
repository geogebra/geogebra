package org.geogebra.desktop.export.pstricks;

import java.io.IOException;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.main.App;

public class GeoGebraToPgfD extends GeoGebraToPgf {

	public GeoGebraToPgfD(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality, EuclidianView euclidianView2) {
		try {
			return new MyGraphicsPgf(ef, inequality, euclidianView2);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	class MyGraphicsPgf extends MyGraphics {

		public MyGraphicsPgf(FunctionalNVar geo, Inequality ineq,
				EuclidianView euclidianView) throws IOException {

			super(geo, ineq, euclidianView);
		}

		@Override
		public void fill(GShape s) {
			((GeoElement) geo).setLineType(ineq.getBorder().lineType);
			switch (ineq.getType()) {
			case INEQUALITY_CONIC:
				GeoConicND conic = ineq.getConicBorder();
				if (conic.getType() == GeoConicNDConstants.CONIC_ELLIPSE
						|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
					((GeoElement) conic).setObjColor(((GeoElement) geo)
							.getObjectColor());
					conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
					((GeoElement) conic).setAlphaValue(((GeoElement) geo)
							.getAlphaValue());
					((GeoElement) conic)
							.setHatchingAngle((int) ((GeoElement) geo)
									.getHatchingAngle());
					((GeoElement) conic).setHatchingDistance(((GeoElement) geo)
							.getHatchingDistance());
					((GeoElement) conic).setFillType(((GeoElement) geo)
							.getFillType());
					drawGeoConic((GeoConic) conic);
					break;
				}
			case INEQUALITY_PARAMETRIC_Y:
			case INEQUALITY_PARAMETRIC_X:
			case INEQUALITY_1VAR_X:
			case INEQUALITY_1VAR_Y:
			case INEQUALITY_LINEAR:
				double[] coords = new double[2];
				double zeroY = ds[5] * ds[3];
				double zeroX = ds[4] * (-ds[0]);
				GPathIterator path = s.getPathIterator(null);
				GColor c = ((GeoElement) geo).getObjectColor();
				code.append("\\draw[");
				;
				code.append(LineOptionCode((GeoElement) geo, true));
				code.append("]");
				double precX = Integer.MAX_VALUE;
				double precY = Integer.MAX_VALUE;
				while (!path.isDone()) {
					path.currentSegment(coords);
					if (coords[0] == precX && coords[1] == precY) {
						code.delete(code.length() - 2, code.length());
						code.append(";\n\\draw[");
						code.append(LineOptionCode((GeoElement) geo, true));
						code.append("]");
					} else {
						double x1 = (coords[0] - zeroX) / ds[4];
						double y1 = -(coords[1] - zeroY) / ds[5];
						if (y1 > ymax)
							y1 = ymax;
						if (y1 < ymin)
							y1 = ymin;
						code.append("(");
						code.append(format(x1));
						code.append(",");
						code.append(format(y1));
						code.append(")--");
					}
					precX = coords[0];
					precY = coords[1];
					path.next();
				}
				int i = code.lastIndexOf(")");
				code.delete(i + 1, code.length());
				code.append(";\n");
				break;
			}

		}
	}
}

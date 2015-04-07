package org.geogebra.desktop.export.pstricks;

import java.io.IOException;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.main.App;

public class GeoGebraToAsymptoteD extends GeoGebraToAsymptote {

	public GeoGebraToAsymptoteD(App app) {
		super(app);
	}

	@Override
	protected GGraphics2D createGraphics(FunctionalNVar ef,
			Inequality inequality, EuclidianView euclidianView2) {

		try {
			return new MyGraphicsAs(ef, inequality, euclidianView2);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	class MyGraphicsAs extends MyGraphics {

		public MyGraphicsAs(FunctionalNVar geo, Inequality ineq,
				EuclidianView euclidianView) throws IOException {
			super(geo, ineq, euclidianView);
		}

		@Override
		public void fill(GShape s) {
			importpackage.add("patterns");
			GColor c = ((GeoElement) geo).getObjectColor();
			int lineType = ((GeoElement) geo).getLineType();
			((GeoElement) geo).setLineType(ineq.getBorder().lineType);
			code.append("\npen border=" + penStyle((GeoElement) geo));
			ColorCode(c, code);
			((GeoElement) geo).setLineType(lineType);
			code.append(";\npen fillstyle=" + penStyle((GeoElement) geo));
			ColorCode(c, code);
			if (((GeoElement) geo).getFillType() != FillType.STANDARD) {
				code.append(";\nadd(\"hatch\",hatch(2mm,NW,fillstyle));\n");
			} else {
				code.append(";\nadd(\"hatch\",hatch(0.5mm,NW,fillstyle));\n");
			}
			switch (ineq.getType()) {
			case INEQUALITY_CONIC:
				GeoConicND conic = ineq.getConicBorder();
				if (conic.getType() == GeoConicNDConstants.CONIC_ELLIPSE
						|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
					conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
					((GeoElement) conic).setObjColor(((GeoElement) geo)
							.getObjectColor());
					conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
					((GeoElement) conic).setAlphaValue(((GeoElement) geo)
							.getAlphaValue());
					conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
					((GeoElement) conic)
							.setHatchingAngle((int) ((GeoElement) geo)
									.getHatchingAngle());
					((GeoElement) conic).setHatchingDistance(((GeoElement) geo)
							.getHatchingDistance());
					((GeoElement) conic).setFillType(((GeoElement) geo)
							.getFillType());
					fillInequality = true;
					drawGeoConic((GeoConic) conic);
					fillInequality = false;
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
				code.append("filldraw(");
				double precX = Integer.MAX_VALUE;
				double precY = Integer.MAX_VALUE;
				while (!path.isDone()) {
					path.currentSegment(coords);

					if (coords[0] == precX && coords[1] == precY) {
						code.append("cycle,pattern(\"hatch\"),border);\n");
						code.append("filldraw(");

					} else {
						code.append("(");
						code.append(format((coords[0] - zeroX) / ds[4]));
						code.append(",");
						code.append(format(-(coords[1] - zeroY) / ds[5]));
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

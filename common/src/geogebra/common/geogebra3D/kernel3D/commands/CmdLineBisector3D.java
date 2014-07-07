package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdLineBisector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;

public class CmdLineBisector3D extends CmdLineBisector {
	
	
	
	
	public CmdLineBisector3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok) throws MyError{

		// line through point orthogonal to vector
		if ((ok[0] = (arg[0].isGeoSegment()))
				&& (ok[1] = (arg[1] instanceof GeoDirectionND))) {
			GeoElement[] ret = { kernelA.getManager3D().LineBisector3D(c.getLabel(), (GeoSegmentND) arg[0], (GeoDirectionND) arg[1]) };
			return ret;
		}
		
		return super.process2(c, arg, ok);
	}
	
	
	@Override
	protected GeoElement lineBisector(String label, GeoSegmentND segment){
		
		if (segment.isGeoElement3D()){
			GeoDirectionND orientation = CommandProcessor3D.getCurrentViewOrientation(kernelA, app);
    		if (orientation == null){
    			orientation = kernelA.getSpace(); // will create undefined line bisector: use xOy plane as default?
    		}
			return kernelA.getManager3D().LineBisector3D(label, segment, orientation);
		}
		
		return super.lineBisector(label, segment);
	}


//
//	@Override
//	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok) throws MyError{
//
//		// arc center-two points, oriented
//		if ((ok[0] = (arg[0].isGeoSegment()))
//				(ok[3] = (arg[3] instanceof GeoDirectionND))) {
//			
//			
//			GeoElement[] ret = { (GeoElement) kernelA.getManager3D().
//					CircleArcSector3D(c.getLabel(), 
//							(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoPointND) arg[2], 
//							(GeoDirectionND) arg[3], type) };
//			return ret;
//		}
//		
//		return null;
//	}
	
}

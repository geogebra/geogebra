package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.Operation;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.RenameInputHandler;
import geogebra.kernel.Kernel;
import geogebra.kernel.algos.AlgoDependentList;
import geogebra.kernel.algos.AlgoDependentNumber;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoScriptAction;
import geogebra.kernel.geos.GeoText;
import geogebra.kernel.geos.GeoVec3D;
import geogebra.kernel.geos.GeoVector;
import geogebra.kernel.statistics.SetRandomValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;
import geogebra.main.MyError;
import geogebra.sound.SoundManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class CmdScripting extends CommandProcessor{

	

	public CmdScripting(Kernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}
	public abstract void perform(Command c);
		
	
	@Override
	public final GeoElement[] process (Command c) throws MyError,
			CircularDefinitionException {
		GeoScriptAction sa =  new GeoScriptAction(cons,this,c);	
		return new GeoElement[] {sa};
	}

}

/**
 * Delete[ <GeoElement> ]
 */
class CmdDelete extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDelete(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoElement())) {
				GeoElement geo = (GeoElement) arg[0];
				
				// delete object
				geo.removeOrSetUndefinedIfHasFixedDescendent();
				return;
			} else
				throw argErr(app, "Delete", arg[0]);

		default:
			throw argNumErr(app, "Delete", n);
		}
	}
}

/**
 * Relation[ <GeoElement>, <GeoElement> ]
 */
class CmdRelation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRelation(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// show relation string in a message dialog
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoElement()))) {
				app.showRelation((GeoElement) arg[0], (GeoElement) arg[1]);
				return;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Relation", arg[0]);
				else
					throw argErr(app, "Relation", arg[1]);
			}

		default:
			throw argNumErr(app, "Relation", n);
		}
	}
}
/**
 *SetCaption
 */
class CmdSetCaption extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCaption(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setCaption(((GeoText) arg[1]).getTextString());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *StartAnimation
 */
class CmdStartAnimation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStartAnimation(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy
		

		switch (n) {
		case 0:

			app.getKernel().getAnimatonManager().startAnimation();
			return;

		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoNumeric() && ((GeoNumeric) arg[0]).isIndependent()) ||
					arg[0].isPointOnPath()) {				
				arg[0].setAnimating(true);
				app.getKernel().getAnimatonManager().startAnimation();
				return;
			}			
			else if (arg[0].isGeoBoolean()) {

				GeoBoolean geo = (GeoBoolean) arg[0];

				if (geo.getBoolean()) {
					app.getKernel().getAnimatonManager().startAnimation();

				} else {
					app.getKernel().getAnimatonManager().stopAnimation();
				}
				return;
			} else
				throw argErr(app, c.getName(), arg[0]);
		default:
			arg = resArgs(c);
			boolean start = true;
			int sliderCount = n;
			if (arg[n-1].isGeoBoolean()){
				start = ((GeoBoolean) arg[n-1]).getBoolean();
				sliderCount = n-1;
			}
			for(int i = 0; i < sliderCount; i++)
				if(!arg[i].isGeoNumeric() && !arg[i].isPointOnPath())
					throw argErr(app,c.getName(),arg[i]);
			
			for(int i = 0; i < sliderCount; i++){
				if(arg[i].isGeoNumeric())
					((GeoNumeric) arg[0]).setAnimating(start);
				else
					((GeoPoint) arg[0]).setAnimating(start);
				if(start)
					app.getKernel().getAnimatonManager().startAnimation();
			} 
			
			return;		
		}
	}
}
/**
 *SelectObjects
 */
class CmdSelectObjects extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectObjects(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		

		app.clearSelectedGeos();

		if (n > 0) {
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if ((arg[i].isGeoElement())) {
					GeoElement geo = (GeoElement) arg[i];
					app.addSelectedGeo(geo, true);
				}
			}

		}
		return;

	}
}

/**
 *PlaySound
 */
class CmdPlaySound extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPlaySound(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		boolean[] ok = new boolean[n];
		SoundManager sm = app.getSoundManager();

		switch (n) {
		case 1:
			arg = resArgs(c);

			// play a midi file
			if (ok[0] = arg[0].isGeoText()) {
				sm.playMidiFile(((String) ((GeoText) arg[0]).toValueString()));
				return;
			}
			// pause/resume current sound
			else if (ok[0] = arg[0].isGeoBoolean()) {
				sm.pauseResumeSound(((boolean) ((GeoBoolean) arg[0]).getBoolean()));
				return;
			}
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			arg = resArgs(c);

			if ( (ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {

				// play a note using args: note and duration
				// using instrument 0 (piano) and velocity 127 (100% of external volume control) 
				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(),
						((GeoNumeric) arg[1]).getDouble(), 0, 127);

				return;
			}

			else if ((ok[0] = arg[0].isGeoText()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {
				// play a sequence string
				sm.playSequenceFromString(((String) ((GeoText) arg[0]).toValueString()),
						(int) ((GeoNumeric) arg[1]).getDouble());
				return;
			}

			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			

		case 3:
			arg = resArgs(c);

			// play a note using args: note, duration, instrument
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(), // note
						((GeoNumeric) arg[1]).getDouble(), // duration
						(int) ((GeoNumeric) arg[2]).getDouble(), // instrument
						127); // 100% of external volume control 

				return;
			}

			else if ((ok[0] = arg[0].isGeoFunction()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playFunction(((GeoFunction) arg[0]), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble()); // max value
				return;
			}
			
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			
			

		case 5:
			arg = resArgs(c);

			if ((ok[0] = arg[0].isGeoFunction() 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric()) 
					&& (ok[4] = arg[4].isGeoNumeric()))) {

				sm.playFunction(((GeoFunction) arg[0]), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble(), // max value
						(int)((GeoNumeric) arg[3]).getDouble(), // sample rate
						(int)((GeoNumeric) arg[4]).getDouble()); // bit depth
				
				return;
			} 
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);
			
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
/**
 *Rename
 */
class CmdRename extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRename(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = (GeoElement) arg[0];

				if (RenameInputHandler.checkName(geo, ((GeoText) arg[1]).getTextString())) {
					geo.rename(((GeoText) arg[1]).getTextString());
					geo.updateRepaint();

					
					return;
				} else {
					throw argErr(app, c.getName(), arg[1]);
				}
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *HideLayer
 */
class CmdHideLayer extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHideLayer(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(false);
						geo.updateRepaint();
					}
				}

				
				return;

			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ShowLayer
 */
class CmdShowLayer extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowLayer(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(true);
						geo.updateRepaint();
					}
				}

				
				return;

			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetCoords
 */
class CmdSetCoords extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCoords(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			//we don't want to change coords unless the point is free or Point[path/region]
			if ((ok[0] = (arg[0] instanceof GeoVec3D && arg[0].isMoveable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				double x = ((GeoNumeric) arg[1]).getDouble();
				double y = ((GeoNumeric) arg[2]).getDouble();

				GeoElement geo = (GeoElement) arg[0];

				if (geo.isGeoPoint()) {
					((GeoPoint) geo).setCoords(x, y, 1);
					geo.updateRepaint();
				} else if (geo.isGeoVector()) {
					((GeoVector) geo).setCoords(x, y, 0);
					geo.updateRepaint();
				} else
					throw argErr(app, c.getName(), arg[0]);

				
				return;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
/**
 *Pan
 */
class CmdPan extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPan(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoNumeric() && arg[1].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.setCoordSystemFromMouseMove((int) x.getDouble(), -(int) y
						.getDouble(), EuclidianController.MOVE_VIEW);

				
				return;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetActiveView
 */
class CmdSetActiveView extends CmdScripting {

	public CmdSetActiveView(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		
		

		if (!app.isUsingFullGui()) return;
			
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				int view = (int)numGeo.getDouble();
				
				// ignore all errors (eg when a view is not available etc)
				switch (view) {
				case 1:
					app.setActiveView(Application.VIEW_EUCLIDIAN);
					 break;
				case 2:
					app.setActiveView(Application.VIEW_EUCLIDIAN2);
					 break;
				case 3:
					app.setActiveView(Application.VIEW_EUCLIDIAN3D);
					 break;
				case -1:
					app.setActiveView(Application.VIEW_SPREADSHEET);
					 break;
				case -2:
					app.setActiveView(Application.VIEW_ALGEBRA);
					 break;
				case -3:
					app.setActiveView(Application.VIEW_CAS);
					 break;
				// default: // might be needed when support for more than 2 Euclidian Views added 
				}
				
				return;

			} else
				throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
/**
 *ZoomIn
 */
class CmdZoomIn extends CmdScripting {

	public CmdZoomIn(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.getWidth() / 2; // mouseLoc.x;
				double py = ev.getHeight() / 2; // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint p = (GeoPoint) arg[1];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 4:
			arg = resArgs(c);
			for(int i=0;i<3;i++)
					if(!arg[i].isNumberValue())
						throw argErr(app, c.getName(),arg[i]);
			EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
			ev.setXminObject((NumberValue)arg[0]);
			ev.setXmaxObject((NumberValue)arg[2]);
			ev.setYminObject((NumberValue)arg[1]);
			ev.setYmaxObject((NumberValue)arg[3]);
			ev.updateBounds();
			
			return;
						
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ZoomOut
 */
class CmdZoomOut extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZoomOut(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.getWidth() / 2.0; // mouseLoc.x;
				double py = ev.getHeight() / 2.0; // mouseLoc.y;

				double factor = numGeo.getDouble();

				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint p = (GeoPoint) arg[1];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

class CmdSetAxesRatio extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetAxesRatio(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok0;
		switch (n) {
	
		case 2:
			arg = resArgs(c);
			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				ev.zoomAxesRatio(numGeo.getDouble()/numGeo2.getDouble(), true);
				
				return;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 3:
			arg = resArgs(c);			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				GeoNumeric numGeo3 = (GeoNumeric) arg[2];
				EuclidianViewInterface ev = (EuclidianViewInterface)app.getActiveEuclidianView();
				//TODO: Fix this once 3D view supports zoom
				if(!ev.isDefault2D()){
					ev.zoom(numGeo.getDouble()/numGeo3.getDouble(),
							numGeo2.getDouble()/numGeo3.getDouble(),  1, 3, true);
				}
				
				return;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLayer
 */
class CmdSetLayer extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLayer(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLayer((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLabelMode
 */
class CmdSetLabelMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLabelMode(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLabelMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetTooltipMode
 */
class CmdSetTooltipMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetTooltipMode(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setTooltipMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetColor
 */
class CmdSetColor extends CmdScripting {

	boolean background = false;
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetColor(Kernel kernel) {
		super(kernel);
	}

	public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (!arg[1].isGeoText())
				throw argErr(app, c.getName(), arg[1]);

			try {

				String color = geogebra.util.Util.removeSpaces(
						((GeoText) arg[1]).getTextString());
				// lookup Color
				//HashMap<String, Color> colors = app.getColorsHashMap();
				//Color col = colors.get(color);
				
				Color col = GeoGebraColorConstants.getGeogebraColor(app,  color);

				// support for translated color names
				//if (col == null) {
				//	// translate to English
				//	color = app.reverseGetColor(color).toUpperCase();
				//	col = (Color) colors.get(color);
				//	// Application.debug(color);
				//}

				if (col == null) 
					throw argErr(app, c.getName(), arg[1]);
				
				
				if (background)
					arg[0].setBackgroundColor(col);
				else
					arg[0].setObjColor(col);
				
				arg[0].updateRepaint();				
				
				return;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c.getName(), arg[0]);
			}

		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {
				int red = (int) (((NumberValue) arg[1]).getDouble() * 255);
				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;
				int green = (int) (((NumberValue) arg[2]).getDouble() * 255);
				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;
				int blue = (int) (((NumberValue) arg[3]).getDouble() * 255);
				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;

				if (background)
					arg[0].setBackgroundColor(new Color(red, green, blue));
				else
					arg[0].setObjColor(new Color(red, green, blue));
				
				arg[0].updateRepaint();
				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

class CmdSetBackgroundColor extends CmdSetColor {
	
	public CmdSetBackgroundColor(Kernel kernel) {
		super(kernel);
		background = true;
	}

	final public void perform(Command c) throws MyError {
		super.perform(c);
	}
}

/**
 *UpdateConstruction
 */
class CmdUpdateConstruction extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUpdateConstruction(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			app.getKernel().updateConstruction();
			app.setUnsaved();
			
			return;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetValue
 */
class CmdSetValue extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetValue(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		boolean ok;
		

		switch (n) {
		case 2:
			if (arg[0].isIndependent() || arg[0].isMoveable()) {
				if (arg[0].isGeoNumeric() && arg[1].isNumberValue()) {
					NumberValue num = (NumberValue) arg[1];
					((GeoNumeric) arg[0]).setValue(num.getDouble());
				} else {
					arg[0].set(arg[1]);
				}
				arg[0].updateRepaint();
			} else if (arg[1].isNumberValue() && arg[0].isGeoNumeric() && arg[0].getParentAlgorithm() instanceof SetRandomValue) {
				// eg a = RandomBetween[0,10]
				SetRandomValue algo = (SetRandomValue) arg[0].getParentAlgorithm();
				algo.setRandomValue(((NumberValue)arg[1]).getDouble());
			} else if (arg[1].isNumberValue() && arg[0].getParentAlgorithm() instanceof AlgoDependentNumber) {
				// eg a = random()
				double val = ((NumberValue)arg[1]).getDouble();
				if (val >= 0 && val <= 1) {
					AlgoDependentNumber al = (AlgoDependentNumber)arg[0].getParentAlgorithm();
					ExpressionNode en = al.getExpression();
					if (en.getOperation().equals(Operation.RANDOM)) {
						GeoNumeric num = ((GeoNumeric)al.getOutput()[0]);
						num.setValue(val);
						num.updateRepaint();
					}
				}
			}
			return;
		case 3:
			if (ok = (arg[0].isGeoList() && arg[0].isIndependent()) && arg[1].isNumberValue()) {
				GeoList list = (GeoList) arg[0];
				int nn = (int) ((NumberValue) arg[1]).getDouble();

				if (nn < 1 || nn > list.size() + 1)
					throw argErr(app, c.getName(), arg[1]);
				if(nn > list.size()){
					list.add((GeoElement)arg[2].deepCopy(kernel));
					list.updateRepaint();
					return;
				}
				else{
				GeoElement geo = list.get(nn - 1);
				if (geo.isIndependent()) {
					if (geo.isGeoNumeric() && arg[2].isNumberValue()) {
						NumberValue num = (NumberValue) arg[2];
						((GeoNumeric) geo).setValue(num.getDouble());
					} else {
						geo.set(arg[2]);						
					}
				}
				else Application.debug(geo.getParentAlgorithm());

				geo.updateRepaint();

				// update the list too if necessary
				if (!geo.isLabelSet()) { // eg like first element of {1,2,a}
					Iterator<GeoElement> it = kernel.getConstruction()
							.getGeoSetConstructionOrder().iterator();
					while (it.hasNext()) {
						GeoElement geo2 = it.next();
						if (geo2.isGeoList()) {
							GeoList gl = (GeoList) geo2;
							for (int i = 0; i < gl.size(); i++) {
								if (gl.get(i) == geo)
									gl.updateRepaint();
							}
						}
					}
				}}

			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			return;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetDynamicColor
 */
class CmdSetDynamicColor extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetDynamicColor(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {

				GeoElement geo = (GeoElement) arg[0];

				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add((GeoElement) arg[1]); 
				listItems.add((GeoElement) arg[2]); 
				listItems.add((GeoElement) arg[3]); 
				//listItems.add((GeoElement) arg[4]); // no opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernel.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);
		case 5:
			ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())
					&& (ok[4] = arg[4].isNumberValue())) {

				GeoElement geo = (GeoElement) arg[0];
				
				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add((GeoElement) arg[1]); 
				listItems.add((GeoElement) arg[2]); 
				listItems.add((GeoElement) arg[3]); 
				listItems.add((GeoElement) arg[4]); // opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernel.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetConditionToShowObject
 */
class CmdSetConditionToShowObject extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetConditionToShowObject(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				try {
					geo.setShowObjectCondition((GeoBoolean) arg[1]);
				} catch (CircularDefinitionException e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[1]);
				}
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetFilling
 */
class CmdSetFilling extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetFilling(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setAlphaValue((float) ((NumberValue) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
/**
 *LineStyle
 */
class CmdLineStyle extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineStyle(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int style = (int) ((NumberValue) arg[1]).getDouble();
				Integer[] types = EuclidianView.getLineTypes();

				//For invalid number we assume it's 0
				//We do this also for SetPointStyle
				 
				if (style < 0 || style >= types.length)
					style = 0;
				
				arg[0].setLineType(types[style].intValue());
				arg[0].updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLineThickness
 */
class CmdSetLineThickness extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLineThickness(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1].isNumberValue()) {

				int thickness = (int) ((NumberValue) arg[1]).getDouble();

				arg[0].setLineThickness(thickness);
				arg[0].updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetPointStyle
 */
class CmdSetPointStyle extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointStyle(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (ok = arg[0].isGeoPoint() && arg[1].isNumberValue()) {

				GeoPoint point = (GeoPoint) arg[0];

				int style = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointStyle(style);
				point.updateRepaint();

				
				return;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetPointSize
 */
class CmdSetPointSize extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (ok = arg[0].isGeoPoint() && arg[1].isNumberValue()) {

				GeoPoint point = (GeoPoint) arg[0];

				int size = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointSize(size);
				point.updateRepaint();

				
				return;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetFixed
 */
class CmdSetFixed extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetFixed(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setFixed(((GeoBoolean) arg[1]).getBoolean());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ShowLabel
 */
class CmdShowLabel extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowLabel(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLabelVisible(((GeoBoolean) arg[1]).getBoolean());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetVisibleInView
 */
class CmdSetVisibleInView extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetVisibleInView(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if (!arg[1].isNumberValue())
				throw argErr(app, c.getName(), arg[1]);


			if (arg[2].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];
				

				int viewNo = (int)((NumberValue)arg[1]).getDouble();

				EuclidianView ev = null;

				switch (viewNo) {
				case 1:
					ev = app.getEuclidianView();
					break;
				case 2:
					if (!app.hasEuclidianView2()) break;
					ev = app.getEuclidianView2();
					break;
				default:
					// do nothing
				}

				if (ev != null) {
					boolean show = ((GeoBoolean)arg[2]).getBoolean();

					if (show) {
						geo.addView(ev.getViewID());
						ev.add(geo);
					} else {
						geo.removeView(ev.getViewID());
						ev.remove(geo);
					}
					
					geo.updateRepaint();
				}

				return;
			} else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}


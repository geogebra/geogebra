package geogebra.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.main.Application;
import geogebra.sound.SoundManager;

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
	public CmdPlaySound(AbstractKernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		boolean[] ok = new boolean[n];
		SoundManager sm = ((Application) app).getSoundManager();

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

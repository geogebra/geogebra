package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.main.MyError;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.RenameInputHandler;
import geogebra.kernel.Kernel;
import geogebra.kernel.algos.AlgoDependentList;
import geogebra.kernel.algos.AlgoDependentNumber;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoScriptAction;
import geogebra.kernel.geos.GeoText;
import geogebra.kernel.geos.GeoVector;
import geogebra.kernel.statistics.SetRandomValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;
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

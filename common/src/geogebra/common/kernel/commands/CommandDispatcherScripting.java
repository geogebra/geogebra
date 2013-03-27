package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.scripting.CmdButton;
import geogebra.common.kernel.scripting.CmdCenterView;
import geogebra.common.kernel.scripting.CmdCheckbox;
import geogebra.common.kernel.scripting.CmdDelete;
import geogebra.common.kernel.scripting.CmdHideLayer;
import geogebra.common.kernel.scripting.CmdLineStyle;
import geogebra.common.kernel.scripting.CmdPan;
import geogebra.common.kernel.scripting.CmdParseToFunction;
import geogebra.common.kernel.scripting.CmdParseToNumber;
import geogebra.common.kernel.scripting.CmdPlaySound;
import geogebra.common.kernel.scripting.CmdRelation;
import geogebra.common.kernel.scripting.CmdRename;
import geogebra.common.kernel.scripting.CmdRigidPolygon;
import geogebra.common.kernel.scripting.CmdRunClickScript;
import geogebra.common.kernel.scripting.CmdRunUpdateScript;
import geogebra.common.kernel.scripting.CmdSelectObjects;
import geogebra.common.kernel.scripting.CmdSetActiveView;
import geogebra.common.kernel.scripting.CmdSetAxesRatio;
import geogebra.common.kernel.scripting.CmdSetBackgroundColor;
import geogebra.common.kernel.scripting.CmdSetCaption;
import geogebra.common.kernel.scripting.CmdSetColor;
import geogebra.common.kernel.scripting.CmdSetConditionToShowObject;
import geogebra.common.kernel.scripting.CmdSetCoords;
import geogebra.common.kernel.scripting.CmdSetDynamicColor;
import geogebra.common.kernel.scripting.CmdSetFilling;
import geogebra.common.kernel.scripting.CmdSetFixed;
import geogebra.common.kernel.scripting.CmdSetLabelMode;
import geogebra.common.kernel.scripting.CmdSetLayer;
import geogebra.common.kernel.scripting.CmdSetLineThickness;
import geogebra.common.kernel.scripting.CmdSetPointSize;
import geogebra.common.kernel.scripting.CmdSetPointStyle;
import geogebra.common.kernel.scripting.CmdSetSeed;
import geogebra.common.kernel.scripting.CmdSetTooltipMode;
import geogebra.common.kernel.scripting.CmdSetTrace;
import geogebra.common.kernel.scripting.CmdSetValue;
import geogebra.common.kernel.scripting.CmdSetVisibleInView;
import geogebra.common.kernel.scripting.CmdShowAxes;
import geogebra.common.kernel.scripting.CmdShowGrid;
import geogebra.common.kernel.scripting.CmdShowLabel;
import geogebra.common.kernel.scripting.CmdShowLayer;
import geogebra.common.kernel.scripting.CmdSlider;
import geogebra.common.kernel.scripting.CmdSlowPlot;
import geogebra.common.kernel.scripting.CmdStartAnimation;
import geogebra.common.kernel.scripting.CmdTextfield;
import geogebra.common.kernel.scripting.CmdToolImage;
import geogebra.common.kernel.scripting.CmdTurtle;
import geogebra.common.kernel.scripting.CmdTurtleBack;
import geogebra.common.kernel.scripting.CmdTurtleForward;
import geogebra.common.kernel.scripting.CmdTurtleLeft;
import geogebra.common.kernel.scripting.CmdTurtleRight;
import geogebra.common.kernel.scripting.CmdUpdateConstruction;
import geogebra.common.kernel.scripting.CmdZoomIn;
import geogebra.common.kernel.scripting.CmdZoomOut;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet loading)
 *
 */
public class CommandDispatcherScripting implements CommandDispatcherInterface {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){
		// scripting
					case RigidPolygon:
						return new CmdRigidPolygon(kernel);
					case Relation:
						return new CmdRelation(kernel);
					case CopyFreeObject:
						return new CmdCopyFreeObject(kernel);
					case SetColor:
						return new CmdSetColor(kernel);
					case SetBackgroundColor:
						return new CmdSetBackgroundColor(kernel);
					case SetDynamicColor:
						return new CmdSetDynamicColor(kernel);
					case SetConditionToShowObject:
						return new CmdSetConditionToShowObject(kernel);
					case SetFilling:
						return new CmdSetFilling(kernel);
					case SetLineThickness:
						return new CmdSetLineThickness(kernel);
					case SetLineStyle:
						return new CmdLineStyle(kernel);
					case SetPointStyle:
						return new CmdSetPointStyle(kernel);
					case SetPointSize:
						return new CmdSetPointSize(kernel);
					case SetFixed:
						return new CmdSetFixed(kernel);
					case SetTrace:
						return new CmdSetTrace(kernel);
					case Rename:
						return new CmdRename(kernel);
					case HideLayer:
						return new CmdHideLayer(kernel);
					case ShowLayer:
						return new CmdShowLayer(kernel);
					case SetCoords:
						return new CmdSetCoords(kernel);
					case Pan:
						return new CmdPan(kernel);
					case CenterView:
						return new CmdCenterView(kernel);
					case ZoomIn:
						return new CmdZoomIn(kernel);
					case SetSeed:
						return new CmdSetSeed(kernel);
					case ZoomOut:
						return new CmdZoomOut(kernel);
					case SetActiveView:
						return new CmdSetActiveView(kernel);
					case SelectObjects:
						return new CmdSelectObjects(kernel);
					case SetLayer:
						return new CmdSetLayer(kernel);
					case SetCaption:
						return new CmdSetCaption(kernel);
					case SetLabelMode:
						return new CmdSetLabelMode(kernel);
					case SetTooltipMode:
						return new CmdSetTooltipMode(kernel);
					case UpdateConstruction:
						return new CmdUpdateConstruction(kernel);
					case SetValue:
						return new CmdSetValue(kernel);
					case PlaySound:
						return new CmdPlaySound(kernel);
					case ParseToNumber:
						return new CmdParseToNumber(kernel);
					case ParseToFunction:
						return new CmdParseToFunction(kernel);
					case StartAnimation:
						return new CmdStartAnimation(kernel);
					case Delete:
						return new CmdDelete(kernel);
					case Slider:
						return new CmdSlider(kernel);
					case Checkbox:
						return new CmdCheckbox(kernel);
					case Textfield:
						return new CmdTextfield(kernel);
					case Button:
						return new CmdButton(kernel);
					case Execute:
						return new CmdExecute(kernel);
					case GetTime:
						return new CmdGetTime(kernel);
					case ShowLabel:
						return new CmdShowLabel(kernel);
					case SetAxesRatio:
						return new CmdSetAxesRatio(kernel);
					case SetVisibleInView:
						return new CmdSetVisibleInView(kernel);
					case ShowAxes:
						return new CmdShowAxes(kernel);
					case ShowGrid:
						return new CmdShowGrid(kernel);
					case SlowPlot:
						return new CmdSlowPlot(kernel);
					case ToolImage:
						return new CmdToolImage(kernel);		
					case Turtle: 
						return new CmdTurtle(kernel);
					case TurtleForward:
						return new CmdTurtleForward(kernel);
					case TurtleBack:
						return new CmdTurtleBack(kernel);
					case TurtleLeft:
						return new CmdTurtleLeft(kernel);
					case TurtleRight:
						return new CmdTurtleRight(kernel);	
					case RunClickScript:
						return new CmdRunClickScript(kernel);
					case RunUpdateScript:
						return new CmdRunUpdateScript(kernel);
		}
		return null;
	}
}

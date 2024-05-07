package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.scripting.CmdButton;
import org.geogebra.common.kernel.scripting.CmdCenterView;
import org.geogebra.common.kernel.scripting.CmdCheckbox;
import org.geogebra.common.kernel.scripting.CmdDelete;
import org.geogebra.common.kernel.scripting.CmdExecute;
import org.geogebra.common.kernel.scripting.CmdPan;
import org.geogebra.common.kernel.scripting.CmdParseToFunction;
import org.geogebra.common.kernel.scripting.CmdParseToNumber;
import org.geogebra.common.kernel.scripting.CmdPerspective;
import org.geogebra.common.kernel.scripting.CmdPlaySound;
import org.geogebra.common.kernel.scripting.CmdReadText;
import org.geogebra.common.kernel.scripting.CmdRelation;
import org.geogebra.common.kernel.scripting.CmdRename;
import org.geogebra.common.kernel.scripting.CmdRepeat;
import org.geogebra.common.kernel.scripting.CmdRigidPolygon;
import org.geogebra.common.kernel.scripting.CmdRunClickScript;
import org.geogebra.common.kernel.scripting.CmdRunUpdateScript;
import org.geogebra.common.kernel.scripting.CmdSelectObjects;
import org.geogebra.common.kernel.scripting.CmdSetActiveView;
import org.geogebra.common.kernel.scripting.CmdSetAxesRatio;
import org.geogebra.common.kernel.scripting.CmdSetBackgroundColor;
import org.geogebra.common.kernel.scripting.CmdSetCaption;
import org.geogebra.common.kernel.scripting.CmdSetColor;
import org.geogebra.common.kernel.scripting.CmdSetConditionToShowObject;
import org.geogebra.common.kernel.scripting.CmdSetCoords;
import org.geogebra.common.kernel.scripting.CmdSetDecoration;
import org.geogebra.common.kernel.scripting.CmdSetDynamicColor;
import org.geogebra.common.kernel.scripting.CmdSetFilling;
import org.geogebra.common.kernel.scripting.CmdSetFixed;
import org.geogebra.common.kernel.scripting.CmdSetImage;
import org.geogebra.common.kernel.scripting.CmdSetLabelMode;
import org.geogebra.common.kernel.scripting.CmdSetLayer;
import org.geogebra.common.kernel.scripting.CmdSetLevelOfDetail;
import org.geogebra.common.kernel.scripting.CmdSetLineStyle;
import org.geogebra.common.kernel.scripting.CmdSetLineThickness;
import org.geogebra.common.kernel.scripting.CmdSetPointSize;
import org.geogebra.common.kernel.scripting.CmdSetPointStyle;
import org.geogebra.common.kernel.scripting.CmdSetSeed;
import org.geogebra.common.kernel.scripting.CmdSetTooltipMode;
import org.geogebra.common.kernel.scripting.CmdSetTrace;
import org.geogebra.common.kernel.scripting.CmdSetValue;
import org.geogebra.common.kernel.scripting.CmdSetVisibleInView;
import org.geogebra.common.kernel.scripting.CmdShowAxesOrGrid;
import org.geogebra.common.kernel.scripting.CmdShowHideLayer;
import org.geogebra.common.kernel.scripting.CmdShowLabel;
import org.geogebra.common.kernel.scripting.CmdSlider;
import org.geogebra.common.kernel.scripting.CmdSlowPlot;
import org.geogebra.common.kernel.scripting.CmdStartAnimation;
import org.geogebra.common.kernel.scripting.CmdStartRecord;
import org.geogebra.common.kernel.scripting.CmdToolImage;
import org.geogebra.common.kernel.scripting.CmdTurtle;
import org.geogebra.common.kernel.scripting.CmdTurtleBack;
import org.geogebra.common.kernel.scripting.CmdTurtleDown;
import org.geogebra.common.kernel.scripting.CmdTurtleForward;
import org.geogebra.common.kernel.scripting.CmdTurtleLeft;
import org.geogebra.common.kernel.scripting.CmdTurtleRight;
import org.geogebra.common.kernel.scripting.CmdTurtleUp;
import org.geogebra.common.kernel.scripting.CmdUpdateConstruction;
import org.geogebra.common.kernel.scripting.CmdZoomIn;
import org.geogebra.common.kernel.scripting.CmdZoomOut;

/**
 * Factory for scripting commands.
 * @see CommandProcessorFactory
 */
public class ScriptingCommandProcessorFactory implements CommandProcessorFactory {
	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {
		switch (command) {
		// scripting
		case RigidPolygon:
			return new CmdRigidPolygon(kernel);
		case Relation:
			return new CmdRelation(kernel);
		case CopyFreeObject:
			return new CmdCopyFreeObject(kernel);
		case DataFunction:
			return new CmdDataFunction(kernel);
		case SetColor:
			return new CmdSetColor(kernel);
		case SetBackgroundColor:
			return new CmdSetBackgroundColor(kernel);
		case SetDecoration:
			return new CmdSetDecoration(kernel);
		case SetDynamicColor:
			return new CmdSetDynamicColor(kernel);
		case SetConditionToShowObject:
			return new CmdSetConditionToShowObject(kernel);
		case SetFilling:
			return new CmdSetFilling(kernel);
		case SetLevelOfDetail:
			return new CmdSetLevelOfDetail(kernel);
		case SetLineThickness:
			return new CmdSetLineThickness(kernel);
		case SetLineStyle:
			return new CmdSetLineStyle(kernel);
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
			return new CmdShowHideLayer(kernel, false);
		case ShowLayer:
			return new CmdShowHideLayer(kernel, true);
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
		case ReadText:
			return new CmdReadText(kernel);
		case ParseToNumber:
			return new CmdParseToNumber(kernel);
		case ParseToFunction:
			return new CmdParseToFunction(kernel);
		case StartAnimation:
			return new CmdStartAnimation(kernel);
		case SetPerspective:
			return new CmdPerspective(kernel);
		case StartRecord:
			return new CmdStartRecord(kernel);
		case Delete:
			return new CmdDelete(kernel);
		case Repeat:
			return new CmdRepeat(kernel);
		case Slider:
			return new CmdSlider(kernel);
		case Checkbox:
			return new CmdCheckbox(kernel);
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
			return new CmdShowAxesOrGrid(kernel, Commands.ShowAxes);
		case ShowGrid:
			return new CmdShowAxesOrGrid(kernel, Commands.ShowGrid);
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
		case TurtleUp:
			return new CmdTurtleUp(kernel);
		case TurtleDown:
			return new CmdTurtleDown(kernel);
		case RunClickScript:
			return new CmdRunClickScript(kernel);
		case RunUpdateScript:
			return new CmdRunUpdateScript(kernel);
		case SetImage:
			return new CmdSetImage(kernel);
		// case DensityPlot:
		// return new CmdDensityPlot(kernel);
		default:
			break;
		}
		return null;
	}
}

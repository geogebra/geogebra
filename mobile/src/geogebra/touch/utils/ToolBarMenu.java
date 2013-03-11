package geogebra.touch.utils;

/**
 * 
 * Merging {@link ToolBarCommand ToolBarCommands} of the buttons from the
 * main-toolBar with the ToolBarCommands of all the buttons in the submenu.
 * 
 * @author Thomas Krismayer
 * 
 */
public enum ToolBarMenu
{

	// TODO: add menu items when implemented

	Point(ToolBarCommand.NewPoint, new ToolBarCommand[] { ToolBarCommand.NewPoint, ToolBarCommand.PointOnObject, ToolBarCommand.AttachDetachPoint,
	    // TODO: ToolBarCommand.IntersectTwoObjects,
	    ToolBarCommand.MidpointOrCenter, ToolBarCommand.ComplexNumbers }),

	Line(ToolBarCommand.LineThroughTwoPoints,
	    new ToolBarCommand[] { ToolBarCommand.LineThroughTwoPoints, ToolBarCommand.SegmentBetweenTwoPoints, ToolBarCommand.RayThroughTwoPoints,
	        ToolBarCommand.PolylineBetweenPoints, ToolBarCommand.VectorBetweenTwoPoints, ToolBarCommand.VectorFromPoint }),

	SpecialLine(ToolBarCommand.PerpendicularLine, new ToolBarCommand[] { ToolBarCommand.PerpendicularLine, ToolBarCommand.ParallelLine,
	    ToolBarCommand.PerpendicularBisector, ToolBarCommand.AngleBisector, ToolBarCommand.Tangents,
	// TODO: ToolBarCommand.PolarOrDiameterLine,
	// TODO: ToolBarCommand.BestFitLine,
	// TODO: ToolBarCommand.Locus
	    }),

	Polygon(ToolBarCommand.Polygon, new ToolBarCommand[] { ToolBarCommand.Polygon, ToolBarCommand.RegularPolygon, ToolBarCommand.RigidPolygon,
	    ToolBarCommand.VectorPolygon }),

	CircleAndArc(ToolBarCommand.CircleWithCenterThroughPoint, new ToolBarCommand[] {
	    ToolBarCommand.CircleWithCenterThroughPoint,
	    // TODO: ToolBarCommand.Compasses,
	    ToolBarCommand.CircleThroughThreePoints, ToolBarCommand.Semicircle, ToolBarCommand.CircularArcWithCenterBetweenTwoPoints,
	    ToolBarCommand.CircumCirculuarArcThroughThreePoints, ToolBarCommand.CircularSectorWithCenterBetweenTwoPoints,
	    ToolBarCommand.CircumCircularSectorThroughThreePoints }),

	ConicSection(ToolBarCommand.Ellipse, new ToolBarCommand[] { ToolBarCommand.Parabola, ToolBarCommand.Ellipse, ToolBarCommand.Hyperbola,
	    ToolBarCommand.ConicThroughFivePoints }),

	Mesurement(ToolBarCommand.Angle, new ToolBarCommand[] { ToolBarCommand.Angle,
	// TODO: ToolBarCommand.DistanceOrLength,
	// TODO: ToolBarCommand.Area,
	// TODO: ToolBarCommand.Slope,
	// TODO: ToolBarCommand.CreateList
	    }),

	Transformation(ToolBarCommand.ReflectObjectAboutLine, new ToolBarCommand[] { ToolBarCommand.ReflectObjectAboutLine,
	    ToolBarCommand.ReflectObjectAboutPoint, ToolBarCommand.ReflectObjectAboutCircle, ToolBarCommand.TranslateObjectByVector }),

	// TODO:
	// SpecialObject(ToolBarCommand.InsertText, new ToolBarCommand[] {
	// ToolBarCommand.InsertText,
	// ToolBarCommand.InsertImage,
	// ToolBarCommand.Pen,
	// ToolBarCommand.FreehandShape,
	// ToolBarCommand.RelationBetweenTwoObjects }),

	// TODO:
	// ActionObject(ToolBarCommand.Slider, new ToolBarCommand[] {
	// ToolBarCommand.Slider,
	// ToolBarCommand.CheckBoxToShowHideObjects,
	// ToolBarCommand.InsertButton,
	// ToolBarCommand.InsertInputBox }),

	ManipulateObjects(ToolBarCommand.Move_Mobile, new ToolBarCommand[] { ToolBarCommand.Move_Mobile,
	    // TODO: ToolBarCommand.Select,
	    ToolBarCommand.DeleteObject });

	ToolBarCommand[] entry;
	ToolBarCommand command;

	ToolBarMenu(ToolBarCommand command, ToolBarCommand[] entries)
	{
		this.command = command;
		this.entry = entries;
	}

	public ToolBarCommand getCommand()
	{
		return this.command;
	}

	public ToolBarCommand[] getEntries()
	{
		return this.entry;
	}

}

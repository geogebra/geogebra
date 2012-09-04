package geogebra.mobile.utils;

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
	Point(ToolBarCommand.NewPoint, new ToolBarCommand[] {
			ToolBarCommand.NewPoint, ToolBarCommand.AttachDetachPoint,
			ToolBarCommand.ComplexNumbers, ToolBarCommand.IntersectTwoObjects,
			ToolBarCommand.MidpointOrCenter, ToolBarCommand.PointOnObject }),

	Line(ToolBarCommand.LineThroughTwoPoints, new ToolBarCommand[] {
			ToolBarCommand.LineThroughTwoPoints,
			ToolBarCommand.PolylineBetweenPoints,
			ToolBarCommand.RayThroughTwoPoints,
			ToolBarCommand.SegmentBetweenTwoPoints,
			ToolBarCommand.SegmentWithGivenLengthFromPoint,
			ToolBarCommand.VectorBetweenTwoPoints,
			ToolBarCommand.VectorFromPoint }),

	SpecialLine(ToolBarCommand.PerpendicularLine, new ToolBarCommand[] {
			ToolBarCommand.AngleBisector, ToolBarCommand.BestFitLine,
			ToolBarCommand.Locus, ToolBarCommand.ParallelLine,
			ToolBarCommand.PerpendicularBisector,
			ToolBarCommand.PerpendicularLine,
			ToolBarCommand.PolarOrDiameterLine, ToolBarCommand.Tangents }),

	Polygon(ToolBarCommand.Polygon, new ToolBarCommand[] {
			ToolBarCommand.Polygon, ToolBarCommand.RegularPolygon,
			ToolBarCommand.RigidPolygon, ToolBarCommand.VectorPolygon }),

	CircleAndArc(ToolBarCommand.CircleWithCenterThroughPoint,
			new ToolBarCommand[] { ToolBarCommand.CircleThroughThreePoints,
					ToolBarCommand.CircleWithCenterAndRadius,
					ToolBarCommand.CircleWithCenterThroughPoint,
					ToolBarCommand.CircularArcWithCenterBetweenTwoPoints,
					ToolBarCommand.CircularSectorWithCenterBetweenTwoPoints,
					ToolBarCommand.CircumCirculuarArcThroughThreePoints,
					ToolBarCommand.CircumCircularSectorThroughThreePoints,
					ToolBarCommand.Compasses, ToolBarCommand.Semicircle }),

	ConicSection(ToolBarCommand.Ellipse, new ToolBarCommand[] {
			ToolBarCommand.ConicThroughFivePoints, ToolBarCommand.Ellipse,
			ToolBarCommand.Hyperbola, ToolBarCommand.Parabola }),

	Mesurement(ToolBarCommand.Angle, new ToolBarCommand[] {
			ToolBarCommand.Angle, ToolBarCommand.AngleWithGivenSize,
			ToolBarCommand.Area, ToolBarCommand.CreateList,
			ToolBarCommand.DistanceOrLength, ToolBarCommand.Slope }),

	Transformation(ToolBarCommand.ReflectObjectAboutLine, new ToolBarCommand[] {
			ToolBarCommand.DilateObjectFromPointByFactor,
			ToolBarCommand.ReflectObjectAboutCircle,
			ToolBarCommand.ReflectObjectAboutLine,
			ToolBarCommand.ReflectObjectAboutPoint,
			ToolBarCommand.RotateObjectAboutPointByAngle,
			ToolBarCommand.TranslateObjectByVector }),

	SpecialObject(ToolBarCommand.InsertText, new ToolBarCommand[] {
			ToolBarCommand.FreehandShape, ToolBarCommand.FunctionInspector,
			ToolBarCommand.InsertImage, ToolBarCommand.InsertText,
			ToolBarCommand.Pen, ToolBarCommand.ProbabilityCalculator,
			ToolBarCommand.RelationBetweenTwoObjects }),

	ActionObject(ToolBarCommand.Slider, new ToolBarCommand[] {
			ToolBarCommand.CheckBoxToShowHideObjects,
			ToolBarCommand.InsertButton, ToolBarCommand.InsertInputBox,
			ToolBarCommand.Slider }),

	ManipulateObjects(ToolBarCommand.Move, new ToolBarCommand[] {
			ToolBarCommand.Move, ToolBarCommand.Select,
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

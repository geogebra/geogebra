package geogebra.touch.utils;

/**
 * list of commands that can be drawn with a slide
 * 
 * @author Thomas Krismayer
 * 
 */
public class Swipeables {
    private static ToolBarCommand[] allowedCommands = new ToolBarCommand[] { ToolBarCommand.LineThroughTwoPoints,
	    ToolBarCommand.SegmentBetweenTwoPoints, ToolBarCommand.VectorBetweenTwoPoints, ToolBarCommand.RayThroughTwoPoints,
	    ToolBarCommand.CircleWithCenterThroughPoint };

    public static boolean isSwipeable(ToolBarCommand command) {
	for (final ToolBarCommand cmd : allowedCommands) {
	    if (command != null && cmd == command) {
		return true;
	    }
	}
	return false;
    }

    private Swipeables() {
    }
}

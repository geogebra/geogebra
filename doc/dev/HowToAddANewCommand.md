# How to add a new command
Markus Hohenwarter, May 5, 2007


This document describes how to write code for a new command in
GeoGebra's input field. We will explain this using one very simple
command, the midpoint of two points P and Q: `Midpoint[P, Q]`.

## 0. Before coding
Add a name and syntax of the command to GGBTrans translation system.

```
 Midpoint=Midpoint
 Midpoint.Syntax=[ <Point>, <Point> ]\n[ <Segment> ]\n[ <Conic> ]
```

Once added to GGBTrans, these keys will be added to localization files for all platforms automatically (the update script runs nightly).

## 1. Create representation in enums
First create a value in `geogebra.common.kernel.commands.Commands`; it must be the same as the chosen name of your command (including capitalization). The constructor parameter lets you set in which category the command will appear in input help.

## 2. Create a new algorithm class
Such classes are located in `geogebra.common.kernel.algos`.

The best way to do this, is by copying an existing algorithm
class and changing it. We will explain the following steps
using the class geogebra.kernel.AlgoMidpoint as an example.
 * Provide a constructor that takes the label of the resulting object, e.g. "M" for M = Midpoint[P, Q], and the input objects,  e.g. Points P and Q.
 * Within the constructor method make sure to call the methods  setInputOutput() and compute()
 * Change the following methods in your new algorithm class
   * `getClassName()`: returns the corresponding Commands object
   * `setInputOutput()`: make sure to call setDependencies() at its end
   * `compute()`: here the actual work of your algorithm is done, e.g. the midpoint of P and Q is calculated
   * `toString(StringTemplate tpl)`: for basic commands GeoGebra provides human-readable description (e.g. "Line bisectof segment A, B"). If your command needs this, please override the method

      ```
	  @Override
	  final public String toString(StringTemplate tpl) {
        return getLoc().getPlain("LineBisectorOfA", s.getLabel(tpl));
	  }
      ```

     If you omit this, the description will default to "Midpoint[A, B]" for this example.

   * `getResult()` provide a method that returns the resulting object of the algorithm

## 3. (Optional) Add a new method for your command to AlgoDispatcher
__Only do this if necessary -- ie if the command should be used as tool or if it must be handled differently in 3D__

This method's parameterlist should provide a label for the command's
result and its input parameters, e.g. for our Midpoint command:
```
 final public GeoPoint Midpoint(String label, GeoPoint P, GeoPoint Q) {
   AlgoMidpoint algo = new AlgoMidpoint(cons, label, P, Q);
   GeoPoint M = algo.getPoint();
   return M;
 }
```
The reason for this method is to provide a convenient way to
use the command both from the command line of GeoGebra and
in the geometry window when working with the mouse.

## 4. Create a new command processing class to geogebra.common.kernel.commands

Add a new subclass (a new file!) of CommandProcessor to the geogebra.common.kernel.commands
package. The first three letters of the new classname should be "Cmd", for example CmdMidpoint.
Here, the arguments of a command are processed and their types
are checked in order to call the right variant of a command.
For example the Midpoint command works for either a conic, a segment
or two points.

If step 3) was skipped, this command processor should instantiate the `AlgoElement` object directly and return its results.

```
/*
 * Midpoint[ <GeoConic> ]
 * Midpoint[ <Segment> ]
 * Midpoint[ <GeoPoint>, <GeoPoint> ]
 */
class CmdMidpoint extends CommandProcessor {
   
    public CmdMidpoint(Kernel kernel) {
        super(kernel);
    }
   
    public GeoElement[] process(Command c) throws MyError {
        int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
        GeoElement[] arg;
        switch (n) {
            case 1 :
              // Midpoint[ <GeoConic> ]
                arg = resArgs(c);
                if (ok[0] = (arg[0].isGeoConic())) {
                    GeoElement[] ret =
                        { kernel.Center(c.getLabel(), (GeoConic) arg[0])};
                    return ret;
            // Midpoint[ <GeoSegment> ]
                } else if (arg[0].isGeoSegment()) {
                    GeoElement[] ret =
                        { kernel.Midpoint(c.getLabel(), (GeoSegment) arg[0])};
                    return ret;
                } else
               throw argErr(app, c.getName(), arg[0]);
            case 2 :
            // Midpoint[ <GeoPoint>, <GeoPoint> ]
                arg = resArgs(c);
                if ((ok[0] = (arg[0].isGeoPoint()))
                    && (ok[1] = (arg[1].isGeoPoint()))) {
                    GeoElement[] ret =
                        {
                             kernel.Midpoint(
                                c.getLabel(),
                                (GeoPoint) arg[0],
                                (GeoPoint) arg[1])};
                    return ret;
                } else {
                    if (!ok[0])
                        throw argErr(app, c.getName(), arg[0]);
                    else
                        throw argErr(app, c.getName(), arg[1]);
                }
            default :
                throw argNumErr(app, c.getName(), n);
        }
    }   
}
```

## 5. Add a line to the switch in CommandDispatcher and CommandDispatcherBasic/Advanced/Scripting/Statistics as appropriate

Here we use the Commands enum value for our new command
and associate it with our command processing class.
```
 case Midpoint: return new CmdMidpoint(kernel);
```
## 6. Add a test method to CommandsTest 

You need to check out GeoGebraTest project first if you didn't do so before. In `geogebra.commands.CommandsTest` add a simple method that runs all syntaxes of your command (if you test less syntaxes, the test will fail).

```
@Test
	public void cmdMidpoint() {
		t("Midpoint[ xx+yy=1]", "(0,0)");
		t("MidPoint[1<x<3]", "2");
		t("Midpoint[(1,1),(3,3)]", "(2,2)");
		t("Midpoint[ Segment((1,1),(3,3)) ]", "(2,2)");
	}
```
The method name must be `cmdCommandName`, where `CommandName` comes from `geogebra.common.kernel.commands.Commands`. There is a test that checks coorespondence between test methods, translation keys and values in the Commands enum.
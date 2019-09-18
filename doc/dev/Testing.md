## Unit tests
* [StringUtilTest](https://git.geogebra.org/ggb/geogebra/blob/master/common-jre/src/test/java/org/geogebra/common/util/StringUtilTest.java) for testing a utility class

## Component test
* [CommandsTest](https://git.geogebra.org/ggb/geogebra/blob/master/common-jre/src/test/java/org/geogebra/common/kernel/commands/CommandsTest.java) for thesting the commands and algos package

## Integration test
* Common: [ControllerTest](https://git.geogebra.org/ggb/geogebra/blob/master/common-jre/src/test/java/org/geogebra/common/euclidian/ControllerTest.java) shows how to emulate events in Grraphics to use the tools
* Web: [NotesUndoTest](https://git.geogebra.org/ggb/geogebra/blob/master/web/src/test/java/org/geogebra/web/full/main/NotesUndoTest.java)

## UI test
* web:
    * To install cypress and run, follow https://git.geogebra.org/ggb/web-test-harness/blob/master/README.md
    * [ContextMenuTesst] (https://git.geogebra.org/ggb/web-test-harness/blob/master/cypress/integration/algebraView/contextMenu.spec.js)
    * Please check support/commands.js for the global custom command the test uses

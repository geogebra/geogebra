## Unit tests
* [StringUtilTest](https://git.geogebra.org/ggb/geogebra/blob/main/source/shared/common-jre/src/test/java/org/geogebra/common/util/StringUtilTest.java) for testing a utility class

## Component test
* [CommandsTest](https://git.geogebra.org/ggb/geogebra/blob/main/source/shared/common-jre/src/test/java/org/geogebra/common/kernel/commands/CommandsTest.java) for testing the commands and algos package

## Integration test
* Common: [ControllerTest](https://git.geogebra.org/ggb/geogebra/blob/main/source/shared/common-jre/src/test/java/org/geogebra/common/euclidian/EuclidianControllerTest.java) shows how to emulate events in Grraphics to use the tools
* Web: [NotesUndoTest](https://git.geogebra.org/ggb/geogebra/blob/main/source/web/web/src/test/java/org/geogebra/web/full/main/NotesUndoTest.java)

## UI test
* web:
    * To install cypress and run, follow the [README.md](https://git.geogebra.org/ggb/web-test-harness/blob/master/README.md)
    * AV ContextMenu: [ContextMenuTest](https://git.geogebra.org/ggb/web-test-harness/blob/master/cypress/integration/algebraView/contextMenu.spec.js)
    * Please check [commands.js](https://git.geogebra.org/ggb/web-test-harness/blob/master/cypress/support/commandss.js) for the global custom commands the 
test uses and also [selectors.js](https://git.geogebra.org/ggb/web-test-harness/blob/master/cypress/support/selectors.js)

* mow-front:
    * Install cypress as for web under [uitest](https://git.geogebra.org/mow/mow-front/tree/master/uitest) directory
    * Error when a material cannot be opened: [errormessage.spec.js](https://git.geogebra.org/mow/mow-front/blob/master/uitest/cypress/integration/errormessage/error_message.spec.js)
    * Check structure under [support](https://git.geogebra.org/ggb/web-test-harness/blob/master/cypress/support/) for the commands.
    * To run it (must reach alp of course) use npx cypress run --env tafel.password=$PASSWORD,tafel.url=http://tafel.dlb-dev01.alp-dlg.net/
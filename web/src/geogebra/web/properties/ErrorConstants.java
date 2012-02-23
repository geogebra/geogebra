package geogebra.web.properties;

import com.google.gwt.i18n.client.ConstantsWithLookup;

@SuppressWarnings("javadoc")
/**
 * ErrorConstants and interface that represents the Error properties file
 * @author Rana
 *
 */
public interface ErrorConstants extends ConstantsWithLookup {
	
	@DefaultStringValue("Invalid input")
	String InvalidInput();

	@DefaultStringValue("Invalid equation:\nPlease enter a polynomial equation in x and y")
	String InvalidEquation();

	@DefaultStringValue("Incomplete equation:\nPlease enter both sides of the equation")
	String IncompleteEquation();

	@DefaultStringValue("Tool could not be created")
	String Tool_CreationFailed();

	@DefaultStringValue("Input object is not needed")
	String Tool_InputNotNeeded();

	@DefaultStringValue("Point or vector expected")
	String VectorExpected();

	@DefaultStringValue("Every exponent has to be constant")
	String ExponentMustBeConstant();

	@DefaultStringValue("Every length has to be constant")
	String LengthMustBeConstant();

	@DefaultStringValue("Illegal comparison")
	String IllegalComparison();

	@DefaultStringValue("Illegal variable")
	String IllegalVariable();

	@DefaultStringValue("Please upgrade to a newer version of GeoGebra\nThis file format is not supported")
	String FileFormatNewer();

	@DefaultStringValue("Illegal Boolean operation")
	String IllegalBoolean();

	@DefaultStringValue("Number expected")
	String NumberExpected();

	@DefaultStringValue("Illegal argument")
	String IllegalArgument();

	@DefaultStringValue("File not found")
	String FileNotFound();

	@DefaultStringValue("Illegal exponent")
	String IllegalExponent();

	@DefaultStringValue("Function expected")
	String FunctionExpected();

	@DefaultStringValue("Illegal complex multiplication")
	String IllegalComplexMultiplication();

	@DefaultStringValue("Undefined variable")
	String UndefinedVariable();

	@DefaultStringValue("Dependent objects may not be overwritten")
	String AssignmentToDependent();

	@DefaultStringValue("Illegal list operation")
	String IllegalListOperation();

	@DefaultStringValue("Unknown file format")
	String FileFormatUnknown();

	@DefaultStringValue("Sorry - couldn't paste bitmap from the clipboard")
	String PasteImageFailed();

	@DefaultStringValue("Sorry, something went wrong. Please check your input")
	String CAS_GeneralErrorMessage();

	@DefaultStringValue("Opening URL failed")
	String URLnotFound();

	@DefaultStringValue("Illegal assignment")
	String IllegalAssignment();

	@DefaultStringValue("Illegal division")
	String IllegalDivision();

	@DefaultStringValue("Illegal number of arguments")
	String IllegalArgumentNumber();

	@DefaultStringValue("Sorry, the geogebra_exportjar file is missing or corrupt")
	String ExportJarMissing();

	@DefaultStringValue("Data file is too large")
	String FileIsTooLarge();

	@DefaultStringValue("Illegal multiplication")
	String IllegalMultiplication();

	@DefaultStringValue("Invalid inequality:\nPlease enter a linear inequality in x and y")
	String InvalidInequality();

	@DefaultStringValue("Every exponent has to be an integer")
	String ExponentMustBeInteger();

	@DefaultStringValue("Rename failed")
	String RenameFailed();

	@DefaultStringValue("The chosen look and feel is not available on your computer")
	String UnsupportedLAF();

	@DefaultStringValue("Free objects may not be overwritten by dependent objects")
	String AssignmentDependentToFree();

	@DefaultStringValue("Redefinition failed")
	String ReplaceFailed();

	@DefaultStringValue("Calculation took too long and was aborted")
	String CAS_TimeoutError();

	@DefaultStringValue("Dependent objects may not be changed")
	String ChangeDependent();

	@DefaultStringValue("Circular definition")
	String CircularDefinition();

	@DefaultStringValue("This label is already in use")
	String NameUsed();

	@DefaultStringValue("Saving file failed")
	String SaveFileFailed();

	@DefaultStringValue("Please close the browser window to exit GeoGebra")
	String AppletWindowClosing();

	@DefaultStringValue("Output object does not depend on input objects")
	String Tool_OutputNotDependent();

	@DefaultStringValue("Could not find online help")
	String HelpNotFound();

	@DefaultStringValue("Illegal addition")
	String IllegalAddition();

	@DefaultStringValue("Unknown command")
	String UnknownCommand();

	@DefaultStringValue("Invalid function:\nPlease enter an explicit function in x")
	String InvalidFunction();

	@DefaultStringValue("Please check the structure of your selection")
	String CAS_SelectionStructureError();

	@DefaultStringValue("Coordinates have to be constant")
	String CoordinatesMustBeConstant();

	@DefaultStringValue("Error")
	String Error();

	@DefaultStringValue("One or more references are invalid")
	String CAS_InvalidReferenceError();

	@DefaultStringValue("Opening file failed")
	String LoadFileFailed();

	@DefaultStringValue("Fixed objects may not be changed")
	String AssignmentToFixed();

	@DefaultStringValue("Following tools were used to create selected objects and cannot be deleted:")
	String Tool_DeleteUsed();

	@DefaultStringValue("Illegal subtraction")
	String IllegalSubtraction();

	@DefaultStringValue("Every angle has to be constant")
	String AngleMustBeConstant();

	@DefaultStringValue("Every divisor has to be constant")
	String DivisorMustBeConstant();

	@DefaultStringValue("Command name is already used by another tool")
	String Tool_CommandNameTaken();
}

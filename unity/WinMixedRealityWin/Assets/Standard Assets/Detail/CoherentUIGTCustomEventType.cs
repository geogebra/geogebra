using System;

namespace Coherent.UIGT
{
public enum CustomEventValueType
{
	Null,
	Bool,
	Int,
	Float,
	Double,
	String,
	Object
}

[Serializable]
public struct CustomEventType
{
	public CustomEventType(CustomEventValueType arg1,
						   CustomEventValueType arg2,
						   CustomEventValueType arg3,
						   CustomEventValueType arg4)
	{
		Arg1 = arg1;
		Arg2 = arg2;
		Arg3 = arg3;
		Arg4 = arg4;
	}

	public CustomEventValueType Arg1;
	public CustomEventValueType Arg2;
	public CustomEventValueType Arg3;
	public CustomEventValueType Arg4;
}
}
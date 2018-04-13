using UnityEngine;
using System;
using System.Collections;

[AttributeUsage( AttributeTargets.Property )]
public class CoherentUIGTExposePropertyInfo : Attribute
{
	public enum FoldoutType
	{
		General = 0,
		Rendering,
		AdvancedRendering,
		Input,
		Scripting,

		Count
	};

	public CoherentUIGTExposePropertyInfo()
	{
		this.Category = FoldoutType.General;
	}

	public CoherentUIGTExposePropertyInfo(FoldoutType category)
	{
		this.Category = category;
	}

	public string PrettyName
	{ get; set; }

	public string Tooltip
	{ get; set; }

	public FoldoutType Category
	{ get; set;}

	public bool IsStatic
	{ get; set; }
}

[AttributeUsage( AttributeTargets.Property )]
public class CoherentUIGTExposePropertyAttribute : CoherentUIGTExposePropertyInfo
{}

[AttributeUsage( AttributeTargets.Property )]
public class CoherentUIGTExposePropertyStandaloneAttribute : CoherentUIGTExposePropertyInfo
{}

[AttributeUsage( AttributeTargets.Property )]
public class CoherentUIGTExposePropertyMobileAttribute : CoherentUIGTExposePropertyInfo
{}

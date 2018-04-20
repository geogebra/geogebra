using UnityEditor;
using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;

[InitializeOnLoad]
public static class CoherentUIGTExposeProperties
{
	static Texture m_Texture;
	static Texture m_LogoTexture;
	static Texture m_SupportTexture;
	static Texture m_DocsTexture;

	static CoherentUIGTExposeProperties()
	{
		EditorApplication.hierarchyWindowItemOnGUI += HierarchyWindowListElementOnGUI;
	}

	public static void Expose(CoherentUIGTFoldout[] foldouts)
	{
		GUILayoutOption[] emptyOptions = new GUILayoutOption[0];

		if (!m_LogoTexture)
		{
			m_LogoTexture = EditorGUIUtility.FindTexture("Coherent_UI_inspector");
		}
		if (!m_SupportTexture)
		{
			m_SupportTexture = EditorGUIUtility.FindTexture("Coherent_UI_support");
		}
		if (!m_DocsTexture)
		{
			m_DocsTexture = EditorGUIUtility.FindTexture("Coherent_UI_docs");
		}
		EditorGUILayout.BeginVertical(GUILayout.Height(32));
		EditorGUILayout.BeginHorizontal(emptyOptions);
		var labelStyle = new GUIStyle();
		labelStyle.fixedHeight = 32;
		labelStyle.fixedWidth = 163;
		if (m_LogoTexture)
		{
			EditorGUILayout.LabelField(new GUIContent("", m_LogoTexture), labelStyle, emptyOptions);
		}

		if (m_DocsTexture && GUILayout.Button(m_DocsTexture))
		{
			Application.OpenURL("https://coherent-labs.com/Documentation/unity-gt/");
		}
		if (m_SupportTexture && GUILayout.Button(m_SupportTexture))
		{
			Application.OpenURL("https://coherent-labs.com/developer/");
		}

		EditorGUILayout.EndHorizontal();
		EditorGUILayout.EndVertical();

		#if FALSE && !DISABLE_SEAT_ENFORCE
		if (!(ActivationChecker.IsActive() || ActivationChecker.ShowActivationDialog()))
		{
			return;
		}
		#endif

		EditorGUILayout.BeginVertical(emptyOptions);
		foreach (CoherentUIGTFoldout fold in foldouts)
		{
			if (fold == null)
			{
				continue;
			}
			bool hasPropertiesToShow = false;
			foreach (CoherentUIGTPropertyField field in fold.Fields)
			{
				if (!Application.isPlaying || !field.IsStatic)
				{
					hasPropertiesToShow = true;
					break;
				}
			}
			if (!hasPropertiesToShow)
			{
				continue;
			}

			fold.Show = EditorGUILayout.Foldout(fold.Show, new GUIContent(fold.Name, fold.Tooltip));

			if (fold.Show)
			{
				foreach (CoherentUIGTPropertyField field in fold.Fields)
				{
					if (Application.isPlaying && field.IsStatic)
					{
						continue;
					}

					var realType = field.RealType;

					EditorGUILayout.BeginHorizontal(emptyOptions);
					GUIContent content = new GUIContent(field.Name);
					if (field.Tooltip.Length > 0)
					{
						content.tooltip = field.Tooltip;
					}

					switch (field.Type)
					{
					case SerializedPropertyType.Integer:
						field.SetValue(EditorGUILayout.IntField(content, (int)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.Float:
						field.SetValue(EditorGUILayout.FloatField(content, (float)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.Boolean:
						field.SetValue(EditorGUILayout.Toggle(content, (bool)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.String:
						field.SetValue(EditorGUILayout.TextField(content, (String)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.Vector2:
						field.SetValue(EditorGUILayout.Vector2Field(field.Name, (Vector2)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.Vector3:
						field.SetValue(EditorGUILayout.Vector3Field(field.Name, (Vector3)field.GetValue(), emptyOptions));
						break;

					case SerializedPropertyType.Enum:
						field.SetValue(EditorGUILayout.EnumPopup(content, (Enum)field.GetValue(), emptyOptions));
						break;
					case SerializedPropertyType.ObjectReference:
						field.SetValue(EditorGUILayout.ObjectField(content.text, (UnityEngine.Object)field.GetValue(), realType, true, emptyOptions));
						break;

					default:

						break;

					}

					EditorGUILayout.EndHorizontal();
				}
			}
		}
		EditorGUILayout.EndVertical();
	}

	public static CoherentUIGTFoldout[] GetProperties(System.Object obj)
	{
		CoherentUIGTFoldout[] foldouts = new CoherentUIGTFoldout[(int)CoherentUIGTExposePropertyInfo.FoldoutType.Count];

		PropertyInfo[] infos = obj.GetType().GetProperties(BindingFlags.Public | BindingFlags.Instance);

		foreach (PropertyInfo info in infos)
		{
			if (! (info.CanRead && info.CanWrite))
			{
				continue;
			}

			object[] attributes = info.GetCustomAttributes(true);

			bool isExposed = false;
			object infoAttribute = null;

			foreach (object o in attributes)
			{
				var t = o.GetType();
				if (t == typeof(CoherentUIGTExposePropertyAttribute) ||
					(t == typeof(CoherentUIGTExposePropertyStandaloneAttribute)
						&& IsStandaloneTarget()) ||
					(t == typeof(CoherentUIGTExposePropertyMobileAttribute)
						&& IsMobileTarget()))
				{
					infoAttribute = o;
					isExposed = true;
					break;
				}
			}

			if (!isExposed)
			{
				continue;
			}

			SerializedPropertyType type = SerializedPropertyType.Integer;

			if (CoherentUIGTPropertyField.GetPropertyType(info, out type))
			{
				CoherentUIGTPropertyField field = new CoherentUIGTPropertyField(obj, info, type, infoAttribute);

				var category = CoherentUIGTExposePropertyInfo.FoldoutType.General;
				var attr = infoAttribute as CoherentUIGTExposePropertyInfo;
				if (attr != null)
				{
					category = attr.Category;
				}

				if (foldouts[(int)category] == null)
				{
					foldouts[(int)category] = new CoherentUIGTFoldout(category);
				}
				foldouts[(int)category].AddField(field);
			}

		}

		return foldouts;
	}

	static private bool IsStandaloneTarget()
	{
		var target = EditorUserBuildSettings.activeBuildTarget.ToString();
		return target.StartsWith("Standalone");
	}

	static private bool IsMobileTarget()
	{
		var target = EditorUserBuildSettings.activeBuildTarget;
        #if UNITY_5
		return (target == BuildTarget.Android || target == BuildTarget.iOS);
        #else
		return (target == BuildTarget.Android || target == BuildTarget.iOS);
		#endif
	}

	static void HierarchyWindowListElementOnGUI(int instanceID, Rect selectionRect)
	{
		var go = EditorUtility.InstanceIDToObject(instanceID) as GameObject;
		if (go)
		{
			var view = go.GetComponent(typeof(CoherentUIGTView));
			var sys = go.GetComponent(typeof(CoherentUIGTSystem));
			if (view || sys)
			{
				if (!m_Texture)
				{
					m_Texture = EditorGUIUtility.FindTexture("CoherentUIView_icon");
				}

				if (m_Texture)
				{
					var newRect = selectionRect;
					newRect.x = selectionRect.xMax - selectionRect.height;
					newRect.width = selectionRect.height;
					newRect.height = selectionRect.height;
					GUI.DrawTexture(newRect, m_Texture);
				}
			}
		}
	}
}

public class CoherentUIGTPropertyField
{
	System.Object m_Instance;
	System.Object m_InfoAttribute;
	PropertyInfo m_Info;
	SerializedPropertyType m_Type;
	MethodInfo m_Getter;
	MethodInfo m_Setter;

	public CoherentUIGTPropertyField(System.Object instance, PropertyInfo info, SerializedPropertyType type, System.Object infoAttribute)
	{
		m_Instance = instance;
		m_Info = info;
		m_Type = type;
		m_InfoAttribute = infoAttribute;

		m_Getter = m_Info.GetGetMethod();
		m_Setter = m_Info.GetSetMethod();
	}

	public System.Type RealType
	{
		get
		{
			return m_Info.PropertyType;
		}
	}

	public SerializedPropertyType Type
	{
		get
		{
			return m_Type;
		}
	}

	public String Name
	{
		get
		{
			string name;

			var ia = m_InfoAttribute as CoherentUIGTExposePropertyInfo;
			if (ia != null && ia.PrettyName != null && ia.PrettyName.Length > 0)
			{
				name = ia.PrettyName;
			}
			else
			{
				name = ObjectNames.NicifyVariableName(m_Info.Name);
			}

			return name;
		}
	}

	public String Tooltip
	{
		get
		{
			string tip = "";

			var ia = m_InfoAttribute as CoherentUIGTExposePropertyInfo;
			if (ia != null && ia.Tooltip != null)
			{
				tip = ia.Tooltip;
			}

			return tip;
		}
	}

	public bool IsStatic
	{
		get
		{
			var ia = m_InfoAttribute as CoherentUIGTExposePropertyInfo;
			if (ia != null)
			{
				return ia.IsStatic;
			}
			else
			{
				return false;
			}
		}
	}

	public System.Object GetValue()
	{
		return m_Getter.Invoke(m_Instance, null);
	}

	public void SetValue(System.Object value)
	{
#pragma warning disable 618
		if (!Equal(value))
		{
			Undo.RegisterUndo((UnityEngine.Object)m_Instance, this.Name);
			m_Setter.Invoke(m_Instance, new System.Object[] { value });
			EditorUtility.SetDirty((UnityEngine.Object)m_Instance);
		}
#pragma warning restore 618
	}

	public static bool GetPropertyType(PropertyInfo info, out SerializedPropertyType propertyType)
	{
		propertyType = SerializedPropertyType.Generic;

		Type type = info.PropertyType;

		if (type == typeof(int))
		{
			propertyType = SerializedPropertyType.Integer;
			return true;
		}

		if (type == typeof(float))
		{
			propertyType = SerializedPropertyType.Float;
			return true;
		}

		if (type == typeof(bool))
		{
			propertyType = SerializedPropertyType.Boolean;
			return true;
		}

		if (type == typeof(string))
		{
			propertyType = SerializedPropertyType.String;
			return true;
		}

		if (type == typeof(Vector2))
		{
			propertyType = SerializedPropertyType.Vector2;
			return true;
		}

		if (type == typeof(Vector3))
		{
			propertyType = SerializedPropertyType.Vector3;
			return true;
		}

		if (type.IsEnum)
		{
			propertyType = SerializedPropertyType.Enum;
			return true;
		}

		if (type.IsSubclassOf(typeof(UnityEngine.Object)))
		{
			propertyType = SerializedPropertyType.ObjectReference;
			return true;
		}

		return false;
	}

	private bool Equal(System.Object other)
	{
		switch (m_Type)
		{
		case SerializedPropertyType.Integer:
			return (int)GetValue() == (int)other;

		case SerializedPropertyType.Float:
			return (float)GetValue() == (float)other;

		case SerializedPropertyType.Boolean:
			return (bool)GetValue() == (bool)other;

		case SerializedPropertyType.String:
			return (string)GetValue() == (string)other;

		case SerializedPropertyType.Vector2:
			return (Vector2)GetValue() == (Vector2)other;

		case SerializedPropertyType.Vector3:
			return (Vector3)GetValue() == (Vector3)other;

		case SerializedPropertyType.Enum:
			return (Enum)GetValue() == (Enum)other;

		case SerializedPropertyType.ObjectReference:
			return (UnityEngine.Object)GetValue() == (UnityEngine.Object)other;

		default:

			break;

		}
		return false;
	}
}

public class CoherentUIGTFoldout
{
	public CoherentUIGTFoldout(CoherentUIGTExposePropertyInfo.FoldoutType type)
	{
		m_Fields = new List<CoherentUIGTPropertyField>();

		Type = type;

		switch (Type)
		{
		case CoherentUIGTExposePropertyInfo.FoldoutType.General:
			Name = "General";
			Tooltip = "Shows the most common UI properties";
			Show = true;
			break;
		case CoherentUIGTExposePropertyInfo.FoldoutType.Rendering:
			Name = "Rendering";
			Tooltip = "Shows rendering-related properties";
			break;
		case CoherentUIGTExposePropertyInfo.FoldoutType.AdvancedRendering:
			Name = "Advanced rendering";
			Tooltip = "Shows advanced rendering properties";
			break;
		case CoherentUIGTExposePropertyInfo.FoldoutType.Input:
			Name = "Input";
			Tooltip = "Shows UI input-related properties";
			break;
		case CoherentUIGTExposePropertyInfo.FoldoutType.Scripting:
			Name = "Scripting";
			Tooltip = "Shows UI scripting-related properties";
			break;
		}
	}

	public String Name
	{
		get;
		private set;
	}

	public String Tooltip
	{
		get;
		private set;
	}

	public CoherentUIGTExposePropertyInfo.FoldoutType Type
	{
		get;
		private set;
	}

	public bool Show
	{
		get { return m_Show; }
		set { m_Show = value; }
	}

	public List<CoherentUIGTPropertyField> Fields
	{
		get { return m_Fields; }
	}

	public void AddField(CoherentUIGTPropertyField f)
	{
		m_Fields.Add(f);
	}

	List<CoherentUIGTPropertyField> m_Fields;
	bool m_Show = false;
}
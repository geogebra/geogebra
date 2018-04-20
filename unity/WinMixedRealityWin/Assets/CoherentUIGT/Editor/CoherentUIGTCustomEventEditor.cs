using System;
using System.IO;
using UnityEditor;
using UnityEngine;

using Coherent.UIGT;

[CustomEditor(typeof(CoherentUIGTCustomEvent))]
public partial class CoherentUIGTCustomEventEditor : Editor
{
	SerializedProperty m_TargetViewProperty;
	SerializedProperty m_CreatedEventTypesProperty;
	CustomEventValueType m_Arg1;
	CustomEventValueType m_Arg2;
	CustomEventValueType m_Arg3;
	CustomEventValueType m_Arg4;
	GUIContent m_EventNameContent;
	GUIContent m_IconToolbarMinus;
	GUILayoutOption[] m_EmptyOptions = new GUILayoutOption[0];
	Vector2 m_ContentSize;
	bool m_IsRefreshing;

	protected virtual void OnEnable()
	{
		m_TargetViewProperty = serializedObject.FindProperty("m_TargetView");
		m_CreatedEventTypesProperty = serializedObject.FindProperty("m_CreatedEventTypes");

		m_EventNameContent = new GUIContent();
		m_EventNameContent.text = "Event name";
		m_IconToolbarMinus = new GUIContent(EditorGUIUtility.IconContent("Toolbar Minus"));
		m_IconToolbarMinus.tooltip = "Remove event.";

		m_ContentSize = GUIStyle.none.CalcSize(m_IconToolbarMinus);
		m_ContentSize.x += 8.0f;

		OnEnableGenerated();
	}

	public override void OnInspectorGUI()
	{
		DrawArgumentTypesSection();
		EditorGUILayout.Space();
		DrawCreateButton();
		EditorGUILayout.Space();
		DrawRemoveButton();
		EditorGUILayout.Space();
		GUILayout.Box("", GUILayout.ExpandWidth(true), GUILayout.Height(1));
		EditorGUILayout.Space();
		DrawTargetViewField();
		EditorGUILayout.Space();
		OnInspectorGUIGenerated();
		EditorGUILayout.Space();
		DrawAddButton();
		EditorGUILayout.Space();
	}

	void DrawArgumentTypesSection()
	{
		EditorGUILayout.LabelField("Arguments type", m_EmptyOptions);

		EditorGUILayout.BeginHorizontal();

		GUI.enabled = true;

		m_Arg1 = (CustomEventValueType)EditorGUILayout.EnumPopup(m_Arg1, m_EmptyOptions);
		if (m_Arg1 == CustomEventValueType.Null)
		{
			m_Arg2 = CustomEventValueType.Null;
			m_Arg3 = CustomEventValueType.Null;
			m_Arg4 = CustomEventValueType.Null;
			GUI.enabled = false;
		}

		m_Arg2 = (CustomEventValueType)EditorGUILayout.EnumPopup(m_Arg2, m_EmptyOptions);
		if (m_Arg2 == CustomEventValueType.Null)
		{
			m_Arg3 = CustomEventValueType.Null;
			m_Arg4 = CustomEventValueType.Null;
			GUI.enabled = false;
		}

		m_Arg3 = (CustomEventValueType)EditorGUILayout.EnumPopup(m_Arg3, m_EmptyOptions);
		if (m_Arg3 == CustomEventValueType.Null)
		{
			m_Arg4 = CustomEventValueType.Null;
			GUI.enabled = false;
		}

		m_Arg4 = (CustomEventValueType)EditorGUILayout.EnumPopup(m_Arg4, m_EmptyOptions);

		GUI.enabled = true;

		EditorGUILayout.EndHorizontal();
	}

	void DrawCreateButton()
	{
		GUIStyle buttonStyle = new GUIStyle(GUI.skin.button);
		buttonStyle.fixedWidth = 200f;

		EditorGUILayout.BeginHorizontal();
		GUILayout.FlexibleSpace();

		string buttonContent;

		if (ArgumentTypeExists())
		{
			buttonContent = "Event type created";
			GUI.enabled = false;
		}
		else
		{
			buttonContent = "Create New Event Type";
			GUI.enabled = true;
		}

		if (GUILayout.Button(buttonContent, buttonStyle))
		{
			CreateNewEventType();
		}

		GUI.enabled = true;

		GUILayout.FlexibleSpace();
		EditorGUILayout.EndHorizontal();
	}

	void CreateNewEventType()
	{
		serializedObject.Update();

		int addedEventPos = (int)m_Arg1 + (int)m_Arg2 * 10 + (int)m_Arg3 * 100 + (int)m_Arg4 * 1000;

		int indexToInsert = 0;

		for (; indexToInsert < m_CreatedEventTypesProperty.arraySize; indexToInsert++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(indexToInsert);

			int elementPos = elementAtIndex.FindPropertyRelative("Arg1").enumValueIndex +
				elementAtIndex.FindPropertyRelative("Arg2").enumValueIndex * 10 +
				elementAtIndex.FindPropertyRelative("Arg3").enumValueIndex * 100 +
				elementAtIndex.FindPropertyRelative("Arg4").enumValueIndex * 1000;

			if (elementPos > addedEventPos)
			{
				break;
			}
		}

		m_CreatedEventTypesProperty.InsertArrayElementAtIndex(indexToInsert);
		SerializedProperty element = m_CreatedEventTypesProperty.GetArrayElementAtIndex(indexToInsert);
		element.FindPropertyRelative("Arg1").enumValueIndex = (int)m_Arg1;
		element.FindPropertyRelative("Arg2").enumValueIndex = (int)m_Arg2;
		element.FindPropertyRelative("Arg3").enumValueIndex = (int)m_Arg3;
		element.FindPropertyRelative("Arg4").enumValueIndex = (int)m_Arg4;
		serializedObject.ApplyModifiedProperties();

		GenerateNewEventType();
		GenerateNewEventTypeEditor();

		m_IsRefreshing = true;
		AssetDatabase.Refresh();
	}

	void DrawRemoveButton()
	{
		GUIStyle buttonStyle = new GUIStyle(GUI.skin.button);
		buttonStyle.fixedWidth = 200f;

		EditorGUILayout.BeginHorizontal();
		GUILayout.FlexibleSpace();

		string buttonContent;

		if (EditorApplication.isCompiling && m_IsRefreshing)
		{
			buttonContent = "Refreshing...";
			GUI.enabled = false;
		}
		else
		{
			m_IsRefreshing = false;
			buttonContent = "Remove Event Type";

			if (m_CreatedEventTypesProperty.arraySize == 0)
			{
				GUI.enabled = false;
			}
			else
			{
				GUI.enabled = true;
			}
		}

		if (GUILayout.Button(buttonContent, buttonStyle))
		{
			ShowRemoveEventTypeMenu();
		}

		GUI.enabled = true;

		GUILayout.FlexibleSpace();
		EditorGUILayout.EndHorizontal();
	}

	void ShowRemoveEventTypeMenu()
	{
		GenericMenu menu = new GenericMenu();

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);
			menu.AddItem(new GUIContent(GetFriendlyMenuName(elementAtIndex)), false, OnRemoveSelected, i);
		}

		menu.AddSeparator("");
		menu.AddItem(new GUIContent("Remove all event types"), false, OnRemoveSelected, -1);

		menu.ShowAsContext();

		Event.current.Use();
	}

	void OnRemoveSelected(object index)
	{
		serializedObject.Update();

		if ((int)index == -1)
		{
			m_CreatedEventTypesProperty.ClearArray();
		}
		else
		{
			m_CreatedEventTypesProperty.DeleteArrayElementAtIndex((int)index);
		}

		serializedObject.ApplyModifiedProperties();

		GenerateNewEventType();
		GenerateNewEventTypeEditor();

		m_IsRefreshing = true;
		AssetDatabase.Refresh();
	}

	void GenerateNewEventType()
	{
		string code = "";

		code += "// This file is auto-generated. Do not edit.\r\n";
		code += "\r\n";
		code += "using System;\r\n";
		code += "using System.Collections.Generic;\r\n";
		code += "using UnityEngine;\r\n";
		code += "using UnityEngine.Events;\r\n";
		code += "\r\n";
		code += "public partial class CoherentUIGTCustomEvent\r\n";
		code += "{\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			string friendlyArguments = GetFriendlyTypes(elementAtIndex, false);

			if (!string.IsNullOrEmpty(friendlyArguments))
			{
				friendlyArguments = "<" + friendlyArguments + ">";
			}

			code += "\t[Serializable] public class " + GetFriendlyEventName(elementAtIndex) + " : UnityEvent" + friendlyArguments + " { }\r\n";
		}

		code += "\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			code += "\tpublic List<string> " + "m_" + GetFriendlyEventName(elementAtIndex) + "Names;\r\n";
			code += "\tpublic List<" + GetFriendlyEventName(elementAtIndex) + "> " + "m_" + GetFriendlyEventName(elementAtIndex) + "s;\r\n";
		}

		code += "\r\n";
		code += "\tvoid OnReadyForBindings()\r\n";
		code += "\t{\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			string friendlyArguments = GetFriendlyTypes(elementAtIndex, false);

			if (!string.IsNullOrEmpty(friendlyArguments))
			{
				friendlyArguments = "<" + friendlyArguments + ">";
			}

			code += "\t\tfor (int i = 0; i < " + "m_" + GetFriendlyEventName(elementAtIndex) + "Names.Count; i++)\r\n";
			code += "\t\t{\r\n";
			code += "\t\t\tint indexCopy = i;\r\n";
			code += "\t\t\tm_TargetView.View.RegisterForEvent(" + "m_" + GetFriendlyEventName(elementAtIndex) + "Names[indexCopy],\r\n";
			code += "\t\t\t(Action" + friendlyArguments + ")delegate(" + GetFriendlyArguments(elementAtIndex, true) + ") " +
				"{ " + "m_" + GetFriendlyEventName(elementAtIndex) + "s[indexCopy].Invoke(" + GetFriendlyArguments(elementAtIndex, false) + "); });\r\n";
			code += "\t\t}\r\n";

			if (i != m_CreatedEventTypesProperty.arraySize - 1)
			{
				code += "\r\n";
			}
		}

		code += "\t}\r\n";
		code += "}\r\n";
		code += "\r\n";

		MonoScript script = MonoScript.FromMonoBehaviour(target as CoherentUIGTCustomEvent);

		string path = Application.dataPath + AssetDatabase.GetAssetPath(script).Remove(0, 6);
		path = path.Insert(path.Length - 3, ".generated");

		File.WriteAllText(path, code);
	}

	void GenerateNewEventTypeEditor()
	{
		string code = "";

		code += "// This file is auto-generated. Do not edit.\r\n";
		code += "\r\n";
		code += "using System.Collections.Generic;\r\n";
		code += "using UnityEditor;\r\n";
		code += "using UnityEngine;\r\n";
		code += "using Coherent.UIGT;\r\n";
		code += "\r\n";
		code += "public partial class CoherentUIGTCustomEventEditor\r\n";
		code += "{\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			code += "\tSerializedProperty " + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty;\r\n";
			code += "\tSerializedProperty " + "m_" + GetFriendlyEventName(elementAtIndex) + "sProperty;\r\n";
		}

		code += "\r\n";

		code += "\tpublic void OnEnableGenerated()\r\n";
		code += "\t{\r\n";

		code += "\t\t(target as CoherentUIGTCustomEvent).m_CreatedEventTypes =\r\n";
		code += "\t\t\tnew List<CustomEventType>(new CustomEventType[] {\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			string[] enumDisplayNames = Enum.GetNames(typeof(CustomEventValueType));

			code += "\t\t\t\tnew CustomEventType(CustomEventValueType." + enumDisplayNames[elementAtIndex.FindPropertyRelative("Arg1").enumValueIndex] + ",\r\n";
			code += "\t\t\t\t\t\t\t\t\tCustomEventValueType." + enumDisplayNames[elementAtIndex.FindPropertyRelative("Arg2").enumValueIndex] + ",\r\n";
			code += "\t\t\t\t\t\t\t\t\tCustomEventValueType." + enumDisplayNames[elementAtIndex.FindPropertyRelative("Arg3").enumValueIndex] + ",\r\n";
			code += "\t\t\t\t\t\t\t\t\tCustomEventValueType." + enumDisplayNames[elementAtIndex.FindPropertyRelative("Arg4").enumValueIndex] + "),\r\n";
		}

		code += "\t\t});\r\n";
		code += "\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			code += "\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty = " +
				"serializedObject.FindProperty(\"" + "m_" + GetFriendlyEventName(elementAtIndex) + "Names\");\r\n";
			code += "\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "sProperty = " +
				"serializedObject.FindProperty(\"" + "m_" + GetFriendlyEventName(elementAtIndex) + "s\");\r\n";
		}

		code += "\t}\r\n";
		code += "\r\n";
		code += "\tpublic void OnInspectorGUIGenerated()\r\n";
		code += "\t{\r\n";
		code += "\t\tserializedObject.Update();\r\n";
		code += "\r\n";

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			code += "\t\tfor (int i = 0; i < " + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty.arraySize; i++)\r\n";
			code += "\t\t{\r\n";
			code += "\t\t\tSerializedProperty eventName = " + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty.GetArrayElementAtIndex(i);\r\n";
			code += "\r\n";
			code += "\t\t\tEditorGUILayout.PropertyField(eventName, m_EventNameContent);\r\n";
			code += "\t\t\tEditorGUILayout.PropertyField(" + "m_" + GetFriendlyEventName(elementAtIndex) + "sProperty.GetArrayElementAtIndex(i),\r\n";
			code += "\t\t\t\t\t\t\t\t\t\t  new GUIContent(eventName.stringValue));\r\n";
			code += "\r\n";
			code += "\t\t\tRect lastRect = GUILayoutUtility.GetLastRect();\r\n";
			code += "\t\t\tRect rect = new Rect(lastRect.xMax - m_ContentSize.x, lastRect.y + 1f, m_ContentSize.x, m_ContentSize.y);\r\n";
			code += "\r\n";
			code += "\t\t\tif (GUI.Button(rect, m_IconToolbarMinus, GUIStyle.none))\r\n";
			code += "\t\t\t{\r\n";
			code += "\t\t\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "sProperty.DeleteArrayElementAtIndex(i);\r\n";
			code += "\t\t\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty.DeleteArrayElementAtIndex(i);\r\n";
			code += "\t\t\t\ti--;\r\n";
			code += "\t\t\t}\r\n";
			code += "\t\t}\r\n";
		}

		code += "\r\n";
		code += "\t\tserializedObject.ApplyModifiedProperties();\r\n";
		code += "\t}\r\n";
		code += "\r\n";
		code += "\tvoid OnAddNewSelected(object index)\r\n";
		code += "\t{\r\n";

		if (m_CreatedEventTypesProperty.arraySize > 0)
		{
			code += "\t\tserializedObject.Update();\r\n";
			code += "\t\tswitch ((int)index)\r\n";
			code += "\t\t{\r\n";

			for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
			{
				SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

				code += "\t\tcase " + i + ":\r\n";
				code += "\t\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "NamesProperty.arraySize++;\r\n";
				code += "\t\t\t" + "m_" + GetFriendlyEventName(elementAtIndex) + "sProperty.arraySize++;\r\n";
				code += "\t\t\tbreak;\r\n";
			}

			code += "\t\t}\r\n";
			code += "\t\tserializedObject.ApplyModifiedProperties();\r\n";
		}

		code += "\t}\r\n";
		code += "}\r\n";
		code += "\r\n";

		MonoScript script = MonoScript.FromScriptableObject(this);

		string path = Application.dataPath + AssetDatabase.GetAssetPath(script).Remove(0, 6);
		path = path.Insert(path.Length - 3, ".generated");

		File.WriteAllText(path, code);
	}

	bool ArgumentTypeExists()
	{
		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);

			if (elementAtIndex.FindPropertyRelative("Arg1").enumValueIndex == (int)m_Arg1 &&
				elementAtIndex.FindPropertyRelative("Arg2").enumValueIndex == (int)m_Arg2 &&
				elementAtIndex.FindPropertyRelative("Arg3").enumValueIndex == (int)m_Arg3 &&
				elementAtIndex.FindPropertyRelative("Arg4").enumValueIndex == (int)m_Arg4)
			{
				return true;
			}
		}

		return false;
	}

	void DrawTargetViewField()
	{
		serializedObject.Update();
		EditorGUILayout.PropertyField(m_TargetViewProperty);
		serializedObject.ApplyModifiedProperties();
	}

	void DrawAddButton()
	{
		GUIStyle buttonStyle = new GUIStyle(GUI.skin.button);
		buttonStyle.fixedWidth = 200f;

		EditorGUILayout.BeginHorizontal();
		GUILayout.FlexibleSpace();

		string buttonContent;

		if (m_CreatedEventTypesProperty.arraySize == 0)
		{
			buttonContent = "No Event Types Created";
			GUI.enabled = false;
		}
		else if (EditorApplication.isCompiling && m_IsRefreshing)
		{
			buttonContent = "Refreshing...";
			GUI.enabled = false;
		}
		else
		{
			m_IsRefreshing = false;
			buttonContent = "Add New Event Type";
			GUI.enabled = true;
		}

		if (GUILayout.Button(buttonContent, buttonStyle))
		{
			ShowAddEventMenu();
		}

		GUI.enabled = true;

		GUILayout.FlexibleSpace();
		EditorGUILayout.EndHorizontal();
	}

	void ShowAddEventMenu()
	{
		GenericMenu menu = new GenericMenu();

		for (int i = 0; i < m_CreatedEventTypesProperty.arraySize; i++)
		{
			SerializedProperty elementAtIndex = m_CreatedEventTypesProperty.GetArrayElementAtIndex(i);
			menu.AddItem(new GUIContent(GetFriendlyMenuName(elementAtIndex)), false, OnAddNewSelected, i);
		}

		menu.ShowAsContext();

		Event.current.Use();
	}

	string GetFriendlyMenuName(SerializedProperty customEventType)
	{
		return "Event(" + GetFriendlyTypes(customEventType, true) + ")";
	}

	string GetFriendlyArguments(SerializedProperty customEventType, bool includingType)
	{
		string friendlyTypes = GetFriendlyTypes(customEventType, false);

		if (string.IsNullOrEmpty(friendlyTypes))
		{
			return "";
		}

		string[] types = friendlyTypes.Replace(" ", "").Split(',');

		string friendlyArguments = "";

		for (int i = 0; i < types.Length; i++)
		{
			if (includingType)
			{
				friendlyArguments += types[i] + " ";
			}

			friendlyArguments += "arg" + (i + 1) + ", ";
		}

		friendlyArguments = friendlyArguments.Remove(friendlyArguments.Length - 2);

		return friendlyArguments;
	}

	string GetFriendlyTypes(SerializedProperty customEventType, bool capitalized)
	{
		int arg1EnumIndex = customEventType.FindPropertyRelative("Arg1").enumValueIndex;
		string friendlyTypes = "";

		if (arg1EnumIndex != 0)
		{
			string[] enumDisplayNames = Enum.GetNames(typeof(CustomEventValueType));
			friendlyTypes += enumDisplayNames[arg1EnumIndex];
			int arg2EnumIndex = customEventType.FindPropertyRelative("Arg2").enumValueIndex;
			if (arg2EnumIndex != 0)
			{
				friendlyTypes += ", " + enumDisplayNames[arg2EnumIndex];
				int arg3EnumIndex = customEventType.FindPropertyRelative("Arg3").enumValueIndex;
				if (arg3EnumIndex != 0)
				{
					friendlyTypes += ", " + enumDisplayNames[arg3EnumIndex];
					int arg4EnumIndex = customEventType.FindPropertyRelative("Arg4").enumValueIndex;
					if (arg4EnumIndex != 0)
					{
						friendlyTypes += ", " + enumDisplayNames[arg4EnumIndex];
					}
				}
			}
		}

		return capitalized ? friendlyTypes : friendlyTypes.ToLower();
	}

	string GetFriendlyEventName(SerializedProperty customEventType)
	{
		string friendlyEventName = "Custom";

		int arg1EnumIndex = customEventType.FindPropertyRelative("Arg1").enumValueIndex;

		if (arg1EnumIndex != 0)
		{
			string[] enumDisplayNames = Enum.GetNames(typeof(CustomEventValueType));
			friendlyEventName += enumDisplayNames[arg1EnumIndex];
			int arg2EnumIndex = customEventType.FindPropertyRelative("Arg2").enumValueIndex;
			if (arg2EnumIndex != 0)
			{
				friendlyEventName += enumDisplayNames[arg2EnumIndex];
				int arg3EnumIndex = customEventType.FindPropertyRelative("Arg3").enumValueIndex;
				if (arg3EnumIndex != 0)
				{
					friendlyEventName += enumDisplayNames[arg3EnumIndex];
					int arg4EnumIndex = customEventType.FindPropertyRelative("Arg4").enumValueIndex;
					if (arg4EnumIndex != 0)
					{
						friendlyEventName += enumDisplayNames[arg4EnumIndex];
					}
				}
			}
		}

		friendlyEventName += "Event";

		return friendlyEventName;
	}
}
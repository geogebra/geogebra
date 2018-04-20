using UnityEditor;
using UnityEngine;

[CustomEditor(typeof(CoherentUIGTGamepad))]
public class CoherentUIGTGamepadEditor : Editor
{
	struct VirtualAxisData
	{
		public string name;
		public string negativeButton;
		public string positiveButton;
		public string altNegativeButton;
		public string altPositiveButton;

		public int type; //KeyOrMouseButton = 0, MouseMovement = 1, JoystickAxis = 2
		public int axis;
		public int joyNum;
	}

	private SerializedProperty m_GamepadMappings;
	private SerializedProperty m_VirtualAxisNames;
	private CoherentUIGTGamepad m_Target;
	private bool m_MappingsUnfolded;

	public void OnEnable()
	{
		m_GamepadMappings = serializedObject.FindProperty("m_GamepadMappings");
		m_VirtualAxisNames = serializedObject.FindProperty("m_VirtualAxisNames");
		m_Target = serializedObject.targetObject as CoherentUIGTGamepad;
	}

	public override void OnInspectorGUI()
	{
		serializedObject.Update();

		m_MappingsUnfolded = EditorGUILayout.Foldout(m_MappingsUnfolded, "Gamepad Mappings");
		if (m_MappingsUnfolded)
		{
			EditorGUI.indentLevel++;

			if (ArraySizeField(m_GamepadMappings, "Size:"))
			{
				serializedObject.ApplyModifiedProperties();
				return;
			}

			for (int i = 0; i < m_GamepadMappings.arraySize; i++)
			{
				SerializedProperty map = m_GamepadMappings.GetArrayElementAtIndex(i);
				SerializedProperty id = serializedObject.FindProperty(map.propertyPath + ".Id");
				SerializedProperty axes = serializedObject.FindProperty(map.propertyPath + ".Axes");
				SerializedProperty buttons = serializedObject.FindProperty(map.propertyPath + ".Buttons");

				EditorGUILayout.LabelField("Gamepad " + i + ":");

				EditorGUI.indentLevel++;

				id.intValue = EditorGUILayout.IntField("Id:", id.intValue);

				if (ArraySizeField(axes, "Axes Count:"))
				{
					serializedObject.ApplyModifiedProperties();
					return;
				}

				EditorGUI.indentLevel++;

				for (int j = 0; j < axes.arraySize; j++)
				{

					SerializedProperty axis = axes.GetArrayElementAtIndex(j);
					axis.intValue = EditorGUILayout.Popup("Axis " + j + ":", axis.intValue,
					                                      m_Target.m_VirtualAxisNames.ToArray(), EditorStyles.popup);
				}

				EditorGUI.indentLevel--;

				if (ArraySizeField(buttons, "Buttons Count:"))
				{
					serializedObject.ApplyModifiedProperties();
					return;
				}

				EditorGUI.indentLevel++;

				for (int j = 0; j < buttons.arraySize; j++)
				{

					SerializedProperty button = buttons.GetArrayElementAtIndex(j);
					button.intValue = EditorGUILayout.Popup("Button " + j + ":", button.intValue,
					                                        m_Target.m_VirtualAxisNames.ToArray(), EditorStyles.popup);

				}

				EditorGUI.indentLevel--;
				EditorGUI.indentLevel--;
			}

			EditorGUI.indentLevel--;
		}

		if (GUILayout.Button("Update Axis Names"))
		{
			GetVirtualAxisNames();
		}

		if (GUILayout.Button("Map from Input Manager"))
		{
			MapFromInputManager();
		}

		serializedObject.ApplyModifiedProperties();
	}

	void GetVirtualAxisNames()
	{
		m_VirtualAxisNames.ClearArray();
		m_VirtualAxisNames.InsertArrayElementAtIndex(0);
		m_VirtualAxisNames.GetArrayElementAtIndex(0).stringValue = "None";

		Object[] assets = AssetDatabase.LoadAllAssetsAtPath("ProjectSettings/InputManager.asset");
		if (assets.Length != 0)
		{
			SerializedObject inputManager = new SerializedObject(assets[0]);
			SerializedProperty axes = inputManager.FindProperty("m_Axes");

			for (int i = 0; i < axes.arraySize; i++)
			{
				SerializedProperty axisData = axes.GetArrayElementAtIndex(i);
				VirtualAxisData parsedAxisData = ParseAxisData(axisData);
				m_VirtualAxisNames.InsertArrayElementAtIndex(m_VirtualAxisNames.arraySize);
				m_VirtualAxisNames.GetArrayElementAtIndex(m_VirtualAxisNames.arraySize - 1).stringValue = parsedAxisData.name;
			}
		}
	}

	bool ArraySizeField(SerializedProperty array, string fieldName)
	{
		int size = array.arraySize;
		size = EditorGUILayout.IntField(fieldName, size);

		if (size != array.arraySize)
		{
			while (size > array.arraySize)
			{
				array.InsertArrayElementAtIndex(array.arraySize);
			}
			while (size < array.arraySize)
			{
				array.DeleteArrayElementAtIndex(array.arraySize - 1);
			}
			return true;
		}

		return false;
	}

	void MapFromInputManager()
	{
		GetVirtualAxisNames();

		Object[] assets = AssetDatabase.LoadAllAssetsAtPath("ProjectSettings/InputManager.asset");
		if (assets.Length != 0)
		{
			SerializedObject inputManager = new SerializedObject(assets[0]);
			SerializedProperty axes = inputManager.FindProperty("m_Axes");

			m_GamepadMappings.ClearArray();

			for (int i = 0; i < axes.arraySize; i++)
			{
				SerializedProperty axisData = axes.GetArrayElementAtIndex(i);

				VirtualAxisData parsedAxisData = ParseAxisData(axisData);

				if (!IsGamepad(parsedAxisData))
				{
					continue;
				}

				int gamepadID = GetGamepadId(parsedAxisData);

				int mapIndex = GetGamepadMapIndexById(gamepadID);

				if (mapIndex == -1)
				{
					mapIndex = m_GamepadMappings.arraySize;
					m_GamepadMappings.InsertArrayElementAtIndex(m_GamepadMappings.arraySize);
					SerializedProperty prop = m_GamepadMappings.GetArrayElementAtIndex(mapIndex);
					prop.Next(true);

					do
					{
						if (prop.name == "Id")
						{
							prop.intValue = gamepadID;
						}

						if (prop.isArray)
						{
							prop.ClearArray();
						}
					}
					while (prop.Next(false) && prop.propertyPath.Contains(m_GamepadMappings.propertyPath));
				}

				if (parsedAxisData.type == 0)
				{
					int buttonIndex = GetGamepadButton(parsedAxisData);

					if (buttonIndex == -1)
					{
						Debug.LogError("Failed to map axis " + parsedAxisData.name + "!" +
							"Couldn't get joystick button id.");
						continue;
					}

					SerializedProperty prop = m_GamepadMappings.GetArrayElementAtIndex(mapIndex);
					while (prop.name != "Buttons")
					{
						prop.Next(true);
					}

					while (buttonIndex >= prop.arraySize)
					{
						prop.InsertArrayElementAtIndex(prop.arraySize);
						prop.GetArrayElementAtIndex(prop.arraySize - 1).intValue = 0;
					}

					prop.GetArrayElementAtIndex(buttonIndex).intValue = i + 1;
				}

				if (parsedAxisData.type == 2)
				{
					SerializedProperty prop = m_GamepadMappings.GetArrayElementAtIndex(mapIndex);
					while (prop.name != "Axes")
					{
						prop.Next(true);
					}

					while (parsedAxisData.axis >= prop.arraySize)
					{
						prop.InsertArrayElementAtIndex(prop.arraySize);
						prop.GetArrayElementAtIndex(prop.arraySize - 1).intValue = 0;
					}

					prop.GetArrayElementAtIndex(parsedAxisData.axis).intValue = i + 1;
				}
			}
		}
	}

	VirtualAxisData ParseAxisData(SerializedProperty data)
	{
		VirtualAxisData parsedAxisData = new VirtualAxisData();

		data.Next(true);
		do
		{
			switch (data.name)
			{
			case "m_Name":
				parsedAxisData.name = data.stringValue;
				break;
			case "negativeButton":
				parsedAxisData.negativeButton = data.stringValue;
				break;
			case "positiveButton":
				parsedAxisData.positiveButton = data.stringValue;
				break;
			case "altNegativeButton":
				parsedAxisData.altNegativeButton = data.stringValue;
				break;
			case "altPositiveButton":
				parsedAxisData.altPositiveButton = data.stringValue;
				break;
			case "type":
				parsedAxisData.type = data.intValue;
				break;
			case "axis":
				parsedAxisData.axis = data.intValue;
				break;
			case "joyNum":
				parsedAxisData.joyNum = data.intValue;
				break;
			}
		}
		while (data.Next(false) && data.propertyType != SerializedPropertyType.Generic);

		return parsedAxisData;
	}

	int GetGamepadMapIndexById(int id)
	{
		for (int i = 0; i < m_GamepadMappings.arraySize; i++)
		{
			SerializedProperty prop = m_GamepadMappings.GetArrayElementAtIndex(i);

			while (prop.name != "Id")
			{
				prop.Next(true);
			}

			if (prop.intValue == id)
			{
				return i;
			}
		}

		return -1;
	}

	int GetGamepadId(VirtualAxisData data)
	{
		if (data.type == 0)
		{
			if (data.positiveButton.StartsWith("joystick")
			    && char.IsDigit(data.positiveButton[9]))
			{
				string[] parts = data.positiveButton.Split(new char[] {' '} );

				if (parts.Length > 3)
				{
					return int.Parse(parts[1]);
				}
			}

			if (data.negativeButton.StartsWith("joystick")
			    && char.IsDigit(data.negativeButton[9]))
			{
				string[] parts = data.negativeButton.Split(new char[' ']);

				if (parts.Length > 3)
				{
					return int.Parse(parts[1]);
				}
			}

			if (data.altPositiveButton.StartsWith("joystick")
			    && char.IsDigit(data.altPositiveButton[9]))
			{
				string[] parts = data.altPositiveButton.Split(new char[' ']);

				if (parts.Length > 3)
				{
					return int.Parse(parts[1]);
				}
			}

			if (data.altNegativeButton.StartsWith("joystick")
			    && char.IsDigit(data.altNegativeButton[9]))
			{
				string[] parts = data.altNegativeButton.Split(new char[' ']);

				if (parts.Length > 3)
				{
					return int.Parse(parts[1]);
				}
			}
		}

		if (data.type == 2)
		{
			return data.joyNum;
		}

		return 0;
	}

	bool IsGamepad(VirtualAxisData data)
	{
		if (data.type == 1)
		{
			return false;
		}

		if (data.type == 0)
		{
			return data.positiveButton.StartsWith("joystick") ||
					data.negativeButton.StartsWith("joystick") ||
					data.altPositiveButton.StartsWith("joystick") ||
					data.altNegativeButton.StartsWith("joystick");
		}

		return true;
	}

	int GetGamepadButton(VirtualAxisData data)
	{
		if (data.positiveButton.StartsWith("joystick"))
		{
			return int.Parse(data.positiveButton.Substring(data.positiveButton.LastIndexOf(" ") + 1));
		}

		if (data.negativeButton.StartsWith("joystick"))
		{
			return int.Parse(data.negativeButton.Substring(data.negativeButton.LastIndexOf(" ") + 1));
		}

		if (data.altPositiveButton.StartsWith("joystick"))
		{
			return int.Parse(data.altPositiveButton.Substring(data.altPositiveButton.LastIndexOf(" ") + 1));
		}

		if (data.altNegativeButton.StartsWith("joystick"))
		{
			return int.Parse(data.altNegativeButton.Substring(data.altNegativeButton.LastIndexOf(" ") + 1));
		}

		return -1;
	}
}
using UnityEditor;
using UnityEngine;

using System.IO;
using System.Collections.Generic;

[CustomEditor(typeof(CoherentUIGTLocalization))]
public class CoherentUIGTLocalizationEditor : Editor
{
	private SerializedProperty m_Language;
	private SerializedProperty m_Ids;
	private SerializedProperty m_Translations;
	private bool m_TranslationsUnfolded;
	private GUIStyle m_MinusButtonStyle;

	public void OnEnable()
	{
		m_Language = serializedObject.FindProperty("m_Language");
		m_Ids = serializedObject.FindProperty("m_Ids");
		m_Translations = serializedObject.FindProperty("m_Translations");
		m_MinusButtonStyle = new GUIStyle(EditorGUIUtility.
		                                  GetBuiltinSkin(EditorSkin.Inspector).
		                                  GetStyle("OL Minus"));
	}

	public override void OnInspectorGUI()
	{
		serializedObject.Update();

		List<string> languages = new List<string>();

		foreach (SerializedProperty translation in m_Translations)
		{
			languages.Add(translation.FindPropertyRelative("Language").stringValue);
		}

		if (languages.Count > 0)
		{
			int currentLanguageIndex = languages.IndexOf(m_Language.stringValue);
			if (currentLanguageIndex == -1)
			{
				currentLanguageIndex = 0;
			}
			currentLanguageIndex = EditorGUILayout.Popup("Default Language:",
			                                             currentLanguageIndex,
			                                             languages.ToArray(),
			                                             EditorStyles.popup);
			m_Language.stringValue = languages[currentLanguageIndex];
		}

		EditorGUILayout.Space();

		float marginOffset = EditorStyles.textField.margin.horizontal / 2f;
		float minusIconOffset = 19f;
		float indentOffset = 15f;
		float windowWidth = Screen.width - indentOffset;

		EditorGUILayout.BeginHorizontal();
		EditorGUILayout.LabelField("Translations:",
		                           GUILayout.Width(windowWidth * 0.5f - marginOffset));

		if (GUILayout.Button("Add Language", GUILayout.Width(windowWidth * 0.5f - marginOffset)))
		{
			AddLanguage();
		}
		EditorGUILayout.EndHorizontal();

		float columnWidth = windowWidth / (m_Translations.arraySize + 1f);

		GUILayoutOption cellWidth = GUILayout.Width(columnWidth - marginOffset);
		GUILayoutOption cellWidthMinus = GUILayout.Width(columnWidth - minusIconOffset - marginOffset);

		EditorGUILayout.BeginHorizontal();
		for (int i = 0; i < m_Translations.arraySize; i++)
		{
			if (i == 0)
			{
				EditorGUILayout.LabelField("IDs:", cellWidth);
			}
			if (GUILayout.Button("", m_MinusButtonStyle, GUILayout.Width(15f)))
			{
				RemoveLanguage(i);
				serializedObject.ApplyModifiedProperties();
				return;
			}
			EditorGUILayout.LabelField("Language:", cellWidthMinus);
		}
		EditorGUILayout.EndHorizontal();

		EditorGUILayout.BeginHorizontal();
		for (int i = 0; i < m_Translations.arraySize; i++)
		{
			if (i == 0)
			{
				GUI.enabled = false;
				EditorGUILayout.LabelField("LanguageID",
				                           EditorStyles.textField,
				                           cellWidth);
				GUI.enabled = true;
			}
			SerializedProperty language = m_Translations.GetArrayElementAtIndex(i).
										  FindPropertyRelative("Language");
			language.stringValue = EditorGUILayout.TextField(language.stringValue,
			                                                 cellWidth);
		}
		EditorGUILayout.EndHorizontal();

		if (m_Translations.arraySize > 0)
		{
			for (int i = 0; i < m_Ids.arraySize; i++)
			{
				EditorGUILayout.BeginHorizontal();
				if (GUILayout.Button("", m_MinusButtonStyle, GUILayout.Width(15f)))
				{
					RemoveId(i);
					serializedObject.ApplyModifiedProperties();
					return;
				}

				SerializedProperty id = m_Ids.GetArrayElementAtIndex(i);
				id.stringValue = EditorGUILayout.TextField(id.stringValue,
				                                           cellWidthMinus);
				for (int j = 0; j < m_Translations.arraySize; j++)
				{
					SerializedProperty text = m_Translations.
											  GetArrayElementAtIndex(j).
											  FindPropertyRelative("Texts").
											  GetArrayElementAtIndex(i);
					text.stringValue = EditorGUILayout.TextField(text.stringValue,
					                                             cellWidth);
				}
				EditorGUILayout.EndHorizontal();
			}
		}

		EditorGUILayout.BeginHorizontal();
		for (int i = 0; i < m_Translations.arraySize; i++)
		{
			if (i == 0)
			{
				if (GUILayout.Button("Add text", cellWidth))
				{
					AddId();
				}
			}
			EditorGUILayout.BeginVertical();
			if (GUILayout.Button("Import", cellWidth))
			{
				ImportFromCSV(i);
			}
			if (GUILayout.Button("Export", cellWidth))
			{
				ExportToCSV(i);
			}
			EditorGUILayout.EndVertical();
		}
		EditorGUILayout.EndHorizontal();

		serializedObject.ApplyModifiedProperties();
	}

	void AddLanguage()
	{
		m_Translations.InsertArrayElementAtIndex(m_Translations.arraySize);
		SerializedProperty translation = GetLastArrayElement(m_Translations);
		translation.FindPropertyRelative("Language").stringValue = "";

		SerializedProperty texts = translation.FindPropertyRelative("Texts");
		texts.ClearArray();

		for (int i = 0; i < m_Ids.arraySize; i++)
		{
			texts.InsertArrayElementAtIndex(texts.arraySize);
			GetLastArrayElement(texts).stringValue = "";
		}
	}

	void RemoveLanguage(int index)
	{
		m_Translations.DeleteArrayElementAtIndex(index);
	}

	void AddId()
	{
		m_Ids.InsertArrayElementAtIndex(m_Ids.arraySize);
		GetLastArrayElement(m_Ids).stringValue = "";

		for (int i = 0; i < m_Translations.arraySize; i++)
		{
			SerializedProperty translation = m_Translations.GetArrayElementAtIndex(i);
			SerializedProperty texts = translation.FindPropertyRelative("Texts");
			texts.InsertArrayElementAtIndex(texts.arraySize);
			GetLastArrayElement(texts).stringValue = "";
		}
	}

	void RemoveId(int index)
	{
		m_Ids.DeleteArrayElementAtIndex(index);

		for (int i = 0; i < m_Translations.arraySize; i++)
		{
			SerializedProperty translation = m_Translations.GetArrayElementAtIndex(i);
			SerializedProperty texts = translation.FindPropertyRelative("Texts");
			texts.DeleteArrayElementAtIndex(index);
		}
	}

	void ImportFromCSV(int index)
	{
		string path = EditorUtility.OpenFilePanel("Import translation from CSV", "", "csv");

		if (string.IsNullOrEmpty(path))
		{
			return;
		}

		SerializedProperty translation = m_Translations.GetArrayElementAtIndex(index);
		string filename = Path.GetFileNameWithoutExtension(path);
		translation.FindPropertyRelative("Language").stringValue = filename;
		SerializedProperty texts = translation.FindPropertyRelative("Texts");
		string[] lines = File.ReadAllLines(path);

		for (int i = 0; i < lines.Length; i++)
		{
			string[] idTextPair = ParseCSVLine(lines[i]);

			if (idTextPair.Length != 2)
			{
				Debug.LogWarning("[Coherent GT] Importing line " + i +
					" from file " + filename + ".csv failed! " +
					"Format not supported. Expected format " +
					"\"id,text\".");
				continue;
			}

			bool foundId = false;

			for (int j = 0; j < m_Ids.arraySize; j++)
			{
				if (m_Ids.GetArrayElementAtIndex(j).stringValue == idTextPair[0])
				{
					texts.GetArrayElementAtIndex(j).stringValue = idTextPair[1];
					foundId = true;
					break;
				}
			}

			if (!foundId)
			{
				AddId();
				GetLastArrayElement(m_Ids).stringValue = idTextPair[0];
				GetLastArrayElement(texts).stringValue = idTextPair[1];
			}
		}
	}

	string[] ParseCSVLine(string line)
	{
		if (line.Contains("\""))
		{
			return ParseQuotedCSVLine(line);
		}

		string[] idTextPair = line.Split(new char[] { ',' });

		if (idTextPair.Length != 2)
		{
			return null;
		}

		idTextPair[0] = idTextPair[0].Trim();
		idTextPair[1] = idTextPair[1].Trim();

		return idTextPair;
	}

	string[] ParseQuotedCSVLine(string line)
	{
		line = line.Trim();
		if (line.StartsWith("\""))
		{
			int endOfIdIndex = -1;
			for (int i = 1; i < line.Length; i++)
			{
				if (line[i] == '"')
				{
					if (line[i + 1] != '"')
					{
						endOfIdIndex = i;
						break;
					}

					i++;
				}
			}

			string id = line.Substring(1, endOfIdIndex - 1);
			id = id.Replace("\"\"", "\"");

			string text = line.Substring(endOfIdIndex + 2);

			text = text.Trim();

			if (line.EndsWith("\""))
			{
				text = text.Substring(1, text.Length - 2);
			}
			else
			{
				text = text.Trim();
			}

			return new string[] { id, text };
		}
		else
		{
			int delimiterIndex = line.IndexOf(",");
			string id = line.Substring(0, delimiterIndex);
			string text = line.Substring(delimiterIndex + 1).Trim();
			text = text.Substring(1, text.Length - 2);
			text = text.Replace("\"\"", "\"");
			return new string[] { id, text};
		}
	}

	void ExportToCSV(int index)
	{
		SerializedProperty translation = m_Translations.GetArrayElementAtIndex(index);
		string language = translation.FindPropertyRelative("Language").stringValue;
		string path = EditorUtility.SaveFilePanel("Export translation to CSV",
		                                          "",
		                                          language,
		                                          "csv");

		if (string.IsNullOrEmpty(path))
		{
			return;
		}

		SerializedProperty texts = translation.FindPropertyRelative("Texts");

		string csvContent = "";

		for (int j = 0; j < m_Ids.arraySize; j++)
		{
			string id = m_Ids.GetArrayElementAtIndex(j).stringValue;
			if (id.IndexOfAny(new char[] {',', '"'}) != -1)
			{
				id = id.Replace("\"", "\"\"");
				id = "\"" + id + "\"";
			}

			string text = texts.GetArrayElementAtIndex(j).stringValue;
			if (text.IndexOfAny(new char[] {',', '"'}) != -1)
			{
				text = text.Replace("\"", "\"\"");
				text = "\"" + text + "\"";
			}

			csvContent += id + ", " + text + System.Environment.NewLine;
		}

		File.WriteAllText(path, csvContent);
	}

	SerializedProperty GetLastArrayElement(SerializedProperty array)
	{
		return array.GetArrayElementAtIndex(array.arraySize - 1);
	}
}
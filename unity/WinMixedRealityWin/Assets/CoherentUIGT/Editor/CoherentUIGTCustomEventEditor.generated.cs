// This file is auto-generated. Do not edit.

using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using Coherent.UIGT;

public partial class CoherentUIGTCustomEventEditor
{
	SerializedProperty m_CustomEventNamesProperty;
	SerializedProperty m_CustomEventsProperty;

	public void OnEnableGenerated()
	{
		(target as CoherentUIGTCustomEvent).m_CreatedEventTypes =
			new List<CustomEventType>(new CustomEventType[] {
				new CustomEventType(CustomEventValueType.Null,
									CustomEventValueType.Null,
									CustomEventValueType.Null,
									CustomEventValueType.Null),
		});

		m_CustomEventNamesProperty = serializedObject.FindProperty("m_CustomEventNames");
		m_CustomEventsProperty = serializedObject.FindProperty("m_CustomEvents");
	}

	public void OnInspectorGUIGenerated()
	{
		serializedObject.Update();

		for (int i = 0; i < m_CustomEventNamesProperty.arraySize; i++)
		{
			SerializedProperty eventName = m_CustomEventNamesProperty.GetArrayElementAtIndex(i);

			EditorGUILayout.PropertyField(eventName, m_EventNameContent);
			EditorGUILayout.PropertyField(m_CustomEventsProperty.GetArrayElementAtIndex(i),
										  new GUIContent(eventName.stringValue));

			Rect lastRect = GUILayoutUtility.GetLastRect();
			Rect rect = new Rect(lastRect.xMax - m_ContentSize.x, lastRect.y + 1f, m_ContentSize.x, m_ContentSize.y);

			if (GUI.Button(rect, m_IconToolbarMinus, GUIStyle.none))
			{
				m_CustomEventsProperty.DeleteArrayElementAtIndex(i);
				m_CustomEventNamesProperty.DeleteArrayElementAtIndex(i);
				i--;
			}
		}

		serializedObject.ApplyModifiedProperties();
	}

	void OnAddNewSelected(object index)
	{
		serializedObject.Update();
		switch ((int)index)
		{
		case 0:
			m_CustomEventNamesProperty.arraySize++;
			m_CustomEventsProperty.arraySize++;
			break;
		}
		serializedObject.ApplyModifiedProperties();
	}
}


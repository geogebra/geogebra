using UnityEditor;
using UnityEngine;

[CustomEditor(typeof(CoherentUIGTViewEvents))]
public class CoherentUIGTViewEventsEditor : Editor
{
	readonly string[] m_EventNames = new string[] {
		"OnAudioDataReceived",
		"OnAudioStreamClosed",
		"OnAudioStreamCreated",
		"OnAudioStreamEnded",
		"OnAudioStreamPause",
		"OnAudioStreamPlay",
		"OnBindingsReleased",
		"OnCallback",
		"OnCaretRectChanged",
		"OnCursorChanged",
		"OnFailLoad",
		"OnFinishLoad",
		"OnIMEShouldCancelComposition",
		"OnNavigateTo",
		"OnPerformanceAudit",
		"OnReadyForBindings",
		"OnStartLoading",
		"OnTextInputTypeChanged",
		"OnViewCreated",
		"OnViewDestroyed"
	};
	SerializedProperty m_TargetViewProperty;
	SerializedProperty[] m_EventsProperties;
	bool[] m_IsEventAdded;
	GUIContent m_IconToolbarMinus;
	GUIContent[] m_EventTypes;
	GUIContent m_AddButonContent;

	protected virtual void OnEnable()
	{
		m_TargetViewProperty = serializedObject.FindProperty("m_TargetView");
		m_EventsProperties = new SerializedProperty[m_EventNames.Length];

		for (int i = 0; i < m_EventsProperties.Length; i++)
		{
			m_EventsProperties[i] = serializedObject.FindProperty(m_EventNames[i]);
		}

		m_IsEventAdded = new bool[m_EventNames.Length];

		m_AddButonContent = new GUIContent("Add New Event Type");
		m_IconToolbarMinus = new GUIContent(EditorGUIUtility.IconContent("Toolbar Minus"));
		m_IconToolbarMinus.tooltip = "Remove event.";
		m_EventTypes = new GUIContent[m_EventNames.Length];

		for (int i = 0; i < m_EventNames.Length; i++)
		{
			string displayName = m_EventNames[i];
			displayName = char.ToUpper(displayName[0]) + displayName.Substring(1);
			m_EventTypes[i] = new GUIContent(displayName);
		}
	}

	public override void OnInspectorGUI()
	{
		serializedObject.Update();
		int toBeRemovedEntry = -1;

		EditorGUILayout.PropertyField(m_TargetViewProperty);
		EditorGUILayout.Space();
		EditorGUILayout.Space();
		Vector2 vector2 = GUIStyle.none.CalcSize(m_IconToolbarMinus);

		for (int i = 0; i < m_EventsProperties.Length; i++)
		{
			if (!m_IsEventAdded[i] && !IsEventInUse(i))
			{
				continue;
			}

			EditorGUILayout.PropertyField(m_EventsProperties[i], m_EventTypes[i], new GUILayoutOption[0]);

			Rect lastRect = GUILayoutUtility.GetLastRect();
			Rect rect = new Rect(lastRect.xMax - vector2.x - 8.0f, lastRect.y + 1f, vector2.x, vector2.y);

			if (GUI.Button(rect, m_IconToolbarMinus, GUIStyle.none))
			{
				toBeRemovedEntry = i;
			}

			EditorGUILayout.Space();
		}

		if (toBeRemovedEntry > -1)
		{
			RemoveEntry(toBeRemovedEntry);
		}

		Rect rect1 = GUILayoutUtility.GetRect(m_AddButonContent, GUI.skin.button);
		rect1.x = rect1.x + ((rect1.width - 200.0f) / 2.0f);
		rect1.width = 200f;

		if (GUI.Button(rect1, m_AddButonContent))
		{
			ShowAddEventMenu();
		}

		serializedObject.ApplyModifiedProperties();
	}

	void RemoveEntry(int index)
	{
		m_IsEventAdded[index] = false;

		switch (index)
		{
		case 0:
			(target as CoherentUIGTViewEvents).OnAudioDataReceived = new CoherentUIGTViewEvents.OnAudioDataReceivedEvent();
			break;
		case 1:
			(target as CoherentUIGTViewEvents).OnAudioStreamClosed = new CoherentUIGTViewEvents.OnAudioStreamClosedEvent();
			break;
		case 2:
			(target as CoherentUIGTViewEvents).OnAudioStreamCreated = new CoherentUIGTViewEvents.OnAudioStreamCreatedEvent();
			break;
		case 3:
			(target as CoherentUIGTViewEvents).OnAudioStreamEnded = new CoherentUIGTViewEvents.OnAudioStreamEndedEvent();
			break;
		case 4:
			(target as CoherentUIGTViewEvents).OnAudioStreamPause = new CoherentUIGTViewEvents.OnAudioStreamPauseEvent();
			break;
		case 5:
			(target as CoherentUIGTViewEvents).OnAudioStreamPlay = new CoherentUIGTViewEvents.OnAudioStreamPlayEvent();
			break;
		case 6:
			(target as CoherentUIGTViewEvents).OnBindingsReleased = new CoherentUIGTViewEvents.OnBindingsReleasedEvent();
			break;
		case 7:
			(target as CoherentUIGTViewEvents).OnCallback = new CoherentUIGTViewEvents.OnCallbackEvent();
			break;
		case 8:
			(target as CoherentUIGTViewEvents).OnCaretRectChanged = new CoherentUIGTViewEvents.OnCaretRectChangedEvent();
			break;
		case 9:
			(target as CoherentUIGTViewEvents).OnCursorChanged = new CoherentUIGTViewEvents.OnCursorChangedEvent();
			break;
		case 10:
			(target as CoherentUIGTViewEvents).OnFailLoad = new CoherentUIGTViewEvents.OnFailLoadEvent();
			break;
		case 11:
			(target as CoherentUIGTViewEvents).OnFinishLoad = new CoherentUIGTViewEvents.OnFinishLoadEvent();
			break;
		case 12:
			(target as CoherentUIGTViewEvents).OnIMEShouldCancelComposition = new CoherentUIGTViewEvents.OnIMEShouldCancelCompositionEvent();
			break;
		case 13:
			(target as CoherentUIGTViewEvents).OnNavigateTo = new CoherentUIGTViewEvents.OnNavigateToEvent();
			break;
		case 14:
			(target as CoherentUIGTViewEvents).OnPerformanceAudit = new CoherentUIGTViewEvents.OnPerformanceAuditEvent();
			break;
		case 15:
			(target as CoherentUIGTViewEvents).OnReadyForBindings = new CoherentUIGTViewEvents.OnReadyForBindingsEvent();
			break;
		case 16:
			(target as CoherentUIGTViewEvents).OnStartLoading = new CoherentUIGTViewEvents.OnStartLoadingEvent();
			break;
		case 17:
			(target as CoherentUIGTViewEvents).OnTextInputTypeChanged = new CoherentUIGTViewEvents.OnTextInputTypeChangedEvent();
			break;
		case 18:
			(target as CoherentUIGTViewEvents).OnViewCreated = new CoherentUIGTViewEvents.OnViewCreatedEvent();
			break;
		case 19:
			(target as CoherentUIGTViewEvents).OnViewDestroyed = new CoherentUIGTViewEvents.OnViewDestroyedEvent();
			break;
		default:
			break;
		}
	}

	bool IsEventInUse(int index)
	{
		switch (index)
		{
		case 0:
			return (target as CoherentUIGTViewEvents).OnAudioDataReceived.GetPersistentEventCount() > 0;
		case 1:
			return (target as CoherentUIGTViewEvents).OnAudioStreamClosed.GetPersistentEventCount() > 0;
		case 2:
			return (target as CoherentUIGTViewEvents).OnAudioStreamCreated.GetPersistentEventCount() > 0;
		case 3:
			return (target as CoherentUIGTViewEvents).OnAudioStreamEnded.GetPersistentEventCount() > 0;
		case 4:
			return (target as CoherentUIGTViewEvents).OnAudioStreamPause.GetPersistentEventCount() > 0;
		case 5:
			return (target as CoherentUIGTViewEvents).OnAudioStreamPlay.GetPersistentEventCount() > 0;
		case 6:
			return (target as CoherentUIGTViewEvents).OnBindingsReleased.GetPersistentEventCount() > 0;
		case 7:
			return (target as CoherentUIGTViewEvents).OnCallback.GetPersistentEventCount() > 0;
		case 8:
			return (target as CoherentUIGTViewEvents).OnCaretRectChanged.GetPersistentEventCount() > 0;
		case 9:
			return (target as CoherentUIGTViewEvents).OnCursorChanged.GetPersistentEventCount() > 0;
		case 10:
			return (target as CoherentUIGTViewEvents).OnFailLoad.GetPersistentEventCount() > 0;
		case 11:
			return (target as CoherentUIGTViewEvents).OnFinishLoad.GetPersistentEventCount() > 0;
		case 12:
			return (target as CoherentUIGTViewEvents).OnIMEShouldCancelComposition.GetPersistentEventCount() > 0;
		case 13:
			return (target as CoherentUIGTViewEvents).OnNavigateTo.GetPersistentEventCount() > 0;
		case 14:
			return (target as CoherentUIGTViewEvents).OnPerformanceAudit.GetPersistentEventCount() > 0;
		case 15:
			return (target as CoherentUIGTViewEvents).OnReadyForBindings.GetPersistentEventCount() > 0;
		case 16:
			return (target as CoherentUIGTViewEvents).OnStartLoading.GetPersistentEventCount() > 0;
		case 17:
			return (target as CoherentUIGTViewEvents).OnTextInputTypeChanged.GetPersistentEventCount() > 0;
		case 18:
			return (target as CoherentUIGTViewEvents).OnViewCreated.GetPersistentEventCount() > 0;
		case 19:
			return (target as CoherentUIGTViewEvents).OnViewDestroyed.GetPersistentEventCount() > 0;
		default:
			return false;
		}
	}

	void ShowAddEventMenu()
	{
		GenericMenu menu = new GenericMenu();

		for (int i = 0; i < m_EventTypes.Length; i++)
		{
			if (!m_IsEventAdded[i] && !IsEventInUse(i))
			{
				menu.AddItem(m_EventTypes[i], false, OnAddNewSelected, i);
			}
			else
			{
				menu.AddDisabledItem(m_EventTypes[i]);
			}
		}

		menu.ShowAsContext();

		Event.current.Use();
	}

	void OnAddNewSelected(object index)
	{
		m_IsEventAdded[(int)index] = true;
	}
}

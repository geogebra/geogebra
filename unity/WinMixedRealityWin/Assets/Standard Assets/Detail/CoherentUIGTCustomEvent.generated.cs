// This file is auto-generated. Do not edit.

using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Events;

public partial class CoherentUIGTCustomEvent
{
	[Serializable] public class CustomEvent : UnityEvent { }

	public List<string> m_CustomEventNames;
	public List<CustomEvent> m_CustomEvents;

	void OnReadyForBindings()
	{
		for (int i = 0; i < m_CustomEventNames.Count; i++)
		{
			int indexCopy = i;
			m_TargetView.View.RegisterForEvent(m_CustomEventNames[indexCopy],
			(Action)delegate() { m_CustomEvents[indexCopy].Invoke(); });
		}
	}
}


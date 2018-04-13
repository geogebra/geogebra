using UnityEngine;
using System.Collections.Generic;

using Coherent.UIGT;

public partial class CoherentUIGTCustomEvent : MonoBehaviour
{
	public CoherentUIGTView m_TargetView;
	public List<CustomEventType> m_CreatedEventTypes;

	void Start()
	{
		if (m_TargetView == null)
		{
			m_TargetView = GetComponent<CoherentUIGTView>();

			if (m_TargetView == null)
			{
				Debug.LogWarning("[Coherent GT] CustomEvents : Target view " +
								 "component is not attached. Events won't " +
								 "be triggered.");
				return;
			}
		}

		m_TargetView.Listener.ReadyForBindings += OnReadyForBindings;
	}
}

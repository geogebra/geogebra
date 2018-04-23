using UnityEngine;
using System;
using System.Collections;

public class LocalizedMenuGT : MonoBehaviour
{
	private CoherentUIGTSystem m_System;
	private CoherentUIGTView m_View;

	void Awake()
	{
		m_System = CoherentUIGTSystem.CurrentUISystem;
	}

	void Start()
	{
		m_View = GetComponent<CoherentUIGTView>();
		m_View.Listener.ReadyForBindings += HandleReadyForBindings;
	}

	void HandleReadyForBindings ()
	{
		m_View.View.BindCall("Play", (Action)Play);
		m_View.View.BindCall("Quit", (Action)Quit);
		m_View.View.BindCall("ChangeLanguage", (Action<string>)ChangeLanguage);
	}

	void Play()
	{
		Debug.Log("Play pressed");
	}

	void Quit()
	{
		Debug.Log("Quit pressed");

		#if UNITY_EDITOR
		UnityEditor.EditorApplication.isPlaying = false;
		#endif

		Application.Quit();
	}

	void ChangeLanguage(string language)
	{
		if (m_System.LocalizationEnabled)
		{
			m_System.LocalizationManager.ChangeLanguage(language);
		}
		else
		{
			m_View.View.SetScriptError(Coherent.UIGT.ScriptCallErrorType.SCE_NoResult, "Localization Disabled");
		}
	}
}

using System;
using UnityEngine;

public class VideoSampleScriptGT : MonoBehaviour
{
	private CoherentUIGTView m_View;

	void Start()
	{
		m_View = GetComponent<CoherentUIGTView>();
		m_View.Listener.ReadyForBindings += HandleReadyForBindings;
	}

	void HandleReadyForBindings ()
	{
		m_View.View.RegisterForEvent("SetVolume", (Action<float>)SetVolume);
		m_View.View.RegisterForEvent("Quit", (Action)Quit);
		m_View.View.RegisterForEvent("ToggleMaximizeWindow", (Action)ToggleMaximizeWindow);
	}

	void SetVolume(float volume)
	{
		if (m_View.AudioSourceComponent != null)
		{
			m_View.AudioSourceComponent.volume = volume;
		}
	}

	void Quit()
	{
		Debug.Log("Quit pressed");

		#if UNITY_EDITOR
		UnityEditor.EditorApplication.isPlaying = false;
		#endif

		Application.Quit();
	}

	void ToggleMaximizeWindow()
	{
		Screen.fullScreen = !Screen.fullScreen;
	}
}

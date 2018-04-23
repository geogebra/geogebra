using UnityEngine;
using System;
using System.Collections;
using Coherent.UIGT;

public class ManualBindingGT : MonoBehaviour
{
	private CoherentUIGTView m_View;
	private GameOptionsGT m_GameOptions;

	// Use this for initialization
	void Start () {
		m_View = GetComponent<CoherentUIGTView>();
		m_View.Listener.ReadyForBindings += HandleReadyForBindings;

		m_GameOptions = new GameOptionsGT {
			Backend = "Unity3D",
			Width = 1024,
			Height = 768,
			NetPort = 17777,
		};
	}

	void HandleReadyForBindings ()
	{
		// bind ApplyOptions to "ApplyOptions" in JavaScript
		m_View.View.BindCall("ApplyOptions", (Action<GameOptionsGT>)this.ApplyOptions);
		m_View.View.BindCall("GetLatency", (Func<int>)this.GetNetworkLatency);
		m_View.View.BindCall("GetGameTime", (Func<int>)this.GetGameTime);

		// triggered by the view when it has loaded
		m_View.View.RegisterForEvent("ViewReady", (Action)this.ViewReady);
	}

	public void ApplyOptions(GameOptionsGT options)
	{
		m_View.View.TriggerEvent("gameConsole:Trace", options);

		Screen.SetResolution(
			(int)m_GameOptions.Width,
			(int)m_GameOptions.Height,
			Screen.fullScreen);
	}

	public int GetNetworkLatency()
	{
		// not actual latency :)
		return (int)UnityEngine.Random.Range(0, 1000);
	}

	public int GetGameTime()
	{
		return (int)Time.time;
	}

	public void ViewReady()
	{
		// show the options
		m_View.View.TriggerEvent("OpenOptions", m_GameOptions);
	}
}

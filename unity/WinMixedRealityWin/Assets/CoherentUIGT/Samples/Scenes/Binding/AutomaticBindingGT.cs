using UnityEngine;
using System.Collections;
using Coherent.UIGT;

public class AutomaticBindingGT : MonoBehaviour
{
	private CoherentUIGTView m_View;
	private GameOptionsGT m_GameOptions;

	// Use this for initialization
	void Start () {
		m_View = GetComponent<CoherentUIGTView>();

		m_GameOptions = new GameOptionsGT {
			Backend = "Unity3D",
			Width = 1024,
			Height = 768,
			NetPort = 17777,
		};
	}

	[Coherent.UIGT.CoherentUIGTMethod("ApplyOptions", false)]
	public void ApplyOptions(GameOptionsGT options)
	{
		m_View.View.TriggerEvent("gameConsole:Trace", options);

		Screen.SetResolution(
			(int)m_GameOptions.Width,
			(int)m_GameOptions.Height,
			Screen.fullScreen);
	}

	// By default, the second argument of CoherentMethod is false
	[Coherent.UIGT.CoherentUIGTMethod("GetLatency")]
	public int GetNetworkLatency()
	{
		// not actual latency :)
		return (int)UnityEngine.Random.Range(0, 1000);
	}

	[Coherent.UIGT.CoherentUIGTMethod("GetGameTime")]
	public int GetGameTime()
	{
		return (int)Time.time;
	}

	[Coherent.UIGT.CoherentUIGTMethod("ViewReady", true)]
	public void ViewReady()
	{
		// show the options
		m_View.View.TriggerEvent("OpenOptions", m_GameOptions);
	}
}

using UnityEngine;
using System;
using System.Collections.Generic;

using Coherent.UIGT;

public class CoherentUIGTGamepad : MonoBehaviour
{
	[Serializable]
	public struct GamepadMap
	{
		public int Id;
		public List<int> Buttons;
		public List<int> Axes;
	}

	public List<GamepadMap> m_GamepadMappings = new List<GamepadMap>();
	public List<string> m_VirtualAxisNames = new List<string>();
	private GamepadState[] m_GamepadStates = new GamepadState[0];

	void Start()
	{
		CoherentUIGTSystem.CurrentUISystem.SystemReady += RegisterGamepads;
		CoherentUIGTSystem.CurrentUISystem.UISystemDestroying += UnregisterGamepads;
	}

	void Update()
	{
		for (int i = 0; i < m_GamepadStates.Length; i++)
		{
			for (int j = 0; j < m_GamepadMappings[i].Buttons.Count; j++)
			{
				m_GamepadStates[i].ButtonValues[j] = m_GamepadMappings[i].Buttons[j] != 0 ? Input.GetAxisRaw(m_VirtualAxisNames[m_GamepadMappings[i].Buttons[j]]) : 0f;
			}

			for (int j = 0; j < m_GamepadMappings[i].Axes.Count; j++)
			{
				m_GamepadStates[i].AxisValues[j] = m_GamepadMappings[i].Axes[j] != 0 ? Input.GetAxisRaw(m_VirtualAxisNames[m_GamepadMappings[i].Axes[j]]) : 0f;
			}

			CoherentUIGTSystem.CurrentUISystem.UISystem.UpdateGamepadState(m_GamepadStates[i]);
		}
	}

	void RegisterGamepads()
	{
		m_GamepadStates = new GamepadState[m_GamepadMappings.Count];

		for (int i = 0; i < m_GamepadMappings.Count; i++)
		{
			CoherentUIGTSystem.CurrentUISystem.UISystem.RegisterGamepad((uint)i, "Gamepad " + i,
			                                                            (uint)m_GamepadMappings[i].Axes.Count,
			                                                            (uint)m_GamepadMappings[i].Buttons.Count);

			m_GamepadStates[i].Id = (uint)m_GamepadMappings[i].Id;
			m_GamepadStates[i].ButtonValues = new float[m_GamepadMappings[i].Buttons.Count];
			m_GamepadStates[i].AxisValues = new float[m_GamepadMappings[i].Axes.Count];
		}
	}

	void UnregisterGamepads()
	{
		for (int i = 0; i < m_GamepadMappings.Count; i++)
		{
			CoherentUIGTSystem.CurrentUISystem.UISystem.UnregisterGamepad((uint)i);
		}
	}
}

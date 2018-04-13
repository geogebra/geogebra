using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

[AddComponentMenu("Coherent GT/Coherent GT Localization")]
public class CoherentUIGTLocalization : MonoBehaviour
{
	[Serializable]
	public struct Translation
	{
		public string Language;
		public List<string> Texts;
	}

	public string m_Language;
	public List<string> m_Ids;
	public List<Translation> m_Translations;
	private CoherentUIGTSystem m_System;
	private int m_CurrentTranslationIndex;

	void Awake()
	{
		m_System = CoherentUIGTSystem.CurrentUISystem;
	}

	void Start()
	{
		if (m_System.LocalizationEnabled)
		{
			m_System.LocalizationManager.OnChangeLanguage += OnChangeLanguage;
			m_System.LocalizationManager.OnTranslate += OnTranslate;
		}
	}

	void OnDestroy()
	{
		if (m_System != null && m_System.LocalizationManager != null)
		{
			m_System.LocalizationManager.OnChangeLanguage -= OnChangeLanguage;
			m_System.LocalizationManager.OnTranslate -= OnTranslate;
		}
	}

	void OnChangeLanguage(string language)
	{
		for (int i = 0; i < m_Translations.Count; i++)
		{
			if (m_Translations[i].Language == language)
			{
				m_Language = language;
				m_CurrentTranslationIndex = i;
				return;
			}
		}

		Debug.LogWarning("[Coherent GT] Changing language failed. " +
						 "Translations for language \"" + language +
						 "\" not found.");
	}

	void OnTranslate(string input, out string output)
	{
		output = input;

		int index = m_Ids.IndexOf(input);

		if (index == -1)
		{
			Debug.LogWarning("[Coherent GT] Missing localization Id \"" +
							 input + "\"!");
			return;
		}

		if (m_Translations[m_CurrentTranslationIndex].Language != m_Language)
		{
			for (int i = 0; i < m_Translations.Count; i++)
			{
				if (m_Translations[i].Language == m_Language)
				{
					m_CurrentTranslationIndex = i;
				}
			}

			if (m_Translations[m_CurrentTranslationIndex].Language != m_Language)
			{
				Debug.LogWarning("[Coherent GT] Translation of \"" + input + "\" " +
				                 "failed. Language \"" + m_Language + "\" not found.");
				return;
			}
		}

		output = m_Translations[m_CurrentTranslationIndex].Texts[index];
	}
}

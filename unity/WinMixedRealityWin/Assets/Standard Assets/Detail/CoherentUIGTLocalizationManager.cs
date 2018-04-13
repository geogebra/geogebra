using UnityEngine;
using System.Collections;

namespace Coherent.UIGT
{
public class CoherentUIGTLocalizationManager : LocalizationManager
{
	public delegate void ChangeLanguageFunc(string language);

	public event ChangeLanguageFunc OnChangeLanguage;

	public void ChangeLanguage(string language)
	{
		if (OnChangeLanguage != null)
		{
			OnChangeLanguage(language);
		}
	}

	public delegate void TranslateFunc(string input, out string output);

	public event TranslateFunc OnTranslate;

	public override string Translate(string text)
	{
		string output = text;

		if (OnTranslate != null)
		{
			OnTranslate(text, out output);
		}

		return output;
	}
}
}
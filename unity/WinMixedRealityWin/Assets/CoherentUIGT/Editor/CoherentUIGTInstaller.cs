using UnityEngine;
using UnityEditor;
using System.IO;

[InitializeOnLoad]
public class CoherentUIGTInstaller
{
	static CoherentUIGTInstaller()
	{
		string uiResources = PlayerPrefs.GetString("CoherentGT:UIResources");
		if (!string.IsNullOrEmpty(uiResources))
		{
			string absolutePath = Path.Combine(Application.dataPath, uiResources);
			if (!Directory.Exists(absolutePath))
			{
				Debug.LogError("The UI Resources directory \"" + absolutePath + "\" does not exist! " +
				               "Please reselect the UI Resources folder using " +
				               "Coherent GT->Setup->Select UI Resources Folder entry.");
			}
		}
		else
		{
			string defaultUIPath = "WebPlayerTemplates/uiresources";
			PlayerPrefs.SetString("CoherentGT:UIResources", defaultUIPath);
			Debug.Log("Setting default UI resource path to: " + defaultUIPath);
		}
	}
}
using System.IO;
using UnityEngine;
using UnityEditor;
using Process = System.Diagnostics.Process;
using ProcessStartInfo = System.Diagnostics.ProcessStartInfo;
using Uri = System.Uri;

public static partial class CoherentUIGTEditorMenu
{
	public static void LaunchCoherentEditorWithURL(string url)
	{
		string editorPath = Application.dataPath +
			"/CoherentUIGT/Editor/CoherentEditor";
		string editorResourcesPath = Application.dataPath +
			"/WebPlayerTemplates/editor";
		string uiresourcesPath = Application.dataPath + "/" +
			PlayerPrefs.GetString("CoherentGT:UIResources");

		if (Application.platform == RuntimePlatform.OSXEditor)
		{
			editorPath += "/CoherentEditor.app/Contents/MacOS/CoherentEditor";
		}
		else if (Application.platform == RuntimePlatform.WindowsEditor)
		{
			editorPath += "/CoherentEditor.exe";
		}
		else
		{
			Debug.LogError("Platform not supported.");
			return;
		}

		string editorResourcesArg = " --editor_resources=\"" + editorResourcesPath + "\"";
		string uiResourcesArg = " --uiresources=\"" + uiresourcesPath + "\"";
		string urlArg = !string.IsNullOrEmpty(url) ? " --url=\"" + url + "\"" : "";

		ProcessStartInfo startInfo = new ProcessStartInfo(editorPath);
		startInfo.WorkingDirectory = editorPath.Remove(editorPath.LastIndexOf('/'));
		startInfo.Arguments = editorResourcesArg + uiResourcesArg + urlArg;
		Process.Start(startInfo);
	}

	[MenuItem("Coherent GT/Setup/Select UI Resources folder")]
	static void SelectUIResourcesFolder()
	{
		string defaultFolder = new DirectoryInfo(Application.dataPath).Parent.FullName;
		string folder = EditorUtility.OpenFolderPanel("Select UI resources folder", defaultFolder, "");

		if (string.IsNullOrEmpty(folder))
		{
			return;
		}

		Uri folderAsUri = new Uri(folder);
		Uri workPathAsUri = new Uri(Application.dataPath + "/.");
		Uri relativePath = workPathAsUri.MakeRelativeUri(folderAsUri);

		PlayerPrefs.SetString("CoherentGT:UIResources", relativePath.ToString());
	}

	[MenuItem("Coherent GT/Launch Coherent Editor", false, 0)]
	static void LaunchCoherentEditor()
	{
		LaunchCoherentEditorWithURL(null);
	}

	[MenuItem("Coherent GT/Open Editor Documentation", false, 201)]
	static void CoherentEditorDocumentation()
	{
		Application.OpenURL("https://coherent-labs.com/editor/documentation/");
	}

	[MenuItem("Assets/Open in Coherent Editor", true)]
	static bool EditInCoherentEditorValidation()
	{
		string assetPath = AssetDatabase.GetAssetPath(Selection.activeInstanceID);
		string uiresources = PlayerPrefs.GetString("CoherentGT:UIResources");

		if (assetPath.Contains(uiresources))
		{
			if (assetPath.EndsWith(".html") || assetPath.EndsWith(".css") || assetPath.EndsWith(".js"))
			{
				return true;
			}
		}

		return false;
	}

	[MenuItem("Assets/Open in Coherent Editor", false, 0)]
	static void EditAssetInCoherentEditor()
	{
		string assetPath = AssetDatabase.GetAssetPath(Selection.activeInstanceID);
		string uiresources = PlayerPrefs.GetString("CoherentGT:UIResources");
		string relativePath = assetPath.Substring(assetPath.IndexOf(uiresources) + uiresources.Length);
		string url = "coui://uiresources" + relativePath;
		LaunchCoherentEditorWithURL(url);
	}
}

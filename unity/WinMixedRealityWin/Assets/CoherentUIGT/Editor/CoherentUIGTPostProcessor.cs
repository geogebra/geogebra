using UnityEngine;
using System.IO;
using UnityEditor;
using UnityEditor.Callbacks;

public class CoherentUIGTPostProcessor
{
	[PostProcessBuild]
	public static void OnPostprocessBuild(BuildTarget target, string pathToBuiltProject)
	{
		if (!IsTargetPlatformSupported(target))
		{
			Debug.LogError("[Coherent GT] Trying to build Coherent GT for Unsupported target (" + target + ")!");
			return;
		}

		string outDir = Path.GetDirectoryName(pathToBuiltProject);
		string projName = Path.GetFileNameWithoutExtension(pathToBuiltProject);

		CopyUIResources(target, projName, outDir);

		if (Debug.isDebugBuild)
		{
			CopyInspectorResources(target, projName, outDir);
		}
		else
		{
			RemoveDevelopmentModule(target, projName, outDir);
		}

		switch (target)
		{
		case BuildTarget.StandaloneWindows:
		case BuildTarget.StandaloneWindows64:
			if (Debug.isDebugBuild)
			{
				CopyDebugSymbols(target, projName, outDir);
			}
			break;
		case BuildTarget.StandaloneLinux64:
			CopyLinuxLibraries(projName, outDir);
			break;
		case BuildTarget.PS4:
			CopyPS4Libraries(projName, outDir);
			break;
		case BuildTarget.XboxOne:
			CopyXBoxOneLibraries(projName, outDir);
			break;
		}
	}

	static void CopyUIResources(BuildTarget target, string projName, string outDir)
	{
		string resourcesDir = PlayerPrefs.GetString("CoherentGT:UIResources");

		if (!string.IsNullOrEmpty(resourcesDir))
		{
			int lastDelim = resourcesDir.LastIndexOf('/');
			string folderName = lastDelim != -1 ?
				resourcesDir.Substring(lastDelim) :
				resourcesDir;

			string inDir = Path.Combine(Application.dataPath, resourcesDir);

			if (!Directory.Exists(inDir))
			{
				resourcesDir = Path.Combine("..", resourcesDir);
				inDir = Path.Combine(Application.dataPath, resourcesDir);
			}

			switch (target)
			{
			case BuildTarget.StandaloneWindows:
			case BuildTarget.StandaloneWindows64:
			case BuildTarget.StandaloneLinux64:
				outDir = Path.Combine(outDir, string.Format("{0}_Data/{1}", projName, folderName));
				break;
			case BuildTarget.StandaloneOSXIntel:
			case BuildTarget.StandaloneOSXIntel64:
			case BuildTarget.StandaloneOSXUniversal:
				outDir = Path.Combine(outDir, string.Format("{0}.app/Contents/{1}", projName, folderName));
				break;
			case BuildTarget.PS4:
				outDir = Path.Combine(outDir, string.Format("{0}/Media/{1}", projName, folderName));
				break;
			case BuildTarget.XboxOne:
				outDir = Path.Combine(outDir, string.Format("{0}/{1}/Data/{2}", projName, Application.productName, folderName));
				break;
			default:
				return;
			}

			CoherentUIGTFileUtilities.DirectoryCopy(inDir, outDir, true, null, new []{ "*.meta" }, false);
		}
	}

	static void CopyInspectorResources(BuildTarget target, string projName, string outDir)
	{
		string inspectorDir = Path.Combine(Application.dataPath, "WebPlayerTemplates/inspector");

		switch (target)
		{
		case BuildTarget.StandaloneWindows:
		case BuildTarget.StandaloneWindows64:
			outDir = Path.Combine(outDir, string.Format("{0}_Data/inspector", projName));
			break;
		case BuildTarget.StandaloneOSXIntel:
		case BuildTarget.StandaloneOSXIntel64:
		case BuildTarget.StandaloneOSXUniversal:
			outDir = Path.Combine(outDir, string.Format("{0}.app/Contents/inspector", projName));
			break;
		case BuildTarget.PS4:
			outDir = Path.Combine(outDir, string.Format("{0}/Media/inspector", projName));
			break;
		case BuildTarget.XboxOne:
			outDir = Path.Combine(outDir, string.Format("{0}/{1}/Data/inspector", projName, Application.productName));
			break;
		default:
			return;
		}

		if (Directory.Exists(inspectorDir))
		{
			CoherentUIGTFileUtilities.DirectoryCopy(inspectorDir, outDir, true, null, new []{ "*.meta" }, false);
		}
		else
		{
			Debug.LogError("[Coherent GT] Debugger resources not found at 'WebPlayerTemplates/inspector'. " +
				"Debugger won't work unless you provide the inspector resources at path " +
				outDir);
		}
	}

	static void RemoveDevelopmentModule(BuildTarget target, string projName, string outDir)
	{
		switch (target)
		{
		case BuildTarget.StandaloneWindows:
		case BuildTarget.StandaloneWindows64:
			string pluginsDir = Path.Combine(outDir, string.Format("{0}_Data/Plugins", projName));
			string dllFile = Path.Combine(pluginsDir, "CoherentUIGTDevelopment.dll");
			if (File.Exists(dllFile))
			{
				File.Delete(dllFile);
			}
			break;
		case BuildTarget.StandaloneOSXIntel:
		case BuildTarget.StandaloneOSXIntel64:
		case BuildTarget.StandaloneOSXUniversal:
			string appContentsPath = Path.Combine(outDir, string.Format("{0}.app/Contents/", projName));
			string bundleContents = Path.Combine(appContentsPath, "Plugins/" + "CoherentUIGT_Native.bundle/" + "Contents/MacOS");
			string dylibFile = Path.Combine(bundleContents, "CoherentUIGTDevelopment.dylib");
			if (File.Exists(dylibFile))
			{
				File.Delete(dylibFile);
			}
			break;
		case BuildTarget.PS4:
			string ps4pluginsDir = Path.Combine(outDir, string.Format("{0}/Media/Plugins", projName));
			string prxFile = Path.Combine(ps4pluginsDir, "CoherentUIGTDevelopment.prx");
			if (File.Exists(prxFile))
			{
				File.Delete(prxFile);
			}
			break;
		case BuildTarget.XboxOne:
			string xboxpluginsDir = Path.Combine(outDir, string.Format("{0}/{1}/Data/Plugins", projName, Application.productName));
			string dllxFile = Path.Combine(xboxpluginsDir, "CoherentUIGTDevelopment.dll");
			string sdefFile = Path.Combine(xboxpluginsDir, "CoherentUIGTDevelopment.s.def");
			if (File.Exists(dllxFile))
			{
				File.Delete(dllxFile);
			}
			if (File.Exists(sdefFile))
			{
				File.Delete(sdefFile);
			}
			break;
		}
	}

	static void CopyDebugSymbols(BuildTarget target, string projName, string outDir)
	{
		string projectPluginsDir = target == BuildTarget.StandaloneWindows ?
			Path.Combine(Application.dataPath, "Plugins/x86") :
			Path.Combine(Application.dataPath, "Plugins/x86_64");
		string buildPluginsDir = Path.Combine(outDir, string.Format("{0}_Data/Plugins", projName));

		CoherentUIGTFileUtilities.DirectoryCopy(projectPluginsDir, buildPluginsDir, false, new []{ "*.pdb" }, null, false);
	}

	static void CopyLinuxLibraries(string projName, string outDir)
	{
		string projectPluginsDir = Path.Combine(Application.dataPath, "Plugins/Linux/x86_64");
		string buildPluginsDir = Path.Combine(outDir, string.Format("{0}_Data/Plugins", projName));
		string buildPluginsDirDestination = Path.Combine(buildPluginsDir, "x86_64");

		CoherentUIGTFileUtilities.DirectoryCopy(projectPluginsDir, buildPluginsDirDestination, false, new[] { "*.53" }, null, false);
		CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, buildPluginsDirDestination, false, new[] { "*.so" }, null, true);
	}

	static void CopyPS4Libraries(string projName, string outDir)
	{
		string buildProjectDir = Path.Combine(outDir, projName);
		string buildResourceDir = Path.Combine(buildProjectDir, "Media");
		string buildPluginsDir = Path.Combine(buildResourceDir, "Plugins");
		string projectPluginsDir = Path.Combine(Application.dataPath, "Plugins/PS4");

		CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, buildProjectDir, false, new []{ "*.prx" }, new []{ "CoherentUIGT_Native.prx", "CoherentUIGTDevelopment.prx" }, true);
		CoherentUIGTFileUtilities.DirectoryCopy(projectPluginsDir, buildProjectDir, false, new []{ "*.dat" }, null, false);
		if (Debug.isDebugBuild)
		{
			CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, buildResourceDir, false, new []{ "CoherentUIGTDevelopment.prx" }, null, true);
		}
	}

	static void CopyXBoxOneLibraries(string projName, string outDir)
	{
		string buildProjectDir = Path.Combine(Path.Combine(outDir, projName), Application.productName);
		string buildPluginsDir = Path.Combine(buildProjectDir, "Data/Plugins");
		string projectPluginsDir = Path.Combine(Application.dataPath, "Plugins/XboxOne");

		CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, buildProjectDir, false, new []{ "*.dll" }, new []{ "CoherentUIGT_Native.dll" }, true);
		CoherentUIGTFileUtilities.DirectoryCopy(projectPluginsDir, buildProjectDir, false, new []{ "*.dat" }, null, false);

		if (Debug.isDebugBuild)
		{
			CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, buildProjectDir, false, new []{ "*.pdb" }, new []{ "CoherentUIGT_Native.pdb", "CoherentUIGTNet_AOT.pdb" }, true);
		}
		else
		{
			CoherentUIGTFileUtilities.DirectoryCopy(buildPluginsDir, null, false, new []{ "*.pdb" }, null, true);
		}
	}

	static bool IsTargetPlatformSupported(BuildTarget target)
	{
		return target == BuildTarget.StandaloneWindows ||
		target == BuildTarget.StandaloneWindows64 ||
		target == BuildTarget.StandaloneOSXIntel ||
		target == BuildTarget.StandaloneOSXIntel64 ||
		target == BuildTarget.StandaloneOSXUniversal ||
		target == BuildTarget.StandaloneLinux64 ||
		target == BuildTarget.PS4 ||
		target == BuildTarget.XboxOne;
	}
}

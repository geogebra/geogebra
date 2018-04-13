using System;
using System.IO;
using UnityEngine;
using System.Collections.Generic;

namespace Coherent.UIGT
{
	class CoherentUIGTLibrary
	{
		private ILogHandler m_LogHandler = null;

		private List<UISystem> m_UISystems = new List<UISystem>();

		private static CoherentUIGTLibrary Instance = new CoherentUIGTLibrary();

		private static UnityPluginListener m_UnityPluginListener = null;

		public static void SetDependenciesPath()
		{
			#if UNITY_EDITOR_WIN || UNITY_STANDALONE_WIN
			string pluginsPath = GetPluginsFolderPath();
			string currentPath = Environment.GetEnvironmentVariable("PATH", EnvironmentVariableTarget.Process);

			if (!currentPath.Contains(pluginsPath))
			{
				Environment.SetEnvironmentVariable("PATH", pluginsPath + Path.PathSeparator + currentPath, EnvironmentVariableTarget.Process);
			}
			#endif
		}

		public static void RestoreProcessPath()
		{
			#if UNITY_EDITOR_WIN || UNITY_STANDALONE_WIN
			string pluginsPath = GetPluginsFolderPath();
			string currentPath = Environment.GetEnvironmentVariable("PATH", EnvironmentVariableTarget.Process);

			if (currentPath.Contains(pluginsPath))
			{
				Environment.SetEnvironmentVariable("PATH", currentPath.Replace(pluginsPath + Path.PathSeparator, ""), EnvironmentVariableTarget.Process);
			}
			#endif
		}

		static string GetPluginsFolderPath()
		{
			string dataFolder;
			string pluginsFolder = "Plugins";

			#if UNITY_EDITOR_WIN
			dataFolder = Path.Combine(Directory.GetCurrentDirectory(), "Assets");
			string currentArch = IntPtr.Size == 4 ? "x86" : "x86_64";
			pluginsFolder = Path.Combine(pluginsFolder, currentArch);
			#else
			string managedFolder = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
			dataFolder = Path.GetFullPath(Path.Combine(managedFolder, ".."));
			#endif

			return Path.Combine(dataFolder, pluginsFolder);
		}

		/// <summary>
		/// Creates a new ViewContext
		/// </summary>
		/// <param name="ctxSettings">Settings for the context</param>
		/// <param name="listener">Listener for events for the context</param>
		/// <param name="fileHandler">File handler for the context</param>
		/// <returns>the newly created ViewContext</returns>
		public static UISystem CreateUISystem(SystemSettings systemSettings)
		{
			if (Instance.m_UISystems.Count > 0)
			{
				Debug.LogWarning("You can only create one UISystem. "
					+ "Creation of second CoherentGTSystem will fail!");
				return null;
			}

			Instance.m_LogHandler = new UnityGTLogHandler();

			SetDependenciesPath();

			var uiSystem = CoherentUIGT_Native.InitializeUIGTSystem(
				Coherent.UIGT.License.COHERENT_KEY,
				systemSettings,
				Severity.Debug,
				Instance.m_LogHandler,
				null);
			uiSystem.SetUnityPluginListener(UnityPluginListener);
			Instance.m_UISystems.Add(uiSystem);

			RestoreProcessPath();

			return uiSystem;
		}

		/// <summary>
		/// Destroys the given UI System
		/// </summary>
		/// <param name="system">The UI System to be destroyed</param>
		public static void DestroyUISystem(UISystem system)
		{
			Instance.m_UISystems.Remove(system);
			system.Dispose();

			if (Instance.m_UISystems.Count == 0)
			{
				// TODO: Means for recreating the log handler
				Instance.m_LogHandler.Dispose();
				Instance.m_LogHandler = null;
			}
		}

		public static UnityPluginListener UnityPluginListener
		{
			get
			{
				if (m_UnityPluginListener == null)
				{
					m_UnityPluginListener = new UnityPluginListener();
				}
				return m_UnityPluginListener;
			}
		}
	}
}

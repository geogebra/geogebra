using UnityEditor;

public class CoherentUIGTResourcesMonitor : AssetPostprocessor
{
	static void OnPostprocessAllAssets(
		string[] importedAssets,
		string[] deletedAssets,
		string[] movedAssets,
		string[] movedFromAssetPaths)
	{
		CoherentUIGTSystem system = CoherentUIGTSystem.CurrentUISystem;

		if (system == null)
		{
			return;
		}

		for (int i = 0; i < system.UIViews.Count; i++)
		{
			CoherentUIGTView view = system.UIViews[i];

			if (!view.AutoRefresh)
			{
				continue;
			}

			if (CheckResourcesAndReloadView(view, importedAssets) ||
				CheckResourcesAndReloadView(view, deletedAssets) ||
				CheckResourcesAndReloadView(view, movedFromAssetPaths))
			{
				continue;
			}
		}
	}

	static bool CheckResourcesAndReloadView(CoherentUIGTView view, string[] changedFiles)
	{
		if (view.CheckResourcesUsedByView(changedFiles))
		{
			view.Reload();
			return true;
		}

		return false;
	}
}

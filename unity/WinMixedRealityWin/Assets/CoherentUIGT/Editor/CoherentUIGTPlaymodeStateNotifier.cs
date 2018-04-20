using UnityEditor;

namespace Coherent.UIGT
{
[InitializeOnLoad]
public class PlaymodeStateNotifier
{
	static PlaymodeStateNotifier()
	{
		EditorApplication.playmodeStateChanged += OnPlayModeChanged;
	}

	static void OnPlayModeChanged()
	{
		if (!EditorApplication.isPlaying)
		{
			try
			{
				CoherentUIGT_Native.UnityOnEditorStop();
			}
			catch (System.TypeInitializationException)
			{
				// Ignore DLLNotFoundException when our library is not
				// yet loaded as then we don't have to send OnEditorStop event.
			}
		}
	}
}
}

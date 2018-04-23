#if !UNITY_5 || UNITY_5_0 || UNITY_5_1 || UNITY_5_2
#define COHERENT_UNITY_PRE_5_3
#endif

using UnityEngine;
using System.Collections;

#if !COHERENT_UNITY_PRE_5_3
using UnityEngine.SceneManagement;
#endif

using Coherent.UIGT;

public class ComplexMenuGT : MonoBehaviour
{
	public AudioSource menuMusic;
	CoherentUIGTView viewComponent;
	bool canLoadGame;

	void Awake()
	{
		viewComponent = GetComponent<CoherentUIGTView>();
	}

	IEnumerator LoadResources()
	{
		string[] actions =
		{
			"Raising a zombie army",
			"Loading cute kitten pictures",
			"Emptying your credit card",
			"Oops, disregard the last message",
		};

		float progress = 0f;
		float maxProgress = 0.005f;
		while (progress < 1f)
		{
			string action = actions[(int)(progress * actions.Length)];
			progress += Random.value * maxProgress;
			viewComponent.View.TriggerEvent("LoadingProgress", progress, action);
			yield return null;
		}
		canLoadGame = true;
	}

	void Update()
	{
		if (canLoadGame && Input.anyKeyDown)
		{
			#if COHERENT_UNITY_PRE_5_3
			Application.LoadLevel("ComplexGameGT");
			#else
			SceneManager.LoadScene("ComplexGameGT");
			#endif
		}
	}


	[Coherent.UIGT.CoherentUIGTMethod("EnteringMainMenu", true)]
	void EnteringMainMenu()
	{
		menuMusic.Play();
	}

	[Coherent.UIGT.CoherentUIGTMethod("SaveVideoSettings", true)]
	void SaveVideoSettings(string resolution, int textureQuality, int shadowQuality, bool isFullscreen)
	{
		string[] resolutionValues = resolution.Split(new char[] {'x'}, 2);
		int width = int.Parse(resolutionValues[0]);
		int height = int.Parse(resolutionValues[1]);

		Screen.SetResolution(width, height, isFullscreen);
	}

	[Coherent.UIGT.CoherentUIGTMethod("SaveAudioSettings", true)]
	void SaveAudioSettings(float volume)
	{
		menuMusic.volume = volume;
	}

	[Coherent.UIGT.CoherentUIGTMethod("LoadGame", true)]
	void LoadGame(string rank, string name)
	{
		viewComponent.View.TriggerEvent("ShowLoadingScreen", rank + " " + name);
		StartCoroutine("LoadResources");
	}

	[Coherent.UIGT.CoherentUIGTMethod("ExitGame", true)]
	void ExitGame()
	{
		#if UNITY_EDITOR
		UnityEditor.EditorApplication.isPlaying = false;
		#endif

		Application.Quit();
	}
}

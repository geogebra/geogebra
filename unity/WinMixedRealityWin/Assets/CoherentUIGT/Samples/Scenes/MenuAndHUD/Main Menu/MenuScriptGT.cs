#if !UNITY_5 || UNITY_5_0 || UNITY_5_1 || UNITY_5_2
#define COHERENT_UNITY_PRE_5_3
#endif

using UnityEngine;
using System.Collections;

#if !COHERENT_UNITY_PRE_5_3
using UnityEngine.SceneManagement;
#endif

using Coherent.UIGT;

public class MenuScriptGT : MonoBehaviour
{
	private CoherentUIGTView ViewComponent;

	void Start()
	{
		ViewComponent = GetComponent<CoherentUIGTView>();

		if (ViewComponent)
		{
			ViewComponent.Listener.ReadyForBindings += this.RegisterBindings;
		}

		ViewComponent.ReceivesInput = true;
	}

	private void RegisterBindings()
	{
		var view = ViewComponent.View;

		if (view != null)
		{
			view.BindCall("NewGame", (System.Action)this.NewGame);
		}
	}

	private void NewGame()
	{
		this.StartCoroutine(LoadGameScene());
	}

	IEnumerator LoadGameScene()
	{
		// Display a loading screen
		ViewComponent.View.LoadURL("coui://UIResources/MenuAndHUDGT/loading/loading.html");

		// The game level is very simple and loads instantly;
		// Add some artificial delay so we can display the loading screen.
		yield return new WaitForSeconds(2.5f);

		// Load the game level
		#if COHERENT_UNITY_PRE_5_3
		Application.LoadLevelAsync("gameGT");
		#else
		SceneManager.LoadSceneAsync("gameGT");
		#endif
	}
}

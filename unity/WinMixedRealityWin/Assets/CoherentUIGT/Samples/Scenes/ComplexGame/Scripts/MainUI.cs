#if !UNITY_5 || UNITY_5_0 || UNITY_5_1 || UNITY_5_2
#define COHERENT_UNITY_PRE_5_3
#endif

using UnityEngine;
using System.Collections;

#if !COHERENT_UNITY_PRE_5_3
using UnityEngine.SceneManagement;
#endif

using Coherent.UIGT;

public class MainUI : MonoBehaviour
{
	static CoherentUIGTView viewComponent;

	public static void SetFuel(float value)
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("updateFuel", value);
	}

	public static void SetHealth(float value)
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("updateHealth", value);
	}

	public static void SetEnergy(float value)
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("updateEnergy", value);
	}

	public static void SetCoordinates(Vector3 worldPoint)
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("setPos", worldPoint.x, worldPoint.y, worldPoint.z);
	}

	public static void SetScore(int value)
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("setScore", value);
	}

	public static void ShowGameOver()
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("showGameOver");
		UnlockCursor();
	}

	public static void ShowScan()
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("showScan");
	}

	public static void ShowOutOfRange()
	{
		if (viewComponent == null || !viewComponent.IsReadyForBindings) return;

		viewComponent.View.TriggerEvent("outOfRange");
	}

	public static void LockCursor()
	{
		#if UNITY_5
		Cursor.lockState = CursorLockMode.Locked;
		Cursor.visible = false;
		#else
		Screen.lockCursor = true;
		#endif
	}

	public static void UnlockCursor()
	{
		#if UNITY_5
		Cursor.lockState = CursorLockMode.None;
		Cursor.visible = true;
		#else
		Screen.lockCursor = false;
		#endif
	}

	void Awake()
	{
		viewComponent = GetComponent<CoherentUIGTView>();
	}

	void Start ()
	{
		if (viewComponent != null)
		{
			viewComponent.Listener.ReadyForBindings += OnReadyForBindings;
		}
		ResumeGame();
	}

	#if UNITY_EDITOR
	void Update()
	{
		if (Input.GetKeyDown(KeyCode.LeftAlt) ||
			Input.GetKeyDown(KeyCode.RightAlt))
		{
			UnlockCursor();
		}
	}
	#endif

	void OnReadyForBindings()
	{
		viewComponent.View.RegisterForEvent("onGoToMenu", (System.Action)LoadMainMenu);
		viewComponent.View.RegisterForEvent("onShowInGameMenu", (System.Action)PauseGame);
		viewComponent.View.RegisterForEvent("onCloseInGameMenu", (System.Action)ResumeGame);
		viewComponent.View.RegisterForEvent("onQuit", (System.Action)Quit);
	}

	void LoadMainMenu()
	{
		#if COHERENT_UNITY_PRE_5_3
		Application.LoadLevel("ComplexMenuGT");
		#else
		SceneManager.LoadScene("ComplexMenuGT");
		#endif
	}

	void PauseGame()
	{
		Time.timeScale = 0f;
		UnlockCursor();
	}

	void ResumeGame()
	{
		Time.timeScale = 1f;
		LockCursor();
	}

	void Quit()
	{
		#if UNITY_EDITOR
		UnityEditor.EditorApplication.isPlaying = false;
		#endif

		Application.Quit();
	}
}

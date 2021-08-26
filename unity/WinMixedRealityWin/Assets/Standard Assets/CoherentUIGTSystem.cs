using UnityEngine;
using IntPtr = System.IntPtr;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;

using Coherent.UIGT;
using UnityEngine.XR;

/// <summary>
/// Component controlling the Coherent GT System
/// </summary>
[DisallowMultipleComponent]
[AddComponentMenu("Coherent GT/Coherent GT System")]
public class CoherentUIGTSystem : MonoBehaviour
{
    public bool isClicked;
    public bool m_IsGazerOnView;
    public Vector3 GazeHitPosition;
    public bool IsMouseLeftDowSimulation = false;
    //public Vector3 cursorPosition;

    private Dictionary<IntPtr, Texture> m_PreloadedImageTextureAssetsInUse = new Dictionary<IntPtr, Texture>();
	public void AddPreloadedImageTextureAsset(ref Texture tex)
	{
		m_PreloadedImageTextureAssetsInUse.Add(tex.GetNativeTexturePtr(), tex);
	}

	private Dictionary<IntPtr, RenderTexture> m_LiveViewRenderTexturesForLateRelease = new Dictionary<IntPtr, RenderTexture>();
	public void AddLiveViewRenderTexturesForLateRelease(ref RenderTexture tex)
	{
		var nativeTexturePtr = tex.GetNativeTexturePtr();
		if (m_LiveViewRenderTexturesForLateRelease.ContainsKey(nativeTexturePtr))
		{
			m_LiveViewRenderTexturesForLateRelease[nativeTexturePtr].Release();
			m_LiveViewRenderTexturesForLateRelease[nativeTexturePtr] = tex;
		}
		else
		{
			m_LiveViewRenderTexturesForLateRelease.Add(nativeTexturePtr, tex);
		}
	}

	private List<uint> m_LiveViewHandles = new List<uint>();
	private uint[] m_LiveViewHandlesArray = new uint[]{};
	private uint m_NextFreeImageHandle;
	public uint GetNextFreeImageHandle()
	{
		uint handle = ++m_NextFreeImageHandle;
		m_LiveViewHandles.Add(handle);
		m_LiveViewHandlesArray = m_LiveViewHandles.ToArray();
		return handle;
	}

	public readonly Dictionary<string, CoherentUIGTLiveGameView> LiveGameViewComponents = new Dictionary<string, CoherentUIGTLiveGameView>();
	public bool RegisterLiveViewComponent(string url, CoherentUIGTLiveGameView component)
	{
		if (LiveGameViewComponents.ContainsKey(url))
		{
			return false;
		}

		LiveGameViewComponents.Add(url, component);
		return true;

	}
	public void UnregisterLiveViewComponent(string url)
	{
		LiveGameViewComponents.Remove(url);
	}

	#if !UNITY_EDITOR && UNITY_STANDALONE_WIN
	static CoherentUIGTSystem()
	{
		CoherentUIGTLibrary.SetDependenciesPath();
	}
	#endif

	private static CoherentUIGTSystem m_Instance = null;

	public static CoherentUIGTSystem CurrentUISystem
	{
		get
		{
			#if UNITY_EDITOR
			// Do not create a system if not playing
			if (!Application.isPlaying)
			{
				return null;
			}
			#endif
			if (m_Instance == null)
			{
				m_Instance = Object.FindObjectOfType(typeof(CoherentUIGTSystem)) as CoherentUIGTSystem;
				if (m_Instance == null)
				{
					m_Instance = CoherentUIGTSystem.Create();
					if (m_Instance == null)
					{
						throw new System.ApplicationException(
							"Unable to create Coherent GT System");
					}
				}
			}
			return m_Instance;
		}
	}

	private string m_InspectorResourcesFolder;
	private UISystem m_UISystem;
	private UISystemRenderer m_UISystemRenderer;

	internal UISystemRenderer UISystemRenderer
	{
		get
		{
			return m_UISystemRenderer;
		}
	}

	private Vector2 m_LastMousePosition = new Vector2(-1, -1);
	protected CoherentUIGTMouseEventData m_MouseEventData;
	protected CoherentUIGTKeyEventData m_KeyEventData;

	private CoherentUIGTLocalizationManager m_LocalizationManager;

	/// <summary>
	/// Indicates whether one of the views in the system is keeping input focus.
	/// </summary>
	private bool m_SystemHasFocusedView = false;

	/// <summary>
	/// Determines whether the Coherent GT System component is currently in its Update() method
	/// </summary>
	/// <returns>
	/// <c>true</c> if this instance is updating; otherwise, <c>false</c>.
	/// </returns>
	public bool IsUpdating { get; private set; }

	public delegate void OnUISystemDestroyingDelegate();

	public event OnUISystemDestroyingDelegate UISystemDestroying;

	public delegate void SystemReadyEventHandler();

	private SystemReadyEventHandler SystemReadyHandlers;

	public event SystemReadyEventHandler SystemReady
	{
		add
		{
			if (!IsReady())
			{
				SystemReadyHandlers += value;
			}
			else
			{
				m_ReadyHandlers.Add(value);
			}
		}
		remove
		{
			SystemReadyHandlers -= value;
		}
	}

	private List<SystemReadyEventHandler> m_ReadyHandlers = new List<SystemReadyEventHandler>();
	private List<CoherentUIGTView> m_Views = new List<CoherentUIGTView>();
	private const bool m_UseDx11NewRenderer = true;
	bool m_IsCoroutineStarted = false;

	internal bool IsUsingDx11NewRenderer
	{
		get { return m_UseDx11NewRenderer; }
	}

	internal void AddView(CoherentUIGTView view)
	{
		m_Views.Add(view);
	}

	internal bool RemoveView(CoherentUIGTView view)
	{
		return m_Views.Remove(view);
	}

	public CoherentUIGTLocalizationManager LocalizationManager
	{
		get
		{
			return m_LocalizationManager;
		}
	}

	public List<CoherentUIGTView> UIViews
	{
		get
		{
			return m_Views;
		}
	}

	public static CoherentUIGTSystem Create()
	{
		var go = new GameObject("CoherentGTSystem");
		CoherentUIGTSystem system = go.AddComponent<CoherentUIGTSystem>();
		return system;
	}

	/// <summary>
	/// port for debugging Views, -1 to disable
	/// </summary>
	[HideInInspector]
	[SerializeField]
	private int m_DebuggerPort = 19999;

	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Debugger port",
		Tooltip="The port where the system will listen for the debugger",
		IsStatic=true)]
	public int DebuggerPort
	{
		get
		{
			return m_DebuggerPort;
		}
		set
		{
			if (m_UISystem != null)
			{
				Debug.LogError("[Coherent GT] Debugger port can only be changed before system creation.");
				return;
			}
			m_DebuggerPort = value;
		}
	}

	/// <summary>
	/// The main camera. Used for obtaining mouse position over the HUD and raycasting in the world.
	/// </summary>
	public Camera m_MainCamera = null;
	[HideInInspector]
	[SerializeField]
	private int m_DiskCacheSize = 32 * 1024 * 1024;
	/// <summary>
	/// Sets the on-disk size of the URL cache
	/// </summary>
	/// <value>
	/// The maximum size of the on-disk cache
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
								PrettyName = "Disk cache size",
								Tooltip="The maximum size of the disk cache",
								IsStatic=true)]
	public int DiskCacheSize
	{
		get
		{
			return m_DiskCacheSize;
		}
		set
		{
			m_DiskCacheSize = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_RunAsynchronous = false;

	/// <summary>
	/// Starts the system in an asynchronous mode which can greatly enhance
	/// performance by running Coherent GT in a separate thread
	///
	/// Note changes to this property will take effect after a restart of Unity.
	/// </summary>
	/// <value>
	/// Whether to run in a separate thread
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
								PrettyName = "Run Asynchronously",
								Tooltip="Whether to run Coherent GT in a separate thread",
								IsStatic=true)]
	public bool RunAsynchronous
	{
		get
		{
			return m_RunAsynchronous;
		}
		set
		{
			m_RunAsynchronous = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_LocalizationEnabled = false;

	/// <summary>
	/// Enables localization of elements with data-l10n-id attribute
	/// </summary>
	/// <value>
	/// Whether to translate or not
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
								PrettyName = "Enable Localization",
								Tooltip="You can enable localization by ticking this check",
								IsStatic=true)]
	public bool LocalizationEnabled
	{
		get
		{
			return m_LocalizationEnabled;
		}
		set
		{
			m_LocalizationEnabled = value;
		}
	}


	internal enum UnityRendererType
	{
		OpenGL,
		D3D9,
		D3D11,
		Unknown,
	}

	internal static UnityRendererType GetUnityRendererType()
	{
		UnityRendererType result = UnityRendererType.Unknown;

		if (SystemInfo.graphicsDeviceVersion.Contains("Open"))
		{
			result = UnityRendererType.OpenGL;
		}
		else if (SystemInfo.graphicsDeviceVersion.Contains("Direct3D 11"))
		{
			result = UnityRendererType.D3D11;
		}
		else if (SystemInfo.graphicsDeviceVersion.Contains("Direct3D 9"))
		{
			result = UnityRendererType.D3D9;
		}

		return result;
	}

	static void OnDeviceReset()
	{
		CoherentUIGTSystem system = MonoBehaviour.FindObjectOfType(
			typeof(CoherentUIGTSystem)) as CoherentUIGTSystem;
		for (int i = 0; i < system.m_Views.Count; i++)
		{
			system.m_Views[i].RecreateRenderTarget();
		}
	}

	void Awake()
	{
		useGUILayout = false;

		CoherentUIGTLibrary.SetDependenciesPath();

		#if UNITY_EDITOR
		m_InspectorResourcesFolder = "./Assets/WebPlayerTemplates";
		#else
		m_InspectorResourcesFolder = Application.dataPath;
		#endif

		m_MouseEventData = new CoherentUIGTMouseEventData();
		m_KeyEventData = new CoherentUIGTKeyEventData();

		if (m_LocalizationEnabled)
		{
			m_LocalizationManager = new CoherentUIGTLocalizationManager();
		}

		CoherentUIGTLibrary.RestoreProcessPath();
	}

	void OnEnable()
	{
		ManagedRenderingNotifications.OnDeviceReset += OnDeviceReset;
	}

	void OnDisable()
	{
		ManagedRenderingNotifications.OnDeviceReset -= OnDeviceReset;
	}

	void Start()
	{
		if ((Debug.isDebugBuild || Application.isEditor) && DebuggerPort != -1)
		{
			Debug.Log("[Coherent GT] Debugger: Application forced to run in background.");
			Application.runInBackground = true;
		}

		if (m_UISystem == null)
		{
			string localStoragePath = Application.persistentDataPath + "/LocalStorage";

			SystemSettings settings = new SystemSettings()
			{
				LocalizationManagerInstance = m_LocalizationManager,
				DebuggerPort = this.DebuggerPort,
				DiskCache = null,
				InspectorResourcesFolder = m_InspectorResourcesFolder,
				ProxyData = null,
				RunAsynchronous = this.RunAsynchronous,
				LocalStorageFolder = localStoragePath
			};

			if (SystemInfo.graphicsDeviceVersion.StartsWith("Direct3D 9"))
			{
				settings.AllowMultipleRenderingThreads = true;
			}

			if (string.IsNullOrEmpty(Coherent.UIGT.License.COHERENT_KEY))
			{
				throw new System.ApplicationException(
					"You must supply a license key to start Coherent GT! " +
					"Follow the instructions in the manual for editing the " +
					"LicenseGT.cs file.");
			}

			m_UISystem = CoherentUIGTLibrary.CreateUISystem(settings);
			if (m_UISystem == null)
			{
				throw new System.ApplicationException(
					"Creating a ViewContext failed!");
			}

			Debug.Log("Coherent GT system initialized.");

			CoherentUIGTLibrary.UnityPluginListener.UserImageTextureReleased += OnUserImageTextureReleased;
		}

		DontDestroyOnLoad(this.gameObject);
	}

	/// <summary>
	/// Determines whether this instance is ready.
	/// </summary>
	/// <returns>
	/// <c>true</c> if this instance is ready; otherwise, <c>false</c>.
	/// </returns>
	public bool IsReady()
	{
		return m_UISystem != null && m_UISystemRenderer != null;
	}

	/// <summary>
	/// Determines whether there is an focused Click-to-focus view
	/// </summary>
	/// <value>
	/// <c>true</c> if there is an focused Click-to-focus view; otherwise, <c>false</c>.
	/// </value>
	public bool HasFocusedView
	{
		get
		{
			return m_SystemHasFocusedView;
		}
	}

	public delegate void OnViewFocusedDelegate(bool focused);

	/// <summary>
	/// Occurs when a Click-to-focus view gains or loses focus
	/// </summary>
	public event OnViewFocusedDelegate OnViewFocused;

	private void SetViewFocused(bool focused)
	{
		m_SystemHasFocusedView = focused;
		if (OnViewFocused != null)
		{
			OnViewFocused(focused);
		}
	}

	private void TrackInputFocus()
	{
		if (m_MainCamera == null)
		{
			m_MainCamera = Camera.main;
			if (m_MainCamera == null)
			{
				return;
			}
		}

		for (int i = 0; i < m_Views.Count; i++)
		{
			SetClickThroughPosition(m_Views[i]);
		}

        bool isClick = Input.GetMouseButtonDown(0);
        if (!m_SystemHasFocusedView
			&& !isClick && !m_HasClickOnCameraView)
		{
			// Do nothing if the left mouse button isn't clicked
			// (and there is no focused view; if there is, we need to track the mouse)
			return;
		}

		//if (m_IsMouseOnView)
        if (m_IsGazerOnView)
		{
			if (m_HasClickOnCameraView)
			{
				bool hasFocusedView = false;
				float lastViewDepth = float.MinValue;
				int lastEnabledViewIndex = -1;
				// Reset input processing for all views except for the clicked view
				for (int i = 0; i < m_Views.Count; i++)
				{
					if (!m_Views [i].ClickToFocus) 
					{
						continue;
					}

					if (m_Views[i].RenderingCamera &&
						m_Views[i].View.IsMouseOnView() &&
						m_Views[i].RenderingCamera.depth > lastViewDepth)
					{
						if (hasFocusedView)
						{
							m_Views[lastEnabledViewIndex].ReceivesInput = false;
						}

						m_Views[i].ReceivesInput = true;
						hasFocusedView = true;
						lastViewDepth = m_Views[i].RenderingCamera.depth;
						lastEnabledViewIndex = i;
					}
					else
					{
						m_Views[i].ReceivesInput = false;
					}
				}

				SetViewFocused(hasFocusedView);
			}
		}

		m_HasClickOnCameraView = isClick && m_IsGazerOnView;

		if (m_IsGazerOnView)
		{
			return;
		}

		// Activate input processing for the view below the mouse cursor
		RaycastHit hitInfo;
        if (Physics.Raycast(m_MainCamera.ScreenPointToRay(Input.mousePosition), out hitInfo))
       /* if (Physics.Raycast(
            Camera.main.transform.position,
            cursorPosition - Camera.main.transform.position,
            out hitInfo,
            20.0f,
            Physics.DefaultRaycastLayers
            ))*/
        {
			CoherentUIGTView viewComponent = null;

			Transform hitTransform = hitInfo.collider.transform;

			for (int i = 0; i < m_Views.Count; i++)
			{
				if (m_Views[i].transform.IsChildOf(hitTransform))
				{
					viewComponent = m_Views[i];
				}
			}

			if (viewComponent != null && viewComponent.ClickToFocus)
			{
				if (isClick)
				{
					// Reset input processing for all views
					for (int i = 0; i < m_Views.Count; i++)
					{
						m_Views[i].ReceivesInput = false;
					}
					// Set input to the clicked view
					viewComponent.ReceivesInput = true;
                    Debug.Log("In viewComponent.ReceivesInput = true;");
					SetViewFocused(true);
				}

				viewComponent.SetMousePosition(
					(int)(hitInfo.textureCoord.x * viewComponent.Width),
					(int)(hitInfo.textureCoord.y * viewComponent.Height));

				return;
			}
		}

		// If neither the HUD nor an object was clicked, clear the focus
		if (m_SystemHasFocusedView && isClick)
		{
			// Reset input processing for all views
			for (int i = 0; i < m_Views.Count; i++)
			{
				m_Views[i].ReceivesInput = false;
			}
			SetViewFocused(false);
		}
	}

	void OnUserImageTextureReleased(IntPtr texturePtr)
	{
		if (m_LiveViewRenderTexturesForLateRelease.ContainsKey(texturePtr))
		{
			m_LiveViewRenderTexturesForLateRelease[texturePtr].Release();
			m_LiveViewRenderTexturesForLateRelease.Remove(texturePtr);
		}
		else if (m_PreloadedImageTextureAssetsInUse.ContainsKey(texturePtr))
		{
			Resources.UnloadAsset(m_PreloadedImageTextureAssetsInUse[texturePtr]);
			m_PreloadedImageTextureAssetsInUse.Remove(texturePtr);
		}
	}

	void Update()
	{
		#if UNITY_EDITOR
		if (UnityEditor.EditorApplication.isCompiling)
		{
			OnAssemblyReload();
		}
		#endif

		if (!IsReady())
		{
			StartCoroutine(InitializeUISystemRenderer());
		}

		if (m_UISystem != null)
		{
			IsUpdating = true;

			//CheckIfMouseOnUI(); // we do not need this for Mixed Reality JB

			m_UISystem.ClearTextures();
			if (m_LiveViewHandlesArray.Length > 0 )
			{
				m_UISystem.UserImagesChanged(m_LiveViewHandlesArray, (uint) m_LiveViewHandlesArray.Length);
			}

			// UISystem.Advance() should be called before View.Layout() when using Dx9 renderer or
			// UI textures won't be recreated on application resizing that triggers Dx9 DeviceLost;
			// this is also recommended for optimal performance in all renderers
			m_UISystem.Advance();
			for (int i = 0; i < m_Views.Count; ++i)
			{
				m_Views[i].Layout();
			}

			if (m_ReadyHandlers.Count > 0)
			{
				foreach (var handler in m_ReadyHandlers)
				{
					handler();
				}
				m_ReadyHandlers.Clear();
			}

			TrackInputFocus();

			SendShiftEventData();

			IsUpdating = false;
		}
	}

	IEnumerator InitializeUISystemRenderer()
	{
		if (m_IsCoroutineStarted)
		{
			yield break;
		}

		m_IsCoroutineStarted = true;

		// Do not recreate the UI System Renderer
		if (m_UISystemRenderer != null)
		{
			yield break;
		}

		// If the UI System is null we can't do anything
		if (m_UISystem == null)
		{
			yield break;
		}

		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.CreateSystemRenderer, 0);

		while (m_UISystemRenderer == null)
		{
			yield return null;
			m_UISystemRenderer = m_UISystem.GetRenderer();
		}

		Debug.Log("[Coherent GT] System renderer initialized.");

		if (SystemReadyHandlers != null)
		{
			SystemReadyHandlers();
		}
	}

	void SetClickThroughPosition(CoherentUIGTView view)
	{
		if (view.View == null)
		{
			return;
		}

		bool isOnSurface = (view.RenderingCamera == null);

		if (!isOnSurface)
		{
			var normX = (Input.mousePosition.x / view.Width);
			var normY = (1 - Input.mousePosition.y / view.Height);

			normX = normX *
				view.WidthToCamWidthRatio(view.RenderingCamera.pixelWidth);

			normY = 1 - ((1 - normY) *
				view.HeightToCamHeightRatio(view.RenderingCamera.pixelHeight));

			if (normX >= 0 && normX <= 1 && normY >= 0 && normY <= 1)
			{
				view.View.SetMouseNormalizedPosition(normX, normY);
			}
		}
		else
		{
			// TODO: Implement raycasting and coordinate transform
			// for click through
		}
	}

	void OnDestroy()
	{
		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.DestroySystemRenderer,	0);

		if (m_UISystemRenderer != null)
		{
			m_UISystemRenderer.Dispose();
			m_UISystemRenderer = null;
		}

		if (CoherentUIGTLibrary.UnityPluginListener != null)
		{
			CoherentUIGTLibrary.UnityPluginListener.UserImageTextureReleased -= OnUserImageTextureReleased;
		}
	}

	void OnApplicationQuit()
	{
		if (UISystemDestroying != null)
		{
			UISystemDestroying();
		}

		OnDestroy();

		foreach(Texture textureAsset in m_PreloadedImageTextureAssetsInUse.Values)
		{
			Resources.UnloadAsset(textureAsset);
		}
		m_PreloadedImageTextureAssetsInUse.Clear();

		foreach(RenderTexture renderTexture in m_LiveViewRenderTexturesForLateRelease.Values)
		{
			renderTexture.Release();
		}
		m_LiveViewRenderTexturesForLateRelease.Clear();

		if (m_UISystem != null)
		{
			CoherentUIGT_Native.UnityOnApplicationQuit();
			m_UISystem.Dispose();
			m_UISystem = null;

			if (m_LocalizationManager != null)
			{
				m_LocalizationManager.Dispose();
				m_LocalizationManager = null;
			}
		}
	}

	public void OnAssemblyReload()
	{
		if (m_UISystem != null)
		{
			for (int i = m_Views.Count - 1; i >= 0; --i)
			{
				m_Views[i].DestroyView();
			}

			Debug.LogWarning("Assembly reload detected. UI System will shut down.");
			OnApplicationQuit();
		}
	}

	public void CheckIfMouseOnUI()
	{
		m_IsMouseOnView = false;

		for (int i = 0; i < m_Views.Count; i++)
		{
			if (m_Views[i].RenderingCamera == null)
			{
				continue;
			}

			if (m_Views[i] != null && m_Views[i].View != null && m_Views[i].ViewRendererComponent != null)
			{
				m_IsMouseOnView |= m_Views[i].View.IsMouseOnView();
			}
		}
	}

	public void UpdateMousePosition()
	{
		Vector2 currentMousePosition = Event.current.mousePosition;

		if (currentMousePosition != m_LastMousePosition)
		{
			if (m_MouseEventData != null && m_Views != null)
			{
				int lastX = (int)m_LastMousePosition.x;
				int lastY = (int)m_LastMousePosition.y;

				m_MouseEventData.Type = MouseEventData.EventType.MouseMove;

				for (int i = 0; i < m_Views.Count; i++)
				{
					if (m_Views[i] != null && m_Views[i].View != null && m_Views[i].ReceivesInput)
					{
						if (m_Views[i].MouseX != -1 && m_Views[i].MouseY != -1)
						{
							//Using view's set coordinates
							m_MouseEventData.X = m_Views[i].MouseX;
							m_MouseEventData.Y = m_Views[i].MouseY;
						}
						else
						{
							//Using screen space for coordinates
							CalculateScaledMouseCoordinates(m_MouseEventData, m_Views[i],
															false);
						}

						m_Views[i].View.MouseEvent(m_MouseEventData);
					}

					//Since we are using a single mouse event for all
					//of the views and CalculateScaledMouseCoordinates
					//mutates the event's X and Y per view, we have to
					//reset the X and Y for the next view

					m_MouseEventData.X = lastX;
					m_MouseEventData.Y = lastY;
				}
			}

			m_LastMousePosition = currentMousePosition;
		}
	}

	protected virtual void OnGUI()
	{
		if (m_Views == null)
		{
			return;
		}

		UpdateMousePosition();

		InputManager.ProcessMouseEvent(m_MouseEventData, Event.current);
		InputManager.ProcessKeyEvent(m_KeyEventData, Event.current);

        if (IsMouseLeftDowSimulation == true)
        {
            m_MouseEventData.Button = MouseEventData.MouseButton.ButtonLeft;
            m_MouseEventData.Type = MouseEventData.EventType.MouseDown;
        }

        if (IsMouseLeftDowSimulation == false)
        {
            m_MouseEventData.Button = MouseEventData.MouseButton.ButtonLeft;
            m_MouseEventData.Type = MouseEventData.EventType.MouseUp;
        }
        SendMouseEventData();
		SendKeyEventData();
	}

	protected void SendMouseEventData()
	{
		if (m_MouseEventData.Type == MouseEventData.EventType.MouseMove)
		{
			return;
		}

		for (int i = 0; i < m_Views.Count; i++)
		{
			var view = m_Views[i].View;

			if (!m_Views[i].ReceivesInput || view == null)
			{
				continue;
			}

			//Set mouse position for click events
			if (m_Views[i].MouseX != -1 && m_Views[i].MouseY != -1)
			{
				//Using view's set coordinates
				m_MouseEventData.X = m_Views[i].MouseX;
				m_MouseEventData.Y = m_Views[i].MouseY;
			}
			else
			{
				//Using screen space for coordinates
				CalculateScaledMouseCoordinates(m_MouseEventData,
				                                m_Views[i],
				                                false);
			}

			view.MouseEvent(m_MouseEventData);
		}
	}

	protected void SendKeyEventData()
	{
		if (m_KeyEventData.Type == KeyEventData.EventType.Unknown)
		{
			return;
		}

		for (int i = 0; i < m_Views.Count; i++)
		{
			var view = m_Views[i].View;

			if (!m_Views[i].ReceivesInput || view == null)
			{
				continue;
			}

			view.KeyEvent(m_KeyEventData);

			if (m_KeyEventData.Type == KeyEventData.EventType.KeyDown)
			{
				//Unity3D doesn't send chars for Escape and Backspace so we send them here
				if (m_KeyEventData.KeyCode == 27 || m_KeyEventData.KeyCode == 8)
				{
					m_KeyEventData.Type = KeyEventData.EventType.Char;
					view.KeyEvent(m_KeyEventData);
				}
			}
		}
	}

	private void SendShiftEventData()
	{
		if (InputManager.ProcessShiftKey(m_KeyEventData,
			Input.GetKey(KeyCode.LeftShift) ||
			Input.GetKey(KeyCode.RightShift)))
		{
			SendKeyEventData();
		}
	}

	private void CalculateScaledMouseCoordinates(MouseEventData data,
												 CoherentUIGTView view,
												 bool invertY)
	{
		float camWidth;
		float camHeight;

		var isOnSurface = (view.RenderingCamera == null);

		if (!isOnSurface)
		{
			Camera cameraComponent = view.RenderingCamera;
			camWidth = cameraComponent.pixelWidth;
			camHeight = cameraComponent.pixelHeight;
		}
		else
		{
			camWidth = view.Width;
			camHeight = view.Height;
		}

		float factorX = view.WidthToCamWidthRatio(camWidth);
		float factorY = view.HeightToCamHeightRatio(camHeight);

		float y = (invertY) ? (camHeight - data.Y) : data.Y;

		data.X = (int)(data.X * factorX);
		data.Y = (int)(y * factorY);
	}

	/// <summary>
	/// Gets the user interface system.
	/// </summary>
	/// <value>
	/// The user interface system.
	/// </value>
	public UISystem UISystem
	{
		get
		{
			return m_UISystem;
		}
	}

	bool m_HasClickOnCameraView = false;

	/// <summary>
	/// Indicates whether the mouse is over UI.
	/// </summary>
	/// <value>
	/// True when mouse is over UI, false otherwise.
	/// </value>
	public bool IsMouseOnView
	{
		get
		{
			return m_IsMouseOnView;
		}
	}
	bool m_IsMouseOnView = false;
}

#if UNITY_2_6 || UNITY_2_6_1 || UNITY_3_0 || UNITY_3_0_0 || UNITY_3_1 || UNITY_3_2 || UNITY_3_3 || UNITY_3_4 || UNITY_3_5
#define COHERENT_UNITY_PRE_4_0
#endif

#if COHERENT_UNITY_PRE_4_0 || UNITY_4_0 || UNITY_4_0_1
#define COHERENT_UNITY_PRE_4_1
#endif

using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using IntPtr = System.IntPtr;

using Coherent.UIGT;

/// <summary>
/// Component containing a Coherent GT view.
/// </summary>
[DisallowMultipleComponent]
[AddComponentMenu("Coherent GT/Coherent GT View")]
public class CoherentUIGTView : MonoBehaviour
{
	#if !UNITY_EDITOR && UNITY_STANDALONE_WIN
	static CoherentUIGTView()
	{
		CoherentUIGTLibrary.SetDependenciesPath();
	}
	#endif
	private ResourceHandler m_ResourceHandler;

	/// <summary>
	/// Creates the ResourceHandler instance for the system. Change to allow usage of custom ResourceHandler
	/// </summary>
	public static System.Func<ResourceHandler> ResourceHandlerFactoryFunc = () =>
	{
		return new UnityGTResourceHandler();
	};

	[HideInInspector]
	[SerializeField]
	private string m_Page = "http://google.com/";

	/// <summary>
	/// Gets or sets the URL of the view
	/// </summary>
	/// <value>
	/// The loaded URL of view
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
							PrettyName = "URL",
							Tooltip="Indicates the URL that will be initially loaded",
							IsStatic=false)]
	public string Page
	{
		get
		{
			return m_Page;
		}
		set
		{
			if (m_Page == value || value == null)
			{
				return;
			}
			m_Page = value;
			var view = View;
			if (view != null)
			{
				view.LoadURL(m_Page);
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private int m_Width = 1024;
	/// <summary>
	/// Gets or sets the width of the view.
	/// </summary>
	/// <value>
	/// The width.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
							PrettyName = "Width",
							Tooltip="Indicates the width of the View",
							IsStatic=false)]
	public int Width
	{
		get
		{
			return m_Width;
		}
		set
		{
			if (m_Width == value)
			{
				return;
			}
			m_Width = value;
			Resize(m_Width, m_Height);
		}
	}

	[HideInInspector]
	[SerializeField]
	private int m_Height = 512;
	/// <summary>
	/// Gets or sets the height of the view.
	/// </summary>
	/// <value>
	/// The height.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
							PrettyName = "Height",
							Tooltip="Indicates the height of the View",
							IsStatic=false)]
	public int Height
	{
		get
		{
			return m_Height;
		}
		set
		{
			if (m_Height == value)
			{
				return;
			}
			m_Height = value;
			Resize(m_Width, m_Height);
		}
	}

	[HideInInspector]
	[SerializeField]
	private string m_InitialScript;

	/// <summary>
	/// Gets or sets the initial JavaScript code to be executed when the view JavaScript engine is created.
	/// </summary>
	/// <value>
	/// The script.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Scripting,
							PrettyName = "Pre-load script",
							Tooltip="The script will be executed before any other code in the GT View",
							IsStatic=true)]
	public string InitialScript
	{
		get
		{
			return m_InitialScript;
		}
		set
		{
			m_InitialScript = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_ClickToFocus = false;
	/// <summary>
	/// When enabled, allows a view to take input focus when clicked with the left mouse button.
	/// </summary>
	/// <value>
	/// <c>true</c> if this view takes input focus when clicked; otherwise, <c>false</c>.
	/// </value>
	[CoherentUIGTExposePropertyStandalone(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Input,
									  PrettyName = "Lockable focus",
									  Tooltip="Users should click on a View for it to become focused",
									  IsStatic=true)]
	public bool ClickToFocus
	{
		get
		{
			return m_ClickToFocus;
		}
		set
		{
			m_ClickToFocus = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_DrawAsPostEffect;
	/// <summary>
	/// Gets or sets a value indicating whether this view is drawn after post effects.
	/// </summary>
	/// <value>
	/// <c>AfterPostEffects</c> if the view is drawn after post effects; otherwise, <c>false</c>.
	/// </value>
	/// <exception cref='System.ApplicationException'>
	/// Is thrown when the application exception.
	/// </exception>
	[CoherentUIGTExposePropertyStandalone(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Rendering,
									  PrettyName = "Post Effects",
									  Tooltip="Enable when using post effects (camera Views only)",
									  IsStatic=true)]
	public bool DrawAsPostEffect
	{
		get
		{
			return m_DrawAsPostEffect;
		}
		set
		{
			if (m_DrawAsPostEffect == value)
			{
				return;
			}
			#if !UNITY_EDITOR
			if (View != null)
			{
				throw new System.ApplicationException("The draw order of a View can't be modified if it's already been created");
			}
			#endif
			m_DrawAsPostEffect = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private float m_ClickThroughThreshold = 0.0f;
	/// <summary>
	/// Gets or sets the click-through threshold for transparent views.
	/// </summary>
	/// <value>
	/// The threshold value.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Rendering,
								PrettyName = "Click-through threshold",
								Tooltip="Indicates the click-through threshold for the View",
								IsStatic=true)]
	public float ClickThroughThreshold
	{
		get
		{
			return m_ClickThroughThreshold;
		}
		set
		{
			if (m_ClickThroughThreshold == value)
			{
				return;
			}
			m_ClickThroughThreshold = value;
		}
	}

	[HideInInspector]
	[SerializeField]
    private bool m_FlipY = false;
    /// <summary>
    /// Gets or sets a value indicating whether the Y axis of this view should be flipped.
    /// </summary>
    /// <value>
    /// <c>true</c> if the Y axis is flipped; otherwise, <c>false</c>.
    /// </value>
    [CoherentUIGTExposePropertyStandalone(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Rendering,
									  PrettyName = "Flip Y",
									  Tooltip="Will flip the View vertically",
									  IsStatic=true)]
	public bool FlipY
	{
		get
		{
			return m_FlipY;
		}
		set
		{
			m_FlipY = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_ReceivesInput = false;

	/// <summary>
	/// Gets or sets a value indicating whether this view receives input.
	/// All automatic processing and reading of this property is done in the
	/// `LateUpdate()` / `OnGUI()` callbacks in Unity, letting you do all your logic
	/// for View focus in `Update()`.
	/// </summary>
	/// <value>
	/// <c>true</c> if this view receives input; otherwise, <c>false</c>.
	/// </value>
	public virtual bool ReceivesInput
	{
		get
		{
			return m_ReceivesInput;
		}
		set
		{
			if (m_UISystem != null && !m_UISystem.IsUpdating && this.ClickToFocus)
			{
				Debug.LogWarning("You're setting the CoherentGTView.ReceivesInput property on a view that manages input focus on its own. " +
								 "To avoid this, before setting the ReceivesInput property, check if the ClickToFocus property is set to false.");
			}
			m_ReceivesInput = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_InterceptAllEvents = false;
	/// <summary>
	/// Gets or sets a value indicating whether this view intercepts all events and sends a message for each event.
	/// </summary>
	/// <value>
	/// <c>true</c> if view intercepts all events; otherwise, <c>false</c>.
	/// </value>
	/// <exception cref='System.ApplicationException'>
	/// Is thrown when the property is modified and the view has already been created
	/// </exception>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Scripting,
							PrettyName = "Auto UI messages",
							Tooltip="Events triggered in the UI will fire Unity messages",
							IsStatic=true)]
	public bool InterceptAllEvents
	{
		get
		{
			return m_InterceptAllEvents;
		}
		set
		{
			if (m_InterceptAllEvents == value)
			{
				return;
			}
			#if !UNITY_EDITOR
			if (View != null)
			{
				throw new System.ApplicationException("Intercepting all events can't be changed if the view has already been created");
			}
			#endif
			m_InterceptAllEvents = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_IsTransparent = true;
	/// <summary>
	/// Gets or sets a value indicating whether this view supports transparency.
	/// </summary>
	/// <value>
	/// <c>true</c> if view supports transparency; otherwise, <c>false</c>.
	/// </value>
	/// <exception cref='System.ApplicationException'>
	/// Is thrown when the property is modified and the view has already been created
	/// </exception>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Rendering,
							PrettyName = "Is transparent",
							Tooltip="Events triggered in the UI will fire Unity messages",
							IsStatic=true)]
	public bool IsTransparent
	{
		get
		{
			return m_IsTransparent;
		}
		set
		{
			if (m_IsTransparent == value)
			{
				return;
			}
			#if !UNITY_EDITOR
			if (View != null)
			{
				throw new System.ApplicationException("Transparency can't be changed if the view has already been created");
			}
			#endif
			m_IsTransparent = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_EnableBindingAttribute = true;
	/// <summary>
	/// Gets or sets a value indicating whether this <see cref="CoherentUIView"/> enables usage of the CoherentMethod attribute
	/// in components in the host GameObject.
	/// When true, the all components in the host GameObject are inspected for the CoherentMethod attribute (in the Awake() function)
	/// and the decorated methods are automatically bound when the ReadyForBindings event is received.
	/// When false, the attribute does nothing.
	/// </summary>
	/// <value>
	/// <c>true</c> if usage of the CoherentMethod is enabled; otherwise, <c>false</c>.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Scripting,
							PrettyName = "Enable [CoherentMethodAttribute]",
							Tooltip="Allows automatic binding of methods to the UI",
							IsStatic=true)]
	public bool EnableBindingAttribute
	{
		get
		{
			return m_EnableBindingAttribute;
		}
		set
		{
			m_EnableBindingAttribute = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_IsIndependentOfZBuffer = false;
	/// <summary>
	/// Gets or sets a value indicating whether this view is z-buffer independent. If it is set to true, the view is rendered on top of everything.
	/// </summary>
	/// <value>
	/// <c>true</c> if it is independent; otherwise <c>false</c>.
	/// </value>
	[CoherentUIGTExposePropertyStandalone(Category = CoherentUIGTExposePropertyInfo.FoldoutType.AdvancedRendering,
									  PrettyName = "Always on top",
									  Tooltip="Ignores the content of the depth buffer",
									  IsStatic=true)]
	public bool IsIndependentOfZBuffer
	{
		get
		{
			return m_IsIndependentOfZBuffer;
		}
		set
		{
			m_IsIndependentOfZBuffer = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_UseCameraDimensions = true;
	/// <summary>
	/// If checked, the view will use the camera's width and height
	/// </summary>
	/// <value>
	/// <c>true</c> if we want to use camera's width and height; otherwise <c>false</c>.
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.Rendering,
							PrettyName = "Match camera size",
							Tooltip="The View will be automatically resized to always match the size of the camera",
							IsStatic=true)]
	public bool UseCameraDimensions
	{
		get
		{
			return m_UseCameraDimensions;
		}
		set
		{
			m_UseCameraDimensions = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private bool m_CorrectGamma = false;
	/// <summary>
	/// Gets or sets a value indicating whether this view should have gamma
	/// corrected.
	/// </summary>
	/// <value>
	/// <c>true</c> if gamma is corrected; otherwise, <c>false</c>.
	/// </value>
	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.AdvancedRendering,
		PrettyName = "Compensate gamma",
		Tooltip="The view will compensate for gamma correction",
		IsStatic=true)]
	// Note: Unused at the moment, please use the RenderTexture format
	private bool CorrectGamma
	{
		get
		{
			return m_CorrectGamma;
		}
		set
		{
			m_CorrectGamma = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private ColorSpace m_RenderTextureFormat = ColorSpace.Uninitialized;
	/// <summary>
	/// Gets or sets the RenderTexture format where Coherent GT draws.
	/// </summary>
	/// <value>
	/// <c>Uninitialized</c> if the texture uses the default format.
	/// <c>sRGB</c> if the texture uses the sRGB color space.
	/// <c>Linear</c> if the texture uses the linear color space.
	/// </value>
	/// <see cref="http://docs.unity3d.com/ScriptReference/RenderTextureReadWrite.html"/>
	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.AdvancedRendering,
		PrettyName = "Texture format",
		Tooltip="The format of the RenderTexture",
		IsStatic=true)]
	public ColorSpace GTRenderTextureFormat
	{
		get
		{
			return m_RenderTextureFormat;
		}
		set
		{
			m_RenderTextureFormat = value;
		}
	}

	[HideInInspector]
	[SerializeField]
	private AudioSource m_AudioSource;
	/// <summary>
	/// Gets or sets the AudioSource for the Coherent View.
	/// </summary>
	/// <value>
	/// The AudioSource component
	/// </value>
	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "AudioSource",
		Tooltip="The AudioSource for the the Coherent View",
		IsStatic=true)]
	public AudioSource AudioSourceComponent
	{
		get
		{
			if (m_AudioSource == null && Application.isPlaying)
			{
				m_AudioSource = GetComponent<AudioSource>();

				if (m_AudioSource == null)
				{
					m_AudioSource = gameObject.AddComponent<AudioSource>();
				}
			}

			return m_AudioSource;
		}
		set
		{
			m_AudioSource = value;
		}
	}
	#if !UNITY_5
	[HideInInspector]
	[SerializeField]
	private bool m_IsAudio3D;
	/// <summary>
	/// Gets or sets a value indicating whether the sound from the view
	/// should be 3D.
	/// </summary>
	/// <value>
	/// <c>true</c> if we want the sound to be 3D; otherwise <c>false</c>.
	/// </value>
	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "3D Audio",
		Tooltip="Is audio 3D",
		IsStatic=true)]
	public bool IsAudio3D
	{
		get
		{
			return m_IsAudio3D;
		}
		set
		{
			m_IsAudio3D = value;
		}
	}
	#endif

	private UnityGTViewListener m_Listener;
	/// <summary>
	/// Gets the underlying UnityViewListener for this view.
	/// </summary>
	/// <value>
	/// The listener.
	/// </value>
	public UnityGTViewListener Listener
	{
		get
		{
			return m_Listener;
		}
	}
	private Camera m_Camera;
	/// <summary>
	/// Gets the camera the view is rendering on. If it isn't rendering on camera returns null.
	/// </summary>
	public Camera RenderingCamera
	{
		get
		{
			return m_Camera;
		}
	}

	private CoherentUIGTSystem m_UISystem;
	private bool m_QueueCreateView = false;

	internal int MouseX { get; set; }
	internal int MouseY { get; set; }

	public bool IsReadyForBindings { get; private set; }

	private List<CoherentUIGTMethodBindingInfo> m_CoherentMethods;

	private AudioClip m_AudioClip;
	private int m_BufferPosition;
	private bool m_AutoPlayOnDataReceived;
	private const int m_MaxBufferSize = 8192;
	private const int m_PreBufferSize = 2048;
	private int ptrSize;
	private bool m_IsCoroutineStarted = false;
	private float[] m_AudioBuffer;
	private float[] m_AudioClipData;


	#if UNITY_EDITOR
	internal HashSet<string> m_ResourcesInUse;

	public bool CheckResourcesUsedByView(string[] changedFiles)
	{
		return m_ResourcesInUse.Overlaps(changedFiles);
	}

	[HideInInspector]
	[SerializeField]
	private bool m_AutoRefresh = false;
	/// <summary>
	/// Automatically reloads the view if any of its resources are changed.
	/// </summary>
	/// <value>
	/// <c>true</c> to enable auto refresh; otherwise, <c>false</c>.
	/// </value>
	[CoherentUIGTExposePropertyStandalone(
		Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Auto Refresh",
		Tooltip = "Automatically reloads the view if any of its resources are changed",
		IsStatic = false)]
	public bool AutoRefresh
	{
		get
		{
			return m_AutoRefresh;
		}
		set
		{
			m_AutoRefresh = value;
		}
	}
	#endif

	// Invert flipping on camera-attached views on OpenGL
	internal bool ForceInvertY()
	{
		return m_Camera != null
			&& SystemInfo.graphicsDeviceVersion.Contains("Open")
				&& Application.platform != RuntimePlatform.WindowsEditor;
	}

	/// <summary>
	/// Sets the mouse position. Note UI coordinates (0,0) is the upper left corner of the screen.
	/// </summary>
	/// <param name='x'>
	/// X coordinate of the mouse.
	/// </param>
	/// <param name='y'>
	/// Y coordinate of the mouse.
	/// </param>
	public void SetMousePosition(int x, int y)
	{
		MouseX = x;
		MouseY = y;

		if (FlipY)
		{
			MouseY = Height - MouseY;
		}
	}
    public void SetMousePosition(int x, int y, string flip)
    {
        MouseX = x;
        MouseY = y;

        if (flip != null)
        {
            if (flip == "y")
            {
                MouseY = Height - MouseY;
            }

            if (flip == "x")
            {
                MouseX = Width - MouseX;
            }
        }
    }



    void Awake ()
	{
		MouseX = -1;
		MouseY = -1;

		m_UISystem = CoherentUIGTSystem.CurrentUISystem;

		m_ObjectsToDestroy = new List<UnityEngine.Object> ();

		m_CoherentMethods = new List<CoherentUIGTMethodBindingInfo>();
		if (EnableBindingAttribute)
		{
			RegisterMethodsForBinding();
		}
		m_Listener = new UnityGTViewListener(this, this.m_Width, this.m_Height);
		m_Listener.ReadyForBindings += this.ReadyForBindings;
		m_Listener.BindingsReleased += this.BindingsReleased;
		IsReadyForBindings = false;

		m_Camera = GetComponent<Camera>();

		m_UISystem.UISystemDestroying += OnDestroy;
		m_UISystem.SystemReady += OnSystemReady;
		m_UISystem.AddView(this);

		m_Listener.AudioStreamCreated += CreateStream;
		m_Listener.AudioDataReceived += ReceiveDataForStream;
		m_Listener.AudioStreamPlay += AutoPlayStream;
		m_Listener.AudioStreamPause += StopStream;
		m_Listener.AudioStreamEnded += StopStream;
		m_Listener.AudioStreamClosed += DestroyStream;

		ptrSize = Marshal.SizeOf(typeof(IntPtr));
	}

	void CreateStream(int id, int bitDepth, int channels, float samplingRate)
	{
		#if UNITY_5
		m_AudioClip = AudioClip.Create("CoherentAudioStream" + id,
			                           (int)samplingRate,
			                           channels,
			                           (int)samplingRate,
			                           false);
		#else
		m_AudioClip = AudioClip.Create("CoherentAudioStream" + id,
			                           (int)samplingRate,
			                           channels,
			                           (int)samplingRate,
			                           m_IsAudio3D,
			                           false);
		#endif

		m_AudioBuffer = new float[channels * m_PreBufferSize];
		m_AudioClipData = new float[channels * (int)samplingRate];

		AudioSourceComponent.clip = m_AudioClip;
	}

	void ReceiveDataForStream(int id, int samples, IntPtr pcm, int channels)
	{
		int receivedSamples = channels * samples;

		if (receivedSamples > m_AudioBuffer.Length)
		{
			m_AudioBuffer = new float[receivedSamples];
		}

		for (int i = 0; i < channels; i++)
		{
			IntPtr channelData = Marshal.ReadIntPtr(pcm, i * ptrSize);
			Marshal.Copy(channelData, m_AudioBuffer, i * samples, samples);
		}
			
		if ((m_BufferPosition * channels) + receivedSamples > m_AudioClipData.Length)
		{
			int partOneSamples = m_AudioClipData.Length / channels - m_BufferPosition;
			int partTwoSamples = samples - partOneSamples;

			for (int i = 0; i < partOneSamples; i++)
			{
				for (int j = 0; j < channels; j++)
				{
					m_AudioClipData[(m_BufferPosition + i) * channels + j] = m_AudioBuffer[i + j * samples];
				}
			}

			for (int i = 0; i < partTwoSamples; i++)
			{
				for (int j = 0; j < channels; j++)
				{
					m_AudioClipData[i * channels + j] = m_AudioBuffer[partOneSamples + i + j * samples];
				}
			}
		}
		else
		{
			for (int i = 0; i < samples; i++)
			{
				for (int j = 0; j < channels; j++)
				{
					m_AudioClipData[(m_BufferPosition + i) * channels + j] = m_AudioBuffer[i + j * samples];
				}
			}
		}

		m_AudioClip.SetData(m_AudioClipData, 0);

		bool inSync = m_BufferPosition >= m_AudioSource.timeSamples &&
					  m_BufferPosition - m_AudioSource.timeSamples < m_MaxBufferSize ||
					  m_BufferPosition < m_AudioSource.timeSamples &&
					  (m_AudioClip.samples - m_AudioSource.timeSamples) + m_BufferPosition < m_MaxBufferSize;

		if (!inSync)
		{
			Debug.LogWarning("[Coherent GT] Audio playback was out of sync with the video. " +
				"Synchronizing audio now, this may cause a skip.");
			m_BufferPosition = AudioSourceComponent.timeSamples + m_PreBufferSize;
		}

		m_BufferPosition += samples;

		if (m_BufferPosition >= m_AudioClip.samples)
		{
			m_BufferPosition -= m_AudioClip.samples;
		}

		if (!AudioSourceComponent.isPlaying &&
		   m_AutoPlayOnDataReceived &&
		   m_BufferPosition > m_PreBufferSize)
		{
			AudioSourceComponent.Play();
			m_AutoPlayOnDataReceived = false;
		}
	}

	void AutoPlayStream(int id)
	{
		#if !COHERENT_UNITY_PRE_4_1
		AudioSourceComponent.ignoreListenerPause = true;
		#endif
		AudioSourceComponent.loop = true;

		m_AutoPlayOnDataReceived = true;
	}

	void StopStream(int id)
	{
		AudioSourceComponent.Stop();
		m_BufferPosition = 0;
	}

	void DestroyStream(int id)
	{
		StopStream(id);
		Destroy(m_AudioClip);
	}

	private void RegisterMethodsForBinding()
	{
		List<CoherentUIGTMethodBindingInfo> methods = CoherentUIGTMethodHelper.GetCoherentMethodsInGameObject(this.gameObject);
		m_CoherentMethods.AddRange(methods);
	}

	private void ReadyForBindings()
	{
		foreach (var item in m_CoherentMethods)
		{
			if (item.IsEvent)
			{
				View.RegisterForEvent(item.ScriptEventName, item.BoundFunction);
			}
			else
			{
				View.BindCall(item.ScriptEventName, item.BoundFunction);
			}
		}

		if (m_InterceptAllEvents)
		{
			View.RegisterForEvent("all", (System.Action<string, Value[]>)this.InterceptEvent);
		}

		IsReadyForBindings = true;
	}

	void BindingsReleased()
	{
		IsReadyForBindings = false;
	}

	private void InterceptEvent(string name, Value[] arguments)
	{
		SendMessage(name, arguments, SendMessageOptions.DontRequireReceiver);
	}

	IEnumerator SendCreateView()
	{
		if (m_IsCoroutineStarted)
		{
			yield break;
		}

		m_IsCoroutineStarted = true;

		if (string.IsNullOrEmpty(Page))
		{
			throw new System.ApplicationException("The Page of a view must not be null or empty.");
		}

		if (ResourceHandlerFactoryFunc != null)
		{
			m_ResourceHandler = ResourceHandlerFactoryFunc();
		}

		if (m_ResourceHandler == null)
		{
			Debug.LogWarning("Unable to create file handler using factory function! Falling back to default handler.");
			m_ResourceHandler = new UnityGTResourceHandler();
		}

		ViewLoadPolicy policy = ViewLoadPolicy.VLP_UseCacheOrLoad;

		#if UNITY_EDITOR
		m_ResourcesInUse = new HashSet<string>();
		m_ResourceHandler = new UnityGTResourceHandlerDecorator(m_ResourceHandler, this);

		if (AutoRefresh)
		{
			policy = ViewLoadPolicy.VLP_IgnoreCache;
		}
		#endif

		var viewInfo = new ViewInfo();
		viewInfo.Width = (uint)this.m_Width;
		viewInfo.Height = (uint)this.m_Height;
		viewInfo.IsTransparent = this.m_IsTransparent;
		viewInfo.ViewListenerInstance = m_Listener;
		viewInfo.ResourceHandlerInstance = m_ResourceHandler;
		viewInfo.ClickThroughAlphaThreshold = ClickThroughThreshold;

		if (string.IsNullOrEmpty(InitialScript))
		{
			View = m_UISystem.UISystem.CreateView(viewInfo, Page, policy);
		}
		else
		{
			View = m_UISystem.UISystem.CreateView(viewInfo, Page, InitialScript, policy);
		}

		RecreateViewTexture(m_Width, m_Height);

		View.SetNewRenderTarget(ViewTexture.GetNativeTexturePtr(),
			DepthTexture.GetNativeTexturePtr(),
			(uint)m_Width, (uint)m_Height, 1);

		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.CreateViewRenderer,
			View.GetId());

		while(ViewRenderer == null)
		{
			ViewRenderer = View.GetViewRenderer();
			yield return null;
		}

		AddViewRendererComponent();
	}

	void RecreateViewTexture(int width, int height)
	{
		RenderTextureReadWrite textureReadWrite =
			RenderTextureReadWrite.Default;
		if (GTRenderTextureFormat == ColorSpace.Gamma)
		{
			textureReadWrite = RenderTextureReadWrite.sRGB;
		}
		else if (GTRenderTextureFormat == ColorSpace.Linear)
		{
			textureReadWrite = RenderTextureReadWrite.Linear;
		}

		var rt = new RenderTexture (
			width, height, 0,
			RenderTextureFormat.ARGB32,
			textureReadWrite);
		rt.name = "CoherentRenderingRTT" + View.GetId();
		rt.Create ();

		#if !UNITY_EDITOR_WIN && !UNITY_STANDALONE_WIN
		RenderTexture current = RenderTexture.active;
		RenderTexture.active = rt;
		GL.Clear(true, true, Color.clear);
		RenderTexture.active = current;
		#endif
		ViewTexture = rt;

		var dt = new RenderTexture (
			width, height, 24,
			RenderTextureFormat.Depth,
			textureReadWrite);
		dt.name = "CoherentRenderingRTD" + View.GetId();
		dt.Create ();
		DepthTexture = dt;

		if (RenderMaterial != null)
		{
			RenderMaterial.SetTexture("_MainTex", ViewTexture);
		}
	}

	private Material RenderMaterial;

	void AddViewRendererComponent ()
	{
		View.SetFocus();

		var id = View.GetId();

		if (m_Camera != null)
		{
			ViewRendererComponent = gameObject.AddComponent<CoherentUIGTViewRenderer>();

			Shader shader = Shader.Find("Coherent/ViewShader");
			if (shader == null)
			{
				Debug.LogError("No shader found");
			}

			RenderMaterial = new Material(shader);
			RenderMaterial.SetTexture("_MainTex", ViewTexture);

			// make sure added components are destroyed too
			m_ObjectsToDestroy.Add(ViewRendererComponent);
		}
		else
		{
			ViewRendererComponent = GetComponent<CoherentUIGTViewRenderer>();

			if (ViewRendererComponent == null)
			{
				ViewRendererComponent = gameObject.AddComponent<CoherentUIGTViewRenderer>();
			}

			m_ObjectsToDestroy.Add(ViewRendererComponent);

			Shader shader = Shader.Find(IsTransparent
										? "Coherent/TransparentDiffuse"
										: "Coherent/Diffuse");
			if (shader == null)
			{
				Debug.LogError("No shader found");
			}

			RenderMaterial = new Material(shader);

			RenderMaterial.SetTexture("_MainTex", ViewTexture);
			RenderMaterial.name = "CoherentMaterialRTT" + id;
			GetComponent<Renderer>().material = RenderMaterial;
		}
		m_ObjectsToDestroy.Add(RenderMaterial);

		ViewRendererComponent.ViewId = (ushort)id;

		var shaderKeywords = new List<string>();

		if (FlipY)
		{
			shaderKeywords.Add ("COHERENT_FLIP_Y");
		}

		if (CorrectGamma
			&& CoherentUIGTSystem.GetUnityRendererType() == CoherentUIGTSystem.UnityRendererType.D3D9)
		{
			shaderKeywords.Add ("COHERENT_CORRECT_GAMMA");
		}

		#if COHERENT_UNITY_PRE_4_1
			ViewRendererComponent.ShaderKeywords = shaderKeywords;
		#else
			RenderMaterial.shaderKeywords = shaderKeywords.ToArray();
		#endif
	}

	private void OnSystemReady()
	{
		if (this == null)
		{
			return;
		}

		if (this.enabled)
		{
			StartCoroutine(SendCreateView());
		}
		else
		{
			m_QueueCreateView = true;
		}
	}

	void Update ()
	{
		if (m_QueueCreateView && this.enabled)
		{
			StartCoroutine(SendCreateView());
			m_QueueCreateView = false;
		}
	}

	bool m_WasLastFrameFullscreen;
	public void Layout()
	{
		// Layout is called on CoherentUIGTSystem component Update
		// because UISystem.Advance() should happen before View.Layout()
		var view = View;
		if (!enabled || view == null)
		{
			return;
		}

		if ((!ViewTexture.IsCreated() || !DepthTexture.IsCreated())
			&& Screen.fullScreen == m_WasLastFrameFullscreen)
		{
			// Make sure that we don't call RecreateRenderTarget twice when transitioning from
			// Fullscreen to Windowed mode and vice versa; calling RecreateRenderTarget second
			// time sets wrong render texture target that leads to missing UI on the screen

			Debug.Log("[Debug] No render texture present. Recreating it");
			RecreateRenderTarget();
		}
		m_WasLastFrameFullscreen = Screen.fullScreen;

		if (m_UseCameraDimensions)
		{
			int width = 0;
			int height = 0;
			if (GetCamDimensions(out width, out height))
			{
				if (width != m_Width || height != m_Height)
				{
					Resize(width, height);
				}
			}
		}

		if (ViewRenderer != null)
		{
			view.Layout();
		}
	}

	void OnPostRender()
	{
		if (DrawAsPostEffect)
		{
			return;
		}

		if (ViewTexture)
		{
			#if COHERENT_UNITY_PRE_4_1
			ViewRendererComponent.EnableShaderKeywords();
			#endif

			GL.PushMatrix();
			GL.LoadPixelMatrix(0, Screen.width, Screen.height, 0);
			Graphics.DrawTexture(new Rect(0f, 0, Screen.width, Screen.height),
			                     ViewTexture,
			                     new Rect(0f, 0f, 1f, 1f),
			                     0, 0, 0, 0,
			                     Color.white,
			                     RenderMaterial);
			GL.PopMatrix();
		}
	}

	void OnRenderImage(RenderTexture src, RenderTexture dest)
	{
		Graphics.Blit(src, dest);

		if (DrawAsPostEffect && ViewTexture && RenderMaterial)
		{
			Graphics.Blit(ViewTexture, dest, RenderMaterial);
		}
	}

	void OnDisable()
	{
		if (ViewRendererComponent != null)
		{
			ViewRendererComponent.IsActive = false;
		}
	}

	void OnEnable()
	{
		if (ViewRendererComponent != null)
		{
			ViewRendererComponent.IsActive = true;
		}
	}

	void OnDestroy()
	{
		m_UISystem.UISystemDestroying -= OnDestroy;
		m_UISystem.SystemReady -= OnSystemReady;

		foreach (var o in m_ObjectsToDestroy)
		{
			UnityEngine.Object.Destroy(o);
		}
		m_ObjectsToDestroy.Clear();

		m_UISystem.RemoveView(this);

		if (View != null)
		{
			// The ViewRenderer must be destroyed immediately to avoid
			// an assertion in Unity3D.
			// It would be more correct to do that in the render thread
			// using ViewRendererComponent.DestroyViewRenderer(), but
			// Unity quits too fast and the destruction never happens,
			// leading to an assert.
			CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.DestroyViewRenderer,
				View.GetId());

			if (ViewRenderer != null)
			{
				ViewRenderer.Dispose();
				ViewRenderer = null;
			}

			View.Destroy();
			View.Dispose();
			View = null;

			ViewTexture = null;
			DepthTexture = null;
		}

		if (m_Listener != null)
		{
			m_Listener.Dispose();
			m_Listener = null;
		}

		if (OnViewDestroyed != null)
		{
			OnViewDestroyed();
		}

		if (m_AudioClip != null)
		{
			DestroyStream(0);
		}
	}

	/// <summary>
	/// Destroy this view. Destroys the Coherent GT view and removes the CoherentUIView
	/// component from its game object. Any usage of the view after this method is
	/// undefined behaviour.
	/// </summary>
	public void DestroyView()
	{
		OnDestroy();
		UnityEngine.Object.Destroy(this);
	}

	/// <summary>
	/// Handler for ViewDestroyed event.
	/// </summary>
	public delegate void ViewDestroyedHandler();

	/// <summary>
	/// Occurs when the view has been destroyed and the CoherentUIView component
	/// is going to be removed from the game object.
	/// </summary>
	public event ViewDestroyedHandler OnViewDestroyed;


	/// <summary>
	/// Resize the view to the specified width and height.
	/// </summary>
	/// <param name='width'>
	/// New width for the view.
	/// </param>
	/// <param name='height'>
	/// New height for the view.
	/// </param>
	public void Resize(int width, int height)
	{
		m_Width = width;
		m_Height = height;
		var view = View;
		if (view != null)
		{
			view.Resize(width, height);
			RecreateRenderTarget();
		}
	}

	internal void RecreateRenderTarget()
	{
		RecreateViewTexture(Width, Height);
		View.SetNewRenderTarget(
			ViewTexture.GetNativeTexturePtr(), DepthTexture.GetNativeTexturePtr(),
			(uint)Width, (uint)Height, 1);

		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.SetNewRenderTarget,
			View.GetId());
	}

	/// <summary>
	/// Gets the underlying View instance.
	/// </summary>
	/// <value>
	/// The underlying View instance.
	/// </value>
	public View View
	{
		get;
		private set;
	}

	public ViewRenderer ViewRenderer
	{
		get;
		private set;
	}

	public RenderTexture ViewTexture
	{
		get;
		private set;
	}

	public RenderTexture DepthTexture
	{
		get;
		private set;
	}

	private List<Object> m_ObjectsToDestroy;
	public CoherentUIGTViewRenderer ViewRendererComponent
	{
		get;
		private set;
	}

	/// <summary>
	/// Request redraw of this view.
	/// </summary>
	public void Redraw()
	{
		var view = View;
		if (view != null)
		{
			view.RedrawAll();
		}
	}

	/// <summary>
	/// Request reload of this view.
	/// </summary>
	public void Reload()
	{
		var view = View;
		if (view != null)
		{
			view.Reload();
		}
	}

	/// <summary>
	/// Returns the camera dimensions of the current view.
	/// </summary>
	public bool GetCamDimensions(out int x, out int y)
	{
		if (m_Camera != null)
		{
			x = (int)m_Camera.pixelWidth;
			y = (int)m_Camera.pixelHeight;

			return true;
		}
		else if (ViewRendererComponent != null)
		{
			x = m_Width;
			y = m_Height;
			// TODO: Check if this is needed for 3D views
			/*
			GameObject rendererGO = m_Listener.ViewRendererComponent.gameObject;

			if ( rendererGO != null)
			{
				var camera = rendererGO.GetComponent<Camera>();
				x = (int)camera.pixelWidth;
				y = (int)camera.pixelHeight;

				return true;
			}
			*/
		}

		x = -1;
		y = -1;

		return false;
	}

	public float WidthToCamWidthRatio(float camWidth)
	{
		return m_Width / camWidth;
	}

	public float HeightToCamHeightRatio(float camHeight)
	{
		return m_Height / camHeight;
	}
}



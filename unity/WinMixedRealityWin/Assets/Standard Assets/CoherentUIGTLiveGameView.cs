using UnityEngine;
using IntPtr = System.IntPtr;
using System.Collections;
using System.Collections.Generic;

using Coherent.UIGT;

/// <summary>
/// Component that needs to be attached to a camera and creates a Coherent UI Live Game View
/// </summary>
[AddComponentMenu("Coherent GT/Coherent GT Live Game View")]
public class CoherentUIGTLiveGameView : MonoBehaviour
{
	const string JS_RESIZE =
@"(function(){{
var allImages = document.getElementsByTagName('img');
for (var i = 0, len = allImages.length; i < len; i++){{
	var img = allImages[i];
	if (img.src === 'coui://{0}'){{
		img.width = {1};
		img.height = {2};
	}}
}}
}})()";

	private CoherentUIGTSystem m_UISystem;

	[HideInInspector]
	[SerializeField]
	private string m_LiveName = "MyLiveView";

	/// <summary>
	/// Gets or sets the Name of the live view
	/// </summary>
	/// <value>
	/// The Name of the view
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Name",
		Tooltip = "Indicates the Name of the Live View available in the page",
		IsStatic = false)]
	public string LiveName
	{
		get
		{
			return m_LiveName;
		}
		set
		{
			string oldLiveName = m_LiveName;
			m_LiveName = value;
			if (m_UISystem.RegisterLiveViewComponent("coui://" + m_LiveName, this))
			{
				m_UISystem.UnregisterLiveViewComponent("coui://" + oldLiveName);
			}
			else
			{
				Debug.LogError("[Coherent GT] Can't set live view Name to: " +
					m_LiveName + " . Live view Name should be unique!");
				m_LiveName = oldLiveName;
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private int m_Width = 256;

	/// <summary>
	/// Gets or sets the Width of the live view
	/// </summary>
	/// <value>
	/// The width of the view
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Width",
		Tooltip = "Indicates the Width that the Live Game View will have",
		IsStatic = false)]
	public int Width
	{
		get
		{
			return m_Width;
		}
		set
		{
			m_Width = Mathf.Clamp(value, 1, 16384);

			if (Application.isPlaying && isActiveAndEnabled)
			{
				Resize();
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private int m_Height = 256;

	/// <summary>
	/// Gets or sets the Height of the live view
	/// </summary>
	/// <value>
	/// The height of the view
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Height",
		Tooltip = "Indicates the Height that the Live Game View will have",
		IsStatic = false)]
	public int Height
	{
		get
		{
			return m_Height;
		}
		set
		{
			m_Height = Mathf.Clamp(value, 1, 16384);

			if (Application.isPlaying && isActiveAndEnabled)
			{
				Resize();
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private CoherentUIGTView m_TargetView;

	/// <summary>
	/// Gets or sets the Target View component displaying this live view
	/// </summary>
	/// <value>
	/// The Coherent GT View component
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Target View",
		Tooltip = "The Coherent GT View component displaying this live view",
		IsStatic = false)]
	public CoherentUIGTView TargetView
	{
		get
		{
			return m_TargetView;
		}
		set
		{
			if (Application.isPlaying && enabled)
			{
				UnregisterForSizeRequest();
				UnsetAndReleaseLiveViewTexture();
			}

			m_TargetView = value;

			if (Application.isPlaying && enabled)
			{
				OnEnable();
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private Camera m_SourceCamera;

	/// <summary>
	/// Gets or sets the Camera source for the live view
	/// </summary>
	/// <value>
	/// The Camera component
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Source Camera",
		Tooltip = "The Camera that will render the Live Game View",
		IsStatic = false)]
	public Camera SourceCamera
	{
		get
		{
			return m_SourceCamera;
		}
		set
		{
			if (Application.isPlaying && enabled)
			{
				UnsetAndReleaseLiveViewTexture();
			}

			m_SourceTexture = null;
			m_SourceCamera = value;

			if (Application.isPlaying && enabled)
			{
				OnEnable();
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private uint m_ImageHandle;

	[HideInInspector]
	[SerializeField]
	public uint ImageHandle
	{
		get
		{
			return m_ImageHandle;
		}
		set
		{
			if (m_ImageHandle == 0)
			{
				m_ImageHandle = value;
			}
		}
	}

	[HideInInspector]
	[SerializeField]
	private RenderTexture m_SourceTexture;

	/// <summary>
	/// Gets or sets the source texture for the live view
	/// </summary>
	/// <value>
	/// The Texture object
	/// </value>
	[CoherentUIGTExposeProperty(Category = CoherentUIGTExposePropertyInfo.FoldoutType.General,
		PrettyName = "Source Texture",
		Tooltip = "A texture used as source for the Live View",
		IsStatic = false)]
	public RenderTexture SourceTexture
	{
		get
		{
			return m_SourceTexture;
		}
		set
		{
			if (Application.isPlaying && enabled)
			{
				UnsetAndReleaseLiveViewTexture();
			}

			if (value != null)
			{
				m_SourceCamera = null;
			}

			m_SourceTexture = value;

			if (Application.isPlaying && enabled)
			{
				OnEnable();
			}
		}
	}

	bool m_IsRegisteredForSystemReady;
	bool m_IsRegisteredForSizeRequest;
	bool m_IsLiveViewTextureSet;
	bool m_IsDisabling;
	uint m_ResetLiveViewTextureCounter;

	void Awake()
	{
		m_UISystem = CoherentUIGTSystem.CurrentUISystem;

		if (m_SourceTexture == null && m_SourceCamera == null)
		{
			var cameraComponent = GetComponent<Camera>();

			if (cameraComponent != null && cameraComponent != Camera.main)
			{
				m_SourceCamera = cameraComponent;
			}
		}
	}

	void Start()
	{
		if (m_TargetView != null)
		{
			m_TargetView.Listener.LiveViewActivate += OnLiveViewActivate;
		}
		m_UISystem.RegisterLiveViewComponent("coui://" + m_LiveName, this);
	}

	void OnEnable()
	{
		ManagedRenderingNotifications.OnDeviceLost += OnDeviceLost;
		ManagedRenderingNotifications.OnDeviceReset += OnDeviceReset;

		if (m_SourceTexture == null && m_SourceCamera == null)
		{
			Debug.LogError("[Coherent GT] No source defined for live view " + m_LiveName + ".");
			enabled = false;
			return;
		}

		if (m_TargetView == null)
		{
			Debug.LogError("[Coherent GT] No target view defined for live view " + m_LiveName + ".");
			enabled = false;
			return;
		}

		if (!m_IsRegisteredForSystemReady)
		{
			m_UISystem.SystemReady += RegisterForSizeRequest;
			m_IsRegisteredForSystemReady = true;
		}

		Resize();
	}

	void OnDisable()
	{
		ManagedRenderingNotifications.OnDeviceLost -= OnDeviceLost;
		ManagedRenderingNotifications.OnDeviceReset -= OnDeviceReset;

		m_IsDisabling = true;
		UnsetAndReleaseLiveViewTexture();
		UnregisterForSizeRequest();
		m_IsDisabling = false;
	}

	void OnApplicationQuit()
	{
		//should be called before OnDestroy
		m_UISystem.UnregisterLiveViewComponent("coui://" + m_LiveName);
	}

	void OnDestroy()
	{
		if (m_TargetView != null && m_TargetView.Listener != null)
		{
			m_TargetView.Listener.LiveViewActivate -= OnLiveViewActivate;
		}
		if (m_SourceTexture)
		{
			m_SourceTexture.Release();
		}
	}

	void RegisterForSizeRequest()
	{
		if (m_TargetView == null)
		{
			Debug.LogError("[Coherent GT] Target view is null for live view " + m_LiveName + ".");
			enabled = false;
			return;
		}

		if (m_TargetView.Listener == null)
		{
			Debug.LogError("[Coherent GT] Target view Listener is null for live view " + m_LiveName + ".");
			enabled = false;
			return;
		}

		if (!m_IsRegisteredForSizeRequest)
		{
			m_TargetView.Listener.LiveViewSizeRequest += OnLiveViewSizeRequest;
			m_IsRegisteredForSizeRequest = true;
		}
	}

	void UnregisterForSizeRequest()
	{
		if (m_IsRegisteredForSizeRequest)
		{
			if (m_TargetView != null && m_TargetView.Listener != null)
			{
				m_TargetView.Listener.LiveViewSizeRequest -= OnLiveViewSizeRequest;
			}

			m_IsRegisteredForSizeRequest = false;
		}
	}

	void OnLiveViewSizeRequest(string name, ref int width, ref int height)
	{
		if (name == m_LiveName)
		{
			width = m_Width;
			height = m_Height;

			RecreateRenderTexture();
		}
	}

	void OnLiveViewActivate(string name, bool active)
	{
		if (name == m_LiveName)
		{
			enabled = active;
		}
	}

	void OnDeviceLost()
	{
		if (m_ImageHandle != 0)
		{
			UnsetLiveViewTexture();
		}
	}

	void OnDeviceReset()
	{
		// LiveViewTexture will be reset when this counter hits zero
		m_ResetLiveViewTextureCounter = 3;
	}

	void Update()
	{
		if (m_SourceTexture == null || !m_SourceTexture.IsCreated())
		{
			RecreateRenderTexture();
		}

		if (m_SourceCamera != null && m_SourceCamera.targetTexture != m_SourceTexture)
		{
			AttachCameraTargetTexture();
		}

		if (m_ResetLiveViewTextureCounter > 0 && --m_ResetLiveViewTextureCounter == 0)
		{
			m_IsLiveViewTextureSet = false;
		}

		if (!m_IsLiveViewTextureSet)
		{
			m_IsLiveViewTextureSet = TrySetLiveViewTexture();
		}
	}

	void Resize()
	{
		if (!m_UISystem)
		{
			m_UISystem = CoherentUIGTSystem.CurrentUISystem;
		}

		RecreateRenderTexture();

		if (TargetView != null && TargetView.View != null)
		{
			string resizeJS = string.Format(JS_RESIZE, m_LiveName, m_Width, m_Height);
			TargetView.View.ExecuteScript(resizeJS);
		}
	}

	void RecreateRenderTexture()
	{
		if (m_SourceTexture == null)
		{
			m_SourceTexture = new RenderTexture(m_Width, m_Height, 16,
				RenderTextureFormat.ARGB32,
				RenderTextureReadWrite.Default);
		}
		else
		{
			UnsetAndReleaseLiveViewTexture();
			m_SourceTexture.width = m_Width;
			m_SourceTexture.height = m_Height;
		}

		m_SourceTexture.Create();

		#if !UNITY_EDITOR_WIN && !UNITY_STANDALONE_WIN
		RenderTexture current = RenderTexture.active;
		RenderTexture.active = m_SourceTexture;
		GL.Clear(true, true, Color.clear);
		RenderTexture.active = current;
		#endif

		AttachCameraTargetTexture();

		m_IsLiveViewTextureSet = TrySetLiveViewTexture();
	}

	void AttachCameraTargetTexture()
	{
		if (m_SourceCamera != null && m_SourceTexture != null)
		{
			m_SourceCamera.targetTexture = m_SourceTexture;
			m_SourceCamera.enabled = true;
		}
	}

	bool TrySetLiveViewTexture()
	{
		if (m_TargetView != null && m_TargetView.ViewRenderer != null &&
			m_SourceTexture != null && m_SourceTexture.IsCreated())
		{
			if (m_ImageHandle != 0)
			{
				ResourceResponseUIGT.UserImageData data = new ResourceResponseUIGT.UserImageData();
				data.ImageHandle = m_ImageHandle;
				data.Width = (uint)m_Width;
				data.Height = (uint)m_Height;
				data.ContentRectX = 0;
				data.ContentRectY = 0;
				data.ContentRectWidth = data.Width;
				data.ContentRectHeight = data.Height;
				data.Texture = m_SourceTexture.GetNativeTexturePtr();

				m_UISystem.UISystem.ReplaceUserImage(data);

				CoherentUIGTRenderEvents.SendRenderEvent(
					CoherentRenderEventType.SetLiveViewTexture,
					m_TargetView.View.GetId());
			}
			return true;
		}

		return false;
	}

	void UnsetLiveViewTexture()
	{
		ResourceResponseUIGT.UserImageData data = new ResourceResponseUIGT.UserImageData();
		data.ImageHandle = m_ImageHandle;
		data.Width = 0;
		data.Height = 0;
		data.ContentRectX = 0;
		data.ContentRectY = 0;
		data.ContentRectWidth = 0;
		data.ContentRectHeight = 0;
		data.Texture = System.IntPtr.Zero;

		m_UISystem.UISystem.ReplaceUserImage(data);

		CoherentUIGTRenderEvents.SendRenderEvent(
			CoherentRenderEventType.SetLiveViewTexture,
			m_TargetView.View.GetId());
	}

	void UnsetAndReleaseLiveViewTexture()
	{
		if (m_TargetView != null && m_TargetView.ViewRenderer != null)
		{
			if (m_ImageHandle != 0 && m_IsDisabling)
			{
				UnsetLiveViewTexture();
			}
			m_IsLiveViewTextureSet = false;
		}
		ReleaseRenderTexture();
	}

	void ReleaseRenderTexture()
	{
		if (m_SourceCamera != null)
		{
			m_SourceCamera.targetTexture = null;
			m_SourceCamera.enabled = false;
		}

		if (m_SourceTexture != null && !m_IsDisabling)
		{
			// Zero ImageHandle means this liveView is not requested from ResourceHandle
			// and not used by CoherentGT, so we can safely release it immediately
			if (m_ImageHandle != 0)
			{
				var oldSourceTexture = m_SourceTexture;
				m_SourceTexture = new RenderTexture(m_Width, m_Height, 16,
					RenderTextureFormat.ARGB32,
					RenderTextureReadWrite.Default);

				var oldSrcTexturePtr = oldSourceTexture.GetNativeTexturePtr();
				var newSrcTexturePtr = m_SourceTexture.GetNativeTexturePtr();
				if (oldSrcTexturePtr == newSrcTexturePtr || oldSrcTexturePtr == System.IntPtr.Zero)
				{
					oldSourceTexture.Release();
				}
				else
				{
					m_UISystem.AddLiveViewRenderTexturesForLateRelease(ref oldSourceTexture);
				}
			}
			else
			{
				m_SourceTexture.Release();
			}
		}
	}
}

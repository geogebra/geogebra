using UnityEngine;
using System;
using System.IO;
using System.Text.RegularExpressions;
using System.Collections.Generic;

namespace Coherent.UIGT
{
	#if UNITY_EDITOR
	class UnityGTResourceHandlerDecorator : ResourceHandler
	{
		readonly ResourceHandler m_Handler;
		readonly CoherentUIGTView m_ViewComponent;

		internal UnityGTResourceHandlerDecorator(ResourceHandler handler,
												 CoherentUIGTView viewComponent)
		{
			m_Handler = handler;
			m_ViewComponent = viewComponent;

			m_ViewComponent.Listener.NavigateTo += OnNavigateTo;
		}

		void OnNavigateTo(string path, bool isMainFrame)
		{
			if (isMainFrame && m_ViewComponent != null)
			{
				m_ViewComponent.m_ResourcesInUse.Clear();
			}
		}

		public override void OnResourceRead(ResourceRequestUIGT request,
											ResourceResponseUIGT response)
		{
			if (m_ViewComponent != null)
			{
				string url = request.GetURL();
				var asUri = new Uri(url);
				if (asUri.Scheme == "coui" && !url.EndsWith(".renderTexture"))
				{
					string uiresources = PlayerPrefs.GetString("CoherentGT:UIResources");
					string resourcePath = asUri.GetComponents(UriComponents.Path, UriFormat.Unescaped);
					string path = "Assets/" + uiresources + "/" + resourcePath;
					m_ViewComponent.m_ResourcesInUse.Add(path);
				}
			}

			m_Handler.OnResourceRead(request, response);
		}
	}
	#endif

	class UnityGTResourceHandler : ResourceHandler
	{
		const string PRELOADEDIMAGES_PATH = "CoherentUIGTPreloadedImages/";

		private CoherentUIGTSystem m_UISystem;

		private Regex m_RangeRequestValue =
			new Regex("bytes=(?<From>\\d+)\\-(?<To>\\d*)",
					  RegexOptions.ExplicitCapture);

		private string GetFilepath(string url)
		{
			var asUri = new Uri(url);
			string cleanUrl;
			if (asUri.Scheme != "file")
			{
#if UNITY_EDITOR
			cleanUrl = asUri.GetComponents(
				UriComponents.Path, UriFormat.Unescaped);
			// Read resources from the project folder
			var uiResources = PlayerPrefs.GetString("CoherentGT:UIResources");
			if (uiResources == string.Empty)
			{
				Debug.LogError("Missing path for Coherent GT resources. " +
					"Please select path to your resources via Edit -> " +
					"Project Settings -> Coherent GT -> Select Resources Folder");
				// Try to fall back to the default location
				uiResources = Path.Combine(
					Path.Combine(Application.dataPath, "WebPlayerTemplates"),
					"uiresources");
				Debug.LogWarning("Falling back to the default location of the "+
					"UI Resources in the Unity Editor: " + uiResources);
				PlayerPrefs.SetString("CoherentGT:UIResources",
					"WebPlayerTemplates/uiresources");
			}
			else
			{
				uiResources = Path.Combine(Application.dataPath, uiResources);
			}
			cleanUrl = cleanUrl.Insert(0, uiResources + '/');
#else
			cleanUrl = asUri.GetComponents(
				UriComponents.Host | UriComponents.Path, UriFormat.Unescaped);
			// Read resources from the <executable>_Data folder
			cleanUrl = Application.dataPath + '/' + cleanUrl;
#endif
			}
			else
			{
				cleanUrl = asUri.GetComponents(
					UriComponents.Path, UriFormat.Unescaped);
			}
			return cleanUrl;
		}

		bool IsImageRequest(string url)
		{
			if (!url.Contains("."))
			{
				return false;
			}
			string fileExtension = url.Substring(url.LastIndexOf('.'));
			return fileExtension.IndexOf("png", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("jpg", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("tga", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("psd", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("gif", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("bmp", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("jpeg", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("astc", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("pkm", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("ktx", StringComparison.OrdinalIgnoreCase) >= 0
				|| fileExtension.IndexOf("dds", StringComparison.OrdinalIgnoreCase) >= 0;
		}

		bool LoadPreloadedImage(string texturePath, ref ResourceResponseUIGT response)
		{
			Texture tex = Resources.Load<Texture>(texturePath);
			if (tex != null)
			{
				ResourceResponseUIGT.UserImageData data = new ResourceResponseUIGT.UserImageData();
				data.Width = (uint) tex.width;
				data.Height = (uint) tex.height;
				data.ContentRectX = 0;
				data.ContentRectY = 0;
				data.ContentRectWidth = (uint) tex.width;
				data.ContentRectHeight = (uint) tex.height;
				data.Texture =  tex.GetNativeTexturePtr();
				data.ImageHandle = 0; // PreloadedImages should use ImageHandle = 0

				m_UISystem.AddPreloadedImageTextureAsset(ref tex);

				response.ReceiveUserImage(data);
				return true;
			}
			Debug.LogError("[Coherent GT] Texture asset " + texturePath + " not found");
			return false;
		}

		bool LoadLiveViewFromComponent(string url, ref ResourceResponseUIGT response)
		{
			var liveViewComponent = m_UISystem.LiveGameViewComponents[url];
			if (liveViewComponent)
			{
				RenderTexture tex = liveViewComponent.SourceTexture;
				if (tex != null)
				{
					ResourceResponseUIGT.UserImageData data = new ResourceResponseUIGT.UserImageData();
					data.Width = (uint) tex.width;
					data.Height = (uint) tex.height;
					data.ContentRectX = 0;
					data.ContentRectY = 0;
					data.ContentRectWidth = (uint) tex.width;
					data.ContentRectHeight = (uint) tex.height;
					data.Texture = tex.GetNativeTexturePtr();

					if (liveViewComponent.ImageHandle == 0)
					{
						liveViewComponent.ImageHandle = m_UISystem.GetNextFreeImageHandle();
					}
					data.ImageHandle = liveViewComponent.ImageHandle;

					response.ReceiveUserImage(data);
					return true;
				}
			}
			Debug.LogError("[Coherent GT] RenderTexture " + url + " not found");
			return false;
		}

		public override void OnResourceRead(ResourceRequestUIGT request,
			ResourceResponseUIGT response)
		{
			if (m_UISystem == null)
			{
				m_UISystem = CoherentUIGTSystem.CurrentUISystem;
			}

			string url = request.GetURL();
			string cleanUrl = GetFilepath(url);

			if (IsImageRequest(url) && url.StartsWith("coui://" + PRELOADEDIMAGES_PATH))
			{
				var asUri = new Uri(url);
				string fileExtension = url.Substring(url.LastIndexOf('.'));
				string texturePath = url.Replace(asUri.Scheme + "://", "").Replace(fileExtension, "");;
				if(LoadPreloadedImage(texturePath, ref response))
				{
					response.SignalSuccess();
				}
				else
				{
					response.SignalFailure();
				}
				response.Release();
				return;
			}
			else if (m_UISystem.LiveGameViewComponents.ContainsKey(url))
			{
				if (LoadLiveViewFromComponent(url, ref response))
				{
					response.SignalSuccess();
				}
				else
				{
					response.SignalFailure();
				}
				response.Release();
				return;
			}

			if (!File.Exists(cleanUrl))
			{
				Debug.LogError("[Coherent GT] File not found for " + url);
				response.SignalFailure();
				response.Release();
				return;
			}

			if (request.GetHeaderIndex("Range") < 0)
			{
				DoCompleteRead(cleanUrl, request, response);
			}
			else
			{
				DoPartialRead(cleanUrl, request, response);
			}
			response.Release();
		}

		private void DoCompleteRead(string cleanUrl,
			ResourceRequestUIGT request, ResourceResponseUIGT response)
		{
			// TODO: Handle exception & notify response -
			// maybe handle it in the calling method
			byte[] bytes = File.ReadAllBytes(cleanUrl);

			response.SetStatus(200);
			response.SetExpectedLength(bytes.LongLength);
			response.ReceiveData(bytes, bytes.LongLength);
			response.SignalSuccess();
		}

		private void DoPartialRead(string cleanUrl, ResourceRequestUIGT request,
								   ResourceResponseUIGT response)
		{
			string rangeValue = request.GetHeader("Range");
			Match match = m_RangeRequestValue.Match (rangeValue);
			if (!match.Success)
			{
				response.SignalFailure();
				return;
			}

			long fileSize = new FileInfo(cleanUrl).Length;

			long startByte = long.Parse (match.Groups ["From"].Value);
			string endByteString = match.Groups ["To"].Value;
			long endByte = fileSize - 1;
			if (string.IsNullOrEmpty(endByteString))
			{
				// Clamp to a maximum chunk size
				const long MaxPartialReadSize = 16 * 1024 * 1024;
				if (endByte - startByte > MaxPartialReadSize)
				{
					endByte = startByte + MaxPartialReadSize;
				}
			}
			else
			{
				endByte = long.Parse(endByteString);
			}

			// Clamp to int.MaxValue since that's the type BinaryReader
			// allows us to read; if it could read more bytes, then we would
			// clamp the size to uint.MaxValue since ResourceResponse.GetBuffer
			// expects an uint value.
			long bufferSize = Math.Min((long)int.MaxValue,
									   endByte - startByte + 1);

			byte[] bytes = new byte[bufferSize];
			using (BinaryReader reader = new BinaryReader(
				new FileStream(cleanUrl, FileMode.Open)))
			{
				reader.BaseStream.Seek(startByte, SeekOrigin.Begin);
				reader.Read(bytes, 0, (int)bufferSize);
			}

			// Set required response headers
			response.SetStatus(206);
			response.SetExpectedLength(fileSize);
			response.SetResponseHeader("Accept-Ranges", "bytes");
			response.SetResponseHeader("Content-Range", "bytes " + startByte +
									   "-" + endByte + "/" + fileSize);
			response.SetResponseHeader("Content-Length",
									   bufferSize.ToString());

			response.ReceiveData (bytes, bytes.LongLength);

			response.SignalSuccess();
		}
	}
}

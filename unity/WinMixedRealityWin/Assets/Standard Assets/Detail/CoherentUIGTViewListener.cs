using UnityEngine;
using System.Collections.Generic;

namespace Coherent.UIGT
{
public class UnityGTViewListener : ViewListener
{
	public UnityGTViewListener(CoherentUIGTView component, int width, int height)
	{
		m_ViewComponent = component;
	}

	public View View
	{
		get
		{
			return m_View;
		}
	}

	internal CoherentUIGTView ViewComponent
	{
		get
		{
			return m_ViewComponent;
		}
	}

	private View m_View;
	private CoherentUIGTView m_ViewComponent;

	//----------------------------------------------------------------------
	// Event definitions (until the ViewListener declares them itself)
	//----------------------------------------------------------------------

	public delegate void AudioDataReceivedFunc(int id, int samples, System.IntPtr pcm, int channels);

	public event AudioDataReceivedFunc AudioDataReceived;

	public override void OnAudioDataReceived(int id, int samples, System.IntPtr pcm, int channels)
	{
		if (AudioDataReceived != null)
		{
			AudioDataReceived(id, samples, pcm, channels);
		}
	}

	public delegate void AudioStreamClosedFunc(int id);

	public event AudioStreamClosedFunc AudioStreamClosed;

	public override void OnAudioStreamClosed(int id)
	{
		if (AudioStreamClosed != null)
		{
			AudioStreamClosed(id);
		}
	}

	public delegate void AudioStreamCreatedFunc(int id, int bitDepth, int channels, float samplingRate);

	public event AudioStreamCreatedFunc AudioStreamCreated;

	public override void OnAudioStreamCreated(int id, int bitDepth, int channels, float samplingRate)
	{
		if (AudioStreamCreated != null)
		{
			AudioStreamCreated(id, bitDepth, channels, samplingRate);
		}
	}

	public delegate void AudioStreamEndedFunc(int id);

	public event AudioStreamEndedFunc AudioStreamEnded;

	public override void OnAudioStreamEnded(int id)
	{
		if (AudioStreamEnded != null)
		{
			AudioStreamEnded(id);
		}
	}

	public delegate void AudioStreamPauseFunc(int id);

	public event AudioStreamPauseFunc AudioStreamPause;

	public override void OnAudioStreamPause(int id)
	{
		if (AudioStreamPause != null)
		{
			AudioStreamPause(id);
		}
	}

	public delegate void AudioStreamPlayFunc(int id);

	public event AudioStreamPlayFunc AudioStreamPlay;

	public override void OnAudioStreamPlay(int id)
	{
		if (AudioStreamPlay != null)
		{
			AudioStreamPlay(id);
		}
	}

	public delegate void BindingsReleasedFunc();

	public event BindingsReleasedFunc BindingsReleased;

	public override void OnBindingsReleased()
	{
		if (BindingsReleased != null)
		{
			BindingsReleased();
		}
	}

	public delegate void CallbackFunc(string eventName, CallbackArguments arguments);

	public event CallbackFunc Callback;

	public override void OnCallback(string eventName, CallbackArguments arguments)
	{
		if (Callback != null)
		{
			Callback(eventName, arguments);
		}
	}

	public delegate void CaretRectChangedFunc(int x, int y, uint width, uint height);

	public event CaretRectChangedFunc CaretRectChanged;

	public override void OnCaretRectChanged(int x, int y, uint width, uint height)
	{
		if (CaretRectChanged != null)
		{
			CaretRectChanged(x, y, width, height);
		}
	}

	public delegate void CursorChangedFunc(CursorType cursor);

	public event CursorChangedFunc CursorChanged;

	public override void OnCursorChanged(CursorType cursor)
	{
		if (CursorChanged != null)
		{
			CursorChanged(cursor);
		}
	}

	public delegate void FailLoadFunc(string path, string error, bool isMainFrame);

	public event FailLoadFunc FailLoad;

	public override void OnFailLoad(string path, string error, bool isMainFrame)
	{
		if (FailLoad != null)
		{
			FailLoad(path, error, isMainFrame);
		}
	}

	public delegate void FinishLoadFunc(string path, bool isMainFrame);

	public event FinishLoadFunc FinishLoad;

	public override void OnFinishLoad(string path, bool isMainFrame)
	{
		if (FinishLoad != null)
		{
			FinishLoad(path, isMainFrame);
		}
	}

	public delegate void IMEShouldCancelCompositionFunc();

	public event IMEShouldCancelCompositionFunc IMEShouldCancelComposition;

	public override void OnIMEShouldCancelComposition()
	{
		if (IMEShouldCancelComposition != null)
		{
			IMEShouldCancelComposition();
		}
	}

	public delegate void LiveViewActivateFunc(string name, bool active);

	public event LiveViewActivateFunc LiveViewActivate;


	public void OnLiveViewActivate(string name, bool active)
	{
		if (LiveViewActivate != null)
		{
			LiveViewActivate(name, active);
		}
	}

	public delegate void LiveViewSizeRequestFunc(string name, ref int width, ref int height);

	public event LiveViewSizeRequestFunc LiveViewSizeRequest;

	public void OnLiveViewSizeRequest(string name, out int width, out int height)
	{
		//Non-zero default values will force creation of a live view even without response
		int _width = 1;
		int _height = 1;

		if (LiveViewSizeRequest != null)
		{
			LiveViewSizeRequest(name, ref _width, ref _height);
		}

		width = _width;
		height = _height;
	}

	public delegate void NavigateToFunc(string path, bool isMainFrame);

	public event NavigateToFunc NavigateTo;

	public override void OnNavigateTo(string path, bool isMainFrame)
	{
		if (NavigateTo != null)
		{
			NavigateTo(path, isMainFrame);
		}
	}

	public delegate void PerformanceAuditFunc(string result);

	public event PerformanceAuditFunc PerformanceAudit;

	public override void OnPerformanceAudit(string result)
	{
		if (PerformanceAudit != null)
		{
			PerformanceAudit(result);
		}
	}

	public delegate void ReadyForBindingsFunc();

	public event ReadyForBindingsFunc ReadyForBindings;

	public override void OnReadyForBindings()
	{
		if (ReadyForBindings != null)
		{
			ReadyForBindings();
		}
	}

	public delegate void StartLoadingFunc(string path, bool isMainFrame);

	public event StartLoadingFunc StartLoading;

	public override void OnStartLoading(string path, bool isMainFrame)
	{
		if (StartLoading != null)
		{
			StartLoading(path, isMainFrame);
		}
	}

	public delegate void TextInputTypeChangedFunc(TextInputControlType type);

	public event TextInputTypeChangedFunc TextInputTypeChanged;

	public override void OnTextInputTypeChanged(TextInputControlType type)
	{
		if (TextInputTypeChanged != null)
		{
			TextInputTypeChanged(type);
		}
	}

	public delegate void ViewCreatedFunc(View view);

	public event ViewCreatedFunc ViewCreated;

	public override void OnViewCreated(View view)
	{
		m_View = view;

		if (ViewCreated != null)
		{
			ViewCreated(view);
		}
	}

	public delegate void ViewDestroyedFunc();

	public event ViewDestroyedFunc ViewDestroyed;

	public override void OnViewDestroyed()
	{
		if (ViewDestroyed != null)
		{
			ViewDestroyed();
		}
	}
}
}

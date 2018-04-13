using System;
using UnityEngine;
using UnityEngine.Events;
using Coherent.UIGT;

public class CoherentUIGTViewEvents : MonoBehaviour
{
	[Serializable] public class OnAudioDataReceivedEvent : UnityEvent<int, int, IntPtr, int> { }
	[Serializable] public class OnAudioStreamClosedEvent : UnityEvent<int> { }
	[Serializable] public class OnAudioStreamCreatedEvent : UnityEvent<int, int, int, float> { }
	[Serializable] public class OnAudioStreamEndedEvent : UnityEvent<int> { }
	[Serializable] public class OnAudioStreamPauseEvent : UnityEvent<int> { }
	[Serializable] public class OnAudioStreamPlayEvent : UnityEvent<int> { }
	[Serializable] public class OnBindingsReleasedEvent : UnityEvent { }
	[Serializable] public class OnCallbackEvent : UnityEvent<string, CallbackArguments> { }
	[Serializable] public class OnCaretRectChangedEvent : UnityEvent<int, int, uint, uint> { }
	[Serializable] public class OnCursorChangedEvent : UnityEvent<CursorType> { }
	[Serializable] public class OnFailLoadEvent : UnityEvent<string, string, bool> { }
	[Serializable] public class OnFinishLoadEvent : UnityEvent<string, bool> { }
	[Serializable] public class OnIMEShouldCancelCompositionEvent : UnityEvent { }
	[Serializable] public class OnNavigateToEvent : UnityEvent<string, bool> { }
	[Serializable] public class OnPerformanceAuditEvent : UnityEvent<string> { }
	[Serializable] public class OnReadyForBindingsEvent : UnityEvent { }
	[Serializable] public class OnStartLoadingEvent : UnityEvent<string, bool> { }
	[Serializable] public class OnTextInputTypeChangedEvent : UnityEvent<TextInputControlType> { }
	[Serializable] public class OnViewCreatedEvent : UnityEvent<View> { }
	[Serializable] public class OnViewDestroyedEvent : UnityEvent { }

	public CoherentUIGTView m_TargetView;

	public OnAudioDataReceivedEvent OnAudioDataReceived;
	public OnAudioStreamClosedEvent OnAudioStreamClosed;
	public OnAudioStreamCreatedEvent OnAudioStreamCreated;
	public OnAudioStreamEndedEvent OnAudioStreamEnded;
	public OnAudioStreamPauseEvent OnAudioStreamPause;
	public OnAudioStreamPlayEvent OnAudioStreamPlay;
	public OnBindingsReleasedEvent OnBindingsReleased;
	public OnCallbackEvent OnCallback;
	public OnCaretRectChangedEvent OnCaretRectChanged;
	public OnCursorChangedEvent OnCursorChanged;
	public OnFailLoadEvent OnFailLoad;
	public OnFinishLoadEvent OnFinishLoad;
	public OnIMEShouldCancelCompositionEvent OnIMEShouldCancelComposition;
	public OnNavigateToEvent OnNavigateTo;
	public OnPerformanceAuditEvent OnPerformanceAudit;
	public OnReadyForBindingsEvent OnReadyForBindings;
	public OnStartLoadingEvent OnStartLoading;
	public OnTextInputTypeChangedEvent OnTextInputTypeChanged;
	public OnViewCreatedEvent OnViewCreated;
	public OnViewDestroyedEvent OnViewDestroyed;

	void Start()
	{
		if (m_TargetView == null)
		{
			m_TargetView = GetComponent<CoherentUIGTView>();

			if (m_TargetView == null)
			{
				Debug.LogWarning("[Coherent GT] ViewEvents : Target view " +
								 "component is not attached. Events won't be triggered.");
				return;
			}
		}

		m_TargetView.Listener.AudioDataReceived += AudioDataReceivedInvoke;
		m_TargetView.Listener.AudioStreamClosed += AudioStreamClosedInvoke;
		m_TargetView.Listener.AudioStreamCreated += AudioStreamCreatedInvoke;
		m_TargetView.Listener.AudioStreamEnded += AudioStreamEndedInvoke;
		m_TargetView.Listener.AudioStreamPause += AudioStreamPauseInvoke;
		m_TargetView.Listener.AudioStreamPlay += AudioStreamPlayInvoke;
		m_TargetView.Listener.BindingsReleased += BindingsReleasedInvoke;
		m_TargetView.Listener.Callback += CallbackInvoke;
		m_TargetView.Listener.CaretRectChanged += CaretRectChangedInvoke;
		m_TargetView.Listener.CursorChanged += CursorChangedInvoke;
		m_TargetView.Listener.FailLoad += FailLoadInvoke;
		m_TargetView.Listener.FinishLoad += FinishLoadInvoke;
		m_TargetView.Listener.IMEShouldCancelComposition += IMEShouldCancelCompositionInvoke;
		m_TargetView.Listener.NavigateTo += NavigateToInvoke;
		m_TargetView.Listener.PerformanceAudit += PerformanceAuditInvoke;
		m_TargetView.Listener.ReadyForBindings += ReadyForBindingsInvoke;
		m_TargetView.Listener.StartLoading += StartLoadingInvoke;
		m_TargetView.Listener.TextInputTypeChanged += TextInputTypeChangedInvoke;
		m_TargetView.Listener.ViewCreated += ViewCreatedInvoke;
		m_TargetView.Listener.ViewDestroyed += ViewDestroyedInvoke;
	}

	void AudioDataReceivedInvoke(int id, int samples, IntPtr pcm, int channels)
	{
		OnAudioDataReceived.Invoke(id, samples, pcm, channels);
	}

	void AudioStreamClosedInvoke(int id)
	{
		OnAudioStreamClosed.Invoke(id);
	}

	void AudioStreamCreatedInvoke(int id, int bitDepth, int channels, float samplingRate)
	{
		OnAudioStreamCreated.Invoke(id, bitDepth, channels, samplingRate);
	}

	void AudioStreamEndedInvoke(int id)
	{
		OnAudioStreamEnded.Invoke(id);
	}

	void AudioStreamPauseInvoke(int id)
	{
		OnAudioStreamPause.Invoke(id);
	}

	void AudioStreamPlayInvoke(int id)
	{
		OnAudioStreamPlay.Invoke(id);
	}

	void BindingsReleasedInvoke()
	{
		OnBindingsReleased.Invoke();
	}

	void CallbackInvoke(string eventName, CallbackArguments arguments)
	{
		OnCallback.Invoke(eventName, arguments);
	}

	void CaretRectChangedInvoke(int x, int y, uint width, uint height)
	{
		OnCaretRectChanged.Invoke(x, y, width, height);
	}

	void CursorChangedInvoke (CursorType cursor)
	{
		OnCursorChanged.Invoke(cursor);
	}

	void FailLoadInvoke(string path, string error, bool isMainFrame)
	{
		OnFailLoad.Invoke(path, error, isMainFrame);
	}

	void FinishLoadInvoke(string path, bool isMainFrame)
	{
		OnFinishLoad.Invoke(path, isMainFrame);
	}

	void IMEShouldCancelCompositionInvoke()
	{
		OnIMEShouldCancelComposition.Invoke();
	}

	void NavigateToInvoke(string path, bool isMainFrame)
	{
		OnNavigateTo.Invoke(path, isMainFrame);
	}

	void PerformanceAuditInvoke(string result)
	{
		OnPerformanceAudit.Invoke(result);
	}

	void ReadyForBindingsInvoke()
	{
		OnReadyForBindings.Invoke();
	}

	void StartLoadingInvoke(string path, bool isMainFrame)
	{
		OnStartLoading.Invoke(path, isMainFrame);
	}

	void TextInputTypeChangedInvoke(TextInputControlType type)
	{
		OnTextInputTypeChanged.Invoke(type);
	}

	void ViewCreatedInvoke(View view)
	{
		OnViewCreated.Invoke(view);
	}

	void ViewDestroyedInvoke()
	{
		OnViewDestroyed.Invoke();
	}
}

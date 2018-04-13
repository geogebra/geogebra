using UnityEngine;
using System.Collections;

namespace Coherent.UIGT
{
	public static class InputManager
	{
		const float KEY_REPEAT_DELAY = 0.5f; // 500ms
		const float KEY_REPEAT_RATE = 0.033f; // 30 times per second
		const float UNITY_WHEEL_TICK_FACTOR = 1.0f / 3.0f;
		static int[] s_KeyCodeMapping;

		static bool s_WasShiftPressed;
		static float s_LastShiftEventTime;

		#region Keycode mapping initialization
		static InputManager ()
		{
			int keyCodeEnumMaxValue = 0;
			int[] keyCodeValues = (System.Enum.GetValues(typeof(KeyCode)) as int[]);
			for (int i = 0; i < keyCodeValues.Length; i++)
			{
				if (keyCodeValues[i] > keyCodeEnumMaxValue)
				{
					keyCodeEnumMaxValue = keyCodeValues[i];
				}
			}

			s_KeyCodeMapping = new int[keyCodeEnumMaxValue];

			s_KeyCodeMapping [(int)KeyCode.None] = 0;
			s_KeyCodeMapping [(int)KeyCode.Backspace] = 0x08;
			s_KeyCodeMapping [(int)KeyCode.Tab] = 0x09;
			s_KeyCodeMapping [(int)KeyCode.Clear] = 0x0C;
			s_KeyCodeMapping [(int)KeyCode.Return] = 0x0D;
			s_KeyCodeMapping [(int)KeyCode.Pause] = 0x13;
			s_KeyCodeMapping [(int)KeyCode.Escape] = 0x1B;
			s_KeyCodeMapping [(int)KeyCode.Space] = 0x20;
			s_KeyCodeMapping [(int)KeyCode.Exclaim] = 0x31;
			s_KeyCodeMapping [(int)KeyCode.DoubleQuote] = 0xDE;
			s_KeyCodeMapping [(int)KeyCode.Hash] = 0x33;
			s_KeyCodeMapping [(int)KeyCode.Dollar] = 0x34;
			s_KeyCodeMapping [(int)KeyCode.Ampersand] = 0x37;
			s_KeyCodeMapping [(int)KeyCode.Quote] = 0xDE;
			s_KeyCodeMapping [(int)KeyCode.LeftParen] = 0x39;
			s_KeyCodeMapping [(int)KeyCode.RightParen] = 0x30;
			s_KeyCodeMapping [(int)KeyCode.Asterisk] = 0x38;
			s_KeyCodeMapping [(int)KeyCode.Plus] = 0xBB;
			s_KeyCodeMapping [(int)KeyCode.Comma] = 0xBC;
			s_KeyCodeMapping [(int)KeyCode.Minus] = 0xBD;
			s_KeyCodeMapping [(int)KeyCode.Period] = 0xBE;
			s_KeyCodeMapping [(int)KeyCode.Slash] = 0xBF;
			s_KeyCodeMapping [(int)KeyCode.Alpha0] = 0x30;
			s_KeyCodeMapping [(int)KeyCode.Alpha1] = 0x31;
			s_KeyCodeMapping [(int)KeyCode.Alpha2] = 0x32;
			s_KeyCodeMapping [(int)KeyCode.Alpha3] = 0x33;
			s_KeyCodeMapping [(int)KeyCode.Alpha4] = 0x34;
			s_KeyCodeMapping [(int)KeyCode.Alpha5] = 0x35;
			s_KeyCodeMapping [(int)KeyCode.Alpha6] = 0x36;
			s_KeyCodeMapping [(int)KeyCode.Alpha7] = 0x37;
			s_KeyCodeMapping [(int)KeyCode.Alpha8] = 0x38;
			s_KeyCodeMapping [(int)KeyCode.Alpha9] = 0x39;
			s_KeyCodeMapping [(int)KeyCode.Colon] = 0xBA;
			s_KeyCodeMapping [(int)KeyCode.Semicolon] = 0xBA;
			s_KeyCodeMapping [(int)KeyCode.Less] = 0xBC;
			s_KeyCodeMapping [(int)KeyCode.Equals] = 0xBB;
			s_KeyCodeMapping [(int)KeyCode.Greater] = 0xBE;
			s_KeyCodeMapping [(int)KeyCode.Question] = 0xBF;
			s_KeyCodeMapping [(int)KeyCode.At] = 0x32;
			s_KeyCodeMapping [(int)KeyCode.LeftBracket] = 0xDB;
			s_KeyCodeMapping [(int)KeyCode.Backslash] = 0xDC;
			s_KeyCodeMapping [(int)KeyCode.RightBracket] = 0xDD;
			s_KeyCodeMapping [(int)KeyCode.Caret] = 0x36;
			s_KeyCodeMapping [(int)KeyCode.Underscore] = 0xBD;
			s_KeyCodeMapping [(int)KeyCode.BackQuote] = 0xC0;
			s_KeyCodeMapping [(int)KeyCode.A] = 65;
			s_KeyCodeMapping [(int)KeyCode.B] = 66;
			s_KeyCodeMapping [(int)KeyCode.C] = 67;
			s_KeyCodeMapping [(int)KeyCode.D] = 68;
			s_KeyCodeMapping [(int)KeyCode.E] = 69;
			s_KeyCodeMapping [(int)KeyCode.F] = 70;
			s_KeyCodeMapping [(int)KeyCode.G] = 71;
			s_KeyCodeMapping [(int)KeyCode.H] = 72;
			s_KeyCodeMapping [(int)KeyCode.I] = 73;
			s_KeyCodeMapping [(int)KeyCode.J] = 74;
			s_KeyCodeMapping [(int)KeyCode.K] = 75;
			s_KeyCodeMapping [(int)KeyCode.L] = 76;
			s_KeyCodeMapping [(int)KeyCode.M] = 77;
			s_KeyCodeMapping [(int)KeyCode.N] = 78;
			s_KeyCodeMapping [(int)KeyCode.O] = 79;
			s_KeyCodeMapping [(int)KeyCode.P] = 80;
			s_KeyCodeMapping [(int)KeyCode.Q] = 81;
			s_KeyCodeMapping [(int)KeyCode.R] = 82;
			s_KeyCodeMapping [(int)KeyCode.S] = 83;
			s_KeyCodeMapping [(int)KeyCode.T] = 84;
			s_KeyCodeMapping [(int)KeyCode.U] = 85;
			s_KeyCodeMapping [(int)KeyCode.V] = 86;
			s_KeyCodeMapping [(int)KeyCode.W] = 87;
			s_KeyCodeMapping [(int)KeyCode.X] = 88;
			s_KeyCodeMapping [(int)KeyCode.Y] = 89;
			s_KeyCodeMapping [(int)KeyCode.Z] = 90;
			s_KeyCodeMapping [(int)KeyCode.Delete] = 0x2E;
			s_KeyCodeMapping [(int)KeyCode.Keypad0] = 0x60;
			s_KeyCodeMapping [(int)KeyCode.Keypad1] = 0x61;
			s_KeyCodeMapping [(int)KeyCode.Keypad2] = 0x62;
			s_KeyCodeMapping [(int)KeyCode.Keypad3] = 0x63;
			s_KeyCodeMapping [(int)KeyCode.Keypad4] = 0x64;
			s_KeyCodeMapping [(int)KeyCode.Keypad5] = 0x65;
			s_KeyCodeMapping [(int)KeyCode.Keypad6] = 0x66;
			s_KeyCodeMapping [(int)KeyCode.Keypad7] = 0x67;
			s_KeyCodeMapping [(int)KeyCode.Keypad8] = 0x68;
			s_KeyCodeMapping [(int)KeyCode.Keypad9] = 0x69;
			s_KeyCodeMapping [(int)KeyCode.KeypadPeriod] = 0x6E;
			s_KeyCodeMapping [(int)KeyCode.KeypadDivide] = 0x6F;
			s_KeyCodeMapping [(int)KeyCode.KeypadMultiply] = 0x6A;
			s_KeyCodeMapping [(int)KeyCode.KeypadMinus] = 0x6D;
			s_KeyCodeMapping [(int)KeyCode.KeypadPlus] = 0x6B;
			s_KeyCodeMapping [(int)KeyCode.KeypadEnter] = 0x0D;
			s_KeyCodeMapping [(int)KeyCode.KeypadEquals] = 0;
			s_KeyCodeMapping [(int)KeyCode.UpArrow] = 0x26;
			s_KeyCodeMapping [(int)KeyCode.DownArrow] = 0x28;
			s_KeyCodeMapping [(int)KeyCode.RightArrow] = 0x27;
			s_KeyCodeMapping [(int)KeyCode.LeftArrow] = 0x25;
			s_KeyCodeMapping [(int)KeyCode.Insert] = 0x2D;
			s_KeyCodeMapping [(int)KeyCode.Home] = 0x24;
			s_KeyCodeMapping [(int)KeyCode.End] = 0x23;
			s_KeyCodeMapping [(int)KeyCode.PageUp] = 0x21;
			s_KeyCodeMapping [(int)KeyCode.PageDown] = 0x22;
			s_KeyCodeMapping [(int)KeyCode.F1] = 0x70;
			s_KeyCodeMapping [(int)KeyCode.F2] = 0x71;
			s_KeyCodeMapping [(int)KeyCode.F3] = 0x72;
			s_KeyCodeMapping [(int)KeyCode.F4] = 0x73;
			s_KeyCodeMapping [(int)KeyCode.F5] = 0x74;
			s_KeyCodeMapping [(int)KeyCode.F6] = 0x75;
			s_KeyCodeMapping [(int)KeyCode.F7] = 0x76;
			s_KeyCodeMapping [(int)KeyCode.F8] = 0x77;
			s_KeyCodeMapping [(int)KeyCode.F9] = 0x78;
			s_KeyCodeMapping [(int)KeyCode.F10] = 0x79;
			s_KeyCodeMapping [(int)KeyCode.F11] = 0x7A;
			s_KeyCodeMapping [(int)KeyCode.F12] = 0x7B;
			s_KeyCodeMapping [(int)KeyCode.F13] = 0x7C;
			s_KeyCodeMapping [(int)KeyCode.F14] = 0x7D;
			s_KeyCodeMapping [(int)KeyCode.F15] = 0x7E;
			s_KeyCodeMapping [(int)KeyCode.Numlock] = 0x90;
			s_KeyCodeMapping [(int)KeyCode.CapsLock] = 0x14;
			s_KeyCodeMapping [(int)KeyCode.ScrollLock] = 0x91;
			s_KeyCodeMapping [(int)KeyCode.RightShift] = 0x10;
			s_KeyCodeMapping [(int)KeyCode.LeftShift] = 0x10;
			s_KeyCodeMapping [(int)KeyCode.RightControl] = 0x11;
			s_KeyCodeMapping [(int)KeyCode.LeftControl] = 0x11;
			s_KeyCodeMapping [(int)KeyCode.RightAlt] = 0x12;
			s_KeyCodeMapping [(int)KeyCode.LeftAlt] = 0x12;
			s_KeyCodeMapping [(int)KeyCode.RightApple] = 0x5C;
			s_KeyCodeMapping [(int)KeyCode.LeftApple] = 0x5B;
			s_KeyCodeMapping [(int)KeyCode.LeftWindows] = 0x5C;
			s_KeyCodeMapping [(int)KeyCode.RightWindows] = 0x5B;
			s_KeyCodeMapping [(int)KeyCode.AltGr] = 0x12;
			s_KeyCodeMapping [(int)KeyCode.Help] = 0x2F;
			s_KeyCodeMapping [(int)KeyCode.Print] = 0x2A;
			s_KeyCodeMapping [(int)KeyCode.SysReq] = 0x2C;
			s_KeyCodeMapping [(int)KeyCode.Break] = 0x13;
			s_KeyCodeMapping [(int)KeyCode.Menu] = 0x5D;
		}
		#endregion

		private static void GetEventModifiersState(EventModifiersState state, Event evt)
		{
			state.IsCtrlDown = evt.control;
			state.IsAltDown = evt.alt;
			state.IsShiftDown = evt.shift;
			state.IsCapsOn = evt.capsLock;
			state.IsNumLockOn = false; // Indeterminate
			state.IsMetaDown = evt.command;
		}

		private static void GetEventMouseModifiersState(EventMouseModifiersState state, Event evt)
		{
			state.IsLeftButtonDown = evt.button == 0;
			state.IsMiddleButtonDown = evt.button == 2;
			state.IsRightButtonDown = evt.button == 1;
		}

		public static void ProcessMouseEvent(CoherentUIGTMouseEventData eventData, Event evt)
		{
			eventData.WheelX = 0;
			eventData.WheelY = 0;

			switch (evt.type)
			{
			case EventType.MouseDown:
				eventData.Type = MouseEventData.EventType.MouseDown;
				break;
			case EventType.MouseUp:
				eventData.Type = MouseEventData.EventType.MouseUp;
				break;
			case EventType.ScrollWheel:
				eventData.Type = MouseEventData.EventType.MouseWheel;
				eventData.WheelX = evt.delta.x * UNITY_WHEEL_TICK_FACTOR;
				eventData.WheelY = -evt.delta.y * UNITY_WHEEL_TICK_FACTOR;
				break;
			default:
				eventData.Type = MouseEventData.EventType.MouseMove;
				return;
			}

			GetEventModifiersState(eventData.Modifiers, evt);
			GetEventMouseModifiersState(eventData.MouseModifiers, evt);
			eventData.X = (int)evt.mousePosition.x;
			eventData.Y = (int)evt.mousePosition.y;

			switch (evt.button)
			{
			case 0:
				eventData.Button = MouseEventData.MouseButton.ButtonLeft;
				break;
			case 1:
				eventData.Button = MouseEventData.MouseButton.ButtonRight;
				break;
			case 2:
				eventData.Button = MouseEventData.MouseButton.ButtonMiddle;
				break;
			default:
				eventData.Button = MouseEventData.MouseButton.ButtonNone;
				break;
			}
		}

		public static void ProcessKeyEvent(CoherentUIGTKeyEventData eventData, Event evt)
		{
			if (evt.type == EventType.KeyDown)
			{
				eventData.Type = KeyEventData.EventType.KeyDown;
			}
			else if (evt.type == EventType.KeyUp)
			{
				eventData.Type = KeyEventData.EventType.KeyUp;
			}
			else
			{
				eventData.Type = KeyEventData.EventType.Unknown;
				return;
			}

			s_LastShiftEventTime = float.PositiveInfinity;

			GetEventModifiersState(eventData.Modifiers, evt);
			eventData.IsNumPad = evt.numeric;
			eventData.IsAutoRepeat = false; // Indeterminate
			eventData.IsSystemKey = evt.alt && !Input.GetKey(KeyCode.AltGr);

			//TODO: Send Enter as Down->Char->Up

			if (evt.keyCode != KeyCode.Escape &&
				evt.keyCode != KeyCode.Tab &&
				evt.character != 0)
			{
				eventData.Type = KeyEventData.EventType.Char;
				eventData.KeyCode = (int)evt.character;

				//Unity3D reports Return character as NewLine and we want CarrageReturn
				if (evt.character == 10)
				{
					eventData.KeyCode = 13;
				}
			}
			else
			{
				eventData.KeyCode = s_KeyCodeMapping[(int)evt.keyCode];
			}

			if (eventData.KeyCode == 0)
			{
				eventData.Type = KeyEventData.EventType.Unknown;
			}
		}

		public static bool ProcessShiftKey(CoherentUIGTKeyEventData eventData, bool shiftPressed)
		{
			if (shiftPressed != s_WasShiftPressed ||
				(shiftPressed && Time.unscaledTime > s_LastShiftEventTime + KEY_REPEAT_RATE))
			{
				if (shiftPressed)
				{
					eventData.Type = KeyEventData.EventType.KeyDown;
				}
				else
				{
					eventData.Type = KeyEventData.EventType.KeyUp;
				}

				s_LastShiftEventTime = Time.unscaledTime;

				if (shiftPressed != s_WasShiftPressed)
				{
					s_LastShiftEventTime += KEY_REPEAT_DELAY;
				}

				eventData.KeyCode = 16;
				eventData.Modifiers.IsShiftDown = shiftPressed;
				s_WasShiftPressed = shiftPressed;

				return true;
			}

			return false;
		}
	}

	public class CoherentUIGTMouseEventData : MouseEventData
	{
		private EventModifiersState m_Modifiers;
		private EventMouseModifiersState m_MouseModifiers;

		new public EventModifiersState Modifiers
		{
			get
			{
				if (m_Modifiers == null)
				{
					m_Modifiers = base.Modifiers;
				}

				return m_Modifiers;
			}
		}

		new public EventMouseModifiersState MouseModifiers
		{
			get
			{
				if (m_MouseModifiers == null)
				{
					m_MouseModifiers = base.MouseModifiers;
				}

				return m_MouseModifiers;
			}
		}
	}

	public class CoherentUIGTKeyEventData : KeyEventData
	{
		private EventModifiersState m_Modifiers;

		new public EventModifiersState Modifiers
		{
			get
			{
				if (m_Modifiers == null)
				{
					m_Modifiers = base.Modifiers;
				}

				return m_Modifiers;
			}
		}
	}
}

using System.Collections;

using UnityEngine;

using UnityEngine.XR;



namespace Demonixis.Toolbox.XR

{

    public enum XRButton

    {

        Menu,

        Button1,

        Button2,

        Button3,

        Button4,

        Thumbstick,

        ThumbstickTouch,

        SecondaryTouchpad,

        SecondaryTouchpadTouch,

        Trigger,

        Grip,

        ThumbstickUp,

        ThumbstickDown,

        ThumbstickLeft,

        ThumbstickRight

    }



    public enum XRAxis

    {

        Trigger,

        Grip,

        ThumbstickX,

        ThumbstickY,

        SecondaryTouchpadX,

        SecondaryTouchpadY

    }



    public enum XRAxis2D

    {

        Thumbstick,

        SecondaryTouchpad

    }



    public enum XRVendor

    {

        None = 0, Oculus, OpenVR, WindowsMR

    }



    public class XRInput : MonoBehaviour

    {

        private static XRInput instance = null;

        private Vector2 _tmp = Vector2.zero;

        private bool[] _axisStates = null;

        private XRButton[] _buttons = null;

        private bool _running = true;

        private XRVendor _inputVendor = XRVendor.None;



        [SerializeField]

        private float _deadZone = 0.1f;



        public static XRInput Instance

        {

            get

            {

                if (instance == null)

                {

                    var go = new GameObject("VRInput");

                    instance = go.AddComponent<XRInput>();

                }

                return instance;

            }

        }



        public XRVendor Vendor { get { return _inputVendor; } }



        public bool IsConnected

        {

            get

            {

#if UNITY_WSA

                var joys = Input.GetJoystickNames();

                foreach (var joystick in joys)

                    if (joystick.Contains("Spatial"))

                        return true;

#endif



                if (XRSettings.loadedDeviceName == "Oculus")

                {

                    var joysticks = Input.GetJoystickNames();

                    foreach (var joystick in joysticks)

                        if (joystick.Contains("Oculus"))

                            return true;

                }

                else if (XRSettings.loadedDeviceName == "OpenVR")

                    return true;

                return false;

            }

        }



        public float DeadZone

        {

            get { return _deadZone; }

            set

            {

                _deadZone = value;



                if (_deadZone < 0)

                    _deadZone = 0.0f;

                else if (_deadZone >= 1.0f)

                    _deadZone = 0.9f;

            }

        }



        public void Awake()

        {

            if (instance != null && instance != this)

            {

                Destroy(this);

                return;

            }



            var vendor = XRSettings.loadedDeviceName;

            if (vendor == "Oculus")

                _inputVendor = XRVendor.Oculus;

            else if (vendor == "OpenVR")

                _inputVendor = XRVendor.OpenVR;

            else if (vendor.Contains("Windows"))

                _inputVendor = XRVendor.WindowsMR;



            _buttons = new XRButton[]

            {

                XRButton.Grip, XRButton.Trigger,

                XRButton.ThumbstickUp, XRButton.ThumbstickDown,

                XRButton.ThumbstickLeft, XRButton.ThumbstickRight

            };



            _axisStates = new bool[_buttons.Length * 2];



            StartCoroutine(UpdateAxisToButton());

        }



        private void OnDestroy()

        {

            _running = false;

        }



        private IEnumerator UpdateAxisToButton()

        {

            var endOfFrame = new WaitForEndOfFrame();

            var index = 0;



            while (_running)

            {

                index = 0;



                for (var i = 0; i < _buttons.Length; i++)

                {

                    _axisStates[index] = GetButton(_buttons[i], true);

                    _axisStates[index + 1] = GetButton(_buttons[i], false);

                    index += 2;

                }



                yield return endOfFrame;

            }

        }



        /// <summary>

        /// Gets the position of a specific node.

        /// </summary>

        /// <param name="node"></param>

        /// <returns></returns>

        public virtual Vector3 GetLocalPosition(XRNode node)

        {

            return InputTracking.GetLocalPosition(node);

        }



        /// <summary>

        /// Gets the rotation of a specific node.

        /// </summary>

        /// <param name="node"></param>

        /// <returns></returns>

        public virtual Quaternion GetLocalRotation(XRNode node)

        {

            return InputTracking.GetLocalRotation(node);

        }



        /// <summary>

        /// Indicates whether a button is pressed.

        /// </summary>

        /// <param name="button">The button.</param>

        /// <param name="left">Left or Right controller.</param>

        /// <returns>Returns true if pressed otherwise it returns false.</returns>

        public bool GetButton(XRButton button, bool left)

        {

            if (button == XRButton.Menu)

            {

                if (_inputVendor == XRVendor.OpenVR)

                    return Input.GetButton(left ? "Button 2" : "Button 0");

                else if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButton(left ? "Button 6" : "Button 7");



                return Input.GetButton("Button 7");

            }



            else if (button == XRButton.Button1)

                return Input.GetButton("Button 0");



            else if (button == XRButton.Button2)

                return Input.GetButton("Button 1");



            else if (button == XRButton.Button3)

                return Input.GetButton("Button 2");



            else if (button == XRButton.Button4)

                return Input.GetButton("Button 3");



            else if (button == XRButton.Thumbstick)

                return Input.GetButton(left ? "Button 8" : "Button 9");



            else if (button == XRButton.ThumbstickTouch)

            {

                if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButton(left ? "Button 18" : "19");

                else

                    return Input.GetButton(left ? "Button 16" : "17");

            }



            else if (button == XRButton.SecondaryTouchpad)

                return Input.GetButton(left ? "Button 16" : "17");



            else if (button == XRButton.SecondaryTouchpad)

                return Input.GetButton(left ? "Button 18" : "19");



            else if (button == XRButton.Trigger)

                return GetAxis(XRAxis.Trigger, left) > _deadZone;



            else if (button == XRButton.Grip)

                return GetAxis(XRAxis.Grip, left) > _deadZone;



            else if (button == XRButton.ThumbstickUp)

                return GetAxis(XRAxis.ThumbstickY, left) > _deadZone;



            else if (button == XRButton.ThumbstickDown)

                return GetAxis(XRAxis.ThumbstickY, left) < _deadZone * -1.0f;



            else if (button == XRButton.ThumbstickLeft)

                return GetAxis(XRAxis.ThumbstickX, left) < _deadZone * -1.0f;



            else if (button == XRButton.ThumbstickRight)

                return GetAxis(XRAxis.ThumbstickX, left) > _deadZone;



            return false;

        }



        /// <summary>

        /// Indicates whether a button was pressed.

        /// </summary>

        /// <param name="button">The button.</param>

        /// <param name="left">Left or Right controller.</param>

        /// <returns>Returns true if pressed otherwise it returns false.</returns>

        public bool GetButtonDown(XRButton button, bool left)

        {

            if (button == XRButton.Menu)

            {

                if (_inputVendor == XRVendor.OpenVR)

                    return Input.GetButtonDown(left ? "Button 2" : "Button 0");

                else if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButtonDown(left ? "Button 6" : "Button 7");



                return Input.GetButtonDown("Button 7");

            }



            else if (button == XRButton.Button1)

                return Input.GetButtonDown("Button 0");



            else if (button == XRButton.Button2)

                return Input.GetButtonDown("Button 1");



            else if (button == XRButton.Button3)

                return Input.GetButtonDown("Button 2");



            else if (button == XRButton.Button4)

                return Input.GetButtonDown("Button 3");



            else if (button == XRButton.Thumbstick)

                return Input.GetButtonDown(left ? "Button 8" : "Button 9");



            else if (button == XRButton.ThumbstickTouch)

            {

                if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButtonDown(left ? "Button 18" : "19");

                else

                    return Input.GetButtonDown(left ? "Button 16" : "17");

            }



            // Simulate other buttons using previous states.

            var index = 0;

            for (var i = 0; i < _buttons.Length; i++)

            {

                if (_buttons[i] != button)

                {

                    index += 2;

                    continue;

                }



                var prev = _axisStates[left ? index : index + 1];

                var now = GetButton(_buttons[i], left);



                return now && !prev;

            }



            return false;

        }



        /// <summary>

        /// Indicates whether a button was released.

        /// </summary>

        /// <param name="button">The button.</param>

        /// <param name="left">Left or Right controller.</param>

        /// <returns>Returns true if pressed otherwise it returns false.</returns>

        public bool GetButtonUp(XRButton button, bool left)

        {

            if (button == XRButton.Menu)

            {

                if (_inputVendor == XRVendor.OpenVR)

                    return Input.GetButtonUp(left ? "Button 2" : "Button 0");

                else if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButtonUp(left ? "Button 6" : "Button 7");



                return Input.GetButtonUp("Button 7");

            }



            else if (button == XRButton.Button1)

                return Input.GetButtonUp("Button 0");



            else if (button == XRButton.Button2)

                return Input.GetButtonUp("Button 1");



            else if (button == XRButton.Button3)

                return Input.GetButtonUp("Button 2");



            else if (button == XRButton.Button4)

                return Input.GetButtonUp("Button 3");



            else if (button == XRButton.Thumbstick)

                return Input.GetButtonUp(left ? "Button 8" : "Button 9");



            else if (button == XRButton.ThumbstickTouch)

            {

                if (_inputVendor == XRVendor.WindowsMR)

                    return Input.GetButtonUp(left ? "Button 18" : "19");

                else

                    return Input.GetButtonUp(left ? "Button 16" : "17");

            }



            // Simulate other buttons using previous states.

            var index = 0;

            for (var i = 0; i < _buttons.Length; i++)

            {

                if (_buttons[i] != button)

                {

                    index += 2;

                    continue;

                }



                var prev = _axisStates[left ? index : index + 1];

                var now = GetButton(_buttons[i], left);



                return !now && prev;

            }



            return false;

        }



        /// <summary>

        /// Gets an axis value.

        /// </summary>

        /// <param name="axis">The axis.</param>

        /// <param name="left">Left or Right controller.</param>

        /// <returns>Returns the axis value.</returns>

        public float GetAxis(XRAxis axis, bool left)

        {

            if (axis == XRAxis.Trigger)

                return Input.GetAxis(left ? "Axis 9" : "Axis 10");



            else if (axis == XRAxis.Grip)

                return Input.GetAxis(left ? "Axis 11" : "Axis 12");



            else if (axis == XRAxis.ThumbstickX)

                return Input.GetAxis(left ? "Axis 1" : "Axis 4");



            else if (axis == XRAxis.ThumbstickY)

                return Input.GetAxis(left ? "Axis 2" : "Axis 5");



            else if (axis == XRAxis.SecondaryTouchpadX)

                return Input.GetAxis(left ? "Axis 17" : "Axis 20");



            else if (axis == XRAxis.SecondaryTouchpadY)

                return Input.GetAxis(left ? "Axis 18" : "Axis 21");



            return 0.0f;

        }



        /// <summary>

        /// Gets two axis values.

        /// </summary>

        /// <param name="axis"></param>

        /// <param name="left">Left or Right controller.</param>

        /// <returns>Returns two axis values.</returns>

        public Vector2 GetAxis2D(XRAxis2D axis, bool left)

        {

            _tmp.x = 0;

            _tmp.y = 0;



            if (axis == XRAxis2D.Thumbstick)

            {

                _tmp.x = Input.GetAxis(left ? "Axis 1" : "Axis 4");

                _tmp.y = Input.GetAxis(left ? "Axis 2" : "Axis 5");

            }

            else if (axis == XRAxis2D.SecondaryTouchpad)

            {

                _tmp.x = Input.GetAxis(left ? "Axis 17" : "Axis 20");

                _tmp.y = Input.GetAxis(left ? "Axis 18" : "Axis 21");

            }



            return _tmp;

        }

    }

}
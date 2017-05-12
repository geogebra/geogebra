window.ZSpace = (function () {
  "use strict";

  var displayDevice = null;
  var stylusDevice = null;
  var stylusButtonsDevice = null;

  var zspace = function (gl, c) {
    this.gl = gl;
    this.canvas = c;

    this.frameBuffer = null;
    this.renderBuffer = null;

    this.frameBufferDepthTexture = null;

    this.swapStereo = false;

    this.nearClip = 0.1;
    this.farClip = 10000.0;
    this.viewerScale = 1.0;

    this.leftViewMatrix = mat4.create();
    this.rightViewMatrix = mat4.create();
    this.leftProjectionMatrix = mat4.create();
    this.rightProjectionMatrix = mat4.create();
    this.stylusCameraMatrix = mat4.create();
    this.viewportSpaceStylusPose = mat4.create();
    this.displaySpaceStylusPose = mat4.create();
    this.displaySpaceHeadPose = mat4.create();
    this.viewportSpaceHeadPose = mat4.create();
    this.cameraToViewportMatrix = mat4.create();
    this.cameraToDisplayMatrix = mat4.create();
    this.displayAngleX = 90.0;

    this.currentWidth = 0;
    this.currentHeight = 0;

    this.buttonPressed = [0, 0, 0];

    this.useExternalTexture = false;


    this.stereoEnable = true;
    this.stylusGamepad = null;
    this.canvasOffset = [0, 0];
    this.frameBufferTexture = null;
    this.presenting = false;
    this.fullscreen = false;
  }

  // Helper function to get an element's exact position
  function getPosition(zspace) {
    var xPos = 0;
    var yPos = 0;
    xPos = window.screenX + zspace.canvas.offsetLeft - screen.availLeft + zspace.canvasOffset[0];
    yPos = window.screenY + zspace.canvas.offsetTop + 75 + zspace.canvasOffset[1];

    return {
      x: xPos,
      y: yPos
    };
  }

  function keyEvent(e, zspace) {
    var e = window.event;

    if (e.keyCode == '90') {
      zspace.swapStereo = !zspace.swapStereo;
    }

    //if (e.keyCode == '70') {
    //  zspace.fullscreen = !zspace.fullscreen;
    //  if (zspace.fullscreen) {
    //    if (zspace.canvas.webkitRequestFullScreen) {
    //      zspace.canvas.webkitRequestFullScreen();
    //    }
    //  }
    //}

    //if (e.keyCode == '50') {
    //  if (zspace.stylusGamepad && zspace.stylusGamepad.haptics && zspace.stylusGamepad.haptics[0]) {
    //    zspace.stylusGamepad.haptics[0].vibrate(1.0, 0.5);
    //  }
    //}

    //if (e.keyCode == '51') {
    //  if (zspace.stylusGamepad && zspace.stylusGamepad.haptics && zspace.stylusGamepad.haptics[0]) {
    //    zspace.stylusGamepad.haptics[0].vibrate(1.0, 1.0);
    //  }
    //}

    //if (e.keyCode == '77') {
    //  zspace.stereoEnable = !zspace.stereoEnable;
    //  if (!zspace.stereoEnable) {
    //    if (displayDevice != null) {
    //      displayDevice.requestPresent([{ source: zspace.canvas, rightSource: null }]);
    //      zspace.presenting = false;
    //    }
    //    return;
    //  } else {
    //    if (displayDevice != null) {
    //      displayDevice.requestPresent([{ source: zspace.canvas, rightSource: zspace.frameBufferTexture }]);
    //      zspace.presenting = true;
    //    }
    //  }
    //}

    //if (e.keyCode == '80') {
    //  if (!zspace.stereoEnable) {
    //    if (displayDevice != null) {
    //      displayDevice.requestPresent([{ source: zspace.canvas, rightSource: null }]);
    //      zspace.presenting = false;
    //    }
    //    return;
    //  } else {
    //    if (displayDevice != null) {
    //      displayDevice.requestPresent([{ source: zspace.canvas, rightSource: zspace.frameBufferTexture }]);
    //      zspace.presenting = true;
    //    }
    //  }
    //}
  }

  function zSpaceConnectHandler(e, zspace) {
    zspace.stylusGamepad = e.gamepad;
  }

  function zSpaceDisconnectHandler(e, zspace) {
    zspace.stylusGamepad = null;
  }

  function onVRPresentChange(zspace) {
    if (displayDevice != null) {
      if (displayDevice.isPresenting) {
        zspace.presenting = true;
      } else {
        zspace.presenting = false;
      }
    }
  }

  function onVRRequestPresent(zspace) {
    if (displayDevice != null) {
      displayDevice.requestPresent([{ source: zspace.canvas, rightSource: zspace.frameBufferTexture }]).then(function () {
        zspace.presenting = true;
      }, function () {
        zspace.presenting = false;
      });
    }
  }

  function onVRExitPresent(zspace) {
    if (displayDevice != null) {
      if (!displayDevice.isPresenting) {
        return;
      }
      displayDevice.exitPresent().then(function () {
        zspace.presenting = false;
      }, function () {
        zspace.presenting = true;
      });
    }
  }

  zspace.prototype.setExternalTexture = function setExternalTexture(texture) {
    this.frameBufferTexture = texture;
    if (texture) {
      this.useExternalTexture = true;
    } else {
      this.useExternalTexture = false;
    }
  }

  zspace.prototype.setViewerScale = function setViewerScale(scale) {
    this.viewerScale = scale;
  }

  zspace.prototype.setFarClip = function setFarClip(clip) {
    this.farClip = clip;
  }

  zspace.prototype.setNearClip = function setNearClip(clip) {
    this.nearClip = clip;
  }

  zspace.prototype.setCanvasOffset = function setCanvasOffset(x, y) {
    this.canvasOffset[0] = x;
    this.canvasOffset[1] = y;
  }

  zspace.prototype.vibrateStylus = function vibrateStylus(intensity, duration) {
    if (this.stylusGamepad && this.stylusGamepad.haptics && this.stylusGamepad.haptics[0]) {
      this.stylusGamepad.haptics[0].vibrate(intensity, duration);
    }
  }

  zspace.prototype.zspaceInit = function zspaceInit() {
    if (navigator.getVRDisplays) {
      navigator.getVRDisplays().then(function (displays) {
        if (displays.length > 0) {
          var i;
          for (i = 0; i < displays.length; i++) {
            if (displays[i].displayName == "ZSpace Display") {
              displayDevice = displays[i];
            }

            if (displays[i].displayName == "ZSpace Stylus") {
              stylusDevice = displays[i];
            }

            if (displays[i].displayName == "ZSpace Stylus Buttons") {
              stylusButtonsDevice = displays[i];
            }
          }
        }
      });
    }

    var me = this;
    //window.addEventListener("gamepadconnected", function () { zSpaceConnectHandler(e, me) }, false);
    //window.addEventListener("gamepaddisconnected", function () { zSpaceDisconnectHandler(e, me) }, false);
    window.addEventListener('vrdisplaypresentchange', function () { onVRPresentChange(me) }, false);
    window.addEventListener('vrdisplayactivate', function () { onVRRequestPresent(me) }, false);
    window.addEventListener('vrdisplaydeactivate', function () { onVRExitPresent(me) }, false);
    window.addEventListener("keydown", function (e) { keyEvent(e, me) }, false);
  }

  zspace.prototype.allocateBuffers = function allocateBuffers() {

    if (this.frameBuffer == null) {
      this.frameBuffer = this.gl.createFramebuffer();
    }

    this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, this.frameBuffer);

    if (this.renderBuffer != null) {
      this.gl.deleteRenderbuffer(this.renderBuffer);
      this.renderBuffer = null;
    }

    if (this.renderBuffer == null) {
      this.renderBuffer = this.gl.createRenderbuffer();
      this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, this.renderBuffer);

      this.gl.renderbufferStorage(this.gl.RENDERBUFFER, this.gl.DEPTH_STENCIL, this.canvas.clientWidth, this.canvas.clientHeight);
      this.gl.framebufferRenderbuffer(this.gl.FRAMEBUFFER, this.gl.DEPTH_STENCIL_ATTACHMENT, this.gl.RENDERBUFFER, this.renderBuffer);

      this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, null);
    }

    this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, this.renderBuffer);

    if (this.frameBufferTexture != null) {
      this.gl.deleteTexture(this.frameBufferTexture);
    }

    this.frameBufferTexture = this.gl.createTexture();
    this.gl.bindTexture(this.gl.TEXTURE_2D, this.frameBufferTexture);
    this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MIN_FILTER, this.gl.LINEAR);
    this.gl.texParameteri(this.gl.TEXTURE_2D, this.gl.TEXTURE_MAG_FILTER, this.gl.LINEAR);
    this.gl.texImage2D(this.gl.TEXTURE_2D, 0, this.gl.RGBA, this.canvas.clientWidth, this.canvas.clientHeight, 0, this.gl.RGBA, this.gl.UNSIGNED_BYTE, null);

    this.gl.framebufferTexture2D(this.gl.FRAMEBUFFER, this.gl.COLOR_ATTACHMENT0, this.gl.TEXTURE_2D, this.frameBufferTexture, 0);

    this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, null);
    this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, null);
  }

  zspace.prototype.zspaceLeftView = function zspaceLeftView() {
    if (!this.stereoEnable)
      return;

    if (this.swapStereo) {
      if (!this.useExternalTexture) {
        this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, this.frameBuffer);
        this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, this.renderBuffer);
      }
    }
    else {
      this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, null);
      this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, null);
    }
  }

  zspace.prototype.zspaceRightView = function zspaceRightView() {
    if (!this.stereoEnable)
      return;

    if (this.swapStereo) {
      this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, null);
      this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, null);
    }
    else {
      if (!this.useExternalTexture) {
        this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, this.frameBuffer);
        this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, this.renderBuffer);
      }
    }
  }

  zspace.prototype.zspaceFrameEnd = function zspaceFrameEnd() {
    if (!this.stereoEnable)
      return;

    this.gl.bindFramebuffer(this.gl.FRAMEBUFFER, null);
    this.gl.bindRenderbuffer(this.gl.RENDERBUFFER, null);
    if (this.presenting) {
      displayDevice.submitFrame();
    }
  }

  zspace.prototype.makeProjection = function makeProjection(projection, up, down, left, right) {
    var o = Math.tan(up);
    var u = Math.tan(down);
    var l = Math.tan(left);
    var e = Math.tan(right);
    var M = 2 / (l + e), s = 2 / (o + u);
    projection[0] = M;
    projection[1] = 0;
    projection[2] = 0;
    projection[3] = 0;
    projection[4] = 0;
    projection[5] = s;
    projection[6] = 0;
    projection[7] = 0;
    projection[8] = -((l - e) * M * .5);
    projection[9] = (o - u) * s * .5;
    projection[10] = this.farClip / (this.nearClip - this.farClip);
    projection[11] = -1;
    projection[12] = 0;
    projection[13] = 0;
    projection[14] = this.farClip * this.nearClip / (this.nearClip - this.farClip);
    projection[15] = 0;
  }

  zspace.prototype.zspaceUpdate = function zspaceUpdate() {

    var displaySize = [0.521, 0.293];
    var displayResolution = [1920, 1080];
    var displayScaleFactor = [0.0, 0.0];
    var IPD = 0.06;
    displayScaleFactor[0] = displaySize[0] / displayResolution[0];
    displayScaleFactor[1] = displaySize[1] / displayResolution[1];

    if (this.currentWidth != this.canvas.clientWidth ||
        this.currentHeight != this.canvas.clientHeight) {
      this.currentWidth = this.canvas.clientWidth;
      this.currentHeight = this.canvas.clientHeight;
      if (!this.useExternalTexture) {
        this.allocateBuffers();
      }
    }

    if (displayDevice != null && displayDevice.isPresenting) {
      displayDevice.requestPresent([{ source: this.canvas, rightSource: this.frameBufferTexture }]);
    }

    // Find the offset of the canvas from the browser window
    var canvasPosition = getPosition(this);
    var canvasWidth = this.canvas.clientWidth * displayScaleFactor[0] * this.viewerScale;
    var canvasHeight = this.canvas.clientHeight * displayScaleFactor[1] * this.viewerScale;

    // Calculate the offset to use for moving the WebVR data from screen relative
    // to window relative.
    var displayCenterX = displayResolution[0] * 0.5;
    var displayCenterY = displayResolution[1] * 0.5;
    var viewportCenterX = canvasPosition.x + (this.canvas.clientWidth * 0.5);
    var viewportCenterY = displayResolution[1] - (canvasPosition.y + (this.canvas.clientHeight * 0.5));

    var viewportShift = [0.0, 0.0, 0.0];
    viewportShift[0] = (viewportCenterX - displayCenterX) * displayScaleFactor[0];
    viewportShift[1] = (viewportCenterY - displayCenterY) * displayScaleFactor[1];
    var offsetTranslation = mat4.create();
    mat4.identity(offsetTranslation);
    mat4.translate(offsetTranslation, offsetTranslation, viewportShift);

    // Crete the scale matrix to use for viewer scale
    var viewScale = mat4.create();
    mat4.identity(viewScale);
    var scale = vec3.create();
    scale[0] = this.viewerScale; scale[1] = this.viewerScale; scale[2] = this.viewerScale;
    mat4.scale(viewScale, viewScale, scale);

    var frameData = new VRFrameData();
    if (displayDevice) {
      // Get the left view matrix and shift it to compensate for the window shift and viewer scale
      var eyePoseMatrix = mat4.create();
      var newPosition = vec3.create();
      var currentPosition = vec3.create();
      displayDevice.getFrameData(frameData);
      mat4.set(this.leftViewMatrix,
               frameData.leftViewMatrix[0], frameData.leftViewMatrix[1], frameData.leftViewMatrix[2], frameData.leftViewMatrix[3],
               frameData.leftViewMatrix[4], frameData.leftViewMatrix[5], frameData.leftViewMatrix[6], frameData.leftViewMatrix[7],
               frameData.leftViewMatrix[8], frameData.leftViewMatrix[9], frameData.leftViewMatrix[10], frameData.leftViewMatrix[11],
               frameData.leftViewMatrix[12], frameData.leftViewMatrix[13], frameData.leftViewMatrix[14], frameData.leftViewMatrix[15]);
      currentPosition[0] = this.leftViewMatrix[12];
      currentPosition[1] = this.leftViewMatrix[13];
      currentPosition[2] = this.leftViewMatrix[14];
      vec3.transformMat4(newPosition, currentPosition, offsetTranslation);
      vec3.transformMat4(newPosition, newPosition, viewScale);
      this.leftViewMatrix[12] = newPosition[0];
      this.leftViewMatrix[13] = newPosition[1];
      this.leftViewMatrix[14] = newPosition[2];

      // Get the right view matrix and shift it to compensate for the window shift and viewer scale
      mat4.set(this.rightViewMatrix,
         frameData.rightViewMatrix[0], frameData.rightViewMatrix[1], frameData.rightViewMatrix[2], frameData.rightViewMatrix[3],
         frameData.rightViewMatrix[4], frameData.rightViewMatrix[5], frameData.rightViewMatrix[6], frameData.rightViewMatrix[7],
         frameData.rightViewMatrix[8], frameData.rightViewMatrix[9], frameData.rightViewMatrix[10], frameData.rightViewMatrix[11],
         frameData.rightViewMatrix[12], frameData.rightViewMatrix[13], frameData.rightViewMatrix[14], frameData.rightViewMatrix[15]);
      currentPosition[0] = this.rightViewMatrix[12];
      currentPosition[1] = this.rightViewMatrix[13];
      currentPosition[2] = this.rightViewMatrix[14];
      vec3.transformMat4(newPosition, currentPosition, offsetTranslation);
      vec3.transformMat4(newPosition, newPosition, viewScale);
      this.rightViewMatrix[12] = newPosition[0];
      this.rightViewMatrix[13] = newPosition[1];
      this.rightViewMatrix[14] = newPosition[2];

      offsetTranslation[12] = -offsetTranslation[12];
      offsetTranslation[13] = -offsetTranslation[13];

      // Use the display space pose in the frame data to calculate the correct window based projections.
      var leftEye = vec3.create();
      var leftEyeDisplay = vec3.create();
      var centerEyeDisplay = vec3.create();
      var centerEyeViewport = vec3.create();
      
      centerEyeDisplay[0] = frameData.pose.position[0]; centerEyeDisplay[1] = frameData.pose.position[1];  centerEyeDisplay[2] = frameData.pose.position[2]; 
      mat4.fromRotationTranslation(eyePoseMatrix, frameData.pose.orientation, frameData.pose.position);

      centerEyeViewport[0] = frameData.pose.position[0] + offsetTranslation[12];
      centerEyeViewport[1] = frameData.pose.position[1] + offsetTranslation[13];
      centerEyeViewport[2] = frameData.pose.position[2] + offsetTranslation[14];
      mat4.fromRotationTranslation(this.displaySpaceHeadPose, frameData.pose.orientation, frameData.pose.position);
      mat4.fromRotationTranslation(this.viewportSpaceHeadPose, frameData.pose.orientation, centerEyeViewport);
      
      var xAxis = vec3.create();
      xAxis[0] = eyePoseMatrix[0]; xAxis[1] = eyePoseMatrix[1]; xAxis[2] = eyePoseMatrix[2];
      vec3.scaleAndAdd(leftEyeDisplay, centerEyeDisplay, xAxis ,- IPD / 2.0);

      vec3.transformMat4(leftEye, leftEyeDisplay, offsetTranslation);
      vec3.transformMat4(leftEye, leftEye, viewScale);

      var up = Math.atan((canvasHeight * 0.5 - leftEye[1]) / leftEye[2]);
      var down = Math.atan((canvasHeight * 0.5 + leftEye[1]) / leftEye[2]);
      var left = Math.atan((canvasWidth * 0.5 + leftEye[0]) / leftEye[2]);
      var right = Math.atan((canvasWidth * 0.5 - leftEye[0]) / leftEye[2]);
      this.makeProjection(this.leftProjectionMatrix, up, down, left, right);

      var rightEye = vec3.create();
      var rightEyeDisplay = vec3.create();
      vec3.scaleAndAdd(rightEyeDisplay, centerEyeDisplay, xAxis, IPD / 2.0);

      vec3.transformMat4(rightEye, rightEyeDisplay, offsetTranslation);
      vec3.transformMat4(rightEye, rightEye, viewScale);

      var up = Math.atan((canvasHeight * 0.5 - rightEye[1]) / rightEye[2]);
      var down = Math.atan((canvasHeight * 0.5 + rightEye[1]) / rightEye[2]);
      var left = Math.atan((canvasWidth * 0.5 + rightEye[0]) / rightEye[2]);
      var right = Math.atan((canvasWidth * 0.5 - rightEye[0]) / rightEye[2]);
      this.makeProjection(this.rightProjectionMatrix, up, down, left, right);

      // Get the display angle and the Camera to Display Transform
      this.displayAngleX = frameData.timestamp;
      mat4.set(this.cameraToDisplayMatrix,
        frameData.leftProjectionMatrix[0], frameData.leftProjectionMatrix[1], frameData.leftProjectionMatrix[2], frameData.leftProjectionMatrix[3],
        frameData.leftProjectionMatrix[4], frameData.leftProjectionMatrix[5], frameData.leftProjectionMatrix[6], frameData.leftProjectionMatrix[7],
        frameData.leftProjectionMatrix[8], frameData.leftProjectionMatrix[9], frameData.leftProjectionMatrix[10], frameData.leftProjectionMatrix[11],
        frameData.leftProjectionMatrix[12], frameData.leftProjectionMatrix[13], frameData.leftProjectionMatrix[14], frameData.leftProjectionMatrix[15]);
    }

    // TODO: Enable this gamepad processing once the gamepad bugs are resolved on the 300.

    //var gamepads = navigator.getGamepads();
    //for (var i = 0; i < gamepads.length; i++) {
    //  if (gamepads[i]) {
    //    if (gamepads[i].id == "zSpace Stylus Gamepad") {
    //      this.stylusGamepad = gamepads[i];
    //    }
    //  }
    //}

    //if (this.stylusGamepad) {
    //  var stylusPose = this.stylusGamepad.pose;
    //  if (stylusPose != null) {
    //    var newPosition = vec3.create();
    //    vec3.transformMat4(newPosition, stylusPose.position, offsetTranslation);
    //    vec3.transformMat4(newPosition, newPosition, viewScale);
    //    mat4.fromRotationTranslation(this.stylusCameraMatrix, stylusPose.orientation, newPosition);

    //    this.buttonPressed[0] = stylusGamepad.buttons[0].pressed;
    //    this.buttonPressed[1] = stylusGamepad.buttons[1].pressed;
    //    this.buttonPressed[2] = stylusGamepad.buttons[2].pressed;
    //  }
    //}

    // Get the stylus camera pose from the stylus device and compensate for window shift and viewer scale
    if (stylusDevice) {
      stylusDevice.getFrameData(frameData);
      var stylusPose = frameData.pose;
      if (stylusPose && stylusPose.orientation && stylusPose.position) {
        var newPosition = vec3.create();
        vec3.transformMat4(newPosition, stylusPose.position, offsetTranslation);
        vec3.transformMat4(newPosition, newPosition, viewScale);
        mat4.fromRotationTranslation(this.stylusCameraMatrix, stylusPose.orientation, newPosition);

        var cameraSpaceStylusMatrix = mat4.create();
        mat4.fromRotationTranslation(cameraSpaceStylusMatrix, stylusPose.orientation, stylusPose.position);
        mat4.multiply(this.displaySpaceStylusPose, this.cameraToDisplayMatrix, cameraSpaceStylusMatrix);

        mat4.multiply(this.cameraToViewportMatrix, offsetTranslation, this.cameraToDisplayMatrix);
        mat4.multiply(this.viewportSpaceStylusPose, this.cameraToViewportMatrix, cameraSpaceStylusMatrix);
      }
    } else {
      mat4.identity(this.stylusCameraMatrix);
    }

    // Get the stylus button states from another stylus device
    if (stylusButtonsDevice) {
      stylusButtonsDevice.getFrameData(frameData);
      var stylusButtonsState = frameData.pose;
      if (stylusButtonsState && stylusButtonsState.position) {
        this.buttonPressed[0] = stylusButtonsState.position[0];
        this.buttonPressed[1] = stylusButtonsState.position[1];
        this.buttonPressed[2] = stylusButtonsState.position[2];
      }
    } else {
      this.buttonPressed[0] = 0;
      this.buttonPressed[1] = 0;
      this.buttonPressed[2] = 0;
    }
  }

  return zspace;
})();
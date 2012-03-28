/*
Copyright (c) 2005-2008 Apple Inc.  All Rights Reserved.

IMPORTANT:  This Apple software and the associated images located in
/System/Library/WidgetResources/AppleClasses/ (collectively "Apple Software")
are supplied to you by Apple Inc. (“Apple”) in consideration of your
agreement to the following terms. Your use, installation and/or redistribution
of this Apple Software constitutes acceptance of these terms. If you do not
agree with these terms, please do not use, install, or redistribute this Apple
Software.

In consideration of your agreement to abide by the following terms, and subject
to these terms, Apple grants you a personal, non-exclusive license, under
Apple’s copyrights in the Apple Software, to use, reproduce, and redistribute
the Apple Software, in text form (for JavaScript files) or binary form (for
associated images), for the sole purpose of creating Dashboard widgets for Mac
OS X.

If you redistribute the Apple Software, you must retain this entire notice and
the warranty disclaimers and limitation of liability provisions (last two
paragraphs below) in all such redistributions of the Apple Software.

You may not use the name, trademarks, service marks or logos of Apple to endorse
or promote products that include the Apple Software without the prior written
permission of Apple. Except as expressly stated in this notice, no other rights
or licenses, express or implied, are granted by Apple herein, including but not
limited to any patent rights that may be infringed by your products that
incorporate the Apple Software or by other works in which the Apple Software may
be incorporated.

The Apple Software is provided on an "AS IS" basis.  APPLE MAKES NO WARRANTIES,
EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF
NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE,
REGARDING THE APPPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN COMBINATION
WITH YOUR PRODUCTS.

IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, AND/OR DISTRIBUTION OF THE
APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT
(INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

function AppleSlider(slider, onchanged)
{
}

/*
 * init() member function
 * Initialize the slider.
 * You do not need to call this directly, it will be called when necessary.
 * You probably want to be calling refresh().
 * pre: this.slider
 * post: this._thumb, this._track + event handlers
 */
AppleSlider.prototype._init = function()
{
	// For JavaScript event handlers
	var _self = this;
	
	this._captureEventHandler = function(event) { _self._captureEvent(event); };
	this._mousedownThumbHandler = function(event) { _self._mousedownThumb(event); };
	this._mousemoveThumbHandler = function(event) { _self._mousemoveThumb(event); };
	this._mouseupThumbHandler = function(event) { _self._mouseupThumb(event); };
	this._mousedownTrackHandler = function(event) { _self._mousedownTrack(event); };
	
	var style = null;
	var element = null;
	
	// Slider Track
	this._track = document.createElement("div");
	style = this._track.style;
	// fill our containing div
	style.height = "100%";
	style.width = "100%";
	this.slider.appendChild(this._track);
	
	// Slider Track Left
	element = document.createElement("div");
	element.style.position = "absolute";
	this._setObjectStart(element, 0);
	this._track.appendChild(element);
	
	// Slider Track Middle
	element = document.createElement("div");
	element.style.position = "absolute";
	this._track.appendChild(element);
	
	// Slider Track Right
	element = document.createElement("div");
	element.style.position = "absolute";
	this._setObjectEnd(element, 0);
	this._track.appendChild(element);
	
	// Slider Thumb
	this._thumb = document.createElement("div");
	style = this._thumb.style;
	style.position = "absolute";
	this._track.appendChild(this._thumb);
	
	this.setSize(this.size);
	this.setTrackStart(this.trackStartPath, this.trackStartLength);
	this.setTrackMiddle(this.trackMiddlePath);
	this.setTrackEnd(this.trackEndPath, this.trackEndLength);
	this.setThumb(this.thumbPath, this.thumbLength);
	
	this.slider.style.appleDashboardRegion = "dashboard-region(control rectangle)";
	
	// Add event listeners
	this._track.addEventListener("mousedown", this._mousedownTrackHandler, true);
	this._thumb.addEventListener("mousedown", this._mousedownThumbHandler, true);
	
	this.refresh();
}

AppleSlider.prototype.remove = function()
{
	var parent = this._track.parentNode;
	parent.removeChild(this._track);
}

/*
 * refresh() member function
 * Refresh the current slider position and size.
 * Call this to make the slider appear after the widget has loaded and 
 * the AppleSlider object has been instantiated.
 */
AppleSlider.prototype.refresh = function()
{
	// get the scrollbar offset
	this._trackOffset = this._computeTrackOffset();
	this._sliderLength = this._computeSliderLength();
	
	this._numScrollablePixels = this._sliderLength - this.thumbLength - (2 * this.padding);
	
	this.slideTo(this._numScrollablePixels * this.value + this.padding);
}

AppleSlider.prototype.slideTo = function(newThumbPos)
{	
	if (newThumbPos < this.padding)
	{
		newThumbPos = this.padding;
	}
	else if (newThumbPos > this._numScrollablePixels)
	{
		newThumbPos = this._numScrollablePixels;
	}
	
	this.value = (newThumbPos - this.padding) / (this._numScrollablePixels - this.padding);
	this._setObjectStart(this._thumb, newThumbPos);
	
	if (this.continuous && this.onchanged != null)
		this.onchanged(this.value);
}

// Capture events that we don't handle but also don't want getting through
AppleSlider.prototype._captureEvent = function(event)
{
	event.stopPropagation();
	event.preventDefault();
}

/*********************
 * Thumb scroll events
 */
AppleSlider.prototype._mousedownThumb = function(event)
{
	// temporary event listeners
	document.addEventListener("mousemove", this._mousemoveThumbHandler, true);
	document.addEventListener("mouseup", this._mouseupThumbHandler, true);
	document.addEventListener("mouseover", this._captureEventHandler, true);
	document.addEventListener("mouseout", this._captureEventHandler, true);
	
	this._thumbStartTemp = this._getMousePosition(event);
	
	this._sliderThumbStartPos = this._getThumbStartPos();

	event.stopPropagation();
	event.preventDefault();
}

AppleSlider.prototype._mousemoveThumb = function(event)
{
	var delta = this._getMousePosition(event) - this._thumbStartTemp
	
	var new_pos = this._sliderThumbStartPos + delta;
	this.slideTo(new_pos);
	
	event.stopPropagation();
	event.preventDefault();
}

AppleSlider.prototype._mouseupThumb = function(event)
{
	document.removeEventListener("mousemove", this._mousemoveThumbHandler, true);
	document.removeEventListener("mouseup", this._mouseupThumbHandler, true);
	document.removeEventListener("mouseover", this._captureEventHandler, true);
	document.removeEventListener("mouseout", this._captureEventHandler, true);
	
	// reset the starting position
	delete this._thumbStartTemp;
	delete this._sliderThumbStartPos;
	
	event.stopPropagation();
	event.preventDefault();
	
	// Fire our onchanged event now if they have discontinuous event firing
	if (!this.continuous && this.onchanged != null)
		this.onchanged(this.value);
}

/*********************
 * Track scroll events
 */
AppleSlider.prototype._mousedownTrack = function(event)
{
	this._thumbStartTemp = this._getMousePosition(event);
	this._sliderThumbStartPos = this._getMousePosition(event) - this._trackOffset - (this.thumbLength / 2);
	
	// temporary event listeners
	document.addEventListener("mousemove", this._mousemoveThumbHandler, true);
	document.addEventListener("mouseup", this._mouseupThumbHandler, true);
	document.addEventListener("mouseover", this._captureEventHandler, true);
	document.addEventListener("mouseout", this._captureEventHandler, true);
	
	this.slideTo(this._sliderThumbStartPos);
} 

AppleSlider.prototype.setSize = function(size)
{
	this.size = size;
	
	this._setObjectSize(this.slider, size);
	this._setObjectSize(this._track.children[1], size);
	this._setObjectSize(this._thumb, size);
}

AppleSlider.prototype.setTrackStart = function(imgpath, length)
{
	this.trackStartPath = imgpath;
	this.trackStartLength = length;
	
	var element = this._track.children[0];
	element.style.background = "url(" + imgpath + ") no-repeat top left";
	this._setObjectLength(element, length);
	this._setObjectSize(element, this.size);
	this._setObjectStart(this._track.children[1], length);
}

AppleSlider.prototype.setTrackMiddle = function(imgpath)
{
	this.trackMiddlePath = imgpath;
	
	this._track.children[1].style.background = "url(" + imgpath + ") " + this._repeatType + " top left";
}

AppleSlider.prototype.setTrackEnd = function(imgpath, length)
{
	this.trackEndPath = imgpath;
	this.trackEndLength = length;
	
	var element = this._track.children[2];
	element.style.background = "url(" + imgpath + ") no-repeat top left";
	this._setObjectLength(element, length);
	this._setObjectSize(element, this.size);
	this._setObjectEnd(this._track.children[1], length);
}

AppleSlider.prototype.setThumb = function(imgpath, length)
{
	this.thumbPath = imgpath;
	this.thumbLength = length;

	this._thumb.style.background = "url(" + imgpath + ") no-repeat top left";
	this._setObjectLength(this._thumb, length);
}

AppleSlider.prototype.setValue = function(newvalue)
{
	this.slideTo(this._numScrollablePixels * newvalue + this.padding);
}

/*******************************************************************************
* AppleHorizontalSlider
* Implementation of AppleSlider
*
*
*/

function AppleHorizontalSlider(slider, onchanged)
{
	/* Objects */
	this.slider = slider;
	
	/* public properties */
	// These are read-write. Set them as needed.
	this.onchanged = onchanged;
	this.continuous = true; // Fire onchanged live, as opposed to onmouseup
	this.padding = 0;
	
	// These are read-only. Use the setter functions to set them.
	this.value = 0.0;
	this.size = 22;
	this.trackStartPath = "AppleClasses/Images/slide_track_hleft.png";
	this.trackStartLength = 8;
	this.trackMiddlePath = "AppleClasses/Images/slide_track_hmid.png";
	this.trackEndPath = "AppleClasses/Images/slide_track_hright.png";
	this.trackEndLength = 8;
	this.thumbPath = "AppleClasses/Images/slide_thumb.png";
	this.thumbLength = 23;
	
	/* Internal objects */
	this._track = null;
	this._thumb = null;
	
	/* Dimensions */
	// these only need to be set during refresh()
	this._trackOffset = 0;
	this._sliderLength = 0;
	this._numScrollablePixels = 0;
	this._repeatType = "repeat-x";
	
	this._init();
}

// Inherit from AppleSlider
AppleHorizontalSlider.prototype = new AppleSlider(null);

/*********************
 * Orientation-specific functions.
 * These helper functions return vertical values.
 */
AppleHorizontalSlider.prototype._computeTrackOffset = function()
{
	// get the absolute left of the track
	var obj = this.slider;
	var curtop = 0;
	while (obj.offsetParent)
	{
		curtop += obj.offsetLeft;
		obj = obj.offsetParent;
	}
	
	return curtop;
}

AppleHorizontalSlider.prototype._computeSliderLength = function()
{
	// get the current actual slider length
	var style = document.defaultView.getComputedStyle(this.slider, '');
	return style ? parseInt(style.getPropertyValue("width"), 10) : 0;
}

AppleHorizontalSlider.prototype._setObjectSize = function(object, size)
{ object.style.height = size + "px"; }

AppleHorizontalSlider.prototype._setObjectLength = function(object, length)
{ object.style.width = length + "px"; }

AppleHorizontalSlider.prototype._setObjectStart = function(object, start)
{ object.style.left = start + "px"; }

AppleHorizontalSlider.prototype._setObjectEnd = function(object, end)
{ object.style.right = end + "px"; }

AppleHorizontalSlider.prototype._getMousePosition = function(event)
{
	if (event != undefined)
		return event.pageX;
	else
		return 0;
}

AppleHorizontalSlider.prototype._getThumbStartPos = function()
{
	return parseInt(document.defaultView.getComputedStyle(this._thumb, '').getPropertyValue("left"), 10);
}


/*******************************************************************************
* AppleVerticalSlider
* Implementation of AppleSlider
*
*
*/

function AppleVerticalSlider(slider, onchanged)
{
	/* Objects */
	this.slider = slider;
	
	/* public properties */
	// These are read-write. Set them as needed.
	this.onchanged = onchanged;
	this.continuous = true; // Fire onchanged live, as opposed to onmouseup
	this.padding = 0;
	
	// These are read-only. Use the setter functions to set them.
	this.value = 0.0;
	this.size = 22;
	this.trackStartPath = "AppleClasses/Images/slide_track_vtop.png";
	this.trackStartLength = 8;
	this.trackMiddlePath = "AppleClasses/Images/slide_track_vmid.png";
	this.trackEndPath = "AppleClasses/Images/slide_track_vbottom.png";
	this.trackEndLength = 8;
	this.thumbPath = "AppleClasses/Images/slide_thumb.png";
	this.thumbLength = 23;
	
	/* Internal objects */
	this._track = null;
	this._thumb = null;
	
	/* Dimensions */
	// these only need to be set during refresh()
	this._trackOffset = 0;
	this._sliderLength = 0;
	this._numScrollablePixels = 0;
	this._repeatType = "repeat-y";
	
	this._init();
}

// Inherit from AppleSlider
AppleVerticalSlider.prototype = new AppleSlider(null);

/*********************
* Orientation-specific functions.
* These helper functions return vertical values.
*/
AppleVerticalSlider.prototype._computeTrackOffset = function()
{
	// get the absolute top of the track
	var obj = this.slider;
	var curtop = 0;
	while (obj.offsetParent)
	{
		curtop += obj.offsetTop;
		obj = obj.offsetParent;
	}
	
	return curtop;
}

AppleVerticalSlider.prototype._computeSliderLength = function()
{
	// get the current actual slider length
	var style = document.defaultView.getComputedStyle(this.slider, '');
	return style ? parseInt(style.getPropertyValue("height"), 10) : 0;
}

AppleVerticalSlider.prototype._setObjectSize = function(object, size)
{ object.style.width = size + "px"; }

AppleVerticalSlider.prototype._setObjectLength = function(object, length)
{ object.style.height = length + "px"; }

AppleVerticalSlider.prototype._setObjectStart = function(object, start)
{ object.style.top = start + "px"; }

AppleVerticalSlider.prototype._setObjectEnd = function(object, end)
{ object.style.bottom = end + "px"; }

AppleVerticalSlider.prototype._getMousePosition = function(event)
{
	if (event != undefined)
		return event.pageY;
	else
		return 0;
}

AppleVerticalSlider.prototype._getThumbStartPos = function()
{
	return parseInt(document.defaultView.getComputedStyle(this._thumb, '').getPropertyValue("top"), 10);
}

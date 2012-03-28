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

function AppleButton(button, text, height,
					 imgLeft, imgLeftClicked, imgLeftWidth,
					 imgMiddle, imgMiddleClicked,
					 imgRight, imgRightClicked, imgRightWidth,
					 onclick)
{	
	if (button == null)
		return;
	
	/* Objects */
	this.textElement = null;
	
	/* Read-write properties */
	this.onclick = onclick;
	
	/* Read-only properties */
	this.enabled = true;
	
	
	this._init(button, text, height, 
			   imgLeft, imgLeftClicked, imgLeftWidth,
			   imgMiddle, imgMiddleClicked,
			   imgRight, imgRightClicked, imgRightWidth);
	
	this.textElement.innerHTML = text;
}

AppleButton.prototype._init = function(button, text, height,
									   imgLeft, imgLeftClicked, imgLeftWidth,
									   imgMiddle, imgMiddleClicked,
									   imgRight, imgRightClicked, imgRightWidth)
{
	this._imgLeftPath = imgLeft;
	this._imgLeftClickedPath = imgLeftClicked;
	this._imgMiddlePath = imgMiddle;
	this._imgMiddleClickedPath = imgMiddleClicked;
	this._imgRightPath = imgRight;
	this._imgRightClickedPath = imgRightClicked;
	
	var container = document.createElement("div");
	this._container = container;

	button.appendChild(container);

	// For JavaScript event handlers
	var _self = this;
	this._mousedownHandler = function(event) { _self._mousedown(event); }
	this._mousemoveHandler = function(event)
	{
		event.stopPropagation();
		event.preventDefault();
	}
	this._mouseoverHandler = function(event) { _self._mouseover(event); }
	this._mouseoutHandler = function(event) { _self._mouseout(event); }
	this._mouseupHandler = function(event) { _self._mouseup(event); }
	
	// Create the inner elements	
	var element = document.createElement("div");
	var style = element.style;
	style.display = "inline-block";
	style.verticalAlign = "bottom";
	style.background = "url(" + this._imgLeftPath + ") no-repeat top left";
	style.height = height + "px";
	style.width = imgLeftWidth + "px";
	container.appendChild(element);
	
	element = document.createElement("div");
	element.innerText = text;
	style = element.style;
	style.display = "inline-block";
	style.verticalAlign = "bottom";
	style.backgroundRepeat = "repeat-x";
	style.backgroundImage = "url(" + this._imgMiddlePath + ")";
	style.lineHeight = height + "px";
	style.height = height + "px";
	style.overflow = "hidden";
	style.whiteSpace = "nowrap";
	
	container.appendChild(element);
	this.textElement = element;
	
	element = document.createElement("div");
	style = element.style;
	style.display = "inline-block";
	style.verticalAlign = "bottom";
	style.background = "url(" + this._imgRightPath + ") no-repeat top left";
	style.height = height + "px";
	style.width = imgRightWidth + "px";
	container.appendChild(element);
	style = container.style;
	style.appleDashboardRegion = "dashboard-region(control rectangle)";
	style.height = height + "px";
	
	// preload clicked images
	var img = new Image(imgLeftWidth, height);
	img.src = this._imgLeftClickedPath;
	img = new Image();
	img.src = this._imgMiddleClickedPath;
	img = new Image(imgRightWidth, height);
	img.src = this._imgRightClickedPath;
	
	container.addEventListener("mousedown", this._mousedownHandler, true);
}

AppleButton.prototype.remove = function()
{	
	var parent = this._container.parentNode;
	parent.removeChild(this._container);
}

AppleButton.prototype.setDisabledImages = function(imgLeftDisabled, imgMiddleDisabled, imgRightDisabled)
{
	this._imgLeftDisabledPath = imgLeftDisabled;
	this._imgMiddleDisabledPath = imgMiddleDisabled;
	this._imgRightDisabledPath = imgRightDisabled;
}

AppleButton.prototype.setEnabled = function(enabled)
{
	this.enabled = enabled;
	if (enabled)
	{
		this._container.children[0].style.backgroundImage = "url(" + this._imgLeftPath + ")";
		this._container.children[1].style.backgroundImage = "url(" + this._imgMiddlePath + ")";
		this._container.children[2].style.backgroundImage = "url(" + this._imgRightPath + ")";
		this._container.style.appleDashboardRegion = "dashboard-region(control rectangle)";
	}
	else if (this._imgLeftDisabledPath !== undefined)
	{
		this._container.children[0].style.backgroundImage = "url(" + this._imgLeftDisabledPath + ")";
		this._container.children[1].style.backgroundImage = "url(" + this._imgMiddleDisabledPath + ")";
		this._container.children[2].style.backgroundImage = "url(" + this._imgRightDisabledPath + ")";
		this._container.style.appleDashboardRegion = "none";
	}
}


/*********************
* Private handlers
*/

AppleButton.prototype._setPressed = function(pressed)
{
	if (pressed)
	{
		this._container.children[0].style.backgroundImage = "url(" + this._imgLeftClickedPath + ")";
		this._container.children[1].style.backgroundImage = "url(" + this._imgMiddleClickedPath + ")";
		this._container.children[2].style.backgroundImage = "url(" + this._imgRightClickedPath + ")";
	}
	else
	{
		this._container.children[0].style.backgroundImage = "url(" + this._imgLeftPath + ")";
		this._container.children[1].style.backgroundImage = "url(" + this._imgMiddlePath + ")";
		this._container.children[2].style.backgroundImage = "url(" + this._imgRightPath + ")";
	}
}

AppleButton.prototype._mousedown = function(event)
{
	// If we're disabled, don't do anything
	if (!this.enabled)
	{
		event.stopPropagation();
		event.preventDefault();
		return;
	}
	
	// Change images to clicked state
	this._setPressed(true);
	
	// add temp event listeners
	document.addEventListener("mousemove", this._mousemoveHandler, true);
	document.addEventListener("mouseup", this._mouseupHandler, true);
	this._container.addEventListener("mouseover", this._mouseoverHandler, true);
	this._container.addEventListener("mouseout", this._mouseoutHandler, true);
	
	this._inside = true;
	
	event.stopPropagation();
	event.preventDefault();
}

AppleButton.prototype._mouseover = function(event)
{
	// Change images to clicked state
	this._setPressed(true);
	
	this._inside = true;
	
	event.stopPropagation();
	event.preventDefault();		
}

AppleButton.prototype._mouseout = function(event)
{
	// Change images to regular state
	this._setPressed(false);
	
	this._inside = false;
	
	event.stopPropagation();
	event.preventDefault();	
}

AppleButton.prototype._mouseup = function(event)
{
	// Change images to regular state
	this._setPressed(false);
	
	// Remove temp event listeners
	document.removeEventListener("mousemove", this._mousemoveHandler, true);
	document.removeEventListener("mouseup", this._mouseupHandler, true);
	this._container.removeEventListener("mouseover", this._mouseoverHandler, true);
	this._container.removeEventListener("mouseout", this._mouseoutHandler, true);
	
	// Perform callback if we're inside the button
	try {
		if (this._inside && this.onclick != null)
			this.onclick(event);
	} catch(ex) {
		throw ex;
	} finally {
		event.stopPropagation();
		event.preventDefault();
		delete this._inside;
	}
}

//
// AppleGlassButton class
//

function AppleGlassButton(button, text, onclick)
{
	/* Objects */
	this.textElement = null;
	
	/* Read-write properties */
	this.onclick = onclick;
	
	/* Read-only properties */
	this.enabled = true;
	
	this._init(button, text, 23,
			   "file:///System/Library/WidgetResources/button/glassbuttonleft.png",
			   "file:///System/Library/WidgetResources/button/glassbuttonleftclicked.png",
			   10,
			   "file:///System/Library/WidgetResources/button/glassbuttonmiddle.png",
			   "file:///System/Library/WidgetResources/button/glassbuttonmiddleclicked.png",
			   "file:///System/Library/WidgetResources/button/glassbuttonright.png",
			   "file:///System/Library/WidgetResources/button/glassbuttonrightclicked.png",
			   10);
	
	var style = this.textElement.style;
	style.fontSize = "12px";
	style.fontFamily = "Helvetica Neue";
	style.color = "white";
	style.fontWeight = "bold";
}

// Inherit from AppleButton
AppleGlassButton.prototype = new AppleButton(null);

// Override regular disabled functionality
AppleGlassButton.prototype.setEnabled = function(enabled)
{
	this.enabled = enabled;
	if (enabled)
	{
		this._container.children[1].style.color = "white";
		this._container.style.appleDashboardRegion = "dashboard-region(control rectangle)";
	}
	else
	{
		this._container.children[1].style.color = "rgb(150,150,150)";
		this._container.style.appleDashboardRegion = "none";
	}
}

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

function AppleInfoButton(flipper, front, foregroundStyle, backgroundStyle, onclick)
{
	/* Read-write properties */
    this.onclick = onclick;
    
    /* Internal */
	this._front = front;
	this._flipper = flipper;
	this._flipLabel = document.createElement("img");
	this._flipLabel.width = 13;
	this._flipLabel.height = 13;
	this._flipLabel.setAttribute("alt", "Info");
	this._flipCircle = document.createElement("div");
	flipper.appendChild(this._flipCircle);
	flipper.appendChild(this._flipLabel);
    this._labelshown = false;
		
	// For JavaScript event handlers
	var _self = this;
	
	this._updateOpacity = function(animation, now, first, done)
	{
		_self._flipLabel.style.opacity = now;
	}
	
	this._animationComplete = function()
	{
		delete _self._animation;
		delete _self._animator;
	}
	
	this._frontMove = function(event)
	{
		if (_self._outdelay !== undefined)
		{
			clearTimeout(_self._outdelay);
			delete _self._outdelay;
		}
		if (_self._labelshown)
			return;
		
		var from = 0.0;
		var duration = 500;
		if (_self._animation !== undefined)
		{
			from = _self._animation.now;
			duration = (new Date).getTime() - _self._animator.startTime;
			_self._animator.stop();
		}
		
		_self._labelshown = true;
		
		var animator = new AppleAnimator(duration, 13);
		animator.oncomplete = _self._animationComplete;
		_self._animator = animator;
		
		_self._animation = new AppleAnimation(from, 1.0, _self._updateOpacity);
		animator.addAnimation(_self._animation);
		animator.start();
	}
	
	this._frontOutDelay = function(event)
	{
		if (_self._outdelay === undefined)
		{
			_self._outdelay = setTimeout(_self._frontOut, 0, _self);
		}
	}
	
	this._frontOut = function(_self)
	{
		if (_self._outdelay !== undefined)
		{
			clearTimeout(_self._outdelay);
			delete _self._outdelay;
		}
		if (!_self._labelshown)
			return;
		
		var from = 1.0;
		var duration = 500;
		if (_self._animation !== undefined)
		{
			from = _self._animation.now;
			duration = (new Date).getTime() - _self._animator.startTime;
			_self._animator.stop();
		}
		
		var animator = new AppleAnimator(duration, 13);
		animator.oncomplete = _self._animationComplete;
		_self._animator = animator;
		
		_self._animation = new AppleAnimation(from, 0.0, _self._updateOpacity);
		animator.addAnimation(_self._animation);
		animator.start();
	
		_self._labelshown = false;
	}
	
	this._labelOver = function(event)
	{
		_self._tempMouseOver = true; // remove later
		_self._flipCircle.style.visibility = "visible";
	}
	
	this._labelOut = function(event)
	{
		delete _self._tempMouseOver; // remove later
		_self._flipCircle.style.visibility = "hidden";
	}
	
	this._labelClicked = function(event)
	{		
		_self._flipCircle.style.visibility = "hidden";
		
		try {
			if (_self.onclick != null)
				_self.onclick(event);
		} catch(ex) {
			throw ex;
		} finally {
			event.stopPropagation();
    	    event.preventDefault();
    	}
	}
	
	this._tempLabelDown = function(event)
	{
		document.addEventListener("mouseup", _self._tempDocUp, true);
		event.stopPropagation();
		event.preventDefault();
	}
	
	this._tempDocUp = function(event)
	{
		document.removeEventListener("mouseup", _self._tempDocUp, true);
		
		// if we're over the label
		if (_self._tempMouseOver !== undefined)
		{
			delete _self._tempMouseOver;
			_self._labelClicked(event);
		}
	}

	// Set up style
	var style = this._flipLabel.style;
	style.position = "absolute";
	style.top = 0;
	style.left = 0;
	style.opacity = 0;
	
	style = this._flipCircle.style;
	style.position = "absolute";
	style.top = 0;
	style.left = 0;
	style.width = "13px";
	style.height = "13px";
	this.setCircleOpacity(0.25);
	style.visibility = "hidden";
	
	this.setStyle(foregroundStyle, backgroundStyle);
	
	this._front.addEventListener("mousemove", this._frontMove, true);
	this._front.addEventListener("mouseout", this._frontOutDelay, true);

// temp stuff
	this._flipper.addEventListener("mousedown", this._tempLabelDown, true);
	this._flipper.setAttribute("onclick", "event.stopPropagation(); event.preventDefault();");
// switch to this later
//	this._flipper.addEventListener("click", this._labelClicked, true);
	this._flipper.addEventListener("mouseover", this._labelOver, true);
	this._flipper.addEventListener("mouseout", this._labelOut, true);
}

AppleInfoButton.prototype.remove = function()
{
	this._front.removeEventListener("mousemove", this._frontMove, true);
	this._front.removeEventListener("mouseout", this._frontOutDelay, true);

	this._flipper.removeEventListener("mousedown", this._tempLabelDown, true);
//	this._flipper.removeEventListener("click", this._labelClicked, true);
	this._flipper.removeEventListener("mouseover", this._labelOver, true);
	this._flipper.removeEventListener("mouseout", this._labelOut, true);
	
	var parent = this._flipLabel.parentNode;
	parent.removeChild(this._flipCircle);
	parent.removeChild(this._flipLabel);
}

AppleInfoButton.prototype.setStyle = function(foregroundStyle, backgroundStyle)
{
	this._flipLabel.src = "file:///System/Library/WidgetResources/ibutton/" + foregroundStyle + "_i.png";
	this._flipCircle.style.background = "url(file:///System/Library/WidgetResources/ibutton/" + backgroundStyle + "_rollie.png) no-repeat top left";
}

AppleInfoButton.prototype.setCircleOpacity = function(opacity)
{
	this._flipCircle.style.opacity = opacity;
}

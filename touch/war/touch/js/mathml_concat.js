/*Lib js */
var Prototype = {
    __name__: "Prototype",
    __init__: function (spec) {
	if (spec) {
	    for (var prop in spec) {
		if (spec.hasOwnProperty(prop)) {
		    this[prop] = spec[prop];
		}
	    }
	}
	return this;
    },
    specialise: function (spec) {
	var name = spec && spec.__name__ || this.__name__;
	var F;
	eval("F = function " + name + " () {};");
	F.prototype = this;
	var obj = new F();
	if (!obj.hasOwnProperty("__proto__")) {
	    obj.__proto__ = this;
	}
	if (spec) {
	    for (var prop in spec) {
		if (spec.hasOwnProperty(prop)) {
		    // Comment the following in order to be compatible with IE8
		    // This means no more descriptors :(
		    /*var desc = Object.getOwnPropertyDescriptor(spec, prop);
		    var g = desc.get;
		    var s = desc.set;
		    if ( g || s ) {
			var def = {};
			if ( g )
			    def.get = g;
			if ( s )
			    def.set = s;
			Object.defineProperty(obj, prop, def);
		    } else {*/
			obj[prop] = spec[prop];
		    //}
		}
	    }
	}
	return obj;
    },
    instanciate: function () {
	var obj = this.specialise();
	obj.__init__.apply(obj, arguments);
	return obj;
    },
    bind: function (prop) {
	return this[prop].bind(this);
    },
    addMixin: function (mixin) {
	for (var prop in mixin) {
	    if (mixin.hasOwnProperty(prop)) {
		if (this[prop]) {
		    throw "object has property '" + prop + "' already"; 
		}
		this[prop] = mixin[prop];
	    }
	}
	return this;
    }
};

var getEventCoords = function (e, element) {
    var offset = $(element).offset();
    return {
	x: e.pageX - offset.left,
	y: e.pageY - offset.top
    };
};

var PlatformInfo = {
    __init__: function () {
	var platform = navigator.platform;
	if (platform.indexOf("Mac") !== -1) {
	    this.os = "macos";
	} else if (platform.indexOf("Win") !== -1) {
	    this.os = "windows";
	} else if (platform.indexOf("Linux") !== -1) {
	    this.os = "linux";
	} else {
	    this.os = "unknown";
	}
    }
};
PlatformInfo = Prototype.specialise(PlatformInfo);
var platformInfo = PlatformInfo.instanciate();

//
// Shortcuts manager.  All shortcuts must be registered with this.
//

var KeyboardShortcuts = {
    __init__: function () {
	this.shortcuts = {};
	this.table = this.osTable[platformInfo.os];
	if (!this.table) {
	    this.table = this.osTable.windows;
	}
    },
    osTable: {
	windows: {
	    A: { txt: "Alt+", cmd: "A", name: "alt" },
	    C: { txt: "Ctrl+", cmd: "C", name: "control" },
	    M: { txt: "Meta+", cmd: "M", name: "meta" },
	    S: { txt: "Shift+", cmd: "S", name: "shift" }
	},
	macos: {
	    A: { txt: "&#x2325;", cmd: "A", name: "option" },
	    C: { txt: "^", cmd: "M", name: "control" },
	    M: { txt: "&#x2318;", cmd: "C", name: "command" },
	    S: { txt: "&#x21E7;", cmd: "S", name: "shift" }
	},
	linux: {
	    A: { txt: "Alt+", cmd: "A", name: "alt" },
	    C: { txt: "Ctrl+", cmd: "C", name: "control" },
	    M: { txt: "Meta+", cmd: "M", name: "meta" },
	    S: { txt: "Shift+", cmd: "S", name: "shift" }
	}
    },
    add: function (shortcut, action) {
	var self = this;
	var keycuts;
	var parts = shortcut.split('-');
	var key = parts[1].toUpperCase();
	var keyCode;
	var modifiers = parts[0];
	if (key.length > 1) {
	    keyCode = parseInt(key);
	} else {
	    keyCode = key.charCodeAt(0);
	}
	keycuts = this.shortcuts[keyCode];
	if (!keycuts) {
	    this.shortcuts[keyCode] = keycuts = {};
	}
	modifiers = modifiers.toUpperCase();
	var modsText = '';
	var osMods = '';
	'CASM'.split('').forEach(function (mod) {
	    if (modifiers.indexOf(self.table[mod].cmd) !== -1) {
		modsText += self.table[mod].txt;
		osMods += mod;
	    }
	});
	keycuts[osMods] = action;
	return { mods: modsText, key: key };
    },
    callFromEvent: function (e) {
	var keycuts = this.shortcuts[e.keyCode];
	if (!keycuts) {
	    return false;
	}
	var modifiers = (e.ctrlKey ? 'C': '') + (e.altKey ? 'A': '') +
	    (e.shiftKey ? 'S': '') + (e.originalEvent.metaKey ? 'M' : '');
	var action = keycuts[modifiers];
	if (action) {
	    action(e);
	    return true;
	}
	return false;
    }
};
KeyboardShortcuts = Prototype.specialise(KeyboardShortcuts);

Object.forEachItem = function (obj, f) {
    var key;
    for (key in obj) {
	if (obj.hasOwnProperty(key)) {
	    if (f(key, obj[key])) {
		return false;
	    }
	}
    }
    return true;
};

// Taken from jquery.ui.core.js
var KEY = {
    ALT: 18,
    BACKSPACE: 8,
    CAPS_LOCK: 20,
    COMMA: 188,
    COMMAND: 91,
    COMMAND_LEFT: 91, // COMMAND
    COMMAND_RIGHT: 93,
    CONTROL: 17,
    DELETE: 46,
    DOWN: 40,
    END: 35,
    ENTER: 13,
    ESCAPE: 27,
    HOME: 36,
    INSERT: 45,
    LEFT: 37,
    MENU: 93, // COMMAND_RIGHT
    NUMPAD_ADD: 107,
    NUMPAD_DECIMAL: 110,
    NUMPAD_DIVIDE: 111,
    NUMPAD_ENTER: 108,
    NUMPAD_MULTIPLY: 106,
    NUMPAD_SUBTRACT: 109,
    PAGE_DOWN: 34,
    PAGE_UP: 33,
    PERIOD: 190,
    RIGHT: 39,
    SHIFT: 16,
    SPACE: 32,
    TAB: 9,
    UP: 38,
    WINDOWS: 91 // COMMAND
};

/* Box.js */

if (window.cvm === undefined) {
    cvm = {};
}

(function (cvm) {

var calibrationImg;
var calibrationTxt;

var initBox = function () {
    if (calibrationImg && calibrationTxt) {
	return;
    }
    var div = document.createElement("div");
    div.style.position = "absolute";
    div.style.visibility = "hidden";
    calibrationImg = document.createElement("img");
    //dirty hack, but no other idea :-)
    calibrationImg.src = window.ggw.getGWTModuleBaseURL()+"images/10x1.png";
    calibrationTxt = document.createElement("span");
    div.appendChild(calibrationImg);
    div.appendChild(calibrationTxt);
    document.body.appendChild(div);
};

var getTextMetrics = function (text, font) {
    calibrationTxt.innerHTML = "";
    calibrationTxt.style.font = font;
    calibrationTxt.appendChild(document.createTextNode(text));
    var baselineFromTop = calibrationImg.offsetTop - calibrationTxt.offsetTop;
    var height = calibrationTxt.offsetHeight;
    var data = {
	text: text,
	font: font,
	width: calibrationTxt.offsetWidth,
	height: height,
	ascent: baselineFromTop,
	descent: baselineFromTop - height
    };
    return data;
};

var Box = {
    __name__: "Box",
    alignAdjustment: function (align) {
	switch(align || "left") {
	    case "left":
		return 0;
	    case "right":
		return this.width;
	    case "center":
		return 0.5*this.width;
	    default:
		throw "Illegal align adjustment:" + align;
	}
    },
    alignOnCanvas: function (ctx, x, y, align) {
	this.drawOnCanvas(ctx, x - this.alignAdjustment(align), y);
    },
    contains: function (x, y) {
	return (0 <= x && x < this.width 
		&& -this.descent >= y && y > -this.ascent);
    },
    getContainers: function (x, y) {
	var containers = [];
	this.pushContainers(containers, x, y);
	return containers;
    },
    pushContainers: function (containers, x, y, align) {
	x += this.alignAdjustment(align);
	if (this.contains(x, y)) {
	    this.pushSubContainers(containers, x, y);
	    containers.push({box: this, x: x, y: y});
	}
    },
    pushSubContainers: function () {
    },
    bindLayout: function (layout, key) {
	if (!this.boundLayouts) {
	    this.boundLayouts = [];
	}
	this.boundLayouts.push({layout: layout, key: key});
    },
    setStack: function (stack) {
    }
};
Box = Prototype.specialise(Box);

var TextBox = {
    __name__: "TextBox",
    __init__: function (text, font) {
	this.text = text;
	this.font = font;
	this.calculate();
    },
    calculate: function () {
	var m = getTextMetrics(this.text, this.font);
	this.width = m.width;
	this.height = m.height;
	this.ascent = m.ascent;
	this.descent = m.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.font = this.font;
	// ctx.fillStyle = "black";
	ctx.fillText(this.text, x, y);
	ctx.restore();
    },
    getCharacterIndexAt: function (ctx, x) {
	var i, m;
	ctx.save();
	ctx.font = this.font;
	for (i = 0; i < this.text.length; i++) {
	    m = ctx.measureText(this.text.substr(0, i + 1));
	    if (x < m) {
		break;
	    }
	}
	return i;
    }
};
TextBox = Box.specialise(TextBox);

var Decoration = {
    __name__: "Decoration",
    __init__: function (box, hOffset, vOffset) {
	this.box = box;
        this.vOffset = vOffset;
        this.hOffset = hOffset;
	this.calculate();
    },
    calculate: function () {
        var box = this.box;
	this.top = this.vOffset + box.ascent;
        this.bottom = this.vOffset + box.descent;
        this.left = this.hOffset;
        this.right = this.hOffset + box.width;
    },
    drawOnCanvas: function (ctx, x, y) {
	this.box.drawOnCanvas(ctx, x + this.hOffset, y - this.vOffset);
    },
    pushContainers: function (containers, x, y) {
	this.box.pushContainers(containers,
	                        x - this.hOffset, y + this.vOffset);
    }
};
Decoration = Prototype.specialise(Decoration);

var DecoratedBox = {
    __name__: "DecoratedBox",
    __init__: function (box, decorations) {
        this.box = box;
	this.decorations = decorations || [];
        this.calculate();
    },
    calculate: function () {
        var box = this.box;
        var ascent = box.ascent;
        var descent = box.descent;
        var left = 0;
        var right = box.width;
        this.decorations.forEach(function (dec) {
	    if (dec.top > ascent) {
                ascent = dec.top;
            }
	    if (dec.bottom < descent) {
                descent = dec.bottom;
            }
            if (dec.left < left) {
                left = dec.left;
            }
            if (dec.right > right) {
                right = dec.right;
	    }
	});
	this.ascent = ascent;
	this.descent = descent;
        this.width = right - left;
	this.hOffset = left;
        this.height = this.ascent - this.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
        x += this.hOffset;
	this.box.drawOnCanvas(ctx, x, y);
	this.decorations.forEach(function (dec) {
	    dec.drawOnCanvas(ctx, x, y);
	});
    },
    pushSubContainers: function (containers, x, y) {
	x -= this.hOffset;
	this.box.pushContainers(containers, x, y);
	this.decorations.forEach(function (dec) {
	    dec.pushContainers(containers, x, y);
	});
    }
};
DecoratedBox = Box.specialise(DecoratedBox);

var Scale = {
    __name__: "Scale",
    __init__: function (box, scale) {
	this.box = box;
	this.scale = scale;
	this.calculate();
    },
    calculate: function () {
	var self = this;
	var box = this.box;
	var scale = this.scale;
	["width", "height", "ascent", "descent"].forEach(function (prop) {
	    self[prop] = scale * box[prop];
	});
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.translate(x, y);
	ctx.scale(this.scale, this.scale);
	this.box.drawOnCanvas(ctx, 0, 0);
	ctx.restore();
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x/this.scale, y/this.scale);
    }
};
Scale = Box.specialise(Scale);

var Train = {
    __name__: "Train",
    __init__: function () {
	var boxes = arguments;
        if (boxes.length === 1) {
	    boxes = boxes[0];
	} else {
	    var filter = Array.prototype.filter;
	    boxes = filter.call(boxes, function () { return true; });
	}
	this.boxes = boxes;
	this.calculate();
    },
    calculate: function () {
	var ascent = -1000;
	var descent = 1000;
	var width = 0;
	this.boxes.forEach(function (box) {
	    width += box.width;
	    if (ascent < box.ascent) {
		ascent = box.ascent;
	    }
	    if (descent > box.descent) {
		descent = box.descent;
	    }
	});
	this.ascent = ascent;
	this.descent = descent;
	this.height = ascent - descent;
	this.width = width;
    },
    drawOnCanvas: function (ctx, x, y) {
	this.boxes.forEach(function (box) {
	    box.drawOnCanvas(ctx, x, y);
	    x += box.width;
	});
    },
    pushSubContainers: function (containers, x, y) {
	this.boxes.forEach(function (box) {
	    box.pushContainers(containers, x, y);
	    x -= box.width;
	});
    }
};
Train = Box.specialise(Train);

var Paren = {
    __name__: "Paren",
    __init__: function (box, reflect) {
	this.box = box;
	this.reflect = reflect;
	this.calculate();
    },
    calculate: function () {
	var box = this.box;
	this.height = box.height;
	this.ascent = box.ascent;
	this.descent = box.descent;
	this.width = 3;
	this.left = this.reflect ? 2 : 0;
	this.right = this.reflect ? -3 : 3;
	this.r = 5;
    },
    drawOnCanvas: function (ctx, x, y) {
	var r = this.r;
	ctx.save();
	ctx.translate(x, y);
	ctx.beginPath();
	ctx.moveTo(this.right, -this.ascent);
	ctx.arcTo(this.left, -this.ascent, this.left, r - this.ascent, r);
	ctx.lineTo(this.left, -r - this.descent);
	ctx.arcTo(this.left, -this.descent, this.right, -this.descent, r);
	ctx.stroke();
	ctx.restore();
    }
};
Paren = Box.specialise(Paren);

var Paren2 = {
    __name__: "Paren2",
    __init__: function (box, reflect) {
	this.box = box;
	this.reflect = reflect;
	this.calculate();
    },
    calculate: function () {
	var box = this.box;
	this.height = box.height - 4;
	this.ascent = box.ascent - 2;
	this.descent = box.descent + 2;
	this.width = 6;
	this.bracketWidth = 1.5;
	this.left = this.reflect ? this.width : 0;
	this.right = this.reflect ? 0 : this.width;
	this.middle = this.reflect ? this.width - this.bracketWidth : this.bracketWidth;
	this.curveHeight = 10;
    },
    drawOnCanvas: function (ctx, x, y) {
	var r = this.r;
	ctx.save();
	ctx.translate(x, y);
	ctx.beginPath();
	ctx.moveTo(this.right, -this.ascent);
	ctx.bezierCurveTo(
	    this.right, -this.ascent,
	    this.left, -this.ascent,
	    this.left, -this.ascent + this.curveHeight
	);
	ctx.lineTo(this.left, -this.descent - this.curveHeight);
	ctx.bezierCurveTo(
	    this.left, -this.descent,
	    this.right, -this.descent,
	    this.right, -this.descent
	);
	ctx.bezierCurveTo(
	    this.right, -this.descent,
	    this.middle, -this.descent,
	    this.middle, -this.descent - this.curveHeight
	);
	ctx.lineTo(this.middle, -this.ascent + this.curveHeight);
	ctx.bezierCurveTo(
	    this.middle, -this.ascent,
	    this.right, -this.ascent,
	    this.right, -this.ascent
	);
	ctx.closePath();
	ctx.fill();
	ctx.restore();
    }
};
Paren2 = Box.specialise(Paren2);

var CurlyBracket = {
    __name__: "CurlyBracket",
    __init__: function (box, reflect) {
	this.box = box;
	this.reflect = reflect;
	this.calculate();
    },
    calculate: function () {
	var box = this.box;
	this.height = box.height - 4;
	this.ascent = box.ascent - 2;
	this.descent = box.descent + 2;
	this.width = 12;
	this.bracketWidth = 1.5;
    },
    drawOnCanvas: function (ctx, x, y) {
	var middle = this.width / 2;
	var midleft = middle - this.bracketWidth / 2;
	var midright = middle + this.bracketWidth / 2;
	var left = 0;
	var right = this.width;
	if (this.reflect) {
	    var tmp = midright;
	    midright = midleft;
	    midleft = tmp;
	    tmp = right;
	    right = left;
	    left = tmp;
	}
	var midy = -0.5*(this.ascent + this.descent);
	var top = -this.ascent;
	var bottom = -this.descent;
	var midtop = top + this.height/4;
	var midbottom = bottom - this.height/4;
	ctx.save();
	ctx.translate(x, y);
	ctx.beginPath();
	ctx.moveTo(right, top);
	ctx.bezierCurveTo(
	    midleft, top,
	    midleft, top,
	    midleft, midtop
	);
	ctx.bezierCurveTo(
	    midleft, midy,
	    midleft, midy,
	    left, midy
	);
	ctx.bezierCurveTo(
	    midleft, midy,
	    midleft, midy,
	    midleft, midbottom
	);
	ctx.bezierCurveTo(
	    midleft, bottom,
	    midleft, bottom,
	    right, bottom
	);
	ctx.bezierCurveTo(
	    midright, bottom,
	    midright, bottom,
	    midright, midbottom
	);
	ctx.bezierCurveTo(
	    midright, midy,
	    midright, midy,
	    left, midy
	);
	ctx.bezierCurveTo(
	    midright, midy,
	    midright, midy,
	    midright, midtop
	);
	ctx.bezierCurveTo(
	    midright, top,
	    midright, top,
	    right, top
	);
	ctx.closePath();
	ctx.fill();
	//ctx.stroke();
	ctx.restore();
    }
};
CurlyBracket = Box.specialise(CurlyBracket);

var ElasticVBar = {
    __name__: "Paren",
    __init__: function (box, top, bottom, reflect) {
	this.box = box;
	this.top = top;
	this.bottom = bottom;
	this.reflect = reflect;
	this.calculate();
    },
    calculate: function () {
	var box = this.box;
	this.height = box.height;
	this.ascent = box.ascent;
	this.descent = box.descent;
	this.left = 0;
	this.right = this.reflect ? -4 : 4;
	this.width = 1;
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.translate(x, y);
	ctx.beginPath();
	if (this.top) {
	    ctx.moveTo(this.left, -this.ascent);
	    ctx.lineTo(this.right, -this.ascent);
	}
	if (this.bottom) {
	    ctx.moveTo(this.left, -this.descent);
	    ctx.lineTo(this.right, -this.descent);
	}
	ctx.moveTo(this.left, -this.ascent);
	ctx.lineTo(this.left, -this.descent);
	ctx.stroke();
	ctx.restore();
    }
};
ElasticVBar = Box.specialise(ElasticVBar);

var ElasticBox = {
    __name__: "ElasticBox",
    __init__: function (ref, box) {
	this.ref = ref;
	this.box = box;
	this.calculate();
    },
    calculate: function () {
	var ref = this.ref;
	this.height = ref.height;
	this.ascent = ref.ascent;
	this.descent = ref.descent;
	var scale = this.height / this.box.height;
	var scaledBox = Scale.instanciate(this.box, scale);
	var rise = scaledBox.ascent - this.ascent;
	this.raisedBox = RaiseBox.instanciate(-rise, scaledBox);
	this.width = this.raisedBox.width;
    },
    drawOnCanvas: function (ctx, x, y) {
	this.raisedBox.drawOnCanvas(ctx, x, y);
    }
};
ElasticBox = Box.specialise(ElasticBox);

var getElasticBox = function (type, box) {
    switch (type) {
    case "(":
	return Paren2.instanciate(box);
    case ")":
	return Paren2.instanciate(box, true);
    case "{":
	return CurlyBracket.instanciate(box);
    case "}":
	return CurlyBracket.instanciate(box, true);
    case "|":
	return ElasticVBar.instanciate(box);
    case "[":
	return ElasticVBar.instanciate(box, true, true);
    case "]":
	return ElasticVBar.instanciate(box, true, true, true);
    case "|+":
	return ElasticVBar.instanciate(box, true);
    case "+|":
	return ElasticVBar.instanciate(box, true, false, true);
    case "|_":
	return ElasticVBar.instanciate(box, false, true);
    case "_|":
	return ElasticVBar.instanciate(box, false, true, true);
    }
    throw "Elasctic box type '" + type + "' unknown";
};

var HSpace = {
    __name__: "HSpace",
    __init__: function (width) {
	this.width = width;
	this.calculate();
    },
    calculate: function () {
	this.height = 0;
	this.descent = 0;
	this.ascent = 0;
    },
    drawOnCanvas: function (ctx, x, y) {
    }
};
HSpace = Box.specialise(HSpace);

var VSpace = {
    __name__: "VSpace",
    __init__: function (height) {
	this.height = height;
	this.calculate();
    },
    calculate: function () {
	this.width = 0;
	this.ascent = this.height*0.5;
	this.descent = -this.ascent;
    },
    drawOnCanvas: function (ctx, x, y) {}
};
VSpace = Box.specialise(VSpace);

var Stack = {
    __name__: "Stack",
    __init__: function (boxes, baseline, align) {
	var self = this;
	this.boxes = boxes;
	this.baseline = baseline || 0;
	this.align = align || "center";
	this.calculate();
	boxes.forEach(function (box) {
	    box.setStack(self);
	});
    },
    calculate: function () {
	var height = 0;
	var descent = 0;
	var ascent = 0;
	var boxes = this.boxes;
	var width = 0;
	var baseline = this.baseline;
	var box;
	var i;
	for (i = 0; i < boxes.length; i++) {
	    box = boxes[i];
	    if (i < baseline) {
		descent -= box.height;
	    } else if (i === baseline) {
		descent += box.descent;
		ascent += box.ascent;
	    } else {
		ascent += box.height;
	    }
	    if (width < box.width) {
		width = box.width;
	    }
	}
	this.width = width;
	this.ascent = ascent;
	this.descent = descent;
	this.height = ascent - descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	var align = this.align;
	y -= this.descent;
	x += this.alignAdjustment(align);
	this.boxes.forEach(function (box) {
	    y += box.descent;
	    box.alignOnCanvas(ctx, x, y, align);
	    y -= box.ascent;
	});
    },
    pushSubContainers: function (containers, x, y) {
	var align = this.align;
	y += this.descent;
	x -= this.alignAdjustment(align);
	this.boxes.forEach(function(box) {
	    y -= box.descent;
	    box.pushContainers(containers, x, y, align);
	    y += box.ascent;
	});
    }
};
Stack = Box.specialise(Stack);

var HLine = {
    __name__: "HLine",
    __init__: function (width, height) {
	this._width = width;
        this.width = width | 0;
	this.height = height || 1;
	this.calculate();
    },
    calculate: function () {
	this.ascent = this.height;
	this.descent = 0;
    },/* Removed for compatibility with IE9
    get width() {
	return this._width || (this.stack && this.stack.width) || 0;
    },*/
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.fillRect(x, y - this.ascent, this.width, this.height);
	ctx.restore();
    },
    setStack: function (stack) {
	if (this._width === null) {
	    this.width = stack.width;
	}
    }
};
HLine = Box.specialise(HLine);
/*
// Instead of get width() for compatibility with IE9
Object.defineProperty(HLine, "width", {
    get: function () {
	return this._width || (this.stack && this.stack.width) || 0;
    }
});*/

var ColorBox = {
    __name__: "ColorBox",
    __init__: function (color, box) {
	this.color = color;
	this.box = box;
	this.calculate();
    },
    calculate: function () {
	this.width = this.box.width;
	this.height = this.box.height;
	this.ascent = this.box.ascent;
	this.descent = this.box.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.fillStyle = this.color;
	ctx.strokeStyle = this.color;
	this.box.drawOnCanvas(ctx, x, y);
	ctx.restore();
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x, y);
    }
};
ColorBox = Box.specialise(ColorBox);

var Cursor = {
    __name__: "Cursor",
    __init__: function (box) {
	this.box = box;
	this.calculate();
    },
    calculate: function () {
	this.width = this.box.width;
	this.height = this.box.height;
	this.ascent = this.box.ascent;
	this.descent = this.box.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	this.box.drawOnCanvas(ctx, x, y);
	ctx.save();
	ctx.strokeStyle = "red";
	ctx.lineWidth = 1;
	ctx.beginPath();
	ctx.moveTo(x + this.width, y - this.ascent);
	ctx.lineTo(x + this.width, y - this.descent);
	ctx.stroke();
	ctx.beginPath();
	ctx.strokeStyle = "gray";
	ctx.moveTo(x + 4, y - this.ascent);
	ctx.lineTo(x, y - this.ascent);
	ctx.lineTo(x, y - this.descent);
	ctx.lineTo(x + 4, y - this.descent);
	ctx.stroke();
	ctx.restore();
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x, y);
    }    
}
Cursor = Box.specialise(Cursor);

var Frame = {
    __name__: "Frame",
    __init__: function (style, box) {
	this.style = style || {};
	this.box = box;
	this.calculate();
    },
    calculate: function () {
	var style = this.style;
	this.width = this.box.width;
	this.height = this.box.height;
	this.ascent = this.box.ascent;
	this.descent = this.box.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	var extra = this.extra;
	// Paint background first, then framed box, then draw
	// frame
	if (this.style.background) {
	    ctx.save();
	    ctx.fillStyle = this.style.background;
	    ctx.fillRect(x, y - this.ascent, this.width, this.height);
	    ctx.restore();
	}
	this.box.drawOnCanvas(ctx, x, y);
	if (this.style.border && this.style.width) {
	    ctx.save();
	    ctx.strokeStyle = this.style.border;
	    ctx.lineWidth = this.style.width;
	    ctx.strokeRect(x, y - this.ascent, this.width, this.height);
	    ctx.restore();
	}
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x, y);
    }
};
Frame = Box.specialise(Frame);

var RootSign = {
    __name__: "RootSign",
    __init__: function (box, nth) {
	this.box = box;
	this.nth = nth;
	this.calculate();
    },
    calculate: function () {
	this.nthWidth = this.nth ? this.nth.width : 0;
	this.nthWidth = Math.max(5, this.nthWidth);
	this.width = this.box.width + this.nthWidth + 7;
	this.height = this.box.height + 3;
	this.ascent = this.box.ascent + 3;
	this.descent = this.box.descent;
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.beginPath();
	ctx.save();
	ctx.translate(x + this.nthWidth, y);
	ctx.moveTo(-5, - 5);
	//ctx.lineTo(0, -this.descent - 5);
	ctx.lineTo(0, -this.descent);
	ctx.lineTo(5, -this.ascent);
	ctx.lineTo(this.width - this.nthWidth, -this.ascent);
	ctx.stroke();
	this.box.drawOnCanvas(ctx, 7, 0);
	if (this.nth) {
	    this.nth.alignOnCanvas(ctx, 0, this.nth.descent - 2/* - 7 + this.nth.descent*/, "right");
	}
	ctx.restore();
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x - this.nthWidth - 7, y);
	if (this.nth) {
	    this.nth.pushContainers(containers, x - this.nthWidth, y + this.descent + 5 - this.nth.descent, "right");
	}
    }
};
RootSign = Box.specialise(RootSign);

var RaiseBox = {
    __name__: "RaiseBox",
    __init__: function (height, box) {
	this.raiseHeight = height;
	this.box = box;
	this.calculate();
    },
    calculate: function () {
	this.width = this.box.width;
	this.height = this.box.height;
	this.ascent = this.box.ascent + this.raiseHeight;
	this.descent = this.box.descent + this.raiseHeight;
    },
    drawOnCanvas: function (ctx, x, y) {
	ctx.save();
	ctx.translate(0, -this.raiseHeight);
	this.box.drawOnCanvas(ctx, x, y);
	ctx.restore();
    },
    pushSubContainers: function (containers, x, y) {
	this.box.pushContainers(containers, x, y + this.raiseHeight);
    }
};
RaiseBox = Box.specialise(RaiseBox);

var Table = {
    __name__: "Table",
    __init__: function (array, hspace, vspace, align) {
	this.rows = array;
	this.nrows = this.rows.length;
	this.ncols = this.rows.reduce(function (m, r) {
	    return Math.max(m, r.length);
	}, 0);
	this.hspace = hspace || 0;
	this.vspace = vspace || 0;
	this.align = align || "";
	this.calculate();
    },
    calculate: function () {
	var i, j;
	var widths = this.widths = [];
	for (i = 0; i < this.ncols; i++) {
	    widths.push(0);
	}
	var ascents = this.ascents = [];
	var descents = this.descents = [];
	for (j = 0; j < this.nrows; j++) {
	    ascents.push(-1000);
	    descents.push(1000);
	}
	var add = function (x, y) { return x + y; };
	this.rows.forEach(function (row, i) {
	    row.forEach(function (box, j) {
		if (widths[j] < box.width) {
		    widths[j] = box.width;
		}
		if (ascents[i] < box.ascent) {
		    ascents[i] = box.ascent;
		}
		if (descents[i] > box.descent) {
		    descents[i] = box.descent;
		}
	    });
	});
	this.height = 0;
	for (i = 0; i < this.nrows; i++) {
	    this.height += ascents[i] - descents[i];
	}
	this.width = widths.reduce(add, 0) + this.hspace*(this.ncols - 1);
	this.ascent = this.height/2;
	this.descent = -this.ascent;
    },
    drawOnCanvas: function (ctx, x, y) {
	var self = this;
	var dx;
	y -= this.ascent;
	this.rows.forEach(function (row, i) {
	    y += self.ascents[i];
	    dx = x;
	    row.forEach(function (box, j) {
		switch (self.align.charAt(j)) {
		case "l":
		    box.alignOnCanvas(ctx, dx, y, "left");
		    break;
		case "r":
		    box.alignOnCanvas(ctx, dx + self.widths[j], y, "right");
		case "c":
		case "":
		    box.alignOnCanvas(ctx, dx + self.widths[j]/2, y, "center");
		    break;
		default:
		    throw "Invalid align code: " + self.align.charAt(j);
		}
		dx += self.widths[j] + self.hspace;
	    });
	    y += self.vspace - self.descents[i];
	});
    },
    pushSubContainers: function (containers, x, y) {
	var self = this;
	var dx;
	y += this.ascent;
	this.rows.forEach(function (row, i) {
	    y -= self.ascents[i];
	    dx = 0;
	    row.forEach(function (box, j) {
		// XXX TODO: switch statement as above
		box.pushContainers(containers, x - dx - self.widths[j]/2, y, "center");
		dx += self.widths[j] + self.hspace;
	    });
	    y -= self.vspace - self.descents[i];
	});
    }
};
Table = Box.specialise(Table);

cvm.box = {
    Box: Box,
    TextBox: TextBox,
    Decoration: Decoration,
    DecoratedBox: DecoratedBox,
    Scale: Scale,
    Train: Train,
    Paren: Paren,
    Paren2: Paren2,
    CurlyBracket: CurlyBracket,
    ElasticVBar: ElasticVBar,
    ElasticBox: ElasticBox,
    HSpace: HSpace,
    VSpace: VSpace,
    Stack: Stack,
    HLine: HLine,
    ColorBox: ColorBox,
    Cursor: Cursor,
    Frame: Frame,
    RootSign: RootSign,
    RaiseBox: RaiseBox,
    Table: Table,
    init: initBox,
    getElasticBox: getElasticBox
};

})(cvm);

/* Layout.js */


if (window.cvm === undefined) {
    cvm = {};
}

(function (cvm) {

var bx = cvm.box;

var Layout = {
    bindExpr: function (expr, key) {
	if (!this.boundExprs) {
	    this.boundExprs = [];
	}
	this.boundExprs.push({expr: expr, key: key});
    }
};
Layout = Prototype.specialise(Layout);

var LTrain = {
    __name__: "LTrain",
    __init__: function (elems) {
	this.elems = elems;
    },
    box: function () {
	var boxes = this.elems.map(function (elem) {
	    return elem.box();
	});
	var train = bx.Train.instanciate(boxes);
	train.bindLayout(this);
	return train;
    }
};
LTrain = Layout.specialise(LTrain);

var LText = {
    __name__: "LText",
    __init__: function (text, style) {
	this.text = text;
	this.style = style || {};
    },
    box: function () {
	var font = [];
	var style = this.style;
	var box;
	style.style && font.push(style.style);
	style.variant && font.push(style.variant);
	style.weight && font.push(style.weight);
	font.push(style.size || "20px");
	font.push(style.family || "serif");
	this.font = font.join(" ");
	box = bx.TextBox.instanciate(this.text, this.font);
	box.bindLayout(this);
	return box;
    }
};
LText = Layout.specialise(LText);

var LScale = {
    __name__: "LScale",
    __init__: function (elem, scale) {
	this.elem = elem;
	this.scale = scale;
    },
    box: function () {
	var box = bx.Scale.instanciate(this.elem.box(), this.scale);
	box.bindLayout(this);
	return box;
    }
};
LScale = Layout.specialise(LScale);

var LBracket = {
    __name__: "LBracket",
    __init__: function (elem, color) {
	this.elem = elem;
	this.color = color;
    },
    box: function () {
	var box = this.elem.box();
	var left = bx.Paren.instanciate(box);
	var right = bx.Paren.instanciate(box, true);
	if (this.color) {
	    left = bx.ColorBox.instanciate(this.color, left);
	    right = bx.ColorBox.instanciate(this.color, right);
	}
	var train = bx.Train.instanciate(left, box, right);
	left.bindLayout(this, "left");
	right.bindLayout(this, "right");
	train.bindLayout(this);
	return train;
    }
};
LBracket = Layout.specialise(LBracket);

var LLREnclosure = {
    __name__: "LLREnclosure",
    __init__: function (elem, left, right, color) {
	this.elem = elem;
	this.left = left;
	this.right = right;
	this.color = color;
    },
    box: function () {
	var box = bx.Stack.instanciate([
	    bx.VSpace.instanciate(2),
	    this.elem.box(),
	    bx.VSpace.instanciate(2)
	], 1);
	var left = this.left && bx.getElasticBox(this.left, box);
	var right = this.right && bx.getElasticBox(this.right, box);
	if (this.color) {
	    left = left && bx.ColorBox.instanciate(this.color, left);
	    right = right && bx.ColorBox.instanciate(this.color, right);
	}
	var boxes;
	if (left) {
	    boxes = [bx.HSpace.instanciate(2), left, bx.HSpace.instanciate(2)];
	} else {
	    boxes = [];
	}
	boxes.push(box);
	if (right) {
	    boxes.push(bx.HSpace.instanciate(2));
	    boxes.push(right);
	    boxes.push(bx.HSpace.instanciate(2));
	}
	var train = bx.Train.instanciate(boxes);
	left && left.bindLayout(this, "left");
	right && right.bindLayout(this, "right");
	train.bindLayout(this);
	return train;
    }
};
LLREnclosure = Layout.specialise(LLREnclosure);

var LSuperscript = {
    __name__: "LSuperscript",
    __init__: function (elem, superscript) {
	this.elem = elem;
	this.superscript = superscript;
    },
    box: function () {
	var box = this.elem.box();
	var supbox = this.superscript.box();
	var superscript = bx.Decoration.instanciate(supbox, box.width, box.ascent - 10 - supbox.descent);
	var decbox = bx.DecoratedBox.instanciate(box, [superscript]);
	decbox.bindLayout(this);
	return decbox;
    }
};
LSuperscript = Layout.specialise(LSuperscript);

var LSubscript = {
    __name__: "LSubscript",
    __init__: function (elem, subscript) {
	this.elem = elem;
	this.subscript = subscript;
    },
    box: function () {
	var box = this.elem.box();
	var supbox = this.subscript.box();
	var subscript = bx.Decoration.instanciate(supbox, box.width, box.descent + 10 - supbox.ascent);
	var decbox = bx.DecoratedBox.instanciate(box, [subscript]);
	decbox.bindLayout(this);
	return decbox;
    }
};
LSubscript = Layout.specialise(LSubscript);

var LTopAlign = {
    __name__: "LTopAlign",
    __init__: function (elem, superscript) {
	this.elem = elem;
	this.superscript = superscript;
    },
    box: function () {
	var box = this.elem.box();
	var supbox = this.superscript.box();
	var superscript = bx.Decoration.instanciate(supbox, box.width, box.ascent - supbox.ascent);
	var decbox = bx.DecoratedBox.instanciate(box, [superscript]);
	decbox.bindLayout(this);
	return decbox;
    }
};
LTopAlign = Layout.specialise(LTopAlign);

var LHSpace = {
    __name__: "LHSpace",
    __init__: function (width) {
	this.width = width;
    },
    box: function () {
	return bx.HSpace.instanciate(this.width);
    }
};
LHSpace = Layout.specialise(LHSpace);

var LVSpace = {
    __name__: "LVSpace",
    __init__: function (height) {
	this.height = height;
    },
    box: function () {
	return bx.VSpace.instanciate(this.height);
    }
};
LVSpace = Layout.specialise(LVSpace);

var LStack = {
    __name__: "LStack",
    __init__: function (elems, baseline) {
	this.elems = elems;
	this.baseline = baseline;
    },
    box: function () {
	var boxes = this.elems.map(function (el) { return el.box(); });
	var stack = bx.Stack.instanciate(boxes, this.baseline);
	stack.bindLayout(this);
	return stack;
    }
};
LStack = Layout.specialise(LStack);

var LHLine = {
    __name__: "LHLine",
    __init__: function (width, height) {
	this.width = width;
	this.height = height;
    },
    box: function () {
	var line = bx.HLine.instanciate(this.width, this.height);
	line.bindLayout(this);
	return line;
    }
};
LHLine = Layout.specialise(LHLine);

var LColor = {
    __name__: "LColor",
    __init__: function (color, elem) {
	this.color = color;
	this.elem = elem;
    },
    box: function () {
	var box = this.elem.box();
	var cbox = bx.ColorBox.instanciate(this.color, box);
	cbox.bindLayout(this);
	return cbox;
    }
};
LColor = Layout.specialise(LColor);

var LCursor = {
    __name__: "LCursor",
    __init__: function (elem) {
	this.elem = elem;
    },
    box: function () {
	var box = this.elem.box();
	var cbox = bx.Cursor.instanciate(box);
	cbox.bindLayout(this);
	return cbox;
    }
};
LCursor = Layout.specialise(LCursor);

var LFrame = {
    __name__: "LFrame",
    __init__: function (style, elem) {
	this.style = style;
	this.elem = elem;
    },
    box: function () {
	var box = this.elem.box();
	var fbox = bx.Frame.instanciate(this.style, box);
	fbox.bindLayout(this);
	return fbox;
    }
};
LFrame = Layout.specialise(LFrame);

var LSqrt = {
    __name__: "LSqrt",
    __init__: function (elem, nth) {
	this.elem = elem;
	this.nth = nth;
    },
    box: function () {
	var box = this.elem.box();
	var nthbox = this.nth && this.nth.box();
	var rbox = bx.RootSign.instanciate(box, nthbox);
	rbox.bindLayout(this);
	return rbox;
    }
};
LSqrt = Layout.specialise(LSqrt);

var LRaise = {
    __name__: "LRaise",
    __init__: function (height, elem) {
	this.height = height;
	this.elem = elem;
    },
    box: function () {
	var box = this.elem.box();
	var rbox = bx.RaiseBox.instanciate(this.height, box);
	rbox.bindLayout(this);
	return rbox;
    }
};
LRaise = Layout.specialise(LRaise);

var LTable = {
    __name__: "LTable",
    __init__: function (array, hspace, vspace, align) {
	this.rows = array;
	this.hspace = hspace;
	this.vspace = vspace;
	this.align = align;
    },
    box: function () {
	var brows = this.rows.map(function (row) {
	    return row.map(function (elem) {
		return elem.box();
	    });
	});
	var tbox = bx.Table.instanciate(brows, this.hspace, 
	    this.vspace, this.align);
	return tbox;
    }
};
LTable = Layout.specialise(LTable);

cvm.layout = {
    ofExpr: function (expr) {
	var l;
	if (expr.selected) {
	    expr = expr.selected;
	}
	return expr.layout(this);
    },
    select: function (l, editing) {
	if (editing) {
	    return this.cursor(l);
	    /*l = this.frame({background: "#DDDDDD"}, l);
	    return this.lrEnclosure(l, "", "|", "red");*/
	} else {
	    return this.frame({background: "#AAFFAA"}, l);
	}
    },
    train: function () {
	var elems = arguments;
        if (elems.length === 1) {
	    elems = elems[0];
	} else {
	    var filter = Array.prototype.filter;
	    elems = filter.call(elems, function () { return true; });
	}
	return LTrain.instanciate(elems);
    },
    text: function (text, style) {
	return LText.instanciate(text, style);
    },
    scale: function (elem, scale) {
	return LScale.instanciate(elem, scale);
    },
    bracket: function (elem, color) {
	return LLREnclosure.instanciate(elem, "(", ")", color);
    },
    lrEnclosure: function (elem, left, right, color) {
	return LLREnclosure.instanciate(elem, left, right, color);
    },
    superscript: function (elem, superscript) {
	return LSuperscript.instanciate(elem, superscript);
    },
    subscript: function (elem, subscript) {
	return LSubscript.instanciate(elem, subscript);
    },
    topAlign: function (elem, superscript) {
	return LTopAlign.instanciate(elem, superscript);
    },
    hspace: function (width) {
	return LHSpace.instanciate(width);
    },
    vspace: function (height) {
	return LVSpace.instanciate(height);
    },
    stack: function (elems, baseline) {
	return LStack.instanciate(elems, baseline);
    },
    hline: function (width, height) {
	return LHLine.instanciate(width, height);
    },
    color: function (color, elem) {
	return LColor.instanciate(color, elem);
    },
    cursor: function (elem) {
	return LCursor.instanciate(elem);
    },
    frame: function (style, elem) {
	return LFrame.instanciate(style, elem);
    },
    raise: function (height, elem) {
	return LRaise.instanciate(height, elem);
    },
    sqrt: function (elem, nth) {
	return LSqrt.instanciate(elem, nth);
    },
    table: function (array, hspace, vspace, align) {
	return LTable.instanciate(array, hspace, vspace, align);
    }
};

})(cvm);

/* Operators.js */


if (window.cvm === undefined) {
    cvm = {};
}

(function (cvm) {

var operators = {
    prefix: {},
    infix: {},
    postfix: {},
    addPrefix: function (name, value) {
	this.prefix[name] = value;
    },
    addInfix: function (name, value) {
	this.infix[name] = value;
    },
    addPostfix: function (name, value) {
	this.postfix[name] = value;
    },
    getPrefix: function (name) {
	return this.prefix[name];
    },
    getInfix: function (name) {
	return this.infix[name];
    },
    getPostfix: function (name) {
	return this.postfix[name];
    },
    simpleOperator: function (symbol, lspace, rspace) {
	if (lspace !== undefined) {
	    return {
		layout: function (layout) {
		    return layout.train(
			layout.hspace(lspace),
			layout.text(symbol),
			layout.hspace(rspace === undefined ? lspace : rspace)
		    );
		}
	    };
	} else {
	    return {
		layout: function (layout) {
		    return layout.text(symbol);
		}
	    };
	}
    },
    addSumOperator: function (name, prefixSymbol, infixSymbol) {
	if (prefixSymbol || prefixSymbol === "") {
	    this.addPrefix(name, this.simpleOperator(prefixSymbol));
	}
	if (infixSymbol || prefixSymbol === "") {
	    this.addInfix(name, this.simpleOperator(infixSymbol, 3));
	}
    }
};

operators.addSumOperator("empty", "", "");
operators.addSumOperator("plus", "+", "+");
operators.addSumOperator("minus", "\u2212", "\u2212");
operators.addSumOperator("plusMinus", "\u00b1", "\u00b1");
operators.addSumOperator("minusPlus", "\u2213", "\u2213");

operators.addInfix("times", operators.simpleOperator("\u00D7", 1));

operators.addInfix("eq", operators.simpleOperator("=", 5));
operators.addInfix("leq", operators.simpleOperator("\u2264", 5));
operators.addInfix("geq", operators.simpleOperator("\u2265", 5));
operators.addInfix("lt", operators.simpleOperator("<", 5));
operators.addInfix("gt", operators.simpleOperator(">", 5));

operators.addInfix("comma", operators.simpleOperator(",", 0, 3));

operators.addInfix("and", operators.simpleOperator("\u2227", 5));
operators.addInfix("or", operators.simpleOperator("\u2228", 5));
operators.addPrefix("not", operators.simpleOperator("\u00ac"));

operators.addPostfix("prime", operators.simpleOperator("\u2032", 2, 0));

operators.addPostfix("factorial", operators.simpleOperator("!"));

operators.addPrefix("sum", {
    layout: function (layout) {
	return layout.scale(layout.text("\u2211"), 1.5);
    }
});

operators.addPrefix("product", {
    layout: function (layout) {
	return layout.scale(layout.text("\u220F"), 1.5);
    }
});

operators.addPrefix("integral", {
    layout: function (layout) {
	return layout.train([
	    layout.scale(layout.text("\u222B"), 1.5),
		layout.hspace(5)
	]);
    }
});

cvm.operators = operators;

})(cvm);

/* expr.js */


if (window.cvm === undefined) {
    cvm = {};
}

(function (cvm) {

if (cvm.expr !== undefined) {
    return;
}

var operators = cvm.operators;

if (operators === undefined) {
    throw "operators must be loaded";
}

var Expression = {
    __name__: "Expression",
    subLayout: function (layout, subexpr, bracketFlag) {
	var l = layout.ofExpr(subexpr);
	if (bracketFlag === true ||
	    bracketFlag !== false && 
	    !subexpr.isContainer && this.priority >= subexpr.priority) {
	    l = layout.bracket(l);
	}
	return l;
    },
    isNumber: function () {
	return false;
    },
    removeChild: function (e) {
	return null;
    },
    setPreviousSibling: function (prev, reciprocate) {
	this.previousSibling = prev;
	if (!prev && this.parent) {
	    this.parent.firstChild = this;
	} else if (reciprocate) {
	    prev.setNextSibling(this);
	}
    },
    setNextSibling: function (next, reciprocate) {
	this.nextSibling = next;
	if (!next && this.parent) {
	    this.parent.lastChild = this;
	} else if (reciprocate) {
	    next.setPreviousSibling(this);
	}
    },
    setRelations: function (parent, prev, next, reciprocate) {
	this.parent = parent;
	this.setNextSibling(next, reciprocate);
	this.setPreviousSibling(prev, reciprocate);
    },
    removeFromSiblings: function () {
	if (this.previousSibling) {
	    this.previousSibling.nextSibling = this.nextSibling;
	}
	if (this.nextSibling) {
	    this.nextSibling.previousSibling = this.previousSibling;
	}
	this.parent = null;
	this.nextSibling = null;
	this.previousSibling = null;
    },
    getSelection: function (expr) {
	var a, b, i;
	var child, start, stop;
	var myAncestors = [];
	if (!expr) {
	    return {expr: this};
	}
	for (a = this; !a.isRoot; a = a.parent) {
	    if (expr === a) {
		return {expr: a};
	    }
	    myAncestors.push(a);
	}
	for (a = expr; !a.isRoot; a = a.parent) {
	    if (a === this) {
		return {expr: a};
	    }
	    i = myAncestors.indexOf(a.parent);
	    if (i === 0) {
		return {expr: this};
	    } else if (i !== -1) {
		b = myAncestors[i - 1];
		for (child = a.parent.firstChild;; child = child.nextSibling) {
		    if (child === a) {
			start = a;
			stop = b;
			break;
		    } else if (child === b) {
			start = b;
			stop = a;
			break;
		    }
		}
		if (start === a.parent.firstChild 
		    && stop === a.parent.lastChild) {
		    return {expr: a.parent};
		}
		return {expr: a.parent, start: start, stop: stop.nextSibling};
	    }
	}
	return null;
    },
    getPredecessor: function () {
	var e;
	if (this.previousSibling) {
	    e = this.previousSibling;
	    while (e.lastChild) {
		e = e.lastChild;
	    }
	    return e;
	}
	return this.parent;
    },
    getPredecessor2: function () {
	var e = this.lastChild;
	if (e) {
	    return e;
	} else {
	    for (e = this; !e.previousSibling; e = e.parent) {
		if (e.isRoot) {
		    return this;
		}
	    }
	    return e.previousSibling;
	}
    },
    getSuccessor2: function () {
	var e = this.nextSibling;
	if (e) {
	    while (e.firstChild) {
		e = e.firstChild;
	    }
	    return e;
	} else {
	    return this.parent.isRoot ? this : this.parent;
	}
    },
    getVPredecessor: function () {
	var e = this.getSiblingUp();
	if (e) {
	    while (e.firstChild) {
		e = e.getBottomChild();
	    }
	    return e;
	} else {
	    return this.parent.isRoot ? this : this.parent;
	}
    },
    getVSuccessor: function () {
	var e = this.getTopChild();
	if (e) {
	    return e;
	} else {
	    for (e = this; !e.getSiblingDown(); e = e.parent) {
		if (e.isRoot) {
		    return this;
		}
	    }
	    return e.getSiblingDown();
	}
    },
    getSiblingUp: function () {
	return this.parent.getNextChildUp(this);
    },
    getSiblingDown: function () {
	return this.parent.getNextChildDown(this);
    },
    getNextChildUp: function (child) {
	return child.previousSibling;
    },
    getNextChildDown: function (child) {
	return child.nextSibling;
    },
    getTopChild: function () {
	return this.firstChild;
    },
    getBottomChild: function () {
	return this.lastChild;
    },
    getRoot: function () {
	var e = this;
	while (e && !e.isRoot) {
	    e = e.parent;
	}
	return e;
    },
    getPreviousLeaf: function () {
	// Unused
	var e;
	for (e = this; !e.previousSibling; e = e.parent) {
	    if (e.isRoot) {
		return null;
	    }
	}
	e = e.previousSibling;
	while (e.lastChild) {
	    e = e.lastChild;
	}
	return e;
    },
    getNextLeaf: function () {
	// Unused
	var e = this;
	for (e = this; !e.nextSibling; e = e.parent) {
	    if (e.isRoot) {
		return null;
	    }
	}
	e = e.nextSibling;
	while (e.firstChild) {
	    e = e.firstChild;
	}
	return e;
    },
    setSelected: function (sel) {
	var p;
	this.selected = sel;
	// Following unused
	/*for (p = this; !p.isRoot; p = p.parent) {
	    p.containsSelection = true;
	}*/
    },
    clearSelected: function () {
	var p;
	this.selected = false;
	// Following unused
	/*for (p = this; !p.isRoot; p = p.parent) {
	    p.containsSelection = false;
	}*/
    },
    needsFactorSeparator: function () {
	return false;
    },
    sumSeparator: operators.infix.plus,
    getSumExpression: function () {
	return this;
    }
};
Expression = Prototype.specialise(Expression);

var FixedChildrenExpression = {
    __name__: "FixedChildrenExpression",
    childProperties: [], // This needs to be set
    optionalProperties: {}, // this needs to be set
    vOrder: [], // this needs to be set
    __init__: function () {
	var initArgs = arguments;
	var lastChildIndex = this.childProperties.length - 1;
	var prev, next;
	var self = this;
	this.childProperties.forEach(function (prop, i) {
	    var arg = initArgs[i];
	    var next = i < lastChildIndex ? initArgs[i + 1] : undefined;
	    if (arg) {
		self[prop] = arg;
		arg.parentIndex = i;
		arg.setRelations(self, prev, next);
	    }
	    prev = arg;
	});
    },
    copy: function() {
	var self = this;
	var childCopies = this.childProperties.map(function (prop) {
	    var child = self[prop];
	    return child && child.copy();
	});
	var copy = this.__proto__.specialise();
	copy.__init__.apply(copy, childCopies);
	return copy;
    },
    replaceChild: function (oldChild, newChild) {
	var i = oldChild.parentIndex;
	var prop = this.childProperties[i];
	newChild.parentIndex = i;
	this[prop] = newChild;
	newChild.setRelations(this, 
	    oldChild.previousSibling, oldChild.nextSibling, true);
	oldChild.setRelations();
    },
    removeChild: function (child) {
	var self = this;
	var nonEmptyChildCount = 0;
	var nonEmptyChild = null;
        this.childProperties.forEach(function (prop) {
	    var ch = self[prop];
            if (ch !== child && ch && !(ch.isEditExpr && ch.isEmpty())) {
		nonEmptyChildCount++;
		nonEmptyChild = ch;
	    }
	});
	var i = child.parentIndex;
	var prop = this.childProperties[i];
	if (self.optionalProperties[prop]) {
	    self[prop] = null;
	    child.removeFromSibling();
	    return null;
	} else if (nonEmptyChildCount > 1) {
	    this.replaceChild(child, EditExpr());
	    return null;
	} else if (nonEmptyChildCount == 1) {
	    this.parent.replaceChild(this, nonEmptyChild);
	    return nonEmptyChild;
	} else {
	    return this.parent.removeChild(this);
	}
    },
    getTopChild: function () {
	var i, e;
	for (i = 0; i < this.vOrder.length; i++) {
	    e = this[this.vOrder[i]];
	    if (e) {
		return e;
	    }
	}
	return null;
    },
    getBottomChild: function () {
	var i, e;
	for (i = this.vOrder.length; i >= 0; i--) {
	    e = this[this.vOrder[i]];
	    if (e) {
		return e;
	    }
	}
	return null;
    },
    getNextChildUp: function (child) {
	var prop = this.childProperties[child.parentIndex];
	var i = this.vOrder.indexOf(prop);
	return this[this.vOrder[i - 1]] || null;
    },
    getNextChildDown: function (child) {
	var prop = this.childProperties[child.parentIndex];
	var i = this.vOrder.indexOf(prop);
	return this[this.vOrder[i + 1]] || null;
    }
};
FixedChildrenExpression = Expression.specialise(FixedChildrenExpression);


var OneChildExpression = {
    __name__: "OneChildExpression",
    __init__: function (child) {
	var initArgs = arguments;
	var self = this;
	this.child = child;
	child.setRelations(this);
	if (this.extraProperties) {
	    this.extraProperties.forEach(function (prop, i) {
		self[prop] = initArgs[i + 1];
	    });
	}
    },
    copy: function () {
	var initArgs, self;
	if (!this.extraProperties) {
	    return this.__proto__.instanciate(this.child.copy());
	} else {
	    self = this;
	    initArgs = [this.child.copy()];
	    this.extraProperties.forEach(function (prop) {
		initArgs.push(self[prop]);
	    });
	    return this.__proto__.instanciate.apply(this.__proto__, initArgs);
	};
    },
    replaceChild: function (oldChild, newChild) {
	if (this.child === oldChild) {
	    this.child = newChild;
	    newChild.setRelations(this);
	    oldChild.setRelations();
	    return true;
	}
	return false;
    },
    removeChild: function (child) {
	if (this.child === child) {
	    return this.parent.removeChild(this);
	} else {
	    return null;
	}
    }
};
OneChildExpression = Expression.specialise(OneChildExpression);

var RootExpression = {
    __name__: "RootExpression",
    isRoot: true,
    __init__: function (expr) {
	this.parent = this;
	this.expr = expr;
	expr.setRelations(this, null, null);
    },
    layout: function (layout) {
	var l = layout.ofExpr(this.expr);
	l.bindExpr(this);
	return l;
    },
    replaceChild: function (oldChild, newChild) {
	if (oldChild === this.expr) {
	    this.expr = newChild;
	    newChild.setRelations(this, null, null);
	    oldChild.setRelations();
	    return newChild;
	}
	return null;
    }
};
RootExpression = Expression.specialise(RootExpression);

var Number_ = {
    __name__: "Number",
    __init__: function (value) {
	this.value = value;
    },
    layout: function (layout) {
	var ltext = layout.text(this.value.toString());
	ltext.bindExpr(this);
	return ltext;
    },
    isNumber: function () {
	return true;
    },
    copy: function () {
	return expr.number(this.value);
    },
    needsFactorSeparator: function () {
	return true;
    }
};
Number_ = Expression.specialise(Number_);

var Parameter = {
    __name__: "Parameter",
    __init__: function (name, value) {
	this.name = name;
	this.value = value || name;
    },
    layout: function (layout) {
	var options = null;
	if (this.value.length === 1) {
	    options = {style: "italic"};
	}
	var ltext = layout.text(this.value, options);
	ltext.bindExpr(this);
	return ltext;
    },
    copy: function () {
	return expr.parameter(this.name, this.value);
    }
};
Parameter = Expression.specialise(Parameter);

var Subscript = {
    __name__: "Subscript",
    childProperties: ["base", "subscript"],
    vOrder: ["base", "subscript"],
    layout: function (layout) {
	var l = layout.subscript(
	    this.subLayout(layout, this.base),
	    layout.scale(layout.ofExpr(this.subscript), 0.8)
	);
	l.bindExpr(this);
	return l;
    }
};
Subscript = FixedChildrenExpression.specialise(Subscript);


var PrefixOperation = {
    __name__: "PrefixOperation",
    isPrefixOperation: true,
    childProperties: ["value"],
    vOrder: ["value"],
    layout: function (layout) {
	var lneg = this.prefixOp.layout(layout);
	var lval = this.subLayout(layout, this.value);
	var ltrain = layout.train(lneg, lval);
	lneg.bindExpr(this, "prefix");
	ltrain.bindExpr(this);
	return ltrain;
    },
    getSumExpression: function () {
	return this.value;
    }
};
PrefixOperation = FixedChildrenExpression.specialise(PrefixOperation);

var Negation = {
    __name__: "Negation",
    isNegation: true,
    prefixOp: operators.prefix.minus,
    sumSeparator: operators.infix.minus
};
Negation = PrefixOperation.specialise(Negation);

var PlusMinus = {
    __name__: "PlusMinus",
    isPlusMinus: true,
    prefixOp: operators.prefix.plusMinus,
    sumSeparator: operators.infix.plusMinus
};
PlusMinus = PrefixOperation.specialise(PlusMinus);

var MinusPlus = {
    __name__: "MinusPlus", 
    isMinusPlus: true,
    prefixOp: operators.prefix.minusPlus,
    sumSeparator: operators.infix.minusPlus
};
MinusPlus = PrefixOperation.specialise(MinusPlus);

var Not = {
    __name__: "Not",
    isNot: true,
    prefixOp: operators.prefix.not
};
Not = PrefixOperation.specialise(Not);

var Bracket = {
    __name__: "Bracket",
    isBracket: true,
    isContainer: true,
    childProperties: ["expr"],
    vOrder: ["expr"],
    layout: function (layout) {
	var lbracket;
	var lexpr = layout.ofExpr(this.expr);
	lbracket = layout.bracket(lexpr, "red");
	// lbracket = layout.frame({border: "red", width: 1}, lexpr);
	lbracket.bindExpr(this);
	return lbracket;
    }
};
Bracket = FixedChildrenExpression.specialise(Bracket);

var VarLenOperation = {
    __name__: "VarLenOperation",
    isVarLenOperation: true,
    __init__: function () {
	var self = this;
	var i;
	var operands = arguments[0];
	if (arguments.length !== 1 || !operands instanceof Array) {
	    operands = [];
	    for (i = 0; i < arguments.length; i++) {
		operands.push(arguments[i]);
	    }
	}
	this.operands = operands;
	operands.forEach(function (t, i) {
	    t.setRelations(self, operands[i - 1], operands[i + 1]);
	});
    },
    fromSlice: function (slice) {
	var op;
	var operands = [];
	for (op = slice.start; op !== slice.stop; op = op.nextSibling) {
	    operands.push(op.copy());
	}
	return this.__proto__.instanciate(operands);
    },
    pushOp: null,
    layout: function (layout) {
	var self = this;
	var train = [];
	var ltrain;
	this.operands.forEach(function (op, i) {
	    self.pushOp(layout, train, i);
	});
	ltrain = layout.train(train);
	ltrain.bindExpr(this);
	return ltrain;
    },
    slicedLayout: function (layout, slice) {
	var self = this;
	var left = [];
	var right = [];
	var middle = [];
	var train = left;
	var ltrain;
	this.operands.forEach(function (op, i) {
	    switch (op) {
		case slice.start:
		    train = middle;
		    break;
		case slice.stop:
		    train = right;
		    break;
	    }
	    self.pushOp(layout, train, i);
	});
	left = layout.train(left);
	middle = layout.train(middle);
	right = layout.train(right);
	ltrain = layout.train([left, middle, right]);
	ltrain.bindExpr(this);
	return ltrain;
    },
    copy: function () {
	return this.__proto__.instanciate(this.operands.map(function (t) {
	    return t.copy();
	}));
    },
    replaceChild: function (oldChild, newChild, noAggregate) {
	var self = this;
	return this.operands.some(function (t, i, operands) {
	    var res;
	    if (t === oldChild) {
		res = self.insertAfter(t, newChild, noAggregate);
		self.removeChild(t);
		return res;
	    }
	    return null;
	});
    },
    removeChild: function (child) {
	var i = this.operands.indexOf(child);
	if (i === -1) {
	    return null;
	}
	child.setRelations();
	if (this.operands.length === 2 && !this.oneOperandPossible) {
	    this.parent.replaceChild(this, this.operands[1 - i]);
	    return this.operands[1 - i];
	} else {
	    this.operands.splice(i, 1);
	    if (i) {
		this.operands[i - 1].setNextSibling(this.operands[i]);
	    }
	    if (i < this.operands.length) {
		this.operands[i].setPreviousSibling(this.operands[i - 1]);
	    }
	    return null;
	}
    },
    removeSlice: function (slice) {
	var operands = this.operands;
	var len = operands.length;
	var i = slice.start ? operands.indexOf(slice.start) : 0;
	var j = slice.stop ? operands.indexOf(slice.stop) : len;
	var sliceLen = j - i;
	switch (len - sliceLen) {
	    case 0:
		this.parent.removeChild(this);
		return true;
	    case 1:
		if (!this.oneOperandPossible) {
		    this.parent.replaceChild(this, operands[i ? 0 : j]);
		    return true;
		}
	    default:
		if (i) {
		    operands[i - 1].setNextSibling(operands[j]);
		}
		if (j < len) {
		    operands[j].setPreviousSibling(operands[i - 1]);
		}
		operands.splice(i, sliceLen);
		return true;
	}
    },
    replaceSlice: function (slice, newOperand) {
	this.insertBefore(slice.start, newOperand);
	this.removeSlice(slice);
	return true;
    },
    insertAt: function (i, newOperand) {
	var prev, next;
	var self = this;
	if (i < 0 || i > this.operands.length) {
	    return false;
	}
	if (!newOperand.isGroup && newOperand.__proto__ === this.__proto__) {
	    // Same type so aggregate both operations
	    newOperand.operands.forEach(function (op, j) {
		self.insertAt(i + j, op);
	    });
	} else {
	    prev = this.operands[i - 1];
	    next = this.operands[i];
	    this.operands.splice(i, 0, newOperand);
	    newOperand.setRelations(this, prev, next, true);
	}
	return true;
    },
    insertAfter: function (operand, newOperand) {
	var i = this.operands.indexOf(operand);
	if (i === -1) {
	    return false;
	}
	return this.insertAt(i + 1, newOperand);
    },
    insertBefore: function (operand, newOperand) {
	var i = this.operands.indexOf(operand);
	if (i === -1) {
	    return false;
	}
	return this.insertAt(i, newOperand);
    }
};
VarLenOperation = Expression.specialise(VarLenOperation);

var Sum = {
    __name__: "Sum",
    pushOp: function (layout, train, i, forceOp) {
	var op;
	var term = this.operands[i];
	if (i) {
	    if (term.selected) {
		if (term.isPrefixOperation) {
		    op = operators.infix.empty.layout(layout);
		} else {
		    op = Expression.sumSeparator.layout(layout);
		}
		op.bindExpr(term);
	    } else {
		op = term.sumSeparator.layout(layout);
		op.bindExpr(term);
		term = term.getSumExpression();
	    }
	    train.push(op);
	}
	train.push(this.subLayout(layout, term));
    }
};
Sum = VarLenOperation.specialise(Sum);

var ExprWithRelation = {
    __name__: "ExprWithRelation",
    isExprWithRelation: true,
    extraProperties: ['relation']
};
ExprWithRelation = OneChildExpression.specialise(ExprWithRelation);

var Equation = {
    __name__: "Equation",
    isProposition: true,
    isEquation: true,
    fromSlice: function (slice) {
	var op;
	var operands = [];
	for (op = slice.start; op !== slice.stop; op = op.nextSibling) {
	    if (op == slice.start && op.isExprWithRelation) {
		operands.push(op.child.copy());
	    } else {
		operands.push(op.copy());
	    }
	}
	return this.__proto__.instanciate(operands);
    },
    pushOp: function (layout, train, i, forceOp) {
	var op;
	var operand = this.operands[i];
	var relation;
	if (i) {
	    if (operand.isExprWithRelation) {
		relation = operand.relation;
		operand = operand.child;
	    } else {
		relation = 'eq';
	    }
	    op = operators.infix[relation].layout(layout);
	    train.push(op);
	    op.bindExpr(operand);
	}
	train.push(this.subLayout(layout, operand));
    }
};
Equation = VarLenOperation.specialise(Equation);

var Product = {
    __name__: "Product",
    isProduct: true,
    subLayout: function (layout, subexpr) {
	// This is to prevent standard functions which are factors from
        // being surrounded in brackets
	if (subexpr.isTrigFunction) {
	    var space = layout.hspace(2);
	    var ltrain = layout.train([space, layout.ofExpr(subexpr), space]);
	    return ltrain;
	}
	return Expression.subLayout.call(this, layout, subexpr);
    },
    pushOp: function (layout, train, i, forceOp) {
	var op;
	var factor = this.operands[i];
	if (i && (factor.needsFactorSeparator())) {
	    op = operators.infix.times.layout(layout);
	    train.push(op);
	    op.bindExpr(this, i);
	}
	train.push(this.subLayout(layout, factor));
    }
};
Product = VarLenOperation.specialise(Product);

var ArgumentList = {
    __name__: "ArgumentList",
    isArgumentList: true,
    oneOperandPossible: true,
    pushOp: function (layout, train, i, forceOp) {
	var op;
	if (i) {
	    op = operators.infix.comma.layout(layout);
	    train.push(op);
	    op.bindExpr(this, i);
	}
	train.push(this.subLayout(layout, this.operands[i]));
    },
    insertAfterInRow: function (arg, newArg) {
	return this.insertAfter(arg, newArg);
    }
};
ArgumentList = VarLenOperation.specialise(ArgumentList);

var Conjunction = {
    __name__: "Conjunction",
    isConjunction: true,
    pushOp: function (layout, train, i, forceOp) {
	var op;
	if (i) {
	    op = operators.infix.and.layout(layout);
	    train.push(op);
	    op.bindExpr(this, i);
	}
	train.push(this.subLayout(layout, this.operands[i]));
    }	
};
Conjunction = VarLenOperation.specialise(Conjunction);

var Disjunction = {
    __name__: "Disjunction",
    isDisjunction: true,
    pushOp: function (layout, train, i, forceOp) {
	var op;
	if (i) {
	    op = operators.infix.or.layout(layout);
	    train.push(op);
	    op.bindExpr(this, i);
	}
	train.push(this.subLayout(layout, this.operands[i]));
    }	
};
Disjunction = VarLenOperation.specialise(Disjunction);

var ConditionalExpression = {
    __name__: "ConditionalExpression",
    isConditionalExpression: true,
    childProperties: ["expr", "condition"],
    layout: function (layout) {
	if (this.expr.isConjunction) {
	    var rows = this.expr.operands.map(function (e) {
		return [layout.ofExpr(e)];
	    });
	    var lrows = layout.table(rows, 10, 2, "l");
	    var lbr = layout.lrEnclosure(lrows, null, "}");
	    var lconj = layout.raise(4, lbr);
	    var ltrain = layout.train([lconj, layout.ofExpr(this.condition)]);
	    ltrain.bindExpr(this);
	    return ltrain;
	} else {
	    var lexpr = this.subLayout(layout, this.expr);
	    var lop = operators.infix.comma.layout(layout);
	    var lcond = this.subLayout(layout, this.condition);
	    var ltrain = layout.train([lexpr, lop, lcond]);
	    ltrain.bindExpr(this);
	    return ltrain;
	}
    }
};
ConditionalExpression = FixedChildrenExpression.specialise(ConditionalExpression);

var Piecewise = {
    __name__: "Piecewise",
    isPiecewise: true,
    isContainer: true,
    layout: function (layout) {
	var rows = this.operands.map(function (piece) {
	    if (piece.isConditionalExpression) {
		return [layout.ofExpr(piece.expr), layout.ofExpr(piece.condition)];
	    } else {
		return [layout.ofExpr(piece), layout.text("otherwise")];
	    }
	});
	var lrows = layout.table(rows, 10, 2, "ll");
	var lbr = layout.lrEnclosure(lrows, "{", null);
	var l = layout.raise(4, lbr);
	l.bindExpr(this);
	return l;
    }
};
Piecewise = VarLenOperation.specialise(Piecewise);

var Parametric = {
    __name__: "Parametric",
    isParametric: true,
    __init__: function (equations, domain) {
	this.equations = equations;
    },
    layout: function (layout) {
	var leqs = layout.stack(this.equations.operands);
	var lbr = layout.lrEnclosure(leqs, null, "}");
	var ldom = layout.ofExpr(this.domain);
    }
};

var FunctionApplication = {
    __name__: "FunctionApplication",
    isFunctionApplication: true,
    childProperties: ["func", "arglist"],
    layout: function (layout) {
	var lfunc = layout.ofExpr(this.func);
	var largs = layout.bracket(layout.ofExpr(this.arglist), "blue");
	var ltrain = layout.train([lfunc, largs]);
	ltrain.bindExpr(this);
	return ltrain;
    }
};
FunctionApplication = FixedChildrenExpression.specialise(FunctionApplication);

var Power = {
    __name__: "Power",
    isPower: true,
    childProperties: ["base", "power"],
    vOrder: ["power", "base"],
    subLayout: function (layout, subexpr) {
	// This is to make sure roots are surrounded in brackets.
	// The general rule fails to do this as roots are containers
	var l = Expression.subLayout.call(this, layout, subexpr);
	if (subexpr === this.base && subexpr.isSqrt) {
	    l = layout.bracket(l);
	}
	return l;
    },
    layout: function (layout) {
	var bLayout = this.subLayout(layout, this.base);
	var pLayout = layout.ofExpr(this.power);
	var ls = layout.superscript(bLayout, layout.scale(pLayout, 0.8));
	ls.bindExpr(this);
	return ls;
    },
    needsFactorSeparator: function () {
	return this.base.needsFactorSeparator();
    }
};
Power = FixedChildrenExpression.specialise(Power);

var Fraction = {
    __name__: "Fraction",
    isFraction: true,
    childProperties: ["num", "den"],
    vOrder: ["num", "den"],
    __init__: function (num, den, keepScale) {
	FixedChildrenExpression.__init__.call(this, num, den);
	this.scaleDown = !keepScale;
    },
    layout: function (layout) {
	var line = layout.hline(null, 1);
	var vspace = layout.vspace(2);
	var hspace = layout.hspace(4);
	var den = layout.train([hspace, layout.ofExpr(this.den), hspace]);
	var num = layout.train([hspace, layout.ofExpr(this.num), hspace]);
	var stack = layout.stack([den, vspace, line, vspace, num], 1);
	stack.bindExpr(this);
	line.bindExpr(this, "line");
	if (this.scaleDown) {
	     stack = layout.scale(stack, 0.8);
	} else {
	    return stack;
	}
	return layout.raise(4, stack);
    },
    needsFactorSeparator: function () {
	return true;
    }
};
Fraction = FixedChildrenExpression.specialise(Fraction);

var Sqrt = {
    __name__: "Sqrt",
    isContainer: true,
    isSqrt: true,
    __init__: function (expr, nth) {
	this.expr = expr;
	this.nth = nth;
	if (nth) {
	    nth.setRelations(this, null, expr);
	}
	expr.setRelations(this, nth, null);
    },
    layout: function (layout) {
	var l = layout.ofExpr(this.expr);
	var lnth = this.nth && layout.scale(layout.ofExpr(this.nth), 0.8);
	var lroot = layout.sqrt(l, lnth);
	lroot.bindExpr(this);
	return lroot;
    },
    copy: function () {
	return expr.sqrt(this.expr.copy(), this.nth && this.nth.copy());
    },
    replaceChild: function (oldChild, newChild) {
	if (oldChild === this.expr) {
	    this.expr = newChild;
	    newChild.setRelations(this, this.nth, null, true);
	    oldChild.setRelations();
	    return true;
	} else if (oldChild === this.nth) {
	    this.nth = newChild;
	    newChild.setRelations(this, null, this.expr, true);
	    oldChild.setRelations();
	    return true;
	}
	return false;
    },
    removeChild: function (child) {
	if (child === this.nth) {
	    this.parent.replaceChild(this, this.expr);
	    return this.parent;
	} else if (child === this.expr) {
	    if (this.nth) {
		this.parent.replaceChild(this, this.nth);
		return this.parent;
	    } else {
		return this.parent.removeChild(this);
	    }
	} else {
	    return null;
	}
    }	
};
Sqrt = Expression.specialise(Sqrt);

var TrigFunction = {
    __name__: "TrigFunction",
    isTrigFunction: true,
    __init__: function (name, arg, power) {
	this.name = name;
	this.arg = arg;
	this.power = power;
	this.arg.setRelations(this);
	if (power) {
	    this.power.setRelations(this, null, arg, true);
	}
    },
    subLayout: function (layout, subexpr) {
	// If subexpr is a product containing at least one standard
	// function then it must be surrounded in brackets
	var trigFactor;
	if (subexpr.isProduct) {
	     trigFactor = subexpr.operands.some(function (op) {
		return op.isTrigFunction;
	     });
	     if (trigFactor) {
		 return layout.bracket(layout.ofExpr(subexpr));
	     }
	}
	return Expression.subLayout.call(this, layout, subexpr);
    },
    layout: function (layout) {
	var lname = layout.text(this.name);
	var lspace = layout.hspace(3);
	var larg = this.subLayout(layout, this.arg);
	var lpower;
	if (this.power) {
	    lpower = layout.ofExpr(this.power);
	    lname = layout.superscript(lname, layout.scale(lpower, 0.8));
	}
	var l = layout.train([lname, lspace, larg]);
	l.bindExpr(this);
	return l;
    },
    copy: function () {
	return expr.trigFunction(
	    this.name, 
	    this.arg.copy(), 
	    this.power && this.power.copy()
	);
    },
    replaceChild: function (oldChild, newChild) {
	if (oldChild === this.arg) {
	    this.arg = newChild;
	    oldChild.setRelations();
	    newChild.setRelations(this, this.power, null, true);
	    return true;
	} else if (oldChild === this.power) {
	    this.power = newChild;
	    oldChild.setRelations();
	    newChild.setRelations(this, null, this.arg, true);
	    return true;
	}
	return false;
    },
    removeChild: function (child) {
	if (child === this.arg) {
	    return this.parent.removeChild(this);
	} else if (child === this.power) {
	    this.power = undefined;
	    this.arg.setRelations(this);
	    child.setRelations();
	}
	return null;
    },
    getTopChild: function () {
	return this.power || this.arg;
    },
    getBottomChild: function () {
	return this.arg;
    },
    getNextChildUp: function (child) {
	if (child === this.arg) {
	    return this.power;
	} else {
	    return null;
	}
    },
    getNextChildDown: function (child) {
	if (child === this.power) {
	    return this.arg;
	} else {
	    return null;
	}
    }
};
TrigFunction = Expression.specialise(TrigFunction);

var Matrix = {
    __name__: "Matrix",
    isMatrix: true,
    isContainer: true,
    __init__: function (array) {
	var self = this;
	var lastItem = null;
	this.rows = array;
	this.ncols = array.reduce(function (m, r) {
	    return Math.max(m, r.length);
	}, 0);
	this.nrows = array.length;
	this.rows.forEach(function (row, i) {
	    row.forEach(function (item, j) {
		var nextItem;
		if (j + 1 === self.ncols) {
		    nextItem = self.getItemAt(i + 1, 0);
		} else {
		    nextItem = self.getItemAt(i, j + 1);
		}
		item.setRelations(self, lastItem, nextItem);
		lastItem = item;
	    });
	});

    },
    getItemAt: function (i, j) {
	var row = this.rows[i];
	return row && row[j];
    },
    layout: function (layout) {
	var lrows = this.rows.map(function (row) {
	    return row.map(function (item) {
		return layout.ofExpr(item);
	    });
	});
	var ltable = layout.table(lrows, 7, 2);
	var lbracket = layout.lrEnclosure(ltable, "[", "]");
	ltable.bindExpr(this);
	lbracket.bindExpr(this, "bracket");
	return layout.raise(4, lbracket);
    },
    copy: function () {
	return expr.matrix(this.rows.map(function (row) {
	    return row.map(function (item) { return item.copy(); });
	}));
    },
    findChild: function (child, callback) {
	var self = this;
	return this.rows.some(function (row, i) {
	    return row.some(function (item, j) {
		if (item === child) {
		    callback(row, i, item, j);
		    return true;
		}
		return false;
	    });
	});
    },
    replaceChild: function (oldChild, newChild) {
	var self = this;
	return this.findChild(oldChild, function (row, i, item, j) {
	    newChild.setRelations(self, item.previousSibling, item.nextSibling, true);
	    row[j] = newChild;
	    oldChild.setRelations();
	});
    },
    removeChild: function (child) {
	var self = this;
	return this.findChild(child, function (row, i, item, j) {
	    var prev = child.previousSibling;
	    var next = child.nextSibling;
	    var nItems = self.rows.reduce(function (x, y) {
		return x + y.length;
	    }, 0);
	    if (nItems === 2) {
		self.parent.replaceChild(self, prev || next);
		return prev || next;
	    } else if (row.length === 1) {
		self.rows.splice(i, 1);
	    } else {
		row.splice(j, 1);
	    }
	    if (prev) {
		prev.setNextSibling(next, true);
	    } else {
		next.setPreviousSibling(prev, true);
	    }
	    child.setRelations();
	    return null;
	});
    },
    insertAfterInRow: function (oldItem, newItem) {
	var self = this;
	return this.findChild(oldItem, function (row, i, item, j) {
	    newItem.setRelations(self, item, item.nextSibling, true);
	    row.splice(j + 1, 0, newItem);
	});
    },
    insertRowAfter: function (oldItem, newRow) {
	var self = this;
	return this.findChild(oldItem, function (row, i, item, j) {
	    newRow.forEach(function (newItem, k) {
		newItem.setRelations(self,
		    newRow[k - 1] || row[row.length - 1],
		    newRow[k + 1] || self.rows[i + 1] && self.rows[i + 1][0],
		    true);
	    });
	    self.rows.splice(i + 1, 0, newRow);
	});
    },
    getNextChildUp: function (child) {
	var self = this;
	var next = null;
	this.findChild(child, function (row, i, item, j) {
	    if (i > 0) {
	        next = self.rows[i - 1][j];
	    }
	});
	return next;
    },
    getNextChildDown: function (child) {
	var self = this;
	var next = null;
	this.findChild(child, function (row, i, item, j) {
	    if (i < self.rows.length) {
	        next = self.rows[i + 1][j];
	    }
	});
	return next;
    },
    needsFactorSeparator: function () {
	return true;
    }
};
Matrix = Expression.specialise(Matrix);
			  
var EditExpr = {
    __name__: "EditExpr",
    isEditExpr: true,
    __init__: function (content, operand) {
	this.content = content || "";
	this.operand = operand;
	this.resetCompletions();
    },
    layout: function (layout) {
	var lcontent = layout.text(this.content || "?");
	var lcolor = layout.color("red", lcontent);
	var comp, lcomp, lcompcolor, ltrain, ledit;
	lcontent.bindExpr(this);
	if (this.completionIndex === -1) {
	    ledit = lcolor;
	} else {
	    comp = this.completions[this.completionIndex];
	    lcomp = layout.text(comp);
	    lcompcolor = layout.color("gray", lcomp);
	    ltrain = layout.train([lcolor, lcompcolor]);
	    ltrain.bindExpr(this);
	    ledit = ltrain;
	}
	if (this.operand) {
	    return layout.train([layout.ofExpr(this.operand), ledit]);
	} else {
	    return ledit;
	}
    },
    copy: function () {
	return expr.editExpr(this.content, this.operand);
    },
    isEmpty: function () {
	return !this.content;
    },
    isInteger: function () {
	return /^\d+$/.test(this.content);
    },
    isDecimal: function () {
	return /^\d+\.\d*$/.test(this.content);
    },
    resetCompletions: function () {
	this.completions = [];
	this.completionIndex = -1;
    },
    setCompletions: function (completions) {
	this.completions = completions;
	this.completionIndex = 0;
    },
    cycleCompletions: function () {
	if (this.completionIndex !== -1) {
	    this.completionIndex++;
	    this.completionIndex %= this.completions.length;
	}
    },
    getCurrentCompletion: function () {
	if (this.completionIndex !== -1) {
	    return this.completions[this.completionIndex];
	} else {
	    return "";
	}
    },
    needsFactorSeparator: function () {
	if (this.operand) {
	    return this.operand.needsFactorSeparator();
	}
	return /^\d/.test(this.content);
    }
};
EditExpr = Expression.specialise(EditExpr);

var Fencing = {
    __name__: "Fencing",
    isContainer: true,
    layout: function (layout) {
	var lvalue = layout.ofExpr(this.child);
	var labs = layout.lrEnclosure(lvalue,
		this.leftFence, this.rightFence);
	labs.bindExpr(this);
	return labs;
    }
};
Fencing = OneChildExpression.specialise(Fencing);

var Abs = {
    __name__: "Abs",
    leftFence: "|",
    rightFence: "|"
};
Abs = Fencing.specialise(Abs);

var Ceiling = {
    __name__: "Ceiling",
    leftFence: "|+",
    rightFence: "+|"
};
Ceiling = Fencing.specialise(Ceiling);

var Floor = {
    __name__: "Floor",
    leftFence: "|_",
    rightFence: "_|"
};
Floor = Fencing.specialise(Floor);

var Conjugate = {
    __name__: "Conjugate",
    isContainer: true,
    layout: function(layout) {
	var lvalue = layout.ofExpr(this.child);
	var line = layout.hline(null, 1);
	var vspace = layout.vspace(2);
	var stack = layout.stack([lvalue, vspace, line], 0);
	stack.bindExpr(this);
	line.bindExpr(this, "line");
	return stack;
    }
};
Conjugate = OneChildExpression.specialise(Conjugate);

var Factorial = {
    __name__: "Factorial",
    layout: function (layout) {
	var lvalue = this.subLayout(layout, this.child);
	var excl = operators.getPostfix("factorial").layout(layout);
	var ltrain = layout.train([lvalue, excl]);
	ltrain.bindExpr(this);
	return ltrain;
    }
};
Factorial = OneChildExpression.specialise(Factorial);

var OpOf = {
    __name__: "OpOf",
    isOpOf: true,
    childProperties: ["arg", "from", "to"],
    optionalProperties: {from: true, to: true},
    layout: function (layout) {
	var stack = [];
	var i = 0;
	var lstack, ltrain;
	if (this.from) {
	    stack.push(layout.scale(layout.ofExpr(this.from), 0.8));
	    i = 1;
	}
	stack.push(this.operator.layout(layout));
	if (this.to) {
	    stack.push(layout.scale(layout.ofExpr(this.to), 0.8));
	}
	lstack = layout.stack(stack, i);
	ltrain = layout.train(lstack, this.subLayout(layout, this.arg));
	ltrain.bindExpr(this);
	return ltrain;
    },
    setFrom: function (newFrom) {
	if (this.from) {
	    this.from.setRelations();
	}
	this.from = newFrom;
	newFrom.setRelations(this, this.arg, this.to, true);
    },
    setTo: function (newTo) {
	if (this.to) {
	    this.to.setRelations();
	}
	this.to = newTo;
	newTo.setRelations(this, this.from, null, true);
    }
};
OpOf = FixedChildrenExpression.specialise(OpOf);

var SumOf = {
    __name__: "SumOf",
    isSumOf: true,
    operator: operators.getPrefix("sum")
};
SumOf = OpOf.specialise(SumOf);

var ProductOf = {
    __name__: "ProductOf",
    isProductOf: true,
    operator: operators.getPrefix("product")
};
ProductOf = OpOf.specialise(ProductOf);

var IntegralOf = {
    __name__: "IntegralOf",
    isIntegralOf: true,
    operator: operators.getPrefix("integral")
};
IntegralOf = OpOf.specialise(IntegralOf);

var Differential = {
    __name__: "Differential",
    isDifferential: true,
    layout: function (layout) {
	var ld = layout.text("d");
	var lvar = this.subLayout(layout, this.child);
	var l = layout.train([ld, lvar]);
	l.bindExpr(this);
	return l;
    }
};
Differential = OneChildExpression.specialise(Differential);

var Derivative = {
    __name__: "Derivative",
    isDerivative: true,
    childProperties: ["expr", "variable"],
    optionalProperties: {variable: true},
    layout: function (layout) {
	var ltrain;
	if (!this.variable) {
	    ltrain = layout.topAlign(
		this.subLayout(layout, this.expr),
		operators.getPostfix("prime").layout(layout)
	    );
	    ltrain.bindExpr(this);
	    return ltrain;
	} else {
	    var frac = expr.fraction(
		expr.parameter("d"), 
		expr.differential(this.variable)
	    );
	    var diff = expr.applyFunction(frac, this.expr);
	    var ldiff = layout.ofExpr(diff);
	    ldiff.bindExpr(this);
	    return ldiff;
	}
    }
};
Derivative = FixedChildrenExpression.specialise(Derivative);

var ColorExpr = {
    __name__: "ColorExpr",
    extraProperties: ["color"],
    __init__: function () {
	OneChildExpression.__init__.apply(this, arguments);
	this.priority = this.child.priority;
    },
    layout: function (layout) {
	return layout.color(this.color, this.child.layout(layout));
    },
    replaceChild: function () {
	OneChildExpression.replaceChild.apply(this, arguments);
	this.priority = this.child.priority;
    }
};
ColorExpr = OneChildExpression.specialise(ColorExpr);

//
// Set priorities
//

var priorities = [
    [Number_, 100],
    [Parameter, 100],
    [EditExpr, 100],
    [Bracket, 97],
    [Subscript, 96.5],
    [FunctionApplication, 96.5],
    [Derivative, 96.5],
    [Not, 96.4],
    [Factorial, 96],
    [Differential, 96],
    [Sqrt, 95],
    [Abs, 95],
    [Ceiling, 95],
    [Floor, 95],
    [Conjugate, 95],
    [Power, 90],
    [Fraction, 80],
    [Product, 50],
    [SumOf, 40],
    [ProductOf, 40],
    [IntegralOf, 40],
    [TrigFunction, 40],
    [Negation, 20],
    [Sum, 10],
    [Matrix, 7],
    [ExprWithRelation, 5.1],
    [Equation, 5],
    [ConditionalExpression, 4.5],
    [Piecewise, 4.2],
    [Conjunction, 4],
    [Disjunction, 3]
];

priorities.forEach(function (pl) {
    pl[0].priority = pl[1];
});

var expr = cvm.expr = {
    Number: Number_,
    Parameter: Parameter,
    EditExpr: EditExpr,
    Bracket: Bracket,
    Subscript: Subscript,
    FunctionApplication: FunctionApplication,
    Derivative: Derivative,
    Not: Not,
    Factorial: Factorial,
    Differential: Differential,
    Sqrt: Sqrt,
    Abs: Abs,
    Ceiling: Ceiling,
    Floor: Floor,
    Conjugate: Conjugate,
    Power: Power,
    Fraction: Fraction,
    Product: Product,
    SumOf: SumOf,
    ProductOf: ProductOf,
    IntegralOf: IntegralOf,
    TrigFunction: TrigFunction,
    Negation: Negation,
    Sum: Sum,
    Matrix: Matrix,
    ExprWithRelation: ExprWithRelation,
    Equation: Equation,
    ConditionalExpression: ConditionalExpression,
    Piecewise: Piecewise,
    Conjunction: Conjunction,
    Disjunction: Disjunction,

    number: function (n) {
	return Number_.instanciate(n);
    },
    parameter: function (name, value) {
	return Parameter.instanciate(name, value);
    },
    subscript: function (base, subscript) {
	return Subscript.instanciate(base, subscript);
    },
    neg: function (x) {
	return Negation.instanciate(x);
    },
    plusMinus: function (x) {
	return PlusMinus.instanciate(x);
    },
    minusPlus: function (x) {
	return MinusPlus.instanciate(x);
    },
    not: function (x) {
	return Not.instanciate(x);
    },
    brackets: function (x) {
	return Bracket.instanciate(x);
    },
    sum: function (terms) {
	return Sum.instanciate(terms);
    },
    argumentList: function (args) {
	return ArgumentList.instanciate(args);
    },
    applyFunction: function (f, arglist) {
	return FunctionApplication.instanciate(f, arglist);
    },
    product: function (factors) {
	return Product.instanciate(factors);
    },
    conjunction: function (props) {
	return Conjunction.instanciate(props);
    },
    disjunction: function (props) {
	return Disjunction.instanciate(props);
    },
    conditionalExpression: function (expr, cond) {
	return ConditionalExpression.instanciate(expr, cond);
    },
    piecewise: function (pieces) {
	return Piecewise.instanciate(pieces);
    },
    power: function (x, y) {
	return Power.instanciate(x, y);
    },
    fraction: function (x, y) {
	return Fraction.instanciate(x, y);
    },
    editExpr: function (content, operand) {
	return EditExpr.instanciate(content, operand);
    },
    root: function (e) {
	return RootExpression.instanciate(e);
    },
    sqrt: function (e, nth) {
	return Sqrt.instanciate(e, nth);
    },
    abs: function (e) {
	return Abs.instanciate(e);
    },
    ceiling: function (e) {
	return Ceiling.instanciate(e);
    },
    conjugate: function (e) {
	return Conjugate.instanciate(e);
    },
    factorial: function (e) {
	return Factorial.instanciate(e);
    },
    floor: function (e) {
	return Floor.instanciate(e);
    },
    trigFunction: function (name, e, pow) {
	return TrigFunction.instanciate(name, e, pow);
    },
    matrix: function (array) {
	return Matrix.instanciate(array);
    },
    sumOf: function (e, from, to) {
	return SumOf.instanciate(e, from, to);
    },
    productOf: function (e, from, to) {
	return ProductOf.instanciate(e, from, to);
    },
    integralOf: function (e, from, to) {
	return IntegralOf.instanciate(e, from, to);
    },
    differential: function (e) {
	return Differential.instanciate(e);
    },
    derivative: function (e, v) {
	return Derivative.instanciate(e, v);
    },
    exprWithRelation: function (e, r) {
	return ExprWithRelation.instanciate(e, r);
    },
    equation: function (ops) {
	return Equation.instanciate(ops);
    },
    drawOnNewCanvas: function (e) {
	var canvas = $("<canvas/>")[0];
	// Following for IE8
	if (canvas.getContext === undefined) {
	    G_vmlCanvasManager.initElement(canvas); 
	}
	this.drawOnCanvas(e, canvas);
	return canvas;
    },
    drawOnCanvas: function (e, canvas) {
	var box = cvm.layout.ofExpr(e).box();
	e.box = box;
	canvas.style.verticalAlign = box.descent + "px";
	canvas.width = box.width + 2; // + 2 for IE9...
	canvas.height = box.height + 1;
	var ctx = canvas.getContext("2d");
	box.drawOnCanvas(ctx, 0.5, box.ascent + 0.5);
	return canvas;
    },
    color: function (e, color) {
	return ColorExpr.instanciate(e, color);
    }
};


})(cvm);

/* Parse.js */


if (window.cvm === undefined) {
    cvm = {};
}

(function (cvm) {

var expr = cvm.expr;

if (cvm.expr === undefined) {
    throw "expr module must be loaded";
}


var operations = {
    priorityMode: true,
    binop: function (Op, e, rhs) {
	if (!rhs) {
	    rhs = expr.editExpr();
	}
	if (this.priorityMode || Op.isProposition) {
	    while (!e.parent.isRoot && !e.parent.isBracket && 
		e.parent.priority > Op.priority) {
		/*if (e === e.parent.from) {
		    break;
		}*/
		e = e.parent;
	    }
	} else {
	    if (Op == expr.Sum && e.parent.isProduct) {
		e = e.parent;
	    }
	    if (Op == expr.Sum && e.parent.isPrefixOperation) {
		e = e.parent;
	    }
	}
	// The next two lines are a hack to allow e.g. sin^2x to mean sin^2(x)
	if (
		this.priorityMode && Op === expr.Product &&
		e.parent.isTrigFunction && e === e.parent.power
	   ) {
	    e.parent.replaceChild(e.parent.arg, rhs);
	} else // end of hack XXX
	// Now a hack to allow sum from(i=1) to (n) (1/n)
	if (this.priorityMode && Op === expr.Product && e.parent.isOpOf && 
		(e === e.parent.to || e === e.parent.from)) {
	    e.parent.replaceChild(e.parent.arg, rhs);
	} else // end of hack XXX
	if (e.__proto__ === Op && !e.isGroup) {
	    e.insertAfter(e.lastChild, rhs);
	} else {
	    var p = e.parent;
	    var s = Op.instanciate(e.copy(), rhs);
	    e.parent.replaceChild(e, s);
	}
	return rhs;
    },
    add: function (e, rhs) {
	return operations.binop(expr.Sum, e, rhs);
    },
    mult: function (e, rhs) {
	return operations.binop(expr.Product, e, rhs);
    },
    addRelation: function (rel) {
	return function (e, rhs) {
	    if (!rhs) {
		rhs = expr.editExpr();
	    }
	    var relRhs = expr.ExprWithRelation.instanciate(rhs, rel);
	    operations.binop(expr.Equation, e, relRhs);
	    return rhs;
	};
    },
    and: function (e, rhs) {
	return operations.binop(expr.Conjunction, e, rhs);
    },
    or: function (e, rhs) {
	return operations.binop(expr.Disjunction, e, rhs);
    },
    conditional: function (e, rhs) {
	return operations.binop(expr.ConditionalExpression, e, rhs);
    },
    piecewise: function (e, rhs) {
	return operations.binop(expr.Piecewise, e, rhs);
    },
    multByBracket: function (e) {
	var rhs = expr.editExpr();
	operations.mult(e, expr.brackets(rhs));
	return rhs;
    },
    openArgList: function (e) {
	var rhs = expr.editExpr();
	var func = expr.applyFunction(e.copy(), expr.argumentList([rhs]));
	e.parent.replaceChild(e, func);
	return rhs;
    },
    addprefixop: function (maker) {
	return function (e) {
	    var rhs = expr.editExpr();
	    operations.add(e, maker(rhs));
	    return rhs;
	};
    },
    pow: function (e) {
	var p = e.parent;
	var pow = expr.editExpr();
	p.replaceChild(e, expr.power(e.copy(), pow));
	return pow;
    },
    frac: function (e) {
	var rhs = expr.editExpr();
	if (operations.priorityMode) {
	    while (!e.parent.isRoot && !e.parent.isBracket && 
		e.parent.priority > expr.Fraction.priority) {
		e = e.parent;
	    }
	}
	e.parent.replaceChild(e, expr.fraction(e.copy(), rhs));
	return rhs;
    },
    prefixop: function (maker) {
	return function (e) {
	    var p = e.parent;
	    var ce = e.copy();
	    var cex = maker(ce);
	    p.replaceChild(e, maker(ce));
	    return ce;
	};
    },
    closeBracket: function (e) {
	var p;
	for (p = e.parent; !p.isRoot; p = p.parent) {
	    if (p.isBracket) {
		e = p.expr;
		e.isGroup = true;
		p.parent.replaceChild(p, e);
		break;
	    }
	}
	return e;
    },
    closeArgList: function (e) {
	var p;
	for (p = e.parent; !p.isRoot; p = p.parent) {
	    if (p.insertAfterInRow || p.insertRowAfter) {
		return p.parent;
	    }
	}
	return e;
    },
    factorial: function (e) {
	var p = e.parent;
	var fac_e = expr.factorial(e.copy());
	p.replaceChild(e, fac_e);
	return fac_e;
    },
    differentiate: function (e) {
	var p = e.parent;
	var diff_e = expr.derivative(e.copy());
	p.replaceChild(e, diff_e);
	return diff_e;
    },
    nthRoot: function (e, rhs) {
	var p = e.parent;
	if (!rhs) {
	    rhs = expr.editExpr();
	}
	p.replaceChild(e, expr.sqrt(rhs, e.copy()));
	return rhs;
    },
    subscript: function (e, rhs) {
	var p = e.parent;
	if (!rhs) {
	    rhs = expr.editExpr();
	}
	p.replaceChild(e, expr.subscript(e.copy(), rhs));
	return rhs;
    },
    subscriptList: function (e) {
	var p = e.parent;
	var rhs = expr.editExpr();
	operations.subscript(e, expr.argumentList([rhs]));
	return rhs;
    },
    addColumn: function (e, rhs) {
	rhs = expr.editExpr();
	if (operations.priorityMode) {
	    while (!e.parent.isRoot && 
		   !e.parent.insertAfterInRow && 
		   !e.parent.isBracket) {
		e = e.parent;
	    }
	}
	if (e.parent.insertAfterInRow) {
	    e.parent.insertAfterInRow(e, rhs);
	} else {
	    e.parent.replaceChild(e, expr.matrix([[e.copy(), rhs]]));
	}
	return rhs;
    },
    addRow: function (e, rhs) {
	rhs = expr.editExpr();
	if (operations.priorityMode) {
	    while (!e.parent.isRoot && !e.parent.insertRowAfter && !e.parent.isBracket) {
		e = e.parent;
	    }
	}
	if (e.parent.insertRowAfter) {
	    e.parent.insertRowAfter(e, [rhs]);
	} else {
	    e.parent.replaceChild(e, expr.matrix([[e.copy()], [rhs]]));
	}
	return rhs;
    },
    fromOp: function(e, rhs) {
	var target = e;
	rhs = expr.editExpr();
	if (true || operations.priorityMode) {
	    while (!target.isRoot && !target.setFrom && !target.isBracket) {
		target = target.parent;
	    }
	}
	if (target.setFrom) {
	    target.setFrom(rhs);
	    return rhs;
	}
	return e;
    },
    toOp: function(e, rhs) {
	var target = e;
	rhs = expr.editExpr();
	if (true || operations.priorityMode) {
	    while (!target.isRoot && !target.setTo && !target.isBracket) {
		target = target.parent;
	    }
	}
	if (target !== e && e.isEditExpr && e.operand) {
	    e.parent.replaceChild(e, e.operand);
	}
	if (target.setTo) {
	    target.setTo(rhs);
	    return rhs;
	}
	return e;
    }
};

var infixBinaryOps = {
    "+": operations.add,
    "*": operations.mult,
    "-": operations.addprefixop(expr.neg),
    "±": operations.addprefixop(expr.plusMinus),
    "+-": operations.addprefixop(expr.plusMinus),
    "-+": operations.addprefixop(expr.minusPlus),
    "/": operations.frac,
    "^": operations.pow,
    "(": operations.multByBracket,
    "[": operations.openArgList,
    "=": operations.addRelation('eq'),
    "<": operations.addRelation('lt'),
    ">": operations.addRelation('gt'),
    "<=": operations.addRelation('leq'),
    ">=": operations.addRelation('geq'),
    ",": operations.addColumn,
    ";": operations.addRow,
    "root": operations.nthRoot,
    "_": operations.subscript,
    "_[": operations.subscriptList,
    "and": operations.and,
    "or": operations.or,
    "if": operations.conditional,
    "else": operations.piecewise
};

var prefixUnaryOps = {
    "-": operations.prefixop(expr.neg),
    "±": operations.prefixop(expr.plusMinus),
    "+-": operations.prefixop(expr.plusMinus),
    "-+": operations.prefixop(expr.minusPlus),
    "not": operations.prefixop(expr.not),
    "(": operations.prefixop(expr.brackets),
    "d.": operations.prefixop(expr.differential),
    "from": operations.fromOp,
    "to": operations.toOp
};

var postfixUnaryOps = {
    ")": operations.closeBracket,
    "]": operations.closeArgList,
    "!": operations.factorial,
    "'": operations.differentiate
};

var greekLowercase = [
    {name:"alpha", code:"\u03b1"},
    {name:"beta", code:"\u03b2"},
    {name:"gamma", code:"\u03b3"},
    {name:"delta", code:"\u03b4"},
    {name:"epsilon", code:"\u03b5"},
    {name:"zeta", code:"\u03b6"},
    {name:"eta", code:"\u03b7"},
    {name:"theta", code:"\u03b8"},
    {name:"iota", code:"\u03b9"},
    {name:"kappa", code:"\u03ba"},
    {name:"lambda", code:"\u03bb"},
    {name:"mu", code:"\u03bc"},
    {name:"nu", code:"\u03bd"},
    {name:"xi", code:"\u03be"},
    {name:"omicron", code:"\u03bf"},
    {name:"pi", code:"\u03c0"},
    {name:"rho", code:"\u03c1"},
    {name:"sigma", code:"\u03c3"},
    {name:"tau", code:"\u03c4"},
    {name:"upsilon", code:"\u03c5"},
    {name:"phi", code:"\u03c6"},
    {name:"chi", code:"\u03c7"},
    {name:"psi", code:"\u03c8"},
    {name:"omega", code:"\u03c9"}
];

var greekUppercase = [
    {name:"Alpha", code:"\u0391"},
    {name:"Beta", code:"\u0392"},
    {name:"Gamma", code:"\u0393"},
    {name:"Delta", code:"\u0394"},
    {name:"Epsilon", code:"\u0395"},
    {name:"Zeta", code:"\u0396"},
    {name:"Eta", code:"\u0397"},
    {name:"Theta", code:"\u0398"},
    {name:"Iota", code:"\u0399"},
    {name:"Kappa", code:"\u039a"},
    {name:"Lambda", code:"\u039b"},
    {name:"Mu", code:"\u039c"},
    {name:"Nu", code:"\u039d"},
    {name:"Xi", code:"\u039e"},
    {name:"Omicron", code:"\u039f"},
    {name:"Pi", code:"\u03a0"},
    {name:"Rho", code:"\u03a1"},
    {name:"Sigma", code:"\u03a3"},
    {name:"Tau", code:"\u03a4"},
    {name:"Upsilon", code:"\u03a5"},
    {name:"Phi", code:"\u03a6"},
    {name:"Chi", code:"\u03a7"},
    {name:"Psi", code:"\u03a8"},
    {name:"Omega", code:"\u03a9"}
];

var constants = {
    // Exponential

    exp: "\u212f"
};

[greekLowercase, greekUppercase].forEach(function (list) {
    list.forEach(function (item) {
	constants[item.name] = item.code;
    });
});

var functions = {
};

[
   {name: "sqrt", expr: expr.sqrt},
   {name: "abs", expr: expr.abs},
   {name: "ceil", expr: expr.ceiling},
   {name: "conj", expr: expr.conjugate},
   {name: "floor", expr: expr.floor},
   {name: "sum", expr: expr.sumOf},
   {name: "prod", expr: expr.productOf},
   {name: "integral", expr: expr.integralOf}
].forEach(function (fdata) {
    functions[fdata.name] = fdata.expr;
});
   
[
    "sin", "cos", "tan", "cosec", "sec", "cot",
    "sinh", "cosh", "tanh", "cosech", "sech", "coth"
].forEach(function (f) {
    functions[f] = function (arg) {
	return expr.trigFunction(f, arg);
    };
    functions[f + "^"] = function (arg) {
	return expr.trigFunction(f, expr.editExpr(), arg);
    };
});

[
    "arcsin", "arccos", "arctan",
    "arcsinh", "arccosh", "arctanh"
].forEach(function (f) {
    functions[f] = function (arg) {
	return expr.trigFunction(f, arg);
    };
});

var Keywords = Prototype.specialise({
    __init__: function () {
	this.list = [];
	this.map = {};
    },
    updateWithObject: function (obj, type) {
	var kw, info;
	for (kw in obj) {
	    if (obj.hasOwnProperty(kw)) {
		info = {kw: kw, type: type, value: obj[kw]};
		this.list.push(info);
		this.map[kw] = info;
	    }
	}
	this.list.sort(function (x, y) {
	    return x.kw.localeCompare(y.kw);
	});
    },
    getCompletions: function (word) {
	var completions = [];
	var maxlen = 0;
	var wordlen = word.length;
	var longestPrefix = null;
	this.list.forEach(function (item) {
	    if (word.length <= item.kw.length && !item.kw.lastIndexOf(word, 0)) {
		completions.push(item.kw.substr(wordlen));
	    }
	    if (!word.lastIndexOf(item.kw, 0) && item.kw.length > maxlen) {
		maxlen = item.kw.length;
		longestPrefix = item;
	    }
	});
	return {completions: completions, longestPrefix: longestPrefix};
    },
    contains: function (kw) {
	return this.map.hasOwnProperty(kw);
    }
});

var prefixKeywords = Keywords.instanciate();
var postfixKeywords = Keywords.instanciate();

prefixKeywords.updateWithObject(constants, "Constant");
prefixKeywords.updateWithObject(functions, "Function");
prefixKeywords.updateWithObject(prefixUnaryOps, "PrefixOp");

postfixKeywords.updateWithObject(infixBinaryOps, "InfixOp");
postfixKeywords.updateWithObject(postfixUnaryOps, "PostfixOp");

var directives = {
    color: function (color, target) {
	var colorExpr;
	if (target.operand) {
	    colorExpr = expr.color(target.operand, color);
	    target.parent.replaceChild(target, colorExpr);
	    return colorExpr;
	} else {
	    var editExpr = expr.editExpr();
	    colorExpr = expr.color(editExpr, color);
	    target.parent.replaceChild(target, colorExpr);
	    return editExpr;
	}
    }
};

var parser = {
    interpretNumber: function (input, target) {
	var numberExpr = expr.number(parseFloat(input));
	if (target.operand) {
	    target.parent.replaceChild(target, target.operand);
	    operations.mult(target.operand, numberExpr);
	} else {
	    target.parent.replaceChild(target, numberExpr);
	}
	return numberExpr;
    },
    interpretDirective: function (input, target) {
	input = input.substring(1, input.length - 1);
	var parts = input.split(":", 2);
	var cmd, args;
	if (parts.length == 1) {
	    cmd = input;
	    args = "";
	} else {
	    cmd = parts[0];
	    args = parts[1];
	}
	if (directives[cmd]) {
	    return directives[cmd](args, target);
	} else {
	    console && console.log("Unknown directive: " + cmd);
	    return target;
	}
    },
    interpretParameter: function (input, target) {
	var param = expr.parameter(input);
	if (target.operand) {
	    target.parent.replaceChild(target, target.operand);
	    operations.mult(target.operand, param);
	} else {
	    target.parent.replaceChild(target, param);
	}
	return param;
    },
    interpretFunction: function (func, target) {
	var arg = expr.editExpr();
	var parent = target.parent;
	func = func(arg);
	// The following is a hack for e.g. sin2xcosx to interpret as
	// (sin 2x)(cos x) rather than sin(2x cos x)
	// XXX
	if (operations.priorityMode) {
	    if (parent.isProduct && parent.parent.isTrigFunction) {
		parent = parent.removeChild(target) || parent;
		operations.mult(parent.parent, func);
		return arg;
	    }
	}
	target.parent.replaceChild(target, func);
	return arg;
    },
    interpretConstant: function (cons, target, k) {
	cons = expr.parameter(k, cons);
	target.parent.replaceChild(target, cons);
	return cons;
    },
    interpretPrefixOp: function (op, target) {
	return op(target);
    },
    interpretInfixOp: function (op, target) {
	target.parent.replaceChild(target, target.operand);
	return op(target.operand);
    },
    interpretPostfixOp: function (op, target) {
	target.parent.replaceChild(target, target.operand);
	return op(target.operand);
    },    
    interpretKeyword: function (k, target) {
	return this['interpret' + k.type](k.value, target, k.kw);
    },
    interpret: function (target, input, ongoing) {
	var comp, newTarget, kw;
	if (input === undefined || input === null) {
	    input = target.content;
	} 
	if (!input) {
	    if (target.isEditExpr) {
		if (target.operand) {
		    target.parent.replaceChild(target, target.operand);
		    target = target.operand;
		} else {
		    target.content = "";
		    target.resetCompletions();
		}
	    }
	    return target;
	}
	if (!target.isEditExpr) {
	    newTarget = expr.editExpr(input, target);
	    target.parent.replaceChild(target, newTarget);
	    target = newTarget;
	}
	// XXX Hack to prevent weird bug:
	target.content = null;
	if (input.charAt(0) === " ") {
	    // input starts with space: force interpretation of target
	    // then continue
	    target = this.interpret(target);
	    input = input.substr(1);
	    return this.interpret(target, input, ongoing);
	}
	var groups = /^\\[^\\]+\\/.exec(input);
	if (groups) {
	    // Input starts with a directive
	    var directive = groups[0];
	    if (directive.length === input.length) {
		// Input is just a directive
		if (ongoing) {
		    // Input ongoing so keep it as it is
		    target.content = input;
		    return target;
		}
		// Input finished so apply directive
		return this.interpretDirective(directive, target);
	    }
	    // There is more after the directive
	    target = this.interpretDirective(directive, target);
	    input = input.substr(directive.length);
	    return this.interpret(target, input, ongoing);
	}
	if (input.charAt(0) === "\\") {
	    // We're writing a directive but it's not finished
	    if (ongoing) {
		target.content = input;
		return target;
	    }
	    throw "unfinished directive";
	}
	groups = /^\d+(?:\.\d*)?/.exec(input);
	if (groups) {
	    // Input starts with a number
	    var number = groups[0];
	    if (number.length === input.length) {
		// Input is just a number
		if (ongoing) {
		    // Input ongoing so keep it as it is
		    target.content = input;
		    return target;
		}
		// Input finished so replace with a number expression
		return this.interpretNumber(number, target);
	    }
	    // There is more after the number so interpret the rest
	    target = this.interpretNumber(number, target);
	    input = input.substr(number.length);
	    return this.interpret(target, input, ongoing);
	}
	// Input doesn't start with a number.  Look for keywords
	if (target.operand) {
	    comp = postfixKeywords.getCompletions(input);
	} else {
	    comp = prefixKeywords.getCompletions(input);
	}
	if (comp.completions.length && ongoing) {
	    // There are keyword completions and input is ongoing so
	    // wait for more input
	    target.content = input;
	    if (input.length > 1) {
		target.setCompletions(comp.completions);
	    } else {
		target.resetCompletions();
	    }
	    return target;
	}
	// We are left with two cases:
	// 1. There are no completions
	// 2. There may be completions but input must be interpreted
	if (comp.longestPrefix) {
	    // Input starts with a keyword
	    kw = comp.longestPrefix.kw;
	    target = this.interpretKeyword(comp.longestPrefix, target);
	    if (kw.length === input.length) {
		// Input is just a keyword
		return target;
	    }
	    // There is more after the keyword so that needs interpreting
	    input = input.substr(kw.length);
	    return this.interpret(target, input, ongoing);	
	}
	// We are in the situation where the input doesn't start with
	// a keyword and has no possible completions (or if it does
	// the whole input must be interpreted anyway).
	if (target.operand) {
	    // There is an operand so change to a product
	    target.parent.replaceChild(target, target.operand);
	    target = operations.mult(target.operand);
	    // XXX This is a hack to allow sin^2x to mean sin^2(x)
	    /*var gp = target.parent.parent;
	    if (gp.isTrigFunction && target.parent == gp.power) {
		target.parent.removeChild(target);
		target = gp.arg;
	    }*/
	    return this.interpret(target, input, ongoing);
	}
	// This means that the first letter must be a parameter
	if (!/^\w/.test(input)) {
	    // Input doesn't start with an alphanumeric character.
	    // For now, do not process it. XXX
	    target.content = input;
	    return target;
	}
	if (input.length === 1) {
	    // The input is just a parameter
	    if (ongoing) {
		// Input is ongoing, keep it as input
		target.content = input;
		target.resetCompletions();
		return target;
	    }
	    return this.interpretParameter(input, target);
	}
	// There is more input after the parameter so it's a product
	target = this.interpretParameter(input.charAt(0), target);
	input = input.substr(1);
	return this.interpret(target, input, ongoing);
    },
    confirmCompletion: function (e) {
	if (e.isEditExpr) { // XXX
	    if (e.getCurrentCompletion() !== null) {
		return this.interpret(e, e.content + e.getCurrentCompletion());
	    }
	}
	return e;
    },
    addChar: function (e, c) {
	if (c == "\r") { // XXX
	    return this.confirmCompletion(e);
	}
	var input = e.isEditExpr ? e.content + c : c;
	return this.interpret(e, input, true);
    },
    parse: function (input) {
	var edit = expr.editExpr();
	var root = expr.root(edit);
	this.interpret(edit, input.replace(/\s+/g, " "));
	return root;
    }
};

cvm.parse = {
    parser: parser,
    operations: operations,
    prefixKeywords: prefixKeywords,
    postfixKeywords: postfixKeywords,
    greekLowercase: greekLowercase,
    greekUppercase: greekUppercase
};

})(cvm);

/* Mathml.js */

(function (cvm) {

var initBox = cvm.box.init;
var expr = cvm.expr;

var mathMLTransformInline = function (tagname) {
    var element, i, text;
    var root, canvas;
    var elements = document.getElementsByTagName(tagname || "math");
    initBox();
    for (i = elements.length - 1; i >= 0; i--) {
	element = elements[i];
	root = mathMLParser.parse(element.firstElementChild);
	canvas = expr.drawOnNewCanvas(root);
	element.parentNode.replaceChild(canvas, element);
    }
};

if (!window.preventAutomaticTransform) {
    window.addEventListener('load', function () {
	mathMLTransformInline();
    }, false);
}

var mathMLParser = {
    functions: {},
    registerFunction: function (name, arity, applyMethod) {
	this.functions[name] = {
	    name: name,
	    arity: arity,
	    apply: applyMethod
	};
	this[name] = function (node) {
	    if (node.firstElementChild) {
		throw "<" + name + "> should be an empty tag";
	    }
	    return expr.parameter(name);
	};
    },
    registerRelation: function (relation) {
	this.registerFunction(relation, null, function (args) {
	    var argsWithRel = args.map(function (arg, i) {
		if (!i) {
		    return arg;
		} else {
		    return expr.exprWithRelation(arg, relation);
		}
	    });
	    return expr.equation(argsWithRel);
	});
    },
    parse: function (el) {
	var tag = el.tagName.toLowerCase();
	if (this[tag]) {
	    var e = this[tag](el);
	    return e;
	} else {
	    return expr.editExpr();
	}
    },
    parseFunc: function (func, args, qualifiers) {
	if (func.arity) {
	    if (args.length !== func.arity) {
		throw ("Function " + func.name + " expects " +
		       func.arity + " arguments, got " + args.length);
	    }
	    args.push(qualifiers);
	    return func.apply.apply(func, args);
	} else {
	    return func.apply.call(func, args, qualifiers);
	}
    },
    apply: function (node) {
	var funcEl = node.firstElementChild;
	var el = funcEl.nextElementSibling;
	var args = [];
	var qualifiers = {};
	var arg;
	var func;
	while (el) {
	    arg = this.parse(el);
	    if (arg.isQualifier) {
		qualifiers[arg.name] = arg.value;
	    } else {
		args.push(this.parse(el));
	    }
	    el = el.nextElementSibling;
	}
	func = this.functions[funcEl.tagName.toLowerCase()];
	if (!func) {
	    args = expr.argumentList(args);
	    func = this.parse(funcEl);
	    return expr.applyFunction(func, args);
	}
	return this.parseFunc(func, args, qualifiers);
    },
    ci: function (el) {
	return expr.parameter(el.textContent);
    },
    cn: function (el) {
	return expr.number(el.textContent);
    },
    matrixrow: function (node) {
	var el = node.firstElementChild;
	var row = [];
	while (el) {
	    row.push(this.parse(el));
	    el = el.nextElementSibling;
	}
	return expr.argumentList(row);
    },
    matrix: function (node) {
	var el = node.firstElementChild;
	var rows = [];
	while (el) {
	    var row = this.parse(el);
	    rows.push(row.operands);
	    el = el.nextElementSibling;
	}
	return expr.matrix(rows);
    },
    piecewise: function (node) {
	var el = node.firstElementChild;
	var pieces = [];
	while (el) {
	    pieces.push(this.parse(el));
	    el = el.nextElementSibling;
	}
	return expr.piecewise(pieces);
    },
    piece: function (node) {
	var exprEl = node.firstElementChild;
	var conditionEl = exprEl.nextElementSibling;
	return expr.conditionalExpression(
	    this.parse(exprEl), this.parse(conditionEl));
    },
    otherwise: function (node) {
	var exprEl = node.firstElementChild;
	return this.parse(exprEl);
    }
};

var mathMLElements = {
    unaryStandardFunctions: [
	'sin', 'cos', 'tan', 'sec', 'csc', 'cot',
	'sinh', 'cosh', 'tanh', 'sech', 'csch', 'coth',
	'arcsin', 'arccos', 'arctan', 'arcsec', 'arccsc', 'arccot',
	'arcsinh', 'arccosh', 'arctanh', 'arcsech', 'arccsch', 'arccoth',
	'exp', 'ln', 'log',
	'arg'
    ],
    qualifiers: [
	'bvar', 'lowlimit', 'uplimit', 'interval', 'condition',
	'domainofapplication', 'degree', 'momentabout', 'logbase'
    ]
};

mathMLElements.unaryStandardFunctions.forEach(function (fn) {
    mathMLParser.registerFunction(fn, 1, function (arg) {
	return expr.trigFunction(fn, arg);
    });
});

mathMLElements.qualifiers.forEach(function (qual) {
    mathMLParser[qual] = function (el) {
	return {
	    isQualifier: true,
	    name: qual,
	    value: this.parse(el.firstElementChild)
	};
    };
});

mathMLParser.registerFunction("plus", null, function (args) {
    return expr.sum(args);
});
mathMLParser.registerFunction("times", null, function (args) {
    return expr.product(args);
});
mathMLParser.registerFunction("power", 2, function (base, pow) {
    return expr.power(base, pow);
});
mathMLParser.registerFunction("minus", null, function (args) {
    // Minus can be unary or binary.
    if (args.length === 1) {
	return expr.neg(args[0]);
    } else if (args.length === 2) {
	return expr.sum([args[0], expr.neg(args[1])]);
    } else {
	throw "minus expects 1 or 2 arguments, got " + args.length;
    }
});
mathMLParser.registerFunction("divide", 2, function (num, den) {
    return expr.fraction(num, den);
});
mathMLParser.registerFunction("abs", 1, function (val) {
    return expr.abs(val);
});
mathMLParser.registerFunction("conjugate", 1, function (val) {
    return expr.conjugate(val);
});
mathMLParser.registerFunction("factorial", 1, function (val) {
    return expr.factorial(val);
});
mathMLParser.registerFunction("floor", 1, function (val) {
    return expr.floor(val);
});
mathMLParser.registerFunction("ceiling", 1, function (val) {
    return expr.ceiling(val);
});
mathMLParser.registerFunction("root", 1, function (val, quals) {
    return expr.sqrt(val, quals.degree);
});
mathMLParser.registerFunction("sum", 1, function (val, quals) {
    var from;
    if (quals.bvar && quals.lowlimit) {
	from = expr.equation([quals.bvar, quals.lowlimit]);
    }
    return expr.sumOf(val, from, quals.uplimit);
});
mathMLParser.registerFunction("product", 1, function (val, quals) {
    var from;
    if (quals.bvar && quals.lowlimit) {
	from = expr.equation([quals.bvar, quals.lowlimit]);
    }
    return expr.productOf(val, from, quals.uplimit);
});
mathMLParser.registerFunction("int", 1, function (val, quals) {
    if (quals.bvar) {
	val = expr.product([val, expr.differential(quals.bvar)]);
    }
    return expr.integralOf(val, quals.lowlimit, quals.uplimit);
});
mathMLParser.registerFunction("diff", 1, function (val, quals) {
    return expr.derivative(val, quals.bvar);
});
mathMLParser.registerFunction("selector", null, function (args, quals) {
    var base = args[0];
    var sub;
    if (args.length === 2) {
	sub = args[1];
    } else {
	sub = expr.argumentList(args.slice(1));
    }
    return expr.subscript(base, sub);
});
mathMLParser.registerFunction("and", null, function (args) {
    return expr.conjunction(args);
});
mathMLParser.registerFunction("or", null, function (args) {
    return expr.disjunction(args);
});
mathMLParser.registerFunction("not", 1, function (arg) {
    return expr.not(arg);
});
['eq', 'lt', 'gt', 'geq', 'leq'].forEach(function (relation) {
    mathMLParser.registerRelation(relation);
});

cvm.mathml = {
    parser: mathMLParser
};

})(cvm);

"use strict";
/**

The MIT License (MIT)

Copyright © 2014 Remy Sharp, http://remysharp.com
Modifications Copyright © 2014 International GeoGebra Institute (IGI)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/


/*globals Node:true, NodeList:true*/
window.minQuery = (function (document, window, minQuery) {
  var OurObj = function() {
    this.minQuery = true;
  };
  OurObj.prototype = new Array;

  // Node covers all elements, but also the document objects
  var node = Node.prototype,
      nodeList = OurObj.prototype,
      forEach = 'forEach',
      trigger = 'trigger',
      each = [][forEach],
      // note: createElement requires a string in Firefox
      dummy = document.createElement('i');

  nodeList[forEach] = each;

  // we have to explicitly add a window.on as it's not included
  // in the Node object.
  window.on = node.on = function (event, fn) {
    this.addEventListener(event, fn, false);

    // allow for chaining
    return this;
  };

  // we save a few bytes (but none really in compression)
  // by using [trigger] - really it's for consistency in the
  // source code.
  window[trigger] = node[trigger] = function (type, data) {
    // construct an HTML event. This could have
    // been a real custom event
    var event = document.createEvent('HTMLEvents');
    event.initEvent(type, true, true);
    event.data = data || {};
    event.eventName = type;
    event.target = this;
    this.dispatchEvent(event);
    return this;
  };

  // just a polyfill/shim
  node.ggbMatches = function(selector) {
    // helper method for e.g. ggbChildren
    if (this.matches) {
      return this.matches(selector);
    } else if (this.matchesSelector) {
      return this.matchesSelector(selector);
      // now come the browser-prefixed versions,
      // of which we are aware of
    } else if (this.msMatchesSelector) {
      return this.msMatchesSelector(selector);
    } else if (this.mozMatchesSelector) {
      return this.mozMatchesSelector(selector);
    } else if (this.webkitMatchesSelector) {
      return this.webkitMatchesSelector(selector);
    } else if (this.oMatchesSelector) {
      return this.oMatchesSelector(selector);
    }
    // well, we have not heard about any prefixMatches...
    return false;
  }

  // combinator 1: returning this
  var extendToList = function(nodeMethod) {
    return function() {//arguments
      var args = Array.prototype.slice.call(arguments);
      this[forEach](function (el) {
        nodeMethod.apply(el, args);
      });
      return this;
    }
  }

  // combinator 2: returning combined return value
  var extendToList2 = function(nodeMethod) {
    return function() {//arguments
      var args = Array.prototype.slice.call(arguments);
      var ret = new OurObj();
      var temp;
      this[forEach](function (el) {
        // supposing that nodeMethod always returns
        // a minQuery object, which is like an array!
        temp = nodeMethod.apply(el, args);
        for (var i = 0; i < temp.length; i++) {
          ret.push(temp[i]);
        }
      });
      return ret;//.ggbFS();
    };
  }

  // combinator 3: no simple method, only implement for nodeList
  // e.g. sometimes clone, sometimes use the value itself

  // methods for combinator 1

  node.ggbAddClass = function(className) {
    var classList;
    if (this.classList) {
      this.classList.add(className);
    } else if (this.className) {//avoid text nodes
      classList = this.className.split(" ");

      // there might be more of the same className,
      // but it does not matter as removeClass will remove them all
      classList.push(className);

      this.className = classList.join(" ");
    }
    return this;
  }
  nodeList.ggbAddClass = extendToList(node.ggbAddClass);

  node.ggbRemoveClass = function(className) {
    var classList, index, i;
    if (this.classList) {
      this.classList.remove(className);
    } else if (this.className) {//avoid text nodes
      classList = this.className.split(" ");
      index = classList.indexOf(className);
      if (index > -1) {
        for (i = classList.length - 1; i >= 0; i--) {
          if (classList[i] === className) {
            classList.splice(i, 1);
          }
        }
        this.className = classList.join(" ");
      }
    }
    return this;
  }
  nodeList.ggbRemoveClass = extendToList(node.ggbRemoveClass);

  node.ggbToggleClass = function(className) {
    var classList, index, i;
    if (this.classList) {
      this.classList.toggle(className);
    } else if (this.className) {//avoid text nodes
      classList = this.className.split(" ");
      index = classList.indexOf(className);
      if (index > -1) {
        for (i = classList.length - 1; i >= 0; i--) {
          if (classList[i] === className) {
            classList.splice(i, 1);
          }
        }
      } else {
        classList.push(className);
      }
      this.className = classList.join(" ");
    }
    return this;
  }
  nodeList.ggbToggleClass = extendToList(node.ggbToggleClass);

  // TODO: DOM Node and DOM Element should be distinguished!
  node.ggbSetAttribute = Element.prototype.setAttribute;
  nodeList.ggbSetAttribute = extendToList(node.ggbSetAttribute);

  nodeList.on = extendToList(node.on); // simplification for min.js
  nodeList[trigger] = extendToList(node[trigger]); // simplification for min.js, in theory

  // methods for combinator 2

  node.ggbContents = function() {
    if (this.childNodes) {
      // this is filtered, sorted by default
      return minQuery(this.childNodes);
    }
    return new OurObj();
  }
  // a child element cannot be the child of two different parents, OK
  // but note that this set is not sorted
  nodeList.ggbContents = extendToList2(node.ggbContents);

  node.ggbChildren = function(selector) {
    if (this.children) {
      // arr is filtered (for duplicates), sorted
      var arr = minQuery(this.children);
      if (selector !== undefined) {
        if (selector == ":first") {
          // basic support
          return minQuery(arr[0]);
        } else if (selector == ":last") {
          return minQuery(arr[arr.length - 1]);
        }
        // now the task is to filter out elements
        // of arr based on the selector
        for (var i = arr.length - 1; i >= 0; i--) {
          // presupposed that arr[i] is a Node/Element
          if (!arr[i].ggbMatches(selector)) {
            arr.splice(i, 1);
          }
        }
      }
      return arr;
    }
    return new OurObj();
  }
  // a child element cannot be the child of two different parents, OK
  // but note that this set is not sorted
  nodeList.ggbChildren = extendToList2(node.ggbChildren);

  // methods for combinator 3

  nodeList.ggbFirst = function() {
    // the first solution might not be compatible with jQuery
    //this.splice(1);
    //return this;

    var sl = this[0];//.slice(0,1);
    return minQuery(sl);// 1 element, ggbFS() not needed
  }

  nodeList.ggbLast = function() {
    // the first solution might not be compatible with jQuery
    //this.splice(0, this.length - 1);
    //return this;

    var sl = this[this.length - 1];//this.slice(this.length - 1);
    return minQuery(sl);//.ggbFS();
    // however, we assume that "this" is filtered, sorted
  }

  nodeList.ggbFilter = function(selector) {
    // if "this" is filtered (for duplicates) and sorted, it well be kept
    return minQuery(this.filter(function(element, position, what) {
      return element.ggbMatches(selector);
    }));
  }

  nodeList.ggbFS = function() {
    // return the filtered (for duplicates), sorted version of "this"
    // note that "this" is not filtered, sorted
    return minQuery(this.filter(function(element, position, what) {
      // AFAIK, indexOf is about object equality, not content equality,
      // and it is right
      return what.indexOf(element) == position;
    }).sort(function(a,b) {
      // as "filter" returns a new array, this sorts the new array
      if (a.compareDocumentPosition && b.compareDocumentPosition) {
        var docpos = a.compareDocumentPosition(b);
        if (docpos) {
          if (docpos % 4) {
            return (-1);
          }
          return 1;
        }
        // returning 0 later;
      }
      return 0;
    }));
    // as we don't know where our code is buggy,
    // it's probably better to use this method everywhere we can,
    // i.e. where it is not important to return "this"
  }

  nodeList.ggbAdd = function(nodelist) {
    // nodelist can be non-array, but array-like object
    // and concat returns an array, so this is so complex
    
    // first making an array out of nodelist, to make it compatible with concat
    var nodelist1 = minQuery.ggbArray(this);
    var nodelist2 = minQuery.ggbArray(nodelist);
    // then concat
    var clist = nodelist1.concat(nodelist2);
    // then filter, sort
    clist = clist.filter(function(element, position, what) {
      // AFAIK, indexOf is about object equality, not content equality,
      // and it is right
      return what.indexOf(element) == position;
    }).sort(function(a,b) {
      // as "filter" returns a new array, this sorts the new array
      if (a.compareDocumentPosition && b.compareDocumentPosition) {
        var docpos = a.compareDocumentPosition(b);
        if (docpos) {
          if (docpos % 4) {
            return (-1);
          }
          return 1;
        }
        // returning 0 later;
      }
      return 0;
    });

    // converting back to OurObj
    return minQuery(clist);
  }

  nodeList.ggbParent = function() {
    var ret = new OurObj();
    for (var i = 0; i < this.length; i++) {
      if (this[i].parentNode) {
        ret.push(this[i].parentNode);
      }
    }
    return ret.ggbFS();
  }

  nodeList.ggbFind = function(selector) {
    var oo = new Array(), x;
    for (var i = 0; i < this.length; i++) {
      if (this[i].querySelectorAll) {
        x = this[i].querySelectorAll(selector);
        x = minQuery.ggbArray(x);
        oo = oo.concat(x);
      }
    }
    // oo is just an array, in theory
    // but we have to filter it and sort it!
    var dummy = new OurObj();
    return dummy.ggbAdd(oo);
  }

  nodeList.ggbOffsetLeft = function() {
    // "this" is ideally filtered, sorted
    if (this[0]) {
      var el = this[0];
      var sum;
      if (el.getBoundingClientRect) {
        // note: in theory, this branch will always execute
        // in the browsers GeoGebra is targeted to
        sum = el.getBoundingClientRect().left;
      } else {
        // note: the following solution is just a fallback,
        // probably not working right in all browsers in all cases,
        // but this branch will probably never be executed anyway
        sum = el.offsetLeft; // + el.scrollLeft;
        while (el.offsetParent != document.body) {
          el = el.offsetParent;
          sum += el.offsetLeft;
          // TODO: whether we need el.scrollLeft and/or
          // window.scrollX at more places?
        }
      }
      // it is necessary to add scroll position to it;
      // the following should work in theory
      if (window.pageXOffset) {
        sum += window.pageXOffset;
      } else if (document.documentElement && document.documentElement.scrollLeft) {
        sum += document.documentElement.scrollLeft;
      } else if (document.body.parentNode && document.body.parentNode.scrollLeft) {
        sum += document.body.parentNode.scrollLeft;
      } else {
        sum += document.body.scrollLeft;
      }
      return sum;
    }
    return 0;
  }

  nodeList.ggbOuterWidth = function() {
    // "this" is ideally filtered, sorted
    // this method should return the outer width without margins
    if (this[0]) {
      var el = this[0];
      var sum;
      if (el.getBoundingClientRect) {
        // note: in theory, this branch will always execute
        // in the browsers GeoGebra is targeted to
        var tr = el.getBoundingClientRect();
        if (tr.width) {
          sum = tr.width;
        } else if (tr.right) {
          sum = tr.right - tr.left;
        } else {
          sum = el.offsetWidth;
        }
      } else {
        sum = el.offsetWidth;
      }
      return sum;
    }
    return 0;
  }

  nodeList.ggbOuterHeight = function() {
    // "this" is ideally filtered, sorted
    // this method should return the outer height without margins
    var ind = 0;
    while (this[ind] && this[ind].nodeType !== 1) {
      ind++;
    }
    if (this[ind]) {
      var el = this[ind];
      var sum;
      if (el.getBoundingClientRect) {
        // note: in theory, this branch will always execute
        // in the browsers GeoGebra is targeted to
        var tr = el.getBoundingClientRect();
        if (tr.height) {
          sum = tr.height;
        } else if (tr.bottom) {
          sum = tr.bottom - tr.top;
        } else {
          sum = el.offsetHeight;
        }
      } else {
        sum = el.offsetHeight;
      }
      return sum;
    }
    return 0;
  }

  nodeList.ggbHasClass = function(className) {
    var classList;
    for (var i = 0; i < this.length; i++) {
      if (this[i].classList) {
        if (this[i].classList.contains(className)) {
          return true;
        }
      } else if (this[i].className) {// avoid text nodes
        classList = this[i].className.split(" ");
        if (classList.indexOf(className) > -1) {
          return true;
        }
      }
    }
    return false;
  }

  nodeList.ggbDetach = function() {
    // detaching in DOM, but not in "this", neither in return value
    var ret = this;//OurObj
    var thi;
    for (var i = 0; i < ret.length; i++) {
      thi = ret[i];
      if (thi.parentNode) {
        var par = thi.parentNode;
        par.removeChild(thi);
      }
    }
    return ret;
  }

  nodeList.ggbReplaceWith = function(nodelist) {
    // nodelist should be sorted, ideally!
    //var nodelist2 = nodelist.ggbFS();

    var firstFind = true, par, cloned;
    for (var i = 0; i < this.length; i++) {
      if (this[i].parentNode && nodelist.length) {
        par = this[i].parentNode;
        if (firstFind) {
          par.replaceChild(this[i], nodelist[0]);
          for (var j = 1; j < nodelist.length; j++) {
            // one website tells that nextSibling can be null for text nodes,
            // but this information is nowhere else supported,
            // so the following code is probably OK
            par.insertBefore(nodelist[j], nodelist[j-1].nextSibling);
          }
          firstFind = false;
        } else {
          // now it's non-trivial what to do when this is executed more times
          // we will choose to clone nodelist's elements,
          // but the documentation does not write about that.
          cloned = nodelist[0].cloneNode(true);
          par.replaceChild(this[i], cloned);
          for (var j = 1; j < nodelist.length; j++) {
            par.insertBefore(nodelist[j].cloneNode(true), cloned.nextSibling);
            cloned = cloned.nextSibling;//i.e. nodelist[j].cloneNode(true)
          }
        }
      }
    }
    // this method should not change "this"
    // the documentation is also inaccurate when writing about
    // returning the "removed elements"
    return this;
  }

  nodeList.ggbWrapAll = function(nodelist) {
    // nodelist can be obtained by minQuery.ggbHTML()
    // and note that it should ideally contain only
    // one innermost element, which we will get by
    // looping down the firstChild elements
    // is is also assumed that "nodelist" is ggbFS()-d

    // the wrapping nodelist will be at the
    // first element of the wrapped nodeList
    // so we shall assume that "this" is sorted
    var first = this[0];
    if (first.parentNode && nodelist.length > 0) {// TODO: while loop for parentNode!
      var par = first.parentNode;
      par.insertBefore(nodelist[0], first);
      // now get the place of inserting elements of "this"
      var inside = nodelist[0];// or first.previousSibling;
      while (inside.firstElementChild) {
        // firstChild is not good here
        inside = inside.firstElementChild;
      }
      // inside will be the container to put things in
      // but at first, add more elements if necessary
      for (var i = 1; i < nodelist.length; i++) {
        // if nodelist comes from a HTML structure,
        // ggbHTML will only return its outermost elements
        // by the way, we shall assume that "nodelist" is sorted as well
        par.insertBefore(nodelist[i], first);
      }
      // OK, ready to put elements of "this" into "inside"
      for (var j = 0; j < this.length; j++) {
        // appendChild does the following two lines as well
        //par = this[j].parentNode;
        //par.removeChild(this[j]);
        inside.appendChild(this[j]);
      }
    }
    // everything is OK, in theory
    // return value is not specified,
    // but we can return nodelist
    return nodelist;
  }

  nodeList.ggbCSS = function(propName, propValue) {
    // not going to support more pairs {} for ease
    if (propValue) {
      // set CSS of all the elements in "this"
      for (var i = 0; i < this.length; i++) {
        if (this[i].style) {
          this[i].style[propName] = propValue;
        }
      }
    } else if (this[0] && this[0].style) {
      // get CSS of this[0]
      if (window.getComputedStyle) {
        // note: font-size instead of fontSize!
        return window.getComputedStyle(this[0], null).getPropertyValue(propName);
      }
      return this[0].style[propName];
    } else {
      for (var i = 1; i < this.length; i++) {
        if (this[i] && this[i].style) {
          if (window.getComputedStyle) {
            // note: font-size instead of fontSize!
            return window.getComputedStyle(this[i], null).getPropertyValue(propName);
          }
          return this[i].style[propName];
        }
      }
      // CSS of nothing, what could it be?
      return '';
    }
    return this;
  }

  nodeList.ggbText = function() {
    // this is as simple...
    // but note that this will behave imperfectly for
    // script, style, input and textarea nodes;
    // but it does not matter, as we will not use this
    // method for those purposes...
    var tc = "";
    for (var i = 0; i < this.length; i++) {
      if (this[i].textContent) {
        tc += this[i].textContent;
      }
    }
    return tc;
  }

  nodeList.ggbAppend = function(it) {// it is an array
    // "this", "it" ggbFS()-d
    if (this.length && this.length > 1) {
      for (var j = 0; j < this.length; j++) {
        if (it.length) {
          for (var i = 0; i < it.length; i++) {
            if (it[i].cloneNode) {
              this[j].appendChild(it[i].cloneNode(true));
            }
          }
        } else if (it.cloneNode) {
          this[j].appendChild(it.cloneNode(true));
        }
      }
    } else {
      if (it.length) {
        for (var i = 0; i < it.length; i++) {
          this[0].appendChild(it[i]);
        }
      } else {
        this[0].appendChild(it);
      }
    }
    return this;
  }

  nodeList.ggbPrepend = function(it) {// it is an array
    // "this", "it" ggbFS()-d
    if (this.length && this.length > 1) {
      for (var j = 0; j < this.length; j++) {
        if (it.length) {
          for (var i = it.length - 1; i >= 0; i--) {
            if (it[i].cloneNode) {
              this[j].insertBefore(it[i].cloneNode(true), this[j].firstChild);
            }
          }
        } else if (it.cloneNode) {
          this[j].insertBefore(it.cloneNode(true), this[j].firstChild);
        }
      }
    } else {
      if (it.length) {
        for (var i = it.length - 1; i >= 0; i--) {
          this[0].insertBefore(it[i], this[0].firstChild);
        }
      } else {
        this[0].insertBefore(it, this[0].firstChild);
      }
    }
    return this;
  }

  nodeList.ggbInsertBefore = function(par) {
    // par is an array that should not change in this method,
    // and this means that it does not matter in theory
    // if the for loop is increasing or decreasing

    // for min.js, we presuppose that par is distinct from "this"

    // if the members of "this" are already in the DOM,
    // then insertBefore will remove them from their original place in theory

    var ret = new OurObj();
    var greatp = document.createElement('div');

    if (par.length) {
      var parpar = par[0].parentNode;
      if (!parpar) {
        // if par is not attached to the DOM,
        // create a dummy parent node for it
        parpar = greatp;
        parpar.appendChild(par[0]);
      }
      for (var j = 0; j < this.length; j++) {
        parpar.insertBefore(this[j], par[0]);
        ret.push(this[j]);
      }
      ret.push(par[0]);

      var thisclone;
      for (var i = 1; i < par.length; i++) {
        parpar = par[i].parentNode;
        if (!parpar) {
          parpar = greatp;
          parpar.appendChild(par[i]);
        }

        for (var j = 0; j < this.length; j++) {
          if (this[j].cloneNode) {
            thisclone = this[j].cloneNode(true);
            parpar.insertBefore(thisclone, par[i]);
            ret.push(thisclone);
          }
        }
        ret.push(par[i]);
      }
    } else {
      var parpar = par.parentNode;
      if (!parpar) {
        parpar = greatp;
        parpar.appendChild(par);
      }
      for (var j = 0; j < this.length; j++) {
        parpar.insertBefore(this[j], par);
        ret.push(this[j]);
      }
      ret.push(par);
    }

    // based on our understanding of the documentation,
    // ret should do what is compatible
    return ret;
  }

  nodeList.ggbInsertAfter = function(par) {
    // see comment in ggbInsertBefore
    var ret = new OurObj();
    var greatp = document.createElement('div');

    if (par.length) {
      var parpar = par[0].parentNode;
      if (!parpar) {
        // if par is not attached to the DOM,
        // create a dummy parent node for it
        parpar = greatp;
        parpar.appendChild(par[0]);
      }
      ret.push(par[0]);
      for (var j = 0; j < this.length; j++) {
        parpar.insertBefore(this[j], par[0].nextSibling);
        ret.push(this[j]);
      }

      var thisclone;
      for (var i = 1; i < par.length; i++) {
        parpar = par[i].parentNode;
        if (!parpar) {
          // if par is not attached to the DOM,
          // create a dummy parent node for it
          parpar = greatp;
          parpar.appendChild(par[i]);
        }
        ret.push(par[i]);
        for (var j = 0; j < this.length; j++) {
          if (this[j].cloneNode) {
            thisclone = this[j].cloneNode(true);
            parpar.insertBefore(thisclone, par[i].nextSibling);
            ret.push(thisclone);
          }
        }
      }
    } else {
      var parpar = par.parentNode;
      if (!parpar) {
        // if par is not attached to the DOM,
        // create a dummy parent node for it
        parpar = greatp;
        parpar.appendChild(par);
      }
      ret.push(par);
      for (var j = 0; j < this.length; j++) {
        parpar.insertBefore(this[j], par.nextSibling);
        ret.push(this[j]);
      }
    }

    // based on our understanding of the documentation,
    // ret should do what is compatible
    return ret;
  }

  nodeList.ggbAppendTo = function(par) {
    // see comment in ggbInsertBefore
    var ret = new OurObj();

    if (par.length) {
      for (var j = 0; j < this.length; j++) {
        if (par[0].appendChild) {//text nodes?
          par[0].appendChild(this[j]);
        }
      }
      ret.push(par[0]);

      var thisclone;
      for (var i = 1; i < par.length; i++) {
        for (var j = 0; j < this.length; j++) {
          if (this[j].cloneNode) {
            if (par[i].appendChild) {//text nodes?
              par[i].appendChild(this[j].cloneNode(true));
            }
          }
        }
        ret.push(par[i]);
      }
    } else {
      for (var j = 0; j < this.length; j++) {
        if (par.appendChild) {
          par.appendChild(this[j]);
        }
      }
      ret.push(par);
    }

    return ret;
  }

  nodeList.ggbPrependTo = function(par) {
    // see comment in ggbInsertBefore
    var ret = new OurObj();

    if (par.length) {
      for (var j = 0; j < this.length; j++) {
        par[0].insertBefore(this[j], par[0].firstChild);
      }
      ret.push(par[0]);

      var thisclone;
      for (var i = 1; i < par.length; i++) {
        for (var j = 0; j < this.length; j++) {
          if (this[j].cloneNode) {
            par[i].insertBefore(this[j].cloneNode(true), par[i].firstChild);
          }
        }
        ret.push(par[i]);
      }
    } else {
      for (var j = 0; j < this.length; j++) {
        par.insertBefore(this[j], par.firstChild);
      }
      ret.push(par);
    }

    return ret;
  }

  // minQuery main object syntax from here to the end

  minQuery = function (s) {
    // input: Node, NodeList, HTMLCollection, StaticNodeList, Array
    // output: one array-like object to rule them all - OurObj
    var ret = new OurObj();
    if (s && s.length !== undefined) {
      // NodeList, StaticNodeList, HTMLCollection, Array, OurObj
      for (var i = 0; i < s.length; i++) {
        ret.push(s[i]);
      }
    } else if (s) {
      // Node
      ret.push(s);
    }
    // note that no ggbFS() is called here, as ggbFS() calls this method!
    return ret;
  };

  minQuery.ggbArray = function(s) {
    // sometimes we need making an array
    var ret = new Array();
    if (s && s.length !== undefined) {
      // NodeList, StaticNodeList, HTMLCollection, Array, OurObj
      for (var i = 0; i < s.length; i++) {
        ret.push(s[i]);
      }
    } else if (s) {
      // Node
      ret.push(s);
    }
    return ret;
  }

  minQuery.ggbQuery = function(s) {
    // querySelectorAll requires a string with a length
    // otherwise it throws an exception
    var r = document.querySelectorAll(s || '\u263A');
    // however, this method may call ggbFS(),
    // although it's probably unnecessary
    return minQuery(r);//.ggbFS();
  }

  minQuery.ggbHTML = function(s) {
    var cont = document.createElement("div");
    cont.innerHTML = s;
    // ggbFS() is not necessary, content is
    // filtered for duplicates and sorted
    return minQuery(cont).ggbContents();
  }

  // minQuery.on and minQuery.trigger allow for pub/sub type global
  // custom events.
  minQuery.on = node.on.bind(dummy);
  minQuery[trigger] = node[trigger].bind(dummy);

  return minQuery;
})(document, this);

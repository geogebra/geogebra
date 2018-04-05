/*jslint browser: true, nomen: true, plusplus: true */

/// @file coherent.js
/// @namespace engine

/// Coherent UI JavaScript interface.
/// The `engine` module contains all functions for communication between the UI and the game / application.
(function (factory) {
	if (typeof module === 'object' && module.exports) {
		module.exports = factory(global, global.engine, false);
	} else {
		window.engine = factory(window, window.engine, true);
	}
})(function (global, engine, hasOnLoad) {
	'use strict';

	var VERSION = [2, 3, 0, 2];

	/**
	* Event emitter
	*
	* @class Emitter
	*/
	function Emitter() {
		this.events = {};
	}

	function Handler(code, context) {
		this.code = code;
		this.context = context;
	}

	Emitter.prototype._createClear = function (object, name, handler) {
		return function() {
			var handlers = object.events[name];
			if (handlers) {
				var index = -1;
				// this was in native previously
				if(handler === undefined)
				{
					for(var i = 0; i < handlers.length; ++i)
					{
						if(handlers[i].wasInCPP !== undefined)
						{
							index = i;
							break;
						}
					}
				}
				else
				{
					index = handlers.indexOf(handler);
				}
				if (index != -1) {
					handlers.splice(index, 1);
					if (handlers.length === 0) {
						delete object.events[name];
					}
				}
			} else {
				if(engine.RemoveOnHandler !== undefined) {
					engine.RemoveOnHandler(name);
				}
			}
		};
	};

	/// @file coherent.js

	/**
	* Add a handler for an event
	*
	* @method on
	* @param name the event name
	* @param callback function to be called when the event is triggered
	* @param context this binding for executing the handler, defaults to the Emitter
	* @return connection object
	*/
	Emitter.prototype.on = function (name, callback, context) {
		var handlers = this.events[name];
		if (handlers === undefined)
			handlers = this.events[name] = [];

		var handler = new Handler(callback, context || this);
		handlers.push(handler);
		return { clear: this._createClear(this, name, handler) };
	};

	/**
	* Remove a handler from an event
	*
	* @method off
	* @param name the event name
	* @param callback function to be called when the event is triggered
	* @param context this binding for executing the handler, defaults to the Emitter
	* @return connection object
	*/
	Emitter.prototype.off = function (name, handler, context) {
		var handlers = this.events[name];

		if (handlers !== undefined) {
			context = context || this;

			var index;
			var length = handlers.length;
			for (index = 0; index < length; ++index) {
				var reg = handlers[index];
				if (reg.code == handler && reg.context == context) {
					break;
				}
			}
			if (index < length) {
				handlers.splice(index, 1);
				if (handlers.length === 0) {
					delete this.events[name];
				}
			}
		}
		else
		{
			engine.RemoveOnHandler(name);
		}
	};


	var isAttached = engine !== undefined;
	engine = engine || {};
	/// @var engine.isAttached
	/// Indicates whether the script is currently running inside Coherent GT
	engine.isAttached = isAttached;

	/// @var engine.forceEnableMocking
	/// Indicates whether mocking should be enabled despite running inside Coherent GT
	engine.forceEnableMocking = global.engineForceEnableMocking || false;
	
	/// @var engine.IsAttached
	/// [DEPRECATED] Indicates whether the script is currently running inside Coherent GT
	/// @warning This property is deprecated, please use engine.isAttached (with camelCase)
	engine.IsAttached = engine.isAttached;

	engine.onEventsReplayed = null;
	Emitter.prototype.trigger = function(name) {
		var handlers = this.events[name];

		if (handlers !== undefined) {
			var args = Array.prototype.slice.call(arguments, 1);

			handlers.forEach(function (handler) {
				handler.code.apply(handler.context, args);
			});
		}
	};

	Emitter.prototype.merge = function (emitter) {
		var lhs = this.events,
			rhs = emitter.events,
			push = Array.prototype.push,
			events;

		for (var e in rhs) {
			events = lhs[e] = lhs[e] || [];
			push.apply(events, rhs[e]);
		}
	};

	var pending = 'pending';
	var fulfilled = 'fulfilled';
	var broken = 'broken';

	function callAsync(code, context, argument) {
		var async = function () {
			code.call(context, argument);
		};
		setTimeout(async);
	}

	function Promise () {
		this.emitter = new Emitter();
		this.state = pending;
		this.result = null;
	}

	Promise.prototype.resolve = function (result) {
		this.state = fulfilled;
		this.result = result;

		this.emitter.trigger(fulfilled, result);
	};

	Promise.prototype.reject = function (result) {
		this.state = broken;
		this.result = result;

		this.emitter.trigger(broken, result);
	};

	Promise.prototype.success = function (code, context) {
		if (this.state !== fulfilled) {
			this.emitter.on(fulfilled, code, context);
		} else {
			callAsync(code, context || this, this.result);
		}
		return this;
	};

	Promise.prototype.always = function (code, context) {
		this.success(code, context);
		this.otherwise(code, context);
		return this;
	};

	Promise.prototype.otherwise = function (code, context) {
		if (this.state !== broken) {
			this.emitter.on(broken, code, context);
		} else {
			callAsync(code, context || this, this.result);
		}
		return this;
	};

	Promise.prototype.merge = function (other) {
		if (this.state === pending) {
			this.emitter.merge(other.emitter);
		} else {
			var handlers = other.emitter.events[this.state];
			var self = this;
			if (handlers !== undefined) {
				handlers.forEach(function (handler) {
					handler.code.call(handler.context, self.result);
				});
			}
		}
	};

	Promise.prototype.make_chain = function (handler, promise, ok) {
		return function (result) {
			var handlerResult;
			try {
				handlerResult = handler.code.call(handler.context, result);
				if (handlerResult instanceof Promise) {
					handlerResult.merge(promise);
				} else if (this.state === ok) {
					promise.resolve(handlerResult);
				} else {
					promise.reject(handlerResult);
				}
			} catch (error) {
				engine._ThrowError(error);
				promise.reject(error);
			}
		};
	};

	function makeDefaultHandler(promise) {
		return function () {
			return promise;
		};
	}

	Promise.prototype.then = function (callback, errback) {
		var promise = new Promise();

		var handler = new Handler(callback || makeDefaultHandler(this), this);

		this.success(this.make_chain(handler, promise, fulfilled), this);

		var errorHandler = new Handler(errback || makeDefaultHandler(this), this);
		this.otherwise(this.make_chain(errorHandler, promise, broken), this);


		return promise;
	};

	if (!engine.isAttached || engine.forceEnableMocking) {
		Emitter.prototype.on = function (name, callback, context) {
			var handlers = this.events[name];
			if (this.browserCallbackOn) {
				this.browserCallbackOn(name, callback, context);
			}

			if (handlers === undefined) {
				handlers = this.events[name] = [];
			}

			var handler = new Handler(callback, context || this);
			handlers.push(handler);
			return { clear: this._createClear(this, name, handler) };
		};
		Emitter.prototype.off = function (name, handler, context) {
			var handlers = this.events[name];

			if (handlers !== undefined) {
				context = context || this;

				var index;
				var length = handlers.length;
				for (index = 0; index < length; ++index) {
					var reg = handlers[index];
					if (reg.code == handler && reg.context == context) {
						break;
					}
				}
				if (index < length) {
					handlers.splice(index, 1);
					if (handlers.length === 0) {
						delete this.events[name];

						if (this.browserCallbackOff) {
							this.browserCallbackOff(name, handler, context);
						}
					}
				}
			}
		};

		engine.SendMessage = function (name, id) {
			var args = Array.prototype.slice.call(arguments, 2);
			var deferred = engine._ActiveRequests[id];

			delete engine._ActiveRequests[id];
			var call = function () {
				var mock = engine._mocks[name];

				if (mock !== undefined) {
					deferred.resolve(mock.apply(engine, args));
				}
			};

			window.setTimeout(call, 16);
		};

		engine.TriggerEvent = function () {
			var args = Array.prototype.slice.call(arguments);

			var trigger = function () {
				var mock = engine._mocks[args[0]];

				if (mock !== undefined) {
					mock.apply(engine, args.slice(1));
				}
			};
			window.setTimeout(trigger, 16);
		};

		engine.BindingsReady = function () {
			engine._OnReady();
		};

		engine.__observeLifetime = function () {
		};

		engine.beginEventRecording =
		engine.endEventRecording =
		engine.saveEventRecord = function() {
			console.warning("Event recording will not work in the browser!");
		};

		engine._mocks = {};
		engine._mockImpl = function (name, mock, isCppCall, isEvent) {
			if (mock && isCppCall) {
				this._mocks[name] = mock;
			}
			// Extract the name of the arguments from Function.prototype.toString
			var functionStripped = mock.toString().replace("function " + mock.name + "(", "");
			var rightParanthesis = functionStripped.indexOf(")");
			var args = functionStripped.substr(0, rightParanthesis);
			if (this.browserCallbackMock) {
				this.browserCallbackMock(name,
										 args,
										 isCppCall,
										 Boolean(isEvent));
			}
		}
		engine.mock = function (name, mock, isEvent) {
			this._mockImpl(name, mock, true, isEvent);
		};
		// Mock the call to translate to always return the key
		engine.translate = function (text) {
			return text;
		};
	}

	engine.events = {};
	for (var property in Emitter.prototype) {
		engine[property] = Emitter.prototype[property];
	}

	if (engine.isAttached && !engine.forceEnableMocking) {
		engine.on = function (name, callback, context) {
			var handlers = this.events[name];

			if (handlers === undefined && engine.AddOrRemoveOnHandler !== undefined) {
				// Check where to cache the handler
				var prevEvent = engine.AddOrRemoveOnHandler(name, callback, context);

				// handler cached in C++
				if(prevEvent === undefined) {
					return { clear: this._createClear(this, name, undefined) };
				}

				handlers = this.events[name] = [];

				// Add the previous handler
				var prevHandler = new Handler(prevEvent[0], prevEvent[1] || this);
				prevHandler.wasInCPP = true;
				handlers.push(prevHandler);
			} else if (handlers === undefined) {
				handlers = this.events[name] = [];
			}

			var handler = new Handler(callback, context || this);
			handlers.push(handler);
			return { clear: this._createClear(this, name, handler) };
		}
	}

	/// @function engine.on
	/// Register handler for and event
	/// @param {String} name name of the event
	/// @param {Function} callback callback function to be executed when the event has been triggered
	/// @param context *this* context for the function, by default the engine object

	/// @function engine.beginEventRecording
	/// Begins recording all events triggered using View::TriggerEvent from the game

	/// @function engine.endEventRecording
	/// Ends event recording

	/// @function engine.saveEventRecord
	/// Saves the events recorded in between the last calls to engine.beginEventRecording and engine.endEventRecording to a file
	/// @param {String} path The path to the file where to save the recorded events. Optional. Defaults to "eventRecord.json"

	/// @function engine.replayEvents
	/// Replays the events previously recorded and stored in path. If you need to be notified when all events
	/// are replayed, assign a callback to engine.onEventsReplayed
	/// @param {Number} timeScale The speed at which to replay the events (e.g. pass 2 to double the speed). Optional. Defaults to 1.
	/// @param {String} path The path to the file the recorded events are stored. Optional. Defaults to "eventRecord.json"

	/// @function engine.translate
	/// Translates the given text by invoking the system's localization manager if one exists.
	/// @param {text} text The text to translate.
	/// @return {String} undefined if no localization manager is set or no translation exists, else returns the translated string

	/// @function engine.reloadLocalization
	/// Updates the text on all elements with the data-l10n-id attribute by calling engine.translate

	/// @function engine.off
	/// Remove handler for an event
	/// @param {String} name name of the event, by default removes all events
	/// @param {Function} callback the callback function to be removed, by default removes all callbacks for a given event
	/// @param context *this* context for the function, by default all removes all callbacks, regardless of context
	/// @warning Removing all handlers for `engine` will remove some *Coherent UI* internal events, breaking some functionality.

	/// @function engine.trigger
	/// Trigger an event
	/// This function will trigger any C++ handler registered for this event with `Coherent::UI::View::RegisterForEvent`
	/// @param {String} name name of the event
	/// @param ... any extra arguments to be passed to the event handlers

	engine._trigger = Emitter.prototype.trigger;
	var concatArguments = Array.prototype.concat;
	engine.trigger = function (name) {
		this._trigger.apply(this, arguments);
		this.TriggerEvent.apply(this, arguments);

		if (this.events['all'] !== undefined) {
			var allArguments = concatArguments.apply(['all'], arguments);
			this._trigger.apply(this, allArguments);
		}
	};
	/// @function engine.showOverlay
	/// Shows the debugging overlay in the browser.
	/// Will also work in Coherent GT only if *engineForceEnableMocking* is set to *true*.
	engine.showOverlay = function () {};


	/// @function engine.hideOverlay
	/// Hides the debugging overlay in the browser.
	/// Will also work in Coherent GT only if *engineForceEnableMocking* is set to *true*.
	engine.hideOverlay = function () {};

	/// @function engine.mock
	/// Mocks a C++ function call with the specified function.
	/// Will also work in Coherent GT only if *engineForceEnableMocking* is set to *true*.
	/// @param {String} name name of the event
	/// @param {Function} mock a function to be called in-place of your native binding
	/// @param {Boolean} isEvent whether you are mocking an event or function call
	if (engine.isAttached && !engine.forceEnableMocking) {
		engine.mock = function (name, mock, isEvent) { };
	}

	engine._BindingsReady = false;
	engine._WindowLoaded = false;
	engine._RequestId = 0;
	engine._ActiveRequests = {};

	/// @function engine.createDeferred
	/// Create a new deferred object.
	/// Use this to create deferred / promises that can be used together with `engine.call`.
	/// @return {Deferred} a new deferred object
	/// @see @ref CustomizingPromises
	engine.createDeferred = (global.engineCreateDeferred === undefined) ?
		function () { return new Promise(); }
		: global.engineCreateDeferred;

	/// @function engine.call
	/// Call asynchronously a C++ handler and retrieve the result
	/// The C++ handler must have been registered with `Coherent::UI::View::BindCall`
	/// @param {String} name name of the C++ handler to be called
	/// @param ... any extra parameters to be passed to the C++ handler
	/// @return {Deferred} deferred object whose promise is resolved with the result of the C++ handler
	engine.call = function () {
		engine._RequestId++;
		var id = engine._RequestId;

		var deferred = engine.createDeferred();
		engine._ActiveRequests[id] = deferred;
		var messageArguments = Array.prototype.slice.call(arguments);
		messageArguments.splice(1, 0, id);
		engine.SendMessage.apply(this, messageArguments);
		return deferred;
	};

	engine._Result = function (requestId) {
		var deferred = engine._ActiveRequests[requestId];
		if (deferred !== undefined)
		{
			delete engine._ActiveRequests[requestId];

			var resultArguments = Array.prototype.slice.call(arguments);
			resultArguments.shift();
			deferred.resolve.apply(deferred, resultArguments);
		}
	};

	engine._Errors = [ 'Success', 'ArgumentType', 'NoSuchMethod', 'NoResult' ];

	engine._ForEachError = function (errors, callback) {
		var length = errors.length;

		for (var i = 0; i < length; ++i) {
			callback(errors[i].first, errors[i].second);
		}
	};

	engine._MapErrors = function (errors) {
		var length = errors.length;

		for (var i = 0; i < length; ++i) {
			errors[i].first = engine._Errors[errors[i].first];
		}
	};

	engine._TriggerError = function (type, message) {
		engine.trigger('Error', type, message);
	};

	engine._OnError = function (requestId, errors) {
		engine._MapErrors(errors);

		if (requestId === null || requestId === 0) {
			engine._ForEachError(errors, engine._TriggerError);
		}
		else {
			var deferred = engine._ActiveRequests[requestId];

			delete engine._ActiveRequests[requestId];

			deferred.reject(errors);
		}
	};

	engine._eventHandles = {};

	engine._Register = function (eventName) {
		var trigger = (function (name, engine) {
			return function () {
				var eventArguments = [name];
				eventArguments.push.apply(eventArguments, arguments);
				engine.TriggerEvent.apply(this, eventArguments);
			};
		}(eventName, engine));

		engine._eventHandles[eventName] = engine.on(eventName, trigger);
	};

	engine._removeEventThunk = function (name) {
		var handle = engine._eventHandles[name];
		handle.clear();
		delete engine._eventHandles[name];
	};

	engine._Unregister = function (name) {
		if (typeof name === 'string') {
			engine._removeEventThunk(name);
		} else {
			name.forEach(engine._removeEventThunk, engine);
		}
	};

	function createMethodStub(name) {
		var stub = function() {
			var args = Array.prototype.slice.call(arguments);
			args.splice(0, 0, name, this._id);
			return engine.call.apply(engine, args);
		};
		return stub;
	}

	engine._boundTypes = {};

	engine._createInstance = function (args) {
		var type = args[0],
			id = args[1],
			methods = args[2],
			constructor = engine._boundTypes[type];

		if (constructor === undefined) {
			constructor = function (id) {
				this._id = id;
			};
			constructor.prototype.__Type = type;
			methods.forEach(function (name) {
				constructor.prototype[name] = createMethodStub(type + '_' + name);
			});
			engine._boundTypes[type] = constructor;
		}

		var instance = new constructor(id);
		engine.__observeLifetime(instance);
		return instance;
	}

	engine._OnReady = function () {
		engine._BindingsReady = true;
		if (engine._WindowLoaded) {
			engine.trigger('Ready');
		}
	};

	engine._OnWindowLoaded = function () {
		engine._WindowLoaded = true;
		if (engine._BindingsReady) {
			engine.trigger('Ready');
		}
	};

	engine._ThrowError = function (error) {
		var prependTab = function (s) { return "\t" + s; };
		var errorString = error.name + ": " + error.message + "\n" +
						  error.stack.split("\n").map(prependTab).join("\n");
		console.error(errorString);
	};

	if (hasOnLoad) {
		global.addEventListener("load", function () {
			engine._OnWindowLoaded();
		});
	} else {
		engine._WindowLoaded = true;
	}

	engine._coherentGlobalCanvas = document.createElement('canvas');
	engine._coherentGlobalCanvas.id     = "coherentGlobalCanvas";
	engine._coherentGlobalCanvas.width  = 1;
	engine._coherentGlobalCanvas.height = 1;
	engine._coherentGlobalCanvas.style.zIndex   = 0;
	engine._coherentGlobalCanvas.style.position = "absolute";
	engine._coherentGlobalCanvas.style.border   = "0px solid";

	engine._coherentLiveImageData = new Array();
	engine._coherentCreateImageData = function(name, guid) {
		var ctx = engine._coherentGlobalCanvas.getContext("2d");

		var coherentImage = ctx.coherentCreateImageData(guid);
		engine._coherentLiveImageData[name] = coherentImage;
	}
	engine._coherentUpdatedImageData = function(name) {
		engine._coherentLiveImageData[name].coherentUpdate();
		var canvases = document.getElementsByTagName('canvas');
		for(var i = 0; i < canvases.length; ++i) {
			if(canvases[i].onEngineImageDataUpdated != null) {
				canvases[i].onEngineImageDataUpdated(name,
					engine._coherentLiveImageData[name]);
			}
		}
	}

	engine.reloadLocalization = function () {
		var localizedElements = document.querySelectorAll('[data-l10n-id]');
		for (var i = 0; i < localizedElements.length; i++) {
			var element = localizedElements.item(i);
			var translated = engine.translate(element.dataset.l10nId);
			if (!translated) {
				var warning = "Failed to find translation for key: " + element.dataset.l10nId;
				console.warn(warning);
			} else {
				element.textContent = translated;
			}
		}
	};

	engine.on("_coherentCreateImageData", engine._coherentCreateImageData);
	engine.on("_coherentUpdatedImageData", engine._coherentUpdatedImageData);

	engine.on('_Result', engine._Result, engine);
	engine.on('_Register', engine._Register, engine);
	engine.on('_Unregister', engine._Unregister, engine);
	engine.on('_OnReady', engine._OnReady, engine);
	engine.on('_OnError', engine._OnError, engine);

	engine.on('__OnReplayRecordCompleted', function(jsonRecords) {
		if (engine.onEventsReplayed) {
			engine.onEventsReplayed();
		}
	});

	engine.BindingsReady(VERSION[0], VERSION[1], VERSION[2], VERSION[3]);

	return engine;
});


/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
window.__MODULE_FUNC__ = function() {
  var $wnd = __WINDOW_DEF__;
  var $doc = __DOCUMENT_DEF__;

  /****************************************************************************
   * Internal Helper Functions
   ***************************************************************************/

  function isHostedMode() {
    var query = $wnd.location.search;
    return ((query.indexOf('gwt.codesvr.__MODULE_NAME__=') != -1) ||
            (query.indexOf('gwt.codesvr=') != -1));
  }

  // Helper function to send statistics to the __gwtStatsEvent function if it
  // exists.
  function sendStats(evtGroupString, typeString) {
    if ($wnd.__gwtStatsEvent) {
      $wnd.__gwtStatsEvent({
        moduleName: '__MODULE_NAME__',
        sessionId: $wnd.__gwtStatsSessionId,
        subSystem: 'startup',
        evtGroup: evtGroupString,
        millis:(new Date()).getTime(),
        type: typeString,
      });
    }
  }


  /****************************************************************************
   * Exposed Functions and Variables
   ***************************************************************************/
  // These are set by various parts of the bootstrapping code, but they always
  // need to exist, so give them all default values here.

  // Exposed for the convenience of the devmode.js and md5.js files
  window.__MODULE_FUNC__.__sendStats = sendStats;

  // Exposed for the call made to gwtOnLoad. Some are not figured out yet, so
  // assign them later, once the values are known.
  window.__MODULE_FUNC__.__moduleName = '__MODULE_NAME__';
  window.__MODULE_FUNC__.__errFn = null;
  window.__MODULE_FUNC__.__moduleBase = 'DUMMY';
  window.__MODULE_FUNC__.__softPermutationId = 0;

  // Exposed for devmode.js
  window.__MODULE_FUNC__.__computePropValue = null;
  // Exposed for super dev mode
  window.__MODULE_FUNC__.__getPropMap = null;

  // Exposed for runAsync
  window.__MODULE_FUNC__.__installRunAsyncCode = function() {};
  window.__MODULE_FUNC__.__gwtStartLoadingFragment = function() { return null; };

  // Exposed for property provider code
  window.__MODULE_FUNC__.__gwt_isKnownPropertyValue = function() { return false; };
  window.__MODULE_FUNC__.__gwt_getMetaProperty = function() { return null; };

  // Exposed for permutations code
  var __propertyErrorFunction = null;


  // Set up our entry in the page-wide registry of active modules.
  // It must be set up before calling computeScriptBase() and
  // getCompiledCodeFilename().
  var activeModules =
      ($wnd.__gwt_activeModules = ($wnd.__gwt_activeModules || {}));
  activeModules["__MODULE_NAME__"] = {moduleName: "__MODULE_NAME__"};

  window.__MODULE_FUNC__.__moduleStartupDone = function(permProps) {
    // Make embedded properties available to Super Dev Mode.
    // (They override any properties already exported.)
    var oldBindings = activeModules["__MODULE_NAME__"].bindings;
    activeModules["__MODULE_NAME__"].bindings = function() {
      var props = oldBindings ? oldBindings() : {};
      var embeddedProps = permProps[window.__MODULE_FUNC__.__softPermutationId];
      for (var i = 0; i < embeddedProps.length; i++) {
        var pair = embeddedProps[i];
        props[pair[0]] = pair[1];
      }
      return props;
    };
  };

  /****************************************************************************
   * Internal Helper functions that have been broken out into their own .js
   * files for readability and for easy sharing between linkers.  The linker
   * code will inject these functions in these placeholders.
   ***************************************************************************/
  // Provides getInstallLocationDoc() function.
  __INSTALL_LOCATION__

  // Installs the script directly, by simply appending a script tag with the
  // src set to the correct location to the install location.
  function installScript(filename) {
    // Provides the setupWaitForBodyLoad()function
    __WAIT_FOR_BODY_LOADED__

    function installCode(code) {
      var doc = getInstallLocationDoc();
      var docbody = doc.body;
      var script = doc.createElement('script');
      script.language='javascript';
      script.crossOrigin='';
      script.src = code;
      if (window.__MODULE_FUNC__.__errFn) {
        script.onerror = function() {
          window.__MODULE_FUNC__.__errFn('__MODULE_FUNC__', new Error("Failed to load " + code));
        }
      }
      docbody.appendChild(script);
    }

    // Just pass along the filename so that a script tag can be installed in the
    // iframe to download it.  Since we will be adding the iframe to the body,
    // we still need to wait for the body to load before going forward.
    setupWaitForBodyLoad(function() {
      installCode(filename);
    });
  }


  // Sets the *.__installRunAsyncCode and
  // *.__startLoadingFragment functions
  window.__MODULE_FUNC__.__startLoadingFragment = function(fragmentFile) {
    return computeUrlForResource(fragmentFile);
  };

  window.__MODULE_FUNC__.__installRunAsyncCode = function(code) {
    var doc = getInstallLocationDoc();
    var docbody = doc.body;
    var script = doc.createElement('script');
    script.text = code;
    docbody.appendChild(script);

    // Unless we're in pretty mode, remove the tags to shrink the DOM a little.
    // It should have installed its code immediately after being added.
    docbody.removeChild(script);
  }

  // Provides the computeScriptBase() function
  function computeScriptBase() {
   function getDirectoryOfFile(path) {
      // Truncate starting at the first '?' or '#', whichever comes first.
      var hashIndex = path.lastIndexOf('#');
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf('?');
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf('/', Math.min(queryIndex, hashIndex));
      return (slashIndex >= 0) ? path.substring(0, slashIndex + 1) : '';
    }
    return getDirectoryOfFile(import.meta.url);
  }

  // Provides the computeUrlForResource() function
  function computeUrlForResource(resource) {
    /* return an absolute path unmodified */
    if (resource.match(/^\//)) {
      return resource;
    }
    /* return a fully qualified URL unmodified */
    if (resource.match(/^[a-zA-Z]+:\/\//)) {
      return resource;
    }
    return window.__MODULE_FUNC__.__moduleBase + resource;
  }

  // Provides the getCompiledCodeFilename() function
  function getCompiledCodeFilename() {
    // Default to 0, as the strongName for permutation 0 does not include a ":0" suffix
    // for backwards compatibility purposes (@see PermutationsUtil::addPermutationsJs).
    var softPermutationId = 0;
    var strongName;

    try {
      // __PERMUTATIONS_BEGIN__
      // Permutation logic is injected here. this code populates the
      // answers variable.
      // __PERMUTATIONS_END__
      var idx = strongName.indexOf(':');
      if (idx != -1) {
        softPermutationId = parseInt(strongName.substring(idx + 1), 10);
        strongName = strongName.substring(0, idx);
      }
    } catch (e) {
      // intentionally silent on property failure
    }
    window.__MODULE_FUNC__.__softPermutationId = softPermutationId;
    return computeUrlForResource(strongName + '.cache.js');
  }

  /****************************************************************************
   * Bootstrap startup code
   ***************************************************************************/

  // Must be set before getCompiledFilename() is called
  window.__MODULE_FUNC__.__moduleBase = computeScriptBase();
  activeModules["__MODULE_NAME__"].moduleBase = window.__MODULE_FUNC__.__moduleBase;

  // Must be done right before the "bootstrap" "end" stat is sent
  var filename = getCompiledCodeFilename();

  installScript(filename);

  return true; // success
}

window.__MODULE_FUNC__.submodules = {};
window.__MODULE_FUNC__.onReady = function(submodule, userRender) {
  function beforeRender(options, onload) {
     return new Promise(resolve => {
       __BEFORE_RENDER__
     });
  }

  const render = (options, onload) => {
    beforeRender(options, onload).then(opts => userRender(opts, onload))
  }

  for (let callback of window.__MODULE_FUNC__.submodules[submodule].callbacks) {
    callback(render);
  }
  window.__MODULE_FUNC__.submodules[submodule].render = render;
}

window.__MODULE_FUNC__.succeeded = window.__MODULE_FUNC__();

function Widget(options, submodule, baseTag)  {
  const self = this;
  self.loading = false;
  this.apiCallbacks = [api => self.api = api];

  function runCallbacks(api) {
    for (const callback of self.apiCallbacks) {
      callback(api);
    }
    if (options.removePreview) {
      options.removePreview();
    }
  }

  function load() {
    self.loading = true;
    if (submodule.render) {
      submodule.render(options, runCallbacks);
    } else {
      submodule.callbacks.push(render => render(options, runCallbacks));
    }
  }

  this.inject = function(element) {
    const target = document.createElement(baseTag);
    options.element = target;
    element.appendChild(target);
    load();
    return this;
  }

  this.getAPI = function() {
    return new Promise(resolve => {
      if (self.api) {
        resolve(self.api);
      } else if (self.loading) {
        self.apiCallbacks.push(resolve);
      } else {
        load(resolve);
      }
    });
  }

  if (options.tagName || options.element) {
    load();
  }
}

const createSubmoduleAPI = (submodule, baseTag) => {
  window.__MODULE_FUNC__.submodules[submodule] = {callbacks:[]};
  return {
    create: (options) => {
      return new Widget(options || {}, window.__MODULE_FUNC__.submodules[submodule], baseTag);
    }
  }
};
// add export statements
__EXPORT_SUBMODULES__
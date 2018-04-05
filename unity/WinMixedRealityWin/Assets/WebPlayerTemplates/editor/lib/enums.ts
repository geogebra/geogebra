/**
 * Editor enums
 * @readonly
 * @module lib/enums
 * @export lib/enums.Enums
 */

export default {
    /**
     * @property {string} animation-class-prefix
     */
    'animationClassPrefix': '',
    /**
     * @member newScene
     * @property {string}  _DOMId - id of the scene
     * @property {object}  sceneSize  - contains size of the scene
     * @property {object}  sceneEvents - contains scene events
     * @property {array}  scripts - contains external scripts paths
     * @property {array}  styles  - contains external style paths
     * @property {array}  widgets - contains scene widgets
     * @property {array}  animations - contains widgets animations
     * @property {object} style - contains styles properties for the scene
     * @alias Enums.newScene
     */
    'newScene': {
        '_DOMId': 'scene',
        'sceneSize': {
            'width': '1920px',
            'height': '1080px',
            'type': 'aspectRatio16_9_full_hd'
        },
        'deps': [],
        'sceneEvents': {
            'sceneLoad': ''
        },
        'scripts': [],
        'styles': [],
        'fonts': [],
        'widgets': [],
        'animations': {},
        'animationClasses': {},
        'style': {
            'backgroundColor': 'transparent'
        }
    },
    /**
     * @member newAnimation
     * @property {object} keyframes
     * @property {object} animationsData
     * @property {array} labels
     * @property {string} className
     * @property {object} events
     */
    'newAnimation': {
        'keyframes': {},
        'animationsData': {},
        'labels': [],
        'className': '',
        'events': {
            'onEnd': '',
            'onStart': '',
            'onPlay': ''
        }
    },
    /**
     * @property {string} preview
     * @property {string} endlessPreview
     */
    'animationPreviewType': {
        'preview': 'preview',
        'endlessPreview': 'endlessPreview'
    },
    /**
     * @property {number}  p
     * @property {number}  s
     * @property {number}  z
     * @property {number}  alt
     * @property {number}  ctrl
     * @property {number}  enter
     * @property {number}  delete
     * @property {number}  rightMouseClick
     * @property {number}  f7
     * @property {number}  f8
     * @property {number}  t
     */
    'Keys': {
        'esc': 27,
        'p': 80,
        's': 83,
        'z': 90,
        'alt': 18,
        'ctrl': 17,
        'enter': 13,
        'delete': 46,
        'rightMouseClick': 3,
        'f7': 118,
        'f8': 119,
        't': 84,
        'w': 87,
        'left': 37,
        'up': 38,
        'right': 39,
        'down': 40,
        'tab': 9,
        'c': 67,
        'v': 86,
        'space': 32,
        'zero': 48,
        'nine': 57,
        'shift': 16
    },

    /**
     * @member Backends
     * @property {String} Standalone
     * @property {String} Debug
     * @property {String} Unreal
     * @property {String} Website
     * @property {String} Hummingbird
     */
    'Backends': {
        'Standalone-2': 'Standalone-2',
        'Standalone': 'Standalone',
        'Debug': 'Debug',
        'Unreal': 'Unreal',
        'Website': 'Website',
        'Hummingbird': 'Hummingbird'
    },
    /**
     * @member FilterProperties
     */
    'FilterProperties': [
        'blur', 'grayscale', 'sepia', 'brightness', 'contrast',
        'hue-rotate', 'invert', 'saturate', 'opacity', 'drop-shadow'
    ],

    'nonDisplayInheritElements': ['rectangle', 'roundedRect', 'circle', 'ellipse', 'button', 'div'],
    /**
     * @member SetPropertyFnNames
     */
    'SetPropertyFnNames': {
        'geometry': '_setGeometry',
        'transform': '_setTransform',
        '-webkit-filter': '_setFilter',
        'filter': '_setFilter',
        'box-shadow': '_setBoxShadow',
        'boxShadow': '_setBoxShadow',
        'backgroundColor': '_setBackgroundColor',
        'color': '_setColor',
        'borderColor': '_setBorderColor',
        'font': '_setFonts',
        'styles': '_setStyles'
    },
    /**
     * @member -webkit-filterDefaultsValues
     */
    '-webkit-filterDefaultValues': {
        'blur': 0,
        'grayscale': 0,
        'sepia': 0,
        'brightness': 1,
        'contrast': 1,
        'hue-rotate': 0,
        'invert': 0,
        'saturate': 1,
        'opacity': 1,
        'dropShadowBlur': 0,
        'dropShadowX': 0,
        'dropShadowY': 0,
        'dropShadowColor': 'rgb(0,0,0)'
    },
    /**
     *  @member boxShadowDefaultsValues
     */
    'boxShadowDefaultValues': {
        'horizontalLength': 0,
        'verticalLength': 0,
        'blurRadius': 0,
        'spreadRadius': 0,
        'insetOutset': 'remove',
        'boxShadowColor': 'rgb(0,0,0)'
    },
    /**
     * @member WidgetGroups
     */
    'WidgetGroups': {
        'width': 'geometry',
        'top': 'geometry',
        'left': 'geometry',
        'height': 'geometry',
        'color': 'styles',
        'borderColor': 'styles',
        'rotate': 'transform',
        'filterProperties': '-webkit-filter',
        'boxShadowProperties': 'boxShadow',
        'zIndex': 'styles',
        'opacity': 'styles',
        'backgroundPositionX': 'styles',
        'backgroundPositionY': 'styles',
        'backgroundSize': 'styles',
        'borderWidth': 'styles',
        'borderTopLeftRadius': 'styles',
        'borderTopRightRadius': 'styles',
        'borderBottomLeftRadius': 'styles',
        'borderBottomRightRadius': 'styles',
        'fontSize': 'font',
        'fontStyle': 'font',
        'fontWeight': 'font',
        'box-shadow': 'boxShadow',
        'background-color': 'backgroundColor',
        'backgroundColor': 'backgroundColor',
        'webkitMaskPositionX': 'styles',
        'webkitMaskPositionY': 'styles',
        '-webkit-mask-size': 'styles',
        '-webkit-mask-position-x': 'styles',
        '-webkit-mask-position-y': 'styles',
        'webkitMaskSize': 'styles',
        'rotateX': 'transform',
        'rotateY': 'transform',
        'rotateZ': 'transform',
        'translateX': 'transform',
        'translateY': 'transform',
        'translateZ': 'transform',
        'scaleX': 'transform',
        'scaleY': 'transform',
        'scaleZ': 'transform',
        'skewX': 'transform',
        'skewY': 'transform',
        'perspective': 'styles',
        'transform-origin': 'transform-origin',
        'perspective-origin': 'styles'
    },
    'Messages': {
        'overwriteWidgetAnimations': 'The selected widget contains animation classes that already ' +
        'exist in the main scene. Would you like to merge them?',
        'duplicateAnimationsTabToTab': 'The selection you are about to paste contains animation ' +
        'classes that already exist in this scene. Please rename them in the original tab before pasting them here.',
        'cannotSavePublishPage': 'Unable to save file!\n' +
        '\nThe published file must be in the same directory as the working file and with a different name.',
        'duplicationFileName': 'Unable to save file!\n' +
        '\nThe published file must be with a different name from the original file.',
        'cannotFindPublishPage': 'Cannot find working file. Please verify that the original working file is in the ' +
        'same directory as the published file.',
        'widgetConvertSuccess': 'Widget successfully converted to GT Component!'
    },
    /**
     * @member Links
     */
    'Links': {
        'documentation': 'https://coherent-labs.com/editor/documentation/',
        'tutorials': 'http://coherent-labs.com/tutorials/',
        'communityForum': 'https://forums.coherent-labs.com/index.php/board,37.0.html',
        'roadmap': 'https://trello.com/b/frsFI7Di/coherent-editor-roadmap'

    },
    /**
     * @member EventTypes
     */
    'EventTypes': ['click', 'dblclick', 'mouseenter', 'mouseover',
        'mouseleave', 'mousemove', 'mousedown', 'mouseup', 'mousewhell',
        'change', 'scroll', 'focus', 'focusin', 'focusout', 'keypress',
        'keydown', 'keyup', 'touchend', 'touchmove', 'touchstart'
    ],
    /**
     * @member DataBindingTypes
     */
    'DataBindingTypes': ['bindClass', 'bindClassToggle', 'bindStyleLeft', 'bindStyleTop',
        'bindStyleOpacity', 'bindStyleWidth', 'bindStyleHeight',
        'bindStyleColor', 'bindStyleBackgroundColor', 'bindFor', 'bindValue',
        'bindModel', 'bindTemplate', 'bindBackgroundImageUrl',
        'bindTransform2d'
    ],
    /**
     * @member DataBindingGroups
     */
    'DataBindingGroups': {
        'bindClassToggle': 'data-bind-class-toggle',
        'bindStyleLeft': 'data-bind-style-left',
        'bindStyleTop': 'data-bind-style-top',
        'bindStyleOpacity': 'data-bind-style-opacity',
        'bindStyleWidth': 'data-bind-style-width',
        'bindStyleHeight': 'data-bind-style-height',
        'bindStyleColor': 'data-bind-style-color',
        'bindStyleBackgroundColor': 'data-bind-style-background-color',
        'bindFor': 'data-bind-for',
        'bindValue': 'data-bind-value',
        'bindModel': 'data-bind-model',
        'bindTemplate': 'data-bind-template',
        'bindClass': 'data-bind-class',
        'bindBackgroundImageUrl': 'data-bind-background-image-url',
        'bindTransform2d': 'data-bind-transform2d'
    },
    /**
     * @member TransformTypes
     */
    'TransformTypes': ['rotateX', 'rotateY', 'rotateZ', 'translateX',
        'translateY', 'translateZ', 'scaleX', 'scaleY', 'scaleZ',
        'skewX', 'skewY', 'transform-origin', 'transform-origin-x',
        'transform-origin-y', 'perspective-origin', 'perspective-origin-x',
        'perspective-origin-y'
    ],
    /**
     * @member TransformDefaultValue
     */
    'TransformDefaultValue': {
        'rotate': '0',
        'rotateX': '0deg',
        'rotateY': '0deg',
        'rotateZ': '0deg',
        'translateX': '0px',
        'translateY': '0px',
        'translateZ': '0px',
        'scaleX': '1.0',
        'scaleY': '1.0',
        'scaleZ': '1.0',
        'skewX': '0deg',
        'skewY': '0deg',
        'perspective': '0px',
        'transform-origin-x': '50%',
        'transform-origin-y': '50%',
        'perspective-origin-x': '50%',
        'perspective-origin-y': '50%'
    },
    /**
     * @member TransformOrder
     */
    'TransformsOrder': [
        'translateX',
        'translateY',
        'translateZ',
        'rotateX',
        'rotateY',
        'rotateZ',
        'skewX',
        'skewY',
        'scaleX',
        'scaleY',
        'scaleZ'
    ],
    /**
     * @member {Object} SavePropertiesTypes
     * @property {String} Events
     * @property {String} Events.Engine
     * @property {String} Events.Engine.call
     * @property {String} Events.Engine.trigger
     * @property {String} Events.Engine.blueprint
     * @property {String} Events.Local
     * @property {String} Events.Local.javascript
     * @property {String} Events.Local.modular
     * @property {String} Events.Local.sceneLoad
     * @property {String} Events.Local.empty
     *
     */
    'SavePropertiesTypes': {
        'Events': {
            'Engine': {
                'call': 'engineCallArguments',
                'trigger': 'engineTriggerArguments',
                'blueprint': 'blueprintFunction'
            },
            'Local': {
                'javascript': 'javascriptFunction',
                'modular': 'events',
                'sceneLoad': 'sceneLoad',
                'empty': 'remove event'
            }
        }
    },
    /**
     * Total height in pixels of the assetsLibraryLabelSize tab Labels
     * @member assetsLibraryLabelSize
     */
    'assetsLibraryLabelSize': 37,
    /**
     * Total height in pixels of the assetsLibraryPreview tab Labels
     * @member assetLibraryPreviewSize
     */
    'assetLibraryPreviewSize': 245,
    /**
     * Total height in pixels of the assetsLibrary tab Labels
     * @member assetLabelsEntryHeight
     */
    'assetLabelsEntryHeight': 395,
    /**
     * Total height in pixels of an asset entry
     * @member assetEntryHeight
     */
    'assetEntryHeight': 34,
    /**
     * All elements that don't have border style property panel
     * @type {Array}
     */
    'noBorderPropertyElements': ['inputText', 'label', 'range', 'number', 'radio',
        'checkbox', 'responsiveImage', 'image', 'text', 'option', 'select', 'ul',
        'ol', 'li', 'flexbox', 'flexboxChild', 'widget'],
    /**
     * All all properties not allowed for animation
     * @type {Object}
     */
    'notAllowedForAnimation': {
        'fontSize': 'auto'
    },
    /**
     * Style types
     * @type {ISaveType}
     */
    'StyleTypes': {
        'geometry': 'geometry',
        'transform': 'transform',
        '-webkit-filter': '-webkit-filter',
        'boxShadow': 'boxShadow',
        'backgroundColor': 'backgroundColor',
        'styles': 'styles',
        'font': 'font',
        'units': 'units'
    },
    'StylePropToKeyframeProp': {
        '-webkit-filter': {
            'blur': 'blur',
            'grayscale': 'grayscale',
            'sepia': 'sepia',
            'brightness': 'brightness',
            'contrast': 'contrast',
            'hue-rotate': 'hue-rotate',
            'invert': 'invert',
            'saturate': 'saturate',
            'opacity': 'opacity',
            'dropShadowBlur': 'dropShadowBlur',
            'dropShadowX': 'dropShadowX',
            'dropShadowY': 'dropShadowY',
            'dropShadowColor': 'dropShadowColor'
        },
        'boxShadow': {
            'horizontalLength': 'boxShadowProperties',
            'verticalLength': 'boxShadowProperties',
            'blurRadius': 'boxShadowProperties',
            'spreadRadius': 'boxShadowProperties',
            'insetOutset': 'boxShadowProperties',
            'color': 'boxShadowProperties',
        },
        'styles': {
            'zIndex': 'zIndex',
            'opacity': 'opacity',
            'backgroundColor': 'backgroundColor',
            'background': 'backgroundColor',
            '-webkit-mask-position-x': '-webkit-mask-position-x',
            '-webkit-mask-position-y': '-webkit-mask-position-y',
            'webkitMaskPositionX': 'webkitMaskPositionX',
            'webkitMaskPositionY': 'webkitMaskPositionY',
            '-webkit-mask-size': '-webkit-mask-size',
            '-webkit-mask-sizeWidth': '-webkit-mask-size',
            '-webkit-mask-sizeHeight': '-webkit-mask-size',
            'backgroundPositionX': 'backgroundPositionX',
            'backgroundPositionY': 'backgroundPositionY',
            'backgroundSize': 'backgroundSize',
            'backgroundSizeWidth': 'backgroundSize',
            'backgroundSizeHeight': 'backgroundSize',
            'borderWidth': 'borderWidth',
            'borderTopLeftRadius': 'borderTopLeftRadius',
            'borderTopRightRadius': 'borderTopRightRadius',
            'borderBottomLeftRadius': 'borderBottomLeftRadius',
            'borderBottomRightRadius': 'borderBottomRightRadius',
            'color': 'color',
            'borderColor': 'borderColor',
            'fontStyle': 'fontStyle',
            'fontWeight': 'fontWeight'
        },
        'geometry': {
            'width': 'width',
            'top': 'top',
            'left': 'left',
            'height': 'height'
        },
        'font': {
            'fontSize': 'fontSize'
        },
        'transform': {
            'rotate': 'rotate',
            'rotateX': 'rotateX',
            'rotateY': 'rotateY',
            'rotateZ': 'rotateZ',
            'translateX': 'translateX',
            'translateY': 'translateY',
            'translateZ': 'translateZ',
            'scaleX': 'scaleX',
            'scaleY': 'scaleY',
            'scaleZ': 'scaleZ',
            'skewX': 'skewX',
            'skewY': 'skewY',
            'perspective': 'perspective',
        },
        'transform-origin': {
            'transform-origin': 'transform-origin',
            'transform-origin-x': 'transform-origin',
            'transform-origin-y': 'transform-origin'
        },
        'perspective-origin': {
            'perspective-origin': 'perspective-origin',
            'perspective-origin-x': 'perspective-origin',
            'perspective-origin-y': 'perspective-origin'
        }
    },
    'extensions': {
        'html': 'html',
        'js': 'javascript',
        'css': 'css',
        'component': 'component',
        'ttf': 'ttf',
        'otf': 'otf'
    },
    'warnMessages': {
        /* tslint:disable:max-line-length  */
        'sceneSettings': 'Scene settings cannot be set. Values will fallback to default - 1920x1080px with aspect ratio 16 to 9.',
        'idValidationWarning': function (field) {
            return 'The ' + field + ' must contain at least one letter and can begin with letter or a dash (-) followed only by letter.';
        },
        'invalidCharacter': 'Invalid character! The ID can contain only letters, numbers, dash and underscore.',
        'nameDuplication': 'There is already a widget with that name. Would you like to replace it?',
        'idNamesCollisionWarning': 'The scene already contains an element with that name.',
        'animationNameNotAllowed': 'Animation names can begin with a letter or dash (-), followed only by letters.',
        'deleteComponents': function (name) {
            return 'Are you sure you want to delete ' + name;
        },
        'closeTabToConvertTheWidget': 'Unable to convert Widget. Currently there is a component with the same name open for editing. Please close it before converting the widget.'
        /* tslint:enable:max-line-length  */
    },
    /**
     * @member voidElements
     * Elements which have no closing tag and thus cannot be nested in.
     */
    'voidElements': ['AREA', 'BASE', 'BR', 'COL', 'COMMAND', 'EMBED', 'HR', 'IMG',
        'INPUT', 'LINK', 'META', 'PARAM', 'SOURCE'],

    defaultHTML: {
        top: '<!DOCTYPE html>' + '\n' +
        '<html lang="en" id="scene">' + '\n' +
        '<head>' + '\n' +
        '<meta charset="UTF-8">' + '\n' +
        '<title>Document</title>',
        bottom: '</body>' + '\n' +
        '</html>',
    },

    'EDITOR_VERSION': /\/\* Editor Version Start \*\/([\s\S]*)\/\* Editor Version End \*\//,
    'couiEnvironment': /\/\* Editor Environment Start \*\/([\s\S]*)\/\* Editor Environment End \*\//,

    // All code inside Component Wrapper including the comments itself
    componentWrapper: /<!-- Wrapper Components Start -->[\s\S]*?<!-- Wrapper Components End -->/,

    // All code inside Component Register including the comments itself
    componentRegister: /<!-- Register Components Start -->[\s\S]*?<!-- Register Components End -->/,
};

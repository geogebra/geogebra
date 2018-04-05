/**
 *  @module lib/editor_properties
 */

'use strict';
declare let $;

module EditorProperties {
    /**
     * [COMMON_PROPERTIES description]
     * @var COMMON_PROPERTIES
     * @const {Object}
     */
    let COMMON_PROPERTIES = {

        'Elements': {
            'CommonElements': {},
            'AdvancedWidgets': {
                'flexbox': 'Flexible panel',
                'flexboxChild': 'Flexible panel element'
            }
        },
        'DefaultWidget': {
            'attrs': {},
            'children': [],
            'geometry': {
                'height': '0vh',
                'left': '0vw',
                'position': 'absolute',
                'top': '0vh',
                'width': '0vw'
            },
            'id': '',
            'className': '',
            'styles': {
                'display': 'inherit',
                'overflow': 'visible',
                'zIndex': '0',
                'opacity': '1'
            },
            'boxShadow': {
                'blurRadius': '0px',
                'color': '#000',
                'horizontalLength': '0px',
                'insetOutset': 'none',
                'spreadRadius': '0px',
                'verticalLength': '0px',
            },
            'transform': {},
            'type': '',
            'events': {},
            'dataBindings': {}
        },
        'DefaultExtensions': {
            'image': ['bmp', 'jpeg', 'svg', 'jpg', 'png', 'svgz', 'dds', 'psd', 'tga'],
            'video': ['webm', 'mkv'],
            'style': ['css'],
            'script': ['js'],
            'html': ['html'],
            'font': ['otf', 'ttf']
        },
        'ORIGINAL_SOURCE_SCENE_PATH': '<!-- Original scene path -->',
        'ORIGINAL_SOURCE_SCENE_PATH_END': '<!-- Original scene path end -->',
        'COMMENT_MARK_START': '<!-- Coherent Editor Start -->',
        'COMMENT_MARK_END': '<!-- Coherent Editor End -->',
        'CSS_ANIMATIONS_MARK_START': '/* CSS Animations Start */',
        'CSS_ANIMATIONS_MARK_END': '/* CSS Animations End */',
        'ASPECT_RATIO_MARK_START': '/* Aspect Ratio Start */',
        'ASPECT_RATIO_MARK_END': '/* Aspect Ratio End */',
        'EDITOR_VERSION_MARK_START': '/* Editor Version Start */',
        'EDITOR_VERSION_MARK_END': '/* Editor Version End */',
        'EDITOR_ENV_MARK_START': '/* Editor Environment Start */',
        'EDITOR_ENV_MARK_END': '/* Editor Environment End */',
        'COMPONENTS_MARK_START': '/* Editor Components Start */',
        'COMPONENTS_MARK_END': '/* Editor Components End */',
        'REGISTER_COMPONENTS_MARK_START': '<!-- Register Components Start -->',
        'REGISTER_COMPONENTS_MARK_END': '<!-- Register Components End -->',
        'WRAPPER_COMPONENTS_MARK_START': '<!-- Wrapper Components Start -->',
        'WRAPPER_COMPONENTS_MARK_END': '<!-- Wrapper Components End -->',
        'PHOTOSHOP_STYLE_COMMENT_END': '<!-- End Save for Web Styles -->',
        'PHOTOSHOP_CONTENT_COMMENT_END': '<!-- End Save for Web Slices -->'
    };

    // GT setup
    let gtExport = $.extend(true, {}, COMMON_PROPERTIES);
    let gtExtend = {
        'Elements': {
            'CommonElements': {
                'rectangle': 'Rectangle',
                'roundedRect': 'Rounded rectangle',
                'circle': 'Circle',
                'ellipse': 'Ellipse',
                'text': 'Text',
                'responsiveImage': 'Responsive image',
                'image': 'Image',
                'video': 'Video',
                'button': 'Button',
                'div': 'Div',
                'liveview': 'Liveview',
            },
            'FormElements': {
                'inputText': 'Input field',
                // TODO: remove all logic surrounding select and option elements if not integrated in GT 2
                // 'select': 'Dropdown menu',
                // 'option': 'Dropdown menu option',
                'label': 'Label',
                'range': 'Range',
                'number': 'Number',
                'radio': 'Radio',
                'checkbox': 'Checkbox'
            },
            'Lists': {
                'ul': 'Unordered list',
                'li': 'List element',
                'ol': 'Ordered list',
            },
        },
        'DefaultWidget': {
            'attrs': {
                'data-l10n-id': ''
            },
            '-coherent-layer-clip-aa': 'off',
            '-webkit-filter': {},
            'styles': {
                'fontStyle': 'inherit',
                'fontWeight': 'inherit',
            },
        }
    };

    gtExport = $.extend(true, gtExtend, gtExport);

    // Hummingbird setup
    let hummingbirdExport = $.extend(true, {}, COMMON_PROPERTIES);
    let hummingbirdExtend = {
        'Elements': {
            'CommonElements': {
                'rectangle': 'Rectangle',
                'roundedRect': 'Rounded rectangle',
                'text': 'Text',
                'textarea': 'Textarea',
                'responsiveImage': 'Responsive image',
                'image': 'Image',
                'div': 'Div',
                'inputText': 'Input field'
            }
        }
    };

    hummingbirdExport = $.extend(true, hummingbirdExtend, hummingbirdExport);

    export var GT = gtExport;
    export var Hummingbird = hummingbirdExport;
}

export default EditorProperties;

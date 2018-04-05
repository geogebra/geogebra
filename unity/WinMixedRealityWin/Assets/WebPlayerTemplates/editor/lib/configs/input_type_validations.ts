export const configuration: InputTypeValidation = {
    id: {
        validation: ['noSpace', 'id'],
        validateOnBlur: true,
        parse: false
    },
    className: {
        validation: ['className'],
        validateOnBlur: true,
        parse: false
    },
    zIndex: {
        validation: ['noSpace', 'number', 'integer'],
        validateOnBlur: false,
        parse: true
    },
    backgroundPositionX: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    backgroundPositionY: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    opacity: {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [0, 1],
        parse: true
    },
    'data-l10n-id': {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    '-webkit-mask-position-x': {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    '-webkit-mask-position-y': {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    '-webkit-mask-sizeWidth': {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    '-webkit-mask-sizeHeight': {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    top: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    left: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    width: {
        validation: ['noSpace', 'number', 'positive'],
        validateOnBlur: false,
        parse: true
    },
    height: {
        validation: ['noSpace', 'number', 'positive'],
        validateOnBlur: false,
        parse: true
    },
    fontSize: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderWidth: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderTopLeftRadius: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderTopRightRadius: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderBottomLeftRadius: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderBottomRightRadius: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    borderTopBottomRadius: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    blur: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    grayscale: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    sepia: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    brightness: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    contrast: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    'hue-rotate': {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    invert: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    saturate: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    filterOpacity: {
        validation: ['noSpace', 'number', 'integer', 'range'],
        validateOnBlur: false,
        range: [0, 100],
        parse: true
    },
    dropShadowX: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    dropShadowY: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    dropShadowBlur: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    rotateZ: {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [-360, 360],
        parse: true
    },
    translateX: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    translateY: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    translateZ: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    scaleX: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    scaleY: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    scaleZ: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    skewX: {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [-360, 360],
        parse: true
    },
    skewY: {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [-360, 360],
        parse: true
    },
    'transform-origin-x': {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [-360, 360],
        parse: true
    },
    'transform-origin-y': {
        validation: ['noSpace', 'number', 'range'],
        validateOnBlur: false,
        range: [-360, 360],
        parse: true
    },
    'bind-class': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'class-toggle': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-left': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-top': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-opacity': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-width': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-height': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-style-color': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'background-color': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-for': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-value': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-model': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-template': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'background-image': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    'bind-transform2d': {
        validation: ['noSpace', 'dataBind'],
        validateOnBlur: true,
        parse: false
    },
    text: {
        validation: [''],
        validateOnBlur: false,
        parse: false
    },
    padding: {
        validation: ['noSpace', 'number'],
        validateOnBlur: false,
        parse: true
    },
    value: {
        validation: [''],
        validateOnBlur: false,
        parse: false
    },
    placeHolder: {
        validation: [''],
        validateOnBlur: true,
        parse: false
    },
    min: {
        validation: ['noSpace', 'number'],
        validateOnBlur: true,
        parse: true
    },
    max: {
        validation: ['noSpace', 'number'],
        validateOnBlur: true,
        parse: true

    },
    step: {
        validation: ['noSpace', 'number'],
        validateOnBlur: true,
        parse: true
    },
    order: {
        validation: ['noSpace', 'number', 'integer'],
        validateOnBlur: false,
        parse: true
    },
    flex: {
        validation: ['flex'],
        validateOnBlur: true,
        parse: false
    },
    click: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    dblclick: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mouseenter: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mouseover: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mouseleave: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mousemove: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mousedown: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mouseup: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    mousewhell: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    change: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    scroll: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    focus: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    focusin: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    focusout: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    keypress: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    keydown: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    keyup: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    touchend: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    touchmove: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    },
    touchstart: {
        validation: ['noSpace'],
        validateOnBlur: false,
        parse: false
    }
};

export const nonValidatedElements = ['input:checkbox', 'select', 'input:radio', 'input[type="range"]'];

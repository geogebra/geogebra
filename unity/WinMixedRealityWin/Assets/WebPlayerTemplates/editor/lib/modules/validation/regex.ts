export default {
    // accepts integers - 1, 2, 3333, 44444, and floats - .1111 1.323213
    number: /^[-+]?([0-9]+|\.[0-9]+|[0-9]+\.[0-9]+)$/,
    // ID and NAME tokens must begin with a letter ([A-Za-z])
    // and may be followed by any number of letters, digits ([0-9]), hyphens ("-"), underscores ("_"), colons (":"), and periods (".").
    namesToken: /^[A-Za-z]+[\w\-:.]*$/,
    // accepts one or more spaces
    spaces: /[ ,]+/,
    // accepts auto, inherit and any number followed by "%", "px", "em" or any other length unit
    flex: /auto|inherit|\d*(cm|em|ex|in|mm|pc|pt|px|vh|vmin|%)/,
    // accepts string in the following format - {{<any sequence of characters>}}.
    // stops at first match
    dataBinding: /{{.*?}}/,
    // same as dataBinding, with global flag enabled
    globalDataBinding: /{{.*?}}/g
};

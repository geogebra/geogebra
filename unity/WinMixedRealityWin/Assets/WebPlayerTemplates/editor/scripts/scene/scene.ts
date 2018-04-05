/**
 *  @module scripts/scene/scene
 *  @export scripts/scene/scene.Scene
 *  @require module:lib/enums
 */
'use strict';
declare let $;
import Enums from 'lib/enums';

/**
 * You can see all class properties in module {@link  module:lib/enums}. View property `newScene`
 * @memberOf module:scripts/scene/scene
 * @class
 * @constructor
 * @see module:lib/enums
 */
export default class Scene implements IScene {
    _DOMId: string;
    animationClasses: IAnimationClasses;
    animations: any;
    deps: string[];
    sceneEvents: any;
    sceneSize: any;
    scripts: string[];
    style: any;
    fonts: string[];
    styles: string[];
    widgets: IWidget[];

    constructor() {
        $.extend(true, this, Enums.newScene);
    }

    /**
     * Get scene style property.
     * @memberOf module:scripts/scene/scene.Scene
     * @param property {string}
     * @returns {*}
     */
    getStyleProperty(property) {
        return this.style[property];
    }

    /**
     * Set scene style property.
     * @memberOf module:scripts/scene/scene.Scene
     * @param key {string}
     * @param value {*}
     * @returns {Scene}
     */
    setStyleProperty(key, value) {
        this.style[key] = value;

        this.applyStyleProperty(key, value);

        return this;
    }

    /**
     * Visually applies a style property to the scene.
     * @memberOf module:scripts/scene/scene.Scene
     * @param key {string}
     * @param value {*}
     * @returns {Scene}
     */
    applyStyleProperty(key, value) {
        var scene = this.getSceneRootElement();

        scene.style[key] = value;

        return this;
    }

    /**
     * Returns the root DOM element of the scene.
     * @memberOf module:scripts/scene/scene.Scene
     * @returns {element}
     */
    getSceneRootElement() {
        return document.getElementById(this._DOMId);
    }
}



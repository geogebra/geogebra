import helpers from './function_helpers';
import couiEditor from '../scripts/main';
import elementIsDisabled = webdriver.until.elementIsDisabled;

export default class Transform {
    public element: IWidget;

    constructor(element?: IWidget) {
        this.element = element;
    }

    /**
     * Gets the element's coordinates according to its transform origin
     * @param numbersGeometry{IWidgetGeometry}
     * @param tOrigin{IPosition}
     *
     * @returns {Array}
     * */
    getOriginCoords(numbersGeometry: IWidgetGeometry, tOrigin: IPosition): Array<Array<number>> {
        return [
            [Number(numbersGeometry.left) - tOrigin.x, Number(numbersGeometry.top) - tOrigin.y, 0, 1], //A
            [
                (Number(numbersGeometry.left) + Number(numbersGeometry.width)) - tOrigin.x,                //B
                Number(numbersGeometry.top) - tOrigin.y, 0, 1],
            [
                (Number(numbersGeometry.left) + Number(numbersGeometry.width)) - tOrigin.x,                //C
                (Number(numbersGeometry.top) + Number(numbersGeometry.height)) - tOrigin.y, 0, 1],
            [
                Number(numbersGeometry.left) - tOrigin.x,                                                  //D
                (Number(numbersGeometry.top) + Number(numbersGeometry.height)) - tOrigin.y, 0, 1]
        ];
    }

    /**
     * Transforms the coordinates of the element from 3D to 2D
     * @param tPoins{Array}
     * @returns tPoints {Array}
     * */
    coordsIn2D(tPoints: Array<Array<number>>): Array<Array<number>> {
        for (let i = 0; i < tPoints.length; i++) {
            let w = tPoints[i][3];

            tPoints[i][0] = tPoints[i][0] / w;
            tPoints[i][1] = tPoints[i][1] / w;
            tPoints[i][2] = 0;
            tPoints[i][3] = w / w;
        }

        return tPoints;
    }

    /**
     * Gets the coordinates of the transfrom origin of the $element accordin to the parent's plane
     * @param numbersGeometry {IWidgetGeometry}
     * @param parentElGeometry {IWidgetGeometry}
     * @param $element {HTMLElement}
     * @returns {IPosition}
     * */
    getOriginInParent(elGeometry: IWidgetGeometry, parentGeometry: IWidgetGeometry, $element: HTMLElement): IPosition {
        let elStyle = getComputedStyle(document.getElementById($element.id));
        let parentStyle = getComputedStyle(document.getElementById($element.parentElement.id));
        let elTOrigin = elStyle.transformOrigin.split(' ');
        let parentTOrigin = parentStyle.transformOrigin.split(' ');
        //sets the transform origin of the scene to [0, 0]
        if ($element.parentElement.id === 'scene') {
            parentTOrigin = ['0px', '0px'];
        }
        let re = /(-|[0-9\.0-9])+/g;

        return {
            x: parseFloat(elGeometry.left) + Number(elTOrigin[0].match(re)[0]) - Number(parentTOrigin[0].match(re)[0]),
            y: parseFloat(elGeometry.top) + Number(elTOrigin[1].match(re)[0]) - Number(parentTOrigin[1].match(re)[0])
        };
    }

    /**
     * Updates the transformed_position property of a given element
     * @param element {IWidget}
     * */
    transform(element: IWidget) {
        let tPosition = this.getPosition(element);
        element.transformed_position = tPosition;
    }

    /**
     * Creates the matrix that describe the translation of the element from it's parent
     * @param $element {HTMLElement}
     * @returns {WebKitCSSMatrix}
     * */
    getTranslationMatrix($element: HTMLElement): WebKitCSSMatrix {
        let translation = this.getElTranslation($element);

        let translationMatrix = new WebKitCSSMatrix();
        translationMatrix.m41 = -translation.x;
        translationMatrix.m42 = -translation.y;

        return translationMatrix;
    }

    /**
     * Recursively loops each of the element's parent element and
     * gets the transformation of each element according to it's parent
     * @param $element {HTMLElement}
     * @param parentId {string}
     * @parem position {Array}
     * @returns position{Array}
     * */
    getTransformedPos($element: HTMLElement, parentId: string, position: Array<Array<number>>): Array<Array<number>> {

        while ($element.id !== parentId) {
            let matrix = new WebKitCSSMatrix(getComputedStyle($element).webkitTransform);
            //inverse the translation matrix
            //multiply it by the translation matrix
            //inverse the result
            matrix = matrix.inverse().multiply(this.getTranslationMatrix($element)).inverse();

            position = this.coordsIn2D(this.getTrsnsformedPoints(helpers.parseMatrix(matrix, true), position));

            $element = $element.parentElement;

            this.getTransformedPos($element, parentId, position);
        }

        return position;
    }

    /**
     * Gets the difference between the transform origins of an element and it's parent
     * @param $element {HTMLElement}
     * @returns translation {IPosition}
     * */
    getElTranslation(element: HTMLElement): IPosition {
        var editorId = couiEditor.selectedEditor;
        var runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;
        let widget = runtimeEditor.mappedWidgets[element.id].widget;
        let parentElement = runtimeEditor.mappedWidgets[element.parentElement.id];
        parentElement = parentElement ? parentElement.widget : {};

        let elPosition = helpers.getElementVertices(widget);
        let parentElPos = helpers.getElementVertices(parentElement);

        let translation = this.getOriginInParent(elPosition, parentElPos, element);

        return translation;
    }

    /**
     * Gets the position of the element before the transformation and
     * returns the transformed position
     * @param element {IWidget}
     * @returns position {Array}
     * */
    getPosition(element: IWidget): Array<Array<number>> {
        let numbersGeometry = helpers.getElementVertices(element);
        let $element = document.getElementById(element.id);
        let elAbsolutePosition = helpers.getAbsolutePosition($element, 'scene');
        Object.assign(numbersGeometry, elAbsolutePosition);
        let tOrigin = helpers.getTransformOrigin(numbersGeometry, element);
        let elCoords = this.getOriginCoords(numbersGeometry, tOrigin);

        let position = this.getTransformedPos($element, 'scene', elCoords);

        return position;
    }

    /**
     * Multiplies the transformation matrix by the coordinates of each point of the element to
     * get the coordinates of the element after the transformation
     * @param matrix {Array}
     * @param  elementPoints {Array}
     * @returns transformedPoints {Array}
     * */
    getTrsnsformedPoints(matrix: Array<Array<number>>, elementPoints: Array<Array<number>>): Array<Array<number>> {
        let transformedPoints = [];
        for (let i = 0; i < elementPoints.length; i++) {
            transformedPoints.push(helpers.multiply(matrix, elementPoints[i]));
        }

        return transformedPoints;
    }

    /**
     * Gets the position of the element according to the viewport
     * Calculates the translation and scale translations of the scene element
     * @param emenet {IWidget}
     * @returns viewportPos {Array}
     * */
    getViewportPosition(element: IWidget): Array<IPosition> {
        var viewportPos = [];

        if (!element.transformed_position) {
            return viewportPos;
        }
        this.transform(element);
        let sceneEl = document.getElementById('scene');
        let sceneOffset = $('#scene').offset();
        let sceneWrapperOffset = $('#scene-wrapper').offset();

        let sceneTransformation = new WebKitCSSMatrix(window.getComputedStyle(sceneEl).webkitTransform);

        let sceneOffsetX = (sceneOffset.left - sceneWrapperOffset.left );
        let sceneOffsetY = (sceneOffset.top - sceneWrapperOffset.top );

        for (let i = 0; i < element.transformed_position.length; i++) {
            viewportPos.push({
                x: element.transformed_position[i][0] * sceneTransformation.a + sceneOffsetX,
                y: element.transformed_position[i][1] * sceneTransformation.a + sceneOffsetY
            });
        }

        return viewportPos;
    }
}

/**
 * Create a new <template> element, populate it with a string DOM and return
 * the child Nodes
 * @param {string} domString
 * @returns {NodeList}
 * @private
 */
export function createVDOM(domString: string): HTMLElement[] {
    const div = document.createElement('div');
    div.innerHTML = domString;

    const arrayDOM: HTMLElement[] = [].slice.call(div.children);

    arrayDOM.forEach((node, index) => {
        node.style.top = (index * 34) + 'px';
        node.setAttribute('vList-index', String(index));
    });

    return arrayDOM;
}

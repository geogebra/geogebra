/**
 * Filter the whole asset list based on input value
 * @param {Event} event
 * @param {HTMLElement[]} data
 * @returns {HTMLElement[]}
 */
export function assetSearch(event: Event, data): HTMLElement[] {
    let _data = data;
    const value = (<HTMLInputElement>event.target).value;

    if (value !== '') {
        _data = data.filter(element => {
            const textElement = element.querySelector('[data-link]');

            if (textElement) {
                return textElement
                    .getAttribute('data-link')
                    .toLowerCase()
                    .indexOf(value.toLowerCase()) !== -1;
            } else {
                return false;
            }
        });
    }

    _data.forEach((node, i) => {
        node.style.top = (i * 34) + 'px';
        node.setAttribute('vList-index', String(i));
    });

    return _data;
}

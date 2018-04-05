/**
 * Event callback, called on asset sort button click.
 * Toggles the active class on the button clicked, get all active sort buttons
 * and filters the vDOM based on active buttons
 * @param {Event} e
 * @param vDOM - DOM representation of the
 * @returns {any}
 */
export function onClickFilter(e: Event, vDOM: HTMLElement[]): HTMLElement[] {
    e.preventDefault();

    const target = (e.target as HTMLElement);
    target.classList.toggle('active');

    const activeElements = getActiveSortButtons();
    return filterVDOM(activeElements, vDOM);
}

/**
 * Returns an array of all active sort's button data-type property
 * @returns {string[]}
 */
export function getActiveSortButtons(): string[] {
    const element = document.querySelector('.assets-filter').children;
    return [].slice.call(element)
        .filter(el => el.classList.contains('active'))
        .map(el => el.getAttribute('data-type'));
}

/**
 * Adds class "active" to all buttons that match the provided
 * data-type
 * @param {string[]} buttons
 */
export function applyActive(buttons): void {
    const elements: HTMLCollection = document.querySelector('.assets-filter').children;
    [].slice.call(elements).forEach(element => {
        if (buttons.includes(element.getAttribute('data-type'))) {
            element.classList.add('active');
        }
    });
}

/**
 * Filters the vDOM based on active button types provided and reposition the elements.
 * Returns the filtered array of HTML elements.
 * @param {string[]} activeElements
 * @param {HTMLElement[]} vDOM
 * @returns {HTMLElement[]}
 */
export function filterVDOM(activeElements: string[], vDOM: HTMLElement[]): HTMLElement[] {
    let filtered = vDOM;
    if (activeElements.length !== 0) {
        filtered = filtered.filter(element => {
            const dataType = element.getAttribute('data-type') || element.getAttribute('data-type-filter');
            return activeElements.includes(dataType);
        });
    }

    filtered.forEach((node, i) => {
        node.style.top = (i * 34) + 'px';
        node.setAttribute('vList-index', String(i));
    });

    return filtered;
}

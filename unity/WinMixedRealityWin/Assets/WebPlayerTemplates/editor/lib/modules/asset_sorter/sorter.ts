export interface SortButtons {
    [key: string]: HTMLElement;
}


export default class Sorter {
    public buttons: SortButtons;

    public init(): Sorter {
        this.buttons = {
            image: document.getElementById('asset-filter-image'),
            video: document.getElementById('asset-filter-video'),
            widget: document.getElementById('asset-filter-widget'),
            css: document.getElementById('asset-filter-css'),
            javascript: document.getElementById('asset-filter-javascript'),
            font: document.getElementById('asset-filter-font')
        };

        return this;
    }

    /**
     * Attach an event to all sort buttons
     * @param {string} event
     * @param {EventListenerOrEventListenerObject} fn
     * @returns {Sorter}
     */
    public listen(event: string, fn: EventListenerOrEventListenerObject): Sorter {
       for (const element in this.buttons) {
           if (element === 'undefined') {
               throw new Error('Sorter: Missing sort button');
           }

           this.buttons[element].addEventListener(event, fn);
       }

       return this;
    }

    /**
     * Remove events from all sort buttons
     * @param event
     * @param fn
     * @returns {Sorter}
     */
    public stopListening(event, fn): Sorter {
        for (const element in this.buttons) {
            if (element === 'undefined') {
                throw new Error('Sorter: Missing sort button');
            }

            this.buttons[element].removeEventListener(event, fn);
        }

        return this;
    }
}

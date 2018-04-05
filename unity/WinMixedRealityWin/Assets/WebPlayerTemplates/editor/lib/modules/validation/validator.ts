import regex from './regex';

export default class InputValidator {
    private isValid: boolean;
    private config: InputTypeValidation;
    private value: string;
    private nonValidatedElements: string[];

    /**
     *
     * @param configuration {InputTypeValidation} - a configuration option with all property elements
     * @param nonValidatedElements {string[]} - all element types that doesn't need to be validated
     */
    constructor(configuration: InputTypeValidation, nonValidatedElements: string[]) {
        this.config = configuration;
        this.nonValidatedElements = nonValidatedElements;
    }

    /**
     * Validate the input value passed based on type passed
     * @param {string} value - the value to be validated
     * @param {string} propType - input's type
     * @returns {boolean} -
     */
    public validate(value: string, propType: string): boolean {
        this.isValid = true;
        this.value = value;

        if (this.checkProp(propType)) {
            throw new Error(`propType: ${propType} is missing from available input types.`);
        }

        this.config[propType].validation.forEach(validation => {
            if (validation === 'range') {
                this.isValid = this.syncValidator(this.range(this.config[propType][validation]));
            } else if (validation) {
                this.isValid = this.syncValidator(this[validation]());
            }
        });

        return this.isValid;
    }

    /**
     * Check if an element is of specific type and should it be validated
     * @param $element - currently clicked jQuery element
     * @returns {boolean}
     */
    public shouldValidate($element): boolean {
        let validate = true;

        this.nonValidatedElements.forEach(type => {
            if ($element.is(type)) {
                validate = false;
            }
        });

        return validate;
    }

    /**
     * Checks if a property exist in the configuration object
     * @param {string} prop - the property to be checked
     * @returns {boolean}
     */
    private checkProp(prop: string): boolean {
        for (const property in this.config) {
            if (property === prop) {
                return false;
            }
        }

        return true;
    }

    /**
     * Synchronize the current and global input validation
     * @param {boolean} current
     * @returns {boolean}
     */
    private syncValidator(current: boolean): boolean {
        return this.isValid && current;
    }

    /**
     * Checks if value is a valid number and synchronizes
     * with the global validation property
     */
    private number(): boolean {
        return regex.number.test(this.value);
    }

    /**
     * Checks if value is a valid integer number and synchronizes
     * with the global validation property
     */
    private integer(): boolean {
        return Number(this.value) % 1 === 0;
    }

    /**
     * Checks if value is a valid number between a range of numbers
     * and synchronizes with the global validation property
     * @param {[number , number]} range
     */
    private range(range: [number, number]): boolean {
        return Number(this.value) <= range[1] && Number(this.value) >= range[0];
    }

    /**
     * Checks if value has no space in it and synchronizes
     * with the global validation property
     */
    private noSpace(): boolean {
        return this.value.indexOf(' ') === -1;
    }

    /**
     * Checks if value is a valid id name and synchronizes
     * with the global validation property
     */
    private id(): boolean {
        return regex.namesToken.test(this.value);
    }

    /**
     * Checks if value is a class name and synchronizes
     * with the global validation property.
     *
     * The method recognizes empty string as valid
     */
    private className(): boolean {
        let valid = true;

        if (this.value !== '') {
            const classList = this.value.trim().split(regex.spaces);
            valid = classList.every(className => regex.namesToken.test(className));
        }

        return valid;
    }

    /**
     * Splits the flex value, checks each of them and synchronizes
     * with the global validation property
     */
    private flex(): boolean {
        let valid = true;
        const validFlexValues = ['auto', 'initial', 'none', 'inherit'];

        if (!validFlexValues.includes(this.value)) {
            const valueArr = this.value.split(regex.spaces);
            const flexGrow = regex.number.test(valueArr[0]);
            const flexShrink = regex.number.test(valueArr[1]);
            const flexBasis = regex.flex.test(valueArr[3]);

            if (valueArr.length !== 3 || !flexGrow || !flexShrink || !flexBasis) {
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Checks if value is a data bind string and synchronizes
     * with the global validation property
     *
     * The method recognizes empty string as valid
     */
    private dataBind(): boolean {
        let valid = true;

        if (this.value !== '') {
            valid = regex.dataBinding.test(this.value) && this.value.match(regex.globalDataBinding).length <= 1;
        }

        return valid;
    }

    private positive(): boolean {
        return Number(this.value) > 0;
    }
}

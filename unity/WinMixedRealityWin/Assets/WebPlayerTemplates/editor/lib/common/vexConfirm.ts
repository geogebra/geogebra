/// <reference path="../../../typings/coui-editor/editor.d.ts"/>
/**
 *  @module lib/helpers/vexConfirm
 *  @export lib/helpers/vexConfirm
 */
'use strict';
declare let $;

export default class VexConfirm implements IVexConfirm {

    create(message) {
        return new Promise(function (resolve) {
            var $vex = vex.dialog.confirm({
                closeOnOverlayClick: true,
                contentClassName: 'modal-about',
                message: message,
                buttons: [
                    $.extend({}, vex.dialog.buttons.NO, {
                        text: 'Yes',
                        click: function () {
                            vex.close($vex.data().vex.id);
                            resolve('yes');
                        }
                    }), $.extend({}, vex.dialog.buttons.NO, {
                        text: 'No',
                        click: function () {
                            vex.close($vex.data().vex.id);
                            resolve('no');
                        }
                    })
                ]
            });
        });
    }
}



/*global $, jQuery, GGBT_wsf_view, console, alert, GGBApplet, renderGGBElement, GGBT_wsf_general*/

window.GGBT_wsf_view = (function($, general) {
    "use strict";

    var APPLET_CONTROLS_HTML = '<div class="wsf-applet-controls">' +
            '<button class="fixapplet">FixAppletIcon</button>' +
        '</div>';

    if (!general) {
        console.log("general not loaded");
    }

    function initWsfTeacherInfoButton() {
        var button = $(".wsf-teacher-info-button");
        button.off("click").on("click", function(e) {
            e.preventDefault();
            general.initTeacherInfoPage($(this));
        });
    }

    function initWsfElementInfoButton() {
        var buttons = $(".wsf-element-info-button");
        buttons.off("click").on("click", function(e) {
            e.preventDefault();
            var parent = $(this).parents(".worksheet_element");
            general.setWsfActiveContent(parent);
            general.initElementInfoPage($(this));
        });
    }

    function initWsfButtonInformation() {
        var button = general.getButtonInfoClose();
        button.off("click").on("click", function(e) {
            general.closeInfoFromView();
            e.preventDefault();
        });
    }

    function initSaveExerciseButtons() {
        if (!window.GGBT_gen_edit) {
            return;
        }
        window.GGBT_gen_edit.initSaveAndContinue(function(e) {
            saveUserExamData(false, false);
        }, '.save');

        window.GGBT_gen_edit.initSave( function(e) {
            var save = window.confirm($('.done.show').data('confirm'));

            if(save) {
                saveUserExamData(true, false, function done(success) {
                    if (success) {
                        $('.button.done.show').hide();
                        $('.button.save').hide();
                        $('.restart').show();
                        $('.cancel').toggle();
                        $(":input").not('.button').prop('disabled', true);
                        $('.ws-element-question').each(function () {
                            markAnswerOfQuestion($(this), true);
                        });
                    }
                });
            }
        }, '.done.show');

        window.GGBT_gen_edit.initSave( function(e) {
            saveUserExamData(true, false, function done(success) {
                if (success) {
                    $('.button.done').hide();
                    $('.button.save').hide();
                    $('.button.reopen').show();
                    $(":input").not('.button').prop('disabled', true);
                    var doneURL = window.GGBT_gen_edit.getDoneURL();
                    if (doneURL) {
                        window.location = doneURL;
                    }
                }
            });
        }, '.done');

        window.GGBT_gen_edit.initCancel('.cancel', true);

        window.GGBT_gen_edit.initSaveAndContinue(function(e) {
            var save = window.confirm($('.restart').data('confirm'));

            if(save) {
                $('.worksheet_tbl').find('input:checked').removeAttr('checked');
                saveUserExamData(false, true, function done(success) {
                    if(success) {
                        $('.restart').hide();
                        $('.done.show').show();
                        $('.save').show();
                        $('.cancel').toggle();

                        $('.ws-element-question').each(function() {
                            markAnswerOfQuestion($(this), false);
                        });
                        $(":input").not('.button').prop('disabled', false);
                    }
                });
            }
        }, '.restart');
    }

    function initSaveEvalButtons() {
        if (!window.GGBT_gen_edit) {
            return;
        }

        $('#student_worksheets').on("change", function(e){
            e.preventDefault();
            $(this).attr('name',$(this).val());
            saveEvalData(false, false, function done(success, url) {
                if (success) {
                    window.location = $('#student_worksheets').attr('name');
                }
            });
        });

        window.GGBT_gen_edit.initSave(function(e) {
            saveEvalData(false, false, function done(success) {
                if (success) {
                    window.opener.location.reload();
                    window.close();
                }
            });
        }, '.done.close');

        window.GGBT_gen_edit.initSave(function(e) {
            var save = window.confirm($('.done.return').data('confirm'));


            if( $('.done.return').attr('disabled') !== 'disabled' && save){
                saveEvalData(true, false, function done(success) {
                    if (success) {
                        $('.student-state').text($('.student-state').data('returned'));
                        $('.done.return').hide();
                        $('.done.close').hide();
                        $('.cancel').hide();
                        $('.reopen').show();
                        $('.button.cancel').show();
                    }
                });
            }
        }, '.done.return');

        window.GGBT_gen_edit.initSave(function(e) {
            var redo = window.confirm($('.reopen').data('confirm'));
            if(redo) {
                saveEvalData(true, true, function done(success) {
                    if (success) {
                        window.location.reload();
                        $('.button.cancel').hide();
                        $('.done.close').show();
                        $('.cancel').show();
                    }
                });
            }
        }, '.reopen');


        window.GGBT_gen_edit.initCancel('.cancel', true);
    }

    function getUserExamData(worksheet, onDone) {
        var data = {elements: []};
        $('.ws-element-question', worksheet).each(function(idx, question) {
            data.elements.push(getQuestionData(question));
        });

        var exercises = $('.ws-element-exercise', worksheet);
        if (exercises.length) {
            var idx = 0;
            var callback = function(exerciseData) {
                if (exerciseData !== undefined) {
                    data.elements.push(exerciseData);
                }
                if (idx<exercises.length) {
                    getExerciseData(exercises[idx++], callback);
                } else {
                    onDone(data);
                }
            };
            callback();
        } else {
            onDone(data);
        }
    }

    function getQuestionData(question) {
        var data = {id: $(question).data("id"), data: {hasData: true}};
        var choices = $('.ws-question-choice', question);
        if (choices.length) {
            data.data.answers = [];
            $('input:checked', choices).each(function(idx, choice) {
                data.data.answers.push($(choice).data("id"));
            });
        } else {
            data.data.answer = $('.ws-element-question-answertext textarea', question).val();
        }
        data.data.points_max = $(question).data("points_max");
        return data;
    }

    function getExerciseData(exercise, callback) {
        var data = {id: $(exercise).data("id"), data: {hasData: true}};
        var material_id = $(exercise).data("material_id");
        if (window["applet_"+material_id] === undefined) {
            callback(data);
            return;
        }
        var applet = window["applet_"+material_id].getAppletObject();
        applet.getBase64(true, function(ggb64) {
            if ($('article', exercise).data("param-ggbbase64") !== ggb64) {
                data.data.ggb64 = ggb64;
            }
            callback(data);
        });
    }

    function saveUserExamData(markAsDone, reopen, done) {
        window.GGBT_gen_edit.setSaveStateInProgress();

        var worksheet = window.GGBT_wsf_general.getWorkSheet();
        getUserExamData(worksheet, function(data) {
            if (markAsDone) {
                data.markAsDone = true;
            }
            if(reopen) {
                data.reopen = true;
            }

            var fail = function(msg) {
                window.GGBT_gen_edit.setSaveStateError(msg);
                if (done !== undefined) {
                    done(false);
                }
            };

            $.post($('.save-wrapper').data('saveurl'), {data: data}).done(function(result) {
                if (result.type === "success") {
                    window.GGBT_gen_edit.setSaveStateSuccessful();
                    if (done !== undefined) {
                        done(true);
                    }
                } else {
                    fail(result.message);
                }
            }).fail(function(result) {
                fail(result.responseText);
            });
        });
    }

    function saveEvalData(markAsGraded, reopen, done) {
        window.GGBT_gen_edit.setSaveStateInProgress();

        var data = {user_id: $('.wsf-worksheet-title').data('exercise-student'), markAsGraded: markAsGraded, reopen: reopen, elements: []};
        $('.points').each(function () {
            var mat_eval = {id: $(this).parents(".worksheet_element").data('mat-id'), data: {hasData: true}};
            if ($(this).val().length !== 0) {
                mat_eval.points = $(this).val();
            } else {
                mat_eval.points = null;
            }
            data.elements.push(mat_eval);
        });



        var fail = function(msg) {
            window.GGBT_gen_edit.setSaveStateError(msg);
            if (done !== undefined) {
                done(false);
            }
        };

        $.post($('.save-wrapper').data('saveurl'), {data: data}).done(function(result) {
            if (result.type === "success") {
                window.GGBT_gen_edit.setSaveStateSuccessful();
                if (done !== undefined) {
                    done(true);
                }
            } else {
                fail(result.message);
            }
        }).fail(function(result) {
            fail(result.responseText);
        });
    }

    function initButtons() {
        initWsfTeacherInfoButton();
        initWsfElementInfoButton();
        initWsfButtonInformation();
        initTeachersReturnButton();

        var mode = general.getWorkSheet().data('mode');
        if(mode==="eval"){
            initSaveEvalButtons();
        }
        else{
            initSaveExerciseButtons();
        }

        initToolBar();
    }

    function initQuestionElements() {

        //elastic Textarea
        jQuery.fn.elasticArea = function() {
            return this.each(function(){
                function resizeTextarea(textarea) {
                    textarea.style.height = textarea.scrollHeight/2 + 'px';
                    textarea.style.height = textarea.scrollHeight + 'px';
                }
                $(this).keypress(function(e) {
                    resizeTextarea(e.target);
                })
                    .keydown(function(e) {
                        resizeTextarea(e.target);
                    })
                    .keyup(function(e) {
                        resizeTextarea(e.target);
                    })
                    .css('overflow','hidden');
                resizeTextarea(this);
            });
        };

        $('.ws-element-question-answertext textarea').elasticArea();
    }

    function initTeachersReturnButton() {
        if ($('.done.return')) {
            $(".points").each(function() {
                if($(this).val() === ""){
                    $('.done.return').attr('disabled',true);
                    $('.done.return').css('pointer-events', 'none');
                    return false;
                }
            });
        }
    }

    function initToolBar() {
        $('.worksheet_element[data-type="G"], .worksheet_element[data-type="E"]', general.getWorkSheet()).each(function(idx,elem) {
            var header = $('.ws-element-header.notitle', elem);
            var width;
            if ($('article', elem).length) {
                 width = $('article', elem).data("param-width");
            } else if ($('.applet_container', elem).length) {
                width = $('.applet_container', elem).width();
            }
            if (header.length && width !== undefined) {
                $('.wsf-element-toolbar', header).css({right: (width - header.width() + 25)*-1});
            }
        });
    }

    function setAppletFixed(button) {
        var applet = button.parents(".worksheet_element"),
            position;
        if (!applet.data("isFixed")) {
            position = applet.find(".ws-element-applet, .ws-element-exercise").offset();
            console.log(position);
            applet.find(".ws-element-applet, .ws-element-exercise").addClass("fixed").css({
                top: position.top,
                left: position.left
            });
            applet.css({
                height: applet.find("article").attr("data-param-height")
            });
            button.text("UnFixAppletIcon");
            applet.data("isFixed", true);
        } else {
            applet.find(".ws-element-applet, .ws-element-exercise").removeClass("fixed").css("top", "auto");
            applet.css({
                height: "auto"
            });
            button.text("FixAppletIcon");
            applet.removeData("isFixed");
        }
        console.log($(".wsf-ws-scroller").scrollTop());
    }

    function initAppletControlEvents(html) {
        var fragment = $(html);
        fragment.find(".fixapplet").on("click", function(e) {
            setAppletFixed($(this));
        });
        return fragment;
    }

    function initAppletControls(c) {
        if (c.find("article[data-param-fixapplet=true]").length && !c.find(".wsf-applet-controls").length) {
            c.prepend(initAppletControlEvents(APPLET_CONTROLS_HTML));
        }
    }

    function onLoadTextElements(processMathquill, allWorksheets) {
        var texts;
        if (allWorksheets) {
            texts = $('.worksheet_tbl .ws-bbcode-text');
        } else {
            texts = $('.worksheet_tbl .ws-bbcode-text', general.getWorkSheet());
        }
        texts.each(function() {
            var bbcode = this.textContent || this.innerText;
            if (bbcode === undefined) {
                bbcode = "";
            }
            var html = window.GGBT_gen_edit.getHTMLFromBBCode(bbcode);
            $(this).html(html);
            if (processMathquill) {
                $('.mathquill-embedded-latex', this).mathquill();
            }
        });
    }

    function initNewWorksheet(worksheet) {
        var oldWorksheet = general.getWorkSheet();
        general.setWorksheet(worksheet);
        initButtons();
        onLoadTextElements(true);
        if (oldWorksheet && oldWorksheet.length>0) {
            general.setWorksheet(oldWorksheet);
        }
    }

    function initSumOfPoints(){
        $(".points").keyup(function(event){
            if (event.keyCode === 13 || event.keyCode === 9){
                sumUpPoints();
            }
        });
        $(".points").blur(function(event){
            sumUpPoints();
        });
    }

    function sumUpPoints(){
        var worksheet = $(".points").parents(".worksheet_tbl"),
            sum = 0,
            ret = true;
        $(".points").each(function() {
            sum+=Number($(this).val());
            if($(this).val() === ''){
                ret = false;
            }
        });
        if(ret){
            $('.done.return').attr('disabled',false);
            $('.done.return').css('pointer-events', '');
        } else {
            $('.done.return').attr('disabled',true);
            $('.done.return').css('pointer-events', 'none');
        }

        worksheet.find(".total_points").data('param-id',String(sum));
        worksheet.find(".total_points").text(worksheet.find(".total_points").data('param-id')+" / "+worksheet.find(".total_points").data('text'));
    }

    function init() {
        general.getWorkSheet();
        general.getWsfInfoContent();
        general.getWsfInfo();
        initSumOfPoints();
        initButtons();
        initMarkAnswers();
        initQuestionElements();
        onLoadTextElements(false, true);
        logInPopUp();
    }

    function initMarkAnswers() {
        $('.ws-question-choices').each(function(){
            if($(this).data('showsolution') === true) {
                markAnswerOfQuestion($(this), true);
            }
        });
    }

    function markAnswerOfQuestion(question, mark) {
        question.find('.ws-question-choice').each( function(){
            //mark right question
            if($(this).data('ticked')===true) {
                if(mark){
                    $(this).addClass("ticked");
                } else{
                    $(this).removeClass("ticked");
                }
            }

            //singleChoice Question
            if(question.data('multiple')===0) {
                if ($(this).find(":input").attr('checked') && $(this).data('ticked') === true) {
                    if(mark){
                        $(this).find('.answer').addClass("correct");
                        $(this).find('.answer').removeClass("placeholder");
                    }else{
                        $(this).find('.answer').addClass("placeholder");
                        $(this).find('.answer').removeClass("correct");
                    }
                } else if ($(this).find(":input").attr('checked') && $(this).data('ticked') !== true) {
                    if(mark){
                        $(this).find('.answer').addClass("incorrect");
                        $(this).find('.answer').removeClass("placeholder");
                    }else{
                        $(this).find('.answer').addClass("placeholder");
                        $(this).find('.answer').removeClass("incorrect");
                    }
                }
            }else {
                //multiChoice Question
                //check answer of student
                if (($(this).find(":input").attr('checked') && $(this).data('ticked') === true) ||
                    (!$(this).find(":input").attr('checked') && $(this).data('ticked') !== true)) {
                    if(mark){
                        $(this).find('.answer').addClass("correct");
                        $(this).find('.answer').removeClass("placeholder");
                    }else{
                        $(this).find('.answer').addClass("placeholder");
                        $(this).find('.answer').removeClass("correct");
                        $(this).find('.answer').removeClass("incorrect");
                    }
                } else {
                    if(mark){
                        $(this).find('.answer').addClass("incorrect");
                        $(this).find('.answer').removeClass("placeholder");
                    }else{
                        $(this).find('.answer').addClass("placeholder");
                        $(this).find('.answer').removeClass("incorrect");
                        $(this).find('.answer').removeClass("correct");
                    }
                }
            }
        });

        $('.ws-open-question-solution').toggle(mark);
    }

    function logInPopUp(){
        var login = $('.wsf-worksheet-title').data('login'),
            loginURL = $('.wsf-worksheet-title').data('loginurl');

        if(login){
            var redirect = window.confirm(login);
            if(redirect){
                window.location = loginURL;
            } else {
                $('.button.save').hide();
            }
        }
    }

    function setData(d) {
        general.setData(d);
    }

    function postProcessApplet(container) {
        if (container) {
           initAppletControls(container);
           general.adjustContentToResize(container.parents(".ws-element-applet, .ws-element-exercise"));
        }
    }

    return {
        init: init,
        setData : setData,
        initNewWorksheet: initNewWorksheet,
        postProcessApplet: postProcessApplet
    };

})(jQuery, GGBT_wsf_general);

jQuery(document).ready(function() {
    "use strict";
    GGBT_wsf_view.init();
});


/*global $, jQuery, console, alert, GGBApplet, renderGGBElement, GGBT_wsf_general*/


window.GGBT_wsf_general = (function($) {
    "use strict";

    var wsfInfo = null,
        wsfInfoContent = null,
        pageInfoState = false,
        wsfActiveContent = null,
        wsfCurrentContent = null,
        wsfButtonInfoClose = null,
        WSF_CLIENT_ID = 0,
        CLASS_TEXT = "wsf-text",
        CLASS_VIDEO = "wsf-video",
        CLASS_APPLET = "wsf-applet",
        CLASS_IMAGE = "wsf-image",
        CLASS_PDF = "wsf-pdf",
        CLASS_META = "wsf-meta",
        wsfActiveInfo,
        wsfWorksheet = null,
        data = null,
        defaults;

    function getWsfInfo() {
        if (wsfInfo === null) {
            wsfInfo = $(".wsf-info-wrapper");
        }
        return wsfInfo;
    }

    function getWsfInfoContent() {
        if (wsfInfoContent === null) {
            wsfInfoContent = $("#wsf-content-info");
        }
        return wsfInfoContent;
    }


    function getWorksheet() {
        if (wsfWorksheet === null) {
            wsfWorksheet = $(".wsf-ws-scroller");
        }
        return wsfWorksheet;
    }

    function getAllWorksheets() {
        return $(".wsf-ws-scroller");
    }

    function setWorksheet(worksheet) {
        if (worksheet.has('.wsf-ws-scroller').length > 0) {
            worksheet = $('.wsf-ws-scroller', worksheet);
        }

        wsfWorksheet = worksheet;
    }

    function getButtonInfoClose() {
        if (wsfButtonInfoClose === null) {
            wsfButtonInfoClose = $(".wsf-button-information-close");
        }
        return wsfButtonInfoClose;
    }

    function setWsfCurrentContent(currentContent) {
        wsfCurrentContent = currentContent;
    }

    function generateClientId() {
        return "wsf_client_" + WSF_CLIENT_ID++;
    }

    function saveContentClientId(content, element) {
        if (!element.clientId) {
            element.clientId = generateClientId();
        }
        content.attr("data-content-client_id", element.clientId);
    }

    function setInfoTitle(title) {
        $("#wsf-info-title").text(defaults.info.title.text + (title !== "" ? ": " + title : ""));
    }

    function getHTMLFromBBCode (bbdata,skiplt) {
        if (bbdata === null) {
            return '';
        }

        var usedTags = ["bold","italic","underline","strike","sup","sub","img","video","link","bullist","numlist","fontcolor","fontsize","fontfamily","justifyleft","justifycenter","justifyright","quote","code","table","math","fs_verysmall", "fs_small", "fs_normal", "fs_big", "fs_verybig"],
            allTags = {
            math: {
                transform: {
                    '<span class="mathquill-embedded-latex">{SELTEXT}</span>':'[math]{SELTEXT}[/math]'

                }
            },
            bold : {
                    transform : {
                    '<b>{SELTEXT}</b>':"[b]{SELTEXT}[/b]",
                        '<strong>{SELTEXT}</strong>':"[b]{SELTEXT}[/b]"
                }
            },
            italic : {
                    transform : {
                    '<i>{SELTEXT}</i>':"[i]{SELTEXT}[/i]",
                        '<em>{SELTEXT}</em>':"[i]{SELTEXT}[/i]"
                }
            },
            underline : {
                    transform : {
                    '<u>{SELTEXT}</u>':"[u]{SELTEXT}[/u]"
                }
            },
            strike : {
                    transform : {
                    '<strike>{SELTEXT}</strike>':"[s]{SELTEXT}[/s]",
                        '<s>{SELTEXT}</s>':"[s]{SELTEXT}[/s]"
                }
            },
            sup : {
                    transform : {
                    '<sup>{SELTEXT}</sup>':"[sup]{SELTEXT}[/sup]"
                }
            },
            sub : {
                    transform : {
                    '<sub>{SELTEXT}</sub>':"[sub]{SELTEXT}[/sub]"
                }
            },
            link : {
                transform : {
                    '<a href="{URL}">{SELTEXT}</a>':"[url={URL}]{SELTEXT}[/url]",
                        '<a href="{URL}">{URL}</a>':"[url]{URL}[/url]"
                }
            },
            img : {
                transform : {
                    '<img src="{SRC}" />':"[img]{SRC}[/img]",
                        '<img src="{SRC}" width="{WIDTH}" height="{HEIGHT}"/>':"[img width={WIDTH},height={HEIGHT}]{SRC}[/img]"
                }
            },
            bullist : {
                    transform : {
                    '<ul class="bbcode-list">{SELTEXT}</ul>':"[list]{SELTEXT}[/list]",
                        '<li>{SELTEXT}</li>':"[*]{SELTEXT}[/*]"
                }
            },
            numlist : {
                    transform : {
                    '<ol class="bbcode-list">{SELTEXT}</ol>':"[list=1]{SELTEXT}[/list]",
                        '<li>{SELTEXT}</li>':"[*]{SELTEXT}[/*]"
                }
            },
            quote : {
                    transform : {
                    '<blockquote>{SELTEXT}</blockquote>':"[quote]{SELTEXT}[/quote]"
                }
            },
            code : {
                    transform : {
                    '<code>{SELTEXT}</code>':"[code]{SELTEXT}[/code]"
                }
            },
            offtop : {
                    transform : {
                    '<span style="font-size:10px;color:#ccc">{SELTEXT}</span>':"[offtop]{SELTEXT}[/offtop]"
                }
            },
            fontcolor: {
                    transform: {
                    '<font color="{COLOR}">{SELTEXT}</font>':'[color={COLOR}]{SELTEXT}[/color]'
                }
            },
            table: {
                    transform: {
                    '<td>{SELTEXT}</td>': '[td]{SELTEXT}[/td]',
                        '<tr>{SELTEXT}</tr>': '[tr]{SELTEXT}[/tr]',
                        '<table class="wbb-table">{SELTEXT}</table>': '[table]{SELTEXT}[/table]'
                },
                skipRules: true
            },
            fontsize: {
                type: 'select',
                    options: "fs_verysmall,fs_small,fs_normal,fs_big,fs_verybig"
            },
            fontfamily: {
                    transform: {
                    '<font face="{FONT}">{SELTEXT}</font>':'[font={FONT}]{SELTEXT}[/font]'
                }
            },
            justifyleft: {
                    transform: {
                    '<p style="text-align:left">{SELTEXT}</p>': '[left]{SELTEXT}[/left]'
                }
            },
            justifyright: {
                    transform: {
                    '<p style="text-align:right">{SELTEXT}</p>': '[right]{SELTEXT}[/right]'
                }
            },
            justifycenter: {
                    transform: {
                    '<p style="text-align:center">{SELTEXT}</p>': '[center]{SELTEXT}[/center]'
                }
            },
            video: {
                transform: {
                    '<iframe src="http://www.youtube.com/embed/{SRC}" width="640" height="480" frameborder="0"></iframe>':'[video]{SRC}[/video]'
                }
            },

            //select options
            fs_verysmall: {
                    transform: {
                    '<font size="1">{SELTEXT}</font>':'[size=50]{SELTEXT}[/size]'
                }
            },
            fs_small: {
                    transform: {
                    '<font size="2">{SELTEXT}</font>':'[size=85]{SELTEXT}[/size]'
                }
            },
            fs_normal: {
                    transform: {
                    '<font size="3">{SELTEXT}</font>':'[size=100]{SELTEXT}[/size]'
                }
            },
            fs_big: {
                    transform: {
                    '<font size="4">{SELTEXT}</font>':'[size=150]{SELTEXT}[/size]'
                }
            },
            fs_verybig: {
                    transform: {
                    '<font size="6">{SELTEXT}</font>':'[size=200]{SELTEXT}[/size]'
                }
            }
        },
        systr =  {
            '<br/>':"\n",
            '<br />' : "[br]",
            '<li>' : '[*]',
            '<span class="wbbtab">{SELTEXT}</span>': '   {SELTEXT}'
        },
        customRules = {
            td: [["[td]{SELTEXT}[/td]",{seltext: {rgx:false,attr:false,sel:false}}]],
                tr: [["[tr]{SELTEXT}[/tr]",{seltext: {rgx:false,attr:false,sel:false}}]],
                table: [["[table]{SELTEXT}[/table]",{seltext: {rgx:false,attr:false,sel:false}}]]
            //blockquote: [["   {SELTEXT}",{seltext: {rgx:false,attr:false,sel:false}}]]
        },
        smileList = [
            //{title:CURLANG.sm1, img: '<img src="{themePrefix}{themeName}/img/smiles/sm1.png" class="sm">', bbcode:":)"},
        ],
            attrWrap = ['src','color','href']; //use becouse FF and IE change values for this attr, modify [attr] to _[attr]

        function keysToLower(o) {
            $.each(o,function(k,v) {
                if (k!==k.toLowerCase()) {
                    delete o[k];
                    o[k.toLowerCase()]=v;
                }
            });
            return o;
        }

        function strf(str,data) {
            data = keysToLower($.extend({},data));
            return str.replace(/\{([\w\.]*)\}/g, function (str, key) {key = key.toLowerCase();var keys = key.split("."), value = data[keys.shift().toLowerCase()];$.each(keys, function () { value = value[this]; }); return (value === null || value === undefined) ? "" : value;});
        }

        if (!skiplt) {bbdata = bbdata.replace(/</g,"&lt;").replace(/\{/g,"&#123;").replace(/\}/g,"&#125;");}
        bbdata = bbdata.replace(/\[code\]([\s\S]*?)\[\/code\]/g,function(s) {
            s = s.substr("[code]".length,s.length-"[code]".length-"[/code]".length).replace(/\[/g,"&#91;").replace(/\]/g,"&#93;");
            return "[code]"+s+"[/code]";
        });


        $.each(usedTags,function(i,b){
                var find=true;
                if (!allTags[b] || !allTags[b].transform) {
                    console.log("unknown tag?");
                    return true;
                }

                $.each(allTags[b].transform,function(html,bb) {
                    html = html.replace(/\n/g,""); //IE 7,8 FIX
                    var a=[];
                    bb = bb.replace(/(\(|\)|\[|\]|\.|\*|\?|\:|\\|\\)/g,"\\$1");
                    //.replace(/\s/g,"\\s");
                    bb = bb.replace(/\{(.*?)(\\\[.*?\\\])*\}/gi,function(str,s,vrgx) {
                        a.push(s);
                        if (vrgx) {
                            //has validation regexp
                            vrgx = vrgx.replace(/\\/g,"");
                            return "("+vrgx+"*?)";
                        }
                        return "([\\s\\S]*?)";
                    });
                    var n=0,am;
                    while ((am = (new RegExp(bb,"mgi")).exec(bbdata)) !== null) {
                        if (am) {
                            var r={};
                            $.each(a,function(i,k) {
                                r[k]=am[i+1];
                            });
                            var nhtml = html;
                            nhtml = nhtml.replace(/\{(.*?)(\[.*?\])\}/g,"{$1}");
                            nhtml = strf(nhtml,r);
                            bbdata = bbdata.replace(am[0],nhtml);
                        }
                    }
                });
        });

        //transform system codes
        $.each(systr,function(html,bb) {
            bb = bb.replace(/(\(|\)|\[|\]|\.|\*|\?|\:|\\|\\)/g,"\\$1")
                .replace(" ","\\s");
            bbdata = bbdata.replace(new RegExp(bb,"g"),html);
        });


        //var $wrap = $(elFromString("<div>"+bbdata+"</div>"));
        //transform smiles
        /* $wrap.contents().filter(function() {return this.nodeType==3}).each($.proxy(smilerpl,this)).end().find("*").contents().filter(function() {return this.nodeType==3}).each($.proxy(smilerpl,this));

         function smilerpl(i,el) {
         var ndata = el.data;
         $.each(this.options.smileList,$.proxy(function(i,row) {
         var fidx = ndata.indexOf(row.bbcode);
         if (fidx!=-1) {
         var afternode_txt = ndata.substring(fidx+row.bbcode.length,ndata.length);
         var afternode = document.createTextNode(afternode_txt);
         el.data = ndata = el.data.substr(0,fidx);
         $(el).after(afternode).after(this.strf(row.img,this.options));
         }
         },this));
         } */
        //this.getHTMLSmiles($wrap);
        //$wrap.contents().filter(function() {return this.nodeType==3}).each($.proxy(this,smileRPL,this));

        //return $wrap.html();

        return bbdata;
    }

    function adjustContentToResize(content) {
        var scale = (content.find("article").attr("data-param-scale") || 1),
            width = parseInt(content.find("article").attr("data-param-width") * scale),
            height = parseInt(content.find("article").attr("data-param-height") * scale);
        content.height(height).width(width);
    }

    function buildYouTubeVideo(link) {
        var videoId;
        if (link.indexOf("www.youtube.com") > -1) {
            videoId = link.split("=")[1];
        } else {
            videoId = link.substring(link.lastIndexOf("/"));
        }
        return '<iframe class="video" width="560" height="315" src="//www.youtube.com/embed/' + videoId + '" frameborder="0" allowfullscreen></iframe>';
    }

    function buildVimeoVideo(link) {
        var videoId = /vimeo\.com\/(\d+)/.exec(link)[1];
        return '<iframe class="video" width="560" height="315" src="//player.vimeo.com/video/' + videoId + '" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>';
    }

    function buildHtml5Video(link) {
        var type;
        if (link.indexOf(".ogv") > -1 || link.indexOf(".ogg") > -1) {
            type = "ogg";
        } else if (link.indexOf(".mp4") > -1) {
            type = "mp4";
        }
        return '<video class="video" type="video/' + type + '" width="560" height="315" src="' + link + '" />';
    }

    function buildVideoFromLink(link) {
        if (!link) {
            return null;
        }
        var content;
        if (link.indexOf("www.youtube.com") > -1 || link.indexOf("youtu.be") > -1) {
            content = buildYouTubeVideo(link);
        } else if (link.indexOf("vimeo.com") > -1) {
            content = buildVimeoVideo(link);
        } else {
            content = buildHtml5Video(link);
        }
        return $(content).attr("data-link", link);
    }

    function buildVideoContent(title, video, copy) {
        var wrapper = $('<li class="' + CLASS_VIDEO +  ' wsf-content-added">' +
                '<h5 class="content-added-title">' + title + '</h5></li>'),
            content = $('<div class="content-added-content" />'),
            vid;

        if (copy) {
            vid = video.clone();
        } else {
            vid = video;
        }
        content.append(vid);
        wrapper.append(content).appendTo(wsfCurrentContent);
        return wrapper;
    }

    function buildImageContent(title, image, copy) {
        var wrapper = $('<li class="' + CLASS_IMAGE +  ' wsf-content-added">' +
                '<h5 class="content-added-title">' + title + '</h5></li>'),
            content = $('<div class="content-added-content" />'),
            img;
        if (copy) {
            img = image.clone();
        } else {
            img = image;
        }
        content.append(img);
        wrapper.append(content).appendTo(wsfCurrentContent);
        return wrapper;
    }

    function buildTextContent(title, bbcode) {
        var html = getHTMLFromBBCode(bbcode);
        var content = $('<li class="' + CLASS_TEXT +  ' wsf-content-added">' +
            '<h5 class="content-added-title">' + title + '</h5>' +
            '<div class="content-added-content" data-text-bbcode="' + encodeURIComponent(bbcode) + '">' + html + '</div>' +
            '</li>').appendTo(wsfCurrentContent);
        $('.mathquill-embedded-latex', content).mathquill();
        return content;
    }

    function buildAppletContent(title, applet, copy) {
        var wrapper = $('<li class="' + CLASS_APPLET +  ' wsf-content-added">' +
                '<h5 class="content-added-title">' + title + '</h5></li>'),
            content = $('<div class="content-added-content" />'),
            ap;
        if (copy) {
            ap = applet.clone();
        } else {
            ap = applet;
        }
        if (ap !== null) {
            content.append(ap);
        }
        wrapper.append(content).appendTo(wsfCurrentContent);
        if (copy) {
            renderGGBElement(ap.get(0), function() {
                adjustContentToResize(content);
            });
        }
        return wrapper;
    }

    function populateTextContent(element) {
        var content = buildTextContent(element.title, element.text);
        saveContentMetaDataId(content, element);
        return content;
    }

    function addAppletOnLoad(params, content) {
        params.appletOnLoad = function() {
            adjustContentToResize(content);
        };
    }

    function populateAppletContent(element) {
        var content = buildAppletContent(element.title, null),
            applet,
            params;
        if (element.parameters) {
            params = element.parameters;
        } else {
            params = {};
        }
        addAppletOnLoad(params, content.find(".content-added-content"));
        if (element.base64) {
            params.ggbBase64 = element.base64;
            applet = GGBApplet(params);
        } else if (element.sharing_key) {
            params.material_id = element.sharing_key;
            applet = GGBApplet(params);
        } else if (element.material_id) {
            params.material_id = element.material_id;
            applet = GGBApplet(params);
        }
        if (!params.preferredAppletType) {
            params.preferredAppletType = "auto";
        }
        applet.inject(content.find(".content-added-content").get(0), params.preferredAppletType);
        saveContentMetaDataId(content,  element);

        return content;
    }

    function populateVideoContent(element) {
        var video = buildVideoFromLink(element.link),
            content = buildVideoContent(element.title, video);
        saveContentMetaDataId(content,  element);
        return content;
    }

    function populateImageContent(element) {
        var img = new Image(),
            content;
        img.src = element.link || element.base64;
        content = buildImageContent(element.title, img);
        saveContentMetaDataId(content,  element);
        return content;
    }

    function saveContentMetaDataId(content, element) {
        content.attr("data-content-meta_id", element.id);
        populateElementInfoContent(element,  content);
    }

    function populateContent(element) {
        var content = null;
        switch (element.type) {
            case "text":
                content = populateTextContent(element);
                break;
            case "applet":
                content = populateAppletContent(element);
                break;
            case "video":
                content = populateVideoContent(element);
                break;
            case "image":
                content = populateImageContent(element);
                break;
            default:
                console.log("unknown applet type");
        }
        return content;
    }


    function updatePageInfoContent() {
        if (data.info_worksheet !== null) {
            getWorksheet().data("wsf-info", JSON.stringify(data.info_worksheet));
        }
    }

    function updateElementInfoContent(element, content) {
        if (element.info_worksheet !== null) {
            content.data("wsf-info", JSON.stringify(element.info_worksheet));
        }
    }


    function populatePageInfoContent() {
        updatePageInfoContent();
    }

    function populateElementInfoContent(element,  content) {
        updateElementInfoContent(element,  content);
    }

    function sortByOrder(elements) {
        elements.sort(function(a, b) {
            return a.order - b.order;
        });
    }

    function loadInfoContent(elements) {
        var i,
            l;
        if (elements && elements.length) {
            setWsfCurrentContent(wsfInfoContent);
            sortByOrder(elements);
            for (i = 0, l = elements.length; i < l; i++) {
                if (window.GGBT_wsf_edit !== undefined) {
                    saveContentClientId(populateContent(elements[i]), elements[i]);
                } else {
                    populateContent(elements[i]);
                }
            }
        }
    }

    function loadElementInfoContent() {
        var data_attr = getWsfActiveContent().data("wsf-info"),
            data_obj;
        if (data_attr) {
            if (typeof data_attr === "string") {
                data_obj = JSON.parse(data_attr);
            } else {
                data_obj = data_attr;
            }
        }
        clearInfoContent();
        if (data_obj && data_obj.elements) {
            loadInfoContent(data_obj.elements);
            getWsfActiveContent().data("wsf-info", JSON.stringify(data_obj));
        }
        setPageInfoState(false);
        wsfActiveInfo = getWsfActiveContent();
    }

    function closeInfoFromView() {
        getAllWorksheets().removeClass("info-shown");
        getWsfInfo().removeClass("shown");
        //$(".wsf-teacher-info-button").fadeIn("fast");
        getAllWorksheets().animate({
            //width: $(window).width() + "px"
            width: "100%"
        }, 1000);
        /*getWsfInfo().animate({
            width: "0px"
        }, 1000);*/
        //getButtonInfoClose().fadeOut("fast");
        getAllWorksheets().removeClass("fullscreen");
        getWsfInfo().removeClass("teacher-info");
        lastOpenedInfo = null;

        //if view mode show again all info buttons
        /*if (typeof GGT_wsf_view !== undefined) {
            $(".wsf-element-info-button").fadeIn("fast");
        }*/
        if (typeof GGT_wsf_view !== undefined) {
            $(".wsf-element-info-button").removeClass("selected");
            $(".wsf-teacher-info-button").removeClass("selected");
        }

        $(window).resize();
    }

    function onSwitchWorksheet(worksheet) {
        var oldWorksheet = getWorksheet();
        setWorksheet(worksheet);

        // If the info is open, load the info of the new worksheet
        if (oldWorksheet && oldWorksheet.hasClass("info-shown")) {
            oldWorksheet.removeClass("info-shown");
            initTeacherInfoPage($(".wsf-teacher-info-button"));
        }
    }

    function initTeacherInfoPage(button) {
        var currentlyClickedInfo,
            titleToShow;
        currentlyClickedInfo = button;

        titleToShow = $(".wsf-worksheet-title", getWorksheet()).val() ? $(".wsf-worksheet-title", getWorksheet()).val() : $(".wsf-worksheet-title", getWorksheet()).text();

        setInfoTitle(titleToShow);

        if(getLastOpenedInfo() === getInfoButtonID(currentlyClickedInfo)) {
            closeInfoFromView();
        } else {
            openInfoPage(currentlyClickedInfo);

            //$(".wsf-teacher-info-button").fadeOut("fast");
            //getButtonInfoClose().css({top: '19px'});
            getWsfInfo().addClass("teacher-info");
        }
    }

    function initElementInfoPage(button) {
        var currentlyClickedInfo = button,
            titleToShow = "",
            needToSave;

        if (getWsfActiveContent() !== null && getWsfActiveContent().has(".content-added-title")) {
            titleToShow = getWsfActiveContent().find(".content-added-title").text().trim();
        }

        setInfoTitle(titleToShow);

        //if(currentlyClickedInfo.get(0) === getLastOpenedInfo()) {
        if(getInfoButtonID(currentlyClickedInfo) === getLastOpenedInfo()) {
            closeInfoFromView();
            needToSave = true;
        } else {
            //getButtonInfoClose().css({top: (button.offset().top - $(".wsf-wrapper").offset().top - 7) + 'px'});
            //$(".wsf-teacher-info-button").fadeIn("fast");
            openInfoPage(currentlyClickedInfo);
            getWsfInfo().removeClass("teacher-info");
        }
        return needToSave;
    }



    function setPageInfoState(state) {
        pageInfoState  = state;
    }

    function isPageInfoState() {
        return pageInfoState;
    }

    function loadPageInfoContent() {
        var data_attr = getWorksheet().data("wsf-info"),
            data_obj;
        if (data_attr) {
            if (typeof data_attr === "string") {
                data_obj = JSON.parse(data_attr);
            } else {
                data_obj = data_attr;
            }
        }
        clearInfoContent();
        if (data_obj && data_obj.elements) {
            loadInfoContent(data_obj.elements);
            getWorksheet().data("wsf-info", JSON.stringify(data_obj));
        }
        setPageInfoState(true);
    }



    function clearInfoContent() {
        wsfInfoContent.empty();
    }

    function setWsfActiveContent(content) {
        wsfActiveContent = content;
    }

    function getWsfActiveContent() {
        return wsfActiveContent;
    }

    function getWsfCurrentContent() {
        return wsfCurrentContent;
    }

    function setData(d) {
        data = d;
        console.log(d);
    }

    function setDefaults(d) {
        defaults = d;
    }

    function getData() {
        return data;
    }

    function getDefaults() {
        return defaults;
    }


    var infoPageWidth = 830;
    var lastOpenedInfo = null;

    function openInfoPage(currentlyClickedInfo) {
        if ((currentlyClickedInfo.attr("class").search("wsf-teacher-info-button")) >= 0) {
            loadPageInfoContent();
        } else {
            loadElementInfoContent();
        }

        //if view mode hide currently clicked button
        /*if (typeof GGT_wsf_view !== undefined) {
            $(".wsf-element-info-button").fadeIn("fast");
            currentlyClickedInfo.fadeOut("fast");
        }*/
        if (typeof GGT_wsf_view !== undefined) {
            $(".wsf-teacher-info-button").removeClass("selected");
            $(".wsf-element-info-button").removeClass("selected");
            currentlyClickedInfo.addClass("selected");
        }

        //getButtonInfoClose().show();
        getWorksheet().addClass("info-shown");
        getWsfInfo().addClass("shown");
        $(".wsf-info-scroller").scrollTop(0);

        if(lastOpenedInfo === null) {
            if($(window).width() >= 1706) {
                getWorksheet().animate({
                    width: ($(window).width() - infoPageWidth) + "px"
                }, 1000);
            }
        }
        lastOpenedInfo = getInfoButtonID(currentlyClickedInfo);
        $(window).resize();
    }

    function getInfoButtonID(button) {
        var id = button.closest("li.wsf-content-added").data("content-client_id");
        if(id === undefined) {
            id = button.closest(".worksheet_element").attr("id");
        }
        if(id === undefined && (button.attr("class").search("wsf-teacher-info-button") >= 0)) {
            id = "wsf-teacher-info";
        }
        return id;
    }

    function getLastOpenedInfo() {
        return lastOpenedInfo;
    }


    $(window).resize(function() {
        // If there is enough space to place the teacher info on the right side of the worksheet, do so.
        // Otherwise the teacher info should be the whole screen.
        if(getWsfInfo().hasClass("shown")) {
            if ($(window).width() < 1706) {
                getWsfInfo().addClass("fullscreen");
                // display correct width of wsf-ws-scroller (worksheet)
                wsfWorksheet.addClass("fullscreen");
                //$("#wsf-ws-scroller").width($(window).width());
            } else {
                getWsfInfo().removeClass("fullscreen");
                // display correct width of wsf-ws-scroller (worksheet)
                wsfWorksheet.removeClass("fullscreen");
                wsfWorksheet.width($(window).width() - infoPageWidth);
            }

            $(".wsf-info-scroller").css({height: $(window).height() + 'px'});
        } else {
            getWorksheet().css({width: '100%'});
            //getWsfInfo().css({width: '0px'});
        }

        // set correct height for worksheet
        if (!window.GGBT_book_general) {
            wsfWorksheet.css({height: $(window).height() + 'px'});
        }
    });

    function getWsfActiveInfo() {
        return wsfActiveInfo;
    }

    function setWsfActiveInfo(info) {
        wsfActiveInfo = info;
    }

    function init() {
        getWorksheet();
        getWsfInfoContent();
        getWsfInfo();
        getButtonInfoClose();
        $(window).resize();
    }

    return {
        getWsfInfo : getWsfInfo,
        getWsfInfoContent: getWsfInfoContent,
        clearInfoContent : clearInfoContent,
        setPageInfoState: setPageInfoState,
        isPageInfoState: isPageInfoState,
        getWsfActiveContent: getWsfActiveContent,
        setWsfActiveContent: setWsfActiveContent,
        populateContent: populateContent,
        getWsfCurrentContent: getWsfCurrentContent,
        setWsfCurrentContent: setWsfCurrentContent,
        buildTextContent: buildTextContent,
        buildAppletContent: buildAppletContent,
        buildImageContent: buildImageContent,
        buildVideoContent: buildVideoContent,
        populatePageInfoContent: populatePageInfoContent,
        getData: getData,
        setData: setData,
        setDefaults: setDefaults,
        getDefaults: getDefaults,
        getButtonInfoClose: getButtonInfoClose,
        getWorkSheet: getWorksheet,
        setWorksheet: setWorksheet,
        openInfoPage: openInfoPage,
        closeInfoFromView: closeInfoFromView,
        setInfoTitle: setInfoTitle,
        initTeacherInfoPage: initTeacherInfoPage,
        initElementInfoPage: initElementInfoPage,
        getLastOpenedInfo: getLastOpenedInfo,
        getWsfActiveInfo: getWsfActiveInfo,
        setWsfActiveInfo: setWsfActiveInfo,
        saveContentClientId: saveContentClientId,
        generateClientId: generateClientId,
        init: init,
        onSwitchWorksheet : onSwitchWorksheet,
        getHTMLFromBBCode : getHTMLFromBBCode,
        adjustContentToResize: adjustContentToResize,

        CLASS_TEXT: CLASS_TEXT,
        CLASS_APPLET: CLASS_APPLET,
        CLASS_IMAGE: CLASS_IMAGE,
        CLASS_META: CLASS_META,
        CLASS_PDF: CLASS_PDF,
        CLASS_VIDEO: CLASS_VIDEO
    };
})(jQuery);

jQuery(document).ready(function() {
    "use strict";
    GGBT_wsf_general.init();
});
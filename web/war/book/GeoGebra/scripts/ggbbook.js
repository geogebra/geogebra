var swiper;
window.GGBT_book_general = true;
$(function($) {
    swiper = initSwipe($);
    showNavMenu($, navVisible, false, false);

    // Load all worksheets as screenshots
    $('.page.worksheet').each(function() {
        loadAppletForPage($, $(this), true);
    });

    // Select the chapter or page from the hash
    processHash($);

    // Switch page when a link on the chapter page was clicked.
    $('.page.submenu li').click(function(event) {
        if (! swiper.inSwipe) {
            event.stopPropagation();
            switchToPage($, $('#'+$(this).data('page-id')), false);
        }
    });

    // Switch the chapter when a menu item is clicked
    $('#menu li .menu-item').click(function() {
        var pageId = "submenu_"+getIdFromMenuItem($, $(this).parent().parent());
        switchToPage($, $('#'+pageId), true);
    });

    // Switch the worksheet when a submenu item was clicked
    $('#menu .submenu-items li').click(function() {
        var pageId = "worksheet_"+$(this).attr("id");
        switchToPage($, $('#'+pageId), true);
    });

    // Hide/Show Navigation when clicking on the bookmark arrow
    $('#menu .bookmark-arrow, #bookmark-overlay').click(function() {
        if(!navVisible && window.GGBT_wsf_general) {
            window.GGBT_wsf_general.closeInfoFromView();
        }

        showNavMenu($, !navVisible, true);
    });

    if (getIEVersion() < 10) {
        $('.submenu li').hover(null, function() {
            $('#menuBg').css("box-shadow", "1px 0px 3px #F7F7F7");
        });
    }

    $('.nav_next').click(function() {
        switchToPage($, activePage.next('.page'), true, false);
        return false;
    });
    $('.nav_prev').click(function() {
        switchToPage($, activePage.prev('.page'), true, false);
        return false;
    });
    $('.nav_overview').click(function() {
        if (activePage.hasClass("submenu")) {
            // Switch to title
            switchToPage($, $('#submenu_title'), true);
        } else {
            // Switch to chapter overview/title
            var chapterPage = activePage.prevAll('.submenu').first();
            switchToPage($, chapterPage, true);
        }
    });

    $(window).resize(function() {
        initHeight($);
    });

    $(window).on('hashchange', function() {
        if (!inUpdateHash) {
            processHash($);
        }
    });

    $('.page').scroll(function() {
        swiper.scrollToActivePage(false);
        refreshAppletHitPoints($);
    });

    $('#menu').scroll(function() {
        updateBookMarkArrow($);
        updateBookMarkOverlay($, true);
    });

});

$(window).on("load", function() {
    initHeight(jQuery);
});

function bookHandleMathJaxLoad() {
    // Workaround for scrolling problem with mathjax on IE
    // Ensure that scroll position stays the same for the next second
    var oriPageOffset = activePage.offset();
    var count = 0;
    function checkPos() {
        if (activePage.scrollTop() != 0 || $('#page_container').scrollLeft() != 75) { //} || $("#page_container").offset().left != 300) {
            activePage.scrollTop(0);
            initHeight($, false);
        }
        if (++count < 20)
            setTimeout(checkPos, 40);
    };
    checkPos();
}

var activePage = null;
var activeApplet = null;
var preLoadAppletCount = 1;
function switchToPage($, pageElem, animate, animateScroll) {
    if (animateScroll == null) animateScroll = false;

    if ((activePage != null && pageElem[0] == activePage[0]) || ! pageElem.hasClass("page")) {
        swiper.scrollToActivePage(animateScroll);
        return;
    }

    // Switch to new page
    activePage = pageElem;

    if (!animateScroll) {
        // Hide the nav menu for worksheets
        if (activePage.hasClass("worksheet") && navVisible) {
            showNavMenu($, false, false, false);
        }
        $('body').css({visibility:"visible"});
    }

    initHeight($, animateScroll);

    // Load the page from the server
    if (activePage.hasClass("ajax-toload")) {
        lazyLoadPage($, activePage, continueSwitch);
    } else {
        if (animateScroll) {
            // Wait until the scroll to the new page has finished before loading the applet
            setTimeout(continueSwitch, 310);
        } else {
            continueSwitch();
        }
    }

    function continueSwitch() {
        if (animateScroll) {
            if (activePage.hasClass("worksheet") && navVisible) {
                showNavMenu($, false, false, false);
            }
            $('body').css({visibility:"visible"});
        }

        var preLoadAllowed = false;
        if (activePage.hasClass("worksheet")) {
            sel_material_id = getMaterialIdFromPage($, activePage);
            sel_chapter_id = null;
        } else {
            sel_material_id = null;
            sel_chapter_id = activePage.attr("id").substr(8);
        }

        // Select menu item
        markMenuItemSelected($, navVisible ? animate : false, false);

        // Update navigation items
        $('.nav_prev', activePage).toggleClass("inactive", ! activePage.prev().hasClass("page"));
        $('.nav_next', activePage).toggleClass("inactive", ! activePage.next().hasClass("page"));

        var nextPage = activePage.nextAll('.worksheet').first();

        // Remove all loaded applets (except from the new and the next page)
        $('.worksheet.loaded').not(activePage).each(function() {
            if ((activePage.hasClass("worksheet") && preLoadAppletCount == 0) || this != nextPage[0]) {
                removeAppletForPage($, $(this));
            } else {
                startAppletAnimationForPage($, $(this), false);
            }
        });

        updateHash($);

        if (activePage.hasClass("worksheet")) {

            var newApplet = getAppletForPage($, activePage, sel_material_id);

            var appletType = window["appletType_"+sel_material_id];

            // Check if the new applet uses a different HTML 5 source and reload the page
            if (!(newApplet instanceof Array) && activeApplet != null && newApplet != null) {
                if (ggbHTML5LoadedCodebaseVersion != null && (ggbHTML5LoadedCodebaseVersion != newApplet.getHTML5CodebaseVersion() ||
                    (newApplet.getViews().is3D && ggbHTML5LoadedScript.indexOf("web3d") == -1))) {

                    if (appletType.indexOf("java") == -1 || !newApplet.isJavaInstalled()) {

                        // The page has to be reloaded
                        location.reload();
                    }
                }
            }

            // Load the applet of the new page
            if (! bookIsOffline) {
                gaTrackMaterialView(sel_material_id, GA_MATERIAL_VIEW_BOOK);
            }
            loadAppletForPage($, activePage, false, sel_material_id);

            activeApplet = newApplet;

//            if (nextPage.length > 0) {
//                var nextApplet = getAppletForPage($, nextPage);
//                preLoadAllowed = (nextApplet != null && ggbHTML5LoadedCodebaseVersion == nextApplet.getHTML5CodebaseVersion() && preLoadAppletCount > 0);
//                preLoadAllowed = (nextApplet != null && preLoadAppletCount > 0 && nextAppletType.indexOf("compiled") > -1)
//            }

            refreshAppletHitPoints($);

            // Start the animation for the active applet
            //startAppletAnimationForPage($, activePage, true);

        }

        // load the next and the previous pages
        if (nextPage.hasClass("ajax-toload")) {
            lazyLoadPage($, nextPage, continuePreloadNextPage);
        } else {
            continuePreloadNextPage();
        }
        var prevPage = activePage.prevAll('.worksheet').first();
        if (prevPage.hasClass("ajax-toload")) {
            lazyLoadPage($, prevPage);
        }

        // Switch the active flexible worksheet
        if (activePage.hasClass("worksheet")) {
            if (window.GGBT_wsf_general) {
                window.GGBT_wsf_general.onSwitchWorksheet(activePage);

                activePage.append($('.wsf-info-wrapper'));
            }
        }

        function continuePreloadNextPage() {
            var page = nextPage;

            // check if preloading of the page is allowed
            if (page.length > 0) {
                var appletType = window["appletType_"+getMaterialIdFromPage($, page)];
                var applet = getAppletForPage($, page);

                // Preload the worksheet
                if (!applet instanceof Array && applet != null && preLoadAppletCount > 0 && appletType.indexOf("compiled") > -1) {
                    if (activePage.hasClass("worksheet")) {
                        loadAppletForPage($, page, false);
                    } else {
                        setTimeout(function() {loadAppletForPage($, page, false)}, 400);
                    }
                }
            }
        }
    }
}

function lazyLoadPage($, pageElem, onSuccess) {
    pageElem.removeClass("ajax-toload");
    pageElem.addClass("ajax-load");

    $.get(pageElem.data("url"), null, function(resp) {
        pageElem.removeClass("ajax-load");

        // Delete the items from the table
        if (resp.substr(0,5) !== "error") {
            if (pageElem.has('.wsf-wrapper')) {
                $('.wsf-wrapper', pageElem).append(resp);
            } else {
                pageElem.append(resp);
            }

            // Inject the screenshot
            loadAppletForPage($, pageElem, true);

            // Process latex with mathjax
            if (window.MathJax) {
                MathJax.Hub.Process(pageElem[0]);
            }

            if (window.GGBT_wsf_view) {
                window.GGBT_wsf_view.initNewWorksheet(pageElem);
            }

            if (typeof onSuccess === "function") {
                onSuccess();
            }
        } else {
            pageElem.addClass("ajax-toload");
            alert("An error occurred while loading the page from the server. Please refresh the page to try again.");
        }
    });
}

var navVisible = true;
function showNavMenu($, show, animate, animateScroll) {
    if (navVisible == show) return;

    var duration = animate ? 400 : 0;

    var complete = function() {
        initHeight($, animateScroll);
        refreshAppletHitPoints($);
        updateBookMarkOverlay($, true);
    };

    navVisible = show;

    if(show) {
        $('#menuBg').animate( {left: '0px'}, {duration: duration});
        $('#menu').animate( {left: '0px'}, {duration: duration});
        $('.bookmark-arrow').animate({left: '300px'}, {duration: duration});
        $('#page_container').animate( {marginLeft: '300px'}, {duration: duration, complete: complete});
        $('.chapter-number').hide();
    } else {
        if (animate) {
            $('#menuBg').animate( {left: '-300px'}, {duration: duration});
            $('#menu').animate( {left: '-300px'}, {duration: duration});
            $('.bookmark-arrow').animate({left: '0px'}, {duration: duration});
            $('#page_container').animate( {marginLeft: '0'}, {duration: duration, complete: complete});
        } else {
            $('#page_container').css( {marginLeft: '0'});
            $('#menuBg').css( {left: '-300px'});
            $('#menu').css( {left: '-300px'});
            $('.bookmark-arrow').css({left: '0px'});
//            complete();
        }
        $('.chapter-number').show();
    }
}

var isiPad = navigator.userAgent.match(/iPad/i) !== null;
function initHeight($, animateScroll) {
    var windowHeight = $(window).height();

    // Set menu height
    $('#menu').height(windowHeight);
    $('#menuBg').height(windowHeight);

    var pageWidth = $(window).width() - (navVisible ? $('#menuBg').width() : 0);

    $('#page_container').width(pageWidth);
    $('#pages').height(windowHeight);
    $('#page_container').height(windowHeight);

    swiper.setPageWidth(pageWidth);
    //$('.page').width(pageWidth - 90); // The width off all pages is equal
    $('.page').width(pageWidth);

    if (activePage !== null) {
        var pageScroller = (activePage.has('.wsf-ws-scroller').length > 0 ? $('.wsf-ws-scroller', activePage) : activePage);
        var pageScroll = activePage.scrollTop();
        $('.footer', activePage).css({position: 'static'});
        pageScroller.height("auto");
        var pageScrollHeight = pageScroller.height();
        var pageMargin = parseInt(activePage.css('padding-top'));

        // Expand the worksheet to the full height, to show the footer at the bottom of the page
        var pageHeight = windowHeight-pageMargin - (isiPad ? 20 : 0);
        $('.page').height(pageHeight); // All pages have the same height
        pageScroller.height(pageHeight);

        if ((pageScrollHeight+pageMargin) < windowHeight) {
            $('.footer', activePage).css({position: 'absolute', bottom: 0});
        }
        $('.footer', activePage).width($('.pageHeader', activePage).width());
        pageScroller.scrollTop(pageScroll);
        swiper.scrollToActivePage(animateScroll);
    }
}


function refreshAppletHitPoints($) {
    if (activeApplet != null) {
        if (activeApplet instanceof Array) {
            for (i = 0; i < activeApplet.length; ++i) {
                var appletElement = window["applet_"+activeApplet[i]];
                if (typeof appletElement == 'object') {
                    appletElement.refreshHitPoints();
                }
            }
        } else {
            activeApplet.refreshHitPoints();
        }
    }
}

// Selects a menu item by setting the sel class and calling the callback function onSelectMenuItem.
function markMenuItemSelected($, animate, animateScroll) {
    $('#menu li').removeClass("sel"); // remove sel from menu and submenu items

    if (sel_material_id != null) {

        // Select the submenu item (worksheet) and the according main menu item
        var menu = $('.submenu-items #'+sel_material_id);
        menu.parent().parent().parent().parent().addClass("sel");
        menu.addClass("sel");
    } else {

        // Select the menu item (chapter or title)
        $('#menu_'+sel_chapter_id).addClass("sel");
    }

    openSelectedSubMenu($, animate, animateScroll);
}

/**
 * Builds the hash string that stores the current navigation information and changes it in the browser URL
 */
var inUpdateHash = false;
function updateHash($) {
    inUpdateHash = true;
    var hash = buildHashString($, getSelectedMenuId($));
    try {
        parent.location.hash = hash;
    } catch (e) {
        location.hash = hash;
    }
    setTimeout(function() {inUpdateHash = false;}, 50);
}

function buildHashString($, menu_id) {
    if (sel_chapter_id != null && sel_chapter_id != "title") {
        return "#chapter/" + sel_chapter_id;
    } else if (sel_material_id != null) {
        return "#material/" + sel_material_id;
    } else {
        return '';
    }
}

function processHash($) {
    var hash = location.hash;
    try {
        hash = parent.location.hash;
    } catch (e) {}
    var hashes = hash.substr(1).split("/");

    if (hashes[0] == "chapter") {
        sel_chapter_id = parseInt(hashes[1])
        sel_material_id = null;
    } else if (hashes[0] == "material") {
        sel_chapter_id = null;
        sel_material_id = parseInt(hashes[1]);
    } else {
        sel_chapter_id = "title";
        sel_material_id = null;
    }

    if (sel_chapter_id != null) {
        switchToPage($, $('#submenu_'+sel_chapter_id), false);
    } else if (sel_material_id != null) {
        switchToPage($, $('#worksheet_'+sel_material_id), false);
    }
    if (activePage == null) {
        switchToPage($, $('#submenu_title'), false);
    }
}

function loadAppletForPage($, pageElem, onlyScreenshot, material_id) {
    if (!$(pageElem).hasClass('worksheet')) {
        return;
    }
    if (material_id == null) {
        material_id = getMaterialIdFromPage($, pageElem);
    }

    // Hide the applet until loading is complete
    showAppletsForPage($, pageElem, false);

    var applet = window["applet_"+material_id];
    if (applet instanceof Array) {
        for (i = 0; i < applet.length; ++i) {
            var appletElement = window["applet_"+applet[i]];
            if (typeof appletElement == 'object') {
                loadApplet($, appletElement, applet[i], onlyScreenshot, true);
            }
        }
    } else if (applet instanceof Object) {
        loadApplet($, applet, material_id, onlyScreenshot, false);
    } else {
        return;
    }

    if (! onlyScreenshot) {
        pageElem.addClass("loaded");
    }
}

function loadApplet($, applet, material_id, onlyScreenshot, isElement) {

    // Check if the applet is already loaded
    var currentType = applet.getLoadedAppletType();
    if (currentType == 'java' || currentType == 'html5' || currentType == 'compiled' || (currentType == 'screenshot' && onlyScreenshot)) {
        showApplet($, material_id, true)
        return;
    }

    // Find type of applet to load
    var appletType = (onlyScreenshot ? 'screenshot' : window["appletType_"+material_id]);
    if (appletType == undefined)
        appletType = 'auto';

    if (appletType == 'screenshot') {
        var containerId = 'applet_container_preview_'+material_id;
    } else {
        var containerId = 'applet_container_'+material_id;
    }

    // Load the applet
    applet.inject(containerId, appletType, true);

    if (applet.getLoadedAppletType() == 'java') {
        // Show java applet immediately, because they have no onload event.
        showApplet($, material_id, true)
    }

    applet.toggleAppletTypeControls('#worksheet_'+(isElement?'element_':'')+material_id);
}

function showAppletsForPage($, pageElem, show) {
    $(".applet_container", pageElem).toggleClass('showapplet', show);
    $(".applet_container_preview", pageElem).toggleClass('showapplet', show);
}

function showApplet($, material_id, show) {
    $('#applet_container_'+material_id).toggleClass('showapplet', show);
    $('#applet_container_preview_'+material_id).toggleClass('showapplet', show);
}

function ggbAppletOnLoad(id) {
    // Hide the screenshot and move the applet into view
    var container = (jQuery('#'+id).hasClass('applet_container') ? jQuery('#'+id) : jQuery('#'+id).parents('.applet_container'));
    var material_id = getMaterialIdFromAppletContainer(jQuery, container);
    showApplet(jQuery, material_id, true);

    refreshAppletHitPoints(jQuery);

    var page = jQuery('#'+id).parents('.page');
    if ((page[0] != activePage[0])) {
        var applet = window["applet_"+material_id];
        startAppletAnimation(applet, false);
    }
//    bookHandleMathJaxLoad();
}

// This method is called when the first compiled applets are loaded -> Show all loaded applets
function ggbCompiledAppletsOnLoad() {
    jQuery('.worksheet.loaded').each(function() {
        showAppletsForPage(jQuery, jQuery(this), true);
        startAppletAnimationForPage(jQuery, jQuery(this), (this == activePage[0]));
    });
}


function getAppletForPage ($, pageElem, material_id) {
    if (material_id == null) {
        material_id = getMaterialIdFromPage($, pageElem);
    }
    return window["applet_"+material_id];
}

function removeAppletForPage($, pageElem, material_id) {
    if (material_id == null) {
        material_id = getMaterialIdFromPage($, pageElem);
    }
    var applet = window["applet_"+material_id];

    // Check if the applet is already loaded
    if (applet instanceof Array) {
        for (i = 0; i < applet.length; ++i) {
            var appletElement = window["applet_"+applet[i]];
            if (typeof appletElement == 'object') {
                removeApplet($, appletElement, applet[i]);
            }
        }
    } else if (applet instanceof Object) {
        removeApplet($, applet, material_id)
    } else {
        return;
    }

    pageElem.removeClass("loaded");
}

function removeApplet($, applet, material_id) {

    // Remove the applet, but keep the screenshot
    if (applet.getLoadedAppletType() == 'java' || applet.getLoadedAppletType() == 'html5' || applet.getLoadedAppletType() == 'compiled') {
        // Remove the applet, but keep the screenshot
        applet.removeExistingApplet('applet_container_'+material_id, true);
    }
    showApplet($, material_id, false);
}

function startAppletAnimationForPage($, pageElem, start) {
    var material_id = getMaterialIdFromPage($, pageElem);
    var applet = window["applet_"+material_id];

    if (applet instanceof Array) {
        for (i = 0; i < applet.length; ++i) {
            var appletElement = window["applet_"+applet[i]];
            if (typeof appletElement == 'object') {
                startAppletAnimation(appletElement, start);
            }
        }
    } else if (applet instanceof Object) {
        startAppletAnimation(applet, start);
    } else {
        return;
    }
}

function startAppletAnimation(applet, start) {
    if (start) {
        applet.startAnimation();
    } else {
        applet.stopAnimation();
    }
}

function getMaterialIdFromPage($, pageElem) {
    return $(pageElem).attr("id").substr(10);
}

function getMaterialIdFromAppletContainer($, appletContainer) {
    return $(appletContainer).attr("id").substr(17);
}

/**
 * Opens one submenu and closed all others
 * @param $
 * @param animate
 */
function openSelectedSubMenu($, animate, animateScroll) {
    function final() {

        initHeight($, animateScroll);
        updateBookMarkOverlay($);

        // Scroll the  selected menu into view
        var menuItem = $('.menu-opened.sel .menu-item, .menu-closed.sel .menu-item');
        if (menuItem.offset().top + menuItem.height() + 20 > $(window).height())
            menuItem[0].scrollIntoView(false);
    }

    var openedMenu = $('.menu-opened:not(.sel) .menu-wrapper.open');
    if (openedMenu.length>0) {
        var openedTop = openedMenu.offset().top;
        var openedHeight = $('.submenu-items', openedMenu).outerHeight(true);
    }

    // Close all submenues that are not selected
    $('.menu-opened:not(.sel) .submenu-items').hide((animate ? 'blind' : null));
    $('.menu-opened:not(.sel) .menu-wrapper').removeClass('open');

    // Show the selected submenu
    $('.menu-opened.sel .menu-wrapper').addClass('open');
    $('.menu-opened.sel .submenu-items').show((animate ? 'blind' : null), null, null, final);

    // Move the bookmark to the new menu position
    var correction = 0;
    if (openedMenu.length>0 && openedTop != null && openedTop < $('.menu-opened.sel, .menu-closed.sel').offset().top)
        correction = openedHeight * (-1);
    updateBookMarkArrow($, correction);

    // Trigger the height update now if it will not be triggered after the show animation
    if (!animate || $('.menu-opened.sel .submenu-items').length == 0) {
        final();
    }
}


function updateBookMarkArrow($, topCorrection, animate, final) {
    var newBookMarkTop = Math.min(Math.max($('.menu-opened.sel, .menu-closed.sel').offset().top + (topCorrection == null ? 0 : topCorrection), 0), $(window).height() - 65);
    if (animate) {
        $('.bookmark-arrow').animate({top: newBookMarkTop + 'px'}, {duration: animate ? 400 : 0, complete: final});
    } else {
        $('.bookmark-arrow').css({top: newBookMarkTop + 'px'});
    }
}

function updateBookMarkOverlay($, onlyPos) {
    // Move the overlay to the active page
    if (activePage == null) return;
    var overlay = $('#bookmark-overlay');
    if (onlyPos == null || !onlyPos)
        activePage.append(overlay);
    if ($('.bookmark-arrow').length > 0) {
        var arrow = $('.bookmark-arrow').offset();
        overlay.offset({top: arrow.top-10, left: arrow.left});
    }
}

// Helper functions
function getSelectedMenuId($) {
    return getIdFromMenuItem($, $("#menu li.sel"));
}

function getIdFromMenuItem($, item) {
    if ($(item).length>0) {
        return $(item).attr("id").substr(5);
    } else {
        return null;
    }
}
if(typeof String.prototype.trim !== 'function') {
    String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, '');
    }
}

var log = function(text) {
    if ( window.console && window.console.log ) {
        console.log(text);
    }
}

var getIEVersion = function() {
    a=navigator.appVersion;
    return a.indexOf('MSIE')+1?parseFloat(a.split('MSIE')[1]):999
}

//font-styling
if (! bookIsOffline) {
    WebFontConfig = {google: { families: [ 'Muli:300:latin' ] }};
    (function() {
        var wf = document.createElement('script');
        wf.src = ('https:' == document.location.protocol ? 'https' : 'http') + '://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
        wf.type = 'text/javascript';
        wf.async = 'true';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(wf, s);
    })();
}

// Swipe/Touch handling (uses jquery.touchSwipe.min.js)
var inSwipeEvent = false;
function initSwipe($) {
    var swiper = {};
    var PAGE_WIDTH = 800;
    var maxPages=$('.page').length;
    var speed=350;
    var pages = $("#pages");
    var page_container = $("#page_container");

    // Check if we are on Windows metro. If yes, we have to fall back to mouse events.
    var windowsMetro = false;
    if (navigator.appName == 'Microsoft Internet Explorer' && getIEVersion() >= 10) {
        if(window.innerWidth == screen.width && window.innerHeight == screen.height) {
            windowsMetro = true;
        }
    }

    var swipeOptions = {
        triggerOnTouchEnd : true,
        swipeStatus : swipeStatus,
        tap: onTap,
        swipe: onSwipe,
        allowPageScroll:"vertical",
        fallbackToMouseEvents:windowsMetro,
        triggerOnTouchLeave:true,
        threshold:150,
        excludedElements:$.fn.swipe.defaults.excludedElements+", article, applet, canvas, .canvasHolder, #menu, .bookmark-arrow, .nav_next, .nav_prev, #bookmark-overlay, .wsf-info-wrapper"
    }
    pages.swipe( swipeOptions );

    swiper.setPageWidth = function(width) {
        PAGE_WIDTH = width;
    };

    swiper.setPageCount = function(pages) {
        maxPages = pages;
    };

    function onTap(event, target) {
        if ($(target).is('.page.submenu li')) {
            var pageId = $(target).data('page-id');
            switchToPage($, $('#'+pageId), false);
        } else if ($(target).parents('.page.submenu li').length>0) {
            var pageId = $(target).parents('.page.submenu li').data('page-id');
            switchToPage($, $('#'+pageId), false);
//        } else if ($(target).is('.nav_next')) {
//            switchToPage($, activePage.next('.page'), true, false);
        }
    }

    swiper.inSwipe = false;
    var resetInSwipe = function() {
        swiper.inSwipe = false;
    }
    function swipeStatus(event, phase, direction, distance) {
        if (phase=="move" && (direction=="left" || direction=="right") ) {
            // Scroll to the drag position while dragging
            swiper.inSwipe = true;
            if (direction == "left")
                swiper.scrollToActivePage(false, distance);
            else if (direction == "right")
                swiper.scrollToActivePage(false, distance*(-1));
        } else if (phase=="cancel") {
            setTimeout(resetInSwipe, 100);
            swiper.scrollToActivePage(true);
        } else if ( phase == "end" ) {
            setTimeout(resetInSwipe, 100);
            // As the cancel event does not work (bug?) we have to check the thresholds ourself to swipe back on cancel
            if (distance<swipeOptions.threshold && distance > 1) {
                swiper.scrollToActivePage(true);
            }
        }
    }

    function onSwipe(event, direction, distance, duration, fingerCount) {
        setTimeout(resetInSwipe, 100);

        // Switch to new page
        if (direction == "right")
            switchToPage($, activePage.prev('.page'), true, true);
        else if (direction == "left")
            switchToPage($, activePage.next('.page'), true, true);
    }

    swiper.scrollToActivePage = function(animateScroll, distanceDelta) {
        if (activePage == null)
            return;
        var pageOffset = activePage.offset();
        var pagesOffset = $('#pages').offset();
        var pagePosition = pageOffset.left - pagesOffset.left;
        scrollPages( pagePosition + (distanceDelta == null ? 0 : distanceDelta), animateScroll ? speed : 0);
    }

    var scrollPages = function(distance, duration) {
        if (pages != undefined) {
            if (duration > 0) {
                page_container.animate({ scrollLeft: distance}, duration);
            } else {
                page_container.scrollLeft(distance);
            }
        }
    }

    return swiper;
}

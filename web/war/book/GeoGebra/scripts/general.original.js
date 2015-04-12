$(document).ready(function($) {
	// some elements are useless if javascript is disabled, therefore the
	// js-only css class hides them, we remove that class here
	$(".js-only").removeClass('js-only');
	
	// some elements are just by-foot alternatives to javascript methods,
	// so we hide them
	$(".js-hide").hide();

	// for some actions (eg delete) we display a confirmation message
	$("a.confirm").click(function() {
		return confirm(lang_confirm);
	});

    // Set the focus to the search field
    if ($('.materials').length > 0 && $('#general-search-term').val() == "" && !$.support.touch) {
        $('#general-search-term').focus();
    }

    // Download section
    $('#cb_agree_license').change(function() {
        if ($('#cb_agree_license:checked').length >= 1) {
            enableDownloadLinks(true);
        } else {
            enableDownloadLinks(false);
        }
    });

	// Make tab headers clickable
	$("#tabs li").click(function(e) {
		var link = $('a', this);
		if (link.length && link[0] != e.target) {
			e.preventDefault();
			window.location.href = link[0].href;
		}
	});

    // Messages
    $('.message_announcement_close').click(function () {
        var id = $(this).parent().attr("id");
        id = id.substr(id.lastIndexOf("_")+1);
        $(this).parents(".message-box").hide();
        document.cookie="GeoGebraTubeA_"+id+"=1";
    });

    function enableDownloadLinks(enable) {
        enableLink('download_ggb', enable);
        enableLink('download_offline', enable);
        enableLink('download_latex', enable);
        enableLink('download_ibook_online', enable);
        enableLink('download_ibook_lite', enable);
        enableLink('download_ibook_widget_lite_tuturl', enable);
        if (enable) {
            $('#download_list').removeAttr("disabled");
        } else {
            $('#download_list').attr("disabled", "disabled");
        }
    }

    function enableLink(id, enable) {
        var link = $("#"+id);
        if (enable) {
            link.removeClass("nounderline");
            link.attr("href", link.data("href"));
            link.removeAttr("disabled");
        } else {
            link.addClass("nounderline");
            link.data("href", link.attr("href"));
            link.attr("href", "#");
            link.attr("disabled", "disabled");
        }
    }

    if (typeof(license_agreed) != 'undefined') {
        $('#cb_agree_license').attr('checked', license_agreed);
        enableDownloadLinks(license_agreed);
    }


	// make thumbnails of featured materials clickable 
	$('#featured li').click(function() {
		window.location = $(this).find('h4 a').attr('href');
	});
	
	// .. but don't trigger that reaction for clicks in the overlay area
	$('#featured li .overlay').click(function(event) {
		event.stopPropagation();
		return true;
	});

	$('select[name=taglanguage]').change(function() {
		var selectedlang = $(this).val();
		$.ajax({
			url: $(this).closest("form").attr("action"),
			type:'post',
			async: true,
			dataType: 'html',
			data: 'selectedlang='+encodeURIComponent(selectedlang)+'&textwritten='+encodeURIComponent('+'),
			success: function(result){
				$('textarea[name=predefined_tags]').val(result);
			},
			error: function(result) {
			}
		});
	});

	$('input#tagsubmit').click(function() {
		var selectedlang = $('select[name=taglanguage]').val();
		var textwritten = $('textarea[name=predefined_tags]').val();
		$.ajax({
			url: $(this).closest("form").attr("action"),
			type:'post',
			async: true,
			dataType: 'html',
			data: 'selectedlang='+encodeURIComponent(selectedlang)+'&textwritten='+encodeURIComponent(textwritten),
			success: function(result){
				$('textarea[name=predefined_tags]').val(result);
			},
			error: function(result) {
			}
		});
	});
	
	// 'detect-size' checkbox on material creation page
	if($('#detect-size').length > 0) {
		$('#detect-size').change(function() {
			// checkbox is checked => disable textfields for manual size values 
			if($(this).is(':checked')) {
				$('#applet-size').css('visibility','hidden');
			} else {
				$('#applet-size').css('visibility','visible');
			}
		});
	}
	
	if($('#appletsize_warning').length > 0) {
	
		function checkAppletSize() {
			var width = $('#width').val();
			var height = $('#height').val();
			
			if(width > 900 || height > 600+102) {
				$('#appletsize_warning').show(300);
			} else {
				$('#appletsize_warning').hide(300);
			}
		}
		
		// check size on load
		checkAppletSize();
		
		// and if the user changes the text
		$('#width').blur(function() {
			checkAppletSize();
		});
		
		$('#height').blur(function() {
			checkAppletSize();
		});
	}
	
	// favorite material
	if($('#favorite').length > 0) {
		$('#favorite').click(function() {
			var button = $(this);
			$.get(button.attr("href")).success(function(result) {
				if (result.type == "success") {
					if(result.newData) {
						button.removeClass('icon-favorite');
						button.addClass('icon-is-favorite');
						gaTrackFavoriteMaterial('unfavorite', material_id);
					} else {
						button.removeClass('icon-is-favorite');
						button.addClass('icon-favorite');
						gaTrackFavoriteMaterial('favorite', material_id);
					}
				}
			});

			return false;
		});
	}

	$("#tag_add").click(function() {
		if ($("#tag_add_appear").css('display') == 'none') {
			$("#tag_add_appear").css('display', 'block');
		} else {
			$("#tag_add_appear").css('display', 'none');
		}
	});
	
	$("#meta-more-link a").click(function() {
		$('#meta-more').slideDown();
		$('#meta-more-link').animate({opacity: 'hide', height: 'hide'}, 'fast');
		return false;
	});
	
	$("#derivatives_more").click(function() {
		$('.derivative.js-hide').show(300);
		$(this).hide();
		return false;
	});

    $("#tags_more").click(function() {
        $('.tag.js-hide').after("&nbsp;");
        $('.tag.js-hide').show(300);
        $(this).hide();
        return false;
    });

	$(".collection_delete").click(function() {
		var answer = confirm(lang_confirm);
		if (answer) {
			$.ajax({
				url: $(this).attr("href"),
				type:'post',
				async: true,
				dataType: 'html',
				success: function(result){
					location.reload();
				},
				error: function(result) {
					location.reload();
				}
			});
		}
		return false;
	});





	$('.simplemodal-close-hack').click(function() {
		location.reload();
		return false;
	});

	$("#collection-add").click(function() {
		var source = $(this);
		$("#collection-manage-dialog").modal({
            escClose: true,
            overlayClose:true,
            focus:true,
            opacity: 20,
			onOpen: function(dialog) {
				dialog.data.show();
				dialog.container.show();
				dialog.overlay.show();

				$("#collection-manage-dialog").addClass("ajax-load");

				$.get(source.attr('href'), "", function(data) {
					$("#collection-manage-dialog").removeClass("ajax-load");
					$("#collection-manage-dialog-content").html(data);

                    function toggleChapterList(listItem) {
                        if ($('input[type="checkbox"]', listItem).is(':checked')) {
                            $('.collection_add_list_chapter', listItem).show("fast");
//                            $('.collection_add_list_chapter', listItem).addClass("active");
                        } else {
                            $('.collection_add_list_chapter', listItem).hide("fast");
//                            $('.collection_add_list_chapter', listItem).removeClass("active");
                        }
                    }

                    function saveBookAssignemnt(listItem) {
                        var url = listItem.data("url");
                        var chapter_id = $('.collection_add_list_chapter select', listItem).val();
                        if (chapter_id != undefined && chapter_id != 0) {
                            url += '/chapter_id/' + chapter_id;
                        }
                        url += '/add/' + ($('input[type="checkbox"]', listItem).is(':checked') ? 'true' : 'false');
                        $.get(url, "", function(data) {
                            if (data != 'success') {
                                // revert changes
                                $('input[type="checkbox"]', listItem).attr('checked', ! $('input[type="checkbox"]', listItem).is(':checked'));
                                toggleChapterList(listItem);
                            }
                        });
                    }

                    // Save assignment as soon as the checkbox is checked/unchecked
                    $('input[type="checkbox"]', "#collection-manage-dialog-content").change(function() {
                        var source = $(this).parent();
                        toggleChapterList(source);
                        saveBookAssignemnt(source);
                    });

                    // Save assignment when the chapter is changed
                    $('.collection_add_list_chapter select').change(function() {
                        saveBookAssignemnt($(this).parent().parent());
                    });
				});
			},
			onClose: function (dialog) {
				dialog.data.hide();
				dialog.container.hide();
				dialog.overlay.hide();
				$.modal.close();
			}
		});
		return false;
	});

    //groups
    $("#create_group").on("click", function(e) {
        e.preventDefault();
        window.GGBT_gen_modal.showAjaxPopup("/group/create", {}, {className: "title_group_popup"}, $(this).data("popup-title"), null, function() {
            $("#savetitle").on("click", function(e) {
                var title = $("#grouptitle").val(),
                    desc = $("#groupdescription").val();
                if (title) {
                    $.post("/group/save", {
                        "data": {
                            "title": title,
                            "description": desc
                        }
                    }).done(function (e) {
                        if (e.newData && e.newData.group_id) {
                            window.location = "/group/stream/id/" + e.newData.group_id;
                        }
                    });
                }
            });
            addFormattingOptions($("#groupdesc"));
            setTimeout(function() {$('#grouptitle').focus();}, 50);
        }, $('<div class="group_popup">'));
    });
    $("#join_group").on("click", function(e) {
        e.preventDefault();
        window.GGBT_gen_modal.showAjaxPopup("/group/join", {}, {className: "group_join_popup", maxWidth: "300"}, $(this).data("popup-title"), null, function() {
            $("#joingroup").on("click", function(e) {
                var code = $("#code").val();
                if (code) {
                    $.get("/group/join", {"code": code}).done(function (e) {
                        if (e.type === 'success' && e.newData) {
                            window.location = e.newData;
                        } else {
                            alert(e.message);
                        }
                    });
                }
            });
        });
    });
	
	// clicking upon the reply link of a comment will make a form appear
	// directly below the comment
	$(".reply").click(function() {
		var source = $(this);
		var id = source.attr("href").substring("#comment-".length);
		var reply_form = $("#comment-reply");
		var wasHidden = reply_form.css("display") == "none";
		var report_form = $("#comment-report");
		var wasHiddenReport = report_form.css("display") == "none";
		
		// Just change fields if the user requested replying to another comment
		// or the form was hidden. The wasHidden test is also important because
		// the value of the reply_to field may be cached by the browser across
		// requests.
		if(wasHidden || $("#reply_to").val() != id) {		
			var original = $(source.attr("href")+" > .children");
			var original_haschildren = true;
			if (original.length == 0)
			{
				original_haschildren = false;
				original = $(source.attr("href"));
			}

			// clear text
			reply_form.find("textarea").val("");
			
			if (!wasHiddenReport) {
				report_form.hide(200);
			}
			
			// if a reply form was displayed already hide this form first
			// before fading in the form at the new place, otherwise just 
			// fade in the form
			if(!wasHidden) {
				reply_form.hide(200, function() {
					if (original_haschildren) {
						original.prepend(reply_form);
					} else {
						original.append(reply_form);
					}
					reply_form.show(400, function() {
						this.scrollIntoView(false);
					});
				});
			} else {
				if (original_haschildren) {
					original.prepend(reply_form);
				} else {
					original.append(reply_form);
				}
				reply_form.show(400, function() {
					this.scrollIntoView(false);
				});
			}
			
			// change the form value which is indicating to which comment should
			// be replied
			$("#reply_to").val(id);
		}
		
		return false;
	});

	// clicking on report of comment gives a form
	$(".report_button").click(function() {
		var source = $(this);
		var id = source.attr("href").substring("#comment-".length);
		var report_form = $("#comment-report");
		var wasHidden = report_form.css("display") == "none";
		var reply_form = $("#comment-reply");
		var wasHiddenReply = reply_form.css("display") == "none";
		
		// Just change fields if the user requested reporting to another comment
		// or the form was hidden. The wasHidden test is also important because
		// the value of the report_which field may be cached by the browser across
		// requests.
		if(wasHidden || $("#report_which").val() != id) {		
			var original = $(source.attr("href")+" > .children");
			var original_haschildren = true;
			if (original.length == 0)
			{
				original_haschildren = false;
				original = $(source.attr("href"));
			}
			
			// clear text
			report_form.find("textarea").val("");
			
			if (!wasHiddenReply) {
				reply_form.hide(200);
			}
			
			// if a report form was displayed already hide this form first
			// before fading in the form at the new place, otherwise just 
			// fade in the form
			if(!wasHidden) {
				report_form.hide(200, function() {
					if (original_haschildren) {
						original.prepend(report_form);
					} else {
						original.append(report_form);
					}
					report_form.show(400, function() {
						this.scrollIntoView(false);
					});
				});
			} else {
				if (original_haschildren) {
					original.prepend(report_form);
				} else {
					original.append(report_form);
				}
				report_form.show(400, function() {
					this.scrollIntoView(false);
				});
			}

			// change the form value which is indicating which comment should
			// be reported
			$("#report_which").val(id);
		}
		
		return false;
	});

	// this link is shown in the reply form and hides that form
	$("#reply-remove").click(function() {
		$("#comment-reply").hide(200);
		$("#reply_to").val("0");
	});
	
	var commentEditForm;
	
	// edit comment
	$('.comment-edit').click(function() {
		var source = $(this);
		var commentContainer = source.parentsUntil('.comment').parent();
		
		// the user clicked on the edit link but the form is already visible
		if(commentEditForm != null && commentEditForm.parentsUntil('.comment').parent().attr('id') == commentContainer.attr('id')) {
			commentEditForm.hide(200);
			commentEditForm = null;
		}
		
		// load edit form
		else {
			$.get(source.attr('href'), function(result) {
				if(commentEditForm != null) {
					commentEditForm.hide();
					commentEditForm = null;
				}				
				
				commentContainer.find('.text:first').after(result);
				addFormattingOptions(commentContainer.find('#comment-edit-form'));
				commentEditForm = commentContainer.find('#comment-edit-form');
			});
		}
		
		return false;
	});

    $(".pagination li").click(function() {
        var link = $("a", this)[0];
        if (link != undefined)
            link.click();
    });
	
	// this link is shown in the report form and hides that form
	$("#report-remove").click(function() {
		$("#comment-report").hide(200);
		$("#report_which").val("0");
	});

	// report comment (save report in database)
	$("#comment-report").submit(function() {
		$.ajax({
			url: $("#comment-report").attr("action"),
			type:'post',
			async: true,
			dataType: 'html',
			data: $("#comment-report").serialize(),
			success: function(result){
				if (result == 'success')
				{
					$("#comment-report").before($("#report-message"));
					$("#report-message").fadeIn().delay(2000).fadeOut('slow');
				}
				else if (result == 'error_15seconds')
				{
					$("#comment-report").before($("#report-message-15"));
					$("#report-message-15").fadeIn().delay(2000).fadeOut('slow');
				}
				else
				{
					$("#comment-report").before($("#report-message-fail"));
					$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
				}
				$("#comment-report").hide(200);
				$("#report_which").val("0");
			},
			error: function(result) {
				$("#comment-report").before($("#report-message-fail"));
				$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
			}
		});
		return false;
	});

	// clicking on report of comment gives a form
	$("#report_button_material").click(function() {
		var source = $(this);
		var id = source.attr("href").substring(1);
		var report_form = $("#material-report");
		var wasHidden = report_form.css("display") == "none";
		
		// Just change fields if the user requested reporting to another comment
		// or the form was hidden. The wasHidden test is also important because
		// the value of the report_which field may be cached by the browser across
		// requests.
		if(wasHidden) {
			// clear text
			report_form.find("textarea").val("");
			report_form.show(400);
		} else {
			// clear text
			report_form.find("textarea").val("");
			report_form.hide(200);
		}
		return false;
	});

	$("#report_broken_link").click(function() {
		var answer = confirm(brokenlink_confirm);
		if (answer) {
			$.ajax({
				url: $("#report_broken_link").attr("href"),
				type:'post',
				async: true,
				dataType: 'html',
				data: 'message='+encodeURIComponent(report_message_broken),
				success: function(result){
					if (result == "success")
					{
						$("#material-report").before($("#report-message"));
						$("#report-message").fadeIn().delay(2000).fadeOut('slow');
					}
					else if (result == "error_15seconds")
					{
						$("#material-report").before($("#report-message-15"));
						$("#report-message-15").fadeIn().delay(2000).fadeOut('slow');
					}
					else
					{
						$("#material-report").before($("#report-message-fail"));
						$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
					}
				},
				error: function(result) {
					$("#material-report").before($("#report-message-fail"));
					$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
					//alert("Ajax error");
				}
			});
		}
		return false;
	});

	$("#report-remove-material").click(function() {
		$("#material-report").hide(200);
	});

	$("#material-report").submit(function() {
		$.ajax({
			url: $("#material-report").attr("action"),
			type:'post',
			async: true,
			dataType: 'html',
			data: $("#material-report").serialize(),
			success: function(result){
				if (result == "success")
				{
					$("#material-report").before($("#report-message"));
					$("#report-message").fadeIn().delay(2000).fadeOut('slow');
				}
				else if (result == "error_15seconds")
				{
					$("#material-report").before($("#report-message-15"));
					$("#report-message-15").fadeIn().delay(2000).fadeOut('slow');
				}
				else
				{
					$("#material-report").before($("#report-message-fail"));
					$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
				}
				$("#material-report").hide(200);
			},
			error: function(result) {
				$("#material-report").before($("#report-message-fail"));
				$("#report-message-fail").fadeIn().delay(2000).fadeOut('slow');
			}
		});
		return false;
	});

    function likeMaterial(source, url) {
        $.get(url, function(data) {
            if(data.status == 'success') {
                var preLikes = parseInt(source.html());
                source.html(data.stats.likes);

                var action = "like";
                if (preLikes > data.stats.likes)
                    action = "unlike";

                gaTrackLikeMaterial(action, material_id);
            }
        }, 'json');
    }
	
	$("#like").click(function() {
        likeMaterial($("#likes"), $(this).attr("href"));

		return false;
	});

	$(".meta-community .likes.click").click(function() {
        likeMaterial($(this), $(this).data("url"));

		return false;
	});

	$("input[name=manage_all1]").mousedown(function() {
		if ($(this).is(':checked'))
		{
			$(".mccheck").attr('checked', false);
			$("input[name=manage_all2]").attr('checked', false);
		}
		else
		{
			$(".mccheck").attr('checked', true);
			$("input[name=manage_all2]").attr('checked', true);
		}
	});

	$("input[name=manage_all2]").mousedown(function() {
		if ($(this).is(':checked'))
		{
			$(".mccheck").attr('checked', false);
			$("input[name=manage_all1]").attr('checked', false);
		}
		else
		{
			$(".mccheck").attr('checked', true);
			$("input[name=manage_all1]").attr('checked', true);
		}
	});

	$("input[name=submit1]").submit(function() {
		return false;
	});

	$("input[name=submit2]").submit(function() {
		return false;
	});
	
	$("select[name=report_select1]").change(function() {
		$("select[name=report_select2]").val(
			$("select[name=report_select1]").val()
		);
	});

	$("select[name=report_select2]").change(function() {
		$("select[name=report_select1]").val(
			$("select[name=report_select2]").val()
		);
	});

	$(".report_message_query_link").click(function() {
		var entryid = $(this).attr("href").substring(1);
		var container = $("#comment_for_"+entryid);
		
		if(container.data('loaded') == true) {
			if(container.css('display') == 'none') {
				container.css('display', 'block');
			} else {
				container.css('display', 'none');
			}
		} else {
			$.ajax({
				url: $("#report_manage_url").val(),
				type:'post',
				async: true,
				dataType: 'html',
				data: "entry="+encodeURIComponent(entryid),
				success: function(result){
					container.html(result);
					container.css('display', 'block');
					container.data('loaded', true);
				},
				error: function(result) {
					alert("Ajax error");
				}
			});
		}
		return false;
	});


	// invokes the tag suggest plugin which loads new suggestions for tags
	// using AJAX
	if($('#tags').length > 0) {
		$("#tags").tagSuggest({
			url: url_tagsuggest,
			delay: 2
		});
	}
	

	var setToggleMaterialsFirstLoad = true;
	
	// show or hide the materials belonging to the collection currently shown
	$("#collection-toggle-materials").click(function() {
		var container = $("#collection-bar-materials");
		
		if(container.css("display") == "block") {
			container.hide(400);
		} else {
			if(setToggleMaterialsFirstLoad) {
				$.ajax({
					url: $(this).attr("href"),
					type:'post',
					async: true, 
					dataType: 'html',
					success: function(result){
						// add 
						container.html(result);
						
						container.find("li a").mouseenter(function() {
							$("#collection-bar-materials-info").append($(this).parent().find("div").clone());
						});
						
						container.find("li a").mouseout(function() {
							$("#collection-bar-materials-info").empty();
						});
						
						container.show(400);
						setToggleMaterialsFirstLoad = false;
					}
				});
			} else {
				container.show(400);
			}
		}
		
		return false;
	});
});


window.GGBT_gen_modal = (function($) {
    "use strict";

    var storedOptions;

    function showPopup(dlgElem, options, title, onOpenFct) {

        // Check if another dialog is already open and store it
        if ($('#ggt-popup-wrapper').length === 1) {
            $('body').append($('<div id="ggt-popup-stored"></div>').append($('#ggt-popup-header')).append($('#ggt-popup-content')));
            $('#ggt-popup-header').attr("id", "ggt-popup-header-stored");
            $('#ggt-popup-content').attr("id", "ggt-popup-content-stored");
            storedOptions = $.extend(true, {}, $.modal.getOptions());
            $.modal.close();

            var oriOnClose = options.onClose;
            options.onClose = function() {
                if (typeof oriOnClose == "function") {
                    oriOnClose.onClose();
                }
                $.modal.close();

                // Reopen the previously opened dialog
                if ($('#ggt-popup-stored').length == 1) {
                    var storedDlg = $('#ggt-popup-stored').detach();
                    storedDlg.attr("id", "ggt-popup-wrapper");
                    $('#ggt-popup-header-stored', storedDlg).attr("id", "ggt-popup-header");
                    $('#ggt-popup-content-stored', storedDlg).attr("id", "ggt-popup-content");
                    storedDlg.modal(storedOptions);
                    $('#ggt-popup').show();
                    $('#ggt-popup-wrapper').show();
                }
            };
        }

        var o = {
            onOpen: function(dialog) {
                if (typeof dlgElem != 'string') {
                    dlgElem.show();
                }
                dialog.data.show();
                dialog.container.show();
                dialog.overlay.show();
                $('body').css('top', -($(window).scrollTop()) + 'px').addClass('noscroll');
                if (typeof onOpenFct === 'function') {
                    onOpenFct();
                }
            },
            onClose: function (dialog) {
                dialog.data.hide();
                dialog.container.hide();
                dialog.overlay.hide();
                $.modal.close();
                var pos = $('body').position();
                $('body').removeClass("noscroll");
                $(window).scrollTop(pos.top*(-1));
                //take care of not delete the dialogelem forewer
                if (typeof dlgElem != 'string') {
                    dlgElem.hide();
                    dlgElem.appendTo("body");
                }
            },
            escClose: true,
            overlayClose:true,
            focus: true,
            opacity: 70,
            overlayId: 'ggt-popup-overlay',
            containerId: 'ggt-popup',
            autoPosition: true,
            autoResize: true,
            minHeight: 400
            //position: [btnPos.top,btnPos.left]
        };

        if (typeof options == 'object') {
            if (options.minHeight) {
                // Add the height of the title from the minheight
                options.minHeight += 30;
            }
            // merge defaults and user options
            o = $.extend({}, o, options);
        }

        o.minHeight = undefined;


        var header = $('<div id="ggt-popup-header"></div>').prepend('<a class="ggt-popup-close"></a>');
        if (title !== null && title !== undefined) {
            header.prepend('<h5>' + title + '</h5>');
        }
        var content = $('<div id="ggt-popup-content"></div>').prepend(dlgElem);

        var dlg = $('<div id="ggt-popup-wrapper"></div>').append(header).append(content);

        return dlg.modal(o);
    }

    function setSize(height, width) {
        if (height !== undefined) {
            $('#ggt-popup-iframe').height(height);
            height += $('#ggt-popup-header').height() + 3;
            if ($.modal.minHeight() > height) {
                $.modal.minHeight(height);
            }
        }
        if (width !== undefined) {
            $('#ggt-popup-iframe').width(width);
        }
        $.modal.update(height, width);
    }

    function title(newTitle) {
        if (newTitle !== undefined && newTitle !== null) {
            $('#ggt-popup-header h5').html(newTitle);
        } else {
            return $('#ggt-popup-header h5').html();
        }
    }

    function showAjaxPopup(url, postParams, options, title, onOpenFct, onSuccessFct, contentWrapper) {
        var o = {
            onOpen: function(dialog) {
                dialog.overlay.show();
                $('#ggt-popup').hide();
                $('body').css('top', -($(window).scrollTop()) + 'px').addClass('noscroll');
                if (typeof onOpenFct === 'function') {
                    onOpenFct();
                }
            }
        };
        if (typeof options == 'object') {
            // merge defaults and user options
            o = $.extend({}, o, options);
        }
        var dlg = showPopup('Loading ...', o, title, onOpenFct);

        var fail = function(error) {
            var message = (typeof error.responseText != "undefined" ? error.responseText : error.message);
            $('#ggt-popup-content').html("<h4>Error: "+message+"</h4>");
            $.modal.update($('#ggt-popup-content').height());
			$('#ggt-popup').show();
        };

        var onReceive = function(data) {
            var html;
            if (typeof data == "object") {
                if (data.type == "success") {
                    html = data.newData;
                } else {
                    fail(data);
                }
            } else {
                html = data;
            }

            if (html !== undefined) {
                if (contentWrapper !== undefined) {
                    html = $(contentWrapper).append(html);
                }

                $('#ggt-popup-content').html(html);
                $('#ggt-popup').show();
                $.modal.update();
                if (typeof onSuccessFct === 'function') {
                    onSuccessFct();
                }
            }

        };
        var always = function() {
            $('body').removeClass('ajax-load');
        }

        $('body').addClass('ajax-load');
        if (postParams != null) {
            $.post(url, postParams, onReceive).fail(fail).always(always);
        } else {
            $.get(url, onReceive).fail(fail).always(always);
        }
        return dlg;
    }

    var iFrameSuccessFct;
    function showIframePopup(url, options, title, onOpenFct, onSuccessFct) {
        iFrameSuccessFct = onSuccessFct;
        var o = {
            autoResize: true,
            onOpen: function(dialog) {
                dialog.overlay.show();
                $('#ggt-popup').hide();

                $('body').css('top', -($(window).scrollTop()) + 'px').addClass('noscroll');
                if (typeof onOpenFct === 'function') {
                    onOpenFct();
                }
            }
        };
        if (typeof options == 'object') {
            // merge defaults and user options
            o = $.extend({}, o, options);
        }

        window.GGBT_gen_modal.onLoadIframe = function() {
            $('#ggt-popup').show();
            $('body').removeClass('ajax-load');
            $.modal.update();
            $('#ggt-popup-iframe').width($('#ggt-popup .simplemodal-wrap').width());
            $('#ggt-popup-iframe').height($('#ggt-popup .simplemodal-wrap').height() - $('#ggt-popup-header').height()-3);
            if (typeof iFrameSuccessFct === 'function') {
                iFrameSuccessFct();
            }
            window.GGBT_gen_modal.onLoadIframe = undefined;
        };

        var iframe = '<iframe id="ggt-popup-iframe" frameborder="0" src="'+url+'" title="'+title+'" onload="window.GGBT_gen_modal.onLoadIframe()"/>';

        $('body').addClass('ajax-load');
        return showPopup(iframe, o, title, onOpenFct);
    }


    function showSearchPopup(url, postParams, options, title, onOpenFct, collection_add_function) {
        var header,
            input,
            collection_search_scrollstep = 0,
            collection_search_ison = false,
            collection_search_next = null;

        var o = {
            autoResize: false,
            minHeight: 500
        };
        if (typeof options == 'object') {
            // merge defaults and user options
            o = $.extend({}, o, options);
        }

        var params = {
            keyword: '',
            step: collection_search_scrollstep
        };
        if (typeof postParams == 'object') {
            params = $.extend({}, params, postParams);
        }

        function listLoaded() {
            if (typeof collection_add_function === "function") {
                collection_add_function();
            }
        }

        var onFirstLoad = function(result){
            listLoaded();

            $('#ggt-popup-search-results').scroll(function() {
                if ($('#coll-addlist-loading').length > 0 && visible_in_container(this, $('#coll-addlist-loading')[0])) {
                    // Load additional results
                    if (!collection_search_ison) {
                        collection_search_scrollstep += 1;
                        collectionAjaxSearch($('#collection_search').val());
                    }
                }
            });
        };

        var onAddLoad = function(result){
            var resultWrapper = $('#ggt-popup-search-results');
            $("#coll-addlist-loading").remove();
            if (collection_search_scrollstep === 0) {
                resultWrapper.empty();
                resultWrapper.append(result);
                resultWrapper.scrollTop(0);
            } else {
                resultWrapper.append(result);
            }
            console.log($(".collection_add").length);

            listLoaded();

            if (collection_search_next === null) {
                collection_search_ison = false;
            } else {
                var nextreq = collection_search_next;
                collection_search_next = null;
                collectionAjaxSearch(nextreq);
            }
        };

        function collectionAjaxSearch(keywordval) {
            collection_search_ison = true;
            params.keyword = keywordval;
            params.step = collection_search_scrollstep;
            $.ajax({
                url: url,
                type:"post",
                async: true,
                dataType: "html",
                data: params,
                success: onAddLoad,
                error: function(result) {
                    if (collection_search_next === null) {
                        collection_search_ison = false;
                    } else {
                        var nextreq = collection_search_next;
                        collection_search_next = null;
                        collectionAjaxSearch(nextreq);
                    }
                }
            });
        }

        function visible_in_container(p, e) {
            var z = p.getBoundingClientRect();
            var r = e.getBoundingClientRect();

            //check style visiblilty and off-limits
            return !(r.top>z.bottom || r.bottom<z.top ||
            r.left>z.right || r.right<z.left);
        }

        var resultWrapper = $('<div id="ggt-popup-search-results"/>');
        showAjaxPopup(url, params, o, title, onOpenFct, onFirstLoad, resultWrapper);

        header = $("#ggt-popup-header");
        input = $('<input id="ggt-popup-search" placeholder="'+o.searchplaceholder+'" />');

        header.prepend(input);
        input.keyup(function () {
            collection_search_scrollstep = 0;
            if (collection_search_ison) {
                collection_search_next = $(this).val();
            } else {
                collectionAjaxSearch($(this).val());
            }
        });

        //collectionAjaxSearch('');
    }


    var popupParent;
    function pushPopup(dlgElem, options, title, onOpenFct) {
        if (typeof dlgElem === 'object') {
            // convert DOM object to a jQuery object
            dlgElem = dlgElem instanceof $ ? dlgElem : $(dlgElem);
            popupParent = dlgElem.parent();
        } else {
            popupParent = undefined;
        }

        $('#ggt-popup-content').attr('id', 'ggt-popup-oldcontent');
        $('#ggt-popup-oldcontent').hide();
        $('#ggt-popup-oldcontent').data('title', this.title());
        var newContent = $('<div id="ggt-popup-content"></div>').prepend(dlgElem);
        var dlg = $('#ggt-popup-wrapper').append(newContent);
        $(dlgElem).show();
        this.title(title);
        setSize(newContent.height(), newContent.width());
    }

    function closePopup() {
        if ($('#ggt-popup-oldcontent').length == 1) {
            var oldDlg = $('#ggt-popup-content').children().first();
            var newDlg = $('#ggt-popup-oldcontent').children().first();
            if (popupParent !== undefined) {
                popupParent.append(oldDlg);
                oldDlg.hide();
            }
            $('#ggt-popup-content').remove();

            $('#ggt-popup-oldcontent').attr('id', 'ggt-popup-content');
            this.title($('#ggt-popup-content').data('title'));
            $('#ggt-popup-content').show();

            setSize(newDlg.height(), newDlg.width());
        } else {
            $.modal.close();
        }
    }

    var cache = {
        dom : null,
        scrollTop : null
    }

    function replacePopupContent(u, conf) {
        var data = conf && conf.data || {},
            onLoad = conf && conf.onLoad,
            json = conf && conf.json,
            url = u;

        if (conf.selectorToCache) {
            cache.scrollTop = $(conf.selectorToCache).scrollTop();
            cache.dom = $(conf.selectorToCache).detach();
        }
        if (url !== null) {
            if (json) {
                url += "/json/true";
            }
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                dataType: json ? "json" : "html"
            })
                .done(function (result) {
                    if (!json) {
                        $("#ggt-popup-content").html(result);
                    }
                    if (typeof onLoad === "function") {
                        onLoad(json ? result : undefined);
                    }
                })
                .fail(function () {
                    alert("operation was wrong");
                })
                .always(function () {
                    console.log("replacecontent finished");
                });
        } else if(conf) {
            if (conf.fromCache) {
                var dom = $("#ggt-popup-content").empty().append(cache.dom);
                if (cache.scrollTop) {
                    cache.dom.scrollTop(cache.scrollTop);
                    cache.dom = null;
                    cache.scrollTop = null;
                }
            } else if (conf.html) {
                $("#ggt-popup-content").html(conf.html);
            } else if (conf.jq) {
                $("#ggt-popup-content").append(conf.jq);
            }
        }
    }

    function emptyPopupContent() {
        $("#ggt-popup-content").empty();
    }

    return {
        showPopup: showPopup,
        showAjaxPopup: showAjaxPopup,
        showSearchPopup: showSearchPopup,
        replacePopupContent: replacePopupContent,
        emptyPopupContent: emptyPopupContent,
        showIframePopup: showIframePopup,
        closePopup: closePopup,
        pushPopup: pushPopup,
        setSize: setSize,
        title: title
    };

})(jQuery);
window.GGBT_gen_edit = (function($) {

    var SAVE_STATE_READY = 0,
        SAVE_STATE_INPROGRESS = 1,
        SAVE_STATE_SUCCESSFUL = 2,
        SAVE_STATE_ERROR = 3,
        saveState = 0,
        saveFunction,
		saveSelector,
		cancelSelector,
		saveAndContinueSelector;

    // 0=ready, 1=in progress, 2=successful, 3=error
    function setSaveState(state, message) {
        var disableButtons = false;
        $('#save-info-inprogress').hide();
        $('#save-info-successful').hide();
        $('#save-info-error').hide();
        if (state === SAVE_STATE_INPROGRESS) {
            disableButtons = true;
            $('#save-info-inprogress').show();
        } else if (state === SAVE_STATE_SUCCESSFUL) {
			if(message != undefined ) {
				if ($('#save-info-successful .message').length) {
					$('#save-info-successful .message').text(message);
				} else {
					$('#save-info-successful').text(message);
				}
			}
            $('#save-info-successful').show();
            setTimeout(function() {
                $('#save-info-successful').hide();
            }, 5000);
        } else if (state === SAVE_STATE_ERROR) {
			if ($('#save-info-error .message').length) {
				$('#save-info-error .message').text(message);
			} else {
				$('#save-info-error').text(message);
			}
            $('#save-info-error').show();
        }
        saveState = state;
        if (disableButtons) {
            $(saveAndContinueSelector).attr("disabled","disabled");
            $(saveSelector).attr("disabled","disabled");
        } else {
            $(saveAndContinueSelector).removeAttr("disabled");
            $(saveSelector).removeAttr("disabled");
        }
    }

    function getDoneURL() {
		return $(cancelSelector).data("doneurl");
	}

    function setDoneURL(doneURL) {
        $(cancelSelector).data("doneurl", doneURL);
        $(saveSelector).data("doneurl", doneURL);
    }

    function initWsfSaveAndContinue(saveFunction, selector) {
		if (selector === undefined) {
			selector = "#save-and-continue";
		}
		saveAndContinueSelector = selector;
        var button = $(selector);
        button.on("click", function(e) {
            if (saveState !== SAVE_STATE_INPROGRESS) {
                saveFunction(e);
            }
        });
    }

    function initSaveButton(saveFunction, selector) {
		if (selector === undefined) {
			selector = "#save";
		}
		saveSelector = selector;
        var button = $(saveSelector);
        button.on("click", function(e) {
            var doneUrl;
            e.preventDefault();
            if (saveState !== SAVE_STATE_INPROGRESS) {
                saveFunction(e);
            }
        });
    }

    function initCancel(selector, exit) {
		if (selector === undefined) {
			selector = "#cancel";
		}
		cancelSelector = selector;
        var button = $(cancelSelector);
        button.on("click", function(e) {
            if(exit){
                if (window.opener) {
                    window.opener.location.reload();
                } else if (button.data('doneurl') !== ''){
                    window.location = getDoneURL();
                }
                window.close();
            } else {
                window.location = getDoneURL();
            }
        });
    }

    function initSave(saveFunction, selector) {
        initSaveButton(saveFunction, selector);
        initCancel();
    }


    function setSaveStateSuccessful(msg) {
        setSaveState(SAVE_STATE_SUCCESSFUL, msg);
    }

    function setSaveStateError(msg) {
        setSaveState(SAVE_STATE_ERROR, msg);
    }

    function setSaveStateInProgress() {
        setSaveState(SAVE_STATE_INPROGRESS);
    }

    function setSaveFunction(func) {
        saveFunction = func;
    }

    function autoSave(data, callback) {
        if (typeof saveFunction === "function") {
            saveFunction(data, callback);
        }
    }

    function initBBCodeEditor(textField, conf) {
        var mathFound,
            node,
            wbbOpt = {
                lang: conf.defaults.input.text.wysibb_lang,
                buttons: "[,fontfamily,|,fontsize,|,bold,italic,underline,strike,sup,sub,fontcolor,|,justifyleft,justifyright,justifycenter,|,bullist,numlist,|,link,|quote,code,table,removeFormat,],math,|,icons",
                allButtons: {
                    math: {
                        title: conf.defaults.input.text.toolbar.math.title,
                        buttonText: conf.defaults.input.text.toolbar.math.text,
                        modal: { //Description of modal window
                            title: conf.defaults.input.text.dlg_math.title,
                            width: "750px",
                            tabs: [
                                {
                                    input: [ //List of form fields
                                        {   param: "SELTEXT",
                                            type:"math",
                                            btn_basic: conf.defaults.input.text.dlg_math.btn_basic,
                                            btn_greek: conf.defaults.input.text.dlg_math.btn_greek,
                                            btn_operators: conf.defaults.input.text.dlg_math.btn_operators,
                                            btn_relationships: conf.defaults.input.text.dlg_math.btn_relationships,
                                            btn_arrows: conf.defaults.input.text.dlg_math.btn_arrows,
                                            btn_delimiters: conf.defaults.input.text.dlg_math.btn_delimiters,
                                            btn_misc: conf.defaults.input.text.dlg_math.btn_misc
                                        }
                                    ]
                                }
                            ],
                            onLoad: function(cmd,t,opt,queryState) {
                                if (opt && opt.seltext) {
                                    var editor = $(".wysibb-text-editor.wysibb-body"),
                                        range = document.createRange(),
                                        start = 0,
                                        end = opt.seltext.length;

                                    mathFound = false;
                                    node = null;

                                    if (editor.find(".mathquill-embedded-latex").length) {
                                        editor.find(".mathquill-embedded-latex").each(function() {
                                            if ($(this).text() === opt.seltext) {
                                                node = $(this).get(0);
                                                mathFound = true;
                                            }
                                        });
                                    }
                                    if (node === null) {
                                        node = editor.get(0);
                                    }

                                    start =  node.textContent.indexOf(opt.seltext);

                                    range.setStart(node.firstChild, start);
                                    range.setEnd(node.firstChild, end);
                                    var selection = window.getSelection();
                                    selection.removeAllRanges();
                                    selection.addRange(range);
                                }
                            },
                            onSubmit: function(cmd) {
                                var latex = this.$modal.find('.wsf-input-math-editor').mathquill('latex'),
                                    toInsert = this.getCodeByCommand(cmd,{seltext:latex});
                                if (mathFound) {
                                    $(node).remove();
                                }
                                this.insertAtCursor(toInsert);
                                this.closeModal();
                                this.updateUI();
                                return false;
                            }
                        },
                        transform: {
                            //'<div class="myquote">{SELTEXT}</div>':'[math]{SELTEXT}[/math]'
                            '<span class="mathquill-embedded-latex">{SELTEXT}</span>':'[math]{SELTEXT}[/math]'

                        }
                    },
                    icons : {
                        title: conf.defaults.input.text.toolbar.icons.title,
                        buttonText : conf.defaults.input.text.toolbar.icons.text,
                        modal: { //Description of modal window
                            title: conf.defaults.input.text.dlg_icons.title,
                            width: "700px",
                            tabs: [
                                {
                                    input: [ //List of form fields
                                        {param: "SRC",type:"icons"}
                                    ]
                                }
                            ],
                            onLoad: function() {
                            },
                            onSubmit: function(cmd, opt, queryState) {
                                var icon = this.$modal.find('.wsf-input-icon-editor img.selected'),
                                    src;
                                if (icon.length) {
                                    src = icon.attr("src");
                                    if (src) {
                                        this.insertAtCursor(this.getCodeByCommand(cmd, {SRC : src}));
                                        this.closeModal();
                                        this.updateUI();
                                    }
                                }
                                //this.insertAtCursor(this.getCodeByCommand(cmd,{seltext:icon}));
                                //this.closeModal();
                                //this.updateUI();
                                return false;
                            }
                        },
                        transform: {
                            //'<div class="myquote">{SELTEXT}</div>':'[math]{SELTEXT}[/math]'
                            '<img  class="wsf-icon" src="{SRC}"/>':'[icon]{SRC}[/icon]'

                        }
                    },
                    texttools : {
                        title:  conf.defaults.input.text.toolbar.text.title,
                        buttonText: '[Text]',
                        buttonHTML: '<span class="texttools">A</span>'
                    }

                },
                customKeyDown: function() {
                    if (conf.customKeyDown) {
                        conf.customKeyDown(this);
                    }
                },
                modalClosed: function(t) {
                    if (conf.modalClosed) {
                        conf.modalClosed(t);
                    }
                }
            };
        textField.wysibb(wbbOpt);
    }

    function getHTMLFromBBCode (bbdata,skiplt) {
        if (bbdata === null) {
            return '';
        }

        var usedTags = ["bold","italic","underline","strike","sup","sub","img","video","link","bullist","numlist","fontcolor","fontsize","fontfamily","justifyleft","justifycenter","justifyright","quote","code","table","math","fs_verysmall", "fs_small", "fs_normal", "fs_big", "fs_verybig", "icon"],
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
                },
                icon: {
                    transform: {
                        '<img  class="wsf-icon" src="{SRC}"/>':'[icon]{SRC}[/icon]'
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


    return {
        initSave : initSave,
        initCancel : initCancel,
        initSaveAndContinue: initWsfSaveAndContinue,
        setSaveState : setSaveState,
        setDoneURL : setDoneURL,
        getDoneURL : getDoneURL,
        setSaveStateSuccessful: setSaveStateSuccessful,
        setSaveStateError: setSaveStateError,
        setSaveStateInProgress : setSaveStateInProgress,
        setSaveFunction: setSaveFunction,
        autoSave: autoSave,
        initBBCodeEditor: initBBCodeEditor,
        getHTMLFromBBCode : getHTMLFromBBCode
    };
})(jQuery);

function confirmLogin(formId) {
    if (confirm(login_confirm)) {
        $('#'+formId).submit();
    }
}


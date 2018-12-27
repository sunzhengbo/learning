$(function(){
    // 导航栏左侧显示隐藏
    function topRightNav(){
        var topNavLeft = $(".wrapper .top-nav-wrap .nav-bd .nav-left .domain");
        topNavLeft.mouseover(function(){
            $(this).children("ul").show();
            $(this).css('background-color','#fff');
        });
        topNavLeft.mouseout(function(){
            $(this).children("ul").hide();
            $(this).css('background-color','#f5f5f5')
        })
    }
    topRightNav();

    // 导航栏右侧显示隐藏
    $(".wrapper .top-nav-wrap .nav-bd .nav-rigth>li:not(:eq(4),:eq(3))").hover(
        function(){
            $(this).css("background-color","#fff");
            $(this).children("ul").show().parent().siblings().children("ul").hide();
            //显示网站导航的二级菜单
            $(this).children(".site").show();
        },
        function(){
            $(this).css("background-color","#f5f5f5");
            $(this).children("ul").hide();
            $(this).children(".site").hide();
        }
    )

    // 左侧二级导航栏
    function leftSecondNav(){
        var left_nar_wrap = $(".wrapper .body-wrap .main .main-inner .left-nav");
        var sub = $(".wrapper .body-wrap .main .main-inner .left-nav .nav-left-wrapper");
        var activeRow;
        var activeMenu;
        var timer;
        left_nar_wrap.on("mouseenter",function(){
            sub.show();
        }).on("mouseleave",function(){
            sub.hide();
            if(activeRow){
                activeRow.removeClass("active_bg");
                activeRow.children().removeClass("active_a");
                activeRow = null;
            }
            if(activeMenu){
                activeMenu.hide();
            }
        }).on("mouseenter",".product li",function(){
            if(activeRow){
                // 给li添加背景和分割线添加颜色
                activeRow.removeClass("active_bg")
                // 给字体添加颜色
                activeRow.children().removeClass("active_a")
                activeMenu.hide();
            }

            activeRow = $(this)
            activeRow.addClass("active_bg");
            activeRow.children().addClass("active_a");
            activeMenu = sub.children(".sub-item1").eq(activeRow.index());
            activeMenu.show().siblings().hide();
        })
    }
    leftSecondNav();

    //主要部分轮播图
    function changePic(){
        // 设置一个全局变量控制图片切换
        var index = 0;

        // 获取图片的个数
        var total =  $('.wrapper .body-wrap .main .main-inner .core .top-pic>a>img').length;
        function move(){
            index ++;
            if(index >= total){
                index = 0;
            }else if(index < 0){
                index = total-1;
            }
            // 让index图片显示，其他的图片隐藏
            var img = $('.wrapper .body-wrap .main .main-inner .core .top-pic a').eq(index)
            img.children('img').show();
            img.siblings('a').children('img').hide();

            // 让圈圈随着图片的切换变色
            var img = $('.wrapper .body-wrap .main .main-inner .core .top-pic ul li').eq(index).css('background-color','#ff5000')
            .siblings().css('background-color','#fff');   
        }
        //定时器
        var timer = setInterval(move,2000);

        //鼠标放在图片上停止图片切换,鼠标离开图片上开始图片切换
        $('.wrapper .body-wrap .main .main-inner .core .top-pic').hover(function(){
        // 清理定时器
        clearInterval(timer);
            // 显示按钮
            $(this).find('.left-button').show();
            $(this).find('.rigth-button').show();
            },function(){
            // 设置定时器
            timer = setInterval(move,2000);
            // 隐藏按钮
            $(this).find('.left-button').hide();
            $(this).find('.rigth-button').hide();
        });

        //点击圆圈控制图片切换
        $('.wrapper .body-wrap .main .main-inner .core .top-pic ul li').click(function(){
            index = $(this).index() - 1;
            move();
        }); 

        //制作左右按钮变色
        function myHover(elemNode){
            elemNode.hover(function(){
                $(this).css('background-color','#999');
            },function(){
                $(this).css('background-color','rgba(163, 160, 160, 0.5)');
            });
        }
        var leftButton = $('.wrapper .body-wrap .main .main-inner .core .top-pic .left-button');
        var rigthButton = $('.wrapper .body-wrap .main .main-inner .core .top-pic .rigth-button');
        myHover(leftButton);
        myHover(rigthButton);

        //制作左右按钮切换图片
        rigthButton.click(function(){
            move();
        });

        leftButton.click(function(){
            index -= 2;
            move();
        });
    }
    changePic();

    //右侧公告信息显示
    $(".wrapper .body-wrap .col-right .notice .notice-content li").mouseover(function(){
        //设置字体加粗
        $(this).children("a").css("font-weight","bold").parent().siblings().children("a").css("font-weight","normal");
        // 设置下划线
        $(this).children("p").show().parent().siblings().children("p").hide();
        // 设置内容切换
        $(".wrapper .body-wrap .col-right .notice .notice-text ul").eq($(this).index()).show().siblings().hide();
    });

    //新闻栏部分轮播
    function newsChangerPic(){
        var newWrap = $(".wrapper .body-wrap .main .foot .news");
        var index = 0;
        newWrap.eq(index).show().siblings(".news").hide();

        var showAndhide = function(){
            index++;
            index = index >= newWrap.length ? 0 : index;
            newWrap.eq(index).stop().slideDown("slow").siblings(".news").stop().slideUp("fast");
        }

        var timer = setInterval(showAndhide,3000);

        newWrap.hover(function(){
            clearInterval(timer);
        },function(){
            timer = setInterval(showAndhide,3000);
        })
    }
    newsChangerPic();

    // 理想生活上天猫部分图片轮播
    function imgSwitch(){
        var targetCollections = $(".wrapper .body-wrap .main .main-inner .core .botton-pic .tm-bd ul");
        var btnCollections = $(".wrapper .body-wrap .main .main-inner .core .botton-pic .tm-nav .tm-btn li");
        var showIndex = $(".wrapper .body-wrap .main .main-inner .core .botton-pic .tm-hd .inner .show-index");
        var index = 0;
        targetCollections.eq(index).show().siblings().hide();
        btnCollections.eq(index).addClass("active").siblings().addClass("static") ;
        // 显示角标
        showIndex.children(".index").html(1).css("color","#f40");
        showIndex.children(".total").html("/"+targetCollections.length);

        var timer = setInterval(startChange,2000);
    
        function startChange(){
            index ++;
            if(index >= targetCollections.length){
                index = 0;
            }else if(index < 0){
                index = targetCollections.length -1;
            }
            targetCollections.eq(index).stop().fadeIn().siblings().stop().fadeOut();
            btnCollections.eq(index).removeClass().addClass("active").siblings().removeClass().addClass("static");
            // 显示角标
            showIndex.children(".index").html(index+1);
            showIndex.children(".total").html("/"+targetCollections.length);
        }
    
        // 聚焦停止轮播
        targetCollections.parent().hover(function(){
            clearInterval(timer);
        },function(){
            timer = setInterval(startChange,2000);
        })

        btnCollections.click(function(){
            index = $(this).index()-1;
            startChange();
        })
    }
    imgSwitch();
});
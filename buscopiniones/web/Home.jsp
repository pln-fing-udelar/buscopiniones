<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html class="no-js">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Buscopiniones</title>

        <!--[if lt IE 9]>
                <script src="js/css3-mediaqueries.js"></script>
        <![endif]-->
        <link type="text/css" rel="stylesheet" media="all" href="css/style.css"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <!-- Adding "maximum-scale=1" fixes the Mobile Safari auto-zoom bug: http://filamentgroup.com/examples/iosScaleBug/ -->


        <!-- JS -->
        <script src="js/jquery-1.6.4.min.js"></script>

        <!--  <script src="js/less-grid-4.js"></script> -->
        <script src="js/custom.js"></script>
        <script src="js/tabs.js"></script>

        <!-- Masonry -->
        <script src="js/masonry.min.js" ></script>
        <script src="js/imagesloaded.js" ></script>
        <!-- ENDS Masonry -->

        <!-- Tweet -->
        <link rel="stylesheet" href="css/jquery.tweet.css" media="all"  /> 
        <script src="js/tweet/jquery.tweet.js" ></script> 
        <!-- ENDS Tweet -->

        <!-- superfish -->
        <link rel="stylesheet" media="screen" href="css/superfish.css" /> 
        <script  src="js/superfish-1.4.8/js/hoverIntent.js"></script>
        <script  src="js/superfish-1.4.8/js/superfish.js"></script>
        <script  src="js/superfish-1.4.8/js/supersubs.js"></script>
        <!-- ENDS superfish -->

        <!-- prettyPhoto -->
        <script  src="js/prettyPhoto/js/jquery.prettyPhoto.js"></script>
        <link rel="stylesheet" href="js/prettyPhoto/css/prettyPhoto.css"  media="screen" />
        <!-- ENDS prettyPhoto -->

        <!-- poshytip -->
        <link rel="stylesheet" href="js/poshytip-1.1/src/tip-twitter/tip-twitter.css"  />
        <link rel="stylesheet" href="js/poshytip-1.1/src/tip-yellowsimple/tip-yellowsimple.css"  />
        <script  src="js/poshytip-1.1/src/jquery.poshytip.min.js"></script>
        <!-- ENDS poshytip -->


        <!-- GOOGLE FONTS 
        <link href='http://fonts.googleapis.com/css?family=Allan:700' rel='stylesheet' type='text/css'>
        -->
        <!-- Flex Slider -->
        <link rel="stylesheet" href="css/flexslider.css" >
        <script src="js/jquery.flexslider-min.js"></script>
        <!-- ENDS Flex Slider -->


        <!--[if IE 6]>
        <link rel="stylesheet" href="css/ie6-hacks.css" media="screen" />
        <script type="text/javascript" src="js/DD_belatedPNG.js"></script>
                <script>
                        /* EXAMPLE */
                        DD_belatedPNG.fix('*');
                </script>
        <![endif]-->

        <!-- Lessgrid -->
        <link rel="stylesheet" media="all" href="css/lessgrid.css"/>

        <!-- modernizr -->
        <script src="js/modernizr.js"></script>


    </head>

    <body lang="es">


        <!-- mobile-nav -->
        <div id="mobile-nav-holder">
            <div class="wrapper">
                <ul id="mobile-nav">
                    <li  class="current-menu-item"><a href="index.html">home</a></li>
                    <li><a href="blog.html">blog</a></li>
                    <li><a href="page.html">about</a>
                        <ul>
                            <li><a href="page-full.html">Fullwidth Page</a></li>
                            <li><a href="page-features.html">Features</a></li>
                            <li><a href="page-typography.html">Typography</a></li>
                            <li><a href="page-icons.html">Icons</a></li>
                        </ul>
                    </li>
                    <li><a href="portfolio.html">portfolio</a></li>
                    <li><a href="contact.html">contact</a></li>
                    <li><a href="http://luiszuno.com/blog/downloads/modus-html-template">Grab it!</a></li>
                </ul>
                <div id="nav-open"><a href="#">Menu</a></div>
            </div>
        </div>
        <!-- ENDS mobile-nav -->

        <header>


            <div class="wrapper">

                <a href="./" id="logo"><img  src="img/logo.png" alt="Tandem"></a>
                <form method="GET" id="contactForm" class="buscador">

                    <div>

                        <span class="spanBuscador">Opiniones de </span><input type="text" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("fuente")%>" <% }%> name="fuente" class="form-poshytip" title="Ingrese la fuente de la opinion" /> 

                        <span class="spanBuscador"> sobre </span><input type="text" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("asunto")%>" <% }%>name="asunto" class="form-poshytip" title="Ingrese el asunto de la opinion" /> 

                        <input type="submit" name="buscar" value="Buscar!" class="link-button green" />

                    </div>

                </form>
                <nav>
                    <ul id="nav" class="sf-menu">
                        <li class="current-menu-item"><a href="./">Opiniones<span class="subheader">buscá lo que opina la gente</span></a></li>
                        <li><a href="./VerTemas">Tema de la semana<span class="subheader">fijate de qué se habló</span></a></li>

                    </ul>
                </nav>

                <div class="clearfix"></div>

            </div>

        </header>




        <!-- MAIN -->
        <div id="main">

            <!-- social -->
            <div id="social-bar">
                <ul>
                    <li><a href="http://www.facebook.com"  title="Become a fan"><img src="img/social/facebook_32.png"  alt="Facebook" /></a></li>
                    <li><a href="http://www.twitter.com" title="Follow my tweets"><img src="img/social/twitter_32.png"  alt="Facebook" /></a></li>
                    <li><a href="http://www.google.com"  title="Add to the circle"><img src="img/social/google_plus_32.png" alt="Facebook" /></a></li>
                </ul>
            </div>
            <!-- ENDS social -->



            <!-- Content -->
            <div id="content">

                <!-- slider 
                <div class="flexslider home-slider">
                        <ul class="slides">
                                <li>
                                        <img src="img/slides/01.jpg" alt="alt text" />
                                        <p class="flex-caption">Placeholder images by thebeaststudio.com </p>
                                </li>
                                <li>
                                        <img src="img/slides/02.jpg" alt="alt text" />
                                        <p class="flex-caption">Visit luiszuno.com for more freebies!</p>
                                </li>
                                <li>
                                        <img src="img/slides/03.jpg" alt="alt text" />
                                </li>
                        </ul>
                </div>
                <div class="shadow-slider"></div>
                <!-- ENDS slider -->
                <div id="timeline-embed"></div>
                <script type="text/javascript">
					var timeline_config = {
						width: "100%",
						height: "100%",
						source: './JsonTimeline?fuente=<%= request.getParameter("fuente")%>&asunto=<%= request.getParameter("asunto")%>',
						//source: 'example_json.json',
						start_at_end: true,
						lang: 'es'
					};
                </script>
                <script type="text/javascript" src="./compiled/js/storyjs-embed.js"></script>


                <!-- Headline -->
                <div class="headline">
                    Aca hay que poner para que se pueda filtrar por fecha
                </div>
                <!-- ENDS Headline -->
            </div>





        </div>
        <!-- ENDS MAIN -->

        <footer>
            <div class="wrapper">

                <ul id="footer-cols">

                    <li class="first-col">

                        <div class="widget-block">
                            <h4>Recent posts</h4>
                            <div class="recent-post">
                                <a href="#" class="thumb"><img src="img/dummies/54x54.gif" alt="Post" /></a>
                                <div class="post-head">
                                    <a href="#">Pellentesque habitant morbi senectus</a><span>March 12, 2011</span>
                                </div>
                            </div>
                            <div class="recent-post">
                                <a href="#" class="thumb"><img src="img/dummies/54x54.gif" alt="Post" /></a>
                                <div class="post-head">
                                    <a href="#">Pellentesque habitant morbi senectus</a><span>March 12, 2011</span>
                                </div>
                            </div>
                            <div class="recent-post">
                                <a href="#" class="thumb"><img src="img/dummies/54x54.gif" alt="Post" /></a>
                                <div class="post-head">
                                    <a href="#">Pellentesque habitant morbi senectus</a><span>March 12, 2011</span>
                                </div>
                            </div>
                        </div>
                    </li>

                    <li class="second-col">

                        <div class="widget-block">
                            <h4>Dummy text</h4>
                            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies ege. Aenean ultricies mi vitae est. Mauris placerat eleifend leo.</p>
                            <p>Pellentesque habitant morbi tristique senectus et netus et malesuada.</p>
                        </div>

                    </li>

                    <li class="third-col">

                        <div class="widget-block">
                            <div id="tweets" class="footer-col tweet">
                                <h4>Twitter widget</h4>
                            </div>
                        </div>

                    </li>	
                </ul>				
                <div class="clearfix"></div>


            </div>

            <div id="to-top"></div>
        </footer>

    </body>

</html>

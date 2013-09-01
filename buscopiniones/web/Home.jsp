<%@page import="java.util.Collection"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<html>
	<head>
		<meta charset="utf-8">
		<title>Buscopiniones</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">

		<link href='http://fonts.googleapis.com/css?family=Lato:400,700,300' rel='stylesheet' type='text/css'>
		<!--[if IE]>
			<link href="http://fonts.googleapis.com/css?family=Lato" rel="stylesheet" type="text/css">
			<link href="http://fonts.googleapis.com/css?family=Lato:400" rel="stylesheet" type="text/css">
			<link href="http://fonts.googleapis.com/css?family=Lato:700" rel="stylesheet" type="text/css">
			<link href="http://fonts.googleapis.com/css?family=Lato:300" rel="stylesheet" type="text/css">
		<![endif]-->

		<link href="css/bootstrap.css" rel="stylesheet">
		<link href="css/font-awesome.min.css" rel="stylesheet">
		<link href="css/theme.css" rel="stylesheet">
		<link href="css/prettyPhoto.css" rel="stylesheet" type="text/css"/>
		<link href="css/zocial.css" rel="stylesheet" type="text/css"/>
		<link rel="stylesheet" href="css/nerveslider.css">

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<!--[if IE 7]>
		<link rel="stylesheet" href="css/font-awesome-ie7.min.css">
		<![endif]-->

		<!-- datepicker -->
		<script src="js/jquery.js"></script>
		<script src="js/jquery-ui.min.js"></script>
		<script src="js/jquery.ui.datepicker-es.js"></script>
		<link rel="stylesheet" media="all" href="js/jquery-ui.css"/>
		<script>
			$(function() {
				$("#desde").datepicker();
				$("#hasta").datepicker();
			});
		</script>
	</head>

	<body>
		<!--header-->
		<div class="header ">
			<!--logo-->
			<div class="container">
				<div class="logo">
					<a href="/buscopiniones/"><img src="img/logo.png" alt="" class="animated bounceInDown" /></a>  
				</div>
				<!--menu-->				
				<nav id="main_menu">					
					<div class="menu_wrap">

						<ul class="nav sf-menu">

							<li class="sub-menu active"><a href="Home">Opiniones</a> 								
							</li>
							<li class="sub-menu"><a href="VerTemas">Temas</a>								
							</li>							
							<li class="last"><a href="contact.html">Contacto</a></li>
						</ul>
					</div>
				</nav>
			</div>
		</div>
		<!--//header-->
		<!--page-->

		<div style="background:rgb(240, 240, 240); box-shadow:3px 3px 3px #cecece;">
			<div class="container">				
				<form method="GET" class="form-inline" style="margin:14px 0 14px 0;">
					<label>Opiniones de:</label>
					<input type="text" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("fuente")%>" <% }%> name="fuente" title="Ingrese la fuente de la opinion" style="margin-right:10px;" />
					<label>sobre:</label>
					<input type="text" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("asunto")%>" <% }%> name="asunto" title="Ingrese el asunto de la opinion"  style="margin-right:10px" />
					<input type="submit" name="buscar" value="Buscar" class="btn btn-medium btn-primary btn-rounded" style="padding:8px 20px;" />
				</form>
			</div>
		</div>

		<div id="timeline-embed"></div>
		<script type="text/javascript">
			var timeline_config = {
				width: "100%",
				height: "100%",
				source: './JsonTimeline?fuente=<%= request.getParameter("fuente")%>&asunto=<%= request.getParameter("asunto")%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>',
				//source: 'example_json.json',
				start_at_end: true,
				lang: 'es'
			};
		</script>
		<script type="text/javascript" src="./compiled/js/storyjs-embed.js"></script>
		<!-- /SLIDER -->

		<div id="banner">
			<div class="container intro_wrapper">
				<div class="inner_content">

					<!--welcome-->
					<div class="container">				
						<form method="GET" class="form-inline" style="margin:14px 0 14px 0;">
							<label>Opiniones desde: </label>
							<input type="hidden" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("fuente")%>" <% }%> name="fuente" title="Ingrese la fuente de la opinion" style="margin-right:10px;" />
							<input type="hidden" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("asunto")%>" <% }%> name="asunto" title="Ingrese el asunto de la opinion"  style="margin-right:10px" />
							<input type="text" <% if (request.getParameter("desde") != null) {%>value="<%= request.getParameter("desde")%>" <% }%> name="desde" id="desde" title="Ingrese la fecha inicial" style="margin-right:10px;" />
							<label>hasta: </label>
							<input type="text" <% if (request.getParameter("hasta") != null) {%>value="<%= request.getParameter("hasta")%>" <% }%> name="hasta" id="hasta" title="Ingrese la fecha final" style="margin-right:10px" />
							<input type="submit" name="filtrar" value="Filtrar" class="btn btn-medium btn-primary btn-rounded" style="padding:8px 20px;" />
						</form>
					</div>
					<!--//welcome-->
				</div>
			</div>
		</div>
		<!--//banner-->

		<div class="container wrapper">
			<div class="inner_content">
				<div class="pad45"></div>

				<!--info boxes-->
				<div class="row">
					<div class="span3">
						<div class="tile">
							<div class="intro-icon-disc cont-large"><i class="icon-wrench intro-icon-large"></i></div>
							<h6><small>FUENTES</small>
								<br /><span>Otras fuentes que opinaron sobre este tema</span></h6>
								<h5>
								<p>
								<%
									Collection<String> fuentes = (Collection<String>) request.getAttribute("fuentes");
									for (String fuente : fuentes) {
								%>
								<a href="?fuente=<%= fuente%>&asunto=<%= request.getParameter("asunto")%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>"><b><%= fuente %></b></a><br/>
								<%	
									}
								%>
								</p>
								</h5>
						</div> 
						<div class="pad25"></div>
					</div> 

					<div class="span3">
						<div class="tile">
							<div class="intro-icon-disc cont-large"><i class="icon-rocket intro-icon-large"></i></div>
							<h6><small>CODE</small>
								<br><a href="#"><span>12-column grid</span></a></h6>
							<p>Bootstrap is designed to help people of all skill levels - designer or developer, huge nerd or early beginner. 
								Use it as a complete kit or use to start something.</p>
						</div> 
						<div class="pad25"></div>
					</div> 

					<div class="span3">
						<div class="tile">
							<div class="intro-icon-disc cont-large"><i class="icon-beaker intro-icon-large"></i></div>
							<h6><small>CREATE</small>
								<br><a href="#"><span>responsive</span></a></h6>
							<p>Bootstrap have gone fully responsive. Our components are scaled according to a range of resolutions and devices to provide a consistent 
								experience.</p>	
						</div> 
						<div class="pad25"></div>
					</div> 

					<div class="span3">
						<div class="tile tile-hot">
							<div class="intro-icon-disc cont-large"><i class="icon-book  intro-icon-large"></i></div>
							<h6> <small>SUPPORT</small>
								<br><a href="#"><span>growing library</span></a></h6>
							<p>Despite being only 7kb (gzipped), Bootstrap is one of the most complete front-end toolkits out there with dozens of fully functional components.</p>
						</div>
						<div class="pad25"></div>	
					</div> 
				</div> 

				<!--//info boxes-->
				<div class="row">
					<!--col 1-->
					<div class="span12">
						<div class="row">
							<div class="pad25 hidden-phone"></div>	

							<div class="span4">
								<h1>Recent Work</h1>
								<h4>Lorem ipsum dolor sit amet, rebum putant recusabo in ius, pri simul tempor ne, his ei summo virtute.</h4>
								<p>Nam ea labitur pericula. Meis tamquam pro te, cibo mutat necessitatibus id vim. An his tamquam postulant, pri id mazim nostrud diceret 
									sapientem eloquentiam sea cu, sea ut exerci delicata. Corrumpit vituperata.</p>

								<a href="#" class="btn btn-primary  btn-custom btn-rounded">view portfolio</a>
								<div class="pad45"></div>
							</div>
							<!--column 2 slider-->
							<div class="span8 pad15 col_full2">

								<div id="slider_home">
									<div class="slider-item">	
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s1.jpg" data-rel="prettyPhoto">
													<img src="img/small/s1.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">catalogue</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s2.jpg" data-rel="prettyPhoto">
													<img src="img/small/s2.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">loupe</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s3.jpg" data-rel="prettyPhoto">
													<img src="img/small/s3.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">retro rocket</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s4.jpg" data-rel="prettyPhoto">
													<img src="img/small/s4.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">infographics</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s5.jpg" data-rel="prettyPhoto">
													<img src="img/small/s5.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">mock up</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s6.jpg" data-rel="prettyPhoto">
													<img src="img/small/s6.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">retro badges</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s7.jpg" data-rel="prettyPhoto">
													<img src="img/small/s7.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">details</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>

									<div class="slider-item">
										<div class="slider-image">
											<div class="hover_colour">
												<a href="img/large/s8.jpg" data-rel="prettyPhoto">
													<img src="img/small/s8.jpg" alt="" /></a>
											</div>
										</div>
										<div class="slider-title">
											<h3><a href="#">vintage form</a></h3>
											<p>An his tamquam postulant, pri id mazim nostrud diceret.</p>
										</div>
									</div>
								</div>
								<div id="sl-prev" class="widget-scroll-prev"><i class="icon-chevron-left white"></i></div>
								<div id="sl-next" class="widget-scroll-next"><i class="icon-chevron-right white but_marg"></i></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--//page-->

			<div class="pad25 hidden-desktop"></div>
		</div>

		<!-- footer -->
		<div id="footer">
			<h1>get in touch</h1>
			<h3 class="center follow">
				We're social and we'd love to hear from you! Feel free to send us an email, find us on Google Plus, follow us on Twitter and join us on Facebook.</h3>

			<div class="follow_us">
				<a href="#" class="zocial twitter"></a>
				<a href="#" class="zocial facebook"></a>
				<a href="#" class="zocial linkedin"></a>
				<a href="#" class="zocial googleplus"></a>
				<a href="#" class="zocial vimeo"></a>
			</div>
		</div>

		<!-- footer 2 -->
		<div id="footer2">
			<div class="container">
				<div class="row">
					<div class="span12">
						<div class="copyright">
							BUSCOPINIONES
							&copy;
							<script type="text/javascript">
								//<![CDATA[
								var d = new Date();
								document.write(d.getFullYear());
								//]]>
							</script>
							- Todos los derechos reservados por Buscopiniones&#8482;							
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- up to top -->
		<a href="#"><i class="go-top hidden-phone hidden-tablet  icon-double-angle-up"></i></a>
		<!--//end-->


		<script src="js/bootstrap.min.js"></script>	
		<script src="js/jquery.touchSwipe.min.js"></script>
		<script src="js/jquery.mousewheel.min.js"></script>				
		<script src="js/superfish.js"></script>
		<script type="text/javascript" src="js/jquery.prettyPhoto.js"></script>
		<script type="text/javascript" src="js/scripts.js"></script>
		<!-- carousel -->
		<script type="text/javascript" src="js/jquery.carouFredSel-6.2.1-packed.js"></script>
		<script type="text/javascript">
								//<![CDATA[
								jQuery(document).ready(function($) {
									$("#slider_home").carouFredSel({width: "100%", height: "auto",
										responsive: true, circular: true, infinite: false, auto: false,
										items: {width: 231, visible: {min: 1, max: 3}
										},
										swipe: {onTouch: true, onMouse: true},
										scroll: {items: 3, },
										prev: {button: "#sl-prev", key: "left"},
										next: {button: "#sl-next", key: "right"}
									});
								});
								//]]>
		</script>

		<!-- slider -->
		<script type="text/javascript" src="js/jquery.nerveSlider.min.js"></script>
		<script>
			//<![CDATA[
			$(document).ready(function() {
				$(".myslider").show();
				$(".myslider").startslider({
					slideTransitionSpeed: 500,
					slideTransitionEasing: "easeOutExpo",
					slidesDraggable: true,
					sliderResizable: true,
					showDots: true,
				});
			});
			//]]>
		</script>
	</body>
</html>

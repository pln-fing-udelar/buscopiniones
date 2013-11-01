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
		<script>
			$(function() {
				$(".bsqAvanzada").click(function(event) {
					event.preventDefault();
					$(".divOcultar").slideToggle();
				});
			});

			var prmstr = window.location.search.substr(1);
			var prmarr = prmstr.split("&");
			var params = {};
			var mostrarBusqAvanzada = false;
			for (var i = 0; i < prmarr.length; i++) {
				var tmparr = prmarr[i].split("=");
				params[tmparr[0]] = tmparr[1];
				if ((tmparr[0] != null && tmparr[0] == "desde" && tmparr[1] != null && tmparr[1] != "")
						|| (tmparr[0] != null && tmparr[0] == "hasta" && tmparr[1] != null && tmparr[1] != "")
						|| (tmparr[0] != null && tmparr[0] == "medioDePrensa" && tmparr[1] != null && tmparr[1] != "")
						|| (tmparr[0] != null && tmparr[0] == "cantResultados" && tmparr[1] != null && tmparr[1] != "")) {
					mostrarBusqAvanzada = true;
				}
			}
			if (mostrarBusqAvanzada) {
				$(document).ready(function() {
					$(".divOcultar").slideToggle();
				});
			}
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
							<li class="last"><a href="Contacto">Contacto</a></li>
						</ul>
					</div>
				</nav>
			</div>
		</div>
		<!--//header-->
		<!--page-->
		<style>
			#formBusqueda label {width:8%; text-align:right; margin-right:4px; margin-bottom:12px}
			#formBusqueda label.samallLabel {width:4%}
			@media (max-width:768px) {
				#formBusqueda label,formBusqueda label.samallLabel {width:100%; display:block; text-align:left; margin:10px 0 !important}
			}
			#quizasquiso label{width:70%; text-align:left; margin-right:4px; margin-bottom:12px}
		</style>
		<div style="background:rgb(240, 240, 240); box-shadow:3px 3px 3px #cecece;">
			<div id="formBusqueda" class="container">				
				<form method="GET" class="form-inline" style="margin:14px 0 14px 0;">
					<label>Opiniones de:</label>
					<input type="text" <% if (request.getParameter("fuente") != null) {%>value="<%= request.getParameter("fuente")%>" <% }%> name="fuente" title="Ingrese la fuente de la opinion" style="margin-right:10px;" />
					<label class="samallLabel">sobre:</label>
					<input type="text" <% if (request.getParameter("asunto") != null) {%>value="<%= request.getParameter("asunto")%>" <% }%> name="asunto" title="Ingrese el asunto de la opinion"  style="margin-right:10px" />
					<input type="submit" name="buscar" value="Buscar" class="btn btn-medium btn-primary btn-rounded" style="padding:8px 20px;" />
					<a href="#" class="bsqAvanzada"> <i class="icon-expand-alt "></i> Búsqueda avanzada</a>


					<div class="divOcultar" style="display:none;">
						<label>Opiniones desde: </label>
						<input type="text" <% if (request.getParameter("desde") != null) {%>value="<%= request.getParameter("desde")%>" <% }%> name="desde" id="desde" title="Ingrese la fecha inicial" style="margin-right:10px;" />
						<label class="samallLabel">hasta: </label>
						<input type="text" <% if (request.getParameter("hasta") != null) {%>value="<%= request.getParameter("hasta")%>" <% }%> name="hasta" id="hasta" title="Ingrese la fecha final" style="margin-right:10px" />
						<br />
						<label>Medio de prensa:</label>
						<select name="medioDePrensa" id="medioDePrensa" title="Ingrese el medio de Prensa" style="margin-right:10px">
							<option value="">Todos</option>
							<option value="elpais" <% if (request.getParameter("medioDePrensa") != null && request.getParameter("medioDePrensa").equals("elpais")) {%> selected<% }%>>El País</option>
							<option value="larepublica" <% if (request.getParameter("medioDePrensa") != null && request.getParameter("medioDePrensa").equals("larepublica")) {%> selected<% }%>>La República</option>
							<option value="elobservador" <% if (request.getParameter("medioDePrensa") != null && request.getParameter("medioDePrensa").equals("elobservador")) {%> selected<% }%>>El Observador</option>
						</select>

						<br />
						<label>Cantidad de resultados:</label>
						<input type="text" <% if (request.getParameter("cantResultados") != null) {
							   %>value="<%= request.getParameter("cantResultados")%>" <%
								   }%> name="cantResultados" id="cantResultados" title="Ingrese la cantidad de resultados" style="margin-right:10px" />
						<br />
					</div>
				</form>

			</div>
			<%
				String spellCheckFuente = request.getParameter("fuente");
				String spellCheckAsunto = request.getParameter("asunto");
				boolean sugiero = false;
				if (request.getAttribute("spellCheckFuente") != null && !((String) request.getAttribute("spellCheckFuente")).isEmpty()) {
					sugiero = true;
					spellCheckFuente = (String) request.getAttribute("spellCheckFuente");
				}
				if (request.getAttribute("spellCheckAsunto") != null && !((String) request.getAttribute("spellCheckAsunto")).isEmpty()) {
					sugiero = true;
					spellCheckAsunto = (String) request.getAttribute("spellCheckAsunto");
				}
				if (sugiero) {
			%>
			<div id="quizasquiso" class="container"><label>Quizás quiso decir: <a href="?fuente=<%= spellCheckFuente%>&asunto=<%= spellCheckAsunto%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>"> Opiniones de <%= spellCheckFuente%> sobre <%= spellCheckAsunto%></a></label></div>
			<%
				}
			%>
		</div>

		<div id="timeline-embed"></div>
		<script type="text/javascript">
			var timeline_config = {
				width: "100%",
				height: "100%",
				source: './JsonTimeline?fuente=<%= request.getParameter("fuente")%>&asunto=<%= request.getParameter("asunto")%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>&medioDePrensa=<%= request.getParameter("medioDePrensa")%>&cantResultados=<%= request.getParameter("cantResultados")%>',
				//source: 'example_json.json',
				start_at_end: true,
				start_at_slide: <%= request.getAttribute("start_at_slide")%>,
				lang: 'es'
			};
		</script>
		<script type="text/javascript" src="./compiled/js/storyjs-embed.js"></script>
		<!-- /SLIDER -->


		<!--//banner-->

		<div class="container wrapper">
			<div class="inner_content">
				<div class="pad45"></div>

				<!--info boxes-->
				<div class="row">
					<div class="span3" style="width:100%">
						<div class="tile">
							<div class="intro-icon-disc cont-large"><i class="icon-user intro-icon-large"></i></div>
							<h3><small>FUENTES</small>
								<br /><span>Otras fuentes que opinaron sobre este tema</span></h3>
							<h2>
								<p>
									<%
										Collection<String> fuentes = (Collection<String>) request.getAttribute("fuentes");
										for (String fuente : fuentes) {
									%>
									<a href="?fuente=<%= fuente%>&asunto=<%= request.getParameter("asunto")%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>"><b><%= fuente%></b></a><br/>
											<%
												}
											%>
								</p>
							</h2>
						</div> 
						<div class="pad25"></div>
					</div> 


				</div> 


			</div>
			<!--//page-->

			<div class="pad25 hidden-desktop"></div>
		</div>

		<!-- footer -->
		<div id="footer">
			<h1>Ponte en contacto</h1>
			<h3 class="center follow">
				¿Te ha sido de utilidad esta herramienta? Ponte en contacto con nosotros <a href="Contacto">aquí</a></h3>

			<div class="follow_us">
				<a href="#" class="zocial twitter"></a>
				<a href="#" class="zocial facebook"></a>
				<a href="#" class="zocial linkedin"></a>
				<a href="https://plus.google.com/u/0/b/108400617712156541730/108400617712156541730/posts" class="zocial googleplus"></a>
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

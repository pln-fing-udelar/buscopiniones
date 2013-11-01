<%@page import="sun.misc.BASE64Encoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="buscopiniones.Noticia"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<html>
	<head>
		<meta charset="utf-8">
		<title>Tema de la semana</title>
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
			$( document ).ready(function() {
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

							<li class="sub-menu "><a href="Home">Opiniones</a> 								
							</li>
							<li class="sub-menu active"><a href="VerTemas">Temas</a>								
							</li>							
							<li class="last"><a href="Contacto">Contacto</a></li>
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
					<label>El tema desde: </label>
					<input type="text" <% if (request.getParameter("desde") != null) {%>value="<%= request.getParameter("desde")%>" <% }%> name="desde" id="desde" title="Ingrese la fecha inicial" style="margin-right:10px;" />
					<label>hasta: </label>
					<input type="text" <% if (request.getParameter("hasta") != null) {%>value="<%= request.getParameter("hasta")%>" <% }%> name="hasta" id="hasta" title="Ingrese la fecha final" style="margin-right:10px" />
					<input type="submit" name="buscar" value="Buscar" class="btn btn-medium btn-primary btn-rounded" style="padding:8px 20px;" />
				</form>
			</div>
		</div>
		<!--//banner-->

		<div class="container wrapper">
			<div class="inner_content">

				<!--<div id="options">                                           
                    <ul id="filters" class="option-set" data-option-key="filter">
                        <li><a href="#filter" data-option-value="*" class=" selected">All</a></li>
                        <li><a href="#filter" data-option-value=".category01">Category 01</a></li>                                            
                        <li><a href="#filter" data-option-value=".category02">Category 02</a></li>
                        <li><a href="#filter" data-option-value=".category03">Category 03</a></li> 
                    </ul>                                           
                    <div class="clear"></div>
                </div>
				<!-- portfolio_block -->
				<div class="row">      
                    <div class="projects">
						<%
							if (request.getAttribute("Noticias") != null) {
								Collection<Noticia> noticias = ((Collection<Noticia>) request.getAttribute("Noticias"));
								String category = "category01";
								int j = 0;
								for (Noticia noti : noticias) {
									BASE64Encoder encoder = new BASE64Encoder();
									String base64 = encoder.encode(noti.getUrl().getBytes()).replaceAll("\r\n", "").replaceAll("\n", "");
									String imagen = "ImagenNoticia/" + base64 + ".jpg";
									if(j > 3){
										category = "category02";
									}else if(j > 7){
										category = "category03";
									}
									j++;
						%>
                        <div class="span3 element <%= category%>" data-category=" <%= category%>">
                            <div class="hover_img">
								<a href="<%= noti.getUrl()%>" data-rel="prettyPhoto[portfolio1]">	
									<img src="<%= imagen%>" alt="<%= noti.getTitle().replace("- Diario EL PAIS - Montevideo - Uruguay","")%>" /></a>
                            </div>  
                            <div class="item_description">
								<a href="<%= noti.getUrl()%>"><span><%= noti.getTitle().replace("- Diario EL PAIS - Montevideo - Uruguay","")%></span></a><br/>
								<p><%= noti.getDescripcion()%></p>
								<p><%= noti.getFecha()%></p>
								<p>
									Personas que opinaron sobre este tema:
									<br/>
									<%
										Collection<String> fuentes = noti.getFuentesRel();
										int i = 0;
										for (String fuente : fuentes) {
											if (i++ > 3) {
												break;
											}
									%>
									<a href="Home?fuente=<%= fuente%>&asunto=<%= URLEncoder.encode(noti.getTitle())%>&desde=<%= request.getParameter("desde")%>&hasta=<%= request.getParameter("hasta")%>"><b><%= fuente%></b></a><br/>
									<%
										}
									%>
								</p>
                            </div>                                    
                        </div>
						<%
								}
							}
						%>
					</div>
				</div>

				<div class="pad45"></div>
				
				
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



		
		<script src="js/jquery.isotope.min.js" type="text/javascript"></script>
		<script type="text/javascript" src="js/sorting.html"></script>
		<script type="text/javascript">
								//<![CDATA[
								$(window).load(function() {
									
									$('.projects').isotope({
									});
								});
								//]]>
		</script>		
		<script type="text/javascript">
			//<![CDATA[
			$(function() {
				$('div.element').hide();
			});
			var i = 0;//initialize
			var int = 0;
			$(window).bind("load", function() {
				var int = setInterval("doThis(i)", 100);
				fade in speed in milliseconds
			});
			function doThis() {
				var imgs = $('div.element').length;
				if (i >= imgs) {
					clearInterval(int);
				}
				$('div.element:hidden').eq(0).fadeIn(100);
				i++;//add 1 to the count
			}
			//]]>
		</script>
	</body>
</html>

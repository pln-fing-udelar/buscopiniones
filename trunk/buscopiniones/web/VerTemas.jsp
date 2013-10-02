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

				<div id="options">                                           
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
									<img src="<%= imagen%>" alt="<%= noti.getTitle()%>" /></a>
                            </div>  
                            <div class="item_description">
								<a href="<%= noti.getUrl()%>"><span><%= noti.getTitle()%></span></a><br/>
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

				<!--info boxes-->
				<div class="row">
					<%
						if (request.getAttribute("Noticias") != null) {
							Collection<Noticia> noticias = ((Collection<Noticia>) request.getAttribute("Noticias"));
							for (Noticia noti : noticias) {
					%>
					<div class="span3">
						<div class="tile">
							<div class="intro-icon-disc cont-large"><i class="icon-wrench intro-icon-large"></i></div>
							<h6><small>Noticia</small>
								<br/><a href="<%= noti.getUrl()%>"><span style="color: #2BA6CB;"><%= noti.getTitle()%></span></a></h6>
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
						<div class="pad25"></div>
					</div>
					<%
							}
						}
					%>

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
							FLATI
							&copy;
							<script type="text/javascript">
								//<![CDATA[
								var d = new Date();
								document.write(d.getFullYear());
								//]]>
							</script>
							- All Rights Reserved :
							Template by <a href="http://spiralpixel.com/">Spiral Pixel</a>
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

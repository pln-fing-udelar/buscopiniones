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

							<li class="sub-menu"><a href="Home">Opiniones</a> 								
							</li>
							<li class="sub-menu"><a href="VerTemas">Temas</a>								
							</li>							
							<li class="last active"><a href="contact.html">Contacto</a></li>
						</ul>
					</div>
				</nav>
			</div>
		</div>
		<!--//header-->
		<!--page-->
		<div id="banner">
			<div class="container intro_wrapper">
				<div class="inner_content">
					<h1>Ponte En Contacto</h1>
					<h1 class="title">Información de Contacto</h1>

					<h1 class="intro">Si nuestra herramienta para la <span>búsqueda</span> de <span>opiniones</span>
						te ha parecido <span class="hue">útil</span> entonces ponte en <span>contacto</span> con nosotros.</h1>
				</div>
			</div>
		</div>

		<!--//GOOGLE MAP -ADD YOUR EMBED INFO HERE-->
		<div id="map">
			<iframe src="https://maps.google.com/maps?f=q&amp;source=s_q&amp;hl=en&amp;geocode=&amp;q=Facultad+de+Ingenier%C3%ADa,+Av.+Julio+Herrera+y+Reissig,+Montevideo,+Montevideo+Department,+Uruguay&amp;aq=0&amp;oq=facultad+de+ingenieria+montevideo+uruguay&amp;sll=37.0625,-95.677068&amp;sspn=42.581364,86.572266&amp;ie=UTF8&amp;hq=&amp;hnear=&amp;t=m&amp;ll=-34.916077,-56.166143&amp;spn=0.006158,0.009141&amp;z=16&amp;iwloc=lyrftr:m,17510820920065147146,-34.918171,-56.166573&amp;output=embed"></iframe><br /><small><a href="https://maps.google.com/maps?f=q&amp;source=embed&amp;hl=en&amp;geocode=&amp;q=Facultad+de+Ingenier%C3%ADa,+Av.+Julio+Herrera+y+Reissig,+Montevideo,+Montevideo+Department,+Uruguay&amp;aq=0&amp;oq=facultad+de+ingenieria+montevideo+uruguay&amp;sll=37.0625,-95.677068&amp;sspn=42.581364,86.572266&amp;ie=UTF8&amp;hq=&amp;hnear=&amp;t=m&amp;ll=-34.916077,-56.166143&amp;spn=0.006158,0.009141&amp;z=16&amp;iwloc=lyrftr:m,17510820920065147146,-34.918171,-56.166573" ></iframe>
		</div>
		<div class="pad10"></div>

		<div class="container wrapper">
			<div class="inner_content">
				<div class="pad10"></div>
				<div class="row">
					<div class="span4">
						<h3>¿Tienes alguna consulta acerca de nuestra herramienta de búsqueda? Por favor, ponte en contacto con nosotros a través de este formulario</h3>

						<h5>
							<span>Dirección</span><br>
							Facultad de Ingeniería, Udelar<br>
							Julio Herrera y Reissig 565,<br>							
							Montevideo, Uruguay					
						</h5>

						<h5>
							E: <a href="mailto:#">contacto@buscopiniones.com</a><br>							
						</h5>
					</div>

					<div class="span8 ">
						<div class="contact_form">  
							<div id="note"></div>
							<div id="fields">
								<form method="GET" id="ajax-contact-form">
									<p class="form_info">nombre <span class="required">*</span></p>
									<input class="span5" type="text" name="nombre" value="" />
									<p class="form_info">email <span class="required">*</span></p>
									<input class="span5" type="text" name="email" value=""  />
									<p class="form_info">asunto</p>
									<input class="span5" type="text" name="asunto" value="" /><br>
									<p class="form_info">mensaje</p>
									<textarea name="mensaje" id="message" class="span8" ></textarea>
									<div class="clear"></div>

									<input type="submit" name="enviar" class="btn  btn-primary btn-form marg-right5" value="enviar" />
									<input type="reset"  class="btn  btn-primary btn-form" value="reset" />
									<div class="clear"></div>
								</form>
							</div>
						</div>                   
					</div>                	
				</div>
			</div>
		</div>
		<!--//page-->

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

		<script src="js/jquery.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script src="js/superfish.js"></script>
		<script type="text/javascript" src="js/scripts.js"></script>
		
	</body>
</html>

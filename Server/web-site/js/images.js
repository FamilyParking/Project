jQuery(function($){
				
		jQuery('.open-header').click(showHeader);
		jQuery('.close-header').click(hideHeader);
		
		function showHeader(){
		jQuery('.header-content').slideDown('medium');
		jQuery('.close-header').slideDown('fast');
		jQuery('.open-header').fadeOut('fast');}
		
		function hideHeader(){
		jQuery('.header-content').slideUp('medium');
		jQuery('.close-header').fadeOut('fast');
		jQuery('.open-header').fadeIn('fast');}

		$.supersized({
				
					// Functionality
					slide_interval          :   3000,		// Length between transitions
					transition              :   1, 			// 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
					transition_speed		:	2000,		// Speed of transition
															   
					// Components							
					slide_links				:	'blank',	// Individual links for each slide (Options: false, 'num', 'name', 'blank')
					slides 					:  	[			// Slideshow Images
														{image : 'images/background/01.jpg'},
														{image : 'images/background/02.jpg'},
														{image : 'images/background/03.jpg'},
														{image : 'images/background/04.jpg'},
														{image : 'images/background/05.jpg'},
														{image : 'images/background/06.jpg'},
												]
					
				});
		    });
// Code goes here

var myApp = angular.module('myApp',['angularUtils.directives.dirPagination','ui.bootstrap']);



function MyController($scope, $http, $window) {

	$scope.showData = function()
	{
	
	  $scope.currentPage = 1;
	  $scope.pageSize = 10;
	  $scope.groupSearch = {'visibility': 'hidden'};
	  
	  $scope.options = [
	                      {'argument': 'time'},
	                      {'argument': 'phi'},
	                      {'argument': 'theta'}
	                  ];

	  
	  $scope.clusters= [ 
	                    { 
	                        "title" : "Time",
	                        "groups" : [ 
		                    { 
		                      "label" : "Time steps 1 to 15",
		                      "image1" : "groupImages/group 1/-72.png",
		                      "image2" : "groupImages/group 1/-36.png",
		                      "image3" : "groupImages/group 1/144.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 1 AND 15"
		                      },
		                    { 
		                      "label" : "Time steps 16 to 30",
		                      "image1" : "groupImages/group 2/-54.png",
		                      "image2" : "groupImages/group 2/-90.png",
		                      "image3" : "groupImages/group 2/36.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 16 AND 30"
		                      },
		                      { 
		                      "label" : "Time steps 31 to 45",
		                      "image1" : "groupImages/group 3/-108.png",
		                      "image2" : "groupImages/group 3/36.png",
		                      "image3" : "groupImages/group 3/72.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 31 AND 45"
		                      },
		                      { 
		                      "label" : "Time steps 46 to 59",
		                      "image1" : "groupImages/group 4/0.png",
		                      "image2" : "groupImages/group 4/90.png",
		                      "image3" : "groupImages/group 4/126.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 46 AND 59"
		                      }]}/*,
	                    
	                    { 
	                        "title" : "Temp",
	                        "groups" : [ 
		                    { 
		                      "label" : "High Tem",
		                      "image1" : "groupImages/group 1/-72.png",
		                      "image2" : "groupImages/group 1/-36.png",
		                      "image3" : "groupImages/group 1/144.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 1 AND 15"
		                      },
		                    { 
		                      "label" : "Medium Temp",
		                      "image1" : "groupImages/group 2/-54.png",
		                      "image2" : "groupImages/group 2/-90.png",
		                      "image3" : "groupImages/group 2/36.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 16 AND 30"
		                      },
		                      { 
		                      "label" : "Low Temp",
		                      "image1" : "groupImages/group 3/-108.png",
		                      "image2" : "groupImages/group 3/36.png",
		                      "image3" : "groupImages/group 3/72.png",
		                      "location": ["","","","","","","","","",""],
		                      "query": "time between 31 AND 45"
		                      },
		                      { 
			                      "label" : "Extremely Low Temp",
			                      "image1" : "groupImages/group 3/-108.png",
			                      "image2" : "groupImages/group 3/36.png",
			                      "image3" : "groupImages/group 3/72.png",
			                      "location": ["","","","","","","","","",""],
			                      "query": "time between 31 AND 45"
			                      }]}*/
		                ];
	  
	  $scope.range = function() {
	      var rangeSize = 4;
	      var ps = [];
	      var start;
	
	      start = $scope.currentPage;
	      
	      if ( start > $scope.pageCount()-rangeSize ) {
	        start = $scope.pageCount()-rangeSize+1;
	      }
	
	      for (var i=start; i<start+rangeSize; i++) {
	        if(i>=0) 
	           ps.push(i);
	      }
	      return ps;
	    };

 
	    $scope.prevPage = function() {
	      if ($scope.currentPage > 0) {
	        $scope.currentPage--;
	      }
	    };
	
	    $scope.DisablePrevPage = function() {
	      return $scope.currentPage === 0 ? "disabled" : "";
	    };
	
	    $scope.pageCount = function() {
	      return Math.ceil($scope.phis.length*$scope.thetas.length*$scope.times.length/$scope.itemsPerPage)-1;
	    };
	
	    $scope.nextPage = function() {
	      if ($scope.currentPage < $scope.pageCount()) {
	        $scope.currentPage++;
	      }
	    };
	
	    $scope.DisableNextPage = function() {
	      return $scope.currentPage === $scope.pageCount() ? "disabled" : "";
	    };
	
	    $scope.setPage = function(n) {
	      $scope.currentPage = n;
	    };
	}
	
	
	$scope.pageChangeHandler = function(num) {
	      console.log('images page changed to ' + num);
	  };
	  
	  
		$scope.clearSearch = function() {
			 $scope.arguments = '';
			 $scope.cinemaSearch='';
			  $scope.groupSearch = {'visibility': 'hidden'};	
			  $scope.paginationTab = {'visibility': 'hidden'};		
		  };
		  
	  $scope.doSearch = function() {
			$http.get('http://localhost:8080/CinemaWeb/rest/searchCinema/'+$scope.cinemaSearch).
	      success(function(data) {
	          $scope.message = data;  
	          if($scope.message=='Null')
	        	  $scope.message = 'Invalid Error';
	          else
	        	  $scope.arguments = data;
	          $scope.groupSearch = {'visibility': 'visible'};	
	          $scope.paginationTab = {'visibility': 'visible'};	
	            });
		  };
	


	$scope.doSpecificSearch = function(specificQuery) {
		$http.get('http://localhost:8080/CinemaWeb/rest/searchCinema/'+specificQuery+' and '+$scope.cinemaSearch).
	  success(function(data) {
	      
	    	  $scope.arguments = data;
	          $scope.groupSearch = {'visibility': 'visible'};	
	          $scope.paginationTab = {'visibility': 'visible'};	
	        });
	  };
	  
	  
	  $scope.pageChangeHandler = function(num) {
		    console.log('going to page ' + num);
		  };
	}

  
	 


/*function OtherController($scope) {
  $scope.pageChangeHandler = function(num) {
    console.log('going to page ' + num);
  };
  
  $scope.clearPagination = function() {
	  $scope.paginationTab = {'visibility': 'hidden'};	
	  };
  
}*/


function PostsCtrlAjax($scope, $http)
{
   $http({method: 'POST', url: 'data/info.json'}).success(function(data)
   {
              $scope.posts = data; // response data
   });
}

function Search($scope, $http) {
    $http.get('http://localhost:8080/CinemaWeb/rest/searchCinema').
        success(function(data) {
            $scope.greeting = data;
           
        });
}
     


myApp.controller('MyController', MyController);
/*myApp.controller('OtherController', OtherController);*/
myApp.controller('PostsCtrlAjax', PostsCtrlAjax);
myApp.controller('Search', Search);




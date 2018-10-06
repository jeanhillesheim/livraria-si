angular.module('BookStore', [])

	// Controller
	.controller('BookController', ['$rootScope', '$scope', 'BookService', function($rootScope, $scope, BookService) {
		
		$scope.users = [];
		
		$scope.init = function() {
			BookService.loadBooks()
				.then(
					function(response) {
						$scope.users = response.data;
						console.log(response);
					}
				);
		}
		
		
	}])
	
	// Service
	.service('BookService', ['$http', '$rootScope', function($http, $rootScope) {
		
		this.loadBooks = function() {
			return $http({
				method: 'GET',
				url: $rootScope.baseUrl + 'livraria/books'
			});
		}
		
	}])
	
	// Filter
	.filter('format', function() {
	    return function(x) {
	    		if (!x) return '';
	    		var temp = x.split('_');
	        return temp.join(' ');
	    };
	})

	.run(function($rootScope) {
		$rootScope.baseUrl = angular.element('#baseUrl').val();
	});
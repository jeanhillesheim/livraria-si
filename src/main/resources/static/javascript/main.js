angular.module('BookStore', [])

	// Controller
	.controller('BookController', ['$rootScope', '$scope', 'BookService', function($rootScope, $scope, BookService) {
		
		$scope.users = [];
		
		$scope.init = function() {
			BookService.loadBooksByUser()
				.then(
					function(response) {
						$scope.users = response.data;
					}
				);
		}
		
		
	}])
	
	// Service
	.service('BookService', ['$http', '$rootScope', function($http, $rootScope) {

		this.loadBooksByUser = function() {
			return $http({
				method: 'GET',
				url: $rootScope.baseUrl + 'livraria/books-by-user'
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
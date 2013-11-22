from rest_framework.routers import Route, DefaultRouter

class CustomRouter(DefaultRouter):
	routes = [
        		# List route.
        		Route(
            			url=r'^{prefix}{trailing_slash}$',
            			mapping={
                			'get': 'list',
                			'post': 'create'
            			},
            			name='{basename}-list',
            			initkwargs={'suffix': 'List'}
        		),
        		# Detail route.
        		Route(
            			url=r'^{prefix}/{lookup}{trailing_slash}$',
            			mapping={
                			'get': 'retrieve',
                			'put': 'update',
                			'patch': 'partial_update',
                			'delete': 'destroy'
            			},
            			name='{basename}-detail',
            			initkwargs={'suffix': 'Instance'}
        		),
        			# Dynamically generated routes.
        			# Generated using @action or @link decorators on methods of the viewset.
        		Route(
            			url=r'^{prefix}/{methodname}{trailing_slash}$',
            			mapping={
                			'{httpmethod}': '{methodname}',
            			},
            			name='{basename}-{methodnamehyphen}',
            			initkwargs={}
        		),
    		]

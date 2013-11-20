from django.conf.urls import url, patterns, include
from django.contrib.auth.models import User, Group
from django.contrib import admin
from nfc.models import WaitingItem, UsedItem
admin.autodiscover()

from rest_framework import viewsets, routers
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope


# ViewSets define the view behavior.
class UserViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    model = User


class GroupViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.IsAuthenticated, TokenHasScope]
    required_scopes = ['groups']
    model = Group

class WaitingViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    model = WaitingItem

    #@link(renderer_classes=[renderers.StaticHTMLRenderer])
    @link()
    def highlight(self, request, *args, **kwargs):
        #snippet = self.get_object()
	a = 1
	return Response(a)
        #return Response(snippet.highlighted)


class UsedViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    model = UsedItem

    #@link(renderer_classes=[renderers.StaticHTMLRenderer])
    @link()
    def highlight(self, request, *args, **kwargs):
        #snippet = self.get_object()
	a = 1
	return Response(a)
        #return Response(snippet.highlighted)


# Routers provide an easy way of automatically determining the URL conf
router = routers.DefaultRouter()
#router.register(r'users', UserViewSet)
#router.register(r'groups', GroupViewSet)
router.register(r'waitings', WaitingViewSet)


# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browseable API.
urlpatterns = patterns('',
    url(r'^', include(router.urls)),
    url(r'^nfc/', include('nfc.urls')),
    url(r'^o/', include('oauth2_provider.urls', namespace='oauth2_provider')),
    url(r'^admin/', include(admin.site.urls)),
)

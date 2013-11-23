from django.conf.urls import url, patterns, include
from django.contrib.auth.models import User, Group
from django.contrib import admin
from nfc.models import WaitingItem, UsedItem
from nfc.viewsets import WaitingViewSet, UsedViewSet, StatisticViewSet
admin.autodiscover()

from routers import CustomRouter
from nfc.views import ApiEndpoint
from rest_framework import viewsets, routers
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope


# Routers provide an easy way of automatically determining the URL conf
router = routers.DefaultRouter()
router.register(r'waitings', WaitingViewSet)
router.register(r'useds', UsedViewSet)
#router.register(r'statistics', StatisticViewSet)
# Wire up our API using automatic URL routing.

router2 = CustomRouter(trailing_slash=True)
router2.register(r'statistics', StatisticViewSet)

# Additionally, we include login URLs for the browseable API.
urlpatterns = patterns('',
    url(r'^', include(router.urls)),
    url(r'^', include(router2.urls)),
    #url(r'^nfc/', include('nfc.urls')),
    url(r'^o/', include('oauth2_provider.urls', namespace='oauth2_provider')),
    url(r'^admin/', include(admin.site.urls)),
    #url(r'^statistic/test', StatisticViewSet.as_view({'get': 'test'})),
    url(r'^api/hello', ApiEndpoint.as_view()),
)


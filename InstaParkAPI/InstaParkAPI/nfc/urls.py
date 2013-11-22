from django.conf.urls import patterns, url

urlpatterns = patterns('nfc.views',
    url(r'^nfc/$', 'waiting_list'),
    url(r'^nfc/(?P<pk>[0-9]+)/$', 'waiting_detail'),
)



from models import WaitingItem, UsedItem
from serializers import WaitingItemSerializer, UsedItemSerializer
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope


class WaitingViewSet(viewsets.ModelViewSet):

	permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    	model = WaitingItem

    	@link()
    	def newest(self, request, *args, **kwargs):
		result = WaitingItem.objects.raw("SELECT * FROM nfc_waitingitem ORDER BY checkin desc LIMIT 1")[0]
		serializer = WaitingItemSerializer(result)
		print serializer
		return Response(serializer.data)

	@link()
	def newest(self, request, *args, **kwargs):
		result = WaitingItem.objects.raw("SELECT * FROM nfc_waitingitem ORDER BY checkin desc LIMIT 1")[0]
		serializer = WaitingItemSerializer(result)
		print serializer
                return Response(serializer.data)

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




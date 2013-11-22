from models import WaitingItem, UsedItem
from serializers import WaitingItemSerializer, UsedItemSerializer
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from rest_framework import status
from django.core.exceptions import ObjectDoesNotExist
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope
import datetime

class WaitingViewSet(viewsets.ModelViewSet):

	permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    	model = WaitingItem

    	@link()
    	def newest(self, request, *args, **kwargs):
		rawQuerySet = WaitingItem.objects.raw("SELECT * FROM nfc_waitingitem ORDER BY checkin desc LIMIT 1")
		if len(list(rawQuerySet)) > 0:
			serializer = WaitingItemSerializer(rawQuerySet[0])
			return Response(serializer.data)
		else:
			return Response(None)
	
	@link()
	def oldestOfToday(self, request, *args, **kwargs):
		current_date = datetime.datetime.now().strftime('%Y-%m-%d 00:00:00')
		query = "SELECT * FROM nfc_waitingitem WHERE checkin >= '%s'  ORDER BY checkin desc LIMIT 1" % current_date
		rawQuerySet = WaitingItem.objects.raw(query)
		if len(list(rawQuerySet)) > 0:
			serializer = WaitingItemSerializer(rawQuerySet[0])
			return Response(serializer.data)
		else:
			return Response(None)

class UsedViewSet(viewsets.ModelViewSet):
    	permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    	model = UsedItem

	def create(self, request, *args, **kwargs):
        	serializer = self.get_serializer(data=request.DATA, files=request.FILES)

        	if serializer.is_valid():
			try:
				waitingItem = WaitingItem.objects.get(nfcid=serializer.object.nfcid)
				serializer.object.checkin = waitingItem.checkin
				self.pre_save(serializer.object)
            			self.object = serializer.save(force_insert=True)
            			self.post_save(self.object, created=True)
				waitingItem.delete()
            			headers = self.get_success_headers(serializer.data)
            			return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)
			except ObjectDoesNotExist:
				return Response("Not found this tag %s" %serializer.object.nfcid, status=status.HTTP_400_BAD_REQUEST)

	        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



    	@link()
    	def newest(self, request, *args, **kwargs):
		rawQuerySet = UsedItem.objects.raw("SELECT * FROM nfc_useditem ORDER BY checkout desc LIMIT 1")
		if len(list(rawQuerySet)) > 0:
			serializer = UsedItemSerializer(result)
			return Response(serializer.data)
		else:
			return Response(None)




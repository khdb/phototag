from models import WaitingItem, UsedItem
from serializers import WaitingItemSerializer, UsedItemSerializer
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from rest_framework import status
from django.core.exceptions import ObjectDoesNotExist
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope
from datetime import datetime
from django.db import connection

class StatisticType():
	DAY = "%H"
	WEEK = "%d"
	MONTH = "%d"
	YEAR = "%m"

class TableName():
	WAITING = "nfc_waitingitem"
	USED = "nfc_useditem"
	COLUMN_CHECKIN = "checkin"
	COLUMN_CHECKOUT = "checkout"

class StatisticViewSet(viewsets.GenericViewSet):
	
	permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
	model = WaitingItem

	@link()
	def checkinday(self, request, *args, **kwargs):
		datestr = request.GET.get('date')
		if datestr is None:
			return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		fromtime = date.strftime('%Y-%m-%d 00:00:00')
		totime = date.strftime('%Y-%m-%d 23:59:59')
		waitingitem = self.rawQuery(fromtime, totime, StatisticType.DAY, TableName.WAITING, TableName.COLUMN_CHECKIN)
		useditem = self.rawQuery(fromtime, totime, StatisticType.DAY, TableName.USED, TableName.COLUMN_CHECKIN)
		resultmap = []
		print "waitingitem = %s" %waitingitem
		print "useditem = %s" %useditem
		for index in range(0,24):
			key = "%02d" % (index,)
			d = dict(name = key, index = index, value = 0)
			if (waitingitem.has_key(key)):
				d["value"] += waitingitem[key]
			if (useditem.has_key(key)):
				d["value"] += useditem[key]
			resultmap.append(d)
	 	return Response(resultmap)

	@link()
	def checkinweek(self, request, *args, **kwarfs):
		return Response("Not implement...")
	
	@link()
	def checkinmonth(self, request, *args, **kwarfs):
		datestr = request.GET.get('date')
		if datestr is None:
			return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		fromtime = date.strftime('%Y-%m-1 00:00:00')
		totime = date.strftime('%Y-%m-31 23:59:59')
		waitingitem = self.rawQuery(fromtime, totime, StatisticType.MONTH, TableName.WAITING, TableName.COLUMN_CHECKIN)
		useditem = self.rawQuery(fromtime, totime, StatisticType.MONTH, TableName.USED, TableName.COLUMN_CHECKIN)
		resultmap = []
		print "waitingitem = %s" %waitingitem
		print "useditem = %s" %useditem
		for index in range(0,31):
			key = "%02d" % (index,)
			d = dict(name = key, index = index, value = 0)
			if (waitingitem.has_key(key)):
				d["value"] += waitingitem[key]
			if (useditem.has_key(key)):
				d["value"] += useditem[key]
			resultmap.append(d)
		#Get count of waiting item
	 	return Response(resultmap)


	def rawQuery(self, fromtime, totime, statistictype, table, column):
		cursor = connection.cursor()
		selectQuery = "SELECT strftime('%s', %s), count(*) " %(statistictype, column)
		fromQuery = "FROM %s " %table
		whereQuery = "WHERE(" + column + " >= %s AND " + column +" <= %s) "
		groupQuery = "GROUP BY (strftime('%s', checkin))" %statistictype
		query = selectQuery + fromQuery + whereQuery + groupQuery
		print query
    		cursor.execute(query, [fromtime, totime])
		#item_map = []
		d = {}
		for row in cursor.fetchall():
			d[row[0]] = row[1]
			#item_map.append(d)
		#print item_map
		return d
		#print self.dictfetchall(cursor)


	def dictfetchall(self, cursor):
    		"Returns all rows from a cursor as a dict"
		desc = cursor.description
		print "desc = %" %desc
		return [
        		dict(zip([col[0] for col in desc], row))
        		for row in cursor.fetchall()
    		]

	@link()
	def test(self, request, *args, **kwargs):
		print request.GET.get('date')
        	queryset = WaitingItem.objects.all()
        	serializer = WaitingItemSerializer(queryset, many=True)
		return Response(1)
        	#return Response(serializer.data)

	

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

	@link()
	def countToday(self, request, *args, **kwargs):
		begindate = datetime.now().strftime('%Y-%m-%d 00:00:00')
		rawQuerySet = WaitingItem.objects.filter(checkin__gte=begindate).count()
		return Response(rawQuerySet)	

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

	@link()
	def countToday(self, request, *args, **kwargs):
		begindate = datetime.now().strftime('%Y-%m-%d 00:00:00')
		rawQuerySet = UsedItem.objects.filter(checkout__gte=begindate).count()
		return Response(rawQuerySet)



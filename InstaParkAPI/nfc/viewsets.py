from models import WaitingItem, UsedItem
from serializers import WaitingItemSerializer, UsedItemSerializer
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.decorators import link
from rest_framework.response import Response
from rest_framework import status
from django.core.exceptions import ObjectDoesNotExist
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope
from datetime import datetime, timedelta
from django.db import connection
from django.utils.datastructures import MultiValueDictKeyError

import calendar

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
		datestr = request.GET.get('date')
		if datestr is None:
                	return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		fromdate = date - timedelta(6)
		fromtime = fromdate.strftime('%Y-%m-%d 00:00:00')
		totime = date.strftime('%Y-%m-%d 23:59:59')
		waitingitem = self.rawQuery(fromtime, totime, StatisticType.WEEK, TableName.WAITING, TableName.COLUMN_CHECKIN)
		useditem = self.rawQuery(fromtime, totime, StatisticType.WEEK, TableName.USED, TableName.COLUMN_CHECKIN)
		resultmap = []
		for index in range(0, 7):
			key = "%02d" % (index + fromdate.day)
			d = dict(name = key, index = index, value = 0)
			if (waitingitem.has_key(key)):
				d["value"] += waitingitem[key]
			if (useditem.has_key(key)):
				d["value"] += useditem[key]
			resultmap.append(d)
		return Response(resultmap)
	
	@link()
	def checkinmonth(self, request, *args, **kwarfs):
		datestr = request.GET.get('date')
		if datestr is None:
			return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		maxday = calendar.monthrange(date.year,date.month)[1]
		fromtime = date.strftime('%Y-%m-1 00:00:00')
		totime = date.strftime('%Y-%m-31 23:59:59')
		waitingitem = self.rawQuery(fromtime, totime, StatisticType.MONTH, TableName.WAITING, TableName.COLUMN_CHECKIN)
		useditem = self.rawQuery(fromtime, totime, StatisticType.MONTH, TableName.USED, TableName.COLUMN_CHECKIN)
		resultmap = []
		d1 = dict(name = "1~7", index = 1, value = 0)
		d2 = dict(name = "8~14", index = 2, value = 0)
		d3 = dict(name = "15~21", index = 3, value = 0)
		d4 = dict(name = "22~28", index = 4, value = 0)
		d5 = dict(name = "28~end", index = 5, value = 0)
		for index in range(1,maxday+1):
			key = "%02d" % (index)
			d = dict(name = key, index = index, value = 0)
			value = 0
			if (waitingitem.has_key(key)):
				value  += waitingitem[key]
			if (useditem.has_key(key)):
				value  += useditem[key]
			if value > 0:
				if index <= 7:
					d1["value"] += value
				elif index <= 14:
					d2["value"] += value
				elif index <= 21:
					d3["value"] += value
				elif index <= 28:
					d4["value"] += value
				else:
					d5["value"] += value
					
		resultmap.append(d1)
		resultmap.append(d2)
		resultmap.append(d3)
		resultmap.append(d4)
		resultmap.append(d5)
	 	return Response(resultmap)

	@link()
	def checkinyear(self, request, *args, **kwarfs):
		datestr = request.GET.get('date')
		if datestr is None:
			return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		fromtime = date.strftime('%Y-1-1 00:00:00')
		totime = date.strftime('%Y-12-31 23:59:59')
		waitingitem = self.rawQuery(fromtime, totime, StatisticType.YEAR, TableName.WAITING, TableName.COLUMN_CHECKIN)
		useditem = self.rawQuery(fromtime, totime, StatisticType.YEAR, TableName.USED, TableName.COLUMN_CHECKIN)
		resultmap = []
		for index in range(1,13):
			key = "%02d" % (index,)
			d = dict(name = key, index = index, value = 0)
			if (waitingitem.has_key(key)):
				d["value"] += waitingitem[key]
			if (useditem.has_key(key)):
				d["value"] += useditem[key]
			resultmap.append(d)
	 	return Response(resultmap)

	@link()
	def delta(self, request, *args, **kwarfs):
		datestr = request.GET.get('date')
		if datestr is None:
			return Response("Wrong paramater", status=status.HTTP_400_BAD_REQUEST)
		date = datetime.strptime(datestr, '%Y-%m-%d')
		fromtime = date.strftime('%Y-%m-1 00:00:00')
		totime = date.strftime('%Y-%m-31 23:59:59')
		deltaquery = self.rawDeltaQuery(fromtime, totime)
		thresholds = [3600, 7200, 10800, 14400, 18000, 21600, 25200, 28800, 32400, 36000]
		name = ["<1h", "<2h", "<3h", "<4h", "<5h", "<6h", "<7h", "<8h", "<9h", "<10h", ">10h"]
		qualities = [0, 0, 0, 0, 0, 0, 0, 0, 0 ,0, 0]
		for row in deltaquery:
			for idx, val in enumerate(thresholds):
				if row <= val:
					qualities[idx] += 1
					break
				if idx == (len(thresholds) - 1):
					print row
					qualities[idx+1] += 1
		resultmap = []
		for idx,val in enumerate(qualities):
			d = dict(index = idx, name = name[idx], value = val)
			resultmap.append(d)
		print deltaquery
	 	return Response(resultmap)


	def rawQuery(self, fromtime, totime, statistictype, table, column):
		cursor = connection.cursor()
		selectQuery = "SELECT strftime('%s', %s), count(*) " %(statistictype, column)
		fromQuery = "FROM %s " %table
		whereQuery = "WHERE(" + column + " >= %s AND " + column +" <= %s) "
		groupQuery = "GROUP BY (strftime('%s', checkin))" %statistictype
		query = selectQuery + fromQuery + whereQuery + groupQuery
    		cursor.execute(query, [fromtime, totime])
		d = {}
		for row in cursor.fetchall():
			d[row[0]] = row[1]
		cursor.close()
		return d

	def rawDeltaQuery(self, fromtime, totime):
		cursor = connection.cursor()
		selectQuery = "SELECT checkin, checkout "
		fromQuery = "FROM nfc_useditem "
		whereQuery = "WHERE(checkout >= %s AND checkout <= %s) "
		query = selectQuery + fromQuery + whereQuery
    		cursor.execute(query, [fromtime, totime])
		deltamap = []
		for row in cursor.fetchall():
			deltamap.append((row[1] - row[0]).seconds)
		cursor.close()
		return deltamap
		

	def getDateNam7DayAgo(self, date):
		result = []
		result.append(date.day)
		for i in range(0,6):
			date = date - timedelta(1)
			result.append(date.day)
		return result

	

class WaitingViewSet(viewsets.ModelViewSet):

	permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    	model = WaitingItem

    	def create(self, request, *args, **kwargs):
        	serializer = self.get_serializer(data=request.DATA, files=request.FILES)
        	if serializer.is_valid():
			try:
				imagefile = self.handlerUploadFile(request.FILES['file'], serializer.object.nfcid)
				serializer.object.image = imagefile
				print imagefile
				self.pre_save(serializer.object)
            			self.object = serializer.save(force_insert=True)
            			self.post_save(self.object, created=True)
            			headers = self.get_success_headers(serializer.data)
            			return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)
			except MultiValueDictKeyError:	
				return Response("Not found image.", status=status.HTTP_400_BAD_REQUEST)				
			except ObjectDoesNotExist:
				return Response("Not found this tag %s" %serializer.object.nfcid, status=status.HTTP_400_BAD_REQUEST)
		return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
	
	def handlerUploadFile(self, f, nfcid):
		timename = datetime.now().strftime("%Y%m%d-%H%M%S")
		filename = "%s-%s" %(nfcid, timename)
		mediafolder = "/home/maihuy/InstaParkMedia/"
		extension = ".jpg"
		absolutepath = "%s%s%s" %(mediafolder, filename, extension)
		with open(absolutepath, 'wb+') as destination:
        		for chunk in f.chunks():
            			destination.write(chunk)
		return "%s%s" %(filename, extension)

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
		print "Welcome create used item"
        	if serializer.is_valid():
			try:
				imagefile = self.handlerUploadFile(request.FILES['file'], serializer.object.nfcid)
				print imagefile
				waitingItem = WaitingItem.objects.get(nfcid=serializer.object.nfcid)
				serializer.object.checkin = waitingItem.checkin
				self.pre_save(serializer.object)
            			self.object = serializer.save(force_insert=True)
            			self.post_save(self.object, created=True)
				waitingItem.delete()
            			headers = self.get_success_headers(serializer.data)
            			return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)
			except MultiValueDictKeyError:	
				return Response("Not found image.", status=status.HTTP_400_BAD_REQUEST)				
			except ObjectDoesNotExist:
				return Response("Not found this tag %s" %serializer.object.nfcid, status=status.HTTP_400_BAD_REQUEST)

	        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
	
	def handlerUploadFile(self, f, nfcid):
		timename = datetime.now().strftime("%Y%m%d-%H%M%S")
		filename = "%s-%s" %(nfcid, timename)
		mediafolder = "/home/maihuy/InstaParkMedia/"
		extension = ".jpg"
		absolutepath = "%s%s%s" %(mediafolder, filename, extension)
		with open(absolutepath, 'wb+') as destination:
        		for chunk in f.chunks():
            			destination.write(chunk)
		return absolutepath

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



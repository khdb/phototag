from django.forms import widgets
from rest_framework import serializers
from models import WaitingItem, UsedItem


class WaitingItemSerializer(serializers.ModelSerializer):
	class Meta:
        	model = WaitingItem
        	fields = ('nfcid', 'image', 'checkin')

class UsedItemSerializer(serializers.ModelSerializer):
	class Meta:
        	model = UsedItem
        	fields = ('id', 'nfcid', 'checkin', 'checkout')

from django.db import models


class WaitingItem(models.Model):
    nfcid = models.CharField(unique=True,primary_key=True, max_length=20)
    image = models.CharField(max_length=100, blank=True, default='')
    checkin = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ('checkin',)

class UsedItem(models.Model):
    nfcid = models.CharField(max_length=20)
    checkin = models.DateTimeField(auto_now_add=True)
    checkout = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ('checkout',)


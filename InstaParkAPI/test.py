from random import randrange
from datetime import timedelta
from nfc.models import WaitingItem
import string
import random


def random_date(start, end):
    	"""
    	This function will return a random datetime between two datetime 
    	objects.
   	"""
	delta = end - start
	int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
	random_second = randrange(int_delta)
	return start + timedelta(seconds=random_second)

def id_generator(size=6, chars=string.ascii_uppercase + string.digits):
	return ''.join(random.choice(chars) for x in range(size))

def generateWaitingItem(start,end,count):
	for x in range(0, count):
		date = random_date(start, end)
		print "INSERT INTO nfc_waitingitem (nfcid, image, checkin) VALUES('ID%s', 'image path', '%s');" %(id_generator(),date.strftime('%Y-%m-%d %H:%M:%S'))


def generateUsedItem(start,end,count):
	for x in range(0, count):
		date = random_date(start, end)
		t = date.strftime('%Y-%m-%d %H:%M:%S')
 		print "INSERT INTO nfc_useditem (nfcid,  checkin, checkout) VALUES('ID%s', '%s', '%s');" %(id_generator(),t,t)


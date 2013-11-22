from django.forms import widgets
from rest_framework import serializers
from nfc.models import WaitingItem, LANGUAGE_CHOICES, STYLE_CHOICES


class WaitingItemSerializer(serializers.Serializer):
    #pk = serializers.Field()  # Note: `Field` is an untyped read-only field.
    nfcid = serializers.CharField(required=True, max_length=50)
    image = serializers.CharField(required=True, max_length=100)
    checkin = serializers.DateTimeField()

    def restore_object(self, attrs, instance=None):
        """
        Create or update a new snippet instance, given a dictionary
        of deserialized field values.

        Note that if we don't define this method, then deserializing
        data will simply return a dictionary of items.
        """
        if instance:
            # Update existing instance
            instance.nfcid = attrs.get('nfcid', instance.nfcid)
            instance.image = attrs.get('image', instance.image)
            instance.checkin = attrs.get('checkin', instance.checkin)
            return instance

        # Create new instance
	return WaitingItem(**attrs)

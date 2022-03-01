from rest_framework import serializers

from items.models import Item


class ItemSerializer(serializers.ModelSerializer):
    id = serializers.ReadOnlyField()
    name = serializers.CharField()
    value = serializers.IntegerField()

    class Meta:
        model = Item
        fields = ('id', 'name', 'value')

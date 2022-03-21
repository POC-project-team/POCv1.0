from rest_framework import generics, serializers

from items.models import Item
from items.serializers import ItemSerializer


class ItemsView(generics.RetrieveAPIView):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer

    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)


class ItemsListCreateView(generics.ListCreateAPIView):
    queryset = Item.objects.all()
    serializer_class = ItemSerializer


from django.db import models


class Item(models.Model):
    name = models.CharField(verbose_name='Name', max_length=1024)
    value = models.IntegerField(verbose_name='Value')


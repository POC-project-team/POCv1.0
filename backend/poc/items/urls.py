from django.urls import path

from .views import *

app_name = "items"

urlpatterns = [
    path('', ItemsListCreateView.as_view()),
    path('<pk>', ItemsView.as_view()),
]
from django.conf.urls import patterns, url, include
from django.contrib import admin
from rest_framework import routers
from api import viewsets

router = routers.DefaultRouter()
router.register(r'users', viewsets.UserViewSet)
router.register(r'groups', viewsets.GroupViewSet)
router.register(r'api-tokens', viewsets.AuthToken)
admin.autodiscover()

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browseable API.
urlpatterns = patterns('',
                       url(r'^login-queue/authenticate/$', 'backend.api.login_queue.authenticate'),
                       url(r'^', include(router.urls)),
                       url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
                       url(r'^admin/', include(admin.site.urls)),
)

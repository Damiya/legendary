from django.conf.urls import patterns, url, include
from django.contrib import admin
from rest_framework import routers
from api import views

router = routers.DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'groups', views.GroupViewSet)
router.register(r'api-tokens', views.AuthToken)
admin.autodiscover()

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browseable API.
urlpatterns = patterns('',
                       url(r'^', include(router.urls)),

                       #url(r'^api-tokens/$', views.AuthToken.as_view()),
                       url(r'^get-csrf-token/', views.GetCSRFToken.as_view()),

                       url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),

                       url(r'^admin/', include(admin.site.urls)),
)

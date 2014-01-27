import datetime

from django.contrib.auth.models import User, Group
from rest_framework import viewsets
from django.views.decorators.csrf import ensure_csrf_cookie
from django.utils.timezone import utc
from rest_framework.views import APIView
from rest_framework import status, parsers, renderers
from rest_framework.authtoken.models import Token
from rest_framework.authtoken.serializers import AuthTokenSerializer
from django.utils.decorators import method_decorator
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated

from .permissions import IsAdminOrSelf
from .serializers import UserSerializer, GroupSerializer


class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    permission_classes = (IsAdminOrSelf,)
    serializer_class = UserSerializer
    model = User

    def retrieve(self, request, *args, **kwargs):
        if kwargs['pk'] == 'current' and request.user:
            user_object = User.objects.get(id=request.user.id)
        elif isinstance(kwargs['pk'], (int, long)):
            user_object = User.objects.get(id=kwargs['pk'])

        if user_object:
            serializer = self.get_serializer(user_object)
            return Response(serializer.data)



class GroupViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    """
    API endpoint that allows groups to be viewed or edited.
    """
    queryset = Group.objects.all()
    serializer_class = GroupSerializer


class AuthToken(viewsets.ViewSet):
    throttle_classes = ()
    permission_classes = ()
    parser_classes = (parsers.FormParser, parsers.MultiPartParser, parsers.JSONParser,)
    renderer_classes = (renderers.JSONRenderer,)
    serializer_class = AuthTokenSerializer
    model = Token

    def create(self, request):
        serializer = self.serializer_class(data=request.DATA)
        if serializer.is_valid():
            serialized_user = serializer.object['user']
            token, created = Token.objects.get_or_create(user=serialized_user)

            if not created:
                # update the created time of the token to keep it valid
                token.created = datetime.datetime.utcnow().replace(tzinfo=utc)
                token.save()

            return Response({'token': token.key, 'user_id': serialized_user.id})
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def destroy(self, request, pk=None):
        if request.user:
            token = Token.objects.get(user=request.user)
            if token.key == pk:
                token.delete()
                return Response(status=status.HTTP_200_OK)
            return Response(status=status.HTTP_403_FORBIDDEN)
        else:
            return Response(status=status.HTTP_404_NOT_FOUND)


class GetCSRFToken(APIView):
    @method_decorator(ensure_csrf_cookie)
    def get(self, request):
        token = {'token': request.META['CSRF_COOKIE']}
        return Response(data=token, status=200)


get_csrf_token = GetCSRFToken.as_view()
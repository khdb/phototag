from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from nfc.models import WaitingItem
from nfc.serializers import WaitingItemSerializer

from rest_framework import permissions
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope, TokenHasScope

class JSONResponse(HttpResponse):
    """
    An HttpResponse that renders its content into JSON.
    """
    def __init__(self, data, **kwargs):
        content = JSONRenderer().render(data)
        kwargs['content_type'] = 'application/json'
        super(JSONResponse, self).__init__(content, **kwargs)


@csrf_exempt
def waiting_list(request):
    """
    List all code snippets, or create a new snippet.
    """
    permission_classes = [permissions.IsAuthenticated, TokenHasReadWriteScope]
    if request.method == 'GET':
        waitings = WaitingItem.objects.all()
        serializer = WaitingItemSerializer(waitings, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = WaitingItemSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        else:
            return JSONResponse(serializer.errors, status=400)

@csrf_exempt
def waiting_detail(request, pk):
    """
    Retrieve, update or delete a code snippet.
    """
    try:
        waiting = WaitingItem.objects.get(pk=pk)
    except WaitingItem.DoesNotExist:
        return HttpResponse(status=404)

    if request.method == 'GET':
        serializer = WaitingSerializer(waiting)
        return JSONResponse(serializer.data)

    elif request.method == 'PUT':
        data = JSONParser().parse(request)
        serializer = WaitingSerializer(waiting, data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data)
        else:
            return JSONResponse(serializer.errors, status=400)

    elif request.method == 'DELETE':
        waiting.delete()
        return HttpResponse(status=204)

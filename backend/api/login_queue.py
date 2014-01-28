from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes
import sys
import requests
import logging

lq_endpoint = 'https://lq.na1.lol.riotgames.com/login-queue/rest/'


@api_view(['POST'])
@permission_classes((IsAuthenticated, ))
def authenticate(request):
    url = lq_endpoint + 'queues/lol/authenticate'
    headers = {
        'Referer': 'app:/LolClient.swf/[[DYNAMIC]]/7',
        'x-flash-version': '11,7,700,169',
        'User-Agent': 'Mozilla/5.0 (Windows; U; en-US) AppleWebKit/533.19.4 (KHTML, like Gecko) AdobeAIR/3.7'
    }
    payload = request.DATA
    r = requests.post(url, headers=headers, data=payload, verify=False)
    return Response(data=r.json(), status=r.status_code)

@api_view(['GET'])
@permission_classes((IsAuthenticated, ))
def ticker(request):
    return Response(status=400)

#     node = data.node;
#     champ = data.champ;
#     rate = data.rate;
#     delay = data.delay;
#     tickers = data.tickers;
#     numTickers = tickers.length;
#     for (var i = 0; i < numTickers; i++) {
#     ticker = numTickers[i];
#     if (ticker.node != = node) {
#     continue;
#     }
#
#     id = ticker.id;
#     current = ticker.current;
#     break;
#
# }
#
# console.log('In queue, #' + (id - current) + ' in line');
# while (id - current > rate) {
# current = recheck();
# }

import asyncio
import aiohttp
import jinja2
from aiohttp import web
import aiohttp_jinja2
import json
from aiojobs.aiohttp import setup as setup_aiojobs
from utils import build_qrcode
import platform


#app = Flask(__name__)
#app.secret_key = "development key"



@aiohttp_jinja2.template('index.html')
async def init(request):    
    return  {
        'image': build_qrcode(
            request.headers['User-Agent'], 
            str(platform.dist()),)}


def do_login(sessionid, qrcode):
    pass



async def login(request):
    request_data = await request.json()
    if not request_data:
        return web.json_response("Invalid payload")
    else:
        response_obj = {
            "message": "Login successfull",
            "status": "success",
            "auth_token": "I will randomly generate you a token. This is just for test purposes"
        }
        return web.json_response(response_obj)



async def weblogin(request):

    response_object={
        "message" : "Invalid payload",
        "status": "fail"
    }
    authorization = request.headers['Authorization']
    request_data = await request.json()
    if not request_data and not authorization:
        return web.json_response(response_object)
    else:
        response_object["message"] = "Web login was successfull "
        response_object["status"] = "success"
        response_object["cookie"] = request.headers["Cookie"]
        response_object["auth_token"] = "I will randomly generate you a token. This is just for test purposes"
        return web.json_response(response_object)
   
    sess = aiohttp.ClientSession()
    ws =  await sess.ws_connect('http://localhost:8080/ws') 
    await ws.send_str("This is just to notify the client via Websocket")
  

async def websocket_handler(request):
    ws = web.WebSocketResponse()
    await ws.prepare(request)
    print(request)
    async for msg in ws:
        if msg.type == aiohttp.WSMsgType.TEXT:
            if msg.data == "success":
                await ws.send_str("Hello, Login was successful")
            elif msg.data == "fail":
                await ws.send_str("Invalid PayLoad")
            else:
                await ws.close()
        elif msg.type == aiohttp.WSMsgType.ERROR:
            ws.exception()

    return ws



def setup_routes(app):
    app.router.add_static('/static', 'static')
    app.router.add_get('/', init)
    app.router.add_post('/login', login)
    app.router.add_post('/weblogin', weblogin)
    app.router.add_get('/ws', websocket_handler)


@asyncio.coroutine
def create_app():

    app = web.Application()
    aiohttp_jinja2.setup(app, loader=jinja2.FileSystemLoader('templates'))
    app['static_root_url'] = '/static'
    setup_routes(app)
    setup_aiojobs(app)
    return app


asyncio.get_event_loop().run_until_complete(create_app())
web.run_app(create_app())
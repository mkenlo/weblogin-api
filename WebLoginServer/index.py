from flask import (
    Flask, redirect, url_for, jsonify, render_template, request, session)
import pyqrcode
import png
import platform
import jwt
import datetime
import base64
import json
import asyncio
import aiohttp
from aiohttp import web
import websockets


app = Flask(__name__)
app.secret_key = "development key"

def build_qrcode():
    built_QR = {
        "user_agent": request.headers['User-Agent'],
        "user_platform" : str(platform.dist()),
        "created_at" : str(datetime.datetime.utcnow()),
        'secret': "secret_salt"
    }    
    qr = pyqrcode.create(json.dumps(built_QR))
    return "data:image/png;base64,{}".format(
        qr.png_as_base64_str(scale=5))
    


@app.route("/")
def index():
    qr_image = build_qrcode()
    return render_template("index.html" , image= qr_image)


@app.route("/login", methods = ["POST"])
def login():
    return render_template("index.html")




if __name__ == "__main__":
    app.debug = True




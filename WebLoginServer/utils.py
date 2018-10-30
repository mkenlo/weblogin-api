import pyqrcode
import png
import platform
import jwt
import datetime
import base64
import json


def build_qrcode(browser, os):
    built_QR = {
        "user_agent": browser,
        "user_platform" : os,
        "created_at" : str(datetime.datetime.utcnow()),
        'secret': "secret_salt"
    }    
    qr = pyqrcode.create(json.dumps(built_QR))
    return "data:image/png;base64,{}".format(
        qr.png_as_base64_str(scale=5))
    
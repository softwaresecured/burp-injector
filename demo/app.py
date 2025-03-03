import subprocess
import pickle
import base64
from flask import Flask, make_response, request, abort, send_from_directory

app = Flask(__name__,static_folder="static")

@app.route("/", methods=['GET'])
def webroot():
    return send_from_directory("static", "main.html")

@app.route("/pickledemo", methods=['POST'])
def demo1():
    arg2base64 = request.form.get("arg2")
    arg2pickle = base64.b64decode(arg2base64)
    arg2object = pickle.loads(arg2pickle)
    result = subprocess.run(["/bin/sh","-c",arg2object["param7"][0]], stdout=subprocess.PIPE)
    output = result.stdout.decode("utf-8")
    if result.stderr is not None:
        output += result.stderr.decode("utf-8")
    response = make_response(output,200)
    return response

if __name__ == '__main__':
      app.run(host='0.0.0.0', port=5000)

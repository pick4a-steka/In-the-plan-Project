from slr import main
from flask import Flask, request, jsonify
import numpy
import cv2
import base64
from slr.utils.load_model import load_model

# Загрузка модели
loaded_model, label, hands = load_model()

app = Flask(__name__)
@app.route("/", methods=["POST"])
def index():
    data = request.json
    data64 = data['imageBase64']
    # Декодирование base64 в бинарные данные изображения
    image_data = base64.b64decode(data64)
    
    # Преобразование бинарных данных в массив numpy
    nparr = numpy.frombuffer(image_data, numpy.uint8)

    # Декодирование массива numpy в изображение с помощью OpenCV
    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    cv2.imshow("Image", image)
    cv2.waitKey(0)
    simbol = main(loaded_model, label, hands, image)
    print("Server response: ", simbol)
    answer = {"simbol" : simbol}
    return jsonify(answer)

if __name__ == "__main__":
    app.run(debug=True, host="192.168.195.160", port="5000")

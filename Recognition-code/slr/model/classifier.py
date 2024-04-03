import numpy #: импорт библотеки NumPy для работы с массивами и матрицами, а также множеством мат функций
import tensorflow

class KeyPointClassifier(object):
    def __init__( #: Конструктор с аргументами и значениями по умолчанию
        self, #: ключевое слово self используется в методах класса для обращения к атрибутам и методам этого же объекта класса.
        model_path="slr/model/mymodel_all-34.hdf5", #: указание пути к модели
        num_threads=1 #: аргумент для установки количества потоков
    ):
        self.model = tensorflow.keras.models.load_model(model_path) #: загрузка модели

    def __call__(self, landmark): #: метод, который используется для вызова экземпляра класса KeyPointClassifier как функции
        xList = []
        yList = []
        for id, lm in enumerate(landmark):
            px, py = lm[0], lm[1]
            xList.append(px)
            yList.append(py)
        xmin, xmax = min(xList), max(xList)
        ymin, ymax = min(yList), max(yList)
        magicx = max(landmark[0][0] - xmin, xmax - landmark[0][0])
        magicy = max(landmark[0][1] - ymin, ymax - landmark[0][1])
        bufcord = []
        for sublandmark in landmark:
            bufcord.append(((sublandmark[0] - landmark[0][0]) / magicx) * 0.5 + 0.5)
            bufcord.append(((sublandmark[1] - landmark[0][1]) / magicy) * 0.5 + 0.5)
        prediction_result = self.model.predict(numpy.array([bufcord]), verbose = 0)
        answer = [numpy.argmax(numpy.squeeze(prediction_result)), max(prediction_result[0])]
        if (answer[1] > 0.5):
            return answer[0]
        return 35
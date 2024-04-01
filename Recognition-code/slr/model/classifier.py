import numpy #: импорт библотеки NumPy для работы с массивами и матрицами, а также множеством мат функций
import tensorflow

class KeyPointClassifier(object):
    def __init__( #: Конструктор с аргументами и значениями по умолчанию
        self, #: ключевое слово self используется в методах класса для обращения к атрибутам и методам этого же объекта класса.
        model_path="slr/model/mymodel_all-34.hdf5", #: указание пути к модели
        num_threads=1 #: аргумент для установки количества потоков
    ):
        self.model = tensorflow.keras.models.load_model(model_path) #: загрузка модели

    def __call__(self, landmark_list): #: метод, который используется для вызова экземпляра класса KeyPointClassifier как функции
        landmark_array = numpy.array([landmark_list], dtype=numpy.float32)

        result = self.model.predict(landmark_array) #: получить прогнозы модели для входных данных.
        result_index = numpy.argmax(result) #: находит индекс класса с наибольшей вероятностью. Мы используем numpy.argmax, чтобы
        #: получить индекс элемента с наибольшим значением в массиве вероятностей result.
        if numpy.max(result) > 0.5:
            return result_index
        return 35
from collections import deque
import cv2 as cv

class CvFPSCalc(object):
    def __init__(self, buffer_len=1):
        self._start_tick = cv.getTickCount() #: начальное время в тиках. Время в OpenCV измеряется в тактах (ticks).
        self._freq = 1000.0 / cv.getTickFrequency() #: частота кадров, вычисленная как 1000.0 (миллисекунд в секунде) деленное на частоту тиков в секунду.
        self._diffetimes = deque(maxlen=buffer_len) #: _diffetimes: очередь для хранения времени между кадрами. Максимальный размер очереди задается параметром buffer_len.

    def get(self): #: получение текущего FPS
        current_tick = cv.getTickCount() #: текущее время в тиках
        different_time = (current_tick - self._start_tick) #: изменённое время как разница между текущими тиками и начальными
        self._start_tick = current_tick #: значение стартового тика видимо итеративно приравнивается к предыдущему текущему тику

        self._diffetimes.append(different_time) # разница между тиками добавляется в дек

        fps = 1000.0 / (sum(self._diffetimes) / len(self._diffetimes)) #: Вычисляется среднее значение времени между кадрами из очереди _diffetimes.
        fps_rounded = round(fps, 2) #: Вычисляется FPS как обратное значение времени между кадрами (в миллисекундах), округленное до двух знаков после запятой.

        return fps_rounded
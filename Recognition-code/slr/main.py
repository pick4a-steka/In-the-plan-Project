import copy #: модуль, который предоставляет функции для создания глубоких копий объектов
import csv #: модуль, который позволяет работать с файлами CSV в Python (чтение и запись данных из и в файлы CSV)
import os #: модуль, который предоставляет функции для взаимодействия с ОС (создание, удаление и перемещение файлов и директорий)
import datetime #: модуль, который предоставляет классы для работы с датами и временем

import cv2 as cv
import mediapipe #: модуль, который предоставляет набор инструментов для анализа иэображений и видео, таких как обнаружение объектов, распознавание жестов
from dotenv import load_dotenv #: импорт функции load_dotenv из модуля dotenv, который обычно используется для загрузки переменных сред окружения
#: Это позволяет скрыть конфиденциальные данные, такие как пароли или ключи API, открытыми из исходного кода.

from slr.model.classifier import KeyPointClassifier #: тмпортируем класс из файла classifier.py. Класс содержит методы для работы с моделью

from slr.utils.cvfpscalc import CvFPSCalc
from slr.utils.landmarks import draw_landmarks

from slr.utils.draw_debug import draw_bounding_rect
from slr.utils.draw_debug import draw_hand_label

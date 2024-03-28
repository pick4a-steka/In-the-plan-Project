import copy
import itertools
from typing import List

import numpy
import cv2 as cv

def calc_bounding_rect(image, landmarks) -> List:
    image_width = image.shape[1] #: Ширина изображения
    image_height = image.shape[0] #: Высота изображения

    landmark_array = numpy.empty((0, 2), int) #: устой массив numpy размерности (0, 2) с типом данных int

    for _, landmark in enumerate(landmarks.landmark):
        '''
        функция проходит по всем ключевым точкам руки и вычисляет их координаты в пикселях относительно
        размеров изображения. Эти координаты добавляются в массив landmark_array
        '''
        #: Здесь landmark.x и landmark.y представляют относительные координаты ключевых
        landmark_x = min(int(landmark.x * image_width), image_width -1) #:  точек руки в диапазоне от 0 до 1, где (0, 0) - это верхний левый угол изображения, а (1, 1) - это нижний правый угол.
        landmark_y = min(int(landmark.y * image_height), image_height -1) #: Функция int() используется для округления рассчитанных координат до целых чисел
        #:  Функция min() используется для того, чтобы убедиться, что координаты находятся в пределах размеров изображения.

        landmark_point = [numpy.array((landmark_x, landmark_y))] #: Создается новый массив с двумя элементами, представляющими координаты одной ключевой точки руки.
        #: Затем этот массив добавляется в основной массив landmark_array. Параметр axis=0 указывает, что добавление происходит по вертикальной оси, т.е. 
        landmark_array = numpy.append(landmark_array, landmark_point, axis=0) #: каждая новая точка руки добавляется в новую строку основного массива.

    x, y, w, h = cv.boundingRect(landmark_array) #: вычисление ограничивающего прямоугольника, в который "вписана" рука.

    return [x, y, x+w, y+h]

def calc_landmark_list(image, landmarks) -> List:
    image_width = image.shape[1]
    image_height = image.shape[0]

    landmark_point = [] #: список, который будет содержать координаты ключевых точек руки

    for _, landmark in enumerate(landmarks.landmark): #: итерируем по всем ключевым точкам руки
        landmark_x = min(int(landmark.x * image_width), image_width -1) #: Для каждой ключевой точки рассчитывает абсолютные координаты
        landmark_y = min(int(landmark.y * image_height), image_height -1) #: учитывая относительные координаты и размеры изображения.

        landmark_point.append([landmark_x, landmark_y]) #: Добавляем координаты каждой ключевой точки в список 
    
    return landmark_point

def pre_process_landmark(landmakr_list) -> List:
    #: Создается глубокая копия входного списка  чтобы избежать изменения оригинального списка.
    temp_landmark_list = copy.deepcopy(landmark_list)

    base_x, base_y = 0, 0 #: Переменные для хранения координат базовой точки (точка запястья)
    #: в цикле перебираются индексы и координаты точек во временном списке
    for index, landmark_point in enumerate(temp_landmark_list):
        if index == 0: #: Если это первая точка (индекс 0), то ее координаты устанавливаются как базовые.
            base_x, base_y = landmark_point[0], landmark_point[1]

        #: Для каждой точки в списке вычитаются базовые координаты из ее собственных координат
        temp_landmark_list[index][0] = temp_landmark_list[index][0] - base_x
        temp_landmark_list[index][1] = temp_landmark_list[index][1] - base_y

        #: Список преобразуется в одномерный список с помощью itertools.chain.from_iterable()
        temp_landmark_list = list(itertools.chain.from_iterable(temp_landmark_list))

        #: Вычисляется максимальное абсолютное значение в списке
        max_value = max(list(map(abs,temp_landmark_list)))

        #: Затем каждая координата делится на это максимальное значение, чтобы нормализовать их в диапазоне от -1 до 1.
        def normalize_(n):
            return n / max_value
        
        temp_landmark_list = list(map(normalize_, temp_landmark_list))

        #: Нормализованный одномерный список координат возвращается в качестве результата функции.
        return temp_landmark_list
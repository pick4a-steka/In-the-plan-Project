import copy #: модуль, который предоставляет функции для создания глубоких копий объектов
import csv #: модуль, который позволяет работать с файлами CSV в Python (чтение и запись данных из и в файлы CSV)
import os #: модуль, который предоставляет функции для взаимодействия с ОС (создание, удаление и перемещение файлов и директорий)
import datetime #: модуль, который предоставляет классы для работы с датами и временем
import numpy

import cv2 as cv
import mediapipe #: модуль, который предоставляет набор инструментов для анализа иэображений и видео, таких как обнаружение объектов, распознавание жестов
from dotenv import load_dotenv #: импорт функции load_dotenv из модуля dotenv, который обычно используется для загрузки переменных сред окружения
#: Это позволяет скрыть конфиденциальные данные, такие как пароли или ключи API, открытыми из исходного кода.

from slr.model.classifier import KeyPointClassifier #: импортируем класс из файла classifier.py. Класс содержит методы для работы с моделью

from slr.utils.cvfpscalc import CvFPSCalc
from slr.utils.landmarks import draw_landmarks

from slr.utils.draw_debug import draw_bounding_rect
from slr.utils.draw_debug import draw_hand_label

from slr.utils.pre_process import calc_bounding_rect
from slr.utils.pre_process import calc_landmark_list
#from slr.utils.pre_process import pre_process_landmark

def main():
    CAP_DEVICE = 0
    CAP_WIDTH = 640
    CAP_HEIGHT = 480

    USE_STATIC_IMAGE_MODE = True
    MAX_NUM_HANDS = 1
    MIN_DETECTION_CONFIDENCE = 0.7
    MIN_TRACKING_CONFIDENCE = 0.5

    print("ИНФ: Система инициализированна успешно!")
    print("ИНФ: Подключение камеры")

    #: Захват видеопотока
    cap = cv.VideoCapture(CAP_DEVICE)
    cap.set(cv.CAP_PROP_FRAME_WIDTH, CAP_WIDTH)
    cap.set(cv.CAP_PROP_FRAME_HEIGHT, CAP_HEIGHT)

    black_image = numpy.zeros((100, 100, 3), dtype=numpy.uint8)
    font = cv.FONT_HERSHEY_COMPLEX

    #: Загрузка инструментов для обнаружения руки
    mp_hands = mediapipe.solutions.hands
    hands = mp_hands.Hands(
        static_image_mode=USE_STATIC_IMAGE_MODE,
        max_num_hands=MAX_NUM_HANDS,
        min_detection_confidence=MIN_DETECTION_CONFIDENCE,
        min_tracking_confidence=MIN_TRACKING_CONFIDENCE
    )

    #: Загрузка модели
    keypoint_classifier = KeyPointClassifier()

    nums = list(range (0, 34))
    letters = []

    for letter in ''.join([chr(i) for i in range (ord('а'), ord('а') + 32)]):
        letters.append(letter)
    letters.append('~')
    letters.append('-')

    keypoint_classifier_label = dict(zip(nums, letters))

    cv_fps = CvFPSCalc(buffer_len=10)
    print("ИНФ: Система начинает работу")

    while True:
        fps = cv_fps.get()

        key = cv.waitKey(1)
        if key == 27: #: ESC
            print("ИНФ: Завершение работы")
            break

        #: Чтение кадра
        success, image = cap.read()
        if not success:
            continue

        image = cv.resize(image, (CAP_WIDTH, CAP_HEIGHT))

        #: отзеркаливаем изображение
        image = cv.flip(image, 1)
        debug_image = copy.deepcopy(image)

        #: RGB -> BGR
        image = cv.cvtColor(image, cv.COLOR_BGR2RGB)

        image.flags.writeable = False
        results = hands.process(image)
        image.flags.writeable = True

        #: Начало распознавания
        if results.multi_hand_landmarks is not None:
            for hand_landmarks, handedness in zip(results.multi_hand_landmarks, results.multi_handedness):
                #: Рассчет огрничивающего квадрата для руки
                use_brect = True
                brect = calc_bounding_rect(debug_image, hand_landmarks)

                #: Расчет ориентира
                landmark_list = calc_landmark_list(debug_image, hand_landmarks)

                #: pre_processed_landmark_list = pre_process_landmark(landmark_list)

                hand_sign_id = keypoint_classifier(landmark_list)

                if hand_sign_id == 35:
                    hand_sign_text = ""
                else:
                    hand_sign_text = keypoint_classifier_label[hand_sign_id]
                    print(hand_sign_text)

                text_size = cv.getTextSize(hand_sign_text, font, 1, 2)[0]
                text_x = (100 - text_size[0]) // 2
                text_y = (100 - text_size[1]) // 2
                black_image = numpy.zeros((100, 100, 3), dtype=numpy.uint8)
                cv.putText(black_image, hand_sign_text, (text_x, text_y), font, 1, (255, 255, 255), 2)

                cv.imshow("Symbol", black_image)
                
                debug_image = draw_bounding_rect(debug_image, use_brect, brect)
                debug_image = draw_landmarks(debug_image, landmark_list)
                debug_image = draw_hand_label(debug_image, brect, handedness)
        cv.imshow("Sign Language Recognition", debug_image)

    cap.release()
    cv.destroyAllWindows()
    print("ИНФ: Программа завершила работу")

if __name__ == "__main__":
    main()

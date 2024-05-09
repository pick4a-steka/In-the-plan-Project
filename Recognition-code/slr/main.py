import copy #: модуль, который предоставляет функции для создания глубоких копий объектов

import cv2 as cv
import mediapipe #: модуль, который предоставляет набор инструментов для анализа иэображений и видео, таких как обнаружение объектов, распознавание жестов
from dotenv import load_dotenv #: импорт функции load_dotenv из модуля dotenv, который обычно используется для загрузки переменных сред окружения
#: Это позволяет скрыть конфиденциальные данные, такие как пароли или ключи API, открытыми из исходного кода.

from slr.utils.pre_process import calc_landmark_list

def main(keypoint_classifier, keypoint_classifier_label, hands, image):
    # CAP_WIDTH = 640
    image = cv.rotate(image, cv.ROTATE_90_CLOCKWISE) # Поворачиваем изображение на 90 градусов против часовой стрелки
    # height, width = image.shape[:2]
    # aspect_ratio = width / height

    # CAP_HEIGHT = int(CAP_WIDTH / aspect_ratio)
    # hand_sign_text = "?"

    # #: Чтение кадра
    # image = cv.resize(image, (CAP_WIDTH, CAP_HEIGHT))

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

            #: Расчет ориентира
            landmark_list = calc_landmark_list(debug_image, hand_landmarks)

            hand_sign_id = keypoint_classifier(landmark_list)

            if hand_sign_id == 35:
                hand_sign_text = "-"
            else:
                hand_sign_text = keypoint_classifier_label[hand_sign_id]

    return hand_sign_text

if __name__ == "__main__":
    main()

from slr.model.classifier import KeyPointClassifier #: импортируем класс из файла classifier.py. Класс содержит методы для работы с моделью
import mediapipe

def load_model():
    # Определение констант для обнаружение руки
    USE_STATIC_IMAGE_MODE = True
    MAX_NUM_HANDS = 1
    MIN_DETECTION_CONFIDENCE = 0.7
    MIN_TRACKING_CONFIDENCE = 0.5
    
    #: Загрузка модели
    keypoint_classifier = KeyPointClassifier()

    nums = list(range (0, 34))
    letters = []

    for letter in ''.join([chr(i) for i in range (ord('а'), ord('а') + 32)]):
        letters.append(letter)
    letters.append('~')
    letters.append('-')

    keypoint_classifier_label = dict(zip(nums, letters))

    #: Загрузка инструментов для обнаружения руки
    mp_hands = mediapipe.solutions.hands
    hands = mp_hands.Hands(
        static_image_mode=USE_STATIC_IMAGE_MODE,
        max_num_hands=MAX_NUM_HANDS,
        min_detection_confidence=MIN_DETECTION_CONFIDENCE,
        min_tracking_confidence=MIN_TRACKING_CONFIDENCE
    )

    return keypoint_classifier, keypoint_classifier_label, hands
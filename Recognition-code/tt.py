import tensorflow as tf

gpus = tf.config.list_physical_devices('GPU')
if gpus:
    # Если есть доступные GPU, выведем информацию о них
    for gpu in gpus:
        print("Устройство GPU:", gpu)
else:
    print("Нет доступных устройств GPU.")

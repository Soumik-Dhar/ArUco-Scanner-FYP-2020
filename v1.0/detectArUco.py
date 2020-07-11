# importing required modules
from __future__ import print_function
import cv2
from ar_markers import detect_markers

# storing server address
address = "http://192.168.2.2:8080/video"
# storing frame height and width
height = 720
width = 1280

if __name__ == '__main__':
    print('Press "q" to quit')
    capture = cv2.VideoCapture(0)
    # opening video feed from IP Webcam
    capture.open(address)
    # setting frame height and width
    capture.set(cv2.CAP_PROP_FRAME_HEIGHT, height)
    capture.set(cv2.CAP_PROP_FRAME_WIDTH, width)
    cv2.startWindowThread()
    # capturing first frame
    if capture.isOpened():
        frame_captured, frame = capture.read()
    else:
        frame_captured = False
    # detecting all markers in captured frame
    while frame_captured:
        markers = detect_markers(frame)
        # drawing bounding boxes and marker ID
        for marker in markers:
            marker.highlite_marker(frame)
        # showing updated frame
        cv2.imshow('ArUco Scanner', frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        # capturing next frame
        frame_captured, frame = capture.read()
    # releasign frames and closing window
    capture.release()
    cv2.destroyAllWindows()

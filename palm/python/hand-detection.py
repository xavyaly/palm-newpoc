import cv2
import mediapipe
import time
import matplotlib.pyplot as plt
import numpy as np
import math
import os
# Suppress logging warnings
os.environ["GRPC_VERBOSITY"] = "ERROR"
os.environ["GLOG_minloglevel"] = "2"

class ImageProcess:
    def __init__(self):
        #self.sift=cv2.xfeatures2d.SIFT_create()
        self.sift=cv2.SIFT_create()
    def xout(self,bgr_image):
        image = cv2.GaussianBlur(bgr_image, (5, 5), 0)
        return image
    def kps(self,bgr_image):
        kps, dscs = self.sift.detectAndCompute(bgr_image, None)
        return kps,dscs;

class HandDetection:
    def __init__(self, markLandmarks=list(range(21)), drawPalm=True, mark=True):
        self.imgProcess=ImageProcess()
        self.mark = mark
        self.delayFrame = 1
        self.prevTime = 0
        self.currTime = 0
        self.drawPalm = drawPalm
        #self.stream = cv2.VideoCapture(0)
        self.markLandmarks = markLandmarks
        self.mediapipeHands = mediapipe.solutions.hands
        #self.hands = self.mediapipeHands.Hands(max_num_hands=1)
        self.hands=self.mediapipeHands.Hands(static_image_mode=True,model_complexity=1,min_detection_confidence=0.75, min_tracking_confidence=0.75,max_num_hands=1)
        self.mediapipeDraw = mediapipe.solutions.drawing_utils

    def markPoints(self, frame,findex):
        markList = []
        palm=None
        x5,y5=0,0
        x17,y17=0,0
        x2,y2=0,0
        x0,y0=0,0
        for index, landmrk in enumerate(self.handLandmarks.landmark):
            if index in self.markLandmarks:
                height, width, c = frame.shape
                pixelX, pixelY = int(landmrk.x * width), int(landmrk.y * height)
                if self.mark:
                    #cv2.circle(frame, (pixelX, pixelY), 10, (100, 255, 0), cv2.FILLED)
                    #cv2.putText(frame, str(int(index)), (pixelX, pixelY),cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 2)
                    if(index==5):
                        x5,y5=pixelX, pixelY
                    elif(index==17):
                        x17,y17=pixelX, pixelY
                    elif(index==2):
                        x2,y2=pixelX, pixelY
                    elif(index==0):
                        x0,y0=pixelX, pixelY
                      
                markList.append([index, pixelX, pixelY])
        #5 17 
        #2 0
        ROI_FLAG=0
        roi_w=int(math.sqrt( (x17 - x5)**2 + (y17 - y5)**2 ))
        (h, w) = frame.shape[:2]
        cx=int((x17+x5) / 2)
        cy=int((y17+y5) / 2)
        center = (cx, cy)
        if(x5<=x17 and y5<=y17):
            ROI_FLAG=1  #LEFT HAND ANTI CLOCK WISE
            theta = np.arctan2((y17 - y5), (x17 - x5))*180/np.pi
        elif(x5<x17 and y5>y17):
             ROI_FLAG=-1  #LEFT HAND CLOCK WISE
             theta = np.arctan2((y17 - y5), (x17 - x5))*180/np.pi
        elif(x17<=x5 and y17<=y5):
            ROI_FLAG=2  #RIGHT HAND ANTI CLOCK WISE
            theta = np.arctan2((y5 - y17), (x5 - x17))*180/np.pi
        elif(x17<x5 and y17>y5):
            ROI_FLAG=-2  #RIGHT HAND  CLOCK WISE
            theta = np.arctan2((y5 - y17), (x5 - x17))*180/np.pi
        if(ROI_FLAG!=0):     
            M = cv2.getRotationMatrix2D(center=center, angle=theta, scale=1.0)
            rotated = cv2.warpAffine(frame, M, (w, h))
            x5=int(center[0]-roi_w/2)
            y5=center[1]
            x17=int(center[0]+roi_w/2)
            y17=center[1]
            roi=rotated[y5:y5+roi_w, x5:x17, :].copy()
            roi_resized = self.imgProcess.xout(cv2.resize(roi, (180, 180)));
            #cv2.imwrite("./resources/palm/"+str(findex)+".jpg",roi_resized)
            cv2.imwrite("./100001_RGB_PALM.jpg",roi_resized)
            print("HAND DETECTED")
            
        return markList,palm

    def detectHand(self, frame,j):
        rgbImg = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        processedImageResults = self.hands.process(rgbImg)
        if processedImageResults.multi_hand_landmarks:
            for self.handLandmarks in processedImageResults.multi_hand_landmarks:
                # print(processedImageResults.multi_hand_landmarks)
                self.markPoints(frame,j)
                '''if self.drawPalm:
                    self.mediapipeDraw.draw_landmarks(frame, self.handLandmarks,self.mediapipeHands.HAND_CONNECTIONS)
                '''        
        return 0

    def main(self):
        #_dir="C:/Users/HP/Downloads/PALM-HAND/Hands/Hands"
        _dir="./"
        mimage=_dir+"/100001_RGB.jpg"
        self.frame=cv2.imread(mimage)
        #self.frame=cv2.flip(self.frame,0) # revers row
        self.detectHand(self.frame,0)
            
        


if __name__ == '__main__':
    '''
        See the image PATH: handDetection/res/hand_landmarks.png to get an idea to 
        display that particular section of hand marking and print its locations.
        list:"markLandmarks" contains any value in range(21).
        Ex: markLandmarks = [4,8,12,16,20] 
    '''
    webCamStream = HandDetection(markLandmarks=list(range(21)), drawPalm=True, mark=True)
    webCamStream.main()
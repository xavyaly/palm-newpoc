import cv2
import numpy as np
import os
#from skimage import metrics
import threading
PALM_FOUND=False
def create_gaborfilter():
    # This function is designed to produce a set of GaborFilters 
    # an even distribution of theta values equally distributed amongst pi rad / 180 degree
     
    filters = []
    num_filters = 16
    ksize = 31  # 35The local area to evaluate
    sigma = 4.5  # 3.0Larger Values produce more edges
    lambd = 10.0
    gamma = 0.5
    psi = 0  # Offset value - lower generates cleaner results
    for theta in np.arange(0, np.pi, np.pi / num_filters):  # Theta is the orientation for edge detection
        kern = cv2.getGaborKernel((ksize, ksize), sigma, theta, lambd, gamma, psi, ktype=cv2.CV_64F)
        kern /= 1.0 * kern.sum()  # Brightness normalization
        filters.append(kern)
    return filters
def apply_filter(img, filters):
# This general function is designed to apply filters to our image
     
    # First create a numpy array the same size as our input image
    newimage = np.zeros_like(img)
     
    # Starting with a blank image, we loop through the images and apply our Gabor Filter
    # On each iteration, we take the highest value (super impose), until we have the max value across all filters
    # The final image is returned
    depth = -1 # remain depth same as original image
     
    for kern in filters:  # Loop through the kernels in our GaborFilter
        image_filter = cv2.filter2D(img, depth, kern)  #Apply filter to image
         
        # Using Numpy.maximum to compare our filter and cumulative image, taking the higher value (max)
        np.maximum(newimage, image_filter, newimage)
    return newimage
def processedImage(imageM1):
    image1 =cv2.imread(imageM1) 
    image1 =cv2.cvtColor(image1, cv2.COLOR_BGR2GRAY) 
    image1=cv2.GaussianBlur(image1, (5, 5), 0)
    low_threshold = 50
    high_threshold = 120
    aperture_size=5 #default 3
    gfilters = create_gaborfilter()
    image1 = apply_filter(image1, gfilters)
    #image1 = cv2.Canny(image1, low_threshold, high_threshold,apertureSize=aperture_size)
    #image2 = cv2.Canny(image2, low_threshold, high_threshold,apertureSize=aperture_size)
    return(image1)

def matching(imageM1,imageM2):
    #image1 =cv2.imread(imageM1) 
    #image2 =cv2.imread(imageM2)
    image1=processedImage(imageM1)
    image2=processedImage(imageM2)
    #sift = cv2.xfeatures2d.SIFT_create()
    sift = cv2.SIFT_create()
    keypoints_1, descriptors_1 = sift.detectAndCompute(image1, None)
    keypoints_2, descriptors_2 = sift.detectAndCompute(image2, None)
    KL1=len(keypoints_1)
    KL2=len(keypoints_2)
    if(KL1<5 or KL2<5):
        return(0)
    ffn=cv2.FlannBasedMatcher(dict( algorithm=1, trees=20),dict())
    matches = ffn.knnMatch(descriptors_1, descriptors_2, k=2)
    match_points = []
    
    
    good_matches = [[0,0] for i in range(len(matches))]
    i=0
    for p, q in matches:
        if p.distance < 0.50*q.distance:
            match_points.append(p)
            good_matches[i]=[1,0]
        i=i+1
    '''
    if len(match_points)>=3:
        print(KL1,KL2,len(match_points),imageM1,imageM2)
        Matched = cv2.drawMatchesKnn(image1,	 
                                keypoints_1, 
                                image2, 
                                keypoints_2, 
                                matches, 
                                outImg = None, 
                                matchColor = (0,0,255), 
                                singlePointColor = (0,255,255), 
                                matchesMask = good_matches, 
                                flags = 0
                                )

        # Save the image 
        cv2.imshow('result', Matched)
        cv2.waitKey(0) '''    
    return(len(match_points))

def start(_dir,_iname,i,L):
    global PALM_FOUND
    inputImage=iname;
    maxKM=0
    matchedFN=""
    maxThresold=5
    for j in range(i,L):
        mimage=_dir+"/"+files[j]
        ret=matching(inputImage, mimage);
        if(ret>=maxThresold and maxKM<ret):
            maxKM=ret
            matchedFN=files[j]

    if(maxKM>0):
        print("PALM MATCHED::",matchedFN,maxKM)
    else:
        print("NOT MACTHING")    

_dir="./rgb" 
files = os.listdir(_dir)
L=len(files);
iname="./100001_RGB_PALM.jpg";

'''t1 = threading.Thread(target=start, args=(_dir,iname,0,100))
t2 = threading.Thread(target=start, args=(_dir,iname,10,200))
t1.start()
t2.start()'''
x=L
d=100
y=x//d
r=x-y*d
print("PALM MATCHING STARTS")
for j in range(0,y,1):
    print(" THREAD IS READY-j ");
    th=threading.Thread(target=start, args=(_dir,iname,j*d,j*d+d))
    th.start()
if r>0:
    print(" THREAD IS READY-0 ");
    th=threading.Thread(target=start, args=(_dir,iname,y*d,y*d+r))
    th.start()



        
        





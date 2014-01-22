#include "stdafx.h"

#include "opencv/cv.hpp"  
#include "opencv2/highgui/highgui.hpp"  

#include <stdio.h>  
  
using namespace std;  
using namespace cv; 
int HistogramBins = 256;  
float HistogramRange1[2]={0,255};  
float *HistogramRange[1]={&HistogramRange1[0]}; 
CVAPI(double) CalcHistaccuracy(IplImage* sample_image, IplImage* test_image)  
{  
    IplImage* srcImage;  
    IplImage* targetImage;  
    if (sample_image->nChannels != 1) {  
        srcImage = cvCreateImage(cvSize(sample_image->width, sample_image->height), sample_image->depth, 1);  
        cvCvtColor(sample_image, srcImage, CV_BGR2GRAY);  
    } else {  
        srcImage = sample_image;  
    }  
  
    if (test_image->nChannels != 1) {  
        targetImage = cvCreateImage(cvSize(test_image->width, test_image->height), srcImage->depth, 1);  
        cvCvtColor(test_image, targetImage, CV_BGR2GRAY);  
    } else {  
        targetImage = test_image;  
    }  
  
    CvHistogram *Histogram1 = cvCreateHist(1, &HistogramBins, CV_HIST_ARRAY,HistogramRange);  
    CvHistogram *Histogram2 = cvCreateHist(1, &HistogramBins, CV_HIST_ARRAY,HistogramRange);  
  
    cvCalcHist(&srcImage, Histogram1);  
    cvCalcHist(&targetImage, Histogram2);  
  
    cvNormalizeHist(Histogram1, 1);  
    cvNormalizeHist(Histogram2, 1);  
  
    
    double accuracy_CHIISQR = cvCompareHist(Histogram1, Histogram2, CV_COMP_CHISQR);
	double accuracy_BHATTACHARYYA = cvCompareHist(Histogram1, Histogram2, CV_COMP_BHATTACHARYYA);
  
	double accuracy_CORREL = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);
	double accuracy_INTERSECT = cvCompareHist(Histogram1, Histogram2, CV_COMP_INTERSECT);
  
    cvReleaseHist(&Histogram1);  
    cvReleaseHist(&Histogram2);  
    if (sample_image->nChannels != 1) {  
        cvReleaseImage(&srcImage);  
    }  
    if (test_image->nChannels != 1) {  
        cvReleaseImage(&targetImage);  
    }  
	double calc_accuracy = ((1-accuracy_CHIISQR)+(1-accuracy_BHATTACHARYYA)+accuracy_CORREL+accuracy_INTERSECT)/4;
	return calc_accuracy;  
}  
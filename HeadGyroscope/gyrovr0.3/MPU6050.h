#include <Wire.h>

class MPU6050
{
 public:
  float gx, gy, gz;
  float gXoffset = 0, gYoffset = 0, gZoffset = 0; 
  float gXVariance = 0.1, gYVariance = 0.1, gZVariance = 0.1;

  float gxAngle = 0, gyAngle = 0, gzAngle = 0;
  long timer;
  float tim;
 
  MPU6050();
  
  void initialize();
  void initializeMPU();
  void readGyroscope();
  void calculateGyroscopeMesuremants();
  void gyroscopeCalibration();
  void calculateGyroscopeOffset();
  bool shakeDetection();
  void varianceCalibration();
  void setVariance(float x, float y, float z);
  void calculatePosition();

  
};

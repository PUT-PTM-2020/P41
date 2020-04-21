#include "MPU6050.h"
#include "Arduino.h"

MPU6050::MPU6050(){}

void MPU6050::initialize()
{
  initializeMPU();
  gyroscopeCalibration();
  delay(1000);
}

void MPU6050::initializeMPU()
{
  Wire.beginTransmission(0b1101000);
  Wire.write(0x1B); //gyroscope register settings
  Wire.write(0x00000000); // 250*
  Wire.endTransmission(); 
  
  Wire.beginTransmission(0b1101000);
  Wire.write(0x1C); //accelometer register settings
  Wire.write(0b00000000); // 2g
  Wire.endTransmission(); 
  
  Wire.beginTransmission(0b1101000); 
  Wire.write(0x6B);
  Wire.write(0b00000000); //device mode
  Wire.endTransmission();  
}

void MPU6050::readGyroscope() 
{
  Wire.beginTransmission(0b1101000); 
  Wire.write(0x43); //Gyroscoper read register
  Wire.endTransmission();
  
  Wire.requestFrom(0b1101000,6); 
  while(Wire.available() < 6);
  gx = (Wire.read()<<8)|(Wire.read());
  gy = (Wire.read()<<8)|(Wire.read());
  gz = (Wire.read()<<8)|(Wire.read());
  
  calculateGyroscopeMesuremants();
}

void MPU6050::calculateGyroscopeMesuremants()
{
  gx = gx/131.0;
  gy = gy/131.0;
  gz = gz/131.0;
}

void MPU6050::gyroscopeCalibration()
{
  //Serial.print("GYROSCOPE CALIBRATION...\n");
  for(int i = 0; i < 10000; i++)
  {
    readGyroscope();
    gXoffset += gx;
    gYoffset += gy;
    gZoffset += gz;
  }
  gXoffset = gXoffset/10000.0;
  gYoffset = gYoffset/10000.0;
  gZoffset = gZoffset/10000.0;
  
  //Serial.print("READY\n");
}

void MPU6050::calculateGyroscopeOffset()
{
  gx -= gXoffset;
  gy -= gYoffset;
  gz -= gZoffset;
}

bool MPU6050::shakeDetection()
{
  if(abs(gx) > gXVariance || abs(gy) > gYVariance || abs(gz) > gZVariance)
  {
    return 1;
  }
  else
  {
    return 0;
  }
}

void MPU6050::varianceCalibration()
{
  //Serial.print("GYROSCOPE VARIANCE CALIBRATION...\n");
  for(int i = 0; i < 20000; i++)
  {
    this->readGyroscope();
    this->calculateGyroscopeOffset();
    
    if(abs(gx) > gXVariance)
    {
      gXVariance += 0.001;
    }
    if(abs(gy) > gYVariance)
    {
      gYVariance += 0.001;
    }
    if(abs(gz) > gZVariance)
    {
      gZVariance += 0.001;
    }
    /*Serial.print(this->gx);Serial.print(" ");
    Serial.print(this->gy);Serial.print(" ");
    Serial.print(this->gz);Serial.print("\n");
    Serial.print(gXVariance);Serial.print(" ");
    Serial.print(gYVariance);Serial.print(" ");
    Serial.print(gZVariance);Serial.print("\n");
    delay(1000);*/
  }
  gXVariance += 0.01;
  gYVariance += 0.01;
  gZVariance += 0.01;
  /*Serial.println(gXVariance);
  Serial.println(gYVariance);
  Serial.println(gZVariance);
  Serial.println("READY\n");*/
  
}

void MPU6050::setVariance(float vx, float vy, float vz)
{
  gXVariance = vx;
  gYVariance = vy;
  gZVariance = vz;
}

void MPU6050::calculatePosition()
{
  tim = ((millis()-timer)*0.001);
  timer = millis();
  gxAngle += gx * tim;
  gyAngle += gy * tim;
  gzAngle += gz * tim;
  
}

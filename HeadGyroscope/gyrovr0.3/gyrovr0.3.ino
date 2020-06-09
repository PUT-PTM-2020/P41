#include "MPU6050.h"

int counter = 0;
uint16_t mz = 0, my = 0;

uint8_t data[4];

MPU6050 mpu = MPU6050();

void setup() 
{
  Serial.begin(9600);
  mpu.initialize();
  mpu.setVariance(0.5,0.5,0.5);
}


void loop() 
{
  counter++;
  mpu.readGyroscope();
  mpu.calculateGyroscopeOffset();
  
  
  if(mpu.shakeDetection())
  {
    mpu.calculatePosition();
  }
  if(counter > 100)
  {
    counter = 0;
    /*Serial.print(mpu.gyAngle);Serial.print(" ");
    Serial.print(mpu.gzAngle);Serial.print("\n");
    
    Serial.print(my);Serial.print(" ");
    Serial.print(mz);Serial.print("\n");*/
    toRawData();
    Serial.write(data, 4);
    
  }
  
}


void toRawData()
{
  if(mpu.gzAngle*-1 < 0)
  mpu.gzAngle -= 360;
  if(mpu.gzAngle*-1 > 360)
  mpu.gzAngle += 360;
  
  mz = ((mpu.gzAngle*-1) / 360.0) * 0b11111111111111;
  my = ((mpu.gyAngle+90) / 180.0) * 0b1111111111111;
  
  if(mpu.gyAngle+90 < 0)
  my = 0;
  if(mpu.gyAngle+90 > 180)
  my = 0b1111111111111;

  data[0] = 16 + ((mz & 0b0011100000000000) >> 11);
  data[1] = ((mz & 0b0000011111111000) >> 3);
  data[2] = ((mz & 0b0000000000000111) << 5);
  data[2] = data[2] + ((my & 0b0001111100000000) >> 8);
  data[3] = my & 0b0000000011111111;
  
}

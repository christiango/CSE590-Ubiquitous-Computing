/*
 * Adapted from https://learn.adafruit.com/adafruit-arduino-lesson-3-rgb-leds?view=all
 * and https://github.com/jonfroehlich/CSE590Sp2018/blob/master/L05-Arduino/RedBearDuoRGB/RedBearDuoRGB.ino
 */
SYSTEM_MODE(MANUAL); 

const int RGB_RED_PIN = D0;
const int RGB_GREEN_PIN  = D1;
const int RGB_BLUE_PIN  = D2;

const int RED_INPUT_PIN = D8;
const int GREEN_INPUT_PIN = D9;
const int BLUE_INPUT_PIN = D10;
const int DELAY = 200; // delay between changing colors

void setup() {
  pinMode(RGB_RED_PIN, OUTPUT);
  pinMode(RGB_GREEN_PIN, OUTPUT);
  pinMode(RGB_BLUE_PIN, OUTPUT);

  // Turn on Serial so we can verify expected colors via Serial Monitor
  Serial.begin(9600); 
}

void loop() {
  int redValue = getColorFromInput(RED_INPUT_PIN);
  int greenValue = getColorFromInput(GREEN_INPUT_PIN);
  int blueValue = getColorFromInput(BLUE_INPUT_PIN);
  setColor(redValue, greenValue, blueValue);
  delay(DELAY);
}

int getColorFromInput(int pinNumber)
{
  return 255 - map(analogRead(pinNumber), 0, 4096, 0, 255);
}

void setColor(int red, int green, int blue)
{
  red = 255 - red;
  green = 255 - green;
  blue = 255 - blue;
  analogWrite(RGB_RED_PIN, red);
  analogWrite(RGB_GREEN_PIN, green);
  analogWrite(RGB_BLUE_PIN, blue);  
}

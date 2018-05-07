/*
 * Adapted from https://learn.adafruit.com/adafruit-arduino-lesson-3-rgb-leds?view=all
 * and https://github.com/jonfroehlich/CSE590Sp2018/blob/master/L05-Arduino/RedBearDuoRGB/RedBearDuoRGB.ino
 *
 * Bluetooth code skeleten provided by Liang He, April 27th, 2018
 * 
 * The Library is created based on Bjorn's code for RedBear BLE communication: 
 * https://github.com/bjo3rn/idd-examples/tree/master/redbearduo/examples/ble_led
 * 
 * Our code is created based on the provided example code (Simple Controls) by the RedBear Team:
 * https://github.com/RedBearLab/Android
 * 
 * Starter code provided by Liang He for bluetooth communication
 * https://github.com/jonfroehlich/CSE590Sp2018/tree/master/A03-BLEBasic
 */

#include "ble_config.h"
SYSTEM_MODE(MANUAL); 

#define RECEIVE_MAX_LEN    4
#define SEND_MAX_LEN    3
#define BLE_SHORT_NAME_LEN 0x08 // must be in the range of [0x01, 0x09]
#define BLE_SHORT_NAME 'c','h','r','i','s','g','o' 

// UUID is used to find the device by other BLE-abled devices
static uint8_t service1_uuid[16]    = { 0x71,0x3d,0x00,0x00,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };
static uint8_t service1_tx_uuid[16] = { 0x71,0x3d,0x00,0x03,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };
static uint8_t service1_rx_uuid[16] = { 0x71,0x3d,0x00,0x02,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };

// Define the receive and send handlers
static uint16_t receive_handle = 0x0000; // recieve
static uint16_t send_handle = 0x0000; // send

static uint8_t receive_data[RECEIVE_MAX_LEN] = { 0x01 };
static uint8_t send_data[SEND_MAX_LEN] = { 0x00 };

// Define the configuration data
static uint8_t adv_data[] = {
  0x02,
  BLE_GAP_AD_TYPE_FLAGS,
  BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE, 
  
  BLE_SHORT_NAME_LEN,
  BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME,
  BLE_SHORT_NAME, 
  
  0x11,
  BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE,
  0x1e,0x94,0x8d,0xf1,0x48,0x31,0x94,0xba,0x75,0x4c,0x3e,0x50,0x00,0x00,0x3d,0x71 
};

const int RGB_RED_PIN = D0;
const int RGB_GREEN_PIN = D1;
const int RGB_BLUE_PIN = D2;

const int RED_INPUT_PIN = D8;
const int GREEN_INPUT_PIN = D9;
const int BLUE_INPUT_PIN = D10;
const int PHOTO_RESISTOR_PIN = D11;
const int DELAY = 200; // delay between changing colors

bool gotSmartPhoneValue = false;

/**
 * @brief Callback for writing event.
 *
 * @param[in]  value_handle  
 * @param[in]  *buffer       The buffer pointer of writting data.
 * @param[in]  size          The length of writting data.   
 *
 * @retval 
 */
int bleWriteCallback(uint16_t value_handle, uint8_t *buffer, uint16_t size) {
  Serial.print("Write value handler: ");
  Serial.println(value_handle, HEX);

  if (receive_handle == value_handle) {
    memcpy(receive_data, buffer, RECEIVE_MAX_LEN);
    Serial.print("Write value: ");
    for (uint8_t index = 0; index < RECEIVE_MAX_LEN; index++) {
      Serial.print(receive_data[index], HEX);
      Serial.print(" ");
    }
    Serial.println(" ");
    
    if (receive_data[0] == 0x01) { // Command is to control digital out pin
      setColor(receive_data[1], receive_data[2], receive_data[3]);
      Serial.println(receive_data[1]);
      Serial.println(receive_data[2]);
      Serial.println(receive_data[3]);

      gotSmartPhoneValue = true;
    }
  }
  return 0;
}


void setup() {
  Serial.begin(9600); 
  
  // Initialize ble_stack.
  ble.init();
  configureBLE(); //lots of standard initialization hidden in here - see ble_config.cpp
  // Set BLE advertising data
  ble.setAdvertisementData(sizeof(adv_data), adv_data);
  
  // Register BLE callback functions
  ble.onDataWriteCallback(bleWriteCallback);

  // Add user defined service and characteristics
  ble.addService(service1_uuid);
  receive_handle = ble.addCharacteristicDynamic(service1_tx_uuid, ATT_PROPERTY_NOTIFY|ATT_PROPERTY_WRITE|ATT_PROPERTY_WRITE_WITHOUT_RESPONSE, receive_data, RECEIVE_MAX_LEN);
  send_handle = ble.addCharacteristicDynamic(service1_rx_uuid, ATT_PROPERTY_NOTIFY, send_data, SEND_MAX_LEN);

  // BLE peripheral starts advertising now.
  ble.startAdvertising();
  Serial.println("BLE start advertising.");
  
  pinMode(RGB_RED_PIN, OUTPUT);
  pinMode(RGB_GREEN_PIN, OUTPUT);
  pinMode(RGB_BLUE_PIN, OUTPUT);

  pinMode(RED_INPUT_PIN, INPUT);
  pinMode(GREEN_INPUT_PIN, INPUT);
  pinMode(BLUE_INPUT_PIN, INPUT);
  pinMode(PHOTO_RESISTOR_PIN, INPUT);
}

void loop() {
  // Only listen to physical controls if we have not yet gotten a value from the smart phone
  if (!gotSmartPhoneValue) {
    double brightnessRatio = 1 - analogRead(PHOTO_RESISTOR_PIN)/4096.0;
    int redValue = getColorFromInput(RED_INPUT_PIN, brightnessRatio);
    int greenValue = getColorFromInput(GREEN_INPUT_PIN, brightnessRatio);
    int blueValue = getColorFromInput(BLUE_INPUT_PIN, brightnessRatio);
    setColor(redValue, greenValue, blueValue);  
  }
  
  delay(DELAY);
}

int getColorFromInput(int pinNumber, double brightnessRatio)
{
  return round((255 - map(analogRead(pinNumber), 0, 4096, 0, 255)) * brightnessRatio);
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

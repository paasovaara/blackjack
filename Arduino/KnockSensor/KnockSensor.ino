int analogPin1 = 0;
int digitalOutPin1 = 2;
int analogPin2 = 1;
int digitalOutPin2 = 6;

//TODO test, possibly increase sample freq to 200?
const int SAMPLE_RATE = 100; // Hz
const float DELAY_MS = 1000.0f / (float)SAMPLE_RATE;

const int THRESHOLD = 128; //TODO validate using debug plots
const bool debug = true; //set true to plot all values, false for production

//Object oriented way would be nicer.. TODO this in C++
bool highPrev1 = false;
bool highPrev2 = false;
//int val1 = 0;

void setup()
{
  Serial.begin(9600);              //  setup serial
  pinMode(digitalOutPin1, OUTPUT);
  digitalWrite(digitalOutPin1, LOW);
  
  pinMode(digitalOutPin2, OUTPUT);
  digitalWrite(digitalOutPin2, LOW);
}

void loop()
{
  // read the input pins
  int val1 = analogRead(analogPin1); 
  //val1 = analogRead(analogPin1); 
  // Some cool-down time for ADC might be needed
  // https://github.com/firmata/arduino/issues/334
  delay(DELAY_MS / 2); 
  int val2 = analogRead(analogPin2);
  //val2 = analogRead(analogPin2);
  
  if (debug) {
    Serial.print(0);  // To freeze the lower limit
    Serial.print(" ");
    Serial.print(THRESHOLD);
    Serial.print(" ");
    Serial.print(1024);  // To freeze the upper limit
    Serial.print(" ");
    Serial.print(val1);
    Serial.print(" ");
    Serial.println(val2);  // To send all the 'data' points to the plotter
  }
  else {
    /// Sensor 1
    bool highNow1 = val1 > THRESHOLD;
    digitalWrite(digitalOutPin1, highNow1 ? HIGH : LOW);
    
    if (highNow1 != highPrev1) {
      highPrev1 = highNow1;
      if (highNow1) {
        Serial.println("KNOCK1");
      }
    }

    /// Sensor 2
    bool highNow2 = val2 > THRESHOLD;
    digitalWrite(digitalOutPin2, highNow2 ? HIGH : LOW);
    
    if (highNow2 != highPrev2) {
      highPrev2 = highNow2;
      if (highNow2) {
        Serial.println("KNOCK2");
      }
    }
  }
  
  //delay(DELAY_MS);
  delay(DELAY_MS / 2); 
  
}

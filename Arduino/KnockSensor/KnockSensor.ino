int analogPin = 0;
int digitalOutPin = 2;

int val = 0;           // variable to store the value read

const int SAMPLE_RATE = 100; // Hz
const float DELAY_MS = 1000.0f / (float)SAMPLE_RATE;

const int THRESHOLD = 128; //TODO validate using debug plots
const bool debug = false; //set true to plot all values, false for production

bool highPrev = false;

void setup()
{
  Serial.begin(9600);              //  setup serial
  pinMode(digitalOutPin, OUTPUT);
  digitalWrite(digitalOutPin, LOW);
}

void loop()
{
  val = analogRead(analogPin);     // read the input pin
  if (debug) {
    Serial.print(0);  // To freeze the lower limit
    Serial.print(" ");
    Serial.print(THRESHOLD);
    Serial.print(" ");
    Serial.print(1024);  // To freeze the upper limit
    Serial.print(" ");
    Serial.println(val);  // To send all the 'data' points to the plotter
  }
  else {
    bool highNow = val > THRESHOLD;
    digitalWrite(digitalOutPin, highNow ? HIGH : LOW);
    
    if (highNow != highPrev) {
      Serial.println(highNow ? "KNOCK" : "NO");
      highPrev = highNow;      
    }
    else {
      //NOP
    }
  }
  
  delay(DELAY_MS);
}

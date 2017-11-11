int analogPin = 0;
int val = 0;           // variable to store the value read

void setup()
{
  Serial.begin(9600);              //  setup serial
}

void loop()
{
  val = analogRead(analogPin);     // read the input pin
  Serial.print(0);  // To freeze the lower limit
  Serial.print(" ");
  Serial.print(1024);  // To freeze the upper limit
  Serial.print(" ");
  Serial.println(val);  // To send all three 'data' points to the plotter
  
  delay(10);
}

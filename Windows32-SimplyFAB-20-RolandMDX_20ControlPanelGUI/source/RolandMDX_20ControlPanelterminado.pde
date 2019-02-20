//String filename = "data/circuit.rml"; //old loading file method

import processing.serial.*;
import static javax.swing.JOptionPane.*; ////

Roland roland;

Serial roland_port;
final boolean debug = true; /////

int buttonSizeX = 80;
int buttonSizeY = 20;
int buttonSizeXfile = 150;
int buttonHomeX = 10;
int buttonHomeY = 25;

int buttonSetZx = 100; // 
int buttonSetZup = 280;//190;
int buttonSetZdown = 370;//280;

int buttonMotorOn = 460; //370;
int buttonMotorOff = 550;//460;

int buttonGoZ = 190;//550;

int buttonFile = 640;

int x = mouseX;
int y = mouseY;
int val = 0;
void setup() 
{
  //fullScreen();
  size(800, 650);


  String COMx, COMlist = "";
/*
  Other setup code goes here - I put this at
  the end because of the try/catch structure.
*/
  try {
    if(debug) printArray(Serial.list());
    int i = Serial.list().length;
    if (i != 0) {
      if (i >= 2) {
        // need to check which port the inst uses -
        // for now we'll just let the user decide
        for (int j = 0; j < i;) {
          COMlist += char(j+'a') + " = " + Serial.list()[j];
          if (++j < i) COMlist += ",  ";
        }
        COMx = showInputDialog("Which COM port is Roland MDX20 connected to? (a,b,..):\n"+COMlist);
        if (COMx == null) exit();
        if (COMx.isEmpty()) exit();
        i = int(COMx.toLowerCase().charAt(0) - 'a') + 1;
      }
      String portName = Serial.list()[i-1];
      if(debug) println(portName);
      roland_port = new Serial(this, portName, 9600); // change baud rate to your liking
      roland_port.bufferUntil('\n'); // buffer until CR/LF appears, but not required..
    }
    else {
      showMessageDialog(frame,"Device is not connected to the PC");
      exit();
    }
  }
  catch (Exception e)
  { //Print the type of error
    showMessageDialog(frame,"COM port is not available (may\nbe in use by another program)");
    println("Error:", e);
    exit();
  }
  String ports = Serial.list()[0];
  //println(ports);
  noStroke();

 // roland_port = new Serial(this, ports, 9600);

  roland = new Roland(roland_port);


  roland.initialize();
  roland.setZRange(-1120, 0);
}

void draw() {
  background(241, 196, 0); 
  pushMatrix();
  translate(0, height);
  //rotate(PI);
  scale(1, -1);
  ellipse(roland.x/10, roland.y/10, 10, 10);
  popMatrix();
  
  pushMatrix();
  translate(0,0);
  fill(44,62,80);
  rect(0,0,800,50);
  /// Display Coordinates
  ////
  textAlign(RIGHT);
  text( "Coordinates XY" ,790, 620);
  text( x + "-" + y ,790, 640);
  //text( x + "-" + y , width/2, height/2);
  textAlign(LEFT);
  /// Home Button ////
  fill(52, 152, 219);
  textSize(20);
  text("  Home", 10, 20); 
  rect(buttonHomeX,buttonHomeY,buttonSizeX,buttonSizeY);
  
  
  
  /// Set>Home ////
  fill(46, 204, 113);
  textSize(20);
  text(" Set XY", buttonSetZx, 20); 
  rect(buttonSetZx,buttonHomeY,buttonSizeX,buttonSizeY);
  
  /// Z + ////
  fill(41, 128, 185);
  textSize(20);
  text("   Z-up", buttonSetZup, 20); 
  rect(buttonSetZup,buttonHomeY,buttonSizeX,buttonSizeY);
  /// Z - ////
  fill(41, 128, 185);
  textSize(18);
  text(" Z-down", buttonSetZdown, 20); 
  rect(buttonSetZdown,buttonHomeY,buttonSizeX,buttonSizeY);
 
  /// MotorOn ////
  fill(211, 84, 0);
  textSize(18);
  text("MotorOn", buttonMotorOn, 20); 
  rect(buttonMotorOn,buttonHomeY,buttonSizeX,buttonSizeY);
   /// MotorOff////
  fill(211, 84, 0);
  textSize(18);
  text("MotorOff", buttonMotorOff, 20); 
  rect(buttonMotorOff,buttonHomeY,buttonSizeX,buttonSizeY);
  
  /// GotoZero////
  fill(46, 204, 113);
  textSize(18);
  text("Go to Z0", buttonGoZ, 20); 
  rect(buttonGoZ,buttonHomeY,buttonSizeX,buttonSizeY);
  
  /// LoadFile////
  fill(231,76, 60);
  textSize(18);
  text("       LoadFile", buttonFile, 20); 
  rect(buttonFile,buttonHomeY,buttonSizeXfile,buttonSizeY);
    



  
 popMatrix();
  
}

void checkButtons(int x, int y){

  if(x > buttonHomeX && x < buttonHomeX + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    roland.home();
    fill(41, 128, 185);
    rect(buttonHomeX,buttonHomeY,buttonSizeX,buttonSizeY);
    println("Home clicked");
    roland.moveXYZ(roland.x, roland.y, roland.z);

  }
  
  if(x > buttonSetZx && x < buttonSetZx + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    //roland.setZAtMaterialSurface();  // doesnt seem to work - needs lot of work and testing
    // println(toMM(roland.z_at_material_surface) + " mm");
    //roland.moveXYZ(roland.x, roland.y, roland.z);
    //roland.setZ();  // doesnt seem to work - needs lot of work and testing
    //roland.moveXYZ(roland.x, roland.y, roland.z);
    //roland.initialize();
    //println("initialize");    
    fill(39, 174, 96);
    rect(buttonSetZx,buttonHomeY,buttonSizeX,buttonSizeY);
    roland.setZeroX = roland.x;
    roland.setZeroY = roland.y;
    
    print("New Machine zero = ");    
    print(roland.setZeroX);    
    print("  ");
    println(roland.setZeroY);
    
   // roland.moveXYZ(roland.x, roland.y, roland.z);

  }

  if(x > buttonSetZup && x < buttonSetZup + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    roland.z += roland.step;
    println("Z-UP");
    fill(39, 174, 96);
    rect(buttonSetZup,buttonHomeY,buttonSizeX,buttonSizeY);
    roland.moveXYZ(roland.x, roland.y, roland.z);
    

    
  }
  if(x > buttonSetZdown && x < buttonSetZdown + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    roland.z -= roland.step;
    println("Z-DOWN");
    fill(39, 174, 96);
    rect(buttonSetZdown,buttonHomeY,buttonSizeX,buttonSizeY);
    roland.moveXYZ(roland.x, roland.y, roland.z);
    
  }
  if(x > buttonMotorOn && x < buttonMotorOn + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
   
   roland.setMotorMode(1);
    println("motor on");
    fill(39, 174, 96);
    rect(buttonMotorOn,buttonHomeY,buttonSizeX,buttonSizeY);
    roland.moveXYZ(roland.x, roland.y, roland.z);
  }
    if(x > buttonMotorOff && x < buttonMotorOff + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    roland.setMotorMode(0);
    println("motor off");
    fill(39, 174, 96);
    rect(buttonMotorOff,buttonHomeY,buttonSizeX,buttonSizeY);
    roland.moveXYZ(roland.x, roland.y, roland.z);
  }
    if(x > buttonGoZ && x < buttonGoZ + buttonSizeX && y > buttonHomeY && y < buttonHomeY + buttonSizeY){
    roland.goToMaterialSurface();
    fill(39, 174, 96);
    rect(buttonGoZ,buttonHomeY,buttonSizeX,buttonSizeY);
    println("Go to Z-0");
    roland.moveXYZ(roland.x, roland.y, roland.z);

  }  
  
   if(x > buttonFile && x < buttonFile + buttonSizeXfile && y > buttonHomeY && y < buttonHomeY + buttonSizeY)
   {
    //roland.sendRMLFile(filename);  // old loading file method
    fill(39, 174, 96);
    rect(buttonFile,buttonHomeY,buttonSizeXfile,buttonSizeY);
    selectInput("Select a file to open:","fileSelected");
    println("LoadFile"); 
  } 
  
}

 void fileSelected(File selection){
       if ( selection ==null){
         println("Window was closed or the user hit cancel");
       }else{
         println("User selected" + selection.getAbsolutePath());
         roland.sendRMLFile(selection.getAbsolutePath());
       }
     }
     

void exit() {
}


void mousePressed() {
 // println(mouseY);
  if(mouseY > 50){
    roland.moveXYZ(mouseX*10, (height - mouseY)*10, roland.z);
    x = mouseX*10;
    y = (height - mouseY)*10;
  }
  checkButtons(mouseX,mouseY);
}

void keyPressed() {
  
  switch(key) {
  case '5':
    roland.step = (roland.step == 1)?100:1;
    break;
  case '8':
    roland.z += roland.step;
    break;
  case '2':
    roland.z -= roland.step;
    break;
  case 'z':
  case 'Z':
    roland.setZAtMaterialSurface();
    println(toMM(roland.z_at_material_surface) + " mm");
    break;
  case 'd':
  case 'D':
    println("drilling hole");
    roland.drillHole(150);
    println("drilled hole");
    break;
  case '0':
    roland.setMotorMode(0);
    println("motor off");
    break;
  case 'h':
  case 'H':
    roland.home();
    println("home");
    break;
  case 'i':
  case 'I':
    roland.initialize();
    println("initialize");
    break;
  case '1':
    roland.setMotorMode(1);
    println("motor on");
    break;

  case 't':
  case 'T':
    roland.goToMaterialSurface();
    break;
  }

  switch(keyCode) {
  case UP: 
    roland.y+=roland.step;
    break;
  case DOWN: 
    roland.y-=roland.step;
    break;
  case LEFT:
    roland.x-=roland.step;
    break;
  case RIGHT:
    roland.x+=roland.step;
    break;
  case ENTER:
   // roland.sendRMLFile(filename);
   selectInput("Select a file to open:","fileSelected");
    break;
  }

  roland.moveXYZ(roland.x, roland.y, roland.z);
}

float toMM(int milli_inch) {
  return 0.0254 * milli_inch;
}

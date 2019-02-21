import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import static javax.swing.JOptionPane.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class RolandMDX_20ControlPanelterminadofinal extends PApplet {

//String filename = "data/circuit.rml"; //old loading file method


 ////

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
int readjustZeroX = 0;
int readjustZeroY = 0;

int x = mouseX;
int y = mouseY;
int val = 0;
public void setup() 
{
  //fullScreen();
  


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
          COMlist += PApplet.parseChar(j+'a') + " = " + Serial.list()[j];
          if (++j < i) COMlist += ",  ";
        }
        COMx = showInputDialog("Which COM port is Roland MDX20 connected to? (a,b,..):\n"+COMlist);
        if (COMx == null) exit();
        if (COMx.isEmpty()) exit();
        i = PApplet.parseInt(COMx.toLowerCase().charAt(0) - 'a') + 1;
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

public void draw() {
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

public void checkButtons(int x, int y){

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
    readjustZeroX = roland.x;
    readjustZeroY = roland.y;
    
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

 public void fileSelected(File selection){
       if ( selection ==null){
         println("Window was closed or the user hit cancel");
       }else{
         println("User selected" + selection.getAbsolutePath());
         roland.sendRMLFile(selection.getAbsolutePath());
       }
     }
     

public void exit() {
}


public void mousePressed() {
 // println(mouseY);
  if(mouseY > 50){
    roland.moveXYZ(mouseX*10, (height - mouseY)*10, roland.z);
    x = mouseX*10;
    y = (height - mouseY)*10;
  }
  checkButtons(mouseX,mouseY);
}

public void keyPressed() {
  
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

public float toMM(int milli_inch) {
  return 0.0254f * milli_inch;
}
class Roland {
  Serial myPort;  
  int x, y, z, step;
  int z_at_material_surface;
  int setZeroX;
  int setZeroY;
  int xset;
  int yset;
  int offsetX;
  int offsetY;
  String sendme;
  String sendme2;
  
  int prevX, prevY;

  Roland(Serial port) {
    this.myPort = port;
    this.prevX = this.prevY = this.x = this.y = this.z = 0;
    this.step = 10; //steps to move each time
    this.setZeroX = 0;
    this.setZeroY = 0;
  }

 public void home() {
    this.myPort.write("H;");
    println("H;");
  }
  public void initialize() {
    println("IN;!MC0;");
  }
  
    
  public void drillHole(int depth) {
    int start_z = this.z;
    int end_z = this.z - depth;
    setMotorMode(1);
    while (this.z > end_z) {
      moveXYZ(this.x, this.y, this.z);
      this.z -= 3;
      delay(1000);
    }
    setMotorMode(0);
    initialize();
    moveXYZ(this.x, this.y, start_z);
  }

  // x, y movement - might be redundant (See moveXYZ())
  public void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
    this.myPort.write("PA"+this.x+","+this.y+";");
    println("PA"+this.x+","+this.y+";");
  }

  public void setZAtMaterialSurface() {
    this.z_at_material_surface = this.z;
    println("Set z_at_material_surface to " + this.z_at_material_surface);
  }
  public void goToMaterialSurface() {
    println("going to material surface");
    moveXYZ(this.x,this.y, z_at_material_surface);
  }
  public void wait(int millis) {
    this.myPort.write("W"+millis+";");
    println("W"+millis+";");
    //delay(millis);
  }
  public void setMotorMode(int mode) {
    String command = "!MC"+mode+";";
    this.myPort.write(command);
    println(command);
  }
  // doesnt seem to work - needs lot of work and testing
  //void setZ(int z)
  public void setZ(){
     //this.z = z;
    this.z_at_material_surface = this.z;
    this.myPort.write("!ZM"+this.z+";");
    println("!ZM"+this.z+";");
    this.myPort.write("!PZ"+this.z+";");
    println("!PZ"+this.z+";");
    
    this.myPort.write("!ZO"+this.z+";");
    println("!ZO"+this.z+";");
    this.z = 0;
    this.myPort.write("!ZO"+this.z+";");
    println("!ZO"+this.z+";");

}

  public void setZRange(int z1, int z2) {
    this.myPort.write("!PZ" + z1 + "," + z2+";");
    println("!PZ" + z1 + "," + z2+";");
  }

  public void moveXYZ(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.myPort.write("Z" + this.x + ","  + this.y + "," + this.z+";");
    println("Z" + this.x + ","  + this.y + "," + this.z+";");
  }

 public void moveWithOffset(int x,int y,int z, int offsetX, int offsetY){
    this.x = x;
    this.y = y;
    this.z = z;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.myPort.write("Z" + this.x + ","  + this.y + "," + this.z+";");
    println("Z" + this.x + ","  + this.y + "," + this.z+";");
 
 }

public void sendRMLFile(String path) {
  //setZeroX = readjustZeroX ;
  //setZeroY = readjustZeroY;
   roland.moveXYZ( readjustZeroX, readjustZeroY, roland.z);
   println( readjustZeroX, readjustZeroY, roland.z);
   println("X= : " + this.x);
   println("Y= : " + this.y);
   setZeroX = this.x;
   setZeroY = this.y;
   delay(2000);
   String lines[] = loadStrings(path);
   int lastLine = lines.length;
  // println(lines.length);
   
  //IGNORE THE FIRST LINE AND GET THE SPEEDS FROM THE FILE//
 // println(lines[0]);
  String[] values3 = split(lines[0].substring(0), ';');  // Get value VS (vertical speed)
  //println(values3);
  String getVS = values3[2].substring(2);
  float VsToFloat = PApplet.parseFloat(getVS);
  
 //println(VsToFloat);
 
 String sendFirstLineWithSpeeds = "PA;PA;VS" + VsToFloat + ";!VZ" + VsToFloat + ";!PZ0,400;!MC1;";
 println(sendFirstLineWithSpeeds);
 roland_port.write(sendFirstLineWithSpeeds);
 

     //roland_port.write("PU140,1630;");
    for (int i = 1; i < lines.length-1; i++) {
     // roland_port.write(lines[i+1]);
      //roland_port.write(lines[i]);
      int distance = 200;
      if (lines[i].charAt(0) == 'Z') {
        int[] values = PApplet.parseInt(split(lines[i].substring(1), ','));
        String[]  values1 = split(lines[i].substring(1), ',');
        //int[] values = int(split(lines[i], ','));
        //this.z = int(split(lines[i].substring(1), ','));
        this.x = values[0] + setZeroX;
        this.y = values[1] + setZeroY;
        distance = PApplet.parseInt(dist(this.x, this.y, this.prevX, this.prevY));
        //println("Distance : " + distance);
        sendme = "Z" + this.x + "," + this.y + "," + values1[2];
       // println(int(values1[2].substring(0,2)));
        println(sendme);
       roland_port.write(sendme);
      }
      else{
        String first2Chars = lines[i].substring(0,2);
        String[]  penLift = split(lines[i].substring(2), ',');
        int indexToPrint = penLift[1].length() - 1;
        
        int sendX = PApplet.parseInt(penLift[0]) + setZeroX;
        int sendY = PApplet.parseInt(penLift[1].substring(0,indexToPrint)) + setZeroY;
       
        String sendPenPos = first2Chars + sendX + "," +  sendY + ";";
        println(sendPenPos);
        roland_port.write(sendPenPos);
      }
      
      // need to replace the below delay() with something smarter
      float  speed = 1;
      speed = PApplet.parseFloat(getVS) * 10;
      println(speed); 
      if (speed <= 10) {
        //speed = int(getVS);
        delay(PApplet.parseInt(distance* 20/ (speed / 10) ));//2500
      } else {
        delay(PApplet.parseInt(distance * 2 *(speed / 10)  ));//2500
      }
      //println("X= : " + this.x);
      //println("Y= : " + this.y);
      this.prevX = this.x ;
      this.prevY = this.y;
    }
  //println(lines[lastLine-1]);
  //roland_port.write(lines[lastLine-1]);
  println("PU" + setZeroX + "," + setZeroY + ";");
  roland_port.write("PU" + setZeroX + "," + setZeroY + ";");
  sendme2 = "Z" + setZeroX + "," + setZeroY + "," +"0"+";";
  println(sendme2);
  roland_port.write(sendme2);
 // roland_port.write("PA;PA;!PZ0,0;!MC0;");
  //println("PA;PA;!PZ0,0;!MC0;");
  roland.setZeroX = readjustZeroX ;
  roland.setZeroY = readjustZeroY;
  println(readjustZeroX);
      print("New Machine zero = ");    
      print(roland.setZeroX);    
      print("  ");
      println(roland.setZeroY);
  println("!MC0;");
  roland_port.write("!MC0;");
  }
}
  public void settings() {  size(800, 650); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RolandMDX_20ControlPanelterminadofinal" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

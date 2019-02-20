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

 void home() {
    this.myPort.write("H;");
    println("H;");
  }
  void initialize() {
    println("IN;!MC0;");
  }
  
    
  void drillHole(int depth) {
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
  void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
    this.myPort.write("PA"+this.x+","+this.y+";");
    println("PA"+this.x+","+this.y+";");
  }

  void setZAtMaterialSurface() {
    this.z_at_material_surface = this.z;
    println("Set z_at_material_surface to " + this.z_at_material_surface);
  }
  void goToMaterialSurface() {
    println("going to material surface");
    moveXYZ(this.x,this.y, z_at_material_surface);
  }
  void wait(int millis) {
    this.myPort.write("W"+millis+";");
    println("W"+millis+";");
    //delay(millis);
  }
  void setMotorMode(int mode) {
    String command = "!MC"+mode+";";
    this.myPort.write(command);
    println(command);
  }
  // doesnt seem to work - needs lot of work and testing
  //void setZ(int z)
  void setZ(){
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

  void setZRange(int z1, int z2) {
    this.myPort.write("!PZ" + z1 + "," + z2+";");
    println("!PZ" + z1 + "," + z2+";");
  }

  void moveXYZ(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.myPort.write("Z" + this.x + ","  + this.y + "," + this.z+";");
    println("Z" + this.x + ","  + this.y + "," + this.z+";");
  }

 void moveWithOffset(int x,int y,int z, int offsetX, int offsetY){
    this.x = x;
    this.y = y;
    this.z = z;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.myPort.write("Z" + this.x + ","  + this.y + "," + this.z+";");
    println("Z" + this.x + ","  + this.y + "," + this.z+";");
 
 }

  void sendRMLFile(String path) {
     println("X= : " + this.x);
     println("Y= : " + this.y);
     setZeroX = this.x;
     setZeroY = this.y;
     delay(2000);
     String lines[] = loadStrings(path);
     int lastLine = lines.length;
     println(lines.length);
     //roland_port.write("PR;PR;VS4;!VZ4;!PZ0,80;!MC1;");
     roland_port.write("PA;PA;VS4;!VZ4;!PZ0,400;!MC1;");
     println("PA;PA;VS4;!VZ4;!PZ0,400;!MC1;");
  
    
    
   /* for (int i = 1; i < lines.length-1;) {
     int[] values = int(split(lines[i].substring(1), ','));
     String[]  values1 = split(lines[i].substring(1), ',');
     println(values1[3]);
     println(values1[4]);

    }
*/



     //roland_port.write("PU140,1630;");
    for (int i = 1; i < lines.length-1; i++) {
     // roland_port.write(lines[i+1]);
      //roland_port.write(lines[i]);
      int distance = 200;
      if (lines[i].charAt(0) == 'Z') {
        int[] values = int(split(lines[i].substring(1), ','));
        String[]  values1 = split(lines[i].substring(1), ',');
        //int[] values = int(split(lines[i], ','));
        //this.z = int(split(lines[i].substring(1), ','));
        this.x = values[0] + setZeroX;
        this.y = values[1] + setZeroY;
        distance = int(dist(this.x, this.y, this.prevX, this.prevY));
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
        
        int sendX = int(penLift[0]) + setZeroX;
        int sendY = int(penLift[1].substring(0,indexToPrint)) + setZeroY;
       
        String sendPenPos = first2Chars + sendX + "," +  sendY + ";";
        println(sendPenPos);
        roland_port.write(sendPenPos);
      }
      
      // need to replace the below delay() with something smarter
      if (distance < 200) {
        delay(distance*10);//500
      } else {
        delay(distance*6);//2500
      }
      //println("X= : " + this.x);
      //println("Y= : " + this.y);
      this.prevX = this.x ;
      this.prevY = this.y;
    }
  //println(lines[lastLine-1]);
  //roland_port.write(lines[lastLine-1]);
  println("PU" + setZeroX + "," + setZeroY + ",");
  roland_port.write("PU" + setZeroX + "," + setZeroY + ",");
  sendme2 = "Z" + setZeroX + "," + setZeroY + "," +"0"+";";
  println(sendme2);
  roland_port.write(sendme2);
  println("!MC0;");
  roland_port.write("!MC0;");
  }
}

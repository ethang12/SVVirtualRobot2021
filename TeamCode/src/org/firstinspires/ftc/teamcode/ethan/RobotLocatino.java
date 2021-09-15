package org.firstinspires.ftc.teamcode.ethan;

public class RobotLocatino {
    double angle;

public RobotLocatino(double angle){
    this.angle =angle;
}

public double getHanding(){
    double angle=this.angle;
    while(angle>180)
    {
    angle-=360;
    }
    while(angle<-180){
        angle+=360;
    }
    return angle;
}
@Override
public String toString(){
        return "Robot Location: angle (" + angle +")";
}
public void turn(double angleChange){
    angle+=angleChange;
}
public void setAngle(double angle) {
    this.angle = angle;
}}
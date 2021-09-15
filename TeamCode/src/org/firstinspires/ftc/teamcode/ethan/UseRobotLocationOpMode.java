package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class UseRobotLocationOpMode extends OpMode {
    RobotLocatino robotLocatino = new RobotLocatino(0);

    @Override
    public void init(){
        robotLocatino.setAngle(0);
    }
    @Override
    public void loop(){
        if(gamepad1.a){
            robotLocatino.turn(0.1);
        }
        else if(gamepad1.b){
            robotLocatino.turn(-.1);
        }
        if(gamepad1.dpad_left){
            robotLocatino.changeX(-.1);
        }else if(gamepad1.dpad_right){
            robotLocatino.changeX(.1);
        }
        if(gamepad1.dpad_down){
            robotLocatino.changeY(-.1);
        }else if(gamepad1.dpad_up){
            robotLocatino.changeY(.1);
        }
        telemetry.addData("Location", robotLocatino);
        telemetry.addData("Heading", robotLocatino.getHeading());
        telemetry.addData("Angle", robotLocatino.getAngle());
    }
}

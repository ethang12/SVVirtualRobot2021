package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Exercises4 extends OpMode {

    public void init(){

    }
    public void loop(){
        double speed=gamepad1.left_stick_y;

        if (!gamepad1.a){
            speed *= .5;
        }
        telemetry.addData("speed is", speed);
    }
}


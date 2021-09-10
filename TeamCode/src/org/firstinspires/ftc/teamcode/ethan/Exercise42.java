package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Exercise42 extends OpMode {
    @Override
    public void loop(){
        double yspeed= gamepad1.left_stick_y;
        double xspeed= gamepad1.left_stick_x;
        if (gamepad1.b){
            xspeed = gamepad1.left_stick_y;
            yspeed = gamepad1.left_stick_x;
        }
        telemetry.addData("y speed", yspeed);
        telemetry.addData("xspeed", xspeed);
    }
}

package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class GamepadReading extends OpMode {
    @Override
    public void init() {

    }
    public void loop(){
        telemetry.addData ("Left Stick X", gamepad1.left_stick_x);
        telemetry.addData ("Left Stick Y", gamepad1.left_stick_y);
        telemetry.addData ("Left Stick", gamepad1.a);
    }
}

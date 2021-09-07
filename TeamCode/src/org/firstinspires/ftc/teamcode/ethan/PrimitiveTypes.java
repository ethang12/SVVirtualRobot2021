package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp
public class PrimitiveTypes  extends OpMode {
    @Override
    public void init() {
        int teamNumber = 17334;
        double motorSpeed = 0.5;
        boolean touchSensorPressed = true;
        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("Motor Speed", motorSpeed);
        telemetry.addData("Touch Sensor", touchSensorPressed);
//telemetry.addData means it is sent to the driver station
    }
    @Override
    public void loop() {
    }
}

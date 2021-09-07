package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class HelloWorld extends OpMode {
@Override
    public void init() {
    telemetry.addData("Hello","Ethan");
}
// This happens on initialization^^

@Override
    public void loop() {

}
//This happens repeatedly on initialization^^
}


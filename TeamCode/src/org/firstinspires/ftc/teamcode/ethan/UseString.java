package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class UseString extends OpMode {
    @Override
    public void init(){
        String myName = "Ethan Grim";
        int grade = 12;
        telemetry.addData("Hello", myName);
        telemetry.addData("Grade", grade);

    }
    @Override
    public void loop(){

    }
}

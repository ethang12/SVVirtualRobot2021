package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp

public class Chapter3Exercises extends OpMode {
    @Override
    public void init(){

    }
    public void loop(){
        double difference_sticks = gamepad1.left_stick_y- gamepad1.right_stick_y;
        double sum_triggers = gamepad1.right_trigger+ gamepad1.left_trigger;
        telemetry.addData("Right Stick X", gamepad1.right_stick_x);
        telemetry.addData("Right Stick Y", gamepad1.right_stick_y);
        telemetry.addData("B Button", gamepad1.b);
        telemetry.addData("Difference betweem left and right sticks", difference_sticks);
        telemetry.addData("Sum of triggers", sum_triggers);

    }
}

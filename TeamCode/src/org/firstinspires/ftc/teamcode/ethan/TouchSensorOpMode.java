package org.firstinspires.ftc.teamcode.ethan;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ethan.mechanisms.ProgrammingBoard2;

import javax.xml.stream.events.StartElement;

@TeleOp
public class TouchSensorOpMode extends OpMode {
    ProgrammingBoard2 board = new ProgrammingBoard2();
    @Override
    public void init(){
        board.init(hardwareMap);
    }

    @Override
    public void loop(){
        telemetry.addData("Touch Sensor", board.getTouchSensorState());
    }
}

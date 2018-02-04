package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by mzhang on 2/3/2018.
 */
@TeleOp(name="Debug", group="TeleOp")
public class Debug extends VirusMethods{
    public void init(){
        super.init();
        initializeIMU();
    }
    public void loop() {
        telemetry.addData("red", colorSensor.red()-initialRed);
        telemetry.addData("blue", colorSensor.blue()-initialBlue);

    }
}

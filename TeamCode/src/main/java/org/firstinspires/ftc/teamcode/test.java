package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by mzhang on 11/17/2017.
 */
@TeleOp(name="test", group="TeleOp")

public class test extends VirusMethods{
    enum state  {goToPosition,scanJewel,knockJewelRight, knockJewelLeft, stop, debug}
    state state;
    int counter = 0;

    public void init(){
        super.init();
        initializeIMU();
        //while (!imu.isSystemCalibrated());
    }

    public void start(){
        lmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        resetEncoder();
        jewelKnocker.setPosition(.5);
        state=state.goToPosition;

    }
    @Override
    public void loop(){
        updateOrientation();
        if (gamepad2.a){
            turn(90,.1);

        }
        telemetry.addData("Amount Blue:", colorSensor.blue());
        telemetry.addData("Amount Red:", colorSensor.red());
        telemetry.addData("ZHeading", getZHeading());

    }


}

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
        gyroSensor.calibrate();
        while (gyroSensor.isCalibrating());
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
        updateControllerValues();
        if (gamepad2.dpad_up){
            if (counter == 0 && jewelKnocker.getPosition()>0){
                jewelKnocker.setPosition(jewelKnocker.getPosition()-.05);
                counter++;
            }
        }else if (gamepad2.dpad_down){
            if (counter == 0 && jewelKnocker.getPosition()<1){
                jewelKnocker.setPosition(jewelKnocker.getPosition()+.05);
                counter++;
            }
        }else{
            counter = 0;
        }
        telemetry.addData("Jewel knocker position", jewelKnocker.getPosition());

    }


}

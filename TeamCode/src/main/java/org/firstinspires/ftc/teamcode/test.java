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
    boolean topFullOpen=false;
    boolean bottomFullOpen=false;
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
        topGrabberOpen(false);
        jewelKnockerUp();
        cube1.setPosition(0.0);
        cube2.setPosition(1);
        state=state.goToPosition;

    }
    @Override
    public void loop(){
        updateOrientation();
        updateControllerValues();
         switch (state){
            case goToPosition:
                jewelKnockerDown();
                colorSensor.enableLed(true);
                state = state.stop;
                break;
            case stop:
                runMotors(0,0,0,0);
                break;
        }
        telemetry.addData("Blue",getBlue());
        telemetry.addData("Red",getRed());
    }


}

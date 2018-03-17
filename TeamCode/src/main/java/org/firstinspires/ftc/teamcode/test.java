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
        if (gamepad2.left_bumper) {
            cube3.setPosition(cube3.getPosition()-0.05);
            cube4.setPosition(cube4.getPosition()+0.05);

        }
        else if (gamepad2.right_bumper) {
            topGrabberClose();
            topFullOpen=false;
        }
        if (gamepad2.left_trigger>0.5){
            cube1.setPosition(cube1.getPosition()-0.05);
            cube2.setPosition(cube2.getPosition()+0.05);

        }
        else if (gamepad2.right_trigger>0.5){
            cube1.setPosition(.6);
            cube2.setPosition(.4);
            bottomFullOpen=false;
        }
        if (gamepad2.a){
            lift(0);
        }
        else if (gamepad2.b && !gamepad2.start){
            lift(2200);
        }
        else if (gamepad2.y){
            lift(3500);
        }
        else if (gamepad2.x){
            lift(4500);
        }
        else{
            if (liftLeft.getCurrentPosition()>4600){
                lift(4490);
            }
            else if(liftLeft.getCurrentPosition()<=-50){
                lift(10);
            }
            else{
                liftPower(gamepad2.right_stick_y * -.5);
            }
        }
        telemetry.addData("cube1",cube1.getPosition());
        telemetry.addData("cube2",cube2.getPosition());
        telemetry.addData("cube3",cube3.getPosition());
        telemetry.addData("cube4",cube4.getPosition());
    }


}

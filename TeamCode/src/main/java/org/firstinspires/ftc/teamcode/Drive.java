package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="TeleOp", group="TeleOp")

public class Drive extends VirusMethods {
    int counter = 0;
    public void start(){
        lmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // glyphSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        // glyphSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        cryptoboxSection=0;
        topGrabberOpen();
        jewelKnockerUp();
        cube1.setPosition(0.0);
        cube2.setPosition(1);
        glyphArm.setPosition(0);
        glyphClaw.setPosition(1);
    }
    public void loop(){
        updateControllerValues();
        if (leftx!=0 || lefty!=0){
            runMotors(var1,var2,var2,var1,rightx); //var1 and 2 are computed values found in theUpdateControllerValues method
        } else {
            runMotors(0,0,0,0,rightx);
        }
        if (gamepad1.right_bumper){
            maxPower=1;
            maxSteerPower=1;
            lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        else if (gamepad1.left_bumper){
            maxPower=.75;
            maxSteerPower=.3;
            lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        if (gamepad2.left_bumper) {
            topGrabberOpen();
        }
        else if (gamepad2.right_bumper) {
            topGrabberClose();
        }
        if (gamepad2.left_trigger>0.5){
            cube1.setPosition(0.0);
            cube2.setPosition(1);
        }
        else if (gamepad2.right_trigger>0.5){
            cube1.setPosition(.6);
            cube2.setPosition(.4);
        }
        liftPower(gamepad2.right_stick_y);
        if (gamepad2.a){
            lift(0);
        }
        else if (gamepad2.b && !gamepad2.start){
            lift(-2333.5);
        }
        else if (gamepad2.y){
            lift(-4667);
        }
        else if (gamepad2.x){
            lift(-7000.5);
        }
        if (gamepad2.back){
            cryptoboxSection++;
        }
//        if (gamepad2.dpad_left){
//            glyphClaw.setPosition(.5);
//        }
//        else if (gamepad2.dpad_up){
//            glyphArm.setPosition(.5);
//        }
//        else if (gamepad2.dpad_right){
//            glyphClaw.setPosition(1);
//        }
//        else if (gamepad2.dpad_down){
//            glyphArm.setPosition(0);
//        }
        /*if (gamepad2.dpad_down){
            if (counter == 0 && lift.getPosition()>0){
                lift.setPosition(lift.getPosition()+.05);
                counter++;
            }
        }else if (gamepad2.dpad_up){
            if (counter == 0 && lift.getPosition()<0.48){
                lift.setPosition(lift.getPosition()-.05);
                counter++;
            }
        }else{
            counter = 0;
        } */

        // glyphSlide.setPower(gamepad2.left_stick_y);
        relicRetractor.setPower(gamepad2.left_stick_y);

        telemetry.addData("Bottom Grabber",GPS(true));
        telemetry.addData("Top Grabber",GPS(false));
        telemetry.addData("Cryptobox Location (relative to robot)",cryptoboxLocation());
        telemetry.addData("liftLeft Encoder:", liftLeft.getCurrentPosition());
        telemetry.addData("liftRight Encoder:", liftRight.getCurrentPosition());
        // Telemetry();
    }
}
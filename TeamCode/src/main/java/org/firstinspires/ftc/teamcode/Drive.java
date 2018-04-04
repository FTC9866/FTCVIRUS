package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="TeleOp", group="TeleOp")

public class Drive extends VirusMethods {
    boolean topFullOpen=false;
    boolean bottomFullOpen=false;
    public void init() {
        super.init();
        initializeIMU();
    }
    public void start() {
        lmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // glyphSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // glyphSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        cryptoboxSection=0;
        topGrabberOpen(false);
        jewelKnockerUp();

        cube1.setPosition(0.0);
        cube2.setPosition(1);
        jewelKnockerBase.setPosition(.5);
        relicArm.setPosition(0);
        relicClaw.setPosition(0);
        grabberLeft.setPosition(0);
        grabberRight.setPosition(1);

        mode = true;
    }
    public void loop() {
        updateControllerValues();
        updateOrientation();
        //driver1
        if(gamepad1.b) {
            autoBalance = !autoBalance;
            while(gamepad1.b);
        }
        if(autoBalance) {
            balance();
        }
        else if (leftx!=0 || lefty!=0) {
            runMotors(var1, var2, var2, var1, rightx); //var1 and 2 are computed values found in theUpdateControllerValues method
        }
        else if(gamepad1.dpad_left) {
            runMotors(-.35,.35,.35,-.35); //var1 and 2 are computed values found in theUpdateControllerValues method

        }
        else if(gamepad1.dpad_right) {
            runMotors(.35,-.35,-.35,35); //var1 and 2 are computed values found in theUpdateControllerValues method
        }
        else {
            runMotors(0,0,0,0,rightx);
        }
        if (gamepad1.right_bumper) {
            maxPower = 1;
            maxSteerPower = 1;
            lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        else if (gamepad1.left_bumper) {
            maxPower = 0.75;
            maxSteerPower = 0.3;
            lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        //driver2
        if (mode) {
            if (gamepad2.left_bumper) {
                if (!topFullOpen) {
                    topGrabberOpen(true);
                    waitTime(200); //to give some time for user to release bumper
                    topFullOpen = true;
                } else {
                    topGrabberOpen(false);
                    waitTime(200); //to give some time for user to release bumper
                    topFullOpen = false;
                }

            }
            else if (gamepad2.right_bumper) {
                topGrabberClose();
                topFullOpen = false;
            }

            if (gamepad2.left_trigger>0.5) {
                if(!bottomFullOpen){
                    cube1.setPosition(.4);
                    cube2.setPosition(.6);
                    waitTime(200);
                    bottomFullOpen = true;
                } else {
                    cube1.setPosition(0.0);
                    cube2.setPosition(1);
                    waitTime(200);
                    bottomFullOpen = false;
                }
            }
            else if (gamepad2.right_trigger>0.5) {
                cube1.setPosition(.6);
                cube2.setPosition(.4);
                bottomFullOpen = false;
            }
            if (gamepad2.a) {
                liftPosition = 0;
            }
            else if (gamepad2.b && !gamepad2.start) {
                liftPosition = (2200/3);
                if(grabberLeft.getPosition()<=.9&&liftPosition==0){
                    grabberLeft.setPosition(.9);
                    grabberRight.setPosition(.1);
                }
            }
            else if (gamepad2.y) {
                liftPosition = (3500/3);
            }
            else if (gamepad2.x) {
                liftPosition = (4500/3);
            }
            if((grabberLeft.getPosition()==.9&&grabberRight.getPosition()==.1)&&(liftLeft.getCurrentPosition()>2100/3&&liftLeft.getCurrentPosition()>2100)){
                grabberLeft.setPosition(1);
                grabberRight.setPosition(0);
            }
            else if (0!=gamepad2.right_stick_y) {
                if (liftLeft.getCurrentPosition()>4600) {
                    lift(4490);
                } else if(liftLeft.getCurrentPosition()<=-50 ){
                    lift(10);
                } else {
                    liftPower(gamepad2.right_stick_y * -.5);
                    liftPosition = liftRight.getCurrentPosition();
                }
            }

            if (gamepad2.dpad_down) {
                grabberLeft.setPosition(0);
                grabberRight.setPosition(1);
                grabberLeftSpin.setPower(0);
                grabberRightSpin.setPower(0);
            }
            else if (gamepad2.dpad_up) {
                if (grabberLeft.getPosition() == 0) {
                    grabberLeft.setPosition(1);
                    grabberRight.setPosition(0);
                }
                else if (grabberLeft.getPosition() == 1) {
                    grabberLeftSpin.setPower(1);
                    grabberRightSpin.setPower(-1);
                }
            }
            else if (grabberLeft.getPosition() == 1){
                grabberLeftSpin.setPower(-1);
                grabberRightSpin.setPower(1);
            }
            else if (gamepad2.dpad_right) {
                if (grabberRight.getPosition() == 0) {
                    grabberRight.setPosition(0.1);
                }
            }
            else if (gamepad2.dpad_left) {
                if (grabberLeft.getPosition() == 1) {
                    grabberLeft.setPosition(0.9);
                }
            }
            else if (grabberRight.getPosition()==.1||grabberLeft.getPosition()==.9) {
                grabberLeft.setPosition(1);
                grabberRight.setPosition(0);
            }
        }
        else {
            if (gamepad2.right_bumper) {
                relicClaw.setPosition(.5);
            }
            if (gamepad2.left_bumper) {
                relicClaw.setPosition(0);
            }
            if (gamepad2.left_trigger>0.5) {
                relicArm.setPosition(0.36);
            }
            else if (gamepad2.right_trigger>0.5) {
                relicArm.setPosition(1);
            } else {

            }
        }

        if(0==gamepad2.right_stick_y){
            lift(liftPosition);
        }

        if (gamepad2.start && !gamepad2.b){
            cryptoboxSection++;
        }

        if (gamepad2.back){
            mode = !mode;
            while(gamepad2.back);
        }

        if (gamepad2.dpad_left){
            relicClaw.setPosition(0);
        }
        else if (gamepad2.dpad_up){
            relicArm.setPosition(1);
        }
        else if (gamepad2.dpad_right){
            relicClaw.setPosition(.5);
        }
        else if (gamepad2.dpad_down){
            relicArm.setPosition(0.36);
        }
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

        relicSlide.setPower(gamepad2.left_stick_y * 0.25);

        telemetry.addData("Gyro Reading",getZHeading());
        /*telemetry.addData("Top Grabber",GPS(false));
        telemetry.addData("Cryptobox Location (relative to robot)",cryptoboxLocation());
        telemetry.addData("liftLeft Encoder:", liftLeft.getCurrentPosition());
        telemetry.addData("liftRight Encoder:", liftRight.getCurrentPosition());
        telemetry.addData("relicArm Position: ", relicArm.getPosition());
        telemetry.addData("relicClaw Position: ", relicClaw.getPosition());
  */      // Telemetry();
    }
}
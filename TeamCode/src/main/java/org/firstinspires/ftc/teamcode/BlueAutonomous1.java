package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;


@TeleOp(name="BlueAutonomous1", group="TeleOp")

public class BlueAutonomous1 extends VirusMethods {
    enum state  {dropArm,scanJewel,knockJewelRight, knockJewelLeft, stop, goToPosition, debug, alignStraight, toCryptoBox, backOnStone, faceCryptoBox, placeGlyph, turnBackLeft, turnBackRight, turnBack, toCryptoBoxpart1, turn90, toCryptoBoxpart2, secondRam, moveUntilScanned}
    state state;
    boolean setMotor;
    boolean knock;

    public void init() {
        super.init();
        gyroSensor.calibrate();
        vuforiaInit();
        while (gyroSensor.isCalibrating());

    }

    public void start() {
        lmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        cube1.setPosition(0);
        cube2.setPosition(1);
        topGrabberClose();
        lift.setPosition(0);
        jewelKnocker.setPosition(0);
        lift.setPosition(0);
        jewelKnocker.setPosition(0);

        state=state.dropArm;

    }
    @Override

    public void loop() {

        readVumark();
        switch (state) {
            case dropArm:

                jewelKnocker.setPosition(0.65);
                colorSensor.enableLed(true);
                waitTime(1000);
                resetEncoder();
                state=state.scanJewel;
                break;

            case scanJewel:
                if (colorSensor.red() < colorSensor.blue()) { //checks to see if object is more red or more blue
                    knock  = true;
                    colorSensor.enableLed(false);
                    state=state.knockJewelLeft;
                }
                else if (colorSensor.blue() < colorSensor.red()) {
                    knock = false;
                    state=state.knockJewelRight;
                }
                break;

            case knockJewelLeft:
                if (turn(345, 0.7)){
                    jewelKnocker.setPosition(0);
                    state=state.turnBack;
                }
                break;

            case knockJewelRight:
                if (turn(15,0.7)) {
                    jewelKnocker.setPosition(0);
                    state = state.turnBack;
                }
                break;
            case turnBack:
                if (turn(0, 0.7)){
                    position = lmotor0.getCurrentPosition();
                    state = state.moveUntilScanned;
                }
                break;
            //turnBackLeft and turnBackRight kept just in case turnMotorsPlus method doesn't work
            case turnBackLeft:
                turnMotors(0, true, 0.5);
                state = state.moveUntilScanned;
                break;

            case turnBackRight:
                turnMotors(0, false, 0.5);
                state = state.moveUntilScanned;
                break;

            case moveUntilScanned:
                runMotors(.1,.1,.1,.1); //program to make it move backwards if it doesn't see it after traveling a certain distance
                    if(vuMark != RelicRecoveryVuMark.UNKNOWN){
                        telemetry.addData("VuMark", "%s visible", vuMark);
                        VuMarkStored = vuMark;
                        amountMovedForward = (lmotor0.getCurrentPosition()-position)*inPerPulse; //how many inches it moved back to scan the vision target
                        state = state.alignStraight;
                    }
                break;
            case alignStraight:
                if (turn(0,1)) {
                    resetEncoder();
                    counter = 0;
                    state = state.toCryptoBox ;
                }
                break;
            case backOnStone: //unused
                if (setMotorPositions(0,0,0,0, .5)){
                    resetEncoder();
                    state=state.toCryptoBox;
                }
                break;
            case toCryptoBox:
                lift(0.15); //so that cube doesn't drag on ground
                if (VuMarkStored == RelicRecoveryVuMark.LEFT){
                    if (setMotorPositionsINCH(-29.6-amountMovedForward,-29.6-amountMovedForward,-29.6-amountMovedForward,-29.6-amountMovedForward, -.5)){
                        resetEncoder();
                        telemetry.addData("reee", "e");
                        state=state.faceCryptoBox;
                    }
                }
                else if (VuMarkStored == RelicRecoveryVuMark.CENTER){
                    if (setMotorPositionsINCH(-39-amountMovedForward,-39-amountMovedForward,-39-amountMovedForward,-39-amountMovedForward, -.5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }
                else if (VuMarkStored == RelicRecoveryVuMark.RIGHT){
                    if (setMotorPositionsINCH(-45-amountMovedForward,-45-amountMovedForward,-45-amountMovedForward,-45-amountMovedForward, .5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }
//                else { //just in case of some weird circumstance that it forgets the VuMark
////                    if (setMotorPositionsINCH(-36,-36,-36,-36,.5)){ //parks in safe zone in front of cryptobox
////                        resetEncoder();
////                        state = state.stop;
////                    }
//                }
                break;
            case faceCryptoBox:
                setThreshold(0);
                if (turn(90,.6)) {
                    resetEncoder();
                    state=state.placeGlyph;
                }
                break;
            case placeGlyph:
                runMotors(0.3,0.3,0.3,0.3);
                waitTime(1000);
                runMotors(0,0,0,0);
                topGrabberOpen();
                waitTime(1000);
                runMotors(-0.3,-0.3,-0.3,-0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                lift(0);

                state = state.secondRam;

                break;
            case secondRam:
                waitTime(1000);
                runMotors(0.3,0.3,0.3,0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                waitTime(1000);
                runMotors(-0.3,-0.3,-0.3,-0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                state = state.stop;
                break;
            case debug:
                //telemetry.addData("done","done");
                telemetry.addData("setMotor returns", setMotor);
                telemetry.addData("inPerPulse", inPerPulse);
                telemetry.addData("left motor", lmotor0.isBusy());
                telemetry.addData("counter",counter);
                break;

            case stop:
                telemetry.addData("state", state);
                runMotors(0,0,0,0);
                break;
        }
        telemetry.addData("Blue: true Red: false ", knock);
        telemetry.addData("Amount Blue:", colorSensor.blue());
        telemetry.addData("Amount Red:", colorSensor.red());
        telemetry.addData("state", state);
        // Telemetry();,k
    }
}
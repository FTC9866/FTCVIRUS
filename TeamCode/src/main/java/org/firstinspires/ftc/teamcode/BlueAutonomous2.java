package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;


@Autonomous(name="BlueAutonomous2", group="Autonomous")

public class BlueAutonomous2 extends VirusMethods {
    enum state  {dropArm,scanJewel,knockJewelRight, knockJewelLeft, stop, goToPosition, debug, alignStraight, toCryptoBox, backOnStone, faceCryptoBox, placeGlyph, turnBackLeft, turnBackRight, turnBack, toCryptoBoxpart1, toCryptoBoxpart2, turn90, secondRam, moveUntilScanned}
    state state;
    boolean setMotor;
    boolean knock;

    public void init() {
        super.init();
        initializeIMU();
    }

    public void start() {
        super.start();
        vuforiaInit();
        lmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        cube1.setPosition(0);
        cube2.setPosition(1);
        jewelKnockerBase.setPosition(0.52);
        topGrabberClose();
        // lift.setPosition(0);
        jewelKnockerUp();
        state=state.dropArm;
    }
    @Override

    public void loop() {
        readVumark();
        updateOrientation();
        switch (state) {
            case dropArm:

                jewelKnockerDown();
                colorSensor.enableLed(true);
                waitTime(1000);
                resetEncoder();
                state=state.scanJewel;
                break;

            case scanJewel:
                if ((Math.abs(getBlue() - getRed()) > 10) && ((getBlue() / (getRed()+.01)) >= 1.5)) { //checks to see if object is more red or more blue
                    knock = true;
                    state = state.knockJewelLeft;
                } else if ((Math.abs(getBlue() - getRed()) > 10) && ((getRed() / (getBlue()+.01)) >= 1.5)) {
                    knock = false;
                    state = state.knockJewelRight;
                } else if (elapsedCounter.milliseconds() >= 50) {
                    elapsedCounter.reset();
                    jewelKnockerBase.setPosition(jewelKnockerBase.getPosition() + 0.005);
                }
                break;

            case knockJewelLeft:
                jewelKnockerBase.setPosition(.25);
                waitTime(500);
                jewelKnockerUp();
                state = state.turnBack;
                break;

            case knockJewelRight:
                jewelKnockerBase.setPosition(.75);
                waitTime(500);
                jewelKnockerUp();
                state = state.turnBack;
                break;

            case turnBack:
                jewelKnockerBase.setPosition(0.52);
                waitTime(500);
                position = lmotor0.getCurrentPosition();
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
                    state = state.toCryptoBoxpart1;
                }
                break;

            case backOnStone: // broken plz fix
                if (setMotorPositions(0,0,0,0, .5)){
                    resetEncoder();
                    state=state.toCryptoBox;
                }
                break;

            case toCryptoBoxpart1:
                lift(2000); //so that cube doesn't drag on ground
                if (setMotorPositionsINCH(-24.5-amountMovedForward,-24.5-amountMovedForward,-24.5-amountMovedForward,-24.5-amountMovedForward,-.5)){
                    resetEncoder();
                    state = state.turn90;
                }
                break;
            case turn90:
                if (turn(90,0.5)){
                    resetEncoder();
                    state = state.toCryptoBoxpart2;
                }
                break;

            case toCryptoBoxpart2:
                // lift(0.03); //so that cube doesn't drag on ground
                //change the motor position values as needed after testing on field
                if (VuMarkStored == RelicRecoveryVuMark.LEFT){
                    if (setMotorPositionsINCH(-5.5,-5.5,-5.5,-5.5, -.5)){
                        resetEncoder();
                        telemetry.addData("reee", "e");
                        state=state.faceCryptoBox;
                    }
                }
                else if (VuMarkStored == RelicRecoveryVuMark.CENTER){
                    if (setMotorPositionsINCH(-12.4,-12.4,-12.4,-12.4, -.5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }
                else if (VuMarkStored == RelicRecoveryVuMark.RIGHT){
                    if (setMotorPositionsINCH(-20.5,-20.5,-20.5,-20.5, -.5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }else { //just in case of some weird circumstance that it forgets the VuMark
                    if (setMotorPositionsINCH(-12,-12,-12,-12,.5)){ //parks in safe zone in front of cryptobox
                        resetEncoder();
                        state = state.stop;
                    }
                }
                break;
            case faceCryptoBox:
                if (turn(180,.75)) {
                    resetEncoder();
                    state=state.placeGlyph;
                }
                break;
            case placeGlyph:
                runMotors(0.3,0.3,0.3,0.3);
                waitTime(1000);
                runMotors(0,0,0,0);
                topGrabberOpen(true);
                waitTime(1000);
                runMotors(-0.3,-0.3,-0.3,-0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                topGrabberOpen(false);
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
        telemetry.addData("state", state);
        // Telemetry();,k
    }
}
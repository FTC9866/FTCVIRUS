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


@Autonomous(name="RedAutonomous1", group="Autonomous")

public class RedAutonomous1 extends VirusMethods {
    enum state  {dropArm,scanJewel,knockJewelRight, knockJewelLeft, stop, goToPosition, debug, alignStraight, toCryptoBox, backOnStone, faceCryptoBox, placeGlyph, turnBackLeft, turnBackRight, moveUnitlScanned}
    BlueAutonomous1.state state;
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
        //lift(liftPosition);

        switch (state) {
            case dropArm:

                jewelKnockerDown();
                waitTime(1000);
                resetEncoder();
                state = state.scanJewel;
                elapsedCounter.reset();
                break;

            case scanJewel:
                if ((Math.abs(getBlue() - getRed()) > 30) && ((getBlue() / (getRed()+.01)) > 1.5)) { //checks to see if object is more red or more blue
                    knock = true;
                    finalBlue = getBlue();
                    finalRed = getRed();
                    state = state.knockJewelRight;
                } else if ((Math.abs(getBlue() - getRed()) > 30) && ((getRed() / (getBlue()+.01)) > 1.5)) {
                    knock = false;
                    finalBlue = getBlue();
                    finalRed = getRed();
                    state = state.knockJewelLeft;
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
                    amountMovedForward = (lmotor0.getCurrentPosition()-position)*inPerPulse; //how many inches it moved forward to scan the vision target
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
                liftPosition = (2200/3);
                lift(liftPosition);
                if (VuMarkStored == RelicRecoveryVuMark.LEFT){
                    if (setMotorPositionsINCH(45.5-amountMovedForward,45.5-amountMovedForward,45.5-amountMovedForward,45.5-amountMovedForward, .5)){ //amountMovedForward subtracted to remove the amount of space moved forward to scan vision target
                        resetEncoder();
                        telemetry.addData("reee", "e");
                        state=state.faceCryptoBox;
                    }
                }else if (VuMarkStored == RelicRecoveryVuMark.CENTER){
                    if (setMotorPositionsINCH(39.5-amountMovedForward,39.5-amountMovedForward,39.5-amountMovedForward,39.5-amountMovedForward, .5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }else if (VuMarkStored == RelicRecoveryVuMark.RIGHT){
                    if (setMotorPositionsINCH(32.5-amountMovedForward,32.5-amountMovedForward,32.5-amountMovedForward,32.5-amountMovedForward, .5)){
                        resetEncoder();
                        state=state.faceCryptoBox;
                    }
                }else { //just in case of some weird circumstance that it forgets the VuMark
                    if (setMotorPositionsINCH(36,36,36,36,.5)){ //parks in safe zone in front of cryptobox
                        resetEncoder();
                        state = state.stop;
                    }
                }
                break;
            case faceCryptoBox:
                if (turn(90,1)) {
                    resetEncoder();
                    state=state.placeGlyph;
                }
                break;
            case placeGlyph:
                liftPosition = 50;
                lift(liftPosition);
                if(setMotorPositionsINCH(7,7,7,7,.5)){
                    topGrabberOpen(true);
                    if (deltaT.seconds()<60){
                        state = state.backUp;
                        resetEncoder();
                    }else{
                        state = state.stop;
                    }
                }
                /*runMotors(0.3,0.3,0.3,0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                topGrabberOpen(true);
                waitTime(400);
                runMotors(-0.3,-0.3,-0.3,-0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                topGrabberOpen(false);
                lift(0);
                if (deltaT.seconds()<60){
                    state = state.backUp;
                }else{
                    state = state.stop;
                }*/
                break;
            case secondRam:
                waitTime(400);
                runMotors(0.3,0.3,0.3,0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                waitTime(400);
                runMotors(-0.3,-0.3,-0.3,-0.3);
                waitTime(400);
                runMotors(0,0,0,0);
                if (deltaT.seconds()<50){
                    state = state.backUp;
                    resetEncoder();
                }else{
                    state = state.stop;
                }

                break;
            case backUp:
                if (setMotorPositionsINCH(-12,-12,-12,-12,-0.5)){
                    resetEncoder();
                    topGrabberOpen(false);
                    liftPosition = 10;
                    lift(liftPosition);
                    state = state.turnAround;
                }
                break;
            case turnAround:
                if (turn(270,0.5)){
                    grabberLeft.setPosition(1);
                    grabberRight.setPosition(0);
                    grabberLeftSpin.setPower(-1);
                    grabberRightSpin.setPower(1);
                    waitTime(1000);
                    state = state.grab;
                    resetEncoder();
                }
                break;
            case grab:
                if (setMotorPositionsINCH(36,36,36,36,0.5)){
                    cube3.setPosition(.6);
                    cube4.setPosition(.4);
                    liftPosition = (2200/3);
                    lift(liftPosition);
                    state = state.backUp2;
                    resetEncoder();
                }  //back up, retract spinners, turn around, drop off
                break;
            case backUp2:
                grabberLeftSpin.setPower(0);
                grabberRightSpin.setPower(0);
                if (setMotorPositionsINCH(-24,-24,-24,-24,-0.5)){
                    grabberLeft.setPosition(0);
                    grabberRight.setPosition(1);
                    state = state.turnAround2;
                    resetEncoder();
                }
                break;
            case turnAround2:
                if (turn(90,1)){
                    state = state.insertCube2;
                    resetEncoder();
                }
                break;
            case insertCube2:
                if (setMotorPositionsINCH(24,24,24,24,0.5)){
                    grabberLeft.setPosition(0);
                    grabberRight.setPosition(1);
                    state = state.placeGlyph;
                    resetEncoder();
                }
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
        telemetry.addData("Final Blue:", finalBlue);
        telemetry.addData("Final Red:", finalRed);
        telemetry.addData("Blue: true Red: false ", knock);
        telemetry.addData("state", state);
        // Telemetry();,k
    }
}
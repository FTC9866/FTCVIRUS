package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by mzhang on 11/12/2017.
 */

public abstract class VirusHardware extends OpMode {
    public ElapsedTime deltaT = new ElapsedTime();
    int initialRed=0;
    boolean autoBalance=false;
    double initialRoll;
    double initialPitch;
    int initialBlue=0;
    int liftPosition=0;
    DcMotor rmotor0; //0 is the front
    DcMotor lmotor0;
    DcMotor rmotor1;
    DcMotor lmotor1;
    DcMotor rightLED;
    DcMotor leftLED;
    //DcMotor glyphSlide;
    DcMotor liftRight;
    DcMotor liftLeft;
    DcMotor relicSlide;
    final double inPerPulse=.0175; //experimentally determined value
    Servo jewelKnockerBase;
    Servo jewelKnocker;
    Servo cube1;
    Servo cube2;
    Servo cube3;
    Servo cube4;
    Servo relicArm;
    Servo relicClaw;
    Servo grabberLeft;
    Servo grabberRight;
    CRServo grabberLeftSpin;
    CRServo grabberRightSpin;
    ColorSensor colorSensor;
    ElapsedTime elapsedCounter;
    double maxPower=1;
    double maxSteerPower=1;
    double lefty;
    double leftx;
    double righty;
    double rightx;
    double rtrigger;
    double ltrigger;
    double var1;
    double var2;

    public static final String TAG = "Vuforia VuMark Sample";
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    int cameraMonitorViewId;
    VuforiaTrackables relicTrackables;
    VuforiaTrackable relicTemplate;
    RelicRecoveryVuMark vuMark;
    OpenGLMatrix pose;
    RelicRecoveryVuMark VuMarkStored;
    public ElapsedTime mRunTime = new ElapsedTime();

    BNO055IMU imu;
    Orientation Orientation = new Orientation(AxesReference.EXTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES,0,0,0,0);

    public void init(){
        elapsedCounter = new ElapsedTime();
        lmotor0 = hardwareMap.dcMotor.get("lmotor0");
        rmotor0 = hardwareMap.dcMotor.get("rmotor0");
        lmotor1 = hardwareMap.dcMotor.get("lmotor1");
        rmotor1 = hardwareMap.dcMotor.get("rmotor1");
        //glyphSlide = hardwareMap.dcMotor.get("glyphSlide");
        liftRight = hardwareMap.dcMotor.get("liftRight");
        liftLeft = hardwareMap.dcMotor.get("liftLeft");
        liftRight.setDirection(DcMotor.Direction.REVERSE);
        lmotor0.setDirection(DcMotor.Direction.REVERSE);
        lmotor1.setDirection(DcMotor.Direction.REVERSE);
        lmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rmotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rmotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        cube1 = hardwareMap.servo.get("cube1");
        cube2 = hardwareMap.servo.get("cube2");
        cube3 = hardwareMap.servo.get("cube3");
        cube4 = hardwareMap.servo.get("cube4");
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        jewelKnocker = hardwareMap.servo.get("jewelKnocker");
        jewelKnockerBase = hardwareMap.servo.get("jewelKnockerBase");
        colorSensor = hardwareMap.get(AdafruitI2cColorSensor.class, "colorSensor");
        relicArm = hardwareMap.servo.get("relicArm");
        relicClaw = hardwareMap.servo.get("relicClaw");
        relicSlide = hardwareMap.dcMotor.get("relicSlide");
        grabberLeft = hardwareMap.servo.get("grabberLeft");
        grabberRight = hardwareMap.servo.get("grabberRight");
        grabberLeftSpin = hardwareMap.crservo.get("grabberLeftSpin");
        grabberRightSpin = hardwareMap.crservo.get("grabberRightSpin");
    }
    public void start(){
        //initialBlue=colorSensor.blue();
        //initialRed=colorSensor.red();
        initialPitch=Orientation.secondAngle;
        initialRoll=Orientation.thirdAngle;
        deltaT.reset();
    }



}
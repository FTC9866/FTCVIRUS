package org.firstinspires.ftc.teamcode;

import android.view.OrientationEventListener;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;

public abstract class VirusMethods extends VirusHardware{
    boolean mode;
    double finalRed;
    double finalBlue;
    int counter=0;
    double position;
    double amountMovedForward;
    double turnRate;
    double angleRel;
    boolean patternFound;
    double maxDisplacement;
    double threshold = .5;
    boolean triggered;
    int cryptoboxSection;
    ElapsedTime elapsedTime = new ElapsedTime();
    String[][]cryptobox = {{"brown","gray","gray"},{"brown","brown","gray"},{"gray","brown","brown"},{"gray","gray","brown"}};
    public void runMotors(double Left0, double Left1, double Right0, double Right1, double steerMagnitude){
        if (Left0!=0&&Left1!=0&&Right0!=0&&Right1!=0) {
            steerMagnitude *= 2 * Math.max(Math.max(Left0, Left1), Math.max(Right0, Right1));
        }
        Left0=Left0+maxSteerPower*steerMagnitude;
        Left1=Left1+maxSteerPower*steerMagnitude;
        Right0=Right0-maxSteerPower*steerMagnitude;
        Right1=Right1-maxSteerPower*steerMagnitude;
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);
        rmotor0.setPower(Right0);
        rmotor1.setPower(Right1);
        lmotor0.setPower(Left0);
        lmotor1.setPower(Left1);
    }

    public void runMotors(double Left0, double Left1, double Right0, double Right1){
        lmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rmotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);
        rmotor0.setPower(Right0);
        rmotor1.setPower(Right1);
        lmotor0.setPower(Left0);
        lmotor1.setPower(Left1);
    }
    public void runMotorsAuto(double Left0, double Left1, double Right0, double Right1){
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);
        rmotor0.setPower(Right0);
        rmotor1.setPower(Right1);
        lmotor0.setPower(Left0);
        lmotor1.setPower(Left1);
    }
    public boolean setMotorPositions(int Left0, int Left1, int Right0, int Right1, double power) {
        if (counter == 0) { //makes sure this is only run once, reset back to 0 when OpMode starts or resetEncoders is called
            lmotor0.setTargetPosition(Left0);
            lmotor1.setTargetPosition(Left1);
            rmotor0.setTargetPosition(Right0);
            rmotor1.setTargetPosition(Right1);
            runMotorsAuto(power, power, power, power);
            counter++;
        }
        return !lmotor0.isBusy() && !lmotor1.isBusy() && !rmotor0.isBusy() && !rmotor1.isBusy(); //returns true when motors are not busy
    }

    public boolean setMotorPositionsINCH(double Left0, double Left1, double Right0, double Right1, double power){
        lmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (counter == 0){ //makes sure this is only run once, reset back to 0 when OpMode starts or resetEncoders is called
            lmotor0.setTargetPosition((int)(Left0/inPerPulse));
            lmotor1.setTargetPosition((int)(Left1/inPerPulse));
            rmotor0.setTargetPosition((int)(Right0/inPerPulse));
            rmotor1.setTargetPosition((int)(Right1/inPerPulse));
            runMotorsAuto(power,power,power,power);
            counter++;
        }
        return (!lmotor0.isBusy() && !lmotor1.isBusy() && !rmotor0.isBusy() && !rmotor1.isBusy()); //returns true when motors are not busy
    }

    public boolean turn(double angle, double speed){
        angle=360-angle;
        double currentAngle = getZHeading();
        angleRel = relativeAngle(angle, currentAngle); //should be distance from current angle (negative if to the counterclockwise, positive if to the clockwise)
        turnRate = speed*angleRel/60;
        if (turnRate<0){
            turnRate-=.025;
        }
        else if(turnRate>0){
            turnRate+=.025;
        }
        runMotors(turnRate, turnRate, -turnRate, -turnRate); //negative turnRate will result in a left turn
        if (angleRel<=threshold && angleRel>=-threshold) { //approaching from either side
            return true;
        }
        return false;
    }
    public void setThreshold(double newThreshold){
        threshold = newThreshold;
    }
    private double relativeAngle(double angle, double currentAngle){
        double currentAngleRel = angle-currentAngle;
        if (currentAngleRel > 180){
            currentAngleRel -= 360;
        }else if (currentAngle < -179){
            currentAngleRel += 360;
        }
        return currentAngleRel;
    }


    public void resetEncoder(){
        runMotors(0,0,0,0);
        lmotor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lmotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rmotor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rmotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        counter=0; // sets counter = 0 for setMotorPosition method
        while (lmotor0.isBusy()||lmotor1.isBusy()||rmotor0.isBusy()||rmotor1.isBusy()); //waits until encoders finish reset
        lmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rmotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void waitTime(int time){
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateControllerValues(){
        lefty = -gamepad1.left_stick_y;
        leftx = -gamepad1.left_stick_x;
        righty = -gamepad1.right_stick_y;
        rightx = -gamepad1.right_stick_x;
        rtrigger = -gamepad1.right_trigger;
        ltrigger = -gamepad1.left_trigger;
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(lefty*lefty+leftx*leftx);
        var1= (lefty-leftx)*magnitude/scalar;
        var2= (lefty+leftx)*magnitude/scalar;
        var1*=maxPower;
        var2*=maxPower;
    }
    public void vuforiaInit(){
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AQmuIUP/////AAAAGR6dNDzwEU07h7tcmZJ6YVoz5iaF8njoWsXQT5HnCiI/oFwiFmt4HHTLtLcEhHCU5ynokJgYSvbI32dfC2rOvqmw81MMzknAwxKxMitf8moiK62jdqxNGADODm/SUvu5a5XrAnzc7seCtD2/d5bAIv1ZuseHcK+oInFHZTi+3BvhbUyYNvnVb0tQEAv8oimzjiQW18dSUcEcB/d6QNGDvaDHpxuRCJXt8U3ShJfBWWQEex0Vp6rrb011z8KxU+dRMvGjaIy+P2p5GbWXGJn/yJS9oxuwDn3zU6kcQoAwI7mUgAw5zBGxxM+P35DoDqiOja6ST6HzDszHxClBm2dvTRP7C4DEj0gPkhX3LtBgdolt";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        relicTrackables.activate();
    }

    public void lift(double position) {
        //liftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //liftLeft.setPower(-1);
        liftRight.setPower(-1);
        //liftLeft.setTargetPosition((int) position);
        liftRight.setTargetPosition((int) position);
        if ((!liftRight.isBusy())) {
            liftRight.setPower(0);
        }
    }
    public void liftPower(double power){
        liftRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftRight.setPower(power);
    }
    public void relicSlide(int position){
        relicSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        relicSlide.setPower(-1);
        relicSlide.setTargetPosition(position);
        if ((!relicSlide.isBusy())) {
            relicSlide.setPower(0);
        }
    }

    public void relicSlidePower(double power){
        relicSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        relicSlide.setPower(power);
    }
    public void topGrabberOpen(boolean semi){
        if(semi){
            cube3.setPosition(.7);
            cube4.setPosition(.2);
        }else{
            cube3.setPosition(.6);
            cube4.setPosition(.3);
        }

    }
    public void topGrabberClose(){
        cube3.setPosition(.8);
        cube4.setPosition(.1);
    }
    public void bottomGrabberOpen(boolean semi){
        if(semi){
            cube1.setPosition(.5);
            cube2.setPosition(.45);
        }else{
            cube1.setPosition(.3);
            cube2.setPosition(.67);
        }

    }
    public void bottomGrabberClose(){
        cube1.setPosition(.55);
        cube2.setPosition(.4);
    }
    public String GPS(boolean bottom){
        int bottomx;
        int bottomy;
        if (cryptoboxSection==0){
            bottomx = 0;
            bottomy = 3;
        }else if (cryptoboxSection==1){
            bottomx = 0;
            bottomy = 1;
        }else if (cryptoboxSection==2){
            bottomx = 1;
            bottomy = 3;
        }else if (cryptoboxSection==3){
            bottomx = 1;
            bottomy = 1;
        }else if (cryptoboxSection==4){
            bottomx = 2;
            bottomy = 3;
        }else{
            bottomx = 2;
            bottomy = 1;
        }
        if (bottom){
            return cryptobox[bottomy][bottomx]; //returns block for bottom grabber
        }else{
            return cryptobox[bottomy-1][bottomx]; //returns block for top grabber
        }
    }
    public String cryptoboxLocation(){
        if (cryptoboxSection==0 || cryptoboxSection==1){
            return "Left";
        }else if (cryptoboxSection==2||cryptoboxSection==3){
            return "Center";
        }else{
            return "Right";
        }
    }
    public void jewelKnockerUp(){
        jewelKnocker.setPosition(.5);
    }
    public void jewelKnockerDown(){
        jewelKnocker.setPosition(0.66);
    }
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
    public void readVumark(){
        vuMark = RelicRecoveryVuMark.from(relicTemplate);
        pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
        if (pose != null) {
            VectorF trans = pose.getTranslation();
            Orientation rot = Orientation.getOrientation(pose, AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

            // Extract the X, Y, and Z components of the offset of the target relative to the robot
            double tX = trans.get(0);
            double tY = trans.get(1);
            double tZ = trans.get(2);

            // Extract the rotational components of the target relative to the robot
            double rX = rot.firstAngle;
            double rY = rot.secondAngle;
            double rZ = rot.thirdAngle;
        }
    }

    public void initializeIMU(){
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
    }

    public double getZHeading(){
        //Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
       /* if(Orientation.firstAngle<0){
            return Orientation.firstAngle*-1;
        }else if(Orientation.firstAngle>0){
            return 360-Orientation.firstAngle;
        }
        else{
            return 0;
        }*/
        if(Orientation.firstAngle<0){
            return 360+Orientation.firstAngle;
        }else if(Orientation.firstAngle>0){
            return Orientation.firstAngle;
        }
        else{
            return 0;
        }
    }
    public double getRawZHeading(){
        return Orientation.firstAngle;
    }

    public double getPitch(){
        //Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return Orientation.secondAngle-initialPitch;
    }
    public double getRoll(){
        //Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return Orientation.thirdAngle-initialRoll;
    }
    public double getRed(){return colorSensor.red();}
    public double getBlue(){return colorSensor.blue()+20;}

    public void Telemetry(){
        telemetry.addData("Red",colorSensor.red());
        telemetry.addData("Green",colorSensor.green());
        telemetry.addData("Blue",colorSensor.blue());

        telemetry.addData("lMotor0 Encoder",lmotor0.getCurrentPosition());
        telemetry.addData("lMotor1 Encoder",lmotor1.getCurrentPosition());
        telemetry.addData("rMotor0 Encoder",rmotor0.getCurrentPosition());
        telemetry.addData("rMotor1 Encoder",rmotor1.getCurrentPosition());

        telemetry.addData("lMotor0 Target",lmotor0.getTargetPosition());
        telemetry.addData("lMotor1 Target",lmotor1.getTargetPosition());
        telemetry.addData("rMotor0 Target",rmotor0.getTargetPosition());
        telemetry.addData("rMotor1 Target",rmotor1.getTargetPosition());
    }
    public void updateOrientation (){
        Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES);
    }

    public void snap90() {

        int snappedAngle = (int) (this.getZHeading() / 45);

        if (snappedAngle % 2 != 0) {
            snappedAngle++;
        }

        snappedAngle *= 45;

        turn(snappedAngle, 0.5);
    }

    public void balance(){
        final double constant = -0.014;
        double left0 = getRoll()*constant, left1 = getRoll()*constant, right0 = getRoll()*constant, right1 = getRoll()*constant;
        runMotors(left0,left1,right0,right1);
    }

}
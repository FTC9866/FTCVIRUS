package org.firstinspires.ftc.teamcode;



/**
 * Created by shreshre on 2/2/2018.
 */

public class BalanceTester extends VirusMethods {
    public void init(){
       super.init();
        initializeIMU();
        while (!imu.isSystemCalibrated());
    }
    public void loop(){
        updateOrientation();
        balance();
    }
}

package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by shreshre on 2/2/2018.
 */
@TeleOp(name="BalanceTester", group="TeleOp")

public class BalanceTester extends VirusMethods {
    public void init(){
       super.init();
        initializeIMU();
    }
    public void loop(){
        updateOrientation();
        balance();
    }
}

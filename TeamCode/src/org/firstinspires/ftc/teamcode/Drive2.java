package org.firstinspires.ftc.teamcode;

//import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
//import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.util.HashMap;

public class Drive2 {
    String TAG = "Drive";
    public DcMotor leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive = null;
    private DcMotor Sweep;
    LinearOpMode opMode;
    Telemetry telemetry;
    public HardwareMap hardwareMap; // will be set in OpModeManager.runActiveOpMode
    private ElapsedTime runtime = new ElapsedTime();
    static final double     COUNTS_PER_MOTOR_REV    = 28 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 40 ;     // This is < 1.0 if geared UP //For test robot
    //static final double     DRIVE_GEAR_REDUCTION    = 19.2 ;     // This is < 1.0 if geared UP //For competition robot
    static final double     WHEEL_DIAMETER_INCHES   = 3 ;     // For figuring circumference  //For test robot
    //static final double     WHEEL_DIAMETER_INCHES   = 3.75 ;     // For figuring circumference //For competition robot
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 1;
    static final double     TURN_SPEED              = 0.5;

    HashMap <DcMotor, Integer> motorInitialPositions, motorTargetPositions;
    HashMap <DcMotor, Double> motorPowerFactors;

    static BNO055IMU        imu = null;
    static double imuSecondOpModeAdjustment = 0;
    Orientation lastAngles = new Orientation();

    public Drive2(LinearOpMode _opMode){
        opMode = _opMode;
        hardwareMap = opMode.hardwareMap;
        telemetry = opMode.telemetry;

        motorPowerFactors = new HashMap<>();
    }

    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        //robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        //telemetry.addData("Say", "Hello Driver");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = opMode.hardwareMap.get(DcMotor.class, "front_left_motor");  //For test robot
        rightFrontDrive = opMode.hardwareMap.get(DcMotor.class, "front_right_motor");
        leftBackDrive = opMode.hardwareMap.get(DcMotor.class, "back_left_motor");
        rightBackDrive = opMode.hardwareMap.get(DcMotor.class, "back_right_motor");
//        leftFrontDrive  = opMode.hardwareMap.get(DcMotor.class, "LM DT");  //For Competition robot
//        rightFrontDrive = opMode.hardwareMap.get(DcMotor.class, "RM DT");
//        leftBackDrive = opMode.hardwareMap.get(DcMotor.class, "LR DT");
//        rightBackDrive = opMode.hardwareMap.get(DcMotor.class, "RR DT");

        //Sweep = hardwareMap.get(DcMotor.class, "Sweep");  //For Competition robot
        //Sweep.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        if(imu == null) {
            telemetry.log().add("Drive Init: IMU Null; Initializing");
            telemetry.update();
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            //parameters.mode                = BNO055IMU.SensorMode.GYRONLY;
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.loggingEnabled = true;
            parameters.loggingTag = "IMU";
            //parameters.calibrationDataFile = "IMUCalibration.json"; // see the calibration sample opmode

            // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
            // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
            // and named "imu".
            imu = hardwareMap.get(BNO055IMU.class, "imu");

            imu.initialize(parameters);
            // make sure the imu gyro is calibrated before continuing.
/*            while (!opMode.isStopRequested() && !imu.isGyroCalibrated()) {
                opMode.sleep(50);
                opMode.idle();
            }
            telemetry.log().add("Drive: init: Gyro calibrated");
            telemetry.update();

            // make sure the imu gyro is calibrated before continuing.
            long startMillis = System.currentTimeMillis();
            while (!opMode.isStopRequested() && !imu.isAccelerometerCalibrated() && System.currentTimeMillis() - startMillis < 5000) {
                opMode.sleep(50);
                opMode.idle();
            }
            if(System.currentTimeMillis() - startMillis > 5000)
                telemetry.log().add("Drive: init: Accel calibration timed out");
            else
                telemetry.log().add("Drive: init: Accel calibrated");*/
            telemetry.update();
            imuSecondOpModeAdjustment = 0;
        } else {
            opMode.telemetry.log().add("Drive Init: IMU already initialized");
            //imuSecondOpModeAdjustment = -2.75;
            //imuSecondOpModeAdjustment = getImuAngle();
            imuSecondOpModeAdjustment = 0;
        }
        setTargetAngle(0);
//        opMode.telemetry.log().add("Drive: init: IMU: status: %s, calibr: %s", imu.getSystemStatus().toString(), imu.getCalibrationStatus().toString());
//        telemetry.update();
//        Log.i(TAG, "init: IMU status: " + imu.getSystemStatus());
//        Log.i(TAG, "init: IMU calibration status: " + imu.getCalibrationStatus());
    }

    public void vroom_vroom (double magRight, double thetaRight, double magLeft, double thetaLeft) {
        double rightFrontPowerFactor, leftFrontPowerFactor, rightBackPowerFactor, leftBackPowerFactor;
        double pi = Math.PI;

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path",  "Starting at %7d : %7d",
                leftFrontDrive.getCurrentPosition(),
                rightFrontDrive.getCurrentPosition());
        telemetry.update();

        // reset the timeout time and start motion.
        runtime.reset();

        if(thetaRight > 0 && thetaRight < pi/2){
            rightFrontPowerFactor = -Math.cos(2 * thetaRight);
        }else if(thetaRight >= -pi && thetaRight < -pi/2){
            rightFrontPowerFactor = Math.cos(2 * thetaRight);
        }else if(thetaRight >= pi/2 && thetaRight <= pi){
            rightFrontPowerFactor = 1;
        }else{
            rightFrontPowerFactor = -1;
        }

        if(thetaLeft > 0 && thetaLeft < pi/2) {
            leftBackPowerFactor = -Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= -pi && thetaLeft < -pi/2){
            leftBackPowerFactor = Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= pi/2 && thetaLeft <= pi){
            leftBackPowerFactor = 1;
        }else{
            leftBackPowerFactor = -1;
        }

        if(thetaRight > -pi/2 && thetaRight < 0) {
            rightBackPowerFactor = Math.cos(2 * thetaRight);
        }else if(thetaRight > pi/2 && thetaRight < pi){
            rightBackPowerFactor = -Math.cos(2 * thetaRight);
        }else if(thetaRight >= 0 && thetaRight <= pi/2){
            rightBackPowerFactor = 1;
        }else{
            rightBackPowerFactor = -1;
        }

        if(thetaLeft > -pi/2 && thetaLeft < 0) {
            leftFrontPowerFactor = Math.cos(2 * thetaLeft);
        }else if(thetaLeft > pi/2 && thetaLeft < pi){
            leftFrontPowerFactor = -Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= 0 && thetaLeft <= pi/2){
            leftFrontPowerFactor = 1;
        }else{
            leftFrontPowerFactor = -1;
        }

        motorPowerFactors.put(leftFrontDrive, leftFrontPowerFactor);
        motorPowerFactors.put(leftBackDrive, leftBackPowerFactor);
        motorPowerFactors.put(rightFrontDrive, rightFrontPowerFactor);
        motorPowerFactors.put(rightBackDrive, rightBackPowerFactor);

        //Speeds speeds = getPhiSpeeds((magLeft + magRight)/2);
        setMotorPowers(magLeft, magRight);

    }

    public void vroom_vroom_phi (double targetSpeed, double targetTheta) {
        double rightFrontPowerFactor, leftFrontPowerFactor, rightBackPowerFactor, leftBackPowerFactor;
        double pi = Math.PI;
        double thetaRight = targetTheta;
        double thetaLeft = targetTheta;

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path",  "Starting at %7d : %7d",
                leftFrontDrive.getCurrentPosition(),
                rightFrontDrive.getCurrentPosition());
        telemetry.update();

        // reset the timeout time and start motion.
        runtime.reset();

        if(thetaRight > 0 && thetaRight < pi/2){
            rightFrontPowerFactor = -Math.cos(2 * thetaRight);
        }else if(thetaRight >= -pi && thetaRight < -pi/2){
            rightFrontPowerFactor = Math.cos(2 * thetaRight);
        }else if(thetaRight >= pi/2 && thetaRight <= pi){
            rightFrontPowerFactor = 1;
        }else{
            rightFrontPowerFactor = -1;
        }

        if(thetaLeft > 0 && thetaLeft < pi/2) {
            leftBackPowerFactor = -Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= -pi && thetaLeft < -pi/2){
            leftBackPowerFactor = Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= pi/2 && thetaLeft <= pi){
            leftBackPowerFactor = 1;
        }else{
            leftBackPowerFactor = -1;
        }

        if(thetaRight > -pi/2 && thetaRight < 0) {
            rightBackPowerFactor = Math.cos(2 * thetaRight);
        }else if(thetaRight > pi/2 && thetaRight < pi){
            rightBackPowerFactor = -Math.cos(2 * thetaRight);
        }else if(thetaRight >= 0 && thetaRight <= pi/2){
            rightBackPowerFactor = 1;
        }else{
            rightBackPowerFactor = -1;
        }

        if(thetaLeft > -pi/2 && thetaLeft < 0) {
            leftFrontPowerFactor = Math.cos(2 * thetaLeft);
        }else if(thetaLeft > pi/2 && thetaLeft < pi){
            leftFrontPowerFactor = -Math.cos(2 * thetaLeft);
        }else if(thetaLeft >= 0 && thetaLeft <= pi/2){
            leftFrontPowerFactor = 1;
        }else{
            leftFrontPowerFactor = -1;
        }

        motorPowerFactors.put(leftFrontDrive, leftFrontPowerFactor);
        motorPowerFactors.put(leftBackDrive, leftBackPowerFactor);
        motorPowerFactors.put(rightFrontDrive, rightFrontPowerFactor);
        motorPowerFactors.put(rightBackDrive, rightBackPowerFactor);

        SpeedsPhi speedsPhi = getPhiSpeeds(targetSpeed, motorPowerFactors);
        setMotorPowersPhi(speedsPhi);
    }


    //               ^
    //               |  Y axis
    //               |
    //    ---------------------
    //    |        Front      |
    //    |                   |
    //    |                   |   X axis
    //    |        Robot      |----------->
    //    |                   |
    //    |      top view     |
    //    ---------------------
    public void vroomVroomMonitorTicks(double speed, double xInches, double yInches, double timeout) {
        //Borrowed Holonomic robot navigation ideas from https://www.bridgefusion.com/blog/2019/4/10/robot-localization-dead-reckoning-in-first-tech-challenge-ftc
        //    Robot Localization -- Dead Reckoning in First Tech Challenge (FTC)
        double theta = Math.atan2(yInches, xInches);
        double magnitude = Math.hypot(xInches, yInches);
        int tickCountPriorLeftFront = leftFrontDrive.getCurrentPosition(), tickCountPriorLeftBack = leftBackDrive.getCurrentPosition();
        int tickCountPriorRightFront = rightFrontDrive.getCurrentPosition(), tickCountPriorRightBack = rightBackDrive.getCurrentPosition();
        int ticksTraveledLeftFront = 0, ticksTraveledLeftBack = 0, ticksTraveledRightFront = 0, ticksTraveledRightBack = 0;
        double inchesTraveledX = 0, inchesTraveledY = 0, inchesTraveledTotal = 0, rotationInchesTotal = 0;
        double cycleMillisNow = 0, cycleMillisPrior = System.currentTimeMillis(), cycleMillisDelta, startMillis = System.currentTimeMillis();
        //vroom_vroom(speed, theta, speed, theta);
        getImuAngle();
        vroom_vroom_phi(speed, theta);
        adjustThetaInit();
        //setTargetAngle(mImuCalibrationAngle);
        while (opMode.opModeIsActive() && runtime.seconds() < timeout && inchesTraveledTotal <= magnitude){
            int tickCountNowLeftFront = leftFrontDrive.getCurrentPosition();
            int tickCountNowLeftBack = leftBackDrive.getCurrentPosition();
            int tickCountNowRightFront = rightFrontDrive.getCurrentPosition();
            int tickCountNowRightBack = rightBackDrive.getCurrentPosition();
            int deltaTicksLeftFront = tickCountNowLeftFront - tickCountPriorLeftFront;
            int deltaTicksLeftBack = tickCountNowLeftBack - tickCountPriorLeftBack;
            int deltaTicksRightFront = tickCountNowRightFront - tickCountPriorRightFront;
            int deltaTicksRightBack = tickCountNowRightBack - tickCountPriorRightBack;
            ticksTraveledLeftFront += deltaTicksLeftFront;
            ticksTraveledLeftBack += deltaTicksLeftBack;
            ticksTraveledRightFront += deltaTicksRightFront;
            ticksTraveledRightBack += deltaTicksRightBack;
            double leftFrontInchesDelta = deltaTicksLeftFront / COUNTS_PER_INCH;
            double rightFrontInchesDelta = -deltaTicksRightFront / COUNTS_PER_INCH;  //Minus sign converts to holonomic drive perspective
            double rightBackInchesDelta = -deltaTicksRightBack / COUNTS_PER_INCH;  //Minus sign converts to holonomic drive perspective
            double leftBackInchesDelta = deltaTicksLeftBack / COUNTS_PER_INCH;
            double rotationAvgInchesDelta = (leftFrontInchesDelta + rightFrontInchesDelta + rightBackInchesDelta + leftBackInchesDelta)/4;
            rotationInchesTotal += rotationAvgInchesDelta;
            double leftFrontRobotInchesDelta = leftFrontInchesDelta - rotationAvgInchesDelta;
            double rightFrontRobotInchesDelta = rightFrontInchesDelta - rotationAvgInchesDelta;
            double rightBackRobotInchesDelta = rightBackInchesDelta - rotationAvgInchesDelta;
            double leftBackRobotInchesDelta = leftBackInchesDelta - rotationAvgInchesDelta;
            double deltaInchesRobotX = (leftFrontRobotInchesDelta + rightFrontRobotInchesDelta - rightBackRobotInchesDelta - leftBackRobotInchesDelta) / (2 * Math.sqrt(2));
            double deltaInchesRobotY = (leftFrontRobotInchesDelta - rightFrontRobotInchesDelta - rightBackRobotInchesDelta + leftBackRobotInchesDelta) / (2 * Math.sqrt(2));
            double deltaInchesRobot = Math.hypot(deltaInchesRobotX, deltaInchesRobotY);
            double FUDGE_FACTOR = 36/51.0;
            inchesTraveledX += deltaInchesRobotX * FUDGE_FACTOR;
            inchesTraveledY += deltaInchesRobotY * FUDGE_FACTOR;
            inchesTraveledTotal += Math.hypot(deltaInchesRobotX * FUDGE_FACTOR, deltaInchesRobotY * FUDGE_FACTOR);

            //Provide feedback to keep robot moving in right direction based on encoder ticks
            adjustTheta(xInches, yInches, speed, inchesTraveledX, inchesTraveledY);  //Must call adjustThetaInit() before a loop with adjustTheta()

            cycleMillisNow = System.currentTimeMillis();
            cycleMillisDelta = cycleMillisNow - cycleMillisPrior;
            cycleMillisPrior = cycleMillisNow;
            telemetry.addData("Ticks Traveled (lf, lb)", "%7d, %7d", ticksTraveledLeftFront, ticksTraveledLeftBack);
            telemetry.addData("Ticks Traveled (rf, rb)", "%7d, %7d", ticksTraveledRightFront, ticksTraveledRightBack);
            telemetry.addData("In Traveled (X, Y)", "X: %.1f, Y: %.1f", inchesTraveledX, inchesTraveledY);
            telemetry.addData("In Traveled (Tot, Rot)", "%.1f, %.1f", inchesTraveledTotal,rotationInchesTotal);
            telemetry.addData("Cycle Millis:", "%4f", cycleMillisDelta);
            telemetry.update();
/*            Log.i("Drive", String.format("Ticks Traveled (lf, lb): %7d, %7d", ticksTraveledLeftFront, ticksTraveledLeftBack));
            Log.i("Drive", String.format("Ticks Traveled (rf, rb): %7d, %7d", ticksTraveledRightFront, ticksTraveledRightBack));
            Log.i("Drive", String.format("Ticks Delta (lf, lb): %7d, %7d", deltaTicksLeftFront, deltaTicksLeftBack));
            Log.i("Drive", String.format("Ticks Delta (rf, rb): %7d, %7d", deltaTicksRightFront, deltaTicksRightBack));
            Log.i("Drive", String.format("In Traveled (X, Y): X: %.2f, Y: %.2f", inchesTraveledX, inchesTraveledY));
            Log.i("Drive", String.format("In Traveled (Tot, Rot): %.2f, %.2f", inchesTraveledTotal,rotationInchesTotal));
            Log.i("Drive", String.format("Incremental Speed (in/sec): %.2f", deltaInchesRobot/cycleMillisDelta * 1000));
            Log.i("Drive", String.format("Cycle Millis: %.3f, Total Seconds: %.3f", cycleMillisDelta, (System.currentTimeMillis() - startMillis)/1000));
*/
            tickCountPriorLeftFront = tickCountNowLeftFront;
            tickCountPriorLeftBack = tickCountNowLeftBack;
            tickCountPriorRightFront = tickCountNowRightFront;
            tickCountPriorRightBack = tickCountNowRightBack;
        }
    }

    public void stopResetEncoder(){
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runUsingEncoder(){
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void ceaseMotion(){
        // Stop all motion;
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void setMotorPowers(double magLeft, double magRight){
        leftFrontDrive.setPower(motorPowerFactors.get(leftFrontDrive) * magLeft);
        rightFrontDrive.setPower(motorPowerFactors.get(leftBackDrive) * magRight);
        leftBackDrive.setPower(motorPowerFactors.get(rightFrontDrive) * magLeft);
        rightBackDrive.setPower(motorPowerFactors.get(rightBackDrive) * magRight);
    }

    public void setMotorPowersPhi(SpeedsPhi speedsPhi){
        leftFrontDrive.setPower(speedsPhi.leftFrontSpeed);
        rightFrontDrive.setPower(speedsPhi.rightFrontSpeed);
        leftBackDrive.setPower(speedsPhi.leftBackSpeed);
        rightBackDrive.setPower(speedsPhi.rightBackSpeed);
    }

    private double thetaErrorSum;
    public void adjustThetaInit() { thetaErrorSum = 0; }
    public void adjustTheta(double targetX, double targetY, double targetSpeed, double nowX, double nowY){
        if(nowX == 0 && nowY == 0 && mTargetAngle == 0){
//            Log.i("Drive", "adjustTheta: nowX and nowY are both still zero so not computing an adjustment factor yet");
            return;
        }
        //double GAIN = 0.6, THETA_ERROR_SUM_MAX = Math.PI/4/GAIN; //Max error sum is +/- 45 degrees
        double targetTheta = Math.atan2(targetY, targetX);
        double nowTheta = Math.atan2(nowY, nowX);
//        if((targetTheta - nowTheta > 0 && thetaErrorSum < THETA_ERROR_SUM_MAX) || (targetTheta - nowTheta < 0 && thetaErrorSum > -THETA_ERROR_SUM_MAX))
//            thetaErrorSum += Math.min(Math.max(targetTheta - nowTheta, -THETA_ERROR_SUM_MAX), THETA_ERROR_SUM_MAX);  //Max allowed increment is max value
//        double adjustedTargetTheta = targetTheta + GAIN * thetaErrorSum;

        //The cosine function acts as the gain so that as the error angle approaches 180 degrees, the error factor goes to zero,
        // since the motors will already be pulling in the opposite direction.
        double angleDifference = calculateAngleDifference(targetTheta, nowTheta);
        double adjustedTargetTheta = getEulerAngle(targetTheta + Math.cos(angleDifference/2) * angleDifference);
        //Speeds speeds = getSpeeds(targetSpeed, nowTheta);
//        vroom_vroom(targetSpeed, adjustedTargetTheta, targetSpeed, adjustedTargetTheta);
        //vroom_vroom(speeds.rightSpeed, adjustedTargetTheta, speeds.leftSpeed, adjustedTargetTheta);
        vroom_vroom_phi(targetSpeed, adjustedTargetTheta);
//        vroom_vroom(targetSpeed, targetTheta, targetSpeed, targetTheta);
//        Log.i("Drive", String.format("IMU angle: %.2f, adj angle: %.2f", mCurrentImuAngle, mAdjustedAngle));
//        Log.i("Drive", String.format("adjustTheta: (degrees) target: %.4f, now: %.4f, adjusted: %.4f, error: %.4f",
//                targetTheta/Math.PI*180, nowTheta/Math.PI*180, adjustedTargetTheta/Math.PI*180, thetaErrorSum/Math.PI*180));
    }

    double mCurrentImuAngle, mPriorImuAngle, mTargetAngle, mAdjustedAngle, mPriorAdjustedAngle, mImuCalibrationAngle;

    public void setTargetAngle(double targetAngle){
        mPriorImuAngle = mTargetAngle = targetAngle + imuSecondOpModeAdjustment;
    }

    public double getImuAngle(){
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES);
        double adjustedHeading = angles.firstAngle + imuSecondOpModeAdjustment;
        telemetry.addData("IMU Angles (X/Y/Z)", "%.1f / %.1f / %.1f", angles.secondAngle, angles.thirdAngle, angles.firstAngle);
        telemetry.update();
//        Log.i(TAG, String.format("getImuAngle: IMU Angles (X/Y/Z): %.1f / %.1f / %.1f", angles.secondAngle, angles.thirdAngle, angles.firstAngle));
        return mCurrentImuAngle = angles.firstAngle;
    }

    /**
     *
     * @return Angle in degrees. + = left, - = right.
     */
    public double getImuDeltaAngle(){
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        //Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES);
        double deltaAngle = (mCurrentImuAngle = getImuAngle()) - mPriorImuAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

    /**
     *
     * @return New adjusted angle used to create adjusted power levels
     */
    public double getAdjustedAngle(){
        return mAdjustedAngle = getEulerAngleDegrees(getImuAngle());
    }

    /**
     * Convert an angle such that -pi <= angle <= pi
     */
    private double getEulerAngle(double angle){
        if(angle < -Math.PI) return angle%(2*Math.PI) + 2 * Math.PI;
        else if (angle > Math.PI) return angle%(2*Math.PI) - 2 * Math.PI;
        else return angle;
    }

    /**
     * Convert an angle such that -pi <= angle <= pi
     */
    private double getEulerAngleDegrees(double angle){
        if(angle < -180) return angle%360 + 360;
        else if (angle > 180) return angle%360 - 180;
        else return angle;
    }

    /**
     * See if we are moving in a straight line and if not return a power correction value.
     * @return Power adjustment, + is adjust left - is adjust right.
     */
    private double getPowerCorrection()
    {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double angleError, powerCorrection, angle, gain;

        angle = getAdjustedAngle();  //IMU angle converted to Euler angle (IMU may already deliver Euler angles)

        angleError = mTargetAngle - angle;        // reverse sign of angle for correction.

        gain = Math.max(-0.05*Math.abs(angleError) + 0.1, .05);  //Varies from .2 around zero to .05 for errors above 10 degrees
        powerCorrection = angleError * gain;

        return powerCorrection;
    }

    /**
     * See if we are moving in a straight line and if not return an angle correction value phiCorrection.
     * @return Phi adjustment, + is rotate counter clockwise; - is rotate clockwise.
     */
    private double getPhiCorrection()
    {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double GAIN_HIGH_RANGE = 0.6, GAIN_LOW_RANGE = 2.0;
        double angleError, phiCorrection, angle, gain;

        angle = getAdjustedAngle();

        angleError = mTargetAngle - angle;        // reverse sign of angle for correction.

        gain = Math.max(-(GAIN_LOW_RANGE - GAIN_HIGH_RANGE)*Math.abs(angleError) + GAIN_LOW_RANGE,
                GAIN_HIGH_RANGE);  //Varies from 2.0 around zero to .6 for errors above 10 degrees
        phiCorrection = angleError * gain;

        return phiCorrection;
    }

    private enum Side{
        Left, Right
    }

    private class Speeds{
        public double rightSpeed;
        public double leftSpeed;
        public Speeds(double right, double left){
            rightSpeed = right;
            leftSpeed = left;
        }
    }

    private class SpeedsPhi{
        public double rightFrontSpeed;
        public double leftFrontSpeed;
        public double rightBackSpeed;
        public double leftBackSpeed;
        public SpeedsPhi(double leftFront, double leftBack, double rightFront, double rightBack){
            leftFrontSpeed = leftFront;
            leftBackSpeed = leftBack;
            rightFrontSpeed = rightFront;
            rightBackSpeed = rightBack;
        }
    }

    /**
     * Get the adjusted speeds for each side of the robot to allow it to turn enough to stay on a straight line. Only call once per cycle.
     * @param targetSpeed
     * @return
     */
    private Speeds getSpeeds(double targetSpeed, double nowAngle){
        int sign = nowAngle > 0 ? 1 : -1;
        double powerCorrection = getPowerCorrection() * sign;
        //double powerCorrection = 0;
        double adjustedLeftSpeed, adjustedRightSpeed;
        if(targetSpeed + powerCorrection > 1){
            adjustedRightSpeed = 1;
            adjustedLeftSpeed = 1 - 2 * powerCorrection; // power - (2 * powerCorrection - (1 - power))
        } else if (targetSpeed - powerCorrection > 1){
            adjustedRightSpeed = 1 + 2 * powerCorrection;
            adjustedLeftSpeed = 1;
        } else {
            adjustedRightSpeed = targetSpeed + powerCorrection;
            adjustedLeftSpeed = targetSpeed - powerCorrection;
        }
        mPriorImuAngle = mCurrentImuAngle;
        mPriorAdjustedAngle = mAdjustedAngle;
//        Log.i(TAG, String.format("getSpeeds: targetSpeed: %.3f, powerCorrection: %.3f", targetSpeed, powerCorrection));
//        Log.i(TAG, String.format("getSpeeds: adjustedLeftSpeed: %.3f, adjustedRightSpeed: %.3f", adjustedLeftSpeed, adjustedRightSpeed));
        return new Speeds(adjustedRightSpeed, adjustedLeftSpeed);
    }

    /**
     * Get the adjusted speeds for each side of the robot to allow it to turn enough to stay on a straight line. Only call once per cycle.
     * @param targetSpeed
     * @return
     */
    private SpeedsPhi getPhiSpeeds(double targetSpeed, HashMap<DcMotor, Double> motorPowerFactors){
        double powerCorrection = getPowerCorrection();
        double adjustedLeftFrontSpeed, adjustedLeftBackSpeed, adjustedRightFrontSpeed, adjustedRightBackSpeed;

        if(motorPowerFactors.get(leftFrontDrive)*targetSpeed - powerCorrection > 1){
            adjustedLeftFrontSpeed = 1;
        }else if(motorPowerFactors.get(leftFrontDrive)*targetSpeed - powerCorrection < -1) {
            adjustedLeftFrontSpeed = -1;
        }else{
            adjustedLeftFrontSpeed = motorPowerFactors.get(leftFrontDrive) * targetSpeed - powerCorrection;
        }

        if(motorPowerFactors.get(leftBackDrive)*targetSpeed - powerCorrection > 1){
            adjustedLeftBackSpeed = 1;
        }else if(motorPowerFactors.get(leftBackDrive)*targetSpeed - powerCorrection < -1) {
            adjustedLeftBackSpeed = -1;
        }else{
            adjustedLeftBackSpeed = motorPowerFactors.get(leftBackDrive) * targetSpeed - powerCorrection;
        }

        if(motorPowerFactors.get(rightFrontDrive)*targetSpeed + powerCorrection > 1){
            adjustedRightFrontSpeed = 1;
        }else if(motorPowerFactors.get(rightFrontDrive)*targetSpeed + powerCorrection < -1) {
            adjustedRightFrontSpeed = -1;
        }else{
            adjustedRightFrontSpeed = motorPowerFactors.get(rightFrontDrive) * targetSpeed + powerCorrection;
        }

        if(motorPowerFactors.get(rightBackDrive)*targetSpeed + powerCorrection > 1){
            adjustedRightBackSpeed = 1;
        }else if(motorPowerFactors.get(rightBackDrive)*targetSpeed + powerCorrection < -1) {
            adjustedRightBackSpeed = -1;
        }else{
            adjustedRightBackSpeed = motorPowerFactors.get(rightBackDrive) * targetSpeed + powerCorrection;
        }

        mPriorImuAngle = mCurrentImuAngle;
        mPriorAdjustedAngle = mAdjustedAngle;
//        Log.i(TAG, String.format("getSpeedsPhi: targetSpeed: %.3f, powerCorrection: %.3f", targetSpeed, powerCorrection));
//        Log.i(TAG, String.format("getSpeedsPhi: adjustedLeftFrontSpeed: %.3f, adjustedLeftBackSpeed: %.3f, adjustedRightFrontSpeed: %.3f, adjustedRightBackSpeed: %.3f",
//                adjustedLeftFrontSpeed, adjustedLeftBackSpeed, adjustedRightFrontSpeed, adjustedRightBackSpeed));
//        Log.i(TAG, String.format("getSpeedsPhi: leftFrontMotorPowerFactor: %.3f, leftBackMotorPowerFactor: %.3f, rightFrontMotorPowerFactor: %.3f, rightBackMotorPowerFactor: %.3f",
//                motorPowerFactors.get(leftFrontDrive), motorPowerFactors.get(leftBackDrive), motorPowerFactors.get(rightFrontDrive), motorPowerFactors.get(rightBackDrive)));
        return new SpeedsPhi(adjustedLeftFrontSpeed, adjustedLeftBackSpeed, adjustedRightFrontSpeed, adjustedRightBackSpeed);
    }

    public void turnSweeper(double revolutionsToTurn, double power){
        Sweep.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Sweep.setTargetPosition((int)Math.round(revolutionsToTurn*383.6*2));  //goBilda 5202 435 rpm motor with 2:1 speed reduction via external gears
        Sweep.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Sweep.setPower(power);
        while(Sweep.isBusy()){
//            opMode.sleep(25);
            try{
                Thread.sleep(25);
            } catch(InterruptedException e){}

        }
        Sweep.setPower(0);
        //Sweep.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private double calculateAngleDifference(double targetAngle, double nowAngle){
        double angle1 = 0, angle2 = 0, angleDiff180 = 0, angleDiff0 = 0, angleDifference = 0, returnAngle = 0;
        if(targetAngle >= 0 && nowAngle <= 0){
            angle1 = Math.PI - targetAngle;
            angle2 = nowAngle + Math.PI;
            angleDiff180 = angle1 + angle2;
            angleDiff0 = targetAngle - nowAngle;
            angleDifference = Math.min(angleDiff180, angleDiff0);
            if(angleDiff180 < angleDiff0)
                angleDifference *= -1;
        }else if(targetAngle <= 0 && nowAngle >= 0){
            angle1 = Math.PI - nowAngle;
            angle2 = targetAngle + Math.PI;
            angleDiff180 = angle1 + angle2;
            angleDiff0 = nowAngle - targetAngle;
            angleDifference = Math.min(angleDiff180, angleDiff0);
            if(angleDiff0 < angleDiff180)
                angleDifference *= -1;
        }else{
            // for all cases that target & now have the same sign
            angleDifference = targetAngle - nowAngle;
        }
//        Log.i(TAG, String.format("calculateAngleDifference: angleDiff0: %.2f, angleDiff180: %.2f, angleDifference: %.2f",
//                angleDiff0, angleDiff180, angleDifference));
        return angleDifference;
    }
}

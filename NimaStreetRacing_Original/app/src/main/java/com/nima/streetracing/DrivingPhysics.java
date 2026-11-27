package com.nima.streetracing;

public class DrivingPhysics {
    public float speed=0, steerAngle=0, lateral=0, yaw=0, distance=0;
    public void update(float dt, TouchControls c, CarSpec car) {
        float throttle=c.throttle, brake=c.brake;
        float accel=throttle*(3.4f*car.power);
        float braking=brake*7.5f;
        speed += (accel-braking-0.28f-(speed*speed*0.012f))*dt;
        if(speed<0)speed=0;
        if(speed>car.power*62f)speed=car.power*62f;
        float steer=c.steer;
        float drift=c.handbrake>0?2.2f:1f;
        steerAngle += (steer*0.85f*drift-steerAngle*3.2f)*dt;
        yaw += steerAngle*(0.25f+speed/65f)*dt;
        lateral += (float)Math.sin(yaw)*speed*0.018f*dt;
        lateral *= (float)Math.pow(0.90f,dt*60f);
        distance += speed*dt*0.27778f;
    }
    public int kmh(){return (int)(speed*3.6f);}
}

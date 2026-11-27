package com.nima.streetracing;

public class CarSpec {
    public final String name, className;
    public final float power, grip, mass, red, green, blue;
    public CarSpec(String n,String c,float p,float g,float m,float r,float gr,float b){
        name=n;className=c;power=p;grip=g;mass=m;red=r;green=gr;blue=b;
    }
    public static final CarSpec[] GARAGE = new CarSpec[] {
        new CarSpec("Nissan GT-R R35","SPORT",0.92f,0.90f,1.00f,0.08f,0.12f,0.82f),
        new CarSpec("Lamborghini Aventador","HYPER",1.00f,0.82f,0.94f,0.88f,0.72f,0.08f),
        new CarSpec("Bugatti Veyron","HYPER",1.00f,0.88f,1.18f,0.12f,0.20f,0.72f),
        new CarSpec("Nissan Skyline GT-R R34","TUNER",0.82f,0.86f,0.96f,0.08f,0.30f,0.92f),
        new CarSpec("Toyota Supra MK4","TUNER",0.80f,0.82f,0.91f,0.92f,0.52f,0.08f),
        new CarSpec("BMW M3 E46","SPORT",0.76f,0.84f,0.90f,0.12f,0.48f,0.22f),
        new CarSpec("BMW M5 F90","SPORT",0.90f,0.88f,1.12f,0.10f,0.12f,0.62f),
        new CarSpec("Mercedes-AMG GT R","SPORT",0.91f,0.92f,1.04f,0.86f,0.12f,0.10f),
        new CarSpec("Porsche 911 GT3 RS","TRACK",0.88f,1.00f,0.88f,0.92f,0.42f,0.08f),
        new CarSpec("Ferrari 458 Italia","HYPER",0.91f,0.94f,0.90f,0.88f,0.08f,0.06f),
        new CarSpec("Lamborghini Murcielago","HYPER",0.95f,0.82f,1.12f,0.86f,0.72f,0.06f),
        new CarSpec("Audi R8 V10","SPORT",0.89f,0.92f,1.02f,0.92f,0.92f,0.92f),
        new CarSpec("Chevrolet Corvette C6","MUSCLE",0.86f,0.82f,0.98f,0.08f,0.30f,0.84f),
        new CarSpec("Ford Mustang Shelby GT500","MUSCLE",0.88f,0.72f,1.18f,0.10f,0.16f,0.82f),
        new CarSpec("Dodge Challenger Hellcat","MUSCLE",0.92f,0.70f,1.28f,0.52f,0.08f,0.08f),
        new CarSpec("Mitsubishi Lancer Evo X","TUNER",0.78f,0.92f,1.00f,0.12f,0.26f,0.70f),
        new CarSpec("Subaru WRX STI","TUNER",0.77f,0.94f,1.02f,0.12f,0.30f,0.86f),
        new CarSpec("Mazda RX-7 FD","TUNER",0.74f,0.86f,0.82f,0.92f,0.22f,0.08f),
        new CarSpec("McLaren MP4-12C","HYPER",0.96f,0.98f,0.94f,0.92f,0.34f,0.08f),
        new CarSpec("Koenigsegg Agera R","HYPER",1.00f,0.96f,0.90f,0.92f,0.92f,0.08f)
    };
}

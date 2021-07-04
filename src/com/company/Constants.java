package com.company;

//Done!
public interface Constants {
	int INFINITY = 15;
	double LAMBDA = 0.10;
	/**
	 * If the absolute difference of two doubles is less than this value, they will be considered equal.
	 */
	double EPSILON = 0.01;
	/**
	 * If {@code true}, some debug lines will be printed to {@link Constants#DVR_LOOP_LOG_PATH} and console.
	 */
	boolean DEBUG_DVR_MODE = false;
	/**
	 * If {@code true}, some debug lines will be printed to console.
	 */
	boolean DEBUG_ROUTING_PATH_MODE = false;
	String DVR_LOOP_LOG_PATH = "LogOfDVRLoop.txt";
}
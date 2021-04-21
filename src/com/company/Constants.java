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
	 * If {@code true}, debug lines will be printed to {@link Constants#DVR_LOOP_LOG_PATH}.
	 */
	boolean DEBUG_DVR_MODE = true;
	String DVR_LOOP_LOG_PATH = "LogOfDVRLoop.txt";
}
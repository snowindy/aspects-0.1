package ru.neoflex.aspectj.logging.test;

import java.util.logging.Logger;

import ru.neoflex.aspectj.logging.Logged;
import ru.neoflex.aspectj.logging.NoLog;

public class PerfomanceTest {
	public static void main(String[] args) {
		PerfomanceTest t = new PerfomanceTest();
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			t.plainMethod0(i);
		}
		long e = System.currentTimeMillis();

		System.out.println("10^7 executions of no-log method took: "
				+ (e - s) + " ms");

		s = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			t.plainMethod1(i);
		}
		e = System.currentTimeMillis();

		System.out.println("10^7 executions of traditionally logged method took: " + (e - s)
				+ " ms");

		s = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			t.plainMethod2(i);
		}
		e = System.currentTimeMillis();

		System.out.println("10^7 executions of AJ-logged method took: "
				+ (e - s) + " ms");

		System.out.println();

		s = System.currentTimeMillis();

		PerfomanceTest2 t1 = new PerfomanceTest2();
		
		for (int i = 0; i < 10000000; i++) {
			t1.plainInMethod1(i);
		}
		e = System.currentTimeMillis();

		System.out.println("10^7 executions of @Logged class method took: " + (e - s)
				+ " ms");
		
		s = System.currentTimeMillis();
		
		for (int i = 0; i < 10000000; i++) {
			t1.plainInMethod0(i);
		}
		e = System.currentTimeMillis();

		System.out.println("10^7 executions of @NoLog method took: " + (e - s)
				+ " ms");

	}

	private double plainMethod0(int i) {

		double x = 3;
		x = Math.sin(x);

		return x;
	}

	private double plainMethod1(int i) {
		final String methodName = "sendStaticData(String)";
		logger.entering(CLASS_NAME, methodName, "i = " + i);
		double x = 3;
		x = Math.sin(x);

		logger.exiting(CLASS_NAME, methodName);

		return x;
	}

	@Logged
	private double plainMethod2(int i) {

		double x = 3;
		x = Math.sin(x);

		return x;
	}

	private final static String CLASS_NAME = PerfomanceTest.class.getName();

	private final static Logger logger = Logger.getLogger(CLASS_NAME);
}

@Logged
class PerfomanceTest2 {

	@NoLog
	public double plainInMethod0(int i) {

		double x = 3;
		x = Math.sin(x);

		return x;
	}
	
	public double plainInMethod1(int i) {

		double x = 3;
		x = Math.sin(x);

		return x;
	}
}

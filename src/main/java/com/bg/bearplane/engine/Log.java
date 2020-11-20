package com.bg.bearplane.engine;

import java.io.PrintStream;

public class Log {

	static String logFile = "log.txt";
	static int hour = 0;

	public static boolean useFile = false;

	public static void hacker(String s) {
		PrintStream cur = System.out;
		if (useFile) {
			setOut("logs/hacker.txt");
		}
		BearMinimumLog.error(s);
		if (useFile) {
			System.setOut(cur);
		}
	}

	public static void info(String s) {
		update();
		BearMinimumLog.info(s);
	}

	public static void debug(String s) {
		update();
		BearMinimumLog.debug(s);
	}

	public static void debug(int i) {
		update();
		BearMinimumLog.debug(i + "");
	}
	
	public static void debug(Exception e) {
		update();
		BearMinimumLog.error("Exception: " + e);
		for (StackTraceElement ste : e.getStackTrace()) {
			BearMinimumLog.debug(ste.toString());
		}

	}

	public static void warn(String s) {
		update();
		BearMinimumLog.warn(s);
	}

	public static void error(String s) {
		update();
		BearMinimumLog.error(s);
	}

	public static void error(Exception e) {
		update();
		BearMinimumLog.error("Exception: " + e);
		for (StackTraceElement ste : e.getStackTrace()) {
			BearMinimumLog.info(ste.toString());
		}

	}

	public static void init(String[] args) {
		Util.assureDir("logs");		
		BearMinimumLog.DEBUG(); //keep kryo from running its fucking mouth		
		if( Bearplane.game.isRelease()) {
			useFile = true;
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
				case "-v":
					BearMinimumLog.ERROR();
					break;
				case "-vv":
					BearMinimumLog.WARN();
					break;
				case "-vvv":
					BearMinimumLog.INFO();
					break;
				case "-vvvv":
					BearMinimumLog.DEBUG();
					break;
				default:
					com.esotericsoftware.minlog.Log.NONE();
					break;
				}
			}
		} else {
			useFile = false;
			BearMinimumLog.DEBUG();
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
				case "-f":
					useFile = true;
					break;
				}
			}
		}

		if (useFile) {
			setLogFile();
		}
	}

	public static void update() {
		if (useFile) {
			int h = getHour();
			if (h != hour) {
				setLogFile();
			}
		}
	}

	static int getHour() {
		String d = Util.getDate("MM-dd-yy-HH");
		return Integer.parseInt(d.substring(d.length() - 2));
	}

	public static void setOut(String s) {
		try {
			PrintStream ps = new PrintStream(s);
			System.setOut(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLogFile() {
		try {
			hour = getHour();
			String dir = Util.getDate("MM-dd-yy");
			Util.assureDir("logs/" + dir);
			String f = "logs/" + dir + "/" + dir + " Hour " + hour + ".txt";
			setOut(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

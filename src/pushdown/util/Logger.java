package pushdown.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	private static boolean debug = true;
	private static boolean info = true;
	private static BufferedWriter multiBr = null;
	private static BufferedWriter smallStepBr = null;
	private static BufferedWriter apsBr = null;
	private static BufferedWriter complementionBr = null;
	
	public static void debug(String bug,int mode){
		try {
			String filename ="output/" + Parse.getFilename()+".temp";
			if(mode == Constants.MULTI){
				if(null == multiBr ){
					filename += ".MULTI";
					multiBr = new BufferedWriter(new FileWriter(new File(filename)));
				}
				multiBr.write(bug);
				multiBr.newLine();
			}else if(mode == Constants.SAMLLSTEP){
				if(null == smallStepBr ){
					filename +=".SAMLLSTEP";
					smallStepBr = new BufferedWriter(new FileWriter(new File(filename)));
				}
				smallStepBr.write(bug);
				smallStepBr.newLine();
			}else if(mode == Constants.APS){
				if(null == apsBr ){
					filename += ".APS";
					apsBr = new BufferedWriter(new FileWriter(new File(filename)));
				}
				apsBr.write(bug);
				apsBr.newLine();
			}else if(mode == Constants.COMPLEMENTATION){
				if(null == complementionBr ){
					filename += ".COMPLEMENTATION";
					complementionBr = new BufferedWriter(new FileWriter(new File(filename)));
				}
				complementionBr.write(bug);
				complementionBr.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void debug(String bug){
		if(debug){
			System.out.println(bug);
		}
	}
	public static void debug(Object bug){
		if(debug){
			System.out.println(bug);
		}
	}
	public static void debug(){
		if(debug){
			System.out.println();
		}
	}
	public static void info(String message){
		if(info){
			System.out.println(message);
		}
	}
	
	public static void close(){
		try {
			if(multiBr != null){
				multiBr.close();
					// TODO Auto-generated catch block
			}
			if(smallStepBr != null){
				smallStepBr.close();
			}
			if(apsBr != null){
				apsBr.close();
			}
			if(complementionBr != null){
				complementionBr.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}

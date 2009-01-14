import ngmud.CLog;
import ngmud.ngMUDException;


public class Test {
	public static void main(String[] args) throws ngMUDException { // Test-main-Func
		CLog.Init("",true,(short)5);
		CLog.Info("Log initialized successfull :D");
		
		CLog.UnInit();
	}

}

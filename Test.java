import ngmud.CLog;
import ngmud.ngMUDException;


public class Test {
	public static void main(String[] args) throws ngMUDException { // Test-main-Func
		CLog.Init("",true,CLog.LOG_LEVEL.CUSTOM,(short)5);
		CLog.Info("Log initialized successfull :D");
		CLog.Custom("Custom-Test Level 1", 1);
		CLog.Custom("Custom-Text Level 6", 6);
		CLog.Custom("Custom-Test Level 5", 5);
		CLog.Debug("Debug-Test");
		CLog.Warning("Warnung-Test");
		CLog.Error("Error-Test");
		CLog.Force("Force-Test");
		CLog.CustomForce("Custom-Force Testüüü");
		
		CLog.UnInit();
	}

}

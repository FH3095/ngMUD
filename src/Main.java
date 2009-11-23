
import de.ngmud.CLog;
import de.ngmud.tests.*;

public class Main {

	public static void main(String[] args) throws Exception {
		CLog.Init("",true,CLog.LOG_LEVEL.CUSTOM,(short)5);
		TestBase Test=new OldTest();
		Test.Main(args);
	}
}

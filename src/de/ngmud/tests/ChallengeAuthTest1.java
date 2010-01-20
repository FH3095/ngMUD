package de.ngmud.tests;

import de.ngmud.ngMUDException;
import de.ngmud.network.CChallengeAuthHelper;
import de.ngmud.util.CRandom;

public class ChallengeAuthTest1 extends TestBase {

	/**
	 * How ChallengeAuth works:
	 * Server stores Hash1=HashFunc(PW) inside his database.
	 * On Auth:
	 * - User enters PW.
	 * - Client calcs PWHash=HashFunc(PW).
	 * - Server sends a random byte-array to the client (Length of byte-array see CChallengeAuthHelper.RND_BYTE_ARRAY_LEN).
	 * - Client and server calcs ResultHash=HashFunc(PWHash+RandomByteArray).
	 * - Client sends ResultHash to the server.
	 * - Server compares both hashes.
	 * Thats nearly the same way HTTP-Digest-Auth works (see http://en.wikipedia.org/wiki/Digest_access_authentication#Overview ).
	 */
	public void Main(String[] args) throws ngMUDException {
		System.out.println("ChallengeAuthTest");
		String PW=CRandom.RandomString(4, 8).toString();
		System.out.println("PW="+PW);
		CChallengeAuthHelper.Instance().Init("SHA-256");
		byte PWHash[]=CChallengeAuthHelper.Instance().GenerateHashFromPW(PW);
		byte Rnd[]=new byte[CChallengeAuthHelper.Instance().RND_BYTE_ARRAY_LEN];
		CRandom.Bytes(Rnd);
		byte ResultHash[]=CChallengeAuthHelper.Instance().GenerateResultHash(PWHash, Rnd);
		System.out.println("Hash(PW)           ="+CChallengeAuthHelper.Instance().HashToString(PWHash));
		System.out.println("Rnd                ="+CChallengeAuthHelper.Instance().HashToString(Rnd));
		System.out.println("Hash(PW)+Rnd       ="+CChallengeAuthHelper.Instance().HashToString(PWHash)+"+"+
				CChallengeAuthHelper.Instance().HashToString(Rnd));
		System.out.println("Hash(Hash(PW)+Rnd) ="+CChallengeAuthHelper.Instance().HashToString(ResultHash));
		CChallengeAuthHelper.Instance().Delete();
	}
}

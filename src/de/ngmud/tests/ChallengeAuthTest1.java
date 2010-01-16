package de.ngmud.tests;

import de.ngmud.ngMUDException;
import de.ngmud.network.CChallengeAuthHelper;
import de.ngmud.util.CRandom;

public class ChallengeAuthTest1 extends TestBase {

	/**
	 * How ChallengeAuth works:
	 * Server stores Hash1=Hash(Username+":"+PW) inside his database.
	 * On Auth:
	 * - User inputs Username and PW.
	 * - Client calcs Hash1=Hash(Username+":"+PW).
	 * - Server sends a random hash/string/whatever to the client.
	 * - Client and server calcs ResultHash=Hash(Hash1+":"+RandomHash).
	 * - Client sends ResultHash to the server.
	 * - Server compares both hashes.
	 * Thats the same way HTTP-Digest-Auth works.
	 */
	public void Main(String[] args) throws ngMUDException {
		System.out.println("ChallengeAuthTest");
		String Username="FH";
		String PW=CRandom.RandomString(4, 8).toString();
		System.out.println("Username="+Username+" ; PW="+PW);
		CChallengeAuthHelper.Instance().Init("SHA-256");
		byte UsernamePWHash[]=CChallengeAuthHelper.Instance().GenerateHashFromPW(Username, PW);
		byte RndHash[]=CChallengeAuthHelper.Instance().GenerateRandomHash();
		byte ResultHash[]=CChallengeAuthHelper.Instance().GenerateHashFromHashes(UsernamePWHash, RndHash);
		System.out.println("Hash(Username:PW)                ="+CChallengeAuthHelper.Instance().HashToString(UsernamePWHash));
		System.out.println("Hash(Rnd)                        ="+CChallengeAuthHelper.Instance().HashToString(RndHash));
		System.out.println("Hash(Username:PW):Hash(Rnd)      ="+CChallengeAuthHelper.Instance().HashToString(UsernamePWHash)+":"+
				CChallengeAuthHelper.Instance().HashToString(RndHash));
		System.out.println("Hash(Hash(Username:PW):Hash(Rnd))="+CChallengeAuthHelper.Instance().HashToString(ResultHash));
		CChallengeAuthHelper.Instance().Delete();
	}
}

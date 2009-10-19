/*       +------------------------------------+
 *       | Inspire Internet Relay Chat Daemon |
 *       +------------------------------------+
 *
 *  InspIRCd: (C) 2002-2009 InspIRCd Development Team
 * See: http://wiki.inspircd.org/Credits
 *
 * This program is free but copyrighted software; see
 *            the file COPYING for details.
 *
 * ---------------------------------------------------
 */

#include "inspircd.h"
#include "m_sqlv2.h"
#include "m_sqlutils.h"

/* $ModDesc: Allow/Deny connections based upon ngMUD accounts */
/* $ModDep: m_sqlv2.h m_sqlutils.h */

class ModuleNgmudAuth : public Module
{
	Module* SQLutils;
	Module* SQLprovider;

	std::string AllowNickPattern;
	std::string AllowAccPattern;
	std::string DbId;
	std::string PermBanKillReason;
	std::string TempBanKillReason;
	std::string UnknownAccOrCharKillReason;
	std::string ErrorKillReason;

	std::string HostPostfix;
	std::string NoAccHost;

	std::string NoGuildIdent;

	std::string DynamicDB;

	bool Verbose;

public:
	ModuleNgmudAuth(InspIRCd* Me)
	: Module(Me)
	{
		ServerInstance->Modules->UseInterface("SQLutils");
		ServerInstance->Modules->UseInterface("SQL");

		SQLutils = ServerInstance->Modules->Find("m_sqlutils.so");
		if (!SQLutils)
			throw ModuleException("Can't find m_sqlutils.so. Please load m_sqlutils.so before m_ngmud_auth.so.");

		SQLprovider = ServerInstance->Modules->FindFeature("SQL");
		if (!SQLprovider)
			throw ModuleException("Can't find an SQL provider module. Please load one before attempting to load m_ngmud_auth.");

		OnRehash(NULL);
		Implementation eventlist[] = { I_OnUserRegister, I_OnRehash, I_OnUserDisconnect, I_OnCheckReady, I_OnRequest};
		ServerInstance->Modules->Attach(eventlist, this, 5);
	}

	virtual ~ModuleNgmudAuth()
	{
		ServerInstance->Modules->DoneWithInterface("SQL");
		ServerInstance->Modules->DoneWithInterface("SQLutils");
	}

	virtual void OnRehash(User* user)
	{
		ConfigReader Conf(ServerInstance);

		DbId						= Conf.ReadValue("ngmud_auth", "dbid", 0);
		PermBanKillReason			= Conf.ReadValue("ngmud_auth", "permbankill", 0);
		TempBanKillReason			= Conf.ReadValue("ngmud_auth", "tempbankill", 0);
		UnknownAccOrCharKillReason	= Conf.ReadValue("ngmud_auth", "unknownaccorcharkill",0 );
		AllowNickPattern			= Conf.ReadValue("ngmud_auth", "allownick",0);
		AllowAccPattern				= Conf.ReadValue("ngmud_auth", "allowacc",0);
		ErrorKillReason				= Conf.ReadValue("ngmud_auth", "errorkill",0);
		Verbose						= Conf.ReadFlag ("ngmud_auth", "debug",0);
		HostPostfix					= Conf.ReadValue("ngmud_auth", "hostpostfix",0);
		NoAccHost					= Conf.ReadValue("ngmud_auth", "noacchost",0);
		DynamicDB					= Conf.ReadValue("ngmud_auth", "dynamicdb",0);
		NoGuildIdent				= Conf.ReadValue("ngmud_auth", "noguildname",0);
	}

	virtual int OnUserRegister(User* user)
	{
		if (!AllowAccPattern.empty()  && InspIRCd::Match(user->ident,AllowAccPattern) &&
		    !AllowNickPattern.empty() && InspIRCd::Match(user->nick,AllowNickPattern))
		{
			static int Val=0;
			user->Extend("ngmud_auth",&Val);
			user->ChangeDisplayedHost(NoAccHost.c_str());
			return 0;
		}

		if (!CheckCredentials(user))
		{
			ServerInstance->Users->QuitUser(user, ErrorKillReason);
			return 1;
		}
		return 0;
	}

	bool CheckCredentials(User* user)
	{
													// 0     1
		static std::string Query(std::string("SELECT a.id,a.show_name, "
																	// 2
											 "(SELECT CONCAT(IF(ab.perm!=0,'perm',ab.until),'$$',REPLACE(REPLACE(ab.reason,'\\n',' '),'\\r','')) FROM account_ban ab WHERE "
											 "a.id = ab.id AND "
											 "(ab.until > CURRENT_TIMESTAMP OR ab.perm !=0) ORDER BY ab.perm DESC , ab.until DESC LIMIT 1) AS ban, "
														// 3
											 "(SELECT org.name FROM ")+DynamicDB+
											 std::string(".org_organisation org JOIN ")+
											 DynamicDB+std::string(".char_org member ON member.org_id=org.id "
											 "WHERE member.char_id=c.id) AS org "
											 "FROM account a JOIN ")+DynamicDB+
											 std::string(".char_character c ON c.account=a.id "
											 "WHERE a.login_name LIKE '?' AND a.pwd='?' AND c.name LIKE '?'"));
		/* Build the query */
		SQLrequest req = SQLrequest(this, SQLprovider, DbId, (SQLquery(Query.c_str()),user->ident.c_str(),user->password.c_str(),user->nick.c_str()));

		if(req.Send())
		{
			/* When we get the query response from the service provider we will be given an ID to play with,
			 * just an ID number which is unique to this query. We need a way of associating that ID with a User
			 * so we insert it into a map mapping the IDs to users.
			 * Thankfully m_sqlutils provides this, it will associate a ID with a user or channel, and if the user quits it removes the
			 * association. This means that if the user quits during a query we will just get a failed lookup from m_sqlutils - telling
			 * us to discard the query.
		 	 */
			AssociateUser(this, SQLutils, req.id, user).Send();

			return true;
		}
		else
		{
			if (Verbose)
			{
				ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_auth: SQL-Query failed: %s. Client %s!%s@%s denied.", req.error.Str(), user->nick.c_str(), user->ident.c_str(), user->host.c_str());
			}
			return false;
		}
	}

	virtual void ReplaceInBanReason(std::string& Str,const std::string& SqlResult)
	{
		if(SqlResult.size()<1)
		{	return;	}
		std::size_t ReasonPos=SqlResult.find("$$");
		std::string Until;
		std::string Reason;

		if(std::string::npos==ReasonPos)
		{
			Until=SqlResult;
			Reason="";
		}
		else
		{
			Until=SqlResult.substr(0,ReasonPos);
			Reason=&((SqlResult.c_str())[ReasonPos+2]);
		}



		std::size_t Pos=Str.rfind("$u");
		if(Pos!=std::string::npos && Until.size()!=0)
		{
			Str.replace(Pos,2,Until);
		}

		Pos=Str.rfind("$r");
		if(Pos!=std::string::npos && Reason.size()!=0)
		{
			Str.replace(Pos,2,Reason);
		}
	}

	virtual const char* OnRequest(Request* request)
	{
		if(strcmp(SQLRESID, request->GetId()) == 0)
		{
			SQLresult* res = static_cast<SQLresult*>(request);

			User* user = GetAssocUser(this, SQLutils, res->id).S().user;
			UnAssociate(this, SQLutils, res->id).S();

			if(user)
			{
				if(res->error.Id() == SQL_NO_ERROR)
				{
					if(res->Rows()==1)
					{
						SQLfieldList& Row=res->GetRow();
						if(!Row[2].null)
						{
							static const std::string PermStr("perm");
							if(PermStr.find(Row[2].d.c_str(),0,
											(Row[2].d.size()<PermStr.size() ? Row[2].d.size() : PermStr.size()))==0)
							{
								std::string Reason(PermBanKillReason);
								ReplaceInBanReason(Reason,Row[2].d);
								ServerInstance->Users->QuitUser(user,Reason);
							}
							else
							{
								std::string Reason(TempBanKillReason);
								ReplaceInBanReason(Reason,Row[2].d);
								ServerInstance->Users->QuitUser(user,Reason);
							}
						}
						else
						{
							int Val=ConvToInt(Row[0].d.c_str());
							user->Extend("ngmud_auth",&Val);
							user->ChangeDisplayedHost(std::string(std::string(Row[1].d.c_str())+HostPostfix).c_str());
							if(Row[3].null)
							{
								user->ChangeIdent(NoGuildIdent.c_str());
							}
							else
							{
								user->ChangeIdent(Row[3].d.c_str());
							}
						}
					}
					else
					{
						ServerInstance->Users->QuitUser(user,UnknownAccOrCharKillReason);
					}
				}
				else
				{
					ServerInstance->Users->QuitUser(user, ErrorKillReason);
					if (Verbose)
					{
						ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_auth: SQL-Query failed: %s. Client %s!%s@%s denied.", res->error.Str(), user->nick.c_str(), user->ident.c_str(), user->host.c_str());
					}
				}
			}
			else
			{
				return NULL;
			}

			return SQLSUCCESS;
		}
		return NULL;
	}

	virtual void OnUserDisconnect(User* user)
	{
		user->Shrink("ngmud_auth");
	}

	virtual bool OnCheckReady(User* user)
	{
		return user->GetExt("ngmud_auth");
	}

	virtual Version GetVersion()
	{
		return Version("$Id: m_ngmud_auth.cpp 2009-10-15 FH $", VF_VENDOR, API_VERSION);
	}

};

MODULE_INIT(ModuleNgmudAuth)

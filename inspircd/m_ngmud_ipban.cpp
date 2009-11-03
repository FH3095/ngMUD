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
#include "xline.h"

/* $ModDesc: Manage IP-Bans from ngMUD */
/* $ModDep: m_sqlv2.h */

class ModuleNgmudIpBan : public Module
{
	Module* SQLprovider;

	std::string DbId;
	std::string PermBanKillReason;
	std::string TempBanKillReason;
	std::string NoBanReason;
	std::string BaningNick;

	std::list<std::string> Bans;

	bool Verbose;

public:
	ModuleNgmudIpBan(InspIRCd* Me)
	: Module(Me)
	{
		ServerInstance->Modules->UseInterface("SQL");

		SQLprovider = ServerInstance->Modules->FindFeature("SQL");
		if (!SQLprovider)
			throw ModuleException("Can't find an SQL provider module. Please load one before attempting to load m_ngmud_ipban.");

		OnRehash(NULL);
		Implementation eventlist[] = { I_OnRehash, I_OnRequest};
		ServerInstance->Modules->Attach(eventlist, this, 2);
	}

	virtual ~ModuleNgmudIpBan()
	{
		ClearBans();
		ServerInstance->Modules->DoneWithInterface("SQL");
	}

	virtual void ClearBans()
	{
		ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_ipban: Clearing ZLines.");
		for(std::list<std::string>::iterator it=Bans.begin();it!=Bans.end();it++)
		{
			ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_ipban: Delete ZLine %s.",it->c_str());
			ServerInstance->XLines->DelLine(it->c_str(),"Z",NULL);
		}
		Bans.clear();
	}

	virtual void OnRehash(User* user)
	{
		ConfigReader Conf(ServerInstance);

		DbId						= Conf.ReadValue("ngmud_ipban", "dbid", 0);
		PermBanKillReason			= Conf.ReadValue("ngmud_ipban", "permbankill", 0);
		TempBanKillReason			= Conf.ReadValue("ngmud_ipban", "tempbankill", 0);
		Verbose						= Conf.ReadFlag ("ngmud_ipban", "debug",0);
		NoBanReason					= Conf.ReadValue("ngmud_ipban", "nobanreason",0);
		BaningNick					= Conf.ReadValue("ngmud_ipban", "BaningNick",0);

										//0    1                         2
		static std::string Query("SELECT ip,subnet_mask,UNIX_TIMESTAMP(until)-UNIX_TIMESTAMP(CURRENT_TIMESTAMP) AS duration,"
								 // 3    4    5
								 "until,perm,reason FROM ip_ban WHERE "
								 "(until > CURRENT_TIMESTAMP OR perm != 0) AND only_new_reg=0 "
								 "ORDER BY perm DESC , until DESC");
		SQLrequest req=SQLrequest(this,SQLprovider,DbId,SQLquery(Query.c_str()));
		if(!req.Send())
		{
			if(Verbose)
			{
				ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_ipban: SQL-Query failed: %s.", req.error.Str());
			}
		}
	}

	virtual void ReplaceInBanReason(std::string& Str,const std::string& IPMask,const std::string& Until,
									const std::string& Reason)
	{
		if(Str.size()<1)
		{	return;	}
		std::size_t Pos=0;


		Pos=Str.rfind("$u");
		if(Pos!=std::string::npos)
		{
			Str.replace(Pos,2,Until);
		}

		Pos=Str.rfind("$r");
		if(Pos!=std::string::npos)
		{
			Str.replace(Pos,2,Reason);
		}

		Pos=Str.find("$i");
		if(Pos!=std::string::npos)
		{
			Str.replace(Pos,2,IPMask);
		}
	}

	virtual const char* OnRequest(Request* request)
	{
		if(strcmp(SQLRESID, request->GetId()) == 0)
		{
			SQLresult* res = static_cast<SQLresult*>(request);

			if(res->error.Id() == SQL_NO_ERROR)
			{
				std::string BanIP;
				SQLfieldList* pRow;
				ClearBans();
				while((pRow=&(res->GetRow()))->size()>0)
				{
					SQLfieldList& Row=*pRow;
					BanIP=Row[0].d+std::string("/")+Row[1].d;
					std::string BanMsg;
					long Duration=0;
					if(Row[4].d.compare("0")==0) // Not Perm
					{
						BanMsg=TempBanKillReason;
						Duration=ServerInstance->Duration(Row[2].d.c_str());
					}
					else
					{
						BanMsg=PermBanKillReason;
					}
					ReplaceInBanReason(BanMsg,BanIP,Row[3].d,Row[5].d);
					ZLine* zl=new ZLine(ServerInstance,ServerInstance->Time(),Duration,BaningNick.c_str(),
										BanMsg.c_str(),BanIP.c_str());
					if(!ServerInstance->XLines->AddLine(zl,NULL))
					{
						ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_ipban: Can't add ZLine %s.", BanIP.c_str());
					}
					else
					{
						Bans.push_back(BanIP);
						ServerInstance->SNO->WriteGlobalSno('a',"m_ngmud_ipban: Added ZLine %s until %s Duration %i Reason %s",
															BanIP.c_str(),Row[3].d.c_str(),(int)Duration,BanMsg.c_str());
					}
				}
			}
			else if(Verbose)
			{
				ServerInstance->SNO->WriteGlobalSno('a', "m_ngmud_ipban: SQL-Query failed: %s.", res->error.Str());
			}
			return SQLSUCCESS;
		}
		else
		{
			return NULL;
		}

		return NULL;
	}

	virtual Version GetVersion()
	{
		return Version("$Id: m_ngmud_ipban.cpp 2009-11-3 FH $", VF_VENDOR, API_VERSION);
	}

};

MODULE_INIT(ModuleNgmudIpBan)

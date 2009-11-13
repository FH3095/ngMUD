package de.ngmud.network.packets;

import java.io.IOException;

import de.ngmud.network.CPacketHelper;
import de.ngmud.network.CSocket;

public class CPack_Chat extends CSubPacket {
	public static final short PACK_NUM = 1;
	public long From;
	public byte Content[];

	public enum SUB_TYPE {
		SAY, YELL, GROUP, WHISPER, GUILD, CHANNEL, CHANNEL_JOINED, CHANNEL_LEFT,
	}

	public boolean Recv(CSocket Sock, int Size) {
		try {
			From = Sock.in().readLong();
			Size -= 8;
			Content = new byte[Size];
			if (!CPacketHelper.StreamReadBStr(Sock, Content, Size, 0)) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public boolean Send(CSocket Sock) {
		try {
			Sock.out().writeLong(From);
			Sock.out().write(Content);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public short GetSize() {
		return (short) (8 + Content.length);
	}
}

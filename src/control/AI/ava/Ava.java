package control.AI.ava;

import model.jaxb.AcceptMessageType;
import model.jaxb.AwaitMoveMessageType;
import model.jaxb.CardType;
import model.jaxb.DisconnectMessageType;
import model.jaxb.LoginReplyMessageType;
import model.jaxb.PositionType;
import model.jaxb.WinMessageType;
import control.AI.Player;
import control.AI.ava.Pathfinding.PinPosHelp;
import control.AI.ava.ownClasses.Board;
import control.network.Connection;

public class Ava implements Player {
	private Connection con;
	private int id;
	private WriteIntoFile wif;
	private WriteIntoFile wif_v2;
	private WriteIntoFile possPos;
	private Pathfinding p;

	public Ava(Connection con) {
		this.con = con;
		wif = new WriteIntoFile(WriteIntoFile.FILEPATH + WriteIntoFile.FILEEXTENSION);
		wif_v2 = new WriteIntoFile(WriteIntoFile.FILEPATH + "_v2" + WriteIntoFile.FILEEXTENSION);
		possPos = new WriteIntoFile("src/control/AI/ava/possPos.txt");
		wif.clearFile();
		wif_v2.clearFile();
		wif.write("Ava");
		wif_v2.write("Ava");
		possPos.clearFile();
	}

	public Ava() {
		login();
	}

	@Override
	public String login() {
		return "Humpf";
	}

	@Override
	public void receiveLoginReply(LoginReplyMessageType message) {
		System.out.println("Ava receives a login reply");
		id = message.getNewID();
		p = new Pathfinding(id);
	}

	@Override
	public void receiveAwaitMoveMessage(AwaitMoveMessageType message) {
		wif.write("AWAIT MOVE MESSAGES");
		wif_v2.write("AWAIT MOVE MESSAGES");
		Board b = new Board(message.getBoard());
		b.setTreasure(message.getTreasure());
		p.setBoard(b);
		p.setTreToGo(message.getTreasuresToGo());
		p.setFoundTreasures(message.getFoundTreasures());
		PinPosHelp pph = p.start();
		sendMoveMessage(id, pph.getCardHelp().getC(), pph.getCardHelp().getP(), pph.getPinPos());
	}

	@Override
	public void receiveDisconnectMessage(DisconnectMessageType message) {
		System.out.println("Ava receives a disconnect Message:");
		System.out.println(message.getErrorCode());
		con.sendDisconnect(message.getErrorCode(), id);
		wif.write(message.getErrorCode().toString());
		wif_v2.write(message.getErrorCode().toString());
	}

	@Override
	public void receiveWinMessage(WinMessageType message) {
		if (message.getWinner().getId() == id) {
			con.sendWin(message.getWinner());
		}
	}

	@Override
	public void receiveAcceptMessage(AcceptMessageType message) {
		wif.write(message.getErrorCode().toString());
		wif_v2.write(message.getErrorCode().toString());
		wif.writeNewLine(2);
		wif_v2.writeNewLine(2);
	}

	@Override
	public void sendMoveMessage(int PlayerID, CardType c, PositionType shift, PositionType pin) {
		con.sendMoveMessage(PlayerID, c, shift, pin);
	}

}

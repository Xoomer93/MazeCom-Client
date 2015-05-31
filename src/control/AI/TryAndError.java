package control.AI;

import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import model.Board;
import model.Card;
import model.Position;
import model.jaxb.AcceptMessageType;
import model.jaxb.AwaitMoveMessageType;
import model.jaxb.CardType;
import model.jaxb.DisconnectMessageType;
import model.jaxb.ErrorType;
import model.jaxb.LoginReplyMessageType;
import model.jaxb.MoveMessageType;
import model.jaxb.PositionType;
import model.jaxb.TreasureType;
import model.jaxb.WinMessageType;
import control.network.Connection;

public class TryAndError implements Player {
	private Connection con;
	private int PlayerID;
	private Random random;

	public TryAndError(Connection con) {
		this.con = con;
	}

	@Override
	public String login() {
		System.out.println("Try and Error AI");
		random = new Random();
		return "Doofus";
	}

	@Override
	public void receiveLoginReply(LoginReplyMessageType message) {
		this.PlayerID = message.getNewID();
	}

	@Override
	public void receiveAwaitMoveMessage(AwaitMoveMessageType message) {
		calcMove(Util.getBoard(message), message.getTreasure());
	}

	private void calcMove(Board b, TreasureType t) {
		Board board = (Board) b.clone();
		Position oldPinPos = Util.getPinPos(board, PlayerID);
		List<PositionType> l = board.getAllReachablePositions(oldPinPos);
		Position pt = Util.getTreasurePos(board, t);
		Card shift = Util.getShiftCard(board);
		Position shiftPos;
		MoveMessageType message = new MoveMessageType();
		for (int i = 1; i < 6; i += 2) {
			for (int j = 0; j < 4; j++) {
				Card c = Util.rotateCard(shift, j * 90);
				for (int k = 0; k < 7; k += 6) {
					board = (Board) b.clone();
					board.setShiftCard(c);
					shiftPos = new Position(k, i);
					message.setShiftPosition(shiftPos);
					message.setShiftCard(shift);
					board.proceedShift(message);
					l = board.getAllReachablePositions(oldPinPos);
					if (Util.containsInList(pt, l) != null) {
						System.out.println("found it");
						sendMoveMessage(PlayerID, c, shiftPos, pt);
						return;
					}
				}
			}
		}

		random(b);

	}

	private void random(Board b) {
		System.out.println("random");
		CardType shift = Util.getShiftCard(b);
		MoveMessageType move = new MoveMessageType();
		Position shiftPos = new Position();
		List<PositionType> list;
		Position pinPos = new Position();
		PositionType f = b.getForbidden();
		if (f == null) {
			f = new PositionType();
			f.setCol(-1);
			f.setRow(-1);
		}
		int c = f.getCol();
		int r = f.getRow();
		while (c == f.getCol() && r == f.getRow()) {
			switch (random.nextInt(4)) {
			case 0:
				c = 0;
				r = random.nextInt(3) * 2 + 1;
				break;
			case 1:
				c = 6;
				r = random.nextInt(3) * 2 + 1;
				break;
			case 2:
				r = 0;
				c = random.nextInt(3) * 2 + 1;
				break;
			case 3:
				r = 6;
				c = random.nextInt(3) * 2 + 1;
				break;
			default:
				System.out.println("RANDOM ERROR");
				break;
			}
		}
		shiftPos.setCol(c);
		shiftPos.setRow(r);
		move.setShiftPosition(shiftPos);
		move.setShiftCard(shift);
		b.proceedShift(move);
		list = b.getAllReachablePositions(b.findPlayer(PlayerID));
		pinPos = new Position(list.get(random.nextInt(list.size())));
		sendMoveMessage(PlayerID, shift, shiftPos, pinPos);
	}

	@Override
	public void receiveDisconnectMessage(DisconnectMessageType message) {
		System.out.println(message.getErrorCode());
	}

	@Override
	public void receiveWinMessage(WinMessageType message) {
		System.out.println(message);
		System.out.println(message.getWinner());
	}

	@Override
	public void receiveAcceptMessage(AcceptMessageType message) {
		System.out.println(message.getErrorCode());
	}

	@Override
	public void sendMoveMessage(int PlayerID, CardType c, Position shift, Position pin) {
		con.sendMoveMessage(PlayerID, c, shift, pin);
	}

}

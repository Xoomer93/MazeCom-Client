package view;

import javax.swing.JOptionPane;

import jaxb.AcceptMessageType;
import jaxb.AwaitMoveMessageType;
import jaxb.DisconnectMessageType;
import jaxb.LoginReplyMessageType;
import jaxb.MoveMessageType;
import jaxb.WinMessageType;
import tools.Debug;
import tools.DebugLevel;
import view.data.Context;
import view.data.PersData;
import view.testClasses.Board;
import view.testClasses.Card;
import view.testClasses.Messages;
import view.testClasses.Position;
import config.Settings;

public class EventController {
	private GUIController ctrl_gui;
	private int count = 0;

	public EventController() {
		ctrl_gui = new GUIController(this);
		Debug.addDebugger(System.out, Settings.DEBUGLEVEL);
		Debug.print(Messages.getInstance().getString("Game.Constructor"), DebugLevel.DEBUG); //$NON-NLS-1$
	}

	/**
	 * shows a dialog with a textfield, return: input
	 * 
	 * @return nickname
	 */
	public String login() {
		String name = JOptionPane.showInputDialog("Nickname");
		Context.getInstance().setValue(Context.USER, new PersData(name));
		return name;
	}

	public void receiveLoginReply(LoginReplyMessageType message) {
		JOptionPane.showMessageDialog(null, "Warten auf Mitspieler");
		((PersData) Context.getInstance().getValue(Context.USER)).setID(message.getNewID());
	}

	public void receiveAwaitMoveMessage(AwaitMoveMessageType message) {
		if (count == 0) {
			ctrl_gui.start();
			count++;
		}
		Context.getInstance().setValue(Context.BOARD, new Board(message.getBoard()));
		((PersData) Context.getInstance().getValue(Context.USER)).setCurrentTreasure(message.getTreasure());
		// TODO TreasureToFind != noch zu findenden Schaetze?
		Context.getInstance().setValue(Context.TREASURELIST, message.getTreasuresToGo());
		ctrl_gui.update();
	}

	public void receiveDisconnectMessage(DisconnectMessageType message) {
		ctrl_gui.close();
	}

	public void receiveWinMessage(WinMessageType message) {
		ctrl_gui.endGame(message.getWinner().getId(), message.getWinner().getValue());
	}

	public void receiveAcceptMessage(AcceptMessageType message) {
		ctrl_gui.displayMove(message.isAccept());
	}

	public void receiveMoveMessage(MoveMessageType moveMessage) {
		// TODO Auto-generated method stub
		System.out.println("MOVE");
	}

	public void sendMoveMessage(Card c, Position shift, Position pin) {
		System.out.println("Jetzt muss gesendet werden");
	}

}

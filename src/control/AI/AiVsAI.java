package control.AI;

import java.awt.event.ActionEvent;

import model.jaxb.WinMessageType.Winner;
import server.Game;
import server.userInterface.BetterUI;
import config.Settings;
import control.network.Connection;

public class AiVsAI {
	private int p1 = 0, p2 = 0, p3 = 0, p4 = 0;

	public static void main(String[] args) {
		AiVsAI a = new AiVsAI();
		a.start();
	}

	public void start() {
		Server server = new Server();
		server.start();
		server.startGame(4);

		startClients();
	}

	public void updateWinnerStat(Winner winner) {
		switch (winner.getId()) {
		case 1:
			p1++;
			break;
		case 2:
			p2++;
			break;
		case 3:
			p3++;
			break;
		case 4:
			p4++;
			break;
		}
	}

	private void startClients() {
		Connection connection = new Connection(this);
		Client c1 = new Client(new TryAndError(connection), connection);
		c1.start();

		connection = new Connection(this);
		Client c2 = new Client(new TryAndError(connection), connection);
		c2.start();

		connection = new Connection(this);
		Client c3 = new Client(new TryAndError(connection), connection);
		c3.start();

		connection = new Connection(this);
		Client c4 = new Client(new TryAndError(connection), connection);
		c4.start();
	}

	private class Client extends Thread {
		private Player player;
		private Connection con;

		@Override
		public void start() {
			con.setPlayer(player);
			con.establishConnection("localhost", Settings.PORT);
		}

		public Client(Player player, Connection connection) {
			this.player = player;
			this.con = connection;
		}
	}

	private class Server extends Thread {
		private Game game;
		private BetterUI userinterface;

		@Override
		public void start() {
			game = new Game();
			userinterface = (BetterUI) game.main();
		}

		public void startGame(int player) {
			Settings.DEFAULT_PLAYERS = player;
			userinterface.MIStartActionPerformed(new ActionEvent(this, 1, "ai"));
		}

		public void stopGame() {
			userinterface.MIStartActionPerformed(new ActionEvent(this, 1, "ai"));
		}
	}

}

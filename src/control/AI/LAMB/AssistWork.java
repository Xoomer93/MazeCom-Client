package control.AI.LAMB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Board;
import model.Card;
import model.Position;
import model.jaxb.MoveMessageType;
import model.jaxb.PositionType;
import model.jaxb.TreasureType;
import model.jaxb.TreasuresToGoType;

public class AssistWork {
	public enum Side {UP, RIGHT, DOWN, LEFT}
	public enum Points {
		OWN_START(5000),
		OWN_TARGET(100),
		TARGET_MISSING(-10),
		OTHER_START_OPEN(-700),
		TREASURE_REACHABLE(25);

		private final int value;

		private Points(int v) {
			this.value = v;
		}

		public int value() {
			return value;
		}
	}

	private LAMB lamb;
	private boolean[] canFindTreasure;
	private int depth;
	private int maxDepth;
	private ArrayList<TreasureType> allTreasures;
	private ArrayList<TreasureType> tempFound;
	private int nextPlayer;

	public AssistWork(LAMB lamb) {
		this.lamb = lamb;
		this.depth = 1;
		this.maxDepth = 2;
		this.allTreasures = new ArrayList<TreasureType>();
		for (TreasureType treasure : TreasureType.values()) {
			allTreasures.add(treasure);
		}
		this.tempFound = new ArrayList<TreasureType>();
	}

	public Move calculateMove() {
		canFindTreasure = new boolean[4];
		tempFound.clear();
		ArrayList<Integer> players = new ArrayList<Integer>();
		for (TreasuresToGoType t : lamb.getTreasuresToGo()) {
			players.add(t.getPlayer());
		}
		Collections.sort(players);
		nextPlayer = players.get((players.indexOf(lamb.getPlayerID()) + 1) % players.size());
		System.out.println(new Position(lamb.getBoard().getForbidden()));
		if (lamb.getTreasure().name().startsWith("Start0")) {
			return calculateFinishMove(lamb.getPlayerID(), lamb.getBoard(), lamb.getTreasuresToGo(), lamb.getTreasuresFound(), lamb.getTreasure());
		}
		else {
			return calculateMove(lamb.getPlayerID(), lamb.getBoard(), lamb.getTreasuresToGo(), lamb.getTreasuresFound(), lamb.getTreasure());
		}
	}

	private ArrayList<Position> getShiftPositions(Board oldBoard) {
		ArrayList<Position> positionList = new ArrayList<Position>();
		Position forbiddenPosition;
		if (oldBoard.getForbidden() == null) {
			forbiddenPosition = new Position();
			forbiddenPosition.setCol(-1);
			forbiddenPosition.setRow(-1);
		}
		else {
			forbiddenPosition = new Position(oldBoard.getForbidden());
		}
		for (Side side : Side.values()) {
			Position shiftPosition = new Position();
			switch (side) {
			case UP:
				shiftPosition.setRow(0);
				break;
			case RIGHT:
				shiftPosition.setCol(6);
				break;
			case DOWN:
				shiftPosition.setRow(6);
				break;
			case LEFT:
				shiftPosition.setCol(0);
				break;
			}
			for (int positionAxis = 1; positionAxis <= 5; positionAxis += 2) {
				if (side == Side.UP || side == Side.DOWN) {
					shiftPosition.setCol(positionAxis);
				}
				else {
					shiftPosition.setRow(positionAxis);
				}
				if (!shiftPosition.equals(forbiddenPosition)) {
					positionList.add(shiftPosition);
				}
			}
		}
		return positionList;
	}

	private Move isFinishable(int playerID, Board oldBoard) {
		TreasureType treasure = TreasureType.valueOf("Start0" + playerID);
		for (Position shiftPosition : getShiftPositions(oldBoard)) {
			for (Card shiftRotation : new Card(oldBoard.getShiftCard()).getPossibleRotations()) {
				Board board = (Board) oldBoard.clone();
				MoveMessageType moveMessage = new MoveMessageType();
				moveMessage.setShiftCard(shiftRotation);
				moveMessage.setShiftPosition(shiftPosition);
				board.proceedShift(moveMessage);
				PositionType tPosition = board.findTreasure(treasure);
				if (board.pathPossible(board.findPlayer(playerID), tPosition)) {
					Move finalMove = new Move();
					finalMove.setShiftCard(shiftRotation);
					finalMove.setShiftPosition(shiftPosition);
					finalMove.setMovePosition(new Position(tPosition));
					return finalMove;
				}
			}
		}
		return null;
	}

	private Move calculateFinishMove(int playerID, Board oldBoard, List<TreasuresToGoType> ttgo, List<TreasureType> tfound, TreasureType treasure) {
		Move finalMove = isFinishable(playerID, oldBoard);
		if (finalMove == null) {
			finalMove = calculateMove(playerID, oldBoard, ttgo, tfound, treasure);
		}
		return finalMove;
	}

	@SuppressWarnings("unchecked")
	private Move calculateMove(int playerID, Board oldBoard, List<TreasuresToGoType> ttgo, List<TreasureType> tfound, TreasureType treasure) {
		canFindTreasure[playerID - 1] = false;
		ArrayList<Move> moves = new ArrayList<Move>();
		if (treasure == null) {
			ArrayList<TreasureType> notFound = (ArrayList<TreasureType>) allTreasures.clone();
			notFound.removeAll(tfound);
			notFound.remove(tempFound);
			notFound.remove(lamb.getTreasure());
			Collections.shuffle(notFound);
			treasure = notFound.get(0);
		}
		for (Position shiftPosition : getShiftPositions(oldBoard)) {
			for (Card shiftRotation : new Card(oldBoard.getShiftCard()).getPossibleRotations()) {
				Board board = (Board) oldBoard.clone();
				MoveMessageType moveMessage = new MoveMessageType();
				moveMessage.setShiftCard(shiftRotation);
				moveMessage.setShiftPosition(shiftPosition);
				board.proceedShift(moveMessage);
				PositionType tPosition = board.findTreasure(treasure);
				int boardValue = calculateBoardValue(playerID, board, ttgo, tfound, treasure);
				for (PositionType position : board.getAllReachablePositions(board.findPlayer(playerID))) {
					Move tempMove = new Move();
					int positionValue = calculatePositionValue(playerID, board, new Position(position), tPosition, treasure);
					tempMove.setShiftCard(shiftRotation);
					tempMove.setShiftPosition(shiftPosition);
					tempMove.setMovePosition(new Position(position));
					tempMove.setValue(boardValue + positionValue);
					moves.add(tempMove);
				}
			}
		}
		Move finalMove;
		if (!canFindTreasure[playerID - 1] && (depth < maxDepth) && !treasure.name().startsWith("Start0")) {
			depth++;
			int median = 0;
			for (Move m : moves) {
				median += m.getValue();
			}
			median = median / moves.size();
			median = (Collections.max(moves).getValue() + median) / 2;
			for (int i = 0; i < moves.size(); i++) {
				Move m = moves.get(i);
				if (m.getValue() > median) {
					calculateNewMove(playerID, oldBoard, m);
				}
				else {
					moves.remove(i);
					i--;
				}
			}
			depth--;
			finalMove = Collections.max(moves);
		}
		else {
			finalMove = Collections.max(moves);
		}
		return finalMove;
	}

	private void calculateNewMove(int playerID, Board oldBoard, Move oldMove) {
		//		System.out.println("CALCULATE NEW MOVE ------- " + playerID);
		Board board = (Board) oldBoard.clone();
		MoveMessageType moveMessage = new MoveMessageType();
		Move tempMove;
		moveMessage.setShiftCard(oldMove.getShiftCard());
		moveMessage.setShiftPosition(oldMove.getShiftPosition());
		board.proceedShift(moveMessage);
		movePlayer(playerID, board, oldMove.getMovePosition().getRow(), oldMove.getMovePosition().getCol());
		for (int i = 1; i < lamb.getPlayerCount(); i++) {
			int tempID = playerID + i;
			if (tempID > lamb.getPlayerCount()) {
				tempID -= lamb.getPlayerCount();
			}
			tempMove = calculateMove(tempID, board, lamb.getTreasuresToGo(), lamb.getTreasuresFound(), null);
			moveMessage = new MoveMessageType();
			moveMessage.setShiftCard(tempMove.getShiftCard());
			moveMessage.setShiftPosition(tempMove.getShiftPosition());
			board.proceedShift(moveMessage);
			movePlayer(tempID, board, tempMove.getMovePosition().getRow(), tempMove.getMovePosition().getCol());
		}
		tempMove = calculateMove(playerID, board, lamb.getTreasuresToGo(), lamb.getTreasuresFound(), lamb.getTreasure());
		oldMove.setValue(oldMove.getValue() + tempMove.getValue());
	}

	private int calculateBoardValue(int playerID, Board board, List<TreasuresToGoType> ttgo, List<TreasureType> tfound, TreasureType treasure) {
		int boardValue = 0;
		for (TreasuresToGoType ttg : ttgo) {
			PositionType playerPos = board.findPlayer(ttg.getPlayer());
			List<PositionType> reachablePos = board.getAllReachablePositions(playerPos);
			if (ttg.getPlayer() == playerID) {
				// Count own reachable treasures in relation to full number of remaining treasures
				int treasureCounter = 0;
				for (PositionType pos : reachablePos) {
					TreasureType ttype = board.getCard(pos.getRow(), pos.getCol()).getTreasure();
					if ((ttype != null) && (ttype != treasure) && !tfound.contains(ttype)) {
						treasureCounter++;
					}
				}
				boardValue += (int) (2.0 * treasureCounter / ttg.getTreasures()) * Points.TREASURE_REACHABLE.value();
			}
			else {
				if (ttg.getPlayer() == nextPlayer && ttg.getTreasures() == 1) {
					if (isFinishable(nextPlayer, board) != null) {
						boardValue += Points.OTHER_START_OPEN.value();
					}
				}
				// Count reachable treasures of opponent in relation to full number of remaining treasures
				int treasureCounter = 0;
				for (PositionType pos : reachablePos) {
					TreasureType ttype = board.getCard(pos.getRow(), pos.getCol()).getTreasure();
					if ((ttype != null) && (ttype != treasure) && !tfound.contains(ttype)) {
						treasureCounter++;
					}
				}
				boardValue -= (int) (1.0 * treasureCounter / ttg.getTreasures()) * Points.TREASURE_REACHABLE.value();
			}
		}
		if (board.findTreasure(treasure) == null) {
			boardValue += Points.TARGET_MISSING.value();
		}
		return boardValue;
	}

	private int calculatePositionValue(int playerID, Board board, Position position, PositionType tPosition, TreasureType treasure) {
		int positionValue = 2 * board.getAllReachablePositions(position).size();
		// Calculate the distance to currently needed target
		if (tPosition != null) {
			if (position.equals(tPosition)) {
				positionValue += Points.OWN_TARGET.value();
				canFindTreasure[playerID - 1] = true;
				tempFound.add(treasure);
			}
			else {
				positionValue += 2 * (12 - getDistance(position, tPosition));
			}
		}
		return positionValue;
	}

	private void movePlayer(int playerID, Board board, int row, int col) {
		Position p = new Position(board.findPlayer(playerID));
		List<Integer> pinPlayer = board.getCard(p.getRow(), p.getCol()).getPin().getPlayerID();
		pinPlayer.remove(pinPlayer.indexOf(playerID));
		board.getCard(row, col).getPin().getPlayerID().add(playerID);
		p = new Position(board.findPlayer(playerID));
	}

	private int getDistance(PositionType a, PositionType b) {
		if (a == null || b == null) {
			return -1;
		}
		else {
			return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
		}
	}
}

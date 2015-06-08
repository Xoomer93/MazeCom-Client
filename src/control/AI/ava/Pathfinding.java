package control.AI.ava;

import java.util.ArrayList;
import java.util.List;

import model.jaxb.CardType.Pin;
import model.jaxb.TreasureType;
import model.jaxb.TreasuresToGoType;
import control.AI.ava.WriteIntoFile;
import control.AI.ava.ownClasses.Board;
import control.AI.ava.ownClasses.Card;
import control.AI.ava.ownClasses.Position;

public class Pathfinding {

	private final int x = 7;
	private final int y = 7;
	private Board betterBoard;
	private final int MARKER1 = 1;
	private final int MARKER2 = 2;
	private int PlayerID;
	private static WriteIntoFile wif;
	private static WriteIntoFile possPos;
	private static WriteIntoFile wif_v2;
	private static WriteIntoFile wif_pin;
	private static WriteIntoFile wif_board;
	private List<TreasuresToGoType> list_treToGo;
	private static final String FILEPATH = "src/control/AI/ava/possPos.txt";
	private static int count = 0;

	/**
	 * PinPosHelp with diff from new PinPos to TreasurePos
	 */
	private List<PinPosHelp> list_PinPosHelp_v1;
	/**
	 * PinPosHelp with diff from new PinPos to the next Position with connection to the Treasure
	 */
	private List<PinPosHelp> list_PinPosHelp_v2;

	public Pathfinding(Board b, int PlayerID, List<TreasuresToGoType> list) {
		this(PlayerID);
		this.betterBoard = b;
		this.list_treToGo = list;
	}

	public Pathfinding(int PlayerID) {
		list_PinPosHelp_v1 = new ArrayList<>();
		list_PinPosHelp_v2 = new ArrayList<>();
		wif = new WriteIntoFile(WriteIntoFile.FILEPATH + WriteIntoFile.FILEEXTENSION);
		wif_v2 = new WriteIntoFile(WriteIntoFile.FILEPATH + "_v2" + WriteIntoFile.FILEEXTENSION);
		wif_pin = new WriteIntoFile(WriteIntoFile.FILEPATH + "_pin" + WriteIntoFile.FILEEXTENSION);
		wif_board = new WriteIntoFile(WriteIntoFile.FILEPATH + "_board" + WriteIntoFile.FILEEXTENSION);
		possPos = new WriteIntoFile(FILEPATH);
		possPos.clearFile();
		wif_pin.clearFile();
		wif_board.clearFile();
		this.PlayerID = PlayerID;
	}

	public void setBoard(Board b) {
		this.betterBoard = b;
	}

	public void setTreToGo(List<TreasuresToGoType> list) {
		this.list_treToGo = list;
	}

	public PinPosHelp start() {
		count = 0;
		// wif_board.write(betterBoard.toString());
		// wif_board.write(betterBoard.getShiftCard().toString());
		Position trePos = betterBoard.findTreasure(betterBoard.getTreasure());
		System.out.println(trePos);
		System.out.println(betterBoard.getTreasure());
		return ava(betterBoard.getPinPos(PlayerID));
	}

	public PinPosHelp ava(Position start) {
		list_PinPosHelp_v1.clear();
		list_PinPosHelp_v2.clear();
		// wif.write("PinPos: " + start + " TreasureToFindPos: " + trePos);
		// wif_v2.write("PinPos: " + start + " TreasureToFindPos: " + trePos);
		TreasureType tre = betterBoard.getTreasure();
		PinPosHelp pph = simpleSolution(tre);
		// System.out.println("CardHelp " + ch);
		if (pph == null) {
			System.out.println("no solution");
			pph = nextStep(tre);
		}
		return pph;
	}

	private PinPosHelp nextStep(TreasureType tre) {
		PinPosHelp pph;
		List<PinPosHelp> list = PinPosHelp.getSmallestDiff(list_PinPosHelp_v1);
		// wif.write(list.size());
		// for (PinPosHelp ph : list) {
		// wif.write(ph.toString());
		// }
		// wif.writeNewLine(2);
		List<PinPosHelp> list_rev = PinPosHelp.getSmallestDiff(list_PinPosHelp_v2);
		// wif_v2.write(list_rev.size());
		// for (PinPosHelp ph : list_rev) {
		// wif_v2.write(ph.toString());
		// }
		// wif_v2.writeNewLine(2);
		if (list.size() == 1 && list_rev.size() == 1) {
			if (list.get(0).equals(list_rev.get(0))) {
				pph = list.get(0);
			} else {
				// if (list_rev.get(0).getPinPos().diff(trePos) <= list.get(0).getDiff() + (4 - list_treToGo.size())) {
				// pph = list_rev.get(0);
				// } else {
				pph = list.get(0);
				// }
			}
		} else {
			// abfragen auf list.size == 0
			// if (list_rev.get(0).getPinPos().diff(trePos) <= list.get(0).getDiff() + (4 - list_treToGo.size())) {
			//
			// } else {
			//
			// }
			// list = chooseOrientation(list, trePos);
			// if (ph.getPinPos().isGlued()) {
			// pph = ph;
			// // TODO nach Ausrichtung der Schatzkarte richten
			// // TODO nur Rotationsunterschied? -> moeglichst unnuetz anbringen
			//
			// break;
			// }
			pph = list.get(0);
		}
		return pph;
	}

	private List<PinPosHelp> chooseOrientation(List<PinPosHelp> list, Position trePos) {
		List<PinPosHelp> tmp = new ArrayList<>();
		for (PinPosHelp pph : list) {
			Position p = pph.getPinPos();
			Card c = pph.getCardHelp().getC();
			if (trePos.getCol() == p.getCol()) {
				// if()
			} else if (trePos.getRow() == p.getRow()) {

			}
		}
		return list;
	}

	private List<PinPosHelp> shortestPath(List<Position> list, Position trePos) {
		List<PinPosHelp> pos = new ArrayList<>();
		// wif.write("------------------- " + list.size());
		double diff = x * y;
		for (Position p : list) {
			double tmp = p.diff(trePos);
			if (tmp < diff) {
				diff = tmp;
				pos.clear();
				pos.add(new PinPosHelp(p, diff));
				// wif.write(new PinPosHelp(p, diff).toString());
			} else if (tmp == diff) {
				pos.add(new PinPosHelp(p, tmp));
				// wif.write("add " + new PinPosHelp(p, diff).toString());
			}
		}
		// wif.write("-------------------\n");
		return pos;
	}

	private List<PinPosHelp> shortestPath(List<Position> list, List<Position> list_rev) {
		List<PinPosHelp> pos = new ArrayList<>();
		// wif_v2.write("------------------- " + list.size() + " " + list_rev.size());
		double diff = x * y;
		for (Position p : list) {
			for (Position pr : list_rev) {
				double tmp = p.diff(pr);
				if (tmp < diff) {
					diff = tmp;
					pos.clear();
					pos.add(new PinPosHelp(p, diff));
					// wif_v2.write(new PinPosHelp(p, diff).toString());
				} else if (tmp == diff) {
					pos.add(new PinPosHelp(p, tmp));
					// wif_v2.write("add " + new PinPosHelp(p, diff).toString());
				}
			}
		}
		// wif_v2.write("-------------------");
		return pos;
	}

	private PinPosHelp simpleSolution(TreasureType tre) {
		possPos.write(betterBoard.toString());
		List<Position> l = new ArrayList<>();
		List<Position> l_rev = new ArrayList<>();
		Board board;
		Position trePos;
		Position oldPinPos;
		Position shiftPos;
		CardHelp ch;
		// wif_pin.write("ShiftCard:");
		// wif_pin.write(betterBoard.getShiftCard().toString());

		List<Card> list_c = betterBoard.getShiftCard().getPossibleRotations();
		System.out.println(list_c.size() * 3 * 2 * 2);
		for (Card c : list_c) {
			wif_pin.write(c.toString());
			for (int i = 1; i < 6; i += 2) {
				for (int k = 0; k < 7; k += 6) {
					for (int j = 0; j < 2; j++) {
						board = (Board) betterBoard.clone();
						c.setPin(new Pin());
						shiftPos = new Position(k + (i - k) * j, i + (k - i) * j);
						if (betterBoard.getForbidden() != null && shiftPos.equals(new Position(betterBoard.getForbidden()))) {
							wif_pin.write("ForbiddenPos: " + betterBoard.getForbidden());
							continue;
						}
						board.proceedShift(shiftPos, new Card(c));
						oldPinPos = board.getPinPos(PlayerID);
						trePos = board.findTreasure(tre);
						if(trePos == null){
							continue;
						}
						l.clear();
						l = findPossiblePos(board, l, oldPinPos);
						l_rev.clear();
						l_rev = findPossiblePos(board, l_rev, trePos);
						ch = new CardHelp(c, shiftPos);
						if (l.contains(trePos)) {
							wif.write(board.toString());
							wif.write(oldPinPos.toString());
							wif.write("************************************************\n" + ch.toString() + "\n************************************************");
							wif_pin.write("Found it: " + trePos);
							possPos.write("found it");
							possPos.write(ch.toString());
							wif_v2.write(board.toString());
							wif_v2.write(betterBoard.getPinPos(PlayerID) + " " + oldPinPos.toString());
							wif_v2.write("************************************************\n" + ch.toString() + "\n************************************************");
							return new PinPosHelp(trePos, ch);
						}
						calcData(oldPinPos, trePos, ch, l, l_rev);
					}
				}
			}
		}
		return null;
	}

	private void calcData(Position start, Position trePos, CardHelp ch, List<Position> list, List<Position> list_rev) {
		// wif.write(list.size() + " " + list_rev.size());
		List<PinPosHelp> lpph = shortestPath(list, trePos);
		for (PinPosHelp pph : lpph) {
			pph.setCardHelp(ch);
		}
		possPos.writeList(lpph);
		possPos.writeNewLine(1);
		list_PinPosHelp_v1.addAll(lpph);
		lpph = shortestPath(list, list_rev);
		for (PinPosHelp pph : lpph) {
			pph.setCardHelp(ch);
		}
		list_PinPosHelp_v2.addAll(lpph);
	}

	private List<Position> findPossiblePos(Board b, List<Position> list, Position start) {
		count++;
		Position p;
		int col_start = start.getCol();
		int row_start = start.getRow();
		Card c = new Card(b.getCard(row_start, col_start));
		if (col_start < x - 1) {
			p = new Position(row_start, col_start + 1);
			if (!list.contains(p) && b.getCard(row_start, col_start + 1).getOpenings().isLeft() && c.getOpenings().isRight()) {
				list.add(p);
				// possPos.write("right " + list.size());
				list = (findPossiblePos(b, list, p));
			}
		}
		if (col_start > 0) {
			p = new Position(row_start, col_start - 1);
			if (!list.contains(p) && b.getCard(row_start, col_start - 1).getOpenings().isRight() && c.getOpenings().isLeft()) {
				list.add(p);
				// possPos.write("left " + list.size());
				list = (findPossiblePos(b, list, p));
			}
		}
		if (row_start > 0) {
			p = new Position(row_start - 1, col_start);
			if (!list.contains(p) && b.getCard(row_start - 1, col_start).getOpenings().isBottom() && c.getOpenings().isTop()) {
				list.add(p);
				// possPos.write("up " + list.size());
				list = (findPossiblePos(b, list, p));
			}
		}
		if (row_start < y - 1) {
			p = new Position(row_start + 1, col_start);
			if (!list.contains(p) && b.getCard(row_start + 1, col_start).getOpenings().isTop() && c.getOpenings().isBottom()) {
				list.add(p);
				// possPos.write("down " + list.size());
				list = (findPossiblePos(b, list, p));
			}
		}
		return list;
	}

	public int[][] findPath(Position start, Position end) {
		int[][] reachable = new int[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				reachable[i][j] = 0;
			}
		}
		reachable[start.getRow()][start.getCol()] = MARKER1;
		if (!tilTheEnd(reachable, start, end, MARKER1)) {
			reachable[end.getRow()][end.getCol()] = MARKER2;
			tilTheEnd(reachable, end, start, MARKER2);
			// Path p = new Path(reachable);

			// card is glued? -> false : shift to the other side?
		} else {
			// TODO
			// find possible position for shifting Card without destroying the
			// calculated path
			// 0: find alternative
			// 1: block other player if possible
		}
		return reachable;
	}

	public static class PinPosHelp {
		private Position pinPos;
		private double diff = Double.MAX_VALUE;
		private CardHelp ch;

		public PinPosHelp(Position pinPos, double diff) {
			this.pinPos = pinPos;
			this.diff = diff;
		}

		public PinPosHelp(Position pinPos, CardHelp ch) {
			this.pinPos = pinPos;
			this.ch = ch;
		}

		public Position getPinPos() {
			return pinPos;
		}

		public double getDiff() {
			return diff;
		}

		public static List<PinPosHelp> getSmallestDiff(List<PinPosHelp> list) {
			List<PinPosHelp> res = new ArrayList<>();
			double min = Double.MAX_VALUE;
			double diff;
			for (PinPosHelp pph : list) {
				diff = pph.getDiff();
				if (diff == min) {
					res.add(pph);
				}
				if (diff < min) {
					min = diff;
					res.clear();
					res.add(pph);
				}
			}
			return res;
		}

		public CardHelp getCardHelp() {
			return ch;
		}

		public void setCardHelp(CardHelp ch) {
			this.ch = ch;
		}

		public String toString() {
			return "PinPos: " + pinPos + " " + ch;
		}

		public String debug() {
			if (ch != null) {
				return "PinPos: " + pinPos + " " + ch.debug() + "\t" + diff;
			} else {
				return "PinPos: " + pinPos + " " + " null\t" + diff;
			}
		}

		public boolean equals(PinPosHelp pph) {
			return pph.ch.equals(this.getCardHelp()) && this.pinPos.equals(pph.getPinPos());
		}

		public boolean equalsWithoutRot(PinPosHelp pph) {
			return pph.getCardHelp().equalsWithoutRot(this.ch) && this.pinPos.equals(pph.getPinPos());
		}
	}

	public static class CardHelp {
		private Card c;
		private Position p;

		public CardHelp(Card c, Position p) {
			this.c = c;
			this.p = p;
		}

		public Card getC() {
			return c;
		}

		public Position getP() {
			return p;
		}

		public String toString() {
			return "CardPos: " + p + "\n" + c;
		}

		public String debug() {
			return "CardPos: " + p + " Card: " + c.getShape() + c.getOrientation().value();
		}

		public boolean equals(CardHelp ch) {
			return this.c.equals(ch.getC()) && this.p.equals(ch.getP());
		}

		public boolean equalsWithoutRot(CardHelp ch) {
			return this.c.getShape() == ch.getC().getShape() && this.p.equals(ch.getP());
		}
	}

	private boolean tilTheEnd(int[][] reachable, Position start, Position end, int marker) {
		boolean res = false;
		int col_start = start.getCol();
		int row_start = start.getRow();
		int col_end = end.getCol();
		int row_end = end.getRow();
		Card c = new Card(betterBoard.getCard(row_start, col_start));
		if (col_start < x - 1) {
			if (betterBoard.getCard(row_start, col_start + 1).getOpenings().isLeft() && c.getOpenings().isRight() && 0 == reachable[row_start][col_start + 1]) {
				if (row_start == row_end && col_start + 1 == col_end) {
					System.out.println("1 " + row_start + "/" + (col_start + 1));
					return true;
				}
				reachable[row_start][col_start + 1] = marker;
				res = tilTheEnd(reachable, new Position(row_start, col_start + 1), end, marker) || res;
			}
		}
		if (col_start > 0) {
			if (betterBoard.getCard(row_start, col_start - 1).getOpenings().isRight() && c.getOpenings().isLeft() && 0 == reachable[row_start][col_start - 1]) {
				if (row_start == row_end && col_start - 1 == col_end) {
					System.out.println("2 " + row_start + "/" + (col_start + 1));
					return true;
				}
				reachable[row_start][col_start - 1] = marker;
				res = tilTheEnd(reachable, new Position(row_start, col_start - 1), end, marker) || res;
			}
		}
		if (row_start > 0) {
			if (betterBoard.getCard(row_start - 1, col_start).getOpenings().isBottom() && c.getOpenings().isTop() && reachable[row_start - 1][col_start] == 0) {
				if (row_start - 1 == row_end && col_start == col_end) {
					System.out.println("3 " + (row_start - 1) + "/" + col_start);
					return true;
				}
				reachable[row_start - 1][col_start] = marker;
				res = tilTheEnd(reachable, new Position(row_start - 1, col_start), end, marker) || res;
			}
		}
		if (row_start < y - 1) {
			if (betterBoard.getCard(row_start + 1, col_start).getOpenings().isTop() && c.getOpenings().isBottom() && reachable[row_start + 1][col_start] == 0) {
				if (row_start + 1 == row_end && col_start == col_end) {
					System.out.println("4 " + (row_start + 1) + "/" + col_start);
					return true;
				}
				reachable[row_start + 1][col_start] = marker;
				res = tilTheEnd(reachable, new Position(row_start + 1, col_start), end, marker) || res;
			}
		}

		return res;
	}

}

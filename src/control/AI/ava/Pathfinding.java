package control.AI.ava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.jaxb.CardType.Pin;
import model.jaxb.TreasureType;
import model.jaxb.TreasuresToGoType;
import control.AI.ava.ownClasses.Board;
import control.AI.ava.ownClasses.Card;
import control.AI.ava.ownClasses.Position;

public class Pathfinding {
	// TODO schleifenabbruch?
	// TODO lastChance fuer die anderen spieler. seal startfeld
	private final int x = 7;
	private final int y = 7;
	private Board betterBoard;
	private int PlayerID;
	private List<TreasuresToGoType> list_treToGo;
	private Integer[] nextPlayer;
	private final int row0 = 01;
	private final int row6 = 06;
	private final int col0 = 10;
	private final int col6 = 60;

	/**
	 * PinPosHelp with diff from new PinPos to TreasurePos
	 */
	private List<PinPosHelp> list_PinPosHelp_v1;
	/**
	 * PinPosHelp with diff from new PinPos to the next Position with connection to the Treasure
	 */
	private Map<CardHelp, ReverseHelp> map_PinPosHelp_v2;

	private List<TreasureType> list_foundTreasures;

	public Pathfinding(Board b, int PlayerID, List<TreasuresToGoType> list) {
		this(PlayerID);
		this.betterBoard = b;
		this.list_treToGo = list;
	}

	public Pathfinding(int PlayerID) {
		list_PinPosHelp_v1 = new ArrayList<>();
		map_PinPosHelp_v2 = new HashMap<>();
		list_foundTreasures = new ArrayList<>();
		this.PlayerID = PlayerID;
	}

	public void setBoard(Board b) {
		this.betterBoard = b;
	}

	public void setTreToGo(List<TreasuresToGoType> list) {
		this.list_treToGo = list;

		int i = PlayerID + 1;
		nextPlayer = new Integer[list.size() - 1];
		for (int j = 0; j < nextPlayer.length; j++) {
			if (i > list.size()) {
				i = 1;
			}
			nextPlayer[j] = i;
			i++;
		}
	}

	public void setFoundTreasures(List<TreasureType> foundTreasures) {
		this.list_foundTreasures = foundTreasures;
	}

	/**
	 * start calculating the estimated best turn
	 * 
	 * @return best turn as PinPosHelp
	 */
	public PinPosHelp start() {
		return ava();
	}

	/**
	 * start Ava's Algorithm
	 * 
	 * @return PinPosHelp with shiftPosition, shiftCard and new PinPos
	 */
	private PinPosHelp ava() {
		list_PinPosHelp_v1.clear();
		map_PinPosHelp_v2.clear();

		TreasureType tre = betterBoard.getTreasure();
		PinPosHelp pph = checkNextTurn(tre);
		if (pph != null) {
			return pph;
		}

		List<PinPosHelp> l_pph = simpleSolution(tre, PlayerID);

		if (l_pph.size() == 0) {
			pph = nextStep(tre, PlayerID);
		} else {
			if (l_pph.size() == 1) {
				return l_pph.get(0);
			}
			if (nextPlayer.length > 1) {
				checkOtherPlayer(l_pph);
			}
			l_pph = beAnnoying(l_pph);
			if (l_pph.size() == 1) {
				return l_pph.get(0);
			}
			pph = sealAway(l_pph).get(0);
		}
		return pph;
	}

	private List<PinPosHelp> checkOtherPlayer(List<PinPosHelp> list_pph) {
		for (TreasuresToGoType ttgt : list_treToGo) {
			if (ttgt.getPlayer() == nextPlayer[1] && ttgt.getTreasures() == 1) {
				list_pph = sealAway(list_pph, nextPlayer[1]);
				break;
			}
		}
		return list_pph;
	}
	
	/**
	 * proceed turn that the next opponent is sealed at his current position
	 * 
	 * @param list
	 *            of solutions
	 * @return list of solutions with sealed factor
	 */
	private List<PinPosHelp> sealAway(List<PinPosHelp> list) {
		// TODO
		return list;
	}

	/**
	 * proceed turn that the given opponent is sealed at his current position
	 * 
	 * @param list
	 *            of solutions
	 * @return list of solutions with sealed factor
	 */
	private List<PinPosHelp> sealAway(List<PinPosHelp> list, int ID) {
		// TODO
		return list;
	}

	/**
	 * proceed turn with hopefully bad consequences for the opponent
	 * 
	 * @param list
	 *            of solutions
	 * @return list of solutions with annoying factor
	 */
	private List<PinPosHelp> beAnnoying(List<PinPosHelp> l_sol) {
		List<PinPosHelp> tmp = new ArrayList<>();
		int count = 0;
		for (PinPosHelp pp : l_sol) {
			int c = 0;
			Board b = (Board) betterBoard.clone();
			CardHelp ch = pp.getCardHelp();
			Card shift = ch.getC();
			Position shiftPos = ch.getP();
			b.proceedShift(shiftPos, new Card(shift));
			outer: for (int i = 1; i < nextPlayer.length; i++) {
				Position next = b.getPinPos(nextPlayer[i]);
				if (shiftPos.getRow() % 6 != 0 && shiftPos.getRow() == next.getRow() || (shiftPos.getCol() % 6 != 0 && shiftPos.getCol() == next.getCol())) {
					if (i == 1 && analyse(b, next)) {
						break outer;
					}
					c += (nextPlayer.length - i + 1);
				}
			}
			if (c == count) {
				tmp.add(pp);
			}
			if (c > count) {
				count = c;
				tmp.clear();
				tmp.add(pp);
			}
		}
		return tmp;
	}

	/**
	 * analyse if the opponent shift is estimated good or bad for him
	 * 
	 * @param board
	 * @param position
	 *            of the player to analyse
	 * @return
	 */
	private boolean analyse(Board b, Position player) {
		List<Position> list = new ArrayList<>();
		list = findPossiblePos(b, list, player);
		final int number = 24;
		double pro_treFound = 100. / number * list_foundTreasures.size();

		int treFound = 0;
		int treFind = 0;
		for (Position p : list) {
			TreasureType t = b.getCard(p.getRow(), p.getCol()).getTreasure();
			if (t != null) {
				if (list_foundTreasures.contains(t)) {
					treFound++;
				} else {
					treFind++;
				}
			}
		}

		if (treFound > treFind && treFind <= pro_treFound * (treFound + treFind)) {
			return true;
		} else {
			return false;
		}
	}

	private List<CardHelp> lastChance() {
		int nextPlayer = this.nextPlayer[0];
		List<Position> l = new ArrayList<>();
		List<CardHelp> l_sol = new ArrayList<>();
		Board board;
		Position end = null;

		switch (nextPlayer) {
		case 1:
			end = new Position(0, 0);
			break;
		case 4:
			end = new Position(6, 6);
			break;
		case 2:
			end = new Position(0, 6);
			break;
		case 3:
			end = new Position(6, 0);
			break;
		}

		List<Card> list_c = betterBoard.getShiftCard().getPossibleRotations();
		boolean pos = true;
		for (Card c : list_c) {
			for (int i = 1; i < 6; i += 2) {
				for (int k = 0; k < 7; k += 6) {
					for (int j = 0; j < 2; j++) {
						board = (Board) betterBoard.clone();
						Position shiftPos = new Position(k + (i - k) * j, i + (k - i) * j);
						if (betterBoard.getForbidden() != null && shiftPos.equals(new Position(betterBoard.getForbidden()))) {
							continue;
						}
						board.proceedShift(shiftPos, new Card(c));
						Position oldPinPos = board.getPinPos(nextPlayer);
						l.clear();
						l = findPossiblePos(board, l, oldPinPos);
						CardHelp ch = new CardHelp(c, shiftPos);
						if (!l.contains(end)) {
							pos = true;
							List<Card> list_c2 = betterBoard.getCard(shiftPos.getOpposite()).getPossibleRotations();
							outer: for (Card c2 : list_c2) {
								for (int i2 = 1; i2 < 6; i2 += 2) {
									for (int k2 = 0; k2 < 7; k2 += 6) {
										for (int j2 = 0; j2 < 2; j2++) {
											Board board2 = (Board) board.clone();
											Position shiftPos2 = new Position(k2 + (i2 - k2) * j2, i2 + (k2 - i2) * j2);
											if (shiftPos2.equals(new Position(board.getForbidden()))) {
												continue;
											}
											board2.proceedShift(shiftPos2, new Card(c2));
											Position oldPinPos2 = board2.getPinPos(nextPlayer);
											l.clear();
											l = findPossiblePos(board2, l, oldPinPos2);
											if (l.contains(end)) {
												pos = false;
												break outer;
											}
										}
									}
								}
							}
							if (pos) {
								l_sol.add(ch);
							}
						}
					}
				}
			}
		}
		return l_sol;
	}

	/**
	 * tries to make the best of the given PinPosHelp. 1. chooseOrientation 2. beAnnoying 3. sealAway
	 * 
	 * @param list
	 * @return
	 */
	private PinPosHelp bestMove(List<PinPosHelp> list) {
		if (list.size() == 1) {
			return list.get(0);
		} else {
			list = chooseOrientation(list);
			if (list.size() == 1) {
				return list.get(0);
			} else {
				List<PinPosHelp> list_tmp = beAnnoying(list);
				if (list_tmp.size() == 1) {
					return list_tmp.get(0);
				} else {
					if (list_tmp.size() == 0) {
						list.get(0);
					}
					list_tmp = sealAway(list_tmp);
					return list.get(0);
				}
			}
		}

	}

	private PinPosHelp emergencyPlan(List<CardHelp> list, TreasureType tre) {
		List<PinPosHelp> list_pph = simpleSolution(list, tre, PlayerID);
		if (list_pph.size() == 0) {
			list_pph = shortestPath(list);
			if (list_pph.size() == 0) {
				Position p = emergencyTreIsOnShift(list);
				list_pph = nearBy(p, list);
			}
		}
		return bestMove(list_pph);
	}

	/**
	 * find the nearest pin position for the given position p and the list of cardHelp
	 * 
	 * @param p
	 * @param list_ch
	 * @return
	 */
	private List<PinPosHelp> nearBy(Position p, List<CardHelp> list_ch) {
		List<PinPosHelp> list_pph = new ArrayList<>();
		int diff = Integer.MAX_VALUE;
		for (CardHelp ch : list_ch) {
			Board board = (Board) betterBoard.clone();
			board.proceedShift(ch.getP(), new Card(ch.getC()));
			Position pinPos = board.getPinPos(PlayerID);
			int tmp = pinPos.diff(p);
			if (tmp < diff) {
				list_pph.clear();
				list_pph.add(new PinPosHelp(p, pinPos, ch));
				diff = tmp;
			} else {
				if (tmp == diff) {
					list_pph.add(new PinPosHelp(p, pinPos, ch));
				}
			}
		}
		return list_pph;
	}

	/**
	 * returns the position where the shiftcard with the treasure will probably shift in
	 * 
	 * @param list
	 * @return
	 */
	private Position emergencyTreIsOnShift(List<CardHelp> list) {
		TreasureType tre = null;
		switch (this.nextPlayer[0]) {
		case 1:
			tre = betterBoard.getCard(0, 0).getTreasure();
			break;
		case 4:
			tre = betterBoard.getCard(6, 6).getTreasure();
			break;
		case 2:
			tre = betterBoard.getCard(0, 6).getTreasure();
			break;
		case 3:
			tre = betterBoard.getCard(6, 0).getTreasure();
			break;
		}

		HashMap<Integer, Integer> map = new HashMap<>();

		map.put(row0, 0);
		map.put(row6, 0);
		map.put(col0, 0);
		map.put(col6, 0);
		for (CardHelp ch : list) {
			Board board = (Board) betterBoard.clone();
			board.proceedShift(ch.getP(), new Card(ch.getC()));
			PinPosHelp pph = nextStep(tre, this.nextPlayer[0]);
			Position pos = pph.getCardHelp().getP();
			if (pos.getRow() == 0) {
				map.put(row0, map.get(row0));
			} else if (pos.getCol() == 0) {
				map.put(col0, map.get(col0));
			} else if (pos.getRow() == 6) {
				map.put(row6, map.get(row6));
			} else if (pos.getCol() == 6) {
				map.put(col6, map.get(col6));
			}
		}

		int max1 = 0;
		int max2 = 0;
		List<Integer> key1 = new ArrayList<>();
		List<Integer> key2 = new ArrayList<>();
		for (Integer i : map.keySet()) {
			if (map.get(i) > max1) {
				key2 = key1;
				max2 = max1;
				key1.clear();
				key1.add(i);
				max1 = map.get(i);
			}
			if (map.get(i) == max1) {
				key1.add(i);
			}
			if (map.get(i) == max2) {
				key2.add(i);
			}
		}

		int pos1;
		int pos2;
		switch (key1.size()) {
		case 1:
			pos1 = key1.get(0);
			switch (key2.size()) {
			case 1:
				if ((key2.get(0) + pos1) % 7 == 0) {
					pos2 = 3;
				} else {
					pos2 = key2.get(0);
				}
				break;
			case 2:
				if ((key2.get(0) + key2.get(1)) % 7 == 0) {
					pos2 = 3;
				} else {
					if ((key2.get(0) + pos1) % 7 == 0) {
						pos2 = key2.get(1);
					} else {
						pos2 = key2.get(0);
					}
				}
				break;
			default:
				pos2 = 3;
			}

		case 3:
			pos2 = 3;
			if ((key1.get(0) + key1.get(1) % 7 == 0)) {
				pos1 = key1.get(2);
			} else {
				if ((key1.get(0) + key1.get(2) % 7 == 0)) {
					pos1 = key1.get(1);
				} else {
					pos1 = key1.get(0);
				}
			}
			break;
		case 2:
			if ((key1.get(0) + key1.get(1) % 7 == 0)) {
				pos1 = 3;
				pos2 = 3;
			} else {
				pos1 = key1.get(0);
				pos2 = key2.get(1);
			}
			break;
		default:
			pos1 = 3;
			pos2 = 3;
		}

		Position p = new Position(3, 3);
		switch (pos1) {
		case row0:
			p.setRow(0);
			break;
		case row6:
			p.setRow(6);
			break;
		case col0:
			p.setCol(0);
			break;
		case col6:
			p.setCol(6);
			break;
		}
		switch (pos2) {
		case row0:
			p.setRow(0);
			break;
		case row6:
			p.setRow(6);
			break;
		case col0:
			p.setCol(0);
			break;
		case col6:
			p.setCol(6);
			break;
		}

		return p;
	}

	/**
	 * check if the next player has one treasure left and if tries to deny the win
	 * 
	 * @param tre
	 * @return
	 */
	private PinPosHelp checkNextTurn(TreasureType tre) {
		if (list_treToGo.get(nextPlayer[0] - 1).getTreasures() == 1) {
			List<CardHelp> list_ch = lastChance();
			switch (list_ch.size()) {
			case 0:
				System.out.println("Player " + nextPlayer[0] + " will win. I (" + PlayerID + ") admit my defeat");
				List<PinPosHelp> l_pph = simpleSolution(tre, PlayerID);

				if (l_pph.size() == 0) {
					return nextStep(tre, PlayerID);
				} else {
					if (l_pph.size() == 1) {
						return l_pph.get(0);
					}
					l_pph = beAnnoying(l_pph);
					if (l_pph.size() == 1) {
						return l_pph.get(0);
					}
					return sealAway(l_pph).get(0);
				}
			case 1:
				List<PinPosHelp> list_pph = simpleSolution(list_ch, tre, PlayerID);
				switch (list_pph.size()) {
				case 0:
					list_pph = shortestPath(list_ch);
					if (list_pph.size() == 0) {
						list_pph = nearBy(new Position(3, 3), list_ch);
					}
					break;
				case 1:
					return list_pph.get(0);
				}
				return bestMove(list_pph);
			default:
				return emergencyPlan(list_ch, tre);
			}
		}
		return null;
	}

	/**
	 * find best position to find the treasure in the next step
	 * 
	 * @param treasure
	 * @return PinPosHelp
	 */
	private PinPosHelp nextStep(TreasureType tre, int id) {
		List<PinPosHelp> list = PinPosHelp.getSmallestDiff(list_PinPosHelp_v1);
		Map<CardHelp, ReverseHelp> map = ReverseHelp.getValueableDiff(map_PinPosHelp_v2, list_treToGo.size());
		switch (list.size()) {
		case 1:
			return list.get(0);
		case 0:
			System.out.println("player" + PlayerID + " sonderfall " + list_PinPosHelp_v1.size());
			// TODO annoy or look forward
			// TODO endlosschleife
			return new PinPosHelp(null, betterBoard.getPinPos(id), new CardHelp(betterBoard.getShiftCard(), new Position(0, 1)));
		default:
			List<PinPosHelp> tmp_remove = new ArrayList<>();
			for (PinPosHelp pp : list) {
				if (!map.containsKey(pp.getCardHelp())) {
					tmp_remove.add(pp);
				}
			}
			list.removeAll(tmp_remove);
			if (list.size() == 0) {
				list.addAll(tmp_remove);
				list = chooseOrientation(list);
				if (list.size() == 1) {
					return list.get(0);
				}
			}
			if (list.size() == 1) {
				return bestMove(list);
			} else {
				list.addAll(tmp_remove);
				return bestMove(list);
			}
		}
	}

	/**
	 * take a card where the openings look in the direction of the treasure and it's openings.
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<PinPosHelp> chooseOrientation(List<PinPosHelp> list) {
		ArrayList[] arr = { new ArrayList<PinPosHelp>(), new ArrayList<PinPosHelp>(), new ArrayList<PinPosHelp>(), new ArrayList<PinPosHelp>(), new ArrayList<PinPosHelp>(),
				new ArrayList<PinPosHelp>() };
		for (PinPosHelp pph : list) {
			Position p = pph.getPinPos();
			Board b = (Board) betterBoard.clone();
			b.proceedShift(pph.getCardHelp().getP(), new Card(pph.getCardHelp().getC()));
			Card c = b.getCard(pph.getPinPos());
			Position trePos = betterBoard.findTreasure(betterBoard.getTreasure());
			if (trePos == null) {
				continue;
			}
			Card tre = new Card(betterBoard.getCard(trePos));
			int count = 0;
			if (pph.getTrePos().getCol() <= p.getCol() && c.getOpenings().isTop()) {
				count++;
				if (tre.getOpenings().isBottom()) {
					count++;
				}
			}
			if (pph.getTrePos().getCol() >= p.getCol() && c.getOpenings().isBottom()) {
				count++;
				if (tre.getOpenings().isTop()) {
					count++;
				}
			}
			if (pph.getTrePos().getRow() <= p.getRow() && c.getOpenings().isLeft()) {
				count++;
				if (tre.getOpenings().isRight()) {
					count++;
				}
			}
			if (pph.getTrePos().getRow() >= p.getRow() && c.getOpenings().isRight()) {
				count++;
				if (tre.getOpenings().isLeft()) {
					count++;
				}
			}
			if (count > 0) {
				((ArrayList<PinPosHelp>) arr[count - 1]).add(pph);
			}
		}

		for (int i = arr.length - 1; i >= 0; i--) {
			if (arr[i].size() != 0) {
				return arr[i];
			}
		}
		return list;
	}

	private int chooseOrientation(Position start, Position end, Board board) {
		Card cstart = new Card(board.getCard(start));
		Card cend = new Card(board.getCard(end));
		int count = 0;
		if (start.getCol() <= end.getCol() && cstart.getOpenings().isTop()) {
			count++;
			if (cend.getOpenings().isBottom()) {
				count++;
			}
		}
		if (start.getCol() >= end.getCol() && cstart.getOpenings().isBottom()) {
			count++;
			if (cend.getOpenings().isTop()) {
				count++;
			}
		}
		if (start.getRow() <= end.getRow() && cstart.getOpenings().isLeft()) {
			count++;
			if (cend.getOpenings().isRight()) {
				count++;
			}
		}
		if (start.getRow() >= end.getRow() && cstart.getOpenings().isRight()) {
			count++;
			if (cend.getOpenings().isLeft()) {
				count++;
			}
		}

		return count;
	}

	/**
	 * calculate the best pin position for the given Card Help, does not check if the treasure is reachable
	 * 
	 * @param list_ch
	 * @return List<PinPosHelp>
	 */
	private List<PinPosHelp> shortestPath(List<CardHelp> list_ch) {
		List<PinPosHelp> l_sol = new ArrayList<>();
		List<Position> list_pos = new ArrayList<>();
		Position trePos;
		int min = Integer.MAX_VALUE;
		for (CardHelp ch : list_ch) {
			Board b = (Board) betterBoard.clone();
			b.proceedShift(ch.getP(), new Card(ch.getC()));
			trePos = b.findTreasure(betterBoard.getTreasure());
			if (trePos == null) {
				continue;
			}
			list_pos.clear();
			list_pos = findPossiblePos(b, list_pos, b.getPinPos(PlayerID));
			for (Position p : list_pos) {
				int diff = diff(p, trePos, b);
				if (diff < min) {
					min = diff;
					l_sol.clear();
					l_sol.add(new PinPosHelp(trePos, p, ch));
				} else {
					if (diff == min) {
						l_sol.add(new PinPosHelp(trePos, p, ch));
					}
				}
			}
		}
		return l_sol;
	}

	// private List<PinPosHelp> shortestPath_breakLoop(List<Position> list, Position trePos, CardHelp ch, Board board) {
	// List<PinPosHelp> pos = new ArrayList<>();
	// int diff = x * y;
	// for (Position p : list) {
	// int tmp = diff(p, trePos, board);
	// if (tmp < diff) {
	// diff = tmp;
	// pos.clear();
	// pos.add(new PinPosHelp(trePos, p, ch, diff));
	// } else if (tmp == diff) {
	// pos.add(new PinPosHelp(trePos, p, ch, tmp));
	// }
	// }
	// return pos;
	// }

	private int diff(Position start, Position end, Board b) {
		int diff = (Math.abs(start.getRow() - end.getRow()) + Math.abs(start.getCol() - end.getCol()));
		int chooseOrientation = chooseOrientation(start, end, b);
		return diff + 6 - chooseOrientation;
	}

	/**
	 * calculate the shortest path between all positions in list and the position of the treasure
	 * 
	 * @param list
	 *            <Position>
	 * @param treasurePosition
	 * @param CardHelp
	 *            (source of the current situation)
	 * @return list of PinPosHelp (include CardHelp, treasurePosition, a position of list, the calculated difference)
	 */
	private List<PinPosHelp> shortestPath(List<Position> list, Position trePos, CardHelp ch, Board b) {
		List<PinPosHelp> pos = new ArrayList<>();
		int diff = x * y;
		for (Position p : list) {
			int tmp = diff(p, trePos, b);
			if (tmp < diff) {
				diff = tmp;
				pos.clear();
				pos.add(new PinPosHelp(trePos, p, ch, diff));
			} else if (tmp == diff) {
				pos.add(new PinPosHelp(trePos, p, ch, tmp));
			}
		}
		return pos;
	}

	/**
	 * calculate the shortest Path between each position in list and each position in list_rev
	 * 
	 * @param list
	 *            <Position>
	 * @param list_rev
	 *            <Position>
	 * @param CardHelp
	 *            (source of the current situation)
	 * @param trePos
	 * @return
	 */
	private Map<CardHelp, ReverseHelp> shortestPath(List<Position> list, List<Position> list_rev, CardHelp ch, Position trePos, Board b) {
		Map<CardHelp, ReverseHelp> pos = new HashMap<>();
		int diff = x * y;
		for (Position p : list) {
			for (Position pr : list_rev) {
				int tmp = diff(pr, p, b);
				if (tmp < diff) {
					diff = tmp;
					pos.clear();
					pos.put(ch, new ReverseHelp(p, diff, trePos.diff(pr)));
				} else if (tmp == diff) {
					pos.put(ch, new ReverseHelp(p, diff, trePos.diff(pr)));
				}
			}
		}
		return pos;
	}

	// private Map<CardHelp, ReverseHelp> shortestPath_breakLoop(List<Position> list, List<Position> list_rev, CardHelp ch, Position trePos) {
	// Map<CardHelp, ReverseHelp> pos = new HashMap<>();
	// int diff = x * y;
	// for (Position p : list) {
	// for (Position pr : list_rev) {
	// int tmp = p.diff(pr);
	// if (tmp < diff) {
	// diff = tmp;
	// pos.clear();
	// pos.put(ch, new ReverseHelp(p, diff, trePos.diff(pr)));
	// } else if (tmp == diff) {
	// pos.put(ch, new ReverseHelp(p, diff, trePos.diff(pr)));
	// }
	// }
	// }
	// return pos;
	// }

	/**
	 * check if with the given CardHelp the treasure is reachable
	 * 
	 * @param list_ch
	 * @param tre
	 * @return list of all CardHelp who can reach their treasure
	 */
	private List<PinPosHelp> simpleSolution(List<CardHelp> list_ch, TreasureType tre, int id) {
		List<Position> l = new ArrayList<>();
		List<PinPosHelp> l_sol = new ArrayList<>();
		Board board;
		Position trePos;
		Position oldPinPos;

		for (CardHelp ch : list_ch) {
			board = (Board) betterBoard.clone();
			board.proceedShift(ch.getP(), new Card(ch.getC()));
			oldPinPos = board.getPinPos(id);
			trePos = board.findTreasure(tre);
			if (trePos != null) {
				l.clear();
				l = findPossiblePos(board, l, oldPinPos);
				if (l.contains(trePos)) {
					l_sol.add(new PinPosHelp(trePos, trePos, ch));
				}
			}
		}
		return l_sol;
	}

	/**
	 * calculate an alternative turn to break a possible loop
	 * 
	 * @param tre
	 * @param id
	 * @return
	 */
	// private void simpleSolution_breakLoop(TreasureType tre, PinPosHelp old) {
	// List<Position> l = new ArrayList<>();
	// List<Position> l_rev = new ArrayList<>();
	// Board board;
	// Position trePos;
	// Position oldPinPos;
	// Position shiftPos;
	// CardHelp ch;
	//
	// List<Card> list_c = betterBoard.getShiftCard().getPossibleRotations();
	// for (Card c : list_c) {
	// for (int i = 1; i < 6; i += 2) {
	// for (int k = 0; k < 7; k += 6) {
	// for (int j = 0; j < 2; j++) {
	// board = (Board) betterBoard.clone();
	// c.setPin(new Pin());
	// shiftPos = new Position(k + (i - k) * j, i + (k - i) * j);
	// if (betterBoard.getForbidden() != null && shiftPos.equals(new Position(betterBoard.getForbidden()))) {
	// continue;
	// }
	// board.proceedShift(shiftPos, new Card(c));
	// oldPinPos = board.getPinPos(PlayerID);
	// trePos = board.findTreasure(tre);
	// if (trePos != null) {
	// l.clear();
	// l = findPossiblePos(board, l, oldPinPos);
	// l.remove(old.getPinPos());
	// l_rev.clear();
	// l_rev = findPossiblePos(board, l_rev, trePos);
	// ch = new CardHelp(c, shiftPos);
	// calcData_breakLoop(oldPinPos, trePos, ch, l, l_rev, board);
	// }
	// }
	// }
	// }
	// }
	// }

	private List<PinPosHelp> simpleSolution(TreasureType tre, int id) {
		boolean already = false;
		List<Position> l = new ArrayList<>();
		List<Position> l_rev = new ArrayList<>();
		List<PinPosHelp> l_sol = new ArrayList<>();
		Board board;
		Position trePos;
		Position oldPinPos;
		Position shiftPos;
		CardHelp ch;

		List<Card> list_c = betterBoard.getShiftCard().getPossibleRotations();
		for (Card c : list_c) {
			for (int i = 1; i < 6; i += 2) {
				for (int k = 0; k < 7; k += 6) {
					for (int j = 0; j < 2; j++) {
						board = (Board) betterBoard.clone();
						c.setPin(new Pin());
						shiftPos = new Position(k + (i - k) * j, i + (k - i) * j);
						if (betterBoard.getForbidden() != null && shiftPos.equals(new Position(betterBoard.getForbidden()))) {
							continue;
						}
						board.proceedShift(shiftPos, new Card(c));
						oldPinPos = board.getPinPos(id);
						trePos = board.findTreasure(tre);
						if (trePos != null) {
							l.clear();
							l = findPossiblePos(board, l, oldPinPos);
							l_rev.clear();
							l_rev = findPossiblePos(board, l_rev, trePos);
							ch = new CardHelp(c, shiftPos);
							if (l.contains(trePos)) {
								already = true;
								l_sol.add(new PinPosHelp(trePos, trePos, ch));
							}
							if (!already) {
								calcData(oldPinPos, trePos, ch, l, l_rev, board);
							}
						}
					}
				}
			}
		}
		return l_sol;
	}

	private void calcData(Position start, Position trePos, CardHelp ch, List<Position> list, List<Position> list_rev, Board b) {
		List<PinPosHelp> lpph = shortestPath(list, trePos, ch, b);
		list_PinPosHelp_v1.addAll(lpph);

		Map<CardHelp, ReverseHelp> mpph = shortestPath(list, list_rev, ch, trePos, b);
		map_PinPosHelp_v2.putAll(mpph);
	}

	// private void calcData_breakLoop(Position start, Position trePos, CardHelp ch, List<Position> list, List<Position> list_rev, Board board) {
	// List<PinPosHelp> lpph = shortestPath_breakLoop(list, trePos, ch, board);
	// list_PinPosHelp_v1.addAll(lpph);
	//
	// Map<CardHelp, ReverseHelp> mpph = shortestPath_breakLoop(list, list_rev, ch, trePos);
	// map_PinPosHelp_v2.putAll(mpph);
	// }

	private List<Position> findPossiblePos(Board b, List<Position> list, Position start) {
		Position p;
		int col_start = start.getCol();
		int row_start = start.getRow();
		Card c = new Card(b.getCard(row_start, col_start));
		if (col_start < x - 1) {
			p = new Position(row_start, col_start + 1);
			if (!list.contains(p) && b.getCard(row_start, col_start + 1).getOpenings().isLeft() && c.getOpenings().isRight()) {
				list.add(p);
				list = (findPossiblePos(b, list, p));
			}
		}
		if (col_start > 0) {
			p = new Position(row_start, col_start - 1);
			if (!list.contains(p) && b.getCard(row_start, col_start - 1).getOpenings().isRight() && c.getOpenings().isLeft()) {
				list.add(p);
				list = (findPossiblePos(b, list, p));
			}
		}
		if (row_start > 0) {
			p = new Position(row_start - 1, col_start);
			if (!list.contains(p) && b.getCard(row_start - 1, col_start).getOpenings().isBottom() && c.getOpenings().isTop()) {
				list.add(p);
				list = (findPossiblePos(b, list, p));
			}
		}
		if (row_start < y - 1) {
			p = new Position(row_start + 1, col_start);
			if (!list.contains(p) && b.getCard(row_start + 1, col_start).getOpenings().isTop() && c.getOpenings().isBottom()) {
				list.add(p);
				list = (findPossiblePos(b, list, p));
			}
		}
		return list;
	}

	public static class PinPosHelp {
		private Position pinPos;
		private int diff = Integer.MAX_VALUE;
		private CardHelp ch;
		private Position trePos;

		public PinPosHelp(Position trePos, Position PinPos, CardHelp ch) {
			this(trePos, PinPos, ch, Integer.MAX_VALUE);
		}

		public PinPosHelp(Position trePos, Position pinPos, CardHelp ch, int diff) {
			this.trePos = trePos;
			this.pinPos = pinPos;
			this.ch = ch;
			this.diff = diff;
		}

		public Position getPinPos() {
			return pinPos;
		}

		public int getDiff() {
			return diff;
		}

		public Position getTrePos() {
			return this.trePos;
		}

		public void setTrePos(Position trePos) {
			this.trePos = trePos;
		}

		public static List<PinPosHelp> getSmallestDiff(List<PinPosHelp> list) {
			List<PinPosHelp> res = new ArrayList<>();
			int min = Integer.MAX_VALUE;
			int diff;
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
				return "TrePos: " + trePos + " PinPos: " + pinPos + " " + ch.debug() + "\t" + diff;
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
		private int diff;
		private int player;

		public CardHelp(Card c, Position p) {
			this.c = c;
			this.p = p;
		}

		public CardHelp(Card c, Position p, int player, int diff) {
			this.c = c;
			this.p = p;
			this.player = player;
			this.diff = diff;
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

		public int getDiff() {
			return diff;
		}

		public int getPlayer() {
			return player;
		}

	}

	public static class ReverseHelp {
		private int diff;
		private int dist;
		private Position pinPos;

		public ReverseHelp(Position pinPos, int diff, int dist) {
			this.diff = diff;
			this.dist = dist;
			this.pinPos = pinPos;
		}

		public static Map<CardHelp, ReverseHelp> getValueableDiff(Map<CardHelp, ReverseHelp> map, int player) {
			List<CardHelp> tmp = new ArrayList<>();
			for (CardHelp key : map.keySet()) {
				if (map.get(key).getDist() > 6 - player) {
					tmp.add(key);
				}
			}
			map.remove(tmp);
			return map;
		}

		public int getDiff() {
			return diff;
		}

		public int getDist() {
			return dist;
		}

		public Position getPinPos() {
			return pinPos;
		}

		public String toString() {
			return "ReverseHelp: " + pinPos + " diff:" + diff + " dist:" + dist;
		}
	}

}

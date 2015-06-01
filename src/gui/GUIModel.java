package gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Board;
import model.Card;
import model.Position;
import model.Card.CardShape;
import model.Card.Orientation;
import model.jaxb.TreasureType;

public class GUIModel {

	private Board board;
	private Card shiftCard;
	private Orientation cardOrientation;
	private CardShape cardShape;
	private TreasureType cardTreasure;
	private Map<String, Integer> map;
	private int row;
	private int col;
	private Position pinPos;
	private int id;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public GUIModel() {
		map = new HashMap<>();
	}

	public Orientation getCardOrientation() {
		return cardOrientation;
	}

	public int getOrientation() {
		return cardOrientation.value();
	}

	public void setCardOrientation(int orientation) {
		this.cardOrientation = Orientation.fromValue(orientation);
	}

	public void setCardOrientation(Orientation orientation) {
		this.cardOrientation = orientation;
	}

	public CardShape getCardShape() {
		return cardShape;
	}

	public String getShape() {
		return cardShape.name();
	}

	public void setCardShape(CardShape c) {
		this.cardShape = c;
	}

	public void setCardShape(String c) {
		this.cardShape = CardShape.valueOf(c);
	}

	public TreasureType getCardTreasure() {
		return cardTreasure;
	}

	public String getTreasure() {
		return cardTreasure.name();
	}

	public void setCardTreasure(TreasureType treasureType) {
		this.cardTreasure = treasureType;
	}

	public void setCardTreasure(String treasure) {
		this.cardTreasure = TreasureType.valueOf(treasure);
	}

	public void setKeyEventMap(Map<String, Integer> map) {
		this.map = map;
	}

	public void setKeyEvent(String key, int event) {
		map.put(key, event);
	}

	public int getKeyEvent(String key) {
		return map.get(key);
	}

	public Collection<Integer> getKeyEvents() {
		return map.values();
	}

	public Set<String> getKeys() {
		return map.keySet();
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Card getShiftCard() {
		return shiftCard;
	}

	public void setShiftCard(Card shiftCard) {
		this.shiftCard = shiftCard;
	}

	public Position getPinPos() {
		return pinPos;
	}

	public void setPinPos(Position pinPos) {
		this.pinPos = pinPos;
	}
	
	public void setPlayerID(int id){
		this.id = id;
	}
	
	public int getPlayerID(){
		return id;
	}

}
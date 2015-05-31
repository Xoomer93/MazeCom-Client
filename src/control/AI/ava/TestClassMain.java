package control.AI.ava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import model.Board;
import model.Position;
import control.AI.ava.Pathfinding.PinPosHelp;

public class TestClassMain {
	private static final int playerID = 1;

	public static void main(String[] args) {
		System.out.println("Test for Ava");

		Board b = new Board();
		Position myPinPos = new Position(b.findPlayer(playerID));
		Position myTreasurePos = new Position(6, 4);
		Pathfinding p = new Pathfinding(b, playerID);
		PinPosHelp pph;

		try {
			FileWriter fwriter = new FileWriter((File) Context.getInstance().getValue(Context.FILE));
			fwriter.append("test\n");
			fwriter.write("testen\n");
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(b.getShiftCard());
		// System.out.println(b);

		System.out.println("PinPos: " + myPinPos + "  TreasurePos: " + myTreasurePos);
		pph = p.ava(myPinPos, myTreasurePos);
		System.out.println(pph);

		p = new Pathfinding(b, playerID);
		myPinPos = new Position(6, 6);
		myTreasurePos = new Position(3, 3);
		System.out.println("\nPinPos: " + myPinPos + "  TreasurePos: " + myTreasurePos);
		pph = p.ava(myPinPos, myTreasurePos);
		System.out.println(pph);
	}

}

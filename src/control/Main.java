package control;

import view.testClasses.Tmp_testGUI;

public class Main {
	private static final boolean debug = true;
	
	private Controller controller;
//	private Window window;
	
	private void run() {
		// Create controller
		controller = new Controller();
		
		//erzeugt ein Messagedialog und gibt den eingegeben namen zurueck
		Tmp_testGUI.first();
		
		// Debug code begin -----------------------------------------------------------------------
		if (debug) {
			controller.setName("Test");
			System.out.println(controller.connect("localhost", 5123));
		}
		// Debug code end -------------------------------------------------------------------------
		
		// Create window
//		window = new Window(controller);
	}
	
	public Controller getController() {
		return controller;
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.run();
	}
}

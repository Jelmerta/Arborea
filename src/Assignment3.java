/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

//
class Assignment3 {
	public static void main(String... args) {
		// First argument is the map used (src/maps/characterlocations2 is the map used in the example)
		// Second argument is the game mode you want to play (such as human (you) vs AI orcs, you will play as the humans)
		// Third argument is what side starts (0 or 1, if 0 humans will start, if 1 orcs will start)
		if(args.length == 3) {
			Arborea arborea = new Arborea("Arborea v0.03", args[0], args[1], args[2]);
			arborea.run();
		} else {
			Arborea arborea = new Arborea("Arborea v0.03");
			arborea.run();
		}
	}
}
/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

//
class Assignment3 {
	public static void main(String... args) {
		if(args.length == 1) {
			Arborea arborea = new Arborea("Arborea v0.01", args[0]);
			arborea.run();
		} else {
			Arborea arborea = new Arborea("Arborea v0.01");
			arborea.run();
		}
	}
}
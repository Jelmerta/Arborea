import java.awt.Point;

class Goblin extends Figure {
    Goblin(Point pos){
        super(3, pos);
		type = Figure.TYPE_GOBLIN;
		weapon = 4;
		hit = 3;
        teamIsOrcs = true;
    }
}
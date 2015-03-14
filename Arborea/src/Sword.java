import java.awt.Point;

class Sword extends Figure {
    Sword(Point pos){
        super(1, pos);
		type = Figure.TYPE_SWORD;
		weapon = 6;
		hit = 4;
        teamIsOrcs = false;
    }
}
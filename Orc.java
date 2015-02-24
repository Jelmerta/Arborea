import java.awt.Point;

class Orc extends Figure {

    Orc(Point pos){
        super(4, pos);
		type = Figure.TYPE_ORC;
		weapon = 8;
		hit = 10;       
        teamIsOrcs = true;
    }
}
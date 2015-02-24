import java.awt.Point;

class General extends Figure {
    General(Point pos){
        super(2, pos);
		type = Figure.TYPE_GENERAL;
		weapon = 8;
		hit = 5;
        teamIsOrcs = false;
    }   
}
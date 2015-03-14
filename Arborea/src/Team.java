// Toevoegen wordt nu al gedaan en er wordt bijgehouden in welk team de figures zitten, moet deze file dan alsnog wel?

import java.util.ArrayList;

class Team {
	
	ArrayList<Figure> figures;
	boolean side;

    Team() {
    	figures = new ArrayList<Figure>();
    }
    
    public void addToTeam(Figure figure) {
    	figures.add(figure);
    }
    
    public void remove(Figure figure) {
    	figures.remove(figure);
    }
    
    public ArrayList<Figure> getTeam() {
    	return figures;
    }
}
/*    boolean teamIsOrcs;
    ArrayList<Figure> figures;
    
    // sets up a team, depending on various factors
    Team(boolean isOrcTeam){
        
        teamIsOrcs = isOrcTeam;
        figures = new ArrayList<Figure>();
        
        if (teamIsOrcs){
            for (int i =0; i < Arborea.AMOUNT_GOBLIN; i++){
                figures.add(new Goblin());
            }
            for (int i =0; i < Arborea.AMOUNT_ORC; i++){
                figures.add(new Orc());
            }
            
        } else {
            for (int i =0; i < Arborea.AMOUNT_SWORD; i++){
                figures.add(new Sword());
            }
            for (int i =0; i < Arborea.AMOUNT_GENERAL; i++){
                figures.add(new General());
            }
        }
    }
}*/
package universite.bordeaux.app.ReadersAndParser.Readers;

public final class LogElements {
	
	public static boolean existPlaysMove(String line){
		boolean exist = false;
		if (line.matches("(.*) plays (a|the|an|[0-9]+)"
                        + " \\<span class(.*)")) {
			exist = true;
		}
		return exist;		
	}
	
	public static boolean existBuysMove(String line){
		boolean exist = false;
		if(line.matches("(.*) buys (a|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existDrawsOnFinishTurn(String line){
		boolean exist = false;
		if(line.trim()
                .matches("\\<span class\\=logonly\\>\\((.*)"
                        + " draws: (.*)")){
			exist = true;
		}
		
		return exist;
	}
	
	public static boolean existMoveDrawDiscardCard(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) draws and discard "
                        + "(a|an|[0-9]+) \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}

	public static boolean existGainsAction(String line){
		boolean exist = false;
		if(line.matches("(.*) gains (a|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existActionDiscardsAndGains(String line){
		boolean exist = false;
		if(line.matches("(.*) discards (a|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
			
		}
		return exist;
	}
	
	public static boolean existTrashes(String line){
		boolean exist = false;
		if(line.matches("(.*) trashes (a|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
			
		}
		return exist;
	}
	
	public static boolean existReveals(String line){
		boolean exist = false;
		if(line.matches("(.*) reveals (a|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
			
		}
		return exist;
	}
	
	public static boolean existTrashingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) trashing (a|an|the|[0-9]+)"
                        + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existRevealingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) revealing (a|an|[0-9]+)"
                        + " \\<span class(.*)")){
			exist = true;
			
		}
		return exist;
	}
	
	public static boolean existPuttingAction(String line){
		boolean exist = false;
		if(line.trim().matches("(.*) putting (a|the|an|[0-9]+)"
                + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existGainingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) gaining "
                        + "(a|the|another|an|[0-9]+)"
                        + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existDiscardingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) discarding"
                        + " (a|an|[0-9]+)+"
                        + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existPlayingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) playing "
                        + "(a|an|[0-9]+)+ "
                        + "\\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
	
	public static boolean existDrawingAction(String line){
		boolean exist = false;
		if(line.trim()
                .matches("(.*) drawing (a|an|[0-9]+)+ "
                        + " \\<span class(.*)")){
			exist = true;
		}
		return exist;
	}
}

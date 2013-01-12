package geogebra.common.kernel.algos;
import geogebra.common.kernel.commands.Commands;
public enum Algos implements GetCommand{

	Expression,AlgoMacro;
	private String command;
	private Algos(){
		this.command="Expression";
	}
	private Algos(Commands command){
		this.command = command.name();
	}
	public String getCommand(){
		return command;
	}
	
}

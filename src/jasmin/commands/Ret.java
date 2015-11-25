package jasmin.commands;

import jasmin.core.*;

/**
 * @author Yang Guo
 */

public class Ret extends JasminCommand {
	
	public String[] getID() {
		return new String[] { "RET" };
	}
	
	public ParseError validate(Parameters p) {
            ParseError pe = p.validate(0, Op.NULL);
            if(pe == null) {
                return null;
            }
            pe = p.validate(0, Op.IMM);
            if(pe == null) {
                return p.validate(1, Op.NULL);
            } else {
                return pe;
            }
	}
	
	public void execute(Parameters p) {
		p.pop(dataspace.EIP);
                if(!p.wholeLine.trim().equalsIgnoreCase("RET")) {
                    int par = Integer.parseInt(p.wholeLine.split(" ")[1]);
                    p.pop(par);
                }
	}
	
}

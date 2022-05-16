import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Graphics;

public class Miert_HF_View extends GridWorldView {

	Miert_HF_Model model;

    public Miert_HF_View(Miert_HF_Model model, final Miert_HF_Environment env) {
        super(model, "Distributed taxi system sajat", 1200);
        this.model = model;
        setVisible(true);
        repaint();
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        if (id == 0) {
            super.drawAgent(g, x, y, Color.BLACK, id);
            g.setColor(Color.BLACK);
        } else if (0 < id && id < model.getNumCourier() + 1) {
            super.drawAgent(g, x, y, Color.YELLOW, id);
            g.setColor(Color.YELLOW);
        } else if (model.getNumCourier()+1 < id && id<model.getNumClient()+model.getNumCourier()+1){
        	System.out.println("en amugy elek");
            super.drawAgent(g, x, y, Color.BLUE, id);
            g.setColor(Color.BLUE);
        }else if(id> model.getNumClient()+model.getNumCourier()+1){
        	super.drawAgent(g, x, y, Color.RED, id);
        	g.setColor(Color.RED);
        }
        //ide kell egy olyan fv, ami a shopokat kirajzolja
    }
}
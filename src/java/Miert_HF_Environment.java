//import jason.asSyntax.ASSyntax;
import static jason.asSyntax.ASSyntax.*;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.TimeSteppedEnvironment;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Miert_HF_Environment extends TimeSteppedEnvironment {

    private static Logger logger = Logger.getLogger("miert_hf2.mas2j."+Miert_HF_Environment.class.getName());   //pipa
    private HashMap<String, Integer> agentIds = new HashMap<String, Integer>();     //pipa
    private ArrayList<String> placeableClients = new ArrayList<String>();
    private static int callCounter = 0;
	private int agentNumSum =0;
    //private int agentCounter = 0;

    private Miert_HF_Model model;
    private Miert_HF_View view;

    static class Literals {
        private Literals() {}

        static final Term MOVE_UP = Literal.parseLiteral("move(up)");
        static final Term MOVE_DOWN = Literal.parseLiteral("move(down)");
        static final Term MOVE_RIGHT = Literal.parseLiteral("move(right)");
        static final Term MOVE_LEFT = Literal.parseLiteral("move(left)");

        private static Literal atLiteral(Location loc) {
            if(loc == null)
                return null;
            return Literal.parseLiteral(String.format("at(%d,%d)", loc.x, loc.y));
        }

        private static Literal courierNumLiteral(int numCourier) {							//fontiii pipa
            return Literal.parseLiteral(String.format("courier_num(%d)", numCourier));
        }

        private static Literal gotoLiteral(Location loc) {
            return Literal.parseLiteral(String.format("go_to(%d,%d)", loc.x, loc.y));
        }

        private static Literal removeLiteral(String agentName) {
            return Literal.parseLiteral(String.format("remove(%s)", agentName));
        }

        private static Literal setupLiteral() {
            return Literal.parseLiteral(String.format("setup"));
        }
    }

    @Override
    public void init(String[] args) {
        super.init(new String[] { "75" });
        int numClient = Integer.parseInt(args[0]);                  //pipa
        int numCourier = Integer.parseInt(args[1]);                    //pipa
        int numShops = 4;
        this.agentNumSum = numClient+ numCourier+numShops+1;
        

        model = new Miert_HF_Model(numCourier, numClient,numShops);       //todo
        view = new Miert_HF_View(model, this);            //todo
        model.setView(view);                                         //todo

        int agentId = 0;
        String agName = "headquarters";                                     //pipa
        agentIds.put(agName, agentId);                           //pipa
        addPercept(agName, Literals.courierNumLiteral(model.getNumCourier()));//pipa
        updatePercepts(agName);                                          //pipa

        for (int i = 0; i < numCourier; i++) {
            agName = "courier" + (i+1);
            agentIds.put(agName, ++agentId);
            updatePercepts(agName);
        }

        for (int i = 0; i < numClient; i++) {
            agName = "client" + (i+1);
            agentIds.put(agName, ++agentId);
            //addPercept(agName, Literals.gotoLiteral(model.getGoodTaxiLocation()));
            addPercept(agName, Literals.gotoLiteral(model.getGoodShopLocation()));
            updatePercepts(agName);
        }

        for (int i = 0; i < numShops; i++) {
            agName = "shop" + (i+1);
            agentIds.put(agName, ++agentId);
            //Location loc = model.getFreepos();
        	//setAgPos(agentId,loc);
            //addPercept(agName, Literals.gotoLiteral(loc));
            updatePercepts(agName);
        }

    }

    private void updatePercepts(String agentName) {
        int agentId = agentIds.get(agentName);
        Location prevAgentPosition = model.getPrevAgentLocation(agentId);
        Location agentPosition = model.getAgPos(agentId);

        Literal prevPositionLiteral = Literals.atLiteral(prevAgentPosition);
        if(prevPositionLiteral != null) {
            removePercept(agentName, prevPositionLiteral);
        }
        addPercept(agentName, Literals.atLiteral(agentPosition));
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        int agentId = agentIds.get(agName);
        boolean successful = false;
        if (action.equals(Literals.MOVE_UP)) {
            successful = model.move(agentId, Miert_HF_Model.Direction.UP);
        } else if (action.equals(Literals.MOVE_DOWN)) {
            successful = model.move(agentId, Miert_HF_Model.Direction.DOWN);
        } else if (action.equals(Literals.MOVE_RIGHT)) {
            successful = model.move(agentId, Miert_HF_Model.Direction.RIGHT);
        } else if (action.equals(Literals.MOVE_LEFT)) {
            successful = model.move(agentId, Miert_HF_Model.Direction.LEFT);
        } else if (action.getFunctor().equals("remove")) {
            String clientName = String.valueOf(action.getTerm(0));
            int clientId = agentIds.get(clientName);

            model.removeClient(agentId, clientId);
            placeableClients.add(clientName);

            successful = true;
        }
        callCounter++;
        if (callCounter % 85 == 0) {                //akkor teremjen ujra a kliens, ha ...
            if (placeableClients.size() > 0) {
                String clientToPlace = placeableClients.get(0);
                placeableClients.remove(0);
                int clientToPlaceId = agentIds.get(clientToPlace);

                clearPercepts(clientToPlace);
                model.placeClient(clientToPlaceId);
                updatePercepts(clientToPlace);
                //addPercept(clientToPlace, Literals.gotoLiteral(model.getGoodTaxiLocation()));
                addPercept(clientToPlace, Literals.gotoLiteral(model.getGoodShopLocation()));
                addPercept(clientToPlace, Literals.setupLiteral());
            }
        }

        updatePercepts(agName);
        return successful;
    }
}
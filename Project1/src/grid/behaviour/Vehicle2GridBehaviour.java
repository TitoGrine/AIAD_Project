package grid.behaviour;

import grid.ChargingHub;
import grid.Vehicle2GridConditions;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

public class Vehicle2GridBehaviour extends ContractNetInitiator {
    int peakLoad;
    ChargingHub chub;
    List<AID> vehicles;

    public Vehicle2GridBehaviour(Agent a, int peakLoad, List<AID> vehicles) {
        super(a, new ACLMessage((ACLMessage.CFP)));
        chub = (ChargingHub) a;
        this.peakLoad = peakLoad;
        this.vehicles = vehicles;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> msgs = new Vector<>();

        for (AID vehicle : vehicles) {
            cfp = new ACLMessage((ACLMessage.CFP));
            cfp.setContent("Requesting vehicle proposal to charge grid.");
            cfp.addReceiver(vehicle);

            msgs.add(cfp);
        }

        return msgs;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        PriorityQueue<ACLMessage> priorityQueue = new PriorityQueue<>((a, b) -> {
            try {
                return (int) b.getContentObject() - (int) a.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            return 0;
        });
        boolean acceptedProposal = false;

        for (Object response : responses) {
            if (((ACLMessage) response).getPerformative() == ACLMessage.PROPOSE){
                acceptedProposal = true;
                try {
                    Utilities.printChargingHubMessage("received V2G proposal from " + ((ACLMessage) response).getSender().getLocalName() + " with a value of " + ((ACLMessage) response).getContentObject());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                priorityQueue.add((ACLMessage) response);
            } else {
                Utilities.printChargingHubMessage("received from vehicle " + ((ACLMessage) response).getSender().getLocalName() + ": " +  ((ACLMessage) response).getContent());
            }
        }

        if(priorityQueue.isEmpty())
            chub.addGridDataPoint(peakLoad, 0);

        ACLMessage msg;
        int remainingLoad = peakLoad;

        try {
            while (!priorityQueue.isEmpty() && remainingLoad > 0) {
                msg = priorityQueue.poll();
                ACLMessage reply = msg.createReply();
                Vehicle2GridConditions chargingConditions = new Vehicle2GridConditions(chub.getChargingPrice() * 0.3, Math.min(remainingLoad, (int) msg.getContentObject()));

                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContentObject(chargingConditions);
                remainingLoad -= chargingConditions.getSharedLoad();

                acceptances.add(reply);
            }
        } catch (IOException | UnreadableException e) {
            e.printStackTrace();
        }

        while(!priorityQueue.isEmpty()){
            msg = priorityQueue.poll();

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("V2G charging no longer needed.");

            acceptances.add(reply);
        }

        if(!acceptedProposal) {
            chub.addGridDataPoint(peakLoad, 0);
            chub.distributeLoad();
        }
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        int totalSharedLoad = 0;
        for(Object result : resultNotifications){

            if(((ACLMessage) result).getPerformative() == ACLMessage.INFORM){
                Utilities.printChargingHubMessage("vehicle " + ((ACLMessage) result).getSender().getLocalName() + " confirms charging the grid.");
                chub.removeVehicleFromSystemStatus(((ACLMessage) result).getSender());
                try {
                    totalSharedLoad += (int) ((ACLMessage) result).getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }
        }

        chub.addGridDataPoint(peakLoad, totalSharedLoad);
        chub.distributeLoad();
    }
}

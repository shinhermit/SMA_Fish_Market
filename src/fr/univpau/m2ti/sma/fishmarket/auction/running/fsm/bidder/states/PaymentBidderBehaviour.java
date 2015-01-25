package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.logging.Logger;

/**
 *
 */
public class PaymentBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(PaymentBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    public PaymentBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        ACLMessage mess = this.myFSM.getRequest();

        ACLMessage payment = mess.createReply();

        payment.setConversationId(mess.getConversationId());
        payment.clearAllReceiver();
        payment.addReceiver(((BidderAgent) super.myAgent).getMarketAgentAID());
        payment.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
        payment.setPerformative(FishMarket.Performatives.TO_PAY);
        payment.setContent(String.valueOf(((BidderAgent) super.myAgent).getBiddingPrice()));

        super.myAgent.send(payment);
    }
}

package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class WaitAttributionBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitAttributionBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private int transition;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                    		RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
                    ),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_ATTRIBUTE
                    )
            );

    public WaitAttributionBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        ACLMessage message = myAgent.receive(MESSAGE_FILTER);


        if (message != null)
        {
            System.out.println("Received attribution");
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_GO_GET_FISH;
        }
        else
        {
            this.myFSM.block();
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_ATTRIBUTION;
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}

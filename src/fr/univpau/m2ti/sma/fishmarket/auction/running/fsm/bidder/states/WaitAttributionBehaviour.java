package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.BidderBehaviour;
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
public class WaitAttributionBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitAttributionBehaviour.class.getName());

    private BidderBehaviour myFSM;

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

    public WaitAttributionBehaviour(Agent a, BidderBehaviour fsm)
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
            this.transition = BidderBehaviour.TRANSITION_GO_GET_FISH;
        }
        else
        {
            this.myFSM.block();
            this.transition = BidderBehaviour.TRANSITION_WAIT_ATTRIBUTION;
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}

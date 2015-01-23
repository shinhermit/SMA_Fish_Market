package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class WaitFishBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitFishBehaviour.class.getName());

    private BidderBehaviour myFSM;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                    		RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC
                    ),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_GIVE
                    )
            );

    private int transition;

    public WaitFishBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action()
    {
        System.out.println("action => " + getBehaviourName());

        ACLMessage message = myAgent.receive(MESSAGE_FILTER);

        if (message != null)
        {
            System.out.println("Received Fish");
            this.transition = BidderBehaviour.TRANSITION_TO_PAYMENT;
        }
        else
        {
            System.out.println("Received Fish");
            this.myFSM.block();
            this.transition = BidderBehaviour.TRANSITION_WAIT_FISH;
        }

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}


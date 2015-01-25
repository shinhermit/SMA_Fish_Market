package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class WaitFishBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitFishBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    /** Message filtering */
    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                    		RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
                    ),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_GIVE
                    )
            );

    private int transition;

    public WaitFishBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
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
            this.myFSM.setRequest(message);
            System.out.println("Received Fish");
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_TO_PAYMENT;
        }
        else
        {
            System.out.println("Waiting for fish");
            this.myFSM.block();
            this.transition = RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_FISH;
        }

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}


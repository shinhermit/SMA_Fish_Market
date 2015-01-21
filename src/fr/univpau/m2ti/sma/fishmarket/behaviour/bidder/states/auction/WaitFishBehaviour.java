package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
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
    private static final MessageTemplate MESSAGE_FILTER;

    static
    {
        MESSAGE_FILTER = MessageTemplate.MatchPerformative(
                FishMarket.Performatives.TO_GIVE
        );
    }

    public WaitFishBehaviour(Agent a, BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action()
    {
        System.out.println("action => " + getBehaviourName());

        this.block();

        ACLMessage message = myAgent.receive(MESSAGE_FILTER);

        if (message != null)
        {
            System.out.println("Received Fish");
        }

    }
}


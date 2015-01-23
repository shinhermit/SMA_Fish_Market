package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.Random;
import java.util.logging.Logger;

/**
 *
 */
public class CreateBidderFSMBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(CreateBidderFSMBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    public CreateBidderFSMBehaviour(Agent a, SubscribeToAuctionBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action()
    {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

//        bidderAgent.createBidderFSM(
//            this.getNewPriceLimit(1000, 2000)
//        );

        bidderAgent.addBehaviour(
                new BidderBehaviour(super.myAgent, this.getNewPriceLimit(1000, 2000))
        );
    }

    private float getNewPriceLimit(float min, float max)
    {
        Random rand = new Random();

        if (min == Float.MAX_VALUE)
        {
            min = Float.MAX_VALUE - 1;
        }

        if (max <= min)
        {
            max = min + 1;
        }

        float randFloat = rand.nextFloat();

        if (randFloat < min)
        {
            randFloat = randFloat + min;
        }

        if (randFloat > max)
        {
            randFloat = (randFloat % ((long)(max - min))) + min;
        }


        return randFloat;
    }

}

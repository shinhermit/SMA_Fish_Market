package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubsribeToAuctionBehaviour;
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

    private SubsribeToAuctionBehaviour myFSM;

    public CreateBidderFSMBehaviour(Agent a, SubsribeToAuctionBehaviour fsm)
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

        bidderAgent.createBidderFSM(
            this.myFSM.getLastSubscribedAuction(),
            this.getNewPriceLimit(1000, 2000)
        );
    }

    private long getNewPriceLimit(long min, long max)
    {
        Random rand = new Random();

        if (min == Long.MAX_VALUE)
        {
            min = Long.MAX_VALUE - 1;
        }

        if (max <= min)
        {
            max = min + 1;
        }

        long randLong = rand.nextLong();

        if (randLong < min)
        {
            randLong = randLong + min;
        }

        if (randLong > max)
        {
            randLong = (randLong % (max - min)) + min;
        }


        return randLong;
    }

}

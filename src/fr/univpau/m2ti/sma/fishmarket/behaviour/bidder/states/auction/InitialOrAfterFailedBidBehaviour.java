package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InitialOrAfterFailedBidBehaviour extends Behaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(InitialOrAfterFailedBidBehaviour.class.getName());

    private BidderBehaviour myFSM;

    private boolean isDone = false;

    /** Allows filtering incoming messages. */
    private static final MessageTemplate MESSAGE_FILTER;

    static
    {
        MESSAGE_FILTER =
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.TO_ANNOUNCE
                    ),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.AUCTION_CANCELLED
                            ),
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.AUCTION_OVER
                            )
                    )
            );
    }

    private int transition;

    public InitialOrAfterFailedBidBehaviour(Agent a,
                                            BidderBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        // waiting for an announce

        this.block();

        ACLMessage mess = bidderAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            this.myFSM.setRequest(mess);

            if (mess.getPerformative() == FishMarket.Performatives.TO_ANNOUNCE)
            {
                this.transition = BidderBehaviour.TRANSITION_RECEIVED_FIRST_ANNOUNCE;
            }
            else if (mess.getPerformative() == FishMarket.Performatives.AUCTION_CANCELLED)
            {
                this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED;
                this.isDone = true;
            }
            else
            {
                //mess.getPerformative() == FishMarket.Performatives.AUCTION_OVER
                this.transition = BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
                this.isDone = true;
            }
        }
        else
        {
            //Should not happen
            InitialOrAfterFailedBidBehaviour.LOGGER
                    .log(Level.SEVERE, null, "Received null message");
            this.isDone = true;
        }

        // transition to next step

    }

    @Override
    public boolean done()
    {
        return this.isDone;
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}

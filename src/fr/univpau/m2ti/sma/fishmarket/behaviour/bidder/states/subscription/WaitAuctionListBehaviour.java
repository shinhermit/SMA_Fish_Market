package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class WaitAuctionListBehaviour extends Behaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitAuctionListBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    private static final MessageTemplate MESSAGE_FILTER =
        MessageTemplate.and(
                BidderManagementBehaviour.MESSAGE_FILTER,
                MessageTemplate.MatchPerformative(
                        FishMarket.Performatives.TO_PROVIDE
                )
        );

    public WaitAuctionListBehaviour(Agent a, SubscribeToAuctionBehaviour myFSM)
    {
        super(a);
        this.myFSM = myFSM;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;


        // Receive message
        ACLMessage mess = bidderAgent.receive(MESSAGE_FILTER);

        if (mess != null)
        {
            myFSM.setRequest(mess);
        }
        else
        {
            // wait for incoming message
            this.block();
        }

        // transition to next step

    }

    @Override
    public boolean done()
    {
        // Stays alive in case we need to ask for new auction list
        return false;
    }

    @Override
    public int onEnd()
    {
        // Implemented because of possible early return to end state.
        return SubscribeToAuctionBehaviour.TRANSITION_AUCTION_LIST_RECEIVED;
    }
}

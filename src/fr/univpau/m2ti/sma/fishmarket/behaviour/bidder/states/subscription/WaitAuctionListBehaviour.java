package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.SubscribeToAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderSubscriptionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class WaitAuctionListBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitAuctionListBehaviour.class.getName());

    private SubscribeToAuctionBehaviour myFSM;

    private static final MessageTemplate MESSAGE_FILTER =
        MessageTemplate.and(
                BidderSubscriptionManagementBehaviour.MESSAGE_FILTER,
                MessageTemplate.MatchPerformative(
                        FishMarket.Performatives.TO_PROVIDE
                )
        );

    private int transition;

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
            //TODO : remove
            System.out.println("List received");

            myFSM.setRequest(mess);
            this.transition = SubscribeToAuctionBehaviour.TRANSITION_AUCTION_LIST_RECEIVED;
            //this.myFSM.restart();
        }
        else
        {
            // wait for incoming message
            this.transition = SubscribeToAuctionBehaviour.TRANSITION_WAIT_AUCTION_LIST;

            this.myFSM.block();
        }

        // transition to next step

    }

    @Override
    public int onEnd()
    {
        // Implemented because of possible early return to end state.
        return this.transition;
    }
}

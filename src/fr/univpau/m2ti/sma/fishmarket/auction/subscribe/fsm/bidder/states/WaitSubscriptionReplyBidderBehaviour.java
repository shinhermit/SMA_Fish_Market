package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class WaitSubscriptionReplyBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(WaitSubscriptionReplyBidderBehaviour.class.getName());

    private SubscribeToAuctionBidderFSMBehaviour myFSM;

    private int transition;

    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
            		SubscribeToAuctionMarketFSMBehaviour.MESSAGE_FILTER,
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.TO_ACCEPT
                            ),
                            MessageTemplate.MatchPerformative(
                                    FishMarket.Performatives.TO_REFUSE
                            )
                    )
            );

    public WaitSubscriptionReplyBidderBehaviour(Agent a, SubscribeToAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        BidderAgent bidderAgent =
                (BidderAgent) super.myAgent;

        // Receive message
        ACLMessage mess = bidderAgent.receive(MESSAGE_FILTER);

        String seller = null;

        if(mess != null)
        {

            if(mess.getPerformative() ==
                    FishMarket.Performatives.TO_ACCEPT)
            {
                // subscription succeeded
                this.transition =
                        SubscribeToAuctionBidderFSMBehaviour
                                .TRANSITION_SUBSCRIPTION_ACCEPTED;

                seller =  mess.getContent();
            }
            else
            {
                this.transition =
                        SubscribeToAuctionBidderFSMBehaviour
                                .TRANSITION_SUBSCRIPTION_REFUSED;
            }
        }
        else
        {
            // wait for incoming message
            this.myFSM.block();
            this.transition =
                    SubscribeToAuctionBidderFSMBehaviour
                        .TRANSITION_WAIT_SUBSCRIPTION_RESULT;
        }

        // transition to next step

    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}

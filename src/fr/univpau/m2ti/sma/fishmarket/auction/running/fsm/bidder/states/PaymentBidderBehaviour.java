package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

/**
 *
 */
public class PaymentBidderBehaviour extends OneShotBehaviour
{
    /** Logging. */
    private static final Logger LOGGER =
            Logger.getLogger(PaymentBidderBehaviour.class.getName());

    private RunningAuctionBidderFSMBehaviour myFSM;

    private static final MessageTemplate MESSAGE_FILTER =
            MessageTemplate.and(
                    MessageTemplate.MatchTopic(
                            RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
                    ),
                    MessageTemplate.MatchPerformative(
                            FishMarket.Performatives.AUCTION_OVER
                    )
            );

    private int transition;

    public PaymentBidderBehaviour(Agent a, RunningAuctionBidderFSMBehaviour fsm)
    {
        super(a);
        this.myFSM = fsm;
    }

    @Override
    public void action() {
        System.out.println("action => " + getBehaviourName());

        ACLMessage mess = this.myFSM.getRequest();

        if (mess != null)
        {
            this.myFSM.setRequest(null);
            ACLMessage payment = mess.createReply();


            payment.setConversationId(mess.getConversationId());
            payment.clearAllReceiver();
            payment.addReceiver(
                    ((BidderAgent) super.myAgent).getMarketAgentAID()
            );
            payment.addReceiver(RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
            payment.setPerformative(FishMarket.Performatives.TO_PAY);
            payment.setContent(
                    String.valueOf(
                            ((BidderAgent) super.myAgent).getBiddingPrice()
                    )
            );

            super.myAgent.send(payment);

            //wait for payment acknowledgement (auction_over)
            this.myFSM.block();
            this.transition =
                    RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_AUCTION_OVER;
        }
        else
        {
            mess = myAgent.receive(MESSAGE_FILTER);

            if (mess != null)
            {
                this.transition =
                        RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER;
            }
            else
            {
                //wait some more
                this.myFSM.block();
            }
        }
    }

    @Override
    public int onEnd()
    {
        return this.transition;
    }
}

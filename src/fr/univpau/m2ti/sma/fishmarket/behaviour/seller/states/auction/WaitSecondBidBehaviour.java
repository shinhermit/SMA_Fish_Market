package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitSecondBidBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition =
			FishSellerBehaviour.TRANSITION_TO_ATTRIBUTE;
	
	/** The time to wait for the second bid. */
	private static final long SECOND_BID_WAIT_DURATION = 20000l; // 20 sec
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitSecondBidBehaviour(
			SellerAgent mySellerAgent,
			FishSellerBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		this.block(SECOND_BID_WAIT_DURATION);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.getMessageFilter());
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		if(mess != null)
		{
			this.transition =
					FishSellerBehaviour.TRANSITION_TO_WAIT_MORE_BID;
		}
		else
		{
			this.transition =
					FishSellerBehaviour.TRANSITION_TO_ATTRIBUTE;
			
			// send rep_bid(OK)
			ACLMessage reply = new ACLMessage(
					FishMarket.Performatives.REP_BID);
			
			// Receiver
			reply.addReceiver(
					mySellerAgent.getMarketAgent());
			
			// Set topic
			reply.addReceiver(
					AuctionManagementBehaviour.MESSAGE_TOPIC);
			
			// Set conversation id
			reply.setConversationId(
					this.myFSM.getConversationId());
			
			// Add price and send
			reply.setContent(String.valueOf(true));
			
			// Send
			mySellerAgent.send(reply);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * 
	 * 
	 * @return the message filter to use in this behaviour.
	 */
	private MessageTemplate getMessageFilter()
	{
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.and(
						MessageTemplate.MatchContent(
								String.valueOf(mySellerAgent.getCurrentPrice())),
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_BID)));
	}
}

package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitBidBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitBidBehaviour(
			SellerAgent mySellerAgent,
			RunningAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent, mySellerAgent.getBidWaitingDuration());
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void onWake()
	{
		// DEBUG
		System.out.println("Seller: checking messages for first bid.");
		
		// Receive messages
		ACLMessage mess;
		
		int bidCount = 0;
		
		do
		{
			mess = myAgent.receive(
					this.getMessageFilter());
			
			if(mess != null)
			{
				++ bidCount;
			}
		}
		while(mess != null);
		
		/** New announce or cancel */
		if(bidCount == 0)
		{
			SellerAgent mySellerAgent =
					(SellerAgent)super.myAgent;
			
			// Either: make new announce with lower price OR cancel
			float newStep = mySellerAgent.getPriceStep() / 2f;
			float newPrice = mySellerAgent.getCurrentPrice() - newStep;
			
			if(newPrice >= mySellerAgent.getMinPrice()
					&& newStep >= mySellerAgent.getMinPriceStep())
			{
				mySellerAgent.decreasePriceStep();
				mySellerAgent.decreasePrice();
				
				// DEBUG
				System.out.println("Seller: setting transition to announce.");
				
				this.transition =
						RunningAuctionSellerFSMBehaviour.TRANSITION_TO_ANNOUNCE;
			}
			else
			{
				// DEBUG
				System.out.println("Seller: setting transition to terminate cancel.");
				
				this.transition =
						RunningAuctionSellerFSMBehaviour.TRANSITION_TO_TERMINATE_CANCEL;
			}
		}
		else
		{
			/** Attribute */
			if(bidCount == 1)
			{
				// DEBUG
				System.out.println("Seller: setting transition to attribute.");
				
				SellerAgent mySellerAgent =
						(SellerAgent)super.myAgent;
				
				// DEBUG
				System.out.println("Seller: sending rep bid ok.");
				
				// send rep_bid(OK)
				ACLMessage reply = new ACLMessage(
						FishMarket.Performatives.REP_BID);
				
				// Receiver
				reply.addReceiver(
						mySellerAgent.getMarketAgent());
				
				// Set topic
				reply.addReceiver(
						RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
				
				// Set conversation id
				reply.setConversationId(
						this.myFSM.getConversationId());
				
				// Add price and send
				reply.setContent(String.valueOf(true));
				
				// Send
				mySellerAgent.send(reply);
				
				this.transition =
						RunningAuctionSellerFSMBehaviour.TRANSITION_TO_ATTRIBUTE;
			}
			
			/** Multiple bid decision */
			else
			{
				// DEBUG
				System.out.println("Seller: setting transition to send rep bid nok.");
				
				// Continue to wait
				this.transition =
						RunningAuctionSellerFSMBehaviour.TRANSITION_TO_HANDLE_MULTIPLE_BID;
			}
		}
	}
	
	@Override
	public int onEnd()
	{
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		// For any future return to this state
		this.reset(mySellerAgent.getBidWaitingDuration());
		
		return this.transition;
	}
	
	/**
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

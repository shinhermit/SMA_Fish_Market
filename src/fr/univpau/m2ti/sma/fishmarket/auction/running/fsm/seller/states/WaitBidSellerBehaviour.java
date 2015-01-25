package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitBidSellerBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition;
	
	/** The duration of one cycle to wait for bids. */
	private static final long WAIT_BID_CYCLE_DURATION = 500l; // 0.5 sec
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitBidSellerBehaviour(
			SellerAgent mySellerAgent,
			RunningAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent, WAIT_BID_CYCLE_DURATION);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void onWake()
	{
		this.myFSM.notifyWaitCycle();
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		// Receive messages
		ACLMessage mess;
		
		do
		{
			mess = mySellerAgent.receive(
					this.getMessageFilter());
			
			if(mess != null)
			{
				this.myFSM.notifyNewBid();
			}
		}
		while(mess != null);
		
		// Decide transition
		int bidCount = this.myFSM.getBidCount();
		
		long maxCycleCount =
				mySellerAgent.getBidWaitingDuration() /
				WaitBidSellerBehaviour.WAIT_BID_CYCLE_DURATION;
		
		boolean timeout = this.myFSM.getWaitCycleCount() > maxCycleCount;
		
		if(timeout || bidCount == mySellerAgent.getSubscriberCount())
		{
			this.myFSM.resetWaitCycleCount();
			
			/** Either: make new announce with lower price OR cancel */
			if(bidCount == 0)
			{
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
			
			/** Attribute */
			else if(bidCount == 1)
			{
				// DEBUG
				System.out.println("Seller: setting transition to attribute.");
				
				
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
				
				// Handle multiple bids
				this.transition =
						RunningAuctionSellerFSMBehaviour.TRANSITION_TO_HANDLE_MULTIPLE_BID;
			}
		}
		else
		{
			// Continue to wait
			this.transition =
					RunningAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_TO_BID;
		}
	}
	
	@Override
	public int onEnd()
	{
		// For any future return to this state
		this.reset(WAIT_BID_CYCLE_DURATION);
		
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

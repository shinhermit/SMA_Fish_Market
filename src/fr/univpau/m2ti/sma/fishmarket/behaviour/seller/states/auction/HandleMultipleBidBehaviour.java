package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class HandleMultipleBidBehaviour extends OneShotBehaviour
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
	public HandleMultipleBidBehaviour(
			SellerAgent mySellerAgent,
			RunningAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// DEBUG
		System.out.println("Seller: wait more bid timeout !");
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		// Select transition
		float nextPrice = mySellerAgent.getCurrentPrice() + mySellerAgent.getPriceStep();
		float maxPrice = mySellerAgent.getMaxPrice();
		
		float nextPriceStep = mySellerAgent.getPriceStep() / 2f;
		float minPriceStep = mySellerAgent.getMinPriceStep();
		
		boolean repBidOk = false;
		this.transition =
				RunningAuctionSellerFSMBehaviour.TRANSITION_TO_ANNOUNCE;
		
		if(nextPrice < maxPrice)
		{
			mySellerAgent.increasePrice();
			
			// DEBUG
			System.out.println("Seller: transition is set to announce, with price increased by priceStep !");
		}
		else if(nextPriceStep >= minPriceStep)
		{
			mySellerAgent.decreasePriceStep();
			
			mySellerAgent.increasePrice();
			
			// DEBUG
			System.out.println("Seller: transition is set to announce, with price increased by priceStep/2 !");
		}
		else
		{
			repBidOk = true;
			
			// DEBUG
			System.out.println("Seller: setting transition to attribute !");
			
			this.transition =
					RunningAuctionSellerFSMBehaviour.TRANSITION_TO_ATTRIBUTE;
		}
		
		// DEBUG
		System.out.println("Seller: sending rep_bid("+repBidOk+") !");
		
		// send rep_bid
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
		
		// Add selected bidder AID
		reply.setContent(String.valueOf(repBidOk));
		
		// Send
		mySellerAgent.send(reply);
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}

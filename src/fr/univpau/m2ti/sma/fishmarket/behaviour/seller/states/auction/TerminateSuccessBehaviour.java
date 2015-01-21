package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TerminateSuccessBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateSuccessBehaviour(
			SellerAgent mySellerAgent,
			FishSellerBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		ACLMessage mess = new ACLMessage(
				FishMarket.Performatives.AUCTION_OVER);
		
		// Set topic
		mess.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		mess.setConversationId(
				this.myFSM.getConversationId());
		
		// Receiver
		mess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Send
		mySellerAgent.send(mess);
	}
}

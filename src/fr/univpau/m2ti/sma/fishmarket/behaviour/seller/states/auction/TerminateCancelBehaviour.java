package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TerminateCancelBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateCancelBehaviour(
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
				(SellerAgent)super.myAgent;
		
		// Notify auction cancelled
		ACLMessage cancelMess = new ACLMessage(
				FishMarket.Performatives.TO_CANCEL);
		
		// Receiver
		cancelMess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Set topic
		cancelMess.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		cancelMess.setConversationId(
				this.myFSM.getConversationId());
		
		// send
		mySellerAgent.send(cancelMess);
	}
}

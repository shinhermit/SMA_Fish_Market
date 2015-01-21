package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AnnoucePriceBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public AnnoucePriceBehaviour(
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
				FishMarket.Performatives.TO_ANNOUNCE);
		
		// Set topic
		mess.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		mess.setConversationId(
				this.myFSM.getConversationId());
		
		// Receiver
		mess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Add price
		mess.setContent(String.valueOf(
				mySellerAgent.getCurrentPrice()));
		
		// Send
		mySellerAgent.send(mess);
	}
}

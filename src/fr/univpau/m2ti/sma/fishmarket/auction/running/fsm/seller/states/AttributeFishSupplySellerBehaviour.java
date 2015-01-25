package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AttributeFishSupplySellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public AttributeFishSupplySellerBehaviour(
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
		System.out.println("Seller: sending to attribute !");
		
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		ACLMessage mess = new ACLMessage(
				FishMarket.Performatives.TO_ATTRIBUTE);
		
		// Receiver
		mess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Set topic
		mess.addReceiver(
				RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		mess.setConversationId(
				this.myFSM.getConversationId());
		
		// send
		mySellerAgent.send(mess);
		
		// DEBUG (break point)
		System.out.println();
	}
}

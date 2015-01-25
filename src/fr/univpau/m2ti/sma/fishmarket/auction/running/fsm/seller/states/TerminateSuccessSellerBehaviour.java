package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TerminateSuccessSellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateSuccessSellerBehaviour(
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
		System.out.println("Seller: sending auction over !");
		
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		ACLMessage mess = new ACLMessage(
				FishMarket.Performatives.AUCTION_OVER);
		
		// Receiver
		mess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Set topic
		mess.addReceiver(
				RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		mess.setConversationId(
				this.myFSM.getConversationId());
		
		// Send
		mySellerAgent.send(mess);
		
		mySellerAgent.notifyAuctionOver();
		
		// Remove running auction
		mySellerAgent.removeBehaviour(this.myFSM);
		
		// Add create auction
		mySellerAgent.reset();
		mySellerAgent.addBehaviour(
				new CreateAuctionSellerFSMBehaviour(mySellerAgent));
	}
}

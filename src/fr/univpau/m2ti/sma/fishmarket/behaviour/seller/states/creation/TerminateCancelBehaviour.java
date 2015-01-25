package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TerminateCancelBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateCancelBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// Send to_cancel
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		ACLMessage cancelMess = new ACLMessage(
				FishMarket.Performatives.TO_CANCEL);
		
		// Set topic
		cancelMess.addReceiver(
				RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		cancelMess.setConversationId(
				this.myFSM.getResponse().getConversationId());
		
		// Receiver
		cancelMess.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Add auction and send
		mySellerAgent.send(cancelMess);
		
		mySellerAgent.notifyAuctionCancelled();
		
		mySellerAgent.removeBehaviour(this.myFSM);
		mySellerAgent.reset();
		mySellerAgent.addBehaviour(new CreateAuctionSellerFSMBehaviour(mySellerAgent));
	}
}

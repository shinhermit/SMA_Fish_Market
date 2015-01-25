package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitPaymentSellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/** The transition which will be chosen next. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitPaymentSellerBehaviour(
			SellerAgent mySellerAgent,
			RunningAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter =
				this.createMessageFilter();
	}
	
	@Override
	public void action()
	{
		// DEBUG
		System.out.println("Seller: checking message for to_pay !");
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			// DEBUG
			System.out.println("Seller: setting transition to terminate sucess !");
			
			// Auction over
			this.transition =
					RunningAuctionSellerFSMBehaviour
					.TRANSITION_TO_TERMINATE_SUCCESS;
		}
		else
		{
			// DEBUG
			System.out.println("Seller: setting transition to wait to pay !");
			
			// Continue to wait
			this.transition =
					RunningAuctionSellerFSMBehaviour
					.TRANSITION_TO_WAIT_TO_PAY;
			
			this.myFSM.block();
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * 
	 * @return the message filter to use in this behaviour.
	 */
	private MessageTemplate createMessageFilter()
	{
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.MatchPerformative(
						FishMarket.Performatives.TO_PAY));
	}
}
